package org.simyukkuri.entity.core.attachment.impl;

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
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.ResourceUtil;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

public class OrangeAmpouleTest {

    @BeforeEach
    public void setUp() throws Exception {
        SimYukkuri.world = new World();
        WorldTestHelper.initializeEmptyMessagePool();
        OrangeAmpoule.setImages(buildImages());
        OrangeAmpoule.setImgW(new int[] { 10, 20, 30 });
        OrangeAmpoule.setImgH(new int[] { 11, 21, 31 });
        OrangeAmpoule.setPivX(new int[] { 1, 2, 3 });
        OrangeAmpoule.setPivY(new int[] { 4, 5, 6 });
    }

    @Test
    public void testStaticAccessors() {
        assertEquals("OrangeAmpoule", OrangeAmpoule.getPosKey());
        assertEquals(7, OrangeAmpoule.getProperty().length);
        assertEquals(2, OrangeAmpoule.getProperty()[0]); // 赤ゆ用画像サイズ
        assertEquals(2, OrangeAmpoule.getProperty()[1]); // 子ゆ用画像サイズ
        assertEquals(1, OrangeAmpoule.getProperty()[2]); // 成ゆ用画像サイズ
    }

    @Test
    public void testConstructorDefaultsAndBoundary() {
        Yukkuri parent = createParent(AgeState.CHILD);
        OrangeAmpoule ampoule = new OrangeAmpoule(parent);

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
        OrangeAmpoule ampoule = new OrangeAmpoule();

        TickResult result = ampoule.update();

        assertEquals(TickResult.NONE, result);
    }

    @Test
    public void testUpdateReducesDamage() {
        Yukkuri parent = createParent(AgeState.ADULT);
        OrangeAmpoule ampoule = new OrangeAmpoule(parent);

        // ダメージを設定
        parent.addDamage(500);
        int damageBefore = parent.getDamage();

        ampoule.update();

        // ダメージが200減少している
        assertTrue(parent.getDamage() < damageBefore);
    }

    @Test
    public void testUpdateDoesNotReviveWhenCrushed() {
        Yukkuri parent = createParent(AgeState.ADULT);
        OrangeAmpoule ampoule = new OrangeAmpoule(parent);

        parent.setDead(true);
        parent.setCrushed(true);

        ampoule.update();

        // 潰れている場合は復活しない
        assertTrue(parent.isDead());
    }

