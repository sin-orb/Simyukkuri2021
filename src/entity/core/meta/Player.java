package src.entity.core.meta;

import java.beans.Transient;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;

import src.entity.core.Entity;
import src.system.MainCommandUI;

/*********************************************
 * 
 * プレイヤー情報
 * 
 */
public class Player extends Entity {

	private static final long serialVersionUID = 2359877855058696480L;
	// 所持金
	private long cash;
	// 所持品
	private DefaultListModel<Entity> itemList;
	// アイテム
	private Entity holdItem;
	// セーブ用アイテムリスト
	private List<Entity> itemForSave;

	/**
	 * 所持金を設定する.
	 * 
	 * @param val 所持金
	 */
	public void setCash(long val) {
		cash = val;
	}

	/**
	 * 所持金を取得する.
	 * 
	 * @return 所持金
	 */
	public long getCash() {
		return cash;
	}

	/**
	 * 所持金を増減する.
	 * 
	 * @param addcash 増減する金額
	 */
	public void addCash(int addcash) {
		cash += addcash;
		// 手持ち資金が更新されたら表示変更
		try {
			MainCommandUI.showPlayerStatus();
		} catch (Throwable ignore) {
			// Headless or UI init failure; ignore in non-GUI contexts.
		}
	}

	/**
	 * コンストラクタ.
	 */
	public Player() {
		cash = 10000;
		itemList = new DefaultListModel<Entity>();
		holdItem = null;
		itemForSave = new LinkedList<>();
	}

	/**
	 * アイテムリストを設定する.
	 * 
	 * @param list アイテムリスト
	 */
	public void setItemList(DefaultListModel<Entity> list) {
		itemList = list;
	}

	public Entity getHoldItem() {
		return holdItem;
	}

	public void setHoldItem(Entity holdItem) {
		this.holdItem = holdItem;
	}

	@Transient
	public DefaultListModel<Entity> getItemList() {
		return itemList;
	}

	public List<Entity> getItemForSave() {
		return itemForSave;
	}

	public void setItemForSave(List<Entity> itemForSave) {
		this.itemForSave = itemForSave;
	}

}
