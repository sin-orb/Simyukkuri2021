package src.attachment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.attachment.Badge.BadgeRank;
import src.base.Body;
import src.draw.World;
import src.enums.AgeState;
import src.enums.Event;
import src.system.ResourceUtil;
import src.yukkuri.Reimu;

public class BadgeTest {

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        Badge.setImages(buildImages());
        Badge.setImgW(new int[] { 10, 20, 30 });
        Badge.setImgH(new int[] { 11, 21, 31 });
        Badge.setPivX(new int[] { 1, 2, 3 });
        Badge.setPivY(new int[] { 4, 5, 6 });
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
        Body parent = createParent(AgeState.CHILD);
        Badge badge = new Badge(parent, BadgeRank.BRONZE);

        assertEquals(parent.getUniqueID(), badge.getParent());
        assertEquals(BadgeRank.BRONZE, badge.getBadgeRank());
        assertEquals(BadgeRank.BRONZE, badge.getEBadgeRank());
        assertEquals(0, badge.getValue());
        assertEquals(0, badge.getCost());
    }

    @Test
    public void testConstructorWithGoldBadge() {
        Body parent = createParent(AgeState.ADULT);
        Badge badge = new Badge(parent, BadgeRank.GOLD);

        assertEquals(BadgeRank.GOLD, badge.getBadgeRank());
        assertEquals(3, badge.getPivotX());
        assertEquals(6, badge.getPivotY());
        assertEquals(30, badge.getW());
        assertEquals(31, badge.getH());
    }

    @Test
    public void testUpdateReturnsDoNothing() {
        Body parent = createParent(AgeState.CHILD);
        Badge badge = new Badge(parent, BadgeRank.SILVER);

        Event result = badge.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        Badge badge = new Badge(parent, BadgeRank.BRONZE);

        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());

        BufferedImage image = badge.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsCorrectImageForRank() {
        Body parent = createParent(AgeState.CHILD);

        for (BadgeRank rank : BadgeRank.values()) {
            Badge badge = new Badge(parent, rank);
            BufferedImage image = badge.getImage(parent);

            assertSame(Badge.getImages()[AgeState.CHILD.ordinal()][rank.ordinal()], image);
        }
    }

    @Test
    public void testGetImageReturnsCorrectImageForAge() {
        Body babyParent = createParent(AgeState.BABY);
        Body childParent = createParent(AgeState.CHILD);
        Body adultParent = createParent(AgeState.ADULT);

        Badge babyBadge = new Badge(babyParent, BadgeRank.GOLD);
        Badge childBadge = new Badge(childParent, BadgeRank.GOLD);
        Badge adultBadge = new Badge(adultParent, BadgeRank.GOLD);

        assertSame(Badge.getImages()[AgeState.BABY.ordinal()][BadgeRank.GOLD.ordinal()],
                   babyBadge.getImage(babyParent));
        assertSame(Badge.getImages()[AgeState.CHILD.ordinal()][BadgeRank.GOLD.ordinal()],
                   childBadge.getImage(childParent));
        assertSame(Badge.getImages()[AgeState.ADULT.ordinal()][BadgeRank.GOLD.ordinal()],
                   adultBadge.getImage(adultParent));
    }

    @Test
    public void testSetEBadgeRankChangesRank() {
        Body parent = createParent(AgeState.CHILD);
        Badge badge = new Badge(parent, BadgeRank.FAKE);

        assertEquals(BadgeRank.FAKE, badge.getBadgeRank());

        badge.setEBadgeRank(BadgeRank.GOLD);

        assertEquals(BadgeRank.GOLD, badge.getBadgeRank());
        assertEquals(BadgeRank.GOLD, badge.getEBadgeRank());
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Body parent = createParent(AgeState.ADULT);
        Badge badge = new Badge(parent, BadgeRank.SILVER);

        badge.resetBoundary();

        assertEquals(3, badge.getPivotX());
        assertEquals(6, badge.getPivotY());
        assertEquals(30, badge.getW());
        assertEquals(31, badge.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        Badge badge = new Badge(parent, BadgeRank.BRONZE);

        int origPivotX = badge.getPivotX();
        int origPivotY = badge.getPivotY();

        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());

        badge.resetBoundary();

        assertEquals(origPivotX, badge.getPivotX());
        assertEquals(origPivotY, badge.getPivotY());
    }

    @Test
    public void testToStringUsesResourceUtil() {
        Body parent = createParent(AgeState.BABY);
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
    public void testConstructorWithParentNotInWorld() {
        Body parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        Badge badge = new Badge(parent, BadgeRank.BRONZE);
        assertEquals(0, badge.getValue());
        assertEquals(0, badge.getCost());
        assertEquals(BadgeRank.BRONZE, badge.getBadgeRank());
    }

    private static Body createParent(AgeState ageState) {
        Body parent = new Reimu();
        parent.setAgeState(ageState);
        SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);
        return parent;
    }

    private static BufferedImage[][] buildImages() {
        // [年齢][ランク]
        BufferedImage[][] images = new BufferedImage[3][BadgeRank.values().length];
        for (int age = 0; age < 3; age++) {
            for (int rank = 0; rank < BadgeRank.values().length; rank++) {
                images[age][rank] = new BufferedImage(10 + age * 10, 10 + age * 10, BufferedImage.TYPE_INT_ARGB);
            }
        }
        return images;
    }
}
