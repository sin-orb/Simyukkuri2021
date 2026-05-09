package src.event;
import src.util.GameMessages;
import src.util.GameText;

import src.SimYukkuri;
import src.util.GameRandom;
import src.attachment.Fire;
import src.base.Yukkuri;
import src.event.EventPacket;
import src.base.Entity;
import src.enums.CriticalDamegeType;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.system.MessagePool;
import src.system.ResourceUtil;

/***************************************************
 * 命乞いイベント
 * protected Yukkuri from; // イベントを発した個体
 * protected Yukkuri to; // 攻撃対象
 * protected Entity target; // 未使用
 * protected int count; // 10
 */
public class BegForLifeEvent extends EventPacket {

	private static final long serialVersionUID = 9141159728976292371L;
	private int roop = 0;
	private int roop2 = 0;
	private int roop3 = 0;

	int tick = 0;
	private int wait = 0;

	/**
	 * コンストラクタ.
	 */
	public BegForLifeEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	public BegForLifeEvent() {

	}

	public int getRoop() {
		return roop;
	}

	public void setRoop(int roop) {
		this.roop = roop;
	}

	public int getRoop2() {
		return roop2;
	}

	public void setRoop2(int roop2) {
		this.roop2 = roop2;
	}

	public int getRoop3() {
		return roop3;
	}

	public void setRoop3(int roop3) {
		this.roop3 = roop3;
	}

	public int getTick() {
		return tick;
	}

	public void setTick(int tick) {
		this.tick = tick;
	}

	public int getWait() {
		return wait;
	}

	public void setWait(int wait) {
		this.wait = wait;
	}

	/**
	 * 参加チェック
	 * ここで各種チェックを行い、イベントへ参加するかを返す
	 * また、イベント優先度も必要に応じて設定できる
	 */
	@Override
	public boolean checkEventResponse(Yukkuri body) {

		priority = EventPriority.HIGH;
		Yukkuri sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
		if (body == sourceBody && !body.isUnBirth())
			return true;
		return false;
	}

	/**
	 * イベント開始動作
	 */
	@Override
	public void start(Yukkuri body) {
	}

	/**
	 * 毎フレーム処理
	 * UpdateState.ABORTを返すとイベント終了
	 */
		@Override
		public UpdateState update(Yukkuri body) {
		if (body.isTalking()) {
			// body.setBodyEventResMessage(GameMessages.getMessage(body,
			// MessagePool.Action.ApologyToHuman), 20, false, true);
			return null;
		}
		if (body.getDamage() == 0) {
			roop = 0;
			roop2 = 0;
		}
		if (tick == 0) {
			body.wakeup();
			// 興奮状態解除
			body.setCalm();
			body.stayPurupuru(5);
			body.setForceFace(ImageCode.VAIN.ordinal());
			roop = GameRandom.nextInt(5) + 5;
			roop2 = GameRandom.nextInt(10) + 8;
			roop3 = GameRandom.nextInt(3) + 1;
		} else if (tick >= 7 && roop != 0 && roop2 != 0 && roop3 != 0) {
			body.stay(30);
			body.setForceFace(ImageCode.CRYING.ordinal());
			body.setBegging(true);
			body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ApologyToHuman), 20, false,
					GameRandom.nextBoolean());
			roop--;
		}

		else if (roop == 0 && roop2 != 0 && roop3 != 0) {
			// 反応する
			body.stay(80);
			body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.BegForLife), 20, false,
					GameRandom.nextBoolean());
			body.setHappiness(Happiness.VERY_SAD);
			body.setForceFace(ImageCode.CRYING.ordinal());
			// なつき度設定
			body.addLovePlayer(-10);
			roop2--;
			wait = 0;
		} else if (wait == 50 && roop == 0 && roop2 == 0 && roop3 != 0) {
			body.setBegging(false);
			// 着火状態か足が破れてる状態で見逃してもらう
			if (body.getAttachmentSize(Fire.class) != 0 || body.getCriticalDamegeType() == CriticalDamegeType.CUT) {
				body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ThanksHuman2), 25, true, false);
				body.setHappiness(Happiness.VERY_SAD);
				body.setForceFace(ImageCode.CRYING.ordinal());
				return UpdateState.ABORT;
			}
			// ダメージ状態で見逃してもらう
			if (body.isDamaged()) {
				body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ThanksHuman2), 25, true, false);
				body.setHappiness(Happiness.SAD);
				body.setForceFace(ImageCode.TIRED.ordinal());
				// 増長
				switch (body.getAttitude()) {
					case VERY_NICE:
						body.addStress(body.getStressLimit() / 30);
						body.plusAttitude(10);
						break;
					case NICE:
						body.addStress(body.getStressLimit() / 20);
						body.plusAttitude(5);
						break;
					case AVERAGE:
						body.addStress(body.getStressLimit() / 20);
						// body.plusAttitude(0);
						break;
					case SHITHEAD:
					case SUPER_SHITHEAD:
						body.addStress(body.getStressLimit() / 10);
						if (body.getIntelligence() == Intelligence.FOOL)
							body.plusAttitude(-30);
						else
							body.plusAttitude(-20);
						break;
					default:
						break;
				}
			}

			// 命乞い成功
			else {
				// 賢くないゲス
				if (body.isRude() && body.getIntelligence() != Intelligence.WISE) {
					body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ThanksHuman), 25, true, true);
					body.setHappiness(Happiness.VERY_HAPPY);
					body.setForceFace(ImageCode.RUDE.ordinal());
				} else {
					body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ThanksHuman), 25, true, true);
					body.setHappiness(Happiness.VERY_HAPPY);
					body.setForceFace(ImageCode.SMILE.ordinal());
				}

				// 増長
				switch (body.getAttitude()) {
					case VERY_NICE:
						body.addStress(-body.getStressLimit() / 20);
						body.plusAttitude(10);
						break;
					case NICE:
						body.addStress(-body.getStressLimit() / 10);
						// body.plusAttitude(0);
						break;
					case AVERAGE:
						body.addStress(-body.getStressLimit() / 5);
						body.plusAttitude(-30);
						break;
					case SHITHEAD:
					case SUPER_SHITHEAD:
						body.addStress(-body.getStressLimit() / 3);
						body.plusAttitude(-50);
						break;
					default:
						break;
				}
			}
			roop3--;
			wait = 0;
		} else if (wait == 30 && roop == 0 && roop2 == 0 && roop3 == 0) {
			// 独り言ちる
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Monologue), true);
			return UpdateState.ABORT;
		}
		tick++;
		wait++;
		return null;
	}

	/**
	 * イベント目標に到着した際に呼ばれる
	 * trueを返すとイベント終了
	 */
	@Override
	public boolean execute(Yukkuri body) {
		return true;
	}

	/**
	 * もしもの後始末
	 */
	@Override
	public void end(Yukkuri body) {
		body.setBegging(false);
		return;
	}

	@Override
	public String toString() {
		return GameText.read("event_beg");
	}
}
