package src.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.base.Body;
import src.enums.AgeState;
import src.enums.Happiness;
import src.util.WorldTestHelper;

/**
 * Tests for BodyDeadActionRule.
 */
public class BodyDeadActionRuleTest {

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
	}

	@Test
	void testHandleDeadBodyInteraction_excitingBodyHandlesDeadTarget() {
		Body me = WorldTestHelper.createBody();
		Body dead = WorldTestHelper.createBody();
		me.setExciting(true);
		me.setRaper(true);
		dead.setDead(true);

		assertTrue(BodyDeadActionRule.handleDeadBodyInteraction(dead, me));
	}

	@Test
	void testHandleDeadBodyInteraction_parentDeathMakesAdultVerySad() {
		Body me = WorldTestHelper.createBody();
		Body parent = WorldTestHelper.createBody();
		me.setAgeState(AgeState.ADULT);
		parent.setDead(true);
		WorldTestHelper.setParents(me, -1, parent.getUniqueID());

		assertTrue(BodyDeadActionRule.handleDeadBodyInteraction(parent, me));
		assertEquals(Happiness.VERY_SAD, me.getHappiness());
	}
}
