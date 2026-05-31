package org.simyukkuri.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.world.item.Generator;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;

class GeneratorTest extends ItemTestBase {
    @Test
    void testConstructorDefault() {
        Generator item = new Generator(0, 0, 0);
        // 初期値の完全確認
        assertEquals(Type.PLATFORM,          item.getObjType(),          "objType は PLATFORM であること");
        assertEquals(WorldEntityKind.GENERATOR, item.getWorldEntityType(), "worldEntityType は GENERATOR であること");
        assertEquals(4,     item.getInterval(), "interval は 4 であること");
        assertEquals(5000,  item.getValue(),    "value は 5000 であること");
        assertEquals(10,    item.getCost(),     "cost は 10 であること");
        assertFalse(item.isRemoved(),           "初期状態では removed=false であること");
    }

    @Test
    void testGetImageLayer_enabled_doesNotThrow() {
        Generator item = new Generator(0, 0, 0);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        // enabled=true で getImageLayer が 1 を返すこと
        int count = item.getImageLayer(layer);
        assertEquals(1, count, "enabled 状態で getImageLayer は 1 を返すこと");
    }

    @Test
    void testGetImageLayer_disabled_doesNotThrow() {
        Generator item = new Generator(0, 0, 0);
        item.setEnabled(false);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        // disabled でも getImageLayer が 1 を返すこと（別画像を使用）
        int count = item.getImageLayer(layer);
        assertEquals(1, count, "disabled 状態でも getImageLayer は 1 を返すこと");
    }

    @Test
    void testGetShadowImage_returnsNull() {
        // Generator は影なしの仕様 → getShadowImage() は常に null を返すこと
        Generator item = new Generator(0, 0, 0);
        assertNull(item.getShadowImage(), "Generator の影画像は null（影なし仕様）であること");
    }

    @Test
    void testRemoveListData_doesNotThrow() {
        Generator item = new Generator(0, 0, 0);
        item.removeFromWorld();
        // removeFromWorld 後も item 自体は変化しないこと（Generator は現在 WorldState 未登録）
        assertFalse(item.isRemoved(), "removeFromWorld 後も isRemoved=false のまま（未登録）");
    }
}
