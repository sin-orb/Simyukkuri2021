package src.game;

import java.beans.Transient;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;

import src.base.Obj;
import src.system.MainCommandUI;



/*********************************************

	プレイヤー情報

*/
public class Player extends Obj implements Serializable {

	private static final long serialVersionUID = 2359877855058696480L;
	// 所持金
	private long cash;
	// 所持品
	private DefaultListModel<Obj> itemList;
	// アイテム
	private Obj holdItem;
	// セーブ用アイテムリスト
	private List<Obj> itemForSave;
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
		itemForSave = new LinkedList<>();
	}
	/**
	 * アイテムリストを設定する.
	 * @param list アイテムリスト
	 */
	public void setItemList(DefaultListModel<Obj> list) {
		itemList = list;
	}
	public Obj getHoldItem() {
		return holdItem;
	}
	public void setHoldItem(Obj holdItem) {
		this.holdItem = holdItem;
	}
	@Transient
	public DefaultListModel<Obj> getItemList() {
		return itemList;
	}
	public List<Obj> getItemForSave() {
		return itemForSave;
	}
	public void setItemForSave(List<Obj> itemForSave) {
		this.itemForSave = itemForSave;
	}
	
	
}


