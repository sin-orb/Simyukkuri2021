package org.simyukkuri.entity.core.living.yukkuri;

import org.simyukkuri.Const;
import org.simyukkuri.engine.birth.BabyDnaFactory;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.FootBake;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.UnbirthBabyState;
import org.simyukkuri.event.impl.CutPenipeniEvent;
import org.simyukkuri.event.impl.RaperWakeupEvent;
import org.simyukkuri.logic.EventLogic;
import org.simyukkuri.logic.BodyRelations;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameRandom;

/**
 * ゆっくりの性行為・発情系責務をまとめる委譲クラス。
 */
public final class YukkuriSexualDelegate {
	private final Yukkuri body;

	/**
	 * 性行為・発情関連を扱う委譲を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriSexualDelegate(Yukkuri body) {
		this.body = body;
	}

	/**
	 * すっきりを行う
	 *
	 * @param p すっきり相手
	 */
	public void doSukkiri(Yukkuri p) {
		if (body.isDead()) {
			return;
		}
		if (body.isNYD()) {
			return;
		}
		// change own state
		body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Sukkiri), 60, true, false);
		body.setStress(0);
		body.addMemories(20);
		body.stay(60);
		body.clearActions();
		body.setSukkiri(true);
		body.setCalm();
		body.setHappiness(Happiness.HAPPY);
		body.setHungry(body.getHungry() - body.getHungryLimitBase()[AgeState.BABY.ordinal()]);
		// hungryState = checkHungryState();
		// if it has pants, cannot get pregnant
		if (body.isHasPants() || p.isHasPants()) {
			if (body.isHasPants()) {
				body.makeDirty(true);
			} else {
				p.makeDirty(true);
			}
			return;
		}
		if (body.isSick() && GameRandom.nextBoolean()) {
			p.addSickPeriod(100);
		}
		if (p.isSick() && GameRandom.nextBoolean()) {
			body.addSickPeriod(100);
		}
		if (p.isDead()) {
			return;
		}
		if (p.isNotNYD()) {
			p.setStress(0);
			p.addMemories(20);
			// 相手の妊娠判定
			p.setMessage(GameMessages.getMessage(p, MessagePool.Action.Sukkiri), 60, true, false);
			p.clearActions();
			p.setSukkiri(true);
			p.setHappiness(Happiness.HAPPY);
		}
		p.stay(60);
		p.setCalm();
		p.setHungry(p.getHungry() - (body.getHungryLimitBase()[AgeState.BABY.ordinal()] * 2));

		// 妊娠タイプはランダムで決定
		boolean stalkMode = GameRandom.nextBoolean();
		// 該当タイプが避妊されてたら妊娠失敗
		if ((stalkMode && p.isStalkCastration())
				|| (!stalkMode && p.isBodyCastration())
				|| (!stalkMode && p.getFootBakeLevel() == FootBake.CRITICAL)) {
			p.setHappiness(Happiness.VERY_SAD);
			p.setMessage(GameMessages.getMessage(p, MessagePool.Action.NoPregnancy));
			p.addStress(1000);
			return;
		}
		// 子供の生成
		if (stalkMode) {
			p.setHasStalk(true);
		} else {
			p.setHasBaby(true);
		}
		/*
		 * カップルの設定は結婚イベントでやるので、ここではなし
		 * if (isAdult() && p.isAdult()){
		 * partner = p;
		 * p.partner = this;
		 * }
		 */
		for (int i = 0; i < 5; i++) {
			Dna baby = BabyDnaFactory.createBabyDna(p, body, body.getType(), body.getAttitude(), body.getIntelligence(),
					false, (body.isSickHeavily() || body.isStarving()), i == 4);
			if (stalkMode) {
				p.getStalkBabyTypes().add(baby);
			} else if (baby != null) {
				p.getBabyTypes().add(baby);
			}
		}
		p.subtractPregnantLimit();
	}

	/**
	 * れいぽぅする
	 *
	 * @param p れいぽぅ相手
	 */
	public void doRape(Yukkuri p) {
		if (body.isDead() || body.isSukkiri()) {
			return;
		}
		// 相手がレイパーなら何もしない
		if (p.isRaper()) {
			body.clearActions();
			return;
		}

		// change own state
		body.setMessage(GameMessages.getMessage(body, MessagePool.Action.SukkiriForRaper), 60, true, false);
		body.setStress(0);
		body.stay(65);
		body.addMemories(100);
		p.clearActions();
		p.addStress(500);
		p.stay(65);

		body.setSukkiri(true);
		body.setHappiness(Happiness.HAPPY);
		if (body.isRaper()) {
			body.setHungry(body.getHungry() - (body.getHungryLimitBase()[AgeState.BABY.ordinal()] / 4));
		} else {
			body.setHungry(body.getHungry() - (body.getHungryLimitBase()[AgeState.BABY.ordinal()] * 4));
		}
		// hungryState = checkHungryState();
		// if it has pants, cannot get pregnant
		if (body.isHasPants() || p.isHasPants()) {
			p.setMessage(GameMessages.getMessage(p, MessagePool.Action.ScareRapist));
			p.setHappiness(Happiness.SAD);
			if (body.isHasPants()) {
				body.makeDirty(true);
			} else {
				p.makeDirty(true);
			}
			return;
		}
		// ゆかび持ちとすっきりすると1/2の確率で伝染る
		if ((body.isSick() || p.isSick()) && GameRandom.nextBoolean()) {
			p.addSickPeriod(100);
			body.addSickPeriod(100);
		}
		if (p.isDead()) {
			if (GameRandom.nextInt(3) == 0) {
				p.setCrushed(true);
			}
			// 死体とすっきりすると死体がゆかび持ちでなくとも1/4の確率でゆかび感染
			if (GameRandom.nextInt(4) == 0) {
				body.addSickPeriod(100);
			}
			return;
		}

		// 相手の妊娠判定
		p.wakeup();
		if (p.isNotNYD()) {
			p.setMessage(GameMessages.getMessage(p, MessagePool.Action.RaperSukkiri), 60, true, false);
			p.setSukkiri(true);
			body.setCalm();
			p.setHappiness(Happiness.VERY_SAD);
			p.setForceFace(ImageCode.CRYING.ordinal());
		}
		p.subtractPregnantLimit();
		p.setHungry(p.getHungry() - body.getHungryLimitBase()[AgeState.BABY.ordinal()]);

		// 避妊されてたら妊娠失敗
		if (p.isStalkCastration()) {
			return;
		}

		// 子供の生成
		p.setHasStalk(true);
		for (int i = 0; i < 5; i++) {
			Dna baby = BabyDnaFactory.createBabyDna(p, body, body.getType(), body.getAttitude(), body.getIntelligence(),
					true, (body.isSickHeavily() || body.isStarving()), i == 4);
			p.getStalkBabyTypes().add(baby);
		}
		if (body.isRaper()) {
			// れいぱーは強制れいぽぅ
			forceToRaperExcite(true);
			EventLogic.addWorldEvent(new RaperWakeupEvent(body, null, null, 1), null, null);
		} else if (body.getAttitude() == Attitude.SUPER_SHITHEAD) {
			// ドゲスは婚姻関係を保ちつつもれいぽぅ
			forceToRaperExcite(false);
		}
		p.subtractPregnantLimit();
	}

	/**
	 * オナニー本体処理
	 *
	 * @param p 相手（死体など
	 */
	public void doOnanism(Yukkuri p) {
		if (!body.canAction()) {
			return;
		}
		body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Sukkiri), 60, true, false);
		body.addStress(-50);
		body.addMemories(5);
		body.stay(60);
		body.clearActions();
		body.setCalm();
		body.setHappiness(Happiness.HAPPY);
		body.setHungry(body.getHungry() - body.getHungryLimitBase()[AgeState.BABY.ordinal()]);
		if (body.isHasPants()) {
			body.makeDirty(true);
		}
		if (p != null) {
			if ((p.isDead() || p.isSick()) && GameRandom.nextBoolean()) {
				body.addSickPeriod(100);
			}
			if (p.canAction()) {
				p.addStress(50);
				p.addMemories(-5);
				p.setMessage(GameMessages.getMessage(p, MessagePool.Action.Surprise), 60, true, false);
				p.clearActions();
				p.setHappiness(Happiness.SAD);
			}
			p.stay(60);
			p.setCalm();
		}
	}

	/**
	 * 精子餡注入
	 *
	 * @param dna DNA
	 */
	public void injectInto(Dna dna) {
		if (body.isDead()) {
			return;
		}
		body.strikeByPunish();
		GameEnvironment.setAlarm();
		if (dna == null || body.isBodyCastration()) {
			return;
		}
		Dna baby = BabyDnaFactory.createBabyDna(body, BodyRelations.getBody(dna.getFather()),
				dna.getType(), dna.getAttitude(), dna.getIntelligence(), false, false, true);
		body.getBabyTypes().add(baby);
		body.setHasBaby(true);
		body.subtractPregnantLimit();
	}

	/**
	 * 精子餡滴下
	 *
	 * @param dna DNA
	 */
	public void dripSperm(Dna dna) {
		if (body.isDead()) {
			return;
		}
		if (dna == null || body.isStalkCastration()) {
			return;
		}
		for (int i = 0; i < 5; i++) {
			Dna baby = BabyDnaFactory.createBabyDna(body, BodyRelations.getBody(dna.getFather()),
					dna.getType(), dna.getAttitude(), dna.getIntelligence(), false, false, true);
			body.getStalkBabyTypes().add((GameRandom.nextBoolean() ? baby : null));
		}
		body.setHasStalk(true);
		body.subtractPregnantLimit();
	}

	/**
	 * 強制的に発情させる.
	 */
	public void forceToExcite() {
		if (body.isRaper() && !body.isDead()) {
			body.forceToRaperExcite(true);
			EventLogic.addWorldEvent(new RaperWakeupEvent(body, null, null, 1), body,
					GameMessages.getMessage(body, MessagePool.Action.ExciteForRaper));
			return;
		}

		if (body.isNYD() || body.isMelt()) {
			body.stayPurupuru(20);
			return;
		}

		if (body.isExciting()) {
			body.setForceExciting(true);
		}
		if (!body.canAction()) {
			return;
		}
		if (body.isPenipeniCutted()) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.PenipeniCutted));
			body.setHappiness(Happiness.VERY_SAD);
			body.setForceFace(ImageCode.TIRED.ordinal());
			body.stayPurupuru(20);
			body.addStress(30);
			body.addLovePlayer(-50);
			return;
		}
		body.clearActionsForEvent();
		body.setToSukkiri(true);
		body.wakeup();
		body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Excite));
		body.setForceFace(ImageCode.EXCITING.ordinal());
		body.setExciting(true);
		body.stayPurupuru(Const.STAYLIMIT);
	}

	/**
	 * ぺにぺに切断のトグル
	 */
	public void cutPenipeni() {
		if (body.isPenipeniCutted()) {
			body.setPenipeniCutted(false);
			return;
		}
		body.clearActions();
		body.setSleeping(false);
		EventLogic.addBodyEvent(body, new CutPenipeniEvent(body, null, null, 1), null, null);
		body.checkReactionStalkMother(UnbirthBabyState.ATTAKED);
	}

	/**
	 * 早いすっきり抑制を行う
	 */
	public void rapidExcitingDiscipline() {
		if (body.getExcitingDiscipline() > 0) {
			body.setExcitingDiscipline(body.getExcitingDiscipline() - Yukkuri.TICK);
		}
	}

	/**
	 * れいぱー発情させる.
	 *
	 * @param raper れいぱーかどうか
	 */
	public void forceToRaperExcite(boolean raper) {
		if (body.isDead() || body.isExciting() || body.isPenipeniCutted()) {
			return;
		}
		body.wakeup();
		body.clearActions();
		body.setExciting(raper);
		body.setForceExciting(raper);
		if (raper) {
			body.setPartner(-1);
		}
		body.stay();
	}
}
