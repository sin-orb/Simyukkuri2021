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
 * おうち
 */
public class House extends ObjEX {
	private static final long serialVersionUID = -6609787822366581526L;

	/** おうちの種類テーブル */
	public static enum HouseTable {
		HOUSE_NORA1("floor_nora1.png", "wall_nora1.png", "ceil_nora1.png", "door_nora1.png", 1),
		HOUSE_NORA2("floor_nora2.png", "wall_nora2.png", "ceil_nora2.png", "door_nora2.png", 1),
		;

		private final String floorName;
		private final String wallName;
		private final String ceilName;
		private final String doorName;
		private final int rank;

		HouseTable(String f, String w, String c, String d, int r) {
			floorName = f;
			wallName = w;
			ceilName = c;
			doorName = d;
			rank = r;
		}

		public String getFloorName() {
			return floorName;
		}

		public String getWallName() {
			return wallName;
		}

		public String getCeilName() {
			return ceilName;
		}

		public String getDoorName() {
			return doorName;
		}

		public int getRank() {
			return rank;
		}
	}

	private static BufferedImage[][] images;
	private static Rectangle4y[] boundary;

	private HouseTable houseType;
	private ItemRank itemRank;

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		images = new BufferedImage[HouseTable.values().length][4];
		boundary = new Rectangle4y[HouseTable.values().length];

		for (HouseTable i : HouseTable.values()) {
			images[i.ordinal()][0] = ModLoader.loadItemImage(loader, "house" + File.separator + i.getFloorName());
			images[i.ordinal()][1] = ModLoader.loadItemImage(loader, "house" + File.separator + i.getWallName());
			images[i.ordinal()][2] = ModLoader.loadItemImage(loader, "house" + File.separator + i.getCeilName());
			images[i.ordinal()][3] = ModLoader.loadItemImage(loader, "house" + File.separator + i.getDoorName());
			boundary[i.ordinal()] = new Rectangle4y();
			boundary[i.ordinal()].setWidth(images[i.ordinal()][0].getWidth(io));
			boundary[i.ordinal()].setHeight(images[i.ordinal()][0].getHeight(io));
			boundary[i.ordinal()].setX(boundary[i.ordinal()].getWidth() >> 1);
			boundary[i.ordinal()].setY(boundary[i.ordinal()].getHeight() >> 1);
		}
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		layer[0] = images[0][0];
		return 1;
	}

	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return null;
	}

	/** 境界線の取得 */
	@Transient
	public static Rectangle4y getBounding() {
		return boundary[0];
	}

	@Override
	public void removeListData() {
		SimYukkuri.world.getCurrentMap().getHouse().remove(objId);
	}

	@Override
	public int getValue() {
		return value;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param initX      x座標
	 * @param initY      y座標
	 * @param initOption 0:飼い用、1;野良用
	 */
	public House(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		houseType = HouseTable.values()[initOption];
		setBoundary(boundary[houseType.ordinal()]);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().getHouse().put(objId, this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.HOUSE;
		itemRank = ItemRank.values()[houseType.getRank()];

		interval = 5000;

		if (itemRank == ItemRank.HOUSE) {
			value = 3000;
			cost = 0;
		} else {
			value = 0;
			cost = 0;
		}
	}

	public House() {

	}

	public HouseTable getHouseType() {
		return houseType;
	}

	public void setHouseType(HouseTable houseType) {
		this.houseType = houseType;
	}

	public ItemRank getItemRank() {
		return itemRank;
	}

	public void setItemRank(ItemRank itemRank) {
		this.itemRank = itemRank;
	}

}
