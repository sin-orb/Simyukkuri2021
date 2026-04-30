package src.system;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import src.SimYukkuri;
import src.command.GadgetMenu;

/**
 * メイン項目の選択反映.
 */
public final class MainItemComboBoxListener implements ItemListener {
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
