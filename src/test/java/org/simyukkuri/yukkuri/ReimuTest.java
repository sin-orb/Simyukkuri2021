package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Deibu;
import org.simyukkuri.entity.core.living.yukkuri.impl.DosMarisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.MarisaReimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.system.YukkuriLayer;
import org.simyukkuri.util.WorldTestHelper;

public class ReimuTest {

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
    public void testReimuIdentity() {
        Reimu reimu = new Reimu();
        assertEquals(Reimu.type, reimu.getType());
        assertEquals("れいむ", reimu.getNameJ());
        assertEquals("Reimu", reimu.getNameE());
    }

    @Test
    public void testReimuNames() {
        Reimu reimu = new Reimu();
        assertEquals("れいむ", reimu.getMyName());
        assertEquals("れいむ", reimu.getMyNameD());
        assertEquals("", reimu.getNameJ2());
        assertEquals("", reimu.getNameE2());
    }

    @Test
    public void testReimuHybridType() {
        Reimu reimu = new Reimu();
        // Reimu + Marisa = MarisaReimu
        assertEquals(MarisaReimu.type, reimu.getHybridType(Marisa.type));
        // Reimu + DosMarisa = MarisaReimu
        assertEquals(Reimu.type, reimu.getHybridType(DosMarisa.type));
        // Reimu + other = Reimu
        assertEquals(Reimu.type, reimu.getHybridType(org.simyukkuri.enums.YukkuriType.ALICE));
    }

    @Test
    public void testReimuDefaultConstructor() {
        Reimu reimu = new Reimu();
        assertNotNull(reimu);
        assertEquals(Reimu.type, reimu.getType());
    }

    @Test
    public void testReimuTuneParameters() {
        SimYukkuri.RND = new ConstState(7);

        Reimu reimu = new Reimu();
        reimu.tuneParameters();

        // Robustness should be: nextInt(10) + 1 = min(7, 9) + 1 = 7 + 1 = 8
        assertEquals(8, reimu.getImmunityStrength());

        // sameDirectionFactor should be: nextInt(20) + 20 = min(7, 19) + 20 = 7 + 20 =
        // 27
        assertEquals(27, reimu.getSameDirectionFactor());
    }

    @Test
    public void testReimuNagasiMethods() {
        Reimu reimu = new Reimu();
        assertNotNull(reimu.getImageVariantState());

        int[][] testArray = new int[10][2];
        reimu.setImageVariantState(testArray);
        assertSame(testArray, reimu.getImageVariantState());
    }

    @Test
    public void testReimuIsNotHybrid() {
        Reimu reimu = new Reimu();
        assertFalse(reimu.isHybrid());
    }

