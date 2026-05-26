package org.simyukkuri.draw;

import java.io.Serializable;

/**
 * RGBA カラー値（各チャンネル 0–255）を保持するシリアライズ可能なカラークラス。
 * awt.Color の循環参照を避けるため独自実装。各セッターは値を 0–255 にクランプする。
 */
public class Color4y implements Serializable {

	private static final long serialVersionUID = 6628001935213373183L;
	private int red;
	private int blue;
	private int green;
	private int alpha;

	/** 値を0-255の範囲にクランプする。 */
	private static int clamp(int value) {
		if (value < 0) {
			return 0;
		}
		if (value > 255) {
			return 255;
		}
		return value;
	}

	/**
	 * 赤チャンネル値（0–255）を返す。
	 *
	 * @return 赤チャンネル値
	 */
	public int getRed() {
		return red;
	}

	/**
	 * 赤チャンネル値をセットする。0–255 にクランプされる。
	 *
	 * @param red 赤チャンネル値
	 */
	public void setRed(int red) {
		this.red = clamp(red);
	}

	/**
	 * 青チャンネル値（0–255）を返す。
	 *
	 * @return 青チャンネル値
	 */
	public int getBlue() {
		return blue;
	}

	/**
	 * 青チャンネル値をセットする。0–255 にクランプされる。
	 *
	 * @param blue 青チャンネル値
	 */
	public void setBlue(int blue) {
		this.blue = clamp(blue);
	}

	/**
	 * 緑チャンネル値（0–255）を返す。
	 *
	 * @return 緑チャンネル値
	 */
	public int getGreen() {
		return green;
	}

	/**
	 * 緑チャンネル値をセットする。0–255 にクランプされる。
	 *
	 * @param green 緑チャンネル値
	 */
	public void setGreen(int green) {
		this.green = clamp(green);
	}

	/**
	 * アルファ（不透明度）チャンネル値（0–255）を返す。
	 *
	 * @return アルファチャンネル値
	 */
	public int getAlpha() {
		return alpha;
	}

	/**
	 * アルファチャンネル値をセットする。0–255 にクランプされる。
	 *
	 * @param alpha アルファチャンネル値
	 */
	public void setAlpha(int alpha) {
		this.alpha = clamp(alpha);
	}

	/**
	 * 全チャンネル 0（完全透明の黒）で初期化する。
	 */
	public Color4y() {
		red = 0;
		green = 0;
		blue = 0;
		alpha = 0;
	}

	/**
	 * RGBA 各チャンネルを指定して初期化する. 各値は 0–255 にクランプされる。
	 *
	 * @param red   赤チャンネル値
	 * @param green 緑チャンネル値
	 * @param blue  青チャンネル値
	 * @param alpha アルファチャンネル値
	 */
	public Color4y(int red, int green, int blue, int alpha) {
		this.red = clamp(red);
		this.green = clamp(green);
		this.blue = clamp(blue);
		this.alpha = clamp(alpha);
	}

	/**
	 * 各チャンネル値を含む文字列表現を返す。
	 *
	 * @return 文字列表現
	 */
	public String toString() {
		return "red: " + red + ", green: " + green + ", blue: " + blue + ", alpha: " + alpha;
	}
}
