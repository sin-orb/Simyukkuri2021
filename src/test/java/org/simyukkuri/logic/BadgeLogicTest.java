package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.attachment.Attachment;
import org.simyukkuri.entity.core.attachment.impl.Badge;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.util.WorldTestHelper;

/**
 * Test class for BadgeLogic. BadgeLogic is pure business logic with no World dependencies - highly
 * testable!
 */
public class BadgeLogicTest {

    @BeforeEach
    public void setUp() throws Exception {
        WorldTestHelper.resetWorld();
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
        Badge.setImages(buildImages());
        Badge.setImgW(new int[] {10, 20, 30});
        Badge.setImgH(new int[] {11, 21, 31});
        Badge.setPivX(new int[] {1, 2, 3});
        Badge.setPivY(new int[] {4, 5, 6});
    }

    // Null/Invalid Input Tests
    @Test
    public void testBadgeTestNullBody() {
        boolean result = BadgeLogic.badgeTest(null);
        assertFalse(result, "badgeTest(null) は false を返すこと");
    }

    @Test
    public void testBadgeTestDeadBody() {
        Reimu dead = new Reimu();
        dead.setDead(true);
        boolean result = BadgeLogic.badgeTest(dead);
        assertFalse(result, "dead body には badgeTest が false を返すこと");
        // 死亡ゆっくりにはバッジが付かないこと
        assertEquals(0, dead.getAttachmentSize(Badge.class), "dead body にはバッジが付かないこと");
    }

    @Test
    public void testBadgeTestRemovedBody() {
        Reimu removed = new Reimu();
        removed.setRemoved(true);
        boolean result = BadgeLogic.badgeTest(removed);
        assertFalse(result, "removed body には badgeTest が false を返すこと");
        // 除去済みゆっくりにはバッジが付かないこと
        assertEquals(0, removed.getAttachmentSize(Badge.class), "removed body にはバッジが付かないこと");
    }

    // Non-Kaiyu Tests    // Note: Yukkuri doesn't have setRank/setIdiot setters
    // These tests are simplified to test the method signature

    // ========== Kaiyu Badge Tests (Attitude x Intelligence combinations)

