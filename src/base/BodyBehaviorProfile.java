package src.base;

/**
 * BodyAttributes の行動・性格・繁殖寄りの調整値をまとめた値オブジェクト.
 * なつき度、事故確率、妊娠関連の単体値を保持する.
 */
public class BodyBehaviorProfile implements java.io.Serializable {
	private static final long serialVersionUID = 2065720843157054021L;

	/** なつき度限界 */
	private int lovePlayerLimitBase = 1000;
	/** 攻撃された際のぴこぴこ破壊確率。0だと破壊されない */
	private int braidBreakChance = 0;
	/** 何回のうち1回の確率ですりすり事故で妊娠するかの値 */
	private int surisuriAccidentProb = 200;
	/** 何回のうち1回の確率で路上で車に轢かれるかの値 */
	private int carAccidentProb = 10000;
	/** 何回のうち1回の確率であんよが傷ついているとあんよが破壊されるかの確率 */
	private int breakBodyByShitProb = 100;
	/** 何回のうち1回の確率で苦いフードを食べた際にゆ下痢になるかの確率 */
	private int diarrheaProb = 5;
	/** 何回のうち１回の確率で発情するかの確率 */
	private int exciteProb = 1;
	/** 性格変化の切り替え */
	private boolean notChangeCharacter = false;
	/** ゲスポイント */
	private int attitudePoint = 0;
	/** 同一方向に動き続ける */
	private int sameDirectionFactor = 30;
	/** 固有の免疫力（個体値。これは仮） */
	private int immunityStrength = 1;
	/** 妊娠限界 */
	private int pregnantLimit = 1000;
	/** よりリアルな妊娠限界 */
	private boolean useRealPregnantLimit = true;

	/**
	 * BodyBehaviorProfile を生成する.
	 */
	public BodyBehaviorProfile() {
	}

	/**
	 * 他インスタンスの内容を複製する.
	 *
	 * @param from 複製元
	 */
	public void copyFrom(BodyBehaviorProfile from) {
		if (from == null) {
			return;
		}
		lovePlayerLimitBase = from.lovePlayerLimitBase;
		braidBreakChance = from.braidBreakChance;
		surisuriAccidentProb = from.surisuriAccidentProb;
		carAccidentProb = from.carAccidentProb;
		breakBodyByShitProb = from.breakBodyByShitProb;
		diarrheaProb = from.diarrheaProb;
		exciteProb = from.exciteProb;
		notChangeCharacter = from.notChangeCharacter;
		attitudePoint = from.attitudePoint;
		sameDirectionFactor = from.sameDirectionFactor;
		immunityStrength = from.immunityStrength;
		pregnantLimit = from.pregnantLimit;
		useRealPregnantLimit = from.useRealPregnantLimit;
	}

	/**
	 * 現在値の複製を作る.
	 *
	 * @return 複製済みインスタンス
	 */
	public BodyBehaviorProfile copy() {
		BodyBehaviorProfile ret = new BodyBehaviorProfile();
		ret.copyFrom(this);
		return ret;
	}

	public int getLovePlayerLimitBase() {
		return lovePlayerLimitBase;
	}

	public void setLovePlayerLimitBase(int lovePlayerLimitBase) {
		this.lovePlayerLimitBase = lovePlayerLimitBase;
	}

	public int getBraidBreakChance() {
		return braidBreakChance;
	}

	public void setBraidBreakChance(int braidBreakChance) {
		this.braidBreakChance = braidBreakChance;
	}

	public int getSurisuriAccidentProb() {
		return surisuriAccidentProb;
	}

	public void setSurisuriAccidentProb(int surisuriAccidentProb) {
		this.surisuriAccidentProb = surisuriAccidentProb;
	}

	public int getCarAccidentProb() {
		return carAccidentProb;
	}

	public void setCarAccidentProb(int carAccidentProb) {
		this.carAccidentProb = carAccidentProb;
	}

	public int getBreakBodyByShitProb() {
		return breakBodyByShitProb;
	}

	public void setBreakBodyByShitProb(int breakBodyByShitProb) {
		this.breakBodyByShitProb = breakBodyByShitProb;
	}

	public int getDiarrheaProb() {
		return diarrheaProb;
	}

	public void setDiarrheaProb(int diarrheaProb) {
		this.diarrheaProb = diarrheaProb;
	}

	public int getExciteProb() {
		return exciteProb;
	}

	public void setExciteProb(int exciteProb) {
		this.exciteProb = exciteProb;
	}

	public boolean isNotChangeCharacter() {
		return notChangeCharacter;
	}

	public void setNotChangeCharacter(boolean notChangeCharacter) {
		this.notChangeCharacter = notChangeCharacter;
	}

	public int getAttitudePoint() {
		return attitudePoint;
	}

	public void setAttitudePoint(int attitudePoint) {
		this.attitudePoint = attitudePoint;
	}

	public int getSameDirectionFactor() {
		return sameDirectionFactor;
	}

	public void setSameDirectionFactor(int sameDirectionFactor) {
		this.sameDirectionFactor = sameDirectionFactor;
	}

	public int getImmunityStrength() {
		return immunityStrength;
	}

	public void setImmunityStrength(int immunityStrength) {
		this.immunityStrength = immunityStrength;
	}

	public int getPregnantLimit() {
		return pregnantLimit;
	}

	public void setPregnantLimit(int pregnantLimit) {
		this.pregnantLimit = pregnantLimit;
	}

	public boolean isUseRealPregnantLimit() {
		return useRealPregnantLimit;
	}

	public void setUseRealPregnantLimit(boolean useRealPregnantLimit) {
		this.useRealPregnantLimit = useRealPregnantLimit;
	}
}
