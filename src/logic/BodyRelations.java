package src.logic;

import src.base.Yukkuri;
import src.entity.living.SocialEntity;
import src.enums.Parent;
import src.enums.EnumRelationMine;
import java.util.Iterator;

/**
 * Body同士の家族関係を判定するためのロジック集約クラス。
 * <p>
 * Phase 2の責務分離では、まず{@link Yukkuri}のpublic APIを残したまま実装だけを
 * このクラスへ委譲する。呼び出し側の置換やnull時の挙動整理は後続作業で扱うため、
 * ここでは抽出前の{@link Yukkuri}メソッドと同じ挙動を維持する。
 * </p>
 */
public final class BodyRelations {
	private BodyRelations() {
	}

	/**
	 * ゆっくりIDからインスタンスを取得する。
	 *
	 * @param bodyId ゆっくりID
	 * @return インスタンス。存在しない場合はnull
	 */
	public static Yukkuri getBody(int bodyId) {
		return src.util.BodyRegistry.getBodyInstance(bodyId);
	}

	/**
	 * オブジェクトIDからインスタンスを取得する。
	 *
	 * @param objId オブジェクトID
	 * @return インスタンス。存在しない場合はnull
	 */
	public static Yukkuri getBodyFromObjId(int objId) {
		return src.util.BodyRegistry.getBodyInstanceFromObjId(objId);
	}

	/**
	 * {@code self}が{@code other}にとって何にあたるかを判定する。
	 *
	 * @param self 自分側のゆっくり
	 * @param other 相手側のゆっくり
	 * @return 関係性
	 */
	public static EnumRelationMine checkMyRelation(SocialEntity self, SocialEntity other) {
		if (self.isFather(other)) {
			return EnumRelationMine.FATHER;
		}
		if (self.isMother(other)) {
			return EnumRelationMine.MOTHER;
		}
		if (self.isPartner(other)) {
			return EnumRelationMine.PARTNAR;
		}
		if (other.isFather(self)) {
			return EnumRelationMine.CHILD_FATHER;
		}
		if (other.isMother(self)) {
			return EnumRelationMine.CHILD_MOTHER;
		}
		if (self.isElderSister(other)) {
			return EnumRelationMine.ELDERSISTER;
		}
		if (!self.isElderSister(other) && self.isSister(other)) {
			return EnumRelationMine.YOUNGSISTER;
		}
		return EnumRelationMine.OTHER;
	}

