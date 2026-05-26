package org.simyukkuri.system;

import java.awt.image.BufferedImage;

/**
 * ゆっくりのレイヤー画像テンポラリ.
 */
public class YukkuriLayer {
	/** 画像実体への参照 */
	private transient BufferedImage[] image;
	/** 使用する描画矩形 */
	private int[] dir;
	/** 汎用 */
	private int[] option;

	/**
	 * コンストラクタ.
	 */
	public YukkuriLayer() {
		image = new BufferedImage[10];
		dir = new int[10];
		option = new int[10];
	}

	/**
	 * レイヤー画像配列（最大10枚）を返す。
	 *
	 * @return 画像配列（transient、セーブデータには含まれない）
	 */
	public BufferedImage[] getImage() {
		return image;
	}

	/**
	 * レイヤー画像配列をセットする。
	 *
	 * @param image 新しい画像配列
	 */
	public void setImage(BufferedImage[] image) {
		this.image = image;
	}

	/**
	 * 各レイヤーの描画矩形インデックス配列を返す。
	 *
	 * @return 描画矩形インデックス配列
	 */
	public int[] getDir() {
		return dir;
	}

	/**
	 * 描画矩形インデックス配列をセットする。
	 *
	 * @param dir 新しい描画矩形インデックス配列
	 */
	public void setDir(int[] dir) {
		this.dir = dir;
	}

	/**
	 * 各レイヤーの汎用オプション値配列を返す。
	 *
	 * @return オプション値配列
	 */
	public int[] getOption() {
		return option;
	}

	/**
	 * 汎用オプション値配列をセットする。
	 *
	 * @param option 新しいオプション値配列
	 */
	public void setOption(int[] option) {
		this.option = option;
	}

	/**
	 * 全レイヤーの画像参照・矩形インデックス・オプション値をリセットする.
	 */
	public void clear() {
		for (int i = 0; i < image.length; i++) {
			image[i] = null;
			dir[i] = 0;
			option[i] = 0;
		}
	}
}
