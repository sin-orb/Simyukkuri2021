package src.item;

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
 * がらくた
 */
public class Trash extends ObjEX {

	private static final long serialVersionUID = 4386384968548796846L;
	private static BufferedImage[] images = new BufferedImage[2];
	private static Rectangle4y boundary = new Rectangle4y();

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		images[0] = ModLoader.loadItemImage(loader, "trash" + File.separator + "trash.png");
		images[1] = ModLoader.loadItemImage(loader, "trash" + File.separator + "trash_shadow.png");

		boundary.setWidth(images[0].getWidth(io));
		boundary.setHeight(images[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() - 1);
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		layer[0] = images[0];
		return 1;
	}

	/** 境界線の取得 */
	public static Rectangle4y getBounding() {
		return boundary;
	}

	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return images[1];
	}

	@Override
	public void removeListData() {
		SimYukkuri.world.getCurrentMap().getTrash().remove(objId);
	}

	@Override
	public void kick() {
		kick(0, -8, -4);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param initX      x座標
	 * @param initY      y座標
	 * @param initOption 特に意味なし
	 */
	public Trash(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().getTrash().put(objId, this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.TRASH;

		value = 0;
		cost = 0;
	}

	public Trash() {

	}

}
