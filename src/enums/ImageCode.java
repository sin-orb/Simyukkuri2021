package src.enums;

import java.io.File;

/** 画像のコード */
public enum ImageCode {
	// 正面
	BURNED("front", "burned1", false),
	BURNED2("front", "burned2", false),
	CRUSHED("front", "crushed", false),
	CRUSHED2("front", "crushed2", false),
	CRUSHED3("front", "crushed3", false),
	ROLL_ACCESSORY("front", "okazari", false),
	FRONT_SHIT("front", "shit", false),
	FRONT_HAIR("front", "hair", false),
	FRONT_BLIND("front", "blind", false),
	FRONT_PANTS("front", "pants", false),
	FRONT_BRAID("front", "braid", false),
	FRONT_SEALED("front", "sealed", false),
	FRONT_INJURED("front", "injured", false),
	FRONT_HAIR2("front","hair2",false),
	ROLL_LEFT_SHIT("front", "roll_left", false),
	ROLL_LEFT_HAIR("front","hair_left",false),
	ROLL_LEFT_BLIND("front", "blind_left", false),
	ROLL_LEFT_PANTS("front", "left_pants", false),
	ROLL_LEFT_BRAID("front", "braid_left", false),
	ROLL_LEFT_SEALED("front", "sealed_left", false),
	ROLL_LEFT_INJURED("front", "injured_left", false),
	ROLL_RIGHT_SHIT("front", "roll_right", false),
	ROLL_RIGHT_HAIR("front","hair_right",false),
	ROLL_RIGHT_BLIND("front", "blind_right", false),
	ROLL_RIGHT_PANTS("front", "right_pants", false),
	ROLL_RIGHT_BRAID("front", "braid_right", false),
	ROLL_RIGHT_SEALED("front", "sealed_right", false),
	ROLL_RIGHT_INJURED("front", "injured_right", false),
	PACKED_DEAD("front", "packed_dead", false),
	PACKED1("front", "packed1", false),
	PACKED2("front", "packed2", false),

	//もるケツまりさ用正面画像
	MROLL_LEFT_SHIT("Mfront", "roll_left", false),
	MROLL_ACCESSORY_LEFT("Mfront", "okazari_left", false),
	MROLL_LEFT_HAIR("Mfront","hair_left",false),
	MROLL_LEFT_HAIR2("Mfront","hair2_left",false),
	MROLL_LEFT_BLIND("Mfront", "blind_left", false),
	MROLL_LEFT_PANTS("Mfront", "left_pants", false),
	MROLL_LEFT_BRAID("Mfront", "braid_left", false),
	MROLL_LEFT_CUTBRAID("Mfront", "cutbraid_left", false),
	MROLL_LEFT_SEALED("Mfront", "sealed_left", false),
	MROLL_LEFT_INJURED("Mfront", "injured_left", false),
	MROLL_RIGHT_SHIT("Mfront", "roll_right", false),
	MROLL_ACCESSORY_RIGHT("Mfront", "okazari_right", false),
	MROLL_RIGHT_HAIR("Mfront","hair_right",false),
	MROLL_RIGHT_HAIR2("Mfront","hair2_right",false),
	MROLL_RIGHT_BLIND("Mfront", "blind_right", false),
	MROLL_RIGHT_PANTS("Mfront", "right_pants", false),
	MROLL_RIGHT_BRAID("Mfront", "braid_right", false),
	MROLL_RIGHT_SEALED("Mfront", "sealed_right", false),
	MROLL_RIGHT_INJURED("Mfront", "injured_right", false),
	MROLL_LEFT2_SHIT("Mfront", "roll_left2", false),
	MROLL_ACCESSORY_LEFT2("Mfront", "okazari_left2", false),
	MROLL_LEFT2_HAIR("Mfront","hair_left2",false),
	MROLL_LEFT2_HAIR2("Mfront","hair2_left2",false),
	MROLL_LEFT2_CUTBRAID("Mfront", "cutbraid_left2", false),
	MROLL_LEFT2_BLIND("Mfront", "blind_left2", false),
	MROLL_LEFT2_PANTS("Mfront", "left2_pants", false),
	MROLL_LEFT2_BRAID("Mfront", "braid_left2", false),
	MROLL_LEFT2_SEALED("Mfront", "sealed_left2", false),
	MROLL_LEFT2_INJURED("Mfront", "injured_left2", false),
	MROLL_RIGHT2_SHIT("Mfront", "roll_right2", false),
	MROLL_ACCESSORY_RIGHT2("Mfront", "okazari_right2", false),
	MROLL_RIGHT2_HAIR("Mfront","hair_right2",false),
	MROLL_RIGHT2_HAIR2("Mfront","hair2_right2",false),
	MROLL_RIGHT2_BLIND("Mfront", "blind_right2", false),
	MROLL_RIGHT2_PANTS("Mfront", "right2_pants", false),
	MROLL_RIGHT2_BRAID("Mfront", "braid_right2", false),
	MROLL_RIGHT2_SEALED("Mfront", "sealed_right2", false),
	MROLL_RIGHT2_INJURED("Mfront", "injured_right2", false),

