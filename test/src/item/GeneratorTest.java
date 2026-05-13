package src.item;

import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import src.SimYukkuri;
import src.entity.core.world.item.ItemTestBase;
import src.entity.core.world.item.Generator;

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
