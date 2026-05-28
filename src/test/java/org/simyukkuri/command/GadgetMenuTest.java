package org.simyukkuri.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.simyukkuri.command.GadgetMenu.ActionTarget;
import org.simyukkuri.command.GadgetMenu.GadgetMenuChoice;
import org.simyukkuri.command.GadgetMenu.HelpContext;
import org.simyukkuri.command.GadgetMenu.HelpIcon;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.WorldTestHelper;

public class GadgetMenuTest {

    @BeforeAll
    public static void setUpClass() {
        System.setProperty("java.awt.headless", "true");
        WorldTestHelper.initializeLoadedMessagePool(GadgetMenuTest.class.getClassLoader());
    }

    @Test
    public void testActionTargetFlags() {
        assertEquals(1, ActionTarget.IMMEDIATE.getMask());
        assertEquals(2, ActionTarget.BODY.getMask());
        assertEquals(4, ActionTarget.GADGET.getMask());
        assertEquals(6, ActionTarget.BODY_AND_GADGET.getMask());
        assertEquals(8, ActionTarget.TERRAIN.getMask());
        assertEquals(16, ActionTarget.WALL.getMask());
        assertEquals(32, ActionTarget.FIELD.getMask());
        assertEquals(12, ActionTarget.TERRAIN_AND_GADGET.getMask());
    }

    @Test
    public void testHelpContextToStringMatchesLoadedText() {
        assertEquals(GameText.read("command_lmb_all"), HelpContext.SHIFT_LMB_ALL.toString());
        assertEquals(GameText.read("command_lmb_onoff"), HelpContext.SHIFT_LMB_ALL_ONOFF.toString());
        assertEquals(GameText.read("command_lmb_invert"), HelpContext.CTRL_LMB_ALL_INVERT.toString());
    }

    @Test
    public void testHelpIconProperties() {
        assertEquals(1, HelpIcon.mlb.getImageIndex());
        assertEquals(16, HelpIcon.mlb.getW());
        assertEquals(2, HelpIcon.mrb.getImageIndex());
        assertEquals(16, HelpIcon.mrb.getW());
        assertEquals(3, HelpIcon.sft.getImageIndex());
        assertEquals(32, HelpIcon.sft.getW());
        assertEquals(0, HelpIcon.ctl.getImageIndex());
        assertEquals(32, HelpIcon.ctl.getW());
    }

