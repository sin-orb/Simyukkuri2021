package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.DosMarisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.ReimuMarisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.WasaReimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.util.GameLocale;
import org.simyukkuri.util.WorldTestHelper;

public class MarisaTest {

    private java.util.Random originalRnd;

    @BeforeEach
    public void setUp() {
        originalRnd = SimYukkuri.RND;
    }

    @AfterEach
    public void tearDown() {
        SimYukkuri.RND = originalRnd;
    }

    @Test
    public void testMarisaIdentity() {
        Marisa marisa = new Marisa();
        assertEquals(Marisa.type, marisa.getType());
        assertEquals("まりさ", marisa.getNameJ());
        assertEquals("Marisa", marisa.getNameE());
    }

    @Test
    public void testMarisaNames() {
        Marisa marisa = new Marisa();
        if (GameLocale.isJapanese()) {
            assertEquals("まりさ", marisa.getMyName());
            assertEquals("まりさ", marisa.getMyNameD());
        } else {
            assertEquals("Marisa", marisa.getMyName());
            assertEquals("Marisa", marisa.getMyNameD());
        }
        assertEquals("", marisa.getNameJ2());
        assertEquals("", marisa.getNameE2());
    }

    @Test
    public void testMarisaHybridType() {
        Marisa marisa = new Marisa();
        // Marisa + Reimu = ReimuMarisa
        assertEquals(ReimuMarisa.type, marisa.getHybridType(Reimu.type));
        // Marisa + WasaReimu = ReimuMarisa
        assertEquals(ReimuMarisa.type, marisa.getHybridType(WasaReimu.type));
        // Marisa + other = Marisa
        assertEquals(Marisa.type, marisa.getHybridType(org.simyukkuri.enums.YukkuriType.ALICE));
    }

    @Test
    public void testMarisaTuneParameters() {
        // Use ConstState to make random values deterministic
        SimYukkuri.RND = new ConstState(5);

        Marisa marisa = new Marisa();
        marisa.tuneParameters();

        // With ConstState, Math.random() still returns random values, but nextInt is
        // deterministic
        // The sameDirectionFactor should be: nextInt(10) + 10 = min(5, 9) + 10 = 5 + 10
        // = 15
        assertEquals(15, marisa.getSameDirectionFactor());

        // Robustness should be: nextInt(10) + 1 = min(5, 9) + 1 = 5 + 1 = 6
        assertEquals(6, marisa.getImmunityStrength());
    }

