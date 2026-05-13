package src.entity.core.living.yukkuri;

import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import src.enums.Attitude;
import src.enums.Intelligence;
import src.enums.YukkuriType;

class DnaTest {

    @Test
    void testDefaultConstructor() {
        Dna dna = new Dna();
        assertEquals(YukkuriType.TARINAI, dna.getType());
        assertEquals(Attitude.AVERAGE, dna.getAttitude());
        assertEquals(Intelligence.AVERAGE, dna.getIntelligence());
        assertFalse(dna.isRaperChild());
    }

    @Test
    void testParameterizedConstructor() {
        YukkuriType type = YukkuriType.MARISA;
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

        dna.setType(YukkuriType.REIMU);
        assertEquals(YukkuriType.REIMU, dna.getType());

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
            Dna dna = new Dna(YukkuriType.DOSMARISA, Attitude.SHITHEAD, Intelligence.WISE, true);

            assertEquals(YukkuriType.DOSMARISA, dna.getType());
            assertEquals(Attitude.SHITHEAD, dna.getAttitude());
            assertEquals(Intelligence.WISE, dna.getIntelligence());
            assertTrue(dna.isRaperChild());
            assertEquals(0, dna.getFather());
            assertEquals(0, dna.getMother());
        }
    }
}
