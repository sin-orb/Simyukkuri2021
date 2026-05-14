package org.simyukkuri.event.impl;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.event.EventPacket.UpdateState;
import org.simyukkuri.event.EventTestBase;

public class CutPenipeniEventTest extends EventTestBase {

    @Test
    void testCheckEventResponse_ReturnsTrueForInitiator() {
        Yukkuri attacker = createBody(1, 100, 100);
        Yukkuri victim = createBody(2, 120, 120);

        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);

        // checkEventResponse returns true if b == from
        assertTrue(event.checkEventResponse(attacker));
    }

    @Test
    void testCheckEventResponse_ReturnsFalseForStranger() {
        Yukkuri attacker = createBody(1, 100, 100);
        Yukkuri victim = createBody(2, 120, 120);
        Yukkuri stranger = createBody(3, 200, 200);

        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);

        assertFalse(event.checkEventResponse(stranger));
    }

    @Test
    void testDefaultConstructor_doesNotThrow() {
        assertDoesNotThrow(() -> new CutPenipeniEvent());
    }

    @Test
    void testStart_doesNotThrow() {
        Yukkuri attacker = createBody(1, 100, 100);
        Yukkuri victim = createBody(2, 120, 120);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        assertDoesNotThrow(() -> event.start(victim));
    }

    @Test
    void testExecute_returnsTrue() {
        Yukkuri attacker = createBody(1, 100, 100);
        Yukkuri victim = createBody(2, 120, 120);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        assertTrue(event.execute(victim));
    }

    @Test
    void testEnd_doesNotThrow() {
        Yukkuri attacker = createBody(1, 100, 100);
        Yukkuri victim = createBody(2, 120, 120);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        assertDoesNotThrow(() -> event.end(victim));
        assertTrue(victim.isPenipeniCutted());
    }

    @Test
    void testToString_doesNotThrow() {
        Yukkuri attacker = createBody(1, 100, 100);
        Yukkuri victim = createBody(2, 120, 120);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        assertDoesNotThrow(() -> event.toString());
    }

    @Test
    void testUpdate_UnBirth_returnsForceExec() {
        Yukkuri attacker = createBody(1, 100, 100);
        Yukkuri victim = createBody(2, 120, 120);
        victim.setUnBirth(true);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        UpdateState result = event.update(victim);
        assertEquals(UpdateState.FORCE_EXEC, result);
    }

    @Test
    void testUpdate_tick0_Normal_returnsNull() {
        Yukkuri attacker = createBody(1, 100, 100);
        Yukkuri victim = createBody(2, 120, 120);
        victim.setUnBirth(false);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        UpdateState result = event.update(victim);
        assertNull(result);
        assertTrue(victim.isPenipeniCutted());
    }

    // --- tick=20: isNotNYD=true, nextInt(2)==0 → Scream2 ---
    @Test
    void testUpdate_tick20_rnd0_doesNotThrow() {
        Yukkuri attacker = createBody(1, 100, 100);
        Yukkuri victim = createBody(2, 120, 120);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        event.tick = 20;
        SimYukkuri.RND = new org.simyukkuri.ConstState(0);
        try {
            assertNull(event.update(victim));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- tick=20: isNotNYD=true, nextInt(2)!=0 → Surprise ---
    @Test
    void testUpdate_tick20_rnd1_doesNotThrow() {
        Yukkuri attacker = createBody(1, 100, 100);
        Yukkuri victim = createBody(2, 120, 120);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        event.tick = 20;
        SimYukkuri.RND = new org.simyukkuri.ConstState(1);
        try {
            assertNull(event.update(victim));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- tick=40: VERY_NICE attitude ---
    @Test
    void testUpdate_tick40_veryNice_doesNotThrow() {
        Yukkuri attacker = createBody(1, 100, 100);
        Yukkuri victim = createBody(2, 120, 120);
        victim.setAttitude(org.simyukkuri.enums.Attitude.VERY_NICE);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        event.tick = 40;
        assertNull(event.update(victim));
    }

    // --- tick=40: NICE attitude ---
    @Test
    void testUpdate_tick40_nice_doesNotThrow() {
        Yukkuri attacker = createBody(1, 100, 100);
        Yukkuri victim = createBody(2, 120, 120);
        victim.setAttitude(org.simyukkuri.enums.Attitude.NICE);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        event.tick = 40;
        assertNull(event.update(victim));
    }

    // --- tick=40: AVERAGE attitude ---
    @Test
    void testUpdate_tick40_average_doesNotThrow() {
        Yukkuri attacker = createBody(1, 100, 100);
        Yukkuri victim = createBody(2, 120, 120);
        victim.setAttitude(org.simyukkuri.enums.Attitude.AVERAGE);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        event.tick = 40;
        assertNull(event.update(victim));
    }

    // --- tick=40: SHITHEAD attitude ---
    @Test
    void testUpdate_tick40_shithead_doesNotThrow() {
        Yukkuri attacker = createBody(1, 100, 100);
        Yukkuri victim = createBody(2, 120, 120);
        victim.setAttitude(org.simyukkuri.enums.Attitude.SHITHEAD);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        event.tick = 40;
        assertNull(event.update(victim));
    }

    // --- tick=70: → FORCE_EXEC ---
    @Test
    void testUpdate_tick70_returnsForceExec() {
        Yukkuri attacker = createBody(1, 100, 100);
        Yukkuri victim = createBody(2, 120, 120);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        event.tick = 70;
        SimYukkuri.RND = new org.simyukkuri.ConstState(0);
        try {
            assertEquals(UpdateState.FORCE_EXEC, event.update(victim));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_UnBirthVictimBecomesVerySadNonRaperAfterCut() {
            Yukkuri attacker = createBody(1, 100, 100);
            Yukkuri victim = createBody(2, 120, 120);
            victim.setUnBirth(true);
            victim.setRapist(true);
            int beforeDamage = victim.getDamage();

            CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);

            assertEquals(UpdateState.FORCE_EXEC, event.update(victim));
            assertTrue(victim.isPenipeniCutted());
            assertFalse(victim.isRaper());
            assertEquals(beforeDamage + 50, victim.getDamage());
            assertEquals(Happiness.VERY_SAD, victim.getHappiness());
            assertEquals(ImageCode.CUTPENIPENI.ordinal(), victim.getForceFace());
        }
    }
}
