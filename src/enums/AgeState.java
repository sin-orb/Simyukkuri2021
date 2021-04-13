package src.enums;

/** ゆっくりの成長段階（赤ゆ/子ゆ/成ゆ） */
public enum AgeState {
	BABY("赤ゆ"),
	CHILD("子ゆ"),
	ADULT("成ゆ")
	;
	/** ゆっくりの成長段階名称 */
	public String name;
	/**
	 * コンストラクタ.
	 * @param str ゆっくりの成長段階名称
	 */
	AgeState(String str) { this.name = str; }
}
