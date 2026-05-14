package src.entity.core.living;

/**
 * ゆっくりの体調タイマー（汚れ進行・死ねない期間）をまとめた委譲クラス.
 */
public final class LivingBodyConditionDelegate {
	private final LivingEntity body;

	public LivingBodyConditionDelegate(LivingEntity body) {
		this.body = body;
	}

	/** 汚れ期間を湿度・濡れ・頑固汚れに応じて進める. */
	public void advanceDirtyPeriod(boolean humid, boolean wetOrMelt, boolean stubbornlyDirty) {
		if (humid) {
			body.dirtyPeriod += LivingEntity.TICK * 4;
		} else {
			body.dirtyPeriod += LivingEntity.TICK;
		}
		if (wetOrMelt) {
			body.dirtyPeriod += LivingEntity.TICK;
		}
		if (stubbornlyDirty) {
			body.dirtyPeriod += LivingEntity.TICK;
		}
	}

	/** 汚れからゆかびへ進行させる. @return ゆかびに進行したか */
	public boolean promoteDirtyToSickIfNeeded() {
		if (body.dirtyPeriod > body.getDirtyPeriodBase()) {
			body.addSickPeriod(100);
			body.dirtyPeriod = 0;
			return true;
		}
		return false;
	}

	/** 死ねない期間を1TICK減らす. */
	public void checkCantDie() {
		if (body.cantDiePeriod > 0) {
			body.cantDiePeriod -= LivingEntity.TICK;
		}
	}
}