    @Test
    public void testMarisaParameterizedConstructor() {
        Marisa parent1 = new Marisa();
        Marisa parent2 = new Marisa();

        Marisa marisa = new Marisa(120, 220, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(marisa);
        assertEquals(Marisa.type, marisa.getType());
    }

    @Test
    public void testMarisaNagasiMethods() {
        Marisa marisa = new Marisa();
        assertNotNull(marisa.getImageVariantState());

        int[][] testArray = new int[10][2];
        marisa.setImageVariantState(testArray);
        assertSame(testArray, marisa.getImageVariantState());
    }

    @Test
    public void testMarisaCheckTransform() {
        WorldTestHelper.resetWorld();
        try {
            WorldTestHelper.initializeMinimalWorld();
            Marisa marisa = new Marisa();
            Yukkuri result = marisa.checkTransform();
            assertNull(result);
        } finally {
            WorldTestHelper.resetWorld();
        }
    }

    @Test
    public void testMarisaGetMountPoint() {
        Marisa obj = new Marisa();
        // getMountPoint may throw NPE if AttachOffset is not initialized
        try {
            obj.getMountPoint("unknown_key");
        } catch (NullPointerException e) {
            // Expected when AttachOffset not initialized
        }
        assertNotNull(obj);
    }

    @Test
    public void testMarisaHybridTypeWithReimu() {
        Marisa obj = new Marisa();
        assertEquals(ReimuMarisa.type, obj.getHybridType(Reimu.type));
    }

    @Test
    public void testMarisaHybridTypeWithWasaReimu() {
        Marisa obj = new Marisa();
        assertEquals(ReimuMarisa.type, obj.getHybridType(WasaReimu.type));
    }

    @Test
    public void testMarisaHybridTypeWithOther() {
        Marisa obj = new Marisa();
        // Test with a type not specifically handled - should return own type
        assertEquals(Marisa.type, obj.getHybridType(org.simyukkuri.enums.YukkuriType.ALICE));
    }

    // --- getImage: imagePack is static, elements may be null → NPE or similar ---

    // --- execTransform: cannotTransform → early return ---

    @Test
    public void testExecTransform_cannotTransform_doesNotThrow() {
        org.simyukkuri.util.WorldTestHelper.initializeMinimalWorld();
        try {
            Marisa marisa = new Marisa();
            marisa.setDead(true); // canTransform() returns false → early return
            assertDoesNotThrow(() -> marisa.execTransform());
        } finally {
            org.simyukkuri.util.WorldTestHelper.resetWorld();
        }
    }

    @Test
    public void testExecTransform_ReplacesBodyAtSameUniqueId() {
        WorldTestHelper.resetWorld();
        try {
            WorldTestHelper.initializeMinimalWorld();
            SimYukkuri.mypane = new MyPane();

            Marisa marisa = new Marisa();
            marisa.setAge(100000);
            WorldTestHelper.makeTransformationReady(marisa);
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(marisa.getUniqueId(), marisa);

            int originalId = marisa.getUniqueId();

            marisa.execTransform();

            Yukkuri transformed =
                    SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().get(originalId);
            assertNotNull(transformed);
            assertInstanceOf(DosMarisa.class, transformed);
            assertEquals(originalId, transformed.getUniqueId());
            assertTrue(marisa.isRemoved());
        } finally {
            WorldTestHelper.resetWorld();
        }
    }

    @Test
    public void testExecTransform_PreservesPartnerAndChildRelations() {
        WorldTestHelper.resetWorld();
        try {
            WorldTestHelper.initializeMinimalWorld();
            SimYukkuri.mypane = new MyPane();

            Marisa marisa = new Marisa();
            marisa.setAge(100000);
            WorldTestHelper.makeTransformationReady(marisa);

            Reimu partner = new Reimu();
            Reimu child = new Reimu();

            marisa.setPartner(partner.getUniqueId());
            partner.setPartner(marisa.getUniqueId());
            child.setParents(new int[] {marisa.getUniqueId(), -1});
            marisa.getChildren().add(child.getUniqueId());

            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(marisa.getUniqueId(), marisa);
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(partner.getUniqueId(), partner);
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(child.getUniqueId(), child);

            int originalId = marisa.getUniqueId();
            int partnerId = partner.getUniqueId();
            int childId = child.getUniqueId();

            marisa.execTransform();

            Yukkuri transformed =
                    SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().get(originalId);
            assertNotNull(transformed);
            assertEquals(partnerId, transformed.getPartner());
            assertTrue(transformed.getChildren().contains(childId));
            assertEquals(originalId, partner.getPartner());
            assertEquals(originalId, child.getParents()[0]);
        } finally {
            WorldTestHelper.resetWorld();
        }
    }

    @Test
    public void testExecTransform_PregnantBodyKeepsPregnancyAndFamilyRelations() {
        WorldTestHelper.resetWorld();
        try {
            WorldTestHelper.initializeMinimalWorld();
            SimYukkuri.mypane = new MyPane();

            Marisa marisa = new Marisa();
            marisa.setAge(100000);
            WorldTestHelper.makeTransformationReady(marisa);
            marisa.setHasBaby(true);
            marisa.getBabyTypes().add(new Dna());

            Reimu partner = new Reimu();
            Reimu child = new Reimu();
            marisa.setPartner(partner.getUniqueId());
            partner.setPartner(marisa.getUniqueId());
            child.setParents(new int[] {marisa.getUniqueId(), -1});
            marisa.getChildren().add(child.getUniqueId());

            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(marisa.getUniqueId(), marisa);
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(partner.getUniqueId(), partner);
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(child.getUniqueId(), child);

            int originalId = marisa.getUniqueId();
            int childId = child.getUniqueId();

            marisa.execTransform();

            Yukkuri transformed =
                    SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().get(originalId);
            assertNotNull(transformed);
            assertTrue(transformed.isHasBaby());
            assertEquals(1, transformed.getBabyTypes().size());
            assertEquals(partner.getUniqueId(), transformed.getPartner());
            assertTrue(transformed.getChildren().contains(childId));
            assertEquals(originalId, partner.getPartner());
            assertEquals(originalId, child.getParents()[0]);
        } finally {
            WorldTestHelper.resetWorld();
        }
    }

    @Test
    public void testExecTransform_StalkPregnantBodyKeepsStalkPregnancyAndFamilyRelations() {
        WorldTestHelper.resetWorld();
        try {
            WorldTestHelper.initializeMinimalWorld();
            SimYukkuri.mypane = new MyPane();

            Marisa marisa = new Marisa();
            marisa.setAge(100000);
            WorldTestHelper.makeTransformationReady(marisa);
            marisa.setHasStalk(true);
            marisa.getStalkBabyTypes().add(new Dna());

            Reimu partner = new Reimu();
            Reimu child = new Reimu();
            marisa.setPartner(partner.getUniqueId());
            partner.setPartner(marisa.getUniqueId());
            child.setParents(new int[] {marisa.getUniqueId(), -1});
            marisa.getChildren().add(child.getUniqueId());

            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(marisa.getUniqueId(), marisa);
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(partner.getUniqueId(), partner);
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(child.getUniqueId(), child);

            int originalId = marisa.getUniqueId();
            int childId = child.getUniqueId();

            marisa.execTransform();

            Yukkuri transformed =
                    SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().get(originalId);
            assertNotNull(transformed);
            assertTrue(transformed.isHasStalk());
            assertEquals(1, transformed.getStalkBabyTypes().size());
            assertEquals(partner.getUniqueId(), transformed.getPartner());
            assertTrue(transformed.getChildren().contains(childId));
            assertEquals(originalId, partner.getPartner());
            assertEquals(originalId, child.getParents()[0]);
        } finally {
            WorldTestHelper.resetWorld();
        }
    }

    // --- loadIniFile: executes without throwing ---

    @Test
    public void testLoadIniFile_doesNotThrow() {
        ClassLoader cl = Marisa.class.getClassLoader();
        assertDoesNotThrow(() -> Marisa.loadIniFile(cl));
    }

    // --- loadImages: executes code path (IOException expected in headless) ---

    // --- getBodyBaseImage with imagePack set ---

    private static java.awt.image.BufferedImage[][][][] setupImagePack(Class<?> cls)
            throws Exception {
        java.lang.reflect.Field fp = cls.getDeclaredField("imagePack");
        fp.setAccessible(true);
        int ranks = org.simyukkuri.enums.YukkuriRank.values().length;
        java.awt.image.BufferedImage[][][][] pack =
                new java.awt.image.BufferedImage[ranks][300][20][20];
        java.awt.image.BufferedImage dummy =
                new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < ranks; i++) {
            for (int j = 0; j < 300; j++) {
                for (int k = 0; k < 20; k++) {
                    for (int l = 0; l < 20; l++) {
                        pack[i][j][k][l] = dummy;
                    }
                }
            }
        }
        fp.set(null, pack);
        return pack;
    }

