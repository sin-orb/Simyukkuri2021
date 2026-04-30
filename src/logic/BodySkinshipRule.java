package src.logic;

import src.attachment.Ants;
import src.base.Body;
import src.enums.Happiness;
import src.enums.TakeoutItemType;
import src.util.GameMessages;
import src.util.GameRandom;
import src.system.MessagePool;

/**
 * Skinship and family-contact handling used by BodyLogic.
 */
public final class BodySkinshipRule {

	private BodySkinshipRule() {
	}

	/**
	 * Handle the family-contact and skinship branches in doActionOther.
	 *
	 * @param p target body
	 * @param b actor body
	 * @return true when the branch handled the action
	 */
	public static boolean handleSkinship(Body p, Body b) {
		// 相手がありに食われてる時
		if (p.getAttachmentSize(Ants.class) != 0) {
			// 自分がアリに食われてない時のみ相手をぺろぺろする余裕がある
			if (b.getAttachmentSize(Ants.class) == 0) {
				b.doPeropero(p);
			}
			b.clearActions();
			return true;
		}

		// 餌を保持している
		if (b.isParent(p) && p.isVeryHungry() && !p.isAdult() && b.getTakeoutItem(TakeoutItemType.FOOD) != null) {
			// 吐き出す
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.GiveFood), false);
			b.dropTakeoutItem(TakeoutItemType.FOOD);
			return true;
		}

		if (b.isAdult() && !p.isAdult() && (p.isChild(b) || b.isParent(p))) {
			// 自分が親で相手が子供の時のスキンシップ
			b.constraintDirection(p, false);
			// 相手が汚れていてかつ自分が母親の時か、ランダムでぺろぺろ
			if ((p.isDirty() && b.isMother(p)) || GameRandom.nextBoolean()) {
				b.doPeropero(p);
			}
			// 他はすりすり
			else if (GameRandom.nextBoolean()) {
				b.doSurisuri(p);
			}
			b.clearActions();
			return true;
		}
		if (p.isPartner(b) && GameRandom.nextBoolean()) {
			// 相手が自分の番ならすりすり
			b.constraintDirection(p, false);
			b.doSurisuri(p);
			b.clearActions();
			return true;
		}
		if (!b.isAdult() && (b.isChild(p) || p.isParent(b))) {
			// 自分が子供で、相手が親の時のスキンシップ
			b.constraintDirection(p, false);
			// 自分が汚れた赤ゆなら、ぺろぺろしてもらう
			if (b.isBaby() && b.isDirty() && p.isMother(b)) {
				p.doPeropero(b);
			}
			// 親がダメージ食らってたらランダムでぺろぺろ
			if (p.isDamaged() && GameRandom.nextBoolean()) {
				b.doPeropero(p);
			}
			// 他はすりすり
			else if (GameRandom.nextBoolean()) {
				b.doSurisuri(p);
			}
			b.clearActions();
			return true;
		}
		if (!b.isAdult() && b.isSister(p) && GameRandom.nextBoolean()) {
			// 姉妹の場合のスキンシップ
			// 善良で、赤ゆでなく、相手が汚れていたら無条件でぺろぺろ
			b.constraintDirection(p, false);
			if (b.isSmart() && !b.isBaby() && p.isDirty()) {
				b.doPeropero(p);
			} else {
				if (p.isDamaged() && GameRandom.nextBoolean()) {
					if (b.isElderSister(p)) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.ConcernAboutEldersister));
					} else {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.ConcernAboutSister));
					}
					b.setHappiness(Happiness.SAD);
					b.stay();
					p.stay();
				} else if (p.isDamaged() && GameRandom.nextBoolean()) {
					b.doPeropero(p);
				} else if (GameRandom.nextBoolean()) {
					b.doSurisuri(p);
				}
			}
			b.clearActions();
			return true;
		}

		return false;
	}
}
