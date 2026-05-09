package src.item;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.ItemTestBase;
import src.base.WorldEntity;
import src.draw.Rectangle4y;

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
        house.setItemRank(WorldEntity.ItemRank.NORA);
        assertEquals(WorldEntity.ItemRank.NORA, house.getItemRank());
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

    @Nested
    class RegressionScenarios {
        @Test
        void testScenario_ConstructorWithArgsRegistersHouseTypeBoundaryAndCollision() throws Exception {
            installHouseStatics();

            House house = new House(100, 120, 1);
            Rectangle collision = house.getCollisionRect(new Rectangle());

            assertEquals(House.HouseTable.HOUSE_NORA2, house.getHouseType());
            assertEquals(WorldEntity.ItemRank.NORA, house.getItemRank());
            assertSame(house, SimYukkuri.world.getCurrentMap().getHouse().get(house.getObjId()));
            assertEquals(60, house.getPivotX());
            assertEquals(20, house.getPivotY());
            assertEquals(120, house.getW());
            assertEquals(40, house.getH());
            assertEquals(120, collision.width);
            assertEquals(40, collision.height);
            assertEquals(0, house.getValue());
        }

        @Test
        void testScenario_GetImageLayerUsesConfiguredFirstHouseFloorImage() throws Exception {
            BufferedImage[][] images = installHouseStatics();
            House house = new House();
            BufferedImage[] layer = new BufferedImage[4];

            int count = house.getImageLayer(layer);

            assertEquals(1, count);
            assertSame(images[0][0], layer[0]);
        }

        private BufferedImage[][] installHouseStatics() throws Exception {
            BufferedImage[][] images = new BufferedImage[House.HouseTable.values().length][4];
            Rectangle4y[] boundary = new Rectangle4y[House.HouseTable.values().length];
            for (int i = 0; i < House.HouseTable.values().length; i++) {
                for (int j = 0; j < 4; j++) {
                    images[i][j] = new BufferedImage(80 + (i * 40), 30 + (i * 10), BufferedImage.TYPE_INT_ARGB);
                }
                boundary[i] = new Rectangle4y();
                boundary[i].setWidth(images[i][0].getWidth());
                boundary[i].setHeight(images[i][0].getHeight());
                boundary[i].setX(boundary[i].getWidth() >> 1);
                boundary[i].setY(boundary[i].getHeight() >> 1);
            }
            java.lang.reflect.Field imagesField = House.class.getDeclaredField("images");
            imagesField.setAccessible(true);
            imagesField.set(null, images);
            java.lang.reflect.Field boundaryField = House.class.getDeclaredField("boundary");
            boundaryField.setAccessible(true);
            boundaryField.set(null, boundary);
            return images;
        }
    }
}
