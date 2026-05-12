package src.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import src.entity.core.living.yukkuri.Dna;
import src.enums.Attitude;
import src.enums.Intelligence;
import src.entity.core.living.yukkuri.impl.Tarinai;

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

    @Nested
    class RegressionScenarios {
        @Test
        void testScenario_DefaultConstructorLeavesParentIdsUnset() {
            Dna dna = new Dna();

            assertEquals(0, dna.getFather());
            assertEquals(0, dna.getMother());
            assertFalse(dna.isRaperChild());
        }

        @Test
        void testScenario_ParameterizedConstructorDoesNotImplyAnyParentIds() {
            Dna dna = new Dna(2006, Attitude.SHITHEAD, Intelligence.WISE, true);

            assertEquals(2006, dna.getType());
            assertEquals(Attitude.SHITHEAD, dna.getAttitude());
            assertEquals(Intelligence.WISE, dna.getIntelligence());
            assertTrue(dna.isRaperChild());
            assertEquals(0, dna.getFather());
            assertEquals(0, dna.getMother());
        }
    }
}
