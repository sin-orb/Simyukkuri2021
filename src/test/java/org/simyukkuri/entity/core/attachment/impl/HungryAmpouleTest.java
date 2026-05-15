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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.system.ResourceUtil;

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
        Yukkuri parent = createParent(AgeState.CHILD);
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

        TickResult result = ampoule.update();

        assertEquals(TickResult.NONE, result);
    }

    @Test
    public void testUpdateReducesHungryWhenNotEating() {
        Yukkuri parent = createParent(AgeState.ADULT);
        HungryAmpoule ampoule = new HungryAmpoule(parent);

        // 満腹状態に設定
        parent.setHungry(10000);
        parent.setEating(false);

        int hungryBefore = parent.getHungry();
        TickResult result = ampoule.update();

        assertEquals(TickResult.NONE, result);
        // hungryが減少している（TICK * 1000 減少）
        assertEquals(hungryBefore - 1000, parent.getHungry());
    }

    @Test
    public void testUpdateDoesNotReduceHungryWhenEating() {
        Yukkuri parent = createParent(AgeState.ADULT);
        HungryAmpoule ampoule = new HungryAmpoule(parent);

        parent.setHungry(10000);
        parent.setEating(true);

        int hungryBefore = parent.getHungry();
        TickResult result = ampoule.update();

        assertEquals(TickResult.NONE, result);
        // 食事中なのでhungryは変わらない
        assertEquals(hungryBefore, parent.getHungry());
    }

    @Test
    public void testUpdateClampsHungryToZero() {
        Yukkuri parent = createParent(AgeState.ADULT);
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
        Yukkuri parent = createParent(AgeState.CHILD);
        HungryAmpoule ampoule = new HungryAmpoule(parent);

        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().remove(parent.getUniqueID());

        BufferedImage image = ampoule.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsLeftImageWhenDirectionLeft() {
        Yukkuri parent = createParent(AgeState.CHILD);
        HungryAmpoule ampoule = new HungryAmpoule(parent);

        parent.setDirection(Direction.LEFT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(HungryAmpoule.getImages()[AgeState.CHILD.ordinal()][0], image);
    }

    @Test
    public void testGetImageReturnsRightImageWhenDirectionRight() {
        Yukkuri parent = createParent(AgeState.CHILD);
        HungryAmpoule ampoule = new HungryAmpoule(parent);

        parent.setDirection(Direction.RIGHT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(HungryAmpoule.getImages()[AgeState.CHILD.ordinal()][1], image);
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Yukkuri parent = createParent(AgeState.ADULT);
        HungryAmpoule ampoule = new HungryAmpoule(parent);

        ampoule.resetBoundary();

        assertEquals(3, ampoule.getPivotX());
        assertEquals(6, ampoule.getPivotY());
        assertEquals(30, ampoule.getW());
        assertEquals(31, ampoule.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Yukkuri parent = createParent(AgeState.CHILD);
        HungryAmpoule ampoule = new HungryAmpoule(parent);

        int origPivotX = ampoule.getPivotX();
        int origPivotY = ampoule.getPivotY();

        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().remove(parent.getUniqueID());

        ampoule.resetBoundary();

        assertEquals(origPivotX, ampoule.getPivotX());
        assertEquals(origPivotY, ampoule.getPivotY());
    }

    @Test
    public void testToStringUsesResourceUtil() {
        Yukkuri parent = createParent(AgeState.BABY);
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
        Yukkuri parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        HungryAmpoule ampoule = new HungryAmpoule(parent);
        assertEquals(500, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
    }

    private static Yukkuri createParent(AgeState ageState) {
        Yukkuri parent = new Reimu();
        parent.setAgeState(ageState);
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

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            HungryAmpoule.loadImages(HungryAmpoule.class.getClassLoader(), null);
        } catch (Exception e) {
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_NonEatingBodyLosesExactlyOneTickOfHungry() {
            Yukkuri parent = createParent(AgeState.ADULT);
            HungryAmpoule ampoule = new HungryAmpoule(parent);
            parent.setHungry(4321);
            parent.setEating(false);

            TickResult result = ampoule.update();

            assertEquals(TickResult.NONE, result);
            assertEquals(3321, parent.getHungry());
        }

        @Test
        void testScenario_EatingBodyPreservesHungryEvenNearClampBoundary() {
            Yukkuri parent = createParent(AgeState.ADULT);
            HungryAmpoule ampoule = new HungryAmpoule(parent);
            parent.setHungry(200);
            parent.setEating(true);

            TickResult result = ampoule.update();

            assertEquals(TickResult.NONE, result);
            assertEquals(200, parent.getHungry());
        }
    }
}
