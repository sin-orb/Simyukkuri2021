package org.simyukkuri.logic;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Okazari.OkazariType;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;

/**
 * Okazari-steal handling used by YukkuriLogic.
 */
public final class YukkuriStealRule {

	private YukkuriStealRule() {
	}

	/**
	 * Handle the steal branch in doActionOther.
	 *
	 * @param targetBody target body
	 * @param actorBody  actor body
	 * @return true when the steal branch handled the action
	 */
	public static boolean handleOkazariSteal(Yukkuri targetBody, Yukkuri actorBody) {
		if (!actorBody.hasOkazari() && targetBody.hasOkazari()
				&& actorBody.getAgeState() == targetBody.getAgeState()
				&& actorBody.getType() == targetBody.getType() && !actorBody.isHybrid()
				&& targetBody.getOkazari().getOkazariType() == OkazariType.DEFAULT
				&& (targetBody.getPublicRank() == PublicRank.NONE || actorBody.getPublicRank() == PublicRank.UnunSlave)
				&& !actorBody.isLockmove()) {
			if (actorBody.isRude()) {
				if (!YukkuriWakeupRule.checkWakeupOtherYukkuri(actorBody)) {
					if (actorBody.getPublicRank() != PublicRank.NONE && targetBody.getPublicRank() == PublicRank.NONE) {
						actorBody.setPublicRank(PublicRank.NONE);
						targetBody.setPublicRank(PublicRank.UnunSlave);
					}
					targetBody.takeOkazari(false);
					actorBody.giveOkazari(OkazariType.DEFAULT);
					actorBody.setHappiness(Happiness.HAPPY);
					actorBody.setMessage(
							GameMessages.getMessage(actorBody, MessagePool.Action.GetOtherAccessoryStealthily));
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
