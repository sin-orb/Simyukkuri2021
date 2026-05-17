package org.simyukkuri.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.system.MainCommandSelection;
import org.simyukkuri.util.GameView;
import org.simyukkuri.util.GameWorld;
import org.simyukkuri.util.IniFileUtil;

/**
 * Main command の option popup を処理する.
 */
public final class OptionMenuListener implements ActionListener {
	/** @param e アクションイベント */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		MainCommandUI.OptionPopup sel = MainCommandUI.OptionPopup.valueOf(command);
		switch (sel) {
			case INI_RELOAD:
				GameView.loadImage(false, false, false, false, false, true);
				for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentWorldState().getYukkuriRegistry().entrySet()) {
					Yukkuri b = entry.getValue();
					IniFileUtil.readIniFile(b, true);
					IniFileUtil.readYukkuriIniFile(b, true);
				}
				break;
			default:
				break;
		}
		MainCommandUI.getOptionPopup().setVisible(false);
		MainCommandUI.getOptionButton().setSelected(false);
	}
}
