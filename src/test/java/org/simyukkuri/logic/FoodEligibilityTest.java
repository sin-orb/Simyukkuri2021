package org.simyukkuri.logic;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Okazari;
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
		eater.setPredatorType(PredatorType.BITE);
		assertTrue(FoodEligibility.checkCanEatYukkuri(eater, prey));
	}

	@Test
	void nonPredatorCannotEatLivingBody() {
		prey.setDead(false);
		assertFalse(FoodEligibility.checkCanEatYukkuri(eater, prey));
	}

	@Test
	void nonRudeBodiesRejectOkazariBodies() {
		prey.setDead(true);
		prey.setOkazari(new Okazari());
		assertFalse(FoodEligibility.checkCanEatYukkuri(eater, prey));
	}
}
