package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyStructureRuleTest {
	@Test
	void detectsBodyStructureFlags() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setHasBraid(false);
		body.setHasPants(false);
		body.setHasBaby(false);
		body.setHasStalk(false);
		body.setAnalClose(false);
		body.setBodyCastration(false);
		body.setStalkCastration(false);

		assertFalse(BodyStructureRule.isHasBraid(body));
		assertFalse(BodyStructureRule.isHasPants(body));
		assertFalse(BodyStructureRule.isHasBaby(body));
		assertFalse(BodyStructureRule.isHasStalk(body));
		assertFalse(BodyStructureRule.isAnalClose(body));
		assertFalse(BodyStructureRule.isBodyCastration(body));
		assertFalse(BodyStructureRule.isStalkCastration(body));

		body.setHasBraid(true);
		body.setHasPants(true);
		body.setHasBaby(true);
		body.setHasStalk(true);
		body.setAnalClose(true);
		body.setBodyCastration(true);
		body.setStalkCastration(true);

		assertTrue(BodyStructureRule.isHasBraid(body));
		assertTrue(BodyStructureRule.isHasPants(body));
		assertTrue(BodyStructureRule.isHasBaby(body));
		assertTrue(BodyStructureRule.isHasStalk(body));
		assertTrue(BodyStructureRule.isAnalClose(body));
		assertTrue(BodyStructureRule.isBodyCastration(body));
		assertTrue(BodyStructureRule.isStalkCastration(body));
	}
}
