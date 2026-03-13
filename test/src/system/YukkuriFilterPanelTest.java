package src.system;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;

import org.junit.jupiter.api.Test;

import src.enums.YukkuriType;

public class YukkuriFilterPanelTest {

    // --- Constructor ---

    @Test
    public void testConstructor_doesNotThrow() {
        assertDoesNotThrow(() -> new YukkuriFilterPanel());
    }

    // --- Action enum ---

    @Test
    public void testActionEnum_values() {
        YukkuriFilterPanel.Action[] actions = YukkuriFilterPanel.Action.values();
        assertEquals(2, actions.length);
        for (YukkuriFilterPanel.Action a : actions) {
            assertNotNull(a.name());
            a.toString(); // may return null if ResourceUtil not initialized
        }
    }

    @Test
    public void testActionEnum_valueOf() {
        assertEquals(YukkuriFilterPanel.Action.SELECT_ALL,
                YukkuriFilterPanel.Action.valueOf("SELECT_ALL"));
        assertEquals(YukkuriFilterPanel.Action.DSELECT_ALL,
                YukkuriFilterPanel.Action.valueOf("DSELECT_ALL"));
    }

    // --- ButtonListener.getCheckbox / setCheckbox ---

    @Test
    public void testButtonListenerGetSetCheckbox() {
        JCheckBox[] checkboxes = new JCheckBox[3];
        for (int i = 0; i < checkboxes.length; i++) {
            checkboxes[i] = new JCheckBox("test" + i);
        }
        YukkuriFilterPanel.ButtonListener.setCheckbox(checkboxes);
        assertSame(checkboxes, YukkuriFilterPanel.ButtonListener.getCheckbox());
    }

    // --- openFilterPanel: headless → returns false ---

    @Test
    public void testOpenFilterPanel_headless_returnsFalse() {
        List<String> options = new ArrayList<>();
        List<YukkuriType> result = new ArrayList<>();
        List<Boolean> selection = new ArrayList<>();
        try {
            boolean ret = YukkuriFilterPanel.openFilterPanel("Title", "Top", options, result, selection);
            // In headless, JOptionPane.showOptionDialog may return cancel (-1 or 2) → false
            assertFalse(ret);
        } catch (Exception e) {
            // Expected in headless environment
        }
    }
}
