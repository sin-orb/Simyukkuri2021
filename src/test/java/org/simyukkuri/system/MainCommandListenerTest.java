package org.simyukkuri.system;

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

import java.awt.event.ItemEvent;

import javax.swing.JComboBox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.command.GadgetMenu;
import org.simyukkuri.draw.World;
import org.simyukkuri.util.WorldTestHelper;

public class MainCommandListenerTest {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetWorld();
        SimYukkuri.world = new World();
        WorldTestHelper.initializeMainCommandUITestState();
    }

    @Test
    public void testGameSpeedComboBoxListener_updatesSelectedSpeed() {
        GameSpeedComboBoxListener speedListener = new GameSpeedComboBoxListener();

        @SuppressWarnings("unchecked")
        JComboBox<String> combo = MainCommandUI.getGameSpeedCombo();
        combo.setSelectedIndex(2);

        ItemEvent e = new ItemEvent(combo, ItemEvent.ITEM_STATE_CHANGED, combo.getSelectedItem(), ItemEvent.SELECTED);
        speedListener.itemStateChanged(e);

        assertEquals(2, MainCommandUI.getSelectedGameSpeed());
    }

    @Test
    public void testMainItemComboBoxListener_updatesGadgetMenu() {
        MainItemComboBoxListener itemListener = new MainItemComboBoxListener();

        @SuppressWarnings("unchecked")
        JComboBox<GadgetMenu.GadgetList> combo = MainCommandUI.getMainItemCombo();
        combo.setSelectedIndex(1);

        ItemEvent e = new ItemEvent(combo, ItemEvent.ITEM_STATE_CHANGED, combo.getSelectedItem(), ItemEvent.SELECTED);
        itemListener.itemStateChanged(e);

        assertEquals(GadgetMenu.getMainCategory()[1], GadgetMenu.getSelectMain());
    }

    @Test
    public void testSubItemComboBoxListener_updatesGadgetMenu() {
        SubItemComboBoxListener subListener = new SubItemComboBoxListener();

        // Need to set select main first
        GadgetMenu.setSelectMain(GadgetMenu.GadgetList.FOODS);

        @SuppressWarnings("unchecked")
        JComboBox<GadgetMenu.GadgetList> combo = MainCommandUI.getSubItemCombo();
        // Add items to match indices in GadgetMenu.FoodCategory
        combo.addItem(GadgetMenu.GadgetList.NORMAL);
        combo.addItem(GadgetMenu.GadgetList.BITTER);
        combo.addItem(GadgetMenu.GadgetList.LEMON_POP);
        combo.addItem(GadgetMenu.GadgetList.HOT);
        combo.addItem(GadgetMenu.GadgetList.VIYUGRA);
        combo.addItem(GadgetMenu.GadgetList.SWEETS1);
        combo.setSelectedIndex(5);

        ItemEvent e = new ItemEvent(combo, ItemEvent.ITEM_STATE_CHANGED, combo.getSelectedItem(), ItemEvent.SELECTED);
        subListener.itemStateChanged(e);

        assertEquals(GadgetMenu.GadgetList.SWEETS1, GadgetMenu.getSelectSub());
    }
}
