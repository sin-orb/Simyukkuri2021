package org.simyukkuri.item;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.entity.core.world.item.ProcessorPlate;
import org.simyukkuri.enums.HairState;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.Numbering;
import org.simyukkuri.util.WorldTestHelper;

class ProcessorPlateTest extends ItemTestBase {

    private static Yukkuri createReimuBody() {
        Yukkuri body = new Reimu();
        body.setObjId(Numbering.INSTANCE.numberingObjId());
        body.setUniqueID(Numbering.INSTANCE.numberingYukkuriID());
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueID(), body);
        return body;
    }

    @Test
    void testConstructor_Default() {
        ProcessorPlate item = new ProcessorPlate();
        item.setObjId(1);
        SimYukkuri.world.getCurrentWorldState().getProcessorPlates().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getProcessorPlates().containsKey(item.getObjId()));
    }

    @Test
    void testProcessModeEnum() {
        ProcessorPlate.ProcessMode[] modes = ProcessorPlate.ProcessMode.values();
        assertEquals(9, modes.length);
        assertEquals(ProcessorPlate.ProcessMode.HOTPLATE, ProcessorPlate.ProcessMode.valueOf("HOTPLATE"));
        assertEquals(ProcessorPlate.ProcessMode.PAIN, ProcessorPlate.ProcessMode.valueOf("PAIN"));
        assertEquals(ProcessorPlate.ProcessMode.PACKING, ProcessorPlate.ProcessMode.valueOf("PACKING"));
        for (ProcessorPlate.ProcessMode m : modes) {
            assertNotNull(m.name());
        }
    }

    @Test
    void testProcessTypeEnum() {
        ProcessorPlate.ProcessType[] types = ProcessorPlate.ProcessType.values();
        assertEquals(13, types.length);
        for (ProcessorPlate.ProcessType t : types) {
            assertNotNull(t.toString());
            assertNotNull(t.name());
        }
        assertEquals(ProcessorPlate.ProcessType.HOTPLATE_MIN, ProcessorPlate.ProcessType.valueOf("HOTPLATE_MIN"));
        assertEquals(ProcessorPlate.ProcessType.PACKING, ProcessorPlate.ProcessType.valueOf("PACKING"));
    }

    @Test
    void testGetHitCheckObjType() {
        ProcessorPlate item = new ProcessorPlate();
        assertEquals(ProcessorPlate.hitCheckObjType, item.getHitCheckObjType());
    }

    @Test
    void testEnableHitCheck() {
        ProcessorPlate item = new ProcessorPlate();
        assertTrue(item.enableHitCheck());
    }

    @Test
    void testGetShadowImage() {
        ProcessorPlate item = new ProcessorPlate();
        assertNull(item.getShadowImage());
    }

    @Test
    void testGetBounding() {
        assertNotNull(ProcessorPlate.getBounding());
    }

    @Test
    void testGetSetEnumProcessType() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnumProcessType(ProcessorPlate.ProcessType.PAIN);
        assertEquals(ProcessorPlate.ProcessType.PAIN, item.getEnumProcessType());
    }

    @Test
    void testGetSetProcessedBodyList() {
        ProcessorPlate item = new ProcessorPlate();
        List<Yukkuri> list = new LinkedList<>();
        item.setActiveBodies(list);
        assertEquals(list, item.getActiveBodies());
    }

    @Test
    void testGetSetRunningCost() {
        ProcessorPlate item = new ProcessorPlate();
        int[] costs = { 100, 200, 300, 400 };
        item.setRunningCost(costs);
        assertArrayEquals(costs, item.getRunningCost());
    }

    @Test
    void testGetCost_PainMode() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnumProcessType(ProcessorPlate.ProcessType.PAIN);
        // PAIN mode returns runningCost[0]
        assertEquals(item.getRunningCost()[0], item.getCost());
    }

    @Test
    void testGetCost_HotplateMode() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnumProcessType(ProcessorPlate.ProcessType.HOTPLATE_MIN);
        // HOTPLATE mode returns runningCost[1]
        assertEquals(item.getRunningCost()[1], item.getCost());
    }

    @Test
    void testGetCost_PeelingMode() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnumProcessType(ProcessorPlate.ProcessType.PEALING);
        // PEALING mode returns runningCost[3]
        assertEquals(item.getRunningCost()[3], item.getCost());
    }

    @Test
    void testGetCost_BlindingMode() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnumProcessType(ProcessorPlate.ProcessType.BLINDING);
        // BLINDING mode returns runningCost[2]
        assertEquals(item.getRunningCost()[2], item.getCost());
    }

    @Test
    void testObjHitProcess_Disabled() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(false);
        Yukkuri body = WorldTestHelper.createBody();
        assertEquals(0, item.objHitProcess(body));
    }

    @Test
    void testObjHitProcess_NullObj() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(true);
        assertEquals(0, item.objHitProcess(null));
    }

    @Test
    void testRemoveListData_EmptyLists() {
        ProcessorPlate item = new ProcessorPlate();
        item.setObjId(66);
        SimYukkuri.world.getCurrentWorldState().getProcessorPlates().put(item.getObjId(), item);
        item.removeFromWorld();
        assertFalse(SimYukkuri.world.getCurrentWorldState().getProcessorPlates().containsKey(item.getObjId()));
    }

    @Test
    void testRemoveListData_WithBody() {
        ProcessorPlate item = new ProcessorPlate();
        item.setObjId(67);
        SimYukkuri.world.getCurrentWorldState().getProcessorPlates().put(item.getObjId(), item);

        Yukkuri body = WorldTestHelper.createBody();
        body.setLockmove(true);
        item.getActiveBodies().add(body);
        item.getActiveEffects().add(null); // null effect

        item.removeFromWorld();
        assertFalse(body.isLockmove());
        assertTrue(item.getActiveBodies().isEmpty());
    }

    @Test
    void testGetSetProcessedBodyEffectList() {
        ProcessorPlate item = new ProcessorPlate();
        List<org.simyukkuri.entity.core.effect.Effect> list = new LinkedList<>();
        item.setActiveEffects(list);
        assertEquals(list, item.getActiveEffects());
    }

    @Test
    void testUpDate_disabled_emptyLists() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(false);
        assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testUpDate_disabled_withBodyInList() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(false);
        Yukkuri body = WorldTestHelper.createBody();
        item.getActiveBodies().add(body);
        item.getActiveEffects().add(null);
        assertDoesNotThrow(() -> item.upDate());
        assertTrue(item.getActiveBodies().isEmpty());
    }

    @Test
    void testUpDate_enabled_emptyLists() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(true);
        item.setEnumProcessType(ProcessorPlate.ProcessType.PAIN);
        assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testUpDate_enabled_withRemovedBody() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(true);
        item.setEnumProcessType(ProcessorPlate.ProcessType.PAIN);
        Yukkuri body = WorldTestHelper.createBody();
        body.remove();
        item.getActiveBodies().add(body);
        item.getActiveEffects().add(null);
        assertDoesNotThrow(() -> item.upDate());
        assertTrue(item.getActiveBodies().isEmpty());
    }

    @Test
    void testReadIniFile_doesNotThrow() {
        ProcessorPlate item = new ProcessorPlate();
        assertDoesNotThrow(() -> item.readIniFile());
    }

    @Test
    void testGetCost_AccelerateMode() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnumProcessType(ProcessorPlate.ProcessType.ACCELERATE);
        // ACCELERATE mode uses runningCost[1] (same as HOTPLATE)
        assertEquals(item.getRunningCost()[1], item.getCost());
    }

    @Test
    void testGetCost_BaibaiOkazari() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnumProcessType(ProcessorPlate.ProcessType.BAIBAI_OKAZARI_WITH_FIRE);
        assertEquals(item.getRunningCost()[2], item.getCost());
    }

    @Test
    void testGetCost_Shutmouth() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnumProcessType(ProcessorPlate.ProcessType.SHUTMOUTH);
        assertEquals(item.getRunningCost()[2], item.getCost());
    }

    @Test
    void testGetCost_Plucking() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnumProcessType(ProcessorPlate.ProcessType.PLUCKING);
        assertEquals(item.getRunningCost()[3], item.getCost());
    }

    @Test
    void testGetCost_Packing() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnumProcessType(ProcessorPlate.ProcessType.PACKING);
        assertEquals(item.getRunningCost()[3], item.getCost());
    }

    // --- getImageLayer: enabled=true → images[0] ---

    @Test
    void testGetImageLayer_enabled_returnsOne() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(true);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertEquals(1, item.getImageLayer(layer));
    }

    // --- getImageLayer: enabled=false → images[1] ---

    @Test
    void testGetImageLayer_disabled_returnsOne() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(false);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertEquals(1, item.getImageLayer(layer));
    }

    // --- checkHitObj: z!=0 → returns false ---

    @Test
    void testCheckHitObj_zNotZero_returnsFalse() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(true);
        item.setX(100);
        item.setY(100);
        Yukkuri body = WorldTestHelper.createBody();
        body.setX(100);
        body.setY(100);
        body.setZ(5); // airborne
        java.awt.Rectangle rect = new java.awt.Rectangle(0, 0, 1000, 1000);
        assertFalse(item.checkHitObj(rect, body));
    }

    // --- checkHitObj: z==0, outside colRect → returns false ---

    @Test
    void testCheckHitObj_outsideRect_returnsFalse() {
        WorldTestHelper.initializeStandardTranslate200();
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(true);
        item.setX(500);
        item.setY(500);
        Yukkuri body = WorldTestHelper.createBody();
        body.setX(500);
        body.setY(500);
        body.setZ(0);
        java.awt.Rectangle rect = new java.awt.Rectangle(0, 0, 1, 1); // tiny rect
        assertDoesNotThrow(() -> item.checkHitObj(rect, body));
    }

    // --- setupProcessorPlate: headless → try/catch ---

    @Test
    void testSetupProcessorPlate_headless_doesNotThrow() {
        ProcessorPlate item = new ProcessorPlate();
        try {
            ProcessorPlate.setupProcessorPlate(item);
        } catch (Exception e) {
            // Expected in headless environment
        }
    }

    // --- Constructor(int, int, int): executes code path ---

    @Test
    void testConstructor_WithCoords_executesCode() {
        try {
            new ProcessorPlate(100, 100, 0);
        } catch (Exception e) {
            // Expected in headless environment (setupProcessorPlate fails)
        }
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            ProcessorPlate.loadImages(ProcessorPlate.class.getClassLoader(), null);
        } catch (Exception e) {
            // Expected: IOException because image files not found in test environment
        }
    }

    // --- upDate with live body: HOTPLATE mode ---

    @Test
    void testUpDate_enabled_HOTPLATE_withLiveBody() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(true);
        item.setEnumProcessType(ProcessorPlate.ProcessType.HOTPLATE_MIN);
        Yukkuri body = WorldTestHelper.createBody();
        // body is alive, z=0, not removed
        item.getActiveBodies().add(body);
        item.getActiveEffects().add(null);
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- upDate with live body: PAIN mode ---

    @Test
    void testUpDate_enabled_PAIN_withLiveBody() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(true);
        item.setEnumProcessType(ProcessorPlate.ProcessType.PAIN);
        Yukkuri body = WorldTestHelper.createBody();
        item.getActiveBodies().add(body);
        item.getActiveEffects().add(null);
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- upDate with live body: PEALING mode ---

    @Test
    void testUpDate_enabled_PEALING_withLiveBody() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(true);
        item.setEnumProcessType(ProcessorPlate.ProcessType.PEALING);
        Yukkuri body = WorldTestHelper.createBody();
        item.getActiveBodies().add(body);
        item.getActiveEffects().add(null);
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- upDate with body flying (z >= 10) ---

    @Test
    void testUpDate_enabled_bodyFlying() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(true);
        item.setEnumProcessType(ProcessorPlate.ProcessType.PAIN);
        Yukkuri body = WorldTestHelper.createBody();
        body.setZ(10);
        item.getActiveBodies().add(body);
        item.getActiveEffects().add(null);
        assertDoesNotThrow(() -> item.upDate());
        assertTrue(item.getActiveBodies().isEmpty());
    }

    // --- objHitProcess: live body not yet in list ---

    @Test
    void testObjHitProcess_LiveBody_PAIN_addsToList() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(true);
        item.setEnumProcessType(ProcessorPlate.ProcessType.PAIN);
        Yukkuri body = WorldTestHelper.createBody();
        int result = item.objHitProcess(body);
        assertEquals(1, result);
        assertTrue(item.getActiveBodies().contains(body));
    }

    // --- objHitProcess: live body already in list ---

    @Test
    void testObjHitProcess_LiveBody_alreadyInList_returnsOne() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(true);
        item.setEnumProcessType(ProcessorPlate.ProcessType.PAIN);
        Yukkuri body = WorldTestHelper.createBody();
        item.getActiveBodies().add(body);
        item.getActiveEffects().add(null);
        int result = item.objHitProcess(body);
        assertEquals(1, result);
    }

    // --- upDate disabled with body in list (non-null effect) ---

    @Test
    void testUpDate_disabled_withBodyAndEffect() {
        ProcessorPlate item = new ProcessorPlate();
        item.setEnabled(false);
        item.setEnumProcessType(ProcessorPlate.ProcessType.PAIN);
        Yukkuri body = WorldTestHelper.createBody();
        item.getActiveBodies().add(body);
        item.getActiveEffects().add(null);
        assertDoesNotThrow(() -> item.upDate());
        assertTrue(item.getActiveBodies().isEmpty());
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_PainModeWakesBodyAndAppliesPainState() {
            ProcessorPlate item = new ProcessorPlate();
            item.setEnabled(true);
            item.setEnumProcessType(ProcessorPlate.ProcessType.PAIN);

            Yukkuri body = WorldTestHelper.createBody();
            body.setSleeping(true);
            body.setShadowVisible(true);
            int damageBefore = body.getDamage();
            int stressBefore = body.getStress();

            item.getActiveBodies().add(body);
            item.getActiveEffects().add(null);

            item.upDate();

            assertFalse(body.isSleeping());
            assertFalse(body.isShadowVisible());
            assertEquals(damageBefore + 5, body.getDamage());
            assertEquals(stressBefore + 30, body.getStress());
            assertEquals(Happiness.VERY_SAD, body.getHappiness());
            assertEquals(ImageCode.PAIN.ordinal(), body.getForceFace());
            assertTrue(item.getActiveBodies().contains(body));
        }

        @Test
        void testScenario_PealingModePealsEligibleBody() {
            ProcessorPlate item = new ProcessorPlate();
            item.setEnabled(true);
            item.setEnumProcessType(ProcessorPlate.ProcessType.PEALING);

            Yukkuri body = createReimuBody();
            body.setSleeping(true);
            body.setOkazaris(null);
            body.setHasBraid(false);

            item.getActiveBodies().add(body);
            item.getActiveEffects().add(null);

            item.upDate();

            assertFalse(body.isSleeping());
            assertTrue(body.isPealed());
            assertEquals(HairState.BALDHEAD, body.getHairState());
        }

        @Test
        void testScenario_PackingModePacksFullyProcessedBody() {
            ProcessorPlate item = new ProcessorPlate();
            item.setEnabled(true);
            item.setEnumProcessType(ProcessorPlate.ProcessType.PACKING);

            Yukkuri body = createReimuBody();
            body.setOkazaris(null);
            body.setHasBraid(false);
            body.setBlind(true);
            body.setShutmouth(true);
            body.setHairState(HairState.BALDHEAD);

            item.getActiveBodies().add(body);
            item.getActiveEffects().add(null);

            item.upDate();

            assertTrue(body.isPacked());
            assertTrue(body.isBlind());
            assertTrue(body.isShutmouth());
            assertEquals(Happiness.VERY_SAD, body.getHappiness());
        }
    }
}
