package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.util.WorldTestHelper;

/**
 * Tests for YukkuriDeadActionRule.
 */
public class BodyDeadActionRuleTest {

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
	}

	@Test
	void testHandleDeadBodyInteraction_excitingBodyHandlesDeadTarget() {
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri dead = WorldTestHelper.createBody();
		me.setExciting(true);
		me.setRaper(true);
		dead.setDead(true);

		assertTrue(YukkuriDeadActionRule.handleDeadYukkuriInteraction(dead, me));
		// doRape が呼ばれた副作用: me が sukkiri 状態になり HAPPY になること
		assertTrue(me.isSukkiri(), "doRape により me が sukkiri 状態になること");
		assertEquals(Happiness.HAPPY, me.getHappiness(), "doRape により me が HAPPY になること");
	}

	@Test
	void testHandleDeadBodyInteraction_parentDeathMakesAdultVerySad() {
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri parent = WorldTestHelper.createBody();
		me.setAgeState(AgeState.ADULT);
		me.setIntelligence(Intelligence.AVERAGE);  // addMemories(-2) が確実に -2 になるよう固定
		parent.setDead(true);
		WorldTestHelper.setParents(me, -1, parent.getUniqueId());

		int initialStress   = me.getStress();
		int initialMemories = me.getMemories();

		assertTrue(YukkuriDeadActionRule.handleDeadYukkuriInteraction(parent, me));
		assertEquals(Happiness.VERY_SAD, me.getHappiness(), "親の死亡で VERY_SAD になること");
		assertEquals(initialStress + 100, me.getStress(),     "親の死亡で stress が 100 増加すること");
		assertEquals(initialMemories - 2, me.getMemories(),   "親の死亡で memories が 2 減少すること");
	}
}
