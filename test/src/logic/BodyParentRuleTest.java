package src.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.enums.AgeState;
import src.util.WorldTestHelper;

/**
 * Tests for BodyParentRule.
 */
public class BodyParentRuleTest {

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
	}

	@Test
	void testCheckNearParent_returnsWhenAdult() {
		Body me = WorldTestHelper.createBody();
		me.setAge((long) me.getCHILDLIMITorg());
		assertDoesNotThrow(() -> BodyParentRule.checkNearParent(me));
	}

	@Test
	void testCheckNearParent_callingParentsWakesSleepingParent() {
		Body me = WorldTestHelper.createBody();
		Body parent = WorldTestHelper.createBody();
		me.setAgeState(AgeState.CHILD);
		me.setCallingParents(true);
		parent.setSleeping(true);
		WorldTestHelper.setParents(me, -1, parent.getUniqueID());
		SimYukkuri.world.getCurrentMap().getBody().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);

		BodyParentRule.checkNearParent(me);

		assertFalse(parent.isSleeping());
	}
}
