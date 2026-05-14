package org.simyukkuri.logic;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Damage;

/**
 * ゆっくり本体の生理状態を判定するロジック集約クラス。
 * <p>
 * Phase 2では、{@link BodyAttributes}が保持しているフィールドやJSON互換を動かさず、
 * 空腹、ダメージ、病気などの判定式だけをこのクラスへ委譲する。状態の保存形式や
 * public APIは維持し、後続の責務分離で安全に呼び出し側を整理できるようにする。
 * </p>
 */
public final class BodyVitals {
	private BodyVitals() {
	}

	/**
	 * ダメージを受けていないかどうかを判定する。
	 *
	 * @param body 判定対象のゆっくり
	 * @return ダメージ状態がNONEならtrue
	 */
	public static boolean isNoDamaged(Yukkuri body) {
		return body.getDamageState() == Damage.NONE;
	}

	/**
	 * 軽いダメージ以上を受けているかどうかを判定する。
	 *
	 * @param body 判定対象のゆっくり
	 * @return ダメージ状態がSOME、VERY、TOOMUCHのいずれかならtrue
	 */
	public static boolean isDamagedLightly(Yukkuri body) {
		Damage damage = body.getDamageState();
		return damage == Damage.SOME || damage == Damage.VERY || damage == Damage.TOOMUCH;
	}

	/**
	 * 行動に影響する程度のダメージを受けているかどうかを判定する。
	 *
	 * @param body 判定対象のゆっくり
	 * @return ダメージ状態がVERYまたはTOOMUCHならtrue
	 */
	public static boolean isDamaged(Yukkuri body) {
		Damage damage = body.getDamageState();
		return damage == Damage.VERY || damage == Damage.TOOMUCH;
	}

	/**
	 * 重いダメージを受けているかどうかを判定する。
	 *
	 * @param body 判定対象のゆっくり
	 * @return ダメージ状態がTOOMUCHならtrue
	 */
	public static boolean isDamagedHeavily(Yukkuri body) {
		return body.getDamageState() == Damage.TOOMUCH;
	}

	/**
	 * お腹いっぱい気味かどうかを判定する。
	 *
	 * @param body 判定対象のゆっくり
	 * @return 生存中で、満腹度が空腹限界の80%以上ならtrue
	 */
	public static boolean isFull(Yukkuri body) {
		return !body.isDead() && body.getHungry() >= body.getHungryLimit() * 0.8f;
	}

	/**
	 * お腹が減ってきているかどうかを判定する。
	 *
	 * @param body 判定対象のゆっくり
	 * @return 生存中で、満腹度が空腹限界の半分以下ならtrue
	 */
	public static boolean isHungry(Yukkuri body) {
		return !body.isDead() && body.getHungry() <= body.getHungryLimit() / 2;
	}

	/**
	 * お腹減り気味かどうかを判定する。
	 *
	 * @param body 判定対象のゆっくり
	 * @return 生存中で、満腹度が空腹限界の20%以下ならtrue
	 */
	public static boolean isSoHungry(Yukkuri body) {
		return !body.isDead() && body.getHungry() <= body.getHungryLimit() * 0.2f;
	}

	/**
	 * お腹が完全に減っているかどうかを判定する。
	 *
	 * @param body 判定対象のゆっくり
	 * @return 生存中で、満腹度が0以下ならtrue
	 */
	public static boolean isVeryHungry(Yukkuri body) {
		return !body.isDead() && body.getHungry() <= 0;
	}

	/**
	 * 空腹によるダメージが発生しているかどうかを判定する。
	 *
	 * @param body 判定対象のゆっくり
	 * @return 生存中で、満腹度が0以下かつダメージ状態がNONE以外ならtrue
	 */
	public static boolean isTooHungry(Yukkuri body) {
		return !body.isDead() && body.getHungry() <= 0 && body.getDamageState() != Damage.NONE;
	}

	/**
	 * 餓死寸前かどうかを判定する。
	 *
	 * @param body 判定対象のゆっくり
	 * @return 生存中で、満腹度が0以下かつダメージ状態がTOOMUCHならtrue
	 */
	public static boolean isStarving(Yukkuri body) {
		return !body.isDead() && body.getHungry() <= 0 && body.getDamageState() == Damage.TOOMUCH;
	}

	/**
	 * ゆかび第一段階以上かどうかを判定する。
	 *
	 * @param body 判定対象のゆっくり
	 * @return ゆかび期間が潜伏期間を超えていればtrue
	 */
	public static boolean isSick(Yukkuri body) {
		return body.getSickPeriod() > body.getIncubationPeriodBase();
	}

	/**
	 * ゆかび第二段階以上かどうかを判定する。
	 *
	 * @param body 判定対象のゆっくり
	 * @return ゆかび期間が潜伏期間の8倍を超えていればtrue
	 */
	public static boolean isSickHeavily(Yukkuri body) {
		return body.getSickPeriod() > body.getIncubationPeriodBase() * 8;
	}

	/**
	 * ゆかび第三段階以上で、かつダメージを受けているかどうかを判定する。
	 *
	 * @param body 判定対象のゆっくり
	 * @return ゆかび期間が潜伏期間の32倍を超え、ダメージ判定もtrueならtrue
	 */
	public static boolean isSickTooHeavily(Yukkuri body) {
		return body.getSickPeriod() > body.getIncubationPeriodBase() * 32 && isDamaged(body);
	}
}
