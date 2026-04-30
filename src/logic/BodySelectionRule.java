package src.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import src.base.Body;
import src.enums.PublicRank;
import src.util.GameRandom;
import src.util.GameWorld;
import src.util.YukkuriUtil;

/**
 * ゆっくりの候補選択ロジック.
 */
public class BodySelectionRule {

	/**
	 * 婚姻候補のリストを作る。既婚の場合は、相手のみを含むリストを作る.
	 * 
	 * @param b   自分
	 * @param age ゆん生のステージ
	 * @return 婚姻候補のリスト
	 */
	public static final List<Body> createActiveFianceeList(Body b, int age) {
		// ほかにいないならスキップ
		if (GameWorld.get().getCurrentMap().getBody().size() <= 1) {
			return null;
		}

		List<Body> activeFianceeList = new LinkedList<Body>();

		// 番がすでにいれば要素はそれのみに
		Body pa = YukkuriUtil.getBodyInstance(b.getPartner());
		if (pa != null) {
			activeFianceeList.add(pa);
			return activeFianceeList;
		}

		for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			Body f = entry.getValue();
			// 自身はスキップ
			if (f == b) {
				continue;
			}
			// 死んでる
			if (f.isDead()) {
				continue;
			}
			// 除去された
			if (f.isRemoved()) {
				continue;
			}
			// 生まれてない
			if (f.isUnBirth()) {
				continue;
			}
			// 相手に子供がいる場合はスキップ
			if (f.getChildrenListSize() != 0) {
				continue;
			}
			// 自分とランクが違ったらスキップ
			if (b.getPublicRank() != f.getPublicRank()) {
				continue;
			}
			// 障害ゆんもスキップ
			if (f.hasDisorder()) {
				continue;
			}
			// かびてるのもスキップ
			if (b.findSick(f)) {
				continue;
			}
			// ロリコンはいない
			if (age > f.getBodyAgeState().ordinal()) {
				continue;
			}
			// お相手がすでにいるのは50%の確率でスキップ
			if (YukkuriUtil.getBodyInstance(f.getPartner()) != null) {
				if (GameRandom.nextBoolean()) {
					continue;
				}
			}
			activeFianceeList.add(f);
		}
		return activeFianceeList;
	}

	/**
	 * アクティブな赤ゆ/子ゆのリストを作成する.
	 * 
	 * @param b      ゆっくり
	 * @param bState 子ゆっくりを入れるかどうか（これがfalseなら赤ゆのみのリストになる）
	 * @return アクティブな赤ゆ/子ゆのリスト
	 */
	public static final List<Body> createActiveChildList(Body b, boolean bState) {
		// 子供がいないならスキップ
		int nChildlenListSize = b.getChildrenListSize();
		if (nChildlenListSize == 0) {
			return null;
		}
		List<Body> activeChildlenList = new LinkedList<Body>();
		for (int i = 0; i < nChildlenListSize; i++) {
			Body bodyChild = b.getChildren(i);
			if (bodyChild == null) {
				continue;
			}
			// 死んでる
			if (bodyChild.isDead()) {
				continue;
			}
			// 除去された
			if (bodyChild.isRemoved()) {
				continue;
			}
			// 生まれてない
			if (bodyChild.isUnBirth()) {
				continue;
			}
			// プレイヤーにアイテムとして持たれてる
			if (bodyChild.isTaken()) {
				continue;
			}
			// 子供に子供がいる場合はスキップ
			if (bodyChild.getChildrenListSize() != 0) {
				continue;
			}
			// うんうん奴隷はスキップ
			if (bodyChild.getPublicRank() == PublicRank.UnunSlave) {
				continue;
			}
			if (bodyChild.isNYD() || bodyChild.isNotAllright()) {
				continue;
			}
			if (!bState) {
				// 赤ゆっくり以外参加しないのでスキップ
				if (!bodyChild.isBaby()) {
					continue;
				}
			} else {
				// 赤ゆっくり、子ゆっくり以外参加しないのでスキップ
				if (bodyChild.isAdult()) {
					continue;
				}
			}
			activeChildlenList.add(bodyChild);
		}
		return activeChildlenList;
	}
}
