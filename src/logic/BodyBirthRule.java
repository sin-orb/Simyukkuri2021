package src.logic;

import src.base.BodyAttributes;

/**
 * Bodyの出生状態に関する単純判定を集約する.
 */
public final class BodyBirthRule {
	private BodyBirthRule() {
	}

	/**
	 * 実ゆかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 実ゆならtrue
	 */
	public static boolean isUnBirth(BodyAttributes body) {
		return body.isUnBirthRaw();
	}

	/**
	 * 誕生時メッセージの強制フラグを判定する.
	 *
	 * @param body 判定対象
	 * @return 強制フラグが立っていればtrue
	 */
	public static boolean isBirthMessageForced(BodyAttributes body) {
		return body.isForceBirthMessageRaw();
	}

	/**
	 * うまれて初めての食事かを判定する.
	 *
	 * @param body 判定対象
	 * @return 初回食事ならtrue
	 */
	public static boolean isFirstEatStalk(BodyAttributes body) {
		return body.isFirstEatStalkRaw();
	}

	/**
	 * 初回着地済みかを判定する.
	 *
	 * @param body 判定対象
	 * @return 初回着地済みならtrue
	 */
	public static boolean isFirstGround(BodyAttributes body) {
		return body.isFirstGroundRaw();
	}
}
