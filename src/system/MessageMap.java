package src.system;

import java.util.HashMap;
import java.util.Map;

/*****************************************************
 * 1アクションのメッセージ格納
 */
public class MessageMap
{
	/** 現在有効なサブタグ */
	public enum Tag {
		normal,
		rude,
		baby,
		child,
		adult,
		damage,
		footbake,
		pants,
		loveplayer,
		dislikeplayer,
		ununSlave,
		fool,
		wise,
	}
	/** ゲスでないゆっくりのフラグ */
	public boolean normalFlag = false;
	/** ゲスゆっくりフラグ */
	public boolean rudeFlag = false;
	/** ゲスでないゆっくりのタグ */
	public boolean[] normalTag = null;
	/** ゲスゆっくりのタグ */
	public boolean[] rudeTag = null;
	/** マップ */
	public Map<String, String[]> map = null;
	/**
	 * コンストラクタ.
	 */
	public MessageMap()
	{
		rudeFlag = false;
		normalTag = new boolean[Tag.values().length];
		rudeTag = new boolean[Tag.values().length];
		map = new HashMap<String, String[]>();
	}
}

