package src.event;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket.EventPriority;
import src.draw.World;
import src.enums.AgeState;
import src.system.Sprite;
import src.util.WorldTestHelper;
import src.yukkuri.Reimu;

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
        Body from = createBody();
        Body to = createBody();
        RaperWakeupEvent event = new RaperWakeupEvent(from, to, null, 1);
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(1, event.getCount());
    }

    // --- simpleEventAction ---

    @Test
    public void testSimpleEventAction_returnsFalseWhenBEqualsFrom() {
        Body b = createBody();
        RaperWakeupEvent event = new RaperWakeupEvent(b, null, null, 1);
        // b == from => return false (self is skipped)
        assertFalse(event.simpleEventAction(b));
    }

    @Test
    public void testSimpleEventAction_returnsTrueWhenCanEventResponseIsFalse() {
        Body from = createBody();
        Body b = createBody();
        // Make b unable to respond to events (e.g., dead)
        b.setDead(true);
        RaperWakeupEvent event = new RaperWakeupEvent(from, null, null, 1);
        // b != from, canEventResponse is false => returns true
        assertTrue(event.simpleEventAction(b));
    }

    // --- checkEventResponse ---

    @Test
    public void testCheckEventResponse_alwaysReturnsFalse() {
        Body b = createBody();
        RaperWakeupEvent event = new RaperWakeupEvent();
        assertFalse(event.checkEventResponse(b));

        Body from = createBody();
        RaperWakeupEvent event2 = new RaperWakeupEvent(from, null, null, 1);
        assertFalse(event2.checkEventResponse(b));
        assertFalse(event2.checkEventResponse(from));
    }

    // --- start ---

    @Test
    public void testStart_doesNotThrow() {
        Body b = createBody();
        RaperWakeupEvent event = new RaperWakeupEvent();
        assertDoesNotThrow(() -> event.start(b));
    }

    // --- execute ---

    @Test
    public void testExecute_returnsTrue() {
        Body b = createBody();
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
        Body from = createBody();
        Body b = createBody();
        b.seteCoreAnkoState(src.enums.CoreAnkoState.NonYukkuriDisease);
        RaperWakeupEvent event = new RaperWakeupEvent(from, null, null, 1);
        assertTrue(event.simpleEventAction(b));
    }

    // --- simpleEventAction: b.isNeedled() → canEventResponse=false → true ---
    @Test
    public void testSimpleEventAction_bIsNeedled_returnsTrue() {
        Body from = createBody();
        Body b = createBody();
        b.setbNeedled(true);
        RaperWakeupEvent event = new RaperWakeupEvent(from, null, null, 1);
        assertTrue(event.simpleEventAction(b));
    }

    // --- simpleEventAction: b.isRaper() → forceToRaperExcite, return true ---
    @Test
    public void testSimpleEventAction_bIsRaper_returnsTrue() {
        Body from = createBody();
        Body b = createBody();
        b.setRapist(true); // isRaper() = true
        RaperWakeupEvent event = new RaperWakeupEvent(from, null, null, 1);
        assertTrue(event.simpleEventAction(b));
    }

    // --- simpleEventAction: normal body (not raper) → addBodyEvent, return true ---
    @Test
    public void testSimpleEventAction_normalBody_returnsTrue() {
        Body from = createBody();
        Body b = createBody(); // normal body, no raper
        RaperWakeupEvent event = new RaperWakeupEvent(from, null, null, 1);
        assertTrue(event.simpleEventAction(b));
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_NormalBodyQueuesSingleRaperReactionEvent() {
            Body raper = createBody();
            Body bystander = createBody();
            RaperWakeupEvent event = new RaperWakeupEvent(raper, null, null, 1);

            assertTrue(event.simpleEventAction(bystander));

            assertEquals(1, bystander.getEventList().size(),
                    "normal bystander should receive exactly one follow-up body event");
            assertTrue(bystander.getEventList().get(0) instanceof RaperReactionEvent,
                    "normal bystander should queue a RaperReactionEvent");
            RaperReactionEvent queued = (RaperReactionEvent) bystander.getEventList().get(0);
            assertEquals(raper.getUniqueID(), queued.getFrom(), "queued reaction should point back to the waking raper");
            assertNull(bystander.getCurrentEvent(), "wake-up notice should only queue the reaction at this stage");
        }

        @Test
        void testScenario_RaperBodyGetsForcedExcitedAndDropsPartner() {
            Body sourceRaper = createBody();
            Body chainedRaper = createBody();
            chainedRaper.setRapist(true);
            chainedRaper.setPartner(sourceRaper.getUniqueID());
            chainedRaper.setExciting(false);
            chainedRaper.setbForceExciting(false);
            RaperWakeupEvent event = new RaperWakeupEvent(sourceRaper, null, null, 1);

            assertTrue(event.simpleEventAction(chainedRaper));

            assertTrue(chainedRaper.isExciting(), "raper target should become exciting from wake-up chaining");
            assertTrue(chainedRaper.isForceExciting(), "raper target should be marked as force-excited");
            assertEquals(-1, chainedRaper.getPartner(), "forced raper excitation should clear the previous partner");
            assertTrue(chainedRaper.isStaying(), "forced raper excitation should leave the chained raper staying");
        }
    }

    // --- Helper ---

    private static Body createBody() {
        Body b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setBodySpr(spr);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        return b;
    }
}
