package org.simyukkuri.entity.core.meta;

import java.beans.Transient;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.ui.MainCommandUI;

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
	 * 所持アイテム一覧を設定する.
	 * 
	 * @param inventory 所持アイテム一覧
	 */
	public void setInventory(DefaultListModel<Entity> inventory) {
		itemList = inventory;
	}

	/** 現在手に持っているアイテムを返す。 */
	public Entity getHoldItem() {
		return holdItem;
	}

	/** 現在手に持つアイテムをセットする。 */
	public void setHoldItem(Entity holdItem) {
		this.holdItem = holdItem;
	}

	/** 所持アイテム一覧のビューモデルを返す。 */
	@Transient
	public DefaultListModel<Entity> getInventoryView() {
		return itemList;
	}

	/** セーブ用アイテムリストを返す。 */
	public List<Entity> getItemForSave() {
		return itemForSave;
	}

	/** セーブ用アイテムリストをセットする。 */
	public void setItemForSave(List<Entity> itemForSave) {
		this.itemForSave = itemForSave;
	}

}
