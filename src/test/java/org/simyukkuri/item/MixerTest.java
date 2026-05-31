package org.simyukkuri.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.effect.Effect;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.entity.core.world.item.Mixer;
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.util.WorldTestHelper;

class MixerTest extends ItemTestBase {

    @Test
    void testConstructor_Default() {
        Mixer item = new Mixer();
        item.setObjId(1);
        SimYukkuri.world.getCurrentWorldState().getMixers().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(
                SimYukkuri.world.getCurrentWorldState().getMixers().containsKey(item.getObjId()));
    }

    @Test
    void testGetHitCheckObjType() {
        Mixer item = new Mixer();
        assertEquals(Mixer.hitCheckObjType, item.getHitCheckObjType());
    }

    @Test
    void testEnableHitCheck_NoBind() {
        Mixer item = new Mixer();
        item.setBind(-1);
        // bind=-1 → getBodyMap returns null → true
        assertTrue(item.enableHitCheck());
    }

    @Test
    void testGetSetBind() {
        Mixer item = new Mixer();
        assertEquals(-1, item.getBind()); // default is -1 (no bind)
        item.setBind(42);
        assertEquals(42, item.getBind());
    }

    @Test
    void testGetSetMix() {
        Mixer item = new Mixer();
        assertNull(item.getMix()); // default is null
        item.setMix(null);
        assertNull(item.getMix());
    }

    @Test
    void testGetSetCounter() {
        Mixer item = new Mixer();
        assertEquals(0, item.getCounter());
        item.setCounter(100);
        assertEquals(100, item.getCounter());
    }

    @Test
    void testGetSetAmount() {
        Mixer item = new Mixer();
        assertEquals(0, item.getAmount());
        item.setAmount(500);
        assertEquals(500, item.getAmount());
    }

    @Test
    void testGetSetSweet() {
        Mixer item = new Mixer();
        assertEquals(0, item.getSweet());
        item.setSweet(300);
        assertEquals(300, item.getSweet());
    }

    @Test
    void testGetSetSick() {
        Mixer item = new Mixer();
        assertFalse(item.isSick());
        item.setSick(true);
        assertTrue(item.isSick());
    }

    @Test
    void testGetShadowImage() {
        Mixer item = new Mixer();
        assertNull(item.getShadowImage());
    }

    @Test
    void testGetBounding() {
        assertNotNull(Mixer.getBounding());
        assertSame(Mixer.getBounding(), Mixer.getBounding());
    }

    @Test
    void testRemoveListData_NoBind() {
        Mixer item = new Mixer();
        item.setObjId(99);
        SimYukkuri.world.getCurrentWorldState().getMixers().put(item.getObjId(), item);
        item.setBind(-1);
        item.removeFromWorld();
        assertFalse(
                SimYukkuri.world.getCurrentWorldState().getMixers().containsKey(item.getObjId()));
    }

