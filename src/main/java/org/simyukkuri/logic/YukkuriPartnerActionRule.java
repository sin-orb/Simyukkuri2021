package org.simyukkuri.logic;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Okazari.OkazariType;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.event.impl.HateNoOkazariEvent;
import org.simyukkuri.event.impl.ProposeEvent;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;

/**
 * 目標が見つかった後の partner 行動ロジック.
 */
public final class YukkuriPartnerActionRule {

	private YukkuriPartnerActionRule() {
	}

	/**
	 * 目標が見つかった場合の行動を処理する.
	 *
	 * @param body             自分
	 * @param targetBody       対象
	 * @param bodyHasOkazari   おかざり対象
	 * @param collisionOffsetX 衝突補正X
	 * @param zofs          Z移動量
	 * @return 行動したかどうか
	 */
	public static boolean handleFoundTarget(Yukkuri body, Yukkuri targetBody, Yukkuri bodyHasOkazari,
			int collisionOffsetX, int zofs) {
		// 相手が死体でなく、かつ除去されてなければ
		if (!targetBody.isDead() && !targetBody.isRemoved()) {
			// 生まれていない
			if (body.isUnBirth()) {
				return false;
			}
			// 生まれていない
			if (targetBody.isUnBirth()) {
				return false;
			}

			// 自分が発情していればすっきりに向かう
			if (body.isExciting()) {
				// 自分がれいぱー/既婚/（ドゲスで1/10の確率）の場合はすっきりしに行く
				if (body.isRaper() || (body.isVeryRude() && GameRandom.nextInt(10) == 0) || body.isPartner(targetBody)
						|| targetBody.isPartner(body)) {
					body.moveToSukkiri(targetBody, targetBody.getX() + collisionOffsetX, targetBody.getY(), zofs);
					body.setTargetBind(true);
				} else {
					// れいぱーでない独身勢はプロポーズへ
					// ただし、相手が足りないゆの時はキャンセル
					if (targetBody.isIdiot() && !body.isIdiot()) {
						body.setCalm();
						return true;
					}
					// ドゲスの場合は50%の確率でプロポーズをする
					if (body.getAttitude() != Attitude.SUPER_SHITHEAD
							|| (body.getAttitude() == Attitude.SUPER_SHITHEAD && GameRandom.nextBoolean())) {
						EventLogic.addYukkuriEvent(body, new ProposeEvent(body, targetBody, null, 1), null, null);
						return true;
					} else if (body.getAttitude() == Attitude.SUPER_SHITHEAD) {
						// ドゲスの場合は50%の確率でレイプだけする
						body.moveToSukkiri(targetBody, targetBody.getX() + collisionOffsetX, targetBody.getY(),
								zofs);
						body.setTargetBind(true);
					}
				}
				return true;
			}

			if (!targetBody.hasOkazari() && body.getOkazaris() != null
					&& body.getOkazaris().getOkazariType() == OkazariType.DEFAULT && body.isRude() && !body.isIdiot()
					&& !body.isDamaged() && !targetBody.isUnBirth() && body.getCurrentEvent() == null) {
				// 自分が通常種で相手が捕食種の場合は参加しない
				if (body.isPredatorType() || !targetBody.isPredatorType()) {
					// 相手がおかざりのないゆっくりなら制裁を呼びかける
					if (body.isVeryRude() || !targetBody.isDamaged()) {
						if (GameRandom.nextInt(20) == 0) {
							if (!body.isTalking()) {
								// 自分がうんうん奴隷ではない場合
								if (body.getPublicRank() != PublicRank.UNUN_SLAVE) {
									// 非ゆっくり症は参加しない
									if (body.isNotNyd() && targetBody.isNotNyd()) {
										HateNoOkazariEvent ev = new HateNoOkazariEvent(
												body, targetBody, null, 10);
										String msg = GameMessages.getMessage(body,
												MessagePool.Action.HateYukkuri);
										EventLogic.addWorldEvent(ev, body, msg);
									}
								}
							}
							return true;
						}
					}
				}
			}

			// プレイヤーにすりすりされていた場合の処理
			YukkuriLogic.ActionGo actionGo = YukkuriLogic.checkActionSurisuriFromPlayer(body, targetBody);
			if (actionGo != YukkuriLogic.ActionGo.NONE) {
				if (actionGo == YukkuriLogic.ActionGo.GO) {
					// 近づきすぎないように近づく
					body.moveToYukkuri(targetBody, targetBody.getX() + collisionOffsetX * 2, targetBody.getY(), zofs);
					body.setTargetBind(false);
				}
				return true;
			}

			if (YukkuriLogic.checkEmotionFromUnunSlave(body, targetBody)) {
				return true;
			}

			// 自分のおかざりがなくて、相手にお飾りがある。うんうん奴隷のものは奪わない
			if (bodyHasOkazari != null) {
				body.setToSteal(false);
				// 視界内に起きているゆっくりがいない
				if (!YukkuriLogic.checkWakeupOtherYukkuri(body) || GameRandom.nextInt(20) == 0) {
					body.moveToYukkuri(bodyHasOkazari, bodyHasOkazari.getX() + collisionOffsetX, bodyHasOkazari.getY(),
							zofs);
					body.setTargetBind(false);
					body.setToSteal(true);
					return true;
				}
			}
			if (YukkuriFoundAffinityRule.handleFoundAffinity(body, targetBody, collisionOffsetX, zofs)) {
				return true;
			}

			YukkuriLogic.checkNearParent(body);
			return false;
		}
		// 死体相手の行動
		return YukkuriDeadSearchRule.handleDeadFound(body, targetBody, collisionOffsetX, zofs);
	}
}