	/**
	 * 2体の間に親子、番、姉妹のいずれかの関係があるかを判定する。
	 *
	 * @param self 判定元のゆっくり
	 * @param other 判定対象のゆっくり
	 * @return 家族関係がある場合はtrue
	 */
	public static boolean isFamily(SocialEntity self, SocialEntity other) {
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
	public static boolean isParent(SocialEntity self, SocialEntity other) {
		if (other == null) {
			return false;
		}
		return getBody(other.getParents()[Parent.PAPA.ordinal()]) == self
				|| getBody(other.getParents()[Parent.MAMA.ordinal()]) == self;
	}

	/**
	 * {@code self}が{@code other}の父親として登録されているかを判定する。
	 *
	 * @param self 父親かどうかを調べるゆっくり
	 * @param other 子かどうかを調べるゆっくり
	 * @return {@code self}が{@code other}の父親ならtrue
	 */
	public static boolean isFather(SocialEntity self, SocialEntity other) {
		if (other == null) {
			return false;
		}
		return getBody(other.getParents()[Parent.PAPA.ordinal()]) == self;
	}

	/**
	 * {@code self}が{@code other}の母親として登録されているかを判定する。
	 *
	 * @param self 母親かどうかを調べるゆっくり
	 * @param other 子かどうかを調べるゆっくり
	 * @return {@code self}が{@code other}の母親ならtrue
	 */
	public static boolean isMother(SocialEntity self, SocialEntity other) {
		if (other == null) {
			return false;
		}
		return getBody(other.getParents()[Parent.MAMA.ordinal()]) == self;
	}

	/**
	 * {@code other}が{@code self}の親として登録されているかを判定する。
	 *
	 * @param self 子かどうかを調べるゆっくり
	 * @param other 親かどうかを調べるゆっくり
	 * @return {@code self}が{@code other}の子ならtrue
	 */
	public static boolean isChild(SocialEntity self, SocialEntity other) {
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
	public static boolean isPartner(SocialEntity self, SocialEntity other) {
		if (other == null) {
			return false;
		}
		Yukkuri partner = getBody(self.getPartner());
		return partner != null && partner == other;
	}

	/**
	 * 既知の父親または母親が同じかどうかで姉妹関係を判定する。
	 * <p>
	 * 抽出前の{@link Yukkuri#isSister(SocialEntity)}は、{@code self}側に既知の親がいる状態で
	 * {@code other}がnullの場合に{@link NullPointerException}を送出し得る。
	 * Phase 2の初期抽出では挙動を変えないため、このメソッドも同じnull挙動を維持する。
	 * </p>
	 *
	 * @param self 判定元のゆっくり
	 * @param other 姉妹かどうかを調べるゆっくり
	 * @return 既知の親が同じならtrue
	 */
	public static boolean isSister(SocialEntity self, SocialEntity other) {
		if (getBody(self.getParents()[Parent.MAMA.ordinal()]) != null) {
			return self.getParents()[Parent.MAMA.ordinal()] == other.getParents()[Parent.MAMA.ordinal()];
		}
		if (getBody(self.getParents()[Parent.PAPA.ordinal()]) != null) {
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
	public static boolean isElderSister(SocialEntity self, SocialEntity other) {
		return isSister(self, other) && self.getAge() >= other.getAge();
	}

	/**
	 * {@code self}の妹をインデックス指定で取得する。
	 *
	 * @param self 参照元のゆっくり
	 * @param index 何番目の妹か
	 * @return 妹のインスタンス
	 */
	public static Yukkuri getSister(SocialEntity self, int index) {
		return getBody(self.getSisterList().get(index));
	}

	/**
	 * {@code self}の姉をインデックス指定で取得する。
	 *
	 * @param self 参照元のゆっくり
	 * @param index 何番目の姉か
	 * @return 姉のインスタンス
	 */
	public static Yukkuri getElderSister(SocialEntity self, int index) {
		return getBody(self.getElderSisterList().get(index));
	}

	/**
	 * {@code self}の子をインデックス指定で取得する。
	 *
	 * @param self 参照元のゆっくり
	 * @param index 何番目の子か
	 * @return 子のインスタンス
	 */
	public static Yukkuri getChildren(SocialEntity self, int index) {
		if (self.getChildrenList() == null) {
			return null;
		}
		return getBody(self.getChildrenList().get(index));
	}

	/**
	 * {@code self}の番インスタンスを取得する。
	 *
	 * @param self 参照元のゆっくり
	 * @return 番のインスタンス
	 */
	public static Yukkuri getPartnerBody(SocialEntity self) {
		return getBody(self.getPartner());
	}

	/**
	 * {@code self}の母親インスタンスを取得する。
	 *
	 * @param self 参照元のゆっくり
	 * @return 母親のインスタンス
	 */
	public static Yukkuri getMotherBody(SocialEntity self) {
		return getBody(self.getMother());
	}

	/**
	 * {@code self}の父親インスタンスを取得する。
	 *
	 * @param self 参照元のゆっくり
	 * @return 父親のインスタンス
	 */
	public static Yukkuri getFatherBody(SocialEntity self) {
		return getBody(self.getFather());
	}

	/**
	 * 親IDを指定してインスタンスを取得する。
	 *
	 * @param parentId 親ID
	 * @return 親インスタンス
	 */
	public static Yukkuri getParentBody(int parentId) {
		return getBody(parentId);
	}

	/**
	 * {@code self}の子リストから指定個体を除去する。
	 *
	 * @param self 参照元のゆっくり
	 * @param targetBody 除去対象
	 */
	public static void removeChildrenList(SocialEntity self, SocialEntity targetBody) {
		if (self.getChildrenList() == null || targetBody == null) {
			return;
		}
		Iterator<Integer> itr = self.getChildrenList().iterator();
		while (itr.hasNext()) {
			Yukkuri at = getBody(itr.next());
			if (at == targetBody) {
				itr.remove();
			}
		}
	}

	/**
	 * {@code self}の姉リストから指定個体を除去する。
	 *
	 * @param self 参照元のゆっくり
	 * @param targetBody 除去対象
	 */
	public static void removeElderSisterList(SocialEntity self, SocialEntity targetBody) {
		if (self.getElderSisterList() == null || targetBody == null) {
			return;
		}
		Iterator<Integer> itr = self.getElderSisterList().iterator();
		while (itr.hasNext()) {
			Yukkuri at = getBody(itr.next());
			if (at == targetBody) {
				itr.remove();
			}
		}
	}

	/**
	 * {@code self}の妹リストから指定個体を除去する。
	 *
	 * @param self 参照元のゆっくり
	 * @param targetBody 除去対象
	 */
	public static void removeSisterList(SocialEntity self, SocialEntity targetBody) {
		if (self.getSisterList() == null || targetBody == null) {
			return;
		}
		Iterator<Integer> itr = self.getSisterList().iterator();
		while (itr.hasNext()) {
			Yukkuri at = getBody(itr.next());
			if (at == targetBody) {
				itr.remove();
			}
		}
	}
}
