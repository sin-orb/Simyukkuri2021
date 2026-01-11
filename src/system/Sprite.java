package src.system;

import src.draw.Point4y;
import src.draw.Rectangle4y;



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
	public int getOriginalW() {
		return originalW;
	}
	public void setOriginalW(int originalW) {
		this.originalW = originalW;
	}
	public int getOriginalH() {
		return originalH;
	}
	public void setOriginalH(int originalH) {
		this.originalH = originalH;
	}
	public int getImageW() {
		return imageW;
	}
	public void setImageW(int imageW) {
		this.imageW = imageW;
	}
	public int getImageH() {
		return imageH;
	}
	public void setImageH(int imageH) {
		this.imageH = imageH;
	}
	public int getPivotX() {
		return pivotX;
	}
	public void setPivotX(int pivotX) {
		this.pivotX = pivotX;
	}
	public int getPivotY() {
		return pivotY;
	}
	public void setPivotY(int pivotY) {
		this.pivotY = pivotY;
	}
	public Rectangle4y[] getScreenRect() {
		return screenRect;
	}
	public void setScreenRect(Rectangle4y[] screenRect) {
		this.screenRect = screenRect;
	}
	public int getPivotType() {
		return pivotType;
	}
}



