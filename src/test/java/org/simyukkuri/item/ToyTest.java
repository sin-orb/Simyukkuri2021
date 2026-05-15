package org.simyukkuri.item;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.entity.core.world.WorldEntity.ItemRank;
import org.simyukkuri.entity.core.world.item.Toy;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.util.WorldTestHelper;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.BufferedImage;

class ToyTest extends ItemTestBase {

    @Test
    void testConstructor_Parameterized() {
        // 0 = HOUSE
        Toy toy = new Toy(0, 0, WorldEntity.ItemRank.HOUSE.ordinal());
        assertNotNull(toy);
    }

    // --- Default constructor ---

    @Test
    void testConstructor_Default() {
        Toy toy = new Toy();
        assertNotNull(toy);
    }

    // --- getBounding ---

    @Test
    void testGetBounding_notNull() {
        assertNotNull(Toy.getBounding());
    }

    // --- loadImages ---

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            Toy.loadImages(Toy.class.getClassLoader(), null);
        } catch (Exception e) {
            // Expected: IOException because image files not found
        }
    }

    // --- getImageLayer: HOUSE itemRank ---

    @Test
    void testGetImageLayer_houseRank_returns1() {
        Toy toy = new Toy(0, 0, ItemRank.HOUSE.ordinal());
        BufferedImage[] layer = new BufferedImage[3];
        assertEquals(1, toy.getImageLayer(layer));
    }

    // --- getImageLayer: NORA itemRank ---

    @Test
    void testGetImageLayer_noraRank_returns1() {
        Toy toy = new Toy(0, 0, ItemRank.NORA.ordinal());
        BufferedImage[] layer = new BufferedImage[3];
        assertEquals(1, toy.getImageLayer(layer));
    }

    // --- getShadowImage ---

    @Test
    void testGetShadowImage_doesNotThrow() {
        Toy toy = new Toy();
        assertDoesNotThrow(() -> toy.getShadowImage());
    }

    // --- removeListData ---

    @Test
    void testRemoveListData_doesNotThrow() {
        Toy toy = new Toy(0, 0, ItemRank.HOUSE.ordinal());
        int id = toy.getObjId();
        SimYukkuri.world.getCurrentWorldState().getToys().put(id, toy);
        assertDoesNotThrow(() -> toy.removeFromWorld());
        assertFalse(SimYukkuri.world.getCurrentWorldState().getToys().containsKey(id));
    }

    // --- grab ---

    @Test
    void testGrab_setsGrabbedTrue() {
        Toy toy = new Toy(0, 0, ItemRank.HOUSE.ordinal());
        toy.setOwner(new Reimu());
        toy.grab();
        assertTrue(toy.isGrabbed());
        assertNull(toy.getOwner());
    }

    // --- kick ---

    @Test
    void testKick_doesNotThrow() {
        Toy toy = new Toy(0, 0, ItemRank.HOUSE.ordinal());
        assertDoesNotThrow(() -> toy.kick());
    }

    // --- setOwner / getOwner ---

    @Test
    void testSetGetOwner() {
        Toy toy = new Toy();
        Reimu reimu = new Reimu();
        toy.setOwner(reimu);
        assertSame(reimu, toy.getOwner());
    }

    // --- isOwned ---

    @Test
    void testIsOwned_correctOwner_returnsTrue() {
        Toy toy = new Toy();
        Reimu reimu = new Reimu();
        toy.setOwner(reimu);
        assertTrue(toy.isOwned(reimu));
    }

    @Test
    void testIsOwned_wrongOwner_returnsFalse() {
        Toy toy = new Toy();
        Reimu reimu1 = new Reimu();
        Reimu reimu2 = new Reimu();
        toy.setOwner(reimu1);
        assertFalse(toy.isOwned(reimu2));
    }

    // --- getItemRank / setItemRank ---

    @Test
    void testGetSetItemRank() {
        Toy toy = new Toy();
        toy.setItemRank(ItemRank.NORA);
        assertEquals(ItemRank.NORA, toy.getItemRank());
    }

    // --- Toy(int,int,int) with NORA option ---

    @Test
    void testConstructor_noraOption_setsNora() {
        Toy toy = new Toy(100, 100, ItemRank.NORA.ordinal());
        assertEquals(ItemRank.NORA, toy.getItemRank());
    }
}
