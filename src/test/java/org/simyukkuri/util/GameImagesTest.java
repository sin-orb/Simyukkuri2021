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

import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class GameImagesTest {

	@AfterEach
	public void tearDown() {
		GameImages.clearOverride();
	}

	@Test
	public void testReadUsesOverrideWhenSet() throws IOException {
		final BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		GameImages.setOverride(new ImageSource() {
			@Override
			public BufferedImage read(InputStream input) throws IOException {
				return image;
			}

			@Override
			public BufferedImage read(File file) throws IOException {
				return image;
			}
		});

		assertSame(image, GameImages.read((InputStream) null));
	}

	@Test
	public void testReadFileUsesOverrideWhenSet() throws IOException {
		final BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		GameImages.setOverride(new ImageSource() {
			@Override
			public BufferedImage read(InputStream input) throws IOException {
				return image;
			}

			@Override
			public BufferedImage read(File file) throws IOException {
				return image;
			}
		});

		assertSame(image, GameImages.read((File) null));
	}
}
