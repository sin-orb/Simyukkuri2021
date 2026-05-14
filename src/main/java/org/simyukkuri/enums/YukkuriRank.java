package org.simyukkuri.enums;
import org.simyukkuri.util.GameText;

import org.simyukkuri.system.ResourceUtil;

/** ゆっくりのランク。 */
public enum YukkuriRank {
	/** 飼いゆ */KAIYU(0, 0, GameText.read("enums_petyu")),
	/** 捨てゆ */SUTEYU(0, 0, GameText.read("enums_throwyu")),
	/** 野良ゆ(きれい) */NORAYU_CLEAN(0, 1, GameText.read("enums_strayyu")),
	/** 野良ゆ */NORAYU(1, 1, GameText.read("enums_strayyu")),
	/** 野生ゆ */YASEIYU(0, 1, GameText.read("enums_wildyu")),
	;
	/** ランクに対応した画像のindex */
	private final int imageIndex;
	/** ランクに対応したメッセージのindex */
	private final int messageIndex;
	/** ランクに対応した文字列（"飼いゆ","捨てゆ"など） */
	private final String displayName;

	/**
	 * コンストラクタ.
	 * @param img ランクに対応した画像のindex
	 * @param msg ランクに対応したメッセージのindex
	 * @param str ランクに対応した文字列（"飼いゆ","捨てゆ"など）
	 */
	YukkuriRank(int img, int msg, String str) {
		this.imageIndex = img;
		this.messageIndex = msg;
		this.displayName = str;
	}

	public int getImageIndex() {
		return imageIndex;
	}

	public int getMessageIndex() {
		return messageIndex;
	}

	public String getDisplayName() {
		return displayName;
	}
}
