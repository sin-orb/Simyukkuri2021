package src.system;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import src.SimYukkuri;
import src.base.Body;
import src.command.GadgetMenu;
import src.command.GadgetMenu.GadgetList;
import src.draw.MyPane;
import src.draw.Terrarium;
import src.system.MainCommandUI.OptionPopup;
import src.system.MainCommandUI.SystemButtonLabel;
import src.system.MainCommandUI.ToolButtonLabel;
import src.util.IniFileUtil;

/**
 * メインコマンドリスナ
 */
public class MainCommandListener {
	/**
	 * ゲームスピードリスナ
	 */
	public class GameSpeedComboBoxListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			synchronized(SimYukkuri.lock) {
			    if (e.getStateChange() == ItemEvent.SELECTED){
			    	if (e.getSource() == MainCommandUI.gameSpeedCombo) {
			    		MainCommandUI.selectedGameSpeed = MainCommandUI.gameSpeedCombo.getSelectedIndex();
			    	}
			    }
			}
		}
	}
	/**
	 * アイテムリスナ
	 */
	public class MainItemComboBoxListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			synchronized(SimYukkuri.lock) {
			    if (e.getStateChange() == ItemEvent.SELECTED){
			    	if (e.getSource() == MainCommandUI.mainItemCombo) {
			    		GadgetMenu.selectMain = GadgetMenu.MainCategory[MainCommandUI.mainItemCombo.getSelectedIndex()];
			    		GadgetMenu.setSelectCategory(GadgetMenu.selectMain, 0);
			    		GadgetMenu.selectSub = getSubItem(GadgetMenu.selectMain, 0);
			    	}
			    }
			}
		}
	}
	/**
	 * サブアイテムリスナ
	 */
	public class SubItemComboBoxListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			synchronized(SimYukkuri.lock) {
			    if (e.getStateChange() == ItemEvent.SELECTED){
			    	if (e.getSource() == MainCommandUI.subItemCombo) {
			    		int sel = MainCommandUI.subItemCombo.getSelectedIndex();
			    		GadgetMenu.selectSub = getSubItem(GadgetMenu.selectMain, sel);
			    	}
			    }
			}
		}
	}
	
	private GadgetList getSubItem(GadgetList mainSel, int subSel) {
		GadgetList ret;

		switch(mainSel) {
			case TOOL:
				ret = GadgetMenu.ToolCategory[subSel];
				break;
			case TOOL2:
				ret = GadgetMenu.ToolCategory2[subSel];
				break;
			case AMPOULE:
				ret = GadgetMenu.AmpouleCategory[subSel];
				break;
			case FOODS:
				ret = GadgetMenu.FoodCategory[subSel];
				break;
			case CLEAN:
				ret = GadgetMenu.CleanCategory[subSel];
				break;
			case ACCESSORY:
				ret = GadgetMenu.OkazariCategory[subSel];
				break;
			case PANTS:
				ret = GadgetMenu.PantsCategory[subSel];
				break;
			case FLOOR:
				ret = GadgetMenu.FloorCategory[subSel];
				break;
			case BARRIER:
				ret = GadgetMenu.BarrierCategory[subSel];
				break;
			case TOYS:
				ret = GadgetMenu.ToysCategory[subSel];
				break;
			case CONVEYOR:
				ret = GadgetMenu.ConveyorCategory[subSel];
				break;
			case VOICE:
				ret = GadgetMenu.VoiceCategory[subSel];
				break;
			case TEST:
				ret = GadgetMenu.TestCategory[subSel];
				break;
			default:
				ret = null;
				break;
		}
		return ret;
	}
	/**
	 * ボタンリスナ
	 */
	public class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized(SimYukkuri.lock) {
				if (!SimYukkuri.initialized) {
					return;
				}
			}
			
			Object source = e.getSource();
			JButton[] items = MainCommandUI.systemButton;

			if(source.equals(items[SystemButtonLabel.SAVE.ordinal()])) {
				SimYukkuri.simYukkuri.doSave();
			}
			else if(source.equals(items[SystemButtonLabel.LOAD.ordinal()])) {
				SimYukkuri.simYukkuri.doLoad();
			}
			else if(source.equals(items[SystemButtonLabel.ADDBODY.ordinal()])) {
				SimYukkuri.mypane.initBodies();
			}
			else if(source.equals(items[SystemButtonLabel.PREV.ordinal()])) {
				LoggerYukkuri.addLogPage(-1);
			}
			else if(source.equals(items[SystemButtonLabel.LOG.ordinal()])) {
				LoggerYukkuri.show = !LoggerYukkuri.show;
			}
			else if(source.equals(items[SystemButtonLabel.NEXT.ordinal()])) {
				LoggerYukkuri.addLogPage(1);
			}
			else if(source.equals(items[SystemButtonLabel.LOGCLEAR.ordinal()])) {
				LoggerYukkuri.clearLog();
				LoggerYukkuri.clearLogTime = Terrarium.operationTime;
			}
			else if(source.equals(MainCommandUI.playerButton[ToolButtonLabel.MOVE.ordinal()])) {
				if(MainCommandUI.playerButton[ToolButtonLabel.MOVE.ordinal()].isSelected()) {
					MainCommandUI.mapWindow.setVisible(true);
				} else {
					MainCommandUI.mapWindow.setVisible(false);
				}
			}
			else if(source.equals(MainCommandUI.playerButton[ToolButtonLabel.BAG.ordinal()])) {
				if(MainCommandUI.playerButton[ToolButtonLabel.BAG.ordinal()].isSelected()) {
					MainCommandUI.itemWindow.setVisible(true);
				} else {
					MainCommandUI.itemWindow.setVisible(false);
					SimYukkuri.world.player.setHoldItem(null);
				}
			}
			else if(source.equals(MainCommandUI.scriptButton)) {
				MyPane.isDisableScript = MainCommandUI.scriptButton.isSelected();
			}
			else if(source.equals(MainCommandUI.targetButton)) {
				MyPane.isEnableTarget = MainCommandUI.targetButton.isSelected();
			}
			else if(source.equals(MainCommandUI.helpButton)) {
				MyPane.isDisableHelp = MainCommandUI.helpButton.isSelected();
			}
			else if(source.equals(MainCommandUI.optionButton)) {
				if(MainCommandUI.optionButton.isSelected()) {
					MainCommandUI.optionPopup.show(MainCommandUI.optionButton, 0, MainCommandUI.optionButton.getHeight());
				} else {
					MainCommandUI.optionPopup.setVisible(false);
				}
			}
			else if(source.equals(MainCommandUI.pinButton)) {
				if(MyPane.selectBody != null && !MyPane.selectBody.isRemoved())
				{
					if(MainCommandUI.pinButton.isSelected()) MyPane.selectBody.setPin(true);
					else MyPane.selectBody.setPin(false);
				}
			}
		}
	}
	/**
	 * オプションメニューリスナ
	 */
	public class OptionMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();

			OptionPopup sel = OptionPopup.valueOf(command);
			switch(sel) {
				case INI_RELOAD:
					SimYukkuri.mypane.loadImage(false, false, false, false, false, true);
					for (Map.Entry<Integer, Body> entry : SimYukkuri.world.getCurrentMap().body.entrySet()) {
						Body b = entry.getValue();
						{
							IniFileUtil.readIniFile(b, true);
							IniFileUtil.readYukkuriIniFile(b, true);
						}
					}
					break;
				default:
					break;
			}
			MainCommandUI.optionPopup.setVisible(false);
			MainCommandUI.optionButton.setSelected(false);
		}
	}
	/**
	 * オプションポップアップリスナ
	 */
	public class OptionPopupListener implements PopupMenuListener {

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			MainCommandUI.optionButton.setSelected(false);
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
		}
		
	}

}


