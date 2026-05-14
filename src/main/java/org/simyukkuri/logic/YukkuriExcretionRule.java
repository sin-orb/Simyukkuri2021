package org.simyukkuri.logic;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.util.GameRandom;

/**
 * 排泄に関する判定をまとめる窓口クラス。
 */
public final class YukkuriExcretionRule {
	private YukkuriExcretionRule() {
	}

	public static boolean getDiarrhea(Yukkuri body) {
		if (body.getRank() == YukkuriRank.KAIYU) {
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
