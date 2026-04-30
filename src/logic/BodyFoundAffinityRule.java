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
	 * Handle the target-directed approach rules for a found body.
	 *
	 * @param b      actor body
	 * @param found  found body
	 * @param colX   collision-adjusted x coordinate
	 * @param mz     destination z coordinate
	 * @return true when a branch consumed the action
	 */
	public static boolean handleFoundAffinity(Body b, Body found, int colX, int mz) {
		if (b.getPublicRank() != found.getPublicRank()) {
			return false;
		}

		// 相手に針が刺さっている場合
		if (found.isNeedled()) {
			// ランダムで向かう
			if (GameRandom.nextInt(50) == 0) {
				if (b.isAdult() && !found.isAdult() && (found.isChild(b) || b.isMother(found))) {
					// 自分が母親で相手が針の刺さった子供ならぐーりぐーりしにいく
					b.moveToBody(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(false);
				} else if (found.isPartner(b)) {
					// つがいで相手が針の刺さっているならぐーりぐーりしにいく
					b.moveToBody(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(false);
				}
			}
			return true;
		}

		// 以下相手に針が刺さっていない場合
		if (GameRandom.nextBoolean()) {
			if (b.isAdult() && !found.isAdult() && (found.isChild(b) || b.isMother(found))
					&& (b.getIntelligence() == Intelligence.FOOL && !b.hasOkazari())) {
				// 相手が子供でも、子供にお飾りがなくてかつ親がバカならよらない
				return true;
			}
			// 相手が子か番で、アリに食われていたらそっちに向かう
			if (((found.isChild(b) || b.isMother(found)) || found.isPartner(b))
					&& found.getAttachmentSize(Ants.class) != 0) {
				b.moveToBody(found, found.getX() + colX, found.getY(), mz);
				b.setTargetBind(true);
				return true;
			} else if (b.isAdult() && !found.isAdult() && found.isNormalDirty()
					&& (found.isChild(b) || b.isMother(found))) {
				// 相手が汚れた子供ならぺろぺろしに向かう
				b.moveToBody(found, found.getX() + colX, found.getY(), mz);
				b.setTargetBind(true);
				return true;
			} else if (b.isChild(found) && !b.isAdult() && b.isDirty()) {
				// 自分が汚れた子供なら家族のところへ向かう
				b.moveToBody(found, found.getX() + colX, found.getY(), mz);
				b.setTargetBind(true);
				return true;
			}
		}

		// ランダムでつがいのところへ向かう
		if (found.isPartner(b)) {
			if (GameRandom.nextInt(150) == 0) {
				b.moveToBody(found, found.getX() + colX, found.getY(), mz);
				b.setTargetBind(false);
				return true;
			}
		}

		// ランダムで親のところへ向かう
		if (!b.isAdult() && b.isChild(found)) {
			if (GameRandom.nextInt(100) == 0) {
				b.moveToBody(found, found.getX() + colX, found.getY(), mz);
				b.setTargetBind(false);
				return true;
			}
		}

		// ランダムで姉妹のところへ向かう
		if (!b.isAdult() && b.isSister(found)) {
			if (GameRandom.nextInt(150) == 0) {
				b.moveToBody(found, found.getX() + colX, found.getY(), mz);
				b.setTargetBind(false);
				return true;
			}
		}

		// ランダムで家族のところへ向かう
		if (b.isAdult() && !found.isAdult() && b.isFamily(found)) {
			if (GameRandom.nextInt(150) == 0) {
				b.moveToBody(found, found.getX() + colX, found.getY(), mz);
				b.setTargetBind(false);
				return true;
			}
		}

		return false;
	}
}
