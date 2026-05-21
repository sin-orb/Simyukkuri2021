package org.simyukkuri.draw;

import java.io.Serializable;

/**
 * awt代替のDimension.
 */
public class Dimension4y implements Serializable {

	private static final long serialVersionUID = -7555703845901421580L;
	private int width;
	private int height;

	/**
	 * 幅を返す。
	 *
	 * @return 幅
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * 幅をセットする。
	 *
	 * @param width 新しい幅
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * 高さを返す。
	 *
	 * @return 高さ
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * 高さをセットする。
	 *
	 * @param height 新しい高さ
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * 幅・高さ 0 で初期化する。
	 */
	public Dimension4y() {
		width = 0;
		height = 0;
	}

	/**
	 * 幅と高さを指定して初期化する。
	 *
	 * @param width  幅
	 * @param height 高さ
	 */
	public Dimension4y(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * 幅と高さを含む文字列表現を返す。
	 *
	 * @return 文字列表現
	 */
	public String toString() {
		return "width :" + width + ", height:" + height;
	}
}
