package src.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.enums.Attitude;
import src.util.GameRandom;
import src.util.WorldTestHelper;

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
		Body me = WorldTestHelper.createBody();
		me.setX(40);
		me.setY(40);
		me.setOkazari(null);
		me.setAttitude(Attitude.SUPER_SHITHEAD);

		Body closer = WorldTestHelper.createBody();
		closer.setX(50);
		closer.setY(40);
		closer.setOkazari(null);

		Body pheromone = WorldTestHelper.createBody();
		pheromone.setX(70);
		pheromone.setY(40);
		pheromone.setbPheromone(true);

		SimYukkuri.world.getCurrentMap().getBody().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentMap().getBody().put(closer.getUniqueID(), closer);
		SimYukkuri.world.getCurrentMap().getBody().put(pheromone.getUniqueID(), pheromone);

		BodyPartnerSearchRule.SearchResult result = BodyPartnerSearchRule.selectTargets(me, null, me.getEYESIGHTorg(),
				me.getEYESIGHTorg());
		assertNotNull(result);
		assertEquals(pheromone, result.getFound());
	}

	@Test
	void testSelectTargets_reportsOkazariCandidateForRudeBody() {
		Body me = WorldTestHelper.createBody();
		me.setX(40);
		me.setY(40);
		me.setOkazari(null);
		me.setAttitude(Attitude.SUPER_SHITHEAD);

		Body target = WorldTestHelper.createBody();
		target.setX(50);
		target.setY(40);

		SimYukkuri.world.getCurrentMap().getBody().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentMap().getBody().put(target.getUniqueID(), target);

		BodyPartnerSearchRule.SearchResult result = BodyPartnerSearchRule.selectTargets(me, null, me.getEYESIGHTorg(),
				me.getEYESIGHTorg());
		assertNotNull(result);
		assertEquals(target, result.getBodyHasOkazari());
	}
}
