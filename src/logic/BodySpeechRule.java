package src.logic;

import src.base.BodyAttributes;

/**
 * Bodyの発話状態に関する単純判定を集約する.
 */
public final class BodySpeechRule {
	private BodySpeechRule() {
	}

	/**
	 * 喋っているかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 喋っているならtrue
	 */
	public static boolean isTalking(BodyAttributes body) {
		return body.getMessageTicks() > 0;
	}

	/**
	 * 喋れる状態かどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 喋れるならtrue
	 */
	public static boolean isCanTalk(BodyAttributes body) {
		return body.isCanTalkRaw();
	}
}
