package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;

public class BodyLayerTest {

	@Test
	public void testConstructorInitializesArrays() {
		YukkuriLayer layer = new YukkuriLayer();

		assertEquals(10, layer.getImage().length);
		assertEquals(10, layer.getDir().length);
		assertEquals(10, layer.getOption().length);
		assertNull(layer.getImage()[0]);
		assertEquals(0, layer.getDir()[0]);
		assertEquals(0, layer.getOption()[0]);
	}

	@Test
	public void testClearResetsInjectedArrays() {
		YukkuriLayer layer = new YukkuriLayer();
		BufferedImage[] images = {
				new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
				new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
				new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
		};
		int[] dirs = { 1, 2, 3 };
		int[] options = { 4, 5, 6 };

		layer.setImage(images);
		layer.setDir(dirs);
		layer.setOption(options);

		layer.clear();

		assertNull(images[0]);
		assertNull(images[1]);
		assertNull(images[2]);
		assertEquals(0, dirs[0]);
		assertEquals(0, dirs[1]);
		assertEquals(0, dirs[2]);
		assertEquals(0, options[0]);
		assertEquals(0, options[1]);
		assertEquals(0, options[2]);
	}
}
