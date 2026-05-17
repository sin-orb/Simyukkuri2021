package org.simyukkuri.ui;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * popup の表示中だけゲーム速度を止める listener.
 */
public final class ItemPopupSpeedAction implements PopupMenuListener {
	private int speedBackup;

	/** ポップアップ表示時にゲームを停止する。 */
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		speedBackup = MainCommandUI.getSelectedGameSpeed();
		MainCommandUI.getGameSpeedCombo().setSelectedIndex(0);
	}

	/** ポップアップ非表示時にゲーム速度を復元する。 */
	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		MainCommandUI.setSelectedGameSpeed(speedBackup);
		MainCommandUI.getGameSpeedCombo().setSelectedIndex(speedBackup);
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
	}
}
