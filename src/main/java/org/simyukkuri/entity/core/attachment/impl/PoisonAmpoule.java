package org.simyukkuri.entity.core.attachment.impl;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.attachment.Attachment;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.AttachProperty;
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;

/****************************************
 * 毒アンプル
 * 
 */
public class PoisonAmpoule extends Attachment {

	private static final long serialVersionUID = 2199136938994133802L;
	/** 識別キー */
	private static final String POS_KEY = "PoisonAmpoule";
	/**
	 * 画像の入れ物
	 * <br>
	 * [年齢][左右反転]
	 */
	private static BufferedImage[][] images;
	/** 画像のサイズ */
	private static int[] imgW, imgH;
	/** 画像の描画原点の座標 */
	private static int[] pivX, pivY;
	/** 継承元のenum AttachProperty の代入値 */
	private static final int[] property = {
			2, // 赤ゆ用画像サイズ 原画をこの値で割る
			2, // 子ゆ用画像サイズ
			1, // 成ゆ用画像サイズ
			1, // 親オブジェクトの位置基準 0:顔、お飾り向けの元サイズ 1:妊娠などの膨らみも含むサイズ
			0, // アニメ速度
			0, // アニメループ回数
			1 // アニメ画像枚数
	};

	/**
	 * イメージをロードする.
	 * 
	 * @param loader ローダ
	 * @param io     イメージオブザーバ
	 * @throws IOException IO例外
	 */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		int baby = AgeState.BABY.ordinal();
		int child = AgeState.CHILD.ordinal();
		int adult = AgeState.ADULT.ordinal();
		images = new BufferedImage[3][2];

		images[adult][0] = ModLoader.loadItemImage(loader, "ampoule" + File.separator + "poison.png");

		int w = images[adult][0].getWidth(io);
		int h = images[adult][0].getHeight(io);
		images[child][0] = ModLoader.scaleImage(images[adult][0], w / property[AttachProperty.CHILD_SIZE.ordinal()],
				h / property[AttachProperty.CHILD_SIZE.ordinal()]);
		images[baby][0] = ModLoader.scaleImage(images[adult][0], w / property[AttachProperty.BABY_SIZE.ordinal()],
				h / property[AttachProperty.BABY_SIZE.ordinal()]);

		images[adult][1] = ModLoader.flipImage(images[adult][0]);
		images[child][1] = ModLoader.flipImage(images[child][0]);
		images[baby][1] = ModLoader.flipImage(images[baby][0]);

		imgW = new int[3];
		imgH = new int[3];
		pivX = new int[3];
		pivY = new int[3];
		for (int i = 0; i < 3; i++) {
			imgW[i] = images[i][0].getWidth(io);
			imgH[i] = images[i][0].getHeight(io);
			pivX[i] = imgW[i] >> 1;
			pivY[i] = imgH[i] - 1;
		}
	}

	@Override
	protected TickResult update() {
		Yukkuri pa = org.simyukkuri.util.YukkuriLookup.getYukkuriById(parent);
		if (pa == null)
			return TickResult.NONE;
		if (pa.isDead()) {
			// 死んだゆっくりはうんうんしない
			return TickResult.NONE;
		}
		// ちぎれていない場合
		if (pa.getCriticalDamageType() != CriticalDamageType.CUT) {
			// 常にうんうんを足す
			pa.plusShit(50);
			pa.wakeup();
		}
		pa.setHappiness(Happiness.SAD);
		if (GameRandom.nextInt(1000) == 0) {
			pa.clearActions();
			pa.setCalm();
			pa.setHappiness(Happiness.VERY_SAD);
			pa.addDamage(200);
			pa.setForceFace(ImageCode.PAIN.ordinal());
			pa.setMessage(GameMessages.getMessage(pa, MessagePool.Action.PoisonDamage), 20, true, true);
		}
		return TickResult.NONE;
	}

	@Override
	public BufferedImage getImage(Yukkuri b) {
		Yukkuri pa = org.simyukkuri.util.YukkuriLookup.getYukkuriById(parent);
		if (pa == null)
			return null;
		if (b.getDirection() == Direction.RIGHT) {
			return images[pa.getAgeState().ordinal()][1];
		}
		return images[pa.getAgeState().ordinal()][0];
	}

	@Override
	public void resetBoundary() {
		Yukkuri pa = org.simyukkuri.util.YukkuriLookup.getYukkuriById(parent);
		if (pa != null) {
			setBoundary(pivX[pa.getAgeState().ordinal()],
					pivY[pa.getAgeState().ordinal()],
					imgW[pa.getAgeState().ordinal()],
					imgH[pa.getAgeState().ordinal()]);
		}
	}

	/**
	 * コンストラクタ
	 * 
	 * @param body 装着されるゆっくり
	 */
	public PoisonAmpoule(Yukkuri body) {
		super(body);
		setAttachProperty(property, POS_KEY);
		Yukkuri pa = org.simyukkuri.util.YukkuriLookup.getYukkuriById(parent);
		if (pa != null) {
			setBoundary(pivX[pa.getAgeState().ordinal()],
					pivY[pa.getAgeState().ordinal()],
					imgW[pa.getAgeState().ordinal()],
					imgH[pa.getAgeState().ordinal()]);
		}
		value = 500;
		cost = 0;
	}

	public PoisonAmpoule() {

	}

	@Override
	public String toString() {
		return GameText.read("item_poison");
	}

	// テスト用静的アクセサ
	public static BufferedImage[][] getImages() {
		return images;
	}

	public static void setImages(BufferedImage[][] images) {
		PoisonAmpoule.images = images;
	}

	public static void setImgW(int[] imgW) {
		PoisonAmpoule.imgW = imgW;
	}

	public static void setImgH(int[] imgH) {
		PoisonAmpoule.imgH = imgH;
	}

	public static void setPivX(int[] pivX) {
		PoisonAmpoule.pivX = pivX;
	}

	public static void setPivY(int[] pivY) {
		PoisonAmpoule.pivY = pivY;
	}

	public static String getPosKey() {
		return POS_KEY;
	}

	public static int[] getProperty() {
		return property;
	}
}
