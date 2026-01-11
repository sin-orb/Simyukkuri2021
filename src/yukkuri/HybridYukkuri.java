package src.yukkuri;

import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;

import src.SimYukkuri;
import src.base.Body;
import src.draw.Dimension4y;
import src.draw.Point4y;
import src.enums.AgeState;
import src.enums.BodyRank;
import src.enums.ImageCode;
import src.enums.Parent;
import src.enums.PredatorType;
import src.enums.YukkuriType;
import src.system.BodyLayer;
import src.util.YukkuriUtil;

/**
 * ハイブリッドゆっくり。
 * 2つの異なる種類のゆっくりを親に持ち、両方の親の特徴を受け継ぐハイブリッド。
 * 親ゆっくりインスタンス４つをメンバに持つ。
 */
public class HybridYukkuri extends Body implements java.io.Serializable {
	private static final long serialVersionUID = -4811724924330805888L;
	/** ハイブリッドゆっくりのタイプ */
	public static final int type = 20000;
	/** ハイブリッドゆっくりの和名 */
	private String nameJ;
	/** ハイブリッドゆっくりの英名 */
	private String nameE;
	/** ハイブリッドゆっくりの和名２ */
	private String nameJ2;
	/** ハイブリッドゆっくりの英名２ */
	private String nameE2;
	/** 型となるゆっくり１ */
	protected Body dorei;
	/** 型となるゆっくり２ */
	protected Body dorei2;
	/** 型となるゆっくり３ */
	protected Body dorei3;
	/** 型となるゆっくり４ */
	protected Body dorei4;

