package src.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.base.Body;
import src.util.WorldTestHelper;

/**
 * Tests for BodySurisuriRule.
 */
public class BodySurisuriRuleTest {

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
	}

	@Test
	void testCheckActionSurisuriFromPlayer_returnsNoneForNull() {
		assertEquals(BodyLogic.eActionGo.NONE, BodySurisuriRule.checkActionSurisuriFromPlayer(null, null));
	}

	@Test
	void testCheckActionSurisuriFromPlayer_returnsNoneWhenTargetNotFlagged() {
		Body me = WorldTestHelper.createBody();
		Body you = WorldTestHelper.createBody();
		assertEquals(BodyLogic.eActionGo.NONE, BodySurisuriRule.checkActionSurisuriFromPlayer(me, you));
	}
}
