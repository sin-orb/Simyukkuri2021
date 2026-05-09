package src.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import src.base.Yukkuri;
import src.base.EventTestBase;
import src.enums.Attitude;
import src.enums.Happiness;

public class EatBodyEventTest extends EventTestBase {

    @Test
    void testCheckEventResponse_ReturnsTrueForEater() {
        Yukkuri eater = createBody(1, 100, 100);
        Yukkuri eaten = createBody(2, 120, 120);

        EatBodyEvent event = new EatBodyEvent(eater, eaten, null, 10);

        // checkEventResponse returns true if b == from
        assertTrue(event.checkEventResponse(eater));
    }

    @Test
    void testCheckEventResponse_ReturnsFalseForNonEater() {
        Yukkuri eater = createBody(1, 100, 100);
        Yukkuri eaten = createBody(2, 120, 120);
        Yukkuri bystander = createBody(3, 150, 150);

        EatBodyEvent event = new EatBodyEvent(eater, eaten, null, 10);
        assertFalse(event.checkEventResponse(bystander));
    }

    @Test
    void testCheckEventResponse_ReturnsFalseForSuperShithead() {
        Yukkuri eater = createBody(1, 100, 100);
        Yukkuri eaten = createBody(2, 120, 120);
        eater.setAttitude(Attitude.SUPER_SHITHEAD);

        EatBodyEvent event = new EatBodyEvent(eater, eaten, null, 10);
        assertFalse(event.checkEventResponse(eater));
    }

    @Test
    void testDefaultConstructor() {
        EatBodyEvent event = new EatBodyEvent();
        assertNotNull(event);
    }

    @Test
    void testEndResetsLockmove() {
        Yukkuri eater = createBody(1, 100, 100);
        Yukkuri eaten = createBody(2, 120, 120);
        EatBodyEvent event = new EatBodyEvent(eater, eaten, null, 10);
        eater.setLockmove(true);
        event.end(eater);
        assertFalse(eater.isLockmove());
    }

    @Test
    void testStartDoesNotThrow() {
        Yukkuri eater = createBody(1, 100, 100);
        Yukkuri eaten = createBody(2, 120, 120);
        EatBodyEvent event = new EatBodyEvent(eater, eaten, null, 10);
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> event.start(eater));
    }

    @Test
    void testExecuteMultipleTicks() {
        Yukkuri eater = createBody(1, 100, 100);
        Yukkuri eaten = createBody(2, 120, 120);
        EatBodyEvent event = new EatBodyEvent(eater, eaten, null, 30);
        // tick=0,10,70,120未満を順次実行
        for (int i = 0; i < 15; i++) {
            boolean done = event.execute(eater);
            if (done) break;
        }
        // 例外なく動作すればOK
        assertNotNull(eater);
    }

    @Test
    void testToString_doesNotThrow() {
        Yukkuri eater = createBody(1, 100, 100);
        Yukkuri eaten = createBody(2, 120, 120);
        EatBodyEvent event = new EatBodyEvent(eater, eaten, null, 10);
        assertDoesNotThrow(() -> event.toString());
    }

    // --- tick=120 with various attitudes → stress addition ---

    @Test
    void testExecute_tick120_veryNice_returnsTrue() {
        Yukkuri eater = createBody(1, 100, 100);
        Yukkuri eaten = createBody(2, 120, 120);
        eater.setAttitude(Attitude.VERY_NICE);
        EatBodyEvent event = new EatBodyEvent(eater, eaten, null, 10);
        event.tick = 120;
        assertTrue(event.execute(eater));
    }

    @Test
    void testExecute_tick120_nice_returnsTrue() {
        Yukkuri eater = createBody(1, 100, 100);
        Yukkuri eaten = createBody(2, 120, 120);
        eater.setAttitude(Attitude.NICE);
        EatBodyEvent event = new EatBodyEvent(eater, eaten, null, 10);
        event.tick = 120;
        assertTrue(event.execute(eater));
    }

    @Test
    void testExecute_tick120_average_returnsTrue() {
        Yukkuri eater = createBody(1, 100, 100);
        Yukkuri eaten = createBody(2, 120, 120);
        eater.setAttitude(Attitude.AVERAGE);
        EatBodyEvent event = new EatBodyEvent(eater, eaten, null, 10);
        event.tick = 120;
        assertTrue(event.execute(eater));
    }

    @Test
    void testExecute_tick120_shithead_returnsTrue() {
        Yukkuri eater = createBody(1, 100, 100);
        Yukkuri eaten = createBody(2, 120, 120);
        eater.setAttitude(Attitude.SHITHEAD);
        EatBodyEvent event = new EatBodyEvent(eater, eaten, null, 10);
        event.tick = 120;
        assertTrue(event.execute(eater));
    }

    // --- update (default EventPacket behavior returns null) ---
    @Test
    void testUpdate_returnsNull() {
        Yukkuri b = createBody(1, 100, 100);
        EatBodyEvent event = new EatBodyEvent();
        assertNull(event.update(b));
    }

    // --- start: to==null → early return ---
    @Test
    void testStart_toNull_doesNotThrow() {
        Yukkuri eater = createBody(1, 100, 100);
        EatBodyEvent event = new EatBodyEvent(); // to=-1 → null
        assertDoesNotThrow(() -> event.start(eater));
    }

    // --- checkEventResponse: from==b but !canEventResponse() → false ---
    @Test
    void testCheckEventResponse_eaterDead_returnsFalse() {
        Yukkuri eater = createBody(1, 100, 100);
        Yukkuri eaten = createBody(2, 120, 120);
        eater.setDead(true); // !canEventResponse() → false
        EatBodyEvent event = new EatBodyEvent(eater, eaten, null, 10);
        assertFalse(event.checkEventResponse(eater));
    }

    // --- execute: tick=10 with to!=null ---
    @Test
    void testExecute_tick10_doesNotThrow() {
        Yukkuri eater = createBody(1, 100, 100);
        Yukkuri eaten = createBody(2, 120, 120);
        EatBodyEvent event = new EatBodyEvent(eater, eaten, null, 10);
        event.tick = 10;
        assertDoesNotThrow(() -> event.execute(eater));
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_AverageEaterFinishesVerySadWithStressPenalty() {
            Yukkuri eater = createBody(1, 100, 100);
            Yukkuri eaten = createBody(2, 120, 120);
            eater.setAttitude(Attitude.AVERAGE);
            int beforeStress = eater.getStress();

            EatBodyEvent event = new EatBodyEvent(eater, eaten, null, 10);
            event.tick = 120;

            assertTrue(event.execute(eater));
            assertEquals(beforeStress + 2000, eater.getStress());
            assertEquals(Happiness.VERY_SAD, eater.getHappiness());
        }
    }
}
