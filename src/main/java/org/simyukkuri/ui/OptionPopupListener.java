package org.simyukkuri.ui;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Option popup の開閉同期を行う.
 */
public final class OptionPopupListener implements PopupMenuListener {
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	}

	/** ポップアップ非表示時にオプションボタンの選択を解除する。 */
	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		MainCommandUi.getOptionButton().setSelected(false);
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
	}
}
