package org.simyukkuri.entity.core.living.yukkuri;

import org.simyukkuri.enums.CriticalDamegeType;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.PanicType;
import org.simyukkuri.enums.PlayStyle;
import org.simyukkuri.event.impl.CutPenipeniEvent;
import org.simyukkuri.event.impl.BegForLifeEvent;
import org.simyukkuri.logic.YukkuriEventState;
import org.simyukkuri.logic.EventLogic;
import org.simyukkuri.logic.ToyLogic;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;

/**
 * イベント・休止系の振る舞いを集約する delegate.
 */
public final class YukkuriEventDelegate {
	private final Yukkuri body;

	/**
	 * delegate を生成する.
	 *
	 * @param body 更新対象のゆっくり
	 */
	public YukkuriEventDelegate(Yukkuri body) {
		this.body = body;
	}

	/**
	 * 行動・イベントの取り消しを行う.
	 */
	public void clearActions() {
		YukkuriEventState.clearActions(body);
	}

	/**
	 * イベントをクリアする.
	 */
	public void clearEvent() {
		YukkuriEventState.clearEvent(body);
	}

	/**
	 * イベント用のアクションだけをクリアする.
	 */
	public void clearActionsForEvent() {
		YukkuriEventState.clearActionsForEvent(body);
	}

	/**
	 * 強制的に寝かせる.
	 */
	public void forceToSleep() {
		if (body.isDead()) {
			return;
		}
		if (body.getPanicType() == PanicType.BURN || body.getCriticalDamegeType() == CriticalDamegeType.CUT) {
			return;
		}
		clearActions();
		body.setCalm();
		body.setExcitingPeriod(0);
		body.setPanicType(null);
		body.setPanicPeriod(0);
		body.setSleepingPeriod(0);
		body.setSleeping(true);
	}

	/**
	 * ゆっくりしてる時のアクションを実行する.
	 */
	public void killTime() {
		if (body.getCurrentEvent() != null) {
			return;
		}
		if (body.getPlaying() != null) {
			return;
		}
		int p = GameRandom.nextInt(50);
		// 6/50でキリッ
		if (p <= 5) {
			body.getInVain(true);
		}
		// 6/50でのびのび
		else if (p <= 11) {
			// if yukkuri is not rude, she goes into her shell by discipline.
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Nobinobi), 40);
			body.setNobinobi(true);
			body.addStress(-50);
			body.stay(40);
		}
		// 6/50でふりふり
		else if (p <= 17 && body.willingFurifuri()) {
			// if yukkuri is rude, she will not do furifuri by discipline.
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.FuriFuri), 40);
			body.setFurifuri(true);
			body.addStress(-70);
			body.stay(30);
		}
		// 6/50で腹減った
		else if ((p <= 23 && body.isHungry()) || body.isSoHungry()) {
			// 空腹時
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Hungry), 30);
			body.stay(30);
		}
		// 6/50でおもちゃで遊ぶ
		else if (p <= 29) {
			if (ToyLogic.checkToy(body)) {
				body.setPlaying(PlayStyle.BALL);
				body.setPlayingLimit(150 + GameRandom.nextInt(100) - 49);
				return;
			} else {
				killTime();
			}
		}
		// 6/50でトランポリンで遊ぶ
		else if (p <= 35) {
			if (ToyLogic.checkTrampoline(body)) {
				body.setPlaying(PlayStyle.TRAMPOLINE);
				body.setPlayingLimit(150 + GameRandom.nextInt(100) - 49);
				return;
			} else {
				killTime();
			}
		}
		// 6/50ですいーで遊ぶ
		else if (p <= 41) {
			if (ToyLogic.checkSui(body)) {
				body.setPlaying(PlayStyle.SUI);
				body.setPlayingLimit(150 + GameRandom.nextInt(100) - 49);
				return;
			} else {
				killTime();
			}
		} else {
			// おくるみありで汚れていない場合
			if (body.isHasPants() && !body.isDirty() && (GameRandom.nextInt(10) == 0)) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.RelaxOkurumi));
			} else {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Relax));
			}
			body.addStress(-60);
			body.stay(30);
		}
	}

	/**
	 * 命乞いをする.
	 */
	public void begForLife() {
		begForLife(false);
	}

	/**
	 * 命乞いをする.
	 *
	 * @param forceFlag 強制命乞いフラグ
	 */
	public void begForLife(boolean forceFlag) {
		if (!body.canAction()) {
			return;
		}
		if (!body.isDamaged() && !forceFlag) {
			return;
		}

		int normalProbability = 2;
		int rudeProbability = 3;
		boolean requestBeg = false;
		switch (body.getAttitude()) {
			case VERY_NICE:
				if (GameRandom.nextInt(normalProbability) == 0) {
					requestBeg = true;
				}
				break;
			case NICE:
			case AVERAGE:
				if (body.isStressful() && GameRandom.nextInt(normalProbability) == 0) {
					requestBeg = true;
				}
				break;
			case SHITHEAD:
				if (body.isStressful() && body.getIntelligence() != Intelligence.FOOL
						&& GameRandom.nextInt(normalProbability) == 0) {
					requestBeg = true;
				} else if (body.isVeryStressful() && GameRandom.nextInt(rudeProbability) == 0) {
					requestBeg = true;
				}
				break;
			case SUPER_SHITHEAD:
				if (body.isStressful() && body.getIntelligence() != Intelligence.FOOL
						&& GameRandom.nextInt(rudeProbability) == 0) {
					requestBeg = true;
				} else if (body.isVeryStressful() && body.isDamagedHeavily()
						&& GameRandom.nextInt(rudeProbability) == 0) {
					requestBeg = true;
				}
				break;
			default:
				if (GameRandom.nextInt(normalProbability) == 0) {
					requestBeg = true;
				}
				break;
		}
		if (forceFlag || requestBeg) {
			EventLogic.addYukkuriEvent(body, new BegForLifeEvent(body, null, null, 1), null, null);
		}
	}

	/**
	 * イベントに反応できる状態かチェックする.
	 *
	 * @return イベントに反応できるなら true
	 */
	public boolean canEventResponse() {
		if (body.isDead() || body.getCriticalDamege() == CriticalDamegeType.CUT || body.isPealed() ||
				body.isPacked() || (body.isBlind() && !isCutPeni()) || body.isSleeping() || body.isShitting() || body.isBirth() || body.isSukkiri() ||
				body.isNeedled() || body.getCurrentEvent() != null || body.isNYD() || body.isTaken()
				|| body.getBurialState() != BurialState.NONE || body.isLockmove() || body.isStarving()) {
			return false;
		}
		if (body.isRaper() && (body.isExciting() || body.isForceExciting())) {
			return false;
		}
		return true;
	}

	/**
	 * ぺにぺに切断のみ、盲目状態でも起きて良い.
	 *
	 * @return ぺにぺに切断イベントが先頭にあるかどうか
	 */
	public boolean isCutPeni() {
		if (body.getEventList() == null || body.getEventList().isEmpty()) {
			return false;
		}
		return body.getEventList().get(0) instanceof CutPenipeniEvent;
	}
}
