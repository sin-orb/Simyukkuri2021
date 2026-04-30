package src.logic;

import src.base.Body;
import src.enums.EnumRelationMine;
import src.enums.Happiness;
import src.system.MessagePool;
import src.util.GameRandom;
import src.util.GameMessages;

/**
 * Surisuri reaction rule used by BodyLogic.
 */
public final class BodySurisuriRule {

	private BodySurisuriRule() {
	}

	/**
	 * Check action when another body is being petted by player.
	 *
	 * @param b subject body
	 * @param bodyTarget target body
	 * @return action result
	 */
	public static final BodyLogic.eActionGo checkActionSurisuriFromPlayer(Body b, Body bodyTarget) {
		if (b == null || bodyTarget == null) {
			return BodyLogic.eActionGo.NONE;
		}
		if (!bodyTarget.isbSurisuriFromPlayer()) {
			return BodyLogic.eActionGo.NONE;
		}

		if (GameRandom.nextInt(10) != 0) {
			return BodyLogic.eActionGo.NONE;
		}
		if (b.isIdiot() || b.isNYD()) {
			return BodyLogic.eActionGo.NONE;
		}

		boolean[] abEmote = EmotionLogic.checkEmotionForOther(b, bodyTarget);
		EnumRelationMine eRelation = BodyRelations.checkMyRelation(b, bodyTarget);
		BodyLogic.eActionGo eAct = BodyLogic.eActionGo.NONE;

		if (abEmote[0]) {
			switch (eRelation) {
			case FATHER:
			case MOTHER:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.GladAboutChild));
				b.setHappiness(Happiness.HAPPY);
				b.stay();
				eAct = BodyLogic.eActionGo.GO;
				break;
			case PARTNAR:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.GladAboutPartner));
				b.setHappiness(Happiness.HAPPY);
				b.stay();
				eAct = BodyLogic.eActionGo.GO;
				break;
			case CHILD_FATHER:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.GladAboutFather));
				b.setHappiness(Happiness.HAPPY);
				b.stay();
				eAct = BodyLogic.eActionGo.GO;
				break;
			case CHILD_MOTHER:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.GladAboutMother));
				b.setHappiness(Happiness.HAPPY);
				b.stay();
				eAct = BodyLogic.eActionGo.GO;
				break;
			case ELDERSISTER:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.GladAboutSister));
				b.setHappiness(Happiness.HAPPY);
				b.stay();
				eAct = BodyLogic.eActionGo.GO;
				break;
			case YOUNGSISTER:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.GladAboutElderSister));
				b.setHappiness(Happiness.HAPPY);
				b.stay();
				eAct = BodyLogic.eActionGo.GO;
				break;
			default:
				break;
			}
		}
		if (eAct != BodyLogic.eActionGo.NONE) {
			return eAct;
		}

		if (abEmote[2] && abEmote[5] && !abEmote[1]) {
			switch (eRelation) {
			case FATHER:
			case MOTHER:
			case PARTNAR:
			case CHILD_FATHER:
			case CHILD_MOTHER:
			case ELDERSISTER:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EnvyCryAboutSisterInSurisuri));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = BodyLogic.eActionGo.GO;
				break;
			case YOUNGSISTER:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EnvyCryAboutElderSisterInSurisuri));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = BodyLogic.eActionGo.GO;
				break;
			default:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EnvyCryAboutOther));
				b.setHappiness(Happiness.SAD);
				b.stay();
				eAct = BodyLogic.eActionGo.WAIT;
				break;
			}
		}
		if (eAct != BodyLogic.eActionGo.NONE) {
			return eAct;
		}

		if (abEmote[5] && !abEmote[1]) {
			switch (eRelation) {
			case FATHER:
			case MOTHER:
			case PARTNAR:
			case CHILD_FATHER:
			case CHILD_MOTHER:
				break;
			case ELDERSISTER:
			case YOUNGSISTER:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EnvyAboutSisterInSurisuri));
				b.setHappiness(Happiness.SAD);
				b.stay();
				eAct = BodyLogic.eActionGo.GO;
				break;
			default:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EnvyAboutOther));
				b.setHappiness(Happiness.SAD);
				b.stay();
				eAct = BodyLogic.eActionGo.WAIT;
				break;
			}
		}
		if (eAct != BodyLogic.eActionGo.NONE) {
			return eAct;
		}

		if (abEmote[5] && abEmote[1]) {
			b.addMemories(-1);
			switch (eRelation) {
			case FATHER:
			case MOTHER:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateWithEnvyAboutChild));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = BodyLogic.eActionGo.WAIT;
				break;
			case PARTNAR:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateWithEnvyAboutPartner));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = BodyLogic.eActionGo.WAIT;
				break;
			case CHILD_FATHER:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateWithEnvyAboutFather));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = BodyLogic.eActionGo.WAIT;
				break;
			case CHILD_MOTHER:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateWithEnvyAboutMother));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = BodyLogic.eActionGo.WAIT;
				break;
			case ELDERSISTER:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateWithEnvyAboutSister));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = BodyLogic.eActionGo.WAIT;
				break;
			case YOUNGSISTER:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateWithEnvyAboutElderSister));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = BodyLogic.eActionGo.WAIT;
				break;
			default:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateWithEnvyAboutOther));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = BodyLogic.eActionGo.WAIT;
				break;
			}
		}
		if (eAct != BodyLogic.eActionGo.NONE) {
			return eAct;
		}

		if (!abEmote[2] && abEmote[4]) {
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Scare));
			b.setHappiness(Happiness.SAD);
			b.stay();
			eAct = BodyLogic.eActionGo.WAIT;
		}
		if (eAct != BodyLogic.eActionGo.NONE) {
			return eAct;
		}

		if (abEmote[2] && abEmote[6] && abEmote[4]) {
			switch (eRelation) {
			case FATHER:
			case MOTHER:
				eAct = BodyLogic.eActionGo.GO;
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.ConcernAboutChild));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				break;
			case PARTNAR:
				eAct = BodyLogic.eActionGo.GO;
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.ConcernAboutPartner));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				break;
			case CHILD_FATHER:
				eAct = BodyLogic.eActionGo.GO;
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.ConcernAboutFather));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				break;
			case CHILD_MOTHER:
				eAct = BodyLogic.eActionGo.GO;
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.ConcernAboutMother));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				break;
			case ELDERSISTER:
				eAct = BodyLogic.eActionGo.GO;
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.ConcernAboutEldersister));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				break;
			case YOUNGSISTER:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.ConcernAboutEldersister));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = BodyLogic.eActionGo.GO;
				break;
			default:
				break;
			}
		}
		if (eAct != BodyLogic.eActionGo.NONE) {
			return eAct;
		}

		if (abEmote[2] && abEmote[6] && !abEmote[4]) {
			switch (eRelation) {
			case FATHER:
			case MOTHER:
				eAct = BodyLogic.eActionGo.GO;
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.ConcernAboutChild));
				b.setHappiness(Happiness.SAD);
				b.stay();
				break;
			case PARTNAR:
				eAct = BodyLogic.eActionGo.GO;
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.ConcernAboutPartner));
				b.setHappiness(Happiness.SAD);
				b.stay();
				break;
			case CHILD_FATHER:
				eAct = BodyLogic.eActionGo.GO;
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.ConcernAboutFather));
				b.setHappiness(Happiness.SAD);
				b.stay();
				break;
			case CHILD_MOTHER:
				eAct = BodyLogic.eActionGo.GO;
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.ConcernAboutMother));
				b.setHappiness(Happiness.SAD);
				b.stay();
				break;
			case ELDERSISTER:
				eAct = BodyLogic.eActionGo.GO;
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.ConcernAboutEldersister));
				b.setHappiness(Happiness.SAD);
				b.stay();
				break;
			case YOUNGSISTER:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.ConcernAboutEldersister));
				b.setHappiness(Happiness.SAD);
				b.stay();
				eAct = BodyLogic.eActionGo.GO;
				break;
			default:
				break;
			}
		}
		if (eAct != BodyLogic.eActionGo.NONE) {
			return eAct;
		}

		if (abEmote[2] && !abEmote[6]) {
			switch (eRelation) {
			case FATHER:
			case MOTHER:
			case PARTNAR:
			case CHILD_FATHER:
			case CHILD_MOTHER:
			case ELDERSISTER:
			case YOUNGSISTER:
				break;
			default:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.MercyAboutOther));
				b.setHappiness(Happiness.SAD);
				b.stay();
				eAct = BodyLogic.eActionGo.GO;
				break;
			}
		}
		if (eAct != BodyLogic.eActionGo.NONE) {
			return eAct;
		}

		if (abEmote[0] && abEmote[0]) {
			switch (eRelation) {
			case FATHER:
			case MOTHER:
			case PARTNAR:
			case CHILD_FATHER:
			case CHILD_MOTHER:
			case ELDERSISTER:
			case YOUNGSISTER:
				break;
			default:
				eAct = BodyLogic.eActionGo.WAIT;
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateYukkuri));
				break;
			}
		}
		if (eAct == BodyLogic.eActionGo.NONE) {
			return eAct;
		}

		return eAct;
	}
}
