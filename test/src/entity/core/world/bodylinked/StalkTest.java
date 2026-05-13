package src.entity.core.world.bodylinked;

import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.entity.core.living.yukkuri.Yukkuri;
import src.enums.Direction;
import src.enums.Event;
import src.enums.WorldEntityKind;
import src.enums.Type;
import src.util.WorldTestHelper;

class StalkTest {

    private Stalk stalk;

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();
        WorldTestHelper.initializeStandardTranslate200();
        stalk = new Stalk();
    }

    @AfterEach
    void tearDown() {
        WorldTestHelper.resetWorld();
    }

    @Test
    void testStalkId() {
        UUID id = UUID.randomUUID();
        stalk.setStalkId(id);
        assertEquals(id, stalk.getStalkId());

        UUID generated = new Stalk().getStalkId();
        assertNotNull(generated);
    }

    @Test
    void testDirection() {
        stalk.setDirection(0);
        assertEquals(0, stalk.getOption());

        stalk.setDirection(1);
        assertEquals(1, stalk.getOption());
    }

    @Test
    void testPlantYukkuri() {
        stalk.setPlantYukkuri(100);
        assertEquals(100, stalk.getPlantYukkuri());
        assertEquals(100, stalk.getPyForSeri());

        stalk.setPyForSeri(200);
        assertEquals(200, stalk.getPlantYukkuri());
    }

    @Test
    void testAmount() {
        stalk.setAmount(5000);
        assertEquals(5000, stalk.getAmount());
    }

    @Test
    void testConstructorWithCoords() {
        Stalk s = new Stalk(100, 200, 0);
        assertEquals(100, s.getX());
        assertEquals(200, s.getY());
        assertEquals(Type.OBJECT, s.getObjType());
        assertEquals(WorldEntityKind.STALK, s.getWorldEntityType());
        assertEquals(100 * 24 * 5, s.getAmount());
        assertTrue(SimYukkuri.world.getCurrentMap().getStalk().containsKey(s.getObjId()));
    }

    @Test
    void testGetHitCheckObjType() {
        assertEquals(0, stalk.getHitCheckObjType());
    }

    @Test
    void testObjHitProcess() {
        // 常に0を返す
        assertEquals(0, stalk.objHitProcess(new Stalk()));
    }

    @Test
    void testRemoveListData() {
        Stalk s = new Stalk(50, 50, 0);
        int id = s.getObjId();
        assertTrue(SimYukkuri.world.getCurrentMap().getStalk().containsKey(id));
        s.removeListData();
        assertFalse(SimYukkuri.world.getCurrentMap().getStalk().containsKey(id));
    }

    @Test
    void testSetPlantYukkuriWithBody() {
        Yukkuri body = WorldTestHelper.createBody();
        body.setX(100);
        body.setY(100);
        SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

        stalk.setPlantYukkuri(body);
        assertEquals(body.getUniqueID(), stalk.getPlantYukkuri());
    }

    @Test
    void testSetPlantYukkuriWithNull() {
        stalk.setPlantYukkuri((Yukkuri) null);
        assertEquals(-1, stalk.getPlantYukkuri());
    }

    @Test
    void testDetachFromStalkPreventsRebindOnUpdate() {
        Yukkuri parent = WorldTestHelper.createBody();
        parent.setX(100);
        parent.setY(100);
        parent.setAgeState(src.enums.AgeState.ADULT);
        SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);

        Yukkuri child = WorldTestHelper.createBody();
        child.setX(120);
        child.setY(120);
        child.setAgeState(src.enums.AgeState.BABY);
        child.setUnBirth(true);
        child.setBindStalk(stalk);
        child.setParentLinkId(parent.getUniqueID());
        SimYukkuri.world.getCurrentMap().getBody().put(child.getUniqueID(), child);

        stalk.setPlantYukkuri(parent);
        stalk.getBindBabies().add(child.getUniqueID());

        child.detachFromStalk();

        assertNull(child.getBindStalk());
        assertEquals(-1, child.getParentLinkId());
        assertNull(stalk.getBindBabies().get(0));
        assertTrue(child.isUnBirth());

        stalk.upDate();

        assertNull(child.getBindStalk());
        assertEquals(-1, child.getParentLinkId());
    }

    @Test
    void testDetachFromStalkAllowsFallAfterRelease() {
        Yukkuri parent = WorldTestHelper.createBody();
        parent.setX(100);
        parent.setY(100);
        parent.setAgeState(src.enums.AgeState.ADULT);
        SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);

        Yukkuri child = WorldTestHelper.createBody();
        child.setX(120);
        child.setY(120);
        child.setZ(10);
        child.setAgeState(src.enums.AgeState.BABY);
        child.setUnBirth(true);
        child.setBindStalk(stalk);
        child.setParentLinkId(parent.getUniqueID());
        SimYukkuri.world.getCurrentMap().getBody().put(child.getUniqueID(), child);

        stalk.setPlantYukkuri(parent);
        stalk.getBindBabies().add(child.getUniqueID());

        child.detachFromStalk();
        child.release();
        int before = child.getZ();

        child.clockTick();

        assertTrue(child.isUnBirth());
        assertTrue(child.getZ() <= before);
    }

    @Test
    void testSetBindBabyAndGetBindBabies() {
        Yukkuri baby = WorldTestHelper.createBody();
        baby.setX(50);
        baby.setY(50);
        SimYukkuri.world.getCurrentMap().getBody().put(baby.getUniqueID(), baby);

        stalk.setBindBaby(baby);
        assertEquals(1, stalk.getBindBabies().size());
    }

    @Test
    void testSetBindBabies() {
        java.util.List<Integer> list = new java.util.ArrayList<>();
        list.add(42);
        stalk.setBindBabies(list);
        assertEquals(list, stalk.getBindBabies());
    }

    @Test
    void testDisBindBabys() {
        // bindBabiesが空の場合
        stalk.disBindBabys();
        assertTrue(stalk.getBindBabies().isEmpty());
    }

    @Test
    void testIsPlantYukkuriNoParent() {
        // plantYukkuri=-1でbindBabiesが空 → false
        assertFalse(stalk.isPlantYukkuri());
    }

    @Test
    void testIsPlantYukkuriWithParentId() {
        stalk.setPlantYukkuri(999);
        assertTrue(stalk.isPlantYukkuri());
    }

    @Test
    void testEatStalkReducesAmount() {
        stalk.setAmount(1000);
        stalk.eatStalk(300);
        assertEquals(700, stalk.getAmount());
    }

    @Test
    void testEatStalkToZeroRemoves() {
        Stalk s = new Stalk(50, 50, 0);
        int id = s.getObjId();
        s.setAmount(100);
        s.eatStalk(100);
        assertEquals(0, s.getAmount());
        // amountが0以下になるとremove()が呼ばれる
        assertFalse(SimYukkuri.world.getCurrentMap().getStalk().containsKey(id));
    }

    @Test
    void testGrab() {
        stalk.grab();
        assertTrue(stalk.isGrabbed());
        assertEquals(-1, stalk.getPlantYukkuri());
    }

    @Test
    void testTakePlantYukkuriNull() {
        // plantYukkuri=-1なのでnullが返る
        assertNull(stalk.takePlantYukkuri());
    }

    @Test
    void testUpDateWithNullBindBabies() {
        // bindBabiesがデフォルトで空 → upDate()はループをスキップ
        stalk.setBindBabies(null);
        // nullの場合は早期リターン
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> stalk.upDate());
    }

    @Test
    void testUpDateWithEmptyBindBabies() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> stalk.upDate());
    }

    @Test
    void testClockTickRemovedState() {
        Stalk s = new Stalk(50, 50, 0);
        s.remove();
        // removedになっているのでclockTickはREMOVEDを返す
        assertEquals(Event.REMOVED, s.clockTick());
    }

    @Test
    void testClockTickNormal() {
        Stalk s = new Stalk(50, 50, 0);
        s.setPlantYukkuri(-1);
        // 例外が出ないことを確認
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> s.clockTick());
    }

    @Test
    void testCalcXYZ() {
        stalk.setCalcX(100);
        stalk.setCalcY(200);
        stalk.setCalcZ(300);
        // calcPos後の値は内部計算によるが、例外が出ないことを確認
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> stalk.getX());
    }

    @Test
    void testHasGetPopup() {
        assertNotNull(stalk.hasGetPopup());
    }

    @Test
    void testRemove() {
        stalk.setPlantYukkuri(999);
        stalk.remove();
        assertEquals(-1, stalk.getPlantYukkuri());
        assertTrue(stalk.isRemoved());
    }

    @Test
    void testGetShadowImage() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> stalk.getShadowImage());
    }

    // --- getShadowImage: plantYukkuri != -1 → returns null ---

    @Test
    void testGetShadowImage_planted_returnsNull() {
        stalk.setPlantYukkuri(999);
        assertNull(stalk.getShadowImage());
    }

    // --- getImageLayer: option==0 → images[1] ---

    @Test
    void testGetImageLayer_option0_doesNotThrow() {
        stalk.setDirection(0); // option = 0
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> stalk.getImageLayer(layer));
    }

    // --- getImageLayer: option!=0 → images[0] ---

    @Test
    void testGetImageLayer_option1_doesNotThrow() {
        stalk.setDirection(1); // option = 1
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> stalk.getImageLayer(layer));
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            src.entity.core.world.bodylinked.Stalk.loadImages(src.entity.core.world.bodylinked.Stalk.class.getClassLoader(), null);
        } catch (Exception e) {
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_UpdateLinksUnbornBabyToParentAndAppliesRightFacingOffsets() {
            Yukkuri parent = WorldTestHelper.createBody();
            Yukkuri baby = WorldTestHelper.createBody();
            SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);
            SimYukkuri.world.getCurrentMap().getBody().put(baby.getUniqueID(), baby);
            baby.setUnBirth(true);

            Stalk planted = new Stalk(100, 120, 0);
            planted.setZ(5);
            planted.setPlantYukkuri(parent);
            planted.setDirection(0);
            planted.setBindBaby(baby);

            planted.upDate();

            assertEquals(parent.getUniqueID(), baby.getParentLinkId());
            assertEquals(planted, baby.getBindStalk());
            assertEquals(Direction.RIGHT, baby.getDirection());
            assertEquals(114, baby.getX());
            assertEquals(121, baby.getY());
            assertEquals(19, baby.getZ());
        }

        @Test
        void testScenario_UpdateWithLeftFacingStalkMirrorsBabyPlacement() {
            Yukkuri parent = WorldTestHelper.createBody();
            Yukkuri baby = WorldTestHelper.createBody();
            SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);
            SimYukkuri.world.getCurrentMap().getBody().put(baby.getUniqueID(), baby);
            baby.setUnBirth(true);

            Stalk planted = new Stalk(100, 120, 0);
            planted.setZ(5);
            planted.setPlantYukkuri(parent);
            planted.setDirection(1);
            planted.setBindBaby(baby);

            planted.upDate();

            assertEquals(Direction.LEFT, baby.getDirection());
            assertEquals(86, baby.getX());
            assertEquals(121, baby.getY());
            assertEquals(19, baby.getZ());
        }

        @Test
        void testScenario_EatStalkToZeroUnbindsBabyAndRemovesStalkFromWorld() {
            Yukkuri baby = WorldTestHelper.createBody();
            SimYukkuri.world.getCurrentMap().getBody().put(baby.getUniqueID(), baby);

            Stalk planted = new Stalk(60, 70, 0);
            planted.setBindBaby(baby);
            baby.setBindStalk(planted);
            baby.setBindObj(planted.getObjId());
            planted.setAmount(10);

            planted.eatStalk(10);

            assertEquals(0, planted.getAmount());
            assertTrue(planted.isRemoved());
            assertFalse(SimYukkuri.world.getCurrentMap().getStalk().containsKey(planted.getObjId()));
            assertNull(baby.getBindStalk());
            assertEquals(-1, baby.getBindObj());
            assertTrue(planted.getBindBabies().isEmpty());
        }

        @Test
        void testScenario_GrabDetachesFromParentStalkListAndClearsPlantOwner() {
            Yukkuri parent = WorldTestHelper.createBody();
            SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);
            parent.setHasStalk(true);
            parent.setStalks(new java.util.LinkedList<>());

            Stalk planted = new Stalk(40, 40, 0);
            planted.setPlantYukkuri(parent);
            parent.getStalks().add(planted);

            planted.grab();

            assertTrue(planted.isGrabbed());
            assertEquals(-1, planted.getPlantYukkuri());
            assertTrue(parent.getStalks().isEmpty());
            assertFalse(parent.isHasStalk());
        }
    }
}
