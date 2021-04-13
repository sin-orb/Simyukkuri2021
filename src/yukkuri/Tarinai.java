package src.yukkuri;


import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import src.base.Body;
import src.base.Okazari;
import src.base.Okazari.OkazariType;
import src.draw.ModLoader;
import src.enums.AgeState;
import src.enums.Attitude;
import src.enums.BodyRank;
import src.enums.ImageCode;
import src.enums.YukkuriType;
import src.system.BodyLayer;
import src.util.IniFileUtil;


public class Tarinai extends Body implements java.io.Serializable {
	static final long serialVersionUID = 1L;
	public static final int type = 2000;
	public static final String nameJ = "たりないゆ";
	public static final String nameE = "Tarinaiyu";
	public static final String baseFileName = "tarinai";

	private static BufferedImage[][][][] imagePack = new BufferedImage[BodyRank.values().length][][][];
	private static BufferedImage[][][] imagesKai = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][] imagesNora = new BufferedImage[ImageCode.values().length][2][3];
	private static int directionOffset[][] = new int[ImageCode.values().length][2];
	private static Dimension[] boundary = new Dimension[3];
	private static Dimension[] braidBoundary = new Dimension[3];
	private static boolean imageLoaded = false;
	private static Map<String, Point[]> AttachOffset = new HashMap<String, Point[]>();
	//---
	// iniファイルから読み込んだ初期値
	private static int baseSpeed = 100;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {

		if(imageLoaded) return;

		boolean res;
		res = ModLoader.loadBodyImagePack(loader, imagesNora, directionOffset, ModLoader.YK_WORD_NORA, baseFileName, io);
		if(!res) {
			imagesNora = null;
		}
		res = ModLoader.loadBodyImagePack(loader, imagesKai, directionOffset, null, baseFileName, io);
		if(!res) {
			imagesKai = null;
		}
		imagePack[BodyRank.KAIYU.imageIndex] = imagesKai;
		if(imagesNora != null) {
			imagePack[BodyRank.NORAYU.imageIndex] = imagesNora;
		} else {
			imagePack[BodyRank.NORAYU.imageIndex] = imagesKai;
		}
		ModLoader.setImageSize(imagesKai, boundary, braidBoundary, io);

		imageLoaded = true;
	}
	public boolean isImageLoaded() {
		return imageLoaded;
	}

	public static void loadIniFile(ClassLoader loader) {
		AttachOffset = ModLoader.loadBodyIniMap(loader, ModLoader.DATA_INI_DIR, baseFileName);
		baseSpeed = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, baseFileName, "speed");
	}

	public int getImage(int type, int direction, BodyLayer layer, int index) {
		layer.image[index] = imagePack[getBodyRank().imageIndex][type][direction * directionOffset[type][0]][getBodyAgeState().ordinal()];
		layer.dir[index] = direction * directionOffset[type][1];
		return 1;
	}

	public Point[] getMountPoint(String key) {
		return AttachOffset.get(key);
	}

	@Override
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

	public String getNameJ() {
		return nameJ;
	}

	public String getMyName() {
		if( anMyName[getBodyAgeState().ordinal()] != null ){
			return anMyName[getBodyAgeState().ordinal()];
		}
		return nameJ;
	}

	public String getMyNameD() {
		if( anMyNameD[getBodyAgeState().ordinal()] != null ){
			return anMyNameD[getBodyAgeState().ordinal()];
		}
		return getMyName();
	}

	public String getNameE() {
		return nameE;
	}

	@Override
	public String getNameJ2() {
		return "";
	}

	@Override
	public String getNameE2() {
		return "";
	}

	// public methods
	public Tarinai(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.TARINAI);
		setShitType(YukkuriType.TARINAI);
		setBaseBodyFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}

	/**
	 * たりないゆかどうかを判定する.
	 * @return たりないゆかどうか
	 */
	@Override
	public boolean isIdiot() {
		return true;
	}
	
	/**
	 * お飾りをあげたときの反応を記述する.
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

	
	public void tuneParameters() {
		setOkazari(null);
		setAttitude(Attitude.SUPER_SHITHEAD);
		double factor = Math.random()*2+1;
		HUNGRYLIMIT[AgeState.ADULT.ordinal()] *= factor;
		HUNGRYLIMIT[AgeState.CHILD.ordinal()] *= factor;
		HUNGRYLIMIT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()*2+1;
		SHITLIMIT[AgeState.ADULT.ordinal()] *= factor;
		SHITLIMIT[AgeState.CHILD.ordinal()] *= factor;
		SHITLIMIT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()+0.5;
		DAMAGELIMIT[AgeState.ADULT.ordinal()] *= factor;
		DAMAGELIMIT[AgeState.CHILD.ordinal()] *= factor;
		DAMAGELIMIT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()+0.5;
		BABYLIMIT *= factor;
		CHILDLIMIT *= factor;
		LIFELIMIT *= factor;
		factor = Math.random()+1;
		RELAXPERIOD *= factor;
		EXCITEPERIOD *= factor;
		PREGPERIOD *= factor;
		SLEEPPERIOD *= factor;
		ACTIVEPERIOD *= factor;
		sameDest = RND.nextInt(20)+20;
		DECLINEPERIOD *= (Math.random()+0.5);
		ROBUSTNESS = RND.nextInt(5)+1;
		EYESIGHT /= 8;
		factor = Math.random()+1;
		STRENGTH[AgeState.ADULT.ordinal()] *= factor;
		STRENGTH[AgeState.CHILD.ordinal()] *= factor;
		STRENGTH[AgeState.BABY.ordinal()] *= factor;
		speed = baseSpeed;
		setBraidType(false);
	}
}