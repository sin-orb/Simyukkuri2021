package org.simyukkuri.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;

import org.junit.jupiter.api.Test;

class YukkuriMessageTest {
	@Test
	void setMessageDelegatesToBodyEventState() {
		StubBody body = new StubBody();
		body.setCanTalk(true);
		body.setSilent(false);
		body.setUnBirth(false);

		new YukkuriMessage(body).setMessage("hello", true);

		assertEquals("hello", body.getMessageBuffer());
		assertTrue(body.getMessageTicks() > 0);
	}

	@Test
	void setOrigMessageLineColorDelegatesToLivingEntityColor() {
		StubBody body = new StubBody();

		new YukkuriMessage(body).setOrigMessageLineColor(new Color(1, 2, 3, 4));

		assertEquals(1, body.getMessageLineColor().getRed());
		assertEquals(2, body.getMessageLineColor().getGreen());
		assertEquals(3, body.getMessageLineColor().getBlue());
		assertEquals(4, body.getMessageLineColor().getAlpha());
	}
}
