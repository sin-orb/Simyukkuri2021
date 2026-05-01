package src.logic;

import src.base.Body;
import src.base.Okazari.OkazariType;
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
	 * @param p target body
	 * @param b actor body
	 * @return true when the steal branch handled the action
	 */
	public static boolean handleOkazariSteal(Body p, Body b) {
		if (!b.hasOkazari() && p.hasOkazari() && b.getBodyAgeState() == p.getBodyAgeState()
				&& b.getType() == p.getType() && !b.isHybrid()
				&& p.getOkazari().getOkazariType() == OkazariType.DEFAULT
				&& (p.getPublicRank() == PublicRank.NONE || b.getPublicRank() == PublicRank.UnunSlave)
				&& !b.isLockmove()) {
			if (b.isRude()) {
				if (!BodyWakeupRule.checkWakeupOtherYukkuri(b)) {
					if (b.getPublicRank() != PublicRank.NONE && p.getPublicRank() == PublicRank.NONE) {
						b.setPublicRank(PublicRank.NONE);
						p.setPublicRank(PublicRank.UnunSlave);
					}
					p.takeOkazari(false);
					b.giveOkazari(OkazariType.DEFAULT);
					b.setHappiness(Happiness.HAPPY);
					b.setMessage(GameMessages.getMessage(b, MessagePool.Action.GetOtherAccessoryStealthily));
					b.addMemories(100);
					b.addStress(-b.getStressLimit() / 2);
					b.clearActions();
					b.stay();
					return true;
				}
			}
		}
		return false;
	}
}
