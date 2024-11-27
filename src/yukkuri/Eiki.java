package src.yukkuri;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import src.SimYukkuri;
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
 * えーき
 */
public class Eiki extends Body implements java.io.Serializable {
	private static final long serialVersionUID = -8442445086608413455L;
	/** えーきのタイプ */
	public static final int type = 1007;
	/** えーき和名 */
	public static final String nameJ = "えーき";
	/** えーき英名 */
	public static final String nameE = "Eiki";
	/** えーきベースファイル名 */
	public static final String baseFileName = "eiki";

	private static BufferedImage[][][][] imagePack = new BufferedImage[BodyRank.values().length][][][];
	private static BufferedImage[][][] imagesKai = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][] imagesNora = new BufferedImage[ImageCode.values().length][2][3];
	private static int directionOffset[][] = new int[ImageCode.values().length][2];
	private static Dimension4y[] boundary = new Dimension4y[3];
	private static Dimension4y[] braidBoundary = new Dimension4y[3];
	private static boolean imageLoaded = false;
	private static Map<String, Point4y[]> AttachOffset = new HashMap<String, Point4y[]>();
	//---
	// iniファイルから読み込んだ初期値
	private static int baseSpeed = 100;
	/** イメージをロードする */
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
	@Override
	@Transient
	public boolean isImageLoaded() {
		return imageLoaded;
	}
	/**
	 * INIファイルをロードする
	 * @param loader クラスローダ
	 */
	public static void loadIniFile(ClassLoader loader) {
		AttachOffset = ModLoader.loadBodyIniMap(loader, ModLoader.DATA_INI_DIR, baseFileName);
		baseSpeed = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, baseFileName, "speed");
	}
	@Override
	public int getImage(int type, int direction, BodyLayer layer, int index) {
		layer.image[index] = imagePack[getBodyRank().imageIndex][type][direction * directionOffset[type][0]][getBodyAgeState().ordinal()];
		layer.dir[index] = direction * directionOffset[type][1];
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
	public String getNameJ() {
		return nameJ;
	}
	@Override
	@Transient
	public String getMyName() {
		if( anMyName[getBodyAgeState().ordinal()] != null ){
			return anMyName[getBodyAgeState().ordinal()];
		}
		return nameJ;
	}
	@Override
	@Transient
	public String getMyNameD() {
		if( anMyNameD[getBodyAgeState().ordinal()] != null ){
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
	public Eiki(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.EIKI);
		setShitType(YukkuriType.EIKI);
		setBaseBodyFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}
	public Eiki() {
		
	}
	@Override
	public void tuneParameters() {
		/*if (rnd.nextBoolean()) {
		motherhood = true;
		}*/
		double factor = Math.random()*2+1;
		HUNGRYLIMITorg[AgeState.ADULT.ordinal()] *= factor;
		HUNGRYLIMITorg[AgeState.CHILD.ordinal()] *= factor;
		HUNGRYLIMITorg[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()*2+1;
		SHITLIMITorg[AgeState.ADULT.ordinal()] *= factor;
		SHITLIMITorg[AgeState.CHILD.ordinal()] *= factor;
		SHITLIMITorg[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()+0.5;
		DAMAGELIMITorg[AgeState.ADULT.ordinal()] *= factor;
		DAMAGELIMITorg[AgeState.CHILD.ordinal()] *= factor;
		DAMAGELIMITorg[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()+0.5;
		BABYLIMITorg *= factor;
		CHILDLIMITorg *= factor;
		LIFELIMITorg *= factor;
		factor = Math.random()+1;
		RELAXPERIODorg *= factor;
		EXCITEPERIODorg *= factor;
		PREGPERIODorg *= factor;
		SLEEPPERIODorg *= factor;
		ACTIVEPERIODorg *= factor;
		sameDest = SimYukkuri.RND.nextInt(20)+20;
		DECLINEPERIODorg *= (Math.random()+0.5);
		ROBUSTNESS = SimYukkuri.RND.nextInt(10)+1;
		//EYESIGHT /= 4;
		factor = Math.random()+0.5;
		STRENGTHorg[AgeState.ADULT.ordinal()] *= factor;
		STRENGTHorg[AgeState.CHILD.ordinal()] *= factor;
		STRENGTHorg[AgeState.BABY.ordinal()] *= factor;

		speed = baseSpeed;
	}
}