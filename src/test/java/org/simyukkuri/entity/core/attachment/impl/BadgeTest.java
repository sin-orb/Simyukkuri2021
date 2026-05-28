package org.simyukkuri.entity.core.attachment.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.attachment.impl.Badge.BadgeRank;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.system.ResourceUtil;
import org.simyukkuri.util.WorldTestHelper;

public class BadgeTest {

    @BeforeEach
    public void setUp() throws Exception {
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardAttachmentMountPoints();
        Badge.setImages(buildImages());
        Badge.setImgW(new int[] {10, 20, 30});
        Badge.setImgH(new int[] {11, 21, 31});
        Badge.setPivX(new int[] {1, 2, 3});
        Badge.setPivY(new int[] {4, 5, 6});
    }

    @Test
    public void testBadgeRankEnum() {
        assertEquals(4, BadgeRank.values().length);
        assertEquals("fake.png", BadgeRank.FAKE.getFileName());
        assertEquals("bronze.png", BadgeRank.BRONZE.getFileName());
        assertEquals("silver.png", BadgeRank.SILVER.getFileName());
        assertEquals("gold.png", BadgeRank.GOLD.getFileName());
    }

    @Test
    public void testStaticAccessors() {
        assertEquals("Badge", Badge.getPosKey());
        assertEquals(7, Badge.getProperty().length);
        assertEquals(2, Badge.getProperty()[0]); // 赤ゆ用画像サイズ
        assertEquals(2, Badge.getProperty()[1]); // 子ゆ用画像サイズ
        assertEquals(1, Badge.getProperty()[2]); // 成ゆ用画像サイズ
    }

    @Test
    public void testConstructorWithBronzeBadge() {
        Yukkuri parent = createParent(AgeState.CHILD);
        Badge badge = new Badge(parent, BadgeRank.BRONZE);

        assertEquals(parent.getUniqueId(), badge.getParent());
        assertEquals(BadgeRank.BRONZE, badge.getBadgeRank());
        assertEquals(BadgeRank.BRONZE, badge.getBadgeRank());
        assertEquals(0, badge.getValue());
        assertEquals(0, badge.getCost());
    }

    @Test
    public void testConstructorWithGoldBadge() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Badge badge = new Badge(parent, BadgeRank.GOLD);

