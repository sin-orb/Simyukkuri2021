package src.attachment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.draw.World;
import src.enums.AgeState;
import src.enums.Event;
import src.system.ResourceUtil;
import src.yukkuri.Reimu;

public class AntsTest {

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        Ants.setImages(buildImages());
        Ants.setImgW(new int[] { 10, 20, 30 });
        Ants.setImgH(new int[] { 11, 21, 31 });
        Ants.setPivX(new int[] { 1, 2, 3 });
        Ants.setPivY(new int[] { 4, 5, 6 });
    }

    @Test
    public void testStaticAccessors() {
        assertEquals("Ants", Ants.getPosKey());
        assertEquals(7, Ants.getProperty().length);
        assertEquals(4, Ants.getProperty()[0]); // 赤ゆ用画像サイズ
        assertEquals(2, Ants.getProperty()[1]); // 子ゆ用画像サイズ
        assertEquals(1, Ants.getProperty()[2]); // 成ゆ用画像サイズ
    }

    @Test
    public void testConstructorDefaultsAndBoundary() {
        Body parent = createParent(AgeState.CHILD);
        Ants ants = new Ants(parent);

        assertEquals(parent.getUniqueID(), ants.getParent());
        assertEquals(0, ants.getValue());
        assertEquals(0, ants.getCost());
        assertEquals(100, ants.getProcessInterval());
        assertEquals(2, ants.getPivotX());
        assertEquals(5, ants.getPivotY());
        assertEquals(20, ants.getW());
        assertEquals(21, ants.getH());
    }

    @Test
    public void testConstructorSetsNumOfAntsTo50() {
        Body parent = createParent(AgeState.ADULT);
        new Ants(parent);

        assertEquals(50, parent.getNumOfAnts());
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsNull() {
        // parentがnull（マップから削除された等）の場合、DONOTHINGを返す
        Ants ants = new Ants();

        Event result = ants.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testGetImageReturnsImage0WhenAntsLow() {
        Body parent = createParent(AgeState.CHILD);
        Ants ants = new Ants(parent);

        // アリの数がダメージ限界の1/3未満の場合、画像0を返す
        parent.setNumOfAnts(0);
        BufferedImage image = ants.getImage(parent);
        assertSame(Ants.getImages()[AgeState.CHILD.ordinal()][0], image);
    }

    @Test
    public void testGetImageReturnsImage1WhenAntsMedium() {
        Body parent = createParent(AgeState.CHILD);
        Ants ants = new Ants(parent);

        // アリの数がダメージ限界の1/3以上2/3未満の場合、画像1を返す
        int damageLimit = parent.getDamageLimit();
        parent.setNumOfAnts(damageLimit / 3);
        BufferedImage image = ants.getImage(parent);
        assertSame(Ants.getImages()[AgeState.CHILD.ordinal()][1], image);
    }

    @Test
    public void testGetImageReturnsImage2WhenAntsHigh() {
        Body parent = createParent(AgeState.CHILD);
        Ants ants = new Ants(parent);

        // アリの数がダメージ限界の2/3以上の場合、画像2を返す
        int damageLimit = parent.getDamageLimit();
        parent.setNumOfAnts(damageLimit * 2 / 3);
        BufferedImage image = ants.getImage(parent);
        assertSame(Ants.getImages()[AgeState.CHILD.ordinal()][2], image);
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Body parent = createParent(AgeState.ADULT);
        Ants ants = new Ants(parent);

        ants.resetBoundary();

        assertEquals(3, ants.getPivotX());
        assertEquals(6, ants.getPivotY());
        assertEquals(30, ants.getW());
        assertEquals(31, ants.getH());
    }

    @Test
    public void testToStringUsesResourceUtil() {
        Body parent = createParent(AgeState.BABY);
        Ants ants = new Ants(parent);

        assertEquals(ResourceUtil.getInstance().read("item_ants"), ants.toString());
    }

    @Test
    public void testDefaultConstructor() {
        Ants ants = new Ants();
        // デフォルトコンストラクタが例外を投げないことを確認
        assertEquals(0, ants.getParent());
    }

    @Test
    public void testConstructorWithParentNotInWorld() {
        Body parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        Ants ants = new Ants(parent);
        assertEquals(0, ants.getValue());
        assertEquals(0, ants.getCost());
    }


    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        Ants ants = new Ants(parent);

        int origPivotX = ants.getPivotX();
        int origPivotY = ants.getPivotY();

        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());

        ants.resetBoundary();

        assertEquals(origPivotX, ants.getPivotX());
        assertEquals(origPivotY, ants.getPivotY());
    }

    private static Body createParent(AgeState ageState) {
        Body parent = new Reimu();
        parent.setAgeState(ageState);
        SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);
        return parent;
    }

    private static BufferedImage[][] buildImages() {
        BufferedImage[][] images = new BufferedImage[3][3];
        for (int age = 0; age < 3; age++) {
            for (int level = 0; level < 3; level++) {
                images[age][level] = new BufferedImage(10 + age * 10, 10 + age * 10, BufferedImage.TYPE_INT_ARGB);
            }
        }
        return images;
    }
}
