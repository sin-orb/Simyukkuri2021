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
おうち
*/
public class House extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static enum HouseTable {
		HOUSE_NORA1("floor_nora1.png", "wall_nora1.png", "ceil_nora1.png", "door_nora1.png", 1),
		HOUSE_NORA2("floor_nora2.png", "wall_nora2.png", "ceil_nora2.png", "door_nora2.png", 1),
		;
		public String floorName;
		public String wallName;
		public String ceilName;
		public String doorName;
		public int rank;
		HouseTable(String f, String w, String c, String d, int r) {
			floorName = f;
			wallName = w;
			ceilName = c;
			doorName = d;
			rank = r;
		}
	}

	private static BufferedImage[][] images;
	private static Rectangle[] boundary;

	private HouseTable houseType;
	private ItemRank itemRank;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {

		images = new BufferedImage[HouseTable.values().length][4];
		boundary = new Rectangle[HouseTable.values().length];
		
		for(HouseTable i :HouseTable.values()) {
			images[i.ordinal()][0] = ModLoader.loadItemImage(loader, "house" + File.separator + i.floorName);
			images[i.ordinal()][1] = ModLoader.loadItemImage(loader, "house" + File.separator + i.wallName);
			images[i.ordinal()][2] = ModLoader.loadItemImage(loader, "house" + File.separator + i.ceilName);
			images[i.ordinal()][3] = ModLoader.loadItemImage(loader, "house" + File.separator + i.doorName);
			boundary[i.ordinal()] = new Rectangle();
			boundary[i.ordinal()].width = images[i.ordinal()][0].getWidth(io);
			boundary[i.ordinal()].height = images[i.ordinal()][0].getHeight(io);
			boundary[i.ordinal()].x = boundary[i.ordinal()].width >> 1;
			boundary[i.ordinal()].y = boundary[i.ordinal()].height >> 1;
		}
	}
	
	@Override
	public int getImageLayer(BufferedImage[] layer) {
		layer[0] = images[0][0];
		return 1;
	}

	@Override
	public BufferedImage getShadowImage() {
		return null;
	}

	public static Rectangle getBounding() {
		return boundary[0];
	}

	@Override
	public void removeListData(){
		SimYukkuri.world.currentMap.house.remove(this);
	}

	@Override
	public int getValue() {
		return value;
	}
	
	public House(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		houseType = HouseTable.values()[initOption];
		setBoundary(boundary[houseType.ordinal()]);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.currentMap.house.add(this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.HOUSE;
		itemRank = ItemRank.values()[houseType.rank];
		
//		parts[0] = new HouseDummy(initX, initY, images, images[houseType.ordinal()][0]);

		interval = 5000;

		if(itemRank == ItemRank.HOUSE) {
			value = 3000;
			cost = 0;
		} else {
			value = 0;
			cost = 0;
		}
	}
}



