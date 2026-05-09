package src.logic;

import src.attachment.Ants;
import src.base.Body;
import src.enums.Intelligence;
import src.util.GameRandom;

/**
 * Found-body affinity handling used by BodyLogic.checkPartner.
 */
public final class BodyFoundAffinityRule {

	private BodyFoundAffinityRule() {
	}

	/**
	 * Handle the target-directed approach rules for a target body.
	 *
	 * @param actorBody actor body
	 * @param targetBody target body
	 * @param colX      collision-adjusted x coordinate
	 * @param mz        destination z coordinate
	 * @return true when a branch consumed the action
	 */
	public static boolean handleFoundAffinity(Body actorBody, Body targetBody, int colX, int mz) {
		if (actorBody.getPublicRank() != targetBody.getPublicRank()) {
			return false;
		}

		// 相手に針が刺さっている場合
		if (targetBody.isNeedled()) {
			// ランダムで向かう
			if (GameRandom.nextInt(50) == 0) {
				if (actorBody.isAdult() && !targetBody.isAdult()
						&& (targetBody.isChild(actorBody) || actorBody.isMother(targetBody))) {
					// 自分が母親で相手が針の刺さった子供ならぐーりぐーりしにいく
					actorBody.moveToBody(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
					actorBody.setTargetBind(false);
				} else if (targetBody.isPartner(actorBody)) {
					// つがいで相手が針の刺さっているならぐーりぐーりしにいく
					actorBody.moveToBody(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
					actorBody.setTargetBind(false);
				}
			}
			return true;
		}

		// 以下相手に針が刺さっていない場合
		if (GameRandom.nextBoolean()) {
			if (actorBody.isAdult() && !targetBody.isAdult()
					&& (targetBody.isChild(actorBody) || actorBody.isMother(targetBody))
					&& (actorBody.getIntelligence() == Intelligence.FOOL && !actorBody.hasOkazari())) {
				// 相手が子供でも、子供にお飾りがなくてかつ親がバカならよらない
				return true;
			}
			// 相手が子か番で、アリに食われていたらそっちに向かう
			if (((targetBody.isChild(actorBody) || targetBody.isMother(actorBody)) || targetBody.isPartner(actorBody))
					&& targetBody.getAttachmentSize(Ants.class) != 0) {
				actorBody.moveToBody(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
				actorBody.setTargetBind(true);
				return true;
			} else if (actorBody.isAdult() && !targetBody.isAdult() && targetBody.isNormalDirty()
					&& (targetBody.isChild(actorBody) || actorBody.isMother(targetBody))) {
				// 相手が汚れた子供ならぺろぺろしに向かう
				actorBody.moveToBody(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
				actorBody.setTargetBind(true);
				return true;
			} else if (actorBody.isChild(targetBody) && !actorBody.isAdult() && actorBody.isDirty()) {
				// 自分が汚れた子供なら家族のところへ向かう
				actorBody.moveToBody(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
				actorBody.setTargetBind(true);
				return true;
			}
		}

		// ランダムでつがいのところへ向かう
		if (targetBody.isPartner(actorBody)) {
			if (GameRandom.nextInt(150) == 0) {
				actorBody.moveToBody(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
				actorBody.setTargetBind(false);
				return true;
			}
		}

		// ランダムで親のところへ向かう
		if (!actorBody.isAdult() && actorBody.isChild(targetBody)) {
			if (GameRandom.nextInt(100) == 0) {
				actorBody.moveToBody(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
				actorBody.setTargetBind(false);
				return true;
			}
		}

		// ランダムで姉妹のところへ向かう
		if (!actorBody.isAdult() && actorBody.isSister(targetBody)) {
			if (GameRandom.nextInt(150) == 0) {
				actorBody.moveToBody(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
				actorBody.setTargetBind(false);
				return true;
			}
		}

		// ランダムで家族のところへ向かう
		if (actorBody.isAdult() && !targetBody.isAdult() && actorBody.isFamily(targetBody)) {
			if (GameRandom.nextInt(150) == 0) {
				actorBody.moveToBody(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
				actorBody.setTargetBind(false);
				return true;
			}
		}

		return false;
	}
}