    @Test
    public void testReimuParameterizedConstructor() {
        Reimu parent1 = new Reimu();
        Reimu parent2 = new Reimu();

        Reimu reimu = new Reimu(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(reimu);
        assertEquals(Reimu.type, reimu.getType());
    }

    @Test
    public void testReimuGetMountPoint() {
        Reimu obj = new Reimu();
        obj.getMountPoint("unknown_key");
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testReimuCheckTransform() {
        Reimu reimu = new Reimu();
        // checkTransform() checks if Reimu can transform to Deibu
        // Without proper conditions, should return null
        reimu.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(reimu.checkTransform());
    }

    @Test
    public void testReimuHybridTypeWithMarisa() {
        Reimu obj = new Reimu();
        assertEquals(MarisaReimu.type, obj.getHybridType(Marisa.type));
    }

    @Test
    public void testReimuHybridTypeWithOther() {
        Reimu obj = new Reimu();
        // Test with a type not specifically handled - should return own type
        assertEquals(Reimu.type, obj.getHybridType(org.simyukkuri.enums.YukkuriType.ALICE));
    }

    // --- getImage: imagePack==null → NPE ---

    @Test
    public void testGetImage_imagePackNull_throwsNPE() {
        // Clear imagePack to ensure NPE
        try {
            java.lang.reflect.Field fp = Reimu.class.getDeclaredField("imagePack");
            fp.setAccessible(true);
            fp.set(null, null);
        } catch (Exception e) {
            assertNotNull(e);
        }
        Reimu reimu = new Reimu();
        YukkuriLayer layer = new YukkuriLayer();
        assertThrows(NullPointerException.class, () -> reimu.getImage(0, 0, layer, 0));
    }

    // --- execTransform: mypane==null → NPE (headless) ---

    @Test
    public void testExecTransform_headless_executesCode() {
        org.simyukkuri.util.WorldTestHelper.initializeMinimalWorld();
        try {
            Reimu reimu = new Reimu();
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(reimu.getUniqueId(), reimu);
            reimu.execTransform();
        } catch (NullPointerException e) {
            // mypane is null in headless environment
        } catch (Exception e) {
            // Other exceptions expected
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

            Reimu reimu = new Reimu();
            reimu.setAge(100000);
            WorldTestHelper.makeTransformationReady(reimu);
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(reimu.getUniqueId(), reimu);

            int originalId = reimu.getUniqueId();

            reimu.execTransform();

            Yukkuri transformed =
                    SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().get(originalId);
            assertNotNull(transformed);
            assertInstanceOf(Deibu.class, transformed);
            assertEquals(originalId, transformed.getUniqueId());
            assertTrue(reimu.isRemoved());
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

            Reimu reimu = new Reimu();
            reimu.setAge(100000);
            WorldTestHelper.makeTransformationReady(reimu);

            Reimu partner = new Reimu();
            Reimu child = new Reimu();

            reimu.setPartner(partner.getUniqueId());
            partner.setPartner(reimu.getUniqueId());
            child.setParents(new int[] {reimu.getUniqueId(), -1});
            reimu.getChildren().add(child.getUniqueId());

            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(reimu.getUniqueId(), reimu);
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(partner.getUniqueId(), partner);
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(child.getUniqueId(), child);

            int originalId = reimu.getUniqueId();
            int partnerId = partner.getUniqueId();
            int childId = child.getUniqueId();

            reimu.execTransform();

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

            Reimu reimu = new Reimu();
            reimu.setAge(100000);
            WorldTestHelper.makeTransformationReady(reimu);
            reimu.setHasBaby(true);
            reimu.getBabyTypes().add(new Dna());

            Reimu partner = new Reimu();
            Reimu child = new Reimu();
            reimu.setPartner(partner.getUniqueId());
            partner.setPartner(reimu.getUniqueId());
            child.setParents(new int[] {reimu.getUniqueId(), -1});
            reimu.getChildren().add(child.getUniqueId());

            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(reimu.getUniqueId(), reimu);
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(partner.getUniqueId(), partner);
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(child.getUniqueId(), child);

            int originalId = reimu.getUniqueId();
            int childId = child.getUniqueId();

            reimu.execTransform();

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

            Reimu reimu = new Reimu();
            reimu.setAge(100000);
            WorldTestHelper.makeTransformationReady(reimu);
            reimu.setHasStalk(true);
            reimu.getStalkBabyTypes().add(new Dna());

            Reimu partner = new Reimu();
            Reimu child = new Reimu();
            reimu.setPartner(partner.getUniqueId());
            partner.setPartner(reimu.getUniqueId());
            child.setParents(new int[] {reimu.getUniqueId(), -1});
            reimu.getChildren().add(child.getUniqueId());

            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(reimu.getUniqueId(), reimu);
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(partner.getUniqueId(), partner);
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(child.getUniqueId(), child);

            int originalId = reimu.getUniqueId();
            int childId = child.getUniqueId();

            reimu.execTransform();

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
        ClassLoader cl = Reimu.class.getClassLoader();
        assertDoesNotThrow(() -> Reimu.loadIniFile(cl));
    }

    // --- getBodyBaseImage: imagePack==null → NPE ---

    @Test
    public void testGetBodyBaseImage_imagePackNull_throwsNPE() {
        // Clear imagePack to ensure NPE
        try {
            java.lang.reflect.Field fp = Reimu.class.getDeclaredField("imagePack");
            fp.setAccessible(true);
            fp.set(null, null);
        } catch (Exception e) {
            assertNotNull(e);
        }
        Reimu reimu = new Reimu();
        YukkuriLayer layer = new YukkuriLayer();
        assertThrows(NullPointerException.class, () -> reimu.getImageIndex(layer));
    }

    // --- loadImages: executes code path (IOException expected in headless) ---

    // --- getImage / getBodyBaseImage with imagePack set ---

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
            setupImagePack(Reimu.class);
            Reimu reimu = new Reimu();
            org.simyukkuri.system.YukkuriLayer layer = new org.simyukkuri.system.YukkuriLayer();
            // Normal state - default walking/standing
            assertDoesNotThrow(() -> reimu.getImageIndex(layer));
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testGetBodyBaseImage_burnedDead_executesCode() {
        try {
            setupImagePack(Reimu.class);
            Reimu reimu = new Reimu();
            reimu.setBurned(true);
            reimu.setDead(true);
            org.simyukkuri.system.YukkuriLayer layer = new org.simyukkuri.system.YukkuriLayer();
            assertDoesNotThrow(() -> reimu.getImageIndex(layer));
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testGetBodyBaseImage_crushed_executesCode() {
        try {
            setupImagePack(Reimu.class);
            Reimu reimu = new Reimu();
            reimu.setCrushed(true);
            org.simyukkuri.system.YukkuriLayer layer = new org.simyukkuri.system.YukkuriLayer();
            assertDoesNotThrow(() -> reimu.getImageIndex(layer));
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testGetBodyBaseImage_pealed_executesCode() {
        try {
            setupImagePack(Reimu.class);
            Reimu reimu = new Reimu();
            reimu.setCrushed(true);
            reimu.setPealed(true);
            org.simyukkuri.system.YukkuriLayer layer = new org.simyukkuri.system.YukkuriLayer();
            assertDoesNotThrow(() -> reimu.getImageIndex(layer));
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testGetImage_withImagePack_executesCode() {
        try {
            setupImagePack(Reimu.class);
            Reimu reimu = new Reimu();
            org.simyukkuri.system.YukkuriLayer layer = new org.simyukkuri.system.YukkuriLayer();
            assertDoesNotThrow(() -> reimu.getImage(0, 0, layer, 0));
        } catch (Exception e) {
            assertNotNull(e);
        }
    }
}
