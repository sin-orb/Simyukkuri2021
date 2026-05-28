package org.simyukkuri.engine.birth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.Terrarium;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.impl.Deibu;
import org.simyukkuri.entity.core.living.yukkuri.impl.DosMarisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.HybridYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.MarisaTsumuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.Tarinai;
import org.simyukkuri.entity.core.living.yukkuri.impl.TarinaiReimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.WasaReimu;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.Numbering;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.util.GameRandom;

public class BabyDnaFactoryTest {

    private java.util.Random originalRnd;

    private static final class SequenceBooleanRng extends java.util.Random {
        private final int[] ints;
        private final boolean[] booleans;
        private int intIndex = 0;
        private int booleanIndex = 0;

        private SequenceBooleanRng(int[] ints, boolean... booleans) {
            this.ints = ints;
            this.booleans = booleans;
        }

        @Override
        public int nextInt(int bound) {
            int value = ints[Math.min(intIndex, ints.length - 1)];
            intIndex++;
            return Math.abs(value) % bound;
        }

        @Override
        public boolean nextBoolean() {
            if (booleans.length == 0) {
                return false;
            }
            boolean value = booleans[Math.min(booleanIndex, booleans.length - 1)];
            booleanIndex++;
            return value;
        }
    }

    @BeforeEach
    public void setUp() {
        originalRnd = SimYukkuri.RND;
        resetTerrariumFlags();
    }

    @AfterEach
    public void tearDown() {
        SimYukkuri.RND = originalRnd;
        resetTerrariumFlags();
    }

    private static void setTerrariumFlag(String fieldName, boolean value) {
        try {
            java.lang.reflect.Field field = Terrarium.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setBoolean(null, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void resetTerrariumFlags() {
        setTerrariumFlag("hybridSteam", false);
    }

    @Test
    public void testCreateBabyDnaWithNullMother() {
        SimYukkuri.RND = new ConstState(1);
        Dna dna = BabyDnaFactory.createBabyDna(null, null, YukkuriType.fromTypeId(0), Attitude.AVERAGE,
                Intelligence.AVERAGE,
                false, false, true);
        assertNull(dna);
    }

    @Test
    public void testCreateBabyDnaWithNullFatherDoesNotThrow() {
        ConstState rng = new ConstState(1);
        rng.setFixedBoolean(false);
        SimYukkuri.RND = rng;
        Reimu mother = new Reimu();
        mother.setUniqueId(1);
        Dna dna = BabyDnaFactory.createBabyDna(mother, null, mother.getType(), Attitude.AVERAGE,
                Intelligence.AVERAGE, false, false, true);
        assertNotNull(dna);
        assertEquals(-1, dna.getFather());
        assertEquals(mother.getUniqueId(), dna.getMother());
        assertEquals(Reimu.type, dna.getType());
        assertEquals(Attitude.AVERAGE, dna.getAttitude());
        assertEquals(Intelligence.AVERAGE, dna.getIntelligence());
        assertFalse(dna.isRaperChild());
    }

    @Test
    public void testCreateBabyDnaForceCreate() {
        ConstState rng = new ConstState(1);
        rng.setFixedBoolean(true);
        SimYukkuri.RND = rng;

        Reimu mother = new Reimu();
        Marisa father = new Marisa();
        mother.setUniqueId(1);
        father.setUniqueId(2);

        Dna dna = BabyDnaFactory.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                Intelligence.AVERAGE, false, false, true);

        assertNotNull(dna);
        assertEquals(Marisa.type, dna.getType());
        assertEquals(mother.getUniqueId(), dna.getMother());
        assertEquals(father.getUniqueId(), dna.getFather());
        assertEquals(Attitude.AVERAGE, dna.getAttitude());
        assertEquals(Intelligence.AVERAGE, dna.getIntelligence());
        assertFalse(dna.isRaperChild());
    }

    @Test
    public void testCreateBabyDnaNoHybridWhenDosMarisaParent() {
        ConstState rng = new ConstState(1);
        rng.setFixedBoolean(false);
        SimYukkuri.RND = rng;

        DosMarisa mother = new DosMarisa();
        Reimu father = new Reimu();
        mother.setUniqueId(1);
        father.setUniqueId(2);

        Dna dna = BabyDnaFactory.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                Intelligence.AVERAGE, false, false, true);

        assertNotNull(dna);
        assertEquals(Marisa.type, dna.getType());
        assertEquals(mother.getUniqueId(), dna.getMother());
        assertEquals(father.getUniqueId(), dna.getFather());
        assertEquals(Attitude.AVERAGE, dna.getAttitude());
        assertEquals(Intelligence.AVERAGE, dna.getIntelligence());
        assertFalse(dna.isRaperChild());
        assertNotEquals(HybridYukkuri.type, dna.getType());
    }

    @Test
    public void testCreateBabyDnaFailsWhenRandomZeroAndForceDisabled() {
        SimYukkuri.RND = new ConstState(0);

        Reimu mother = new Reimu();
        Reimu father = new Reimu();

        Dna dna = BabyDnaFactory.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                Intelligence.AVERAGE, false, false, false);

        assertNull(dna, "Force-disabled creation should return null when the random check fails");
    }

