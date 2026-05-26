package org.simyukkuri.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.simyukkuri.command.GadgetMenu.ActionControl;
import org.simyukkuri.command.GadgetMenu.ActionTarget;
import org.simyukkuri.command.GadgetMenu.GadgetMenuChoice;
import org.simyukkuri.command.GadgetMenu.MainCategoryName;
import org.simyukkuri.util.WorldTestHelper;

/**
 * GadgetMenu.GadgetMenuChoice enum のテスト.
 * GadgetMenuChoice は GUI 不要なため headless 環境で実行可能。
 */
public class GadgetMenuChoiceTest {

    @BeforeAll
    static void setUpClass() {
        System.setProperty("java.awt.headless", "true");
        WorldTestHelper.initializeLoadedMessagePool(GadgetMenuChoiceTest.class.getClassLoader());
    }

    @Test
    void testMainCategoryMatchesExpectedOrder() {
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.TOOL,
                GadgetMenuChoice.BODY_CHANGE,
                GadgetMenuChoice.AMPOULE,
                GadgetMenuChoice.FOODS,
                GadgetMenuChoice.CLEAN,
                GadgetMenuChoice.ACCESSORY,
                GadgetMenuChoice.PANTS,
                GadgetMenuChoice.FLOOR,
                GadgetMenuChoice.BARRIER,
                GadgetMenuChoice.TOYS,
                GadgetMenuChoice.CONVEYOR,
                GadgetMenuChoice.VOICE,
                GadgetMenuChoice.DEBUG
        }, GadgetMenu.getMainCategory());
    }

    @Test
    void testToolCategoryMatchesExpectedOrder() {
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.PUNISH,
                GadgetMenuChoice.SNAPPING,
                GadgetMenuChoice.PICKUP,
                GadgetMenuChoice.HOLD,
                GadgetMenuChoice.SURISURI,
                GadgetMenuChoice.VIBRATOR,
                GadgetMenuChoice.PENICUT,
                GadgetMenuChoice.JUICE,
                GadgetMenuChoice.ORANGE_JUICE,
                GadgetMenuChoice.LEMON_SPLAY,
                GadgetMenuChoice.PHEROMONE_SPRAY,
                GadgetMenuChoice.HAMMER,
                GadgetMenuChoice.INJECT_SPERM,
                GadgetMenuChoice.DRIP_SPERM,
                GadgetMenuChoice.PUNCH,
                GadgetMenuChoice.PEAL,
                GadgetMenuChoice.BLIND,
                GadgetMenuChoice.SHUTMOUTH,
                GadgetMenuChoice.HAIRCUT,
                GadgetMenuChoice.PACK,
                GadgetMenuChoice.STOMP,
                GadgetMenuChoice.GODHAND
        }, GadgetMenu.getToolCategory());
    }

    @Test
    void testToolCategory2MatchesExpectedOrder() {
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.BRAID_PLUCK,
                GadgetMenuChoice.ANAL_CLOSE,
                GadgetMenuChoice.STALK_CUT,
                GadgetMenuChoice.CASTRATION,
                GadgetMenuChoice.STALK_UNPLUG,
                GadgetMenuChoice.LIGHTER,
                GadgetMenuChoice.NEEDLE,
                GadgetMenuChoice.WATER,
                GadgetMenuChoice.BURY,
                GadgetMenuChoice.SET_SICK,
                GadgetMenuChoice.SET_RAPER
        }, GadgetMenu.getToolCategory2());
    }

    @Test
    void testAmpouleCategoryMatchesExpectedOrder() {
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.ORANGE_AMP,
                GadgetMenuChoice.ACCEL_AMP,
                GadgetMenuChoice.STOP_AMP,
                GadgetMenuChoice.HUNGRY_AMP,
                GadgetMenuChoice.VERYSHIT_AMP,
                GadgetMenuChoice.POISON_AMP,
                GadgetMenuChoice.BREEDING_AMP,
                GadgetMenuChoice.ANYD_AMP
        }, GadgetMenu.getAmpouleCategory());
    }

    @Test
    void testFoodCategoryMatchesExpectedOrder() {
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.NORMAL,
                GadgetMenuChoice.BITTER,
                GadgetMenuChoice.LEMON_POP,
                GadgetMenuChoice.HOT,
                GadgetMenuChoice.VIYUGRA,
                GadgetMenuChoice.SWEETS1,
                GadgetMenuChoice.SWEETS2,
                GadgetMenuChoice.WASTE,
                GadgetMenuChoice.AUTO
        }, GadgetMenu.getFoodCategory());
    }

    @Test
    void testCleanCategoryMatchesExpectedOrder() {
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.INDIVIDUAL,
                GadgetMenuChoice.YU_CLEAN,
                GadgetMenuChoice.BODY,
                GadgetMenuChoice.SHIT,
                GadgetMenuChoice.ETC,
                GadgetMenuChoice.ALL
        }, GadgetMenu.getCleanCategory());
    }

    @Test
    void testBarrierCategoryMatchesExpectedOrder() {
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.GAP_MINI,
                GadgetMenuChoice.GAP_BIG,
                GadgetMenuChoice.NET_MINI,
                GadgetMenuChoice.NET_BIG,
                GadgetMenuChoice.WALL,
                GadgetMenuChoice.ITEM,
                GadgetMenuChoice.NO_UNUN,
                GadgetMenuChoice.KEKKAI,
                GadgetMenuChoice.POOL,
                GadgetMenuChoice.FARM,
                GadgetMenuChoice.BELTCONVEYOR,
                GadgetMenuChoice.WALL_DELETE,
                GadgetMenuChoice.FIELD_DELETE,
                GadgetMenuChoice.ALL_DELETE
        }, GadgetMenu.getBarrierCategory());
    }

    @Test
    void testToysCategoryMatchesExpectedOrder() {
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.BALL,
                GadgetMenuChoice.YUNBA,
                GadgetMenuChoice.YUNBA_SETUP,
                GadgetMenuChoice.SUI,
                GadgetMenuChoice.TRASH,
                GadgetMenuChoice.TRAMPOLINE,
                GadgetMenuChoice.STONE
        }, GadgetMenu.getToysCategory());
    }

    @Test
    void testConveyorCategoryMatchesExpectedOrder() {
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.BELTCONVEYOR_CUSTOM,
                GadgetMenuChoice.BELTCONVEYOR_SETUP
        }, GadgetMenu.getConveyorCategory());
    }

    @Test
    void testVoiceCategoryMatchesExpectedOrder() {
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.YUKKURISITEITTENE,
                GadgetMenuChoice.YUKKURIDIE,
                GadgetMenuChoice.YUKKURIFURIFURI
        }, GadgetMenu.getVoiceCategory());
    }

    @Test
    void testTestCategoryMatchesExpectedOrder() {
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.RANKSET,
                GadgetMenuChoice.RANKSET2,
                GadgetMenuChoice.GARBAGE_STATION,
                GadgetMenuChoice.BED_NORA,
                GadgetMenuChoice.TOILET_NORA,
                GadgetMenuChoice.HOUSE_NORA,
                GadgetMenuChoice.GARBAGE_NORA,
                GadgetMenuChoice.ORANGE_NORA,
                GadgetMenuChoice.STICKY_NORA,
                GadgetMenuChoice.TOY_NORA,
                GadgetMenuChoice.REMOVEALL,
                GadgetMenuChoice.EVENT_SHIT,
                GadgetMenuChoice.EVENT_EAT,
                GadgetMenuChoice.EVENT_RIDEYUKKURI,
                GadgetMenuChoice.EVENT_PROUDCHILD,
                GadgetMenuChoice.SETVAIN,
                GadgetMenuChoice.YUNNYAA,
                GadgetMenuChoice.BEGGINGFORLIFE,
                GadgetMenuChoice.PREDATORSGAME,
                GadgetMenuChoice.INVITEANTS,
                GadgetMenuChoice.FEED,
                GadgetMenuChoice.BADGE
        }, GadgetMenu.getTestCategory());
    }

    @Test
    void testImportantEntriesHaveExpectedDefinitions() {
        assertEquals(MainCategoryName.TOOL, GadgetMenuChoice.PUNISH.getGroup());
        assertEquals(ActionTarget.BODY, GadgetMenuChoice.PUNISH.getActionTarget());
        assertEquals(ActionControl.LEFT_CLICK, GadgetMenuChoice.PUNISH.getActionControl());
        assertEquals(ActionTarget.IMMEDIATE, GadgetMenuChoice.YU_CLEAN.getActionTarget());
        assertEquals(MainCategoryName.CLEAN, GadgetMenuChoice.YU_CLEAN.getGroup());
        assertNotNull(GadgetMenuChoice.NORMAL.getGadgetClass());
        assertEquals(MainCategoryName.ACCESSORY, GadgetMenuChoice.OKAZARI_HIDE.getGroup());
        assertEquals(ActionTarget.BODY, GadgetMenuChoice.OKAZARI_HIDE.getActionTarget());
        assertEquals(org.simyukkuri.entity.core.world.item.Food.class, GadgetMenuChoice.NORMAL.getGadgetClass());
        assertEquals(3, GadgetMenuChoice.NORMAL.getInitOption());
        assertEquals(ActionTarget.TERRAIN, GadgetMenuChoice.NORMAL.getActionTarget());
        assertEquals(MainCategoryName.FOODS, GadgetMenuChoice.NORMAL.getGroup());
    }
}
