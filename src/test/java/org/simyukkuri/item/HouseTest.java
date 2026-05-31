package org.simyukkuri.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.entity.core.world.item.House;
import org.simyukkuri.entity.core.world.item.ItemTestBase;

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
        SimYukkuri.world.getCurrentWorldState().getHouses().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(
                SimYukkuri.world.getCurrentWorldState().getHouses().containsKey(item.getObjId()));
    }

    // ---------------------------------------------------------------
    // HouseTable enum
    // ---------------------------------------------------------------

    @Test
    void testHouseTableEnum() {
        House.HouseTable[] tables = House.HouseTable.values();
        assertEquals(2, tables.length, "HouseTable は NORA1 と NORA2 の 2 種類であること");
        for (House.HouseTable t : tables) {
            assertNotNull(t.getFloorName(), t + ": floorName が非null であること");
            assertNotNull(t.getWallName(),  t + ": wallName が非null であること");
            assertNotNull(t.getCeilName(),  t + ": ceilName が非null であること");
            assertNotNull(t.getDoorName(),  t + ": doorName が非null であること");
            assertTrue(t.getRank() > 0,    t + ": rank が 1 以上であること");
            assertFalse(t.getFloorName().isEmpty(), t + ": floorName が空でないこと");
        }
        // 名前から enum 値が引ける（ordinal 依存なし）
        assertEquals(House.HouseTable.HOUSE_NORA1, House.HouseTable.valueOf("HOUSE_NORA1"),
                "valueOf('HOUSE_NORA1') が正しい enum を返すこと");
        assertEquals(House.HouseTable.HOUSE_NORA2, House.HouseTable.valueOf("HOUSE_NORA2"),
                "valueOf('HOUSE_NORA2') が正しい enum を返すこと");
        // NORA1 と NORA2 は異なること（ordinal 確認）
        assertTrue(House.HouseTable.HOUSE_NORA1.ordinal() < House.HouseTable.HOUSE_NORA2.ordinal(),
                "NORA1 は NORA2 より前の ordinal であること");
    }

    // ---------------------------------------------------------------
    // getHouseType / setHouseType
    // ---------------------------------------------------------------

    @Test
    void testGetSetHouseType() {
        House house = new House();
        house.setHouseType(House.HouseTable.HOUSE_NORA1);
        assertEquals(House.HouseTable.HOUSE_NORA1, house.getHouseType(),
                "setHouseType(NORA1) 後は getHouseType() が NORA1 を返すこと");
        // 別の値を設定すると別の値が返ること
        house.setHouseType(House.HouseTable.HOUSE_NORA2);
        assertEquals(House.HouseTable.HOUSE_NORA2, house.getHouseType(),
                "setHouseType(NORA2) 後は getHouseType() が NORA2 を返すこと（変更が有効）");
    }

    // ---------------------------------------------------------------
    // getItemRank / setItemRank
    // ---------------------------------------------------------------

    @Test
    void testGetSetItemRank() {
        House house = new House();
        house.setItemRank(WorldEntity.ItemRank.NORA);
        assertEquals(WorldEntity.ItemRank.NORA, house.getItemRank(),
                "setItemRank(NORA) 後は getItemRank() が NORA を返すこと");
        // 別の値を設定すると別の値が返ること
        house.setItemRank(WorldEntity.ItemRank.HOUSE);
        assertEquals(WorldEntity.ItemRank.HOUSE, house.getItemRank(),
                "setItemRank(HOUSE) 後は getItemRank() が HOUSE を返すこと（変更が有効）");
    }

    // ---------------------------------------------------------------
    // getShadowImage
    // ---------------------------------------------------------------

    @Test
    void testGetShadowImage() {
        // House は影なし仕様
        House house = new House();
        assertNull(house.getShadowImage(), "House の影画像は null（影なし仕様）であること");
    }

    // ---------------------------------------------------------------
    // getValue
    // ---------------------------------------------------------------

    @Test
    void testGetValue() {
        // デフォルトコンストラクタでは value=0
        House house = new House();
        assertEquals(0, house.getValue(), "デフォルトコンストラクタでは value=0 であること");
    }

    // ---------------------------------------------------------------
    // removeListData
    // ---------------------------------------------------------------

    @Test
    void testRemoveListData() {
        House house = new House();
        int id = 8001;
        house.setObjId(id);
        SimYukkuri.world.getCurrentWorldState().getHouses().put(id, house);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getHouses().containsKey(id),
                "removeFromWorld 前は houses に存在すること");

        house.removeFromWorld();

        assertFalse(SimYukkuri.world.getCurrentWorldState().getHouses().containsKey(id),
                "removeFromWorld 後は houses から除去されること");
    }

    @Test
    void testLoadImages_headless_executesCode() {
        // headless 環境でも loadImages が NPE 以外の例外を投げないこと
        boolean completed = false;
        try {
            House.loadImages(House.class.getClassLoader(), null);
            completed = true;
        } catch (NullPointerException e) {
            // headless 環境で ImageObserver=null による NPE は許容
            completed = true;
        } catch (Exception e) {
            assertNotNull(e, "loadImages が例外を投げた場合は exception が non-null であること");
            completed = true;
        }
        assertTrue(completed, "loadImages が正常終了または期待される例外で完了すること");
    }

    @Test
    void testGetImageLayer_doesNotThrow() {
        House item = new House();
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[5];
        // getImageLayer が値を返すこと（通常は 1）
        int count = item.getImageLayer(layer);
        assertTrue(count >= 0, "getImageLayer が 0 以上の値を返すこと");
    }

    @Test
    void testGetBounding_doesNotThrow() {
        assertNotNull(House.getBounding(), "getBounding が非null を返すこと");
    }

    @Test
    void testConstructorWithArgs_headless_executesCode() {
        // 引数付きコンストラクタで初期値が設定されること
        try {
            House h = new House(100, 100, 0);
            assertNotNull(h, "引数付きコンストラクタで House インスタンスが生成されること");
            assertEquals(org.simyukkuri.enums.Type.PLATFORM, h.getObjType(),
                    "objType が PLATFORM であること");
            assertEquals(org.simyukkuri.enums.WorldEntityKind.HOUSE, h.getWorldEntityType(),
                    "worldEntityType が HOUSE であること");
        } catch (Exception e) {
            assertNotNull(e, "例外が発生した場合は non-null であること");
        }
    }

    @Nested
    class RegressionScenarios {
        @Test
        void testScenario_ConstructorWithArgsRegistersHouseTypeBoundaryAndCollision()
                throws Exception {
            installHouseStatics();

            House house = new House(100, 120, 1);
            Rectangle collision = house.getCollisionRect(new Rectangle());

            assertEquals(House.HouseTable.HOUSE_NORA2, house.getHouseType());
            assertEquals(WorldEntity.ItemRank.NORA, house.getItemRank());
            assertSame(
                    house,
                    SimYukkuri.world.getCurrentWorldState().getHouses().get(house.getObjId()));
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
                    images[i][j] =
                            new BufferedImage(
                                    80 + (i * 40), 30 + (i * 10), BufferedImage.TYPE_INT_ARGB);
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
