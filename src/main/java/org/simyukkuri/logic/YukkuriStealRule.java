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
	 * doActionOther の盗み分岐を処理する。
	 *
	 * @param targetBody 処理対象ゆっくり
	 * @param actorBody  行動主体ゆっくり
	 * @return 盗み分岐が処理を担当した場合は true
	 */
	public static boolean handleOkazariSteal(Yukkuri targetBody, Yukkuri actorBody) {
		if (!actorBody.hasOkazari() && targetBody.hasOkazari()
				&& actorBody.getAgeState() == targetBody.getAgeState()
				&& actorBody.getType() == targetBody.getType() && !actorBody.isHybrid()
				&& targetBody.getOkazaris().getOkazariType() == OkazariType.DEFAULT
				&& (targetBody.getPublicRank() == PublicRank.NONE || actorBody.getPublicRank() == PublicRank.UNUN_SLAVE)
				&& !actorBody.isLockmove()) {
			if (actorBody.isRude()) {
				if (!YukkuriWakeupRule.checkWakeupOtherYukkuri(actorBody)) {
					if (actorBody.getPublicRank() != PublicRank.NONE && targetBody.getPublicRank() == PublicRank.NONE) {
						actorBody.setPublicRank(PublicRank.NONE);
						targetBody.setPublicRank(PublicRank.UNUN_SLAVE);
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
