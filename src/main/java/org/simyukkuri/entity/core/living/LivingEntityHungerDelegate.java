package org.simyukkuri.entity.core.living;

/**
 * ゆっくりの空腹・代謝をまとめた委譲クラス.
 * 空腹度変動・ストレスクランプ・舌の肥えクランプを担う。
 */
public final class LivingEntityHungerDelegate {
	private final LivingEntity body;

	/** 指定の LivingEntity をラップしてデリゲートを初期化する。 */
	public LivingEntityHungerDelegate(LivingEntity body) {
		this.body = body;
	}

	/** 空腹度を状態に応じて変動させる. */
	public void checkHungry() {
		if (0 < body.getSuperEatingNoHungryPeriod()) {
			body.superEatingNoHungryPeriod--;
			if (body.hungry <= body.getHungryLimit()) {
				body.hungry += LivingEntity.TICK;
			}
			return;
		}
		if (body.isPealed() || body.isPacked()) {
			if (body.getAge() % 7 == 0)
				body.hungry -= LivingEntity.TICK;
		}
		if (body.isUnBirth()) {
			if (!body.isPlantForUnbirthChild()) {
				body.hungry -= LivingEntity.TICK * 100;
			} else {
				body.hungry = body.getHungryLimit();
			}
		} else if (body.isSleeping()) {
			if (body.getAge() % 2 == 0)
				body.hungry -= LivingEntity.TICK;
		} else if (body.isExciting() && !body.isRaper()) {
			body.hungry -= LivingEntity.TICK * (body.getBabyTypes().size() + 1);
		} else {
			body.hungry -= LivingEntity.TICK;
		}
		if (body.isHasStalk() && body.getStalks() != null) {
			body.hungry -= LivingEntity.TICK * body.getStalks().size() * 5;
		}
		if (body.isHasBaby()) {
			body.hungry -= LivingEntity.TICK * body.getBabyTypes().size();
		}
		if (body.hungry <= 0) {
			body.damage += (-body.hungry);
			body.hungry = 0;
		}
		if (!body.isHungry() && !body.isSleeping()) {
			body.noHungryPeriod += LivingEntity.TICK;
		} else {
			body.noHungryPeriod = 0;
		}
	}

	/** ストレス値を下限 0 にクランプする. */
	public void checkStress() {
		if (body.stress < 0)
			body.stress = 0;
	}

	/** バカ舌値を上下限にクランプする. */
	public void checkTang() {
		if (body.getTang() < 0)
			body.setTang(0);
		if (body.getTang() > body.getTangLevelBase()[2])
			body.setTang(body.getTangLevelBase()[2]);
	}
}
