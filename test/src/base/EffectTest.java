package src.base;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.draw.World;
import src.enums.Event;
import src.enums.Type;

public class EffectTest {

    private static class DummyEffect extends Effect {
        DummyEffect(int sX, int sY, int sZ, int vX, int vY, int vZ, boolean invert,
                    int life, int loop, boolean end, boolean grav, boolean front) {
            super(sX, sY, sZ, vX, vY, vZ, invert, life, loop, end, grav, front);
        }

        @Override
        public BufferedImage getImage() {
            return null;
        }
    }

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
    }

    @Test
    public void testConstructorRegistersFrontEffectAndInitializesFields() {
        DummyEffect effect = new DummyEffect(1, 2, 3, 4, 5, 6, true, 10, 0, false, false, true);
        assertTrue(SimYukkuri.world.getCurrentMap().getFrontEffect().containsKey(effect.objId));
        assertEquals(Type.LIGHT_EFFECT, effect.objType);
        assertEquals(1, effect.getDirection());
        assertEquals(10, effect.getLifeTime());
        assertFalse(effect.isAnimate());
        assertFalse(effect.isEnableGravity());
    }

    @Test
    public void testConstructorRegistersSortEffectWhenBack() {
        DummyEffect effect = new DummyEffect(0, 0, 0, 0, 0, 0, false, 5, 1, false, false, false);
        assertTrue(SimYukkuri.world.getCurrentMap().getSortEffect().containsKey(effect.objId));
        assertEquals(0, effect.getDirection());
    }

    @Test
    public void testClockTickRemovesWhenLifetimeExpired() {
        DummyEffect effect = new DummyEffect(0, 0, 0, 0, 0, 0, false, 0, 0, false, false, true);
        assertEquals(Event.REMOVED, effect.clockTick());
    }

    @Test
    public void testClockTickAnimateLoopEndsStopsAnimation() {
        DummyEffect effect = new DummyEffect(0, 0, 0, 0, 0, 0, false, -1, 1, false, false, true);
        effect.setAnimate(true);
        effect.setInterval(0);
        effect.setFrames(2);
        effect.setAnimeFrame(0);
        effect.setAnimeInterval(0);
        effect.setAnimeLoop(1);
        effect.setAnimeEnd(false);

        assertEquals(Event.DONOTHING, effect.clockTick());
        assertEquals(1, effect.getAnimeFrame());
        assertTrue(effect.isAnimate());

        assertEquals(Event.DONOTHING, effect.clockTick());
        assertEquals(0, effect.getAnimeFrame());
        assertFalse(effect.isAnimate());
    }

    @Test
    public void testClockTickAnimateEndRemovesOnComplete() {
        DummyEffect effect = new DummyEffect(0, 0, 0, 0, 0, 0, false, -1, 1, true, false, true);
        effect.setAnimate(true);
        effect.setInterval(0);
        effect.setFrames(1);
        effect.setAnimeFrame(0);
        effect.setAnimeInterval(0);
        effect.setAnimeLoop(-1);
        effect.setAnimeEnd(true);

        assertEquals(Event.REMOVED, effect.clockTick());
    }

    @Test
    public void testClockTickGravityAffectsZ() {
        DummyEffect effect = new DummyEffect(0, 0, 10, 0, 0, 0, false, -1, 0, false, true, true);
        assertEquals(10, effect.getZ());
        assertEquals(0, effect.getVxyz()[2]);

        assertEquals(Event.DONOTHING, effect.clockTick());
        assertEquals(1, effect.getVxyz()[2]);
        assertEquals(9, effect.getZ());
    }
}
