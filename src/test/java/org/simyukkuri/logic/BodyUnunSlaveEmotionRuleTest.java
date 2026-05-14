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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.util.WorldTestHelper;

/**
 * Tests for YukkuriUnunSlaveEmotionRule.
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
		assertFalse(YukkuriUnunSlaveEmotionRule.checkEmotionFromUnunSlave(null, null));
	}

	@Test
	void testCheckEmotionFromUnunSlave_handlesUnunSlaveEnvyReaction() {
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setPublicRank(PublicRank.UnunSlave);
		me.setHappiness(org.simyukkuri.enums.Happiness.AVERAGE);
		you.setHappiness(org.simyukkuri.enums.Happiness.VERY_HAPPY);
		me.setPartner(you.getUniqueID());
		you.setPartner(me.getUniqueID());
		SimYukkuri.RND = new ConstState(0);

		assertDoesNotThrow(() -> YukkuriUnunSlaveEmotionRule.checkEmotionFromUnunSlave(me, you));
	}
}
