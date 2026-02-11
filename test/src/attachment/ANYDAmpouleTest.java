package src.attachment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.draw.World;
import src.enums.AgeState;
import src.enums.Direction;
import src.enums.Event;
import src.system.ResourceUtil;
import src.yukkuri.Reimu;

public class ANYDAmpouleTest {

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        ANYDAmpoule.setImages(buildImages());
        ANYDAmpoule.setImgW(new int[] { 10, 20, 30 });
        ANYDAmpoule.setImgH(new int[] { 11, 21, 31 });
        ANYDAmpoule.setPivX(new int[] { 1, 2, 3 });
        ANYDAmpoule.setPivY(new int[] { 4, 5, 6 });
    }

    @Test
    public void testStaticAccessors() {
        assertEquals("ANYDAmpoule", ANYDAmpoule.getPosKey());
        assertEquals(7, ANYDAmpoule.getProperty().length);
        assertEquals(2, ANYDAmpoule.getProperty()[0]); // 赤ゆ用画像サイズ
        assertEquals(2, ANYDAmpoule.getProperty()[1]); // 子ゆ用画像サイズ
        assertEquals(1, ANYDAmpoule.getProperty()[2]); // 成ゆ用画像サイズ
    }

    @Test
    public void testConstructorDefaultsAndBoundary() {
        Body parent = createParent(AgeState.CHILD);
        ANYDAmpoule ampoule = new ANYDAmpoule(parent);

        assertEquals(parent.getUniqueID(), ampoule.getParent());
        assertEquals(1000, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
        assertEquals(2, ampoule.getPivotX());
        assertEquals(5, ampoule.getPivotY());
        assertEquals(20, ampoule.getW());
        assertEquals(21, ampoule.getH());
    }

    @Test
    public void testUpdateReturnsDoNothing() {
        Body parent = createParent(AgeState.CHILD);
        ANYDAmpoule ampoule = new ANYDAmpoule(parent);

        // ANYDAmpouleのupdate()は何もせずDONOTHINGを返す
        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        ANYDAmpoule ampoule = new ANYDAmpoule(parent);

        // parentをマップから削除
        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());

        BufferedImage image = ampoule.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsLeftImageWhenDirectionLeft() {
        Body parent = createParent(AgeState.CHILD);
        ANYDAmpoule ampoule = new ANYDAmpoule(parent);

        parent.setDirection(Direction.LEFT);
        BufferedImage image = ampoule.getImage(parent);

        // 左向きの場合はimages[age][0]を返す
        assertSame(ANYDAmpoule.getImages()[AgeState.CHILD.ordinal()][0], image);
    }

    @Test
    public void testGetImageReturnsRightImageWhenDirectionRight() {
        Body parent = createParent(AgeState.CHILD);
        ANYDAmpoule ampoule = new ANYDAmpoule(parent);

        parent.setDirection(Direction.RIGHT);
        BufferedImage image = ampoule.getImage(parent);

        // 右向きの場合はimages[age][1]を返す
        assertSame(ANYDAmpoule.getImages()[AgeState.CHILD.ordinal()][1], image);
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Body parent = createParent(AgeState.ADULT);
        ANYDAmpoule ampoule = new ANYDAmpoule(parent);

        ampoule.resetBoundary();

        assertEquals(3, ampoule.getPivotX());
        assertEquals(6, ampoule.getPivotY());
        assertEquals(30, ampoule.getW());
        assertEquals(31, ampoule.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        ANYDAmpoule ampoule = new ANYDAmpoule(parent);

        // 初期値を記録
        int origPivotX = ampoule.getPivotX();
        int origPivotY = ampoule.getPivotY();

        // parentをマップから削除
        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());

        ampoule.resetBoundary();

        // 値が変わらないことを確認
        assertEquals(origPivotX, ampoule.getPivotX());
        assertEquals(origPivotY, ampoule.getPivotY());
    }

    @Test
    public void testToStringUsesResourceUtil() {
        Body parent = createParent(AgeState.BABY);
        ANYDAmpoule ampoule = new ANYDAmpoule(parent);

        assertEquals(ResourceUtil.getInstance().read("item_anti_nyd"), ampoule.toString());
    }

    @Test
    public void testDefaultConstructor() {
        ANYDAmpoule ampoule = new ANYDAmpoule();
        // デフォルトコンストラクタが例外を投げないことを確認
        assertEquals(0, ampoule.getParent());
    }

    @Test
    public void testConstructorWithParentNotInWorld() {
        Body parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        ANYDAmpoule ampoule = new ANYDAmpoule(parent);
        assertEquals(1000, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
    }

    private static Body createParent(AgeState ageState) {
        Body parent = new Reimu();
        parent.setAgeState(ageState);
        SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);
        return parent;
    }

    private static BufferedImage[][] buildImages() {
        BufferedImage[][] images = new BufferedImage[3][2];
        for (int age = 0; age < 3; age++) {
            for (int dir = 0; dir < 2; dir++) {
                images[age][dir] = new BufferedImage(10 + age * 10, 10 + age * 10, BufferedImage.TYPE_INT_ARGB);
            }
        }
        return images;
    }
}