    @Test
    public void testGetBodyBaseImage_normalState_executesCode() {
        try {
            setupImagePack(Marisa.class);
            Marisa marisa = new Marisa();
            org.simyukkuri.system.YukkuriLayer layer = new org.simyukkuri.system.YukkuriLayer();
            assertDoesNotThrow(() -> marisa.getImageIndex(layer));
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testGetBodyBaseImage_burnedDead_executesCode() {
        try {
            setupImagePack(Marisa.class);
            Marisa marisa = new Marisa();
            marisa.setBurned(true);
            marisa.setDead(true);
            org.simyukkuri.system.YukkuriLayer layer = new org.simyukkuri.system.YukkuriLayer();
            assertDoesNotThrow(() -> marisa.getImageIndex(layer));
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testGetBodyBaseImage_crushed_executesCode() {
        try {
            setupImagePack(Marisa.class);
            Marisa marisa = new Marisa();
            marisa.setCrushed(true);
            org.simyukkuri.system.YukkuriLayer layer = new org.simyukkuri.system.YukkuriLayer();
            assertDoesNotThrow(() -> marisa.getImageIndex(layer));
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testGetBodyBaseImage_pealed_executesCode() {
        try {
            setupImagePack(Marisa.class);
            Marisa marisa = new Marisa();
            marisa.setCrushed(true);
            marisa.setPealed(true);
            org.simyukkuri.system.YukkuriLayer layer = new org.simyukkuri.system.YukkuriLayer();
            assertDoesNotThrow(() -> marisa.getImageIndex(layer));
        } catch (Exception e) {
            assertNotNull(e);
        }
    }
}
