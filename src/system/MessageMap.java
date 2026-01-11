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
	private boolean normalFlag = false;
	/** ゲスゆっくりフラグ */
	private boolean rudeFlag = false;
	/** ゲスでないゆっくりのタグ */
	private boolean[] normalTag = null;
	/** ゲスゆっくりのタグ */
	private boolean[] rudeTag = null;
	/** マップ */
	private Map<String, String[]> map = null;
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
	
	public boolean isNormalFlag() {
		return normalFlag;
	}
	
	public void setNormalFlag(boolean normalFlag) {
		this.normalFlag = normalFlag;
	}
	
	public boolean isRudeFlag() {
		return rudeFlag;
	}
	
	public void setRudeFlag(boolean rudeFlag) {
		this.rudeFlag = rudeFlag;
	}
	
	public boolean[] getNormalTag() {
		return normalTag;
	}
	
	public void setNormalTag(boolean[] normalTag) {
		this.normalTag = normalTag;
	}
	
	public boolean[] getRudeTag() {
		return rudeTag;
	}
	
	public void setRudeTag(boolean[] rudeTag) {
		this.rudeTag = rudeTag;
	}
	
	public Map<String, String[]> getMap() {
		return map;
	}
	
	public void setMap(Map<String, String[]> map) {
		this.map = map;
	}
}

