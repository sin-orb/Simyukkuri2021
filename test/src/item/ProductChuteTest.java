package src.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import src.SimYukkuri;
import src.base.ItemTestBase;
import src.enums.ObjEXType;
import src.enums.Type;

class ProductChuteTest extends ItemTestBase {
    @Test
    void testConstructor_Default() {
        ProductChute item = new ProductChute();
        item.setObjId(1);
        SimYukkuri.world.getCurrentMap().getProductchute().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getProductchute().containsKey(item.getObjId()));
    }

    @Test
    void testConstructorWithCoords() {
        ProductChute item = new ProductChute(100, 200, 0);
        assertEquals(100, item.getX());
        assertEquals(200, item.getY());
        assertEquals(Type.PLATFORM, item.getObjType());
        assertEquals(ObjEXType.PRODUCTCHUTE, item.getObjEXType());
        assertEquals(5000, item.getValue());
        assertEquals(50, item.getCost());
        assertEquals(10, item.getInterval());
        assertTrue(SimYukkuri.world.getCurrentMap().getProductchute().containsKey(item.getObjId()));
    }

    @Test
    void testGetShadowImageIsNull() {
        ProductChute item = new ProductChute();
        assertNull(item.getShadowImage());
    }

    @Test
    void testGetHitCheckObjType() {
        ProductChute item = new ProductChute();
        // YUKKURI + SHIT + FOOD + TOY + OBJECT + VOMIT + STALK
        assertTrue(item.getHitCheckObjType() > 0);
    }

    @Test
    void testRemoveListData() {
        ProductChute item = new ProductChute(50, 50, 0);
        int id = item.getObjId();
        assertTrue(SimYukkuri.world.getCurrentMap().getProductchute().containsKey(id));
        item.removeListData();
        assertFalse(SimYukkuri.world.getCurrentMap().getProductchute().containsKey(id));
    }

    @Test
    void testObjHitProcessWithStone() {
        ProductChute chute = new ProductChute(50, 50, 0);
        Stone stone = new Stone(50, 50, 0);
        // Stone は Body でも Diffuser でも Yunba でもないので Cash.addCash + o.remove()
        assertEquals(0, chute.objHitProcess(stone));
        assertTrue(stone.isRemoved());
    }

    @Test
    void testGetImageLayer_enabled_doesNotThrow() {
        ProductChute item = new ProductChute(50, 50, 0);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    @Test
    void testGetImageLayer_disabled_doesNotThrow() {
        ProductChute item = new ProductChute(50, 50, 0);
        item.setEnabled(false);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    @Test
    void testGetBounding_doesNotThrow() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> ProductChute.getBounding());
    }
}
