package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.ConstState;
import src.SimYukkuri;
import src.base.Body;
import src.enums.PublicRank;
import src.util.WorldTestHelper;

/**
 * Tests for BodyUnunSlaveEmotionRule.
 */
public class BodyUnunSlaveEmotionRuleTest {

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void testCheckEmotionFromUnunSlave_returnsFalseForNull() {
		assertFalse(BodyUnunSlaveEmotionRule.checkEmotionFromUnunSlave(null, null));
	}

	@Test
	void testCheckEmotionFromUnunSlave_handlesUnunSlaveEnvyReaction() {
		Body me = WorldTestHelper.createBody();
		Body you = WorldTestHelper.createBody();
		me.setPublicRank(PublicRank.UnunSlave);
		me.setHappiness(src.enums.Happiness.AVERAGE);
		you.setHappiness(src.enums.Happiness.VERY_HAPPY);
		me.setPartner(you.getUniqueID());
		you.setPartner(me.getUniqueID());
		SimYukkuri.RND = new ConstState(0);

		assertDoesNotThrow(() -> BodyUnunSlaveEmotionRule.checkEmotionFromUnunSlave(me, you));
	}
}
