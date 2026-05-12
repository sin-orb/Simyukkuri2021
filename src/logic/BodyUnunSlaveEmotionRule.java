package src.logic;

import src.entity.core.living.yukkuri.Yukkuri;
import src.enums.EnumRelationMine;
import src.enums.Happiness;
import src.enums.PublicRank;
import src.system.MessagePool;
import src.util.GameMessages;
import src.util.GameRandom;

/**
 * Emotional reaction for unun slave bodies.
 */
public final class BodyUnunSlaveEmotionRule {

	private BodyUnunSlaveEmotionRule() {
	}

	/**
	 * Process the special emotion reaction for unun slave bodies.
	 *
	 * @param body       actor body
	 * @param bodyTarget target body
	 * @return true if a reaction was handled
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

		if (body.isNYD()) {
			return false;
		}

		boolean[] emotionFlags = EmotionLogic.checkEmotionForOther(body, bodyTarget);
		EnumRelationMine relation = BodyLogic.checkMyRelation(body, bodyTarget);

		if ((body.getPublicRank() == PublicRank.UnunSlave) && (bodyTarget.getPublicRank() != PublicRank.UnunSlave)) {
			if (emotionFlags[5]) {
				switch (relation) {
					case FATHER:
					case MOTHER:
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutChild));
						break;
					case PARTNAR:
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutPartner));
						break;
					case CHILD_FATHER:
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutFather));
						break;
					case CHILD_MOTHER:
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutMother));
						break;
					case ELDERSISTER:
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutElderSister));
						break;
					case YOUNGSISTER:
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
