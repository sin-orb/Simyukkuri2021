package src.system;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.command.DebugFrame;
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
			speedBackup = MainCommandUI.getSelectedGameSpeed();
			MainCommandUI.getGameSpeedCombo().setSelectedIndex(0);
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			// 時間を戻す
			MainCommandUI.setSelectedGameSpeed(speedBackup);
			MainCommandUI.getGameSpeedCombo().setSelectedIndex(speedBackup);
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
			speedBackup = MainCommandUI.getSelectedGameSpeed();
			MainCommandUI.getGameSpeedCombo().setSelectedIndex(0);
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			// 時間を戻す
			MainCommandUI.setSelectedGameSpeed(speedBackup);
			MainCommandUI.getGameSpeedCombo().setSelectedIndex(speedBackup);
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
					SimYukkuri.world.getPlayer().getItemList().addElement(ItemMenu.getGetTarget());
					if (ItemMenu.getGetTarget() instanceof Body) {
						Body b = (Body) ItemMenu.getGetTarget();
						b.removeAllStalks();
						b.setTaken(true);
						curMap.getBody().remove(b.getUniqueID());
					} else if (ItemMenu.getGetTarget() instanceof Shit) {
						curMap.getShit().remove(ItemMenu.getGetTarget().objId);
					} else if (ItemMenu.getGetTarget() instanceof Vomit) {
						curMap.getVomit().remove(ItemMenu.getGetTarget().objId);
					}
					ItemMenu.setGetTarget(null);
				}
				break;
			case STATUS:
				if (ItemMenu.getGetTarget() == null)
					return;
				Body b = (Body) ItemMenu.getGetTarget();
				ShowStatusFrame instance = ShowStatusFrame.getInstance();
				instance.giveBodyInfo(b);
				instance.setVisible(true);
				break;
			case DEBUG:
				if (ItemMenu.getGetTarget() == null)
					return;
				Obj o = ItemMenu.getGetTarget();
				DebugFrame df = new DebugFrame();
				df.setObjAndDisplay(o);
				df.setVisible(true);
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

			ItemMenu.getShapeTarget().executeShapePopup(m);
			ItemMenu.setShapeTarget(null);
		}
	}
}

