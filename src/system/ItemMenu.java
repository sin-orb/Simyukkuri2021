package src.system;

import java.awt.event.MouseEvent;
import java.util.Locale;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.draw.Point4y;
import src.draw.Translate;
import src.game.Shit;
import src.game.Vomit;
import src.system.ItemListener.GetMenuAction;
import src.system.ItemListener.GetPopupAction;
import src.system.ItemListener.ShapeMenuAction;
import src.system.ItemListener.ShapePopupAction;
import src.system.ItemListener.UseMenuAction;
import src.system.ItemListener.UsePopupAction;


/**********************************************
 * オブジェクトコンテキストメニューのまとめ
 */
public class ItemMenu {

	/** クリック対象とメニュー項目の選択可否 */
	public static enum GetMenuTarget {
		NONE(false, false),
		BODY(true, true),
		SHIT(true, false),
		VOMIT(true, false),
		FOOD(true, false)
		;
		public boolean canPickup;
		public boolean canStatus;
		private GetMenuTarget(boolean pick, boolean stat) {
			this.canPickup = pick;
			this.canStatus = stat;
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
		FARM(false, true, true)
		;
		public boolean canSetup;
		public boolean canSort;
		public boolean isFarm;
		private ShapeMenuTarget(boolean setup, boolean sort, boolean farm) {
			this.canSetup = setup;
			this.canSort = sort;
			this.isFarm = farm;
		}
	}

	/** 素手のとき */
	public static enum GetMenu {
		PICKUP(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage())? "持つ": "Take"),
		STATUS(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage())? "ステータス": "Status")
		;
		public String name;
		private GetMenu(String str) {
			this.name = str;
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
		EAT("食べさせる(開発中)")
		;
		public String name;
		private UseMenu(String str) {
			this.name = str;
		}
		@Override
		public String toString() {
			return this.name;
		}
	}
	
	/** シェイプメニュー */
	public static enum ShapeMenu {
		SETUP(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage())? "設定変更": "Change Settings"),
		HERVEST(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage())? "収穫": "Harvest"),
		TOP(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage())? "最上位へ": "To Highest"),
		UP(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage())? "ひとつ上へ": "higher one"),
		DOWN(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage())? "ひとつ下へ": "lower one"),
		BOTTOM(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage())? "最下位へ": "To Lowest")
		;
		public String name;
		private ShapeMenu(String str) {
			this.name = str;
		}
		@Override
		public String toString() {
			return this.name;
		}
	}
	/** 取得するポップアップ */
	public static JPopupMenu getPopup;
	private static JMenuItem[] getMenu;
	/** 取得のターゲット */
	public static Obj getTarget;
	/** 使用するポップアップ */
	public static JPopupMenu usePopup;
	private static JMenuItem[] useMenu;
	/** 使用のターゲット */
	public static Obj useTarget;
	/** シェイプのポップアップ */
	public static JPopupMenu shapePopup;
	private static JMenuItem[] shapeMenu;
	/** シェイプのターゲット */
	public static FieldShapeBase shapeTarget;

	/**
	 * ポップアップメニューを作成する.
	 */
	public static final void createPopupMenu() {
		
		ItemListener pack = new ItemListener();

		GetMenuAction getact = pack.new GetMenuAction();
		GetPopupAction getpop = pack.new GetPopupAction();
		getPopup = new JPopupMenu();
		getPopup.addPopupMenuListener(getpop);
		getMenu = new JMenuItem[GetMenu.values().length];
		for(int i = 0; i < getMenu.length; i++) {
			getMenu[i] = new JMenuItem(GetMenu.values()[i].toString());
			getMenu[i].addActionListener(getact);
			getMenu[i].setActionCommand(GetMenu.values()[i].name());
			getPopup.add(getMenu[i]);
		}
		getTarget = null;
		
		UseMenuAction useact = pack.new UseMenuAction();
		UsePopupAction usepop = pack.new UsePopupAction();
		usePopup = new JPopupMenu();
		usePopup.addPopupMenuListener(usepop);
		useMenu = new JMenuItem[UseMenu.values().length];
		for(int i = 0; i < useMenu.length; i++) {
			useMenu[i] = new JMenuItem(UseMenu.values()[i].toString());
			useMenu[i].addActionListener(useact);
			useMenu[i].setActionCommand(UseMenu.values()[i].name());
			usePopup.add(useMenu[i]);
		}
		useTarget = null;

		ShapeMenuAction shpact = pack.new ShapeMenuAction();
		ShapePopupAction shppop = pack.new ShapePopupAction();
		shapePopup = new JPopupMenu();
		shapePopup.addPopupMenuListener(shppop);
		shapeMenu = new JMenuItem[ShapeMenu.values().length];
		for(int i = 0; i < shapeMenu.length; i++) {
			shapeMenu[i] = new JMenuItem(ShapeMenu.values()[i].toString());
			shapeMenu[i].addActionListener(shpact);
			shapeMenu[i].setActionCommand(ShapeMenu.values()[i].name());
			shapePopup.add(shapeMenu[i]);
		}
		shapeTarget = null;
	}

	/**
	 *  取得メニューを開く前にターゲットと有効なコマンド設定
	 * @param obj ターゲット
	 */
	public static final void setGetPopupMenu(Obj obj) {
		getTarget = obj;
		getMenu[0].setEnabled(obj.hasGetPopup().canPickup);
		getMenu[1].setEnabled(obj.hasGetPopup().canStatus);
	}

	/**
	 *  シェイプメニューを開く前にターゲットと有効なコマンド設定
	 * @param shp ターゲット
	 */
	public static final void setShapePopupMenu(FieldShapeBase shp) {
		shapeTarget = shp;
		shapeMenu[0].setEnabled(shp.hasShapePopup().canSetup);
		shapeMenu[1].setVisible(shp.hasShapePopup().isFarm);
		shapeMenu[2].setEnabled(shp.hasShapePopup().canSort);
	}

	/**
	 *  アイテム動作のキャンセル
	 * @param isholdCancel ホールドのキャンセル
	 */
	public static final void itemModeCancel(boolean isholdCancel) {
		getPopup.setVisible(false);
		usePopup.setVisible(false);
		shapePopup.setVisible(false);
		if(isholdCancel) {
			SimYukkuri.world.player.setHoldItem(null);
		}
	}
	
	/**
	 * アイテム配置
	 * @param e マウスイベント
	 */
	public static void dropItem(MouseEvent e) {
		Point4y pos = Translate.invertLimit(e.getX(), e.getY());
		Obj item = SimYukkuri.world.player.getHoldItem();
		MapPlaceData curMap = SimYukkuri.world.getCurrentMap();

		if(item instanceof Body) {
			Body b = (Body)item;
			b.setTaken(false);
			curMap.body.put(b.getUniqueID(), b);
		} else if(item instanceof Shit) {
			curMap.shit.put(item.objId, (Shit)item);
		} else if(item instanceof Vomit) {
			curMap.vomit.put(item.objId, (Vomit)item);
		}
		item.setX(pos.x);
		item.setY(pos.y);
		item.setZ(0);
		SimYukkuri.world.player.getItemList().removeElement(item);
		SimYukkuri.world.player.setHoldItem(null);
	}
}


