package src.util;

import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

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
