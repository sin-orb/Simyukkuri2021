package org.simyukkuri.ui;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * popup の表示中だけゲーム速度を止める listener.
 */
public final class ItemPopupSpeedAction implements PopupMenuListener {
	private int speedBackup;

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		speedBackup = MainCommandUI.getSelectedGameSpeed();
		MainCommandUI.getGameSpeedCombo().setSelectedIndex(0);
	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		MainCommandUI.setSelectedGameSpeed(speedBackup);
		MainCommandUI.getGameSpeedCombo().setSelectedIndex(speedBackup);
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
	}
}
