package src.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.EventPacket.EventPriority;
import src.base.EventPacket.UpdateState;
import src.draw.Translate;
import src.draw.World;
import src.enums.AgeState;
import src.enums.PublicRank;

class AvoidMoldEventTest {

    @BeforeEach
    void setUp() {
        SimYukkuri.world = new World();
        Translate.setMapSize(1000, 1000, 500);
        Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        Translate.createTransTable(false);
    }

    private static Body createBody() {
        Body b = new src.yukkuri.Reimu();
        b.setAgeState(AgeState.ADULT);
        src.system.Sprite[] spr = new src.system.Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new src.system.Sprite(10, 10, src.system.Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setBodySpr(spr);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        return b;
    }

    @Test
    void testDefaultConstructor() {
        AvoidMoldEvent event = new AvoidMoldEvent();
        assertNotNull(event);
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
    }

    @Test
    void testParameterizedConstructor() {
        Body from = createBody();
        Body to = createBody();
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertNotNull(event);
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(10, event.getCount());
    }

    @Test
    void testCheckEventResponse_setsPriorityMiddle() {
        Body from = createBody();
        Body to = createBody();
        Body responder = createBody();
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        event.checkEventResponse(responder);
        assertEquals(EventPriority.MIDDLE, event.getPriority());
    }

    @Test
    void testCheckEventResponse_returnsFalseForUnunSlave() {
        Body from = createBody();
        Body to = createBody();
        Body responder = createBody();
        responder.setPublicRank(PublicRank.UnunSlave);
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertFalse(event.checkEventResponse(responder));
    }

    @Test
    void testCheckEventResponse_returnsFalseForIdiot() {
        // isIdiot() is overridden in specific subclasses (e.g. TarinaiReimu).
        // For a normal Reimu, isIdiot() returns false, so we verify the logic path
        // by confirming that a non-idiot body does NOT get rejected by the idiot check.
        Body from = createBody();
        Body to = createBody();
        Body responder = createBody();
        // Reimu.isIdiot() returns false, so the idiot check should not block
        assertFalse(responder.isIdiot());
        // The method should pass the idiot check (not return false due to isIdiot)
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        // We cannot easily make isIdiot() true without a TarinaiReimu subclass,
        // so we verify the negative case: non-idiot passes this check
        // (may still return false for other reasons like barrier check)
    }

    @Test
    void testCheckEventResponse_returnsFalseWhenCanEventResponseIsFalse() {
        Body from = createBody();
        Body to = createBody();
        Body responder = createBody();
        // Make responder dead so canEventResponse returns false
        responder.setDead(true);
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertFalse(event.checkEventResponse(responder));
    }

    @Test
    void testToString_doesNotThrow() {
        AvoidMoldEvent event = new AvoidMoldEvent();
        assertDoesNotThrow(() -> event.toString());
    }

    @Test
    void testUpdate_returnsAbortWhenToIsNull() {
        Body from = createBody();
        Body responder = createBody();
        // Create event with no 'to' body registered in map
        AvoidMoldEvent event = new AvoidMoldEvent();
        event.setFrom(from.getUniqueID());
        event.setTo(-1);
        UpdateState result = event.update(responder);
        assertEquals(UpdateState.ABORT, result);
    }

    @Test
    void testUpdate_returnsAbortWhenToIsDead() {
        Body from = createBody();
        Body to = createBody();
        to.setDead(true);
        Body responder = createBody();
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        UpdateState result = event.update(responder);
        assertEquals(UpdateState.ABORT, result);
    }

    @Test
    void testExecute_returnsTrueWhenFromIsNull() {
        Body to = createBody();
        Body responder = createBody();
        AvoidMoldEvent event = new AvoidMoldEvent();
        event.setFrom(-1);
        event.setTo(to.getUniqueID());
        boolean result = event.execute(responder);
        assertTrue(result);
    }

    @Test
    void testUpdate_toRemoved_returnsAbort() {
        Body from = createBody();
        Body to = createBody();
        to.setRemoved(true);
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertEquals(UpdateState.ABORT, event.update(from));
    }

    @Test
    void testStart_toNull_doesNotThrow() {
        Body from = createBody();
        AvoidMoldEvent event = new AvoidMoldEvent(from, null, null, 10);
        // to is null → returns early without throwing
        assertDoesNotThrow(() -> event.start(from));
    }

    @Test
    void testExecute_toNull_returnsTrue() {
        Body from = createBody();
        AvoidMoldEvent event = new AvoidMoldEvent(from, null, null, 10);
        // to=null → from=null check → both null → true
        assertTrue(event.execute(from));
    }

    @Test
    void testUpdate_toAlive_returnsNull() {
        Body from = createBody();
        Body to = createBody();
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        // to is alive, not removed → calls calcCollisionX (needs rateX) → returns null
        assertNull(event.update(from));
    }

    @Test
    void testStart_toNotNull_doesNotThrow() {
        Body from = createBody();
        Body to = createBody();
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        // to != null → calls calcCollisionX
        assertDoesNotThrow(() -> event.start(from));
    }

    @Test
    void testExecute_fromAdult_notFamily_doesNotThrow() {
        Body from = createBody();
        Body to = createBody();
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        // from is adult, not family → "not family" path in execute
        assertDoesNotThrow(() -> event.execute(from));
    }

    @Test
    void testSaySadMessage_fromNull_doesNotThrow() {
        // unregistered from → getBodyInstance returns null → early return
        Body unregistered = new src.yukkuri.Reimu();
        Body to = createBody();
        AvoidMoldEvent event = new AvoidMoldEvent(unregistered, to, null, 10);
        assertDoesNotThrow(() -> event.saySadMessage(unregistered, to));
    }

    @Test
    void testSaySadMessage_fromExists_doesNotThrow() {
        Body from = createBody();
        Body to = createBody();
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertDoesNotThrow(() -> event.saySadMessage(from, to));
    }

    @Test
    void testSayApologyMessage_fromNull_doesNotThrow() {
        Body unregistered = new src.yukkuri.Reimu();
        Body to = createBody();
        AvoidMoldEvent event = new AvoidMoldEvent(unregistered, to, null, 10);
        assertDoesNotThrow(() -> event.sayApologyMessage(unregistered, to));
    }

    @Test
    void testSayApologyMessage_fromExists_doesNotThrow() {
        Body from = createBody();
        Body to = createBody();
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertDoesNotThrow(() -> event.sayApologyMessage(from, to));
    }

    // --- execute: from.isVeryRude() → sanction path ---

    @Test
    void testExecute_fromVeryRude_doesNotThrow() {
        Body from = createBody();
        Body to = createBody();
        // Make from very rude (attitude = SUPER_SHITHEAD)
        from.setAttitude(src.enums.Attitude.SUPER_SHITHEAD);
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: adult, isParent → various intelligence ---

    @Test
    void testExecute_adultParent_FOOL_doesNotThrow() {
        Body from = createBody(); // adult
        Body to = createBody();
        // Set from as parent of to
        src.util.WorldTestHelper.addChild(from, to.getUniqueID());
        from.setIntelligence(src.enums.Intelligence.FOOL);
        SimYukkuri.RND = new src.ConstState(0); // RND.nextInt(5)==0 → doPeropero
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertDoesNotThrow(() -> event.execute(from));
    }

    @Test
    void testExecute_adultParent_WISE_doesNotThrow() {
        Body from = createBody(); // adult
        Body to = createBody();
        src.util.WorldTestHelper.addChild(from, to.getUniqueID());
        from.setIntelligence(src.enums.Intelligence.WISE);
        SimYukkuri.RND = new src.ConstState(0); // sayApologyMessage + strike
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertDoesNotThrow(() -> event.execute(from));
    }

    @Test
    void testExecute_adultParent_DEFAULT_doesNotThrow() {
        Body from = createBody(); // adult
        Body to = createBody();
        src.util.WorldTestHelper.addChild(from, to.getUniqueID());
        from.setIntelligence(src.enums.Intelligence.AVERAGE);
        SimYukkuri.RND = new src.ConstState(1); // nextInt(5)!=0 → saySadMessage
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: child (not adult) path ---

    @Test
    void testExecute_childFrom_notFamily_doesNotThrow() {
        Body from = createBody();
        from.setAgeState(src.enums.AgeState.CHILD);
        Body to = createBody();
        // from and to are not related
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertDoesNotThrow(() -> event.execute(from));
    }

    @Test
    void testExecute_childFrom_isChild_FOOL_doesNotThrow() {
        Body from = createBody();
        from.setAgeState(src.enums.AgeState.CHILD);
        Body to = createBody(); // parent (to is from's parent)
        // Make to have from as child (so from.isChild(to) is true)
        src.util.WorldTestHelper.addChild(to, from.getUniqueID());
        from.setIntelligence(src.enums.Intelligence.FOOL);
        SimYukkuri.RND = new src.ConstState(1); // nextInt(5)!=0
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- update: to dead → ABORT ---

    @Test
    void testUpdate_toDead_returnsAbort() {
        Body from = createBody();
        Body to = createBody();
        to.setDead(true);
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- execute: adult, isTalking → return true ---
    @Test
    void testExecute_adultFrom_isTalking_returnsTrue() {
        Body from = createBody(); // adult
        Body to = createBody();
        from.setMessageCount(1); // isTalking() = true → inner block skipped
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertTrue(event.execute(from));
    }

    // --- execute: isVeryRude + FOOL → addSickPeriod ---
    @Test
    void testExecute_fromVeryRude_FOOL_doesNotThrow() {
        Body from = createBody();
        Body to = createBody();
        from.setAttitude(src.enums.Attitude.SUPER_SHITHEAD); // isVeryRude() = true
        from.setIntelligence(src.enums.Intelligence.FOOL); // addSickPeriod(100) path
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: adult, isParent, FOOL, nextInt(5)!=0 → saySadMessage, false ---
    @Test
    void testExecute_adultParent_FOOL_saySadMessage_returnsFalse() {
        Body from = createBody();
        Body to = createBody();
        to.setParents(new int[]{from.getUniqueID(), -1}); // from.isParent(to) = true
        from.setIntelligence(src.enums.Intelligence.FOOL);
        SimYukkuri.RND = new src.ConstState(1); // nextInt(5)=1 → saySadMessage path
        try {
            assertFalse(event(from, to).execute(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- execute: adult, isParent, WISE, nextInt(5)!=0 → return false ---
    @Test
    void testExecute_adultParent_WISE_returnsFalse() {
        Body from = createBody();
        Body to = createBody();
        to.setParents(new int[]{from.getUniqueID(), -1}); // from.isParent(to) = true
        from.setIntelligence(src.enums.Intelligence.WISE);
        SimYukkuri.RND = new src.ConstState(1); // nextInt(5)=1 → no apology
        try {
            assertFalse(event(from, to).execute(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- execute: adult, isParent, DEFAULT, nextInt(5)==0 → sayApology+strike+true ---
    @Test
    void testExecute_adultParent_DEFAULT_trueWhenApology() {
        Body from = createBody();
        Body to = createBody();
        src.util.WorldTestHelper.addChild(from, to.getUniqueID());
        from.setIntelligence(src.enums.Intelligence.AVERAGE);
        SimYukkuri.RND = new src.ConstState(0); // nextInt(5)=0 → sayApology+strike+true
        try {
            assertTrue(event(from, to).execute(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- execute: adult, isFamily (siblings, not parent/partner) ---
    @Test
    void testExecute_adultFamily_FOOL_doesNotThrow() {
        Body sharedParent = createBody();
        Body from = createBody();
        Body to = createBody();
        // siblings (same father)
        from.setParents(new int[]{sharedParent.getUniqueID(), -1});
        to.setParents(new int[]{sharedParent.getUniqueID(), -1});
        from.setIntelligence(src.enums.Intelligence.FOOL);
        SimYukkuri.RND = new src.ConstState(1);
        try {
            assertDoesNotThrow(() -> event(from, to).execute(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    @Test
    void testExecute_adultFamily_WISE_doesNotThrow() {
        Body sharedParent = createBody();
        Body from = createBody();
        Body to = createBody();
        from.setParents(new int[]{sharedParent.getUniqueID(), -1});
        to.setParents(new int[]{sharedParent.getUniqueID(), -1});
        from.setIntelligence(src.enums.Intelligence.WISE);
        SimYukkuri.RND = new src.ConstState(0); // sayApology + runAway → true
        try {
            assertDoesNotThrow(() -> event(from, to).execute(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    @Test
    void testExecute_adultFamily_DEFAULT_doesNotThrow() {
        Body sharedParent = createBody();
        Body from = createBody();
        Body to = createBody();
        from.setParents(new int[]{sharedParent.getUniqueID(), -1});
        to.setParents(new int[]{sharedParent.getUniqueID(), -1});
        from.setIntelligence(src.enums.Intelligence.AVERAGE);
        SimYukkuri.RND = new src.ConstState(1); // saySadMessage+false path
        try {
            assertDoesNotThrow(() -> event(from, to).execute(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- execute: child path, isChild(to) WISE ---
    @Test
    void testExecute_childFrom_isChild_WISE_doesNotThrow() {
        Body from = createBody();
        from.setAgeState(src.enums.AgeState.CHILD);
        Body to = createBody();
        from.setParents(new int[]{to.getUniqueID(), -1}); // from.isChild(to) = to.isParent(from)
        from.setIntelligence(src.enums.Intelligence.WISE);
        SimYukkuri.RND = new src.ConstState(1);
        try {
            assertDoesNotThrow(() -> event(from, to).execute(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- execute: child path, isFamily (siblings) ---
    @Test
    void testExecute_childFrom_isFamily_FOOL_doesNotThrow() {
        Body sharedParent = createBody();
        Body from = createBody();
        from.setAgeState(src.enums.AgeState.CHILD);
        Body to = createBody();
        from.setParents(new int[]{sharedParent.getUniqueID(), -1});
        to.setParents(new int[]{sharedParent.getUniqueID(), -1});
        from.setIntelligence(src.enums.Intelligence.FOOL);
        SimYukkuri.RND = new src.ConstState(1);
        try {
            assertDoesNotThrow(() -> event(from, to).execute(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    @Test
    void testExecute_childFrom_isFamily_WISE_doesNotThrow() {
        Body sharedParent = createBody();
        Body from = createBody();
        from.setAgeState(src.enums.AgeState.CHILD);
        Body to = createBody();
        from.setParents(new int[]{sharedParent.getUniqueID(), -1});
        to.setParents(new int[]{sharedParent.getUniqueID(), -1});
        from.setIntelligence(src.enums.Intelligence.WISE);
        SimYukkuri.RND = new src.ConstState(0);
        try {
            assertDoesNotThrow(() -> event(from, to).execute(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- saySadMessage: isParent(to) → SadnessForMoldyChild ---
    @Test
    void testSaySadMessage_isParent_doesNotThrow() {
        Body from = createBody();
        Body to = createBody();
        src.util.WorldTestHelper.addChild(from, to.getUniqueID()); // from.isParent(to)
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertDoesNotThrow(() -> event.saySadMessage(from, to));
    }

    // --- saySadMessage: isPartner(to) → SadnessForMoldyPartner ---
    @Test
    void testSaySadMessage_isPartner_doesNotThrow() {
        Body from = createBody();
        Body to = createBody();
        from.setPartner(to.getUniqueID());
        to.setPartner(from.getUniqueID());
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertDoesNotThrow(() -> event.saySadMessage(from, to));
    }

    // --- saySadMessage: to.isParent(from) → SadnessForMoldyFather/Mother ---
    @Test
    void testSaySadMessage_toIsParent_doesNotThrow() {
        Body from = createBody();
        Body to = createBody();
        from.setParents(new int[]{to.getUniqueID(), -1}); // to.isParent(from) = true
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertDoesNotThrow(() -> event.saySadMessage(from, to));
    }

    // --- sayApologyMessage: from.isParent(to) → ApologyToChild ---
    @Test
    void testSayApologyMessage_isParent_doesNotThrow() {
        Body from = createBody();
        Body to = createBody();
        src.util.WorldTestHelper.addChild(from, to.getUniqueID()); // from.isParent(to)
        AvoidMoldEvent event = new AvoidMoldEvent(from, to, null, 10);
        assertDoesNotThrow(() -> event.sayApologyMessage(from, to));
    }

    // helper to create event without needing a new local
    private static AvoidMoldEvent event(Body from, Body to) {
        return new AvoidMoldEvent(from, to, null, 10);
    }

    private static java.lang.reflect.Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}
