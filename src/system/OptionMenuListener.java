package src.system;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import src.base.Body;
import src.util.GameView;
import src.util.GameWorld;
import src.util.IniFileUtil;

/**
 * Main command の option popup を処理する.
 */
public final class OptionMenuListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		MainCommandUI.OptionPopup sel = MainCommandUI.OptionPopup.valueOf(command);
		switch (sel) {
		case INI_RELOAD:
			GameView.loadImage(false, false, false, false, false, true);
			for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
				Body b = entry.getValue();
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
