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


public class Fran extends Body implements java.io.Serializable {
	static final long serialVersionUID = 2L;
	public static final int type = 3001;
	public static final String nameJ = "ふらん";
	public static final String nameE = "Fran";
	public static final String baseFileName = "fran";

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
		ModLoader.setImageSize(imagesKai, boundary, braidBoundary,true,io);

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
		return Fran.type;
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

	//ゆっくりしてる時のアクション
	//個別の動作がある種ははこれをオーバーライドしているので注意
	@Override
	public void killTime(){
		if(getCurrentEvent() != null)return;
		if(getPlaying()!=null)return;
		int p=RND.nextInt(50);
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
				playingLimit = 150 +RND.nextInt(100)-49;
				return;
			}
			else killTime();
		}
		//2/50ですいーで遊ぶ
		else if (p<=41){
			if(ToyLogic.checkSui(this)){
				setPlaying(PlayStyle.SUI);
				playingLimit = 150 +RND.nextInt(100)-49;
				return;
			}
			else killTime();
		}
		else{
			// おくるみありで汚れていない場合
			if( isHasPants() && !isDirty() && RND.nextInt(10) == 0 ){
				setMessage(MessagePool.getMessage(this, MessagePool.Action.RelaxOkurumi));
			}
			else{
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Relax));
			}
			addStress(-50);
			stay(30);
		}
	}

	// public methods
	public Fran(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.FRAN);
		setShitType(YukkuriType.FRAN);
		setBaseBodyFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}

	public void tuneParameters() {
		/*if (rnd.nextBoolean()) {
		motherhood = true;
		}*/
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
		ROBUSTNESS = RND.nextInt(15)+1;
		//EYESIGHT /= 4;
		factor = Math.random()+0.5;
		STRENGTH[AgeState.ADULT.ordinal()] *= factor;
		STRENGTH[AgeState.CHILD.ordinal()] *= factor;
		STRENGTH[AgeState.BABY.ordinal()] *= factor;

		//speed = 150;
		setFlyingType(true);
		setPredatorType(PredatorType.SUCTION);

		z = (int)(Translate.mapZ * Translate.flyLimit);
		speed = baseSpeed;
	}
}