package org.simyukkuri.logic;

import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.TakeoutItemType;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;

/**
 * Skinship and family-contact handling used by YukkuriLogic.
 */
public final class YukkuriSkinshipRule {

	private YukkuriSkinshipRule() {
	}

	/**
	 * doActionOther の家族接触・スキンシップ分岐を処理する。
	 *
	 * @param targetBody 処理対象ゆっくり
	 * @param actorBody  行動主体ゆっくり
	 * @return 分岐が処理を担当した場合は true
	 */
	public static boolean handleSkinship(Yukkuri targetBody, Yukkuri actorBody) {
		// 相手がありに食われてる時
		if (targetBody.getAttachmentSize(Ants.class) != 0) {
			// 自分がアリに食われてない時のみ相手をぺろぺろする余裕がある
			if (actorBody.getAttachmentSize(Ants.class) == 0) {
				actorBody.doPeropero(targetBody);
			}
			actorBody.clearActions();
			return true;
		}

		// 餌を保持している
		if (actorBody.isParent(targetBody) && targetBody.isVeryHungry() && !targetBody.isAdult()
				&& actorBody.getCarryItem(TakeoutItemType.FOOD) != null) {
			// 吐き出す
			actorBody.setMessage(GameMessages.getMessage(actorBody, MessagePool.Action.GiveFood), false);
			actorBody.dropTakeoutItem(TakeoutItemType.FOOD);
			return true;
		}

		if (actorBody.isAdult() && !targetBody.isAdult()
				&& (targetBody.isChild(actorBody) || actorBody.isParent(targetBody))) {
			// 自分が親で相手が子供の時のスキンシップ
			actorBody.constraintDirection(targetBody, false);
			// 相手が汚れていてかつ自分が母親の時か、ランダムでぺろぺろ
			if ((targetBody.isDirty() && actorBody.isMother(targetBody)) || GameRandom.nextBoolean()) {
				actorBody.doPeropero(targetBody);
			} else if (GameRandom.nextBoolean()) {
				actorBody.doSurisuri(targetBody);
			}
			actorBody.clearActions();
			return true;
		}
		if (targetBody.isPartner(actorBody) && GameRandom.nextBoolean()) {
			// 相手が自分の番ならすりすり
			actorBody.constraintDirection(targetBody, false);
			actorBody.doSurisuri(targetBody);
			actorBody.clearActions();
			return true;
		}
		if (!actorBody.isAdult() && (actorBody.isChild(targetBody) || targetBody.isParent(actorBody))) {
			// 自分が子供で、相手が親の時のスキンシップ
			actorBody.constraintDirection(targetBody, false);
			// 自分が汚れた赤ゆなら、ぺろぺろしてもらう
			if (actorBody.isBaby() && actorBody.isDirty() && targetBody.isMother(actorBody)) {
				targetBody.doPeropero(actorBody);
			}
			// 親がダメージ食らってたらランダムでぺろぺろ
			if (targetBody.isDamaged() && GameRandom.nextBoolean()) {
				actorBody.doPeropero(targetBody);
			} else if (GameRandom.nextBoolean()) {
				actorBody.doSurisuri(targetBody);
			}
			actorBody.clearActions();
			return true;
		}
		if (!actorBody.isAdult() && actorBody.isSister(targetBody) && GameRandom.nextBoolean()) {
			// 姉妹の場合のスキンシップ
			// 善良で、赤ゆでなく、相手が汚れていたら無条件でぺろぺろ
			actorBody.constraintDirection(targetBody, false);
			if (actorBody.isSmart() && !actorBody.isBaby() && targetBody.isDirty()) {
				actorBody.doPeropero(targetBody);
			} else {
				if (targetBody.isDamaged() && GameRandom.nextBoolean()) {
					if (actorBody.isElderSister(targetBody)) {
						actorBody.setMessage(
								GameMessages.getMessage(actorBody, MessagePool.Action.ConcernAboutEldersister));
					} else {
						actorBody.setMessage(GameMessages.getMessage(actorBody, MessagePool.Action.ConcernAboutSister));
					}
					actorBody.setHappiness(Happiness.SAD);
					actorBody.stay();
					targetBody.stay();
				} else if (targetBody.isDamaged() && GameRandom.nextBoolean()) {
					actorBody.doPeropero(targetBody);
				} else if (GameRandom.nextBoolean()) {
					actorBody.doSurisuri(targetBody);
				}
			}
			actorBody.clearActions();
			return true;
		}

		return false;
	}
}
