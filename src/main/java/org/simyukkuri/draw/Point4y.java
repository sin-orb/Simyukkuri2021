package org.simyukkuri.draw;

import java.io.Serializable;

/**
 * awtのPointでなく循環参照なしの独自クラス.
 */
public class Point4y implements Serializable {

	private static final long serialVersionUID = 4990971219226306239L;
	private int x;
	private int y;

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
	 * X・Y 座標 0 で初期化する。
	 */
	public Point4y() {
		x = 0;
		y = 0;
	}

	/**
	 * X・Y 座標を指定して初期化する。
	 *
	 * @param x X 座標
	 * @param y Y 座標
	 */
	public Point4y(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * X・Y 座標を含む文字列表現を返す。
	 *
	 * @return 文字列表現
	 */
	public String toString() {
		return "x: " + x + ", y: " + y;
	}
}
