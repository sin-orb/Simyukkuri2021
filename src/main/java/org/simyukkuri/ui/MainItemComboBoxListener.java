package org.simyukkuri.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.command.GadgetMenu;
import org.simyukkuri.system.MainCommandSelection;

/**
 * メイン項目の選択反映.
 */
public final class MainItemComboBoxListener implements ItemListener {
	/** @param e アイテム状態変更イベント */
	@Override
	public void itemStateChanged(ItemEvent e) {
		synchronized (SimYukkuri.lock) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (e.getSource() == MainCommandUI.getMainItemCombo()) {
					GadgetMenu.setSelectMain(GadgetMenu.getMainCategory()[MainCommandUI.getMainItemCombo().getSelectedIndex()]);
					GadgetMenu.setSelectCategory(GadgetMenu.getSelectMain(), 0);
					GadgetMenu.setSelectSub(MainCommandSelection.getSubItem(GadgetMenu.getSelectMain(), 0));
				}
			}
		}
	}
}
