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

	public static boolean needsDosReservation(YukkuriType targetType) {
		return targetType == YukkuriType.DOSMARISA;
	}

	public static String resolveBaseYukkuriFileName(YukkuriType targetType) {
		if (targetType == null) {
			return null;
		}
		return targetType.getClassName().toLowerCase();
	}

	public static boolean isSelectedYukkuri(Yukkuri body) {
		return body != null && org.simyukkuri.draw.MyPane.getSelectedYukkuri() == body;
	}

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
