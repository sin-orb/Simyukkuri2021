package org.simyukkuri.effect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.TerrariumTickProcessor;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.effect.impl.Hit;
import org.simyukkuri.entity.core.effect.impl.BakeSmoke;
import org.simyukkuri.entity.core.effect.impl.Mix;
import org.simyukkuri.entity.core.effect.impl.Steam;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameWorld;
import org.simyukkuri.util.WorldTestHelper;

public class EffectCoverageTest {

    private BufferedImage dummyImage;
    private BufferedImage[] dummyImages;
    private BufferedImage[][] dummyImages2D;

    @BeforeEach
    public void setUp() throws Exception {
        WorldTestHelper.resetWorld();
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
        assertEquals(dummyImages[0], effect.getImage());
        assertEquals(3, effect.getFrames());
        assertEquals(0, effect.getDirection());
    }

    @Test
    public void testSteam() {
        Steam.setImageLayers(dummyImages);
        Steam effect = new Steam(0, 0, 0, 0, 0, 0, false, 10, 0, false, false, false);
        assertEquals(dummyImages[0], effect.getImage());
        assertEquals(1, effect.getFrames());
        assertEquals(0, effect.getDirection());
    }

    @Test
    public void testMix() {
        Mix.setImageLayers(dummyImages);
        Mix effect = new Mix(0, 0, 0, 0, 0, 0, false, 10, 0, false, false, false);
        assertEquals(dummyImages[0], effect.getImage());
        assertEquals(3, effect.getFrames());
        assertEquals(0, effect.getDirection());
    }

    @Test
    public void testHit() {
        Hit.setImageLayers(dummyImages2D);
        Hit effect = new Hit(0, 0, 0, 0, 0, 0, false, 10, 0, false, false, false);
        assertEquals(dummyImages2D[0][0], effect.getImage());
        effect.setDirection(1);
        effect.setAnimeFrame(3);
        assertEquals(dummyImages2D[1][3], effect.getImage());
        assertEquals(4, effect.getFrames());
    }

    @Test
    public void testHitIsRemovedFromWorldStateAfterLifetimeExpires() {
        Hit.setImageLayers(dummyImages2D);
        WorldState curMap = GameWorld.getCurrentWorldState();

        Hit effect = new Hit(0, 0, 0, 0, 0, 0, false, 0, 0, true, false, false);

        assertTrue(curMap.getSortedEffects().containsKey(effect.getObjId()));
        assertTrue(curMap.getEntityIndex().containsKey(effect.getObjId()));

        TerrariumTickProcessor.processWorldTicks(curMap, 0);

        assertFalse(curMap.getSortedEffects().containsKey(effect.getObjId()));
        assertFalse(curMap.getFrontEffects().containsKey(effect.getObjId()));
        assertFalse(curMap.getEntityIndex().containsKey(effect.getObjId()));
        assertTrue(effect.isRemoved());
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
