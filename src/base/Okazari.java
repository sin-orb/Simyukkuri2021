package src.base;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import src.SimYukkuri;
import src.draw.ModLoader;
import src.draw.Point4y;
import src.draw.Rectangle4y;
import src.enums.AgeState;
import src.enums.Type;
import src.util.YukkuriUtil;

/***************************************************
  おかざりオブジェクトクラス 

 */
public class Okazari extends Obj {

	/** おかざりの種類とファイル名兼マップキー */
	public enum OkazariType {
		DEFAULT(null), BABY1("okazari_baby_01"), BABY2("okazari_baby_02"), CHILD1("okazari_child_01"), CHILD2(
				"okazari_child_02"), ADULT1("okazari_adult_01"), ADULT2("okazari_adult_02"), ADULT3("okazari_adult_03"),
				;

		public String fileName;

		OkazariType(String name) {
			this.fileName = name;
		}
	}

	// 各世代のお飾りの開始位置と数
	private static final int[] OKAZARI_START = { OkazariType.BABY1.ordinal(),
			OkazariType.CHILD1.ordinal(),
			OkazariType.ADULT1.ordinal() };
	private static final int[] OKAZARI_NUM = { 2, 2, 3 };

	private static BufferedImage[][] images = new BufferedImage[OkazariType.values().length][2];
	private static Rectangle4y[] boundary = new Rectangle4y[OkazariType.values().length];

	private int owner;
	private OkazariType okazariType;
	// 胴体に対するオフセット
	private Point4y[] offsetPos;

	/**
	 *  ゴミおかざりの画像読み込み
	 * @param loader
	 * @param io
	 * @throws IOException
	 */
	public static final void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		OkazariType[] o = OkazariType.values();
		for (int i = 1; i < o.length; i++) {
			images[i][0] = ModLoader.loadItemImage(loader, "trash" + File.separator + o[i].fileName + ".png");
			images[i][1] = ModLoader.flipImage(images[i][0]);

			boundary[i] = new Rectangle4y();
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
		int num = OKAZARI_START[ageState.ordinal()] + SimYukkuri.RND.nextInt(OKAZARI_NUM[ageState.ordinal()]);
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
	public Point4y takeOkazariOfsPos() {
		if (offsetPos == null) {
			return null;
		}
		Body o = YukkuriUtil.getBodyInstance(owner);
		if  (o == null) return null;
		return offsetPos[YukkuriUtil.getBodyInstance(owner).getBodyAgeState().ordinal()];
	}

	/**
	 * コンストラクタ.
	 * @param b ゆっくりのインスタンス
	 * @param type おかざりのタイプ
	 */
	public Okazari(Body b, OkazariType type) {

		owner = b.getUniqueID();
		okazariType = type;
		if (okazariType.fileName == null) {
			offsetPos = null;
			setBoundary(64, 127, 128, 128);
		} else {
			Body o = YukkuriUtil.getBodyInstance(owner);
			if (o != null) {
				offsetPos = YukkuriUtil.getBodyInstance(owner).getMountPoint(okazariType.fileName);
				setBoundary(boundary[type.ordinal()]);
			}
		}
		objType = Type.OKAZARI;
		value = 0;
		cost = 0;
	}

	public Okazari() {
	}
	
	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public Point4y[] getOffsetPos() {
		return offsetPos;
	}

	public void setOffsetPos(Point4y[] offsetPos) {
		this.offsetPos = offsetPos;
	}

	public void setOkazariType(OkazariType okazariType) {
		this.okazariType = okazariType;
	}
	
}
