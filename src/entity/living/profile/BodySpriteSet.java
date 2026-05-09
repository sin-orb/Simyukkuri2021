package src.entity.living.profile;

import src.system.Sprite;
import src.draw.Rectangle4y;

/**
 * BodyAttributes のスプライト配列をまとめた値オブジェクト.
 * age 別の body / expand / braid スプライトを保持する.
 */
public class BodySpriteSet implements java.io.Serializable {
	private static final long serialVersionUID = 4732289264004140284L;

	/** 本体のスプライト定義 */
	private Sprite[] bodySpr;
	/** 拡幅分のスプライト定義 */
	private Sprite[] expandSpr;
	/** おさげのスプライト定義 */
	private Sprite[] braidSpr;

	/** BodySpriteSet を生成する. */
	public BodySpriteSet() {
		bodySpr = new Sprite[3];
		expandSpr = new Sprite[3];
		braidSpr = new Sprite[3];
	}

	/**
	 * 他インスタンスの内容を深く複製する.
	 *
	 * @param from 複製元
	 */
	public void copyFrom(BodySpriteSet from) {
		if (from == null) {
			return;
		}
		bodySpr = copyArray(from.bodySpr);
		expandSpr = copyArray(from.expandSpr);
		braidSpr = copyArray(from.braidSpr);
	}

	/**
	 * 現在値の複製を作る.
	 *
	 * @return 複製済みインスタンス
	 */
	public BodySpriteSet copy() {
		BodySpriteSet ret = new BodySpriteSet();
		ret.copyFrom(this);
		return ret;
	}

	private static Sprite[] copyArray(Sprite[] src) {
		if (src == null) {
			return null;
		}
		Sprite[] ret = new Sprite[src.length];
		for (int i = 0; i < src.length; i++) {
			ret[i] = copySprite(src[i]);
		}
		return ret;
	}

	private static Sprite copySprite(Sprite src) {
		if (src == null) {
			return null;
		}
		Sprite ret = new Sprite();
		ret.setOriginalW(src.getOriginalW());
		ret.setOriginalH(src.getOriginalH());
		ret.setImageW(src.getImageW());
		ret.setImageH(src.getImageH());
		ret.setPivotX(src.getPivotX());
		ret.setPivotY(src.getPivotY());
		ret.setPivotType(src.getPivotType());
		Rectangle4y[] rect = src.getScreenRect();
		if (rect != null) {
			Rectangle4y[] rectCopy = new Rectangle4y[rect.length];
			for (int i = 0; i < rect.length; i++) {
				Rectangle4y r = rect[i];
				rectCopy[i] = (r == null) ? null : new Rectangle4y(r.getX(), r.getY(), r.getWidth(), r.getHeight());
			}
			ret.setScreenRect(rectCopy);
		}
		return ret;
	}

	public Sprite[] getBodySpr() {
		return bodySpr;
	}

	public void setBodySpr(Sprite[] bodySpr) {
		this.bodySpr = bodySpr;
	}

	public Sprite[] getExpandSpr() {
		return expandSpr;
	}

	public void setExpandSpr(Sprite[] expandSpr) {
		this.expandSpr = expandSpr;
	}

	public Sprite[] getBraidSpr() {
		return braidSpr;
	}

	public void setBraidSpr(Sprite[] braidSpr) {
		this.braidSpr = braidSpr;
	}
}
