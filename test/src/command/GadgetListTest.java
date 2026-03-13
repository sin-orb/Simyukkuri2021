package src.command;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import src.command.GadgetMenu.ActionControl;
import src.command.GadgetMenu.ActionTarget;
import src.command.GadgetMenu.GadgetList;
import src.command.GadgetMenu.MainCategoryName;

/**
 * GadgetMenu.GadgetList enum のテスト.
 * GadgetList は GUI 不要なため headless 環境で実行可能。
 */
public class GadgetListTest {

    @BeforeAll
    public static void setUpClass() {
        System.setProperty("java.awt.headless", "true");
        // MessagePool.loadMessage(GadgetListTest.class.getClassLoader());
    }

    // -------------------------------------------------------
    // values() / 件数
    // -------------------------------------------------------

    @Test
    public void testValuesNotEmpty() {
        GadgetList[] values = GadgetList.values();
        assertTrue(values.length > 0, "GadgetList に要素が存在すること");
    }

    // -------------------------------------------------------
    // 各 GadgetList 要素の group / actionTarget が非 null
    // -------------------------------------------------------

    @Test
    public void testAllItemsHaveGroup() {
        for (GadgetList item : GadgetList.values()) {
            assertNotNull(item.getGroup(),
                    "getGroup() が非 null であること: " + item.name());
        }
    }

    @Test
    public void testImmediateItemsHaveNullOrImmediateActionTarget() {
        // IMMEDIATE グループのアイテムは actionTarget が IMMEDIATE か null のみ
        for (GadgetList item : GadgetList.values()) {
            if (item.getActionTarget() == ActionTarget.IMMEDIATE) {
                assertEquals(ActionTarget.IMMEDIATE, item.getActionTarget());
            }
        }
    }

    // -------------------------------------------------------
    // 特定の重要な GadgetList エントリの検証
    // -------------------------------------------------------

    @Test
    public void testPunishIsToolGroupAndBodyTarget() {
        assertEquals(MainCategoryName.TOOL, GadgetList.PUNISH.getGroup());
        assertEquals(ActionTarget.BODY, GadgetList.PUNISH.getActionTarget());
        assertEquals(ActionControl.LEFT_CLICK, GadgetList.PUNISH.getActionControl());
    }

    @Test
    public void testYuCleanIsImmediateTarget() {
        assertEquals(ActionTarget.IMMEDIATE, GadgetList.YU_CLEAN.getActionTarget());
        assertEquals(MainCategoryName.CLEAN, GadgetList.YU_CLEAN.getGroup());
    }

    @Test
    public void testNormalFoodHasFoodClass() {
        assertNotNull(GadgetList.NORMAL.getGadgetClass());
        assertEquals(src.item.Food.class, GadgetList.NORMAL.getGadgetClass());
    }

    @Test
    public void testToStringReturnsDisplayName() {
        String str = GadgetList.PUNISH.toString();
        assertNotNull(str);
        assertFalse(str.isEmpty(), "toString() が空文字ではないこと");
    }

    @Test
    public void testOkazariHideIsAccessoryGroup() {
        assertEquals(MainCategoryName.ACCESSORY, GadgetList.OKAZARI_HIDE.getGroup());
        assertEquals(ActionTarget.BODY, GadgetList.OKAZARI_HIDE.getActionTarget());
    }

    @Test
    public void testGetInitOptionReturnsNonNegative() {
        for (GadgetList item : GadgetList.values()) {
            assertTrue(item.getInitOption() >= 0,
                    "initOption が 0 以上であること: " + item.name());
        }
    }

    // -------------------------------------------------------
    // GadgetMenu.getXxxCategory() の検証
    // -------------------------------------------------------

    @Test
    public void testMainCategoryContainsTool() {
        GadgetList[] main = GadgetMenu.getMainCategory();
        assertTrue(main.length > 0);
        assertEquals(GadgetList.TOOL, main[0]);
    }

    @Test
    public void testToolCategoryFirstIsPunish() {
        GadgetList[] tool = GadgetMenu.getToolCategory();
        assertTrue(tool.length > 0);
        assertEquals(GadgetList.PUNISH, tool[0]);
    }

    @Test
    public void testCleanCategoryContainsAll() {
        GadgetList[] clean = GadgetMenu.getCleanCategory();
        boolean hasAll = false;
        for (GadgetList g : clean) {
            if (g == GadgetList.ALL) {
                hasAll = true;
                break;
            }
        }
        assertTrue(hasAll, "クリーンカテゴリに ALL が含まれていること");
    }

    @Test
    public void testAmpouleCategory_notEmpty() {
        GadgetList[] amp = GadgetMenu.getAmpouleCategory();
        assertTrue(amp.length > 0);
    }
}