	//れいむゆんやぁ用画像
	YUNYAA1("Yunyaa", "body_1", true),
	YUNYAA1_ACCESSORY("Yunyaa", "okazari_1", true),
	YUNYAA1_HAIR("Yunyaa","hair_1",true),
	YUNYAA1_HAIR2("Yunyaa","hair2_1",true),
	YUNYAA1_BLIND("Yunyaa", "blind_1", true),
	YUNYAA1_PANTS("Yunyaa", "pants_1", true),
	YUNYAA1_BRAID("Yunyaa", "braid_1", true),
	YUNYAA1_CUTBRAID("Yunyaa", "cutbraid_1", true),
	YUNYAA1_INJURED("Yunyaa", "injured_1", true),
	YUNYAA1_DIRTY("Yunyaa", "dirty_1", true),
	YUNYAA2("Yunyaa", "body_2", true),
	YUNYAA2_ACCESSORY("Yunyaa", "okazari_2", true),
	YUNYAA2_HAIR("Yunyaa","hair_2",true),
	YUNYAA2_HAIR2("Yunyaa","hair2_2",true),
	YUNYAA2_CUTBRAID("Yunyaa", "cutbraid_2", true),
	YUNYAA2_BLIND("Yunyaa", "blind_2", true),
	YUNYAA2_PANTS("Yunyaa", "pants_2", true),
	YUNYAA2_BRAID("Yunyaa", "braid_2", true),
	YUNYAA2_INJURED("Yunyaa", "injured_2", true),
	YUNYAA2_DIRTY("Yunyaa", "dirty_2", true),
	YUNYAA3("Yunyaa", "body_3", true),
	YUNYAA3_ACCESSORY("Yunyaa", "okazari_3", true),
	YUNYAA3_HAIR("Yunyaa","hair_3",true),
	YUNYAA3_HAIR3("Yunyaa","hair3_3",true),
	YUNYAA3_CUTBRAID("Yunyaa", "cutbraid_3", true),
	YUNYAA3_BLIND("Yunyaa", "blind_3", true),
	YUNYAA3_PANTS("Yunyaa", "pants_3", true),
	YUNYAA3_BRAID("Yunyaa", "braid_3", true),
	YUNYAA3_INJURED("Yunyaa", "injured_3", true),
	YUNYAA3_DIRTY("Yunyaa", "dirty_3", true),

