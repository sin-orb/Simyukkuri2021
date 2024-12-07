package src.base;

import java.beans.Transient;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import src.SimYukkuri;
import src.draw.Point4y;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.enums.Event;
import src.enums.Type;
import src.enums.Where;
import src.item.Barrier;
import src.system.ItemMenu.GetMenuTarget;
import src.system.ItemMenu.UseMenuTarget;
import src.system.MapPlaceData;

/*********************************************************
 *  すべてのゲーム内オブジェクトの元となるクラス
 *  でいぶ、ドスなどの突然変異でエラーが出る可能性があるので
 *  private変数は使わず代わりにprotectedを使用してください
 */
@SuppressWarnings("rawtypes")
@JsonTypeInfo(use = Id.CLASS)
public class Obj implements java.io.Serializable, Comparable {

	private static final long serialVersionUID = 4119096412988786726L;

		/**時間経過用係数*/
	public static final int TICK = SimYukkuri.TICK;

	// 基本内部パラメータ
	/** 基本内部パラメータ1
	 * オブジェクトの種類(YUKKURI, SHIT, FOOD, TOILET, TOY)*/
	protected Type objType;
	/** 基本内部パラメータ2
	 * オブジェクトの発生からの経過時間*/
	private long age;
	/** 基本内部パラメータ3
	 * マップ座標*/
	protected int x, y, z;
	/** 基本内部パラメータ4
	 *衝撃を伴う、外力による移動量ベクトル*/
	protected int vx, vy, vz;
	/** 基本内部パラメータ5
	 *コンベアなど拘束による移動量ベクトル*/
	private int bx;
	private int by;
	protected int bz;
	/**基本内部パラメータ6
	 * 除去されたか否か*/
	private boolean removed = false;
	/**基本内部パラメータ7
	 * 掴めるか否か*/
	protected boolean canGrab = true;
	/**基本内部パラメータ8
	 * 掴まれているか否か*/
	protected boolean grabbed = false;
	/**基本内部パラメータ9
	 * 壁に影響されるか否か*/
	protected boolean enableWall = true;
	/**基本内部パラメータ10
	 * */
	protected int bindObj = 0;
	/**基本内部パラメータ11
	 * 価値*/
	protected int value = 0;
	/**基本内部パラメータ12
	 * 価格*/
	protected int cost = 0;
	/**基本内部パラメータ13
	 * どこにあるか*/
	protected Where eWhere = Where.ON_FLOOR;
	/**基本内部パラメータ14
	 * 地下に落下中か*/
	protected boolean bFallingUnderGround = false;
	/**基本内部パラメータ15
	 * プールの中にいるか否か*/
	protected boolean bInPool = false;
	/**基本内部パラメータ16
	 * 最大深度*/
	protected int nMostDepth = 0;

	// 画面描画情報
	/**画面描画情報1
	 * 描画原点*/
	protected Point4y screenPivot = new Point4y();
	/**画面描画情報1
	 * 描画XYWH(xy座標+WHで縦横サイズ)*/
	protected Rectangle4y screenRect = new Rectangle4y(); //
	/**画面描画情報2
	 * 画像サイズ*/
	protected int imgW, imgH;
	/**画面描画情報3
	 * 画像原点 基本床敷きは中心、立っているものは足元*/
	protected int pivX,pivY;
	/**画面描画情報4
	 * 描画オフセット 振動など判定を無視した演出用*/
	protected int ofsX,ofsY;
	/**
	 * このオブジェクトのユニークID
	 */
	public int objId = 0;
	
//	@Override
//	public String toString() {
//		return "名前未設定";
//	}

	/**経過時間ゲッター*/
	public long getAge() {
		return age;
	}

	/**経過時間追加*/
	public void addAge(long val) {
		setAge(getAge() + val);
		if(getAge() < 0) setAge(0);
	}

	/**x座標ゲッター*/
	public int getX() {
		return x;
	}

	/**y座標ゲッター*/
	public int getY() {
		return y;
	}

	/**z座標ゲッター*/
	public int getZ() {
		return z;
	}

