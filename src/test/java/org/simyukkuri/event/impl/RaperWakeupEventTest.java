package org.simyukkuri.event.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.event.EventPacket.EventPriority;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

public class RaperWakeupEventTest {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetWorld();
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
    }

    // --- Default constructor ---

    @Test
    public void testDefaultConstructor() {
        RaperWakeupEvent event = new RaperWakeupEvent();
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
        assertEquals(EventPriority.LOW, event.getPriority());
    }

    // --- Parameterized constructor ---

    @Test
    public void testParameterizedConstructor() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        RaperWakeupEvent event = new RaperWakeupEvent(from, to, null, 1);
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(1, event.getCount());
    }

    // --- simpleEventAction ---

    @Test
    public void testSimpleEventAction_returnsFalseWhenBEqualsFrom() {
        Yukkuri b = createBody();
        RaperWakeupEvent event = new RaperWakeupEvent(b, null, null, 1);
        // b == from => return false (self is skipped)
        assertFalse(event.simpleEventAction(b));
    }

    @Test
    public void testSimpleEventAction_returnsTrueWhenCanEventResponseIsFalse() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        // Make b unable to respond to events (e.g., dead)
        b.setDead(true);
        RaperWakeupEvent event = new RaperWakeupEvent(from, null, null, 1);
        // b != from, canEventResponse is false => returns true
        assertTrue(event.simpleEventAction(b));
    }

    // --- checkEventResponse ---

    @Test
    public void testCheckEventResponse_alwaysReturnsFalse() {
        Yukkuri b = createBody();
        RaperWakeupEvent event = new RaperWakeupEvent();
        assertFalse(event.checkEventResponse(b));

        Yukkuri from = createBody();
        RaperWakeupEvent event2 = new RaperWakeupEvent(from, null, null, 1);
        assertFalse(event2.checkEventResponse(b));
        assertFalse(event2.checkEventResponse(from));
    }

    // --- start ---

    @Test
    public void testStart_doesNotThrow() {
        Yukkuri b = createBody();
        RaperWakeupEvent event = new RaperWakeupEvent();
        assertDoesNotThrow(() -> event.start(b));
    }

    // --- execute ---

    @Test
    public void testExecute_returnsTrue() {
        Yukkuri b = createBody();
        RaperWakeupEvent event = new RaperWakeupEvent();
        assertTrue(event.execute(b));
    }

    // --- toString ---

    @Test
    public void testToString_doesNotThrow() {
        RaperWakeupEvent event = new RaperWakeupEvent();
        assertDoesNotThrow(() -> event.toString());
    }

    // --- simpleEventAction: b.isNYD() → canEventResponse=false → true ---
    @Test
    public void testSimpleEventAction_bIsNYD_returnsTrue() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        b.setCoreAnkoState(org.simyukkuri.enums.CoreAnkoState.NON_YUKKURI_DISEASE);
        RaperWakeupEvent event = new RaperWakeupEvent(from, null, null, 1);
        assertTrue(event.simpleEventAction(b));
    }

    // --- simpleEventAction: b.isNeedled() → canEventResponse=false → true ---
    @Test
    public void testSimpleEventAction_bIsNeedled_returnsTrue() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        b.setNeedled(true);
        RaperWakeupEvent event = new RaperWakeupEvent(from, null, null, 1);
        assertTrue(event.simpleEventAction(b));
    }

    // --- simpleEventAction: b.isRaper() → forceToRaperExcite, return true ---
    @Test
    public void testSimpleEventAction_bIsRaper_returnsTrue() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        b.setRapist(true); // isRaper() = true
        RaperWakeupEvent event = new RaperWakeupEvent(from, null, null, 1);
        assertTrue(event.simpleEventAction(b));
    }

    // --- simpleEventAction: normal body (not raper) → addYukkuriEvent, return true
    // ---
    @Test
    public void testSimpleEventAction_normalBody_returnsTrue() {
        Yukkuri from = createBody();
        Yukkuri b = createBody(); // normal body, no raper
        RaperWakeupEvent event = new RaperWakeupEvent(from, null, null, 1);
        assertTrue(event.simpleEventAction(b));
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_NormalBodyQueuesSingleRaperReactionEvent() {
            Yukkuri raper = createBody();
            Yukkuri bystander = createBody();
            RaperWakeupEvent event = new RaperWakeupEvent(raper, null, null, 1);

            assertTrue(event.simpleEventAction(bystander));

            assertEquals(1, bystander.getEvents().size(),
                    "normal bystander should receive exactly one follow-up body event");
            assertTrue(bystander.getEvents().get(0) instanceof RaperReactionEvent,
                    "normal bystander should queue a RaperReactionEvent");
            RaperReactionEvent queued = (RaperReactionEvent) bystander.getEvents().get(0);
            assertEquals(raper.getUniqueID(), queued.getFrom(),
                    "queued reaction should point back to the waking raper");
            assertNull(bystander.getCurrentEvent(), "wake-up notice should only queue the reaction at this stage");
        }

        @Test
        void testScenario_RaperBodyGetsForcedExcitedAndDropsPartner() {
            Yukkuri sourceRaper = createBody();
            Yukkuri chainedRaper = createBody();
            chainedRaper.setRapist(true);
            chainedRaper.setPartner(sourceRaper.getUniqueID());
            chainedRaper.setExciting(false);
            chainedRaper.setForceExciting(false);
            RaperWakeupEvent event = new RaperWakeupEvent(sourceRaper, null, null, 1);

            assertTrue(event.simpleEventAction(chainedRaper));

            assertTrue(chainedRaper.isExciting(), "raper target should become exciting from wake-up chaining");
            assertTrue(chainedRaper.isForceExciting(), "raper target should be marked as force-excited");
            assertEquals(-1, chainedRaper.getPartner(), "forced raper excitation should clear the previous partner");
            assertTrue(chainedRaper.isStaying(), "forced raper excitation should leave the chained raper staying");
        }
    }

    // --- Helper ---

    private static Yukkuri createBody() {
        Yukkuri b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setSpriteSet(spr);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(b.getUniqueID(), b);
        return b;
    }
}
