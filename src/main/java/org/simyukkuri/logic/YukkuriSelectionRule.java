package org.simyukkuri.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;

/**
 * ゆっくりの候補選択ロジック.
 */
public class YukkuriSelectionRule {

	/**
	 * 婚姻候補のリストを作る。既婚の場合は、相手のみを含むリストを作る.
	 * 
	 * @param body 自分
	 * @param age  ゆん生のステージ
	 * @return 婚姻候補のリスト
	 */
	public static final List<Yukkuri> createActiveFianceeList(Yukkuri body, int age) {
		// ほかにいないならスキップ
		if (GameWorld.get().getCurrentMap().getYukkuriMap().size() <= 1) {
			return null;
		}

		List<Yukkuri> activeFianceeList = new LinkedList<Yukkuri>();

		// 番がすでにいれば要素はそれのみに
		Yukkuri partnerBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(body.getPartner());
		if (partnerBody != null) {
			activeFianceeList.add(partnerBody);
			return activeFianceeList;
		}

		for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentMap().getYukkuriMap().entrySet()) {
			Yukkuri candidate = entry.getValue();
			// 自身はスキップ
			if (candidate == body) {
				continue;
			}
			// 死んでる
			if (candidate.isDead()) {
				continue;
			}
			// 除去された
			if (candidate.isRemoved()) {
				continue;
			}
			// 生まれてない
			if (candidate.isUnBirth()) {
				continue;
			}
			// 相手に子供がいる場合はスキップ
			if (candidate.getChildrenListSize() != 0) {
				continue;
			}
			// 自分とランクが違ったらスキップ
			if (body.getPublicRank() != candidate.getPublicRank()) {
				continue;
			}
			// 障害ゆんもスキップ
			if (candidate.hasDisorder()) {
				continue;
			}
			// かびてるのもスキップ
			if (body.findSick(candidate)) {
				continue;
			}
			// ロリコンはいない
			if (age > candidate.getAgeState().ordinal()) {
				continue;
			}
			// お相手がすでにいるのは50%の確率でスキップ
			if (org.simyukkuri.util.YukkuriLookup.getYukkuriById(candidate.getPartner()) != null) {
				if (GameRandom.nextBoolean()) {
					continue;
				}
			}
			activeFianceeList.add(candidate);
		}
		return activeFianceeList;
	}

	/**
	 * アクティブな赤ゆ/子ゆのリストを作成する.
	 * 
	 * @param body            ゆっくり
	 * @param includeChildren 子ゆっくりを入れるかどうか（これがfalseなら赤ゆのみのリストになる）
	 * @return アクティブな赤ゆ/子ゆのリスト
	 */
	public static final List<Yukkuri> createActiveChildList(Yukkuri body, boolean includeChildren) {
		// 子供がいないならスキップ
		int childCount = body.getChildrenListSize();
		if (childCount == 0) {
			return null;
		}
		List<Yukkuri> activeChildrenList = new LinkedList<Yukkuri>();
		for (int i = 0; i < childCount; i++) {
			Yukkuri childBody = body.getChildren(i);
			if (childBody == null) {
				continue;
			}
			// 死んでる
			if (childBody.isDead()) {
				continue;
			}
			// 除去された
			if (childBody.isRemoved()) {
				continue;
			}
			// 生まれてない
			if (childBody.isUnBirth()) {
				continue;
			}
			// プレイヤーにアイテムとして持たれてる
			if (childBody.isTaken()) {
				continue;
			}
			// 子供に子供がいる場合はスキップ
			if (childBody.getChildrenListSize() != 0) {
				continue;
			}
			// うんうん奴隷はスキップ
			if (childBody.getPublicRank() == PublicRank.UnunSlave) {
				continue;
			}
			// うまれたての赤ゆは、家族イベントの対象にしない。
			if (childBody.isBirthMessageForced() || childBody.getBirthEventBlockedTicks() > 0) {
				continue;
			}
			// 生まれた直後や落下中の赤ゆは、家族イベントの対象にしない。
			if (childBody.isFirstGround() || childBody.isNewborn() || childBody.getZ() > childBody.getMostDepth()) {
				continue;
			}
			if (childBody.isNYD() || childBody.isNotAllright()) {
				continue;
			}
			if (!includeChildren) {
				// 赤ゆっくり以外参加しないのでスキップ
				if (!childBody.isBaby()) {
					continue;
				}
			} else {
				// 赤ゆっくり、子ゆっくり以外参加しないのでスキップ
				if (childBody.isAdult()) {
					continue;
				}
			}
			activeChildrenList.add(childBody);
		}
		return activeChildrenList;
	}
}
