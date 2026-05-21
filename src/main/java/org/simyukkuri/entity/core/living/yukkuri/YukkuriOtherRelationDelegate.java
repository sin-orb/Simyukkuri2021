package org.simyukkuri.entity.core.living.yukkuri;

import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.YukkuriRelationType;
import org.simyukkuri.logic.YukkuriLogic;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;

/**
 * 家族以外の他ゆっくりとの関係処理をまとめる委譲クラス。
 */
public final class YukkuriOtherRelationDelegate {
	private final Yukkuri body;

	/**
	 * 他個体関係の委譲を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriOtherRelationDelegate(Yukkuri body) {
		this.body = body;
	}

	/**
	 * すりすりする.
	 *
	 * @param p すりすり相手
	 */
	public void doSurisuri(Yukkuri p) {
		if (body.isDead() || p.isDead()) {
			return;
		}
		if (body.isVeryHungry()) {
			return;
		}
		if (body.isPeropero()) {
			return;
		}
		if (!body.canAction()) {
			return;
		}
		YukkuriRelationType relation = YukkuriLogic.checkMyRelation(body, p);
		if (body.findSick(p) || p.isFeelHardPain() || p.isDamaged()) {
			switch (relation) {
				case FATHER:
				case MOTHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TreatChildBySurisuri));
					break;
				case PARTNER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TreatPartnerBySurisuri));
					break;
				case CHILD_OF_FATHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TreatFatherBySurisuri));
					break;
				case CHILD_OF_MOTHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TreatMotherBySurisuri));
					break;
				case ELDER_SISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TreatSisterBySurisuri));
					break;
				case YOUNGER_SISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TreatElderSisterBySurisuri));
					break;
				default:
					break;
			}
			body.setHappiness(Happiness.SAD);
			body.addStress(-50);
			p.addStress(-50);
		} else {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.SuriSuri));
			body.addStress(-100);
			p.addStress(-100);
			body.setHappiness(Happiness.VERY_HAPPY);
			if (body.isNotNYD()) {
				p.setHappiness(Happiness.VERY_HAPPY);
			}
		}
		if (p.getAttachmentSize(Ants.class) > 0 && GameRandom.nextInt(200) == 0) {
			if (body.getAntCount() <= 0) {
				body.setAntCount(0);
				body.addAttachment(new Ants(body));
				body.addStress(50);
				body.setHappiness(Happiness.VERY_SAD);
				body.addMemories(-1);
			}
			body.setAntCount(body.getAntCount() + 10);
		}
		body.setNobinobi(true);
		body.stay(40);
		p.stay(40);
		if (body.getIntelligence() != Intelligence.WISE && body.getSurisuriAccidentProb() != 0
				&& GameRandom.nextInt(body.getSurisuriAccidentProb()) == 0) {
			body.doSukkiri(p);
		}
		if (body.isSick() && GameRandom.nextInt(5) == 0) {
			p.addSickPeriod(100);
		}
		if (p.isSick() && GameRandom.nextInt(5) == 0) {
			body.addSickPeriod(100);
		}
	}

	/**
	 * ぺろぺろする.
	 *
	 * @param p ぺろぺろ対象
	 */
	public void doPeropero(Yukkuri p) {
		if (body.isDead() || p.isDead()) {
			return;
		}
		if (body.isNobinobi() || body.isShutmouth()) {
			return;
		}
		if (!body.canAction()) {
			return;
		}
		if (body.isSleeping()) {
			return;
		}

		YukkuriRelationType relation = YukkuriLogic.checkMyRelation(body, p);
		if (body.findSick(p) || p.isFeelHardPain() || p.isDamaged() || p.getAttachmentSize(Ants.class) != 0) {
			switch (relation) {
				case FATHER:
				case MOTHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TreatChildByPeropero));
					break;
				case PARTNER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TreatPartnerByPeropero));
					break;
				case CHILD_OF_FATHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TreatFatherBySurisuri));
					break;
				case CHILD_OF_MOTHER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TreatMotherBySurisuri));
					break;
				case ELDER_SISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TreatSisterByPeropero));
					break;
				case YOUNGER_SISTER:
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TreatElderSisterByPeropero));
					break;
				default:
					break;
			}
			body.setHappiness(Happiness.SAD);
			p.addMemories(1);
			p.addStress(-75);
		} else {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.PeroPero));
			body.setHappiness(Happiness.VERY_HAPPY);
			body.addStress(-50);
			p.setHappiness(Happiness.VERY_HAPPY);
			p.addMemories(1);
			p.addStress(-200);
			if (body.getIntelligence() == Intelligence.WISE) {
				p.addStress(-25);
			}
		}

		int ant = p.getAntCount();
		ant -= 40;
		if (ant <= 0) {
			ant = 0;
			p.removeAnts();
		}
		p.setAntCount(ant);
		if (ant > 0 && GameRandom.nextInt(200) == 0) {
			if (body.getAntCount() <= 0) {
				body.addAttachment(new Ants(body));
				body.addStress(50);
				body.setHappiness(Happiness.VERY_SAD);
				body.addMemories(-1);
			}
			body.setAntCount(body.getAntCount() + 10);
		}

		body.setNobinobi(true);
		body.setPeropero(true);
		body.stay(40);
		p.stay(40);
		p.addDamage(-10);
		if (p.getAttachmentSize(Ants.class) == 0) {
			body.substractNumOfAnts(10 * body.getAgeState().ordinal() * body.getAgeState().ordinal());
		}
		if (!p.isHasPants()) {
			p.makeDirty(false);
		}
		if (body.isSick() && GameRandom.nextBoolean()) {
			p.addSickPeriod(100);
		}
		if (p.isSick() && GameRandom.nextBoolean()) {
			body.addSickPeriod(100);
		}
	}
}
