package src.logic;

import src.base.Body;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.event.FuneralEvent;
import src.system.MessagePool;
import src.util.GameMessages;

/**
 * Dead-body interaction rule used by BodyLogic.
 */
public final class BodyDeadActionRule {

	private BodyDeadActionRule() {
	}

	/**
	 * Handle interaction with a dead target.
	 *
	 * @param p target body
	 * @param b actor body
	 * @return true when the dead-body branch handled the action
	 */
	public static boolean handleDeadBodyInteraction(Body p, Body b) {
		if (b.isExciting()) {
			if (b.isRaper()) {
				if (!p.isRaper()) {
					b.doRape(p);
					b.clearActions();
					return true;
				}
			} else {
				b.doOnanism(p);
				b.clearActions();
				return true;
			}
		}

		if (b.isAdult()) {
			if (!b.isTalking()) {
				if (b.isParent(p)) {
					b.setMessage(GameMessages.getMessage(b, MessagePool.Action.SadnessForChild));
					if (b.checkWait(2000)) {
						b.setLastActionTime();
						EventLogic.addWorldEvent(new FuneralEvent(b, p, null, 10), b, null);
					}
				} else if (b.isPartner(p)) {
					b.setMessage(GameMessages.getMessage(b, MessagePool.Action.SadnessForPartner));
				}
				b.setHappiness(Happiness.VERY_SAD);
				b.addMemories(-2);
				b.addStress(100);
				return true;
			}
		}

		if (p.isParent(b)) {
			if (!b.isTalking()) {
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.SadnessForParent));
			}
			b.setHappiness(Happiness.VERY_SAD);
			b.setForceFace(ImageCode.SURPRISE.ordinal());
			b.addMemories(-2);
			b.addStress(100);
			return true;
		}
		if (b.isSister(p)) {
			if (!b.isTalking()) {
				if (b.getAge() < p.getAge()) {
					b.setMessage(GameMessages.getMessage(b, MessagePool.Action.SadnessForEldersister));
				} else {
					b.setMessage(GameMessages.getMessage(b, MessagePool.Action.SadnessForSister));
				}
				b.setHappiness(Happiness.VERY_SAD);
				b.addStress(100);
				b.addMemories(-2);
			}
		}
		return true;
	}
}
