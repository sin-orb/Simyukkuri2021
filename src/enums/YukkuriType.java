package src.enums;
import src.util.GameLocale;
import src.util.GameText;

import src.system.ResourceUtil;
import src.yukkuri.Alice;
import src.yukkuri.Ayaya;
import src.yukkuri.Chen;
import src.yukkuri.Chiruno;
import src.yukkuri.Deibu;
import src.yukkuri.DosMarisa;
import src.yukkuri.Eiki;
import src.yukkuri.Fran;
import src.yukkuri.HybridYukkuri;
import src.yukkuri.Kimeemaru;
import src.yukkuri.Marisa;
import src.yukkuri.MarisaKotatsumuri;
import src.yukkuri.MarisaReimu;
import src.yukkuri.MarisaTsumuri;
import src.yukkuri.Meirin;
import src.yukkuri.Myon;
import src.yukkuri.Nitori;
import src.yukkuri.Patch;
import src.yukkuri.Ran;
import src.yukkuri.Reimu;
import src.yukkuri.ReimuMarisa;
import src.yukkuri.Remirya;
import src.yukkuri.Sakuya;
import src.yukkuri.Suwako;
import src.yukkuri.Tarinai;
import src.yukkuri.TarinaiReimu;
import src.yukkuri.Tenko;
import src.yukkuri.Udonge;
import src.yukkuri.WasaReimu;
import src.yukkuri.Yurusanae;
import src.yukkuri.Yuuka;
import src.yukkuri.Yuyuko;
/** ゆっくりのタイプ */
public enum YukkuriType {
	ALICE("Alice", "alice", "alice", GameLocale.isJapanese() ? Alice.nameJ : Alice.nameE, Alice.type),
	AYAYA("Ayaya", "ayaya", "ayaya", GameLocale.isJapanese() ? Ayaya.nameJ : Ayaya.nameE, Ayaya.type),
	CHEN("Chen", "chen", "chen", GameLocale.isJapanese() ? Chen.nameJ : Chen.nameE, Chen.type),
	CHIRUNO("Chiruno", "chiruno", "chiruno", GameLocale.isJapanese() ? Chiruno.nameJ : Chiruno.nameE, Chiruno.type),
	DEIBU("Deibu", "deibu", "deibu", GameText.read("enums_deibu"), Deibu.type),
	DOSMARISA("DosMarisa", "dosmarisa", "dosmarisa", GameText.read("enums_dosu"), DosMarisa.type),
	EIKI("Eiki", "eiki", "eiki", GameLocale.isJapanese() ? Eiki.nameJ : Eiki.nameE, Eiki.type),
	FRAN("Fran", "fran", "fran", GameLocale.isJapanese() ? Fran.nameJ : Fran.nameE, Fran.type),
	HYBRIDYUKKURI("HybridYukkuri", "hybridyukkuri", "", GameText.read("enums_hybrid"), HybridYukkuri.type),
	KIMEEMARU("Kimeemaru", "kimeemaru", "kimeemaru", GameLocale.isJapanese() ? Kimeemaru.nameJ : Kimeemaru.nameE, Kimeemaru.type),
	MARISA("Marisa", "marisa", "marisa", GameLocale.isJapanese() ? Marisa.nameJ : Marisa.nameE, Marisa.type),
	MARISAKOTATSUMURI("MarisaKotatsumuri", "marisakotatsumuri", "marisa_kotatumuri", GameText.read("enums_kotatsu"), MarisaKotatsumuri.type),
	MARISAREIMU("MarisaReimu", "marisareimu", "marisa_reimu", GameLocale.isJapanese() ? MarisaReimu.nameJ : MarisaReimu.nameE, MarisaReimu.type),
	MARISATSUMURI("MarisaTsumuri", "marisatsumuri", "marisa_tumuri", GameText.read("enums_tsumuri"), MarisaTsumuri.type),
	MEIRIN("Meirin", "meirin", "merin", GameLocale.isJapanese() ? Meirin.nameJ : Meirin.nameE, Meirin.type),
	MYON("Myon", "myon", "myon", GameLocale.isJapanese() ? Myon.nameJ : Myon.nameE, Myon.type),
	NITORI("Nitori", "nitori", "nitori", GameLocale.isJapanese() ? Nitori.nameJ : Nitori.nameE, Nitori.type),
	PATCH("Patch", "patch", "patch", GameLocale.isJapanese() ? Patch.nameJ : Patch.nameE, Patch.type),
	RAN("Ran", "ran", "ran", GameLocale.isJapanese() ? Ran.nameJ : Ran.nameE, Ran.type),
	REIMU("Reimu", "reimu", "reimu", GameLocale.isJapanese() ? Reimu.nameJ : Reimu.nameE, Reimu.type),
	REIMUMARISA("ReimuMarisa", "reimumarisa", "reimu_marisa", GameLocale.isJapanese() ? ReimuMarisa.nameJ : ReimuMarisa.nameE, ReimuMarisa.type),
	REMIRYA("Remirya", "remirya", "remirya", GameLocale.isJapanese() ? Remirya.nameJ : Remirya.nameE, Remirya.type),
	SAKUYA("Sakuya", "sakuya", "sakuya", GameLocale.isJapanese() ? Sakuya.nameJ : Sakuya.nameE, Sakuya.type),
	SUWAKO("Suwako", "suwako", "suwako", GameLocale.isJapanese() ? Suwako.nameJ : Suwako.nameE, Suwako.type),
	TARINAI("Tarinai", "tarinai", "tarinai", GameLocale.isJapanese() ? Tarinai.nameJ : Tarinai.nameE, Tarinai.type),
	TARINAIREIMU("TarinaiReimu", "tarinai_reimu", "tarinai_reimu", GameLocale.isJapanese() ? TarinaiReimu.nameJ : TarinaiReimu.nameE, TarinaiReimu.type),
	TENKO("Tenko", "tenko", "tenko", GameLocale.isJapanese() ? Tenko.nameJ : Tenko.nameE, Tenko.type),
	UDONGE("Udonge", "udonge", "udonge", GameLocale.isJapanese() ? Udonge.nameJ : Udonge.nameE, Udonge.type),
	WASAREIMU("WasaReimu", "reimu", "wasa", GameText.read("enums_wasa"), WasaReimu.type),
	YURUSANAE("Yurusanae", "yurusanae", "yurusanae", GameLocale.isJapanese() ? Yurusanae.nameJ : Yurusanae.nameE, Yurusanae.type),
	YUUKA("Yuuka", "yuuka", "yuuka", GameLocale.isJapanese() ? Yuuka.nameJ : Yuuka.nameE, Yuuka.type),
	YUYUKO("Yuyuko", "yuyuko", "yuyuko", GameLocale.isJapanese() ? Yuyuko.nameJ : Yuyuko.nameE, Yuyuko.type),
	;
	private final String className;
	private final String messageFileName;
	private final String imageDirName;
	private final String nameJ;
	private final int typeID;
	YukkuriType(String clsName, String msgFile, String imgDir, String nameJ, int id) {
		this.className = clsName;
		this.messageFileName = msgFile;
		this.imageDirName = imgDir;
		this.nameJ = nameJ;
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

	public String getNameJ() {
		return nameJ;
	}

	public int getTypeID() {
		return typeID;
	}
}
