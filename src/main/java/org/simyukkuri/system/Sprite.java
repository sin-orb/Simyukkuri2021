package org.simyukkuri.system;

import org.simyukkuri.draw.Point4y;
import org.simyukkuri.draw.Rectangle4y;



/************************************************
 * 仮想スプライト
 * 画像の実体は持たずサイズや中心点のみ定義
 */
public class Sprite implements java.io.Serializable {
	static final long serialVersionUID = 1L;
	/** 中心点・真ん中/真ん中 */
	public static final int PIVOT_CENTER_CENTER = 0;
	/** 中心点・真ん中/下 */
	public static final int PIVOT_CENTER_BOTTOM = 1;

	// 原画サイズ
	private int originalW;
	private int originalH;

	/** 描画画像サイズ 拡大縮小で変動する*/
	private int imageW, imageH;
	
	/** 描画原点 拡大縮小で変動する */
	private int pivotX,pivotY, pivotType;

	/** 
	 * 描画スクリーン座標
	 *  マウスとの判定で複数参照するのでキャッシュする
	 *   [0]左 [1]右
	 */
	private Rectangle4y[] screenRect;
	/**
	 * コンストラクタ.
	 * @param w 幅
	 * @param h 奥行き
	 * @param piv 中心点
	 */
	public Sprite(int w, int h, int piv) {
		originalW = w;
		originalH = h;

		imageW = w;
		imageH = h;
		
		pivotType = piv;

		calcPivot();
		screenRect = new Rectangle4y[2];
		screenRect[0] = new Rectangle4y();
		screenRect[1] = new Rectangle4y();
	}
	/**
	 * デシリアライズ用デフォルトコンストラクタ。
	 */
	public Sprite() {
		
	}
	
	/**
	 *  受け取ったスクリーン座標から左右向きの描画範囲計算
	 * @param origin スクリーン座標
	 * @param tx X座標
	 * @param ty Y座標
	 * @param tw 幅
	 * @param th 奥行き
	 */
	public void calcScreenRect(Point4y origin, int tx, int ty, int tw, int th) {
		// 左
		screenRect[0].setX(origin.getX() - tx);
		screenRect[0].setY(origin.getY() - ty);
		screenRect[0].setWidth(tw);
		screenRect[0].setHeight(th);
		// 右
		screenRect[1].setX(origin.getX() + tx);
		screenRect[1].setY(origin.getY() - ty);
		screenRect[1].setWidth(-tw);
		screenRect[1].setHeight(th);
	}
	
	
	/**
	 *  描画画像サイズ更新
	 * @param w 幅
	 * @param h 高さ
	 */
	public void setSpriteSize(int w, int h) {
		imageW = w;
		imageH = h;

		calcPivot();
	}
	/**
	 * スプライトサイズを加える
	 * @param w 幅
	 * @param h 高さ
	 */
	public void addSpriteSize(int w, int h) {
		imageW = originalW + w;
		imageH = originalH + h;

		calcPivot();
	}
	/**
	 * 中心点タイプを設定する.
	 * @param type 中心点タイプ
	 */
	public void setPivotType(int type) {
		pivotType = type;
		//calcPivot();
	}

	private void calcPivot() {
		switch(pivotType) {
			default:
			case PIVOT_CENTER_CENTER:
				pivotX = imageW >> 1;
				pivotY = imageH >> 1;
				break;
			case PIVOT_CENTER_BOTTOM:
				pivotX = imageW >> 1;
				pivotY = imageH - 1;
				break;
		}
	}
	/** @return 原画の幅 */
	public int getOriginalW() {
		return originalW;
	}
	/** @param originalW 原画の幅 */
	public void setOriginalW(int originalW) {
		this.originalW = originalW;
	}
	/** @return 原画の高さ */
	public int getOriginalH() {
		return originalH;
	}
	/** @param originalH 原画の高さ */
	public void setOriginalH(int originalH) {
		this.originalH = originalH;
	}
	/** @return 描画画像の幅（拡大縮小で変動） */
	public int getImageW() {
		return imageW;
	}
	/** @param imageW 描画画像の幅 */
	public void setImageW(int imageW) {
		this.imageW = imageW;
	}
	/** @return 描画画像の高さ（拡大縮小で変動） */
	public int getImageH() {
		return imageH;
	}
	/** @param imageH 描画画像の高さ */
	public void setImageH(int imageH) {
		this.imageH = imageH;
	}
	/** @return 描画原点のX座標 */
	public int getPivotX() {
		return pivotX;
	}
	/** @param pivotX 描画原点のX座標 */
	public void setPivotX(int pivotX) {
		this.pivotX = pivotX;
	}
	/** @return 描画原点のY座標 */
	public int getPivotY() {
		return pivotY;
	}
	/** @param pivotY 描画原点のY座標 */
	public void setPivotY(int pivotY) {
		this.pivotY = pivotY;
	}
	/**
	 * キャッシュされた描画スクリーン座標配列を返す。
	 *
	 * @return スクリーン座標矩形の配列（[0]左 [1]右）
	 */
	public Rectangle4y[] getScreenRect() {
		return screenRect;
	}
	/** @param screenRect スクリーン座標矩形の配列 */
	public void setScreenRect(Rectangle4y[] screenRect) {
		this.screenRect = screenRect;
	}
	/** @return 中心点タイプ（PIVOT_CENTER_CENTER / PIVOT_CENTER_BOTTOM） */
	public int getPivotType() {
		return pivotType;
	}
}



