package org.simyukkuri.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.system.LoggerYukkuri;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameView;
import org.simyukkuri.util.GameWorld;

/**
 * Main command area のボタン入力を処理する.
 */
public final class ButtonListener implements ActionListener {
	/** @param e アクションイベント */
	@Override
	public void actionPerformed(ActionEvent e) {
		synchronized (SimYukkuri.lock) {
			if (!SimYukkuri.initialized) {
				return;
			}
		}

		Object source = e.getSource();
		JButton[] items = MainCommandUi.getSystemButton();

		if (source.equals(items[MainCommandUi.SystemButtonLabel.SAVE.ordinal()])) {
			SimYukkuri.simYukkuri.doSave();
		} else if (source.equals(items[MainCommandUi.SystemButtonLabel.LOAD.ordinal()])) {
			SimYukkuri.simYukkuri.doLoad();
		} else if (source.equals(items[MainCommandUi.SystemButtonLabel.ADDBODY.ordinal()])) {
			GameView.initBodies();
		} else if (source.equals(items[MainCommandUi.SystemButtonLabel.PREV.ordinal()])) {
			LoggerYukkuri.addLogPage(-1);
		} else if (source.equals(items[MainCommandUi.SystemButtonLabel.LOG.ordinal()])) {
			LoggerYukkuri.setShow(!LoggerYukkuri.isShow());
		} else if (source.equals(items[MainCommandUi.SystemButtonLabel.NEXT.ordinal()])) {
			LoggerYukkuri.addLogPage(1);
		} else if (source.equals(items[MainCommandUi.SystemButtonLabel.LOGCLEAR.ordinal()])) {
			LoggerYukkuri.clearLog();
			LoggerYukkuri.setClearLogTime(GameEnvironment.getOperationTime());
		} else if (source.equals(MainCommandUi.getPlayerButton()[MainCommandUi.ToolButtonLabel.MOVE.ordinal()])) {
			if (MainCommandUi.getPlayerButton()[MainCommandUi.ToolButtonLabel.MOVE.ordinal()].isSelected()) {
				MainCommandUi.getWorldWindow().setVisible(true);
			} else {
				MainCommandUi.getWorldWindow().setVisible(false);
			}
		} else if (source.equals(MainCommandUi.getPlayerButton()[MainCommandUi.ToolButtonLabel.BAG.ordinal()])) {
			if (MainCommandUi.getPlayerButton()[MainCommandUi.ToolButtonLabel.BAG.ordinal()].isSelected()) {
				MainCommandUi.getItemWindow().setVisible(true);
			} else {
				MainCommandUi.getItemWindow().setVisible(false);
				GameWorld.get().getPlayer().setHoldItem(null);
			}
		} else if (source.equals(MainCommandUi.getScriptButton())) {
			MyPane.setDisableScript(MainCommandUi.getScriptButton().isSelected());
		} else if (source.equals(MainCommandUi.getTargetButton())) {
			MyPane.setEnableTarget(MainCommandUi.getTargetButton().isSelected());
		} else if (source.equals(MainCommandUi.getHelpButton())) {
			MyPane.setDisableHelp(MainCommandUi.getHelpButton().isSelected());
		} else if (source.equals(MainCommandUi.getOptionButton())) {
			if (MainCommandUi.getOptionButton().isSelected()) {
				MainCommandUi.getOptionPopup().show(MainCommandUi.getOptionButton(), 0,
						MainCommandUi.getOptionButton().getHeight());
			} else {
				MainCommandUi.getOptionPopup().setVisible(false);
			}
		} else if (source.equals(MainCommandUi.getPinButton())) {
			Yukkuri selected = MyPane.getSelectedYukkuri();
			if (selected != null && !selected.isRemoved()) {
				selected.setPinned(MainCommandUi.getPinButton().isSelected());
			}
		}
	}
}