	/**外力による移動量ゲッター
	 * @return 外力による移動量のx,y,z,成分*/
	@Transient
	public int[] getVxyz(){
		int V[] = {vx,vy,vz};
		return V;
	}

	/**x座標セッター*/
	public void setCalcX (int X){
		if (X < 0 && enableWall ) {
			x = 0;
		}
		else if (X > Translate.mapW && enableWall ) {
			x = Translate.mapW;
		}
		else {
			x = X;
		}
	}

	/**y座標セッター*/
	public void setCalcY (int Y) {
		if (Y < 0 && enableWall ) {
			y = 0;
		}
		else if(Y > Translate.mapH && enableWall ) {
			y = Translate.mapH;
		}
		else {
			y = Y;
		}
	}

	/**z座標セッター*/
	public void setCalcZ(int Z){
		if (z < nMostDepth && enableWall ) {
			if( bFallingUnderGround  ){
				z = Z;
			}
			else{
				z = nMostDepth;
			}
		}
		if (Z > Translate.mapZ && enableWall ) {
			z = Translate.mapZ;
		}
		else {
			z = Z;
		}
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}

	/**
	 * X座標を強制的に設定する.
	 * @param X X座標
	 */
	public void setForceX (int X){
		x = X;
	}
	/**
	 * Y座標を強制的に設定suru.
	 * @param Y Y座標
	 */
	public void setForceY (int Y) {
		y = Y;
	}

	/**移動量追加
	 * @param x　x方向
	 * @param y　y方向
	 * @param z　z方向
	 */
	public void addBxyz(int x,int y,int z){
		setBx(getBx() + x);
		setBy(getBy() + y);
		bz += z;
	}
	/**移動量セット
	 * @param x　x方向
	 * @param y　y方向
	 * @param z　z方向
	 */
	public void setBxyz(int x,int y,int z){
		setBx(x);
		setBy(y);
		bz = z;
	}
	/**移動量リセット*/
	public void resetBPos() {
		setBx(0);
		setBy(0);
		bz = 0;
	}


	/**オフセット量設定
	 * @param X
	 * @param Y
	 */
	public void setOfsXY(int X,int Y){
		ofsX = X;
		ofsY = Y;
	}
	/**描画時の実際のx座標ゲッター*/
	@Transient
	public int getDrawOfsX(){
		return x + ofsX;
	}
	/**描画時の実際のy座標ゲッター*/
	@Transient
	public int getDrawOfsY(){
		return y + ofsY;
	}

	/** オブジェクト画像の原点とサイズをセット
	 * <br>直接座標値を入力するVer*/
	protected void setBoundary(int px, int py, int w, int h) {
		pivX = px;
		pivY = py;
		imgW = w;
		imgH = h;
	}

	/**オブジェクト画像の原点とサイズをセット
	 * <br> Rectagleを利用するVer*/
	protected void setBoundary(Rectangle4y r) {
		pivX = r.x;
		pivY = r.y;
		imgW = r.width;
		imgH = r.height;
	}

	/**画像の幅ゲッター*/
	public int getW() {
		return imgW;
	}
	/**画像の高さゲッター*/
	public int getH() {
		return imgH;
	}
	/**画像原点のx座標ゲッター*/
	public int getPivotX() {
		return pivX;
	}
	/**画像原点のy座標ゲッター*/
	public int getPivotY() {
		return pivY;
	}

	public int getVx() {
		return vx;
	}

	public void setVx(int vx) {
		this.vx = vx;
	}

	public int getVy() {
		return vy;
	}

	public void setVy(int vy) {
		this.vy = vy;
	}

	public int getVz() {
		return vz;
	}

	public void setVz(int vz) {
		this.vz = vz;
	}

	public int getBz() {
		return bz;
	}

	public void setBz(int bz) {
		this.bz = bz;
	}

	public boolean isEnableWall() {
		return enableWall;
	}

	public void setEnableWall(boolean enableWall) {
		this.enableWall = enableWall;
	}

	public Where geteWhere() {
		return eWhere;
	}

	public void seteWhere(Where eWhere) {
		this.eWhere = eWhere;
	}

