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
	/**
	 * ゴミからランダムなおかざりを取得する.
	 * @param ageState 成長段階
	 * @return おかざりのタイプ
	 */
	public static final OkazariType getRandomOkazari(AgeState ageState) {
		int num = OKAZARI_START[ageState.ordinal()] + rnd.nextInt(OKAZARI_NUM[ageState.ordinal()]);
		return OkazariType.values()[num];
	}
	/**
	 * おかざりのイメージを取得する.
	 * @param type おかざりのタイプ
	 * @param direction 方向
	 * @return おかざりのイメージ
	 */
	public static final BufferedImage getOkazariImage(OkazariType type, int direction) {
		return images[type.ordinal()][direction];
	}
	/**
	 * おかざりのタイプを取得する.
	 * @return おかざりのタイプ
	 */
	public OkazariType getOkazariType() {
		return okazariType;
	}
	/**
	 * おかざりのオフセットポジションを取得する.
	 * @return おかざりのオフセットポジション
	 */
	public Point getOkazariOfsPos() {
		return offsetPos[owner.getBodyAgeState().ordinal()];
	}
	/**
	 * コンストラクタ.
	 * @param b ゆっくりのインスタンス
	 * @param type おかざりのタイプ
	 */
	public Okazari(Body b, OkazariType type) {

		owner = b;
		okazariType = type;
		if(okazariType.fileName == null) {
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
