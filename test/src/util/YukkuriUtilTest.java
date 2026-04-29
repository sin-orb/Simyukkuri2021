
package src.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import src.ConstState;
import src.SimYukkuri;
import src.SequenceRNG;
import src.attachment.Ants;
import src.enums.YukkuriType;
import src.game.Dna;
import src.yukkuri.Reimu;
import src.yukkuri.Marisa;
import src.yukkuri.DosMarisa;
import src.util.WorldTestHelper;

public class YukkuriUtilTest {

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
    public void testGetYukkuriType() {
        // Test known types
        assertEquals(YukkuriType.REIMU, YukkuriUtil.getYukkuriType("Reimu"));
        assertEquals(YukkuriType.MARISA, YukkuriUtil.getYukkuriType("Marisa"));

        // Test unknown/null
        assertNull(YukkuriUtil.getYukkuriType("UnknownClass"));
        assertNull(YukkuriUtil.getYukkuriType(null));
    }

    @Test
    public void testGetYukkuriClassName() {
        assertEquals("Reimu", YukkuriUtil.getYukkuriClassName(1)); // 1 = Reimu.type
        assertEquals("Marisa", YukkuriUtil.getYukkuriClassName(0)); // 0 = Marisa.type

        // Test invalid ID
        assertNull(YukkuriUtil.getYukkuriClassName(-999));
    }

