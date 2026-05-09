package src.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.base.Yukkuri;
import src.base.StubBodyAttributes;
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
		assertEquals(BodyLogic.ActionGo.NONE, BodySurisuriRule.checkActionSurisuriFromPlayer(null, null));
	}

	@Test
	void testCheckActionSurisuriFromPlayer_returnsNoneWhenTargetNotFlagged() {
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		assertEquals(BodyLogic.ActionGo.NONE, BodySurisuriRule.checkActionSurisuriFromPlayer(me, you));
	}

	@Test
	void testIsSurisuriFromPlayerReadsRawFlag() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setSurisuriFromPlayer(false);
		assertFalse(BodySurisuriRule.isSurisuriFromPlayer(body));

		body.setSurisuriFromPlayer(true);
		assertTrue(BodySurisuriRule.isSurisuriFromPlayer(body));
	}
}
