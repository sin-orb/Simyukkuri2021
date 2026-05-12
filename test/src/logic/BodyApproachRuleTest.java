package src.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.entity.core.living.yukkuri.Yukkuri;
import src.enums.PublicRank;
import src.util.WorldTestHelper;

/**
 * Tests for BodyApproachRule.
 */
class BodyApproachRuleTest {

	@Test
	void testHandleApproach_nonFlyerSetsMoveToBodyPosition() {
		WorldTestHelper.initializeMinimalWorld();
		WorldTestHelper.initializeStandardTranslate200();
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setX(100);
		me.setY(100);
		you.setX(200);
		you.setY(120);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);

		assertDoesNotThrow(() -> BodyApproachRule.handleApproach(you, me, 20));
		assertEquals(you.getX() - 20, me.getDestX(), "approach should move toward the target with the computed offset");
		assertEquals(you.getY(), me.getDestY(), "approach should keep the target Y position");
	}
}
