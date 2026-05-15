package org.simyukkuri.entity.core.living;

import org.simyukkuri.engine.Terrarium;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameRandom;

/**
 * ゆっくりの睡眠状態更新をまとめた委譲クラス.
 * 眠気判定・就寝・起床・悪夢の管理を担う。
 */
public final class LivingEntitySleepDelegate {
	private final LivingEntity body;

	public LivingEntitySleepDelegate(LivingEntity body) {
		this.body = body;
	}

	/** 睡眠状態を更新する. @return 睡眠中かどうか */
	public boolean checkSleep() {
		if (GameEnvironment.isNoSleepSteam()) {
			if (!body.isUnBirth() || !body.isPlantForUnbirthChild()) {
				body.setSleepingPeriod(0);
				body.setSleeping(false);
				body.setNightmare(false);
				return false;
			}
		}

		if (body.canflyCheck() && body.isSleepy()) {
			body.moveToZ(0);
			if (body.getZ() != 0) {
				return false;
			}
		}

		if (body.isSleeping()) {
			if (!body.isNightmare()
					&& ((body.isStressful() && GameRandom.nextInt(75) == 0)
							|| (body.isVeryStressful() && GameRandom.nextInt(25) == 0))) {
				body.setNightmare(true);
				body.onNightmare(true);
			} else if (!body.isStressful() || GameRandom.nextInt(100) == 0) {
				body.setNightmare(false);
				body.onNightmare(false);
			}
		}

		if (body.isSleeping() && body.isStarving()) {
			body.onWakeByHunger();
			body.stay();
			body.wakeup();
		} else if (body.isSleeping()
				|| (body.wakeUpTime + body.getActivePeriodBase() * 3 / 2 < body.getAge()
						&& !body.isExciting() && !body.isScare()
						&& !body.isVerySad() && !body.isEating() && !body.isNeedled() && !body.isTooHungry()
						&& !(body.isVeryHungry() && body.isToFood()))
				|| (body.wakeUpTime + body.getActivePeriodBase() * 3 < body.getAge()
						&& !body.isExciting() && !body.isScare()
						&& !body.isEating() && !body.isNeedled() && !(body.isTooHungry() && body.isToFood()))
				|| (body.isUnBirth() && !body.isNeedled())) {
			body.clearActions();
			body.setSleeping(true);
			body.setAngry(false);
			body.setScare(false);
			body.damage -= LivingEntity.TICK;
			if (!body.isUnBirth()) {
				body.onStartSleeping();
			} else {
				return body.isSleeping();
			}
			if (GameEnvironment.getDayState() == Terrarium.DayState.NIGHT) {
				if ((body.getAge() % (GameEnvironment.getNightTime() / body.getSleepPeriodBase() + 1)) == 0) {
					body.sleepingPeriod += LivingEntity.TICK;
				}
			} else {
				body.sleepingPeriod += LivingEntity.TICK;
			}
			if (body.sleepingPeriod > body.getSleepPeriodBase()) {
				body.onWakeupNaturally();
				body.stay();
				body.wakeup();
			}
		} else {
			if (body.getCurrentEvent() != null) {
				return false;
			}
			body.sleepingPeriod = 0;
			body.setSleeping(false);
			body.setNightmare(false);
			if (GameEnvironment.getDayState() == Terrarium.DayState.NIGHT) {
				body.wakeUpTime -= LivingEntity.TICK * 3;
			}
		}

		return body.isSleeping();
	}
}
