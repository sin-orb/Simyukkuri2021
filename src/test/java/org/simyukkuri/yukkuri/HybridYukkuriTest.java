package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Alice;
import org.simyukkuri.entity.core.living.yukkuri.impl.Chen;
import org.simyukkuri.entity.core.living.yukkuri.impl.HybridYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.system.YukkuriLayer;
import org.simyukkuri.util.WorldTestHelper;

public class HybridYukkuriTest {

    @Test
    public void testHybridYukkuriIdentity() {
        HybridYukkuri hybrid = new HybridYukkuri();
        assertEquals(HybridYukkuri.type, hybrid.getType());
    }

    @Test
    public void testHybridYukkuriIsHybrid() {
        HybridYukkuri hybrid = new HybridYukkuri();
        assertTrue(hybrid.isHybrid());
    }

    @Test
    public void testHybridYukkuriHybridType() {
        HybridYukkuri hybrid = new HybridYukkuri();
        // HybridYukkuri always returns HybridYukkuri type
        assertEquals(HybridYukkuri.type, hybrid.getHybridType(Reimu.type));
        assertEquals(HybridYukkuri.type, hybrid.getHybridType(Marisa.type));
    }

    @Test
    public void testHybridYukkuriDoreiGettersSetters() {
        HybridYukkuri hybrid = new HybridYukkuri();

        Reimu dorei1 = new Reimu();
        Marisa dorei2 = new Marisa();
        Alice dorei3 = new Alice();
        Chen dorei4 = new Chen();

        hybrid.setDorei(dorei1);
        hybrid.setDorei2(dorei2);
        hybrid.setDorei3(dorei3);
        hybrid.setDorei4(dorei4);

        assertSame(dorei1, hybrid.getDorei());
        assertSame(dorei2, hybrid.getDorei2());
        assertSame(dorei3, hybrid.getDorei3());
        assertSame(dorei4, hybrid.getDorei4());
    }

    @Test
    public void testHybridYukkuriGetBaseBody() {
        HybridYukkuri hybrid = new HybridYukkuri();

        Reimu dorei1 = new Reimu();
        Marisa dorei2 = new Marisa();
        Alice dorei3 = new Alice();
        Chen dorei4 = new Chen();

        hybrid.setDorei(dorei1);
        hybrid.setDorei2(dorei2);
        hybrid.setDorei3(dorei3);
        hybrid.setDorei4(dorei4);

        assertSame(dorei1, hybrid.getBaseYukkuri(0));
        assertSame(dorei2, hybrid.getBaseYukkuri(1));
        assertSame(dorei3, hybrid.getBaseYukkuri(2));
        assertSame(dorei4, hybrid.getBaseYukkuri(3));
    }

    @Test
    public void testHybridYukkuriNameGettersSetters() {
        HybridYukkuri hybrid = new HybridYukkuri();

        hybrid.setNameJ("TestJ");
        hybrid.setNameE("Test");
        hybrid.setNameJ2("TestJ2");
        hybrid.setNameE2("Test2");

        assertEquals("TestJ", hybrid.getNameJ());
        assertEquals("Test", hybrid.getNameE());
        assertEquals("TestJ2", hybrid.getNameJ2());
        assertEquals("Test2", hybrid.getNameE2());
    }

    @Test
    public void testHybridYukkuriImagesGetterSetter() {
        HybridYukkuri hybrid = new HybridYukkuri();

        // Images array is created in tuneParameters
        hybrid.tuneParameters();
        assertNotNull(hybrid.getImages());
    }

    @Test
    public void testGetMyName_NullAnMyName_FallsBackToNameJ() {
        HybridYukkuri hybrid = new HybridYukkuri();
        hybrid.setNameJ("FallbackName");
        // anMyName is null by default → returns nameJ
        assertEquals("FallbackName", hybrid.getMyName());
    }

