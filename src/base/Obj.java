package src.base;

import java.awt.Point;
import java.awt.Rectangle;

import src.SimYukkuri;
import src.draw.Translate;
import src.enums.Event;
import src.enums.Type;
import src.enums.Where;
import src.item.Barrier;
import src.system.ItemMenu.GetMenuTarget;
import src.system.ItemMenu.UseMenuTarget;

/*********************************************************
 *  すべてのゲーム内オブジェクトの元となるクラス
 *  でいぶ、ドスなどの突然変異でエラーが出る可能性があるので
 *  private変数は使わず代わりにprotectedを使用してください
 */
public class Obj implements java.io.Serializable, Comparable {
	static final long serialVersionUID = 1L;
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
	protected Obj bindObj = null;
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
	protected Point screenPivot = new Point();
	/**画面描画情報1
	 * 描画XYWH(xy座標+WHで縦横サイズ)*/
	protected Rectangle screenRect = new Rectangle(); //
	/**画面描画情報2
	 * 画像サイズ*/
	protected int imgW, imgH;
	/**画面描画情報3
	 * 画像原点 基本床敷きは中心、立っているものは足元*/
	protected int pivX,pivY;
	/**画面描画情報4
	 * 描画オフセット 振動など判定を無視した演出用*/
	protected int ofsX,ofsY;

	@Override
	public String toString() {
		return "名前未設定";
	}

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
		if( x < 0 || Translate.mapW < x){
//			System.out.println("Error x :" + x);
		}
		return x;
	}

	/**y座標ゲッター*/
	public int getY() {
		if( y < 0 || Translate.mapH < y){
//			System.out.println("Error y :" + y);
		}
		return y;
	}

	/**z座標ゲッター*/
	public int getZ() {
		return z;
	}

	/**外力による移動量ゲッター
	 * @return 外力による移動量のx,y,z,成分*/
	public int[] getVxyz(){
		int V[] = {vx,vy,vz};
		return V;
	}

	/**x座標セッター*/
	public void setX (int X){
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
	public void setY (int Y) {
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
	public void setZ(int Z){
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

	public void setForceX (int X){
		x = X;
	}
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
	public int getDrawOfsX(){
		return x + ofsX;
	}
	/**描画時の実際のy座標ゲッター*/
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
	protected void setBoundary(Rectangle r) {
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

	/**画像範囲ゲッター*/
	public void getBoundaryShape(Rectangle r) {
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
	public void setScreenPivot(Point rect) {
		screenPivot.x = rect.x;
		screenPivot.y = rect.y;
	}
	/**画面上に描画されているオブジェクトの原点をゲット*/
	public Point getScreenPivot() {
		return screenPivot;
	}

	/** 画面上に描画されているオブジェクトの左上座標とサイズをセット
	 * <br>直接座標を入力するVer*/
	public void setScreenRect(int x, int y, int w, int h) {
		screenRect.x = x;
		screenRect.y = y;
		screenRect.width = w;
		screenRect.height = h;
	}
	/** 画面上に描画されているオブジェクトの左上座標とサイズをセット
	 * <br>Rectangle利用するVer*/
	public void setScreenRect(Rectangle rect) {
		screenRect.x = rect.x;
		screenRect.y = rect.y;
		screenRect.width = rect.width;
		screenRect.height = rect.height;
	}
	/** 画面上に描画されているオブジェクトの左上座標とサイズをゲット*/
	public Rectangle getScreenRect() {
		return screenRect;
	}

	/**掴めるか否か*/
	public boolean isCanGrab() {
		return canGrab;
	}
	/*public void setCanGrab(boolean b) {
		canGrab = b;
	}*/

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

	/*public void setEnableWall( boolean flag ) {
		enableWall = flag;
	}*/

	/**除去*/
	public void remove(){
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
	public Obj getBindObj() {
		return bindObj;
	}
	/**移動の親元オブジェクトセッター*/
	public void setBindObj( Obj obj ) {
		bindObj = obj;
	}


	public GetMenuTarget hasGetPopup() {
		return GetMenuTarget.NONE;
	}
	public UseMenuTarget hasUsePopup() {
		return UseMenuTarget.NONE;
	}

	/**どこにいるか取得
	 * @return 床の上か、ゆっくりの上or中か
	 */
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

	public int getBx() {
		return bx;
	}

	public void setBx(int bx) {
		this.bx = bx;
	}

	public int getBy() {
		return by;
	}

	public void setBy(int by) {
		this.by = by;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}
}
