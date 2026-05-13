package src.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class YukkuriNydDelegateTest {
	@Test
	void checkNonYukkuriDisease_defaultBodyReturnsFalse() {
		StubBody body = new StubBody();

		assertFalse(new YukkuriNydDelegate(body).checkNonYukkuriDisease());
		assertTrue(body.isNotNYD());
	}
}
