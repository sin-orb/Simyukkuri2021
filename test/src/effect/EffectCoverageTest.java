package src.effect;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import src.SimYukkuri;
import src.draw.World;
import src.util.WorldTestHelper;

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
        BakeSmoke.setImages(dummyImages);
        BakeSmoke effect = new BakeSmoke(0, 0, 0, 0, 0, 0, false, 10, 0, false, false, false);
        assertNotNull(effect.getImage());
        new BakeSmoke();
    }

    @Test
    public void testSteam() {
        Steam.setImages(dummyImages);
        Steam effect = new Steam(0, 0, 0, 0, 0, 0, false, 10, 0, false, false, false);
        assertNotNull(effect.getImage());
        new Steam();
    }

    @Test
    public void testMix() {
        Mix.setImages(dummyImages);
        Mix effect = new Mix(0, 0, 0, 0, 0, 0, false, 10, 0, false, false, false);
        assertNotNull(effect.getImage());
        new Mix();
    }

    @Test
    public void testHit() {
        Hit.setImages(dummyImages2D);
        Hit effect = new Hit(0, 0, 0, 0, 0, 0, false, 10, 0, false, false, false);
        assertNotNull(effect.getImage());
        new Hit();
    }

    @Test
    public void testGetImages() {
        BakeSmoke.setImages(dummyImages);
        assertSame(dummyImages, BakeSmoke.getImages());

        Mix.setImages(dummyImages);
        assertSame(dummyImages, Mix.getImages());

        Steam.setImages(dummyImages);
        assertSame(dummyImages, Steam.getImages());

        Hit.setImages(dummyImages2D);
        assertSame(dummyImages2D, Hit.getImages());
    }
}
