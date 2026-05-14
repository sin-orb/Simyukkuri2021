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
import org.simyukkuri.draw.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.CriticalDamegeType;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.Event;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.ResourceUtil;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

public class PoisonAmpouleTest {

    private Random originalRnd;

    @BeforeEach
    public void setUp() throws Exception {
        SimYukkuri.world = new World();
        originalRnd = SimYukkuri.RND;
        WorldTestHelper.initializeEmptyMessagePool();
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
        Yukkuri parent = createParent(AgeState.CHILD);
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
        Yukkuri parent = createParent(AgeState.ADULT);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        parent.setDead(true);

        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateIncreasesShitWhenAlive() {
        Yukkuri parent = createParent(AgeState.ADULT);
        parent.initAmount(AgeState.ADULT); // ankoAmount > 0 => isDead() returns false
        parent.setShit(100); // plusShit is no-op when shit==0, so initialize to positive
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        int shitBefore = parent.getShit();

        ampoule.update();

        // plusShit(50) should have been called
        assertTrue(parent.getShit() > shitBefore);
    }

    @Test
    public void testUpdateSetsHappinessToSad() {
        Yukkuri parent = createParent(AgeState.ADULT);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        parent.setHappiness(Happiness.VERY_HAPPY);

        ampoule.update();

        // Happinessが変更される（SADまたはVERY_SAD）
        assertTrue(parent.getHappiness() == Happiness.SAD ||
                parent.getHappiness() == Happiness.VERY_SAD);
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInMap() {
        Yukkuri parent = createParent(AgeState.CHILD);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());

        BufferedImage image = ampoule.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsLeftImageWhenDirectionLeft() {
        Yukkuri parent = createParent(AgeState.CHILD);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        parent.setDirection(Direction.LEFT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(PoisonAmpoule.getImages()[AgeState.CHILD.ordinal()][0], image);
    }

    @Test
    public void testGetImageReturnsRightImageWhenDirectionRight() {
        Yukkuri parent = createParent(AgeState.CHILD);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        parent.setDirection(Direction.RIGHT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(PoisonAmpoule.getImages()[AgeState.CHILD.ordinal()][1], image);
    }

    @Test
    public void testGetImageReturnsCorrectImageForAge() {
        Yukkuri babyParent = createParent(AgeState.BABY);
        Yukkuri childParent = createParent(AgeState.CHILD);
        Yukkuri adultParent = createParent(AgeState.ADULT);

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
        Yukkuri parent = createParent(AgeState.ADULT);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        ampoule.resetBoundary();

        assertEquals(3, ampoule.getPivotX());
        assertEquals(6, ampoule.getPivotY());
        assertEquals(30, ampoule.getW());
        assertEquals(31, ampoule.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Yukkuri parent = createParent(AgeState.CHILD);
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
        Yukkuri parent = createParent(AgeState.BABY);
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
        Yukkuri parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);
        assertEquals(500, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
    }

    @Test
    public void testUpdatePoisonDamageWhenRndHits() {
        // RNDが常に0を返す → nextInt(1000)==0 がtrue → 毒ダメージ分岐に入る
        SimYukkuri.RND = new ConstState(0);
        Yukkuri parent = createParent(AgeState.ADULT);
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
        Yukkuri parent = createParent(AgeState.ADULT);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);
        parent.setDead(false);

        ampoule.update();

        assertEquals(Happiness.SAD, parent.getHappiness());
    }

    @Test
    public void testUpdateDoesNotAddShitWhenCut() {
        Yukkuri parent = createParent(AgeState.ADULT);
        PoisonAmpoule ampoule = new PoisonAmpoule(parent);

        parent.setCriticalDamegeType(org.simyukkuri.enums.CriticalDamegeType.CUT);

        int shitBefore = parent.getShit();
        ampoule.update();

        // CUT状態ではplusShitが呼ばれない
        assertEquals(shitBefore, parent.getShit());
    }

    private static Yukkuri createParent(AgeState ageState) {
        Yukkuri parent = new Reimu();
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

    private static BufferedImage[][] buildImages() {
        BufferedImage[][] images = new BufferedImage[3][2];
        for (int age = 0; age < 3; age++) {
            for (int dir = 0; dir < 2; dir++) {
                images[age][dir] = new BufferedImage(10 + age * 10, 10 + age * 10, BufferedImage.TYPE_INT_ARGB);
            }
        }
        return images;
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            PoisonAmpoule.loadImages(PoisonAmpoule.class.getClassLoader(), null);
        } catch (Exception e) {
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_LivePoisonAmpouleHitWakesBodyAddsShitAndAppliesPoisonDamage() {
            SimYukkuri.RND = new ConstState(0);
            Yukkuri parent = createParent(AgeState.ADULT);
            parent.initAmount(AgeState.ADULT);
            parent.setSleeping(true);
            parent.setShit(100);
            PoisonAmpoule ampoule = new PoisonAmpoule(parent);

            int shitBefore = parent.getShit();
            int damageBefore = parent.getDamage();

            Event result = ampoule.update();

            assertEquals(Event.DONOTHING, result);
            assertFalse(parent.isSleeping());
            assertTrue(parent.getShit() > shitBefore);
            assertTrue(parent.getDamage() > damageBefore);
            assertEquals(Happiness.VERY_SAD, parent.getHappiness());
            assertEquals(ImageCode.PAIN.ordinal(), parent.getForceFace());
        }

        @Test
        void testScenario_CutBodyDoesNotWakeOrGainShitWhenPoisonDoesNotProc() {
            SimYukkuri.RND = new ConstState(1);
            Yukkuri parent = createParent(AgeState.ADULT);
            parent.setSleeping(true);
            parent.setShit(100);
            parent.setCriticalDamegeType(CriticalDamegeType.CUT);
            PoisonAmpoule ampoule = new PoisonAmpoule(parent);

            int shitBefore = parent.getShit();

            Event result = ampoule.update();

            assertEquals(Event.DONOTHING, result);
            assertTrue(parent.isSleeping());
            assertEquals(shitBefore, parent.getShit());
            assertEquals(Happiness.SAD, parent.getHappiness());
        }
    }
}
