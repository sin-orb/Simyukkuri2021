package src.base;

/**
 * BodyAttributes の名前関連データをまとめた値オブジェクト.
 * ベース画像名と、年齢別の通常名/ダメージ名を保持する.
 */
public class BodyNameSet implements java.io.Serializable {
	private static final long serialVersionUID = -5360581391282465987L;

	/** 各ゆっくりに特有の画像読み込みのためのファイル名 */
	private String baseBodyFileName;
	/** 赤ゆの一人称 */
	private String[] anBabyName;
	/** 子ゆの一人称 */
	private String[] anChildName;
	/** 大人ゆの一人称 */
	private String[] anAdultName;
	/** [0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称 */
	private String[] anMyName;
	/** 赤ゆの一人称（ダメージ時） */
	private String[] anBabyNameD;
	/** 子ゆの一人称（ダメージ時） */
	private String[] anChildNameD;
	/** 大人ゆの一人称（ダメージ時） */
	private String[] anAdultNameD;
	/** ダメージ時の、[0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称 */
	private String[] anMyNameD;

	/** BodyNameSet を生成する. */
	public BodyNameSet() {
		anMyName = new String[3];
		anMyNameD = new String[3];
	}

	/**
	 * 他インスタンスの内容を深く複製する.
	 *
	 * @param from 複製元
	 */
	public void copyFrom(BodyNameSet from) {
		if (from == null) {
			return;
		}
		baseBodyFileName = from.baseBodyFileName;
		anBabyName = copyArray(from.anBabyName);
		anChildName = copyArray(from.anChildName);
		anAdultName = copyArray(from.anAdultName);
		anMyName = copyArray(from.anMyName);
		anBabyNameD = copyArray(from.anBabyNameD);
		anChildNameD = copyArray(from.anChildNameD);
		anAdultNameD = copyArray(from.anAdultNameD);
		anMyNameD = copyArray(from.anMyNameD);
	}

	/**
	 * 現在値の複製を作る.
	 *
	 * @return 複製済みインスタンス
	 */
	public BodyNameSet copy() {
		BodyNameSet ret = new BodyNameSet();
		ret.copyFrom(this);
		return ret;
	}

	private static String[] copyArray(String[] src) {
		return src == null ? null : src.clone();
	}

	public String getBaseBodyFileName() {
		return baseBodyFileName;
	}

	public void setBaseBodyFileName(String baseBodyFileName) {
		this.baseBodyFileName = baseBodyFileName;
	}

	public String[] getAnBabyName() {
		return anBabyName;
	}

	public void setAnBabyName(String[] anBabyName) {
		this.anBabyName = anBabyName;
	}

	public String[] getAnChildName() {
		return anChildName;
	}

	public void setAnChildName(String[] anChildName) {
		this.anChildName = anChildName;
	}

	public String[] getAnAdultName() {
		return anAdultName;
	}

	public void setAnAdultName(String[] anAdultName) {
		this.anAdultName = anAdultName;
	}

	public String[] getAnMyName() {
		return anMyName;
	}

	public void setAnMyName(String[] anMyName) {
		this.anMyName = anMyName;
	}

	public String[] getAnBabyNameD() {
		return anBabyNameD;
	}

	public void setAnBabyNameD(String[] anBabyNameD) {
		this.anBabyNameD = anBabyNameD;
	}

	public String[] getAnChildNameD() {
		return anChildNameD;
	}

	public void setAnChildNameD(String[] anChildNameD) {
		this.anChildNameD = anChildNameD;
	}

	public String[] getAnAdultNameD() {
		return anAdultNameD;
	}

	public void setAnAdultNameD(String[] anAdultNameD) {
		this.anAdultNameD = anAdultNameD;
	}

	public String[] getAnMyNameD() {
		return anMyNameD;
	}

	public void setAnMyNameD(String[] anMyNameD) {
		this.anMyNameD = anMyNameD;
	}
}
