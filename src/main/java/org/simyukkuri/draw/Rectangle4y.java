package org.simyukkuri.draw;

import java.io.Serializable;

/**
 * Rectangleの循環参照をなくしたクラス.
 */
public class Rectangle4y implements Serializable {

	private static final long serialVersionUID = 4035949738478762553L;
	private int x;
	private int y;
	private int width;
	private int height;

	/**
	 * X 座標を返す。
	 *
	 * @return X 座標
	 */
	public int getX() {
		return x;
	}

	/**
	 * X 座標をセットする。
	 *
	 * @param x 新しい X 座標
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Y 座標を返す。
	 *
	 * @return Y 座標
	 */
	public int getY() {
		return y;
	}

	/**
	 * Y 座標をセットする。
	 *
	 * @param y 新しい Y 座標
	 */
	public void setY(int y) {
		this.y = y;
	}

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
	 * 全フィールド 0 で初期化する。
	 */
	public Rectangle4y() {
		x = 0;
		y = 0;
		width = 0;
		height = 0;
	}

	/**
	 * X・Y 座標・幅・高さを指定して初期化する。
	 *
	 * @param x      X 座標
	 * @param y      Y 座標
	 * @param width  幅
	 * @param height 高さ
	 */
	public Rectangle4y(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * 各フィールドを含む文字列表現を返す。
	 *
	 * @return 文字列表現
	 */
	public String toString() {
		return "x: " + x + ", y: " + y +  ", width :" + width + ", height:" + height;
	}
}
