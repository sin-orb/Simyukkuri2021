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
import src.enums.Direction;
import src.enums.Event;
import src.system.ResourceUtil;
import src.yukkuri.Reimu;

public class StopAmpouleTest {

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        StopAmpoule.setImages(buildImages());
        StopAmpoule.setImgW(new int[] { 10, 20, 30 });
        StopAmpoule.setImgH(new int[] { 11, 21, 31 });
        StopAmpoule.setPivX(new int[] { 1, 2, 3 });
        StopAmpoule.setPivY(new int[] { 4, 5, 6 });
    }

    @Test
    public void testStaticAccessors() {
        assertEquals("StopAmpoule", StopAmpoule.getPosKey());
        assertEquals(7, StopAmpoule.getProperty().length);
        assertEquals(2, StopAmpoule.getProperty()[0]); // 赤ゆ用画像サイズ
        assertEquals(2, StopAmpoule.getProperty()[1]); // 子ゆ用画像サイズ
        assertEquals(1, StopAmpoule.getProperty()[2]); // 成ゆ用画像サイズ
    }

    @Test
    public void testConstructorDefaultsAndBoundary() {
        Body parent = createParent(AgeState.CHILD);
        StopAmpoule ampoule = new StopAmpoule(parent);

        assertEquals(parent.getUniqueID(), ampoule.getParent());
        assertEquals(1000, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
        assertEquals(100, ampoule.getProcessInterval());
        assertEquals(2, ampoule.getPivotX());
        assertEquals(5, ampoule.getPivotY());
        assertEquals(20, ampoule.getW());
        assertEquals(21, ampoule.getH());
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsNull() {
        StopAmpoule ampoule = new StopAmpoule();

        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateReducesAgeWhenNotAdult() {
        Body parent = createParent(AgeState.CHILD);
        StopAmpoule ampoule = new StopAmpoule(parent);

        // CHILDの範囲内のageを設定（16800 <= age < 50400）
        parent.setAge(20000);
        long ageBefore = parent.getAge();

        ampoule.update();

        // 成体でなければageが100減る
        assertTrue(parent.getAge() < ageBefore);
        assertEquals(ageBefore - 100, parent.getAge());
    }

    @Test
    public void testUpdateReducesAgeWhenBaby() {
        Body parent = createParent(AgeState.BABY);
        StopAmpoule ampoule = new StopAmpoule(parent);

        // BABYの範囲内のageを設定（age < 16800、かつaddAgeは0未満にならない）
        parent.setAge(1000);
        long ageBefore = parent.getAge();

        ampoule.update();

        // 赤ゆの場合もageが100減る
        assertEquals(ageBefore - 100, parent.getAge());
    }

    @Test
    public void testUpdateDoesNotReduceAgeWhenAdult() {
        Body parent = createParent(AgeState.ADULT);
        StopAmpoule ampoule = new StopAmpoule(parent);

        // ADULTの範囲内のageを設定（age >= 50400）
        parent.setAge(60000);
        long ageBefore = parent.getAge();

        ampoule.update();

        // 成体の場合はageが変わらない
        assertEquals(ageBefore, parent.getAge());
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        StopAmpoule ampoule = new StopAmpoule(parent);

        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());

        BufferedImage image = ampoule.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsLeftImageWhenDirectionLeft() {
        Body parent = createParent(AgeState.CHILD);
        StopAmpoule ampoule = new StopAmpoule(parent);

        parent.setDirection(Direction.LEFT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(StopAmpoule.getImages()[AgeState.CHILD.ordinal()][0], image);
    }

    @Test
    public void testGetImageReturnsRightImageWhenDirectionRight() {
        Body parent = createParent(AgeState.CHILD);
        StopAmpoule ampoule = new StopAmpoule(parent);

        parent.setDirection(Direction.RIGHT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(StopAmpoule.getImages()[AgeState.CHILD.ordinal()][1], image);
    }

    @Test
    public void testGetImageReturnsCorrectImageForAge() {
        Body babyParent = createParent(AgeState.BABY);
        Body childParent = createParent(AgeState.CHILD);
        Body adultParent = createParent(AgeState.ADULT);

        StopAmpoule babyAmpoule = new StopAmpoule(babyParent);
        StopAmpoule childAmpoule = new StopAmpoule(childParent);
        StopAmpoule adultAmpoule = new StopAmpoule(adultParent);

        babyParent.setDirection(Direction.LEFT);
        childParent.setDirection(Direction.LEFT);
        adultParent.setDirection(Direction.LEFT);

        assertSame(StopAmpoule.getImages()[AgeState.BABY.ordinal()][0], babyAmpoule.getImage(babyParent));
        assertSame(StopAmpoule.getImages()[AgeState.CHILD.ordinal()][0], childAmpoule.getImage(childParent));
        assertSame(StopAmpoule.getImages()[AgeState.ADULT.ordinal()][0], adultAmpoule.getImage(adultParent));
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Body parent = createParent(AgeState.ADULT);
        StopAmpoule ampoule = new StopAmpoule(parent);

        ampoule.resetBoundary();

        assertEquals(3, ampoule.getPivotX());
        assertEquals(6, ampoule.getPivotY());
        assertEquals(30, ampoule.getW());
        assertEquals(31, ampoule.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        StopAmpoule ampoule = new StopAmpoule(parent);

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
        StopAmpoule ampoule = new StopAmpoule(parent);

        assertEquals(ResourceUtil.getInstance().read("item_stop"), ampoule.toString());
    }

    @Test
    public void testDefaultConstructor() {
        StopAmpoule ampoule = new StopAmpoule();
        assertEquals(0, ampoule.getParent());
    }

    @Test
    public void testConstructorWithParentNotInWorld() {
        Body parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        StopAmpoule ampoule = new StopAmpoule(parent);
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
