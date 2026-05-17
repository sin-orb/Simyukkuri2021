package org.simyukkuri.logic;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.YukkuriRelationType;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;

/**
 * Surisuri reaction rule used by YukkuriLogic.
 */
public final class YukkuriSurisuriRule {

	private YukkuriSurisuriRule() {
	}

	/**
	 * プレイヤーになでられているゆっくりに隣接している時の行動を処理する。
	 *
	 * @param body       ゆっくり
	 * @param targetBody 処理対象ゆっくり
	 * @return 行動結果
	 */
	public static final YukkuriLogic.ActionGo checkActionSurisuriFromPlayer(Yukkuri body, Yukkuri targetBody) {
		if (body == null || targetBody == null) {
			return YukkuriLogic.ActionGo.NONE;
		}
		if (!targetBody.isSurisuriFromPlayer()) {
			return YukkuriLogic.ActionGo.NONE;
		}

		if (GameRandom.nextInt(10) != 0) {
			return YukkuriLogic.ActionGo.NONE;
		}
		if (body.isIdiot() || body.isNYD()) {
			return YukkuriLogic.ActionGo.NONE;
		}

		boolean[] emotionFlags = EmotionLogic.checkEmotionForOther(body, targetBody);
		YukkuriRelationType relation = YukkuriRelations.checkMyRelation(body, targetBody);
		YukkuriLogic.ActionGo actionGo = YukkuriLogic.ActionGo.NONE;

		if (emotionFlags[0]) {
			switch (relation) {
				case FATHER:
				case MOTHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.GladAboutChild));
					body.setHappiness(Happiness.HAPPY);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.GO;
					break;
				case PARTNER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.GladAboutPartner));
					body.setHappiness(Happiness.HAPPY);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.GO;
					break;
				case CHILD_OF_FATHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.GladAboutFather));
					body.setHappiness(Happiness.HAPPY);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.GO;
					break;
				case CHILD_OF_MOTHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.GladAboutMother));
					body.setHappiness(Happiness.HAPPY);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.GO;
					break;
				case ELDER_SISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.GladAboutSister));
					body.setHappiness(Happiness.HAPPY);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.GO;
					break;
				case YOUNGER_SISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.GladAboutElderSister));
					body.setHappiness(Happiness.HAPPY);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.GO;
					break;
				default:
					break;
			}
		}
		if (actionGo != YukkuriLogic.ActionGo.NONE) {
			return actionGo;
		}

		if (emotionFlags[2] && emotionFlags[5] && !emotionFlags[1]) {
			switch (relation) {
				case FATHER:
				case MOTHER:
				case PARTNER:
				case CHILD_OF_FATHER:
				case CHILD_OF_MOTHER:
				case ELDER_SISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.EnvyCryAboutSisterInSurisuri));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.GO;
					break;
				case YOUNGER_SISTER:
					body.setMessage(
							GameMessages.getMessage(body, MessagePool.Action.EnvyCryAboutElderSisterInSurisuri));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.GO;
					break;
				default:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.EnvyCryAboutOther));
					body.setHappiness(Happiness.SAD);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.WAIT;
					break;
			}
		}
		if (actionGo != YukkuriLogic.ActionGo.NONE) {
			return actionGo;
		}

		if (emotionFlags[5] && !emotionFlags[1]) {
			switch (relation) {
				case FATHER:
				case MOTHER:
				case PARTNER:
				case CHILD_OF_FATHER:
				case CHILD_OF_MOTHER:
					break;
				case ELDER_SISTER:
				case YOUNGER_SISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.EnvyAboutSisterInSurisuri));
					body.setHappiness(Happiness.SAD);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.GO;
					break;
				default:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.EnvyAboutOther));
					body.setHappiness(Happiness.SAD);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.WAIT;
					break;
			}
		}
		if (actionGo != YukkuriLogic.ActionGo.NONE) {
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
					actionGo = YukkuriLogic.ActionGo.WAIT;
					break;
				case PARTNER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutPartner));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.WAIT;
					break;
				case CHILD_OF_FATHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutFather));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.WAIT;
					break;
				case CHILD_OF_MOTHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutMother));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.WAIT;
					break;
				case ELDER_SISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutSister));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.WAIT;
					break;
				case YOUNGER_SISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutElderSister));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.WAIT;
					break;
				default:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutOther));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.WAIT;
					break;
			}
		}
		if (actionGo != YukkuriLogic.ActionGo.NONE) {
			return actionGo;
		}

		if (!emotionFlags[2] && emotionFlags[4]) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Scare));
			body.setHappiness(Happiness.SAD);
			body.stay();
			actionGo = YukkuriLogic.ActionGo.WAIT;
		}
		if (actionGo != YukkuriLogic.ActionGo.NONE) {
			return actionGo;
		}

		if (emotionFlags[2] && emotionFlags[6] && emotionFlags[4]) {
			switch (relation) {
				case FATHER:
				case MOTHER:
					actionGo = YukkuriLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutChild));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					break;
				case PARTNER:
					actionGo = YukkuriLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutPartner));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					break;
				case CHILD_OF_FATHER:
					actionGo = YukkuriLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutFather));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					break;
				case CHILD_OF_MOTHER:
					actionGo = YukkuriLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutMother));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					break;
				case ELDER_SISTER:
					actionGo = YukkuriLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutEldersister));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					break;
				case YOUNGER_SISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutEldersister));
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.GO;
					break;
				default:
					break;
			}
		}
		if (actionGo != YukkuriLogic.ActionGo.NONE) {
			return actionGo;
		}

		if (emotionFlags[2] && emotionFlags[6] && !emotionFlags[4]) {
			switch (relation) {
				case FATHER:
				case MOTHER:
					actionGo = YukkuriLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutChild));
					body.setHappiness(Happiness.SAD);
					body.stay();
					break;
				case PARTNER:
					actionGo = YukkuriLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutPartner));
					body.setHappiness(Happiness.SAD);
					body.stay();
					break;
				case CHILD_OF_FATHER:
					actionGo = YukkuriLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutFather));
					body.setHappiness(Happiness.SAD);
					body.stay();
					break;
				case CHILD_OF_MOTHER:
					actionGo = YukkuriLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutMother));
					body.setHappiness(Happiness.SAD);
					body.stay();
					break;
				case ELDER_SISTER:
					actionGo = YukkuriLogic.ActionGo.GO;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutEldersister));
					body.setHappiness(Happiness.SAD);
					body.stay();
					break;
				case YOUNGER_SISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutEldersister));
					body.setHappiness(Happiness.SAD);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.GO;
					break;
				default:
					break;
			}
		}
		if (actionGo != YukkuriLogic.ActionGo.NONE) {
			return actionGo;
		}

		if (emotionFlags[2] && !emotionFlags[6]) {
			switch (relation) {
				case FATHER:
				case MOTHER:
				case PARTNER:
				case CHILD_OF_FATHER:
				case CHILD_OF_MOTHER:
				case ELDER_SISTER:
				case YOUNGER_SISTER:
					break;
				default:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.MercyAboutOther));
					body.setHappiness(Happiness.SAD);
					body.stay();
					actionGo = YukkuriLogic.ActionGo.GO;
					break;
			}
		}
		if (actionGo != YukkuriLogic.ActionGo.NONE) {
			return actionGo;
		}

		if (emotionFlags[0]) {
			switch (relation) {
				case FATHER:
				case MOTHER:
				case PARTNER:
				case CHILD_OF_FATHER:
				case CHILD_OF_MOTHER:
				case ELDER_SISTER:
				case YOUNGER_SISTER:
					break;
				default:
					actionGo = YukkuriLogic.ActionGo.WAIT;
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateYukkuri));
					break;
			}
		}
		if (actionGo == YukkuriLogic.ActionGo.NONE) {
			return actionGo;
		}

		return actionGo;
	}

}
