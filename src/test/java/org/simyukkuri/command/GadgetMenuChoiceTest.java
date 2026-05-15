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

import org.simyukkuri.command.GadgetMenu.ActionControl;
import org.simyukkuri.command.GadgetMenu.ActionTarget;
import org.simyukkuri.command.GadgetMenu.GadgetMenuChoice;
import org.simyukkuri.command.GadgetMenu.MainCategoryName;

/**
 * GadgetMenu.GadgetMenuChoice enum のテスト.
 * GadgetMenuChoice は GUI 不要なため headless 環境で実行可能。
 */
public class GadgetMenuChoiceTest {

    @BeforeAll
    public static void setUpClass() {
        System.setProperty("java.awt.headless", "true");
        // MessagePool.loadMessage(GadgetMenuChoiceTest.class.getClassLoader());
    }

    // -------------------------------------------------------
    // values() / 件数
    // -------------------------------------------------------

    @Test
    public void testValuesNotEmpty() {
        GadgetMenuChoice[] values = GadgetMenuChoice.values();
        assertTrue(values.length > 0, "GadgetMenuChoice に要素が存在すること");
    }

    // -------------------------------------------------------
    // 各 GadgetMenuChoice 要素の group / actionTarget が非 null
    // -------------------------------------------------------

    @Test
    public void testAllItemsHaveGroup() {
        for (GadgetMenuChoice item : GadgetMenuChoice.values()) {
            assertNotNull(item.getGroup(),
                    "getGroup() が非 null であること: " + item.name());
        }
    }

    @Test
    public void testImmediateItemsHaveNullOrImmediateActionTarget() {
        // IMMEDIATE グループのアイテムは actionTarget が IMMEDIATE か null のみ
        for (GadgetMenuChoice item : GadgetMenuChoice.values()) {
            if (item.getActionTarget() == ActionTarget.IMMEDIATE) {
                assertEquals(ActionTarget.IMMEDIATE, item.getActionTarget());
            }
        }
    }

    // -------------------------------------------------------
    // 特定の重要な GadgetMenuChoice エントリの検証
    // -------------------------------------------------------

    @Test
    public void testPunishIsToolGroupAndBodyTarget() {
        assertEquals(MainCategoryName.TOOL, GadgetMenuChoice.PUNISH.getGroup());
        assertEquals(ActionTarget.BODY, GadgetMenuChoice.PUNISH.getActionTarget());
        assertEquals(ActionControl.LEFT_CLICK, GadgetMenuChoice.PUNISH.getActionControl());
    }

    @Test
    public void testYuCleanIsImmediateTarget() {
        assertEquals(ActionTarget.IMMEDIATE, GadgetMenuChoice.YU_CLEAN.getActionTarget());
        assertEquals(MainCategoryName.CLEAN, GadgetMenuChoice.YU_CLEAN.getGroup());
    }

    @Test
    public void testNormalFoodHasFoodClass() {
        assertNotNull(GadgetMenuChoice.NORMAL.getGadgetClass());
        assertEquals(org.simyukkuri.entity.core.world.item.Food.class, GadgetMenuChoice.NORMAL.getGadgetClass());
    }

    @Test
    public void testToStringReturnsDisplayName() {
        String str = GadgetMenuChoice.PUNISH.toString();
        assertNotNull(str);
        assertFalse(str.isEmpty(), "toString() が空文字ではないこと");
    }

    @Test
    public void testOkazariHideIsAccessoryGroup() {
        assertEquals(MainCategoryName.ACCESSORY, GadgetMenuChoice.OKAZARI_HIDE.getGroup());
        assertEquals(ActionTarget.BODY, GadgetMenuChoice.OKAZARI_HIDE.getActionTarget());
    }

    @Test
    public void testGetInitOptionReturnsNonNegative() {
        for (GadgetMenuChoice item : GadgetMenuChoice.values()) {
            assertTrue(item.getInitOption() >= 0,
                    "initOption が 0 以上であること: " + item.name());
        }
    }

    // -------------------------------------------------------
    // GadgetMenu.getXxxCategory() の検証
    // -------------------------------------------------------

    @Test
    public void testMainCategoryContainsTool() {
        GadgetMenuChoice[] main = GadgetMenu.getMainCategory();
        assertTrue(main.length > 0);
        assertEquals(GadgetMenuChoice.TOOL, main[0]);
    }

    @Test
    public void testToolCategoryFirstIsPunish() {
        GadgetMenuChoice[] tool = GadgetMenu.getToolCategory();
        assertTrue(tool.length > 0);
        assertEquals(GadgetMenuChoice.PUNISH, tool[0]);
    }

    @Test
    public void testCleanCategoryContainsAll() {
        GadgetMenuChoice[] clean = GadgetMenu.getCleanCategory();
        boolean hasAll = false;
        for (GadgetMenuChoice g : clean) {
            if (g == GadgetMenuChoice.ALL) {
                hasAll = true;
                break;
            }
        }
        assertTrue(hasAll, "クリーンカテゴリに ALL が含まれていること");
    }

    @Test
    public void testAmpouleCategory_notEmpty() {
        GadgetMenuChoice[] amp = GadgetMenu.getAmpouleCategory();
        assertTrue(amp.length > 0);
    }
}
