package org.simyukkuri.logic;

import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.util.GameRandom;

/**
 * Found-body affinity handling used by YukkuriLogic.checkPartner.
 */
public final class YukkuriFoundAffinityRule {

	private YukkuriFoundAffinityRule() {
	}

	/**
	 * 対象ゆっくりへの接近ルールを処理する。
	 *
	 * @param actorBody  行動主体ゆっくり
	 * @param targetBody 処理対象ゆっくり
	 * @param colX       衝突補正後のX座標
	 * @param mz         目標Z座標
	 * @return 分岐が処理を消費した場合は true
	 */
	public static boolean handleFoundAffinity(Yukkuri actorBody, Yukkuri targetBody, int colX, int mz) {
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
					actorBody.moveToYukkuri(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
					actorBody.setTargetBind(false);
				} else if (targetBody.isPartner(actorBody)) {
					// つがいで相手が針の刺さっているならぐーりぐーりしにいく
					actorBody.moveToYukkuri(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
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
				actorBody.moveToYukkuri(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
				actorBody.setTargetBind(true);
				return true;
			} else if (actorBody.isAdult() && !targetBody.isAdult() && targetBody.isNormalDirty()
					&& (targetBody.isChild(actorBody) || actorBody.isMother(targetBody))) {
				// 相手が汚れた子供ならぺろぺろしに向かう
				actorBody.moveToYukkuri(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
				actorBody.setTargetBind(true);
				return true;
			} else if (actorBody.isChild(targetBody) && !actorBody.isAdult() && actorBody.isDirty()) {
				// 自分が汚れた子供なら家族のところへ向かう
				actorBody.moveToYukkuri(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
				actorBody.setTargetBind(true);
				return true;
			}
		}

		// ランダムでつがいのところへ向かう
		if (targetBody.isPartner(actorBody)) {
			if (GameRandom.nextInt(150) == 0) {
				actorBody.moveToYukkuri(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
				actorBody.setTargetBind(false);
				return true;
			}
		}

		// ランダムで親のところへ向かう
		if (!actorBody.isAdult() && actorBody.isChild(targetBody)) {
			if (GameRandom.nextInt(100) == 0) {
				actorBody.moveToYukkuri(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
				actorBody.setTargetBind(false);
				return true;
			}
		}

		// ランダムで姉妹のところへ向かう
		if (!actorBody.isAdult() && actorBody.isSister(targetBody)) {
			if (GameRandom.nextInt(150) == 0) {
				actorBody.moveToYukkuri(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
				actorBody.setTargetBind(false);
				return true;
			}
		}

		// ランダムで家族のところへ向かう
		if (actorBody.isAdult() && !targetBody.isAdult() && actorBody.isFamily(targetBody)) {
			if (GameRandom.nextInt(150) == 0) {
				actorBody.moveToYukkuri(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
				actorBody.setTargetBind(false);
				return true;
			}
		}

		return false;
	}
}
