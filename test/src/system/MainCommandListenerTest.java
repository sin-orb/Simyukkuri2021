package src.system;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.event.ItemEvent;

import javax.swing.JComboBox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.command.GadgetMenu;
import src.draw.World;

public class MainCommandListenerTest {

    private MainCommandListener listener;

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        listener = new MainCommandListener();

        // Initialize MainCommandUI static components
        MainCommandUI.setGameSpeedCombo(new JComboBox<>(new String[] { "0", "1", "2" }));
        MainCommandUI.setMainItemCombo(new JComboBox<>(GadgetMenu.getMainCategory()));
        MainCommandUI.setSubItemCombo(new JComboBox<>());
    }

    @Test
    public void testGameSpeedComboBoxListener_updatesSelectedSpeed() {
        MainCommandListener.GameSpeedComboBoxListener speedListener = listener.new GameSpeedComboBoxListener();

        JComboBox<String> combo = MainCommandUI.getGameSpeedCombo();
        combo.setSelectedIndex(2);

        ItemEvent e = new ItemEvent(combo, ItemEvent.ITEM_STATE_CHANGED, combo.getSelectedItem(), ItemEvent.SELECTED);
        speedListener.itemStateChanged(e);

        assertEquals(2, MainCommandUI.getSelectedGameSpeed());
    }

    @Test
    public void testMainItemComboBoxListener_updatesGadgetMenu() {
        MainCommandListener.MainItemComboBoxListener itemListener = listener.new MainItemComboBoxListener();

        JComboBox<GadgetMenu.GadgetList> combo = MainCommandUI.getMainItemCombo();
        combo.setSelectedIndex(1);

        ItemEvent e = new ItemEvent(combo, ItemEvent.ITEM_STATE_CHANGED, combo.getSelectedItem(), ItemEvent.SELECTED);
        itemListener.itemStateChanged(e);

        assertEquals(GadgetMenu.getMainCategory()[1], GadgetMenu.getSelectMain());
    }

    @Test
    public void testSubItemComboBoxListener_updatesGadgetMenu() {
        MainCommandListener.SubItemComboBoxListener subListener = listener.new SubItemComboBoxListener();

        // Need to set select main first
        GadgetMenu.setSelectMain(GadgetMenu.GadgetList.FOODS);

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
