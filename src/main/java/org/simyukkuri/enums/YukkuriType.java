package org.simyukkuri.enums;

import java.util.HashMap;
import java.util.Map;

import org.simyukkuri.util.GameLocale;

/** ゆっくりのタイプ */
public enum YukkuriType {
	ALICE("Alice", "alice", "alice", "ありす", "Alice", 2),
	AYAYA("Ayaya", "ayaya", "ayaya", "あや", "Ayaya", 1001),
	CHEN("Chen", "chen", "chen", "ちぇん", "Chen", 4),
	CHIRUNO("Chiruno", "chiruno", "chiruno", "ちるの", "Chiruno", 1006),
	DEIBU("Deibu", "deibu", "deibu", "でいぶ", "Deibu", 2005),
	DOSMARISA("DosMarisa", "dosmarisa", "dosmarisa", "どす", "Dosu", 2006),
	EIKI("Eiki", "eiki", "eiki", "えーき", "Eiki", 1007),
	FRAN("Fran", "fran", "fran", "ふらん", "Fran", 3001),
	HYBRIDYUKKURI("HybridYukkuri", "hybridyukkuri", "", "ハイブリッド", "Hybrid", 20000),
	KIMEEMARU("Kimeemaru", "kimeemaru", "kimeemaru", "きめぇまる", "kimeemaru", 2003),
	MARISA("Marisa", "marisa", "marisa", "まりさ", "Marisa", 0),
	MARISAKOTATSUMURI("MarisaKotatsumuri", "marisakotatsumuri", "marisa_kotatumuri", "こたつむり", "Kotatsu", 2004),
	MARISAREIMU("MarisaReimu", "marisareimu", "marisa_reimu", "まりされいむ", "MarisaReimu", 10000),
	MARISATSUMURI("MarisaTsumuri", "marisatsumuri", "marisa_tumuri", "つむり", "Shell", 2002),
	MEIRIN("Meirin", "meirin", "merin", "めーりん", "Merin", 1004),
	MYON("Myon", "myon", "myon", "みょん", "Myon", 5),
	NITORI("Nitori", "nitori", "nitori", "にとり", "Nitori", 1009),
	PATCH("Patch", "patch", "patch", "ぱちゅりー", "Patch", 3),
	RAN("Ran", "ran", "ran", "らん", "Ran", 1008),
	REIMU("Reimu", "reimu", "reimu", "れいむ", "Reimu", 1),
	REIMUMARISA("ReimuMarisa", "reimumarisa", "reimu_marisa", "れいむまりさ", "ReimuMarisa", 10001),
	REMIRYA("Remirya", "remirya", "remirya", "れみりゃ", "Remirya", 3000),
	SAKUYA("Sakuya", "sakuya", "sakuya", "さくや", "Sakuya", 1011),
	SUWAKO("Suwako", "suwako", "suwako", "すわこ", "Suwako", 1005),
	TARINAI("Tarinai", "tarinai", "tarinai", "たりないゆ", "Tarinaiyu", 2000),
	TARINAIREIMU("TarinaiReimu", "tarinai_reimu", "tarinai_reimu", "たりないれいむ", "TarinaiReimu", 2007),
	TENKO("Tenko", "tenko", "tenko", "てんこ", "Tenko", 1002),
	UDONGE("Udonge", "udonge", "udonge", "うどんげ", "Udonge", 1003),
	WASAREIMU("WasaReimu", "reimu", "wasa", "わされいむ", "Wasa", 2001),
	YURUSANAE("Yurusanae", "yurusanae", "yurusanae", "さなえ", "Yurusanae", 1000),
	YUUKA("Yuuka", "yuuka", "yuuka", "ゆうか", "Yuuka", 1010),
	YUYUKO("Yuyuko", "yuyuko", "yuyuko", "ゆゆこ", "Yuyuko", 3002),
	;

	private static final Map<String, YukkuriType> BY_CLASS_NAME = new HashMap<String, YukkuriType>();
	private static final Map<Integer, YukkuriType> BY_TYPE_ID = new HashMap<Integer, YukkuriType>();

	static {
		for (YukkuriType type : values()) {
			BY_CLASS_NAME.put(type.className, type);
			BY_TYPE_ID.put(Integer.valueOf(type.typeID), type);
		}
	}

	private final String className;
	private final String messageFileName;
	private final String imageDirName;
	private final String nameJ;
	private final String nameE;
	private final int typeID;

	YukkuriType(String clsName, String msgFile, String imgDir, String nameJ, String nameE, int id) {
		this.className = clsName;
		this.messageFileName = msgFile;
		this.imageDirName = imgDir;
		this.nameJ = nameJ;
		this.nameE = nameE;
		this.typeID = id;
	}

	public String getClassName() {
		return className;
	}

	public String getMessageFileName() {
		return messageFileName;
	}

	public String getImageDirName() {
		return imageDirName;
	}

	/** ロケールに応じた表示名を返す（旧 getNameJ() と同じ挙動）. */
	public String getNameJ() {
		return GameLocale.isJapanese() ? nameJ : nameE;
	}

	/** 日本語名を返す. */
	public String getJapaneseName() {
		return nameJ;
	}

	/** 英語名を返す. */
	public String getEnglishName() {
		return nameE;
	}

	public int getTypeID() {
		return typeID;
	}

	public static YukkuriType fromClassName(String className) {
		if (className == null) {
			return null;
		}
		return BY_CLASS_NAME.get(className);
	}

	public static YukkuriType fromTypeID(int typeID) {
		return BY_TYPE_ID.get(Integer.valueOf(typeID));
	}

	public static YukkuriType normalizeOffspringType(YukkuriType typeID) {
		switch (typeID) {
			case DOSMARISA:
				return MARISA;
			case DEIBU:
				return REIMU;
			default:
				return typeID;
		}
	}
}
