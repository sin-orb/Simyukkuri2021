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
import org.simyukkuri.SequenceRNG;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.ResourceUtil;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

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
        WorldTestHelper.initializeEmptyMessagePool();
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
        Yukkuri parent = createParent(AgeState.CHILD);
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
        Yukkuri parent = createParent(AgeState.ADULT);
        parent.setFurifuri(true);

        new Needle(parent);

        assertTrue(parent.isFixBack());
    }

    @Test
    public void testConstructorSetsFixBackWhenShitting() {
        Yukkuri parent = createParent(AgeState.ADULT);
        parent.setShitting(true);

        new Needle(parent);

        assertTrue(parent.isFixBack());
    }

    @Test
    public void testConstructorSetsFixBackWhenBirth() {
        Yukkuri parent = createParent(AgeState.ADULT);
        parent.setBirth(true);

        new Needle(parent);

        assertTrue(parent.isFixBack());
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsNull() {
        Needle needle = new Needle();

        TickResult result = needle.update();

        assertEquals(TickResult.NONE, result);
    }

    @Test
    public void testUpdateClearsFixBackWhenParentIsDead() {
        Yukkuri parent = createParent(AgeState.ADULT);
        parent.setFixBack(true);
        parent.setDead(true);
        Needle needle = new Needle(parent);

        needle.update();

        // 死んでいる場合、FixBackが解除される
        assertEquals(false, parent.isFixBack());
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInMap() {
        Yukkuri parent = createParent(AgeState.CHILD);
        Needle needle = new Needle(parent);

        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().remove(parent.getUniqueID());

        BufferedImage image = needle.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsLeftImageWhenDirectionLeft() {
        Yukkuri parent = createParent(AgeState.CHILD);
        Needle needle = new Needle(parent);

        parent.setDirection(Direction.LEFT);
        BufferedImage image = needle.getImage(parent);

        assertSame(Needle.getImages()[AgeState.CHILD.ordinal()][0], image);
    }

    @Test
    public void testGetImageReturnsRightImageWhenDirectionRight() {
        Yukkuri parent = createParent(AgeState.CHILD);
        Needle needle = new Needle(parent);

        parent.setDirection(Direction.RIGHT);
        BufferedImage image = needle.getImage(parent);

        assertSame(Needle.getImages()[AgeState.CHILD.ordinal()][1], image);
    }

    @Test
    public void testGetImageReturnsCorrectImageForAge() {
        Yukkuri babyParent = createParent(AgeState.BABY);
        Yukkuri childParent = createParent(AgeState.CHILD);
        Yukkuri adultParent = createParent(AgeState.ADULT);

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
        Yukkuri parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);

        needle.resetBoundary();

        assertEquals(3, needle.getPivotX());
        assertEquals(6, needle.getPivotY());
        assertEquals(30, needle.getW());
        assertEquals(31, needle.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Yukkuri parent = createParent(AgeState.CHILD);
        Needle needle = new Needle(parent);

        int origPivotX = needle.getPivotX();
        int origPivotY = needle.getPivotY();

        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().remove(parent.getUniqueID());

        needle.resetBoundary();

        assertEquals(origPivotX, needle.getPivotX());
        assertEquals(origPivotY, needle.getPivotY());
    }

    @Test
    public void testToStringUsesResourceUtil() {
        Yukkuri parent = createParent(AgeState.BABY);
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
        Yukkuri parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        Needle needle = new Needle(parent);
        assertEquals(0, needle.getValue());
        assertEquals(0, needle.getCost());
    }

    @Test
    public void testUpdateAliveParentAddsDamageAndStress() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);

        int damageBefore = parent.getDamage();
        needle.update();

        assertTrue(parent.getDamage() > damageBefore);
    }

    @Test
    public void testUpdateAliveParentWakesUpWhenSleeping() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);
        parent.setSleeping(true);

        needle.update();

        assertEquals(false, parent.isSleeping());
    }

    @Test
    public void testUpdateAliveParentNotSleeping() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);
        parent.setSleeping(false);

        needle.update();
    }

    @Test
    public void testUpdateAliveParentFixBackSetsDirectionLeft() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);
        parent.setFixBack(true);
        parent.setDirection(Direction.RIGHT);

        needle.update();

        assertEquals(Direction.LEFT, parent.getDirection());
    }

    @Test
    public void testUpdateAliveParentNotFixBack() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);
        parent.setFixBack(false);

        needle.update();
    }

    @Test
    public void testUpdateAliveParentNotNYDFixBackTalking() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.setFixBack(true);
        parent.setCoreAnkoState(CoreAnkoState.NORMAL);
        // isTalking=trueにしてsetMessage呼び出し回避
        parent.setMessageTicks(1);

        needle.update();
        // isNotNYD && isFixBack && isTalking の分岐を通る
    }

    @Test
    public void testUpdateAliveParentNotNYDNotFixBackTalking() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.setFixBack(false);
        parent.setCoreAnkoState(CoreAnkoState.NORMAL);
        parent.setMessageTicks(1);

        needle.update();
        // isNotNYD && !isFixBack && isTalking の分岐を通る
    }

    @Test
    public void testUpdateAliveParentNYDState() {
        Yukkuri parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);

        TickResult result = needle.update();
        assertEquals(TickResult.NONE, result);
    }

    @Test
    public void testUpdateAliveParentPurupuru() {
        // RNDが常に0を返す → nextInt(50)==0 がtrue → stayPurupuru
        SimYukkuri.RND = new ConstState(0);
        Yukkuri parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);

        needle.update();

        assertTrue(parent.isPurupuru());
    }

    @Test
    public void testUpdateAliveParentNoPurupuru() {
        // RNDが1を返す → nextInt(50)==0 がfalse
        SimYukkuri.RND = new ConstState(1);
        Yukkuri parent = createParent(AgeState.ADULT);
        Needle needle = new Needle(parent);
        parent.setDead(false);
        parent.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);

        needle.update();
    }

    private static Yukkuri createParent(AgeState ageState) {
        Yukkuri parent = new Reimu();
        parent.setAgeState(ageState);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        parent.setSpriteSet(spr);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(parent.getUniqueID(), parent);
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
            // RND=0 → nextInt(50)==0 → checkReactionStalkMother(ATTACKED)
            SimYukkuri.RND = new ConstState(0);
            Yukkuri child = createParent(AgeState.BABY);
            Yukkuri mother = createParent(AgeState.ADULT);
            mother.setHappiness(Happiness.HAPPY);
            mother.setMsgType(YukkuriType.REIMU);
            // 子をNYDにしてsetMessage NPE回避
            child.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);

            Stalk stalk = new Stalk();
            stalk.setPlantYukkuri(mother);
            child.setBindStalk(stalk);

            Needle needle = new Needle(child);
            child.setDead(false);
            mother.setDead(false);

            int stressBefore = mother.getStress();
            needle.update();

            // 母親がSADになりストレスが増加する（checkReactionStalkMother ATTACKED）
            assertTrue(mother.getStress() > stressBefore);
        }

        @Test
        public void testStalkMotherNoReactionWhenRndMisses() {
            // RND=1 → nextInt(50)==1≠0 → 親が反応しない
            SimYukkuri.RND = new ConstState(1);
            Yukkuri child = createParent(AgeState.BABY);
            Yukkuri mother = createParent(AgeState.ADULT);
            mother.setHappiness(Happiness.HAPPY);
            child.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);

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
            Yukkuri child = createParent(AgeState.BABY);
            child.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);

            Needle needle = new Needle(child);
            child.setDead(false);

            // NPEなく正常完了
            TickResult result = needle.update();
            assertEquals(TickResult.NONE, result);
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_FixBackNeedleUpdateWakesBodyFacesPainAndCanTriggerPurupuru() {
            SimYukkuri.RND = new SequenceRNG(0, 1);
            Yukkuri parent = createParent(AgeState.ADULT);
            Needle needle = new Needle(parent);
            parent.setDead(false);
            parent.setFixBack(true);
            parent.setSleeping(true);
            parent.setDirection(Direction.RIGHT);
            parent.setCoreAnkoState(CoreAnkoState.NORMAL);

            int damageBefore = parent.getDamage();
            int stressBefore = parent.getStress();

            TickResult result = needle.update();

            assertEquals(TickResult.NONE, result);
            assertFalse(parent.isSleeping());
            assertEquals(Direction.LEFT, parent.getDirection());
            assertEquals(Happiness.VERY_SAD, parent.getHappiness());
            assertEquals(ImageCode.PAIN.ordinal(), parent.getForceFace());
            assertTrue(parent.isPurupuru());
            assertTrue(parent.getDamage() > damageBefore);
            assertTrue(parent.getStress() > stressBefore);
        }

        @Test
        void testScenario_UnbirthChildNeedleCanTriggerStalkMotherReaction() {
            SimYukkuri.RND = new ConstState(0);
            Yukkuri child = createParent(AgeState.BABY);
            Yukkuri mother = createParent(AgeState.ADULT);
            mother.setHappiness(Happiness.HAPPY);
            mother.setMsgType(YukkuriType.REIMU);
            child.setUnBirth(true);
            child.setCoreAnkoState(CoreAnkoState.NORMAL);

            Stalk stalk = new Stalk();
            stalk.setPlantYukkuri(mother);
            child.setBindStalk(stalk);

            Needle needle = new Needle(child);
            int motherStressBefore = mother.getStress();

            TickResult result = needle.update();

            assertEquals(TickResult.NONE, result);
            assertEquals(Happiness.VERY_SAD, child.getHappiness());
            assertEquals(ImageCode.PAIN.ordinal(), child.getForceFace());
            assertTrue(mother.getStress() > motherStressBefore);
            assertEquals(Happiness.SAD, mother.getHappiness());
        }
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            Needle.loadImages(Needle.class.getClassLoader(), null);
        } catch (Exception e) {
        }
    }
}
