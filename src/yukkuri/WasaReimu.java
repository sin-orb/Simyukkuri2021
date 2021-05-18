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

/**
 * わされいむ
 */
public class WasaReimu extends Reimu implements java.io.Serializable {
	static final long serialVersionUID = 1L;
	/** わされいむのタイプ */
	public static final int type = 2001;
	/** わされいむ和名 */
	public static final String nameJ = "れいむ";
	/** わされいむ英名 */
	public static final String nameE = "Reimu";
	/** わされいむベースファイル名 */
	public static final String baseFileName = "wasa";

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
	// 個別表情管理(まりちゃ流し用)
	private int anImageVerStateCtrlNagasi[][] = new int[ImageCode.values().length][2];
	/** イメージのロード */
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
		res = ModLoader.loadBodyImagePack(loader, imagesNagasi, directionOffsetNagasi, ModLoader.YK_WORD_NAGASI, baseFileName, io);
		if(!res) {
			imagesNagasi = null;
		}

		ModLoader.setImageSize(imagesKai, boundary, braidBoundary, io);

		imageLoaded = true;
	}
	/** INIファイルのロード */
	public static void loadIniFile(ClassLoader loader) {
		AttachOffset = ModLoader.loadBodyIniMap(loader, ModLoader.DATA_INI_DIR, baseFileName);
		baseSpeed = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, baseFileName, "speed");
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
			// インターバル毎に初期化する
			if( Terrarium.getInterval() == 0 && !isDead() )
			{
				for( int i=0; i<ImageCode.values().length; i++ )
				{
					anImageVerStateCtrlNagasi[i][1] = 0;
				}
			}

			// 前回と同じ表示
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
	@Override
	public Point[] getMountPoint(String key) {
		return AttachOffset.get(key);
	}

	@Override
	public int getType() {
		return type;
	}

	/** コンストラクタ */
	public WasaReimu(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.REIMU);
		setShitType(YukkuriType.REIMU);
		speed = baseSpeed;
		setBaseBodyFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}
}

