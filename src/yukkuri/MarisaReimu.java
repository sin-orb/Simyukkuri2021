package src.yukkuri;


import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import src.base.Body;
import src.draw.ModLoader;
import src.draw.Terrarium;
import src.enums.AgeState;
import src.enums.BodyRank;
import src.enums.ImageCode;
import src.enums.YukkuriType;
import src.system.BodyLayer;
import src.util.IniFileUtil;

public class MarisaReimu extends Reimu implements java.io.Serializable
{
	static final long serialVersionUID = 1L;
	public static final int type = 10000;
	public static final String nameJ = "まりされいむ";
	public static final String nameE = "MarisaReimu";
	public static final String baseFileName = "marisa_reimu";

	private static BufferedImage[][][][] imagePack = new BufferedImage[BodyRank.values().length][][][];
	private static BufferedImage[][][] imagesKai = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][] imagesNora = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][][] imagesNagasi = new BufferedImage[ImageCode.values().length][2][3][ModLoader.nMaxImgOtherVer + 1];
	private static int directionOffset[][] = new int[ImageCode.values().length][2];
	private static int directionOffsetNagasi[][] = new int[ImageCode.values().length][2];
	private static Dimension[] boundary = new Dimension[3];
	private static Dimension[] braidBoundary = new Dimension[3];
	private static boolean imageLoaded = false;
	private static Map<String, Point[]> AttachOffset = new HashMap<String, Point[]>();
	//---
	// iniファイルから読み込んだ初期値
	private static int baseSpeed = 100;
	private int anImageVerStateCtrlNagasi[][] = new int[ImageCode.values().length][2];

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		if(imageLoaded) return;

		boolean res;
		res = ModLoader.loadBodyImagePack(loader, imagesNagasi, directionOffsetNagasi, ModLoader.YK_WORD_NAGASI, baseFileName, io);
		if(!res) {
			imagesNagasi = null;
		}
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

	public static void loadIniFile(ClassLoader loader) {
		AttachOffset = ModLoader.loadBodyIniMap(loader, ModLoader.DATA_INI_DIR, baseFileName);
		baseSpeed = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, baseFileName, "speed");
	}

	public int getImage(int type, int direction, BodyLayer layer, int index) {
		if( !isbImageNagasiMode() || imagesNagasi == null)
		{
			layer.image[index] = imagePack[getBodyRank().imageIndex][type][direction * directionOffset[type][0]][getBodyAgeState().ordinal()];
			layer.dir[index] = direction * directionOffset[type][1];
		}else{
			if( Terrarium.getInterval() == 0 && !isDead() )
			{
				for( int i=0; i<ImageCode.values().length; i++ )
				{
					anImageVerStateCtrlNagasi[i][1] = 0;
				}
			}

			if(anImageVerStateCtrlNagasi[type][1] == 1)
			{
				int nIndex = anImageVerStateCtrlNagasi[type][0];
				layer.image[index] = imagesNagasi[type][direction * directionOffsetNagasi[type][0]][getBodyAgeState().ordinal()][nIndex];
				
			}else{
				int nOtherVerCount = 0;
				for( int i=0; i < ModLoader.nMaxImgOtherVer; i++ )
				{
					if( imagesNagasi[type][direction * directionOffsetNagasi[type][0]][getBodyAgeState().ordinal()][i+1] != null )
					{
						nOtherVerCount++;
					}
				}
				
				if( nOtherVerCount != 0 )
				{
					int nRndIndex = RND.nextInt(nOtherVerCount+1);
					anImageVerStateCtrlNagasi[type][0] = nRndIndex;
					layer.image[index] = imagesNagasi[type][direction * directionOffsetNagasi[type][0]][getBodyAgeState().ordinal()][nRndIndex];
				}else{
					anImageVerStateCtrlNagasi[type][0] = 0;
					layer.image[index] = imagesNagasi[type][direction * directionOffsetNagasi[type][0]][getBodyAgeState().ordinal()][0];
				}
				
				anImageVerStateCtrlNagasi[type][1] = 1;
			}
			
			layer.dir[index] = direction * directionOffsetNagasi[type][1];
		}
		return 1;
	}

	public Point[] getMountPoint(String key) {
		return AttachOffset.get(key);
	}

	@Override
	public int getType() {
		return type;
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
	public boolean isHybrid () {
		return true;
	}
	
	// public methods
	public MarisaReimu(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.MARISAREIMU);
		setShitType(YukkuriType.MARISAREIMU);
		setBaseBodyFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}
	
	public void tuneParameters() {
		/*if (rnd.nextBoolean()) {
		motherhood = true;
		}*/
		// Tune individual parameters.
		double factor = Math.random()+1;
		HUNGRYLIMIT[AgeState.ADULT.ordinal()] *= factor; 
		HUNGRYLIMIT[AgeState.CHILD.ordinal()] *= factor;
		HUNGRYLIMIT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()+0.5;
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
		sameDest = RND.nextInt(10)+10;
		DECLINEPERIOD *= (Math.random()+0.5);
		ROBUSTNESS = RND.nextInt(10)+1;
		//EYESIGHT /= 4;
		factor = Math.random()+0.5;
		STRENGTH[AgeState.ADULT.ordinal()] *= factor;
		STRENGTH[AgeState.CHILD.ordinal()] *= factor;
		STRENGTH[AgeState.BABY.ordinal()] *= factor;

		//speed = 150;
		speed = baseSpeed;
	}
}