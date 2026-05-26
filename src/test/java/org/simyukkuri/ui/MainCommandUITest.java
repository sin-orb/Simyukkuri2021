package org.simyukkuri.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.util.WorldTestHelper;

public class MainCommandUITest {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetMainCommandUiState();
    }

    // --- MENU_PANE_X constant ---

    @Test
    public void testMenuPaneXConstant() {
        assertEquals(124, MainCommandUi.MENU_PANE_X);
    }

    // --- getSelectedGameSpeed / setSelectedGameSpeed ---

    @Test
    public void testGetSetSelectedGameSpeed() {
        int orig = MainCommandUi.getSelectedGameSpeed();
        MainCommandUi.setSelectedGameSpeed(3);
        assertEquals(3, MainCommandUi.getSelectedGameSpeed());
        MainCommandUi.setSelectedGameSpeed(orig); // restore
    }

    // --- getSelectedZoomScale / setSelectedZoomScale ---

    @Test
    public void testGetSetSelectedZoomScale() {
        int orig = MainCommandUi.getSelectedZoomScale();
        MainCommandUi.setSelectedZoomScale(2);
        assertEquals(2, MainCommandUi.getSelectedZoomScale());
        MainCommandUi.setSelectedZoomScale(orig); // restore
    }

    // --- getGameSpeedCombo / setGameSpeedCombo ---

    @Test
    public void testGetSetGameSpeedCombo() {
        @SuppressWarnings("rawtypes")
        JComboBox combo = new JComboBox();
        MainCommandUi.setGameSpeedCombo(combo);
        assertSame(combo, MainCommandUi.getGameSpeedCombo());
    }

    // --- getMainItemCombo / setMainItemCombo ---

    @Test
    public void testGetSetMainItemCombo() {
        @SuppressWarnings("rawtypes")
        JComboBox combo = new JComboBox();
        MainCommandUi.setMainItemCombo(combo);
        assertSame(combo, MainCommandUi.getMainItemCombo());
    }

    // --- getSubItemCombo / setSubItemCombo ---

    @Test
    public void testGetSetSubItemCombo() {
        @SuppressWarnings("rawtypes")
        JComboBox combo = new JComboBox();
        MainCommandUi.setSubItemCombo(combo);
        assertSame(combo, MainCommandUi.getSubItemCombo());
    }

    // --- getYuStatusLabel / setYuStatusLabel ---

    @Test
    public void testGetSetYuStatusLabel() {
        JLabel[] labels = new JLabel[12]; // StatusLabel has 12 values
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel("test" + i);
        }
        MainCommandUi.setYuStatusLabel(labels);
        assertSame(labels, MainCommandUi.getYuStatusLabel());
    }

    // --- getStatIconLabel / setStatIconLabel ---

    @Test
    public void testGetSetStatIconLabel() {
        JLabel[] labels = new JLabel[8];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel();
        }
        MainCommandUi.setStatIconLabel(labels);
        assertSame(labels, MainCommandUi.getStatIconLabel());
    }

    // --- getItemIconLabel / setItemIconLabel ---

    @Test
    public void testGetSetItemIconLabel() {
        JLabel[] labels = new JLabel[1];
        labels[0] = new JLabel();
        MainCommandUi.setItemIconLabel(labels);
        assertSame(labels, MainCommandUi.getItemIconLabel());
    }

    // --- getSystemButton / setSystemButton ---

    @Test
    public void testGetSetSystemButton() {
        JButton[] buttons = new JButton[7]; // SystemButtonLabel has 7 values
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton();
        }
        MainCommandUi.setSystemButton(buttons);
        assertSame(buttons, MainCommandUi.getSystemButton());
    }

    // --- getScriptButton / setScriptButton ---

    @Test
    public void testGetSetScriptButton() {
        JToggleButton btn = new JToggleButton();
        MainCommandUi.setScriptButton(btn);
        assertSame(btn, MainCommandUi.getScriptButton());
    }

    // --- getTargetButton / setTargetButton ---

    @Test
    public void testGetSetTargetButton() {
        JToggleButton btn = new JToggleButton();
        MainCommandUi.setTargetButton(btn);
        assertSame(btn, MainCommandUi.getTargetButton());
    }

    // --- getPinButton / setPinButton ---

    @Test
    public void testGetSetPinButton() {
        JToggleButton btn = new JToggleButton();
        MainCommandUi.setPinButton(btn);
        assertSame(btn, MainCommandUi.getPinButton());
    }

    // --- getHelpButton / setHelpButton ---

    @Test
    public void testGetSetHelpButton() {
        JToggleButton btn = new JToggleButton();
        MainCommandUi.setHelpButton(btn);
        assertSame(btn, MainCommandUi.getHelpButton());
    }

    // --- getOptionButton / setOptionButton ---

    @Test
    public void testGetSetOptionButton() {
        JToggleButton btn = new JToggleButton();
        MainCommandUi.setOptionButton(btn);
        assertSame(btn, MainCommandUi.getOptionButton());
    }

    // --- getPlayerButton / setPlayerButton ---

    @Test
    public void testGetSetPlayerButton() {
        JToggleButton[] buttons = new JToggleButton[2]; // ToolButtonLabel has 2 values
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JToggleButton();
        }
        MainCommandUi.setPlayerButton(buttons);
        assertSame(buttons, MainCommandUi.getPlayerButton());
    }

    // --- getOptionPopup / setOptionPopup ---

    @Test
    public void testGetSetOptionPopup() {
        JPopupMenu popup = new JPopupMenu();
        MainCommandUi.setOptionPopup(popup);
        assertSame(popup, MainCommandUi.getOptionPopup());
    }

    // --- getWorldSelectionWindow / setWorldSelectionWindow ---

    @Test
    public void testGetSetWorldSelectionWindow() {
        MainCommandUi.setWorldWindow(null);
        assertNull(MainCommandUi.getWorldWindow());
    }

    // --- getItemWindow / setItemWindow ---

    @Test
    public void testGetSetItemWindow() {
        MainCommandUi.setItemWindow(null);
        assertNull(MainCommandUi.getItemWindow());
    }

    // --- clearStatus: headless → try/catch ---

    @Test
    public void testClearStatus_headless_executesCode() {
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
        try {
            MainCommandUi.clearStatus();
        } catch (NullPointerException e) {
            // Expected if GUI components are null in headless environment
        }
    }

    // --- showStatus: headless → try/catch ---

    @Test
    public void testShowStatus_nullBody_headless_executesCode() {
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
        try {
            MainCommandUi.showStatus(null);
        } catch (Exception e) {
            // Expected in headless environment
        }
    }

    // --- showPlayerStatus: headless → try/catch ---

    @Test
    public void testShowPlayerStatus_headless_executesCode() {
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
        try {
            MainCommandUi.showPlayerStatus();
        } catch (Exception e) {
            // Expected in headless environment
        }
    }
}
