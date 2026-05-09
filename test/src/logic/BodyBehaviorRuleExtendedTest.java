package src.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyBehaviorRuleExtendedTest {
	@Test
	void delegatesBehaviorProfileValues() {
		StubBodyAttributes body = new StubBodyAttributes();

		BodyBehaviorRule.setBraidBreakChance(body, 11);
		BodyBehaviorRule.setSurisuriAccidentProb(body, 22);
		BodyBehaviorRule.setCarAccidentProb(body, 33);
		BodyBehaviorRule.setBreakBodyByShitProb(body, 44);
		BodyBehaviorRule.setDiarrheaProb(body, 55);
		BodyBehaviorRule.setExciteProb(body, 66);
		BodyBehaviorRule.setImmunityStrength(body, 77);
		BodyBehaviorRule.setAttitudePoint(body, 88);
		BodyBehaviorRule.setPregnantLimit(body, 99);

		assertEquals(11, BodyBehaviorRule.getBraidBreakChance(body));
		assertEquals(22, BodyBehaviorRule.getSurisuriAccidentProb(body));
		assertEquals(33, BodyBehaviorRule.getCarAccidentProb(body));
		assertEquals(44, BodyBehaviorRule.getBreakBodyByShitProb(body));
		assertEquals(55, BodyBehaviorRule.getDiarrheaProb(body));
		assertEquals(66, BodyBehaviorRule.getExciteProb(body));
		assertEquals(77, BodyBehaviorRule.getImmunityStrength(body));
		assertEquals(88, BodyBehaviorRule.getAttitudePoint(body));
		assertEquals(99, BodyBehaviorRule.getPregnantLimit(body));
	}
}