	// 横
	BODY("body", null, true),
	DEAD_BODY("body_dead", null, true),
	ACCESSORY("okazari", null, true),
	ACCESSORY_BACK("okazari_back", null, true),
	PANTS("pants", null, true),
	BODY_CUT("body_cut", null, true),
	BODY_INJURED("body_injured", null, true),
	PEALED("pealed", null, true),
	DAMAGED0("damage0", null, true),
	DAMAGED1("damage1", null, true),
	DAMAGED2("damage2", null, true),
	SICK0("sick0", null, true),
	SICK1("sick1", null, true),
	SICK2("sick2", null, true),
	SICK3("sick3", null, true),
	FOOT_BAKE0("foot_bake_0", null, true),
	FOOT_BAKE1("foot_bake_1", null, true),
	BODY_BAKE0("skinburn0", null, true),
	BODY_BAKE1("skinburn1", null, true),
	STAIN("shit", null, true),
	STAIN2("shit2", null, true),
	WET("wet", null, true),
	MELT("melt", null, true),
	MELT_PEALED("melt_pealed",null,true),
	LICK("lick", null, true),
	NOMNOM("nomnom", null, true),
	BLIND("blind",null,true),
	SHUTMOUTH("shutmouth",null,true),
	HUNGRY0("hungry0",null,true),
	HUNGRY1("hungry1",null,true),
	HUNGRY2("hungry2",null,true),
	HAIR0("hair","hair0",true),
	HAIR1("hair","hair1",true),
	HAIR2("hair","hair2",true),
	BRAID("braid", "braid", true),
	BRAID_BACK("braid", "braid_back", true),
	BRAID_CUT("braid", "braid_cut", true),
	BRAID_MV0("braid", "braid_mv_0", true),
	BRAID_MV1("braid", "braid_mv_1", true),
	BRAID_MV2("braid", "braid_mv_2", true),
	//BRAID_BACK_CUT("braid", "braid_back_cut", true),
	BRAID_BACK_MV0("braid", "braid_back_mv_0", true),
	BRAID_BACK_MV1("braid", "braid_back_mv_1", true),
	BRAID_BACK_MV2("braid", "braid_back_mv_2", true),
	// 顔
	CHEER("faces", "cheer", true),
	CRYING("faces", "crying", true),
	DEAD("faces", "dead", true),
	EXCITING("faces", "exciting", true),
	EXCITING_raper("faces","exciting2",true),
	CUTPENIPENI("faces", "penicut", true),
	NORMAL("faces", "normal", true),
	PAIN("faces", "pain", true),
	PUFF("faces", "puff", true),
	REFRESHED("faces", "refreshed", true),
	EMBARRASSED("faces", "embarrassed", true),
	RUDE("faces", "rude", true),
	SLEEPING("faces", "sleeping", true),
	NIGHTMARE("faces", "nightmare", true),
	SMILE("faces", "smile", true),
	VAIN("faces", "vain", true),
	SURPRISE("faces", "surprise", true),
	TIRED("faces", "tired", true),
	EYE2("faces", "eye2", true),
	EYE3("faces", "eye3", true),
	NORMAL0("faces", "normal0", true),
	CHEER0("faces", "cheer0", true),
	PUFF0("faces", "puff0", true),
	RUDE0("faces", "rude0", true),
	TIRED0("faces", "tired0", true),
	PEALEDFACE("faces","pealed", true),
	PEALEDDEADFACE("faces","dead_pealed", true),
	// 顔(非ゆっくり症)NonYukkuriDisease
	NYD_FRONT("faces/NonYukkuriDisease", "front", true),
	NYD_FRONT_CRY1("faces/NonYukkuriDisease", "front_crying01", true),
	NYD_FRONT_CRY2("faces/NonYukkuriDisease", "front_crying02", true),
	NYD_UP("faces/NonYukkuriDisease", "up", true),
	NYD_UP_CRY1("faces/NonYukkuriDisease", "up_crying01", true),
	NYD_UP_CRY2("faces/NonYukkuriDisease", "up_crying02", true),
	NYD_DOWN("faces/NonYukkuriDisease", "down", true),
	NYD_DOWN_CRY1("faces/NonYukkuriDisease", "down_crying01", true),
	NYD_DOWN_CRY2("faces/NonYukkuriDisease", "down_crying02", true),
	NYD_FRONT_WIDE("faces/NonYukkuriDisease", "frontwide", true),
	NYD_FRONT_WIDE_CRY1("faces/NonYukkuriDisease", "frontwide_crying01", true),
	NYD_FRONT_WIDE_CRY2("faces/NonYukkuriDisease", "frontwide_crying02", true),
	;
	private String dir1;
	private String dir2;
	private boolean secondary;
	
	/**
	 * コンストラクタ.
	 * @param d1 ディレクトリ1
	 * @param d2 ディレクトリ2
	 * @param r 2番目があるかどうか
	 */
	ImageCode(String d1, String d2, boolean r) {
		this.dir1 = d1;
		this.dir2 = d2;
		this.secondary = r;
	}
	
	/**
	 * jarファイルのパスを取得する.
	 * @param isSecond 
	 * @return
	 */
	public String getJarPath(boolean isSecond) {
		String buf = dir1;

		if(dir2 != null) {
			buf = buf + "/" + dir2;
		}
		if(secondary) {
			if(isSecond) buf = "right" + "/" + buf;
			else buf = "left" + "/" + buf;
		}
		return buf;
	}

	public String getFilePath(boolean isSecond) {
		String buf = dir1;

		if(dir2 != null) {
			buf = buf + File.separator + dir2;
		}

		if(secondary) {
			if(isSecond) buf = "right" + File.separator + buf;
			else buf = "left" + File.separator + buf;
		}
	return buf;
	}

	public boolean hasSecondary() {
		return secondary;
	}
}