	private Body[] images;
	private static Dimension4y[] boundary = new Dimension4y[3];
	private static Dimension4y[] braidBoundary = new Dimension4y[3];
	/** イメージのロード（なにもしない） */
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
	}
	/**
	 * ハイブリッド用のイメージロード
	 * @throws IOException IO例外
	 */
	public void loadImages_Hyblid () throws IOException {
		HybridYukkuri	parentTmp =	null;
		HybridYukkuri	parentTmp2 = null;
		Body			doreiTmp =	null;
		Body			doreiTmp2 =	null;
		nameJ = "ゆっくり";
		nameE = "Yukkuri";
		nameJ2 = "ゆっくり";
		nameE2 = "Yukkuri";
		Body mama = YukkuriUtil.getBodyInstance(getParents()[Parent.MAMA.ordinal()]);
		Body papa = YukkuriUtil.getBodyInstance(getParents()[Parent.PAPA.ordinal()]);

		if(  mama== null && papa == null  ){
			doreiTmp = new Reimu(100, 100, 0, AgeState.BABY, null, null);
			doreiTmp2 = dorei;
		}
		else{
			if ( mama != null ){
				if ( mama.getType() == 20000 ){
					parentTmp = (HybridYukkuri)mama;
					doreiTmp = parentTmp.dorei;
				}
				else{
					doreiTmp = SimYukkuri.mypane.getTerrarium().makeBody(0, 0, 0,
							mama.getType(), null, AgeState.BABY, mama, papa, false);
				}
			}
			else{
				if ( papa.getType() == 20000 ){
					parentTmp = (HybridYukkuri)papa;
					doreiTmp = parentTmp.dorei;
				}
				else{
					doreiTmp = SimYukkuri.mypane.getTerrarium().makeBody(0, 0, 0, papa.getType(), 
							null, AgeState.BABY, mama, papa, false);
				}
			}

			if ( papa != null ){
				if ( papa.getType() == 20000 ){
					parentTmp2 = (HybridYukkuri)papa;
					doreiTmp2 = parentTmp2.dorei;
				}
				else{
					doreiTmp2 = SimYukkuri.mypane.getTerrarium().makeBody(0, 0, 0, papa.getType(), 
							null, AgeState.BABY, mama, papa, false);
				}
			}else{
				doreiTmp2 = doreiTmp;
			}
		}

		if ( SimYukkuri.RND.nextBoolean() ){
			dorei = doreiTmp;
			dorei2 = doreiTmp2;
		}
		else{
			dorei = doreiTmp2;
			dorei2 = doreiTmp;
		}

		if ( SimYukkuri.RND.nextBoolean() ){
			dorei3 = doreiTmp;
		}
		else{
			dorei3 = doreiTmp2;
		}

		if ( SimYukkuri.RND.nextBoolean() ){
			dorei4 = doreiTmp;
		}
		else{
			dorei4 = doreiTmp2;
		}
		nameJ = dorei.getNameJ();
		anMyName = dorei.getAnMyName();
		anMyNameD = dorei.getAnMyNameD();
		nameE = dorei.getNameE();
		nameJ2 = dorei2.getNameJ();
		nameE2 = dorei2.getNameE();
		setcost(dorei.getYcost());
		getSaleValue()[0] = dorei.getSellingPrice(0);
		getSaleValue()[1] = dorei.getSellingPrice(1);

		// 横
		images[ImageCode.BODY.ordinal()] =		dorei;
		images[ImageCode.DEAD_BODY.ordinal()] =		dorei;
		images[ImageCode.ACCESSORY.ordinal()] =	dorei4;
		images[ImageCode.ACCESSORY_BACK.ordinal()] =	dorei4;
		images[ImageCode.PANTS.ordinal()] =		dorei4;
		images[ImageCode.BODY_CUT.ordinal()] =	dorei;
		images[ImageCode.BODY_INJURED.ordinal()] =	dorei;
		images[ImageCode.PEALED.ordinal()] =		dorei;
		images[ImageCode.DAMAGED0.ordinal()] =	dorei;
		images[ImageCode.DAMAGED1.ordinal()] =	dorei;
		images[ImageCode.DAMAGED2.ordinal()] =	dorei;
		images[ImageCode.SICK0.ordinal()] =		dorei;
		images[ImageCode.SICK1.ordinal()] =		dorei;
		images[ImageCode.SICK2.ordinal()] =		dorei;
		images[ImageCode.SICK3.ordinal()] =		dorei;
		images[ImageCode.FOOT_BAKE0.ordinal()] = dorei;
		images[ImageCode.FOOT_BAKE1.ordinal()] = dorei;
		images[ImageCode.BODY_BAKE0.ordinal()] = dorei;
		images[ImageCode.BODY_BAKE1.ordinal()] = dorei;
		images[ImageCode.STAIN.ordinal()] =		dorei;
		images[ImageCode.STAIN2.ordinal()] =		dorei;
		images[ImageCode.WET.ordinal()] =		dorei;
		images[ImageCode.MELT.ordinal()] =		dorei;
		images[ImageCode.MELT_PEALED.ordinal()] =		dorei;
		images[ImageCode.LICK.ordinal()] =		dorei2;
		images[ImageCode.NOMNOM.ordinal()] =	dorei2;
		images[ImageCode.BLIND.ordinal()] =		dorei;
		images[ImageCode.SHUTMOUTH.ordinal()] =		dorei;
		images[ImageCode.HUNGRY0.ordinal()] =	dorei;
		images[ImageCode.HUNGRY1.ordinal()] =	dorei;
		images[ImageCode.HUNGRY2.ordinal()] =	dorei;
		images[ImageCode.HAIR0.ordinal()] =		dorei;
		images[ImageCode.HAIR1.ordinal()] =		dorei;
		images[ImageCode.HAIR2.ordinal()] =		dorei;
		images[ImageCode.BRAID.ordinal()] =		dorei3;
		images[ImageCode.BRAID_BACK.ordinal()] =		dorei3;
		images[ImageCode.BRAID_CUT.ordinal()] =	dorei3;
		images[ImageCode.BRAID_MV0.ordinal()] =	dorei3;
		images[ImageCode.BRAID_MV1.ordinal()] =	dorei3;
		images[ImageCode.BRAID_MV2.ordinal()] =	dorei3;
		//images[ImageCode.BRAID_BACK_CUT.ordinal()] =	dorei3;
		images[ImageCode.BRAID_BACK_MV0.ordinal()] =	dorei3;
		images[ImageCode.BRAID_BACK_MV1.ordinal()] =	dorei3;
		images[ImageCode.BRAID_BACK_MV2.ordinal()] =	dorei3;
		//顔
		images[ImageCode.CHEER.ordinal()] =		dorei2;
		images[ImageCode.CRYING.ordinal()] =	dorei2;
		images[ImageCode.DEAD.ordinal()] =		dorei2;
		images[ImageCode.EXCITING.ordinal()] =	dorei2;
		images[ImageCode.EXCITING_raper.ordinal()] =	dorei2;
		images[ImageCode.CUTPENIPENI.ordinal()] =		dorei2;
		images[ImageCode.NORMAL.ordinal()] =	dorei2;
		images[ImageCode.PAIN.ordinal()] =		dorei2;
		images[ImageCode.PUFF.ordinal()] =		dorei2;
		images[ImageCode.REFRESHED.ordinal()] =	dorei2;
		images[ImageCode.EMBARRASSED.ordinal()] =	dorei2;
		images[ImageCode.RUDE.ordinal()] =		dorei2;
		images[ImageCode.SLEEPING.ordinal()] =	dorei2;
		images[ImageCode.NIGHTMARE.ordinal()] =	dorei2;
		images[ImageCode.SMILE.ordinal()] =		dorei2;
		images[ImageCode.VAIN.ordinal()] =		dorei2;
		images[ImageCode.SURPRISE.ordinal()] = 	dorei2;
		images[ImageCode.TIRED.ordinal()] =		dorei2;
		images[ImageCode.EYE2.ordinal()] =		dorei2;
		images[ImageCode.EYE3.ordinal()] =		dorei2;
		images[ImageCode.NORMAL0.ordinal()] =	dorei2;
		images[ImageCode.CHEER0.ordinal()] =	dorei2;
		images[ImageCode.PUFF0.ordinal()] =		dorei2;
		images[ImageCode.RUDE0.ordinal()] =		dorei2;
		images[ImageCode.TIRED0.ordinal()] =		dorei2;
		images[ImageCode.PEALEDFACE.ordinal()] =		dorei2;
		images[ImageCode.PEALEDDEADFACE.ordinal()] =		dorei2;
		//非ゆっくり症
		images[ImageCode.NYD_FRONT.ordinal()] =		dorei;
		images[ImageCode.NYD_FRONT_CRY1.ordinal()] =dorei;
		images[ImageCode.NYD_FRONT_CRY2.ordinal()] =dorei;
		images[ImageCode.NYD_UP.ordinal()] =		dorei2;
		images[ImageCode.NYD_UP_CRY1.ordinal()] =	dorei2;
		images[ImageCode.NYD_UP_CRY2.ordinal()] =	dorei2;
		images[ImageCode.NYD_DOWN.ordinal()] =		dorei3;
		images[ImageCode.NYD_DOWN_CRY1.ordinal()] =	dorei3;
		images[ImageCode.NYD_DOWN_CRY2.ordinal()] =	dorei3;
		images[ImageCode.NYD_FRONT_WIDE.ordinal()] =dorei2;
		images[ImageCode.NYD_FRONT_WIDE_CRY1.ordinal()] =dorei2;
		images[ImageCode.NYD_FRONT_WIDE_CRY2.ordinal()] =dorei2;
		//正面
		images[ImageCode.BURNED.ordinal()] =	dorei;
		images[ImageCode.BURNED2.ordinal()] =	dorei;
		images[ImageCode.CRUSHED.ordinal()] =	dorei;
		images[ImageCode.CRUSHED2.ordinal()] =	dorei;
		images[ImageCode.CRUSHED3.ordinal()] =	dorei;
		images[ImageCode.ROLL_ACCESSORY.ordinal()] =	dorei4;
		images[ImageCode.FRONT_SHIT.ordinal()] =dorei;
		images[ImageCode.FRONT_HAIR.ordinal()] =dorei;
		images[ImageCode.FRONT_BLIND.ordinal()] =dorei;
		images[ImageCode.FRONT_PANTS.ordinal()] =dorei;
		images[ImageCode.FRONT_BRAID.ordinal()] =dorei;
		images[ImageCode.FRONT_SEALED.ordinal()] =dorei;
		images[ImageCode.FRONT_INJURED.ordinal()] =dorei;
		images[ImageCode.FRONT_HAIR2.ordinal()] =dorei;
		images[ImageCode.ROLL_LEFT_SHIT.ordinal()] =	dorei;
		images[ImageCode.ROLL_LEFT_HAIR.ordinal()] =	dorei;
		images[ImageCode.ROLL_LEFT_BLIND.ordinal()] =	dorei;
		images[ImageCode.ROLL_LEFT_PANTS.ordinal()] =	dorei;
		images[ImageCode.ROLL_LEFT_BRAID.ordinal()] =	dorei;
		images[ImageCode.ROLL_LEFT_SEALED.ordinal()] =	dorei;
		images[ImageCode.ROLL_LEFT_INJURED.ordinal()] =	dorei;
		images[ImageCode.ROLL_RIGHT_SHIT.ordinal()] =	dorei;
		images[ImageCode.ROLL_RIGHT_HAIR.ordinal()] =	dorei;
		images[ImageCode.ROLL_RIGHT_BLIND.ordinal()] =	dorei;
		images[ImageCode.ROLL_RIGHT_PANTS.ordinal()] =	dorei;
		images[ImageCode.ROLL_RIGHT_BRAID.ordinal()] =	dorei;
		images[ImageCode.ROLL_RIGHT_SEALED.ordinal()] =	dorei;
		images[ImageCode.ROLL_RIGHT_INJURED.ordinal()] =	dorei;
		images[ImageCode.PACKED_DEAD.ordinal()] =	dorei;
		images[ImageCode.PACKED1.ordinal()] =	dorei;
		images[ImageCode.PACKED2.ordinal()] =	dorei;

		for(int i = 0; i < 3; i++) {
			images[ImageCode.BODY.ordinal()].setAgeState(AgeState.values()[i]);
			boundary[i] = new Dimension4y();
			boundary[i].setWidth(images[ImageCode.BODY.ordinal()].getW());
			boundary[i].setHeight(images[ImageCode.BODY.ordinal()].getH());

			images[ImageCode.BRAID.ordinal()].setAgeState(AgeState.values()[i]);
			braidBoundary[i] = new Dimension4y();
			if(dorei3.getType() == Remirya .type || dorei3.getType() == Fran .type || dorei3.getType() == Chiruno .type){
				braidBoundary[i].setWidth(images[ImageCode.BRAID_BACK.ordinal()].getBraidW());
				braidBoundary[i].setHeight(images[ImageCode.BRAID_BACK.ordinal()].getBraidH());
			}
			else{
				braidBoundary[i].setWidth(images[ImageCode.BRAID.ordinal()].getBraidW());
				braidBoundary[i].setHeight(images[ImageCode.BRAID.ordinal()].getBraidH());
			}
		}

		// お下げ有無と位置
		setBraidType(dorei3.isBraidType());
		//お飾りの位置
		setOkazariPosition(dorei4.getOkazariPosition());

		//水耐性引継ぎ
		if(dorei3.isLikeWater()) {
			setLikeWater(true);
		}
		else {
			setLikeWater(false);
		}

		// 飛行状態引継ぎ
		setFlyingType(dorei3.isFlyingType());
		// 捕食種引継ぎ
		if(dorei.isPredatorType()) {
			if(isFlyingType()) setPredatorType(PredatorType.SUCTION);
			else setPredatorType(PredatorType.BITE);
		}
		else {
			setPredatorType(null);
		}
	}
	@Override
	@Transient
	public boolean isImageLoaded() {
		return true;
	}
	@Override
	public void setBodyRank(BodyRank r) {
		dorei.setBodyRank(r);
		dorei2.setBodyRank(r);
		dorei3.setBodyRank(r);
		dorei4.setBodyRank(r);
		bodyRank = r;
	}
	@Override
	public int getImage(int type, int direction, BodyLayer layer, int index) {
		images[type].setAgeState(getBodyAgeState());
		images[type].getImage(type, direction, layer, index);
		return 1;
	}
	/**
	 * 型となるゆっくりを返却する.
	 * @param idx 型のインデックス
	 * @return 型となるゆっくり
	 */
	public Body getBaseBody(int idx) {
		if(idx == 0) return dorei;
		else if(idx == 1) return dorei2;
		else if(idx == 2) return dorei3;
		return dorei4;
	}
	@Override
	public Point4y[] getMountPoint(String key) {
		return dorei.getMountPoint(key);
	}


	@Override
	@Transient
	public int getType() {
		return type;
	}

	@Override
	@Transient
	public int getHybridType(int partnerType) {
		return HybridYukkuri.type;
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
		return nameJ;
	}
	@Override
	public String getNameE() {
		return nameE;
	}

	@Override
	public String getNameJ2() {
		return nameJ2;
	}

	@Override
	public String getNameE2() {
		return nameE2;
	}

	@Override
	@Transient
	public boolean isHybrid () {
		return true;
	}


	/** コンストラクタ */
	public HybridYukkuri(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.HYBRIDYUKKURI);
		setShitType(p1.getShitType());
	}
	public HybridYukkuri() {
		
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
		images = new Body[ImageCode.values().length];
		try {
			loadImages_Hyblid();
		} catch (IOException e1) {
			System.out.println("File I/O error");
		}
	}
	
	@Override
	public void remove() {
		super.remove();
		dorei.remove();
		dorei2.remove();
		dorei3.remove();
		dorei4.remove();
	}
	public Body getDorei() {
		return dorei;
	}
	public void setDorei(Body dorei) {
		this.dorei = dorei;
	}
	public Body getDorei2() {
		return dorei2;
	}
	public void setDorei2(Body dorei2) {
		this.dorei2 = dorei2;
	}
	public Body getDorei3() {
		return dorei3;
	}
	public void setDorei3(Body dorei3) {
		this.dorei3 = dorei3;
	}
	public Body getDorei4() {
		return dorei4;
	}
	public void setDorei4(Body dorei4) {
		this.dorei4 = dorei4;
	}
	public Body[] getImages() {
		return images;
	}
	public void setImages(Body[] images) {
		this.images = images;
	}
	public void setNameJ(String nameJ) {
		this.nameJ = nameJ;
	}
	public void setNameE(String nameE) {
		this.nameE = nameE;
	}
	public void setNameJ2(String nameJ2) {
		this.nameJ2 = nameJ2;
	}
	public void setNameE2(String nameE2) {
		this.nameE2 = nameE2;
	}
	
}
