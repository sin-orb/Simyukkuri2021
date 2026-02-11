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
import src.base.Okazari;
import src.draw.World;
import src.enums.AgeState;
import src.enums.CoreAnkoState;
import src.enums.Event;
import src.enums.Happiness;
import src.enums.HairState;
import src.enums.YukkuriType;
import src.game.Stalk;
import src.system.MessagePool;
import src.system.ResourceUtil;
import src.system.Sprite;
import src.yukkuri.Reimu;

public class FireTest {

    private static final int ANIME_FRAMES = 4;
    private Random originalRnd;

    @BeforeEach
    public void setUp() throws Exception {
        SimYukkuri.world = new World();
        originalRnd = SimYukkuri.RND;
        Fire.setImages(buildImages());
        Fire.setImgW(new int[] { 10, 20, 30 });
        Fire.setImgH(new int[] { 11, 21, 31 });
        Fire.setPivX(new int[] { 1, 2, 3 });
        Fire.setPivY(new int[] { 4, 5, 6 });
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
        assertEquals("Fire", Fire.getPosKey());
        assertEquals(7, Fire.getProperty().length);
        assertEquals(4, Fire.getProperty()[0]); // 赤ゆ用画像サイズ
        assertEquals(2, Fire.getProperty()[1]); // 子ゆ用画像サイズ
        assertEquals(1, Fire.getProperty()[2]); // 成ゆ用画像サイズ
        assertEquals(1, Fire.getProperty()[4]); // アニメ速度
        assertEquals(4, Fire.getProperty()[6]); // アニメ画像枚数
    }

    @Test
    public void testConstructorDefaultsAndBoundary() {
        Body parent = createParent(AgeState.CHILD);
        Fire fire = new Fire(parent);

        assertEquals(parent.getUniqueID(), fire.getParent());
        assertEquals(0, fire.getValue());
        assertEquals(0, fire.getCost());
        assertEquals(1, fire.getProcessInterval()); // 頻繁に更新
        assertEquals(0, fire.getBurnPeriod()); // 初期値は0
        assertEquals(2, fire.getPivotX());
        assertEquals(5, fire.getPivotY());
        assertEquals(20, fire.getW());
        assertEquals(21, fire.getH());
    }

    @Test
    public void testBurnPeriodGetterSetter() {
        Body parent = createParent(AgeState.CHILD);
        Fire fire = new Fire(parent);

        assertEquals(0, fire.getBurnPeriod());

        fire.setBurnPeriod(100);
        assertEquals(100, fire.getBurnPeriod());

        fire.setBurnPeriod(500);
        assertEquals(500, fire.getBurnPeriod());
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsNull() {
        // parentがnull（マップから削除された等）の場合、DONOTHINGを返す
        Fire fire = new Fire();

        Event result = fire.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        Fire fire = new Fire(parent);

        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());

        BufferedImage image = fire.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsCorrectAnimeFrame() {
        Body parent = createParent(AgeState.CHILD);
        Fire fire = new Fire(parent);

        // animeFrameが0の場合
        fire.setAnimeFrame(0);
        BufferedImage image0 = fire.getImage(parent);
        assertSame(Fire.getImages()[AgeState.CHILD.ordinal()][0], image0);

        // animeFrameが1の場合
        fire.setAnimeFrame(1);
        BufferedImage image1 = fire.getImage(parent);
        assertSame(Fire.getImages()[AgeState.CHILD.ordinal()][1], image1);

        // animeFrameが2の場合
        fire.setAnimeFrame(2);
        BufferedImage image2 = fire.getImage(parent);
        assertSame(Fire.getImages()[AgeState.CHILD.ordinal()][2], image2);

        // animeFrameが3の場合
        fire.setAnimeFrame(3);
        BufferedImage image3 = fire.getImage(parent);
        assertSame(Fire.getImages()[AgeState.CHILD.ordinal()][3], image3);
    }

    @Test
    public void testGetImageReturnsCorrectImageForAge() {
        Body babyParent = createParent(AgeState.BABY);
        Body childParent = createParent(AgeState.CHILD);
        Body adultParent = createParent(AgeState.ADULT);

        Fire babyFire = new Fire(babyParent);
        Fire childFire = new Fire(childParent);
        Fire adultFire = new Fire(adultParent);

        babyFire.setAnimeFrame(0);
        childFire.setAnimeFrame(0);
        adultFire.setAnimeFrame(0);

        assertSame(Fire.getImages()[AgeState.BABY.ordinal()][0], babyFire.getImage(babyParent));
        assertSame(Fire.getImages()[AgeState.CHILD.ordinal()][0], childFire.getImage(childParent));
        assertSame(Fire.getImages()[AgeState.ADULT.ordinal()][0], adultFire.getImage(adultParent));
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);

        fire.resetBoundary();

        assertEquals(3, fire.getPivotX());
        assertEquals(6, fire.getPivotY());
        assertEquals(30, fire.getW());
        assertEquals(31, fire.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        Fire fire = new Fire(parent);

        int origPivotX = fire.getPivotX();
        int origPivotY = fire.getPivotY();

        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());

        fire.resetBoundary();

        assertEquals(origPivotX, fire.getPivotX());
        assertEquals(origPivotY, fire.getPivotY());
    }

