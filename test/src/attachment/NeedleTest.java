package src.attachment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;

import src.ConstState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;

import src.SimYukkuri;
import src.base.Body;
import src.draw.World;
import src.enums.AgeState;
import src.enums.CoreAnkoState;
import src.enums.Direction;
import src.enums.Event;
import src.enums.Happiness;
import src.enums.YukkuriType;
import src.game.Stalk;
import src.system.MessagePool;
import src.system.ResourceUtil;
import src.system.Sprite;
import src.yukkuri.Reimu;

public class NeedleTest {

    private Random originalRnd;

    @BeforeEach
    public void setUp() throws Exception {
        SimYukkuri.world = new World();
        originalRnd = SimYukkuri.RND;
        Needle.setImages(buildImages());
        Needle.setImgW(new int[] { 10, 20, 30 });
        Needle.setImgH(new int[] { 11, 21, 31 });
        Needle.setPivX(new int[] { 1, 2, 3 });
        Needle.setPivY(new int[] { 4, 5, 6 });
        initMessagePool();
    }

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

    @AfterEach
    public void tearDown() {
        SimYukkuri.RND = originalRnd;
    }

    @Test
    public void testStaticAccessors() {
        assertEquals("Needle", Needle.getPosKey());
        assertEquals("Needle_In_Anal", Needle.getPosKeyInAnal());
        assertEquals(7, Needle.getProperty().length);
        assertEquals(3, Needle.getProperty()[0]); // 赤ゆ用画像サイズ
        assertEquals(2, Needle.getProperty()[1]); // 子ゆ用画像サイズ
        assertEquals(1, Needle.getProperty()[2]); // 成ゆ用画像サイズ
    }

    @Test
    public void testConstructorDefaultsAndBoundary() {
        Body parent = createParent(AgeState.CHILD);
        Needle needle = new Needle(parent);

        assertEquals(parent.getUniqueID(), needle.getParent());
        assertEquals(0, needle.getValue());
        assertEquals(0, needle.getCost());
        assertEquals(1, needle.getProcessInterval()); // 頻繁に更新
        assertEquals(2, needle.getPivotX());
        assertEquals(5, needle.getPivotY());
        assertEquals(20, needle.getW());
        assertEquals(21, needle.getH());
    }

    @Test
    public void testConstructorSetsFixBackWhenFurifuri() {
        Body parent = createParent(AgeState.ADULT);
        parent.setFurifuri(true);

        new Needle(parent);

        assertTrue(parent.isFixBack());
    }

    @Test
    public void testConstructorSetsFixBackWhenShitting() {
        Body parent = createParent(AgeState.ADULT);
        parent.setShitting(true);

        new Needle(parent);

        assertTrue(parent.isFixBack());
    }

    @Test
    public void testConstructorSetsFixBackWhenBirth() {
        Body parent = createParent(AgeState.ADULT);
        parent.setBirth(true);

        new Needle(parent);

        assertTrue(parent.isFixBack());
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsNull() {
        Needle needle = new Needle();

        Event result = needle.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateClearsFixBackWhenParentIsDead() {
        Body parent = createParent(AgeState.ADULT);
        parent.setFixBack(true);
        parent.setDead(true);
        Needle needle = new Needle(parent);

        needle.update();

        // 死んでいる場合、FixBackが解除される
        assertEquals(false, parent.isFixBack());
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        Needle needle = new Needle(parent);

        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());

        BufferedImage image = needle.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsLeftImageWhenDirectionLeft() {
        Body parent = createParent(AgeState.CHILD);
        Needle needle = new Needle(parent);

        parent.setDirection(Direction.LEFT);
        BufferedImage image = needle.getImage(parent);

        assertSame(Needle.getImages()[AgeState.CHILD.ordinal()][0], image);
    }

    @Test
    public void testGetImageReturnsRightImageWhenDirectionRight() {
        Body parent = createParent(AgeState.CHILD);
        Needle needle = new Needle(parent);

        parent.setDirection(Direction.RIGHT);
        BufferedImage image = needle.getImage(parent);

        assertSame(Needle.getImages()[AgeState.CHILD.ordinal()][1], image);
    }

    @Test
    public void testGetImageReturnsCorrectImageForAge() {
        Body babyParent = createParent(AgeState.BABY);
        Body childParent = createParent(AgeState.CHILD);
        Body adultParent = createParent(AgeState.ADULT);

        Needle babyNeedle = new Needle(babyParent);
        Needle childNeedle = new Needle(childParent);
        Needle adultNeedle = new Needle(adultParent);

        babyParent.setDirection(Direction.LEFT);
        childParent.setDirection(Direction.LEFT);
        adultParent.setDirection(Direction.LEFT);

        assertSame(Needle.getImages()[AgeState.BABY.ordinal()][0], babyNeedle.getImage(babyParent));
        assertSame(Needle.getImages()[AgeState.CHILD.ordinal()][0], childNeedle.getImage(childParent));
        assertSame(Needle.getImages()[AgeState.ADULT.ordinal()][0], adultNeedle.getImage(adultParent));
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Body parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);

        needle.resetBoundary();

        assertEquals(3, needle.getPivotX());
        assertEquals(6, needle.getPivotY());
        assertEquals(30, needle.getW());
        assertEquals(31, needle.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        Needle needle = new Needle(parent);

        int origPivotX = needle.getPivotX();
        int origPivotY = needle.getPivotY();

        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());

        needle.resetBoundary();

        assertEquals(origPivotX, needle.getPivotX());
        assertEquals(origPivotY, needle.getPivotY());
    }

    @Test
    public void testToStringUsesResourceUtil() {
        Body parent = createParent(AgeState.BABY);
        Needle needle = new Needle(parent);

        assertEquals(ResourceUtil.getInstance().read("item_needle"), needle.toString());
    }

    @Test
    public void testDefaultConstructor() {
        Needle needle = new Needle();
        assertEquals(0, needle.getParent());
    }

    @Test
    public void testConstructorWithParentNotInWorld() {
        Body parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        Needle needle = new Needle(parent);
        assertEquals(0, needle.getValue());
        assertEquals(0, needle.getCost());
    }

    @Test
    public void testUpdateAliveParentAddsDamageAndStress() {
        Body parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);

        int damageBefore = parent.getDamage();
        needle.update();

        assertTrue(parent.getDamage() > damageBefore);
    }

