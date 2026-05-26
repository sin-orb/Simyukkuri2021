package org.simyukkuri.event.impl;

import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameText;

/**
 * 出産時の励ましイベント
 * protected Yukkuri from; // イベントを発した個体
 * protected Yukkuri to; // 未使用
 * protected Entity target; // 未使用
 * protected int count; // 2
 */
public class BreedEvent extends EventPacket {

	private static final long serialVersionUID = -569342508529969710L;

	/**
	 * コンストラクタ.
	 */
	public BreedEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public BreedEvent() {

	}

	/**
	 * 参加チェック
	 * ここで各種チェックを行い、イベントへ参加するかを返す
	 * また、イベント優先度も必要に応じて設定できる
	 */
	@Override
	public boolean checkEventResponse(Yukkuri body) {
		// このイベントは固体どうしのイベントだが親子関係の探索が面倒なので
		// ワールドイベントとして登録、受け取り側が自分のつがいか親かを確認する
		priority = EventPriority.MIDDLE;
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (sourceBody == null) {
			return false;
		}

		if (body.nearToBirth()) {
			return false;
		}
		if (body.isUnBirth()) {
			return false;
		}
		// 生まれたての赤ゆは親の出産応援イベントに参加させない。
		// 既に生まれていて見に来る赤ゆは参加してよいので、出生直後の一時フラグだけを見る。
		if (sourceBody.isParent(body) && (body.isBirthMessageForced() || body.getBirthEventBlockedTicks() > 0)) {
			return false;
		}
		if (sourceBody == body) {
			return false;
		}
		if (!body.canEventResponse()) {
			return false;
		}

		// 興奮してるレイパーは参加しない
		if (body.isRaper() && body.isExciting()) {
			return false;
		}

		// うんうん奴隷など格差があれば祝わない
		if (body.getPublicRank() != sourceBody.getPublicRank()) {
			return false;
		}

		// 埋まっていたら参加しない
		if (body.getBurialState() != BurialState.NONE) {
			return false;
		}

		// 自分が馬鹿で親におかざりがなかったら参加しない
		if (!sourceBody.hasOkazari() && body.getIntelligence() == Intelligence.FOOL) {
			return false;
		}

		if (sourceBody.isParent(body) || sourceBody.isPartner(body) || body.isParent(sourceBody)
				|| body.isPartner(sourceBody)) {
			return true;
		}

		// アリにたかられてたらそれどころじゃないので参加しない
		if (sourceBody.getAttachmentSize(Ants.class) != 0 || sourceBody.getAntCount() != 0) {
			return false;
		}

		return false;
	}

	/**
	 * イベント開始動作
	 */
	@Override
	public void start(Yukkuri body) {
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (sourceBody != null) {
			body.moveToEvent(this, sourceBody.getX(), sourceBody.getY());
		}
	}

	/**
	 * 毎フレーム処理
	 * UpdateState.ABORTを返すとイベント終了
	 */
	@Override
	public UpdateState update(Yukkuri body) {
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (sourceBody == null) {
			return UpdateState.ABORT;
		}
		if (sourceBody.isParent(body) && (body.isBirthMessageForced() || body.getBirthEventBlockedTicks() > 0)) {
			return UpdateState.ABORT;
		}
		if (body.nearToBirth()) {
			return UpdateState.FORCE_EXEC;
		}
		// 相手の一定距離まで近づいたら移動終了
		if (Translate.distance(body.getX(), body.getY(), sourceBody.getX(), sourceBody.getY()) < 20000) {
			body.moveToEvent(this, body.getX(), body.getY());
			return UpdateState.FORCE_EXEC;
		} else {
			body.moveToEvent(this, sourceBody.getX(), sourceBody.getY());
		}

		if (sourceBody.isDead() || sourceBody.isPealed()
				|| sourceBody.isBurned() || sourceBody.isBurst()
				|| sourceBody.isRemoved() || sourceBody.isCrushed()
				|| sourceBody.isPacked() || !sourceBody.nearToBirth()) {
			return UpdateState.ABORT;
		}

		// アリにたかられたら参加どころではなくなる
		if (body.getAntCount() != 0 || body.getAttachmentSize(Ants.class) != 0) {
			body.clearEvent();
			return UpdateState.ABORT;
		}

		return null;
	}

	/**
	 * イベント目標に到着した際に呼ばれる
	 * trueを返すとイベント終了
	 */
	@Override
	public boolean execute(Yukkuri body) {
		if (body.nearToBirth()) {
			return true;
		}
		if (body.isNyd()) {
			return false;
		}
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (sourceBody == null) {
			return true;
		}
		if (sourceBody.isParent(body) && body.getBirthEventBlockedTicks() > 0) {
			return true;
		}
		body.setExciting(false);
		// 相手が出産前なら応援
		if (sourceBody.isBirth()) {
			body.setHappiness(Happiness.AVERAGE);
			body.lookTo(sourceBody.getX(), sourceBody.getY());
			body.setEventResMessage(GameMessages.getMessage(body, MessagePool.Action.RootForPartner), 40, false,
					false);
			sourceBody.addMemories(1);
			body.addMemories(1);
			return true;
		} else {
			// 誕生
			if (!sourceBody.hasBabyOrStalk()) {
				body.lookTo(sourceBody.getX(), sourceBody.getY());
				if (sourceBody.isHasPants()) {
					body.setHappiness(Happiness.VERY_SAD);
					body.setEventResMessage(GameMessages.getMessage(body, MessagePool.Action.Surprise), 40, true,
							true);
					body.addStress(1800);
					body.addMemories(-30);
				} else {
					body.setHappiness(Happiness.VERY_HAPPY);
					body.setEventResMessage(GameMessages.getMessage(body, MessagePool.Action.FirstGreeting), 40,
							true,
							false);
					body.addStress(-30);
					body.addMemories(20);
				}
			} else {
				body.setHappiness(Happiness.AVERAGE);
				body.lookTo(sourceBody.getX(), sourceBody.getY());
			}
			if (sourceBody.isParent(body)) {
				// 出生直後の祝福を見た赤ゆは、同じイベントへ即再参加しないよう一時的に外す。
				body.setBirthEventBlockedTicks(Math.max(body.getBirthEventBlockedTicks(), 300));
			}
			return true;
		}
	}

	/** イベント名の文字列表現を返す。 */
	@Override
	public String toString() {
		return GameText.read("event_welcomechild");
	}
}
