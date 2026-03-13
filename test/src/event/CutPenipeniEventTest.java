package src.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket.UpdateState;
import src.base.EventTestBase;

public class CutPenipeniEventTest extends EventTestBase {

    @Test
    void testCheckEventResponse_ReturnsTrueForInitiator() {
        Body attacker = createBody(1, 100, 100);
        Body victim = createBody(2, 120, 120);

        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);

        // checkEventResponse returns true if b == from
        assertTrue(event.checkEventResponse(attacker));
    }

    @Test
    void testCheckEventResponse_ReturnsFalseForStranger() {
        Body attacker = createBody(1, 100, 100);
        Body victim = createBody(2, 120, 120);
        Body stranger = createBody(3, 200, 200);

        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);

        assertFalse(event.checkEventResponse(stranger));
    }

    @Test
    void testDefaultConstructor_doesNotThrow() {
        assertDoesNotThrow(() -> new CutPenipeniEvent());
    }

    @Test
    void testStart_doesNotThrow() {
        Body attacker = createBody(1, 100, 100);
        Body victim = createBody(2, 120, 120);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        assertDoesNotThrow(() -> event.start(victim));
    }

    @Test
    void testExecute_returnsTrue() {
        Body attacker = createBody(1, 100, 100);
        Body victim = createBody(2, 120, 120);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        assertTrue(event.execute(victim));
    }

    @Test
    void testEnd_doesNotThrow() {
        Body attacker = createBody(1, 100, 100);
        Body victim = createBody(2, 120, 120);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        assertDoesNotThrow(() -> event.end(victim));
        assertTrue(victim.isbPenipeniCutted());
    }

    @Test
    void testToString_doesNotThrow() {
        Body attacker = createBody(1, 100, 100);
        Body victim = createBody(2, 120, 120);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        assertDoesNotThrow(() -> event.toString());
    }

    @Test
    void testUpdate_UnBirth_returnsForceExec() {
        Body attacker = createBody(1, 100, 100);
        Body victim = createBody(2, 120, 120);
        victim.setUnBirth(true);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        UpdateState result = event.update(victim);
        assertEquals(UpdateState.FORCE_EXEC, result);
    }

    @Test
    void testUpdate_tick0_Normal_returnsNull() {
        Body attacker = createBody(1, 100, 100);
        Body victim = createBody(2, 120, 120);
        victim.setUnBirth(false);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        UpdateState result = event.update(victim);
        assertNull(result);
        assertTrue(victim.isbPenipeniCutted());
    }

    // --- tick=20: isNotNYD=true, nextInt(2)==0 → Scream2 ---
    @Test
    void testUpdate_tick20_rnd0_doesNotThrow() {
        Body attacker = createBody(1, 100, 100);
        Body victim = createBody(2, 120, 120);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        event.tick = 20;
        SimYukkuri.RND = new src.ConstState(0);
        try {
            assertNull(event.update(victim));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- tick=20: isNotNYD=true, nextInt(2)!=0 → Surprise ---
    @Test
    void testUpdate_tick20_rnd1_doesNotThrow() {
        Body attacker = createBody(1, 100, 100);
        Body victim = createBody(2, 120, 120);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        event.tick = 20;
        SimYukkuri.RND = new src.ConstState(1);
        try {
            assertNull(event.update(victim));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- tick=40: VERY_NICE attitude ---
    @Test
    void testUpdate_tick40_veryNice_doesNotThrow() {
        Body attacker = createBody(1, 100, 100);
        Body victim = createBody(2, 120, 120);
        victim.setAttitude(src.enums.Attitude.VERY_NICE);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        event.tick = 40;
        assertNull(event.update(victim));
    }

    // --- tick=40: NICE attitude ---
    @Test
    void testUpdate_tick40_nice_doesNotThrow() {
        Body attacker = createBody(1, 100, 100);
        Body victim = createBody(2, 120, 120);
        victim.setAttitude(src.enums.Attitude.NICE);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        event.tick = 40;
        assertNull(event.update(victim));
    }

    // --- tick=40: AVERAGE attitude ---
    @Test
    void testUpdate_tick40_average_doesNotThrow() {
        Body attacker = createBody(1, 100, 100);
        Body victim = createBody(2, 120, 120);
        victim.setAttitude(src.enums.Attitude.AVERAGE);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        event.tick = 40;
        assertNull(event.update(victim));
    }

    // --- tick=40: SHITHEAD attitude ---
    @Test
    void testUpdate_tick40_shithead_doesNotThrow() {
        Body attacker = createBody(1, 100, 100);
        Body victim = createBody(2, 120, 120);
        victim.setAttitude(src.enums.Attitude.SHITHEAD);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        event.tick = 40;
        assertNull(event.update(victim));
    }

    // --- tick=70: → FORCE_EXEC ---
    @Test
    void testUpdate_tick70_returnsForceExec() {
        Body attacker = createBody(1, 100, 100);
        Body victim = createBody(2, 120, 120);
        CutPenipeniEvent event = new CutPenipeniEvent(attacker, victim, null, 10);
        event.tick = 70;
        SimYukkuri.RND = new src.ConstState(0);
        try {
            assertEquals(UpdateState.FORCE_EXEC, event.update(victim));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }
}
