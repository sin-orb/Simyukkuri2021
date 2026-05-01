package src.yukkuri;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import src.SimYukkuri;
import src.util.GameRandom;
import src.base.Body;
import src.base.Okazari;
import src.base.Okazari.OkazariType;
import src.draw.Dimension4y;
import src.draw.ModLoader;
import src.draw.Point4y;
import src.enums.AgeState;
import src.enums.Attitude;
import src.enums.BodyRank;
import src.enums.ImageCode;
import src.enums.YukkuriType;
import src.system.BodyLayer;
import src.util.IniFileUtil;

/**
 * たりないゆ
 */
public class Tarinai extends Body {
	private static final long serialVersionUID = 1862934023339026324L;
	/** たりないゆのタイプ */
	public static final int type = 2000;
	/** たりないゆ和名 */
	public static final String nameJ = "たりないゆ";
	/** たりないゆ英名 */
	public static final String nameE = "Tarinaiyu";
	/** たりないゆベースファイル名 */
	public static final String baseFileName = "tarinai";

	private static BufferedImage[][][][] imagePack = new BufferedImage[BodyRank.values().length][][][];
	private static BufferedImage[][][] imagesKai = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][] imagesNora = new BufferedImage[ImageCode.values().length][2][3];
	private static int directionOffset[][] = new int[ImageCode.values().length][2];
	private static Dimension4y[] boundary = new Dimension4y[3];
	private static Dimension4y[] braidBoundary = new Dimension4y[3];
	private static boolean imageLoaded = false;
	private static Map<String, Point4y[]> AttachOffset = new HashMap<String, Point4y[]>();
	// ---
	// iniファイルから読み込んだ初期値
	private static int baseSpeed = 100;

	/** イメージのロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		if (imageLoaded)
			return;

		boolean res;
		res = ModLoader.loadBodyImagePack(loader, imagesNora, directionOffset, ModLoader.getYkWordNora(), baseFileName,
				io);
		if (!res) {
			imagesNora = null;
		}
		res = ModLoader.loadBodyImagePack(loader, imagesKai, directionOffset, null, baseFileName, io);
		if (!res) {
			imagesKai = null;
		}
		imagePack[BodyRank.KAIYU.getImageIndex()] = imagesKai;
		if (imagesNora != null) {
			imagePack[BodyRank.NORAYU.getImageIndex()] = imagesNora;
		} else {
			imagePack[BodyRank.NORAYU.getImageIndex()] = imagesKai;
		}
		ModLoader.setImageSize(imagesKai, boundary, braidBoundary, io);

		imageLoaded = true;
	}

	@Override
	@Transient
	public boolean isImageLoaded() {
		return imageLoaded;
	}

	/** INIファイルをロードする */
	public static void loadIniFile(ClassLoader loader) {
		AttachOffset = ModLoader.loadBodyIniMap(loader, ModLoader.getDataIniDir(), baseFileName);
		baseSpeed = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataIniDir(), baseFileName, "speed");
	}

	@Override
	public int getImage(int type, int direction, BodyLayer layer, int index) {
		layer.getImage()[index] = imagePack[getBodyRank().getImageIndex()][type][direction
				* directionOffset[type][0]][getBodyAgeState().ordinal()];
		layer.getDir()[index] = direction * directionOffset[type][1];
		return 1;
	}

	@Override
	public Point4y[] getMountPoint(String key) {
		return AttachOffset.get(key);
	}

	@Override
	@Transient
	public int getType() {
		return type;
	}

	@Override
	public int getHybridType(int partnerType) {
		switch (partnerType) {
			case Marisa.type:
				return MarisaReimu.type;
			default:
				return Tarinai.type;
		}
	}

	@Override
	@Transient
	public String getNameJ() {
		return nameJ;
	}

	@Override
	@Transient
	public String getMyName() {
		if (getAnMyName()[getBodyAgeState().ordinal()] != null) {
			return getAnMyName()[getBodyAgeState().ordinal()];
		}
		return nameJ;
	}

	@Override
	@Transient
	public String getMyNameD() {
		if (getAnMyNameD()[getBodyAgeState().ordinal()] != null) {
			return getAnMyNameD()[getBodyAgeState().ordinal()];
		}
		return getMyName();
	}

	@Override
	@Transient
	public String getNameE() {
		return nameE;
	}

	@Override
	@Transient
	public String getNameJ2() {
		return "";
	}

	@Override
	@Transient
	public String getNameE2() {
		return "";
	}

	/** コンストラクタ */
	public Tarinai(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.TARINAI);
		setShitType(YukkuriType.TARINAI);
		setBaseBodyFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}

	public Tarinai() {

	}

	/**
	 * たりないゆかどうかを判定する.
	 * 
	 * @return たりないゆかどうか
	 */
	@Override
	@Transient
	public boolean isIdiot() {
		return true;
	}

	/**
	 * お飾りをあげたときの反応を記述する.
	 * 
	 * @param type お飾りのタイプ
	 */
	public void giveOkazari(OkazariType type) {
		if (type == OkazariType.DEFAULT) {
			OkazariType newType = Okazari.getRandomOkazari(getBodyAgeState());
			setOkazari(new Okazari(this, newType));
			// なつき度設定
			addLovePlayer(10);
			setbNoticeNoOkazari(false);
			return;
		} else {
			super.giveOkazari(type);
		}
	}

	@Override
	public void tuneParameters() {
		setOkazari(null);
		setAttitude(Attitude.SUPER_SHITHEAD);
		double factor = Math.random() * 2 + 1;
		getHUNGRYLIMITorg()[AgeState.ADULT.ordinal()] *= factor;
		getHUNGRYLIMITorg()[AgeState.CHILD.ordinal()] *= factor;
		getHUNGRYLIMITorg()[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() * 2 + 1;
		getSHITLIMITorg()[AgeState.ADULT.ordinal()] *= factor;
		getSHITLIMITorg()[AgeState.CHILD.ordinal()] *= factor;
		getSHITLIMITorg()[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() + 0.5;
		getDAMAGELIMITorg()[AgeState.ADULT.ordinal()] *= factor;
		getDAMAGELIMITorg()[AgeState.CHILD.ordinal()] *= factor;
		getDAMAGELIMITorg()[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() + 0.5;
		setBABYLIMITorg((int) (getBABYLIMITorg() * factor));
		setCHILDLIMITorg((int) (getCHILDLIMITorg() * factor));
		setLIFELIMITorg((int) (getLIFELIMITorg() * factor));
		factor = Math.random() + 1;
		setRELAXPERIODorg((int) (getRELAXPERIODorg() * factor));
		setEXCITEPERIODorg((int) (getEXCITEPERIODorg() * factor));
		setPREGPERIODorg((int) (getPREGPERIODorg() * factor));
		setSLEEPPERIODorg((int) (getSLEEPPERIODorg() * factor));
		setACTIVEPERIODorg((int) (getACTIVEPERIODorg() * factor));
		setSameDest(GameRandom.nextInt(20) + 20);
		setDECLINEPERIODorg((int) (getDECLINEPERIODorg() * (Math.random() + 0.5)));
		setROBUSTNESS(GameRandom.nextInt(5) + 1);
		setEYESIGHTorg(getEYESIGHTorg() / 8);
		factor = Math.random() + 1;
		getSTRENGTHorg()[AgeState.ADULT.ordinal()] *= factor;
		getSTRENGTHorg()[AgeState.CHILD.ordinal()] *= factor;
		getSTRENGTHorg()[AgeState.BABY.ordinal()] *= factor;
		speed = baseSpeed;
		setBraidType(false);
	}

}
