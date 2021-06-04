package src.system;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import src.SimYukkuri;
import src.base.Body;
import src.command.ShowStatusFrame;
import src.game.Shit;
import src.game.Vomit;
import src.system.ItemMenu.GetMenu;
import src.system.ItemMenu.ShapeMenu;

/**********************************************
 * オブジェクトコンテキストメニューのアクション
 */
public class ItemListener {

	/**
	 *  取得ポップアップアクション
	 */
	public class GetPopupAction implements PopupMenuListener {
		private int speedBackup;

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			// 時間を止める
			speedBackup = MainCommandUI.selectedGameSpeed;
			MainCommandUI.gameSpeedCombo.setSelectedIndex(0);
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			// 時間を戻す
			MainCommandUI.selectedGameSpeed = speedBackup;
			MainCommandUI.gameSpeedCombo.setSelectedIndex(speedBackup);
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
		}
	}

	/**
	 * 使用ポップアップアクション
	 */
	public class UsePopupAction implements PopupMenuListener {
		private int speedBackup;

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			// 時間を止める
			speedBackup = MainCommandUI.selectedGameSpeed;
			MainCommandUI.gameSpeedCombo.setSelectedIndex(0);
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			// 時間を戻す
			MainCommandUI.selectedGameSpeed = speedBackup;
			MainCommandUI.gameSpeedCombo.setSelectedIndex(speedBackup);
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
		}
	}

	/**
	 * シェイプポップアップアクション
	 */
	public class ShapePopupAction implements PopupMenuListener {

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
		}
	}

	/**
	 * アイテム取得メニューアクション
	 */
	public class GetMenuAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			GetMenu m = GetMenu.valueOf(e.getActionCommand());
			if (m == null)
				return;

			MapPlaceData curMap = SimYukkuri.world.getCurrentMap();

			switch (m) {
			case PICKUP:
				synchronized (SimYukkuri.lock) {
					SimYukkuri.world.player.itemList.addElement(ItemMenu.getTarget);
					if (ItemMenu.getTarget instanceof Body) {
						Body b = (Body) ItemMenu.getTarget;
						b.removeAllStalks();
						b.setTaken(true);
						curMap.body.remove(b);
					} else if (ItemMenu.getTarget instanceof Shit) {
						curMap.shit.remove(ItemMenu.getTarget);
					} else if (ItemMenu.getTarget instanceof Vomit) {
						curMap.vomit.remove(ItemMenu.getTarget);
					}
					ItemMenu.getTarget = null;
				}
				break;
			case STATUS:
				if (ItemMenu.getTarget == null)
					return;
				Body b = (Body) ItemMenu.getTarget;
				ShowStatusFrame instance = ShowStatusFrame.getInstance();
				instance.giveBodyInfo(b);
				instance.setVisible(true);
				break;
			}
		}
	}

	/**
	 * アイテム使用メニューアクション
	 */
	public class UseMenuAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}

	/**
	 *  コンベア、池メニューアクション
	 */
	public class ShapeMenuAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			ShapeMenu m = ShapeMenu.valueOf(e.getActionCommand());
			if (m == null)
				return;

			ItemMenu.shapeTarget.executeShapePopup(m);
			ItemMenu.shapeTarget = null;
		}
	}
}
