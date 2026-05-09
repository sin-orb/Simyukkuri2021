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
	 * @param targetBody target body
	 * @param actorBody actor body
	 * @return true when the dead-body branch handled the action
	 */
	public static boolean handleDeadBodyInteraction(Body targetBody, Body actorBody) {
		if (actorBody.isExciting()) {
			if (actorBody.isRaper()) {
				if (!targetBody.isRaper()) {
					actorBody.doRape(targetBody);
					actorBody.clearActions();
					return true;
				}
			} else {
				actorBody.doOnanism(targetBody);
				actorBody.clearActions();
				return true;
			}
		}

		if (actorBody.isAdult()) {
			if (!actorBody.isTalking()) {
				if (actorBody.isParent(targetBody)) {
					actorBody.setMessage(GameMessages.getMessage(actorBody, MessagePool.Action.SadnessForChild));
					if (actorBody.checkWait(2000)) {
						actorBody.setLastActionTime();
						EventLogic.addWorldEvent(new FuneralEvent(actorBody, targetBody, null, 10), actorBody, null);
					}
				} else if (actorBody.isPartner(targetBody)) {
					actorBody.setMessage(GameMessages.getMessage(actorBody, MessagePool.Action.SadnessForPartner));
				}
				actorBody.setHappiness(Happiness.VERY_SAD);
				actorBody.addMemories(-2);
				actorBody.addStress(100);
				return true;
			}
		}

		if (targetBody.isParent(actorBody)) {
			if (!actorBody.isTalking()) {
				actorBody.setMessage(GameMessages.getMessage(actorBody, MessagePool.Action.SadnessForParent));
			}
			actorBody.setHappiness(Happiness.VERY_SAD);
			actorBody.setForceFace(ImageCode.SURPRISE.ordinal());
			actorBody.addMemories(-2);
			actorBody.addStress(100);
			return true;
		}
		if (actorBody.isSister(targetBody)) {
			if (!actorBody.isTalking()) {
				if (actorBody.getAge() < targetBody.getAge()) {
					actorBody.setMessage(GameMessages.getMessage(actorBody, MessagePool.Action.SadnessForEldersister));
				} else {
					actorBody.setMessage(GameMessages.getMessage(actorBody, MessagePool.Action.SadnessForSister));
				}
				actorBody.setHappiness(Happiness.VERY_SAD);
				actorBody.addStress(100);
				actorBody.addMemories(-2);
			}
		}
		return true;
	}
}