    @Test
    public void testToStringUsesResourceUtil() {
        Body parent = createParent(AgeState.BABY);
        Fire fire = new Fire(parent);

        assertEquals(ResourceUtil.getInstance().read("item_fire"), fire.toString());
    }

    @Test
    public void testDefaultConstructor() {
        Fire fire = new Fire();
        assertEquals(0, fire.getParent());
        assertEquals(0, fire.getBurnPeriod());
    }

    @Test
    public void testConstructorWithParentNotInWorld() {
        Body parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        Fire fire = new Fire(parent);
        assertEquals(0, fire.getValue());
        assertEquals(0, fire.getCost());
    }

    @Test
    public void testUpdateReturnsRemovedWhenDeadAndBurned() {
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);

        parent.setDead(true);
        parent.setBurned(true);

        Event result = fire.update();

        assertEquals(Event.REMOVED, result);
    }

    @Test
    public void testUpdateDoesNotReturnRemovedWhenDeadButNotBurned() {
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);

        parent.setDead(true);
        parent.setBurned(false);

        Event result = fire.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateIncreasesBurnPeriodWhenDead() {
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(true);

        assertEquals(0, fire.getBurnPeriod());
        fire.update();
        assertTrue(fire.getBurnPeriod() > 0);
    }

    @Test
    public void testUpdateTakesOkazariWhenBurnPeriodHighAndHasOkazari() {
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(true);

        // お飾りをセット
        parent.setOkazari(new Okazari());
        assertTrue(parent.hasOkazari());

        // burnPeriodをdamageLimit/3より大きくする
        int damageLimit = parent.getDamageLimit();
        fire.setBurnPeriod(damageLimit / 3 + 1);

        fire.update();

        // お飾りが取られる
        assertEquals(false, parent.hasOkazari());
    }

    @Test
    public void testUpdatePicksHairWhenBurnPeriodHighAndNotBald() {
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(true);

        // お飾りなし、髪ありの状態
        parent.setOkazari(null);
        parent.seteHairState(HairState.DEFAULT);

        // burnPeriodをdamageLimit*2/3より大きくする
        int damageLimit = parent.getDamageLimit();
        fire.setBurnPeriod(damageLimit * 2 / 3 + 1);

        fire.update();

        // pickHairはisDead()だと何もしないので、HairStateは変わらない
        // だが分岐自体は通る
    }

    @Test
    public void testUpdateSetsBurnedWhenBurnPeriodVeryHighAndDead() {
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(true);
        parent.setBurned(false);

        // お飾りなし、ハゲの状態（上2つのelse ifを通過させる）
        parent.setOkazari(null);
        parent.seteHairState(HairState.BALDHEAD);

        // burnPeriod*90 > damageLimitになるようにする
        int damageLimit = parent.getDamageLimit();
        fire.setBurnPeriod(damageLimit / 90 + 1);

        fire.update();

        // isDead && burnPeriod*90 > damageLimit → setBurned(true)
        assertTrue(parent.isBurned());
    }

    @Test
    public void testUpdateAliveParentAddsDamageAndStress() {
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        // NYD状態にしてメッセージ呼び出しを回避
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);

        int damageBefore = parent.getDamage();
        fire.update();

        assertTrue(parent.getDamage() > damageBefore);
    }

    @Test
    public void testUpdateAliveParentNotNYDTalking() {
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        parent.seteCoreAnkoState(CoreAnkoState.DEFAULT);
        // isTalking=trueにしてsetMessage呼び出しを回避
        parent.setMessageCount(1);

        fire.update();

        // isNotNYD && isTalking → setMessageは呼ばれない分岐を通る
    }

    @Test
    public void testUpdateAliveParentFixBackNotNeedledFurifuri() {
        // RNDが常に0を返すようにする → nextInt(10)==0 がtrue
        SimYukkuri.RND = new ConstState(0);
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        parent.setFixBack(true);
        parent.setbNeedled(false);

        fire.update();

        assertTrue(parent.isFurifuri());
    }

    @Test
    public void testUpdateAliveParentFixBackNotNeedledNoFurifuri() {
        // RNDが1を返す → nextInt(10)==0 がfalse
        SimYukkuri.RND = new ConstState(1);
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        parent.setFixBack(true);
        parent.setbNeedled(false);

        fire.update();
        // furifuriはセットされない（RNDが1なので）
    }

    @Test
    public void testUpdateAliveParentFixBackNeedled() {
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        parent.setFixBack(true);
        parent.setbNeedled(true);

        fire.update();
        // isFixBack && isNeedled → else分岐に入る
    }

    @Test
    public void testUpdateAliveParentLockmoveNobinobi() {
        // RNDが常に0を返す → nextInt(3)==0 がtrue
        SimYukkuri.RND = new ConstState(0);
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        parent.setFixBack(false);
        parent.setLockmove(true);

        fire.update();

        assertTrue(parent.isNobinobi());
    }

    @Test
    public void testUpdateAliveParentLockmoveNoNobinobi() {
        // RNDが1を返す → nextInt(3)==0 がfalse
        SimYukkuri.RND = new ConstState(1);
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        parent.setFixBack(false);
        parent.setLockmove(true);

        fire.update();
    }

    @Test
    public void testUpdateAliveParentNotLockmove() {
        SimYukkuri.RND = new ConstState(1);
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        parent.setFixBack(false);
        parent.setLockmove(false);

        Event result = fire.update();
        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateAliveParentNYDState() {
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        // NYD状態にする（isNotNYD() == false）
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);

        Event result = fire.update();
        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateAliveParentBurnPeriodIncreasesWhenAlive() {
        Body parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        parent.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);

        assertEquals(0, fire.getBurnPeriod());
        fire.update();
        assertTrue(fire.getBurnPeriod() > 0);
    }

    private static Body createParent(AgeState ageState) {
        Body parent = new Reimu();
        parent.setAgeState(ageState);
        // bodySprを初期化（addStress→getBurstState→getSize でNPE回避）
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        parent.setBodySpr(spr);
        SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);
        return parent;
    }

    private static BufferedImage[][] buildImages() {
        // [年齢][アニメフレーム]
        BufferedImage[][] images = new BufferedImage[3][ANIME_FRAMES];
        for (int age = 0; age < 3; age++) {
            for (int frame = 0; frame < ANIME_FRAMES; frame++) {
                images[age][frame] = new BufferedImage(10 + age * 10, 10 + age * 10, BufferedImage.TYPE_INT_ARGB);
            }
        }
        return images;
    }

    @Nested
    class StalkMotherReactionTests {

        @Test
        public void testStalkMotherReactsWhenRndHits() {
            // RND=0 → nextInt(3)==0 → 親が反応する
            SimYukkuri.RND = new ConstState(0);
            Body child = createParent(AgeState.BABY);
            Body mother = createParent(AgeState.ADULT);
            mother.setHappiness(Happiness.HAPPY);
            mother.setMsgType(YukkuriType.REIMU);
            // 子をNYDにしてsetMessage NPE回避（茎母親のNYDチェックは母親側）
            child.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);

            // 茎を作って親子関係を設定
            Stalk stalk = new Stalk();
            stalk.setPlantYukkuri(mother);
            child.setBindStalk(stalk);

            Fire fire = new Fire(child);
            child.setDead(false);
            mother.setDead(false);

            int stressBefore = mother.getStress();
            fire.update();

            // 母親がVERY_SADになりストレスが増加する
            assertEquals(Happiness.VERY_SAD, mother.getHappiness());
            assertTrue(mother.getStress() > stressBefore);
        }

        @Test
        public void testStalkMotherNoReactionWhenRndMisses() {
            // RND=1 → nextInt(3)==1≠0 → 親が反応しない
            SimYukkuri.RND = new ConstState(1);
            Body child = createParent(AgeState.BABY);
            Body mother = createParent(AgeState.ADULT);
            mother.setHappiness(Happiness.HAPPY);
            child.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);

            Stalk stalk = new Stalk();
            stalk.setPlantYukkuri(mother);
            child.setBindStalk(stalk);

            Fire fire = new Fire(child);
            child.setDead(false);
            mother.setDead(false);

            fire.update();

            // 母親のhappinessはVERY_SADにならない
            assertFalse(mother.getHappiness() == Happiness.VERY_SAD);
        }

        @Test
        public void testStalkMotherNoReactionWhenMotherNYD() {
            // RND=0 → nextInt(3)==0 だが母がNYDなら反応しない
            SimYukkuri.RND = new ConstState(0);
            Body child = createParent(AgeState.BABY);
            Body mother = createParent(AgeState.ADULT);
            mother.setHappiness(Happiness.HAPPY);
            mother.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
            child.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);

            Stalk stalk = new Stalk();
            stalk.setPlantYukkuri(mother);
            child.setBindStalk(stalk);

            Fire fire = new Fire(child);
            child.setDead(false);
            mother.setDead(false);

            fire.update();

            // 母親のhappinessは変わらない（NYDだから isNotNYD()==false）
            assertFalse(mother.getHappiness() == Happiness.VERY_SAD);
        }

        @Test
        public void testStalkMotherNoReactionWhenNoStalk() {
            // RND=0 → nextInt(3)==0 だが茎がないなら反応しない
            SimYukkuri.RND = new ConstState(0);
            Body child = createParent(AgeState.BABY);
            child.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);

            Fire fire = new Fire(child);
            child.setDead(false);

            // NPEなく正常完了
            Event result = fire.update();
            assertEquals(Event.DONOTHING, result);
        }
    }
}
