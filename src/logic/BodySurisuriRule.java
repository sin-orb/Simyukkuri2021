package src.logic;

import src.entity.core.living.yukkuri.Yukkuri;
import src.enums.EnumRelationMine;
import src.enums.Happiness;
import src.system.MessagePool;
import src.util.GameMessages;
import src.util.GameRandom;

/**
 * Surisuri reaction rule used by BodyLogic.
 */
public final class BodySurisuriRule {

	private BodySurisuriRule() {
	}

	/**
	 * Check action when another body is being petted by player.
	 *
	 * @param body       subject body
	 * @param targetBody target body
	 * @return action result
	 */
	public static final BodyLogic.ActionGo checkActionSurisuriFromPlayer(Yukkuri body, Yukkuri targetBody) {
		if (body == null || targetBody == null) {
			return BodyLogic.ActionGo.NONE;
		}
		if (!targetBody.isSurisuriFromPlayer()) {
			return BodyLogic.ActionGo.NONE;
		}

		if (GameRandom.nextInt(10) != 0) {
			return BodyLogic.ActionGo.NONE;
		}
		if (body.isIdiot() || body.isNYD()) {
			return BodyLogic.ActionGo.NONE;
		}

		boolean[] emotionFlags = EmotionLogic.checkEmotionForOther(body, targetBody);
		EnumRelationMine relation = BodyRelations.checkMyRelation(body, targetBody);
		BodyLogic.ActionGo actionGo = BodyLogic.ActionGo.NONE;

		if (emotionFlags[0]) {
			switch (relation) {
				case FATHER:
				case MOTHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.GladAboutChild));
					body.setHappiness(Happiness.HAPPY);
					body.stay();
					actionGo = BodyLogic.ActionGo.GO;
					break;
				case PARTNAR:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.GladAboutPartner));
					body.setHappiness(Happiness.HAPPY);
					body.stay();
					actionGo = BodyLogic.ActionGo.GO;
					break;
				case CHILD_FATHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.GladAboutFather));
					body.setHappiness(Happiness.HAPPY);
					body.stay();
					actionGo = BodyLogic.ActionGo.GO;
					break;
				case CHILD_MOTHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.GladAboutMother));
					body.setHappiness(Happiness.HAPPY);
					body.stay();
					actionGo = BodyLogic.ActionGo.GO;
					break;
				case ELDERSISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.GladAboutSister));
					body.setHappiness(Happiness.HAPPY);
					body.stay();
					actionGo = BodyLogic.ActionGo.GO;
					break;
				case YOUNGSISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.GladAboutElderSister));
					body.setHappiness(Happiness.HAPPY);
					body.stay();
					actionGo = BodyLogic.ActionGo.GO;
					break;
				default:
					break;
			}
		}
		if (actionGo != BodyLogic.ActionGo.NONE) {
			return actionGo;
		}

		if (emotionFlags[2] && emotionFlags[5] && !emotionFlags[1]) {
			switch (relation) {
				case FATHER:
				case MOTHER:
				case PARTNAR:
				case CHILD_FATHER:
				case CHILD_MOTHER:
				case ELDERSISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.EnvyCryAboutSisterInSurisuri));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = BodyLogic.ActionGo.GO;
					break;
				case YOUNGSISTER:
					body.setMessage(
							GameMessages.getMessage(body, MessagePool.Action.EnvyCryAboutElderSisterInSurisuri));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = BodyLogic.ActionGo.GO;
					break;
				default:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.EnvyCryAboutOther));
					body.setHappiness(Happiness.SAD);
					body.stay();
					actionGo = BodyLogic.ActionGo.WAIT;
					break;
			}
		}
		if (actionGo != BodyLogic.ActionGo.NONE) {
			return actionGo;
		}

		if (emotionFlags[5] && !emotionFlags[1]) {
			switch (relation) {
				case FATHER:
				case MOTHER:
				case PARTNAR:
				case CHILD_FATHER:
				case CHILD_MOTHER:
					break;
				case ELDERSISTER:
				case YOUNGSISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.EnvyAboutSisterInSurisuri));
					body.setHappiness(Happiness.SAD);
					body.stay();
					actionGo = BodyLogic.ActionGo.GO;
					break;
				default:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.EnvyAboutOther));
					body.setHappiness(Happiness.SAD);
					body.stay();
					actionGo = BodyLogic.ActionGo.WAIT;
					break;
			}
		}
		if (actionGo != BodyLogic.ActionGo.NONE) {
			return actionGo;
		}

		if (emotionFlags[5] && emotionFlags[1]) {
			body.addMemories(-1);
			switch (relation) {
				case FATHER:
				case MOTHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutChild));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = BodyLogic.ActionGo.WAIT;
					break;
				case PARTNAR:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutPartner));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = BodyLogic.ActionGo.WAIT;
					break;
				case CHILD_FATHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutFather));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = BodyLogic.ActionGo.WAIT;
					break;
				case CHILD_MOTHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutMother));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = BodyLogic.ActionGo.WAIT;
					break;
				case ELDERSISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutSister));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = BodyLogic.ActionGo.WAIT;
					break;
				case YOUNGSISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutElderSister));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = BodyLogic.ActionGo.WAIT;
					break;
				default:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutOther));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = BodyLogic.ActionGo.WAIT;
					break;
			}
		}
		if (actionGo != BodyLogic.ActionGo.NONE) {
			return actionGo;
		}

		if (!emotionFlags[2] && emotionFlags[4]) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Scare));
			body.setHappiness(Happiness.SAD);
			body.stay();
			actionGo = BodyLogic.ActionGo.WAIT;
		}
		if (actionGo != BodyLogic.ActionGo.NONE) {
			return actionGo;
		}

		if (emotionFlags[2] && emotionFlags[6] && emotionFlags[4]) {
			switch (relation) {
				case FATHER:
				case MOTHER:
					actionGo = BodyLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutChild));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					break;
				case PARTNAR:
					actionGo = BodyLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutPartner));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					break;
				case CHILD_FATHER:
					actionGo = BodyLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutFather));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					break;
				case CHILD_MOTHER:
					actionGo = BodyLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutMother));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					break;
				case ELDERSISTER:
					actionGo = BodyLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutEldersister));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					break;
				case YOUNGSISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutEldersister));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = BodyLogic.ActionGo.GO;
					break;
				default:
					break;
			}
		}
		if (actionGo != BodyLogic.ActionGo.NONE) {
			return actionGo;
		}

		if (emotionFlags[2] && emotionFlags[6] && !emotionFlags[4]) {
			switch (relation) {
				case FATHER:
				case MOTHER:
					actionGo = BodyLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutChild));
					body.setHappiness(Happiness.SAD);
					body.stay();
					break;
				case PARTNAR:
					actionGo = BodyLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutPartner));
					body.setHappiness(Happiness.SAD);
					body.stay();
					break;
				case CHILD_FATHER:
					actionGo = BodyLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutFather));
					body.setHappiness(Happiness.SAD);
					body.stay();
					break;
				case CHILD_MOTHER:
					actionGo = BodyLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutMother));
					body.setHappiness(Happiness.SAD);
					body.stay();
					break;
				case ELDERSISTER:
					actionGo = BodyLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutEldersister));
					body.setHappiness(Happiness.SAD);
					body.stay();
					break;
				case YOUNGSISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutEldersister));
					body.setHappiness(Happiness.SAD);
					body.stay();
					actionGo = BodyLogic.ActionGo.GO;
					break;
				default:
					break;
			}
		}
		if (actionGo != BodyLogic.ActionGo.NONE) {
			return actionGo;
		}

		if (emotionFlags[2] && !emotionFlags[6]) {
			switch (relation) {
				case FATHER:
				case MOTHER:
				case PARTNAR:
				case CHILD_FATHER:
				case CHILD_MOTHER:
				case ELDERSISTER:
				case YOUNGSISTER:
					break;
				default:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.MercyAboutOther));
					body.setHappiness(Happiness.SAD);
					body.stay();
					actionGo = BodyLogic.ActionGo.GO;
					break;
			}
		}
		if (actionGo != BodyLogic.ActionGo.NONE) {
			return actionGo;
		}

		if (emotionFlags[0]) {
			switch (relation) {
				case FATHER:
				case MOTHER:
				case PARTNAR:
				case CHILD_FATHER:
				case CHILD_MOTHER:
				case ELDERSISTER:
				case YOUNGSISTER:
					break;
				default:
					actionGo = BodyLogic.ActionGo.WAIT;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateYukkuri));
					break;
			}
		}
		if (actionGo == BodyLogic.ActionGo.NONE) {
			return actionGo;
		}

		return actionGo;
	}

}
