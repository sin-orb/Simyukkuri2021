package src.item;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.enums.Event;
import src.enums.FavItemType;
import src.enums.Intelligence;
import src.enums.ObjEXType;
import src.enums.Type;
import src.system.MessagePool;

/***************************************************
 * すぃー
 */
public class Sui extends ObjEX implements java.io.Serializable {

	private static final long serialVersionUID = 1901238489894890272L;

	/**処理対象(ゆっくり)*/
	public static final int hitCheckObjType = ObjEX.YUKKURI;
	
	private static int lurd = 0;
	private static int fb = 1;
	private static int ldru = 2;
	private static int rl = 3;
	private static int lr =4;
	private static int ruld = 5;
	private static int bf = 6;
	private static int rdlu = 7;
	private static int direction_num = 8;

	private static int shadow = 0;
	private static int rest = 1;
	private static int yukkuri = 2;
	private static int out_of_control = 3;
	private static int condition_num = 4;
	private static BufferedImage[][] images = new BufferedImage[direction_num][condition_num];
	
	private static Rectangle4y boundary = new Rectangle4y();

	private static int bindbody_num = 3;
	private int current_bindbody_num = 0;
	private Body[] bindBody = new Body[bindbody_num];
	private Obj bindobj = null;

	private int current_direction = bf;
	private int current_condition = rest;
	
	private final static int[][] direction = {{lurd, fb, ldru},
											  {rl, -1, lr},
											  {ruld, bf, rdlu}};
	private final static int ofs = 15;
	private final static int ofsX = 5;
	private final static int ofsY = 1;
	private final static int[][] OfsX = {{-ofs-ofsX*2	,	0,	ofs+ofsX*2,	-ofs-ofsX*2,	ofs+ofsX*2,	-ofs-ofsX*2,	0,	ofs+ofsX*2},
										 {-ofsX*2	,	0,	ofsX*2,		-ofsX*2,		ofsX*2,		-ofsX*2,		0,	ofsX*2},
										 {0,			0,	0,		ofsX,		-ofsX,		0,		0,	0}};
	private final static int[][] OfsY = {{-ofsY,-ofsY*3,	-ofsY,		0,			0,			ofsY,		ofsY*2,	ofsY},
										 {0,	-ofsY*2,			0,		0,			0,				0,		ofsY,	0},
										 {ofsY,		-ofsY,		ofsY,		0,			0,			-ofsY,	    0,	-ofsY},};
	
	private int destX=-1;
	private int destY=-1;
	private int vecX,vecY;
	private int speed = 400;

