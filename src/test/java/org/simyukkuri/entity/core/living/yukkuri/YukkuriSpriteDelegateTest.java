package org.simyukkuri.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class YukkuriSpriteDelegateTest {
	@Test
	void isUnyoActionAllReturnsTrueWhenShitting() {
		StubBody body = new StubBody();
		body.setShitting(true);

		assertTrue(new YukkuriSpriteDelegate(body).isUnyoActionAll());
	}

	@Test
	void resetUnyoClearsOffsets() {
		StubBody body = new StubBody();
		body.setUnyoOffsetH(10);
		body.setUnyoOffsetW(20);

		new YukkuriSpriteDelegate(body).resetUnyo();

		assertEquals(0, body.getUnyoOffsetH());
		assertEquals(0, body.getUnyoOffsetW());
	}
}
