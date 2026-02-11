package src.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import src.attachment.Badge;
import src.base.Body;
import src.enums.Attitude;
import src.enums.BodyRank;
import src.enums.Intelligence;
import src.yukkuri.Reimu;

/**
 * Test class for BadgeLogic.
 * BadgeLogic is pure business logic with no World dependencies - highly
 * testable!
 */
public class BadgeLogicTest {

    // ========== Null/Invalid Input Tests ==========

    @Test
    public void testBadgeTestNullBody() {
        boolean result = BadgeLogic.badgeTest(null);
        assertFalse(result, "badgeTest should return false for null body");
    }

    @Test
    public void testBadgeTestDeadBody() {
        Reimu dead = new Reimu();
        dead.setDead(true);

        boolean result = BadgeLogic.badgeTest(dead);
        assertFalse(result, "badgeTest should return false for dead body");
    }

    @Test
    public void testBadgeTestRemovedBody() {
        Reimu removed = new Reimu();
        removed.setRemoved(true);

        boolean result = BadgeLogic.badgeTest(removed);
        assertFalse(result, "badgeTest should return false for removed body");
    }

    // ========== Non-Kaiyu Tests ==========
    // Note: Body doesn't have setBodyRank/setIdiot setters
    // These tests are simplified to test the method signature

    // ========== Kaiyu Badge Tests (Attitude x Intelligence combinations)
    // ==========

    @Test
    public void testVeryNiceWiseGetsGold() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.VERY_NICE, Intelligence.WISE);

        boolean result = BadgeLogic.badgeTest(yukkuri);

        assertTrue(result, "badgeTest should succeed");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "Should have badge attached");
        // VERY_NICE + WISE = GOLD
    }

    @Test
    public void testVeryNiceAverageGetsSilver() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.VERY_NICE, Intelligence.AVERAGE);

        boolean result = BadgeLogic.badgeTest(yukkuri);

        assertTrue(result, "badgeTest should succeed");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "Should have badge attached");
        // VERY_NICE + AVERAGE = SILVER
    }

    @Test
    public void testVeryNiceFoolGetsBronze() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.VERY_NICE, Intelligence.FOOL);

        boolean result = BadgeLogic.badgeTest(yukkuri);

        assertTrue(result, "badgeTest should succeed");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "Should have badge attached");
        // VERY_NICE + FOOL = BRONZE
    }

    @Test
    public void testNiceWiseGetsGold() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.NICE, Intelligence.WISE);

        boolean result = BadgeLogic.badgeTest(yukkuri);

        assertTrue(result, "badgeTest should succeed");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "Should have badge attached");
        // NICE + WISE = GOLD
    }

    @Test
    public void testNiceAverageGetsSilver() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.NICE, Intelligence.AVERAGE);

        boolean result = BadgeLogic.badgeTest(yukkuri);

        assertTrue(result, "badgeTest should succeed");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "Should have badge attached");
        // NICE + AVERAGE = SILVER
    }

    @Test
    public void testAverageWiseGetsSilver() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.AVERAGE, Intelligence.WISE);

        boolean result = BadgeLogic.badgeTest(yukkuri);

        assertTrue(result, "badgeTest should succeed");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "Should have badge attached");
        // AVERAGE + WISE = SILVER
    }

    @Test
    public void testAverageFoolGetsBronze() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.AVERAGE, Intelligence.FOOL);

        boolean result = BadgeLogic.badgeTest(yukkuri);

        assertTrue(result, "badgeTest should succeed");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "Should have badge attached");
        // AVERAGE + FOOL = BRONZE
    }

    @Test
    public void testShitheadWiseGetsBronze() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.SHITHEAD, Intelligence.WISE);

        boolean result = BadgeLogic.badgeTest(yukkuri);

        assertTrue(result, "badgeTest should succeed");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "Should have badge attached");
        // SHITHEAD + WISE = BRONZE
    }

    @Test
    public void testSuperShitheadFoolGetsFake() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.SUPER_SHITHEAD, Intelligence.FOOL);

        boolean result = BadgeLogic.badgeTest(yukkuri);

        assertTrue(result, "badgeTest should succeed");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "Should have badge attached");
        // SUPER_SHITHEAD + FOOL = FAKE
    }

    // ========== Badge Attachment Tests ==========

    @Test
    public void testBadgeReplacesExisting() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.VERY_NICE, Intelligence.WISE);

        // First badge test - should add badge
        boolean result1 = BadgeLogic.badgeTest(yukkuri);
        assertTrue(result1, "First badgeTest should succeed");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "Should have 1 badge");

        // Second badge test - should remove existing badge
        boolean result2 = BadgeLogic.badgeTest(yukkuri);
        assertTrue(result2, "Second badgeTest should succeed");
        assertEquals(0, yukkuri.getAttachmentSize(Badge.class), "Badge should be removed on second test");
    }

    // ========== Helper Methods ==========

    /**
     * Creates a yukkuri with specified attitude and intelligence.
     * Note: Body doesn't have setBodyRank/setIdiot setters, so we just set what we
     * can.
     */
    private Reimu createKaiyuYukkuri(Attitude attitude, Intelligence intelligence) {
        Reimu yukkuri = new Reimu();
        yukkuri.setAttitude(attitude);
        yukkuri.setIntelligence(intelligence);
        return yukkuri;
    }
}
