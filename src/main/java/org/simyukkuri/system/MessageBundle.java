package org.simyukkuri.system;

import java.util.HashMap;
import java.util.Map;

/*****************************************************
 * 1アクションのメッセージ束
 */
public class MessageBundle {
	/** 現在有効なサブタグ */
	public enum MessageTag {
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
	/** メッセージ表 */
	private Map<String, String[]> messages = null;

	/**
	 * コンストラクタ.
	 */
	public MessageBundle() {
		rudeFlag = false;
		normalTag = new boolean[MessageTag.values().length];
		rudeTag = new boolean[MessageTag.values().length];
		messages = new HashMap<String, String[]>();
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

	public Map<String, String[]> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, String[]> messages) {
		this.messages = messages;
	}
}
