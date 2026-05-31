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

		// 注入した配列のサイズが維持されていること
		assertEquals(3, layer.getImage().length,  "注入した images のサイズが維持されること");
		assertEquals(3, layer.getDir().length,    "注入した dirs のサイズが維持されること");
		assertEquals(3, layer.getOption().length, "注入した options のサイズが維持されること");

		// layer.getImage() を通じて全要素が null/0 であること
		for (int i = 0; i < 3; i++) {
			assertNull(layer.getImage()[i], "clear 後 image[" + i + "] が null になること");
			assertEquals(0, layer.getDir()[i],    "clear 後 dir[" + i + "] が 0 になること");
			assertEquals(0, layer.getOption()[i], "clear 後 option[" + i + "] が 0 になること");
		}
	}
}
