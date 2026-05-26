package org.simyukkuri.entity.core.living.yukkuri;

import java.util.Iterator;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.UnbirthBabyState;
import org.simyukkuri.logic.YukkuriRelations;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;

/**
 * 茎・実ゆ・誕生フラグまわりの振る舞いを扱う delegate.
 */
public final class YukkuriStalkDelegate {
	private final Yukkuri body;

	/**
	 * 茎関連を扱う delegate を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriStalkDelegate(Yukkuri body) {
		this.body = body;
	}

	/**
	 * 茎に触ったときの反応.
	 */
	public void touchStalk() {
		body.setMessage(GameMessages.getMessage(body, MessagePool.Action.AbuseBaby));
		body.setHappiness(Happiness.SAD);
	}

	/**
	 * 未誕生フラグを設定する.
	 *
	 * @param flag 未誕生かどうか
	 */
	public void setUnBirth(boolean flag) {
		body.setUnBirthState(flag);
	}

	/**
	 * 未誕生フラグをロード時向けに設定する.
	 *
	 * @param flag 未誕生かどうか
	 */
	public void setUnBirthForLoad(boolean flag) {
		body.setUnBirthStateForLoad(flag);
	}

	/**
	 * 茎を引っこ抜く.
	 *
	 * @param stalk 茎のインスタンス
	 */
	public void removeStalk(Stalk stalk) {
		if (!body.isDead() && !body.isSleeping() && body.isNotNyd()) {
			body.setHappiness(Happiness.VERY_SAD);
			body.addStress(700);
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.AbuseBaby));
		}
		if (body.getStalks() == null) {
			return;
		}
		if (stalk != null && stalk.getAttachedBabyIds() != null) {
			for (Integer childId : stalk.getAttachedBabyIds()) {
				if (childId == null) {
					continue;
				}
				Yukkuri child = YukkuriRelations.getYukkuriById(childId);
				if (child != null) {
					child.setParentLinkId(-1);
				}
			}
		}
		body.getStalks().remove(stalk);
		if (body.getStalks().isEmpty()) {
			body.setHasStalk(false);
		}
	}

	/**
	 * 茎をすべて掃除する.
	 */
	public void removeAllStalks() {
		body.setHasStalk(false);
		if (body.getStalks() == null) {
			return;
		}
		Iterator<Stalk> itr = body.getStalks().iterator();
		while (itr.hasNext()) {
			try {
				Stalk stalk = itr.next();
				Iterator<Integer> childIds = stalk.getAttachedBabyIds().iterator();
				while (childIds.hasNext()) {
					Yukkuri child = YukkuriRelations.getYukkuriById(childIds.next());
					if (child != null && (child.isDead() || child.isRemoved())) {
						child.remove();
					}
				}
				stalk.setPlantYukkuri(null);
				stalk.remove();
			} catch (Exception e) {
				continue;
			}
		}
		body.setStalks(new java.util.LinkedList<>());
	}

	/**
	 * 茎列の先頭を取り出して削除する.
	 *
	 * @return 先頭の茎。なければ null
	 */
	public Stalk getStalksDequeue() {
		Stalk stalk = null;
		if (body.getStalks() != null && !body.getStalks().isEmpty()) {
			stalk = body.getStalks().get(0);
			body.getStalks().remove(0);
		}
		return stalk;
	}

	/**
	 * 死亡時などに茎とゆっくりのバインドだけを解く.
	 */
	public void disPlantStalks() {
		if (body.getStalks() == null) {
			return;
		}
		for (Stalk stalk : body.getStalks()) {
			if (stalk != null) {
				stalk.setPlantYukkuri(null);
			}
		}
		body.getStalks().clear();
	}

	/**
	 * 茎付き個体を掴む.
	 */
	public void grab() {
		body.setGrabbed(true);
		if (body.getBindStalk() != null) {
			body.checkReactionStalkMother(org.simyukkuri.enums.UnbirthBabyState.KILLED);
			detachFromStalk();
		}
	}

	/**
	 * 茎からこの個体を切り離す.
	 */
	public void detachFromStalk() {
		if (body.getBindStalk() == null) {
			return;
		}
		if (body.getBindStalk().getAttachedBabyIds() != null) {
			int idx = body.getBindStalk().getAttachedBabyIds().indexOf(body.getUniqueId());
			if (idx >= 0) {
				body.getBindStalk().getAttachedBabyIds().set(idx, null);
			}
		}
		body.setBindStalk(null);
		body.setParentLinkId(-1);
	}

	/** 茎の実ゆの状態を母親が感知したときの感情・メッセージ反応. */
	public void onChildStateNotify(UnbirthBabyState state, boolean childDead) {
		switch (state) {
			case ATTACKED:
				if (!childDead) {
					body.setHappiness(Happiness.SAD);
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.AbuseBaby));
					body.addStress(30);
					body.stay();
					break;
				}
				reactToChildKilled();
				break;
			case KILLED:
				reactToChildKilled();
				break;
			case SAD:
				body.setHappiness(Happiness.SAD);
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ConcernAboutChild));
				body.addStress(20);
				body.stay();
				break;
			case HAPPY:
				body.setHappiness(Happiness.VERY_HAPPY);
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.GladAboutChild));
				body.addStress(-100);
				body.stay();
				break;
			default:
				break;
		}
	}

	private void reactToChildKilled() {
		body.setHappiness(Happiness.VERY_SAD);
		body.setMessage(GameMessages.getMessage(body, MessagePool.Action.AbuseBabyKilled));
		body.addStress(500);
		body.stay();
	}
}
