package org.simyukkuri.effect;

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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.effect.impl.BakeSmoke;
import org.simyukkuri.entity.core.effect.impl.Hit;
import org.simyukkuri.entity.core.effect.impl.Mix;
import org.simyukkuri.entity.core.effect.impl.Steam;
import org.simyukkuri.util.WorldTestHelper;

public class EffectCoverageTest {

    private BufferedImage dummyImage;
    private BufferedImage[] dummyImages;
    private BufferedImage[][] dummyImages2D;

    @BeforeEach
    public void setUp() throws Exception {
        WorldTestHelper.initializeMinimalWorld();
        // Force world initialization if static helper didn't handle it for some reason
        if (SimYukkuri.world == null) {
            World world = new World(0, 0);
            java.lang.reflect.Field worldField = SimYukkuri.class.getDeclaredField("world");
            worldField.setAccessible(true);
            worldField.set(null, world);
        }

        dummyImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        dummyImages = new BufferedImage[] {
                dummyImage, dummyImage, dummyImage, dummyImage, dummyImage,
                dummyImage, dummyImage, dummyImage, dummyImage, dummyImage, dummyImage
        };
        dummyImages2D = new BufferedImage[][] {
                { dummyImage, dummyImage, dummyImage, dummyImage },
                { dummyImage, dummyImage, dummyImage, dummyImage }
        };
    }

    @Test
    public void testBakeSmoke() {
        BakeSmoke.setImageLayers(dummyImages);
        BakeSmoke effect = new BakeSmoke(0, 0, 0, 0, 0, 0, false, 10, 0, false, false, false);
        assertNotNull(effect.getImage());
        new BakeSmoke();
    }

    @Test
    public void testSteam() {
        Steam.setImageLayers(dummyImages);
        Steam effect = new Steam(0, 0, 0, 0, 0, 0, false, 10, 0, false, false, false);
        assertNotNull(effect.getImage());
        new Steam();
    }

    @Test
    public void testMix() {
        Mix.setImageLayers(dummyImages);
        Mix effect = new Mix(0, 0, 0, 0, 0, 0, false, 10, 0, false, false, false);
        assertNotNull(effect.getImage());
        new Mix();
    }

    @Test
    public void testHit() {
        Hit.setImageLayers(dummyImages2D);
        Hit effect = new Hit(0, 0, 0, 0, 0, 0, false, 10, 0, false, false, false);
        assertNotNull(effect.getImage());
        new Hit();
    }

    @Test
    public void testGetImages() {
        BakeSmoke.setImageLayers(dummyImages);
        assertSame(dummyImages, BakeSmoke.getImageLayers());

        Mix.setImageLayers(dummyImages);
        assertSame(dummyImages, Mix.getImageLayers());

        Steam.setImageLayers(dummyImages);
        assertSame(dummyImages, Steam.getImageLayers());

        Hit.setImageLayers(dummyImages2D);
        assertSame(dummyImages2D, Hit.getImageLayers());
    }
}
