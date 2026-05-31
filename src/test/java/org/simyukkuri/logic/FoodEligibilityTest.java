package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Okazari;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.PredatorType;
import org.simyukkuri.util.WorldTestHelper;

/**
 * {@link FoodEligibility} の直接テスト.
 */
class FoodEligibilityTest {

	private Yukkuri eater;
	private Yukkuri prey;

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
		eater = WorldTestHelper.createBody();
		prey = WorldTestHelper.createBody();
	}

	@AfterEach
	void tearDown() {
		WorldTestHelper.resetWorld();
	}

	@Test
	void predatorBodiesCanAlwaysEatBody() {
		// 捕食者は生死問わず食べられること
		eater.setPredatorType(PredatorType.BITE);
		prey.setDead(false);
		assertTrue(FoodEligibility.checkCanEatYukkuri(eater, prey),
				"捕食者は生きている prey でも食べられること");
		prey.setDead(true);
		assertTrue(FoodEligibility.checkCanEatYukkuri(eater, prey),
				"捕食者は死んだ prey でも食べられること");
	}

	@Test
	void nonPredatorCannotEatLivingBody() {
		// デフォルトで okazari が付くので外す
		prey.setOkazaris(null);

		// 非捕食者は生きている prey を食べられないこと
		prey.setDead(false);
		assertFalse(FoodEligibility.checkCanEatYukkuri(eater, prey),
				"非捕食者は生きている prey を食べられないこと");

		// 死体（okazariなし）は食べられること
		prey.setDead(true);
		assertTrue(FoodEligibility.checkCanEatYukkuri(eater, prey),
				"非捕食者は死んだ prey（okazariなし）を食べられること");
	}

	@Test
	void nonRudeBodiesRejectOkazariBodies() {
		prey.setDead(true);
		prey.setOkazaris(new Okazari());
		// 非VeryRude（デフォルト）はokazari持ちの死体を食べられないこと
		assertFalse(FoodEligibility.checkCanEatYukkuri(eater, prey),
				"非VeryRude はokazari持ちの死体を拒否すること");
		// VeryRude（SUPER_SHITHEAD）はokazari持ちの死体でも食べられること
		eater.setAttitude(Attitude.SUPER_SHITHEAD);
		assertTrue(FoodEligibility.checkCanEatYukkuri(eater, prey),
				"VeryRude はokazari持ちの死体を食べられること");
	}
}
