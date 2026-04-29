package src.base;

/**
 * BodyAttributes の配列型の統計パラメータをまとめた値オブジェクト.
 * 種族別の各種上限値や確率配列を保持する.
 */
public class BodyStatProfile implements java.io.Serializable {
	private static final long serialVersionUID = -3459479387085400798L;

	/** 一回の食事量 */
	private int EATAMOUNTorg[] = { 100 * 6, 100 * 12, 100 * 24 };
	/** 体重 */
	private int WEIGHTorg[] = { 100, 300, 600 };
	/** 空腹限界 */
	private int HUNGRYLIMITorg[] = { 100 * 24, 100 * 24 * 2, 100 * 24 * 4 };
	/** うんうん限界 */
	private int SHITLIMITorg[] = { 100 * 12, 100 * 24, 100 * 24 };
	/** ダメージ限界 */
	private int DAMAGELIMITorg[] = { 100 * 24, 100 * 24 * 3, 100 * 24 * 7 };
	/** ストレス限界 */
	private int STRESSLIMITorg[] = { 100 * 24, 100 * 24 * 3, 100 * 24 * 7 };
	/** 味覚レベル */
	private int TANGLEVELorg[] = { 300, 600, 1000 };
	/** 足の速さ */
	private int STEPorg[] = { 1, 2, 4 };
	/** 赤ゆ、子ゆ、成ゆの攻撃力 */
	private int STRENGTHorg[] = { 500, 1000, 3000 };
	/** 免疫力(左から順に赤ゆ、子ゆ、成ゆ、老ゆ) */
	private int immunity[] = { 1, 2, 3, 0 };
	/** ゲスポイントの下限 */
	private int RudeLimit[] = { -100, -250 };
	/** 善良限界 */
	private int NiceLimit[] = { 100, 500 };
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
		EATAMOUNTorg = copyArray(from.EATAMOUNTorg);
		WEIGHTorg = copyArray(from.WEIGHTorg);
		HUNGRYLIMITorg = copyArray(from.HUNGRYLIMITorg);
		SHITLIMITorg = copyArray(from.SHITLIMITorg);
		DAMAGELIMITorg = copyArray(from.DAMAGELIMITorg);
		STRESSLIMITorg = copyArray(from.STRESSLIMITorg);
		TANGLEVELorg = copyArray(from.TANGLEVELorg);
		STEPorg = copyArray(from.STEPorg);
		STRENGTHorg = copyArray(from.STRENGTHorg);
		immunity = copyArray(from.immunity);
		RudeLimit = copyArray(from.RudeLimit);
		NiceLimit = copyArray(from.NiceLimit);
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

	public int[] getEATAMOUNTorg() {
		return EATAMOUNTorg;
	}

	public void setEATAMOUNTorg(int[] eATAMOUNTorg) {
		EATAMOUNTorg = eATAMOUNTorg;
	}

	public int[] getWEIGHTorg() {
		return WEIGHTorg;
	}

	public void setWEIGHTorg(int[] wEIGHTorg) {
		WEIGHTorg = wEIGHTorg;
	}

	public int[] getHUNGRYLIMITorg() {
		return HUNGRYLIMITorg;
	}

	public void setHUNGRYLIMITorg(int[] hUNGRYLIMITorg) {
		HUNGRYLIMITorg = hUNGRYLIMITorg;
	}

	public int[] getSHITLIMITorg() {
		return SHITLIMITorg;
	}

	public void setSHITLIMITorg(int[] sHITLIMITorg) {
		SHITLIMITorg = sHITLIMITorg;
	}

	public int[] getDAMAGELIMITorg() {
		return DAMAGELIMITorg;
	}

	public void setDAMAGELIMITorg(int[] dAMAGELIMITorg) {
		DAMAGELIMITorg = dAMAGELIMITorg;
	}

	public int[] getSTRESSLIMITorg() {
		return STRESSLIMITorg;
	}

	public void setSTRESSLIMITorg(int[] sTRESSLIMITorg) {
		STRESSLIMITorg = sTRESSLIMITorg;
	}

	public int[] getTANGLEVELorg() {
		return TANGLEVELorg;
	}

	public void setTANGLEVELorg(int[] tANGLEVELorg) {
		TANGLEVELorg = tANGLEVELorg;
	}

	public int[] getSTEPorg() {
		return STEPorg;
	}

	public void setSTEPorg(int[] sTEPorg) {
		STEPorg = sTEPorg;
	}

	public int[] getSTRENGTHorg() {
		return STRENGTHorg;
	}

	public void setSTRENGTHorg(int[] sTRENGTHorg) {
		STRENGTHorg = sTRENGTHorg;
	}

	public int[] getImmunity() {
		return immunity;
	}

	public void setImmunity(int[] immunity) {
		this.immunity = immunity;
	}

	public int[] getRudeLimit() {
		return RudeLimit;
	}

	public void setRudeLimit(int[] rudeLimit) {
		RudeLimit = rudeLimit;
	}

	public int[] getNiceLimit() {
		return NiceLimit;
	}

	public void setNiceLimit(int[] niceLimit) {
		NiceLimit = niceLimit;
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
