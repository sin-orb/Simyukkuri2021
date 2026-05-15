package org.simyukkuri.command;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * GadgetMenu の popup 構築を担当する helper.
 */
public final class GadgetMenuPopup {

	private GadgetMenuPopup() {
	}

	/**
	 * popup を初期化してカテゴリを追加する.
	 *
	 * @param popup 対象 popup
	 * @param action 選択時の listener
	 */
	public static void createPopupMenu(JPopupMenu popup, ActionListener action) {
		popup.removeAll();
		popup.add(createSubGroup(GadgetMenu.getMainCategory()[0], GadgetMenu.getToolCategory(), action));
		popup.add(createSubGroup(GadgetMenu.getMainCategory()[1], GadgetMenu.getToolCategory2(), action));
		popup.add(createSubGroup(GadgetMenu.getMainCategory()[2], GadgetMenu.getAmpouleCategory(), action));
		popup.add(createSubGroup(GadgetMenu.getMainCategory()[3], GadgetMenu.getFoodCategory(), action));
		popup.add(createSubGroup(GadgetMenu.getMainCategory()[4], GadgetMenu.getCleanCategory(), action));
		popup.add(createSubGroup(GadgetMenu.getMainCategory()[5], GadgetMenu.getOkazariCategory(), action));
		popup.add(createSubGroup(GadgetMenu.getMainCategory()[6], GadgetMenu.getPantsCategory(), action));
		popup.add(createSubGroup(GadgetMenu.getMainCategory()[7], GadgetMenu.getFloorCategory(), action));
		popup.add(createSubGroup(GadgetMenu.getMainCategory()[8], GadgetMenu.getBarrierCategory(), action));
		popup.add(createSubGroup(GadgetMenu.getMainCategory()[9], GadgetMenu.getToysCategory(), action));
		popup.add(createSubGroup(GadgetMenu.getMainCategory()[10], GadgetMenu.getConveyorCategory(), action));
		popup.add(createSubGroup(GadgetMenu.getMainCategory()[11], GadgetMenu.getVoiceCategory(), action));
		popup.add(createSubGroup(GadgetMenu.getMainCategory()[12], GadgetMenu.getTestCategory(), action));
	}

	private static JMenu createSubGroup(GadgetMenu.GadgetMenuChoice root, GadgetMenu.GadgetMenuChoice[] group, ActionListener action) {
		JMenu menu = new JMenu(root.getDisplayName());
		for (int i = 0; i < group.length; i++) {
			JMenuItem subMenu = new JMenuItem(group[i].getDisplayName());
			subMenu.addActionListener(action);
			subMenu.setActionCommand(group[i].name());
			menu.add(subMenu);
		}
		return menu;
	}
}
