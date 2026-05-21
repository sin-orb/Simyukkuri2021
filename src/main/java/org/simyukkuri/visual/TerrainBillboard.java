package org.simyukkuri.visual;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.Serializable;

/**
 * 背景の部品画像管理.
 */
public class TerrainBillboard implements Serializable {

	private static final long serialVersionUID = -587830180580728404L;
	/** 画像 */
	private BufferedImage image;
	/** 変形用ベクトル */
	private AffineTransform xform;
	/** 描画順用Y座標 */
	private int sortY;

	/**
	 * コンストラクタ.
	 * 
	 * @param img 背景部品の画像
	 */
	public TerrainBillboard(BufferedImage img) {
		image = img;
		xform = new AffineTransform();
		sortY = 0;
	}

	/** イメージ取得 */
	@Transient
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

	/** 描画 */
	public void draw(Graphics2D g2, ImageObserver obs) {
		g2.drawImage(image, xform, obs);
	}

	/** 描画順用Y座標取得 */
	public int getSortY() {
		return sortY;
	}

	/** 描画順用Y座標設定 */
	public void setSortY(int sortY) {
		this.sortY = sortY;
	}

	/** @return 変形用アフィン変換 */
	public AffineTransform getXform() {
		return xform;
	}

	/** @param xform 変形用アフィン変換 */
	public void setXform(AffineTransform xform) {
		this.xform = xform;
	}

	/** @param image 背景部品の画像 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}

}
