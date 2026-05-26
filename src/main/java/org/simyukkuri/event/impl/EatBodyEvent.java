package org.simyukkuri.event.impl;

import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.GameView;

/**
 * 死体食事中におかざりがもどってきたイベント
 * protected Yukkuri from; // 食べてる側
 * protected Yukkuri to; // 食べられてる側
 * protected Entity target; // 未使用
 * protected int count; // 30
 */
public class EatBodyEvent extends EventPacket {

	private static final long serialVersionUID = -4086868596282761812L;
	int tick = 0;

	/**
	 * コンストラクタ.
	 */
	public EatBodyEvent(Yukkuri fromBody, Yukkuri toBody, Entity targetObject, int count) {
		super(fromBody, toBody, targetObject, count);
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public EatBodyEvent() {

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
		if (org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom()) == body && body.canEventResponse()
				&& body.getAttitude() != Attitude.SUPER_SHITHEAD) {
			return true;
		}
		return false;
	}

	// イベント開始動作
	/** イベントの開始処理を実行する。 */
	@Override
	public void start(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		if (targetBody == null) {
			return;
		}
		// ゆっくりが隠れないように死体の奥に出る
		body.moveToEvent(this, targetBody.getX() + 5, targetBody.getY() + 4);
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	/** イベント終了判定を行い true で終了する。 */
	@Override
	public boolean execute(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		// 複数の動作を順次行うのでtickで管理
		if (tick == 0) {
			// 固まる
			if (targetBody != null) {
				body.lookTo(targetBody.getX(), targetBody.getY());
			}
			body.setLockmove(true);
			body.setMessage(null, true);
			body.setForceFace(ImageCode.NORMAL.ordinal());
			body.stay();
		} else if (tick == 10) {
			// 驚く
			if (targetBody != null) {
				body.lookTo(targetBody.getX(), targetBody.getY());
			}
			body.setLockmove(false);
			body.setForceFace(ImageCode.SURPRISE.ordinal());
			body.setEventResMessage(GameMessages.getMessage(body, MessagePool.Action.Surprise), 52, true, false);
			body.stay();
		} else if (tick == 70) {
			// 吐く
			if (targetBody != null) {
				body.lookTo(targetBody.getX(), targetBody.getY());
			}
			body.setForceFace(ImageCode.CRYING.ordinal());
			body.setEventResMessage(GameMessages.getMessage(body, MessagePool.Action.Vomit), 62, true, false);
			int ofsX = Translate.invertX(body.getCollisionX() >> 1, body.getY());
			if (body.getDirection() == Direction.LEFT) {
				ofsX = -ofsX;
			}
			GameView.addVomit(body.getX() + ofsX, body.getY(), body.getZ(), body, body.getShitType());
			body.stay();
		} else if (tick == 120) {
			// 善良ほどストレスを受ける
			switch (body.getAttitude()) {
				default:
					break;
				case VERY_NICE:
					body.addStress(5000);
					break;
				case NICE:
					body.addStress(3000);
					break;
				case AVERAGE:
					body.addStress(2000);
					break;
				case SHITHEAD:
					body.addStress(700);
					break;
			}
			if (body.isNotNyd()) {
				body.setHappiness(Happiness.VERY_SAD);
			}
			return true;
		} else {
			if (targetBody != null) {
				body.lookTo(targetBody.getX(), targetBody.getY());
			}
			body.stay();
		}
		tick++;
		return false;
	}

	// もしもの時のために解除
	/**
	 * End.
	 *
	 * @param body the body
	 */
	@Override
	public void end(Yukkuri body) {
		body.setLockmove(false);
		return;
	}

	/** イベント名の文字列表現を返す。 */
	@Override
	public String toString() {
		return GameText.read("event_eaten");
	}
}
