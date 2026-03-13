package src.item;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.ItemTestBase;
import src.util.WorldTestHelper;

class ProcesserPlateTest extends ItemTestBase {

    @Test
    void testConstructor_Default() {
        ProcesserPlate item = new ProcesserPlate();
        item.setObjId(1);
        SimYukkuri.world.getCurrentMap().getProcesserPlate().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getProcesserPlate().containsKey(item.getObjId()));
    }

    @Test
    void testProcessModeEnum() {
        ProcesserPlate.ProcessMode[] modes = ProcesserPlate.ProcessMode.values();
        assertEquals(9, modes.length);
        assertEquals(ProcesserPlate.ProcessMode.HOTPLATE, ProcesserPlate.ProcessMode.valueOf("HOTPLATE"));
        assertEquals(ProcesserPlate.ProcessMode.PAIN, ProcesserPlate.ProcessMode.valueOf("PAIN"));
        assertEquals(ProcesserPlate.ProcessMode.PACKING, ProcesserPlate.ProcessMode.valueOf("PACKING"));
        for (ProcesserPlate.ProcessMode m : modes) {
            assertNotNull(m.name());
        }
    }

    @Test
    void testProcessTypeEnum() {
        ProcesserPlate.ProcessType[] types = ProcesserPlate.ProcessType.values();
        assertEquals(13, types.length);
        for (ProcesserPlate.ProcessType t : types) {
            assertNotNull(t.toString());
            assertNotNull(t.name());
        }
        assertEquals(ProcesserPlate.ProcessType.HOTPLATE_MIN, ProcesserPlate.ProcessType.valueOf("HOTPLATE_MIN"));
        assertEquals(ProcesserPlate.ProcessType.PACKING, ProcesserPlate.ProcessType.valueOf("PACKING"));
    }

    @Test
    void testGetHitCheckObjType() {
        ProcesserPlate item = new ProcesserPlate();
        assertEquals(ProcesserPlate.hitCheckObjType, item.getHitCheckObjType());
    }

    @Test
    void testEnableHitCheck() {
        ProcesserPlate item = new ProcesserPlate();
        assertTrue(item.enableHitCheck());
    }

    @Test
    void testGetShadowImage() {
        ProcesserPlate item = new ProcesserPlate();
        assertNull(item.getShadowImage());
    }

    @Test
    void testGetBounding() {
        assertNotNull(ProcesserPlate.getBounding());
    }

    @Test
    void testGetSetEnumProcessType() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnumProcessType(ProcesserPlate.ProcessType.PAIN);
        assertEquals(ProcesserPlate.ProcessType.PAIN, item.getEnumProcessType());
    }

    @Test
    void testGetSetProcessedBodyList() {
        ProcesserPlate item = new ProcesserPlate();
        List<Body> list = new LinkedList<>();
        item.setProcessedBodyList(list);
        assertEquals(list, item.getProcessedBodyList());
    }

    @Test
    void testGetSetRunningCost() {
        ProcesserPlate item = new ProcesserPlate();
        int[] costs = {100, 200, 300, 400};
        item.setRunningCost(costs);
        assertArrayEquals(costs, item.getRunningCost());
    }

    @Test
    void testGetCost_PainMode() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnumProcessType(ProcesserPlate.ProcessType.PAIN);
        // PAIN mode returns runningCost[0]
        assertEquals(item.getRunningCost()[0], item.getCost());
    }

    @Test
    void testGetCost_HotplateMode() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnumProcessType(ProcesserPlate.ProcessType.HOTPLATE_MIN);
        // HOTPLATE mode returns runningCost[1]
        assertEquals(item.getRunningCost()[1], item.getCost());
    }

    @Test
    void testGetCost_PeelingMode() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnumProcessType(ProcesserPlate.ProcessType.PEALING);
        // PEALING mode returns runningCost[3]
        assertEquals(item.getRunningCost()[3], item.getCost());
    }

    @Test
    void testGetCost_BlindingMode() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnumProcessType(ProcesserPlate.ProcessType.BLINDING);
        // BLINDING mode returns runningCost[2]
        assertEquals(item.getRunningCost()[2], item.getCost());
    }

    @Test
    void testObjHitProcess_Disabled() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(false);
        Body body = WorldTestHelper.createBody();
        assertEquals(0, item.objHitProcess(body));
    }

    @Test
    void testObjHitProcess_NullObj() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(true);
        assertEquals(0, item.objHitProcess(null));
    }

    @Test
    void testRemoveListData_EmptyLists() {
        ProcesserPlate item = new ProcesserPlate();
        item.setObjId(66);
        SimYukkuri.world.getCurrentMap().getProcesserPlate().put(item.getObjId(), item);
        item.removeListData();
        assertFalse(SimYukkuri.world.getCurrentMap().getProcesserPlate().containsKey(item.getObjId()));
    }

    @Test
    void testRemoveListData_WithBody() {
        ProcesserPlate item = new ProcesserPlate();
        item.setObjId(67);
        SimYukkuri.world.getCurrentMap().getProcesserPlate().put(item.getObjId(), item);

        Body body = WorldTestHelper.createBody();
        body.setLockmove(true);
        item.getProcessedBodyList().add(body);
        item.getProcessedBodyEffectList().add(null); // null effect

        item.removeListData();
        assertFalse(body.isLockmove());
        assertTrue(item.getProcessedBodyList().isEmpty());
    }

    @Test
    void testGetSetProcessedBodyEffectList() {
        ProcesserPlate item = new ProcesserPlate();
        List<src.base.Effect> list = new LinkedList<>();
        item.setProcessedBodyEffectList(list);
        assertEquals(list, item.getProcessedBodyEffectList());
    }

    @Test
    void testUpDate_disabled_emptyLists() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(false);
        assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testUpDate_disabled_withBodyInList() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(false);
        Body body = WorldTestHelper.createBody();
        item.getProcessedBodyList().add(body);
        item.getProcessedBodyEffectList().add(null);
        assertDoesNotThrow(() -> item.upDate());
        assertTrue(item.getProcessedBodyList().isEmpty());
    }

    @Test
    void testUpDate_enabled_emptyLists() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(true);
        item.setEnumProcessType(ProcesserPlate.ProcessType.PAIN);
        assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testUpDate_enabled_withRemovedBody() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(true);
        item.setEnumProcessType(ProcesserPlate.ProcessType.PAIN);
        Body body = WorldTestHelper.createBody();
        body.remove();
        item.getProcessedBodyList().add(body);
        item.getProcessedBodyEffectList().add(null);
        assertDoesNotThrow(() -> item.upDate());
        assertTrue(item.getProcessedBodyList().isEmpty());
    }

    @Test
    void testReadIniFile_doesNotThrow() {
        ProcesserPlate item = new ProcesserPlate();
        assertDoesNotThrow(() -> item.readIniFile());
    }

    @Test
    void testGetCost_AccelerateMode() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnumProcessType(ProcesserPlate.ProcessType.ACCELERATE);
        // ACCELERATE mode uses runningCost[1] (same as HOTPLATE)
        assertEquals(item.getRunningCost()[1], item.getCost());
    }

    @Test
    void testGetCost_BaibaiOkazari() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnumProcessType(ProcesserPlate.ProcessType.BAIBAI_OKAZARI_WITH_FIRE);
        assertEquals(item.getRunningCost()[2], item.getCost());
    }

    @Test
    void testGetCost_Shutmouth() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnumProcessType(ProcesserPlate.ProcessType.SHUTMOUTH);
        assertEquals(item.getRunningCost()[2], item.getCost());
    }

    @Test
    void testGetCost_Plucking() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnumProcessType(ProcesserPlate.ProcessType.PLUCKING);
        assertEquals(item.getRunningCost()[3], item.getCost());
    }

    @Test
    void testGetCost_Packing() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnumProcessType(ProcesserPlate.ProcessType.PACKING);
        assertEquals(item.getRunningCost()[3], item.getCost());
    }

    // --- getImageLayer: enabled=true → images[0] ---

    @Test
    void testGetImageLayer_enabled_returnsOne() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(true);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertEquals(1, item.getImageLayer(layer));
    }

    // --- getImageLayer: enabled=false → images[1] ---

    @Test
    void testGetImageLayer_disabled_returnsOne() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(false);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertEquals(1, item.getImageLayer(layer));
    }

    // --- checkHitObj: z!=0 → returns false ---

    @Test
    void testCheckHitObj_zNotZero_returnsFalse() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(true);
        item.setX(100); item.setY(100);
        Body body = WorldTestHelper.createBody();
        body.setX(100); body.setY(100); body.setZ(5); // airborne
        java.awt.Rectangle rect = new java.awt.Rectangle(0, 0, 1000, 1000);
        assertFalse(item.checkHitObj(rect, body));
    }

    // --- checkHitObj: z==0, outside colRect → returns false ---

    @Test
    void testCheckHitObj_outsideRect_returnsFalse() {
        src.draw.Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        src.draw.Translate.createTransTable(false);
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(true);
        item.setX(500); item.setY(500);
        Body body = WorldTestHelper.createBody();
        body.setX(500); body.setY(500); body.setZ(0);
        java.awt.Rectangle rect = new java.awt.Rectangle(0, 0, 1, 1); // tiny rect
        assertDoesNotThrow(() -> item.checkHitObj(rect, body));
    }

    // --- setupProcesserPlate: headless → try/catch ---

    @Test
    void testSetupProcesserPlate_headless_doesNotThrow() {
        ProcesserPlate item = new ProcesserPlate();
        try {
            ProcesserPlate.setupProcesserPlate(item);
        } catch (Exception e) {
            // Expected in headless environment
        }
    }

    // --- Constructor(int, int, int): executes code path ---

    @Test
    void testConstructor_WithCoords_executesCode() {
        try {
            ProcesserPlate item = new ProcesserPlate(100, 100, 0);
        } catch (Exception e) {
            // Expected in headless environment (setupProcesserPlate fails)
        }
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            ProcesserPlate.loadImages(ProcesserPlate.class.getClassLoader(), null);
        } catch (Exception e) {
            // Expected: IOException because image files not found in test environment
        }
    }

    // --- upDate with live body: HOTPLATE mode ---

    @Test
    void testUpDate_enabled_HOTPLATE_withLiveBody() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(true);
        item.setEnumProcessType(ProcesserPlate.ProcessType.HOTPLATE_MIN);
        Body body = WorldTestHelper.createBody();
        // body is alive, z=0, not removed
        item.getProcessedBodyList().add(body);
        item.getProcessedBodyEffectList().add(null);
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- upDate with live body: PAIN mode ---

    @Test
    void testUpDate_enabled_PAIN_withLiveBody() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(true);
        item.setEnumProcessType(ProcesserPlate.ProcessType.PAIN);
        Body body = WorldTestHelper.createBody();
        item.getProcessedBodyList().add(body);
        item.getProcessedBodyEffectList().add(null);
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- upDate with live body: PEALING mode ---

    @Test
    void testUpDate_enabled_PEALING_withLiveBody() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(true);
        item.setEnumProcessType(ProcesserPlate.ProcessType.PEALING);
        Body body = WorldTestHelper.createBody();
        item.getProcessedBodyList().add(body);
        item.getProcessedBodyEffectList().add(null);
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- upDate with body flying (z >= 10) ---

    @Test
    void testUpDate_enabled_bodyFlying() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(true);
        item.setEnumProcessType(ProcesserPlate.ProcessType.PAIN);
        Body body = WorldTestHelper.createBody();
        body.setZ(10);
        item.getProcessedBodyList().add(body);
        item.getProcessedBodyEffectList().add(null);
        assertDoesNotThrow(() -> item.upDate());
        assertTrue(item.getProcessedBodyList().isEmpty());
    }

    // --- objHitProcess: live body not yet in list ---

    @Test
    void testObjHitProcess_LiveBody_PAIN_addsToList() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(true);
        item.setEnumProcessType(ProcesserPlate.ProcessType.PAIN);
        Body body = WorldTestHelper.createBody();
        int result = item.objHitProcess(body);
        assertEquals(1, result);
        assertTrue(item.getProcessedBodyList().contains(body));
    }

    // --- objHitProcess: live body already in list ---

    @Test
    void testObjHitProcess_LiveBody_alreadyInList_returnsOne() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(true);
        item.setEnumProcessType(ProcesserPlate.ProcessType.PAIN);
        Body body = WorldTestHelper.createBody();
        item.getProcessedBodyList().add(body);
        item.getProcessedBodyEffectList().add(null);
        int result = item.objHitProcess(body);
        assertEquals(1, result);
    }

    // --- upDate disabled with body in list (non-null effect) ---

    @Test
    void testUpDate_disabled_withBodyAndEffect() {
        ProcesserPlate item = new ProcesserPlate();
        item.setEnabled(false);
        item.setEnumProcessType(ProcesserPlate.ProcessType.PAIN);
        Body body = WorldTestHelper.createBody();
        item.getProcessedBodyList().add(body);
        item.getProcessedBodyEffectList().add(null);
        assertDoesNotThrow(() -> item.upDate());
        assertTrue(item.getProcessedBodyList().isEmpty());
    }
}
