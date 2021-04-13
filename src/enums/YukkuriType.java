package src.enums;

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
	ALICE("Alice", "alice", "alice", Alice.nameJ, Alice.type),
	AYAYA("Ayaya", "ayaya", "ayaya", Ayaya.nameJ, Ayaya.type),
	CHEN("Chen", "chen", "chen", Chen.nameJ, Chen.type),
	CHIRUNO("Chiruno", "chiruno", "chiruno", Chiruno.nameJ, Chiruno.type),
	DEIBU("Deibu", "deibu", "deibu", Deibu.nameJ, Deibu.type),
	DOSMARISA("DosMarisa", "dosmarisa", "dosmarisa", DosMarisa.nameJ, DosMarisa.type),
	EIKI("Eiki", "eiki", "eiki", Eiki.nameJ, Eiki.type),
	FRAN("Fran", "fran", "fran", Fran.nameJ, Fran.type),
	HYBRIDYUKKURI("HybridYukkuri", "hybridyukkuri", "", "ハイブリッド", HybridYukkuri.type),
	KIMEEMARU("Kimeemaru", "kimeemaru", "kimeemaru", Kimeemaru.nameJ, Kimeemaru.type),
	MARISA("Marisa", "marisa", "marisa", Marisa.nameJ, Marisa.type),
	MARISAKOTATSUMURI("MarisaKotatsumuri", "marisakotatsumuri", "marisa_kotatumuri", "まりさこたつむり", MarisaKotatsumuri.type),
	MARISAREIMU("MarisaReimu", "marisareimu", "marisa_reimu", MarisaReimu.nameJ, MarisaReimu.type),
	MARISATSUMURI("MarisaTsumuri", "marisatsumuri", "marisa_tumuri", "まりさつむり", MarisaTsumuri.type),
	MEIRIN("Meirin", "meirin", "merin", Meirin.nameJ, Meirin.type),
	MYON("Myon", "myon", "myon", Myon.nameJ, Myon.type),
	NITORI("Nitori", "nitori", "nitori", Nitori.nameJ, Nitori.type),
	PATCH("Patch", "patch", "patch", Patch.nameJ, Patch.type),
	RAN("Ran", "ran", "ran", Ran.nameJ, Ran.type),
	REIMU("Reimu", "reimu", "reimu", Reimu.nameJ, Reimu.type),
	REIMUMARISA("ReimuMarisa", "reimumarisa", "reimu_marisa", ReimuMarisa.nameJ, ReimuMarisa.type),
	REMIRYA("Remirya", "remirya", "remirya", Remirya.nameJ, Remirya.type),
	SAKUYA("Sakuya", "sakuya", "sakuya", Sakuya.nameJ, Sakuya.type),
	SUWAKO("Suwako", "suwako", "suwako", Suwako.nameJ, Suwako.type),
	TARINAI("Tarinai", "tarinai", "tarinai", Tarinai.nameJ, Tarinai.type),
	TARINAIREIMU("TarinaiReimu", "tarinai_reimu", "tarinai_reimu", TarinaiReimu.nameJ, TarinaiReimu.type),
	TENKO("Tenko", "tenko", "tenko", Tenko.nameJ, Tenko.type),
	UDONGE("Udonge", "udonge", "udonge", Udonge.nameJ, Udonge.type),
	WASAREIMU("WasaReimu", "reimu", "wasa", "わされいむ", WasaReimu.type),
	YURUSANAE("Yurusanae", "yurusanae", "yurusanae", Yurusanae.nameJ, Yurusanae.type),
	YUUKA("Yuuka", "yuuka", "yuuka", Yuuka.nameJ, Yuuka.type),
	YUYUKO("Yuyuko", "yuyuko", "yuyuko", Yuyuko.nameJ, Yuyuko.type),
	;
	public String className;
	public String messageFileName;
	public String imageDirName;
	public String nameJ;
	public int typeID;
	YukkuriType(String clsName, String msgFile, String imgDir, String nameJ, int id) {
		this.className = clsName;
		this.messageFileName = msgFile;
		this.imageDirName = imgDir;
		this.nameJ = nameJ;
		this.typeID = id;
	}
}