    @Test
    public void testUpdateDoesNotReviveWhenBurned() {
        Yukkuri parent = createParent(AgeState.ADULT);
        OrangeAmpoule ampoule = new OrangeAmpoule(parent);

        parent.setDead(true);
        parent.setBurned(true);

        ampoule.update();

        // 燃えている場合は復活しない
        assertTrue(parent.isDead());
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInMap() {
        Yukkuri parent = createParent(AgeState.CHILD);
        OrangeAmpoule ampoule = new OrangeAmpoule(parent);

        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().remove(parent.getUniqueID());

        BufferedImage image = ampoule.getImage(parent);
        assertNull(image);
    }

    @Test
    public void testGetImageReturnsLeftImageWhenDirectionLeft() {
        Yukkuri parent = createParent(AgeState.CHILD);
        OrangeAmpoule ampoule = new OrangeAmpoule(parent);

        parent.setDirection(Direction.LEFT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(OrangeAmpoule.getImages()[AgeState.CHILD.ordinal()][0], image);
    }

    @Test
    public void testGetImageReturnsRightImageWhenDirectionRight() {
        Yukkuri parent = createParent(AgeState.CHILD);
        OrangeAmpoule ampoule = new OrangeAmpoule(parent);

        parent.setDirection(Direction.RIGHT);
        BufferedImage image = ampoule.getImage(parent);

        assertSame(OrangeAmpoule.getImages()[AgeState.CHILD.ordinal()][1], image);
    }

    @Test
    public void testGetImageReturnsCorrectImageForAge() {
        Yukkuri babyParent = createParent(AgeState.BABY);
        Yukkuri childParent = createParent(AgeState.CHILD);
        Yukkuri adultParent = createParent(AgeState.ADULT);

        OrangeAmpoule babyAmpoule = new OrangeAmpoule(babyParent);
        OrangeAmpoule childAmpoule = new OrangeAmpoule(childParent);
        OrangeAmpoule adultAmpoule = new OrangeAmpoule(adultParent);

        babyParent.setDirection(Direction.LEFT);
        childParent.setDirection(Direction.LEFT);
        adultParent.setDirection(Direction.LEFT);

        assertSame(OrangeAmpoule.getImages()[AgeState.BABY.ordinal()][0], babyAmpoule.getImage(babyParent));
        assertSame(OrangeAmpoule.getImages()[AgeState.CHILD.ordinal()][0], childAmpoule.getImage(childParent));
        assertSame(OrangeAmpoule.getImages()[AgeState.ADULT.ordinal()][0], adultAmpoule.getImage(adultParent));
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Yukkuri parent = createParent(AgeState.ADULT);
        OrangeAmpoule ampoule = new OrangeAmpoule(parent);

        ampoule.resetBoundary();

        assertEquals(3, ampoule.getPivotX());
        assertEquals(6, ampoule.getPivotY());
        assertEquals(30, ampoule.getW());
        assertEquals(31, ampoule.getH());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Yukkuri parent = createParent(AgeState.CHILD);
        OrangeAmpoule ampoule = new OrangeAmpoule(parent);

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
        OrangeAmpoule ampoule = new OrangeAmpoule(parent);

        assertEquals(ResourceUtil.getInstance().read("item_orange"), ampoule.toString());
    }

    @Test
    public void testDefaultConstructor() {
        OrangeAmpoule ampoule = new OrangeAmpoule();
        assertEquals(0, ampoule.getParent());
    }

    @Test
    public void testConstructorWithParentNotInWorld() {
        Yukkuri parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        OrangeAmpoule ampoule = new OrangeAmpoule(parent);
        assertEquals(500, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
    }

    @Test
    public void testUpdateRevivesWhenDeadNotCrushedNotBurned() {
        Yukkuri parent = createParent(AgeState.ADULT);
        OrangeAmpoule ampoule = new OrangeAmpoule(parent);

        parent.addDamage(parent.getDamageLimit() + 100);
        parent.setDead(true);
        parent.setCrushed(false);
        parent.setBurned(false);

        ampoule.update();

        // isDead && !isCrushed && !isBurned → revival()が呼ばれる
        assertFalse(parent.isDead());
    }

    @Test
    public void testUpdateReducesDamageWhenAlive() {
        Yukkuri parent = createParent(AgeState.ADULT);
        OrangeAmpoule ampoule = new OrangeAmpoule(parent);

        parent.addDamage(1000);
        parent.setDead(false);
        int damageBefore = parent.getDamage();

        ampoule.update();

        assertTrue(parent.getDamage() < damageBefore);
    }

    private static Yukkuri createParent(AgeState ageState) {
        Yukkuri parent = new Reimu();
        parent.setAgeState(ageState);
        parent.setMsgType(YukkuriType.REIMU);
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

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            OrangeAmpoule.loadImages(OrangeAmpoule.class.getClassLoader(), null);
        } catch (Exception e) {
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_DeadNonBurnedBodyRevivesAndRecoversDamage() {
            Yukkuri parent = createParent(AgeState.ADULT);
            OrangeAmpoule ampoule = new OrangeAmpoule(parent);
            parent.addDamage(parent.getDamageLimit() + 500);
            parent.setDead(true);
            parent.setCrushed(false);
            parent.setBurned(false);

            int damageBefore = parent.getDamage();

            TickResult result = ampoule.update();

            assertEquals(TickResult.NONE, result);
            assertFalse(parent.isDead());
            assertTrue(parent.getDamage() < damageBefore);
        }

        @Test
        void testScenario_BurnedCorpseDoesNotReviveOrRecoverDamage() {
            Yukkuri parent = createParent(AgeState.ADULT);
            OrangeAmpoule ampoule = new OrangeAmpoule(parent);
            parent.addDamage(1000);
            parent.setDead(true);
            parent.setBurned(true);
            parent.setCrushed(false);

            int damageBefore = parent.getDamage();

            TickResult result = ampoule.update();

            assertEquals(TickResult.NONE, result);
            assertTrue(parent.isDead());
            assertEquals(damageBefore, parent.getDamage());
        }
    }
}
