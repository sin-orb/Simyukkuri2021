package org.simyukkuri.command;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.simyukkuri.command.GadgetMenu.ActionTarget;
import org.simyukkuri.command.GadgetMenu.GadgetMenuChoice;
import org.simyukkuri.ui.MainCommandUI;

/**
 * GadgetMenu popup の選択を UI に反映する listener.
 */
public final class GadgetMenuPopupAction implements ActionListener {

	/**
	 * Action performed.
	 *
	 * @param e イベント
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		GadgetMenuChoice sel = GadgetMenuChoice.valueOf(command);
		MainCommandUI.getMainItemCombo().setSelectedIndex(sel.getGroup().ordinal() - 1);
		GadgetMenu.setSelectMain(GadgetMenuChoice.values()[sel.getGroup().ordinal() - 1]);
		GadgetMenu.setSelectSub(sel);
		GadgetMenu.setActionHelp(sel);
		MainCommandUI.getSubItemCombo().setSelectedIndex(getIndex(sel));

		if (sel.getActionTarget() == ActionTarget.IMMEDIATE) {
			GadgetAction.immediateEvaluate(sel);
		}

		GadgetMenu.setPopupDisplay(false);
	}

	private int getIndex(GadgetMenuChoice item) {
		int categorySize;
		categorySize = GadgetMenu.getToolCategory().length;
		for (int i = 0; i < categorySize; i++) {
			if (item == GadgetMenu.getToolCategory()[i]) {
				return i;
			}
		}
		categorySize = GadgetMenu.getToolCategory2().length;
		for (int i = 0; i < categorySize; i++) {
			if (item == GadgetMenu.getToolCategory2()[i]) {
				return i;
			}
		}
		categorySize = GadgetMenu.getAmpouleCategory().length;
		for (int i = 0; i < categorySize; i++) {
			if (item == GadgetMenu.getAmpouleCategory()[i]) {
				return i;
			}
		}
		categorySize = GadgetMenu.getFoodCategory().length;
		for (int i = 0; i < categorySize; i++) {
			if (item == GadgetMenu.getFoodCategory()[i]) {
				return i;
			}
		}
		categorySize = GadgetMenu.getCleanCategory().length;
		for (int i = 0; i < categorySize; i++) {
			if (item == GadgetMenu.getCleanCategory()[i]) {
				return i;
			}
		}
		categorySize = GadgetMenu.getOkazariCategory().length;
		for (int i = 0; i < categorySize; i++) {
			if (item == GadgetMenu.getOkazariCategory()[i]) {
				return i;
			}
		}
		categorySize = GadgetMenu.getPantsCategory().length;
		for (int i = 0; i < categorySize; i++) {
			if (item == GadgetMenu.getPantsCategory()[i]) {
				return i;
			}
		}
		categorySize = GadgetMenu.getFloorCategory().length;
		for (int i = 0; i < categorySize; i++) {
			if (item == GadgetMenu.getFloorCategory()[i]) {
				return i;
			}
		}
		categorySize = GadgetMenu.getBarrierCategory().length;
		for (int i = 0; i < categorySize; i++) {
			if (item == GadgetMenu.getBarrierCategory()[i]) {
				return i;
			}
		}
		categorySize = GadgetMenu.getToysCategory().length;
		for (int i = 0; i < categorySize; i++) {
			if (item == GadgetMenu.getToysCategory()[i]) {
				return i;
			}
		}
		categorySize = GadgetMenu.getConveyorCategory().length;
		for (int i = 0; i < categorySize; i++) {
			if (item == GadgetMenu.getConveyorCategory()[i]) {
				return i;
			}
		}
		categorySize = GadgetMenu.getVoiceCategory().length;
		for (int i = 0; i < categorySize; i++) {
			if (item == GadgetMenu.getVoiceCategory()[i]) {
				return i;
			}
		}
		categorySize = GadgetMenu.getTestCategory().length;
		for (int i = 0; i < categorySize; i++) {
			if (item == GadgetMenu.getTestCategory()[i]) {
				return i;
			}
		}
		return 0;
	}
}
