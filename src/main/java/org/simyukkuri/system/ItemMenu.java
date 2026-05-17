package org.simyukkuri.system;

import java.awt.event.MouseEvent;
import java.util.Locale;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.simyukkuri.draw.Point4y;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.field.FieldShape;
import org.simyukkuri.ui.ItemGetMenuAction;
import org.simyukkuri.ui.ItemPopupNoopAction;
import org.simyukkuri.ui.ItemPopupSpeedAction;
import org.simyukkuri.ui.ItemShapeMenuAction;
import org.simyukkuri.ui.ItemUseMenuAction;
import org.simyukkuri.util.GameWorld;

/**********************************************
 * オブジェクトコンテキストメニューのまとめ
 */
public class ItemMenu {

	/** クリック対象とメニュー項目の選択可否 */
	public static enum GetMenuTarget {
		NONE(false, false, false),
		BODY(true, true, true),
		SHIT(true, false, true),
		VOMIT(true, false, true),
		FOOD(true, false, true),
		STALK(false, false, true);

		private final boolean canPickup;
		private final boolean canStatus;
		private final boolean canDebug;

		private GetMenuTarget(boolean pick, boolean stat, boolean debug) {
			this.canPickup = pick;
			this.canStatus = stat;
			this.canDebug = debug;
		}

		/**
		 * このターゲットに対して「持つ」操作が有効かどうかを返す。
		 *
		 * @return 「持つ」が有効なら true
		 */
		public boolean canPickup() {
			return canPickup;
		}

		/**
		 * このターゲットに対してステータス表示が有効かどうかを返す。
		 *
		 * @return ステータス表示が有効なら true
		 */
		public boolean canStatus() {
			return canStatus;
		}

		/**
		 * このターゲットに対してデバッグメニューが有効かどうかを返す。
		 *
		 * @return デバッグが有効なら true
		 */
		public boolean canDebug() {
			return canDebug;
		}
	}

	/** メニューのターゲット */
	public static enum UseMenuTarget {
		NONE,
		BODY,
		SHIT,
	}

	/** シェイプメニューのターゲット */
	public static enum ShapeMenuTarget {
		NONE(false, false, false),
		BELT(true, true, false),
		POOL(false, true, false),
		FARM(false, true, true);

		private final boolean canSetup;
		private final boolean canSort;
		private final boolean isFarm;

		private ShapeMenuTarget(boolean setup, boolean sort, boolean farm) {
			this.canSetup = setup;
			this.canSort = sort;
			this.isFarm = farm;
		}

		/**
		 * このシェイプターゲットで「設定変更」操作が有効かどうかを返す。
		 *
		 * @return 設定変更が有効なら true
		 */
		public boolean canSetup() {
			return canSetup;
		}

		/**
		 * このシェイプターゲットで「並び順変更」操作が有効かどうかを返す。
		 *
		 * @return 並び順変更が有効なら true
		 */
		public boolean canSort() {
			return canSort;
		}

		/**
		 * このシェイプターゲットが農場かどうかを返す。農場の場合は「収穫」メニューが表示される。
		 *
		 * @return 農場なら true
		 */
		public boolean isFarm() {
			return isFarm;
		}
	}

