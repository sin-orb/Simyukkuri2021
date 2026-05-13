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
