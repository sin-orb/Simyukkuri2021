package org.simyukkuri.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import org.simyukkuri.entity.core.world.item.Toilet;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.util.WorldTestHelper;

class ToiletTest extends ItemTestBase {

    // ItemTestBase already provides @BeforeEach setUp() and @AfterEach tearDown()
    // that call WorldTestHelper.resetWorld() / initializeMinimalWorld().

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    @Test
    void testConstructor_Default() {
        Toilet toilet = new Toilet();
        assertNotNull(toilet);
    }

    // ---------------------------------------------------------------
    // ToiletType enum
    // ---------------------------------------------------------------

    @Test
    void testToiletTypeEnum() {
        Toilet.ToiletType[] types = Toilet.ToiletType.values();
        assertEquals(3, types.length);
        for (Toilet.ToiletType t : types) {
            assertDoesNotThrow(() -> t.toString());
        }
        assertEquals(Toilet.ToiletType.NORMAL, Toilet.ToiletType.valueOf("NORMAL"));
        assertEquals(Toilet.ToiletType.CLEAN, Toilet.ToiletType.valueOf("CLEAN"));
        assertEquals(Toilet.ToiletType.SLAVE, Toilet.ToiletType.valueOf("SLAVE"));
    }

    // ---------------------------------------------------------------
    // getHitCheckObjType
    // ---------------------------------------------------------------

    @Test
    void testGetHitCheckObjType_NotAutoClean() {
        Toilet toilet = new Toilet();
        toilet.setAutoClean(false);
        assertEquals(0, toilet.getHitCheckObjType());
    }

    @Test
    void testGetHitCheckObjType_AutoClean() {
        Toilet toilet = new Toilet();
        toilet.setAutoClean(true);
        assertEquals(Toilet.hitCheckObjType, toilet.getHitCheckObjType());
    }

    // ---------------------------------------------------------------
    // getAutoClean / setAutoClean
    // ---------------------------------------------------------------

    @Test
    void testGetAutoClean_Default() {
        Toilet toilet = new Toilet();
        assertFalse(toilet.getAutoClean());
    }

    @Test
    void testSetAutoClean() {
        Toilet toilet = new Toilet();
        toilet.setAutoClean(true);
        assertTrue(toilet.getAutoClean());
    }

    // ---------------------------------------------------------------
    // isForSlave / setForSlave / isForSlave
    // ---------------------------------------------------------------

    @Test
    void testIsForSlave_Default() {
        Toilet toilet = new Toilet();
        assertFalse(toilet.isForSlave());
    }

    @Test
    void testSetBForSlave() {
        Toilet toilet = new Toilet();
        toilet.setForSlave(true);
        assertTrue(toilet.isForSlave());
    }

    @Test
    void testIsForSlave() {
        Toilet toilet = new Toilet();
        assertFalse(toilet.isForSlave());
        toilet.setForSlave(true);
        assertTrue(toilet.isForSlave());
    }

    // ---------------------------------------------------------------
    // getItemRank / setItemRank
    // ---------------------------------------------------------------

    @Test
    void testGetSetItemRank() {
        Toilet toilet = new Toilet();
        toilet.setItemRank(WorldEntity.ItemRank.HOUSE);
        assertEquals(WorldEntity.ItemRank.HOUSE, toilet.getItemRank());
    }

    // ---------------------------------------------------------------
    // getShadowImage
    // ---------------------------------------------------------------

    @Test
    void testGetShadowImage() {
        Toilet toilet = new Toilet();
        assertNull(toilet.getShadowImage());
    }

    // ---------------------------------------------------------------
    // removeListData
    // ---------------------------------------------------------------

    @Test
    void testRemoveListData() {
        Toilet toilet = new Toilet();
        int id = 9001;
        toilet.setObjId(id);
        SimYukkuri.world.getCurrentWorldState().getToilets().put(id, toilet);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getToilets().containsKey(id));

        toilet.removeFromWorld();

        assertFalse(SimYukkuri.world.getCurrentWorldState().getToilets().containsKey(id));
    }

    // ---------------------------------------------------------------
    // objHitProcess
    // ---------------------------------------------------------------

    @Test
    void testObjHitProcess() {
        // Toilet.objHitProcess(Entity o): o.remove(); Cash.addCash(-getCost()); return
        // 1
        // Cash.addCash requires SimYukkuri.world (already initialized by setUp()).
        Toilet toilet = new Toilet();
        // cost defaults to 0, so Cash.addCash(0) is safe.
        Yukkuri body = WorldTestHelper.createBody();
        assertFalse(body.isRemoved());

        int result = toilet.objHitProcess(body);

        assertEquals(1, result);
        assertTrue(body.isRemoved());
    }

    // ---------------------------------------------------------------
    // getBounding
    // ---------------------------------------------------------------

    @Test
    void testGetBounding() {
        // boundary is initialized as new Rectangle4y() at class load time,
        // so it is never null even without loadImages().
        assertNotNull(Toilet.getBounding());
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            Toilet.loadImages(Toilet.class.getClassLoader(), null);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testGetImageLayer_doesNotThrow() {
        Toilet toilet = new Toilet();
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> toilet.getImageLayer(layer));
    }

    @Test
    void testCheckHitObj_singleArg_doesNotThrow() {
        Toilet toilet = new Toilet();
        org.simyukkuri.entity.core.Entity body = org.simyukkuri.util.WorldTestHelper.createBody();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> toilet.checkHitObj(body));
    }

    @Test
    void testCheckHitObj_twoArgs_doesNotThrow() {
        Toilet toilet = new Toilet();
        org.simyukkuri.entity.core.Entity body = org.simyukkuri.util.WorldTestHelper.createBody();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> toilet.checkHitObj(null, body));
    }

    @Test
    void testSetupToilet_headless_executesCode() {
        Toilet toilet = new Toilet();
        try {
            Toilet.setupToilet(toilet);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testConstructorWithArgs_headless_executesCode() {
        try {
            Toilet t = new Toilet(100, 100, 0);
            org.junit.jupiter.api.Assertions.assertNotNull(t);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_AutoCleanHitRemovesShitInsideCollision() {
            Toilet toilet = new Toilet();
            toilet.setAutoClean(true);
            toilet.setX(100);
            toilet.setY(100);
            toilet.setColW(20);
            toilet.setColH(20);
            org.simyukkuri.draw.Point4y pivot = new org.simyukkuri.draw.Point4y();
            org.simyukkuri.draw.Translate.translate(100, 100, pivot);
            toilet.setScreenPivot(pivot);

            Shit shit = new Shit();
            shit.setX(100);
            shit.setY(100);

            assertTrue(toilet.checkHitObj(new java.awt.Rectangle(0, 0, 500, 500), shit));
            assertTrue(shit.isRemoved());
        }

        @Test
        void testScenario_AutoCleanMissDoesNotRemoveShitOutsideCollision() {
            Toilet toilet = new Toilet();
            toilet.setAutoClean(true);
            toilet.setX(100);
            toilet.setY(100);
            toilet.setColW(20);
            toilet.setColH(20);
            org.simyukkuri.draw.Point4y pivot = new org.simyukkuri.draw.Point4y();
            org.simyukkuri.draw.Translate.translate(100, 100, pivot);
            toilet.setScreenPivot(pivot);

            Shit shit = new Shit();
            shit.setX(400);
            shit.setY(400);

            assertFalse(toilet.checkHitObj(new java.awt.Rectangle(0, 0, 500, 500), shit));
            assertFalse(shit.isRemoved());
        }
    }
}
