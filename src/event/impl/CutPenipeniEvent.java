package src.event.impl;

import src.entity.core.Entity;
import src.entity.core.living.yukkuri.Yukkuri;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.event.EventPacket;
import src.event.EventPacket.EventPriority;
import src.event.EventPacket.UpdateState;
import src.system.MessagePool;
import src.util.GameMessages;
import src.util.GameRandom;
import src.util.GameText;

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

	public CutPenipeniEvent() {

	}

	public int getTick() {
		return tick;
	}

	public void setTick(int tick) {
		this.tick = tick;
	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	@Override
	public boolean checkEventResponse(Yukkuri body) {

		priority = EventPriority.HIGH;
		Yukkuri fromBody = src.util.BodyRegistry.getBodyInstance(getFrom());
		if (body == fromBody)
			return true;
		return false;
	}

	// イベント開始動作
	@Override
	public void start(Yukkuri body) {
	}

	// 毎フレーム処理
	// UpdateState.ABORTを返すとイベント終了
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
			body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.Scream2), 50, true, true);
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
					body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.Scream2), 30, true,
							false);
				else
					body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.Surprise), 30, true,
							false);
			}
		} else if (tick == 40) {
			// 反応する
			if (body.isNotNYD()) {
				body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.PenipeniCutting), 50, true,
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
	@Override
	public boolean execute(Yukkuri body) {
		return true;
	}

	// イベント終了処理
	@Override
	public void end(Yukkuri body) {
		body.setCalm();
		body.setPenipeniCutted(true);
		body.setHappiness(Happiness.VERY_SAD);
		body.setLockmove(false);
	}

	@Override
	public String toString() {
		return GameText.read("event_cutpeni");
	}
}
