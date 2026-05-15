package org.simyukkuri.ui;

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
import org.simyukkuri.engine.World;
import org.simyukkuri.ui.MainCommandUI;
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
        JComboBox<GadgetMenu.GadgetMenuChoice> combo = MainCommandUI.getMainItemCombo();
        combo.setSelectedIndex(1);

        ItemEvent e = new ItemEvent(combo, ItemEvent.ITEM_STATE_CHANGED, combo.getSelectedItem(), ItemEvent.SELECTED);
        itemListener.itemStateChanged(e);

        assertEquals(GadgetMenu.getMainCategory()[1], GadgetMenu.getSelectMain());
    }

    @Test
    public void testSubItemComboBoxListener_updatesGadgetMenu() {
        SubItemComboBoxListener subListener = new SubItemComboBoxListener();

        // Need to set select main first
        GadgetMenu.setSelectMain(GadgetMenu.GadgetMenuChoice.FOODS);

        @SuppressWarnings("unchecked")
        JComboBox<GadgetMenu.GadgetMenuChoice> combo = MainCommandUI.getSubItemCombo();
        // Add items to match indices in GadgetMenu.FoodCategory
        combo.addItem(GadgetMenu.GadgetMenuChoice.NORMAL);
        combo.addItem(GadgetMenu.GadgetMenuChoice.BITTER);
        combo.addItem(GadgetMenu.GadgetMenuChoice.LEMON_POP);
        combo.addItem(GadgetMenu.GadgetMenuChoice.HOT);
        combo.addItem(GadgetMenu.GadgetMenuChoice.VIYUGRA);
        combo.addItem(GadgetMenu.GadgetMenuChoice.SWEETS1);
        combo.setSelectedIndex(5);

        ItemEvent e = new ItemEvent(combo, ItemEvent.ITEM_STATE_CHANGED, combo.getSelectedItem(), ItemEvent.SELECTED);
        subListener.itemStateChanged(e);

        assertEquals(GadgetMenu.GadgetMenuChoice.SWEETS1, GadgetMenu.getSelectSub());
    }
}
