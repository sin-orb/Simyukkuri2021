package src.base;

import java.beans.Transient;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import src.SimYukkuri;
import src.util.GameWorld;
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
 * すべてのゲーム内オブジェクトの元となるクラス
 * でいぶ、ドスなどの突然変異でエラーが出る可能性があるので
 * private変数は使わず代わりにprotectedを使用してください
 */
@SuppressWarnings("rawtypes")
@JsonTypeInfo(use = Id.CLASS)
public class Obj implements java.io.Serializable, Comparable {

	private static final long serialVersionUID = 4119096412988786726L;

	/** 時間経過用係数 */
	public static final int TICK = SimYukkuri.TICK;

	// 基本内部パラメータ
	/**
	 * 基本内部パラメータ1
	 * オブジェクトの種類(YUKKURI, SHIT, FOOD, TOILET, TOY)
	 */
	protected Type objType;
	/**
	 * 基本内部パラメータ2
	 * オブジェクトの発生からの経過時間
	 */
	private long age;
	/**
	 * 基本内部パラメータ3
	 * マップ座標
	 */
	protected int x, y, z;
	/**
	 * 基本内部パラメータ4
	 * 衝撃を伴う、外力による移動量ベクトル
	 */
	protected int vx, vy, vz;
	/**
	 * 基本内部パラメータ5
	 * コンベアなど拘束による移動量ベクトル
	 */
	private int motionX;
	private int motionY;
	protected int motionZ;
	/**
	 * 基本内部パラメータ6
	 * 除去されたか否か
	 */
	private boolean removed = false;
	/**
	 * 基本内部パラメータ7
	 * 掴めるか否か
	 */
	protected boolean canGrab = true;
	/**
	 * 基本内部パラメータ8
	 * 掴まれているか否か
	 */
	protected boolean grabbed = false;
	/**
	 * 基本内部パラメータ9
	 * 壁に影響されるか否か
	 */
	protected boolean enableWall = true;
	/**
	 * 基本内部パラメータ10
	 */
	protected int bindObj = 0;
	/**
	 * 基本内部パラメータ11
	 * 価値
	 */
	protected int value = 0;
	/**
	 * 基本内部パラメータ12
	 * 価格
	 */
	protected int cost = 0;
	/**
	 * 基本内部パラメータ13
	 * どこにあるか
	 */
	protected Where where = Where.ON_FLOOR;
	/**
	 * 基本内部パラメータ14
	 * 地下に落下中か
	 */
	protected boolean fallingUnderGround = false;
	/**
	 * 基本内部パラメータ15
	 * プールの中にいるか否か
	 */
	protected boolean inPool = false;
	/**
	 * 基本内部パラメータ16
	 * 最大深度
	 */
	protected int mostDepth = 0;

	// 画面描画情報
	/**
	 * 画面描画情報1
	 * 描画原点
	 */
	protected Point4y screenPivot = new Point4y();
	/**
	 * 画面描画情報1
	 * 描画XYWH(xy座標+WHで縦横サイズ)
	 */
	protected Rectangle4y screenRect = new Rectangle4y(); //
	/**
	 * 画面描画情報2
	 * 画像サイズ
	 */
	protected int imgW, imgH;
	/**
	 * 画面描画情報3
	 * 画像原点 基本床敷きは中心、立っているものは足元
	 */
	protected int pivX, pivY;
	/**
	 * 画面描画情報4
	 * 描画オフセット 振動など判定を無視した演出用
	 */
	protected int ofsX, ofsY;
	/**
	 * このオブジェクトのユニークID
	 */
	public int objId = 0;

	// @Override
	// public String toString() {
	// return "名前未設定";
	// }

	/** 経過時間ゲッター */
	public long getAge() {
		return age;
	}

	/** 経過時間追加 */
	public void addAge(long val) {
		setAge(getAge() + val);
		if (getAge() < 0)
			setAge(0);
	}

	/** x座標ゲッター */
	public int getX() {
		return x;
	}

	/** y座標ゲッター */
	public int getY() {
		return y;
	}

	/** z座標ゲッター */
	public int getZ() {
		return z;
	}

	/**
	 * 外力による移動量ゲッター
	 * 
	 * @return 外力による移動量のx,y,z,成分
	 */
	@Transient
	public int[] getVxyz() {
		int V[] = { vx, vy, vz };
		return V;
	}

	/** x座標セッター */
	public void setCalcX(int X) {
		if (X < 0 && enableWall) {
			x = 0;
		} else if (X > Translate.getMapW() && enableWall) {
			x = Translate.getMapW();
		} else {
			x = X;
		}
	}

	/** y座標セッター */
	public void setCalcY(int Y) {
		if (Y < 0 && enableWall) {
			y = 0;
		} else if (Y > Translate.getMapH() && enableWall) {
			y = Translate.getMapH();
		} else {
			y = Y;
		}
	}

