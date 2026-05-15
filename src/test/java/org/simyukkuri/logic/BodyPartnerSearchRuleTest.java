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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.WorldTestHelper;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Remirya;
import org.simyukkuri.entity.core.living.yukkuri.impl.Sakuya;

/**
 * Tests for YukkuriPartnerSearchRule.
 */
class BodyPartnerSearchRuleTest {

	@BeforeEach
	void setUp() {
		WorldTestHelper.initializeMinimalWorld();
		WorldTestHelper.initializeStandardTranslate200();
	}

	@AfterEach
	void tearDown() {
		GameRandom.clearOverride();
		WorldTestHelper.resetWorld();
	}

	@Test
	void testSelectTargets_prefersPheromoneBodyOverCloserNonPheromone() {
		Yukkuri me = WorldTestHelper.createBody();
		me.setX(40);
		me.setY(40);
		me.setOkazaris(null);
		me.setAttitude(Attitude.SUPER_SHITHEAD);

		Yukkuri closer = WorldTestHelper.createBody();
		closer.setX(50);
		closer.setY(40);
		closer.setOkazaris(null);

		Yukkuri pheromone = WorldTestHelper.createBody();
		pheromone.setX(70);
		pheromone.setY(40);
		pheromone.setPheromone(true);

		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(closer.getUniqueID(), closer);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(pheromone.getUniqueID(), pheromone);

		YukkuriPartnerSearchRule.SearchResult result = YukkuriPartnerSearchRule.selectTargets(me, null, me.getEyesightBase(),
				me.getEyesightBase());
		assertNotNull(result);
		assertEquals(pheromone, result.getTargetBody());
	}

	@Test
	void testSelectTargets_reportsOkazariCandidateForRudeBody() {
		Yukkuri me = WorldTestHelper.createBody();
		me.setX(40);
		me.setY(40);
		me.setOkazaris(null);
		me.setAttitude(Attitude.SUPER_SHITHEAD);

		Yukkuri target = WorldTestHelper.createBody();
		target.setX(50);
		target.setY(40);

		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(target.getUniqueID(), target);

		YukkuriPartnerSearchRule.SearchResult result = YukkuriPartnerSearchRule.selectTargets(me, null, me.getEyesightBase(),
				me.getEyesightBase());
		assertNotNull(result);
		assertEquals(target, result.getTargetHasOkazari());
	}

	@Test
	void testSelectTargets_skipsPredatorWhenActorIsPredatorServant() {
		Sakuya me = new Sakuya();
		me.setX(40);
		me.setY(40);
		me.setOkazaris(null);
		me.setAttitude(Attitude.SUPER_SHITHEAD);

		Remirya predator = new Remirya();
		predator.setX(50);
		predator.setY(40);
		predator.setOkazaris(null);

		Marisa normal = new Marisa();
		normal.setX(140);
		normal.setY(40);
		normal.setOkazaris(null);

		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(predator.getUniqueID(), predator);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(normal.getUniqueID(), normal);

		YukkuriPartnerSearchRule.SearchResult result = YukkuriPartnerSearchRule.selectTargets(me, null, me.getEyesightBase(),
				me.getEyesightBase());
		assertNotNull(result);
		assertEquals(normal, result.getTargetBody());
	}
}
