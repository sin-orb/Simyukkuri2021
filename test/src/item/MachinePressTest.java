package src.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import src.SimYukkuri;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.world.WorldEntity;
import src.entity.core.world.item.MachinePress;
import src.entity.core.world.item.Stone;
import src.base.ItemTestBase;
import src.enums.WorldEntityKind;
import src.enums.Type;
import src.util.WorldTestHelper;

class MachinePressTest extends ItemTestBase {
    @Test
    void testConstructor_Default() {
        MachinePress item = new MachinePress();
        item.setObjId(1);
        SimYukkuri.world.getCurrentMap().getMachinePress().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getMachinePress().containsKey(item.getObjId()));
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
        assertTrue(SimYukkuri.world.getCurrentMap().getMachinePress().containsKey(item.getObjId()));
    }

    @Test
    void testGetHitCheckObjType() {
        MachinePress item = new MachinePress();
        int expected = WorldEntity.YUKKURI | WorldEntity.SHIT | WorldEntity.VOMIT;
        assertEquals(expected, item.getHitCheckObjType());
    }

    @Test
    void testGetShadowImageIsNull() {
        MachinePress item = new MachinePress();
        assertNull(item.getShadowImage());
    }

    @Test
    void testRemoveListData() {
        MachinePress item = new MachinePress(50, 50, 0);
        int id = item.getObjId();
        assertTrue(SimYukkuri.world.getCurrentMap().getMachinePress().containsKey(id));
        item.removeListData();
        assertFalse(SimYukkuri.world.getCurrentMap().getMachinePress().containsKey(id));
    }

    @Test
    void testUpDateNoEffect() {
        MachinePress item = new MachinePress(50, 50, 0);
        // age=0なので2400の倍数 → Cash.addCash(-getCost())が呼ばれる。enabled=trueのため
        // ただし例外が出ないことを確認
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testUpDateDisabled() {
        MachinePress item = new MachinePress(50, 50, 0);
        item.setEnabled(false);
        item.setAge(2400);
        // disabled時はupDate何もしない
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testObjHitProcessWithNonYukkuri() {
        MachinePress machine = new MachinePress(50, 50, 0);
        Stone stone = new Stone(50, 50, 0);
        // Stone は YUKKURI タイプでないので早期リターン (return 0)
        assertEquals(0, machine.objHitProcess(stone));
    }

    @Test
    void testGetImageLayer_enabled_doesNotThrow() {
        MachinePress item = new MachinePress(50, 50, 0);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    @Test
    void testGetImageLayer_disabled_doesNotThrow() {
        MachinePress item = new MachinePress(50, 50, 0);
        item.setEnabled(false);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    @Test
    void testGetBounding_doesNotThrow() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> MachinePress.getBounding());
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
