package org.simyukkuri.entity.core;

import java.beans.Transient;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.Where;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.system.ItemMenu.GetMenuTarget;
import org.simyukkuri.system.ItemMenu.UseMenuTarget;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameWorld;

/*********************************************************
 * すべてのゲーム内オブジェクトの元となるクラス
 */
@SuppressWarnings("rawtypes")
@JsonTypeInfo(use = Id.CLASS)
public class Entity implements java.io.Serializable, Comparable {

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
		} else if (X > Translate.getWorldWidth() && enableWall) {
			x = Translate.getWorldWidth();
		} else {
			x = X;
		}
	}

	/** y座標セッター */
	public void setCalcY(int Y) {
		if (Y < 0 && enableWall) {
			y = 0;
		} else if (Y > Translate.getWorldHeight() && enableWall) {
			y = Translate.getWorldHeight();
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
		if (Z > Translate.getWorldDepth() && enableWall) {
			z = Translate.getWorldDepth();
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
	 * WorldStateからobjIdをもつObjを取得する.
	 * 
	 * @param i objId
	 * @return Entity
	 */
	public Entity takeMappedObj(int i) {
		WorldState m = GameWorld.get().getCurrentWorldState();
		if (m.getAutoFeeders().containsKey(i)) {
			return m.getAutoFeeders().get(i);
		}
		if (m.getBeds().containsKey(i)) {
			return m.getBeds().get(i);
		}
		if (m.getBeltconveyorObjects().containsKey(i)) {
			return m.getBeltconveyorObjects().get(i);
		}
		if (m.getBreedingPools().containsKey(i)) {
			return m.getBreedingPools().get(i);
		}
		if (m.getDiffusers().containsKey(i)) {
			return m.getDiffusers().get(i);
		}
		if (m.getFoods().containsKey(i)) {
			return m.getFoods().get(i);
		}
		if (m.getFoodMakers().containsKey(i)) {
			return m.getFoodMakers().get(i);
		}
		if (m.getFrontEffects().containsKey(i)) {
			return m.getFrontEffects().get(i);
		}
		if (m.getGarbageChutes().containsKey(i)) {
			return m.getGarbageChutes().get(i);
		}
		if (m.getGarbageStations().containsKey(i)) {
			return m.getGarbageStations().get(i);
		}
		if (m.getHotPlates().containsKey(i)) {
			return m.getHotPlates().get(i);
		}
		if (m.getHouses().containsKey(i)) {
			return m.getHouses().get(i);
		}
		if (m.getMachinePresses().containsKey(i)) {
			return m.getMachinePresses().get(i);
		}
		if (m.getMixers().containsKey(i)) {
			return m.getMixers().get(i);
		}
		if (m.getOkazaris().containsKey(i)) {
			return m.getOkazaris().get(i);
		}
		if (m.getOrangePools().containsKey(i)) {
			return m.getOrangePools().get(i);
		}
		if (m.getProcessorPlates().containsKey(i)) {
			return m.getProcessorPlates().get(i);
		}
		if (m.getProductChutes().containsKey(i)) {
			return m.getProductChutes().get(i);
		}
		if (m.getShit().containsKey(i)) {
			return m.getShit().get(i);
		}
		if (m.getSortedEffects().containsKey(i)) {
			return m.getSortedEffects().get(i);
		}
		if (m.getStalks().containsKey(i)) {
			return m.getStalks().get(i);
		}
		if (m.getStickyPlates().containsKey(i)) {
			return m.getStickyPlates().get(i);
		}
		if (m.getStones().containsKey(i)) {
			return m.getStones().get(i);
		}
		if (m.getSuis().containsKey(i)) {
			return m.getSuis().get(i);
		}
		if (m.getToilets().containsKey(i)) {
			return m.getToilets().get(i);
		}
		if (m.getToys().containsKey(i)) {
			return m.getToys().get(i);
		}
		if (m.getTrampolines().containsKey(i)) {
			return m.getTrampolines().get(i);
		}
		if (m.getTrashObjects().containsKey(i)) {
			return m.getTrashObjects().get(i);
		}
		if (m.getVomit().containsKey(i)) {
			return m.getVomit().get(i);
		}
		if (m.getYunbas().containsKey(i)) {
			return m.getYunbas().get(i);
		}
		for (Map.Entry<Integer, Yukkuri> entry : m.getYukkuriRegistry().entrySet()) {
			Yukkuri b = entry.getValue();
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
	public TickResult clockTick() {
		setAge(getAge() + TICK);
		if (isRemoved()) {
			return TickResult.REMOVED;
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
				} else if (x > Translate.getWorldWidth()) {
					x = Translate.getWorldWidth();
					vx *= -1;
				} else if (Barrier.onBarrier(x, y, 16, 16, Barrier.ITEM_BLOCK_FLAG)) {
					x -= vx;
					vx = 0;
				}
			}
			if (my != 0) {
				y += my;
				if (y < 0) {
					y = 0;
					vy *= -1;
				} else if (y > Translate.getWorldHeight()) {
					y = Translate.getWorldHeight();
					vy *= -1;
				} else if (Barrier.onBarrier(x, y, 16, 16, Barrier.ITEM_BLOCK_FLAG)) {
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
		return TickResult.NONE;
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

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	/**
	 * 座標をマップの範囲内に収める
	 */
	public void calcPos() {
		int mapX = Translate.getWorldWidth();
		int mapY = Translate.getWorldHeight();
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

	/**
	 * Entity レイヤーのフィールドを to へコピーする.
	 * サブクラスは super.copyStateTo(to) を呼んだ後、自クラスのフィールドをコピーする.
	 */
	public void copyStateTo(Entity to) {
		to.setObjType(objType);
		to.setAge(getAge());
		to.setX(x);
		to.setY(y);
		to.setZ(z);
		to.setVx(vx);
		to.setVy(vy);
		to.setVz(vz);
		to.setMotionX(getMotionX());
		to.setMotionY(getMotionY());
		to.setMotionZ(motionZ);
		to.setRemoved(isRemoved());
		to.setCanGrab(canGrab);
		to.setGrabbed(grabbed);
		to.setEnableWall(enableWall);
		to.setBindObj(bindObj);
		to.setValue(value);
		to.setWhere(where);
		to.setFallingUnderGround(fallingUnderGround);
		to.setInPool(inPool);
		to.setMostDepth(mostDepth);
		to.setScreenPivot(screenPivot);
		to.setScreenRect(screenRect);
		to.setW(imgW);
		to.setH(imgH);
		to.setPivotX(pivX);
		to.setPivotY(pivY);
		to.setOfsX(ofsX);
		to.setOfsY(ofsY);
		to.setObjId(objId);
	}

}
