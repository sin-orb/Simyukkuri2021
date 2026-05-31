package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.util.WorldTestHelper;

class BodyVitalsTest {

	private Yukkuri body;

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
		body = WorldTestHelper.createBody();
		body.setAgeState(AgeState.ADULT);
		body.setDead(false);
	}

	@AfterEach
	void tearDown() {
		WorldTestHelper.resetWorld();
	}

	@Test
	void detectsDamageLevels() {
		int limit = body.getDamageLimit();

		// ダメージなし
		body.setDamage(0);
		assertTrue(YukkuriVitals.isNoDamaged(body),     "damage=0: isNoDamaged=true");
		assertFalse(YukkuriVitals.isDamaged(body),      "damage=0: isDamaged=false");
		assertFalse(YukkuriVitals.isDamagedHeavily(body),"damage=0: isDamagedHeavily=false");

		// ダメージ中程度（limit/2 が閾値）
		body.setDamage(limit / 2);
		assertFalse(YukkuriVitals.isNoDamaged(body),    "damage=limit/2: isNoDamaged=false");
		assertTrue(YukkuriVitals.isDamaged(body),        "damage=limit/2: isDamaged=true");
		assertFalse(YukkuriVitals.isDamagedHeavily(body),"damage=limit/2: isDamagedHeavily=false");
		// isNoDamaged と isDamaged は排他的であること
		assertEquals(!YukkuriVitals.isNoDamaged(body), YukkuriVitals.isDamaged(body) || YukkuriVitals.isDamagedHeavily(body),
				"isNoDamaged と (isDamaged || isDamagedHeavily) は排他的");

		// 重ダメージ
		body.setDamage(limit * 3 / 4);
		assertTrue(YukkuriVitals.isDamaged(body),        "damage=3*limit/4: isDamaged=true");
		assertTrue(YukkuriVitals.isDamagedHeavily(body), "damage=3*limit/4: isDamagedHeavily=true");
	}

	@Test
	void detectsHungerLevelsForLivingBody() {
		body.setHungry(body.getHungryLimit());
		assertTrue(YukkuriVitals.isFull(body));
		assertFalse(YukkuriVitals.isHungry(body));
		assertFalse(YukkuriVitals.isSoHungry(body));
		assertFalse(YukkuriVitals.isVeryHungry(body));

		body.setHungry(body.getHungryLimit() / 2);
		assertTrue(YukkuriVitals.isHungry(body));
		assertFalse(YukkuriVitals.isSoHungry(body));

		body.setHungry(1);
		assertTrue(YukkuriVitals.isSoHungry(body));
		assertFalse(YukkuriVitals.isVeryHungry(body));

		body.setHungry(0);
		assertTrue(YukkuriVitals.isVeryHungry(body));
	}

	@Test
	void deadBodyIsNotHungryOrFull() {
		// alive=false のときはすべての飢え/満腹状態が false であること
		body.setDead(true);
		body.setHungry(0);
		assertFalse(YukkuriVitals.isFull(body),       "dead=true: isFull=false");
		assertFalse(YukkuriVitals.isHungry(body),     "dead=true: isHungry=false");
		assertFalse(YukkuriVitals.isSoHungry(body),   "dead=true: isSoHungry=false");
		assertFalse(YukkuriVitals.isVeryHungry(body), "dead=true: isVeryHungry=false");
		assertFalse(YukkuriVitals.isTooHungry(body),  "dead=true: isTooHungry=false");
		assertFalse(YukkuriVitals.isStarving(body),   "dead=true: isStarving=false");

		// 対比: alive=true のとき hungry=limit なら isFull=true になること
		body.setDead(false);
		body.setHungry(body.getHungryLimit());
		assertTrue(YukkuriVitals.isFull(body), "alive=true, hungry=limit: isFull=true");
	}

	@Test
	void detectsTooHungryAndStarvingFromDamageState() {
		int limit = body.getDamageLimit();

		// hungry=0, damage=0: どちらも false（ダメージなしでは tooHungry にならない）
		body.setHungry(0);
		body.setDamage(0);
		assertFalse(YukkuriVitals.isTooHungry(body), "hungry=0, damage=0: isTooHungry=false");
		assertFalse(YukkuriVitals.isStarving(body),  "hungry=0, damage=0: isStarving=false");

		// 中程度ダメージ: isTooHungry=true, isStarving=false
		body.setDamage(limit / 2);
		assertTrue(YukkuriVitals.isTooHungry(body),  "hungry=0, damage=limit/2: isTooHungry=true");
		assertFalse(YukkuriVitals.isStarving(body),  "hungry=0, damage=limit/2: isStarving=false");

		// 重ダメージ: 両方 true
		body.setDamage(limit * 3 / 4);
		assertTrue(YukkuriVitals.isTooHungry(body),  "hungry=0, damage=3*limit/4: isTooHungry=true");
		assertTrue(YukkuriVitals.isStarving(body),   "hungry=0, damage=3*limit/4: isStarving=true");

		// 対比: hungry が十分あれば damage があっても isTooHungry=false
		body.setHungry(body.getHungryLimit());
		assertFalse(YukkuriVitals.isTooHungry(body), "hungry=limit, damage=3*limit/4: isTooHungry=false");
	}

	@Test
	void detectsSicknessStages() {
		int base = body.getIncubationPeriodBase();

		// 閾値以下: isSick=false
		body.setSickPeriod(base);
		assertFalse(YukkuriVitals.isSick(body),       "sickPeriod=base: isSick=false");
		assertFalse(YukkuriVitals.isSickHeavily(body), "sickPeriod=base: isSickHeavily=false");

		// 閾値+1: isSick=true, isSickHeavily=false
		body.setSickPeriod(base + 1);
		assertTrue(YukkuriVitals.isSick(body),         "sickPeriod=base+1: isSick=true");
		assertFalse(YukkuriVitals.isSickHeavily(body), "sickPeriod=base+1: isSickHeavily=false");

		// 重症閾値: 両方 true
		body.setSickPeriod(base * 8 + 1);
		assertTrue(YukkuriVitals.isSick(body),         "sickPeriod=base*8+1: isSick=true");
		assertTrue(YukkuriVitals.isSickHeavily(body),  "sickPeriod=base*8+1: isSickHeavily=true");
	}

	@Test
	void sickTooHeavilyRequiresHeavyStageAndDamage() {
		int base = body.getIncubationPeriodBase();

		// 重症期間だが damage=0: isSickTooHeavily=false
		body.setSickPeriod(base * 32 + 1);
		body.setDamage(0);
		assertFalse(YukkuriVitals.isSickTooHeavily(body),
				"重症期間 + damage=0: isSickTooHeavily=false（damage 条件も必要）");

		// 重症期間 + damage あり: isSickTooHeavily=true
		body.setDamage(body.getDamageLimit() / 2);
		assertTrue(YukkuriVitals.isSickTooHeavily(body),
				"重症期間 + damage>0: isSickTooHeavily=true");
	}
}
