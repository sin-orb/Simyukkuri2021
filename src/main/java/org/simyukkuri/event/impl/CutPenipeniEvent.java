package org.simyukkuri.event.impl;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;

/***************************************************
 * ぺに切りの反応イベント
 * protected Yukkuri from; // イベントを発した個体
 * protected Yukkuri to; // 攻撃対象
 * protected Entity target; // 未使用
 * protected int count; // 10
 */
public class CutPenipeniEvent extends EventPacket {

	private static final long serialVersionUID = -9152418777999085341L;
	int tick = 0;

	/**
	 * コンストラクタ.
	 */
	public CutPenipeniEvent(Yukkuri fromBody, Yukkuri toBody, Entity targetObject, int count) {
		super(fromBody, toBody, targetObject, count);
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public CutPenipeniEvent() {

	}

	/** イベントの進行ティックカウンタを返す。 */
	public int getTick() {
		return tick;
	}

	/** イベントの進行ティックカウンタをセットする。 */
	public void setTick(int tick) {
		this.tick = tick;
	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	/** イベントへの参加可否を判定し、参加可能なら true を返す。 */
	@Override
	public boolean checkEventResponse(Yukkuri body) {

		setHighPriority();
		Yukkuri fromBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (body == fromBody)
			return true;
		return false;
	}

	// イベント開始動作
	/** イベントの開始処理を実行する。 */
	@Override
	public void start(Yukkuri body) {
	}

	// 毎フレーム処理
	// UpdateState.ABORTを返すとイベント終了
	/** 毎ティック状態を更新する。 */
	@Override
	public UpdateState update(Yukkuri body) {
		if (body.isUnBirth()) {
			body.wakeup();
			// ぺにぺ二があれば切断
			body.setPenipeniCutted(true);
			// 興奮状態解除
			body.setCalm();
			// レイパーじゃなくなる
			body.setRaper(false);
			// ダメージをくらう
			body.addDamage(50);
			body.setForceFace(ImageCode.CUTPENIPENI.ordinal());
			body.setHappiness(Happiness.VERY_SAD);
			body.setCanTalk(true);
			body.setEventResMessage(GameMessages.getMessage(body, MessagePool.Action.Scream2), 50, true, true);
			body.setCanTalk(false);
			return UpdateState.FORCE_EXEC;
		}

		if (tick == 0) {
			body.wakeup();
			// ぺにぺ二があれば切断
			body.setPenipeniCutted(true);
			// 興奮状態解除
			body.setCalm();
			// レイパーじゃなくなる
			body.setRaper(false);
			// 固まる
			body.stayPurupuru(40);
			// ダメージをくらう
			body.addDamage(50);
			body.setLockmove(true);
			body.setForceFace(ImageCode.CUTPENIPENI.ordinal());
		} else if (tick == 20) {
			// 驚く
			body.setLockmove(false);
			if (body.isNotNYD()) {
				body.setForceFace(ImageCode.SURPRISE.ordinal());
				if (GameRandom.nextInt(2) == 0)
					body.setEventResMessage(GameMessages.getMessage(body, MessagePool.Action.Scream2), 30, true,
							false);
				else
					body.setEventResMessage(GameMessages.getMessage(body, MessagePool.Action.Surprise), 30, true,
							false);
			}
		} else if (tick == 40) {
			// 反応する
			if (body.isNotNYD()) {
				body.setEventResMessage(GameMessages.getMessage(body, MessagePool.Action.PenipeniCutting), 50, true,
						true);
				body.setHappiness(Happiness.VERY_SAD);
				body.setForceFace(ImageCode.CRYING.ordinal());
				body.stay(30);
			}
			// なつき度設定
			body.addLovePlayer(-500);
			body.stay(20);
			// ゲスほどストレスを受ける
			switch (body.getAttitude()) {
				default:
					break;
				case VERY_NICE:
					body.addStress(body.getStressLimit() / 10);
					break;
				case NICE:
					body.addStress(body.getStressLimit() / 8);
					break;
				case AVERAGE:
					body.addStress(body.getStressLimit() / 5);
					break;
				case SHITHEAD:
				case SUPER_SHITHEAD:
					body.addStress(body.getStressLimit() / 3);
					break;
			}
		} else if (tick == 70) {
			if (GameRandom.nextBoolean())
				body.doYunnyaa(true);
			return UpdateState.FORCE_EXEC;
		}
		tick++;
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	/** イベント終了判定を行い true で終了する。 */
	@Override
	public boolean execute(Yukkuri body) {
		return true;
	}

	// イベント終了処理
	@Override
	/**
	 * End.
	 *
	 * @param body the body
	 */
	public void end(Yukkuri body) {
		body.setCalm();
		body.setPenipeniCutted(true);
		body.setHappiness(Happiness.VERY_SAD);
		body.setLockmove(false);
	}

	/** イベント名の文字列表現を返す。 */
	@Override
	public String toString() {
		return GameText.read("event_cutpeni");
	}
}
