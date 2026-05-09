package src.engine.transform;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import src.base.Body;

/**
 * 変身時に body の状態をレイヤ単位で複製する.
 */
public final class TransformationBodyCopier {
	private static final String[] NOCOPY_FIELD = {
			"bodySpr",
			"expandSpr",
			"braidSpr",
			"EATAMOUNT",
			"WEIGHT",
			"HUNGRYLIMIT",
			"SHITLIMIT",
			"DAMAGELIMIT",
			"STRESSLIMIT",
			"TANGLEVEL",
			"BABYLIMIT",
			"CHILDLIMIT",
			"LIFELIMIT",
			"LOVEPLAYERLIMIT",
			"ROTTINGTIME",
			"STEP",
			"RELAXPERIOD",
			"EXCITEPERIOD",
			"PREGPERIOD",
			"SLEEPPERIOD",
			"ACTIVEPERIOD",
			"ANGRYPERIOD",
			"SCAREPERIOD",
			"DECLINEPERIOD",
			"DISCIPLINELIMIT",
			"BLOCKEDLIMIT",
			"DIRTYPERIOD",
			"STRENGTH",
			"EYESIGHT",
			"INCUBATIONPERIOD",
			"speed",
			"cost",
			"YValue",
			"AValue",
			"babyNames",
			"childNames",
			"adultNames",
			"myNames",
			"babyNamesDamaged",
			"childNamesDamaged",
			"adultNamesDamaged",
			"myNamesDamaged",
			"baseBodyFileName",
			"parents",
			"childrenList",
			"elderSisterList",
			"sisterList"
	};

	private TransformationBodyCopier() {
	}

	/**
	 * ゆっくりのステータスをfrom->toへ複製する.
	 * シャローコピーなので複製元はbodyListから外しておかないと予期しない動作になるので注意.
	 *
	 * @param to 変異後のゆっくり
	 * @param from 変異前のゆっくり
	 * @throws Exception リフレクションでコピー中に発生する例外
	 */
	public static void copy(Body to, Body from) throws Exception {
		copyEntityLayerFields(to, from);
		copyBodyAttributeLayerFields(to, from);
		copyYukkuriLayerFields(to, from);
		copyTransformIdentityFields(to, from);
	}

	private static void copyEntityLayerFields(Body to, Body from) throws Exception {
		Field[] fromField = from.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredFields();
		Class<?> toClass = to.getClass().getSuperclass().getSuperclass().getSuperclass().getSuperclass();
		copyDeclaredFields(to, from, fromField, toClass);
	}

	private static void copyBodyAttributeLayerFields(Body to, Body from) throws Exception {
		Field[] fromField = from.getClass().getSuperclass().getSuperclass().getDeclaredFields();
		Class<?> toClass = to.getClass().getSuperclass().getSuperclass().getSuperclass();
		copyDeclaredFields(to, from, fromField, toClass);
		to.copyBodyStatSetFrom(from);
		to.copyBodyTimingSetFrom(from);
		to.copyBodyBehaviorSetFrom(from);
	}

	private static void copyYukkuriLayerFields(Body to, Body from) throws Exception {
		Field[] fromField = from.getClass().getSuperclass().getDeclaredFields();
		Class<?> toClass = to.getClass().getSuperclass().getSuperclass();
		copyDeclaredFields(to, from, fromField, toClass);
		to.copyBodyNameSetFrom(from);
		to.copyBodySpriteSetFrom(from);
	}

	private static void copyDeclaredFields(Body to, Body from, Field[] fromField, Class<?> toClass) throws Exception {
		for (int i = 0; i < fromField.length; i++) {
			int mod = fromField[i].getModifiers();
			if (Modifier.isFinal(mod)) {
				continue;
			}
			if (Modifier.isStatic(mod)) {
				continue;
			}
			if (isNoCopyField(fromField[i].getName())) {
				continue;
			}
			Field toField = findField(toClass, fromField[i].getName());
			if (toField == null) {
				continue;
			}
			toField.setAccessible(true);
			fromField[i].setAccessible(true);
			toField.set(to, fromField[i].get(from));
		}
	}

	private static void copyTransformIdentityFields(Body to, Body from) {
		// 可変な関係情報は共有しない
		to.setPartner(from.getPartner());
		to.setParents(copyIntegerArray(from.getParents()));
		to.setChildrenList(copyIntegerList(from.getChildrenList()));
		to.setElderSisterList(copyIntegerList(from.getElderSisterList()));
		to.setSisterList(copyIntegerList(from.getSisterList()));

		to.setBodyRank(from.getBodyRank());
		to.setPublicRank(from.getPublicRank());
	}

	private static int[] copyIntegerArray(int[] values) {
		return values == null ? null : values.clone();
	}

	private static List<Integer> copyIntegerList(List<Integer> values) {
		return values == null ? null : new LinkedList<Integer>(values);
	}

	public static boolean isNoCopyField(String name) {
		for (String f : NOCOPY_FIELD) {
			if (f.equals(name)) {
				return true;
			}
		}
		return false;
	}

	private static Field findField(Class<?> clazz, String name) {
		Class<?> current = clazz;
		while (current != null) {
			try {
				return current.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
				current = current.getSuperclass();
			}
		}
		return null;
	}
}
