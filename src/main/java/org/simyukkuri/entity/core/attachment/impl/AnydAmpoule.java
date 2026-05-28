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
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.util.GameText;

/**
 * 非ゆっくり症防止アンプル
 */
public class AnydAmpoule extends Attachment {

	private static final long serialVersionUID = -8444054215424177161L;
	/** 識別キー */
	private static final String POS_KEY = "AnydAmpoule";
	/**
	 * 画像の入れ物
	 * <br>
	 * [年齢][左右反転]
	 */
	private static BufferedImage[][] images;
	/** 画像の幅 */
	private static int[] imgW;
	/** 画像の高さ */
	private static int[] imgH;
	/** 画像の描画原点X座標 */
	private static int[] pivX;
	/** 画像の描画原点Y座標 */
	private static int[] pivY;
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
	 * イメージのロード
	 * 
	 * @param loader ローダ
	 * @param io     イメージオブザーバ
	 * @throws IOException IO例外
	 */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		final int baby = AgeState.BABY.ordinal();
		int child = AgeState.CHILD.ordinal();
		int adult = AgeState.ADULT.ordinal();
		images = new BufferedImage[3][2];

		images[adult][0] = ModLoader.loadItemImage(loader, "ampoule" + File.separator + "ANYD.png");

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

	/**
	 * 毎ティック処理。このアンプルは能動的な効果を持たないため何もしない。
	 *
	 * @return 常に {@link TickResult#NONE}
	 */
	@Override
	protected TickResult update() {
		return TickResult.NONE;
	}

	/**
	 * 親ゆっくりの年齢と向きに対応した表示画像を返す。
	 *
	 * @param b 描画対象のゆっくり
	 * @return 表示画像。親が存在しない場合は null
	 */
	@Override
	public BufferedImage getImage(Yukkuri b) {
		Yukkuri pa = org.simyukkuri.util.YukkuriLookup.getYukkuriById(parent);
		if (pa == null) {
			return null;
		}
		if (b.getDirection() == Direction.RIGHT) {
			return images[pa.getAgeState().ordinal()][1];
		}
		return images[pa.getAgeState().ordinal()][0];
	}

	/**
	 * 親ゆっくりの年齢に合わせた当たり判定領域を再設定する。
	 */
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
	public AnydAmpoule(Yukkuri body) {
		super(body);
		setAttachProperty(property, POS_KEY);
		Yukkuri pa = org.simyukkuri.util.YukkuriLookup.getYukkuriById(parent);
		if (pa != null) {
			setBoundary(pivX[pa.getAgeState().ordinal()],
					pivY[pa.getAgeState().ordinal()],
					imgW[pa.getAgeState().ordinal()],
					imgH[pa.getAgeState().ordinal()]);
		}
		value = 1000;
		cost = 0;
	}

	/**
	 * デシリアライズ用のデフォルトコンストラクタ。
	 */
	public AnydAmpoule() {

	}

	/**
	 * ゲーム内表示名（非ゆっくり症防止アンプル）を返す。
	 *
	 * @return ローカライズされたアイテム名
	 */
	@Override
	public String toString() {
		return GameText.read("item_anti_nyd");
	}

	// テスト用静的アクセサ
	/** @return 年齢・向き別の画像配列 */
	public static BufferedImage[][] getImages() {
		return images;
	}

	/**
	 * テスト用: 画像配列を差し替える。
	 *
	 * @param images 差し替える画像配列
	 */
	public static void setImages(BufferedImage[][] images) {
		AnydAmpoule.images = images;
	}

	/**
	 * テスト用: 年齢別画像幅を設定する。
	 *
	 * @param imgW 年齢別画像幅の配列
	 */
	public static void setImgW(int[] imgW) {
		AnydAmpoule.imgW = imgW;
	}

	/**
	 * テスト用: 年齢別画像高さを設定する。
	 *
	 * @param imgH 年齢別画像高さの配列
	 */
	public static void setImgH(int[] imgH) {
		AnydAmpoule.imgH = imgH;
	}

	/**
	 * テスト用: 年齢別描画原点X座標を設定する。
	 *
	 * @param pivX 年齢別ピボットX配列
	 */
	public static void setPivX(int[] pivX) {
		AnydAmpoule.pivX = pivX;
	}

	/**
	 * テスト用: 年齢別描画原点Y座標を設定する。
	 *
	 * @param pivY 年齢別ピボットY配列
	 */
	public static void setPivY(int[] pivY) {
		AnydAmpoule.pivY = pivY;
	}

	/** @return マウントポイント識別キー文字列 */
	public static String getPosKey() {
		return POS_KEY;
	}

	/** @return 描画プロパティ設定値配列 */
	public static int[] getProperty() {
		return property;
	}
}
