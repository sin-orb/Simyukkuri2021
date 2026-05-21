package org.simyukkuri.system;

import org.simyukkuri.command.GadgetMenu;
import org.simyukkuri.command.GadgetMenu.GadgetMenuChoice;

/**
 * Main command の選択状態補助.
 */
public final class MainCommandSelection {

	private MainCommandSelection() {
	}

	/**
	 * メインカテゴリとサブインデックスから対応するメニュー項目を返す。
	 * TOOL / BODY_CHANGE / AMPOULE など各カテゴリの配列から該当インデックスの項目を取得する。
	 *
	 * @param mainSel メインカテゴリ選択
	 * @param subSel  サブカテゴリのインデックス
	 * @return 対応する {@link GadgetMenuChoice}、カテゴリ未対応なら null
	 */
	public static GadgetMenuChoice getSubItem(GadgetMenuChoice mainSel, int subSel) {
		switch (mainSel) {
			case TOOL:
				return GadgetMenu.getToolCategory()[subSel];
			case BODY_CHANGE:
				return GadgetMenu.getToolCategory2()[subSel];
			case AMPOULE:
				return GadgetMenu.getAmpouleCategory()[subSel];
			case FOODS:
				return GadgetMenu.getFoodCategory()[subSel];
			case CLEAN:
				return GadgetMenu.getCleanCategory()[subSel];
			case ACCESSORY:
				return GadgetMenu.getOkazariCategory()[subSel];
			case PANTS:
				return GadgetMenu.getPantsCategory()[subSel];
			case FLOOR:
				return GadgetMenu.getFloorCategory()[subSel];
			case BARRIER:
				return GadgetMenu.getBarrierCategory()[subSel];
			case TOYS:
				return GadgetMenu.getToysCategory()[subSel];
			case CONVEYOR:
				return GadgetMenu.getConveyorCategory()[subSel];
			case VOICE:
				return GadgetMenu.getVoiceCategory()[subSel];
			case DEBUG:
				return GadgetMenu.getTestCategory()[subSel];
			default:
				return null;
		}
	}
}