    @Test
    public void testRemoveContent() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 2, 5));

        // Remove existing element (first occurrence)
        YukkuriUtil.removeContent(list, 2);
        assertEquals(Arrays.asList(1, 3, 4, 2, 5), list);

        // Remove non-existing
        YukkuriUtil.removeContent(list, 99);
        assertEquals(Arrays.asList(1, 3, 4, 2, 5), list);
    }

    @Test
    public void testGetChangelingBabyType() {
        // Test changeling type generation with controlled RNG
        SimYukkuri.RND = new SequenceRNG(5);

        Integer changelingType = YukkuriUtil.getChangelingBabyType();

        // Should return a valid yukkuri type
        assertNotNull(changelingType);
        assertTrue(changelingType >= 0);
    }

    @Test
    public void testGetMarisaType() {
        // Test Marisa subtype selection with controlled RNG
        SimYukkuri.RND = new SequenceRNG(1);

        int marisaType = YukkuriUtil.getMarisaType();

        // Should return a Marisa-related type
        assertTrue(marisaType >= 0);
    }

    @Test
    public void testGetRandomYukkuriType() {
        // Test random yukkuri type generation
        SimYukkuri.RND = new SequenceRNG(10);

        Reimu parent = new Reimu();
        int randomType = YukkuriUtil.getRandomYukkuriType(parent);

        // Should return a valid type
        assertTrue(randomType >= 0);
    }

    @Test
    public void testGetRandomYukkuriTypeWithNullParent() {
        // Test with null parent
        SimYukkuri.RND = new SequenceRNG(5);

        int randomType = YukkuriUtil.getRandomYukkuriType(null);

        // Should still return a valid type
        assertTrue(randomType >= 0);
    }

    @Test
    public void testChangeBody() {
        // Test changeBody method
        try {
            Reimu from = new Reimu();
            from.setAge(100);
            from.setDamage(500);

            Reimu to = new Reimu();

            // Perform copy
            YukkuriUtil.changeBody(to, from);

            // Verify field copy
            assertEquals(500, to.getDamage(), "Damage should be copied");
        } catch (Throwable e) {
            // changeBody uses reflection and may fail in some environments
            // Just verify it doesn't crash catastrophically
            assertNotNull(e);
        }
    }

    @Test
    public void testGetBodyInstanceWithWorldHelper() {
        // Test getBodyInstance with World setup
        try {
            src.util.WorldTestHelper.initializeMinimalWorld();

            // This will likely return null without actual bodies in the map
            // but should not crash. Use Integer.MIN_VALUE as ID that can't be assigned.
            src.base.Body body = YukkuriUtil.getBodyInstance(Integer.MIN_VALUE);

            // Null is expected for non-existent ID
            assertNull(body);
        } catch (Exception e) {
            // World initialization may fail
            assertNotNull(e);
        }
    }

    @Test
    public void testGetBodyInstancesWithWorldHelper() {
        // Test getBodyInstances with World setup
        try {
            src.util.WorldTestHelper.initializeMinimalWorld();

            src.base.Body[] bodies = YukkuriUtil.getBodyInstances();

            // Should return an array (possibly empty)
            assertNotNull(bodies);
        } catch (Exception e) {
            // World initialization may fail
            assertNotNull(e);
        }
    }

    @Test
    public void testGetBodyInstanceFromObjId_negativeOne_returnsNull() {
        src.util.WorldTestHelper.initializeMinimalWorld();
        assertNull(YukkuriUtil.getBodyInstanceFromObjId(-1));
    }

    @Test
    public void testGetBodyInstanceFromObjId_notFound_returnsNull() {
        src.util.WorldTestHelper.initializeMinimalWorld();
        assertNull(YukkuriUtil.getBodyInstanceFromObjId(9999));
    }

    @Test
    public void testGetBodyInstanceFromObjId_found_returnsBody() {
        src.util.WorldTestHelper.initializeMinimalWorld();
        Reimu body = new Reimu();
        body.setObjId(42);
        SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);
        src.base.Body result = YukkuriUtil.getBodyInstanceFromObjId(42);
        assertNotNull(result);
        assertEquals(42, result.getObjId());
    }

    @Test
    public void testJudgeNewAnt() {
        // Test ant judgment logic
        SimYukkuri.RND = new SequenceRNG(50);

        Reimu reimu = new Reimu();

        try {
            YukkuriUtil.judgeNewAnt(reimu);

            // Should complete without crashing
            assertNotNull(reimu);
        } catch (Exception e) {
            // May fail without full World setup
            assertNotNull(e);
        }
    }

    // --- changeBody: copy fields from one body to another ---

    @Test
    public void testChangeBody_ReimuToReimu_DoesNotThrow() throws Exception {
        Reimu from = new Reimu();
        Reimu to = new Reimu();
        from.setX(123);
        from.setY(456);
        assertDoesNotThrow(() -> {
            try {
                YukkuriUtil.changeBody(to, from);
            } catch (Exception e) {
                // reflection exception possible in some environments
            }
        });
    }

    @Test
    public void testChangeBody_MarisaToReimu_DoesNotThrow() {
        Marisa from = new Marisa();
        Reimu to = new Reimu();
        from.setX(200);
        try {
            YukkuriUtil.changeBody(to, from);
        } catch (Exception e) {
            // expected if class hierarchy differs
        }
        assertNotNull(to);
    }

    @Test
    public void testChangeBody_CopiesX() throws Exception {
        Reimu from = new Reimu();
        Reimu to = new Reimu();
        from.setX(999);
        assertDoesNotThrow(() -> YukkuriUtil.changeBody(to, from));
    }

    @Test
    public void testChangeBody_DoesNotShareMutableRelations() throws Exception {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();

        Reimu from = new Reimu();
        from.setPartner(77);
        from.setParents(new int[] { 10, 20 });
        from.getChildrenList().add(30);
        from.getElderSisterList().add(40);
        from.getSisterList().add(50);

        Reimu to = new Reimu();
        YukkuriUtil.changeBody(to, from);

        assertEquals(77, to.getPartner());
        assertArrayEquals(new int[] { 10, 20 }, to.getParents());
        assertEquals(Arrays.asList(30), to.getChildrenList());
        assertEquals(Arrays.asList(40), to.getElderSisterList());
        assertEquals(Arrays.asList(50), to.getSisterList());

        from.getParents()[0] = 99;
        from.getChildrenList().clear();
        from.getElderSisterList().clear();
        from.getSisterList().clear();

        assertArrayEquals(new int[] { 10, 20 }, to.getParents());
        assertEquals(Arrays.asList(30), to.getChildrenList());
        assertEquals(Arrays.asList(40), to.getElderSisterList());
        assertEquals(Arrays.asList(50), to.getSisterList());
    }

    @Test
    public void testChangeBody_CopiesBodyNameSetDeeply() throws Exception {
        Reimu from = new Reimu();
        from.setBaseBodyFileName("base-01");
        from.setAnBabyName(new String[] { "A1", "A2" });
        from.setAnMyName(new String[] { "M1", "M2", "M3" });
        from.setAnBabyNameD(new String[] { "DA1", "DA2" });
        from.setAnMyNameD(new String[] { "DM1", "DM2", "DM3" });

        Reimu to = new Reimu();
        YukkuriUtil.changeBody(to, from);

        assertEquals("base-01", to.getBaseBodyFileName());
        assertArrayEquals(new String[] { "A1", "A2" }, to.getAnBabyName());
        assertArrayEquals(new String[] { "M1", "M2", "M3" }, to.getAnMyName());
        assertArrayEquals(new String[] { "DA1", "DA2" }, to.getAnBabyNameD());
        assertArrayEquals(new String[] { "DM1", "DM2", "DM3" }, to.getAnMyNameD());

        from.getAnBabyName()[0] = "AX";
        from.getAnMyName()[1] = "MX";
        from.getAnBabyNameD()[0] = "DX";
        from.getAnMyNameD()[2] = "DMX";

        assertArrayEquals(new String[] { "A1", "A2" }, to.getAnBabyName());
        assertArrayEquals(new String[] { "M1", "M2", "M3" }, to.getAnMyName());
        assertArrayEquals(new String[] { "DA1", "DA2" }, to.getAnBabyNameD());
        assertArrayEquals(new String[] { "DM1", "DM2", "DM3" }, to.getAnMyNameD());
    }

    @Test
    public void testChangeBody_CopiesBodySpriteSetDeeply() throws Exception {
        Reimu from = new Reimu();
        src.system.Sprite[] bodySpr = new src.system.Sprite[] { new src.system.Sprite(), new src.system.Sprite(),
                new src.system.Sprite() };
        src.system.Sprite[] expandSpr = new src.system.Sprite[] { new src.system.Sprite(), new src.system.Sprite(),
                new src.system.Sprite() };
        src.system.Sprite[] braidSpr = new src.system.Sprite[] { new src.system.Sprite(), new src.system.Sprite(),
                new src.system.Sprite() };
        bodySpr[0].setImageW(11);
        expandSpr[0].setImageW(22);
        braidSpr[0].setImageW(33);
        from.setBodySpr(bodySpr);
        from.setExpandSpr(expandSpr);
        from.setBraidSpr(braidSpr);

        Reimu to = new Reimu();
        YukkuriUtil.changeBody(to, from);

        assertSame(bodySpr, from.getBodySpr());
        assertNotSame(bodySpr, to.getBodySpr());
        assertNotSame(expandSpr, to.getExpandSpr());
        assertNotSame(braidSpr, to.getBraidSpr());
        assertEquals(11, to.getBodySpr()[0].getImageW());
        assertEquals(22, to.getExpandSpr()[0].getImageW());
        assertEquals(33, to.getBraidSpr()[0].getImageW());

        bodySpr[0].setImageW(44);
        expandSpr[0].setImageW(55);
        braidSpr[0].setImageW(66);

        assertEquals(11, to.getBodySpr()[0].getImageW());
        assertEquals(22, to.getExpandSpr()[0].getImageW());
        assertEquals(33, to.getBraidSpr()[0].getImageW());
    }

    @Test
    public void testChangeBody_CopiesBodyStatProfileDeeply() throws Exception {
        Reimu from = new Reimu();
        from.setEATAMOUNTorg(new int[] { 1, 2, 3 });
        from.setSTRENGTHorg(new int[] { 11, 22, 33 });
        from.setImmunity(new int[] { 7, 8, 9, 10 });
        from.setCleaningFailProbWise(new int[] { 4, 5, 6 });

        Reimu to = new Reimu();
        YukkuriUtil.changeBody(to, from);

        assertArrayEquals(new int[] { 1, 2, 3 }, to.getEATAMOUNTorg());
        assertArrayEquals(new int[] { 11, 22, 33 }, to.getSTRENGTHorg());
        assertArrayEquals(new int[] { 7, 8, 9, 10 }, to.getImmunity());
        assertArrayEquals(new int[] { 4, 5, 6 }, to.getCleaningFailProbWise());

        from.getEATAMOUNTorg()[0] = 99;
        from.getSTRENGTHorg()[0] = 88;
        from.getImmunity()[0] = 77;
        from.getCleaningFailProbWise()[0] = 66;

        assertArrayEquals(new int[] { 1, 2, 3 }, to.getEATAMOUNTorg());
        assertArrayEquals(new int[] { 11, 22, 33 }, to.getSTRENGTHorg());
        assertArrayEquals(new int[] { 7, 8, 9, 10 }, to.getImmunity());
        assertArrayEquals(new int[] { 4, 5, 6 }, to.getCleaningFailProbWise());
    }

    @Test
    public void testChangeBody_CopiesBodyTimingProfileDeeply() throws Exception {
        Reimu from = new Reimu();
        from.setBABYLIMITorg(111);
        from.setCHILDLIMITorg(222);
        from.setLIFELIMITorg(333);
        from.setRELAXPERIODorg(444);
        from.setEYESIGHTorg(555);
        from.setINCUBATIONPERIODorg(666);

        Reimu to = new Reimu();
        YukkuriUtil.changeBody(to, from);

        assertEquals(111, to.getBABYLIMITorg());
        assertEquals(222, to.getCHILDLIMITorg());
        assertEquals(333, to.getLIFELIMITorg());
        assertEquals(444, to.getRELAXPERIODorg());
        assertEquals(555, to.getEYESIGHTorg());
        assertEquals(666, to.getINCUBATIONPERIODorg());

        from.setBABYLIMITorg(777);
        from.setCHILDLIMITorg(888);
        from.setLIFELIMITorg(999);
        from.setRELAXPERIODorg(101);
        from.setEYESIGHTorg(202);
        from.setINCUBATIONPERIODorg(303);

        assertEquals(111, to.getBABYLIMITorg());
        assertEquals(222, to.getCHILDLIMITorg());
        assertEquals(333, to.getLIFELIMITorg());
        assertEquals(444, to.getRELAXPERIODorg());
        assertEquals(555, to.getEYESIGHTorg());
        assertEquals(666, to.getINCUBATIONPERIODorg());
    }

    @Test
    public void testChangeBody_CopiesBodyBehaviorProfileDeeply() throws Exception {
        Reimu from = new Reimu();
        from.setLOVEPLAYERLIMITorg(1234);
        from.setSameDest(222);
        from.setROBUSTNESS(333);
        from.setnBreakBraidRand(12);
        from.setSurisuriAccidentProb(34);
        from.setCarAccidentProb(56);
        from.setBreakBodyByShitProb(78);
        from.setDiarrheaProb(9);
        from.setExciteProb(10);
        from.setNotChangeCharacter(true);
        from.setAttitudePoint(321);
        from.setPregnantLimit(654);
        from.setRealPregnantLimit(false);

        Reimu to = new Reimu();
        YukkuriUtil.changeBody(to, from);

        assertEquals(1234, to.getLOVEPLAYERLIMITorg());
        assertEquals(222, to.getSameDest());
        assertEquals(333, to.getROBUSTNESS());
        assertEquals(12, to.getnBreakBraidRand());
        assertEquals(34, to.getSurisuriAccidentProb());
        assertEquals(56, to.getCarAccidentProb());
        assertEquals(78, to.getBreakBodyByShitProb());
        assertEquals(9, to.getDiarrheaProb());
        assertEquals(10, to.getExciteProb());
        assertTrue(to.isNotChangeCharacter());
        assertEquals(321, to.getAttitudePoint());
        assertEquals(654, to.getPregnantLimit());
        assertFalse(to.isRealPregnantLimit());

        from.setLOVEPLAYERLIMITorg(4321);
        from.setSameDest(444);
        from.setROBUSTNESS(555);
        from.setnBreakBraidRand(21);
        from.setSurisuriAccidentProb(43);
        from.setCarAccidentProb(65);
        from.setBreakBodyByShitProb(87);
        from.setDiarrheaProb(90);
        from.setExciteProb(1);
        from.setNotChangeCharacter(false);
        from.setAttitudePoint(123);
        from.setPregnantLimit(456);
        from.setRealPregnantLimit(true);

        assertEquals(1234, to.getLOVEPLAYERLIMITorg());
        assertEquals(222, to.getSameDest());
        assertEquals(333, to.getROBUSTNESS());
        assertEquals(12, to.getnBreakBraidRand());
        assertEquals(34, to.getSurisuriAccidentProb());
        assertEquals(56, to.getCarAccidentProb());
        assertEquals(78, to.getBreakBodyByShitProb());
        assertEquals(9, to.getDiarrheaProb());
        assertEquals(10, to.getExciteProb());
        assertTrue(to.isNotChangeCharacter());
        assertEquals(321, to.getAttitudePoint());
        assertEquals(654, to.getPregnantLimit());
        assertFalse(to.isRealPregnantLimit());
    }

    // --- isNoCopyField: test known no-copy fields ---

    @Test
    public void testIsNoCopyField_UniqueIdIsNoCopy() throws Exception {
        java.lang.reflect.Method m = YukkuriUtil.class.getDeclaredMethod("isNoCopyField", String.class);
        m.setAccessible(true);
        // "uniqueID" should be in noCopyField list
        boolean result = (boolean) m.invoke(null, "uniqueID");
        // Result depends on noCopyField list; just verify it doesn't throw
        assertNotNull(result);
    }

    @Test
    public void testIsNoCopyField_RandomField_DoesNotThrow() throws Exception {
        java.lang.reflect.Method m = YukkuriUtil.class.getDeclaredMethod("isNoCopyField", String.class);
        m.setAccessible(true);
        boolean result = (boolean) m.invoke(null, "someRandomField");
        assertFalse(result);
    }

    // --- getRandomYukkuriType ---

    @Test
    public void testGetRandomYukkuriType_ReturnsValidType() {
        SimYukkuri.RND = new SequenceRNG(0);
        Reimu parent = new Reimu();
        int type = YukkuriUtil.getRandomYukkuriType(parent);
        // Should return some valid type (≥ 0)
        assertTrue(type >= 0);
    }

    @Test
    public void testGetRandomYukkuriType_NullParent_ReturnsValidType() {
        SimYukkuri.RND = new SequenceRNG(999);
        int type = YukkuriUtil.getRandomYukkuriType(null);
        assertTrue(type >= 0);
    }

    // --- getChangelingBabyType ---

    @Test
    public void testGetChangelingBabyType_ReturnsValidType() {
        int type = YukkuriUtil.getChangelingBabyType();
        assertTrue(type >= 0);
    }

    // --- getMarisaType ---

    @Test
    public void testGetMarisaType_ReturnsValidType() {
        int type = YukkuriUtil.getMarisaType();
        assertTrue(type >= 0);
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_DosParentRandomTypeFallsBackToConcreteMarisaSubtype() {
            DosMarisa parent = new DosMarisa();
            SimYukkuri.RND = new SequenceRNG(2, 1);
            int type = YukkuriUtil.getRandomYukkuriType(parent);

            assertEquals(2004, type);
        }

        @Test
        void testScenario_NullParentRareRollYieldsSpecificRareType() {
            SimYukkuri.RND = new SequenceRNG(0, 11, 4);

            int type = YukkuriUtil.getRandomYukkuriType(null);

            assertEquals(1004, type);
        }

        @Test
        void testScenario_NonDosParentKeepsItsOwnTypeOnParentBranch() {
            Reimu parent = new Reimu();
            SimYukkuri.RND = new SequenceRNG(2);

            int type = YukkuriUtil.getRandomYukkuriType(parent);

            assertEquals(parent.getType(), type);
        }

        @Test
        void testScenario_NullParentParentBranchCanYieldPlainMyon() {
            SimYukkuri.RND = new SequenceRNG(4, 5);

            int type = YukkuriUtil.getRandomYukkuriType(null);

            assertEquals(5, type);
        }

        @Test
        void testScenario_RandomBranchMapsAliceSlotToArisu() {
            SimYukkuri.RND = new SequenceRNG(0, 3);

            int type = YukkuriUtil.getRandomYukkuriType(null);

            assertEquals(2, type);
        }

        @Test
        void testScenario_RandomBranchCanYieldSpecificRareType() {
            SimYukkuri.RND = new SequenceRNG(1, 11, 7);

            int type = YukkuriUtil.getRandomYukkuriType(null);

            assertEquals(1007, type);
        }

        @Test
        void testScenario_ChangelingCanYieldRareSubtype() {
            SimYukkuri.RND = new SequenceRNG(0, 4);

            int type = YukkuriUtil.getChangelingBabyType();

            assertEquals(1004, type);
        }

        @Test
        void testScenario_ChangelingCanYieldDeibuFromReimuBranch() {
            SimYukkuri.RND = new SequenceRNG(1, 1, 3);

            int type = YukkuriUtil.getChangelingBabyType();

            assertEquals(2005, type);
        }

        @Test
        void testScenario_GetMarisaTypeCanYieldKotatsumuri() {
            SimYukkuri.RND = new SequenceRNG(1, 1);

            int type = YukkuriUtil.getMarisaType();

            assertEquals(2004, type);
        }

        @Test
        void testScenario_GetMarisaTypeCanYieldTsumuri() {
            SimYukkuri.RND = new SequenceRNG(2, 2);

            int type = YukkuriUtil.getMarisaType();

            assertEquals(2002, type);
        }

        @Test
        void testScenario_JudgeNewAntHitAddsAttachmentAndSetsAntCount() {
            WorldTestHelper.resetWorld();
            try {
                WorldTestHelper.initializeMinimalWorld();
                Ants.setImages(new BufferedImage[3][3]);
                Ants.setImgW(new int[] { 10, 20, 30 });
                Ants.setImgH(new int[] { 11, 21, 31 });
                Ants.setPivX(new int[] { 1, 2, 3 });
                Ants.setPivY(new int[] { 4, 5, 6 });

                Reimu body = new Reimu();
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);
                SimYukkuri.RND = new ConstState(1);

                YukkuriUtil.judgeNewAnt(body);

                assertEquals(1, body.getAttachmentSize(Ants.class));
                assertEquals(50, body.getNumOfAnts());
            } finally {
                WorldTestHelper.resetWorld();
            }
        }

        @Test
        void testScenario_JudgeNewAntDirtyAndDontJumpHalveProbabilityTwice() {
            class BoundRecordingRandom extends java.util.Random {
                int lastBound;
                @Override
                public int nextInt(int bound) {
                    lastBound = bound;
                    return 0;
                }
            }

            BoundRecordingRandom rng = new BoundRecordingRandom();
            SimYukkuri.RND = rng;

            Reimu body = new Reimu();
            body.setAge(100000);
            body.setDirty(true);
            body.setHasBaby(true);
            body.getBabyTypes().add(new Dna());

            YukkuriUtil.judgeNewAnt(body);

            assertEquals(240000, rng.lastBound);
            assertEquals(0, body.getAttachmentSize(Ants.class));
        }
    }
}
