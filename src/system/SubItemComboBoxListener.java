package src.system;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import src.SimYukkuri;
import src.command.GadgetMenu;

/**
 * サブ項目の選択反映.
 */
public final class SubItemComboBoxListener implements ItemListener {
	@Override
	public void itemStateChanged(ItemEvent e) {
		synchronized (SimYukkuri.lock) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (e.getSource() == MainCommandUI.getSubItemCombo()) {
					int sel = MainCommandUI.getSubItemCombo().getSelectedIndex();
					GadgetMenu.setSelectSub(MainCommandSelection.getSubItem(GadgetMenu.getSelectMain(), sel));
				}
			}
		}
	}
}
