package src.item;


import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import src.SimYukkuri;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.enums.ObjEXType;
import src.enums.Type;

/***************************************************
 * がらくた
 */
public class Trash extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	private static BufferedImage[] images = new BufferedImage[2];
	private static Rectangle boundary = new Rectangle();
	/**画像ロード*/
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {

		images[0] = ModLoader.loadItemImage(loader, "trash" + File.separator + "trash.png");
		images[1] = ModLoader.loadItemImage(loader, "trash" + File.separator + "trash_shadow.png");
		
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height - 1;
	}


	@Override
	public int getImageLayer(BufferedImage[] layer) {
		layer[0] = images[0];
		return 1;
	}
	/**境界線の取得*/
	public static Rectangle getBounding() {
		return boundary;
	}

	@Override
	public BufferedImage getShadowImage() {
		return images[1];
	}
	
	@Override
	public void removeListData(){
		SimYukkuri.world.currentMap.trash.remove(this);
	}
	
	@Override
	public void kick() {
		kick(0, -8, -4);
	}
	/**
	 * コンストラクタ
	  * @param initX x座標
	 * @param initY y座標
	 * @param initOption 特に意味なし
	 */
	public Trash(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.currentMap.trash.add(this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.TRASH;
		
		value = 0;
		cost = 0;
	}
}


