package src.yukkuri;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import src.SimYukkuri;
import src.base.Body;
import src.draw.Dimension4y;
import src.draw.ModLoader;
import src.draw.Point4y;
import src.draw.Terrarium;
import src.enums.AgeState;
import src.enums.BodyRank;
import src.enums.ImageCode;
import src.enums.YukkuriType;
import src.system.BodyLayer;
import src.util.IniFileUtil;
/**
 * まりさつむり
 */
public class MarisaTsumuri extends Marisa implements java.io.Serializable {
	static final long serialVersionUID = 2L;
	/** まりさつむりのタイプ */
	public static final int type = 2002;
	/** まりさつむり和名 */
	public static final String nameJ = "まりさ";
	/** まりさつむり英名 */
	public static final String nameE = "Marisa";
	/** まりさつむりベースファイル名 */
	public static final String baseFileName = "marisa_tumuri";
	/** 購入基本額 */
	public static int Ycost = 250;
	/** 売却基本額 */
	public static int YValue = 70;
	/** 素材としての売却基本額 */
	public static int AValue = 100;

	private static BufferedImage[][][][] imagePack = new BufferedImage[BodyRank.values().length][][][];
	private static BufferedImage[][][] imagesKai = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][] imagesNora = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][][] imagesNagasi = new BufferedImage[ImageCode.values().length][2][3][ModLoader.nMaxImgOtherVer + 1];
	private static int directionOffset[][] = new int[ImageCode.values().length][2];
	private static int directionOffsetNagasi[][] = new int[ImageCode.values().length][2];
	private static Dimension4y[] boundary = new Dimension4y[3];
	private static Dimension4y[] braidBoundary = new Dimension4y[3];
	private static boolean imageLoaded = false;
	private static Map<String, Point4y[]> AttachOffset = new HashMap<String, Point4y[]>();
	//---
	// iniファイルから読み込んだ初期値
	private static int baseSpeed = 100;

	private int anImageVerStateCtrlNagasi[][] = new int[ImageCode.values().length][2];
	/** イメージのロード */
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
	/**
	 * INIファイルをロードする
	 * @param loader クラスローダ
	 */
	public static void loadIniFile(ClassLoader loader) {
		AttachOffset = ModLoader.loadBodyIniMap(loader, ModLoader.DATA_INI_DIR, baseFileName);
		baseSpeed = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, baseFileName, "speed");
		Ycost = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, baseFileName, "cost");
		YValue = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, baseFileName, "Value");
		AValue = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, baseFileName, "contentValue");
	}
	@Override
	public boolean isImageLoaded() {
		return imageLoaded;
	}
	@Override
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
					int nRndIndex = SimYukkuri.RND.nextInt(nOtherVerCount+1);
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
	@Override
	public Point4y[] getMountPoint(String key) {
		return AttachOffset.get(key);
	}

	@Override
	public int getType() {
		return type;
	}
	@Override
	public String getNameJ() {
		return nameJ;
	}
	@Override
	public String getMyName() {
		if( anMyName[getBodyAgeState().ordinal()] != null ){
			return anMyName[getBodyAgeState().ordinal()];
		}
		return nameJ;
	}
	@Override
	public String getMyNameD() {
		if( anMyNameD[getBodyAgeState().ordinal()] != null ){
			return anMyNameD[getBodyAgeState().ordinal()];
		}
		return getMyName();
	}
	@Override
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

	/** コンストラクタ */
	public MarisaTsumuri(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.MARISATSUMURI);
		setShitType(YukkuriType.MARISATSUMURI);
		setBaseBodyFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}
	public MarisaTsumuri() {
		
	}
	@Override
	public void tuneParameters() {
		// Tune individual parameters.
		double factor = Math.random()+1;
		HUNGRYLIMITorg[AgeState.ADULT.ordinal()] *= factor; 
		HUNGRYLIMITorg[AgeState.CHILD.ordinal()] *= factor;
		HUNGRYLIMITorg[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()+1;
		SHITLIMITorg[AgeState.ADULT.ordinal()] *= factor;
		SHITLIMITorg[AgeState.CHILD.ordinal()] *= factor;
		SHITLIMITorg[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()*2+1;
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
		sameDest = SimYukkuri.RND.nextInt(10)+10;
		DECLINEPERIODorg *= (Math.random()+0.5);
		ROBUSTNESS = SimYukkuri.RND.nextInt(20)+1;
		//EYESIGHT /= 1;
		factor = Math.random()+1;
		STRENGTHorg[AgeState.ADULT.ordinal()] *= factor;
		STRENGTHorg[AgeState.CHILD.ordinal()] *= factor;
		STRENGTHorg[AgeState.BABY.ordinal()] *= factor;
		
		//speed = 120;
		setLikeWater(true);
		speed = baseSpeed;
	}
}