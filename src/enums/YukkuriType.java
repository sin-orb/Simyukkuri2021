package src.enums;

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
	ALICE("Alice", "alice", "alice", ResourceUtil.IS_JP ? Alice.nameJ : Alice.nameE, Alice.type),
	AYAYA("Ayaya", "ayaya", "ayaya", ResourceUtil.IS_JP ? Ayaya.nameJ : Ayaya.nameE, Ayaya.type),
	CHEN("Chen", "chen", "chen", ResourceUtil.IS_JP ? Chen.nameJ : Chen.nameE, Chen.type),
	CHIRUNO("Chiruno", "chiruno", "chiruno", ResourceUtil.IS_JP ? Chiruno.nameJ : Chiruno.nameE, Chiruno.type),
	DEIBU("Deibu", "deibu", "deibu", ResourceUtil.getInstance().read("enums_deibu"), Deibu.type),
	DOSMARISA("DosMarisa", "dosmarisa", "dosmarisa", ResourceUtil.getInstance().read("enums_dosu"), DosMarisa.type),
	EIKI("Eiki", "eiki", "eiki", ResourceUtil.IS_JP ? Eiki.nameJ : Eiki.nameE, Eiki.type),
	FRAN("Fran", "fran", "fran", ResourceUtil.IS_JP ? Fran.nameJ : Fran.nameE, Fran.type),
	HYBRIDYUKKURI("HybridYukkuri", "hybridyukkuri", "", ResourceUtil.getInstance().read("enums_hybrid"), HybridYukkuri.type),
	KIMEEMARU("Kimeemaru", "kimeemaru", "kimeemaru", ResourceUtil.IS_JP ? Kimeemaru.nameJ : Kimeemaru.nameE, Kimeemaru.type),
	MARISA("Marisa", "marisa", "marisa", ResourceUtil.IS_JP ? Marisa.nameJ : Marisa.nameE, Marisa.type),
	MARISAKOTATSUMURI("MarisaKotatsumuri", "marisakotatsumuri", "marisa_kotatumuri", ResourceUtil.getInstance().read("enums_kotatsu"), MarisaKotatsumuri.type),
	MARISAREIMU("MarisaReimu", "marisareimu", "marisa_reimu", ResourceUtil.IS_JP ? MarisaReimu.nameJ : MarisaReimu.nameE, MarisaReimu.type),
	MARISATSUMURI("MarisaTsumuri", "marisatsumuri", "marisa_tumuri", ResourceUtil.getInstance().read("enums_tsumuri"), MarisaTsumuri.type),
	MEIRIN("Meirin", "meirin", "merin", ResourceUtil.IS_JP ? Meirin.nameJ : Meirin.nameE, Meirin.type),
	MYON("Myon", "myon", "myon", ResourceUtil.IS_JP ? Myon.nameJ : Myon.nameE, Myon.type),
	NITORI("Nitori", "nitori", "nitori", ResourceUtil.IS_JP ? Nitori.nameJ : Nitori.nameE, Nitori.type),
	PATCH("Patch", "patch", "patch", ResourceUtil.IS_JP ? Patch.nameJ : Patch.nameE, Patch.type),
	RAN("Ran", "ran", "ran", ResourceUtil.IS_JP ? Ran.nameJ : Ran.nameE, Ran.type),
	REIMU("Reimu", "reimu", "reimu", ResourceUtil.IS_JP ? Reimu.nameJ : Reimu.nameE, Reimu.type),
	REIMUMARISA("ReimuMarisa", "reimumarisa", "reimu_marisa", ResourceUtil.IS_JP ? ReimuMarisa.nameJ : ReimuMarisa.nameE, ReimuMarisa.type),
	REMIRYA("Remirya", "remirya", "remirya", ResourceUtil.IS_JP ? Remirya.nameJ : Remirya.nameE, Remirya.type),
	SAKUYA("Sakuya", "sakuya", "sakuya", ResourceUtil.IS_JP ? Sakuya.nameJ : Sakuya.nameE, Sakuya.type),
	SUWAKO("Suwako", "suwako", "suwako", ResourceUtil.IS_JP ? Suwako.nameJ : Suwako.nameE, Suwako.type),
	TARINAI("Tarinai", "tarinai", "tarinai", ResourceUtil.IS_JP ? Tarinai.nameJ : Tarinai.nameE, Tarinai.type),
	TARINAIREIMU("TarinaiReimu", "tarinai_reimu", "tarinai_reimu", ResourceUtil.IS_JP ? TarinaiReimu.nameJ : TarinaiReimu.nameE, TarinaiReimu.type),
	TENKO("Tenko", "tenko", "tenko", ResourceUtil.IS_JP ? Tenko.nameJ : Tenko.nameE, Tenko.type),
	UDONGE("Udonge", "udonge", "udonge", ResourceUtil.IS_JP ? Udonge.nameJ : Udonge.nameE, Udonge.type),
	WASAREIMU("WasaReimu", "reimu", "wasa", ResourceUtil.getInstance().read("enums_wasa"), WasaReimu.type),
	YURUSANAE("Yurusanae", "yurusanae", "yurusanae", ResourceUtil.IS_JP ? Yurusanae.nameJ : Yurusanae.nameE, Yurusanae.type),
	YUUKA("Yuuka", "yuuka", "yuuka", ResourceUtil.IS_JP ? Yuuka.nameJ : Yuuka.nameE, Yuuka.type),
	YUYUKO("Yuyuko", "yuyuko", "yuyuko", ResourceUtil.IS_JP ? Yuyuko.nameJ : Yuyuko.nameE, Yuyuko.type),
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
