package src.logic;

import src.base.Body;
import src.enums.Parent;
import src.util.YukkuriUtil;

/**
 * Body同士の家族関係を判定するためのロジック集約クラス。
 * <p>
 * Phase 2の責務分離では、まず{@link Body}のpublic APIを残したまま実装だけを
 * このクラスへ委譲する。呼び出し側の置換やnull時の挙動整理は後続作業で扱うため、
 * ここでは抽出前の{@link Body}メソッドと同じ挙動を維持する。
 * </p>
 */
public final class BodyRelations {
	private BodyRelations() {
	}

	/**
	 * 2体の間に親子、番、姉妹のいずれかの関係があるかを判定する。
	 *
	 * @param self 判定元のゆっくり
	 * @param other 判定対象のゆっくり
	 * @return 家族関係がある場合はtrue
	 */
	public static boolean isFamily(Body self, Body other) {
		if (isParent(self, other)) {
			return true;
		}
		if (other.isParent(self)) {
			return true;
		}
		if (isPartner(self, other)) {
			return true;
		}
		if (isSister(self, other)) {
			return true;
		}
		return false;
	}

	/**
	 * {@code self}が{@code other}の父親または母親として登録されているかを判定する。
	 *
	 * @param self 親かどうかを調べるゆっくり
	 * @param other 子かどうかを調べるゆっくり
	 * @return {@code self}が{@code other}の親ならtrue
	 */
	public static boolean isParent(Body self, Body other) {
		if (other == null) {
			return false;
		}
		return YukkuriUtil.getBodyInstance(other.getParents()[Parent.PAPA.ordinal()]) == self
				|| YukkuriUtil.getBodyInstance(other.getParents()[Parent.MAMA.ordinal()]) == self;
	}

	/**
	 * {@code self}が{@code other}の父親として登録されているかを判定する。
	 *
	 * @param self 父親かどうかを調べるゆっくり
	 * @param other 子かどうかを調べるゆっくり
	 * @return {@code self}が{@code other}の父親ならtrue
	 */
	public static boolean isFather(Body self, Body other) {
		if (other == null) {
			return false;
		}
		return YukkuriUtil.getBodyInstance(other.getParents()[Parent.PAPA.ordinal()]) == self;
	}

	/**
	 * {@code self}が{@code other}の母親として登録されているかを判定する。
	 *
	 * @param self 母親かどうかを調べるゆっくり
	 * @param other 子かどうかを調べるゆっくり
	 * @return {@code self}が{@code other}の母親ならtrue
	 */
	public static boolean isMother(Body self, Body other) {
		if (other == null) {
			return false;
		}
		return YukkuriUtil.getBodyInstance(other.getParents()[Parent.MAMA.ordinal()]) == self;
	}

	/**
	 * {@code other}が{@code self}の親として登録されているかを判定する。
	 *
	 * @param self 子かどうかを調べるゆっくり
	 * @param other 親かどうかを調べるゆっくり
	 * @return {@code self}が{@code other}の子ならtrue
	 */
	public static boolean isChild(Body self, Body other) {
		if (other == null) {
			return false;
		}
		return other.isParent(self);
	}

	/**
	 * {@code other}が{@code self}の番として登録されているかを判定する。
	 *
	 * @param self 番情報を持つゆっくり
	 * @param other 番かどうかを調べるゆっくり
	 * @return {@code other}が{@code self}の番ならtrue
	 */
	public static boolean isPartner(Body self, Body other) {
		if (other == null) {
			return false;
		}
		Body partner = YukkuriUtil.getBodyInstance(self.getPartner());
		return partner != null && partner == other;
	}

	/**
	 * 既知の父親または母親が同じかどうかで姉妹関係を判定する。
	 * <p>
	 * 抽出前の{@link Body#isSister(Body)}は、{@code self}側に既知の親がいる状態で
	 * {@code other}がnullの場合に{@link NullPointerException}を送出し得る。
	 * Phase 2の初期抽出では挙動を変えないため、このメソッドも同じnull挙動を維持する。
	 * </p>
	 *
	 * @param self 判定元のゆっくり
	 * @param other 姉妹かどうかを調べるゆっくり
	 * @return 既知の親が同じならtrue
	 */
	public static boolean isSister(Body self, Body other) {
		if (YukkuriUtil.getBodyInstance(self.getParents()[Parent.MAMA.ordinal()]) != null) {
			return self.getParents()[Parent.MAMA.ordinal()] == other.getParents()[Parent.MAMA.ordinal()];
		}
		if (YukkuriUtil.getBodyInstance(self.getParents()[Parent.PAPA.ordinal()]) != null) {
			return self.getParents()[Parent.PAPA.ordinal()] == other.getParents()[Parent.PAPA.ordinal()];
		}
		return false;
	}

	/**
	 * {@code other}が{@code self}より若い姉妹かどうかを判定する。
	 *
	 * @param self 年上側かどうかを調べるゆっくり
	 * @param other 年下側かどうかを調べるゆっくり
	 * @return 姉妹で、かつ{@code self}の年齢が{@code other}以上ならtrue
	 */
	public static boolean isElderSister(Body self, Body other) {
		return isSister(self, other) && self.getAge() >= other.getAge();
	}
}
