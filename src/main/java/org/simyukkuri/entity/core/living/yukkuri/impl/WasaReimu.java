package org.simyukkuri.entity.core.living.yukkuri.impl;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.simyukkuri.draw.Dimension4y;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.YukkuriLayer;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.IniFileUtil;

/**
 * わされいむ
 */
public class WasaReimu extends Reimu {
	private static final long serialVersionUID = -6456378316922420937L;
	/** わされいむのタイプ */
	public static final YukkuriType type = YukkuriType.WASAREIMU;
	/** わされいむ和名 */
	public static final String nameJ = "れいむ";
	/** わされいむ英名 */
	public static final String nameE = "Reimu";
	/** わされいむベースファイル名 */
	public static final String baseFileName = "wasa";

	private static BufferedImage[][][][] imagePack = new BufferedImage[YukkuriRank.values().length][][][];
	private static BufferedImage[][][] imagesKai = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][] imagesNora = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][][] imagesNagasi = new BufferedImage[ImageCode.values().length][2][3][ModLoader
			.getMaxImgOtherVer() + 1];
	private static int[][] directionOffset = new int[ImageCode.values().length][2];
	private static int[][] directionOffsetNagasi = new int[ImageCode.values().length][2];
	private static Dimension4y[] boundary = new Dimension4y[3];
	private static Dimension4y[] braidBoundary = new Dimension4y[3];
	private static boolean imageLoaded = false;
	private static Map<String, Point4y[]> AttachOffset = new HashMap<String, Point4y[]>();
	// ---
	// iniファイルから読み込んだ初期値
	private static int baseSpeed = 100;
	// 個別表情管理(まりちゃ流し用)
	private int[][] imageVariantState = new int[ImageCode.values().length][2];

	/** イメージのロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		if (imageLoaded) {
			return;
		}

		boolean res;
		res = ModLoader.loadYukkuriImagePack(loader, imagesNora, directionOffset, ModLoader.getYkWordNora(), baseFileName,
				io);
		if (!res) {
			imagesNora = null;
		}
		res = ModLoader.loadYukkuriImagePack(loader, imagesKai, directionOffset, null, baseFileName, io);
		if (!res) {
			imagesKai = null;
		}
		imagePack[YukkuriRank.KAIYU.getImageIndex()] = imagesKai;
		if (imagesNora != null) {
			imagePack[YukkuriRank.NORAYU.getImageIndex()] = imagesNora;
		} else {
			imagePack[YukkuriRank.NORAYU.getImageIndex()] = imagesKai;
		}
		res = ModLoader.loadYukkuriImagePack(loader, imagesNagasi, directionOffsetNagasi, ModLoader.getYkWordNagasi(),
				baseFileName, io);
		if (!res) {
			imagesNagasi = null;
		}

		ModLoader.setImageSize(imagesKai, boundary, braidBoundary, io);

		imageLoaded = true;
	}

	/** INIファイルのロード */
	public static void loadIniFile(ClassLoader loader) {
		AttachOffset = ModLoader.loadYukkuriIniOffsets(loader, ModLoader.getDataIniDir(), baseFileName);
		baseSpeed = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataIniDir(), baseFileName, "speed");
	}

	/** 画像が読み込み済みかを返す。 */
	@Override
	@Transient
	public boolean isImageLoaded() {
		return imageLoaded;
	}

	/** 現在の表示状態に基づく画像をレイヤーにセットし、画像番号を返す。 */
	@Override
	public int getImage(int type, int direction, YukkuriLayer layer, int index) {
		if (!isImageNagasiMode() || imagesNagasi == null) {
			layer.getImage()[index] = imagePack[getRank().getImageIndex()][type][direction
					* directionOffset[type][0]][getAgeState().ordinal()];
			layer.getDir()[index] = direction * directionOffset[type][1];
		} else {
			// インターバル毎に初期化する
			if (GameEnvironment.getInterval() == 0 && !isDead()) {
				for (int i = 0; i < ImageCode.values().length; i++) {
					imageVariantState[i][1] = 0;
				}
			}

			// 前回と同じ表示
			if (imageVariantState[type][1] == 1) {
				int imageIndex = imageVariantState[type][0];
				layer.getImage()[index] = imagesNagasi[type][direction
						* directionOffsetNagasi[type][0]][getAgeState().ordinal()][imageIndex];

			} else {
				int otherVersionCount = 0;
				for (int i = 0; i < ModLoader.getMaxImgOtherVer(); i++) {
					if (imagesNagasi[type][direction * directionOffsetNagasi[type][0]][getAgeState().ordinal()][i
							+ 1] != null) {
						otherVersionCount++;
					}
				}

				if (otherVersionCount != 0) {
					int randomIndex = GameRandom.nextInt(otherVersionCount + 1);
					imageVariantState[type][0] = randomIndex;
					layer.getImage()[index] = imagesNagasi[type][direction
							* directionOffsetNagasi[type][0]][getAgeState().ordinal()][randomIndex];
				} else {
					imageVariantState[type][0] = 0;
					layer.getImage()[index] = imagesNagasi[type][direction
							* directionOffsetNagasi[type][0]][getAgeState().ordinal()][0];
				}

				imageVariantState[type][1] = 1;
			}

			layer.getDir()[index] = direction * directionOffsetNagasi[type][1];
		}
		return 1;
	}

	/** アタッチメントキーに対応する取り付け点座標を返す。 */
	@Override
	public Point4y[] getMountPoint(String key) {
		return AttachOffset.get(key);
	}

	/** ゆっくりの種別を返す。 */
	@Override
	@Transient
	public YukkuriType getType() {
		return type;
	}

	/** コンストラクタ */
	public WasaReimu(int initX, int initY, int initZ, AgeState initAgeState, Yukkuri p1, Yukkuri p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.REIMU);
		setShitType(YukkuriType.REIMU);
		speed = baseSpeed;
		setBaseYukkuriFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}

	/** わされいむ のデフォルトコンストラクタ。 */
	public WasaReimu() {

	}

	@Override
	public void reinitializeBoundary() {
		setBoundary(boundary, braidBoundary);
	}

	/** 流し絵モード用の画像バリアント状態を返す。 */
	public int[][] getImageVariantState() {
		return imageVariantState;
	}

	/** 流し絵モード用の画像バリアント状態をセットする。 */
	public void setImageVariantState(int[][] imageVariantState) {
		this.imageVariantState = imageVariantState;
	}

}
