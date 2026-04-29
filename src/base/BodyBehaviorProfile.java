package src.base;

/**
 * BodyAttributes の行動・性格・繁殖寄りの調整値をまとめた値オブジェクト.
 * なつき度、事故確率、妊娠関連の単体値を保持する.
 */
public class BodyBehaviorProfile implements java.io.Serializable {
	private static final long serialVersionUID = 2065720843157054021L;

	/** なつき度限界 */
	private int LOVEPLAYERLIMITorg = 1000;
	/** 攻撃された際のぴこぴこ破壊確率。0だと破壊されない */
	private int nBreakBraidRand = 0;
	/** 何回のうち1回の確率ですりすり事故で妊娠するかの値 */
	private int SurisuriAccidentProb = 200;
	/** 何回のうち1回の確率で路上で車に轢かれるかの値 */
	private int CarAccidentProb = 10000;
	/** 何回のうち1回の確率であんよが傷ついているとあんよが破壊されるかの確率 */
	private int BreakBodyByShitProb = 100;
	/** 何回のうち1回の確率で苦いフードを食べた際にゆ下痢になるかの確率 */
	private int diarrheaProb = 5;
	/** 何回のうち１回の確率で発情するかの確率 */
	private int exciteProb = 1;
	/** 性格変化の切り替え */
	private boolean notChangeCharacter = false;
	/** ゲスポイント */
	private int AttitudePoint = 0;
	/** 同一方向に動き続ける */
	private int sameDest = 30;
	/** 固有の免疫力（個体値。これは仮） */
	private int ROBUSTNESS = 1;
	/** 妊娠限界 */
	private int PregnantLimit = 1000;
	/** よりリアルな妊娠限界 */
	private boolean realPregnantLimit = true;

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
		LOVEPLAYERLIMITorg = from.LOVEPLAYERLIMITorg;
		nBreakBraidRand = from.nBreakBraidRand;
		SurisuriAccidentProb = from.SurisuriAccidentProb;
		CarAccidentProb = from.CarAccidentProb;
		BreakBodyByShitProb = from.BreakBodyByShitProb;
		diarrheaProb = from.diarrheaProb;
		exciteProb = from.exciteProb;
		notChangeCharacter = from.notChangeCharacter;
		AttitudePoint = from.AttitudePoint;
		sameDest = from.sameDest;
		ROBUSTNESS = from.ROBUSTNESS;
		PregnantLimit = from.PregnantLimit;
		realPregnantLimit = from.realPregnantLimit;
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

	public int getLOVEPLAYERLIMITorg() {
		return LOVEPLAYERLIMITorg;
	}

	public void setLOVEPLAYERLIMITorg(int lOVEPLAYERLIMITorg) {
		LOVEPLAYERLIMITorg = lOVEPLAYERLIMITorg;
	}

	public int getnBreakBraidRand() {
		return nBreakBraidRand;
	}

	public void setnBreakBraidRand(int nBreakBraidRand) {
		this.nBreakBraidRand = nBreakBraidRand;
	}

	public int getSurisuriAccidentProb() {
		return SurisuriAccidentProb;
	}

	public void setSurisuriAccidentProb(int surisuriAccidentProb) {
		SurisuriAccidentProb = surisuriAccidentProb;
	}

	public int getCarAccidentProb() {
		return CarAccidentProb;
	}

	public void setCarAccidentProb(int carAccidentProb) {
		CarAccidentProb = carAccidentProb;
	}

	public int getBreakBodyByShitProb() {
		return BreakBodyByShitProb;
	}

	public void setBreakBodyByShitProb(int breakBodyByShitProb) {
		BreakBodyByShitProb = breakBodyByShitProb;
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
		return AttitudePoint;
	}

	public void setAttitudePoint(int attitudePoint) {
		AttitudePoint = attitudePoint;
	}

	public int getSameDest() {
		return sameDest;
	}

	public void setSameDest(int sameDest) {
		this.sameDest = sameDest;
	}

	public int getROBUSTNESS() {
		return ROBUSTNESS;
	}

	public void setROBUSTNESS(int rOBUSTNESS) {
		ROBUSTNESS = rOBUSTNESS;
	}

	public int getPregnantLimit() {
		return PregnantLimit;
	}

	public void setPregnantLimit(int pregnantLimit) {
		PregnantLimit = pregnantLimit;
	}

	public boolean isRealPregnantLimit() {
		return realPregnantLimit;
	}

	public void setRealPregnantLimit(boolean realPregnantLimit) {
		this.realPregnantLimit = realPregnantLimit;
	}
}