	/** 素手のとき */
	public static enum GetMenu {
		PICKUP(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage()) ? "持つ" : "Take"),
		STATUS(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage()) ? "ステータス" : "Status");

		private final String name;

		private GetMenu(String str) {
			this.name = str;
		}

		/**
		 * このメニュー項目の表示名を返す。
		 *
		 * @return ロケールに応じた表示名
		 */
		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	/** アイテム持ち */
	public static enum UseMenu {
		TAKE("あげる(開発中)"),
		THROW("ぶつける(開発中)"),
		EAT("食べさせる(開発中)");

		private final String name;

		private UseMenu(String str) {
			this.name = str;
		}

		/**
		 * このメニュー項目の表示名を返す。
		 *
		 * @return ロケールに応じた表示名
		 */
		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	/** シェイプメニュー */
	public static enum ShapeMenu {
		SETUP(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage()) ? "設定変更" : "Change Settings"),
		HARVEST(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage()) ? "収穫" : "Harvest"),
		TOP(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage()) ? "最上位へ" : "To Highest"),
		UP(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage()) ? "ひとつ上へ" : "higher one"),
		DOWN(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage()) ? "ひとつ下へ" : "lower one"),
		BOTTOM(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage()) ? "最下位へ" : "To Lowest");

		private final String name;

		private ShapeMenu(String str) {
			this.name = str;
		}

		/**
		 * このメニュー項目の表示名を返す。
		 *
		 * @return ロケールに応じた表示名
		 */
		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	/** 取得するポップアップ */
	private static JPopupMenu getPopup;
	private static JMenuItem[] getMenu;
	/** 取得のターゲット */
	private static Entity getTarget;
	/** 使用するポップアップ */
	private static JPopupMenu usePopup;
	private static JMenuItem[] useMenu;
	/** 使用のターゲット */
	private static Entity useTarget;
	/** シェイプのポップアップ */
	private static JPopupMenu shapePopup;
	private static JMenuItem[] shapeMenu;
	/** シェイプのターゲット */
	private static FieldShape shapeTarget;

	/**
	 * ポップアップメニューを作成する.
	 */
	public static final void createPopupMenu() {

		ItemGetMenuAction getact = new ItemGetMenuAction();
		ItemPopupSpeedAction getpop = new ItemPopupSpeedAction();
		getPopup = new JPopupMenu();
		getPopup.addPopupMenuListener(getpop);
		getMenu = new JMenuItem[GetMenu.values().length];
		for (int i = 0; i < getMenu.length; i++) {
			getMenu[i] = new JMenuItem(GetMenu.values()[i].toString());
			getMenu[i].addActionListener(getact);
			getMenu[i].setActionCommand(GetMenu.values()[i].name());
			getPopup.add(getMenu[i]);
		}
		getTarget = null;

		ItemUseMenuAction useact = new ItemUseMenuAction();
		ItemPopupSpeedAction usepop = new ItemPopupSpeedAction();
		usePopup = new JPopupMenu();
		usePopup.addPopupMenuListener(usepop);
		useMenu = new JMenuItem[UseMenu.values().length];
		for (int i = 0; i < useMenu.length; i++) {
			useMenu[i] = new JMenuItem(UseMenu.values()[i].toString());
			useMenu[i].addActionListener(useact);
			useMenu[i].setActionCommand(UseMenu.values()[i].name());
			usePopup.add(useMenu[i]);
		}
		useTarget = null;

		ItemShapeMenuAction shpact = new ItemShapeMenuAction();
		ItemPopupNoopAction shppop = new ItemPopupNoopAction();
		shapePopup = new JPopupMenu();
		shapePopup.addPopupMenuListener(shppop);
		shapeMenu = new JMenuItem[ShapeMenu.values().length];
		for (int i = 0; i < shapeMenu.length; i++) {
			shapeMenu[i] = new JMenuItem(ShapeMenu.values()[i].toString());
			shapeMenu[i].addActionListener(shpact);
			shapeMenu[i].setActionCommand(ShapeMenu.values()[i].name());
			shapePopup.add(shapeMenu[i]);
		}
		shapeTarget = null;
	}

	/**
	 * 取得メニューを開く前にターゲットと有効なコマンド設定
	 * 
	 * @param obj ターゲット
	 */
	public static final void setGetPopupMenu(Entity obj) {
		getTarget = obj;
		getMenu[0].setEnabled(obj.hasGetPopup().canPickup());
		getMenu[1].setEnabled(obj.hasGetPopup().canStatus());
	}

	/**
	 * シェイプメニューを開く前にターゲットと有効なコマンド設定
	 * 
	 * @param shp ターゲット
	 */
	public static final void setShapePopupMenu(FieldShape shp) {
		shapeTarget = shp;
		shapeMenu[0].setEnabled(shp.hasShapePopup().canSetup());
		shapeMenu[1].setVisible(shp.hasShapePopup().isFarm());
		shapeMenu[2].setEnabled(shp.hasShapePopup().canSort());
	}

	/**
	 * アイテム動作のキャンセル
	 * 
	 * @param isholdCancel ホールドのキャンセル
	 */
	public static final void itemModeCancel(boolean isholdCancel) {
		getPopup.setVisible(false);
		usePopup.setVisible(false);
		shapePopup.setVisible(false);
		if (isholdCancel) {
			GameWorld.get().getPlayer().setHoldItem(null);
		}
	}

	/**
	 * 「持つ」ポップアップメニューを返す。
	 *
	 * @return 取得用ポップアップメニュー
	 */
	public static JPopupMenu getGetPopup() {
		return getPopup;
	}

	/**
	 * 「持つ」ポップアップメニューをセットする。
	 *
	 * @param getPopup 新しいポップアップメニュー
	 */
	public static void setGetPopup(JPopupMenu getPopup) {
		ItemMenu.getPopup = getPopup;
	}

	/**
	 * 「持つ」メニューの操作対象エンティティを返す。
	 *
	 * @return 現在の取得ターゲット
	 */
	public static Entity getGetTarget() {
		return getTarget;
	}

	/**
	 * 「持つ」メニューの操作対象エンティティをセットする。
	 *
	 * @param getTarget 新しい取得ターゲット
	 */
	public static void setGetTarget(Entity getTarget) {
		ItemMenu.getTarget = getTarget;
	}

	/**
	 * 「使う」ポップアップメニューを返す。
	 *
	 * @return 使用用ポップアップメニュー
	 */
	public static JPopupMenu getUsePopup() {
		return usePopup;
	}

	/**
	 * 「使う」ポップアップメニューをセットする。
	 *
	 * @param usePopup 新しいポップアップメニュー
	 */
	public static void setUsePopup(JPopupMenu usePopup) {
		ItemMenu.usePopup = usePopup;
	}

	/**
	 * 「使う」メニューの操作対象エンティティを返す。
	 *
	 * @return 現在の使用ターゲット
	 */
	public static Entity getUseTarget() {
		return useTarget;
	}

	/**
	 * 「使う」メニューの操作対象エンティティをセットする。
	 *
	 * @param useTarget 新しい使用ターゲット
	 */
	public static void setUseTarget(Entity useTarget) {
		ItemMenu.useTarget = useTarget;
	}

	/**
	 * シェイプ（バリア・水場・農場）操作用ポップアップメニューを返す。
	 *
	 * @return シェイプポップアップメニュー
	 */
	public static JPopupMenu getShapePopup() {
		return shapePopup;
	}

	/**
	 * シェイプ操作用ポップアップメニューをセットする。
	 *
	 * @param shapePopup 新しいポップアップメニュー
	 */
	public static void setShapePopup(JPopupMenu shapePopup) {
		ItemMenu.shapePopup = shapePopup;
	}

	/**
	 * シェイプメニューの操作対象フィールドシェイプを返す。
	 *
	 * @return 現在のシェイプターゲット
	 */
	public static FieldShape getShapeTarget() {
		return shapeTarget;
	}

	/**
	 * シェイプメニューの操作対象フィールドシェイプをセットする。
	 *
	 * @param shapeTarget 新しいシェイプターゲット
	 */
	public static void setShapeTarget(FieldShape shapeTarget) {
		ItemMenu.shapeTarget = shapeTarget;
	}

	/**
	 * アイテム配置
	 * 
	 * @param e マウスイベント
	 */
	public static void dropItem(MouseEvent e) {
		Point4y pos = Translate.invertLimit(e.getX(), e.getY());
		Entity item = GameWorld.get().getPlayer().getHoldItem();
		WorldState curMap = GameWorld.get().getCurrentWorldState();

		if (item instanceof Yukkuri) {
			Yukkuri b = (Yukkuri) item;
			b.setTaken(false);
			curMap.getYukkuriRegistry().put(b.getUniqueID(), b);
		} else if (item instanceof Shit) {
			curMap.getShit().put(item.objId, (Shit) item);
		} else if (item instanceof Vomit) {
			curMap.getVomit().put(item.objId, (Vomit) item);
		}
		item.setCalcX(pos.getX());
		item.setCalcY(pos.getY());
		item.setCalcZ(0);
		GameWorld.get().getPlayer().getInventoryView().removeElement(item);
		GameWorld.get().getPlayer().setHoldItem(null);
	}
}
