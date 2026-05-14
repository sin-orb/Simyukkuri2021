package org.simyukkuri.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class YukkuriSexualDelegateTest {
	@Test
	void forceToRaperExciteMakesBodyExcitingAndClearsPartner() {
		StubBody body = new StubBody();
		body.setDead(false);
		body.setExciting(false);
		body.setPenipeniCutted(false);
		body.setPartner(42);

		new YukkuriSexualDelegate(body).forceToRaperExcite(true);

		assertTrue(body.isExciting());
		assertTrue(body.isForceExciting());
		assertEquals(-1, body.getPartner());
	}

	@Test
	void doSukkiriSetsBothBodiesToSukkiri() {
		StubBody body = new StubBody();
		StubBody partner = new StubBody();
		body.setDead(false);
		partner.setDead(false);
		body.setHasPants(false);
		partner.setHasPants(false);

		new YukkuriSexualDelegate(body).doSukkiri(partner);

		assertTrue(body.isSukkiri());
		assertTrue(partner.isSukkiri());
	}

	@Test
	void doRapeSetsBothBodiesToSukkiri() {
		StubBody body = new StubBody();
		StubBody partner = new StubBody();
		body.setDead(false);
		partner.setDead(false);
		body.setHasPants(false);
		partner.setHasPants(false);
		partner.setRaper(false);

		new YukkuriSexualDelegate(body).doRape(partner);

		assertTrue(body.isSukkiri());
		assertTrue(partner.isSukkiri());
	}
}
