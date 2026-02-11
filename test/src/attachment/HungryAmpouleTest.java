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

public class HungryAmpouleTest {

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        HungryAmpoule.setImages(buildImages());
        HungryAmpoule.setImgW(new int[] { 10, 20, 30 });
        HungryAmpoule.setImgH(new int[] { 11, 21, 31 });
        HungryAmpoule.setPivX(new int[] { 1, 2, 3 });
        HungryAmpoule.setPivY(new int[] { 4, 5, 6 });
    }

    @Test
    public void testStaticAccessors() {
        assertEquals("HungryAmpoule", HungryAmpoule.getPosKey());
        assertEquals(7, HungryAmpoule.getProperty().length);
        assertEquals(2, HungryAmpoule.getProperty()[0]); // 赤ゆ用画像サイズ
        assertEquals(2, HungryAmpoule.getProperty()[1]); // 子ゆ用画像サイズ
        assertEquals(1, HungryAmpoule.getProperty()[2]); // 成ゆ用画像サイズ
    }

    @Test
    public void testConstructorDefaultsAndBoundary() {
        Body parent = createParent(AgeState.CHILD);
        HungryAmpoule ampoule = new HungryAmpoule(parent);

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
        HungryAmpoule ampoule = new HungryAmpoule();

        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateReducesHungryWhenNotEating() {
        Body parent = createParent(AgeState.ADULT);
        HungryAmpoule ampoule = new HungryAmpoule(parent);

        // 満腹状態に設定
        parent.setHungry(10000);
        parent.setEating(false);

        int hungryBefore = parent.getHungry();
        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
        // hungryが減少している（TICK * 1000 減少）
        assertEquals(hungryBefore - 1000, parent.getHungry());
    }

    @Test
    public void testUpdateDoesNotReduceHungryWhenEating() {
        Body parent = createParent(AgeState.ADULT);
        HungryAmpoule ampoule = new HungryAmpoule(parent);

        parent.setHungry(10000);
        parent.setEating(true);

        int hungryBefore = parent.getHungry();
        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
        // 食事中なのでhungryは変わらない
        assertEquals(hungryBefore, parent.getHungry());
    }

    @Test
    public void testUpdateClampsHungryToZero() {
        Body parent = createParent(AgeState.ADULT);
        HungryAmpoule ampoule = new HungryAmpoule(parent);

        // hungryを低い値に設定
        parent.setHungry(500);
        parent.setEating(false);

        ampoule.update();

        // 0未満にはならない
        assertEquals(0, parent.getHungry());
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        HungryAmpoule ampoule = new HungryAmpoule(parent);

        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());

        BufferedImage image = ampoule.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsLeftImageWhenDirectionLeft() {
        Body parent = createParent(AgeState.CHILD);
        HungryAmpoule ampoule = new HungryAmpoule(parent);

        parent.setDirection(Direction.LEFT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(HungryAmpoule.getImages()[AgeState.CHILD.ordinal()][0], image);
    }

    @Test
    public void testGetImageReturnsRightImageWhenDirectionRight() {
        Body parent = createParent(AgeState.CHILD);
        HungryAmpoule ampoule = new HungryAmpoule(parent);

        parent.setDirection(Direction.RIGHT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(HungryAmpoule.getImages()[AgeState.CHILD.ordinal()][1], image);
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Body parent = createParent(AgeState.ADULT);
        HungryAmpoule ampoule = new HungryAmpoule(parent);

        ampoule.resetBoundary();

        assertEquals(3, ampoule.getPivotX());
        assertEquals(6, ampoule.getPivotY());
        assertEquals(30, ampoule.getW());
        assertEquals(31, ampoule.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        HungryAmpoule ampoule = new HungryAmpoule(parent);

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
        HungryAmpoule ampoule = new HungryAmpoule(parent);

        assertEquals(ResourceUtil.getInstance().read("item_hungry"), ampoule.toString());
    }

    @Test
    public void testDefaultConstructor() {
        HungryAmpoule ampoule = new HungryAmpoule();
        assertEquals(0, ampoule.getParent());
    }

    @Test
    public void testConstructorWithParentNotInWorld() {
        Body parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        HungryAmpoule ampoule = new HungryAmpoule(parent);
        assertEquals(500, ampoule.getValue());
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
