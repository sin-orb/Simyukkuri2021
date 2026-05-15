package org.simyukkuri.entity.core.effect;

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

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.effect.Effect;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.Type;

public class EffectTest {

    private static class DummyEffect extends Effect {
        DummyEffect(int startX, int startY, int startZ, int velocityX, int velocityY, int velocityZ, boolean invert,
                int life, int loop, boolean end, boolean grav, boolean front) {
            super(startX, startY, startZ, velocityX, velocityY, velocityZ, invert, life, loop, end, grav, front);
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
        assertTrue(SimYukkuri.world.getCurrentWorldState().getFrontEffects().containsKey(effect.objId));
        assertEquals(Type.LIGHT_EFFECT, effect.getObjType());
        assertEquals(1, effect.getDirection());
        assertEquals(10, effect.getLifeTime());
        assertFalse(effect.isAnimate());
        assertFalse(effect.isEnableGravity());
    }

    @Test
    public void testConstructorRegistersSortEffectWhenBack() {
        DummyEffect effect = new DummyEffect(0, 0, 0, 0, 0, 0, false, 5, 1, false, false, false);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getSortedEffects().containsKey(effect.objId));
        assertEquals(0, effect.getDirection());
    }

    @Test
    public void testClockTickRemovesWhenLifetimeExpired() {
        DummyEffect effect = new DummyEffect(0, 0, 0, 0, 0, 0, false, 0, 0, false, false, true);
        assertEquals(TickResult.REMOVED, effect.clockTick());
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

        assertEquals(TickResult.NONE, effect.clockTick());
        assertEquals(1, effect.getAnimeFrame());
        assertTrue(effect.isAnimate());

        assertEquals(TickResult.NONE, effect.clockTick());
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

        assertEquals(TickResult.REMOVED, effect.clockTick());
    }

    @Test
    public void testClockTickGravityAffectsZ() {
        DummyEffect effect = new DummyEffect(0, 0, 10, 0, 0, 0, false, -1, 0, false, true, true);
        assertEquals(10, effect.getZ());
        assertEquals(0, effect.getVxyz()[2]);

        assertEquals(TickResult.NONE, effect.clockTick());
        assertEquals(1, effect.getVxyz()[2]);
        assertEquals(9, effect.getZ());
    }

    // --- Missing getter/setter coverage ---

    @Test
    public void testSetDirection_firesProbe() {
        DummyEffect effect = new DummyEffect(0, 0, 0, 0, 0, 0, false, 5, 0, false, false, true);
        effect.setDirection(3);
        assertEquals(3, effect.getDirection());
    }

    @Test
    public void testGetInterval_firesProbe() {
        DummyEffect effect = new DummyEffect(0, 0, 0, 0, 0, 0, false, 5, 0, false, false, true);
        effect.setInterval(7);
        assertEquals(7, effect.getInterval());
    }

    @Test
    public void testGetFrames_firesProbe() {
        DummyEffect effect = new DummyEffect(0, 0, 0, 0, 0, 0, false, 5, 0, false, false, true);
        effect.setFrames(4);
        assertEquals(4, effect.getFrames());
    }

    @Test
    public void testSetLifeTime_firesProbe() {
        DummyEffect effect = new DummyEffect(0, 0, 0, 0, 0, 0, false, 5, 0, false, false, true);
        effect.setLifeTime(20);
        assertEquals(20, effect.getLifeTime());
    }

    @Test
    public void testGetAnimeInterval_firesProbe() {
        DummyEffect effect = new DummyEffect(0, 0, 0, 0, 0, 0, false, 5, 0, false, false, true);
        effect.setAnimeInterval(3);
        assertEquals(3, effect.getAnimeInterval());
    }

    @Test
    public void testGetAnimeLoop_firesProbe() {
        DummyEffect effect = new DummyEffect(0, 0, 0, 0, 0, 0, false, 5, 0, false, false, true);
        effect.setAnimeLoop(2);
        assertEquals(2, effect.getAnimeLoop());
    }

    @Test
    public void testIsAnimeEnd_firesProbe() {
        DummyEffect effect = new DummyEffect(0, 0, 0, 0, 0, 0, false, 5, 0, false, false, true);
        effect.setAnimeEnd(true);
        assertTrue(effect.isAnimeEnd());
        effect.setAnimeEnd(false);
        assertFalse(effect.isAnimeEnd());
    }

    @Test
    public void testSetEnableGravity_firesProbe() {
        DummyEffect effect = new DummyEffect(0, 0, 0, 0, 0, 0, false, 5, 0, false, false, true);
        effect.setEnableGravity(true);
        assertTrue(effect.isEnableGravity());
        effect.setEnableGravity(false);
        assertFalse(effect.isEnableGravity());
    }
}
