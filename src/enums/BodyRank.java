package src.enums;

/** ゆっくりのランク。 */
public enum BodyRank {
	/** 飼いゆ */KAIYU(0, 0, "飼いゆ"),
	/** 捨てゆ */SUTEYU(0, 0, "捨てゆ"),
	/** 野良ゆ(きれい) */NORAYU_CLEAN(0, 1, "野良ゆ"),
	/** 野良ゆ */NORAYU(1, 1, "野良ゆ"),
	/** 野生ゆ */YASEIYU(0, 1, "野生ゆ"),
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
