package org.simyukkuri.engine;

import java.awt.image.BufferedImage;

/**
 * ゆっくり画像のテンポラリクラス
 */
final class BodyImage {
	private BufferedImage[] img; // 画像(左右)
	private boolean[] isDummy; // ダミーファイルあり
	private boolean isFlip; // 反転あり
	private BufferedImage[][] imgOtherVer; // 別バージョン画像(左右)

	/**
	 * コンストラクタ.
	 */
	public BodyImage() {
		img = new BufferedImage[2];
		isDummy = new boolean[2];
		isFlip = false;
		imgOtherVer = new BufferedImage[2][ModLoader.getMaxImgOtherVer()];
	}

	/**
	 * 左右の画像配列を返す。
	 *
	 * @return 画像配列（[0]左 [1]右）
	 */
	public BufferedImage[] getImg() {
		return img;
	}

	/**
	 * 左右のダミーフラグ配列を返す。
	 *
	 * @return ダミーフラグ配列（[0]左 [1]右）
	 */
	public boolean[] getIsDummy() {
		return isDummy;
	}

	/** @return 右画像を左の反転で描画する場合 true */
	public boolean isFlip() {
		return isFlip;
	}

	/** @param flip 右画像を左の反転で描画するか */
	public void setFlip(boolean flip) {
		isFlip = flip;
	}

	/**
	 * 左右の別バージョン画像配列を返す。
	 *
	 * @return 別バージョン画像配列（[左右][バージョン]）
	 */
	public BufferedImage[][] getImgOtherVer() {
		return imgOtherVer;
	}
}
