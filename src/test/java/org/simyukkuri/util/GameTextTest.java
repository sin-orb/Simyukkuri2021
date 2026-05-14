package org.simyukkuri.util;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

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
