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

	/**
	 * 通常ゆっくり向けメッセージが定義済みかどうかを返す。
	 *
	 * @return 通常フラグが有効なら true
	 */
	public boolean isNormalFlag() {
		return normalFlag;
	}

	/**
	 * 通常ゆっくり向けメッセージの定義有無をセットする。
	 *
	 * @param normalFlag true で通常メッセージあり
	 */
	public void setNormalFlag(boolean normalFlag) {
		this.normalFlag = normalFlag;
	}

	/**
	 * ゲスゆっくり向けメッセージが定義済みかどうかを返す。
	 *
	 * @return ゲスフラグが有効なら true
	 */
	public boolean isRudeFlag() {
		return rudeFlag;
	}

	/**
	 * ゲスゆっくり向けメッセージの定義有無をセットする。
	 *
	 * @param rudeFlag true でゲスメッセージあり
	 */
	public void setRudeFlag(boolean rudeFlag) {
		this.rudeFlag = rudeFlag;
	}

	/**
	 * 通常ゆっくり向けの有効タグ配列を返す。インデックスは {@link MessageTag#ordinal()} に対応する。
	 *
	 * @return 通常用タグ有効フラグ配列
	 */
	public boolean[] getNormalTag() {
		return normalTag;
	}

	/**
	 * 通常ゆっくり向けタグ配列をセットする。
	 *
	 * @param normalTag 新しいタグ有効フラグ配列
	 */
	public void setNormalTag(boolean[] normalTag) {
		this.normalTag = normalTag;
	}

	/**
	 * ゲスゆっくり向けの有効タグ配列を返す。インデックスは {@link MessageTag#ordinal()} に対応する。
	 *
	 * @return ゲス用タグ有効フラグ配列
	 */
	public boolean[] getRudeTag() {
		return rudeTag;
	}

	/**
	 * ゲスゆっくり向けタグ配列をセットする。
	 *
	 * @param rudeTag 新しいタグ有効フラグ配列
	 */
	public void setRudeTag(boolean[] rudeTag) {
		this.rudeTag = rudeTag;
	}

	/**
	 * タグ文字列をキー、メッセージ候補配列を値とするメッセージマップを返す。
	 *
	 * @return メッセージマップ
	 */
	public Map<String, String[]> getMessages() {
		return messages;
	}

	/**
	 * メッセージマップをセットする。
	 *
	 * @param messages 新しいメッセージマップ
	 */
	public void setMessages(Map<String, String[]> messages) {
		this.messages = messages;
	}
}