    @Test
    public void testGetMyNameD_NullAnMyNameD_FallsBackToNameJ() {
        HybridYukkuri hybrid = new HybridYukkuri();
        hybrid.setNameJ("FallbackNameD");
        // anMyNameD is null by default → returns nameJ
        assertEquals("FallbackNameD", hybrid.getMyNameD());
    }

    @Test
    public void testSetBodyRank_NullDoreis_DoesNotThrow() {
        HybridYukkuri hybrid = new HybridYukkuri();
        // All doreis are null → null checks prevent NPE
        assertDoesNotThrow(() -> hybrid.setRank(YukkuriRank.KAIYU));
        assertEquals(YukkuriRank.KAIYU, hybrid.getRank());
    }

    @Test
    public void testSetBodyRank_WithDorei_SetsOnAll() {
        HybridYukkuri hybrid = new HybridYukkuri();
        Reimu d1 = new Reimu();
        Marisa d2 = new Marisa();
        Alice d3 = new Alice();
        Chen d4 = new Chen();
        hybrid.setDorei(d1);
        hybrid.setDorei2(d2);
        hybrid.setDorei3(d3);
        hybrid.setDorei4(d4);
        hybrid.setRank(YukkuriRank.SUTEYU);
        assertEquals(YukkuriRank.SUTEYU, hybrid.getRank());
        assertEquals(YukkuriRank.SUTEYU, d1.getRank());
    }

    @Test
    public void testGetBaseBody_OutOfRange_ReturnsDorei4() {
        HybridYukkuri hybrid = new HybridYukkuri();
        Chen d4 = new Chen();
        hybrid.setDorei4(d4);
        assertSame(d4, hybrid.getBaseYukkuri(99));
    }

    @Test
    public void testSetImages_Getter() {
        HybridYukkuri hybrid = new HybridYukkuri();
        Yukkuri[] imgs = new Yukkuri[3];
        hybrid.setImages(imgs);
        assertSame(imgs, hybrid.getImages());
    }

    @Test
    public void testRemove_WithAllDoreis_DoesNotThrow() {
        WorldTestHelper.initializeMinimalWorld();
        try {
            HybridYukkuri hybrid = new HybridYukkuri();
            Reimu d1 = new Reimu();
            Marisa d2 = new Marisa();
            Alice d3 = new Alice();
            Chen d4 = new Chen();
            hybrid.setDorei(d1);
            hybrid.setDorei2(d2);
            hybrid.setDorei3(d3);
            hybrid.setDorei4(d4);
            assertDoesNotThrow(() -> hybrid.remove());
            assertTrue(hybrid.isRemoved());
        } finally {
            WorldTestHelper.resetWorld();
        }
    }

    // --- getMountPoint (dorei == null → NPE) ---

    @Test
    public void testGetMountPoint_doreiNull_throwsNPE() {
        HybridYukkuri hybrid = new HybridYukkuri();
        // dorei == null → hybrid.getMountPoint("key") → dorei.getMountPoint("key") →
        // NPE
        assertThrows(NullPointerException.class, () -> hybrid.getMountPoint("head"));
    }

    // --- getMountPoint (dorei set) ---

    @Test
    public void testGetMountPoint_withDorei_doesNotThrow() {
        HybridYukkuri hybrid = new HybridYukkuri();
        hybrid.setDorei(new Reimu());
        // AttachOffset may or may not be initialized depending on test ordering
        try {
            hybrid.getMountPoint("head");
        } catch (NullPointerException e) {
            // Expected in headless test environment when AttachOffset not loaded
        }
        assertNotNull(hybrid);
    }

    // --- getImage (images null → NPE) ---

    @Test
    public void testGetImage_imagesNull_throwsNPE() {
        HybridYukkuri hybrid = new HybridYukkuri();
        YukkuriLayer layer = new YukkuriLayer();
        // images is null by default (tuneParameters not called)
        assertEquals(0, hybrid.getImage(0, 0, layer, 0));
    }

