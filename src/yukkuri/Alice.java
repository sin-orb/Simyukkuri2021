package src.yukkuri;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import src.SimYukkuri;
import src.base.Body;
import src.command.GadgetAction;
import src.draw.Dimension4y;
import src.draw.ModLoader;
import src.draw.Point4y;
import src.enums.AgeState;
import src.enums.BodyRank;
import src.enums.ImageCode;
import src.enums.PlayStyle;
import src.enums.YukkuriType;
import src.item.Bed;
import src.logic.ToyLogic;
import src.system.BodyLayer;
import src.system.MessagePool;
import src.util.IniFileUtil;

/**
 * ありす
 */
public class Alice extends Body implements java.io.Serializable {
	static final long serialVersionUID = 1L;
	/** ありすのタイプNo：2 */
	public static final int type = 2;
	/** 日本語名 */
	public static final String nameJ = "ありす";
	/** 英名 */
	public static final String nameE = "Alice";
	/** ベースファイル名 */
	public static final String baseFileName = "alice";

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

	/**
	 * ありすのみオーバーライド。レイパーかつありすかつ興奮顔かどうかを返却する.
	 * @param f ImageCodeのordinal
	 * @returns れいぱーかつありすかつ興奮顔かどうか
	 */
	@Override
	protected boolean isRaperExcitingFace(int f) {
		return isRaper()  && f == ImageCode.EXCITING.ordinal();
	};
	
	/**
	 * ありすのみオーバーライド。ありすかつれいぱーかどうかを返却する.
	 * @return ありすかつれいぱーかどうか
	 */
	@Override
	protected boolean isAliceRaper() {
		return isRaper();
	};

	/** 画像ロード */
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
	/** INIファイルロード */
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
		layer.image[index] = imagePack[getBodyRank().imageIndex][type][direction * directionOffset[type][0]][getBodyAgeState().ordinal()];
		layer.dir[index] = direction * directionOffset[type][1];
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
	public int getHybridType(int partnerType) {
		switch (partnerType) {
		default:
			return Alice.type;
		}
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

	//ゆっくりしてる時のアクション
	//個別の動作がある種ははこれをオーバーライドしているので注意
	@Override
	public void killTime(){
		if(getCurrentEvent() != null)return;
		if(getPlaying()!=null)return;
		int p=SimYukkuri.RND.nextInt(50);
		//7/50でキリッ
		if (p<=6) {
			getInVain(true);
		}
		//7/50でのびのび
		else if (p<=14){
			// if yukkuri is not rude, she goes into her shell by discipline.
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Nobinobi), 40);
			setNobinobi(true);
			addStress(-30);
			stay(40);
		}
		//7/50でこーでねーと
		else if(p<=21 && getBodyRank() != BodyRank.KAIYU){
			coordinate();
			addStress(-30);
			stay(40);
		}
		//7/50でふりふり
		else if (p<=28 && willingFurifuri()) {
			//if yukkuri is rude, she will not do furifuri by discipline.
			setMessage(MessagePool.getMessage(this, MessagePool.Action.FuriFuri), 30);
			setFurifuri(true);
			addStress(-50);
			stay(30);
		}
		//7/50でふりふりで腹減った
		else if( (p<=35 && isHungry()) || isSoHungry()) {
			// 空腹時
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Hungry), 30);
			stay(30);
		}
		//4/50でおもちゃで遊ぶ
		else if (p<=39){
			if(ToyLogic.checkToy(this)) {
				setPlaying(PlayStyle.BALL);
				playingLimit = 150 +SimYukkuri.RND.nextInt(100)-49;
				return;
			}
			else killTime();
		}
		//3/50でトランポリンで遊ぶ
		else if (p<=42){
			if(ToyLogic.checkTrampoline(this)){
				setPlaying(PlayStyle.TRAMPOLINE);
				playingLimit = 150 +SimYukkuri.RND.nextInt(100)-49;
				return;
			}
			else killTime();
		}
		//1/50ですいーで遊ぶ
		else if (p<=43){
			if(ToyLogic.checkSui(this)){
				setPlaying(PlayStyle.SUI);
				playingLimit = 150 +SimYukkuri.RND.nextInt(100)-49;
				return;
			}
			else killTime();
		}
		else{
			// おくるみありで汚れていない場合
			if( isHasPants() && !isDirty() && SimYukkuri.RND.nextInt(5) == 0 ){
				setMessage(MessagePool.getMessage(this, MessagePool.Action.RelaxOkurumi));
			}
			else{
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Relax));
			}
			addStress(-50);
			stay(30);
		}
	}
	/**
	 * こーでぃねーとをする.
	 */
	public void coordinate(){
		if(SimYukkuri.world.getCurrentMap().bed.size() == 0){
			int i=0;
			if(getBodyRank() == BodyRank.NORAYU || getBodyRank() == BodyRank.NORAYU_CLEAN || getBodyRank() == BodyRank.SUTEYU){
				i=1;
			}
			getInVain(true);
			Bed bed = (Bed)GadgetAction.putObjEX(Bed.class, getX(), getY(), i);
			SimYukkuri.world.getCurrentMap().bed.put(bed.objId, bed);
			return;
		}
	}

	/**
	 * コンストラクタ
	 */
	public Alice(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.ALICE);
		setShitType(YukkuriType.ALICE);
		setBaseBodyFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}
	public Alice() {
		
	}
	@Override
	public void tuneParameters() {
		/*if (SimYukkuri.RND.nextBoolean()) {
		motherhood = true;
		}*/
		if (SimYukkuri.RND.nextInt(4) == 0) {
			setRapist(true);
		}
		double factor = Math.random()+1;
		HUNGRYLIMITorg[AgeState.ADULT.ordinal()] *= factor;
		HUNGRYLIMITorg[AgeState.CHILD.ordinal()] *= factor;
		HUNGRYLIMITorg[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()+1;
		SHITLIMITorg[AgeState.ADULT.ordinal()] *= factor;
		SHITLIMITorg[AgeState.CHILD.ordinal()] *= factor;
		SHITLIMITorg[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()+1;
		DAMAGELIMITorg[AgeState.ADULT.ordinal()] *= factor;
		DAMAGELIMITorg[AgeState.CHILD.ordinal()] *= factor;
		DAMAGELIMITorg[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random()+0.5;
		BABYLIMITorg *=  factor;
		CHILDLIMITorg *= factor;
		LIFELIMITorg *=  factor;
		factor = Math.random()+1;
		RELAXPERIODorg *= factor;
		EXCITEPERIODorg *= factor;
		PREGPERIODorg *= factor;
		SLEEPPERIODorg *= factor;
		ACTIVEPERIODorg *= factor;
		sameDest = SimYukkuri.RND.nextInt(15)+15;
		DECLINEPERIODorg *= (Math.random()+0.5);
		ROBUSTNESS = SimYukkuri.RND.nextInt(10)+1;
		//EYESIGHT /= 2;
		factor = Math.random()+0.5;
		STRENGTHorg[AgeState.ADULT.ordinal()] *= factor;
		STRENGTHorg[AgeState.CHILD.ordinal()] *= factor;
		STRENGTHorg[AgeState.BABY.ordinal()] *= factor;
		speed = baseSpeed;
	}
}