    @Test
    public void testUpdateAliveParentWakesUpWhenSleeping() {
        Body parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        parent.setSleeping(true);

        needle.update();

        assertEquals(false, parent.isSleeping());
    }

    @Test
    public void testUpdateAliveParentNotSleeping() {
        Body parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        parent.setSleeping(false);

        needle.update();
    }

    @Test
    public void testUpdateAliveParentFixBackSetsDirectionLeft() {
        Body parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        parent.setFixBack(true);
        parent.setDirection(Direction.RIGHT);

        needle.update();

        assertEquals(Direction.LEFT, parent.getDirection());
    }

    @Test
    public void testUpdateAliveParentNotFixBack() {
        Body parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        parent.setFixBack(false);

        needle.update();
    }

    @Test
    public void testUpdateAliveParentNotNYDFixBackTalking() {
        Body parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.setFixBack(true);
        parent.seteCoreAnkoState(CoreAnkoState.DEFAULT);
        // isTalking=trueにしてsetMessage呼び出し回避
        parent.setMessageCount(1);

        needle.update();
        // isNotNYD && isFixBack && isTalking の分岐を通る
    }

    @Test
    public void testUpdateAliveParentNotNYDNotFixBackTalking() {
        Body parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.setFixBack(false);
        parent.seteCoreAnkoState(CoreAnkoState.DEFAULT);
        parent.setMessageCount(1);

        needle.update();
        // isNotNYD && !isFixBack && isTalking の分岐を通る
    }

    @Test
    public void testUpdateAliveParentNYDState() {
        Body parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);

        Event result = needle.update();
        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateAliveParentPurupuru() {
        // RNDが常に0を返す → nextInt(50)==0 がtrue → stayPurupuru
        SimYukkuri.RND = new ConstState(0);
        Body parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);

        needle.update();

        assertTrue(parent.isPurupuru());
    }

    @Test
    public void testUpdateAliveParentNoPurupuru() {
        // RNDが1を返す → nextInt(50)==0 がfalse
        SimYukkuri.RND = new ConstState(1);
        Body parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);

        needle.update();
    }

    private static Body createParent(AgeState ageState) {
        Body parent = new Reimu();
        parent.setAgeState(ageState);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        parent.setBodySpr(spr);
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

    @Nested
    class StalkMotherReactionTests {

        @Test
        public void testStalkMotherReactsWhenRndHits() {
            // RND=0 → nextInt(50)==0 → checkReactionStalkMother(ATTAKED)
            SimYukkuri.RND = new ConstState(0);
            Body child = createParent(AgeState.BABY);
            Body mother = createParent(AgeState.ADULT);
            mother.setHappiness(Happiness.HAPPY);
            mother.setMsgType(YukkuriType.REIMU);
            // 子をNYDにしてsetMessage NPE回避
            child.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);

            Stalk stalk = new Stalk();
            stalk.setPlantYukkuri(mother);
            child.setBindStalk(stalk);

            Needle needle = new Needle(child);
            child.setDead(false);
            mother.setDead(false);

            int stressBefore = mother.getStress();
            needle.update();

            // 母親がSADになりストレスが増加する（checkReactionStalkMother ATTAKED）
            assertTrue(mother.getStress() > stressBefore);
        }

        @Test
        public void testStalkMotherNoReactionWhenRndMisses() {
            // RND=1 → nextInt(50)==1≠0 → 親が反応しない
            SimYukkuri.RND = new ConstState(1);
            Body child = createParent(AgeState.BABY);
            Body mother = createParent(AgeState.ADULT);
            mother.setHappiness(Happiness.HAPPY);
            child.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);

            Stalk stalk = new Stalk();
            stalk.setPlantYukkuri(mother);
            child.setBindStalk(stalk);

            Needle needle = new Needle(child);
            child.setDead(false);
            mother.setDead(false);

            int stressBefore = mother.getStress();
            needle.update();

            // 母親のストレスは針自体のダメージでは増えない（反応しない）
            assertEquals(stressBefore, mother.getStress());
        }

        @Test
        public void testStalkMotherNoReactionWhenNoStalk() {
            // RND=0 → nextInt(50)==0 だが茎がないなら反応しない
            SimYukkuri.RND = new ConstState(0);
            Body child = createParent(AgeState.BABY);
            child.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);

            Needle needle = new Needle(child);
            child.setDead(false);

            // NPEなく正常完了
            Event result = needle.update();
            assertEquals(Event.DONOTHING, result);
        }
    }
}