	public boolean isbFallingUnderGround() {
		return bFallingUnderGround;
	}

	public void setbFallingUnderGround(boolean bFallingUnderGround) {
		this.bFallingUnderGround = bFallingUnderGround;
	}

	public boolean isbInPool() {
		return bInPool;
	}

	public void setbInPool(boolean bInPool) {
		this.bInPool = bInPool;
	}

	public int getnMostDepth() {
		return nMostDepth;
	}

	public void setnMostDepth(int nMostDepth) {
		this.nMostDepth = nMostDepth;
	}

	public void setW(int imgW) {
		this.imgW = imgW;
	}

	public void setH(int imgH) {
		this.imgH = imgH;
	}

	public void setPivotX(int pivX) {
		this.pivX = pivX;
	}
	
	public void setPivotY(int pivY) {
		this.pivY = pivY;
	}

	public int getOfsX() {
		return ofsX;
	}

	public void setOfsX(int ofsX) {
		this.ofsX = ofsX;
	}

	public int getOfsY() {
		return ofsY;
	}

	public void setOfsY(int ofsY) {
		this.ofsY = ofsY;
	}

	public int getObjId() {
		return objId;
	}

	public void setObjId(int objId) {
		this.objId = objId;
	}

	public void setObjType(Type objType) {
		this.objType = objType;
	}

	public void setCanGrab(boolean canGrab) {
		this.canGrab = canGrab;
	}

