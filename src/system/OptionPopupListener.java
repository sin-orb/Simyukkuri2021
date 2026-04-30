package src.system;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Option popup の開閉同期を行う.
 */
public final class OptionPopupListener implements PopupMenuListener {
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		MainCommandUI.getOptionButton().setSelected(false);
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
	}
}