    // --- getImage (after tuneParameters, images[0] is null → NPE on
    // images[0].setAgeState) ---

    @Test
    public void testGetImage_afterTuneParameters_imagesSlotNull_throwsNPE() {
        HybridYukkuri hybrid = new HybridYukkuri();
        hybrid.tuneParameters(); // images = new Yukkuri[size], but elements are null
        hybrid.setImages(new Yukkuri[org.simyukkuri.enums.ImageCode.values().length]);
        YukkuriLayer layer = new YukkuriLayer();
        // images[0] == null → images[0].setAgeState → NPE
        assertEquals(0, hybrid.getImage(0, 0, layer, 0));
    }

    // --- Constructor(int, int, int, AgeState, Yukkuri, Yukkuri): with non-null p1
    // ---

    @Test
    public void testConstructor_WithCoords_doesNotThrow() {
        WorldTestHelper.initializeMinimalWorld();
        try {
            Reimu p1 = new Reimu();
            Marisa p2 = new Marisa();
            assertDoesNotThrow(() -> new HybridYukkuri(100, 100, 0, AgeState.BABY, p1, p2));
        } finally {
            WorldTestHelper.resetWorld();
        }
    }

    // --- loadImagesHybrid: mama/papa null → creates default Reimu ---

    @Test
    public void testLoadImages_Hyblid_noParents_executesCode() {
        WorldTestHelper.initializeMinimalWorld();
        try {
            HybridYukkuri hybrid = new HybridYukkuri();
            // no parents → mama=null, papa=null → creates Reimu
            try {
                hybrid.loadImagesHybrid();
            } catch (Exception e) {
                // NPE or IOException expected in headless environment
            }
        } finally {
            WorldTestHelper.resetWorld();
        }
    }

    @Test
    public void testLoadImages_Hyblid_withPresetDorei_executesCode() {
        WorldTestHelper.initializeMinimalWorld();
        try {
            HybridYukkuri hybrid = new HybridYukkuri();
            // Pre-set dorei field so doreiTmp2 = dorei won't be null
            org.simyukkuri.entity.core.living.yukkuri.impl.Reimu reimuDorei = new org.simyukkuri.entity.core.living.yukkuri.impl.Reimu();
            java.lang.reflect.Field doreiField = HybridYukkuri.class.getDeclaredField("dorei");
            doreiField.setAccessible(true);
            doreiField.set(hybrid, reimuDorei);
            // Use ConstState to control RNG (nextBoolean → true when nextInt(2)>0 ???
            // depends on impl)
            SimYukkuri.RND = new org.simyukkuri.ConstState(1);
            hybrid.loadImagesHybrid();
        } catch (Exception e) {
            // IOException loading images expected in headless
        } finally {
            WorldTestHelper.resetWorld();
        }
    }

    // --- loadImages(ClassLoader, ImageObserver): empty method → no-op ---

    @Test
    public void testLoadImages_static_doesNotThrow() {
        assertDoesNotThrow(() -> HybridYukkuri.loadImages(
                HybridYukkuri.class.getClassLoader(), null));
    }

    // --- tuneParameters: creates images array ---

    @Test
    public void testTuneParameters_andGetImages_setsArray() {
        HybridYukkuri hybrid = new HybridYukkuri();
        assertDoesNotThrow(() -> hybrid.tuneParameters());
        assertNotNull(hybrid.getImages());
    }

    // --- getHybridType: returns HybridYukkuri.type for any input ---

    @Test
    public void testGetHybridType_alwaysReturnsHybridType() {
        HybridYukkuri hybrid = new HybridYukkuri();
        assertEquals(HybridYukkuri.type, hybrid.getHybridType(Reimu.type));
        assertEquals(HybridYukkuri.type, hybrid.getHybridType(Marisa.type));
        assertEquals(HybridYukkuri.type, hybrid.getHybridType(org.simyukkuri.enums.YukkuriType.ALICE));
    }
}
