package src.base;

/**
 * BodyAttributes の配列型の統計パラメータをまとめた値オブジェクト.
 * 種族別の各種上限値や確率配列を保持する.
 */
public class BodyStatProfile implements java.io.Serializable {
	private static final long serialVersionUID = -3459479387085400798L;

	/** 一回の食事量 */
	private int eatAmountBase[] = { 100 * 6, 100 * 12, 100 * 24 };
	/** 体重 */
	private int weightBase[] = { 100, 300, 600 };
	/** 空腹限界 */
	private int hungryLimitBase[] = { 100 * 24, 100 * 24 * 2, 100 * 24 * 4 };
	/** うんうん限界 */
	private int shitLimitBase[] = { 100 * 12, 100 * 24, 100 * 24 };
	/** ダメージ限界 */
	private int damageLimitBase[] = { 100 * 24, 100 * 24 * 3, 100 * 24 * 7 };
	/** ストレス限界 */
	private int stressLimitBase[] = { 100 * 24, 100 * 24 * 3, 100 * 24 * 7 };
	/** 味覚レベル */
	private int tangLevelBase[] = { 300, 600, 1000 };
	/** 足の速さ */
	private int stepBase[] = { 1, 2, 4 };
	/** 赤ゆ、子ゆ、成ゆの攻撃力 */
	private int strengthBase[] = { 500, 1000, 3000 };
	/** 免疫力(左から順に赤ゆ、子ゆ、成ゆ、老ゆ) */
	private int immunity[] = { 1, 2, 3, 0 };
	/** ゲスポイントの下限 */
	private int rudeLimit[] = { -100, -250 };
	/** 善良限界 */
	private int niceLimit[] = { 100, 500 };
	/** 自主洗浄失敗確率 - 賢い [0]:赤ゆ [1]:子ゆ [2]:成ゆ */
	private int[] cleaningFailProbWise = { 10, 5, 2 };
	/** 自主洗浄失敗確率 - 普通 [0]:赤ゆ [1]:子ゆ [2]:成ゆ */
	private int[] cleaningFailProbAverage = { 25, 8, 3 };
	/** 自主洗浄失敗確率 - 餡子脳 [0]:赤ゆ [1]:子ゆ [2]:成ゆ */
	private int[] cleaningFailProbFool = { 50, 10, 5 };

	/**
	 * BodyStatProfile を生成する.
	 */
	public BodyStatProfile() {
	}

	/**
	 * 他インスタンスの内容を深く複製する.
	 *
	 * @param from 複製元
	 */
	public void copyFrom(BodyStatProfile from) {
		if (from == null) {
			return;
		}
		eatAmountBase = copyArray(from.eatAmountBase);
		weightBase = copyArray(from.weightBase);
		hungryLimitBase = copyArray(from.hungryLimitBase);
		shitLimitBase = copyArray(from.shitLimitBase);
		damageLimitBase = copyArray(from.damageLimitBase);
		stressLimitBase = copyArray(from.stressLimitBase);
		tangLevelBase = copyArray(from.tangLevelBase);
		stepBase = copyArray(from.stepBase);
		strengthBase = copyArray(from.strengthBase);
		immunity = copyArray(from.immunity);
		rudeLimit = copyArray(from.rudeLimit);
		niceLimit = copyArray(from.niceLimit);
		cleaningFailProbWise = copyArray(from.cleaningFailProbWise);
		cleaningFailProbAverage = copyArray(from.cleaningFailProbAverage);
		cleaningFailProbFool = copyArray(from.cleaningFailProbFool);
	}

	/**
	 * 現在値の複製を作る.
	 *
	 * @return 複製済みインスタンス
	 */
	public BodyStatProfile copy() {
		BodyStatProfile ret = new BodyStatProfile();
		ret.copyFrom(this);
		return ret;
	}

	private static int[] copyArray(int[] src) {
		return src == null ? null : src.clone();
	}

	public int[] getEatAmountBase() {
		return eatAmountBase;
	}

	public void setEatAmountBase(int[] eatAmount) {
		eatAmountBase = eatAmount;
	}

	public int[] getWeightBase() {
		return weightBase;
	}

	public void setWeightBase(int[] weight) {
		weightBase = weight;
	}

	public int[] getHungryLimitBase() {
		return hungryLimitBase;
	}

	public void setHungryLimitBase(int[] hungryLimit) {
		hungryLimitBase = hungryLimit;
	}

	public int[] getShitLimitBase() {
		return shitLimitBase;
	}

	public void setShitLimitBase(int[] shitLimit) {
		shitLimitBase = shitLimit;
	}

	public int[] getDamageLimitBase() {
		return damageLimitBase;
	}

	public void setDamageLimitBase(int[] damageLimit) {
		damageLimitBase = damageLimit;
	}

	public int[] getStressLimitBase() {
		return stressLimitBase;
	}

	public void setStressLimitBase(int[] stressLimit) {
		stressLimitBase = stressLimit;
	}

	public int[] getTangLevelBase() {
		return tangLevelBase;
	}

	public void setTangLevelBase(int[] tangLevel) {
		tangLevelBase = tangLevel;
	}

	public int[] getStepBase() {
		return stepBase;
	}

	public void setStepBase(int[] step) {
		stepBase = step;
	}

	public int[] getStrengthBase() {
		return strengthBase;
	}

	public void setStrengthBase(int[] strength) {
		strengthBase = strength;
	}

	public int[] getImmunity() {
		return immunity;
	}

	public void setImmunity(int[] immunity) {
		this.immunity = immunity;
	}

	public int[] getRudeLimit() {
		return rudeLimit;
	}

	public void setRudeLimit(int[] rudeLimit) {
		this.rudeLimit = rudeLimit;
	}

	public int[] getNiceLimit() {
		return niceLimit;
	}

	public void setNiceLimit(int[] niceLimit) {
		this.niceLimit = niceLimit;
	}

	public int[] getCleaningFailProbWise() {
		return cleaningFailProbWise;
	}

	public void setCleaningFailProbWise(int[] cleaningFailProbWise) {
		this.cleaningFailProbWise = cleaningFailProbWise;
	}

	public int[] getCleaningFailProbAverage() {
		return cleaningFailProbAverage;
	}

	public void setCleaningFailProbAverage(int[] cleaningFailProbAverage) {
		this.cleaningFailProbAverage = cleaningFailProbAverage;
	}

	public int[] getCleaningFailProbFool() {
		return cleaningFailProbFool;
	}

	public void setCleaningFailProbFool(int[] cleaningFailProbFool) {
		this.cleaningFailProbFool = cleaningFailProbFool;
	}
}
