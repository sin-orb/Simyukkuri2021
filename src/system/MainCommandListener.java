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
			    	if (e.getSource() == MainCommandUI.getGameSpeedCombo()) {
			    		MainCommandUI.setSelectedGameSpeed(MainCommandUI.getGameSpeedCombo().getSelectedIndex());
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
			    	if (e.getSource() == MainCommandUI.getMainItemCombo()) {
			    		GadgetMenu.setSelectMain(GadgetMenu.getMainCategory()[MainCommandUI.getMainItemCombo().getSelectedIndex()]);
			    		GadgetMenu.setSelectCategory(GadgetMenu.getSelectMain(), 0);
			    		GadgetMenu.setSelectSub(getSubItem(GadgetMenu.getSelectMain(), 0));
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
			    	if (e.getSource() == MainCommandUI.getSubItemCombo()) {
			    		int sel = MainCommandUI.getSubItemCombo().getSelectedIndex();
			    		GadgetMenu.setSelectSub(getSubItem(GadgetMenu.getSelectMain(), sel));
			    	}
			    }
			}
		}
	}
	
	private GadgetList getSubItem(GadgetList mainSel, int subSel) {
		GadgetList ret;

		switch(mainSel) {
			case TOOL:
				ret = GadgetMenu.getToolCategory()[subSel];
				break;
			case TOOL2:
				ret = GadgetMenu.getToolCategory2()[subSel];
				break;
			case AMPOULE:
				ret = GadgetMenu.getAmpouleCategory()[subSel];
				break;
			case FOODS:
				ret = GadgetMenu.getFoodCategory()[subSel];
				break;
			case CLEAN:
				ret = GadgetMenu.getCleanCategory()[subSel];
				break;
			case ACCESSORY:
				ret = GadgetMenu.getOkazariCategory()[subSel];
				break;
			case PANTS:
				ret = GadgetMenu.getPantsCategory()[subSel];
				break;
			case FLOOR:
				ret = GadgetMenu.getFloorCategory()[subSel];
				break;
			case BARRIER:
				ret = GadgetMenu.getBarrierCategory()[subSel];
				break;
			case TOYS:
				ret = GadgetMenu.getToysCategory()[subSel];
				break;
			case CONVEYOR:
				ret = GadgetMenu.getConveyorCategory()[subSel];
				break;
			case VOICE:
				ret = GadgetMenu.getVoiceCategory()[subSel];
				break;
			case TEST:
				ret = GadgetMenu.getTestCategory()[subSel];
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
			JButton[] items = MainCommandUI.getSystemButton();

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
				LoggerYukkuri.setShow(!LoggerYukkuri.isShow());
			}
			else if(source.equals(items[SystemButtonLabel.NEXT.ordinal()])) {
				LoggerYukkuri.addLogPage(1);
			}
			else if(source.equals(items[SystemButtonLabel.LOGCLEAR.ordinal()])) {
				LoggerYukkuri.clearLog();
				LoggerYukkuri.setClearLogTime(Terrarium.getOperationTime());
			}
			else if(source.equals(MainCommandUI.getPlayerButton()[ToolButtonLabel.MOVE.ordinal()])) {
				if(MainCommandUI.getPlayerButton()[ToolButtonLabel.MOVE.ordinal()].isSelected()) {
					MainCommandUI.getMapWindow().setVisible(true);
				} else {
					MainCommandUI.getMapWindow().setVisible(false);
				}
			}
			else if(source.equals(MainCommandUI.getPlayerButton()[ToolButtonLabel.BAG.ordinal()])) {
				if(MainCommandUI.getPlayerButton()[ToolButtonLabel.BAG.ordinal()].isSelected()) {
					MainCommandUI.getItemWindow().setVisible(true);
				} else {
					MainCommandUI.getItemWindow().setVisible(false);
					SimYukkuri.world.getPlayer().setHoldItem(null);
				}
			}
			else if(source.equals(MainCommandUI.getScriptButton())) {
				MyPane.setDisableScript(MainCommandUI.getScriptButton().isSelected());
			}
			else if(source.equals(MainCommandUI.getTargetButton())) {
				MyPane.setEnableTarget(MainCommandUI.getTargetButton().isSelected());
			}
			else if(source.equals(MainCommandUI.getHelpButton())) {
				MyPane.setDisableHelp(MainCommandUI.getHelpButton().isSelected());
			}
			else if(source.equals(MainCommandUI.getOptionButton())) {
				if(MainCommandUI.getOptionButton().isSelected()) {
					MainCommandUI.getOptionPopup().show(MainCommandUI.getOptionButton(), 0, MainCommandUI.getOptionButton().getHeight());
				} else {
					MainCommandUI.getOptionPopup().setVisible(false);
				}
			}
			else if(source.equals(MainCommandUI.getPinButton())) {
				if(MyPane.getSelectBody() != null && !MyPane.getSelectBody().isRemoved())
				{
					if(MainCommandUI.getPinButton().isSelected()) MyPane.getSelectBody().setPin(true);
					else MyPane.getSelectBody().setPin(false);
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
					for (Map.Entry<Integer, Body> entry : SimYukkuri.world.getCurrentMap().getBody().entrySet()) {
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
			MainCommandUI.getOptionPopup().setVisible(false);
			MainCommandUI.getOptionButton().setSelected(false);
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
			MainCommandUI.getOptionButton().setSelected(false);
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
		}
		
	}

}




