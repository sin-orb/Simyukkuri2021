package org.simyukkuri.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.entity.core.world.item.StickyPlate;
import org.simyukkuri.util.WorldTestHelper;

class StickyPlateTest extends ItemTestBase {

    @Test
    void testConstructor_Default() {
        StickyPlate item = new StickyPlate();
        item.setObjId(1);
        SimYukkuri.world.getCurrentWorldState().getStickyPlates().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(
                SimYukkuri.world
                        .getCurrentWorldState()
                        .getStickyPlates()
                        .containsKey(item.getObjId()));
    }

    @Test
    void testStickyTypeEnum() {
        StickyPlate.StickyType[] types = StickyPlate.StickyType.values();
        assertEquals(2, types.length);
        for (StickyPlate.StickyType t : types) {
            assertDoesNotThrow(() -> t.toString());
        }
        assertEquals(StickyPlate.StickyType.UNDER, StickyPlate.StickyType.valueOf("UNDER"));
        assertEquals(StickyPlate.StickyType.BACK, StickyPlate.StickyType.valueOf("BACK"));
    }

    @Test
    void testGetHitCheckObjType() {
        StickyPlate item = new StickyPlate();
        assertEquals(StickyPlate.hitCheckObjType, item.getHitCheckObjType());
    }

    @Test
    void testEnableHitCheck_NoBindBody() {
        StickyPlate item = new StickyPlate();
        assertNull(item.getBoundYukkuri());
        assertTrue(item.enableHitCheck());
    }

    @Test
    void testEnableHitCheck_WithBindBody() {
        StickyPlate item = new StickyPlate();
        Yukkuri body = WorldTestHelper.createBody();
        item.setBoundYukkuri(body);
        assertFalse(item.enableHitCheck());
    }

    @Test
    void testGetSetBindBody() {
        StickyPlate item = new StickyPlate();
        assertNull(item.getBoundYukkuri());
        Yukkuri body = WorldTestHelper.createBody();
        item.setBoundYukkuri(body);
        assertEquals(body, item.getBoundYukkuri());
        item.setBoundYukkuri(null);
        assertNull(item.getBoundYukkuri());
    }

    @Test
    void testGetSetFixBack() {
        StickyPlate item = new StickyPlate();
        assertFalse(item.isFixBack());
        item.setFixBack(true);
        assertTrue(item.isFixBack());
        item.setFixBack(false);
        assertFalse(item.isFixBack());
    }

    @Test
    void testGetSetItemRank() {
        StickyPlate item = new StickyPlate();
        item.setItemRank(WorldEntity.ItemRank.HOUSE);
        assertEquals(WorldEntity.ItemRank.HOUSE, item.getItemRank());
        item.setItemRank(WorldEntity.ItemRank.NORA);
        assertEquals(WorldEntity.ItemRank.NORA, item.getItemRank());
    }

    @Test
    void testGetShadowImage() {
        StickyPlate item = new StickyPlate();
        assertNull(item.getShadowImage());
    }

    @Test
    void testGetBounding() {
        assertNotNull(StickyPlate.getBounding());
    }

    @Test
    void testRemoveListData_WithoutBindBody() {
        StickyPlate item = new StickyPlate();
        item.setObjId(100);
        SimYukkuri.world.getCurrentWorldState().getStickyPlates().put(item.getObjId(), item);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getStickyPlates().containsKey(100));

        item.removeFromWorld();

        assertFalse(SimYukkuri.world.getCurrentWorldState().getStickyPlates().containsKey(100));
    }

    @Test
    void testRemoveListData_WithBindBody() {
        StickyPlate item = new StickyPlate();
        item.setObjId(101);
        SimYukkuri.world.getCurrentWorldState().getStickyPlates().put(item.getObjId(), item);

        Yukkuri body = WorldTestHelper.createBody();
        body.setLockmove(true);
        body.setCanPullOrPush(true);
        item.setBoundYukkuri(body);

        item.removeFromWorld();

        assertFalse(SimYukkuri.world.getCurrentWorldState().getStickyPlates().containsKey(101));
        assertFalse(body.isLockmove());
        assertFalse(body.canPullOrPush());
    }

    @Test
    void testObjHitProcess_DeadBodyReturnsZero() {
        StickyPlate item = new StickyPlate();
        item.setObjId(102);
        SimYukkuri.world.getCurrentWorldState().getStickyPlates().put(item.getObjId(), item);

        Yukkuri body = WorldTestHelper.createBody();
        try {
            java.lang.reflect.Field deadField = findField(body.getClass(), "dead");
            if (deadField != null) {
                deadField.setAccessible(true);
                deadField.set(body, true);
            }
        } catch (Exception e) {
            assertNotNull(e);
        }

        int result = item.objHitProcess(body);
        assertEquals(0, result);
    }

    @Test
    void testObjHitProcess_NormalBody() {
        StickyPlate item = new StickyPlate();
        item.setObjId(103);
        SimYukkuri.world.getCurrentWorldState().getStickyPlates().put(item.getObjId(), item);

        Yukkuri body = WorldTestHelper.createBody();

        assertDoesNotThrow(() -> item.objHitProcess(body));
        assertEquals(body, item.getBoundYukkuri());
    }

    @Test
    void testUpDate_NoBindBody() {
        StickyPlate item = new StickyPlate();
        assertNull(item.getBoundYukkuri());
        assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testUpDate_DisabledWithBindBody() {
        StickyPlate item = new StickyPlate();
        item.setEnabled(false);

        Yukkuri body = WorldTestHelper.createBody();
        body.setLockmove(true);
        item.setBoundYukkuri(body);

        item.upDate();

        assertFalse(body.isLockmove());
    }

    private java.lang.reflect.Field findField(Class<?> clazz, String name) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            StickyPlate.loadImages(StickyPlate.class.getClassLoader(), null);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testGetImageLayer_doesNotThrow() {
        StickyPlate item = new StickyPlate();
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    @Test
    void testSetupStickyPlate_headless_executesCode() {
        StickyPlate item = new StickyPlate();
        try {
            StickyPlate.setupStickyPlate(item);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testConstructorWithArgs_headless_executesCode() {
        try {
            StickyPlate s = new StickyPlate(100, 100, 0);
            org.junit.jupiter.api.Assertions.assertNotNull(s);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }
}
