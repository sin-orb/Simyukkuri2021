package src.engine.transform;

import src.base.Yukkuri;
import src.enums.AgeState;
import src.enums.YukkuriType;

/**
 * 変身時のルールをまとめる.
 */
public final class TransformationPolicy {
	private TransformationPolicy() {
	}

	public static boolean needsDosReservation(YukkuriType targetType) {
		return targetType == YukkuriType.DOSMARISA;
	}

	public static String resolveBaseBodyFileName(YukkuriType targetType) {
		if (targetType == null) {
			return null;
		}
		return targetType.getClassName().toLowerCase();
	}

	public static boolean isSelectedBody(Yukkuri body) {
		return body != null && src.draw.MyPane.getSelectBody() == body;
	}

	public static void normalizeTransformedAge(Yukkuri to, Yukkuri from) {
		if (to == null || from == null) {
			return;
		}
		AgeState ageState = from.getBodyAgeState();
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
