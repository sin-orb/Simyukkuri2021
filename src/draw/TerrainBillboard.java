package src.draw;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import src.base.Obj;
import src.enums.Type;

/*************************************************
背景の部品画像管理
*/
public class TerrainBillboard extends Obj {
	/**画像*/
	private BufferedImage image;
	/**変形用ベクトル*/
	private AffineTransform xform;
	/**
	 * コンストラクタ.
	 * @param img 背景部品の画像
	 */
	public TerrainBillboard(BufferedImage img) {
		objType = Type.BG_OBJECT;
		image = img;
		xform = new AffineTransform();
	}
	/**イメージ取得*/
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * 背景の大きさ調整
	 *
	 * @param sx 座標をX軸方向にスケーリングするために使う係数
	 * @param sy 座標をY軸方向にスケーリングするために使う係数
	 */
	public void scale(double sx, double sy) {
		xform.scale(sx, sy);
	}

	/**
	 * 背景の移動
	 *
	 * @param tx 座標がX軸方向で平行移動される距離
	 * @param ty 座標がY軸方向で平行移動される距離
	 */
	public void trans(double tx, double ty) {
		xform.translate(tx, ty);
	}
	/**描画*/
	public void draw(Graphics2D g2, ImageObserver obs) {
		g2.drawImage(image, xform, obs);
	}
}
