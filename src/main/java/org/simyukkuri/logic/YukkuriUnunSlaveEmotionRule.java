package org.simyukkuri.logic;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.enums.YukkuriRelationType;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;

/**
 * Emotional reaction for unun slave bodies.
 */
public final class YukkuriUnunSlaveEmotionRule {

	private YukkuriUnunSlaveEmotionRule() {
	}

	/**
	 * うんうん奴隷ゆっくりの特殊感情反応を処理する。
	 *
	 * @param body       行動主体ゆっくり
	 * @param bodyTarget 処理対象ゆっくり
	 * @return 反応が処理された場合は true
	 */
	public static boolean checkEmotionFromUnunSlave(Yukkuri body, Yukkuri bodyTarget) {
		if (body == null || bodyTarget == null) {
			return false;
		}

		if (GameRandom.nextInt(50) != 0) {
			return false;
		}

		if (body.isIdiot()) {
			return false;
		}

		if (body.isNyd()) {
			return false;
		}

		boolean[] emotionFlags = EmotionLogic.checkEmotionForOther(body, bodyTarget);
		YukkuriRelationType relation = YukkuriLogic.checkMyRelation(body, bodyTarget);

		if ((body.getPublicRank() == PublicRank.UNUN_SLAVE) && (bodyTarget.getPublicRank() != PublicRank.UNUN_SLAVE)) {
			if (emotionFlags[5]) {
				switch (relation) {
					case FATHER:
					case MOTHER:
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutChild));
						break;
					case PARTNER:
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutPartner));
						break;
					case CHILD_OF_FATHER:
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutFather));
						break;
					case CHILD_OF_MOTHER:
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutMother));
						break;
					case ELDER_SISTER:
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutElderSister));
						break;
					case YOUNGER_SISTER:
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutSister));
						break;
					default:
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutOther));
						break;
				}
				body.setHappiness(Happiness.VERY_SAD);
				body.addStress(10);
				body.stay();
				return true;
			}
		}

		return false;
	}
}
