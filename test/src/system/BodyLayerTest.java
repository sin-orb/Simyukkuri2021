package src.system;

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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;

public class BodyLayerTest {

    @Test
    public void testConstructorInitializesArrays() {
        BodyLayer layer = new BodyLayer();
        assertNotNull(layer.getImage());
        assertNotNull(layer.getDir());
        assertNotNull(layer.getOption());
        assertEquals(10, layer.getImage().length);
        assertEquals(10, layer.getDir().length);
        assertEquals(10, layer.getOption().length);
    }

    @Test
    public void testSetAndGetImage() {
        BodyLayer layer = new BodyLayer();
        BufferedImage[] images = new BufferedImage[5];
        images[0] = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        layer.setImage(images);
        assertSame(images, layer.getImage());
    }

    @Test
    public void testSetAndGetDir() {
        BodyLayer layer = new BodyLayer();
        int[] dirs = { 1, 0, 1, 0 };
        layer.setDir(dirs);
        assertSame(dirs, layer.getDir());
    }

    @Test
    public void testSetAndGetOption() {
        BodyLayer layer = new BodyLayer();
        int[] opts = { 2, 3, 4 };
        layer.setOption(opts);
        assertSame(opts, layer.getOption());
    }
}
