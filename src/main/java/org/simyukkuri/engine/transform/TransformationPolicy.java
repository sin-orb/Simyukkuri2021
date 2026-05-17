package org.simyukkuri.engine.transform;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.YukkuriType;

/**
 * 変身時のルールをまとめる.
 */
public final class TransformationPolicy {
	private TransformationPolicy() {
	}

	/**
	 * ドスまりさへの変身前に予約が必要かを返す。
	 *
	 * @param targetType 変身後のゆっくりタイプ
	 * @return ドスまりさへの変身時に true
	 */
	public static boolean needsDosReservation(YukkuriType targetType) {
		return targetType == YukkuriType.DOSMARISA;
	}

	/**
	 * 変身後タイプに対応する画像ファイル名ベースを返す。
	 *
	 * @param targetType 変身後のゆっくりタイプ（null 可）
	 * @return 小文字のクラス名。null の場合は null
	 */
	public static String resolveBaseYukkuriFileName(YukkuriType targetType) {
		if (targetType == null) {
			return null;
		}
		return targetType.getClassName().toLowerCase();
	}

	/**
	 * ゆっくりが現在選択中かを返す。
	 *
	 * @param body 対象ゆっくり
	 * @return 選択中の場合 true
	 */
	public static boolean isSelectedYukkuri(Yukkuri body) {
		return body != null && org.simyukkuri.draw.MyPane.getSelectedYukkuri() == body;
	}

	/**
	 * 変身元の年齢状態に合わせて変身先の年齢を設定する。
	 *
	 * @param to   変身後のゆっくり
	 * @param from 変身前のゆっくり
	 */
	public static void normalizeTransformedAge(Yukkuri to, Yukkuri from) {
		if (to == null || from == null) {
			return;
		}
		AgeState ageState = from.getAgeState();
		switch (ageState) {
			case BABY:
				to.setAge(0);
				break;
			case CHILD:
				to.setAge(to.getBabyLimitBase() + 1);
				break;
			case ADULT:
			default:
				to.setAge(to.getChildLimitBase() + 1);
				break;
		}
	}
}