	/**画像ロード*/
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		String[] tmp = {"_shadow","1","2","3"};
		for(int i=0;i<condition_num;i++){
			images[bf][i] = ModLoader.loadItemImage(loader, "sui" + File.separator + "sui_gray" + File.separator + "bf" + tmp[i] +".png");
			images[fb][i] = ModLoader.loadItemImage(loader, "sui" + File.separator + "sui_gray" + File.separator + "fb" + tmp[i] +".png");
			images[lr][i] = ModLoader.loadItemImage(loader, "sui" + File.separator + "sui_gray" + File.separator + "lr" + tmp[i] +".png");
			images[rl][i] = ModLoader.flipImage(images[lr][i]);
			images[ldru][i] = ModLoader.loadItemImage(loader, "sui" + File.separator + "sui_gray" + File.separator + "ldru" + tmp[i] +".png");
			images[lurd][i] = ModLoader.flipImage(images[ldru][i]);
			images[ruld][i] = ModLoader.loadItemImage(loader, "sui" + File.separator + "sui_gray" + File.separator + "ruld" + tmp[i] +".png");
			images[rdlu][i] = ModLoader.flipImage(images[ruld][i]);
		}
			boundary.setWidth(images[0][0].getWidth(io));
			boundary.setHeight(images[0][0].getHeight(io));
			boundary.setX(boundary.getWidth() >> 1);
			boundary.setY(boundary.getHeight() >> 1);
	}


	@Override
	public int getImageLayer(BufferedImage[] layer) {
		layer[0] = images[current_direction][current_condition];
		return 1;
	}

	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return images[current_direction][shadow];
	}
	/**
	 * すぃーに乗る
	 * @param b 乗るゆっくり
	 * @return 乗れたかどうか
	 */
	public boolean rideOn(Body b){
		if( b == null )
		{
			return false;
		}
		if( b.isNYD() )
		{
			return false;
		}

		// すでに乗っているなら終了
		for(int i=0;i<bindbody_num;i++){
			if(bindBody[i]==b){
				return false;
			}
		}

		// すぃーの所有者が乗っていない場合、1枠あけておく
		if( (bindobj != null) && (bindobj != b)){
			int nCount = 0;
			for(int i=0;i<bindbody_num;i++){
				if(bindBody[i] == null){
					nCount++;
				}
			}

			// 枠が空いていない
			if( nCount < 2 )
			{
				return false;
			}
		}

		for(int i=0;i<bindbody_num;i++){
			if(bindBody[i]==null){
				bindBody[i]=b;
				b.setLinkParent(this.objId);
				bindBody[i].setCalcX(x+OfsX[i][current_direction]);
				bindBody[i].setCalcY(y+OfsY[i][current_direction]+10);
				bindBody[i].setCalcZ(1);
				// すいーの所有者がいないなら所有者になる
				if(bindobj==null){
					bindobj=b;
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GetSui), true);
					b.setFavItem(FavItemType.SUI, this);
				}
				else{
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.RideSui), true);
				}
				break;
			}
		}
		// 乗客数カウントアップ
		current_bindbody_num++;
		b.setDropShadow(false);

		b.addMemories(5);
		
		// すぃーに乗ると目が覚める
		b.wakeup();
		return true;
	}
	/**
	 * すぃーに乗れるかどうか
	 * @return すぃ～に乗れるかどうか
	 */
	@Transient
	public boolean iscanriding(){
		if(bindobj == null) return false;
		for(int i=0;i<bindbody_num;i++){
			if(bindBody[i]==bindobj){
				return true;
			}
		}
		return false;
	}
	/**
	 * すぃ～に乗ってるかどうか
	 * @param b 判定するゆっくり
	 * @return すぃ～に乗ってるかどうか
	 */
	public boolean isriding(Body b){
		if(b == null) return false;
		for(int i=0;i<bindbody_num;i++){
			if(bindBody[i]==b){
				return true;
			}
		}
		return false;
	}
	/**
	 * すぃ～から降りる
	 * @param b 降りるゆっくり
	 */
	public void rideOff(Body b){
		if( b==null)
		{
			return;
		}

		boolean bFlagOwner = false;
		for(int i=0;i<bindbody_num;i++){
			if(bindBody[i]==b){
				if(bindobj==b){
					bFlagOwner = true;
				}
			}
		}
		// 所有者が降りる場合
		if( bFlagOwner)
		{
			//　全員降ろす
			for(int i=0;i<bindbody_num;i++){
				if( bindBody[i] != null)
				{
					bindBody[i].setLinkParent(-1);
					bindBody[i]=null;
				}
			}
			current_bindbody_num = 0;
		}else{
			// 対象だけ降ろす
			b.setLinkParent(-1);
			for(int i=0;i<bindbody_num;i++){
				if( bindBody[i] == b)
				{
					bindBody[i]=null;
				}
			}
			// 乗客を減らす
			current_bindbody_num--;
		}
	}
	/**
	 * 誰も乗っていないかどうか
	 * @return 誰も乗っていないかどうか
	 */
	public boolean NoCanBind(){
		return (bindobj != null);
	}
	
	/**境界線の取得*/
	public static Rectangle4y getBounding() {
		return boundary;
	}

	@Override
	@Transient
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	@Override	
	public boolean enableHitCheck() {
		return true;
	}

	@Override	
	public int objHitProcess( Obj o ) {
		Body b = (Body)o;
		if(current_condition==out_of_control){
			b.strikeByHammer();
			b.strikeByObject(0, 1000, false, vecX, vecY);
		}
		return 0;
	}

	@Override
	public void upDate() {
		// 所有者がいるかつ消滅しているなら所有者をリセット
		if(bindobj != null && bindobj.isRemoved()){
			bindobj=null;
		}
		
		for(int i=0;i<bindbody_num;i++){
//			System.out.println(i + ":" + current_bindbody_num + ":" + bindBody[i]);
			if(bindBody[i] == null) continue;
			// 乗客がマウスでつかまれている場合は降りる
			if(bindBody[i].isGrabbed() ){
				bindBody[i].clearActions();
				rideOff(bindBody[i]);
				continue;
			}
			bindBody[i].setCalcX(x+OfsX[i][current_direction]);
			bindBody[i].setCalcY(y+OfsY[i][current_direction]+10);
			bindBody[i].lookTo(destX, destY);
		}
	}
	
	@Override
	public void removeListData(){
		SimYukkuri.world.getCurrentMap().getSui().remove(objId);
	}
	
	@Override
	public Event clockTick()
	{
		setAge(getAge() + TICK);
		if (isRemoved()) {
			for(Body r:bindBody){
				if( r != null)
				{
					rideOff(r);
					r.removeFavItem(FavItemType.SUI);
					r.clearActions();
				}
			}
			if( bindobj instanceof Body )
			{
				((Body)bindobj).removeFavItem(FavItemType.SUI);
				bindobj = null;
			}
			removeListData();
			return Event.REMOVED;
		}
		
		upDate();
		
		if (grabbed) {
			return Event.DONOTHING;
		}
		
		if(getZ() > 0) {
			z-=5;
			if(z < 0) z = 0;
			return Event.DONOTHING;
		}
		
		if(getAge()>10){
			setAge(0);
			if(destX == -1 && destY == -1) {
				speed=400;	
				if(iscanriding()){
					
					Body b = (Body)bindobj;
					int bx = b.getDestX();
					int by = b.getDestY();
					if(bx == -1){
						bx=x;
					}
					if(by==-1){
						by=y;
					}
					if(b.isIdiot()){
						bx = SimYukkuri.RND.nextInt(Translate.getMapW());
						by = SimYukkuri.RND.nextInt(Translate.getMapH() - boundary.getHeight() / 2);
					}
					if(SimYukkuri.RND.nextInt(100)==0 && b.getIntelligence() == Intelligence.FOOL){
						speed=1000;
					}
					moveTo(bx, by);
				}
							
			}
		}
		else{
			moveBody();
		}
		
		return Event.DONOTHING;
	}
	
	private void moveTo(int toX, int toY)
	{
		destX = Math.max(0, Math.min(toX, Translate.getMapW()));
		destY = Math.max(0, Math.min(toY, (Translate.getMapH() - 1)));
	}

	private int decideDirection(int curPos, int destPos, int range) {
		if (destPos - curPos > range) {
			return 1;
		}
		else if (curPos - destPos > range) {
			return -1;
		}
		return 0;
	}
	
	private void moveBody() {

		int step = 1;
		int dirX = 0;
		int dirY = 0;
		// calculate x direction
		if (destX >= 0) {
			dirX = decideDirection(x, destX, step);
			if (dirX == 0) {
				destX = -1;
			}
		}
		// calculate y direction
		if (destY >= 0) {
			dirY = decideDirection(y, destY, step);
			if (dirY == 0) {
				destY = -1;
			}
		}
		// move to the direction
		vecX = dirX * step * speed / 100;
		vecY = dirY * step * speed / 100;
		x += vecX;
		y += vecY;
		if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.MAP_ITEM)) {
			x -= vecX;
			y -= vecY;
			destX = -1;
			destY = -1;
/*			if(Math.abs(x - destX) > 5 && Math.abs(y - destY) > 5) {
				action = null;
				target = null;
			}*/
			return;
		}
		if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.MAP_NOUNUN)) {
			x -= vecX;
			y -= vecY;
			destX = -1;
			destY = -1;
			return;
		}	
		if(vecX < 0 && x < destX) x = destX;
		if(vecX > 0 && x > destX) x = destX;
		if(vecY < 0 && y < destY) y = destY;
		if(vecY > 0 && y > destY) y = destY;
		
		if (x < 0) {
			x = 0;
			dirX = 1;
		}
		else if (x > Translate.getMapW()) {
			x = Translate.getMapW();
			dirX = -1;
		}
		if (y < 0) {
			y = 0;
			dirY = 1;
		}
		else if (y > Translate.getMapH() - boundary.getHeight() / 2) {
			y = Translate.getMapH() - boundary.getHeight() / 2;
			dirY = -1;
		}
		// update direction of the face
		int directiontmp=direction[dirY+1][dirX+1];
		if(directiontmp !=-1){
			current_direction=directiontmp;
			if(speed>900){
				current_condition=out_of_control;
			}
			else{
				current_condition=yukkuri;
			}
		}
		else{
			current_condition=rest;
			
		}
	}
	/** コンストラクタ */
	public Sui(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().getSui().put(objId, this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.SUI;
		
		moveTo(x, y);
		
		interval = 10;
		value = 20000;
		cost = 0;
	}
	public Sui() {
		
	}
	/**
	 * Y座標を変更する.
	 * @param A 変更するかどうか
	 */
	public void ChangeY(boolean A){
		if(A){
			setForceY(y + boundary.getHeight() / 2);
			for(Body b: bindBody){
				if(b==null) continue;
				b.setForceY(b.getY() + boundary.getHeight());
			}
		}
		else{
			setForceY(y - boundary.getHeight() / 2);
			for(Body b: bindBody){
				if(b==null) continue;
				b.setForceY(b.getY() - boundary.getHeight());
			}
		}
	}


	public int getCurrent_bindbody_num() {
		return current_bindbody_num;
	}


	public void setCurrent_bindbody_num(int current_bindbody_num) {
		this.current_bindbody_num = current_bindbody_num;
	}


	public Body[] getBindBody() {
		return bindBody;
	}


	public void setBindBody(Body[] bindBody) {
		this.bindBody = bindBody;
	}


	public Obj getBindobj() {
		return bindobj;
	}


	public void setBindobj(Obj bindobj) {
		this.bindobj = bindobj;
	}


	public int getCurrent_direction() {
		return current_direction;
	}


	public void setCurrent_direction(int current_direction) {
		this.current_direction = current_direction;
	}


	public int getCurrent_condition() {
		return current_condition;
	}


	public void setCurrent_condition(int current_condition) {
		this.current_condition = current_condition;
	}


	public int getDestX() {
		return destX;
	}


	public void setDestX(int destX) {
		this.destX = destX;
	}


	public int getDestY() {
		return destY;
	}


	public void setDestY(int destY) {
		this.destY = destY;
	}


	public int getVecX() {
		return vecX;
	}


	public void setVecX(int vecX) {
		this.vecX = vecX;
	}


	public int getVecY() {
		return vecY;
	}


	public void setVecY(int vecY) {
		this.vecY = vecY;
	}


	public int getSpeed() {
		return speed;
	}


	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
}




