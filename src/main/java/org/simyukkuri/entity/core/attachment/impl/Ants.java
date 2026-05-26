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
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.util.GameText;

/**
 * アリ
 *
 */
public class Ants extends Attachment {

	private static final long serialVersionUID = -6854644108381452452L;
	/** 識別キー */
	private static final String POS_KEY = "Ants";
	/**
	 * 画像の入れ物
	 * <br>
	 * [年齢][進行度]
	 */
	private static BufferedImage[][] images;
	/** 画像のサイズ */
	private static int[] imgW;
	private static int[] imgH;
	/** 画像の描画原点の座標 */
	private static int[] pivX;
	private static int[] pivY;
	/** 継承元のenum AttachProperty の代入値 */
	private static final int[] property = {
			4, // 赤ゆ用画像サイズ 原画をこの値で割る
			2, // 子ゆ用画像サイズ
			1, // 成ゆ用画像サイズ
			1, // 親オブジェクトの位置基準 0:顔とお飾り向けの元サイズ 1:妊娠などの膨らみも含むサイズ
			0, // アニメ速度
			0, // アニメループ回数
			1 // アニメ画像枚数
	};

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		int baby = AgeState.BABY.ordinal();
		int child = AgeState.CHILD.ordinal();
		int adult = AgeState.ADULT.ordinal();
		images = new BufferedImage[3][3];

		for (int i = 0; i < 3; i++) {
			images[adult][i] = ModLoader.loadItemImage(loader, "animal" + File.separator + "Ants_" + i + ".png");
		}
		int w = images[adult][0].getWidth(io);
		int h = images[adult][0].getHeight(io);
		for (int i = 0; i < images[adult].length; i++) {
			images[child][i] = ModLoader.scaleImage(images[adult][i], w / property[AttachProperty.CHILD_SIZE.ordinal()],
					h / property[AttachProperty.CHILD_SIZE.ordinal()]);
			images[baby][i] = ModLoader.scaleImage(images[adult][i], w / property[AttachProperty.BABY_SIZE.ordinal()],
					h / property[AttachProperty.BABY_SIZE.ordinal()]);
		}
		imgW = new int[3];
		imgH = new int[3];
		pivX = new int[3];
		pivY = new int[3];
		for (int i = 0; i < 3; i++) {
			if (images[i][0] == null) {
				continue;
			}
			imgW[i] = images[i][0].getWidth(io);
			imgH[i] = images[i][0].getHeight(io);
			pivX[i] = imgW[i] >> 1;
			pivY[i] = imgH[i] - 1;
		}
	}

	/** アリのティック処理。親ゆっくりのアリ数に応じてダメージを与える。 */
	@Override
	protected TickResult update() {
		Yukkuri pa = org.simyukkuri.util.YukkuriLookup.getYukkuriById(parent);
		if (pa == null) {
			return TickResult.NONE;
		}
		pa.beEaten((pa.getAntCount() / 3), 0, false);
		return TickResult.NONE;
	}

	/** アリの進行度に応じた画像を返す。 */
	@Override
	public BufferedImage getImage(Yukkuri b) {
		int ants = b.getAntCount();
		if (ants >= b.getDamageLimit() * 2 / 3) {
			return images[b.getAgeState().ordinal()][2];
		} else if (ants >= b.getDamageLimit() / 3) {
			return images[b.getAgeState().ordinal()][1];
		}
		return images[b.getAgeState().ordinal()][0];
	}

	/** 親ゆっくりの年齢層に応じて境界ボックスをリセットする。 */
	@Override
	public void resetBoundary() {
		Yukkuri pa = org.simyukkuri.util.YukkuriLookup.getYukkuriById(parent);
		if (pa == null) {
			return;
		}
		setBoundary(pivX[pa.getAgeState().ordinal()],
				pivY[pa.getAgeState().ordinal()],
				imgW[pa.getAgeState().ordinal()],
				imgH[pa.getAgeState().ordinal()]);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param body 装着されるゆっくり
	 */
	public Ants(Yukkuri body) {
		super(body);
		setAttachProperty(property, POS_KEY);
		Yukkuri pa = org.simyukkuri.util.YukkuriLookup.getYukkuriById(parent);
		if (pa != null) {
			setBoundary(pivX[pa.getAgeState().ordinal()],
					pivY[pa.getAgeState().ordinal()],
					imgW[pa.getAgeState().ordinal()],
					imgH[pa.getAgeState().ordinal()]);
			pa.setAntCount(50);
		}
		value = 0;
		cost = 0;

		// 処理インターヴァルの変更
		processInterval = 100;
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public Ants() {

	}

	/** アイテム名テキストを返す。 */
	@Override
	public String toString() {
		return GameText.read("item_ants");
	}

	// テスト用静的アクセサ
	/** 画像配列を返す。 */
	public static BufferedImage[][] getImages() {
		return images;
	}

	/** 画像配列をセットする。 */
	public static void setImages(BufferedImage[][] images) {
		Ants.images = images;
	}

	/** 画像幅配列をセットする。 */
	public static void setImgW(int[] imgW) {
		Ants.imgW = imgW;
	}

	/** 画像高さ配列をセットする。 */
	public static void setImgH(int[] imgH) {
		Ants.imgH = imgH;
	}

	/** 画像原点 X 配列をセットする。 */
	public static void setPivX(int[] pivX) {
		Ants.pivX = pivX;
	}

	/** 画像原点 Y 配列をセットする。 */
	public static void setPivY(int[] pivY) {
		Ants.pivY = pivY;
	}

	/** 位置キー文字列を返す。 */
	public static String getPosKey() {
		return POS_KEY;
	}

	/** アタッチメントプロパティ配列を返す。 */
	public static int[] getProperty() {
		return property;
	}
}
