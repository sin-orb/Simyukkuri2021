package org.simyukkuri.entity.core.living;

import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.FootBake;

/**
 * ゆっくりの行動制限ペナルティをまとめた委譲クラス.
 * 盲目・口封じ・動けない・足焼き・おかざりなし状態の前提条件チェックを担う。
 */
public final class LivingEntityActionDelegate {
	private final LivingEntity body;

	/** 指定の LivingEntity をラップしてデリゲートを初期化する。 */
	public LivingEntityActionDelegate(LivingEntity body) {
		this.body = body;
	}

	/** 盲目時の生理ペナルティを適用する. @return 盲目なら true */
	public boolean applyBlindnessPenalty() {
		if (!body.isBlind()) {
			return false;
		}
		body.setEyesightBase(5 * 5);
		body.addStress(5);
		return true;
	}

	/** 口封じ時の生理ペナルティを適用する. @return 口封じ中なら true */
	public boolean applyCantSpeakPenalty() {
		if (!body.isShutmouth()) {
			return false;
		}
		body.addStress(2);
		body.setPeropero(false);
		return true;
	}

	/** 動けない時の前提条件を更新する. @return 反応を継続できるなら true */
	public boolean beginLockmoveEmotion() {
		if (!body.isLockmove() || body.isSukkiri()
				|| (body.getFootBakeLevel() != FootBake.NONE
						&& (body.getBurialState() == BurialState.NONE
								|| body.getBurialState() == BurialState.HALF))) {
			return false;
		}
		if (body.isSleeping() || body.isGrabbed()) {
			body.setLockmovePeriod(0);
			return false;
		}
		if (body.getCurrentEvent() != null) {
			body.setLockmovePeriod(0);
			return false;
		}
		body.setLockmovePeriod(body.getLockmovePeriod() + 1);
		return !body.isTalking();
	}

	/** 足焼き時の前提条件を更新する. @return 反応を継続できるなら true */
	public boolean beginFootBakeEmotion() {
		if (body.getFootBakeLevel() == FootBake.NONE || body.isSukkiri()) {
			return false;
		}
		if (body.isSleeping() || body.isGrabbed()) {
			body.setLockmovePeriod(0);
			return false;
		}
		body.setLockmovePeriod(body.getLockmovePeriod() + 1);
		return !body.isTalking();
	}

	/** おかざり・ぴこぴこなし時の前提条件を確認する. @return 反応を継続できるなら true */
	public boolean beginNoOkazariEmotion() {
		if ((body.hasOkazari() && body.isHasBraid()) || body.isSukkiri()) {
			return false;
		}
		if (body.isSleeping() || body.isGrabbed()) {
			body.lockmovePeriod = 0;
			return false;
		}
		return !body.isTalking();
	}
}
