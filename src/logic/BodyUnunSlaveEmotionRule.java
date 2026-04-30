package src.logic;

import src.base.Body;
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
	 * @param b          actor body
	 * @param bodyTarget target body
	 * @return true if a reaction was handled
	 */
	public static boolean checkEmotionFromUnunSlave(Body b, Body bodyTarget) {
		if (b == null || bodyTarget == null) {
			return false;
		}

		if (GameRandom.nextInt(50) != 0) {
			return false;
		}

		if (b.isIdiot()) {
			return false;
		}

		if (b.isNYD()) {
			return false;
		}

		boolean[] abEmote = EmotionLogic.checkEmotionForOther(b, bodyTarget);
		EnumRelationMine eRelation = BodyLogic.checkMyRelation(b, bodyTarget);

		if ((b.getPublicRank() == PublicRank.UnunSlave) && (bodyTarget.getPublicRank() != PublicRank.UnunSlave)) {
			if (abEmote[5]) {
				switch (eRelation) {
					case FATHER:
					case MOTHER:
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateWithEnvyAboutChild));
						break;
					case PARTNAR:
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateWithEnvyAboutPartner));
						break;
					case CHILD_FATHER:
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateWithEnvyAboutFather));
						break;
					case CHILD_MOTHER:
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateWithEnvyAboutMother));
						break;
					case ELDERSISTER:
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateWithEnvyAboutElderSister));
						break;
					case YOUNGSISTER:
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateWithEnvyAboutSister));
						break;
					default:
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateWithEnvyAboutOther));
						break;
				}
				b.setHappiness(Happiness.VERY_SAD);
				b.addStress(10);
				b.stay();
				return true;
			}
		}

		return false;
	}
}
