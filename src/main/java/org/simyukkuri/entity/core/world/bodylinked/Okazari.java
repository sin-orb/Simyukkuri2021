package org.simyukkuri.entity.core.world.bodylinked;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Type;
import org.simyukkuri.util.GameRandom;

/***************************************************
 * おかざりオブジェクトクラス
 * 
 */
public class Okazari extends WorldEntity {

	private static final long serialVersionUID = 5562152108201566916L;

	/** おかざりの種類とファイル名兼マップキー */
	public enum OkazariType {
		DEFAULT(null), BABY1("okazari_baby_01"), BABY2("okazari_baby_02"), CHILD1("okazari_child_01"), CHILD2(
				"okazari_child_02"),
		ADULT1("okazari_adult_01"), ADULT2("okazari_adult_02"), ADULT3("okazari_adult_03"),
		;

		private final String fileName;

		OkazariType(String name) {
			this.fileName = name;
		}

		/** おかざり画像のファイル名を返す。 */
		public String getFileName() {
			return fileName;
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
	 * ゴミおかざりの画像読み込み
	 * 
	 * @param loader
	 * @param io
	 * @throws IOException
	 */
	public static final void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		OkazariType[] o = OkazariType.values();
		for (int i = 1; i < o.length; i++) {
			images[i][0] = ModLoader.loadItemImage(loader, "trash" + File.separator + o[i].getFileName() + ".png");
			images[i][1] = ModLoader.flipImage(images[i][0]);

			boundary[i] = new Rectangle4y();
			boundary[i].setWidth(images[i][0].getWidth(io));
			boundary[i].setHeight(images[i][0].getHeight(io));
			boundary[i].setX(boundary[i].getWidth() >> 1);
			boundary[i].setY(boundary[i].getHeight() - 1);
		}
	}

	/**
	 * ゴミからランダムなおかざりを取得する.
	 * 
	 * @param ageState 成長段階
	 * @return おかざりのタイプ
	 */
	public static final OkazariType getRandomOkazari(AgeState ageState) {
		int num = OKAZARI_START[ageState.ordinal()] + GameRandom.nextInt(OKAZARI_NUM[ageState.ordinal()]);
		return OkazariType.values()[num];
	}

	/**
	 * おかざりのイメージを取得する.
	 * 
	 * @param type      おかざりのタイプ
	 * @param direction 方向
	 * @return おかざりのイメージ
	 */
	public static final BufferedImage getOkazariImage(OkazariType type, int direction) {
		return images[type.ordinal()][direction];
	}

	/**
	 * おかざりのタイプを取得する.
	 * 
	 * @return おかざりのタイプ
	 */
	public OkazariType getOkazariType() {
		return okazariType;
	}

	/**
	 * おかざりのオフセットポジションを取得する.
	 * 
	 * @return おかざりのオフセットポジション
	 */
	public Point4y takeOkazariOfsPos() {
		if (offsetPos == null) {
			return null;
		}
		Yukkuri body = org.simyukkuri.util.YukkuriLookup.getYukkuriById(owner);
		if (body == null)
			return null;
		return offsetPos[body.getAgeState().ordinal()];
	}

	/**
	 * コンストラクタ.
	 * 
	 * @param b    ゆっくりのインスタンス
	 * @param type おかざりのタイプ
	 */
	public Okazari(Yukkuri body, OkazariType type) {

		owner = body.getUniqueID();
		okazariType = type;
		if (okazariType.getFileName() == null) {
			offsetPos = null;
			setBoundary(64, 127, 128, 128);
		} else {
			Yukkuri ownerBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(owner);
			if (ownerBody != null) {
				offsetPos = ownerBody.getMountPoint(okazariType.getFileName());
				setBoundary(boundary[type.ordinal()]);
			}
		}
		objType = Type.OKAZARI;
		value = 0;
		cost = 0;
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public Okazari() {
	}

	/** おかざりを装着しているゆっくりの ID を返す。 */
	public int getOwner() {
		return owner;
	}

	/** おかざりを装着しているゆっくりの ID をセットする。 */
	public void setOwner(int owner) {
		this.owner = owner;
	}

	/** 胴体に対するオフセット座標配列を返す。 */
	public Point4y[] getOffsetPos() {
		return offsetPos;
	}

	/** 胴体に対するオフセット座標配列をセットする。 */
	public void setOffsetPos(Point4y[] offsetPos) {
		this.offsetPos = offsetPos;
	}

	/** おかざりの種別をセットする。 */
	public void setOkazariType(OkazariType okazariType) {
		this.okazariType = okazariType;
	}

	/** 未実装（UnsupportedOperationException を投げる）。 */
	@Override
	public int getImageLayer(BufferedImage[] layer) {
		throw new UnsupportedOperationException("Unimplemented method 'getImageLayer'");
	}

	/** 未実装（UnsupportedOperationException を投げる）。 */
	@Override
	public BufferedImage getShadowImage() {
		throw new UnsupportedOperationException("Unimplemented method 'getShadowImage'");
	}

	/** ワールドからおかざりを除去する。 */
	@Override
	public void removeFromWorld() {
		throw new UnsupportedOperationException("Unimplemented method 'removeListData'");
	}

}
