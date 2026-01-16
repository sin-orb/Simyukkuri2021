package src.item;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;

import src.SimYukkuri;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.enums.ObjEXType;
import src.enums.Type;

/***************************************************
 * ベッド
 */
public class Bed extends ObjEX {

	private static final long serialVersionUID = -2355710591796200973L;
	/** 画像の入れ物 */
	private static BufferedImage[] images;
	/** 判定用長方形 */
	private static Rectangle4y boundary = new Rectangle4y();
	/** ベッドのランク */
	private ItemRank itemRank;

	/** 画像読み込み */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		images = new BufferedImage[3];
		images[0] = ModLoader.loadItemImage(loader, "bed" + File.separator + "bed.png");
		images[1] = ModLoader.loadItemImage(loader,
				"bed" + File.separator + "bed" + ModLoader.getYkWordNora() + ".png");
		images[2] = ModLoader.loadItemImage(loader,
				"bed" + File.separator + "bed" + ModLoader.getYkWordYasei() + ".png");
		boundary.setWidth(images[0].getWidth(io));
		boundary.setHeight(images[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() >> 1);
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		layer[0] = images[itemRank.ordinal()];
		return 1;
	}

	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return null;
	}

	/** 境界線の取得 */
	public static Rectangle4y getBounding() {
		return boundary;
	}

	@Override
	public void removeListData() {
		SimYukkuri.world.getCurrentMap().getBed().remove(objId);
	}

	@Override
	public int getValue() {
		return value;
	}

	/**
	 * Screen用の四角形を取得する.
	 * 
	 * @return Screen用の四角形
	 */
	public Rectangle takeScreenRect() {
		return new Rectangle(boundary.getX(), boundary.getY(), boundary.getWidth(), boundary.getHeight());
	}

	/**
	 * 初期設定
	 * 
	 * @param initX      x座標
	 * @param initY      ｙ座標
	 * @param initOption ランク(0:普通、1:野良用)
	 */
	public Bed(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().getBed().put(objId, this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.BED;
		itemRank = ItemRank.values()[initOption];
		// 森なら野生に変更
		if (SimYukkuri.world.getCurrentMap().getMapIndex() == 5
				|| SimYukkuri.world.getCurrentMap().getMapIndex() == 6) {
			if (itemRank == ItemRank.HOUSE) {
				itemRank = ItemRank.YASEI;
			}
		}
		interval = 5000;

		if (itemRank == ItemRank.HOUSE) {
			value = 3000;
			cost = 0;
		} else {
			value = 0;
			cost = 0;
		}
	}

	public Bed() {

	}

	public ItemRank getItemRank() {
		return itemRank;
	}

	public void setItemRank(ItemRank itemRank) {
		this.itemRank = itemRank;
	}

}