        assertEquals(BadgeRank.GOLD, badge.getBadgeRank());
        assertEquals(3, badge.getPivotX());
        assertEquals(6, badge.getPivotY());
        assertEquals(30, badge.getW());
        assertEquals(31, badge.getH());
    }

    @Test
    public void testUpdateReturnsDoNothing() {
        Yukkuri parent = createParent(AgeState.CHILD);
        Badge badge = new Badge(parent, BadgeRank.SILVER);

        TickResult result = badge.update();

        assertEquals(TickResult.NONE, result);
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInMap() {
        Yukkuri parent = createParent(AgeState.CHILD);
        Badge badge = new Badge(parent, BadgeRank.BRONZE);

        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().remove(parent.getUniqueId());

        BufferedImage image = badge.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsCorrectImageForRank() {
        Yukkuri parent = createParent(AgeState.CHILD);

        for (BadgeRank rank : BadgeRank.values()) {
            Badge badge = new Badge(parent, rank);
            BufferedImage image = badge.getImage(parent);

            assertSame(Badge.getImages()[AgeState.CHILD.ordinal()][rank.ordinal()], image);
        }
    }

    @Test
    public void testGetImageReturnsCorrectImageForAge() {
        Yukkuri babyParent = createParent(AgeState.BABY);
        Yukkuri childParent = createParent(AgeState.CHILD);
        Yukkuri adultParent = createParent(AgeState.ADULT);

        Badge babyBadge = new Badge(babyParent, BadgeRank.GOLD);
        Badge childBadge = new Badge(childParent, BadgeRank.GOLD);
        Badge adultBadge = new Badge(adultParent, BadgeRank.GOLD);

        assertSame(
                Badge.getImages()[AgeState.BABY.ordinal()][BadgeRank.GOLD.ordinal()],
                babyBadge.getImage(babyParent));
        assertSame(
                Badge.getImages()[AgeState.CHILD.ordinal()][BadgeRank.GOLD.ordinal()],
                childBadge.getImage(childParent));
        assertSame(
                Badge.getImages()[AgeState.ADULT.ordinal()][BadgeRank.GOLD.ordinal()],
                adultBadge.getImage(adultParent));
    }

    @Test
    public void testSetEBadgeRankChangesRank() {
        Yukkuri parent = createParent(AgeState.CHILD);
        Badge badge = new Badge(parent, BadgeRank.FAKE);

        assertEquals(BadgeRank.FAKE, badge.getBadgeRank());

        badge.setBadgeRank(BadgeRank.GOLD);

        assertEquals(BadgeRank.GOLD, badge.getBadgeRank());
        assertEquals(BadgeRank.GOLD, badge.getBadgeRank());
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Badge badge = new Badge(parent, BadgeRank.SILVER);

        badge.resetBoundary();

        assertEquals(3, badge.getPivotX());
        assertEquals(6, badge.getPivotY());
        assertEquals(30, badge.getW());
        assertEquals(31, badge.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Yukkuri parent = createParent(AgeState.CHILD);
        Badge badge = new Badge(parent, BadgeRank.BRONZE);

        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().remove(parent.getUniqueId());

        int origPivotX = badge.getPivotX();
        int origPivotY = badge.getPivotY();
        badge.resetBoundary();

        assertEquals(origPivotX, badge.getPivotX());
        assertEquals(origPivotY, badge.getPivotY());
    }

    @Test
    public void testToStringUsesResourceUtil() {
        Yukkuri parent = createParent(AgeState.BABY);
        Badge badge = new Badge(parent, BadgeRank.FAKE);

        assertEquals(ResourceUtil.getInstance().read("item_badge"), badge.toString());
    }

    @Test
    public void testDefaultConstructor() {
        Badge badge = new Badge();
        assertEquals(0, badge.getParent());
        assertNull(badge.getBadgeRank());
    }

    @Test
    public void testConstructorWithParentInWorld() {
        Yukkuri parent = createParent(AgeState.CHILD);
        Badge badge = new Badge(parent, BadgeRank.BRONZE);
        assertEquals(0, badge.getValue());
        assertEquals(0, badge.getCost());
        assertEquals(BadgeRank.BRONZE, badge.getBadgeRank());
    }

    private static Yukkuri createParent(AgeState ageState) {
        Yukkuri parent = new Reimu() {
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
        parent.setAgeState(ageState);
        SimYukkuri.world
                .getCurrentWorldState()
                .getYukkuriRegistry()
                .put(parent.getUniqueId(), parent);
        return parent;
    }

    private static BufferedImage[][] buildImages() {
        // [年齢][ランク]
        BufferedImage[][] images = new BufferedImage[3][BadgeRank.values().length];
        for (int age = 0; age < 3; age++) {
            for (int rank = 0; rank < BadgeRank.values().length; rank++) {
                images[age][rank] =
                        new BufferedImage(
                                10 + age * 10, 10 + age * 10, BufferedImage.TYPE_INT_ARGB);
            }
        }
        return images;
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            Badge.loadImages(Badge.class.getClassLoader(), null);
        } catch (Exception e) {
            // ignore
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_ChangingBadgeRankSwitchesRenderedImage() {
            Yukkuri parent = createParent(AgeState.CHILD);
            Badge badge = new Badge(parent, BadgeRank.FAKE);

            badge.setBadgeRank(BadgeRank.GOLD);

            assertEquals(BadgeRank.GOLD, badge.getBadgeRank());
            assertSame(
                    Badge.getImages()[AgeState.CHILD.ordinal()][BadgeRank.GOLD.ordinal()],
                    badge.getImage(parent));
        }

        @Test
        void testScenario_AdultBadgeUsesAdultBoundaryAndSelectedRankImage() {
            Yukkuri parent = createParent(AgeState.ADULT);
            Badge badge = new Badge(parent, BadgeRank.SILVER);

            assertEquals(3, badge.getPivotX());
            assertEquals(6, badge.getPivotY());
            assertEquals(30, badge.getW());
            assertEquals(31, badge.getH());
            assertSame(
                    Badge.getImages()[AgeState.ADULT.ordinal()][BadgeRank.SILVER.ordinal()],
                    badge.getImage(parent));
        }
    }
}