	/** z座標セッター */
	public void setCalcZ(int Z) {
		if (z < mostDepth && enableWall) {
			if (fallingUnderGround) {
				z = Z;
			} else {
				z = mostDepth;
			}
		}
		if (Z > Translate.getMapZ() && enableWall) {
			z = Translate.getMapZ();
		} else {
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
	 * 
	 * @param X X座標
	 */
	public void setForceX(int X) {
		x = X;
	}

	/**
	 * Y座標を強制的に設定suru.
	 * 
	 * @param Y Y座標
	 */
	public void setForceY(int Y) {
		y = Y;
	}

	/**
	 * 移動量を加算する.
	 *
	 * @param x x方向
	 * @param y y方向
	 * @param z z方向
	 */
	public void addMotion(int x, int y, int z) {
		setMotionX(getMotionX() + x);
		setMotionY(getMotionY() + y);
		motionZ += z;
	}

	/**
	 * 移動量を設定する.
	 *
	 * @param x x方向
	 * @param y y方向
	 * @param z z方向
	 */
	public void setMotion(int x, int y, int z) {
		setMotionX(x);
		setMotionY(y);
		motionZ = z;
	}

	/** 移動量をゼロに戻す. */
	public void resetMotion() {
		setMotionX(0);
		setMotionY(0);
		motionZ = 0;
	}

	/**
	 * オフセット量設定
	 * 
	 * @param X
	 * @param Y
	 */
	public void setOfsXY(int X, int Y) {
		ofsX = X;
		ofsY = Y;
	}

	/** 描画時の実際のx座標ゲッター */
	@Transient
	public int getDrawOfsX() {
		return x + ofsX;
	}

	/** 描画時の実際のy座標ゲッター */
	@Transient
	public int getDrawOfsY() {
		return y + ofsY;
	}

	/**
	 * オブジェクト画像の原点とサイズをセット
	 * <br>
	 * 直接座標値を入力するVer
	 */
	protected void setBoundary(int px, int py, int w, int h) {
		pivX = px;
		pivY = py;
		imgW = w;
		imgH = h;
	}

	/**
	 * オブジェクト画像の原点とサイズをセット
	 * <br>
	 * Rectagleを利用するVer
	 */
	protected void setBoundary(Rectangle4y r) {
		pivX = r.getX();
		pivY = r.getY();
		imgW = r.getWidth();
		imgH = r.getHeight();
	}

	/** 画像の幅ゲッター */
	public int getW() {
		return imgW;
	}

	/** 画像の高さゲッター */
	public int getH() {
		return imgH;
	}

	/** 画像原点のx座標ゲッター */
	public int getPivotX() {
		return pivX;
	}

	/** 画像原点のy座標ゲッター */
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

	public int getMotionZ() {
		return motionZ;
	}

	public void setMotionZ(int motionZ) {
		this.motionZ = motionZ;
	}

	public boolean isEnableWall() {
		return enableWall;
	}

	public void setEnableWall(boolean enableWall) {
		this.enableWall = enableWall;
	}

	public Where getWhere() {
		return where;
	}

	public void setWhere(Where where) {
		this.where = where;
	}

	public boolean isFallingUnderGround() {
		return fallingUnderGround;
	}

	public void setFallingUnderGround(boolean fallingUnderGround) {
		this.fallingUnderGround = fallingUnderGround;
	}

	public boolean isInPool() {
		return inPool;
	}

	public void setInPool(boolean inPool) {
		this.inPool = inPool;
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

	/** 画像範囲ゲッター */
	public void getBoundaryShape(Rectangle4y r) {
		r.setX(pivX);
		r.setY(pivY);
		r.setWidth(imgW);
		r.setHeight(imgH);
	}

	/**
	 * 画面上に描画されているオブジェクトの原点をセット
	 * <br>
	 * 直接座標を入力するVer
	 */
	public void setScreenPivot(int x, int y) {
		screenPivot.setX(x);
		screenPivot.setY(y);
	}

	/**
	 * 画面上に描画されているオブジェクトの原点をセット
	 * <br>
	 * Pointを利用するVer
	 */
	public void setScreenPivot(Point4y rect) {
		screenPivot.setX(rect.getX());
		screenPivot.setY(rect.getY());
	}

	/** 画面上に描画されているオブジェクトの原点をゲット */
	public Point4y getScreenPivot() {
		return screenPivot;
	}

	/**
	 * 画面上に描画されているオブジェクトの左上座標とサイズをセット
	 * <br>
	 * 直接座標を入力するVer
	 */
	public void setScreenRect(int x, int y, int w, int h) {
		screenRect.setX(x);
		screenRect.setY(y);
		screenRect.setWidth(w);
		screenRect.setHeight(h);
	}

	/**
	 * 画面上に描画されているオブジェクトの左上座標とサイズをセット
	 * <br>
	 * Rectangle利用するVer
	 */
	public void setScreenRect(Rectangle4y rect) {
		screenRect.setX(rect.getX());
		screenRect.setY(rect.getY());
		screenRect.setWidth(rect.getWidth());
		screenRect.setHeight(rect.getHeight());
	}

	/** 画面上に描画されているオブジェクトの左上座標とサイズをゲット */
	public Rectangle4y getScreenRect() {
		return screenRect;
	}

	/** 掴めるか否か */
	public boolean isCanGrab() {
		return canGrab;
	}

	/** 掴まれる処理 */
	public void grab() {
		grabbed = true;
	}

	/** 放される処理 */
	public void release() {
		grabbed = false;
	}

	/** 掴まれてるか */
	public boolean isGrabbed() {
		return grabbed;
	}

	/** オブジェクトタイプゲッター */
	public Type getObjType() {
		return objType;
	}

	/** 購入価格のゲッター */
	public int getValue() {
		return value;
	}

	/** ランニングコストのゲッター */
	public int getCost() {
		return cost;
	}

	/** 除去 */
	public void remove() {
		bindObj = -1;
		setRemoved(true);
	}

	/** 除去されてるか否か */
	public boolean isRemoved() {
		return removed;
	}

	/** ケリを入れられる */
	public void kick(int vX, int vY, int vZ) {
		vx = vX;
		vy = vY;
		vz = vZ;
	}

	/** kick(int vX, int vY, int vZ)のショートカット */
	public void kick() {
	}

	/** 移動の親元オブジェクトゲッター */
	public int getBindObj() {
		return bindObj;
	}

	/**
	 * MapPlaceDataからobjIdをもつObjを取得する.
	 * 
	 * @param i objId
	 * @return Obj
	 */
	public Obj takeMappedObj(int i) {
		MapPlaceData m = GameWorld.get().getCurrentMap();
		if (m.getAutofeeder().containsKey(i)) {
			return m.getAutofeeder().get(i);
		}
		if (m.getBed().containsKey(i)) {
			return m.getBed().get(i);
		}
		if (m.getBeltconveyorObj().containsKey(i)) {
			return m.getBeltconveyorObj().get(i);
		}
		if (m.getBreedingPool().containsKey(i)) {
			return m.getBreedingPool().get(i);
		}
		if (m.getDiffuser().containsKey(i)) {
			return m.getDiffuser().get(i);
		}
		if (m.getFood().containsKey(i)) {
			return m.getFood().get(i);
		}
		if (m.getFoodmaker().containsKey(i)) {
			return m.getFoodmaker().get(i);
		}
		if (m.getFrontEffect().containsKey(i)) {
			return m.getFrontEffect().get(i);
		}
		if (m.getGarbagechute().containsKey(i)) {
			return m.getGarbagechute().get(i);
		}
		if (m.getGarbageStation().containsKey(i)) {
			return m.getGarbageStation().get(i);
		}
		if (m.getHotPlate().containsKey(i)) {
			return m.getHotPlate().get(i);
		}
		if (m.getHouse().containsKey(i)) {
			return m.getHouse().get(i);
		}
		if (m.getMachinePress().containsKey(i)) {
			return m.getMachinePress().get(i);
		}
		if (m.getMixer().containsKey(i)) {
			return m.getMixer().get(i);
		}
		if (m.getOkazari().containsKey(i)) {
			return m.getOkazari().get(i);
		}
		if (m.getOrangePool().containsKey(i)) {
			return m.getOrangePool().get(i);
		}
		if (m.getProcesserPlate().containsKey(i)) {
			return m.getProcesserPlate().get(i);
		}
		if (m.getProductchute().containsKey(i)) {
			return m.getProductchute().get(i);
		}
		if (m.getShit().containsKey(i)) {
			return m.getShit().get(i);
		}
		if (m.getSortEffect().containsKey(i)) {
			return m.getSortEffect().get(i);
		}
		if (m.getStalk().containsKey(i)) {
			return m.getStalk().get(i);
		}
		if (m.getStickyPlate().containsKey(i)) {
			return m.getStickyPlate().get(i);
		}
		if (m.getStone().containsKey(i)) {
			return m.getStone().get(i);
		}
		if (m.getSui().containsKey(i)) {
			return m.getSui().get(i);
		}
		if (m.getToilet().containsKey(i)) {
			return m.getToilet().get(i);
		}
		if (m.getToy().containsKey(i)) {
			return m.getToy().get(i);
		}
		if (m.getTrampoline().containsKey(i)) {
			return m.getTrampoline().get(i);
		}
		if (m.getTrash().containsKey(i)) {
			return m.getTrash().get(i);
		}
		if (m.getVomit().containsKey(i)) {
			return m.getVomit().get(i);
		}
		if (m.getYunba().containsKey(i)) {
			return m.getYunba().get(i);
		}
		for (Map.Entry<Integer, Body> entry : m.getBody().entrySet()) {
			Body b = entry.getValue();
			if (b.objId == i) {
				return b;
			}
		}
		return null;
	}

	/** 移動の親元オブジェクトセッター */
	public void setBindObj(int obj) {
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

	/**
	 * オブジェクトごとに毎ティックごとに呼び出される処理
	 * <br>
	 * オーバーライドしてるものが多い
	 */
	public Event clockTick() {
		setAge(getAge() + TICK);
		if (isRemoved()) {
			return Event.REMOVED;
		}

		if (!grabbed) {
			int mx = vx + getMotionX();
			int my = vy + getMotionY();
			int mz = vz + motionZ;

			if (mx != 0) {
				x += mx;
				if (x < 0) {
					x = 0;
					vx *= -1;
				} else if (x > Translate.getMapW()) {
					x = Translate.getMapW();
					vx *= -1;
				} else if (Barrier.onBarrier(x, y, 16, 16, Barrier.MAP_ITEM)) {
					x -= vx;
					vx = 0;
				}
			}
			if (my != 0) {
				y += my;
				if (y < 0) {
					y = 0;
					vy *= -1;
				} else if (y > Translate.getMapH()) {
					y = Translate.getMapH();
					vy *= -1;
				} else if (Barrier.onBarrier(x, y, 16, 16, Barrier.MAP_ITEM)) {
					y -= vy;
					vy = 0;
				}
			}
			if (z != 0 || mz != 0) {
				mz += 1;
				vz += 1;
				z -= mz;
				if (z <= mostDepth) {
					if (!fallingUnderGround) {
						z = mostDepth;
						vz = 0;
					}
					vx = 0;
					vy = 0;
				}
			}
		}
		setMotionX(0);
		setMotionY(0);
		motionZ = 0;
		if (x < 0) {
			x = 5;
		}
		if (y < 0) {
			y = 5;
		}
		return Event.DONOTHING;
	}

	/** 最大深度ゲッター */
	public int getMostDepth() {
		return mostDepth;
	}

	/** 最大深度セッター */
	public void setMostDepth(int mostDepth) {
		this.mostDepth = mostDepth;
	}

	/**
	 * 取り除かれてるかどうかを設定する.
	 * 
	 * @param removed 取り除かれてるかどうか
	 */
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	/**
	 * 存続期間を設定する.
	 * 
	 * @param age 存続期間
	 */
	public void setAge(long age) {
		this.age = age;
	}

	/**
	 * X座標の加速度を取得する.
	 * 
	 * @return X座標の加速度
	 */
	public int getMotionX() {
		return motionX;
	}

	/**
	 * X座標の加速度を設定する.
	 * 
	 * @param bx X座標の加速度
	 */
	public void setMotionX(int motionX) {
		this.motionX = motionX;
	}

	/**
	 * Y座標の加速度を取得する.
	 * 
	 * @param bx Y座標の加速度
	 */
	public int getMotionY() {
		return motionY;
	}

	/**
	 * Y座標の加速度を設定する.
	 * 
	 * @param by Y座標の加速度
	 */
	public void setMotionY(int motionY) {
		this.motionY = motionY;
	}

	/**
	 * 後方互換のための旧API.
	 *
	 * @deprecated use {@link #getMotionX()}
	 */
	@Deprecated
	public int getBx() {
		return getMotionX();
	}

	/**
	 * 後方互換のための旧API.
	 *
	 * @deprecated use {@link #setMotionX(int)}
	 */
	@Deprecated
	public void setBx(int bx) {
		setMotionX(bx);
	}

	/**
	 * 後方互換のための旧API.
	 *
	 * @deprecated use {@link #getMotionY()}
	 */
	@Deprecated
	public int getBy() {
		return getMotionY();
	}

	/**
	 * 後方互換のための旧API.
	 *
	 * @deprecated use {@link #setMotionY(int)}
	 */
	@Deprecated
	public void setBy(int by) {
		setMotionY(by);
	}

	/**
	 * 後方互換のための旧API.
	 *
	 * @deprecated use {@link #getMotionZ()}
	 */
	@Deprecated
	public int getBz() {
		return getMotionZ();
	}

	/**
	 * 後方互換のための旧API.
	 *
	 * @deprecated use {@link #setMotionZ(int)}
	 */
	@Deprecated
	public void setBz(int bz) {
		setMotionZ(bz);
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	/**
	 * 座標をマップの範囲内に収める
	 */
	public void calcPos() {
		int mapX = Translate.getMapW();
		int mapY = Translate.getMapH();
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
