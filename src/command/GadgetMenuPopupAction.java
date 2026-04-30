package src.command;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import src.command.GadgetMenu.ActionTarget;
import src.command.GadgetMenu.GadgetList;
import src.system.MainCommandUI;

/**
 * GadgetMenu popup の選択を UI に反映する listener.
 */
public final class GadgetMenuPopupAction implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		GadgetList sel = GadgetList.valueOf(command);
		MainCommandUI.getMainItemCombo().setSelectedIndex(sel.getGroup().ordinal() - 1);
		GadgetMenu.setSelectMain(GadgetList.values()[sel.getGroup().ordinal() - 1]);
		GadgetMenu.setSelectSub(sel);
		GadgetMenu.setActionHelp(sel);
		MainCommandUI.getSubItemCombo().setSelectedIndex(getIndex(sel));

		if (sel.getActionTarget() == ActionTarget.IMMEDIATE) {
			GadgetAction.immediateEvaluate(sel);
		}

		GadgetMenu.setPopupDisplay(false);
	}

	private int getIndex(GadgetList item) {
		int num;
		num = GadgetMenu.getToolCategory().length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.getToolCategory()[i]) {
				return i;
			}
		}
		num = GadgetMenu.getToolCategory2().length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.getToolCategory2()[i]) {
				return i;
			}
		}
		num = GadgetMenu.getAmpouleCategory().length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.getAmpouleCategory()[i]) {
				return i;
			}
		}
		num = GadgetMenu.getFoodCategory().length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.getFoodCategory()[i]) {
				return i;
			}
		}
		num = GadgetMenu.getCleanCategory().length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.getCleanCategory()[i]) {
				return i;
			}
		}
		num = GadgetMenu.getOkazariCategory().length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.getOkazariCategory()[i]) {
				return i;
			}
		}
		num = GadgetMenu.getPantsCategory().length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.getPantsCategory()[i]) {
				return i;
			}
		}
		num = GadgetMenu.getFloorCategory().length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.getFloorCategory()[i]) {
				return i;
			}
		}
		num = GadgetMenu.getBarrierCategory().length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.getBarrierCategory()[i]) {
				return i;
			}
		}
		num = GadgetMenu.getToysCategory().length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.getToysCategory()[i]) {
				return i;
			}
		}
		num = GadgetMenu.getConveyorCategory().length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.getConveyorCategory()[i]) {
				return i;
			}
		}
		num = GadgetMenu.getVoiceCategory().length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.getVoiceCategory()[i]) {
				return i;
			}
		}
		num = GadgetMenu.getTestCategory().length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.getTestCategory()[i]) {
				return i;
			}
		}
		return 0;
	}
}
