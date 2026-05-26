package org.simyukkuri.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.command.GadgetMenu;
import org.simyukkuri.system.MainCommandSelection;

/**
 * サブ項目の選択反映.
 */
public final class SubItemComboBoxListener implements ItemListener {
	/**
	 * Item state changed.
	 *
	 * @param e イベント
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		synchronized (SimYukkuri.lock) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (e.getSource() == MainCommandUi.getSubItemCombo()) {
					int sel = MainCommandUi.getSubItemCombo().getSelectedIndex();
					GadgetMenu.setSelectSub(MainCommandSelection.getSubItem(GadgetMenu.getSelectMain(), sel));
				}
			}
		}
	}
}