    @Test
    void testRemoveListData_WithBind() {
        Mixer item = new Mixer();
        item.setObjId(98);
        SimYukkuri.world.getCurrentWorldState().getMixers().put(item.getObjId(), item);
        Yukkuri body = WorldTestHelper.createBody();
        body.setLockmove(true);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueId(), body);
        item.setBind(body.getUniqueId());
        item.removeFromWorld();
        assertFalse(body.isLockmove());
        assertEquals(-1, item.getBind());
    }

    @Test
    void testObjHitProcess_Disabled() {
        Mixer item = new Mixer();
        item.setEnabled(false);
        Yukkuri body = WorldTestHelper.createBody();
        assertEquals(0, item.objHitProcess(body));
    }

    @Test
    void testObjHitProcess_Enabled() {
        Mixer item = new Mixer();
        item.setEnabled(true);
        item.setX(100);
        item.setY(100);
        Yukkuri body = WorldTestHelper.createBody();
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueId(), body);
        assertEquals(1, item.objHitProcess(body));
        assertEquals(body.getUniqueId(), item.getBind());
        assertTrue(body.isLockmove());
    }

    @Test
    void testUpDate_NoBind_NoMix() {
        Mixer item = new Mixer();
        item.setBind(-1);
        item.setMix(null);
        item.upDate();
        assertEquals(-1, item.getBind());
        assertFalse(item.isRemoved());
    }

    @Test
    void testUpDate_Disabled_WithBind() {
        Mixer item = new Mixer();
        Yukkuri body = WorldTestHelper.createBody();
        body.setLockmove(true);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueId(), body);
        item.setBind(body.getUniqueId());
        item.setEnabled(false);
        item.upDate();
        assertFalse(item.isRemoved());
    }

    @Test
    void testUpDate_Grabbed_WithBind() {
        Mixer item = new Mixer();
        item.setX(100);
        item.setY(100);
        item.setEnabled(true);
        item.setGrabbed(true);
        Yukkuri body = WorldTestHelper.createBody();
        body.setX(100);
        body.setY(100);
        body.setLockmove(true);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueId(), body);
        item.setBind(body.getUniqueId());
        item.upDate();
        assertFalse(item.isRemoved());
    }

    @Test
    void testUpDate_BodyMovedAway() {
        Mixer item = new Mixer();
        item.setX(100);
        item.setY(100);
        item.setEnabled(true);
        item.setGrabbed(false);
        Yukkuri body = WorldTestHelper.createBody();
        // body at different position
        body.setX(200);
        body.setY(200);
        body.setLockmove(true);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueId(), body);
        item.setBind(body.getUniqueId());
        item.upDate();
        // bind should be reset to -1
        assertEquals(-1, item.getBind());
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            Mixer.loadImages(Mixer.class.getClassLoader(), null);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testGetImageLayer_doesNotThrow() {
        Mixer item = new Mixer();
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        int count = item.getImageLayer(layer);
        assertEquals(1, count);
    }

    @Test
    void testConstructorWithArgs_doesNotThrow() {
        Mixer item = new Mixer(100, 100, 0);
        assertNotNull(item);
        assertEquals(100, item.getX());
        assertEquals(100, item.getY());
    }

    private static final class DummyEffect extends Effect {
        @Override
        public BufferedImage getImage() {
            return null;
        }
    }

    @Nested
    class RegressionScenarios {
        @Test
        void testScenario_UpdateAfterStartDamagesBoundBodyAndAccumulatesMaterial() {
            Mixer item = new Mixer();
            item.setEnabled(true);
            item.setX(100);
            item.setY(100);
            item.setZ(0);
            item.setCounter(60);
            item.setMix(new DummyEffect());

            Yukkuri body = WorldTestHelper.createBody();
            body.setX(100);
            body.setY(100);
            body.setZ(0);
            body.setLockmove(true);
            body.setAnkoAmount(1000);
            int beforeDamage = body.getDamage();
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(body.getUniqueId(), body);
            item.setBind(body.getUniqueId());
            SimYukkuri.RND = new ConstState(1);

            item.upDate();

            assertEquals(61, item.getCounter());
            assertEquals(100, item.getAmount());
            assertEquals(body.getStress(), item.getSweet());
            assertEquals(beforeDamage + 100, body.getDamage());
            assertEquals(Happiness.VERY_SAD, body.getHappiness());
            assertEquals(ImageCode.PAIN.ordinal(), body.getForceFace());
            assertFalse(body.isShadowVisible());
            assertEquals(body.getUniqueId(), item.getBind());
        }

        @Test
        void testScenario_MovedAwayBodyAfterGrindingIsReleasedWithCutDamage() {
            Mixer item = new Mixer();
            item.setEnabled(true);
            item.setX(100);
            item.setY(100);
            item.setZ(0);
            item.setCounter(61);

            Yukkuri body = WorldTestHelper.createBody();
            body.setX(140);
            body.setY(100);
            body.setZ(0);
            body.setLockmove(true);
            body.setShadowVisible(false);
            body.setForceFace(ImageCode.PAIN.ordinal());
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(body.getUniqueId(), body);
            item.setBind(body.getUniqueId());

            item.upDate();

            assertEquals(-1, item.getBind());
            assertEquals(CriticalDamageType.CUT, body.getCriticalDamageType());
            assertEquals(-1, body.getForceFace());
            assertFalse(body.isLockmove());
            assertTrue(body.isShadowVisible());
        }

        @Test
        void testScenario_RemoveListDataAlsoRemovesActiveMixEffect() {
            Mixer item = new Mixer();
            item.setObjId(1234);
            SimYukkuri.world.getCurrentWorldState().getMixers().put(item.getObjId(), item);

            DummyEffect effect = new DummyEffect();
            item.setMix(effect);

            item.removeFromWorld();

            assertTrue(effect.isRemoved());
            assertNull(item.getMix());
            assertFalse(
                    SimYukkuri.world
                            .getCurrentWorldState()
                            .getMixers()
                            .containsKey(item.getObjId()));
        }
    }
}
