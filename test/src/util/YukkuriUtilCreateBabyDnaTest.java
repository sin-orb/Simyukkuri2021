package src.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.SimYukkuri;
import src.ConstState;
import src.SequenceRNG;
import src.draw.Terrarium;
import src.enums.Attitude;
import src.enums.Intelligence;
import src.enums.Numbering;
import src.game.Dna;
import src.yukkuri.Deibu;
import src.yukkuri.DosMarisa;
import src.yukkuri.HybridYukkuri;
import src.yukkuri.Marisa;
import src.yukkuri.MarisaTsumuri;
import src.yukkuri.Reimu;
import src.yukkuri.Tarinai;
import src.yukkuri.TarinaiReimu;
import src.yukkuri.WasaReimu;

public class YukkuriUtilCreateBabyDnaTest {

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
        Dna dna = YukkuriUtil.createBabyDna(null, null, 0, Attitude.AVERAGE, Intelligence.AVERAGE,
                false, false, true);
        assertNull(dna);
    }

    @Test
    public void testCreateBabyDnaForceCreate() {
        SimYukkuri.RND = new SequenceRNG(1, 1, 1, 1, 1, 1, 1);

        Reimu mother = new Reimu();
        Marisa father = new Marisa();

        Dna dna = YukkuriUtil.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                Intelligence.AVERAGE, false, false, true);

        assertNotNull(dna);
        assertTrue(dna.getType() >= 0);
    }

    @Test
    public void testCreateBabyDnaNoHybridWhenDosMarisaParent() {
        SimYukkuri.RND = new SequenceRNG(0, 0, 0, 0, 0, 0, 0);

        DosMarisa mother = new DosMarisa();
        Reimu father = new Reimu();

        Dna dna = YukkuriUtil.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                Intelligence.AVERAGE, false, false, true);

        assertNotNull(dna);
        assertNotEquals(HybridYukkuri.type, dna.getType());
    }

    @Test
    public void testCreateBabyDnaFailsWhenRandomZeroAndForceDisabled() {
        SimYukkuri.RND = new SequenceRNG(100, 100, 100);

        Reimu mother = new Reimu();
        Reimu father = new Reimu();

        Dna dna = YukkuriUtil.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
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
            mother.setUniqueID(Numbering.INSTANCE.numberingYukkuriID());
            Reimu father = new Reimu();
            father.setUniqueID(Numbering.INSTANCE.numberingYukkuriID());

            Dna dna = YukkuriUtil.createBabyDna(mother, father, father.getType(), Attitude.NICE,
                    Intelligence.WISE, true, true, true);

            assertNotNull(dna);
            assertEquals(Tarinai.type, dna.getType());
            assertEquals(mother.getUniqueID(), dna.getMother());
            assertEquals(father.getUniqueID(), dna.getFather());
            assertTrue(dna.isRaperChild());
        }

        @Test
        void testScenario_OverPregnantReimuParentsCanProduceTarinaiReimu() {
            ConstState rng = new ConstState(1);
            rng.setFixedBoolean(true);
            SimYukkuri.RND = rng;

            Reimu mother = new Reimu();
            mother.setUniqueID(Numbering.INSTANCE.numberingYukkuriID());
            mother.setPregnantLimit(0);
            Reimu father = new Reimu();
            father.setUniqueID(Numbering.INSTANCE.numberingYukkuriID());

            Dna dna = YukkuriUtil.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                    Intelligence.AVERAGE, false, false, true);

            assertNotNull(dna);
            assertEquals(TarinaiReimu.type, dna.getType());
            assertEquals(mother.getUniqueID(), dna.getMother());
            assertEquals(father.getUniqueID(), dna.getFather());
            assertFalse(dna.isRaperChild());
        }

        @Test
        void testScenario_DifferentParentsCanProduceConcreteHybridType() {
            Reimu mother = new Reimu();
            Marisa father = new Marisa();
            SimYukkuri.RND = new SequenceBooleanRng(new int[] { 1, 0, 1, 1, 1, 1, 1 }, true);

            Dna dna = YukkuriUtil.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
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

            Dna dna = YukkuriUtil.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
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

            Dna dna = YukkuriUtil.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
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

            Dna dna = YukkuriUtil.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                    Intelligence.AVERAGE, false, false, true);

            assertNotNull(dna);
            assertEquals(Reimu.type, dna.getType());
        }

        @Test
        void testScenario_ChangelingOverrideCanReplaceParentTypeWithRareType() {
            Reimu mother = new Reimu();
            Reimu father = new Reimu();
            SimYukkuri.RND = new SequenceBooleanRng(new int[] { 1, 0, 0, 4, 1 }, false);

            Dna dna = YukkuriUtil.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                    Intelligence.AVERAGE, false, false, true);

            assertNotNull(dna);
            assertEquals(1004, dna.getType());
        }

        @Test
        void testScenario_ReimuTypeCanMutateToWasaReimu() {
            Reimu mother = new Reimu();
            Reimu father = new Reimu();
            SimYukkuri.RND = new SequenceBooleanRng(new int[] { 1, 1, 0, 1, 1 }, false);

            Dna dna = YukkuriUtil.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                    Intelligence.AVERAGE, false, false, true);

            assertNotNull(dna);
            assertEquals(WasaReimu.type, dna.getType());
        }

        @Test
        void testScenario_MarisaTypeCanMutateToTsumuriSubtype() {
            Marisa mother = new Marisa();
            Marisa father = new Marisa();
            SimYukkuri.RND = new SequenceBooleanRng(new int[] { 1, 1, 0, 1, 1 }, false);

            Dna dna = YukkuriUtil.createBabyDna(mother, father, father.getType(), Attitude.AVERAGE,
                    Intelligence.AVERAGE, false, false, true);

            assertNotNull(dna);
            assertEquals(MarisaTsumuri.type, dna.getType());
        }
    }
}
