package org.simyukkuri.event.impl;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.event.EventPacket.EventPriority;
import org.simyukkuri.event.EventPacket.UpdateState;
import org.simyukkuri.logic.YukkuriLogic;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;

/***************************************************
 * プロポーズイベント
 * protected Yukkuri from; // イベントを発した個体
 * protected Yukkuri to; // 結婚対象
 * protected Entity target; // 未使用
 * protected int count; // 1
 */
public class ProposeEvent extends EventPacket {

	private static final long serialVersionUID = 8482363173818957959L;
	int tick = 0;
	protected boolean started = false;

	/**
	 * コンストラクタ.
	 */
	public ProposeEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
		setHighPriority();
	}

	public ProposeEvent() {

	}

	public int getTick() {
		return tick;
	}

	public void setTick(int tick) {
		this.tick = tick;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	// 参加チェック
	@Override
	public boolean checkEventResponse(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (body == sourceBody || body == targetBody)
			return true;

		return false;
	}

	// イベント開始動作
	@Override
	public void start(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (targetBody != null && sourceBody != null) {
			targetBody.wakeup();
			sourceBody.setCurrentEvent(this);
			targetBody.setCurrentEvent(this);
			int collisionX = YukkuriLogic.calcCollisionX(body, targetBody);
			if (sourceBody.canflyCheck()) {
				sourceBody.moveToEvent(this, targetBody.getX() + collisionX, targetBody.getY(), targetBody.getZ());
			} else {
				sourceBody.moveToEvent(this, targetBody.getX() + collisionX, targetBody.getY());
			}
		}
	}

	// 毎フレーム処理
	// UpdateState.ABORTを返すとイベント終了
	@Override
	public UpdateState update(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (sourceBody == null || targetBody == null || sourceBody.isDead() || sourceBody.isRemoved()
				|| sourceBody.isNYD())
			return UpdateState.ABORT;
		// 相手が死んだか 相手が消えてしまったか非ゆっくり症発症したか取られたらイベント中断
		if (targetBody.isDead() || targetBody.isRemoved() || targetBody.isNYD() || targetBody.isTaken()) {
			sourceBody.setCalm();
			sourceBody.setEventResMessage(GameMessages.getMessage(sourceBody, MessagePool.Action.Surprise), 30,
					true,
					false);
			sourceBody.setHappiness(Happiness.VERY_SAD);
			sourceBody.addStress(sourceBody.getStressLimit() / 10);
			if (GameRandom.nextBoolean()) {
				sourceBody.setForceFace(ImageCode.TIRED.ordinal());
			} else {
				sourceBody.setForceFace(ImageCode.CRYING.ordinal());
				if (GameRandom.nextInt(3) == 0)
					sourceBody.doYunnyaa(true);
			}
			return UpdateState.ABORT;
		}

		int collisionX = YukkuriLogic.calcCollisionX(body, targetBody);
		// 相手がつかまれているとき
		if (targetBody.isGrabbed()) {
			sourceBody.setEventResMessage(GameMessages.getMessage(sourceBody, MessagePool.Action.DontPreventUs), 30,
					false, GameRandom.nextBoolean());
			sourceBody.setForceFace(ImageCode.PUFF.ordinal());
			sourceBody.setAngry();
			started = false;
			sourceBody.setLockmove(false);
			targetBody.setLockmove(false);
			if (sourceBody.canflyCheck())
				sourceBody.moveToEvent(this, targetBody.getX() + collisionX, targetBody.getY(), targetBody.getZ());
			else
				sourceBody.moveToEvent(this, targetBody.getX() + collisionX, targetBody.getY());
			// ランダムであきらめる
			if (sourceBody.getIntelligence() != Intelligence.FOOL && GameRandom.nextInt(1500) == 0) {
				if (GameRandom.nextBoolean()) {
					sourceBody.setEventResMessage(
							GameMessages.getMessage(sourceBody, MessagePool.Action.LamentNoYukkuri), 30, true, true);
					sourceBody.setHappiness(Happiness.VERY_SAD);
					sourceBody.setForceFace(ImageCode.CRYING.ordinal());
				} else {
					sourceBody.setEventResMessage(
							GameMessages.getMessage(sourceBody, MessagePool.Action.LamentLowYukkuri), 30, true, true);
					sourceBody.setHappiness(Happiness.SAD);
					sourceBody.setForceFace(ImageCode.TIRED.ordinal());
				}
				return UpdateState.ABORT;
			}
			return null;
		}

		// イベントが始まってたら飛ばす
		if (started)
			return UpdateState.FORCE_EXEC;

		// たどり着くまで
		if (sourceBody.canflyCheck())
			sourceBody.moveToEvent(this, targetBody.getX() + collisionX, targetBody.getY(), targetBody.getZ());
		else
			sourceBody.moveToEvent(this, targetBody.getX() + collisionX, targetBody.getY());
		tick = 0;
		// 行動主の呼び止め
		// from.setCalm();
		// from.setForceFace(ImageCode.EXCITING.ordinal());
		sourceBody.setExciting(true);
		sourceBody.clearActionsForEvent();
		// 相手も興奮して、ぺにぺに相撲になるのの防止
		if (targetBody.isExciting()) {
			targetBody.setCalm();
			targetBody.clearEvent();
		}
		targetBody.stay();
		if (GameRandom.nextInt(20) == 0) {
			if (GameRandom.nextBoolean())
				sourceBody.setEventResMessage(GameMessages.getMessage(sourceBody, MessagePool.Action.PleaseWait),
						30,
						true, false);
			else
				sourceBody.setEventResMessage(GameMessages.getMessage(sourceBody, MessagePool.Action.Excite), 30,
						true,
						false);
		}
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (targetBody == null || sourceBody == null)
			return true;
		// to から呼ばれた場合は tick を進めない（2倍速防止）
		if (body != sourceBody)
			return false;
		if (targetBody.isGrabbed()) {
			return false;
		}
		// 相手がかびてるor食われてる時の挙動
		if (sourceBody.findSick(targetBody) || targetBody.isEatenByAnimals() || targetBody.hasDisorder()) {
			sourceBody.setCalm();
			sourceBody.setEventResMessage(GameMessages.getMessage(sourceBody, MessagePool.Action.Surprise), 30,
					true,
					false);
			sourceBody.setHappiness(Happiness.VERY_SAD);
			sourceBody.addStress(sourceBody.getStressLimit() / 10);
			if (GameRandom.nextBoolean()) {
				sourceBody.setForceFace(ImageCode.TIRED.ordinal());
			} else {
				sourceBody.setForceFace(ImageCode.CRYING.ordinal());
				if (GameRandom.nextInt(3) == 0)
					sourceBody.doYunnyaa(true);
			}
			// 夫婦関係の解消
			sourceBody.setPartner(-1);
			if (targetBody.getPartner() == sourceBody.getUniqueID()) {
				targetBody.setPartner(-1);
			}
			return true;
		}

		if (tick == 0) {
			// 行動主の呼び止め
			sourceBody.setCalm();
			sourceBody.stayPurupuru(30);
			sourceBody.addStress(10);
			if (sourceBody.isRude())
				sourceBody.setForceFace(ImageCode.VAIN.ordinal());
			else
				sourceBody.setForceFace(ImageCode.EMBARRASSED.ordinal());
			sourceBody.setEventResMessage(GameMessages.getMessage(sourceBody, MessagePool.Action.PleaseWait), 30,
					true,
					false);
			started = true;
		} else if (tick == 5) {
			// 振り向く
			targetBody.setCurrentEvent(this);
			targetBody.constraintDirection(sourceBody, false);
			sourceBody.setLockmove(true);
			targetBody.setLockmove(true);
		} else if (tick == 20) {
			// 告白
			if (sourceBody.isRude() || GameRandom.nextInt(20) == 0)
				sourceBody.setForceFace(ImageCode.VAIN.ordinal());
			else
				sourceBody.setForceFace(ImageCode.EMBARRASSED.ordinal());
			// カップルの設定(ただし、ここではやる側のみ)
			sourceBody.setPartner(targetBody.getUniqueID());
			// 告白セリフ
			sourceBody.setEventResMessage(GameMessages.getMessage(sourceBody, MessagePool.Action.Propose), 30, true,
					false);
			sourceBody.stayPurupuru(50);
			targetBody.setForceFace(ImageCode.EMBARRASSED.ordinal());
		} else if (tick == 40) {
			// 双方の反応
			// 成功判定
			boolean sayOK = acceptPropose(sourceBody, targetBody);

			// 成功
			if (sayOK) {
				targetBody.setForceFace(ImageCode.SMILE.ordinal());
				targetBody.setPartner(sourceBody.getUniqueID());
				targetBody.setEventResMessage(GameMessages.getMessage(targetBody, MessagePool.Action.ProposeYes),
						30, true,
						false);
				// ゲスほど幸福度は低い
				switch (sourceBody.getAttitude()) {
					case VERY_NICE:
						sourceBody.addStress(-sourceBody.getStressLimit() / 5);
						sourceBody.addMemories(50);
						break;
					case NICE:
						sourceBody.addStress(-sourceBody.getStressLimit() / 10);
						sourceBody.addMemories(40);
						break;
					case AVERAGE:
						sourceBody.addStress(-sourceBody.getStressLimit() / 20);
						sourceBody.addMemories(30);
						break;
					case SHITHEAD:
					case SUPER_SHITHEAD:
						sourceBody.addStress(-sourceBody.getStressLimit() / 30);
						sourceBody.addMemories(30);
						break;
				}
				switch (targetBody.getAttitude()) {
					case VERY_NICE:
						targetBody.addStress(-targetBody.getStressLimit() / 5);
						targetBody.addMemories(50);
						break;
					case NICE:
						targetBody.addStress(-targetBody.getStressLimit() / 10);
						targetBody.addMemories(40);
						break;
					case AVERAGE:
						targetBody.addStress(-targetBody.getStressLimit() / 20);
						targetBody.addMemories(30);
						break;
					case SHITHEAD:
					case SUPER_SHITHEAD:
						targetBody.addStress(-targetBody.getStressLimit() / 30);
						targetBody.addMemories(30);
						break;
				}
			}
			// 失敗
			else {
				if (targetBody.findSick(sourceBody)) {
					targetBody.setEventResMessage(
							GameMessages.getMessage(targetBody, MessagePool.Action.HateMoldyYukkuri),
							30, true, false);
					targetBody.setForceFace(ImageCode.PUFF.ordinal());
				} else {
					targetBody.setEventResMessage(GameMessages.getMessage(targetBody, MessagePool.Action.ProposeNo),
							30,
							true, false);
					if (targetBody.isRude()) {
						targetBody.setForceFace(ImageCode.RUDE.ordinal());
					} else {
						targetBody.setForceFace(ImageCode.TIRED.ordinal());
					}
				}
				sourceBody.setPartner(-1);
				// ストレスと思い出の上下
				switch (sourceBody.getAttitude()) {
					case VERY_NICE:
						sourceBody.addStress(sourceBody.getStressLimit() / 20);
						sourceBody.addMemories(-50);
						break;
					case NICE:
						sourceBody.addStress(sourceBody.getStressLimit() / 16);
						sourceBody.addMemories(-40);
						break;
					case AVERAGE:
						sourceBody.addStress(sourceBody.getStressLimit() / 10);
						sourceBody.addMemories(-30);
						break;
					case SHITHEAD:
					case SUPER_SHITHEAD:
						sourceBody.addStress(sourceBody.getStressLimit() / 6);
						sourceBody.addMemories(-20);
						break;
				}
				switch (targetBody.getAttitude()) {
					case VERY_NICE:
						targetBody.addStress(-targetBody.getStressLimit() / 20);
						break;
					case NICE:
						targetBody.addStress(-targetBody.getStressLimit() / 16);
						break;
					case AVERAGE:
						targetBody.addStress(-targetBody.getStressLimit() / 10);
						break;
					case SHITHEAD:
					case SUPER_SHITHEAD:
						targetBody.addStress(-targetBody.getStressLimit() / 6);
						break;
				}
			}
		} else if (tick == 60) {
			// 成功時はすっきりを迫る
			if (sourceBody.getPartner() == targetBody.getUniqueID()) {
				sourceBody.setHappiness(Happiness.VERY_HAPPY);
				sourceBody.clearActionsForEvent();
				sourceBody.setExciting(true);
				sourceBody.setForceFace(ImageCode.EXCITING.ordinal());
				sourceBody.setEventResMessage(GameMessages.getMessage(sourceBody, MessagePool.Action.LetsPlay), 30,
						true, false);
				targetBody.setEventResMessage(GameMessages.getMessage(targetBody, MessagePool.Action.OKcome), 30,
						true,
						false);
			}
			// 失敗時は泣いて逃げる
			else {
				sourceBody.setHappiness(Happiness.VERY_SAD);
				sourceBody.setForceFace(ImageCode.CRYING.ordinal());
				sourceBody.setEventResMessage(GameMessages.getMessage(sourceBody, MessagePool.Action.Heartbreak),
						30,
						true, false);
				sourceBody.runAway(targetBody.getX(), targetBody.getY());
				return true;
			}
		} else if (tick == 70) {
			targetBody.constraintDirection(sourceBody, true);
			sourceBody.doSukkiri(targetBody);
			return true;
		}
		tick++;
		return false;
	}

	/**
	 * fのプロポーズのtによる判定。プロポーズはfがした側、tがされた側
	 * 
	 * @param f プロポーズした側
	 * @param t プロポーズされた側
	 * @return プロポーズ成功かどうか
	 */
	public boolean acceptPropose(Yukkuri f, Yukkuri t) {
		// 既婚
		if (org.simyukkuri.util.YukkuriLookup.getYukkuriById(t.getPartner()) != null)
			return false;
		// カビ発見
		if (t.findSick(f))
			return false;
		// 妊娠中個体
		if (f.hasBabyOrStalk())
			return false;
		// 障害ゆん
		if (f.hasDisorder())
			return false;

		return true;
	}

	// イベント終了処理
	@Override
	public void end(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (sourceBody != null) {
			sourceBody.setCalm();
			sourceBody.setCurrentEvent(null);
			sourceBody.setLockmove(false);
		}
		if (targetBody != null) {
			targetBody.setCurrentEvent(null);
			targetBody.setLockmove(false);
		}
	}

	@Override
	public String toString() {
		return GameText.read("event_proposal");
	}
}
