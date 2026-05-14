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
 * Tests for BodyPartnerSearchRule.
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
		me.setOkazari(null);
		me.setAttitude(Attitude.SUPER_SHITHEAD);

		Yukkuri closer = WorldTestHelper.createBody();
		closer.setX(50);
		closer.setY(40);
		closer.setOkazari(null);

		Yukkuri pheromone = WorldTestHelper.createBody();
		pheromone.setX(70);
		pheromone.setY(40);
		pheromone.setPheromone(true);

		SimYukkuri.world.getCurrentMap().getBody().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentMap().getBody().put(closer.getUniqueID(), closer);
		SimYukkuri.world.getCurrentMap().getBody().put(pheromone.getUniqueID(), pheromone);

		BodyPartnerSearchRule.SearchResult result = BodyPartnerSearchRule.selectTargets(me, null, me.getEyesightBase(),
				me.getEyesightBase());
		assertNotNull(result);
		assertEquals(pheromone, result.getFound());
	}

	@Test
	void testSelectTargets_reportsOkazariCandidateForRudeBody() {
		Yukkuri me = WorldTestHelper.createBody();
		me.setX(40);
		me.setY(40);
		me.setOkazari(null);
		me.setAttitude(Attitude.SUPER_SHITHEAD);

		Yukkuri target = WorldTestHelper.createBody();
		target.setX(50);
		target.setY(40);

		SimYukkuri.world.getCurrentMap().getBody().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentMap().getBody().put(target.getUniqueID(), target);

		BodyPartnerSearchRule.SearchResult result = BodyPartnerSearchRule.selectTargets(me, null, me.getEyesightBase(),
				me.getEyesightBase());
		assertNotNull(result);
		assertEquals(target, result.getBodyHasOkazari());
	}

	@Test
	void testSelectTargets_skipsPredatorWhenActorIsPredatorServant() {
		Sakuya me = new Sakuya();
		me.setX(40);
		me.setY(40);
		me.setOkazari(null);
		me.setAttitude(Attitude.SUPER_SHITHEAD);

		Remirya predator = new Remirya();
		predator.setX(50);
		predator.setY(40);
		predator.setOkazari(null);

		Marisa normal = new Marisa();
		normal.setX(140);
		normal.setY(40);
		normal.setOkazari(null);

		SimYukkuri.world.getCurrentMap().getBody().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentMap().getBody().put(predator.getUniqueID(), predator);
		SimYukkuri.world.getCurrentMap().getBody().put(normal.getUniqueID(), normal);

		BodyPartnerSearchRule.SearchResult result = BodyPartnerSearchRule.selectTargets(me, null, me.getEyesightBase(),
				me.getEyesightBase());
		assertNotNull(result);
		assertEquals(normal, result.getFound());
	}
}
