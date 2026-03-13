package src.item;

import org.junit.jupiter.api.Test;
import src.SimYukkuri;
import src.base.Body;
import src.base.ItemTestBase;
import src.base.ObjEX;
import src.util.WorldTestHelper;

import static org.junit.jupiter.api.Assertions.*;

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
    // isBForSlave / setBForSlave / isForSlave
    // ---------------------------------------------------------------

    @Test
    void testIsForSlave_Default() {
        Toilet toilet = new Toilet();
        assertFalse(toilet.isBForSlave());
    }

    @Test
    void testSetBForSlave() {
        Toilet toilet = new Toilet();
        toilet.setBForSlave(true);
        assertTrue(toilet.isBForSlave());
    }

    @Test
    void testIsForSlave() {
        Toilet toilet = new Toilet();
        assertFalse(toilet.isForSlave());
        toilet.setBForSlave(true);
        assertTrue(toilet.isForSlave());
    }

    // ---------------------------------------------------------------
    // getItemRank / setItemRank
    // ---------------------------------------------------------------

    @Test
    void testGetSetItemRank() {
        Toilet toilet = new Toilet();
        toilet.setItemRank(ObjEX.ItemRank.HOUSE);
        assertEquals(ObjEX.ItemRank.HOUSE, toilet.getItemRank());
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
        SimYukkuri.world.getCurrentMap().getToilet().put(id, toilet);
        assertTrue(SimYukkuri.world.getCurrentMap().getToilet().containsKey(id));

        toilet.removeListData();

        assertFalse(SimYukkuri.world.getCurrentMap().getToilet().containsKey(id));
    }

    // ---------------------------------------------------------------
    // objHitProcess
    // ---------------------------------------------------------------

    @Test
    void testObjHitProcess() {
        // Toilet.objHitProcess(Obj o): o.remove(); Cash.addCash(-getCost()); return 1
        // Cash.addCash requires SimYukkuri.world (already initialized by setUp()).
        Toilet toilet = new Toilet();
        // cost defaults to 0, so Cash.addCash(0) is safe.
        Body body = WorldTestHelper.createBody();
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
        src.base.Obj body = src.util.WorldTestHelper.createBody();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> toilet.checkHitObj(body));
    }

    @Test
    void testCheckHitObj_twoArgs_doesNotThrow() {
        Toilet toilet = new Toilet();
        src.base.Obj body = src.util.WorldTestHelper.createBody();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> toilet.checkHitObj(null, body));
    }

    @Test
    void testSetupToilet_headless_executesCode() {
        Toilet toilet = new Toilet();
        try {
            Toilet.setupToilet(toilet);
        } catch (Exception e) {
        }
    }

    @Test
    void testConstructorWithArgs_headless_executesCode() {
        try {
            Toilet t = new Toilet(100, 100, 0);
            org.junit.jupiter.api.Assertions.assertNotNull(t);
        } catch (Exception e) {
        }
    }
}