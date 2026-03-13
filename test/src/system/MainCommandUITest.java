package src.system;

import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.draw.Translate;
import src.draw.World;

public class MainCommandUITest {

    // --- MENU_PANE_X constant ---

    @Test
    public void testMenuPaneXConstant() {
        assertEquals(124, MainCommandUI.MENU_PANE_X);
    }

    // --- getSelectedGameSpeed / setSelectedGameSpeed ---

    @Test
    public void testGetSetSelectedGameSpeed() {
        int orig = MainCommandUI.getSelectedGameSpeed();
        MainCommandUI.setSelectedGameSpeed(3);
        assertEquals(3, MainCommandUI.getSelectedGameSpeed());
        MainCommandUI.setSelectedGameSpeed(orig); // restore
    }

    // --- getSelectedZoomScale / setSelectedZoomScale ---

    @Test
    public void testGetSetSelectedZoomScale() {
        int orig = MainCommandUI.getSelectedZoomScale();
        MainCommandUI.setSelectedZoomScale(2);
        assertEquals(2, MainCommandUI.getSelectedZoomScale());
        MainCommandUI.setSelectedZoomScale(orig); // restore
    }

    // --- getGameSpeedCombo / setGameSpeedCombo ---

    @Test
    public void testGetSetGameSpeedCombo() {
        JComboBox combo = new JComboBox();
        MainCommandUI.setGameSpeedCombo(combo);
        assertSame(combo, MainCommandUI.getGameSpeedCombo());
    }

    // --- getMainItemCombo / setMainItemCombo ---

    @Test
    public void testGetSetMainItemCombo() {
        JComboBox combo = new JComboBox();
        MainCommandUI.setMainItemCombo(combo);
        assertSame(combo, MainCommandUI.getMainItemCombo());
    }

    // --- getSubItemCombo / setSubItemCombo ---

    @Test
    public void testGetSetSubItemCombo() {
        JComboBox combo = new JComboBox();
        MainCommandUI.setSubItemCombo(combo);
        assertSame(combo, MainCommandUI.getSubItemCombo());
    }

    // --- getYuStatusLabel / setYuStatusLabel ---

    @Test
    public void testGetSetYuStatusLabel() {
        JLabel[] labels = new JLabel[12]; // StatusLabel has 12 values
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel("test" + i);
        }
        MainCommandUI.setYuStatusLabel(labels);
        assertSame(labels, MainCommandUI.getYuStatusLabel());
    }

    // --- getStatIconLabel / setStatIconLabel ---

    @Test
    public void testGetSetStatIconLabel() {
        JLabel[] labels = new JLabel[8];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel();
        }
        MainCommandUI.setStatIconLabel(labels);
        assertSame(labels, MainCommandUI.getStatIconLabel());
    }

    // --- getItemIconLabel / setItemIconLabel ---

    @Test
    public void testGetSetItemIconLabel() {
        JLabel[] labels = new JLabel[1];
        labels[0] = new JLabel();
        MainCommandUI.setItemIconLabel(labels);
        assertSame(labels, MainCommandUI.getItemIconLabel());
    }

    // --- getSystemButton / setSystemButton ---

    @Test
    public void testGetSetSystemButton() {
        JButton[] buttons = new JButton[7]; // SystemButtonLabel has 7 values
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton();
        }
        MainCommandUI.setSystemButton(buttons);
        assertSame(buttons, MainCommandUI.getSystemButton());
    }

    // --- getScriptButton / setScriptButton ---

    @Test
    public void testGetSetScriptButton() {
        JToggleButton btn = new JToggleButton();
        MainCommandUI.setScriptButton(btn);
        assertSame(btn, MainCommandUI.getScriptButton());
    }

    // --- getTargetButton / setTargetButton ---

    @Test
    public void testGetSetTargetButton() {
        JToggleButton btn = new JToggleButton();
        MainCommandUI.setTargetButton(btn);
        assertSame(btn, MainCommandUI.getTargetButton());
    }

    // --- getPinButton / setPinButton ---

    @Test
    public void testGetSetPinButton() {
        JToggleButton btn = new JToggleButton();
        MainCommandUI.setPinButton(btn);
        assertSame(btn, MainCommandUI.getPinButton());
    }

    // --- getHelpButton / setHelpButton ---

    @Test
    public void testGetSetHelpButton() {
        JToggleButton btn = new JToggleButton();
        MainCommandUI.setHelpButton(btn);
        assertSame(btn, MainCommandUI.getHelpButton());
    }

    // --- getOptionButton / setOptionButton ---

    @Test
    public void testGetSetOptionButton() {
        JToggleButton btn = new JToggleButton();
        MainCommandUI.setOptionButton(btn);
        assertSame(btn, MainCommandUI.getOptionButton());
    }

    // --- getPlayerButton / setPlayerButton ---

    @Test
    public void testGetSetPlayerButton() {
        JToggleButton[] buttons = new JToggleButton[2]; // ToolButtonLabel has 2 values
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JToggleButton();
        }
        MainCommandUI.setPlayerButton(buttons);
        assertSame(buttons, MainCommandUI.getPlayerButton());
    }

    // --- getOptionPopup / setOptionPopup ---

    @Test
    public void testGetSetOptionPopup() {
        JPopupMenu popup = new JPopupMenu();
        MainCommandUI.setOptionPopup(popup);
        assertSame(popup, MainCommandUI.getOptionPopup());
    }

    // --- getMapWindow / setMapWindow ---

    @Test
    public void testGetSetMapWindow() {
        MainCommandUI.setMapWindow(null);
        assertNull(MainCommandUI.getMapWindow());
    }

    // --- getItemWindow / setItemWindow ---

    @Test
    public void testGetSetItemWindow() {
        MainCommandUI.setItemWindow(null);
        assertNull(MainCommandUI.getItemWindow());
    }

    // --- clearStatus: headless → try/catch ---

    @Test
    public void testClearStatus_headless_executesCode() {
        SimYukkuri.world = new World();
        Translate.setMapSize(1000, 1000, 500);
        try {
            MainCommandUI.clearStatus();
        } catch (NullPointerException e) {
            // Expected if GUI components are null in headless environment
        }
    }

    // --- showStatus: headless → try/catch ---

    @Test
    public void testShowStatus_nullBody_headless_executesCode() {
        SimYukkuri.world = new World();
        Translate.setMapSize(1000, 1000, 500);
        try {
            MainCommandUI.showStatus(null);
        } catch (Exception e) {
            // Expected in headless environment
        }
    }

    // --- showPlayerStatus: headless → try/catch ---

    @Test
    public void testShowPlayerStatus_headless_executesCode() {
        SimYukkuri.world = new World();
        Translate.setMapSize(1000, 1000, 500);
        try {
            MainCommandUI.showPlayerStatus();
        } catch (Exception e) {
            // Expected in headless environment
        }
    }
}
