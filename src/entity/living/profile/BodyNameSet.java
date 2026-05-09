package src.entity.living.profile;

/**
 * BodyAttributes の名前関連データをまとめた値オブジェクト.
 * ベース画像名と、年齢別の通常名/ダメージ名を保持する.
 */
public class BodyNameSet implements java.io.Serializable {
	private static final long serialVersionUID = -5360581391282465987L;

	/** 各ゆっくりに特有の画像読み込みのためのファイル名 */
	private String baseBodyFileName;
	/** 赤ゆの一人称 */
	private String[] babyNames;
	/** 子ゆの一人称 */
	private String[] childNames;
	/** 大人ゆの一人称 */
	private String[] adultNames;
	/** [0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称 */
	private String[] myNames;
	/** 赤ゆの一人称（ダメージ時） */
	private String[] babyNamesDamaged;
	/** 子ゆの一人称（ダメージ時） */
	private String[] childNamesDamaged;
	/** 大人ゆの一人称（ダメージ時） */
	private String[] adultNamesDamaged;
	/** ダメージ時の、[0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称 */
	private String[] myNamesDamaged;

	/** BodyNameSet を生成する. */
	public BodyNameSet() {
		myNames = new String[3];
		myNamesDamaged = new String[3];
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
		babyNames = copyArray(from.babyNames);
		childNames = copyArray(from.childNames);
		adultNames = copyArray(from.adultNames);
		myNames = copyArray(from.myNames);
		babyNamesDamaged = copyArray(from.babyNamesDamaged);
		childNamesDamaged = copyArray(from.childNamesDamaged);
		adultNamesDamaged = copyArray(from.adultNamesDamaged);
		myNamesDamaged = copyArray(from.myNamesDamaged);
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

	public String[] getBabyNames() {
		return babyNames;
	}

	public void setBabyNames(String[] babyNames) {
		this.babyNames = babyNames;
	}

	public String[] getChildNames() {
		return childNames;
	}

	public void setChildNames(String[] childNames) {
		this.childNames = childNames;
	}

	public String[] getAdultNames() {
		return adultNames;
	}

	public void setAdultNames(String[] adultNames) {
		this.adultNames = adultNames;
	}

	public String[] getMyNames() {
		return myNames;
	}

	public void setMyNames(String[] myNames) {
		this.myNames = myNames;
	}

	public String[] getBabyNamesDamaged() {
		return babyNamesDamaged;
	}

	public void setBabyNamesDamaged(String[] babyNamesDamaged) {
		this.babyNamesDamaged = babyNamesDamaged;
	}

	public String[] getChildNamesDamaged() {
		return childNamesDamaged;
	}

	public void setChildNamesDamaged(String[] childNamesDamaged) {
		this.childNamesDamaged = childNamesDamaged;
	}

	public String[] getAdultNamesDamaged() {
		return adultNamesDamaged;
	}

	public void setAdultNamesDamaged(String[] adultNamesDamaged) {
		this.adultNamesDamaged = adultNamesDamaged;
	}

	public String[] getMyNamesDamaged() {
		return myNamesDamaged;
	}

	public void setMyNamesDamaged(String[] myNamesDamaged) {
		this.myNamesDamaged = myNamesDamaged;
	}

}
