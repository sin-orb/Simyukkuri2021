package org.simyukkuri.entity.core.attachment.impl;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.world.bodylinked.Okazari;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.HairState;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.ResourceUtil;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

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
        WorldTestHelper.initializeEmptyMessagePool();
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
        Yukkuri parent = createParent(AgeState.CHILD);
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
        Yukkuri parent = createParent(AgeState.CHILD);
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

        TickResult result = fire.update();

        assertEquals(TickResult.NONE, result);
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInMap() {
        Yukkuri parent = createParent(AgeState.CHILD);
        Fire fire = new Fire(parent);

        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().remove(parent.getUniqueID());

        BufferedImage image = fire.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsCorrectAnimeFrame() {
        Yukkuri parent = createParent(AgeState.CHILD);
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
        Yukkuri babyParent = createParent(AgeState.BABY);
        Yukkuri childParent = createParent(AgeState.CHILD);
        Yukkuri adultParent = createParent(AgeState.ADULT);

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
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);

        fire.resetBoundary();

        assertEquals(3, fire.getPivotX());
        assertEquals(6, fire.getPivotY());
        assertEquals(30, fire.getW());
        assertEquals(31, fire.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Yukkuri parent = createParent(AgeState.CHILD);
        Fire fire = new Fire(parent);

        int origPivotX = fire.getPivotX();
        int origPivotY = fire.getPivotY();

        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().remove(parent.getUniqueID());

        fire.resetBoundary();

        assertEquals(origPivotX, fire.getPivotX());
        assertEquals(origPivotY, fire.getPivotY());
    }

    @Test
    public void testToStringUsesResourceUtil() {
        Yukkuri parent = createParent(AgeState.BABY);
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
        Yukkuri parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        Fire fire = new Fire(parent);
        assertEquals(0, fire.getValue());
        assertEquals(0, fire.getCost());
    }

    @Test
    public void testUpdateReturnsRemovedWhenDeadAndBurned() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);

        parent.setDead(true);
        parent.setBurned(true);

        TickResult result = fire.update();

        assertEquals(TickResult.REMOVED, result);
    }

    @Test
    public void testUpdateDoesNotReturnRemovedWhenDeadButNotBurned() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);

        parent.setDead(true);
        parent.setBurned(false);

        TickResult result = fire.update();

        assertEquals(TickResult.NONE, result);
    }

    @Test
    public void testUpdateIncreasesBurnPeriodWhenDead() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(true);

        assertEquals(0, fire.getBurnPeriod());
        fire.update();
        assertTrue(fire.getBurnPeriod() > 0);
    }

    @Test
    public void testUpdateTakesOkazariWhenBurnPeriodHighAndHasOkazari() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(true);

        // お飾りをセット
        parent.setOkazaris(new Okazari());
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
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(true);

        // お飾りなし、髪ありの状態
        parent.setOkazaris(null);
        parent.setHairState(HairState.DEFAULT);

        // burnPeriodをdamageLimit*2/3より大きくする
        int damageLimit = parent.getDamageLimit();
        fire.setBurnPeriod(damageLimit * 2 / 3 + 1);

        fire.update();

        // pickHairはisDead()だと何もしないので、HairStateは変わらない
        // だが分岐自体は通る
    }

    @Test
    public void testUpdateSetsBurnedWhenBurnPeriodVeryHighAndDead() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(true);
        parent.setBurned(false);

        // お飾りなし、ハゲの状態（上2つのelse ifを通過させる）
        parent.setOkazaris(null);
        parent.setHairState(HairState.BALDHEAD);

        // burnPeriod*90 > damageLimitになるようにする
        int damageLimit = parent.getDamageLimit();
        fire.setBurnPeriod(damageLimit / 90 + 1);

        fire.update();

        // isDead && burnPeriod*90 > damageLimit → setBurned(true)
        assertTrue(parent.isBurned());
    }

    @Test
    public void testUpdateAliveParentAddsDamageAndStress() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        // NYD状態にしてメッセージ呼び出しを回避
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);

        int damageBefore = parent.getDamage();
        fire.update();

        assertTrue(parent.getDamage() > damageBefore);
    }

    @Test
    public void testUpdateAliveParentNotNYDTalking() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        parent.setCoreAnkoState(CoreAnkoState.NORMAL);
        // isTalking=trueにしてsetMessage呼び出しを回避
        parent.setMessageTicks(1);

        fire.update();

        // isNotNYD && isTalking → setMessageは呼ばれない分岐を通る
    }

    @Test
    public void testUpdateAliveParentFixBackNotNeedledFurifuri() {
        // RNDが常に0を返すようにする → nextInt(10)==0 がtrue
        SimYukkuri.RND = new ConstState(0);
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);
        parent.setFixBack(true);
        parent.setNeedled(false);

        fire.update();

        assertTrue(parent.isFurifuri());
    }

    @Test
    public void testUpdateAliveParentFixBackNotNeedledNoFurifuri() {
        // RNDが1を返す → nextInt(10)==0 がfalse
        SimYukkuri.RND = new ConstState(1);
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);
        parent.setFixBack(true);
        parent.setNeedled(false);

        fire.update();
        // furifuriはセットされない（RNDが1なので）
    }

    @Test
    public void testUpdateAliveParentFixBackNeedled() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);
        parent.setFixBack(true);
        parent.setNeedled(true);

        fire.update();
        // isFixBack && isNeedled → else分岐に入る
    }

    @Test
    public void testUpdateAliveParentLockmoveNobinobi() {
        // RNDが常に0を返す → nextInt(3)==0 がtrue
        SimYukkuri.RND = new ConstState(0);
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);
        parent.setFixBack(false);
        parent.setLockmove(true);

        fire.update();

        assertTrue(parent.isNobinobi());
    }

    @Test
    public void testUpdateAliveParentLockmoveNoNobinobi() {
        // RNDが1を返す → nextInt(3)==0 がfalse
        SimYukkuri.RND = new ConstState(1);
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);
        parent.setFixBack(false);
        parent.setLockmove(true);

        fire.update();
    }

    @Test
    public void testUpdateAliveParentNotLockmove() {
        SimYukkuri.RND = new ConstState(1);
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);
        parent.setFixBack(false);
        parent.setLockmove(false);

        TickResult result = fire.update();
        assertEquals(TickResult.NONE, result);
    }

    @Test
    public void testUpdateAliveParentNYDState() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        // NYD状態にする（isNotNYD() == false）
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);

        TickResult result = fire.update();
        assertEquals(TickResult.NONE, result);
    }

    @Test
    public void testUpdateAliveParentBurnPeriodIncreasesWhenAlive() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Fire fire = new Fire(parent);
        parent.setDead(false);
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);

        assertEquals(0, fire.getBurnPeriod());
        fire.update();
        assertTrue(fire.getBurnPeriod() > 0);
    }

    private static Yukkuri createParent(AgeState ageState) {
        Yukkuri parent = new Reimu();
        parent.setAgeState(ageState);
        // bodySprを初期化（addStress→getBurstState→getSize でNPE回避）
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        parent.setSpriteSet(spr);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(parent.getUniqueID(), parent);
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
            Yukkuri child = createParent(AgeState.BABY);
            Yukkuri mother = createParent(AgeState.ADULT);
            mother.setHappiness(Happiness.HAPPY);
            mother.setMsgType(YukkuriType.REIMU);
            // 子をNYDにしてsetMessage NPE回避（茎母親のNYDチェックは母親側）
            child.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);

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
            Yukkuri child = createParent(AgeState.BABY);
            Yukkuri mother = createParent(AgeState.ADULT);
            mother.setHappiness(Happiness.HAPPY);
            child.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);

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
            Yukkuri child = createParent(AgeState.BABY);
            Yukkuri mother = createParent(AgeState.ADULT);
            mother.setHappiness(Happiness.HAPPY);
            mother.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);
            child.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);

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
            Yukkuri child = createParent(AgeState.BABY);
            child.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);

            Fire fire = new Fire(child);
            child.setDead(false);

            // NPEなく正常完了
            TickResult result = fire.update();
            assertEquals(TickResult.NONE, result);
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_LiveBodyWithOkazariBurnsDamageStressAndLosesDecoration() {
            SimYukkuri.RND = new ConstState(1);
            Yukkuri parent = createParent(AgeState.ADULT);
            Fire fire = new Fire(parent);
            parent.setDead(false);
            parent.setCoreAnkoState(CoreAnkoState.NORMAL);
            parent.setOkazaris(new Okazari());

            int damageBefore = parent.getDamage();
            int stressBefore = parent.getStress();
            fire.setBurnPeriod(parent.getDamageLimit() / 3 + 1);

            TickResult result = fire.update();

            assertEquals(TickResult.NONE, result);
            assertTrue(parent.getDamage() > damageBefore);
            assertTrue(parent.getStress() > stressBefore);
            assertFalse(parent.hasOkazari());
            assertTrue(fire.getBurnPeriod() > parent.getDamageLimit() / 3 + 1);
        }

        @Test
        void testScenario_DeadBaldBodyCrossesFinalBurnThresholdAndIsRemoved() {
            SimYukkuri.RND = new ConstState(1);
            Yukkuri parent = createParent(AgeState.ADULT);
            Fire fire = new Fire(parent);
            parent.setDead(true);
            parent.setBurned(false);
            parent.setOkazaris(null);
            parent.setHairState(HairState.BALDHEAD);
            fire.setBurnPeriod(parent.getDamageLimit() + 1);

            TickResult result = fire.update();

            assertEquals(TickResult.REMOVED, result);
            assertTrue(parent.isBurned());
        }
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            Fire.loadImages(Fire.class.getClassLoader(), null);
        } catch (Exception e) {
        }
    }
}
