package src.entity.living.profile;

/**
 * BodyAttributes の時刻・閾値系パラメータをまとめた値オブジェクト.
 * 成長期間、怒り/恐怖期間、視界などを保持する.
 */
public class BodyTimingProfile implements java.io.Serializable {
	private static final long serialVersionUID = -7023557212791696945L;

	/** 赤ゆ期間 */
	private int babyLimitBase = 100 * 24 * 7;
	/** 子ゆ期間 */
	private int childLimitBase = 100 * 24 * 21;
	/** 寿命 */
	private int lifeLimitBase = 100 * 24 * 365;
	/** 腐敗日数 */
	private int rottingTimeBase = 100 * 24 * 3;
	/** リラックス状態の期間 */
	private int relaxPeriodBase = 100 * 1;
	/** 発情状態の期間 */
	private int excitePeriodBase = 100 * 3;
	/** 妊娠期間 */
	private int pregPeriodBase = 100 * 24;
	/** 睡眠時間 */
	private int sleepPeriodBase = 100 * 3;
	/** アクティブな期間 */
	private int activePeriodBase = 100 * 6;
	/** 怒り期間 */
	private int angryPeriodBase = 100 * 1;
	/** 恐怖期間 */
	private int scarePeriodBase = 100 * 1;
	/** ゲーム内12分、衝動の抑制のための変数 */
	private int declinePeriodBase = 20;
	/** 壁等にブロックされた回数の限界（怒りだす等） */
	private int blockedLimitBase = 60;
	/** 汚れ限界（超えるとゆかび状態） */
	private int dirtyPeriodBase = 300;
	/** 視界の広さ */
	private int eyesightBase = 4000 * 4000;
	/** ゆかびの潜伏期間 */
	private int incubationPeriodBase = 100 * 12;

	/** BodyTimingProfile を生成する. */
	public BodyTimingProfile() {
	}

	/**
	 * 他インスタンスの内容を複製する.
	 *
	 * @param from 複製元
	 */
	public void copyFrom(BodyTimingProfile from) {
		if (from == null) {
			return;
		}
		babyLimitBase = from.babyLimitBase;
		childLimitBase = from.childLimitBase;
		lifeLimitBase = from.lifeLimitBase;
		rottingTimeBase = from.rottingTimeBase;
		relaxPeriodBase = from.relaxPeriodBase;
		excitePeriodBase = from.excitePeriodBase;
		pregPeriodBase = from.pregPeriodBase;
		sleepPeriodBase = from.sleepPeriodBase;
		activePeriodBase = from.activePeriodBase;
		angryPeriodBase = from.angryPeriodBase;
		scarePeriodBase = from.scarePeriodBase;
		declinePeriodBase = from.declinePeriodBase;
		blockedLimitBase = from.blockedLimitBase;
		dirtyPeriodBase = from.dirtyPeriodBase;
		eyesightBase = from.eyesightBase;
		incubationPeriodBase = from.incubationPeriodBase;
	}

	/**
	 * 現在値の複製を作る.
	 *
	 * @return 複製済みインスタンス
	 */
	public BodyTimingProfile copy() {
		BodyTimingProfile ret = new BodyTimingProfile();
		ret.copyFrom(this);
		return ret;
	}

	public int getBabyLimitBase() {
		return babyLimitBase;
	}

	public void setBabyLimitBase(int babyLimit) {
		babyLimitBase = babyLimit;
	}

	public int getChildLimitBase() {
		return childLimitBase;
	}

	public void setChildLimitBase(int childLimit) {
		childLimitBase = childLimit;
	}

	public int getLifeLimitBase() {
		return lifeLimitBase;
	}

	public void setLifeLimitBase(int lifeLimit) {
		lifeLimitBase = lifeLimit;
	}

	public int getRottingTimeBase() {
		return rottingTimeBase;
	}

	public void setRottingTimeBase(int rottingTime) {
		rottingTimeBase = rottingTime;
	}

	public int getRelaxPeriodBase() {
		return relaxPeriodBase;
	}

	public void setRelaxPeriodBase(int relaxPeriod) {
		relaxPeriodBase = relaxPeriod;
	}

	public int getExcitePeriodBase() {
		return excitePeriodBase;
	}

	public void setExcitePeriodBase(int excitePeriod) {
		excitePeriodBase = excitePeriod;
	}

	public int getPregPeriodBase() {
		return pregPeriodBase;
	}

	public void setPregPeriodBase(int pregPeriod) {
		pregPeriodBase = pregPeriod;
	}

	public int getSleepPeriodBase() {
		return sleepPeriodBase;
	}

	public void setSleepPeriodBase(int sleepPeriod) {
		sleepPeriodBase = sleepPeriod;
	}

	public int getActivePeriodBase() {
		return activePeriodBase;
	}

	public void setActivePeriodBase(int activePeriod) {
		activePeriodBase = activePeriod;
	}

	public int getAngryPeriodBase() {
		return angryPeriodBase;
	}

	public void setAngryPeriodBase(int angryPeriod) {
		angryPeriodBase = angryPeriod;
	}

	public int getScarePeriodBase() {
		return scarePeriodBase;
	}

	public void setScarePeriodBase(int scarePeriod) {
		scarePeriodBase = scarePeriod;
	}

	public int getDeclinePeriodBase() {
		return declinePeriodBase;
	}

	public void setDeclinePeriodBase(int declinePeriod) {
		declinePeriodBase = declinePeriod;
	}

	public int getBlockedLimitBase() {
		return blockedLimitBase;
	}

	public void setBlockedLimitBase(int blockedLimit) {
		blockedLimitBase = blockedLimit;
	}

	public int getDirtyPeriodBase() {
		return dirtyPeriodBase;
	}

	public void setDirtyPeriodBase(int dirtyPeriod) {
		dirtyPeriodBase = dirtyPeriod;
	}

	public int getEyesightBase() {
		return eyesightBase;
	}

	public void setEyesightBase(int eyesight) {
		eyesightBase = eyesight;
	}

	public int getIncubationPeriodBase() {
		return incubationPeriodBase;
	}

	public void setIncubationPeriodBase(int incubationPeriod) {
		incubationPeriodBase = incubationPeriod;
	}
}
