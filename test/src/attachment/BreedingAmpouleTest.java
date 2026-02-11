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

public class BreedingAmpouleTest {

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        BreedingAmpoule.setImages(buildImages());
        BreedingAmpoule.setImgW(new int[] { 10, 20, 30 });
        BreedingAmpoule.setImgH(new int[] { 11, 21, 31 });
        BreedingAmpoule.setPivX(new int[] { 1, 2, 3 });
        BreedingAmpoule.setPivY(new int[] { 4, 5, 6 });
    }

    @Test
    public void testStaticAccessors() {
        // 注: POS_KEYは"AccelAmpoule"になっている（おそらくコピペミス）
        assertEquals("AccelAmpoule", BreedingAmpoule.getPosKey());
        assertEquals(7, BreedingAmpoule.getProperty().length);
        assertEquals(2, BreedingAmpoule.getProperty()[0]); // 赤ゆ用画像サイズ
        assertEquals(2, BreedingAmpoule.getProperty()[1]); // 子ゆ用画像サイズ
        assertEquals(1, BreedingAmpoule.getProperty()[2]); // 成ゆ用画像サイズ
    }

    @Test
    public void testConstructorDefaultsAndBoundary() {
        Body parent = createParent(AgeState.CHILD);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        assertEquals(parent.getUniqueID(), ampoule.getParent());
        assertEquals(1000, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
        assertEquals(2, ampoule.getPivotX());
        assertEquals(5, ampoule.getPivotY());
        assertEquals(20, ampoule.getW());
        assertEquals(21, ampoule.getH());
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsNull() {
        // parentがnull（マップから削除された等）の場合、DONOTHINGを返す
        BreedingAmpoule ampoule = new BreedingAmpoule();

        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsDead() {
        Body parent = createParent(AgeState.ADULT);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        parent.setDead(true);

        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsBurned() {
        Body parent = createParent(AgeState.ADULT);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        parent.setBurned(true);

        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsCrushed() {
        Body parent = createParent(AgeState.ADULT);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        parent.setCrushed(true);

        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());

        BufferedImage image = ampoule.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsLeftImageWhenDirectionLeft() {
        Body parent = createParent(AgeState.CHILD);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        parent.setDirection(Direction.LEFT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(BreedingAmpoule.getImages()[AgeState.CHILD.ordinal()][0], image);
    }

    @Test
    public void testGetImageReturnsRightImageWhenDirectionRight() {
        Body parent = createParent(AgeState.CHILD);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        parent.setDirection(Direction.RIGHT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(BreedingAmpoule.getImages()[AgeState.CHILD.ordinal()][1], image);
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Body parent = createParent(AgeState.ADULT);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        ampoule.resetBoundary();

        assertEquals(3, ampoule.getPivotX());
        assertEquals(6, ampoule.getPivotY());
        assertEquals(30, ampoule.getW());
        assertEquals(31, ampoule.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

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
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        assertEquals(ResourceUtil.getInstance().read("item_breeding"), ampoule.toString());
    }

    @Test
    public void testDefaultConstructor() {
        BreedingAmpoule ampoule = new BreedingAmpoule();
        assertEquals(0, ampoule.getParent());
    }

    @Test
    public void testConstructorWithParentNotInWorld() {
        Body parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);
        assertEquals(1000, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
    }

    @Test
    public void testUpdateBreedsWhenParentIsAliveAndNotDisabled() {
        Body parent = createParent(AgeState.ADULT);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        parent.setDead(false);
        parent.setBurned(false);
        parent.setCrushed(false);

        int babyTypesBefore = parent.getBabyTypes().size();
        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
        assertEquals(100, parent.getHungry());
        assertTrue(parent.isHasBaby());
        assertEquals(babyTypesBefore + 1, parent.getBabyTypes().size());
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsBodyCastrated() {
        Body parent = createParent(AgeState.ADULT);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        parent.setBodyCastration(true);

        int babyTypesBefore = parent.getBabyTypes().size();
        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
        assertEquals(babyTypesBefore, parent.getBabyTypes().size());
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
