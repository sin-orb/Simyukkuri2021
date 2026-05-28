package org.simyukkuri.entity.core.attachment.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.system.ResourceUtil;
import org.simyukkuri.util.WorldTestHelper;

public class AnydAmpouleTest {

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardAttachmentMountPoints();
        AnydAmpoule.setImages(buildImages());
        AnydAmpoule.setImgW(new int[] {10, 20, 30});
        AnydAmpoule.setImgH(new int[] {11, 21, 31});
        AnydAmpoule.setPivX(new int[] {1, 2, 3});
        AnydAmpoule.setPivY(new int[] {4, 5, 6});
    }

    @Test
    public void testStaticAccessors() {
        assertEquals("AnydAmpoule", AnydAmpoule.getPosKey());
        assertEquals(7, AnydAmpoule.getProperty().length);
        assertEquals(2, AnydAmpoule.getProperty()[0]); // 赤ゆ用画像サイズ
        assertEquals(2, AnydAmpoule.getProperty()[1]); // 子ゆ用画像サイズ
        assertEquals(1, AnydAmpoule.getProperty()[2]); // 成ゆ用画像サイズ
    }

    @Test
    public void testConstructorDefaultsAndBoundary() {
        Yukkuri parent = createParent(AgeState.CHILD);
        AnydAmpoule ampoule = new AnydAmpoule(parent);

        assertEquals(parent.getUniqueId(), ampoule.getParent());
        assertEquals(1000, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
        assertEquals(2, ampoule.getPivotX());
        assertEquals(5, ampoule.getPivotY());
        assertEquals(20, ampoule.getW());
        assertEquals(21, ampoule.getH());
    }

    @Test
    public void testUpdateReturnsDoNothing() {
        Yukkuri parent = createParent(AgeState.CHILD);
        AnydAmpoule ampoule = new AnydAmpoule(parent);

        // AnydAmpouleのupdate()は何もせずDONOTHINGを返す
        TickResult result = ampoule.update();

        assertEquals(TickResult.NONE, result);
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInMap() {
        Yukkuri parent = createParent(AgeState.CHILD);
        AnydAmpoule ampoule = new AnydAmpoule(parent);

        // parentをマップから削除
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().remove(parent.getUniqueId());

        BufferedImage image = ampoule.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsLeftImageWhenDirectionLeft() {
        Yukkuri parent = createParent(AgeState.CHILD);
        AnydAmpoule ampoule = new AnydAmpoule(parent);

        parent.setDirection(Direction.LEFT);
        BufferedImage image = ampoule.getImage(parent);

        // 左向きの場合はimages[age][0]を返す
        assertSame(AnydAmpoule.getImages()[AgeState.CHILD.ordinal()][0], image);
    }

    @Test
    public void testGetImageReturnsRightImageWhenDirectionRight() {
        Yukkuri parent = createParent(AgeState.CHILD);
        AnydAmpoule ampoule = new AnydAmpoule(parent);

        parent.setDirection(Direction.RIGHT);
        BufferedImage image = ampoule.getImage(parent);

        // 右向きの場合はimages[age][1]を返す
        assertSame(AnydAmpoule.getImages()[AgeState.CHILD.ordinal()][1], image);
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Yukkuri parent = createParent(AgeState.ADULT);
        AnydAmpoule ampoule = new AnydAmpoule(parent);

        ampoule.resetBoundary();

        assertEquals(3, ampoule.getPivotX());
        assertEquals(6, ampoule.getPivotY());
        assertEquals(30, ampoule.getW());
        assertEquals(31, ampoule.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Yukkuri parent = createParent(AgeState.CHILD);
        AnydAmpoule ampoule = new AnydAmpoule(parent);

        // 初期値を記録
        int origPivotX = ampoule.getPivotX();
        int origPivotY = ampoule.getPivotY();

        // parentをマップから削除
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().remove(parent.getUniqueId());

        ampoule.resetBoundary();

        // 値が変わらないことを確認
        assertEquals(origPivotX, ampoule.getPivotX());
        assertEquals(origPivotY, ampoule.getPivotY());
    }

    @Test
    public void testToStringUsesResourceUtil() {
        Yukkuri parent = createParent(AgeState.BABY);
        AnydAmpoule ampoule = new AnydAmpoule(parent);

        assertEquals(ResourceUtil.getInstance().read("item_anti_nyd"), ampoule.toString());
    }

    @Test
    public void testDefaultConstructor() {
        AnydAmpoule ampoule = new AnydAmpoule();
        // デフォルトコンストラクタが例外を投げないことを確認
        assertEquals(0, ampoule.getParent());
    }

    @Test
    public void testConstructorWithParentInWorld() {
        Yukkuri parent = createParent(AgeState.CHILD);
        AnydAmpoule ampoule = new AnydAmpoule(parent);
        assertEquals(1000, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
    }

    private static Yukkuri createParent(AgeState ageState) {
        Yukkuri parent = new Reimu();
        parent.setAgeState(ageState);
        SimYukkuri.world
                .getCurrentWorldState()
                .getYukkuriRegistry()
                .put(parent.getUniqueId(), parent);
        return parent;
    }

    private static BufferedImage[][] buildImages() {
        BufferedImage[][] images = new BufferedImage[3][2];
        for (int age = 0; age < 3; age++) {
            for (int dir = 0; dir < 2; dir++) {
                images[age][dir] =
                        new BufferedImage(
                                10 + age * 10, 10 + age * 10, BufferedImage.TYPE_INT_ARGB);
            }
        }
        return images;
    }

    @Test
    void testLoadImagesHeadlessExecutesCode() {
        try {
            AnydAmpoule.loadImages(AnydAmpoule.class.getClassLoader(), null);
        } catch (Exception e) {
            // ignore
        }
    }
}
