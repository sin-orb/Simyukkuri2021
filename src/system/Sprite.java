package src.system;

import java.awt.Point;
import java.awt.Rectangle;



/************************************************

	仮想スプライト
	
	画像の実体は持たずサイズや中心点のみ定義

 */
public class Sprite implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static final int PIVOT_CENTER_CENTER = 0;
	public static final int PIVOT_CENTER_BOTTOM = 1;

	// 原画サイズ
	private int originalW;
	private int originalH;

	// 描画画像サイズ
	// 拡大縮小で変動する
	public int imageW;
	public int imageH;
	
	// 描画原点
	// 拡大縮小で変動する
	public int pivotX;
	public int pivotY;
	public int pivotType;

	// 描画スクリーン座標
	// マウスとの判定で複数参照するのでキャッシュする
	// [0]左 [1]右
	public Rectangle[] screenRect;

	public Sprite(int w, int h, int piv) {
		originalW = w;
		originalH = h;

		imageW = w;
		imageH = h;
		
		pivotType = piv;

		calcPivot();
		screenRect = new Rectangle[2];
		screenRect[0] = new Rectangle();
		screenRect[1] = new Rectangle();
	}
	
	// 受け取ったスクリーン座標から左右向きの描画範囲計算
	public void calcScreenRect(Point origin, int tx, int ty, int tw, int th) {
		// 左
		screenRect[0].x = origin.x - tx;
		screenRect[0].y = origin.y - ty;
		screenRect[0].width = tw;
		screenRect[0].height = th;
		// 右
		screenRect[1].x = origin.x + tx;
		screenRect[1].y = origin.y - ty;
		screenRect[1].width = -tw;
		screenRect[1].height = th;
	}
	
	
	// 描画画像サイズ更新
	public void setSpriteSize(int w, int h) {
		imageW = w;
		imageH = h;

		calcPivot();
	}
	public void addSpriteSize(int w, int h) {
		imageW = originalW + w;
		imageH = originalH + h;

		calcPivot();
	}

	public void setPivotType(int type) {
		pivotType = type;
		calcPivot();
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
}


