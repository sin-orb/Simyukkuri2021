package src.base;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import src.draw.ModLoader;
import src.enums.AgeState;
import src.enums.Type;


/***************************************************
  おかざりオブジェクトクラス 

 */
public class Okazari extends Obj {

	/** おかざりの種類とファイル名兼マップキー */
	public enum OkazariType {
		DEFAULT(null),
		BABY1("okazari_baby_01"),
		BABY2("okazari_baby_02"),
		CHILD1("okazari_child_01"),
		CHILD2("okazari_child_02"),
		ADULT1("okazari_adult_01"),
		ADULT2("okazari_adult_02"),
		ADULT3("okazari_adult_03"),
		;
		public String fileName;
		OkazariType(String name) {
			this.fileName = name;
		}
	}

	private static Random rnd = new Random();

	// 各世代のお飾りの開始位置と数
	private static final int[] OKAZARI_START = {OkazariType.BABY1.ordinal(),
												OkazariType.CHILD1.ordinal(),
												OkazariType.ADULT1.ordinal()};
	private static final int[] OKAZARI_NUM = {2, 2, 3};

	private static BufferedImage[][] images = new BufferedImage[OkazariType.values().length][2];
	private static Rectangle[] boundary = new Rectangle[OkazariType.values().length];
	
	private Body owner;
	private OkazariType okazariType;
	// 胴体に対するオフセット
	private Point[] offsetPos;
	
//	private BufferedImage imageDefault = null;
//	private BufferedImage imageDefaultShadow = null;

	/**
	 *  ゴミおかざりの画像読み込み
	 * @param loader
	 * @param io
	 * @throws IOException
	 */
	public static final void loadImages (ClassLoader loader, ImageObserver io) throws IOException {

		OkazariType[] o = OkazariType.values();
		for(int i = 1; i < o.length; i++) {
			images[i][0] = ModLoader.loadItemImage(loader, "trash" + File.separator + o[i].fileName + ".png");
			images[i][1] = ModLoader.flipImage(images[i][0]);
			
			boundary[i] = new Rectangle();
			boundary[i].width = images[i][0].getWidth(io);
			boundary[i].height = images[i][0].getHeight(io);
			boundary[i].x = boundary[i].width >> 1;
			boundary[i].y = boundary[i].height - 1;
		}
	}
	
	public BufferedImage getImage() {
//		if( okazariType == OkazariType.DEFAULT )
//		{
//			if( imageDefault == null )
//			{
//				BodyLayer layer = new BodyLayer();
//				if( owner.direction == Direction.LEFT )
//				{
//					owner.getImage(ImageCode.ACCESSORY.ordinal(), Body.LEFT, layer, 0);
//					imageDefault = layer.image[0];
//				}else{
//					owner.getImage(ImageCode.ACCESSORY.ordinal(), Body.RIGHT, layer, 0);
//					imageDefault = layer.image[0];		
//				}
//			}
//			return imageDefault;
//		}
		return images[okazariType.ordinal()][0];
	}
	
	public static final OkazariType getRandomOkazari(AgeState ageState) {
		int num = OKAZARI_START[ageState.ordinal()] + rnd.nextInt(OKAZARI_NUM[ageState.ordinal()]);
		return OkazariType.values()[num];
	}
	
	public static final BufferedImage getOkazariImage(OkazariType type, int direction) {
		return images[type.ordinal()][direction];
	}

	public OkazariType getOkazariType() {
		return okazariType;
	}

	public Point getOkazariOfsPos() {
//System.out.println(offsetPos[owner.bodyAgeState.ordinal()]);
		return offsetPos[owner.getBodyAgeState().ordinal()];
	}

	public Okazari(Body b, OkazariType type) {

		owner = b;
		okazariType = type;
		if(okazariType.fileName == null) {
			if( b != null )
			{
//				BodyLayer layer = new BodyLayer();
//				owner.getImage(ImageCode.ACCESSORY.ordinal(), Body.LEFT, layer, 0);
//				imageDefault = layer.image[0];
//				if( b.direction == Direction.LEFT )
//				{
//					BodyLayer layer = new BodyLayer();
//					owner.getImage(ImageCode.ACCESSORY.ordinal(), Body.LEFT, layer, 0);
//					imageDefault = layer.image[0];
//				}else{
//					BodyLayer layer = new BodyLayer();
//					owner.getImage(ImageCode.ACCESSORY.ordinal(), Body.RIGHT, layer, 0);
//					imageDefault = layer.image[0];				
//				}
			}

			offsetPos = null;
			setBoundary(64, 127, 128, 128);
		}
		else {
			offsetPos = owner.getMountPoint(okazariType.fileName);
			setBoundary(boundary[type.ordinal()]);
		}
		objType = Type.OKAZARI;
		value = 0;
		cost = 0;
	}
}