	public void setGrabbed(boolean grabbed) {
		this.grabbed = grabbed;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	/**画像範囲ゲッター*/
	public void getBoundaryShape(Rectangle4y r) {
		r.x = pivX;
		r.y = pivY;
		r.width = imgW;
		r.height = imgH;
	}

	/** 画面上に描画されているオブジェクトの原点をセット
	 * <br>直接座標を入力するVer*/
	public void setScreenPivot(int x, int y) {
		screenPivot.x = x;
		screenPivot.y = y;
	}

	/** 画面上に描画されているオブジェクトの原点をセット
	 * <br>Pointを利用するVer*/
	public void setScreenPivot(Point4y rect) {
		screenPivot.x = rect.x;
		screenPivot.y = rect.y;
	}
	/**画面上に描画されているオブジェクトの原点をゲット*/
	public Point4y getScreenPivot() {
		return screenPivot;
	}

	/** 画面上に描画されているオブジェクトの左上座標とサイズをセット
	 * <br>直接座標を入力するVer*/
	public void setScreenRect(int x, int y, int w, int h) {
		screenRect.setX(x);
		screenRect.setY(y);
		screenRect.setWidth(w);
		screenRect.setHeight(h);
	}
	/** 画面上に描画されているオブジェクトの左上座標とサイズをセット
	 * <br>Rectangle利用するVer*/
	public void setScreenRect(Rectangle4y rect) {
		screenRect.setX(rect.x);
		screenRect.setY(rect.y);
		screenRect.setWidth(rect.width);
		screenRect.setHeight(rect.height);
	}
	/** 画面上に描画されているオブジェクトの左上座標とサイズをゲット*/
	public Rectangle4y getScreenRect() {
		return screenRect;
	}

	/**掴めるか否か*/
	public boolean isCanGrab() {
		return canGrab;
	}

	/**掴まれる処理*/
	public void grab() {
		grabbed = true;
	}
	/**放される処理*/
	public void release() {
		grabbed = false;
	}

	/**掴まれてるか*/
	public boolean isGrabbed() {
		return grabbed;
	}

	/**オブジェクトタイプゲッター*/
	public Type getObjType() {
		return objType;
	}

	/**購入価格のゲッター*/
	public int getValue() {
		return value;
	}
	/**ランニングコストのゲッター*/
	public int getCost() {
		return cost;
	}

	/**除去*/
	public void remove(){
		bindObj = -1;
		setRemoved(true);
	}
	/**除去されてるか否か*/
	public boolean isRemoved() {
		return removed;
	}

	/**ケリを入れられる*/
	public void kick(int vX, int vY, int vZ) {
		vx = vX;
		vy = vY;
		vz = vZ;
	}
	/**kick(int vX, int vY, int vZ)のショートカット*/
	public void kick() {
	}

	/**移動の親元オブジェクトゲッター*/
	public int getBindObj() {
		return bindObj;
	}
	/**
	 * MapPlaceDataからobjIdをもつObjを取得する.
	 * @param i objId
	 * @return Obj
	 */
	public Obj takeMappedObj(int i) {
		MapPlaceData m = SimYukkuri.world.getCurrentMap();
		if(m.autofeeder.containsKey(i)) {
			return m.autofeeder.get(i);
		}
		if (m.bed.containsKey(i)) {
			return m.bed.get(i);
		}
		if (m.beltconveyorObj.containsKey(i)) {
			return m.beltconveyorObj.get(i);
		}
		if (m.breedingPool.containsKey(i)) {
			return m.breedingPool.get(i);
		}
		if(m.diffuser.containsKey(i)) {
			return m.diffuser.get(i);
		}
		if (m.food.containsKey(i)) {
			return m.food.get(i);
		}
		if (m.foodmaker.containsKey(i)) {
			return m.foodmaker.get(i);
		}
		if (m.frontEffect.containsKey(i)) {
			return m.frontEffect.get(i);
		}
		if(m.garbagechute.containsKey(i)) {
			return m.garbagechute.get(i);
		}
		if(m.garbageStation.containsKey(i)) {
			return m.garbageStation.get(i);
		}
		if(m.hotPlate.containsKey(i)) {
			return m.hotPlate.get(i);
		}
		if (m.house.containsKey(i)) {
			return m.house.get(i);
		}
		if(m.machinePress.containsKey(i)) {
			return m.machinePress.get(i);
		}
		if(m.mixer.containsKey(i)) {
			return m.mixer.get(i);
		}
		if(m.okazari.containsKey(i)) {
			return m.okazari.get(i);
		}
		if(m.orangePool.containsKey(i)) {
			return m.orangePool.get(i);
		}
		if(m.processerPlate.containsKey(i)) {
			return m.processerPlate.get(i);
		}
		if(m.productchute.containsKey(i)) {
			return m.productchute.get(i);
		}
		if (m.shit.containsKey(i)) {
			return m.shit.get(i);
		}
		if(m.sortEffect.containsKey(i)) {
			return m.sortEffect.get(i);
		}
		if (m.stalk.containsKey(i)) {
			return m.stalk.get(i);
		}
		if (m.stickyPlate.containsKey(i)) {
			return m.stickyPlate.get(i);
		}
		if (m.stone.containsKey(i)) {
			return m.stone.get(i);
		}
		if (m.sui.containsKey(i)) {
			return m.sui.get(i);
		}
		if (m.toilet.containsKey(i)) {
			return m.toilet.get(i);
		}
		if (m.toy.containsKey(i)) {
			return m.toy.get(i);
		}
		if (m.trampoline.containsKey(i)) {
			return m.trampoline.get(i);
		}
		if (m.trash.containsKey(i)) {
			return m.trash.get(i);
		}
		if (m.vomit.containsKey(i)) {
			return m.vomit.get(i);
		}
		if (m.yunba.containsKey(i)) {
			return m.yunba.get(i);
		}
		for (Map.Entry<Integer, Body> entry : m.body.entrySet()) {
			Body b = entry.getValue();
			if (b.objId == i) {
				return b;
			}
		}
		return null;
	}

	/**移動の親元オブジェクトセッター*/
	public void setBindObj( int obj ) {
		bindObj = obj;
	}

	@Transient
	public GetMenuTarget hasGetPopup() {
		return GetMenuTarget.NONE;
	}
	@Transient
	public UseMenuTarget hasUsePopup() {
		return UseMenuTarget.NONE;
	}

	/**どこにいるか取得
	 * @return 床の上か、ゆっくりの上or中か
	 */
	@Transient
	public Where getWhere(){
		return eWhere;
	}

	/**どこにいるかセット
	 * @param ieWhere 床の上か、ゆっくりの上or中か
	 */
	public void setWhere(Where ieWhere){
		eWhere = ieWhere;
	}

	/**オブジェクトごとに毎ティックごとに呼び出される処理
	 * <br>オーバーライドしてるものが多い*/
	public Event clockTick(){
		setAge(getAge() + TICK);
		if (isRemoved()) {
			return Event.REMOVED;
		}

		if (!grabbed) {
			int mx = vx + getBx();
			int my = vy + getBy();
			int mz = vz + bz;

			if (mx != 0) {
				x += mx;
				if (x < 0) {
					x = 0;
					vx *= -1;
				}
				else if (x > Translate.mapW) {
					x = Translate.mapW;
					vx *= -1;
				}
				else if (Barrier.onBarrier(x, y, 16, 16, Barrier.MAP_ITEM)) {
					x -= vx;
					vx = 0;
				}
			}
			if (my != 0) {
				y += my;
				if (y < 0) {
					y = 0;
					vy *= -1;
				}
				else if (y > Translate.mapH) {
					y = Translate.mapH;
					vy *= -1;
				}
				else if (Barrier.onBarrier(x, y, 16, 16, Barrier.MAP_ITEM)) {
					y -= vy;
					vy = 0;
				}
			}
			if (z != 0 || mz != 0) {
				mz += 1;
				vz += 1;
				z -= mz;
				if (z <= nMostDepth) {
					if( !bFallingUnderGround ){
						z = nMostDepth;
						vz = 0;
					}
					vx = 0;
					vy = 0;
				}
			}
		}
		setBx(0);
		setBy(0);
		bz = 0;
		if (x < 0) {
			x = 5;
		}
		if (y < 0) {
			y = 5;
		}
		return Event.DONOTHING;
	}

	/**地面に堕ちつつあるか否か*/
	public boolean getFallingUnderGround(){
		return bFallingUnderGround;
	}
	/**地面に落とすセッター*/
	public void setFallingUnderGround( boolean bFlag ){
		bFallingUnderGround = bFlag;
	}

	/**池の中か否か*/
	public boolean getInPool(){
		return bInPool;
	}
	/**池の中にいるかフラグのセッター*/
	public void setInPool( boolean bFlag ){
		bInPool = bFlag;
	}

	/**最大深度ゲッター*/
	public int getMostDepth(){
		return nMostDepth;
	}
	/**最大深度セッター*/
	public void setMostDepth( int nTemp ){
		nMostDepth = nTemp;
	}
	/**
	 * 取り除かれてるかどうかを設定する.
	 * @param removed 取り除かれてるかどうか
	 */
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
	/**
	 * 存続期間を設定する.
	 * @param age 存続期間
	 */
	public void setAge(long age) {
		this.age = age;
	}
	/**
	 * X座標の加速度を取得する.
	 * @return X座標の加速度
	 */
	public int getBx() {
		return bx;
	}
	/**
	 * X座標の加速度を設定する.
	 * @param bx X座標の加速度
	 */
	public void setBx(int bx) {
		this.bx = bx;
	}
	/**
	 * Y座標の加速度を取得する.
	 * @param bx Y座標の加速度
	 */
	public int getBy() {
		return by;
	}
	/**
	 * Y座標の加速度を設定する.
	 * @param by Y座標の加速度
	 */
	public void setBy(int by) {
		this.by = by;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}
	/**
	 * 座標をマップの範囲内に収める
	 */
	public void calcPos() {
		int mapX = Translate.mapW;
		int mapY = Translate.mapH;
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		if (x > mapX) {
			x = mapX;
		}
		if (y > mapY) {
			y = mapY;
		}
	}

	public int getImgW() {
		return imgW;
	}

	public void setImgW(int imgW) {
		this.imgW = imgW;
	}

	public int getImgH() {
		return imgH;
	}

	public void setImgH(int imgH) {
		this.imgH = imgH;
	}

	public int getPivX() {
		return pivX;
	}

	public void setPivX(int pivX) {
		this.pivX = pivX;
	}

	public int getPivY() {
		return pivY;
	}

	public void setPivY(int pivY) {
		this.pivY = pivY;
	}
	
}
