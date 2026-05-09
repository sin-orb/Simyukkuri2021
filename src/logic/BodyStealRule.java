package src.logic;

import src.base.Body;
import src.entity.world.bodylinked.Okazari.OkazariType;
import src.enums.Happiness;
import src.enums.PublicRank;
import src.system.MessagePool;
import src.util.GameMessages;

/**
 * Okazari-steal handling used by BodyLogic.
 */
public final class BodyStealRule {

	private BodyStealRule() {
	}

	/**
	 * Handle the steal branch in doActionOther.
	 *
	 * @param targetBody target body
	 * @param actorBody actor body
	 * @return true when the steal branch handled the action
	 */
	public static boolean handleOkazariSteal(Body targetBody, Body actorBody) {
		if (!actorBody.hasOkazari() && targetBody.hasOkazari() && actorBody.getBodyAgeState() == targetBody.getBodyAgeState()
				&& actorBody.getType() == targetBody.getType() && !actorBody.isHybrid()
				&& targetBody.getOkazari().getOkazariType() == OkazariType.DEFAULT
				&& (targetBody.getPublicRank() == PublicRank.NONE || actorBody.getPublicRank() == PublicRank.UnunSlave)
				&& !actorBody.isLockmove()) {
			if (actorBody.isRude()) {
				if (!BodyWakeupRule.checkWakeupOtherYukkuri(actorBody)) {
					if (actorBody.getPublicRank() != PublicRank.NONE && targetBody.getPublicRank() == PublicRank.NONE) {
						actorBody.setPublicRank(PublicRank.NONE);
						targetBody.setPublicRank(PublicRank.UnunSlave);
					}
					targetBody.takeOkazari(false);
					actorBody.giveOkazari(OkazariType.DEFAULT);
					actorBody.setHappiness(Happiness.HAPPY);
					actorBody.setMessage(GameMessages.getMessage(actorBody, MessagePool.Action.GetOtherAccessoryStealthily));
					actorBody.addMemories(100);
					actorBody.addStress(-actorBody.getStressLimit() / 2);
					actorBody.clearActions();
					actorBody.stay();
					return true;
				}
			}
		}
		return false;
	}
}
