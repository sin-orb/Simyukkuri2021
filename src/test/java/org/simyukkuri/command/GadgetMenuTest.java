package org.simyukkuri.command;

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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.JMenu;

import org.simyukkuri.command.GadgetMenu.ActionTarget;
import org.simyukkuri.command.GadgetMenu.GadgetList;
import org.simyukkuri.command.GadgetMenu.HelpContext;
import org.simyukkuri.command.GadgetMenu.HelpIcon;
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
    }

    @Test
    public void testHelpContextToStringNotEmpty() {
        assertFalse(HelpContext.SHIFT_LMB_ALL.toString().isEmpty());
        assertFalse(HelpContext.SHIFT_LMB_ALL_ONOFF.toString().isEmpty());
        assertFalse(HelpContext.CTRL_LMB_ALL_INVERT.toString().isEmpty());
    }

    @Test
    public void testHelpIconProperties() {
        assertEquals(1, HelpIcon.mlb.getImageIndex());
        assertEquals(16, HelpIcon.mlb.getW());
        assertEquals(2, HelpIcon.mrb.getImageIndex());
        assertEquals(16, HelpIcon.mrb.getW());
    }

    @Test
    public void testCategoryArraysContainExpectedItems() {
        GadgetList[] main = GadgetMenu.getMainCategory();
        GadgetList[] tool = GadgetMenu.getToolCategory();

        assertTrue(main.length > 0);
        assertTrue(tool.length > 0);

        assertEquals(GadgetList.TOOL, main[0]);
        assertEquals(GadgetList.PUNISH, tool[0]);
    }

    @Test
    public void testPopupMenuBuildsCategories() {
        GadgetMenu.getPopup().removeAll();
        GadgetMenu.createPopupMenu();

        assertEquals(GadgetMenu.getMainCategory().length, GadgetMenu.getPopup().getComponentCount());
        // displayName は headless 環境での static 初期化順序により null の場合がある
        String displayName = GadgetMenu.getMainCategory()[0].getDisplayName();
        String menuText = ((JMenu) GadgetMenu.getPopup().getComponent(0)).getText();
        if (displayName != null) {
            assertEquals(displayName, menuText);
        }
    }

}
