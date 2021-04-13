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
ベッド
*/
public class Bed extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	private static BufferedImage[] images;
	private static Rectangle boundary = new Rectangle();

	private ItemRank itemRank;
	
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {

		images = new BufferedImage[3];
		images[0] = ModLoader.loadItemImage(loader, "bed" + File.separator + "bed.png");
		images[1] = ModLoader.loadItemImage(loader, "bed" + File.separator + "bed" + ModLoader.YK_WORD_NORA + ".png");
		images[2] = ModLoader.loadItemImage(loader, "bed" + File.separator + "bed" + ModLoader.YK_WORD_YASEI + ".png");
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		layer[0] = images[itemRank.ordinal()];
		return 1;
	}

	@Override
	public BufferedImage getShadowImage() {
		return null;
	}

	public static Rectangle getBounding() {
		return boundary;
	}

	@Override
	public void removeListData(){
		SimYukkuri.world.currentMap.bed.remove(this);
	}

	@Override
	public int getValue() {
		return value;
	}
	
	// initOption = 1 : 野良用
	public Bed(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.currentMap.bed.add(this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.BED;
		itemRank = ItemRank.values()[initOption];
		// 森なら野生に変更
		if( SimYukkuri.world.currentMap.mapIndex == 5 ||  SimYukkuri.world.currentMap.mapIndex == 6 ){
			if( itemRank == ItemRank.HOUSE ){
				itemRank = ItemRank.YASEI;
			}
		}
		interval = 5000;

		if(itemRank == ItemRank.HOUSE) {
			value = 3000;
			cost = 0;
		}
		else {
			value = 0;
			cost = 0;
		}
	}
}

