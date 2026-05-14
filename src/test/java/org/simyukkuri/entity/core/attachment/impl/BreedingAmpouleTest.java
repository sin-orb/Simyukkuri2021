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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.World;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.Event;
import org.simyukkuri.system.ResourceUtil;

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
        Yukkuri parent = createParent(AgeState.CHILD);
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
        Yukkuri parent = createParent(AgeState.ADULT);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        parent.setDead(true);

        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsBurned() {
        Yukkuri parent = createParent(AgeState.ADULT);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        parent.setBurned(true);

        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsCrushed() {
        Yukkuri parent = createParent(AgeState.ADULT);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        parent.setCrushed(true);

        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInMap() {
        Yukkuri parent = createParent(AgeState.CHILD);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        SimYukkuri.world.getCurrentMap().getYukkuriMap().remove(parent.getUniqueID());

        BufferedImage image = ampoule.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsLeftImageWhenDirectionLeft() {
        Yukkuri parent = createParent(AgeState.CHILD);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        parent.setDirection(Direction.LEFT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(BreedingAmpoule.getImages()[AgeState.CHILD.ordinal()][0], image);
    }

    @Test
    public void testGetImageReturnsRightImageWhenDirectionRight() {
        Yukkuri parent = createParent(AgeState.CHILD);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        parent.setDirection(Direction.RIGHT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(BreedingAmpoule.getImages()[AgeState.CHILD.ordinal()][1], image);
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Yukkuri parent = createParent(AgeState.ADULT);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        ampoule.resetBoundary();

        assertEquals(3, ampoule.getPivotX());
        assertEquals(6, ampoule.getPivotY());
        assertEquals(30, ampoule.getW());
        assertEquals(31, ampoule.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Yukkuri parent = createParent(AgeState.CHILD);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        int origPivotX = ampoule.getPivotX();
        int origPivotY = ampoule.getPivotY();

        SimYukkuri.world.getCurrentMap().getYukkuriMap().remove(parent.getUniqueID());

        ampoule.resetBoundary();

        assertEquals(origPivotX, ampoule.getPivotX());
        assertEquals(origPivotY, ampoule.getPivotY());
    }

    @Test
    public void testToStringUsesResourceUtil() {
        Yukkuri parent = createParent(AgeState.BABY);
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
        Yukkuri parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);
        assertEquals(1000, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
    }

    @Test
    public void testUpdateBreedsWhenParentIsAliveAndNotDisabled() {
        Yukkuri parent = createParent(AgeState.ADULT);
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
        Yukkuri parent = createParent(AgeState.ADULT);
        BreedingAmpoule ampoule = new BreedingAmpoule(parent);

        parent.setCastrated(true);

        int babyTypesBefore = parent.getBabyTypes().size();
        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
        assertEquals(babyTypesBefore, parent.getBabyTypes().size());
    }

    private static Yukkuri createParent(AgeState ageState) {
        Yukkuri parent = new Reimu();
        parent.setAgeState(ageState);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(parent.getUniqueID(), parent);
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
            BreedingAmpoule.loadImages(BreedingAmpoule.class.getClassLoader(), null);
        } catch (Exception e) {
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_LiveBodyGetsFedHealedAndPregnantWithNewBabyDna() {
            Yukkuri parent = createParent(AgeState.ADULT);
            parent.setHungry(10);
            parent.addDamage(300);
            BreedingAmpoule ampoule = new BreedingAmpoule(parent);

            int damageBefore = parent.getDamage();
            int babiesBefore = parent.getBabyTypes().size();

            Event result = ampoule.update();

            assertEquals(Event.DONOTHING, result);
            assertEquals(100, parent.getHungry());
            assertTrue(parent.getDamage() < damageBefore);
            assertTrue(parent.isHasBaby());
            assertEquals(babiesBefore + 1, parent.getBabyTypes().size());
            Dna baby = parent.getBabyTypes().get(parent.getBabyTypes().size() - 1);
            assertEquals(false, baby.isRaperChild());
            assertEquals(null, baby.getAttitude());
            assertEquals(null, baby.getIntelligence());
        }

        @Test
        void testScenario_BodyCastrationBlocksPregnancyAndHealingSideEffects() {
            Yukkuri parent = createParent(AgeState.ADULT);
            parent.setHungry(10);
            parent.addDamage(300);
            parent.setCastrated(true);
            BreedingAmpoule ampoule = new BreedingAmpoule(parent);

            int damageBefore = parent.getDamage();
            int babiesBefore = parent.getBabyTypes().size();

            Event result = ampoule.update();

            assertEquals(Event.DONOTHING, result);
            assertEquals(10, parent.getHungry());
            assertEquals(damageBefore, parent.getDamage());
            assertEquals(babiesBefore, parent.getBabyTypes().size());
            assertFalse(parent.isHasBaby());
        }
    }
}
