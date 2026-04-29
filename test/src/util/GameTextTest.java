package src.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class GameTextTest {

	@AfterEach
	public void tearDown() {
		GameText.clearOverride();
	}

	@Test
	public void testReadUsesOverrideWhenSet() {
		GameText.setOverride(new TextSource() {
			@Override
			public String read(String property) {
				return "fake:" + property;
			}
		});

		assertEquals("fake:title", GameText.read("title"));
	}
}
