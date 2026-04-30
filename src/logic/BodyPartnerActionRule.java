package src.logic;

import src.base.Body;
import src.base.EventPacket.EventPriority;
import src.base.Okazari.OkazariType;
import src.draw.Translate;
import src.enums.Attitude;
import src.enums.PublicRank;
import src.event.HateNoOkazariEvent;
import src.event.ProposeEvent;
import src.system.MessagePool;
import src.util.GameMessages;
import src.util.GameRandom;

/**
 * 目標が見つかった後の partner 行動ロジック.
 */
public final class BodyPartnerActionRule {

	private BodyPartnerActionRule() {
	}

	/**
	 * 目標が見つかった場合の行動を処理する.
	 *
	 * @param b 自分
	 * @param found 対象
	 * @param bodyHasOkazari おかざり対象
	 * @param colX 衝突補正X
	 * @param mz Z移動量
	 * @return 行動したかどうか
	 */
	public static boolean handleFoundTarget(Body b, Body found, Body bodyHasOkazari, int colX, int mz) {
		// 相手が死体でなく、かつ除去されてなければ
		if (!found.isDead() && !found.isRemoved()) {
			// 生まれていない
			if (b.isUnBirth()) {
				return false;
			}
			// 生まれていない
			if (found.isUnBirth()) {
				return false;
			}

			// 自分が発情していればすっきりに向かう
			if (b.isExciting()) {
				// 自分がれいぱー/既婚/（ドゲスで1/10の確率）の場合はすっきりしに行く
				if (b.isRaper() || (b.isVeryRude() && GameRandom.nextInt(10) == 0) || b.isPartner(found)
						|| found.isPartner(b)) {
					b.moveToSukkiri(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(true);
				}
				// れいぱーでない独身勢はプロポーズへ
				else {
					// ただし、相手が足りないゆの時はキャンセル
					if (found.isIdiot() && !b.isIdiot()) {
						b.setCalm();
						return true;
					}
					// ドゲスの場合は50%の確率でプロポーズをする
					if (b.getAttitude() != Attitude.SUPER_SHITHEAD
							|| (b.getAttitude() == Attitude.SUPER_SHITHEAD && GameRandom.nextBoolean())) {
						EventLogic.addBodyEvent(b, new ProposeEvent(b, found, null, 1), null, null);
						return true;
					} else if (b.getAttitude() == Attitude.SUPER_SHITHEAD) {
						// ドゲスの場合は50%の確率でレイプだけする
						b.moveToSukkiri(found, found.getX() + colX, found.getY(), mz);
						b.setTargetBind(true);
					}
				}
				return true;
			}

			if (!found.hasOkazari() && b.getOkazari() != null
					&& b.getOkazari().getOkazariType() == OkazariType.DEFAULT && b.isRude() && !b.isIdiot()
					&& !b.isDamaged() && !found.isUnBirth() && b.getCurrentEvent() == null) {
				// 自分が通常種で相手が捕食種の場合は参加しない
				if (b.isPredatorType() || !found.isPredatorType()) {
					// 相手がおかざりのないゆっくりなら制裁を呼びかける
					if (b.isVeryRude() || !found.isDamaged()) {
						if (GameRandom.nextInt(20) == 0) {
							if (!b.isTalking()) {
								// 自分がうんうん奴隷ではない場合
								if (b.getPublicRank() != PublicRank.UnunSlave) {
									// 非ゆっくり症は参加しない
									if (b.isNotNYD() && found.isNotNYD()) {
										EventLogic.addWorldEvent(new HateNoOkazariEvent(b, found, null, 10), b,
												GameMessages.getMessage(b, MessagePool.Action.HateYukkuri));
									}
								}
							}
							return true;
						}
					}
				}
			}

			// プレイヤーにすりすりされていた場合の処理
			BodyLogic.eActionGo eAct = BodyLogic.checkActionSurisuriFromPlayer(b, found);
			if (eAct != BodyLogic.eActionGo.NONE) {
				if (eAct == BodyLogic.eActionGo.GO) {
					// 近づきすぎないように近づく
					b.moveToBody(found, found.getX() + colX * 2, found.getY(), mz);
					b.setTargetBind(false);
				}
				return true;
			}

			if (BodyLogic.checkEmotionFromUnunSlave(b, found)) {
				return true;
			}

			// 自分のおかざりがなくて、相手にお飾りがある。うんうん奴隷のものは奪わない
			if (bodyHasOkazari != null) {
				b.setToSteal(false);
				// 視界内に起きているゆっくりがいない
				if (!BodyLogic.checkWakeupOtherYukkuri(b) || GameRandom.nextInt(20) == 0) {
					b.moveToBody(bodyHasOkazari, bodyHasOkazari.getX() + colX, bodyHasOkazari.getY(), mz);
					b.setTargetBind(false);
					b.setToSteal(true);
					return true;
				}
			}
			if (BodyFoundAffinityRule.handleFoundAffinity(b, found, colX, mz)) {
				return true;
			}

			BodyLogic.checkNearParent(b);
			return false;
		}
		// 死体相手の行動
		return BodyDeadSearchRule.handleDeadFound(b, found, colX, mz);
	}
}
