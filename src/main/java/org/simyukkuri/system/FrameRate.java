package org.simyukkuri.system;

/**
 * フレームレート計測.
 */
public class FrameRate {

	private long basetime;
	private int count;
	private float framerate;

	/**
	 * コンストラクタ.
	 */
	public FrameRate() {
		basetime = System.currentTimeMillis();
	}

	/**
	 * フレームレートを取得する。
	 *
	 * @return フレームレート
	 */
	public float getFrameRate() {
		return framerate;
	}

	/**
	 * 描画時に呼ぶ。
	 */
	public void count() {
		++count;
		long now = System.currentTimeMillis();
		if (now - basetime >= 1000) {
			framerate = (float) (count * 1000) / (float) (now - basetime);
			basetime = now;
			count = 0;
		}
	}
}
