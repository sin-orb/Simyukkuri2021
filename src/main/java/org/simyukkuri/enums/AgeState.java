package org.simyukkuri.enums;

import org.simyukkuri.util.GameText;

/** ゆっくりの成長段階（赤ゆ/子ゆ/成ゆ） */
public enum AgeState {
	BABY(GameText.read("enums_babyyu")),
	CHILD(GameText.read("enums_childyu")),
	ADULT(GameText.read("enums_adultyu"));

	/** ゆっくりの成長段階名称 */
	private final String name;

	/**
	 * コンストラクタ.
	 * 
	 * @param str ゆっくりの成長段階名称
	 */
	AgeState(String str) {
		this.name = str;
	}

	public String getName() {
		return name;
	}
}
