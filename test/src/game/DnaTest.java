package src.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.enums.Attitude;
import src.enums.Intelligence;
import src.yukkuri.Tarinai;

class DnaTest {

    @Test
    void testDefaultConstructor() {
        Dna dna = new Dna();
        assertEquals(Tarinai.type, dna.getType());
        assertEquals(Attitude.AVERAGE, dna.getAttitude());
        assertEquals(Intelligence.AVERAGE, dna.getIntelligence());
        assertFalse(dna.isRaperChild());
    }

    @Test
    void testParameterizedConstructor() {
        // public Dna(int t, Attitude att, Intelligence intel, boolean rape)
        int type = 100;
        Attitude att = Attitude.NICE;
        Intelligence intel = Intelligence.FOOL;
        boolean rape = true;

        Dna dna = new Dna(type, att, intel, rape);
        assertEquals(type, dna.getType());
        assertEquals(att, dna.getAttitude());
        assertEquals(intel, dna.getIntelligence());
        assertTrue(dna.isRaperChild());
    }

    @Test
    void testGettersAndSetters() {
        Dna dna = new Dna();

        dna.setType(999);
        assertEquals(999, dna.getType());

        dna.setAttitude(Attitude.SHITHEAD);
        assertEquals(Attitude.SHITHEAD, dna.getAttitude());

        dna.setIntelligence(Intelligence.WISE);
        assertEquals(Intelligence.WISE, dna.getIntelligence());

        dna.setRaperChild(true);
        assertTrue(dna.isRaperChild());

        dna.setFather(10);
        assertEquals(10, dna.getFather());

        dna.setMother(20);
        assertEquals(20, dna.getMother());
    }
}
