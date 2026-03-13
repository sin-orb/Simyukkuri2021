package src.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import src.SimYukkuri;
import src.attachment.Badge;
import src.base.Body;
import src.draw.Translate;
import src.draw.World;
import src.enums.Attitude;
import src.enums.BodyRank;
import src.enums.Intelligence;
import src.yukkuri.Reimu;
import src.yukkuri.Tarinai;
import src.base.Attachment;
import java.util.List;

/**
 * Test class for BadgeLogic.
 * BadgeLogic is pure business logic with no World dependencies - highly
 * testable!
 */
public class BadgeLogicTest {

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        Translate.setMapSize(1000, 1000, 500);
        Translate.setCanvasSize(800, 600, 100, 100, new float[] { 1.0f });
        Translate.createTransTable(false);
    }

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
        assertTrue(BadgeLogic.badgeTest(yukkuri));
        assertEquals(Badge.BadgeRank.GOLD, getBadgeRank(yukkuri));
    }

    @Test
    public void testVeryNiceAverageGetsSilver() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.VERY_NICE, Intelligence.AVERAGE);
        assertTrue(BadgeLogic.badgeTest(yukkuri));
        assertEquals(Badge.BadgeRank.SILVER, getBadgeRank(yukkuri));
    }

    @Test
    public void testVeryNiceFoolGetsBronze() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.VERY_NICE, Intelligence.FOOL);
        assertTrue(BadgeLogic.badgeTest(yukkuri));
        assertEquals(Badge.BadgeRank.BRONZE, getBadgeRank(yukkuri));
    }

    @Test
    public void testNiceWiseGetsGold() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.NICE, Intelligence.WISE);
        assertTrue(BadgeLogic.badgeTest(yukkuri));
        assertEquals(Badge.BadgeRank.GOLD, getBadgeRank(yukkuri));
    }

    @Test
    public void testNiceAverageGetsSilver() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.NICE, Intelligence.AVERAGE);
        assertTrue(BadgeLogic.badgeTest(yukkuri));
        assertEquals(Badge.BadgeRank.SILVER, getBadgeRank(yukkuri));
    }

    @Test
    public void testAverageWiseGetsSilver() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.AVERAGE, Intelligence.WISE);
        assertTrue(BadgeLogic.badgeTest(yukkuri));
        assertEquals(Badge.BadgeRank.SILVER, getBadgeRank(yukkuri));
    }

    @Test
    public void testAverageFoolGetsBronze() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.AVERAGE, Intelligence.FOOL);
        assertTrue(BadgeLogic.badgeTest(yukkuri));
        assertEquals(Badge.BadgeRank.BRONZE, getBadgeRank(yukkuri));
    }

    @Test
    public void testShitheadWiseGetsBronze() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.SHITHEAD, Intelligence.WISE);
        assertTrue(BadgeLogic.badgeTest(yukkuri));
        assertEquals(Badge.BadgeRank.BRONZE, getBadgeRank(yukkuri));
    }

    @Test
    public void testSuperShitheadFoolGetsFake() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.SUPER_SHITHEAD, Intelligence.FOOL);
        assertTrue(BadgeLogic.badgeTest(yukkuri));
        assertEquals(Badge.BadgeRank.FAKE, getBadgeRank(yukkuri));
    }

    @Test
    public void testStrayFoolGetsFake() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.AVERAGE, Intelligence.FOOL);
        yukkuri.setBodyRank(BodyRank.NORAYU);
        assertTrue(BadgeLogic.badgeTest(yukkuri));
        assertEquals(Badge.BadgeRank.FAKE, getBadgeRank(yukkuri));
    }

    @Test
    public void testIdiotGetsFake() {
        src.yukkuri.Tarinai yukkuri = new src.yukkuri.Tarinai();
        yukkuri.setBodyRank(BodyRank.KAIYU);
        assertTrue(BadgeLogic.badgeTest(yukkuri));
        assertEquals(Badge.BadgeRank.FAKE, getBadgeRank(yukkuri));
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
        yukkuri.setBodyRank(BodyRank.KAIYU);
        return yukkuri;
    }

    private Badge.BadgeRank getBadgeRank(Body b) {
        List<Attachment> list = b.getAttach();
        if (list == null)
            return null;
        for (Attachment at : list) {
            if (at instanceof Badge) {
                return ((Badge) at).getBadgeRank();
            }
        }
        return null;
    }
}