    @Nested
    class RegressionScenarios {
        @Test
        void testScenario_FatherDamageDegradesBabyToTarinaiAndCopiesParentIds() {
            ConstState rng = new ConstState(1);
            rng.setFixedBoolean(false);
            SimYukkuri.RND = rng;

            Reimu mother = new Reimu();
            mother.setUniqueId(Numbering.INSTANCE.numberingYukkuriId());
            Reimu father = new Reimu();
            father.setUniqueId(Numbering.INSTANCE.numberingYukkuriId());

            Dna dna = BabyDnaFactory.createBabyDna(mother, father, father.getType(), Attitude.NICE,
                    Intelligence.WISE, true, true, true);

            assertNotNull(dna);
            assertEquals(Tarinai.type, dna.getType());
            assertEquals(mother.getUniqueId(), dna.getMother());
            assertEquals(father.getUniqueId(), dna.getFather());
            assertTrue(dna.isRaperChild());
        }

        @Test
        void testScenario_OverPregnantReimuParentsCanProduceTarinaiReimu() {
            ConstState rng = new ConstState(1);
            rng.setFixedBoolean(true);
            SimYukkuri.RND = rng;

            Reimu mother = new Reimu();
            mother.setUniqueId(Numbering.INSTANCE.numberingYukkuriId());
            mother.setPregnantLimit(0);
            Reimu father = new Reimu();
            father.setUniqueId(Numbering.INSTANCE.numberingYukkuriId());

            Dna dna = BabyDnaFactory.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                    Intelligence.AVERAGE, false, false, true);

            assertNotNull(dna);
            assertEquals(TarinaiReimu.type, dna.getType());
            assertEquals(mother.getUniqueId(), dna.getMother());
            assertEquals(father.getUniqueId(), dna.getFather());
            assertFalse(dna.isRaperChild());
        }

        @Test
        void testScenario_DifferentParentsCanProduceConcreteHybridType() {
            Reimu mother = new Reimu();
            Marisa father = new Marisa();
            SimYukkuri.RND = new SequenceBooleanRng(new int[] { 1, 0, 1, 1, 1, 1, 1 }, true);

            Dna dna = BabyDnaFactory.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                    Intelligence.AVERAGE, false, false, true);

