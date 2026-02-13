package src.command;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import src.command.GadgetMenu.ActionTarget;
import src.command.GadgetMenu.GadgetList;
import src.command.GadgetMenu.HelpContext;
import src.command.GadgetMenu.HelpIcon;
import src.system.MessagePool;

@Disabled("GUI-dependent")
public class GadgetMenuTest {

    @BeforeAll
    public static void setUpClass() {
        Assumptions.assumeTrue(hasDisplay());
        MessagePool.loadMessage(GadgetMenuTest.class.getClassLoader());
    }

    @Test
    public void testActionTargetFlags() {
        assertEquals(1, ActionTarget.IMMEDIATE.getFlag());
        assertEquals(2, ActionTarget.BODY.getFlag());
        assertEquals(4, ActionTarget.GADGET.getFlag());
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

    private static boolean hasDisplay() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        boolean isWindows = osName.contains("windows");
        return isWindows || System.getenv("DISPLAY") != null;
    }
}
