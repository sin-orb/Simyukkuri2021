package src.enums;

import src.system.ResourceUtil;

/** ゆっくりの成長段階（赤ゆ/子ゆ/成ゆ） */
public enum AgeState {
	BABY(ResourceUtil.getInstance().read("enums_babyyu")),
	CHILD(ResourceUtil.getInstance().read("enums_childyu")),
	ADULT(ResourceUtil.getInstance().read("enums_adultyu"))
	;
	/** ゆっくりの成長段階名称 */
	private final String name;
	/**
	 * コンストラクタ.
	 * @param str ゆっくりの成長段階名称
	 */
	AgeState(String str) { this.name = str; }

	public String getName() {
		return name;
	}
}
