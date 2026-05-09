package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodySpeechRuleTest {
	@Test
	void detectsTalkingState() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setMessageTicks(0);
		assertFalse(BodySpeechRule.isTalking(body));

		body.setMessageTicks(1);
		assertTrue(BodySpeechRule.isTalking(body));
	}

	@Test
	void detectsCanTalkState() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setCanTalk(true);
		assertTrue(BodySpeechRule.isCanTalk(body));
	}
}
