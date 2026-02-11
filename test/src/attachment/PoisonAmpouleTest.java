package src.attachment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;

import src.ConstState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.draw.World;
import src.enums.AgeState;
import src.enums.Direction;
import src.enums.CriticalDamegeType;
import src.enums.Event;
import src.enums.Happiness;
import src.enums.YukkuriType;
import src.system.MessagePool;
import src.system.ResourceUtil;
import src.system.Sprite;
import src.yukkuri.Reimu;

public class PoisonAmpouleTest {

    private Random originalRnd;

    @BeforeEach
    public void setUp() throws Exception {
        SimYukkuri.world = new World();
        originalRnd = SimYukkuri.RND;
        initMessagePool();
        PoisonAmpoule.setImages(buildImages());
        PoisonAmpoule.setImgW(new int[] { 10, 20, 30 });
        PoisonAmpoule.setImgH(new int[] { 11, 21, 31 });
        PoisonAmpoule.setPivX(new int[] { 1, 2, 3 });
        PoisonAmpoule.setPivY(new int[] { 4, 5, 6 });
    }

    @AfterEach
    public void tearDown() {
        SimYukkuri.RND = originalRnd;
    }

    @Test
    public void testStaticAccessors() {
        assertEquals("PoisonAmpoule", PoisonAmpoule.getPosKey());
        assertEquals(7, PoisonAmpoule.getProperty().length);
        assertEquals(2, PoisonAmpoule.getProperty()[0]); // 赤ゆ用画像サイズ
        assertEquals(2, PoisonAmpoule.getProperty()[1]); // 子ゆ用画像サイズ
        assertEquals(1, PoisonAmpoule.getProperty()[2]); // 成ゆ用画像サイズ
    }

    @Test
    public void testConstructorDefaultsAndBoundary() {
        Body parent = createParent(AgeState.CHILD);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

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
        PoisonAmpoule ampoule = new PoisonAmpoule();

        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsDead() {
        Body parent = createParent(AgeState.ADULT);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        parent.setDead(true);

        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateIncreasesShitWhenAlive() {
        Body parent = createParent(AgeState.ADULT);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        int shitBefore = parent.getShit();

        ampoule.update();

        // うんうんが50増える
        assertTrue(parent.getShit() > shitBefore);
    }

    @Test
    public void testUpdateSetsHappinessToSad() {
        Body parent = createParent(AgeState.ADULT);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        parent.setHappiness(Happiness.VERY_HAPPY);

        ampoule.update();

        // Happinessが変更される（SADまたはVERY_SAD）
        assertTrue(parent.getHappiness() == Happiness.SAD ||
                   parent.getHappiness() == Happiness.VERY_SAD);
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());

        BufferedImage image = ampoule.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsLeftImageWhenDirectionLeft() {
        Body parent = createParent(AgeState.CHILD);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        parent.setDirection(Direction.LEFT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(PoisonAmpoule.getImages()[AgeState.CHILD.ordinal()][0], image);
    }

    @Test
    public void testGetImageReturnsRightImageWhenDirectionRight() {
        Body parent = createParent(AgeState.CHILD);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        parent.setDirection(Direction.RIGHT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(PoisonAmpoule.getImages()[AgeState.CHILD.ordinal()][1], image);
    }

    @Test
    public void testGetImageReturnsCorrectImageForAge() {
        Body babyParent = createParent(AgeState.BABY);
        Body childParent = createParent(AgeState.CHILD);
        Body adultParent = createParent(AgeState.ADULT);

        PoisonAmpoule babyAmpoule = new PoisonAmpoule(babyParent);
        PoisonAmpoule childAmpoule = new PoisonAmpoule(childParent);
        PoisonAmpoule adultAmpoule = new PoisonAmpoule(adultParent);

        babyParent.setDirection(Direction.LEFT);
        childParent.setDirection(Direction.LEFT);
        adultParent.setDirection(Direction.LEFT);

        assertSame(PoisonAmpoule.getImages()[AgeState.BABY.ordinal()][0], babyAmpoule.getImage(babyParent));
        assertSame(PoisonAmpoule.getImages()[AgeState.CHILD.ordinal()][0], childAmpoule.getImage(childParent));
        assertSame(PoisonAmpoule.getImages()[AgeState.ADULT.ordinal()][0], adultAmpoule.getImage(adultParent));
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Body parent = createParent(AgeState.ADULT);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        ampoule.resetBoundary();

        assertEquals(3, ampoule.getPivotX());
        assertEquals(6, ampoule.getPivotY());
        assertEquals(30, ampoule.getW());
        assertEquals(31, ampoule.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

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
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        assertEquals(ResourceUtil.getInstance().read("item_poison"), ampoule.toString());
    }

    @Test
    public void testDefaultConstructor() {
        PoisonAmpoule ampoule = new PoisonAmpoule();
        assertEquals(0, ampoule.getParent());
    }

    @Test
    public void testConstructorWithParentNotInWorld() {
        Body parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);
        assertEquals(500, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
    }

    @Test
    public void testUpdatePoisonDamageWhenRndHits() {
        // RNDが常に0を返す → nextInt(1000)==0 がtrue → 毒ダメージ分岐に入る
        SimYukkuri.RND = new ConstState(0);
        Body parent = createParent(AgeState.ADULT);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);
        parent.setDead(false);

        int damageBefore = parent.getDamage();
        ampoule.update();

        // addDamage(200)が呼ばれてダメージが増加する
        assertTrue(parent.getDamage() > damageBefore);
        assertEquals(Happiness.VERY_SAD, parent.getHappiness());
    }

    @Test
    public void testUpdateNoPoisonDamageWhenRndMisses() {
        // RNDが1を返す → nextInt(1000)==0 がfalse
        SimYukkuri.RND = new ConstState(1);
        Body parent = createParent(AgeState.ADULT);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);
        parent.setDead(false);

        ampoule.update();

        assertEquals(Happiness.SAD, parent.getHappiness());
    }

    @Test
    public void testUpdateDoesNotAddShitWhenCut() {
        Body parent = createParent(AgeState.ADULT);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        parent.setCriticalDamegeType(src.enums.CriticalDamegeType.CUT);

        int shitBefore = parent.getShit();
        ampoule.update();

        // CUT状態ではplusShitが呼ばれない
        assertEquals(shitBefore, parent.getShit());
    }

    private static Body createParent(AgeState ageState) {
        Body parent = new Reimu();
        parent.setAgeState(ageState);
        parent.setMsgType(YukkuriType.REIMU);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        parent.setBodySpr(spr);
        SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);
        return parent;
    }

    @SuppressWarnings("unchecked")
    private static void initMessagePool() throws Exception {
        Field field = MessagePool.class.getDeclaredField("pool_j");
        field.setAccessible(true);
        int len = YukkuriType.values().length;
        HashMap<String, ?>[] pool = new HashMap[len];
        for (int i = 0; i < len; i++) {
            pool[i] = new HashMap<>();
        }
        field.set(null, pool);
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