            assertNotNull(dna);
            assertEquals(mother.getHybridType(father.getType()), dna.getType());
        }

        @Test
        void testScenario_HybridSteamForcesHybridTypeForDifferentParents() {
            ConstState rng = new ConstState(1);
            rng.setFixedBoolean(false);
            SimYukkuri.RND = rng;
            setTerrariumFlag("hybridSteam", true);

            Reimu mother = new Reimu();
            Marisa father = new Marisa();

            Dna dna = BabyDnaFactory.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                    Intelligence.AVERAGE, false, false, true);

            assertNotNull(dna);
            assertEquals(HybridYukkuri.type, dna.getType());
        }

        @Test
        void testScenario_DosMarisaSelectedTypeFallsBackToMarisa() {
            ConstState rng = new ConstState(1);
            rng.setFixedBoolean(true);
            SimYukkuri.RND = rng;

            Reimu mother = new Reimu();
            DosMarisa father = new DosMarisa();

            Dna dna = BabyDnaFactory.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                    Intelligence.AVERAGE, false, false, true);

            assertNotNull(dna);
            assertEquals(Marisa.type, dna.getType());
        }

        @Test
        void testScenario_DeibuSelectedTypeFallsBackToReimu() {
            ConstState rng = new ConstState(1);
            rng.setFixedBoolean(false);
            SimYukkuri.RND = rng;

            Deibu mother = new Deibu();
            Reimu father = new Reimu();

            Dna dna = BabyDnaFactory.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                    Intelligence.AVERAGE, false, false, true);

            assertNotNull(dna);
            assertEquals(Reimu.type, dna.getType());
        }

        @Test
        void testScenario_ChangelingOverrideCanReplaceParentTypeWithRareType() {
            Reimu mother = new Reimu();
            Reimu father = new Reimu();
            SimYukkuri.RND = new SequenceBooleanRng(new int[] { 1, 0, 0, 4, 1 }, false);

            Dna dna = BabyDnaFactory.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                    Intelligence.AVERAGE, false, false, true);

            assertNotNull(dna);
            assertEquals(YukkuriType.MEIRIN, dna.getType());
        }

        @Test
        void testScenario_ReimuTypeCanMutateToWasaReimu() {
            Reimu mother = new Reimu();
            Reimu father = new Reimu();
            SimYukkuri.RND = new SequenceBooleanRng(new int[] { 1, 1, 0, 1, 1 }, false);

            Dna dna = BabyDnaFactory.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                    Intelligence.AVERAGE, false, false, true);

            assertNotNull(dna);
            assertEquals(WasaReimu.type, dna.getType());
        }

        @Test
        void testScenario_MarisaTypeCanMutateToTsumuriSubtype() {
            Marisa mother = new Marisa();
            Marisa father = new Marisa();
            SimYukkuri.RND = new SequenceBooleanRng(new int[] { 1, 1, 0, 1, 1 }, false);

            Dna dna = BabyDnaFactory.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                    Intelligence.AVERAGE, false, false, true);

            assertNotNull(dna);
            assertEquals(MarisaTsumuri.type, dna.getType());
        }

        @Test
        void testScenario_MotherAncestorAtavismCanOverrideBabyType() {
            Reimu mother = new Reimu();
            mother.getAncestors().add(Marisa.type.getTypeId());
            Reimu father = new Reimu();
            SimYukkuri.RND = new SequenceBooleanRng(
                    new int[] { 0, 0, 1, 1, 1, 1, 1, 1, 1 }, false);

            Dna dna = BabyDnaFactory.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                    Intelligence.AVERAGE, false, false, true);

            assertNotNull(dna);
            assertEquals(Marisa.type, dna.getType());
        }

        @Test
        void testResolveAttitudeBaseZeroRareRollCanProduceShithead() {
            GameRandom.setOverride(new org.simyukkuri.util.RandomSource() {
                @Override
                public int nextInt(int bound) {
                    if (bound == 20) {
                        return 0;
                    }
                    if (bound == 2) {
                        return 1;
                    }
                    return 0;
                }

                @Override
                public boolean nextBoolean() {
                    return false;
                }
            });
            try {
                Reimu mother = new Reimu();
                mother.setAttitude(Attitude.VERY_NICE);

                Attitude attitude = YukkuriBirthTypeResolver.resolveAttitude(mother, Attitude.VERY_NICE);

                assertEquals(Attitude.SHITHEAD, attitude);
            } finally {
                GameRandom.clearOverride();
            }
        }

        @Test
        void testScenario_IntelligenceBaseFourRareRollCanProduceWise() {
            Reimu mother = new Reimu();
            Reimu father = new Reimu();
            mother.setAttitude(Attitude.VERY_NICE);
            mother.setIntelligence(Intelligence.FOOL);
            SimYukkuri.RND = new SequenceBooleanRng(
                    new int[] { 1, 1, 1, 1, 2, 0, 0 }, true);

            Dna dna = BabyDnaFactory.createBabyDna(mother, father, Reimu.type, Attitude.VERY_NICE,
                    Intelligence.FOOL, false, false, true);

            assertNotNull(dna);
            assertEquals(Intelligence.WISE, dna.getIntelligence());
        }
    }
}
