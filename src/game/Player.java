package src.game;

import java.io.Serializable;

import javax.swing.DefaultListModel;

import src.base.Obj;
import src.system.MainCommandUI;



/*********************************************

	プレイヤー情報

*/
public class Player extends Obj implements Serializable {
	static final long serialVersionUID = 1L;

	// 所持金
	public long cash;
	// 所持品
	public DefaultListModel<Obj> itemList;
	// アイテム
	public Obj holdItem;

	public void setCash(long val) {
		cash = val;
	}
	
	public long getCash() {
		return cash;
	}
	
	public void addCash( int addcash ) {
		cash += addcash;
		// 手持ち資金が更新されたら表示変更
		MainCommandUI.showPlayerStatus();
	}

	public Player() {
		cash = 10000;
		itemList = new DefaultListModel<Obj>();
		holdItem = null;
	}

	public void setItemList(DefaultListModel<Obj> list) {
		itemList = list;
	}
}


