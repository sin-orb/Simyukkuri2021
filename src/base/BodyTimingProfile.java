package src.base;

/**
 * BodyAttributes の時刻・閾値系パラメータをまとめた値オブジェクト.
 * 成長期間、怒り/恐怖期間、視界などを保持する.
 */
public class BodyTimingProfile implements java.io.Serializable {
	private static final long serialVersionUID = -7023557212791696945L;

	/** 赤ゆ期間 */
	private int BABYLIMITorg = 100 * 24 * 7;
	/** 子ゆ期間 */
	private int CHILDLIMITorg = 100 * 24 * 21;
	/** 寿命 */
	private int LIFELIMITorg = 100 * 24 * 365;
	/** 腐敗日数 */
	private int ROTTINGTIMEorg = 100 * 24 * 3;
	/** リラックス状態の期間 */
	private int RELAXPERIODorg = 100 * 1;
	/** 発情状態の期間 */
	private int EXCITEPERIODorg = 100 * 3;
	/** 妊娠期間 */
	private int PREGPERIODorg = 100 * 24;
	/** 睡眠時間 */
	private int SLEEPPERIODorg = 100 * 3;
	/** アクティブな期間 */
	private int ACTIVEPERIODorg = 100 * 6;
	/** 怒り期間 */
	private int ANGRYPERIODorg = 100 * 1;
	/** 恐怖期間 */
	private int SCAREPERIODorg = 100 * 1;
	/** ゲーム内12分、衝動の抑制のための変数 */
	private int DECLINEPERIODorg = 20;
	/** 壁等にブロックされた回数の限界（怒りだす等） */
	private int BLOCKEDLIMITorg = 60;
	/** 汚れ限界（超えるとゆかび状態） */
	private int DIRTYPERIODorg = 300;
	/** 視界の広さ */
	private int EYESIGHTorg = 4000 * 4000;
	/** ゆかびの潜伏期間 */
	private int INCUBATIONPERIODorg = 100 * 12;

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
		BABYLIMITorg = from.BABYLIMITorg;
		CHILDLIMITorg = from.CHILDLIMITorg;
		LIFELIMITorg = from.LIFELIMITorg;
		ROTTINGTIMEorg = from.ROTTINGTIMEorg;
		RELAXPERIODorg = from.RELAXPERIODorg;
		EXCITEPERIODorg = from.EXCITEPERIODorg;
		PREGPERIODorg = from.PREGPERIODorg;
		SLEEPPERIODorg = from.SLEEPPERIODorg;
		ACTIVEPERIODorg = from.ACTIVEPERIODorg;
		ANGRYPERIODorg = from.ANGRYPERIODorg;
		SCAREPERIODorg = from.SCAREPERIODorg;
		DECLINEPERIODorg = from.DECLINEPERIODorg;
		BLOCKEDLIMITorg = from.BLOCKEDLIMITorg;
		DIRTYPERIODorg = from.DIRTYPERIODorg;
		EYESIGHTorg = from.EYESIGHTorg;
		INCUBATIONPERIODorg = from.INCUBATIONPERIODorg;
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

	public int getBABYLIMITorg() {
		return BABYLIMITorg;
	}

	public void setBABYLIMITorg(int bABYLIMITorg) {
		BABYLIMITorg = bABYLIMITorg;
	}

	public int getCHILDLIMITorg() {
		return CHILDLIMITorg;
	}

	public void setCHILDLIMITorg(int cHILDLIMITorg) {
		CHILDLIMITorg = cHILDLIMITorg;
	}

	public int getLIFELIMITorg() {
		return LIFELIMITorg;
	}

	public void setLIFELIMITorg(int lIFELIMITorg) {
		LIFELIMITorg = lIFELIMITorg;
	}

	public int getROTTINGTIMEorg() {
		return ROTTINGTIMEorg;
	}

	public void setROTTINGTIMEorg(int rOTTINGTIMEorg) {
		ROTTINGTIMEorg = rOTTINGTIMEorg;
	}

	public int getRELAXPERIODorg() {
		return RELAXPERIODorg;
	}

	public void setRELAXPERIODorg(int rELAXPERIODorg) {
		RELAXPERIODorg = rELAXPERIODorg;
	}

	public int getEXCITEPERIODorg() {
		return EXCITEPERIODorg;
	}

	public void setEXCITEPERIODorg(int eXCITEPERIODorg) {
		EXCITEPERIODorg = eXCITEPERIODorg;
	}

	public int getPREGPERIODorg() {
		return PREGPERIODorg;
	}

	public void setPREGPERIODorg(int pREGPERIODorg) {
		PREGPERIODorg = pREGPERIODorg;
	}

	public int getSLEEPPERIODorg() {
		return SLEEPPERIODorg;
	}

	public void setSLEEPPERIODorg(int sLEEPPERIODorg) {
		SLEEPPERIODorg = sLEEPPERIODorg;
	}

	public int getACTIVEPERIODorg() {
		return ACTIVEPERIODorg;
	}

	public void setACTIVEPERIODorg(int aCTIVEPERIODorg) {
		ACTIVEPERIODorg = aCTIVEPERIODorg;
	}

	public int getANGRYPERIODorg() {
		return ANGRYPERIODorg;
	}

	public void setANGRYPERIODorg(int aNGRYPERIODorg) {
		ANGRYPERIODorg = aNGRYPERIODorg;
	}

	public int getSCAREPERIODorg() {
		return SCAREPERIODorg;
	}

	public void setSCAREPERIODorg(int sCAREPERIODorg) {
		SCAREPERIODorg = sCAREPERIODorg;
	}

	public int getDECLINEPERIODorg() {
		return DECLINEPERIODorg;
	}

	public void setDECLINEPERIODorg(int dECLINEPERIODorg) {
		DECLINEPERIODorg = dECLINEPERIODorg;
	}

	public int getBLOCKEDLIMITorg() {
		return BLOCKEDLIMITorg;
	}

	public void setBLOCKEDLIMITorg(int bLOCKEDLIMITorg) {
		BLOCKEDLIMITorg = bLOCKEDLIMITorg;
	}

	public int getDIRTYPERIODorg() {
		return DIRTYPERIODorg;
	}

	public void setDIRTYPERIODorg(int dIRTYPERIODorg) {
		DIRTYPERIODorg = dIRTYPERIODorg;
	}

	public int getEYESIGHTorg() {
		return EYESIGHTorg;
	}

	public void setEYESIGHTorg(int eYESIGHTorg) {
		EYESIGHTorg = eYESIGHTorg;
	}

	public int getINCUBATIONPERIODorg() {
		return INCUBATIONPERIODorg;
	}

	public void setINCUBATIONPERIODorg(int iNCUBATIONPERIODorg) {
		INCUBATIONPERIODorg = iNCUBATIONPERIODorg;
	}
}
