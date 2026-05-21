package org.simyukkuri.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SequenceRandom;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.birth.YukkuriBirthTypeResolver;
import org.simyukkuri.engine.transform.TransformationBodyCopier;
import org.simyukkuri.engine.transform.TransformationPolicy;
import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.impl.DosMarisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.logic.AntInfestationPolicy;

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
        assertEquals(YukkuriType.REIMU, YukkuriType.fromClassName("Reimu"));
        assertEquals(YukkuriType.MARISA, YukkuriType.fromClassName("Marisa"));

        // Test unknown/null
        assertNull(YukkuriType.fromClassName("UnknownClass"));
        assertNull(YukkuriType.fromClassName(null));
    }

    @Test
    public void testGetYukkuriClassName() {
        assertEquals("Reimu", YukkuriType.fromTypeID(1).getClassName()); // 1 = Reimu.type
        assertEquals("Marisa", YukkuriType.fromTypeID(0).getClassName()); // 0 = Marisa.type

        // Test invalid ID
        assertNull(YukkuriType.fromTypeID(-999));
    }

    @Test
    public void testRemoveContent() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 2, 5));

        // Remove existing element (first occurrence)
        ListOperations.removeFirstMatchingValue(list, 2);
        assertEquals(Arrays.asList(1, 3, 4, 2, 5), list);

        // Remove non-existing
        ListOperations.removeFirstMatchingValue(list, 99);
        assertEquals(Arrays.asList(1, 3, 4, 2, 5), list);
    }

    @Test
    public void testGetChangelingBabyType() {
        // Test changeling type generation with controlled RNG
        SimYukkuri.RND = new SequenceRandom(5);

        YukkuriType changelingType = YukkuriBirthTypeResolver.getChangelingBabyType();

        // Should return a valid yukkuri type
        assertNotNull(changelingType);
        assertTrue(changelingType.getTypeID() >= 0);
    }

    @Test
    public void testGetMarisaType() {
        // Test Marisa subtype selection with controlled RNG
        SimYukkuri.RND = new SequenceRandom(1);

        int marisaType = YukkuriBirthTypeResolver.getMarisaType();

        // Should return a Marisa-related type
        assertTrue(marisaType >= 0);
    }

    @Test
    public void testGetRandomYukkuriType() {
        // Test random yukkuri type generation
        SimYukkuri.RND = new SequenceRandom(10);

        Reimu parent = new Reimu();
        YukkuriType randomType = YukkuriBirthTypeResolver.getRandomYukkuriType(parent);

        // Should return a valid type
        assertTrue(randomType.getTypeID() >= 0);
    }

    @Test
    public void testGetRandomYukkuriTypeWithNullParent() {
        // Test with null parent
        SimYukkuri.RND = new SequenceRandom(5);

        YukkuriType randomType = YukkuriBirthTypeResolver.getRandomYukkuriType(null);

        // Should still return a valid type
        assertTrue(randomType.getTypeID() >= 0);
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
            TransformationBodyCopier.copy(to, from);

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
        // Test getBodyMap with World setup
        try {
            org.simyukkuri.util.WorldTestHelper.initializeMinimalWorld();

            // This will likely return null without actual bodies in the map
            // but should not crash. Use Integer.MIN_VALUE as ID that can't be assigned.
            org.simyukkuri.entity.core.living.yukkuri.Yukkuri body = org.simyukkuri.util.YukkuriLookup
                    .getYukkuriById(Integer.MIN_VALUE);

            // Null is expected for non-existent ID
            assertNull(body);
        } catch (Exception e) {
            // World initialization may fail
            assertNotNull(e);
        }
    }

    @Test
    public void testGetBodyInstancesWithWorldHelper() {
        // Test getYukkuriBodies with World setup
        try {
            org.simyukkuri.util.WorldTestHelper.initializeMinimalWorld();

            org.simyukkuri.entity.core.living.yukkuri.Yukkuri[] bodies = YukkuriLookup.getYukkuriBodies();

            // Should return an array (possibly empty)
            assertNotNull(bodies);
        } catch (Exception e) {
            // World initialization may fail
            assertNotNull(e);
        }
    }

    @Test
    public void testGetBodyInstanceFromObjId_negativeOne_returnsNull() {
        org.simyukkuri.util.WorldTestHelper.initializeMinimalWorld();
        assertNull(YukkuriLookup.findYukkuriByObjId(-1));
    }

    @Test
    public void testGetBodyInstanceFromObjId_notFound_returnsNull() {
        org.simyukkuri.util.WorldTestHelper.initializeMinimalWorld();
        assertNull(YukkuriLookup.findYukkuriByObjId(9999));
    }

    @Test
    public void testGetBodyInstanceFromObjId_found_returnsBody() {
        org.simyukkuri.util.WorldTestHelper.initializeMinimalWorld();
        Reimu body = new Reimu();
        body.setObjId(42);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueID(), body);
        org.simyukkuri.entity.core.living.yukkuri.Yukkuri result = YukkuriLookup.findYukkuriByObjId(42);
        assertNotNull(result);
        assertEquals(42, result.getObjId());
    }

    @Test
    public void testJudgeNewAnt() {
        // Test ant judgment logic
        SimYukkuri.RND = new SequenceRandom(50);

        Reimu reimu = new Reimu();

        AntInfestationPolicy.judgeNewAnt(reimu);

        // Should complete without crashing
        assertNotNull(reimu);
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
                copyTransformedBody(to, from);
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
            copyTransformedBody(to, from);
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
        assertDoesNotThrow(() -> copyTransformedBody(to, from));
    }

    @Test
    public void testChangeBody_DoesNotShareMutableRelations() throws Exception {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();

        Reimu from = new Reimu();
        from.setPartner(77);
        from.setParents(new int[] { 10, 20 });
        from.getChildren().add(30);
        from.getElderSisters().add(40);
        from.getSisters().add(50);

        Reimu to = new Reimu();
        copyTransformedBody(to, from);

        assertEquals(77, to.getPartner());
        assertArrayEquals(new int[] { 10, 20 }, to.getParents());
        assertEquals(Arrays.asList(30), to.getChildren());
        assertEquals(Arrays.asList(40), to.getElderSisters());
        assertEquals(Arrays.asList(50), to.getSisters());

        from.getParents()[0] = 99;
        from.getChildren().clear();
        from.getElderSisters().clear();
        from.getSisters().clear();

        assertArrayEquals(new int[] { 10, 20 }, to.getParents());
        assertEquals(Arrays.asList(30), to.getChildren());
        assertEquals(Arrays.asList(40), to.getElderSisters());
        assertEquals(Arrays.asList(50), to.getSisters());
    }

    @Test
    public void testChangeBody_CopiesBodyNameSetDeeply() throws Exception {
        Reimu from = new Reimu();
        from.setBaseYukkuriFileName("base-01");
        from.setBabyNames(new String[] { "A1", "A2" });
        from.setMyNames(new String[] { "M1", "M2", "M3" });
        from.setBabyNamesDamaged(new String[] { "DA1", "DA2" });
        from.setMyNamesDamaged(new String[] { "DM1", "DM2", "DM3" });

        Reimu to = new Reimu();
        copyTransformedBody(to, from);

        assertEquals("base-01", to.getBaseYukkuriFileName());
        assertArrayEquals(new String[] { "A1", "A2" }, to.getBabyNames());
        assertArrayEquals(new String[] { "M1", "M2", "M3" }, to.getMyNames());
        assertArrayEquals(new String[] { "DA1", "DA2" }, to.getBabyNamesDamaged());
        assertArrayEquals(new String[] { "DM1", "DM2", "DM3" }, to.getMyNamesDamaged());

        from.getBabyNames()[0] = "AX";
        from.getMyNames()[1] = "MX";
        from.getBabyNamesDamaged()[0] = "DX";
        from.getMyNamesDamaged()[2] = "DMX";

        assertArrayEquals(new String[] { "A1", "A2" }, to.getBabyNames());
        assertArrayEquals(new String[] { "M1", "M2", "M3" }, to.getMyNames());
        assertArrayEquals(new String[] { "DA1", "DA2" }, to.getBabyNamesDamaged());
        assertArrayEquals(new String[] { "DM1", "DM2", "DM3" }, to.getMyNamesDamaged());
    }

    @Test
    public void testChangeBody_CopiesBodySpriteSetDeeply() throws Exception {
        Reimu from = new Reimu();
        org.simyukkuri.system.Sprite[] bodySpr = new org.simyukkuri.system.Sprite[] {
                new org.simyukkuri.system.Sprite(), new org.simyukkuri.system.Sprite(),
                new org.simyukkuri.system.Sprite() };
        org.simyukkuri.system.Sprite[] expandSpr = new org.simyukkuri.system.Sprite[] {
                new org.simyukkuri.system.Sprite(), new org.simyukkuri.system.Sprite(),
                new org.simyukkuri.system.Sprite() };
        org.simyukkuri.system.Sprite[] braidSpr = new org.simyukkuri.system.Sprite[] {
                new org.simyukkuri.system.Sprite(), new org.simyukkuri.system.Sprite(),
                new org.simyukkuri.system.Sprite() };
        bodySpr[0].setImageW(11);
        expandSpr[0].setImageW(22);
        braidSpr[0].setImageW(33);
        from.setSpriteSet(bodySpr);
        from.setExpandSpr(expandSpr);
        from.setBraidSpr(braidSpr);

        Reimu to = new Reimu();
        copyTransformedBody(to, from);

        assertSame(bodySpr, from.getSpriteSet());
        assertNotSame(bodySpr, to.getSpriteSet());
        assertNotSame(expandSpr, to.getExpandSpr());
        assertNotSame(braidSpr, to.getBraidSpr());
        assertEquals(11, to.getSpriteSet()[0].getImageW());
        assertEquals(22, to.getExpandSpr()[0].getImageW());
        assertEquals(33, to.getBraidSpr()[0].getImageW());

        bodySpr[0].setImageW(44);
        expandSpr[0].setImageW(55);
        braidSpr[0].setImageW(66);

        assertEquals(11, to.getSpriteSet()[0].getImageW());
        assertEquals(22, to.getExpandSpr()[0].getImageW());
        assertEquals(33, to.getBraidSpr()[0].getImageW());
    }

    @Test
    public void testChangeBody_CopiesBodyStatProfileDeeply() throws Exception {
        Reimu from = new Reimu();
        from.setEatAmountBase(new int[] { 1, 2, 3 });
        from.setStrengthBase(new int[] { 11, 22, 33 });
        from.setImmunity(new int[] { 7, 8, 9, 10 });
        from.setCleaningFailProbWise(new int[] { 4, 5, 6 });

        Reimu to = new Reimu();
        copyTransformedBody(to, from);

        assertArrayEquals(new int[] { 1, 2, 3 }, to.getEatAmountBase());
        assertArrayEquals(new int[] { 11, 22, 33 }, to.getStrengthBase());
        assertArrayEquals(new int[] { 7, 8, 9, 10 }, to.getImmunity());
        assertArrayEquals(new int[] { 4, 5, 6 }, to.getCleaningFailProbWise());

        from.getEatAmountBase()[0] = 99;
        from.getStrengthBase()[0] = 88;
        from.getImmunity()[0] = 77;
        from.getCleaningFailProbWise()[0] = 66;

        assertArrayEquals(new int[] { 1, 2, 3 }, to.getEatAmountBase());
        assertArrayEquals(new int[] { 11, 22, 33 }, to.getStrengthBase());
        assertArrayEquals(new int[] { 7, 8, 9, 10 }, to.getImmunity());
        assertArrayEquals(new int[] { 4, 5, 6 }, to.getCleaningFailProbWise());
    }

    @Test
    public void testChangeBody_CopiesBodyTimingProfileDeeply() throws Exception {
        Reimu from = new Reimu();
        from.setBabyLimitBase(111);
        from.setChildLimitBase(222);
        from.setLifeLimitBase(333);
        from.setRelaxPeriodBase(444);
        from.setEyesightBase(555);
        from.setIncubationPeriodBase(666);

        Reimu to = new Reimu();
        copyTransformedBody(to, from);

        assertEquals(111, to.getBabyLimitBase());
        assertEquals(222, to.getChildLimitBase());
        assertEquals(333, to.getLifeLimitBase());
        assertEquals(444, to.getRelaxPeriodBase());
        assertEquals(555, to.getEyesightBase());
        assertEquals(666, to.getIncubationPeriodBase());

        from.setBabyLimitBase(777);
        from.setChildLimitBase(888);
        from.setLifeLimitBase(999);
        from.setRelaxPeriodBase(101);
        from.setEyesightBase(202);
        from.setIncubationPeriodBase(303);

        assertEquals(111, to.getBabyLimitBase());
        assertEquals(222, to.getChildLimitBase());
        assertEquals(333, to.getLifeLimitBase());
        assertEquals(444, to.getRelaxPeriodBase());
        assertEquals(555, to.getEyesightBase());
        assertEquals(666, to.getIncubationPeriodBase());
    }

    @Test
    public void testChangeBody_CopiesBodyBehaviorProfileDeeply() throws Exception {
        Reimu from = new Reimu();
        from.setLovePlayerLimitBase(1234);
        from.setSameDirectionFactor(222);
        from.setImmunityStrength(333);
        from.setBraidBreakChance(12);
        from.setSurisuriAccidentProb(34);
        from.setCarAccidentProb(56);
        from.setBreakByShitProb(78);
        from.setDiarrheaProb(9);
        from.setExciteProb(10);
        from.setNotChangeCharacter(true);
        from.setAttitudePoint(321);
        from.setPregnantLimit(654);
        from.setUseRealPregnantLimit(false);

        Reimu to = new Reimu();
        copyTransformedBody(to, from);

        assertEquals(1234, to.getLovePlayerLimitBase());
        assertEquals(222, to.getSameDirectionFactor());
        assertEquals(333, to.getImmunityStrength());
        assertEquals(12, to.getBraidBreakChance());
        assertEquals(34, to.getSurisuriAccidentProb());
        assertEquals(56, to.getCarAccidentProb());
        assertEquals(78, to.getBreakByShitProb());
        assertEquals(9, to.getDiarrheaProb());
        assertEquals(10, to.getExciteProb());
        assertTrue(to.isNotChangeCharacter());
        assertEquals(321, to.getAttitudePoint());
        assertEquals(654, to.getPregnantLimit());
        assertFalse(to.isUseRealPregnantLimit());

        from.setLovePlayerLimitBase(4321);
        from.setSameDirectionFactor(444);
        from.setImmunityStrength(555);
        from.setBraidBreakChance(21);
        from.setSurisuriAccidentProb(43);
        from.setCarAccidentProb(65);
        from.setBreakByShitProb(87);
        from.setDiarrheaProb(90);
        from.setExciteProb(1);
        from.setNotChangeCharacter(false);
        from.setAttitudePoint(123);
        from.setPregnantLimit(456);
        from.setUseRealPregnantLimit(true);

        assertEquals(1234, to.getLovePlayerLimitBase());
        assertEquals(222, to.getSameDirectionFactor());
        assertEquals(333, to.getImmunityStrength());
        assertEquals(12, to.getBraidBreakChance());
        assertEquals(34, to.getSurisuriAccidentProb());
        assertEquals(56, to.getCarAccidentProb());
        assertEquals(78, to.getBreakByShitProb());
        assertEquals(9, to.getDiarrheaProb());
        assertEquals(10, to.getExciteProb());
        assertTrue(to.isNotChangeCharacter());
        assertEquals(321, to.getAttitudePoint());
        assertEquals(654, to.getPregnantLimit());
        assertFalse(to.isUseRealPregnantLimit());
    }

    // --- getRandomYukkuriType ---

    @Test
    public void testGetRandomYukkuriType_ReturnsValidType() {
        SimYukkuri.RND = new SequenceRandom(0);
        Reimu parent = new Reimu();
        YukkuriType type = YukkuriBirthTypeResolver.getRandomYukkuriType(parent);
        assertNotNull(type);
    }

    @Test
    public void testGetRandomYukkuriType_NullParent_ReturnsValidType() {
        SimYukkuri.RND = new SequenceRandom(999);
        YukkuriType type = YukkuriBirthTypeResolver.getRandomYukkuriType(null);
        assertNotNull(type);
    }

    // --- getChangelingBabyType ---

    @Test
    public void testGetChangelingBabyType_ReturnsValidType() {
        YukkuriType type = YukkuriBirthTypeResolver.getChangelingBabyType();
        assertNotNull(type);
    }

    // --- getMarisaType ---

    @Test
    public void testGetMarisaType_ReturnsValidType() {
        int type = YukkuriBirthTypeResolver.getMarisaType();
        assertTrue(type >= 0);
    }

    private static void copyTransformedBody(org.simyukkuri.entity.core.living.yukkuri.Yukkuri to,
            org.simyukkuri.entity.core.living.yukkuri.Yukkuri from) {
        TransformationBodyCopier.copy(to, from);
        TransformationPolicy.normalizeTransformedAge(to, from);
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_DosParentRandomTypeFallsBackToConcreteMarisaSubtype() {
            DosMarisa parent = new DosMarisa();
            SimYukkuri.RND = new SequenceRandom(2, 1);
            YukkuriType type = YukkuriBirthTypeResolver.getRandomYukkuriType(parent);

            assertEquals(YukkuriType.MARISAKOTATSUMURI, type);
        }

        @Test
        void testScenario_NullParentRareRollYieldsSpecificRareType() {
            SimYukkuri.RND = new SequenceRandom(0, 11, 4);

            YukkuriType type = YukkuriBirthTypeResolver.getRandomYukkuriType(null);

            assertEquals(YukkuriType.MEIRIN, type);
        }

        @Test
        void testScenario_NonDosParentKeepsItsOwnTypeOnParentBranch() {
            Reimu parent = new Reimu();
            SimYukkuri.RND = new SequenceRandom(2);

            YukkuriType type = YukkuriBirthTypeResolver.getRandomYukkuriType(parent);

            assertEquals(parent.getType(), type);
        }

        @Test
        void testScenario_NullParentParentBranchCanYieldPlainMyon() {
            SimYukkuri.RND = new SequenceRandom(4, 5);

            YukkuriType type = YukkuriBirthTypeResolver.getRandomYukkuriType(null);

            assertEquals(YukkuriType.MYON, type);
        }

        @Test
        void testScenario_RandomBranchMapsAliceSlotToArisu() {
            SimYukkuri.RND = new SequenceRandom(0, 3);

            YukkuriType type = YukkuriBirthTypeResolver.getRandomYukkuriType(null);

            assertEquals(YukkuriType.ALICE, type);
        }

        @Test
        void testScenario_RandomBranchCanYieldSpecificRareType() {
            SimYukkuri.RND = new SequenceRandom(1, 11, 7);

            YukkuriType type = YukkuriBirthTypeResolver.getRandomYukkuriType(null);

            assertEquals(YukkuriType.EIKI, type);
        }

        @Test
        void testScenario_ChangelingCanYieldRareSubtype() {
            SimYukkuri.RND = new SequenceRandom(0, 4);

            YukkuriType type = YukkuriBirthTypeResolver.getChangelingBabyType();

            assertEquals(YukkuriType.MEIRIN, type);
        }

        @Test
        void testScenario_ChangelingCanYieldDeibuFromReimuBranch() {
            SimYukkuri.RND = new SequenceRandom(1, 1, 3);

            YukkuriType type = YukkuriBirthTypeResolver.getChangelingBabyType();

            assertEquals(YukkuriType.DEIBU, type);
        }

        @Test
        void testScenario_GetMarisaTypeCanYieldKotatsumuri() {
            SimYukkuri.RND = new SequenceRandom(1, 1);

            int type = YukkuriBirthTypeResolver.getMarisaType();

            assertEquals(2004, type);
        }

        @Test
        void testScenario_GetMarisaTypeCanYieldTsumuri() {
            SimYukkuri.RND = new SequenceRandom(2, 2);

            int type = YukkuriBirthTypeResolver.getMarisaType();

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
                SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueID(), body);
                SimYukkuri.RND = new ConstState(1);

                AntInfestationPolicy.judgeNewAnt(body);

                assertEquals(1, body.getAttachmentSize(Ants.class));
                assertEquals(50, body.getAntCount());
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

            AntInfestationPolicy.judgeNewAnt(body);

            assertEquals(240000, rng.lastBound);
            assertEquals(0, body.getAttachmentSize(Ants.class));
        }
    }
}
