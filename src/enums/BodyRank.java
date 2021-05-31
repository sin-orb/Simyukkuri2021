package src.enums;

import src.system.ResourceUtil;

/** ゆっくりのランク。 */
public enum BodyRank {
	/** 飼いゆ */KAIYU(0, 0, ResourceUtil.getInstance().read("enums_petyu")),
	/** 捨てゆ */SUTEYU(0, 0, ResourceUtil.getInstance().read("enums_throwyu")),
	/** 野良ゆ(きれい) */NORAYU_CLEAN(0, 1, ResourceUtil.getInstance().read("enums_strayyu")),
	/** 野良ゆ */NORAYU(1, 1, ResourceUtil.getInstance().read("enums_strayyu")),
	/** 野生ゆ */YASEIYU(0, 1, ResourceUtil.getInstance().read("enums_wildyu")),
	;
	/** ランクに対応した画像のindex */
	public int imageIndex;
	/** ランクに対応したメッセージのindex */
	public int messageIndex;
	/** ランクに対応した文字列（"飼いゆ","捨てゆ"など） */
	public String displayName;

	/**
	 * コンストラクタ.
	 * @param img ランクに対応した画像のindex
	 * @param msg ランクに対応したメッセージのindex
	 * @param str ランクに対応した文字列（"飼いゆ","捨てゆ"など）
	 */
	BodyRank(int img, int msg, String str) {
		this.imageIndex = img;
		this.messageIndex = msg;
		this.displayName = str;
	}
}
