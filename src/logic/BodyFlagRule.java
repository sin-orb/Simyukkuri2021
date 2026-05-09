package src.logic;

import src.base.BodyAttributes;
import src.enums.CoreAnkoState;

/**
 * Bodyの単純な状態フラグに関する判定を集約する.
 */
public final class BodyFlagRule {
	private BodyFlagRule() {
	}

	/**
	 * トラウマ持ちかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return トラウマがあるならtrue
	 */
	public static boolean hasTrauma(BodyAttributes body) {
		return body.getTraumaRaw() != null;
	}

	/**
	 * 非ゆっくり症かどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 非ゆっくり症ならtrue
	 */
	public static boolean isNYD(BodyAttributes body) {
		return body.getCoreAnkoState() != CoreAnkoState.DEFAULT;
	}

	/**
	 * 非ゆっくり症ではないかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 非ゆっくり症ではなければtrue
	 */
	public static boolean isNotNYD(BodyAttributes body) {
		return body.getCoreAnkoState() == CoreAnkoState.DEFAULT;
	}

	/**
	 * 取られているかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 取られていればtrue
	 */
	public static boolean isTaken(BodyAttributes body) {
		return body.isTakenRaw();
	}

	/**
	 * ペロペロ中かどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return ペロペロ中ならtrue
	 */
	public static boolean isPeropero(BodyAttributes body) {
		return !body.isDead() && body.isPeroperoRaw();
	}

	/**
	 * おねだり中かどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return おねだり中ならtrue
	 */
	public static boolean isBegging(BodyAttributes body) {
		return body.isBeggingRaw();
	}

	/**
	 * 頑固な汚れかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 頑固な汚れならtrue
	 */
	public static boolean isStubbornlyDirty(BodyAttributes body) {
		return !body.isDead() && body.isStubbornlyDirtyRaw();
	}

	/**
	 * ぺにぺにの去勢有無を判定する.
	 *
	 * @param body 判定対象
	 * @return ぺにぺにの去勢有無ならtrue
	 */
	public static boolean isPenipeniCutted(BodyAttributes body) {
		return body.isPenipeniCuttedRaw();
	}

	/**
	 * フェロモン有無を判定する.
	 *
	 * @param body 判定対象
	 * @return フェロモンがあればtrue
	 */
	public static boolean isPheromone(BodyAttributes body) {
		return body.isPheromoneRaw();
	}

	/**
	 * おかざり消失に気づいているかを判定する.
	 *
	 * @param body 判定対象
	 * @return 気づいていればtrue
	 */
	public static boolean isNoticeNoOkazari(BodyAttributes body) {
		return body.isNoticeNoOkazari();
	}

	/**
	 * 移動不可ベルトコンベア上かを判定する.
	 *
	 * @param body 判定対象
	 * @return ベルトコンベア上ならtrue
	 */
	public static boolean isOnNonMovingConveyor(BodyAttributes body) {
		return body.isOnDontMoveBeltconveyorRaw();
	}
}
