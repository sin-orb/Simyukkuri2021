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
	/**
	 * 所持金を設定する.
	 * @param val 所持金
	 */
	public void setCash(long val) {
		cash = val;
	}
	/**
	 * 所持金を取得する.
	 * @return 所持金
	 */
	public long getCash() {
		return cash;
	}
	/**
	 * 所持金を増減する.
	 * @param addcash 増減する金額
	 */
	public void addCash( int addcash ) {
		cash += addcash;
		// 手持ち資金が更新されたら表示変更
		MainCommandUI.showPlayerStatus();
	}
	/**
	 * コンストラクタ.
	 */
	public Player() {
		cash = 10000;
		itemList = new DefaultListModel<Obj>();
		holdItem = null;
	}
	/**
	 * アイテムリストを設定する.
	 * @param list アイテムリスト
	 */
	public void setItemList(DefaultListModel<Obj> list) {
		itemList = list;
	}
}