    @Test
    public void testVeryNiceWiseGetsGold() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.VERY_NICE, Intelligence.WISE);
        assertTrue(BadgeLogic.badgeTest(yukkuri), "badgeTest は true を返すこと");
        assertEquals(Badge.BadgeRank.GOLD, getBadgeRank(yukkuri), "VERY_NICE + WISE = GOLD");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "バッジが 1 つ付いていること");
    }

    @Test
    public void testVeryNiceAverageGetsSilver() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.VERY_NICE, Intelligence.AVERAGE);
        assertTrue(BadgeLogic.badgeTest(yukkuri), "badgeTest は true を返すこと");
        assertEquals(Badge.BadgeRank.SILVER, getBadgeRank(yukkuri), "VERY_NICE + AVERAGE = SILVER");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "バッジが 1 つ付いていること");
    }

    @Test
    public void testVeryNiceFoolGetsBronze() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.VERY_NICE, Intelligence.FOOL);
        assertTrue(BadgeLogic.badgeTest(yukkuri), "badgeTest は true を返すこと");
        assertEquals(Badge.BadgeRank.BRONZE, getBadgeRank(yukkuri), "VERY_NICE + FOOL = BRONZE");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "バッジが 1 つ付いていること");
    }

    @Test
    public void testNiceWiseGetsGold() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.NICE, Intelligence.WISE);
        assertTrue(BadgeLogic.badgeTest(yukkuri), "badgeTest は true を返すこと");
        assertEquals(Badge.BadgeRank.GOLD, getBadgeRank(yukkuri), "NICE + WISE = GOLD");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "バッジが 1 つ付いていること");
    }

    @Test
    public void testNiceAverageGetsSilver() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.NICE, Intelligence.AVERAGE);
        assertTrue(BadgeLogic.badgeTest(yukkuri), "badgeTest は true を返すこと");
        assertEquals(Badge.BadgeRank.SILVER, getBadgeRank(yukkuri), "NICE + AVERAGE = SILVER");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "バッジが 1 つ付いていること");
    }

    @Test
    public void testAverageWiseGetsSilver() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.AVERAGE, Intelligence.WISE);
        assertTrue(BadgeLogic.badgeTest(yukkuri), "badgeTest は true を返すこと");
        assertEquals(Badge.BadgeRank.SILVER, getBadgeRank(yukkuri), "AVERAGE + WISE = SILVER");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "バッジが 1 つ付いていること");
    }

    @Test
    public void testAverageFoolGetsBronze() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.AVERAGE, Intelligence.FOOL);
        assertTrue(BadgeLogic.badgeTest(yukkuri), "badgeTest は true を返すこと");
        assertEquals(Badge.BadgeRank.BRONZE, getBadgeRank(yukkuri), "AVERAGE + FOOL = BRONZE");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "バッジが 1 つ付いていること");
    }

    @Test
    public void testShitheadWiseGetsBronze() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.SHITHEAD, Intelligence.WISE);
        assertTrue(BadgeLogic.badgeTest(yukkuri), "badgeTest は true を返すこと");
        assertEquals(Badge.BadgeRank.BRONZE, getBadgeRank(yukkuri), "SHITHEAD + WISE = BRONZE");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "バッジが 1 つ付いていること");
    }

    @Test
    public void testSuperShitheadFoolGetsFake() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.SUPER_SHITHEAD, Intelligence.FOOL);
        assertTrue(BadgeLogic.badgeTest(yukkuri), "badgeTest は true を返すこと");
        assertEquals(Badge.BadgeRank.FAKE, getBadgeRank(yukkuri), "SUPER_SHITHEAD + FOOL = FAKE");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "バッジが 1 つ付いていること");
    }

    @Test
    public void testStrayFoolGetsFake() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.AVERAGE, Intelligence.FOOL);
        yukkuri.setRank(YukkuriRank.NORAYU);
        assertTrue(BadgeLogic.badgeTest(yukkuri), "badgeTest は true を返すこと");
        assertEquals(Badge.BadgeRank.FAKE, getBadgeRank(yukkuri), "NORAYU = FAKE");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "バッジが 1 つ付いていること");
    }

    @Test
    public void testIdiotGetsFake() {
        org.simyukkuri.entity.core.living.yukkuri.impl.Tarinai yukkuri = createKaiyuTarinai();
        yukkuri.setRank(YukkuriRank.KAIYU);
        assertTrue(BadgeLogic.badgeTest(yukkuri), "badgeTest は true を返すこと");
        assertEquals(Badge.BadgeRank.FAKE, getBadgeRank(yukkuri), "idiot = FAKE");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "バッジが 1 つ付いていること");
    }

    // Badge Attachment Tests
    @Test
    public void testBadgeReplacesExisting() {
        Reimu yukkuri = createKaiyuYukkuri(Attitude.VERY_NICE, Intelligence.WISE);

        // 1回目: バッジが付くこと
        assertTrue(BadgeLogic.badgeTest(yukkuri), "1回目の badgeTest は true を返すこと");
        assertEquals(1, yukkuri.getAttachmentSize(Badge.class), "1回目後はバッジが 1 つあること");
        assertEquals(Badge.BadgeRank.GOLD, getBadgeRank(yukkuri), "1回目後は GOLD バッジであること");

        // 2回目: 既存バッジが除去されること（交換ロジック）
        assertTrue(BadgeLogic.badgeTest(yukkuri), "2回目の badgeTest は true を返すこと");
        assertEquals(0, yukkuri.getAttachmentSize(Badge.class),
                "2回目後は既存バッジが除去されること（badge replaces existing）");
    }

    // Helper Methods
    /**
     * Creates a yukkuri with specified attitude and intelligence. Note: Yukkuri doesn't have
     * setRank/setIdiot setters, so we just set what we can.
     */
    private Reimu createKaiyuYukkuri(Attitude attitude, Intelligence intelligence) {
        Reimu yukkuri = new Reimu() {
            private static final long serialVersionUID = 1L;

            @Override
            public Point4y[] getMountPoint(String key) {
                if ("Badge".equals(key)) {
                    return new Point4y[] {
                            new Point4y(1, 2),
                            new Point4y(3, 4),
                            new Point4y(5, 6)
                    };
                }
                return null;
            }
        };
        yukkuri.setAttitude(attitude);
        yukkuri.setIntelligence(intelligence);
        yukkuri.setRank(YukkuriRank.KAIYU);
        SimYukkuri.world
                .getCurrentWorldState()
                .getYukkuriRegistry()
                .put(yukkuri.getUniqueId(), yukkuri);
        return yukkuri;
    }

    private org.simyukkuri.entity.core.living.yukkuri.impl.Tarinai createKaiyuTarinai() {
        org.simyukkuri.entity.core.living.yukkuri.impl.Tarinai yukkuri =
                new org.simyukkuri.entity.core.living.yukkuri.impl.Tarinai() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Point4y[] getMountPoint(String key) {
                        if ("Badge".equals(key)) {
                            return new Point4y[] {
                                    new Point4y(1, 2),
                                    new Point4y(3, 4),
                                    new Point4y(5, 6)
                            };
                        }
                        return null;
                    }
                };
        SimYukkuri.world
                .getCurrentWorldState()
                .getYukkuriRegistry()
                .put(yukkuri.getUniqueId(), yukkuri);
        return yukkuri;
    }

    private static BufferedImage[][] buildImages() {
        BufferedImage[][] images = new BufferedImage[3][3];
        for (int age = 0; age < 3; age++) {
            for (int rank = 0; rank < 3; rank++) {
                images[age][rank] =
                        new BufferedImage(
                                10 + age * 10, 10 + age * 10, BufferedImage.TYPE_INT_ARGB);
            }
        }
        return images;
    }

    private Badge.BadgeRank getBadgeRank(Yukkuri b) {
        List<Attachment> list = b.getAttach();
        if (list == null) {
            return null;
        }
        for (Attachment at : list) {
            if (at instanceof Badge) {
                return ((Badge) at).getBadgeRank();
            }
        }
        return null;
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_NewGoldBadgeMakesBodyBeVainAndReducesStress() {
            Reimu yukkuri = createKaiyuYukkuri(Attitude.VERY_NICE, Intelligence.WISE);
            yukkuri.addStress(120);

            assertTrue(BadgeLogic.badgeTest(yukkuri));

            assertEquals(Badge.BadgeRank.GOLD, getBadgeRank(yukkuri));
            assertTrue(yukkuri.isBeVain(), "new badge grant should trigger getInVain");
            assertTrue(yukkuri.getStress() < 120, "getInVain should reduce stress");
        }
    }
}
