package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.Terrarium;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.entity.core.living.yukkuri.YukkuriNydDelegate;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.util.EnvironmentSource;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.WorldTestHelper;

/**
 * TerrariumEnvironment 各スチームが yukkuri に正しい変化を起こすことを検証するテスト。
 * GameEnvironment.setOverride(source) で特定フラグのみ true にして検証する。
 */
class TerrariumSteamEffectTest {

	private Yukkuri body;

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
		body = WorldTestHelper.createBody();
		body.setAgeState(AgeState.ADULT);
		body.setDead(false);
		SimYukkuri.RND = new ConstState(1);
	}

	@AfterEach
	void tearDown() {
		GameEnvironment.clearOverride();
		WorldTestHelper.resetWorld();
	}

	// --- ANTI_FUNGAL steam: dirty period が進まない ---

	@Test
	void antifungalSteamPreventsDirectyPeriodAdvance() {
		body.setDirty(true);
		WorldTestHelper.setDamage(body, body.getDamageLimit() / 2 + 1); // isDamaged=true
		body.setDirtyPeriod(0);
		GameEnvironment.setOverride(steamSource(SteamFlag.ANTIFUNGAL));

		body.checkSick();

		assertEquals(0, body.getDirtyPeriod(),
				"抗菌スチーム中は dirtyPeriod が進まないこと");
	}

	@Test
	void noAntifungalSteamAdvancesDirtyPeriod() {
		body.setDirty(true);
		// isDamaged() = getDamageState() != NONE = damage >= limit/2 が必要
		WorldTestHelper.setDamage(body, body.getDamageLimit() / 2 + 1);
		body.setDirtyPeriod(0);
		GameEnvironment.setOverride(steamSource(SteamFlag.NONE));

		body.checkSick();

		assertTrue(body.getDirtyPeriod() > 0,
				"抗菌スチームなしでは dirtyPeriod が進むこと（対照確認）");
	}

	// --- NOSLEEP steam: 眠ったままにならない ---

	@Test
	void noSleepSteamWakesUpSleepingYukkuri() {
		body.setSleeping(true);
		body.setSleepingPeriod(100);
		GameEnvironment.setOverride(steamSource(SteamFlag.NOSLEEP));

		body.checkSleep();

		assertFalse(body.isSleeping(), "眠眠スチーム中は sleeping が false になること");
		assertEquals(0, body.getSleepingPeriod(), "眠眠スチーム中は sleepingPeriod が 0 にリセットされること");
	}

	// --- ANTI_NONYUKKURI steam: NYD 即時治癒 ---

	@Test
	void antiNydSteamCuresNonYukkuriDisease() {
		body.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);
		GameEnvironment.setOverride(steamSource(SteamFlag.ANTI_NYD));

		new YukkuriNydDelegate(body).hasNonYukkuriDisease();

		assertTrue(body.isNotNyd(),
				"NYD 治癒スチーム中は即時 NORMAL 復帰すること");
	}

	// --- ORANGE steam: damage が回復する ---

	@Test
	void orangeSteamRecoversDamage() {
		body.addDamage(500);
		int damageBefore = body.getDamage();
		GameEnvironment.setOverride(steamSource(SteamFlag.ORANGE));

		body.checkDamage();

		assertTrue(body.getDamage() < damageBefore,
				"オレンジスチーム中は damage が減少すること");
	}

	// --- ANTI_YU (poison) steam: damage が増加する ---

	@Test
	void poisonSteamIncreasesDamage() {
		body.setDamage(0);
		GameEnvironment.setOverride(steamSource(SteamFlag.POISON));

		body.checkDamage();

		assertTrue(body.getDamage() > 0,
				"毒スチーム中は damage が増加すること");
	}

	// --- SUGER steam: 重傷ゆっくりが回復する ---

	@Test
	void sugerSteamRecoversSeverelyDamagedYukkuri() {
		int limit = body.getDamageLimit();
		body.setDamage(limit * 80 / 100 + 1); // 80% 以上の重傷
		int damageBefore = body.getDamage();
		GameEnvironment.setOverride(steamSource(SteamFlag.SUGER));

		body.checkDamage();

		assertTrue(body.getDamage() < damageBefore,
				"砂糖スチーム中は重傷ゆっくりのダメージが回復すること");
	}

	@Test
	void sugerSteamDoesNotHealLightlyDamagedYukkuri() {
		// 80% 未満の軽傷 → ガードで回復しない
		body.setDamage(1);
		int damageBefore = body.getDamage();
		GameEnvironment.setOverride(steamSource(SteamFlag.SUGER));

		body.checkDamage();

		// ダメージが減らないこと（suger は 80% 以上のみ効く）
		assertTrue(body.getDamage() >= damageBefore,
				"砂糖スチームは軽傷ゆっくりには効かないこと");
	}

	// --- AGE_BOOST steam: 加齢促進 ---

	@Test
	void ageBoostSteamAcceleratesAgeForNonAdult() {
		body.setAgeState(org.simyukkuri.enums.AgeState.CHILD);
		long ageBefore = body.getAge();
		GameEnvironment.setOverride(steamSource(SteamFlag.AGE_BOOST));

		body.clockTick();

		assertTrue(body.getAge() >= ageBefore + 10000,
				"加齢促進スチームで CHILD の age が 10000+ 増加すること");
	}

	@Test
	void ageBoostSteamDoesNotApplyToAdult() {
		body.setAgeState(org.simyukkuri.enums.AgeState.ADULT);
		long ageBefore = body.getAge();
		GameEnvironment.setOverride(steamSource(SteamFlag.AGE_BOOST));

		body.clockTick();

		assertTrue(body.getAge() < ageBefore + 10000,
				"加齢促進スチームは ADULT には効かないこと（10000増加なし）");
	}

	// --- AGE_STOP steam: 加齢停止 ---

	@Test
	void ageStopSteamPreventsGrowthToAdult() {
		// CHILD age を ADULT 閾値-1 に設定 → ageStop で ADULT に上がらない
		body.setAgeState(org.simyukkuri.enums.AgeState.CHILD);
		body.setAge(body.getChildLimitBase() - 1);
		GameEnvironment.setOverride(steamSource(SteamFlag.AGE_STOP));

		// clockTick で age が増えても ADULT にならないことを確認
		assertDoesNotThrow(() -> body.clockTick());
		assertFalse(body.isAdult(),
				"加齢停止スチーム中は ADULT に成長しないこと");
	}

	// --- RAPIDPREGNANT steam: 妊娠促進 ---

	@Test
	void rapidPregnantSteamBoostsPregnancy() {
		body.setHasBaby(true);
		body.setPregnantPeriod(0); // 出産閾値から遠く離れた位置（birth が起きないように）
		int boostBefore = body.getPregnancyPeriodBoost();
		GameEnvironment.setOverride(steamSource(SteamFlag.RAPIDPREGNANT));

		body.clockTick();

		assertTrue(body.getPregnancyPeriodBoost() > boostBefore,
				"妊娠促進スチームで pregnancyPeriodBoost が増加すること");
	}

	// --- PREDATOR steam: 捕食サーチ抑制 ---

	@Test
	void predatorSteamBlocksPredatorFoodSearch() {
		// isPredatorType=true + predatorSteam=true → サーチがブロックされる
		body.setPredatorType(org.simyukkuri.enums.PredatorType.BITE);
		body.setHungry(0); // 空腹状態
		GameEnvironment.setOverride(steamSource(SteamFlag.PREDATOR));

		boolean result = org.simyukkuri.logic.FoodLogic.checkFood(body);

		assertFalse(result, "捕食抑制スチーム中は捕食種のえさサーチがブロックされること");
	}

	// --- STEAM/humid steam: かび進行速度が変わる ---

	@Test
	void humidSteamAcceleratesSickPeriod() {
		// isHumid=true かつ isDamaged=true → sickPeriod が 4 増加（通常は 1）
		body.setSickPeriod(body.getIncubationPeriodBase() + 1); // barely sick
		WorldTestHelper.setDamage(body, body.getDamageLimit() / 2 + 1); // isDamaged=true
		int before = body.getSickPeriod();
		GameEnvironment.setOverride(steamSource(SteamFlag.HUMID));

		body.checkSick();

		assertEquals(before + 4, body.getSickPeriod(),
				"加湿スチーム中はかびの進行速度が 4x になること");
	}

	@Test
	void noHumidSteamNormalSickPeriod() {
		// isHumid=false → sickPeriod が 1 増加（対照確認）
		body.setDirty(true);
		body.setSickPeriod(body.getIncubationPeriodBase() + 1);
		WorldTestHelper.setDamage(body, body.getDamageLimit() / 2 + 1);
		int before = body.getSickPeriod();
		GameEnvironment.setOverride(steamSource(SteamFlag.NONE));

		body.checkSick();

		assertEquals(before + 1, body.getSickPeriod(),
				"加湿スチームなしでは sickPeriod が 1 増加すること（対照確認）");
	}

	// --- ENDLESS_FURIFURI steam: エンドレスふりふり ---

	@Test
	void endlessFurifuriSteamSetsFurifuri() {
		// isEndlessFurifuriSteam=true → clockTick() で isFurifuri=true になること
		body.setFurifuri(false);
		GameEnvironment.setOverride(steamSource(SteamFlag.ENDLESS_FURIFURI));

		assertDoesNotThrow(() -> body.clockTick());

		assertTrue(body.isFurifuri(), "エンドレスふりふりスチーム中は isFurifuri が true になること");
	}

	// --- ANTI_DOS steam: ドス化防止 ---

	@Test
	void antiDosSteamBlocksTransformation() {
		body.setAgeState(AgeState.ADULT);
		GameEnvironment.setOverride(steamSource(SteamFlag.ANTI_DOS));

		assertFalse(body.judgeCanTransForGodHand(),
				"ドス化防止スチーム中はまりさが judgeCanTransForGodHand()=false になること");
	}

	@Test
	void noAntiDosSteamAllowsTransformation() {
		body.setAgeState(AgeState.ADULT);
		GameEnvironment.setOverride(steamSource(SteamFlag.NONE));

		assertTrue(body.judgeCanTransForGodHand(),
				"ドス化防止スチームなしでは judgeCanTransForGodHand()=true になること");
	}

	// --- EnvironmentSource ヘルパー ---

	private enum SteamFlag {
		NONE, ANTIFUNGAL, NOSLEEP, ANTI_NYD, ORANGE, POISON,
		SUGER, AGE_BOOST, AGE_STOP, RAPIDPREGNANT, PREDATOR, HUMID, ENDLESS_FURIFURI, ANTI_DOS
	}

	private EnvironmentSource steamSource(SteamFlag flag) {
		return new EnvironmentSource() {
			@Override public int getOperationTime() { return 0; }
			@Override public int getDayTime() { return 0; }
			@Override public int getNightTime() { return 0; }
			@Override public boolean isHumid() { return flag == SteamFlag.HUMID; }
			@Override public boolean isOrangeSteam() { return flag == SteamFlag.ORANGE; }
			@Override public boolean isAgeBoostSteam() { return flag == SteamFlag.AGE_BOOST; }
			@Override public boolean isAgeStopSteam() { return flag == SteamFlag.AGE_STOP; }
			@Override public boolean isAntidosSteam() { return flag == SteamFlag.ANTI_DOS; }
			@Override public boolean isPoisonSteam() { return flag == SteamFlag.POISON; }
			@Override public boolean isPredatorSteam() { return flag == SteamFlag.PREDATOR; }
			@Override public boolean isSugerSteam() { return flag == SteamFlag.SUGER; }
			@Override public boolean isNoSleepSteam() { return flag == SteamFlag.NOSLEEP; }
			@Override public boolean isHybridSteam() { return false; }
			@Override public boolean isRapidPregnantSteam() { return flag == SteamFlag.RAPIDPREGNANT; }
			@Override public boolean isAntiNonYukkuriDiseaseSteam() { return flag == SteamFlag.ANTI_NYD; }
			@Override public boolean isEndlessFurifuriSteam() { return flag == SteamFlag.ENDLESS_FURIFURI; }
			@Override public boolean isAntifungalSteam() { return flag == SteamFlag.ANTIFUNGAL; }
			@Override public int getTick() { return 0; }
			@Override public void setAlarm() {}
			@Override public boolean getAlarm() { return false; }
			@Override public Terrarium.DayState getDayState() { return Terrarium.DayState.DAY; }
			@Override public void resetTerrariumEnvironment() {}
			@Override public int getInterval() { return 0; }
		};
	}
}
