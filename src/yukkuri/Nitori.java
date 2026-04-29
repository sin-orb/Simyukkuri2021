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
import src.draw.Dimension4y;
import src.draw.ModLoader;
import src.draw.Point4y;
import src.enums.AgeState;
import src.enums.BodyRank;
import src.enums.ImageCode;
import src.enums.YukkuriType;
import src.system.BodyLayer;
import src.util.IniFileUtil;

/**
 * にとり
 */
public class Nitori extends Body {
	private static final long serialVersionUID = -4781217373745654846L;
	/** にとりのタイプ */
	public static final int type = 1009;
	/** にとり和名 */
	public static final String nameJ = "にとり";
	/** にとり英名 */
	public static final String nameE = "Nitori";
	/** にとりベースファイル名 */
	public static final String baseFileName = "nitori";

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

	/**
	 * INIファイルをロードする
	 * 
	 * @param loader クラスローダ
	 */
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
	@Transient
	public int getHybridType(int partnerType) {
		switch (partnerType) {
			default:
				return Nitori.type;
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
		if (anMyName[getBodyAgeState().ordinal()] != null) {
			return anMyName[getBodyAgeState().ordinal()];
		}
		return nameJ;
	}

	@Override
	@Transient
	public String getMyNameD() {
		if (anMyNameD[getBodyAgeState().ordinal()] != null) {
			return anMyNameD[getBodyAgeState().ordinal()];
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
	public Nitori(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.NITORI);
		setShitType(YukkuriType.NITORI);
		setBaseBodyFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}

	public Nitori() {

	}

	@Override
	public void tuneParameters() {
		double factor = Math.random() + 1;
		getHUNGRYLIMITorg()[AgeState.ADULT.ordinal()] *= factor;
		getHUNGRYLIMITorg()[AgeState.CHILD.ordinal()] *= factor;
		getHUNGRYLIMITorg()[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() + 1;
		getSHITLIMITorg()[AgeState.ADULT.ordinal()] *= factor;
		getSHITLIMITorg()[AgeState.CHILD.ordinal()] *= factor;
		getSHITLIMITorg()[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() + 1;
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
		setSameDest(GameRandom.nextInt(15) + 15);
		setDECLINEPERIODorg((int) (getDECLINEPERIODorg() * (Math.random() + 0.5)));
		setROBUSTNESS(GameRandom.nextInt(20) + 1);
		// EYESIGHT /= 2;
		factor = Math.random() + 0.5;
		getSTRENGTHorg()[AgeState.ADULT.ordinal()] *= factor;
		getSTRENGTHorg()[AgeState.CHILD.ordinal()] *= factor;
		getSTRENGTHorg()[AgeState.BABY.ordinal()] *= factor;

		setLikeWater(true);
		speed = baseSpeed;
	}
}
