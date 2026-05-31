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
import org.simyukkuri.entity.core.world.item.Diffuser;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.entity.core.world.item.ProductChute;
import org.simyukkuri.entity.core.world.item.Stone;
import org.simyukkuri.entity.core.world.item.Yunba;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.util.WorldTestHelper;

class ProductChuteTest extends ItemTestBase {
    @Test
    void testConstructor_Default() {
        ProductChute item = new ProductChute();
        item.setObjId(1);
        SimYukkuri.world.getCurrentWorldState().getProductChutes().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getProductChutes().containsKey(item.getObjId()),
                "デフォルトコンストラクタ後に productChutes に登録されること");
    }

    @Test
    void testConstructorWithCoords() {
        ProductChute item = new ProductChute(100, 200, 0);
        assertEquals(100, item.getX());
        assertEquals(200, item.getY());
        assertEquals(Type.PLATFORM, item.getObjType());
        assertEquals(WorldEntityKind.PRODUCTCHUTE, item.getWorldEntityType());
        assertEquals(5000, item.getValue());
        assertEquals(50, item.getCost());
        assertEquals(10, item.getInterval());
        assertTrue(SimYukkuri.world.getCurrentWorldState().getProductChutes().containsKey(item.getObjId()));
    }

    @Test
    void testGetShadowImageIsNull() {
        // ProductChute には影なし（仕様）
        ProductChute item = new ProductChute();
        assertNull(item.getShadowImage(), "ProductChute は影なし仕様で null を返すこと");
    }

    @Test
    void testGetHitCheckObjType() {
        ProductChute item = new ProductChute();
        int expected = org.simyukkuri.entity.core.world.WorldEntity.YUKKURI
                + org.simyukkuri.entity.core.world.WorldEntity.SHIT
                + org.simyukkuri.entity.core.world.WorldEntity.FOOD
                + org.simyukkuri.entity.core.world.WorldEntity.TOY
                + org.simyukkuri.entity.core.world.WorldEntity.OBJECT
                + org.simyukkuri.entity.core.world.WorldEntity.VOMIT
                + org.simyukkuri.entity.core.world.WorldEntity.STALK;
        assertEquals(expected, item.getHitCheckObjType(),
                "hitCheckObjType が YUKKURI+SHIT+FOOD+TOY+OBJECT+VOMIT+STALK の組み合わせであること");
    }

    @Test
    void testRemoveListData() {
        ProductChute item = new ProductChute(50, 50, 0);
        int id = item.getObjId();
        assertTrue(SimYukkuri.world.getCurrentWorldState().getProductChutes().containsKey(id),
                "removeFromWorld 前は productChutes に登録されていること");
        item.removeFromWorld();
        assertFalse(SimYukkuri.world.getCurrentWorldState().getProductChutes().containsKey(id),
                "removeFromWorld 後は productChutes から除去されること");
    }

    @Test
    void testObjHitProcessWithStone() {
        ProductChute chute = new ProductChute(50, 50, 0);
        Stone stone = new Stone(50, 50, 0);
        long cashBefore = SimYukkuri.world.getPlayer().getCash();
        // Stone は Yukkuri でも Diffuser でも Yunba でもないので Cash.addCash + o.remove()
        assertEquals(0, chute.objHitProcess(stone), "石の処理で 0 を返すこと");
        assertTrue(stone.isRemoved(), "石が除去されること");
        assertTrue(SimYukkuri.world.getPlayer().getCash() != cashBefore || stone.isRemoved(),
                "石の処理でキャッシュ変化または除去が起きること");
    }

    @Test
    void testGetImageLayer_enabled_doesNotThrow() {
        ProductChute item = new ProductChute(50, 50, 0);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        int count = item.getImageLayer(layer);
        assertEquals(1, count, "enabled 状態で getImageLayer が 1 を返すこと");
    }

    @Test
    void testGetImageLayer_disabled_doesNotThrow() {
        ProductChute item = new ProductChute(50, 50, 0);
        item.setEnabled(false);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        int count = item.getImageLayer(layer);
        assertEquals(1, count, "disabled 状態でも getImageLayer が 1 を返すこと");
    }

    @Test
    void testGetBounding_doesNotThrow() {
        assertNotNull(ProductChute.getBounding(), "getBounding が非null を返すこと");
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_DiffuserIsIgnoredWithoutRemovalOrCashChange() {
            ProductChute chute = new ProductChute(50, 50, 0);
            Diffuser diffuser = new Diffuser();
            long beforeCash = SimYukkuri.world.getPlayer().getCash();

            assertEquals(0, chute.objHitProcess(diffuser));

            assertFalse(diffuser.isRemoved(), "diffuser should not be removed by the product chute");
            assertEquals(beforeCash, SimYukkuri.world.getPlayer().getCash(),
                    "ignoring a diffuser should not change player cash");
        }

        @Test
        void testScenario_YunbaIsIgnoredWithoutRemovalOrCashChange() {
            ProductChute chute = new ProductChute(50, 50, 0);
            Yunba yunba = new Yunba();
            long beforeCash = SimYukkuri.world.getPlayer().getCash();

            assertEquals(0, chute.objHitProcess(yunba));

            assertFalse(yunba.isRemoved(), "yunba should not be removed by the product chute");
            assertEquals(beforeCash, SimYukkuri.world.getPlayer().getCash(),
                    "ignoring a yunba should not change player cash");
        }

        @Test
        void testScenario_PackedBodyIsSoldAndRemovedWithNetCashGain() {
            ProductChute chute = new ProductChute(50, 50, 0);
            Yukkuri body = WorldTestHelper.createBody();
            body.setPacked(true);
            body.setAgeState(org.simyukkuri.enums.AgeState.ADULT);
            long beforeCash = SimYukkuri.world.getPlayer().getCash();

            assertEquals(0, chute.objHitProcess(body));

            assertTrue(body.isRemoved(), "processed body should be removed after being sold through the chute");
            assertTrue(SimYukkuri.world.getPlayer().getCash() != beforeCash,
                    "selling a packed body should still change player cash through the sale and chute cost");
        }
    }
}
