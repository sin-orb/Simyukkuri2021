package src.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.SequenceRNG;
import src.SimYukkuri;
import src.enums.Attitude;
import src.enums.Intelligence;
import src.game.Dna;
import src.yukkuri.DosMarisa;
import src.yukkuri.HybridYukkuri;
import src.yukkuri.Marisa;
import src.yukkuri.Reimu;

public class YukkuriUtilCreateBabyDnaTest {

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
}
