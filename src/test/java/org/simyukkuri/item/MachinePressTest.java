package org.simyukkuri.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.entity.core.world.item.MachinePress;
import org.simyukkuri.entity.core.world.item.Stone;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.util.WorldTestHelper;

class MachinePressTest extends ItemTestBase {
    @Test
    void testConstructor_Default() {
        MachinePress item = new MachinePress();
        item.setObjId(1);
        SimYukkuri.world.getCurrentWorldState().getMachinePresses().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getMachinePresses().containsKey(item.getObjId()));
    }

    @Test
    void testConstructorWithCoords() {
        MachinePress item = new MachinePress(100, 200, 0);
        assertEquals(100, item.getX());
        assertEquals(200, item.getY());
        assertEquals(Type.FIX_OBJECT, item.getObjType());
        assertEquals(WorldEntityKind.MACHINEPRESS, item.getWorldEntityType());
        assertEquals(500000, item.getValue());
        assertEquals(1500, item.getCost());
        assertTrue(SimYukkuri.world.getCurrentWorldState().getMachinePresses().containsKey(item.getObjId()));
    }

    @Test
    void testGetHitCheckObjType() {
        MachinePress item = new MachinePress();
        int expected = WorldEntity.YUKKURI | WorldEntity.SHIT | WorldEntity.VOMIT;
        assertEquals(expected, item.getHitCheckObjType(),
                "hitCheckObjType が YUKKURI|SHIT|VOMIT の組み合わせであること");
        // 各ビットが正しく含まれること
        assertTrue((item.getHitCheckObjType() & WorldEntity.YUKKURI) != 0, "YUKKURI ビットが含まれること");
        assertTrue((item.getHitCheckObjType() & WorldEntity.SHIT)   != 0, "SHIT ビットが含まれること");
        assertTrue((item.getHitCheckObjType() & WorldEntity.VOMIT)  != 0, "VOMIT ビットが含まれること");
    }

    @Test
    void testGetShadowImageIsNull() {
        // MachinePress は影なし仕様
        MachinePress item = new MachinePress();
        assertNull(item.getShadowImage(), "MachinePress の影画像は null（影なし仕様）であること");
    }

    @Test
    void testRemoveListData() {
        MachinePress item = new MachinePress(50, 50, 0);
        int id = item.getObjId();
        assertTrue(SimYukkuri.world.getCurrentWorldState().getMachinePresses().containsKey(id),
                "removeFromWorld 前は machinePresses に存在すること");
        item.removeFromWorld();
        assertFalse(SimYukkuri.world.getCurrentWorldState().getMachinePresses().containsKey(id),
                "removeFromWorld 後は machinePresses から除去されること");
    }

    @Test
    void testUpDateNoEffect() {
        MachinePress item = new MachinePress(50, 50, 0);
        // age=0 は 2400 の倍数のため cash が引かれること
        long cashBefore = SimYukkuri.world.getPlayer().getCash();
        item.upDate();
        // age=0 で enabled なら cost が引かれること
        assertEquals(cashBefore - item.getCost(), SimYukkuri.world.getPlayer().getCash(),
                "age=0(2400倍数) で enabled なら cost が引かれること");
    }

    @Test
    void testUpDateDisabled() {
        MachinePress item = new MachinePress(50, 50, 0);
        item.setEnabled(false);
        item.setAge(2400);
        long cashBefore = SimYukkuri.world.getPlayer().getCash();
        item.upDate();
        // disabled 時は何もしないため cash が変化しないこと
        assertEquals(cashBefore, SimYukkuri.world.getPlayer().getCash(),
                "disabled 時は upDate で cash が変化しないこと");
    }

    @Test
    void testObjHitProcessWithNonYukkuri() {
        MachinePress machine = new MachinePress(50, 50, 0);
        Stone stone = new Stone(50, 50, 0);
        // Stone は YUKKURI タイプでないので早期リターン (return 0)
        assertEquals(0, machine.objHitProcess(stone), "非 YUKKURI オブジェクトでは 0 を返すこと");
        assertFalse(stone.isRemoved(), "非 YUKKURI オブジェクトは除去されないこと");
    }

    @Test
    void testGetImageLayer_enabled_doesNotThrow() {
        MachinePress item = new MachinePress(50, 50, 0);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        int count = item.getImageLayer(layer);
        assertEquals(1, count, "enabled 状態で getImageLayer が 1 を返すこと");
    }

    @Test
    void testGetImageLayer_disabled_doesNotThrow() {
        MachinePress item = new MachinePress(50, 50, 0);
        item.setEnabled(false);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        int count = item.getImageLayer(layer);
        assertEquals(1, count, "disabled 状態でも getImageLayer が 1 を返すこと");
    }

    @Test
    void testGetBounding_doesNotThrow() {
        assertNotNull(MachinePress.getBounding(), "getBounding が非null を返すこと");
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_PressCycleSilencesAndDamagesHealthyBody() {
            MachinePress machine = new MachinePress(50, 50, 0);
            Yukkuri body = WorldTestHelper.createBody();
            body.setSilent(false);
            int beforeDamage = body.getDamage();

            assertEquals(0, machine.objHitProcess(body));

            assertTrue(body.isSilent(), "machine press should silence a body on the crushing frame");
            assertTrue(body.getDamage() > beforeDamage, "machine press should increase damage through strikeByPress");
        }

        @Test
        void testScenario_UpdateAtBillingTickConsumesCashWhenEnabled() {
            MachinePress machine = new MachinePress(50, 50, 0);
            machine.setAge(2400);
            long beforeCash = SimYukkuri.world.getPlayer().getCash();

            machine.upDate();

            assertEquals(beforeCash - machine.getCost(), SimYukkuri.world.getPlayer().getCash(),
                    "enabled machine press should charge its running cost every 2400 ticks");
        }
    }
}
