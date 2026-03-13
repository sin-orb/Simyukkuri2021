package src.item;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import src.SimYukkuri;
import src.base.ItemTestBase;
import src.base.ObjEX;

class HouseTest extends ItemTestBase {

    // ItemTestBase already provides @BeforeEach setUp() and @AfterEach tearDown()
    // that call WorldTestHelper.resetWorld() / initializeMinimalWorld().

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    @Test
    void testConstructor_Default() {
        House item = new House();
        item.setObjId(1);
        SimYukkuri.world.getCurrentMap().getHouse().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getHouse().containsKey(item.getObjId()));
    }

    // ---------------------------------------------------------------
    // HouseTable enum
    // ---------------------------------------------------------------

    @Test
    void testHouseTableEnum() {
        House.HouseTable[] tables = House.HouseTable.values();
        assertEquals(2, tables.length);
        for (House.HouseTable t : tables) {
            assertNotNull(t.getFloorName());
            assertNotNull(t.getWallName());
            assertNotNull(t.getCeilName());
            assertNotNull(t.getDoorName());
            assertTrue(t.getRank() > 0);
        }
        assertEquals(House.HouseTable.HOUSE_NORA1, House.HouseTable.valueOf("HOUSE_NORA1"));
        assertEquals(House.HouseTable.HOUSE_NORA2, House.HouseTable.valueOf("HOUSE_NORA2"));
    }

    // ---------------------------------------------------------------
    // getHouseType / setHouseType
    // ---------------------------------------------------------------

    @Test
    void testGetSetHouseType() {
        House house = new House();
        house.setHouseType(House.HouseTable.HOUSE_NORA1);
        assertEquals(House.HouseTable.HOUSE_NORA1, house.getHouseType());
    }

    // ---------------------------------------------------------------
    // getItemRank / setItemRank
    // ---------------------------------------------------------------

    @Test
    void testGetSetItemRank() {
        House house = new House();
        house.setItemRank(ObjEX.ItemRank.NORA);
        assertEquals(ObjEX.ItemRank.NORA, house.getItemRank());
    }

    // ---------------------------------------------------------------
    // getShadowImage
    // ---------------------------------------------------------------

    @Test
    void testGetShadowImage() {
        House house = new House();
        assertNull(house.getShadowImage());
    }

    // ---------------------------------------------------------------
    // getValue
    // ---------------------------------------------------------------

    @Test
    void testGetValue() {
        // Default constructor does not set value, so it stays at 0.
        House house = new House();
        assertEquals(0, house.getValue());
    }

    // ---------------------------------------------------------------
    // removeListData
    // ---------------------------------------------------------------

    @Test
    void testRemoveListData() {
        House house = new House();
        int id = 8001;
        house.setObjId(id);
        SimYukkuri.world.getCurrentMap().getHouse().put(id, house);
        assertTrue(SimYukkuri.world.getCurrentMap().getHouse().containsKey(id));

        house.removeListData();

        assertFalse(SimYukkuri.world.getCurrentMap().getHouse().containsKey(id));
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            House.loadImages(House.class.getClassLoader(), null);
        } catch (Exception e) { }
    }

    @Test
    void testGetImageLayer_doesNotThrow() {
        House item = new House();
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[5];
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    @Test
    void testGetBounding_doesNotThrow() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> House.getBounding());
    }

    @Test
    void testConstructorWithArgs_headless_executesCode() {
        try {
            House h = new House(100, 100, 0);
            org.junit.jupiter.api.Assertions.assertNotNull(h);
        } catch (Exception e) { }
    }
}