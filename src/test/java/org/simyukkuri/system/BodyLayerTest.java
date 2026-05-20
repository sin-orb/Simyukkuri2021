package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

public class BodyLayerTest {

    @Test
    public void testConstructorInitializesArrays() {
        YukkuriLayer layer = new YukkuriLayer();
        assertNotNull(layer.getImage());
        assertNotNull(layer.getDir());
        assertNotNull(layer.getOption());
        assertEquals(10, layer.getImage().length);
        assertEquals(10, layer.getDir().length);
        assertEquals(10, layer.getOption().length);
    }

    @Test
    public void testSetAndGetImage() {
        YukkuriLayer layer = new YukkuriLayer();
        BufferedImage[] images = new BufferedImage[5];
        images[0] = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        layer.setImage(images);
        assertSame(images, layer.getImage());
    }

    @Test
    public void testSetAndGetDir() {
        YukkuriLayer layer = new YukkuriLayer();
        int[] dirs = { 1, 0, 1, 0 };
        layer.setDir(dirs);
        assertSame(dirs, layer.getDir());
    }

    @Test
    public void testSetAndGetOption() {
        YukkuriLayer layer = new YukkuriLayer();
        int[] opts = { 2, 3, 4 };
        layer.setOption(opts);
        assertSame(opts, layer.getOption());
    }
}
