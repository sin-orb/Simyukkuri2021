package src.logic;

import src.base.BodyAttributes;
import src.enums.BodyRank;
import src.util.GameRandom;

/**
 * 排泄に関する判定をまとめる窓口クラス。
 */
public final class BodyExcretionRule {
	private BodyExcretionRule() {
	}

	public static boolean getDiarrhea(BodyAttributes body) {
		if (body.getBodyRank() == BodyRank.KAIYU) {
			return true;
		}
		int diarrheaProb = body.getDiarrheaProb();
		if (body.isSick() || body.isDamaged()) {
			diarrheaProb /= 2;
		}
		if (diarrheaProb < 1) {
			diarrheaProb = 1;
		}
		return GameRandom.nextInt(diarrheaProb) == 0;
	}
}
