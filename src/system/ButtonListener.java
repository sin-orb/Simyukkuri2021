package src.system;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import src.SimYukkuri;
import src.base.Body;
import src.draw.MyPane;
import src.util.GameEnvironment;
import src.util.GameView;
import src.util.GameWorld;

/**
 * Main command area のボタン入力を処理する.
 */
public final class ButtonListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		synchronized (SimYukkuri.lock) {
			if (!SimYukkuri.initialized) {
				return;
			}
		}

		Object source = e.getSource();
		JButton[] items = MainCommandUI.getSystemButton();

		if (source.equals(items[MainCommandUI.SystemButtonLabel.SAVE.ordinal()])) {
			SimYukkuri.simYukkuri.doSave();
		} else if (source.equals(items[MainCommandUI.SystemButtonLabel.LOAD.ordinal()])) {
			SimYukkuri.simYukkuri.doLoad();
		} else if (source.equals(items[MainCommandUI.SystemButtonLabel.ADDBODY.ordinal()])) {
			GameView.initBodies();
		} else if (source.equals(items[MainCommandUI.SystemButtonLabel.PREV.ordinal()])) {
			LoggerYukkuri.addLogPage(-1);
		} else if (source.equals(items[MainCommandUI.SystemButtonLabel.LOG.ordinal()])) {
			LoggerYukkuri.setShow(!LoggerYukkuri.isShow());
		} else if (source.equals(items[MainCommandUI.SystemButtonLabel.NEXT.ordinal()])) {
			LoggerYukkuri.addLogPage(1);
		} else if (source.equals(items[MainCommandUI.SystemButtonLabel.LOGCLEAR.ordinal()])) {
			LoggerYukkuri.clearLog();
			LoggerYukkuri.setClearLogTime(GameEnvironment.getOperationTime());
		} else if (source.equals(MainCommandUI.getPlayerButton()[MainCommandUI.ToolButtonLabel.MOVE.ordinal()])) {
			if (MainCommandUI.getPlayerButton()[MainCommandUI.ToolButtonLabel.MOVE.ordinal()].isSelected()) {
				MainCommandUI.getMapWindow().setVisible(true);
			} else {
				MainCommandUI.getMapWindow().setVisible(false);
			}
		} else if (source.equals(MainCommandUI.getPlayerButton()[MainCommandUI.ToolButtonLabel.BAG.ordinal()])) {
			if (MainCommandUI.getPlayerButton()[MainCommandUI.ToolButtonLabel.BAG.ordinal()].isSelected()) {
				MainCommandUI.getItemWindow().setVisible(true);
			} else {
				MainCommandUI.getItemWindow().setVisible(false);
				GameWorld.get().getPlayer().setHoldItem(null);
			}
		} else if (source.equals(MainCommandUI.getScriptButton())) {
			MyPane.setDisableScript(MainCommandUI.getScriptButton().isSelected());
		} else if (source.equals(MainCommandUI.getTargetButton())) {
			MyPane.setEnableTarget(MainCommandUI.getTargetButton().isSelected());
		} else if (source.equals(MainCommandUI.getHelpButton())) {
			MyPane.setDisableHelp(MainCommandUI.getHelpButton().isSelected());
		} else if (source.equals(MainCommandUI.getOptionButton())) {
			if (MainCommandUI.getOptionButton().isSelected()) {
				MainCommandUI.getOptionPopup().show(MainCommandUI.getOptionButton(), 0,
						MainCommandUI.getOptionButton().getHeight());
			} else {
				MainCommandUI.getOptionPopup().setVisible(false);
			}
		} else if (source.equals(MainCommandUI.getPinButton())) {
			Body selected = MyPane.getSelectBody();
			if (selected != null && !selected.isRemoved()) {
				selected.setPin(MainCommandUI.getPinButton().isSelected());
			}
		}
	}
}
