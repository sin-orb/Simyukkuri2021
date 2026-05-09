package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;
import src.attachment.Ants;

class BodyAnimalRuleTest {
	@Test
	void detectsAnimalEatenStateFromAntAttachments() {
		StubBodyAttributes body = new StubBodyAttributes();

		assertFalse(BodyAnimalRule.isEatenByAnimals(body));

		body.addAttachment(new Ants());
		assertTrue(BodyAnimalRule.isEatenByAnimals(body));
	}
}
