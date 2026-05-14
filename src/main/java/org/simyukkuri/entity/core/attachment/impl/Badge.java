package org.simyukkuri.entity.core.attachment.impl;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.simyukkuri.draw.ModLoader;
import org.simyukkuri.entity.core.attachment.Attachment;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.AttachProperty;
//import org.simyukkuri.Attachment.AttachProperty;
import org.simyukkuri.enums.Event;
import org.simyukkuri.util.GameText;

/****************************************
 * バッジ
 * 
 */
public class Badge extends Attachment {

	private static final long serialVersionUID = -3180311818627859673L;
	private static final String POS_KEY = "Badge";
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

	/** バッジランク定義 */
	public static enum BadgeRank {
		FAKE("fake.png"),
		BRONZE("bronze.png"),
		SILVER("silver.png"),
		GOLD("gold.png"),
		;

		private final String fileName;

		BadgeRank(String fn) {
			fileName = fn;
		}

		public String getFileName() {
			return fileName;
		}
	}

	/**
	 * 画像の入れ物
	 * <br>
	 * [親オブジェクト(ゆっくり)の年齢][ランク]
	 */
	private static BufferedImage[][] images = new BufferedImage[3][BadgeRank.values().length];
	/** ランクの入れ物 */
	@JsonProperty("badgeRank")
	private BadgeRank badgeRank;

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

		for (BadgeRank i : BadgeRank.values()) {
			if (i.getFileName() == null)
				continue;

			images[adult][i.ordinal()] = ModLoader.loadItemImage(loader, "badge" + File.separator + i.getFileName());
			int w = images[adult][i.ordinal()].getWidth(io);
			int h = images[adult][i.ordinal()].getHeight(io);
			images[child][i.ordinal()] = ModLoader.scaleImage(images[adult][i.ordinal()],
					w / property[AttachProperty.CHILD_SIZE.ordinal()],
					h / property[AttachProperty.CHILD_SIZE.ordinal()]);
			images[baby][i.ordinal()] = ModLoader.scaleImage(images[adult][i.ordinal()],
					w / property[AttachProperty.BABY_SIZE.ordinal()], h / property[AttachProperty.BABY_SIZE.ordinal()]);
		}

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
	protected Event update() {
		return Event.DONOTHING;
	}

	@Override
	public BufferedImage getImage(Yukkuri b) {
		Yukkuri pa = org.simyukkuri.util.YukkuriLookup.getYukkuriById(parent);
		if (pa == null)
			return null;
		return images[pa.getAgeState().ordinal()][badgeRank.ordinal()];
	}

	/** バッジランク取得 */
	@Transient
	@JsonIgnore
	public BadgeRank getBadgeRank() {
		return badgeRank;
	}

	@Override
	public void resetBoundary() {
		Yukkuri pa = org.simyukkuri.util.YukkuriLookup.getYukkuriById(parent);
		if (pa == null)
			return;
		setBoundary(pivX[pa.getAgeState().ordinal()],
				pivY[pa.getAgeState().ordinal()],
				imgW[pa.getAgeState().ordinal()],
				imgH[pa.getAgeState().ordinal()]);
	}

	/**
	 * コンストラクタ.
	 * 
	 * @param body        装着されるゆっくり
	 * @param ieBadgeRank バッジランク
	 */
	public Badge(Yukkuri body, BadgeRank badgeRank) {
		super(body);
		setAttachProperty(property, POS_KEY);
		this.badgeRank = badgeRank;
		Yukkuri pa = org.simyukkuri.util.YukkuriLookup.getYukkuriById(parent);
		if (pa != null) {
			setBoundary(pivX[pa.getAgeState().ordinal()],
					pivY[pa.getAgeState().ordinal()],
					imgW[pa.getAgeState().ordinal()],
					imgH[pa.getAgeState().ordinal()]);
		}
		value = 0;
		cost = 0;
	}

	public Badge() {

	}

	@Override
	public String toString() {
		return GameText.read("item_badge");
	}

	public void setBadgeRank(BadgeRank badgeRank) {
		this.badgeRank = badgeRank;
	}

	// テスト用静的アクセサ
	public static BufferedImage[][] getImages() {
		return images;
	}

	public static void setImages(BufferedImage[][] images) {
		Badge.images = images;
	}

	public static void setImgW(int[] imgW) {
		Badge.imgW = imgW;
	}

	public static void setImgH(int[] imgH) {
		Badge.imgH = imgH;
	}

	public static void setPivX(int[] pivX) {
		Badge.pivX = pivX;
	}

	public static void setPivY(int[] pivY) {
		Badge.pivY = pivY;
	}

	public static String getPosKey() {
		return POS_KEY;
	}

	public static int[] getProperty() {
		return property;
	}
}