    @Test
    public void testCategoryArraysContainExpectedItems() {
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
                GadgetMenuChoice.DEBUG,
        }, GadgetMenu.getMainCategory());
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
                GadgetMenuChoice.GODHAND,
        }, GadgetMenu.getToolCategory());
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
                GadgetMenuChoice.SET_RAPER,
        }, GadgetMenu.getToolCategory2());
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.ORANGE_AMP,
                GadgetMenuChoice.ACCEL_AMP,
                GadgetMenuChoice.STOP_AMP,
                GadgetMenuChoice.HUNGRY_AMP,
                GadgetMenuChoice.VERYSHIT_AMP,
                GadgetMenuChoice.POISON_AMP,
                GadgetMenuChoice.BREEDING_AMP,
                GadgetMenuChoice.ANYD_AMP,
        }, GadgetMenu.getAmpouleCategory());
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.NORMAL,
                GadgetMenuChoice.BITTER,
                GadgetMenuChoice.LEMON_POP,
                GadgetMenuChoice.HOT,
                GadgetMenuChoice.VIYUGRA,
                GadgetMenuChoice.SWEETS1,
                GadgetMenuChoice.SWEETS2,
                GadgetMenuChoice.WASTE,
                GadgetMenuChoice.AUTO,
        }, GadgetMenu.getFoodCategory());
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.INDIVIDUAL,
                GadgetMenuChoice.YU_CLEAN,
                GadgetMenuChoice.BODY,
                GadgetMenuChoice.SHIT,
                GadgetMenuChoice.ETC,
                GadgetMenuChoice.ALL,
        }, GadgetMenu.getCleanCategory());
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.OKAZARI_HIDE,
        }, GadgetMenu.getOkazariCategory());
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.PANTS_NORMAL,
        }, GadgetMenu.getPantsCategory());
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.TOILET,
                GadgetMenuChoice.BED,
                GadgetMenuChoice.STICKY_PLATE,
                GadgetMenuChoice.HOT_PLATE,
                GadgetMenuChoice.PROCESSOR_PLATE,
                GadgetMenuChoice.FOOD_MAKER,
                GadgetMenuChoice.MIXER,
                GadgetMenuChoice.DIFFUSER,
                GadgetMenuChoice.ORANGE_POOL,
                GadgetMenuChoice.BREED_POOL,
                GadgetMenuChoice.GARBAGE_CHUTE,
                GadgetMenuChoice.MACHINE_PRESS,
                GadgetMenuChoice.PRODUCT_CHUTE,
        }, GadgetMenu.getFloorCategory());
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
                GadgetMenuChoice.ALL_DELETE,
        }, GadgetMenu.getBarrierCategory());
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.BALL,
                GadgetMenuChoice.YUNBA,
                GadgetMenuChoice.YUNBA_SETUP,
                GadgetMenuChoice.SUI,
                GadgetMenuChoice.TRASH,
                GadgetMenuChoice.TRAMPOLINE,
                GadgetMenuChoice.STONE,
        }, GadgetMenu.getToysCategory());
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.BELTCONVEYOR_CUSTOM,
                GadgetMenuChoice.BELTCONVEYOR_SETUP,
        }, GadgetMenu.getConveyorCategory());
        assertArrayEquals(new GadgetMenuChoice[] {
                GadgetMenuChoice.YUKKURISITEITTENE,
                GadgetMenuChoice.YUKKURIDIE,
                GadgetMenuChoice.YUKKURIFURIFURI,
        }, GadgetMenu.getVoiceCategory());
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
                GadgetMenuChoice.BADGE,
        }, GadgetMenu.getTestCategory());
    }

    @Test
    public void testPopupMenuBuildsCategories() {
        JPopupMenu popup = GadgetMenu.getPopup();
        popup.removeAll();
        GadgetMenu.createPopupMenu();

        GadgetMenuChoice[] labels = GadgetMenu.getMainCategory();
        GadgetMenuChoice[][] groups = {
                GadgetMenu.getToolCategory(),
                GadgetMenu.getToolCategory2(),
                GadgetMenu.getAmpouleCategory(),
                GadgetMenu.getFoodCategory(),
                GadgetMenu.getCleanCategory(),
                GadgetMenu.getOkazariCategory(),
                GadgetMenu.getPantsCategory(),
                GadgetMenu.getFloorCategory(),
                GadgetMenu.getBarrierCategory(),
                GadgetMenu.getToysCategory(),
                GadgetMenu.getConveyorCategory(),
                GadgetMenu.getVoiceCategory(),
                GadgetMenu.getTestCategory(),
        };

        assertEquals(groups.length, popup.getComponentCount());
        for (int i = 0; i < groups.length; i++) {
            JMenu menu = (JMenu) popup.getComponent(i);
            GadgetMenuChoice[] group = groups[i];
            String expectedText = labels[i].getDisplayName();
            if (expectedText == null) {
                expectedText = "";
            }
            assertEquals(expectedText, menu.getText());
            assertEquals(group.length, menu.getItemCount());
            for (int j = 0; j < group.length; j++) {
                JMenuItem item = menu.getItem(j);
                String expectedItemText = group[j].getDisplayName();
                if (expectedItemText == null) {
                    expectedItemText = "";
                }
                assertEquals(expectedItemText, item.getText());
                assertEquals(group[j].name(), item.getActionCommand());
            }
        }
    }

}
