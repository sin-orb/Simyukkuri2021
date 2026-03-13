package src.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import src.SimYukkuri;
import src.base.ItemTestBase;

class GeneratorTest extends ItemTestBase {
    @Test
    void testConstructor_Default() {
        Generator item = new Generator(0, 0, 0);
        item.setObjId(1);
        // Generator is not in MapPlaceData currently
        verifyCommonProperties(item);
    }

    @Test
    void testGetImageLayer_enabled_doesNotThrow() {
        Generator item = new Generator(0, 0, 0);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    @Test
    void testGetImageLayer_disabled_doesNotThrow() {
        Generator item = new Generator(0, 0, 0);
        item.setEnabled(false);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    @Test
    void testGetShadowImage_returnsNull() {
        Generator item = new Generator(0, 0, 0);
        assertNull(item.getShadowImage());
    }

    @Test
    void testRemoveListData_doesNotThrow() {
        Generator item = new Generator(0, 0, 0);
        assertDoesNotThrow(() -> item.removeListData());
    }
}
