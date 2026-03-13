package src.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import src.SimYukkuri;
import src.base.Body;
import src.base.ItemTestBase;
import src.base.ObjEX;
import src.enums.ObjEXType;
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
        assertEquals(ObjEXType.MACHINEPRESS, item.getObjEXType());
        assertEquals(500000, item.getValue());
        assertEquals(1500, item.getCost());
        assertTrue(SimYukkuri.world.getCurrentMap().getMachinePress().containsKey(item.getObjId()));
    }

    @Test
    void testGetHitCheckObjType() {
        MachinePress item = new MachinePress();
        int expected = ObjEX.YUKKURI | ObjEX.SHIT | ObjEX.VOMIT;
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
}
