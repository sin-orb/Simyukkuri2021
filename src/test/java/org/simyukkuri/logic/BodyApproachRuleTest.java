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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.util.WorldTestHelper;

/**
 * Tests for YukkuriApproachRule.
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

		assertDoesNotThrow(() -> YukkuriApproachRule.handleApproach(you, me, 20));
		assertEquals(you.getX() - 20, me.getDestX(), "approach should move toward the target with the computed offset");
		assertEquals(you.getY(), me.getDestY(), "approach should keep the target Y position");
	}
}
