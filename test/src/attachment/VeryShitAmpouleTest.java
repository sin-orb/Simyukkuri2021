package src.attachment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.draw.World;
import src.enums.AgeState;
import src.enums.CriticalDamegeType;
import src.enums.Direction;
import src.enums.Event;
import src.system.ResourceUtil;
import src.yukkuri.Reimu;

public class VeryShitAmpouleTest {

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        VeryShitAmpoule.setImages(buildImages());
        VeryShitAmpoule.setImgW(new int[] { 10, 20, 30 });
        VeryShitAmpoule.setImgH(new int[] { 11, 21, 31 });
        VeryShitAmpoule.setPivX(new int[] { 1, 2, 3 });
        VeryShitAmpoule.setPivY(new int[] { 4, 5, 6 });
    }

    @Test
    public void testStaticAccessors() {
        assertEquals("VeryShitAmpoule", VeryShitAmpoule.getPosKey());
        assertEquals(7, VeryShitAmpoule.getProperty().length);
        assertEquals(2, VeryShitAmpoule.getProperty()[0]); // 赤ゆ用画像サイズ
        assertEquals(2, VeryShitAmpoule.getProperty()[1]); // 子ゆ用画像サイズ
        assertEquals(1, VeryShitAmpoule.getProperty()[2]); // 成ゆ用画像サイズ
    }

    @Test
    public void testConstructorDefaultsAndBoundary() {
        Body parent = createParent(AgeState.CHILD);
        VeryShitAmpoule ampoule = new VeryShitAmpoule(parent);

        assertEquals(parent.getUniqueID(), ampoule.getParent());
        assertEquals(500, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
        assertEquals(2, ampoule.getPivotX());
        assertEquals(5, ampoule.getPivotY());
        assertEquals(20, ampoule.getW());
        assertEquals(21, ampoule.getH());
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsNull() {
        VeryShitAmpoule ampoule = new VeryShitAmpoule();

        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsDead() {
        Body parent = createParent(AgeState.ADULT);
        VeryShitAmpoule ampoule = new VeryShitAmpoule(parent);

        parent.setDead(true);

        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsCut() {
        Body parent = createParent(AgeState.ADULT);
        VeryShitAmpoule ampoule = new VeryShitAmpoule(parent);

        parent.setCriticalDamegeType(CriticalDamegeType.CUT);

        Event result = ampoule.update();

        // ちぎれている場合はうんうんを設定しない
        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateSetsShitWhenAlive() {
        Body parent = createParent(AgeState.ADULT);
        VeryShitAmpoule ampoule = new VeryShitAmpoule(parent);

        // shitを0にリセット
        parent.setShit(0);
        int shitBefore = parent.getShit();

        ampoule.update();

        // setShit(50, true)はSHITLIMITorg - 50を設定する
        // うんうんが増加していることを確認
        assertTrue(parent.getShit() > shitBefore);
        // うんうん限界に近い値（限界値-50）になっていることを確認
        assertEquals(parent.getShitLimit() - 50, parent.getShit());
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        VeryShitAmpoule ampoule = new VeryShitAmpoule(parent);

        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());

        BufferedImage image = ampoule.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsLeftImageWhenDirectionLeft() {
        Body parent = createParent(AgeState.CHILD);
        VeryShitAmpoule ampoule = new VeryShitAmpoule(parent);

        parent.setDirection(Direction.LEFT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(VeryShitAmpoule.getImages()[AgeState.CHILD.ordinal()][0], image);
    }

    @Test
    public void testGetImageReturnsRightImageWhenDirectionRight() {
        Body parent = createParent(AgeState.CHILD);
        VeryShitAmpoule ampoule = new VeryShitAmpoule(parent);

        parent.setDirection(Direction.RIGHT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(VeryShitAmpoule.getImages()[AgeState.CHILD.ordinal()][1], image);
    }

    @Test
    public void testGetImageReturnsCorrectImageForAge() {
        Body babyParent = createParent(AgeState.BABY);
        Body childParent = createParent(AgeState.CHILD);
        Body adultParent = createParent(AgeState.ADULT);

        VeryShitAmpoule babyAmpoule = new VeryShitAmpoule(babyParent);
        VeryShitAmpoule childAmpoule = new VeryShitAmpoule(childParent);
        VeryShitAmpoule adultAmpoule = new VeryShitAmpoule(adultParent);

        babyParent.setDirection(Direction.LEFT);
        childParent.setDirection(Direction.LEFT);
        adultParent.setDirection(Direction.LEFT);

        assertSame(VeryShitAmpoule.getImages()[AgeState.BABY.ordinal()][0], babyAmpoule.getImage(babyParent));
        assertSame(VeryShitAmpoule.getImages()[AgeState.CHILD.ordinal()][0], childAmpoule.getImage(childParent));
        assertSame(VeryShitAmpoule.getImages()[AgeState.ADULT.ordinal()][0], adultAmpoule.getImage(adultParent));
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Body parent = createParent(AgeState.ADULT);
        VeryShitAmpoule ampoule = new VeryShitAmpoule(parent);

        ampoule.resetBoundary();

        assertEquals(3, ampoule.getPivotX());
        assertEquals(6, ampoule.getPivotY());
        assertEquals(30, ampoule.getW());
        assertEquals(31, ampoule.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        VeryShitAmpoule ampoule = new VeryShitAmpoule(parent);

        int origPivotX = ampoule.getPivotX();
        int origPivotY = ampoule.getPivotY();

        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());

        ampoule.resetBoundary();

        assertEquals(origPivotX, ampoule.getPivotX());
        assertEquals(origPivotY, ampoule.getPivotY());
    }

    @Test
    public void testToStringUsesResourceUtil() {
        Body parent = createParent(AgeState.BABY);
        VeryShitAmpoule ampoule = new VeryShitAmpoule(parent);

        assertEquals(ResourceUtil.getInstance().read("item_veryshit"), ampoule.toString());
    }

    @Test
    public void testDefaultConstructor() {
        VeryShitAmpoule ampoule = new VeryShitAmpoule();
        assertEquals(0, ampoule.getParent());
    }

    @Test
    public void testConstructorWithParentNotInWorld() {
        Body parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        VeryShitAmpoule ampoule = new VeryShitAmpoule(parent);
        assertEquals(500, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
    }

    private static Body createParent(AgeState ageState) {
        Body parent = new Reimu();
        // 適切な年齢を設定（getBodyAgeState()はageの値から計算される）
        switch (ageState) {
            case BABY:
                parent.setAge(1000);    // BABY: age < 16800
                break;
            case CHILD:
                parent.setAge(20000);   // CHILD: 16800 <= age < 50400
                break;
            case ADULT:
                parent.setAge(60000);   // ADULT: age >= 50400
                break;
        }
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
