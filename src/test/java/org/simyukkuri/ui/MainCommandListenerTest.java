package org.simyukkuri.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.awt.event.ItemEvent;
import javax.swing.JComboBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.command.GadgetMenu;
import org.simyukkuri.engine.World;
import org.simyukkuri.util.WorldTestHelper;

public class MainCommandListenerTest {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetWorld();
        SimYukkuri.world = new World();
        WorldTestHelper.initializeMainCommandUiTestState();
    }

    @Test
    public void testGameSpeedComboBoxListener_updatesSelectedSpeed() {
        GameSpeedComboBoxListener speedListener = new GameSpeedComboBoxListener();

        @SuppressWarnings("unchecked")
        JComboBox<String> combo = MainCommandUi.getGameSpeedCombo();
        // まず index 0 に設定してから index 2 に変更
        combo.setSelectedIndex(0);
        ItemEvent e0 = new ItemEvent(combo, ItemEvent.ITEM_STATE_CHANGED, combo.getSelectedItem(), ItemEvent.SELECTED);
        speedListener.itemStateChanged(e0);
        int speedBefore = MainCommandUi.getSelectedGameSpeed();

        combo.setSelectedIndex(2);
        ItemEvent e2 = new ItemEvent(combo, ItemEvent.ITEM_STATE_CHANGED, combo.getSelectedItem(), ItemEvent.SELECTED);
        speedListener.itemStateChanged(e2);

        assertEquals(2, MainCommandUi.getSelectedGameSpeed(), "index 2 を選択するとゲームスピードが 2 になること");
        assertNotEquals(speedBefore, MainCommandUi.getSelectedGameSpeed(), "index を変えるとゲームスピードが変化すること");
    }

    @Test
    public void testMainItemComboBoxListener_updatesGadgetMenu() {
        MainItemComboBoxListener itemListener = new MainItemComboBoxListener();

        @SuppressWarnings("unchecked")
        JComboBox<GadgetMenu.GadgetMenuChoice> combo = MainCommandUi.getMainItemCombo();
        // まず index 0 を選択してから index 1 に変更
        combo.setSelectedIndex(0);
        ItemEvent e0 = new ItemEvent(combo, ItemEvent.ITEM_STATE_CHANGED, combo.getSelectedItem(), ItemEvent.SELECTED);
        itemListener.itemStateChanged(e0);
        GadgetMenu.GadgetMenuChoice before = GadgetMenu.getSelectMain();

        combo.setSelectedIndex(1);
        ItemEvent e1 = new ItemEvent(combo, ItemEvent.ITEM_STATE_CHANGED, combo.getSelectedItem(), ItemEvent.SELECTED);
        itemListener.itemStateChanged(e1);

        assertEquals(GadgetMenu.getMainCategory()[1], GadgetMenu.getSelectMain(),
                "index 1 を選択すると getSelectMain が getMainCategory()[1] になること");
        assertNotEquals(before, GadgetMenu.getSelectMain(),
                "index を変えると selectMain が変化すること");
    }

    @Test
    public void testSubItemComboBoxListener_updatesGadgetMenu() {
        SubItemComboBoxListener subListener = new SubItemComboBoxListener();

        // Need to set select main first
        GadgetMenu.setSelectMain(GadgetMenu.GadgetMenuChoice.FOODS);

        @SuppressWarnings("unchecked")
        JComboBox<GadgetMenu.GadgetMenuChoice> combo = MainCommandUi.getSubItemCombo();
        // Add items to match indices in GadgetMenu.FoodCategory
        combo.addItem(GadgetMenu.GadgetMenuChoice.NORMAL);
        combo.addItem(GadgetMenu.GadgetMenuChoice.BITTER);
        combo.addItem(GadgetMenu.GadgetMenuChoice.LEMON_POP);
        combo.addItem(GadgetMenu.GadgetMenuChoice.HOT);
        combo.addItem(GadgetMenu.GadgetMenuChoice.VIYUGRA);
        combo.addItem(GadgetMenu.GadgetMenuChoice.SWEETS1);

        // まず index 0（NORMAL）を選択してから index 5（SWEETS1）に変更
        combo.setSelectedIndex(0);
        ItemEvent e0 = new ItemEvent(combo, ItemEvent.ITEM_STATE_CHANGED, combo.getSelectedItem(), ItemEvent.SELECTED);
        subListener.itemStateChanged(e0);
        GadgetMenu.GadgetMenuChoice before = GadgetMenu.getSelectSub();

        combo.setSelectedIndex(5);
        ItemEvent e5 = new ItemEvent(combo, ItemEvent.ITEM_STATE_CHANGED, combo.getSelectedItem(), ItemEvent.SELECTED);
        subListener.itemStateChanged(e5);

        assertEquals(GadgetMenu.GadgetMenuChoice.SWEETS1, GadgetMenu.getSelectSub(),
                "SWEETS1 を選択すると getSelectSub が SWEETS1 になること");
        assertNotEquals(before, GadgetMenu.getSelectSub(),
                "index を変えると selectSub が変化すること");
    }
}
