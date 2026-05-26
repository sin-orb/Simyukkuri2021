package org.simyukkuri.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import org.simyukkuri.SimYukkuri;

/**
 * ゲームスピードの選択反映.
 */
public final class GameSpeedComboBoxListener implements ItemListener {
	/** @param e アイテム状態変更イベント */
	@Override
	public void itemStateChanged(ItemEvent e) {
		synchronized (SimYukkuri.lock) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (e.getSource() == MainCommandUi.getGameSpeedCombo()) {
					MainCommandUi.setSelectedGameSpeed(MainCommandUi.getGameSpeedCombo().getSelectedIndex());
				}
			}
		}
	}
}
