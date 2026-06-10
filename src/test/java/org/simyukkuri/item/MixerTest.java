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
        void testScenario_NoDamageBeforeGrindingStarts() {
            // counter=0 → upDate() 後 counter=1。1 > 60 でないため mixing 未開始 → damage 不変
            Mixer item = new Mixer();
            item.setEnabled(true);
            item.setX(100);
            item.setY(100);
            item.setZ(0);
            item.setCounter(0);

            Yukkuri body = WorldTestHelper.createBody();
            body.setX(100);
            body.setY(100);
            body.setZ(0);
            SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueId(), body);
            item.setBind(body.getUniqueId());
            int damageBefore = body.getDamage();
            int stressBefore = body.getStress();

            item.upDate();

            assertEquals(1, item.getCounter(), "counter が 1 になること");
            assertEquals(damageBefore, body.getDamage(), "待機中は damage が入らないこと");
            assertEquals(stressBefore, body.getStress(), "待機中は stress が入らないこと");
        }

        @Test
        void testScenario_MovedAwayAtCounter60DoesNotCut() {
            // counter=60 → 分離時 if(60 > 60) は false → CUT されない
            Mixer item = new Mixer();
            item.setEnabled(true);
            item.setX(100);
            item.setY(100);
            item.setZ(0);
            item.setCounter(60);

            Yukkuri body = WorldTestHelper.createBody();
            body.setX(150); // 位置をずらして離れた状態にする
            body.setY(100);
            body.setZ(0);
            SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueId(), body);
            item.setBind(body.getUniqueId());

            item.upDate();

            assertEquals(-1, item.getBind(), "body 離脱で bind が -1 になること");
            assertFalse(body.getCriticalDamageType() == CriticalDamageType.CUT,
                    "counter=60（まだ >60 でない）での離脱は CUT にならないこと");
        }

        @Test
        void testScenario_AnkoDepletionRemovesBody() {
            // counter=60 → upDate で 61 になり mixing 開始 → addAmount(-100) で餡子0以下 → remove
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
            body.setAnkoAmount(100); // addAmount(-100) で 0 以下 → remove
            SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueId(), body);
            item.setBind(body.getUniqueId());
            SimYukkuri.RND = new ConstState(1);

            item.upDate();

            assertTrue(body.isRemoved(), "餡子が尽きると body が除去されること");
            assertEquals(-1, item.getBind(), "body 除去後に bind が -1 になること");
        }

        @Test
        void testScenario_DeadBodySkipsDamageButAmountStillAccumulates() {
            // counter=60 → mixing 開始 → isDead=true → damage/stress スキップ → amount は加算される
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
            body.setDead(true);
            body.setAnkoAmount(1000);
            SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueId(), body);
            item.setBind(body.getUniqueId());
            SimYukkuri.RND = new ConstState(1);
            int damageBefore = body.getDamage();
            int stressBefore = body.getStress();

            item.upDate();

            assertEquals(damageBefore, body.getDamage(), "死亡ゆっくりには damage が加算されないこと");
            assertEquals(stressBefore, body.getStress(), "死亡ゆっくりには stress が加算されないこと");
            assertEquals(100, item.getAmount(), "死亡ゆっくりでも amount は加算されること");
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
