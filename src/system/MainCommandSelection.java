package src.system;

import src.command.GadgetMenu;
import src.command.GadgetMenu.GadgetList;

/**
 * Main command の選択状態補助.
 */
public final class MainCommandSelection {

	private MainCommandSelection() {
	}

	public static GadgetList getSubItem(GadgetList mainSel, int subSel) {
		switch (mainSel) {
		case TOOL:
			return GadgetMenu.getToolCategory()[subSel];
		case TOOL2:
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
		case TEST:
			return GadgetMenu.getTestCategory()[subSel];
		default:
			return null;
		}
	}
}
