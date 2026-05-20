package org.simyukkuri.logic;

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
		body.setDamage(0);
		assertTrue(YukkuriVitals.isNoDamaged(body));
		assertFalse(YukkuriVitals.isDamaged(body));
		assertFalse(YukkuriVitals.isDamagedHeavily(body));

		body.setDamage(body.getDamageLimit() / 2);
		assertFalse(YukkuriVitals.isNoDamaged(body));
		assertTrue(YukkuriVitals.isDamaged(body));
		assertFalse(YukkuriVitals.isDamagedHeavily(body));

		body.setDamage(body.getDamageLimit() * 3 / 4);
		assertTrue(YukkuriVitals.isDamaged(body));
		assertTrue(YukkuriVitals.isDamagedHeavily(body));
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
		body.setDead(true);
		body.setHungry(0);

		assertFalse(YukkuriVitals.isFull(body));
		assertFalse(YukkuriVitals.isHungry(body));
		assertFalse(YukkuriVitals.isSoHungry(body));
		assertFalse(YukkuriVitals.isVeryHungry(body));
		assertFalse(YukkuriVitals.isTooHungry(body));
		assertFalse(YukkuriVitals.isStarving(body));
	}

	@Test
	void detectsTooHungryAndStarvingFromDamageState() {
		body.setHungry(0);
		body.setDamage(0);
		assertFalse(YukkuriVitals.isTooHungry(body));
		assertFalse(YukkuriVitals.isStarving(body));

		body.setDamage(body.getDamageLimit() / 2);
		assertTrue(YukkuriVitals.isTooHungry(body));
		assertFalse(YukkuriVitals.isStarving(body));

		body.setDamage(body.getDamageLimit() * 3 / 4);
		assertTrue(YukkuriVitals.isTooHungry(body));
		assertTrue(YukkuriVitals.isStarving(body));
	}

	@Test
	void detectsSicknessStages() {
		body.setSickPeriod(body.getIncubationPeriodBase());
		assertFalse(YukkuriVitals.isSick(body));
		assertFalse(YukkuriVitals.isSickHeavily(body));

		body.setSickPeriod(body.getIncubationPeriodBase() + 1);
		assertTrue(YukkuriVitals.isSick(body));
		assertFalse(YukkuriVitals.isSickHeavily(body));

		body.setSickPeriod(body.getIncubationPeriodBase() * 8 + 1);
		assertTrue(YukkuriVitals.isSickHeavily(body));
	}

	@Test
	void sickTooHeavilyRequiresHeavyStageAndDamage() {
		body.setSickPeriod(body.getIncubationPeriodBase() * 32 + 1);
		body.setDamage(0);
		assertFalse(YukkuriVitals.isSickTooHeavily(body));

		body.setDamage(body.getDamageLimit() / 2);
		assertTrue(YukkuriVitals.isSickTooHeavily(body));
	}
}
