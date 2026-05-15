package org.simyukkuri.entity.core.living;

import org.simyukkuri.entity.core.attachment.impl.Badge;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.Damage;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;

/**
 * ゆっくりのダメージ更新処理をまとめた委譲クラス.
 * 時間経過・体調異常・致命傷・環境ダメージの計算と finalize を担う。
 */
public final class LivingEntityBodyDamageDelegate {
	private final LivingEntity body;

	public LivingEntityBodyDamageDelegate(LivingEntity body) {
		this.body = body;
	}

	/** ダメージ更新の entry point. */
	public void checkDamage() {
		boolean healFlag = canHealDamageFromEnvironment();
		applyDamageOverTime();
		applyCriticalDamage();
		applyBodyConditionDamage();
		applyExternalDamage(healFlag);
		finalizeDamageState();
	}

	private boolean canHealDamageFromEnvironment() {
		return !body.isUnBirth() || body.isPlantForUnbirthChild();
	}

	private void applyDamageOverTime() {
		if (body.isSick()) {
			if (body.getSickPeriod() > (body.getIncubationPeriodBase() * 32) && body.isDamagedHeavily()) {
				if (GameRandom.nextInt(3) == 0) {
					body.damage += LivingEntity.TICK;
				}
			} else if (body.getSickPeriod() > (body.getIncubationPeriodBase() * 32)) {
				body.damage += LivingEntity.TICK * 3;
			} else if (body.getSickPeriod() > (body.getIncubationPeriodBase() * 8)) {
				body.damage += LivingEntity.TICK * 2;
			} else if (body.getSickPeriod() > body.getIncubationPeriodBase()) {
				body.damage += LivingEntity.TICK;
			}
		} else if (!body.isHungry()) {
			body.damage -= LivingEntity.TICK;
		}
		if (body.hungry <= 0) {
			body.damage += LivingEntity.TICK;
		}
	}

	private void applyBodyConditionDamage() {
		if (body.isPealed()) {
			if (body.isSleeping()) {
				body.wakeup();
			}
			body.damage += LivingEntity.TICK * 50;
			body.setSickPeriod(0);
			GameEnvironment.setAlarm();
			body.addStress(200);
			body.onPealed();
		}
		if (body.isPacked()) {
			GameEnvironment.setAlarm();
			body.addStress(50);
			body.setCanTalk(false);
			if (GameRandom.nextInt(200) == 0) {
				body.stayPurupuru(20);
			}
			body.onPacked();
		}
		if (GameWorld.get().getCurrentWorldState().getWorldIndex() == 2
				&& !(body.isSmart() && body.getAttachmentSize(Badge.class) != 0)
				&& body.getCarAccidentProb() != 0
				&& GameRandom.nextInt(body.getCarAccidentProb()) == 0) {
			body.onCarAccident();
		}
	}

	private void applyExternalDamage(boolean healFlag) {
		if (GameEnvironment.isOrangeSteam() && healFlag) {
			body.damage -= LivingEntity.TICK * 50;
		}
		if (GameEnvironment.isSugerSteam()
				&& body.damage >= body.getDamageLimitBase()[body.getAgeState().ordinal()] * 80 / 100
				&& healFlag) {
			body.damage -= LivingEntity.TICK * 100;
		}
		if (GameEnvironment.isPoisonSteam()) {
			body.damage += LivingEntity.TICK * 100;
			body.setExciting(false);
			body.setShitting(false);
			body.setFurifuri(false);
			body.wakeup();
			body.onPoisonSteam();
		}
	}

	private void applyCriticalDamage() {
		if (body.getCriticalDamege() == null) {
			return;
		}
		if (body.getCriticalDamege() == CriticalDamageType.CUT) {
			body.damage += LivingEntity.TICK * 100;
			body.addStress(50);
			if (body.isSleeping()) {
				body.wakeup();
			}
			GameEnvironment.setAlarm();
			if (GameRandom.nextInt(50) == 0) {
				body.onCutDamageReaction();
			}
			return;
		}
		if (body.getCriticalDamege() == CriticalDamageType.INJURED && !body.isSleeping()) {
			if (GameRandom.nextInt(300) == 0) {
				body.onInjuredScream(body.getX() + 3 - GameRandom.nextInt(6), body.getY() - 2);
				body.addStress(5);
				body.addDamage(50);
			}
			if (body.isFull() && body.isNoDamaged() && GameRandom.nextInt(4800) == 0) {
				body.setCriticalDamege(null);
			} else if (!body.isDamagedHeavily() && GameRandom.nextInt(33600) == 0) {
				body.setCriticalDamege(null);
			}
		}
	}

	private void finalizeDamageState() {
		if (body.damage < 0) {
			body.damage = 0;
		}
		Damage newDamageState = body.getDamageState();
		if (body.getDamageState() == Damage.NONE && newDamageState == Damage.NONE && !body.isSleeping()) {
			body.noDamagePeriod += LivingEntity.TICK;
		} else {
			body.noDamagePeriod = 0;
		}
		body.setDamageState(newDamageState);
		if (body.getDamageState() == Damage.TOOMUCH && body.getCurrentEvent() != null
				&& body.getCurrentEvent().getPriority() != EventPacket.EventPriority.HIGH) {
			body.clearEvent();
		}
	}
}
