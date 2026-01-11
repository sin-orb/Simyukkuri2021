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
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.BodyRank;
import src.enums.ImageCode;
import src.enums.PlayStyle;
import src.enums.PredatorType;
import src.enums.YukkuriType;
import src.event.PredatorsGameEvent;
import src.logic.EventLogic;
import src.logic.ToyLogic;
import src.system.BodyLayer;
import src.system.MessagePool;
import src.util.IniFileUtil;

/**
 * ふらん
 */
public class Fran extends Body implements java.io.Serializable {
	private static final long serialVersionUID = -7839169741238599190L;
	/** ふらんのタイプ */
	public static final int type = 3001;
	/** ふらんの和名 */
	public static final String nameJ = "ふらん";
	/** ふらんの英名 */
	public static final String nameE = "Fran";
	/** ふらんのベースファイル名 */
	public static final String baseFileName = "fran";

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
		res = ModLoader.loadBodyImagePack(loader, imagesNora, directionOffset, ModLoader.getYkWordNora(), baseFileName, io);
		if(!res) {
			imagesNora = null;
		}
		res = ModLoader.loadBodyImagePack(loader, imagesKai, directionOffset, null, baseFileName, io);
		if(!res) {
			imagesKai = null;
		}
		imagePack[BodyRank.KAIYU.getImageIndex()] = imagesKai;
		if(imagesNora != null) {
			imagePack[BodyRank.NORAYU.getImageIndex()] = imagesNora;
		} else {
			imagePack[BodyRank.NORAYU.getImageIndex()] = imagesKai;
		}
		ModLoader.setImageSize(imagesKai, boundary, braidBoundary,true,io);

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
		AttachOffset = ModLoader.loadBodyIniMap(loader, ModLoader.getDataIniDir(), baseFileName);
		baseSpeed = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataIniDir(), baseFileName, "speed");
	}
	@Override
	public int getImage(int type, int direction, BodyLayer layer, int index) {
		layer.getImage()[index] = imagePack[getBodyRank().getImageIndex()][type][direction * directionOffset[type][0]][getBodyAgeState().ordinal()];
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
		return Fran.type;
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

	//ゆっくりしてる時のアクション
	//個別の動作がある種ははこれをオーバーライドしているので注意
	@Override
	public void killTime(){
		if(getCurrentEvent() != null)return;
		if(getPlaying()!=null)return;
		int p=SimYukkuri.RND.nextInt(50);
		//8/50でキリッ
		if (p<=7) {
			getInVain(true);
		}
		//8/50でのびのび
		else if (p<=15){
			// if yukkuri is not rude, she goes into her shell by discipline.
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Nobinobi), 40);
			setNobinobi(true);
			addStress(-30);
			stay(40);
		}
		//8/50でふりふり
		else if (p<=23 && willingFurifuri()) {
			//if yukkuri is rude, she will not do furifuri by discipline.
			setMessage(MessagePool.getMessage(this, MessagePool.Action.FuriFuri), 30);
			setFurifuri(true);
			addStress(-50);
			stay(30);
		}
		//8/50で腹減った
		else if( (p<=31 && isHungry()) || isSoHungry()) {
			// 空腹時
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Hungry), 30);
			stay(30);
		}
		//5/50でおもちゃで遊ぶ
		else if (p<=36){
			EventLogic.addWorldEvent(new PredatorsGameEvent(this, null, null, 1), this, MessagePool.getMessage(this, MessagePool.Action.GameStart));
			return;
		}
		//3/50でトランポリンで遊ぶ
		else if (p<=39){
			if(ToyLogic.checkTrampoline(this)){
				setPlaying(PlayStyle.TRAMPOLINE);
				playingLimit = 150 +SimYukkuri.RND.nextInt(100)-49;
				return;
			}
			else killTime();
		}
		//2/50ですいーで遊ぶ
		else if (p<=41){
			if(ToyLogic.checkSui(this)){
				setPlaying(PlayStyle.SUI);
				playingLimit = 150 +SimYukkuri.RND.nextInt(100)-49;
				return;
			}
			else killTime();
		}
		else{
			// おくるみありで汚れていない場合
			if( isHasPants() && !isDirty() && SimYukkuri.RND.nextInt(10) == 0 ){
				setMessage(MessagePool.getMessage(this, MessagePool.Action.RelaxOkurumi));
			}
			else{
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Relax));
			}
			addStress(-50);
			stay(30);
		}
	}

	/** コンストラクタ */
	public Fran(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.FRAN);
		setShitType(YukkuriType.FRAN);
		setBaseBodyFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}
	public Fran() {
		
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
		ROBUSTNESS = SimYukkuri.RND.nextInt(15)+1;
		//EYESIGHT /= 4;
		factor = Math.random()+0.5;
		STRENGTHorg[AgeState.ADULT.ordinal()] *= factor;
		STRENGTHorg[AgeState.CHILD.ordinal()] *= factor;
		STRENGTHorg[AgeState.BABY.ordinal()] *= factor;

		//speed = 150;
		setFlyingType(true);
		setPredatorType(PredatorType.SUCTION);

		z = (int)(Translate.getMapZ() * Translate.getFlyLimit());
		speed = baseSpeed;
	}
}
