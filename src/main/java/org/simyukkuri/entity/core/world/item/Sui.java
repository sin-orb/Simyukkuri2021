package org.simyukkuri.entity.core.world.item;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.enums.FavItemType;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;

/**
 * すぃー.
 */
public class Sui extends WorldEntity {

	private static final long serialVersionUID = 1901238489894890272L;

	/** 処理対象(ゆっくり) */
	public static final int hitCheckObjType = WorldEntity.YUKKURI;

	private static int lurd = 0;
	private static int fb = 1;
	private static int ldru = 2;
	private static int rl = 3;
	private static int lr = 4;
	private static int ruld = 5;
	private static int bf = 6;
	private static int rdlu = 7;
	private static final int DIRECTION_NUM = 8;

	private static int shadow = 0;
	private static int rest = 1;
	private static int yukkuri = 2;
	private static int out_of_control = 3;
	private static final int CONDITION_NUM = 4;
	private static BufferedImage[][] images = new BufferedImage[DIRECTION_NUM][CONDITION_NUM];

	private static Rectangle4y boundary = new Rectangle4y();

	private static final int BIND_BODY_NUM = 3;
	private int boundBodyCount = 0;
	private Yukkuri[] ridingBodies = new Yukkuri[BIND_BODY_NUM];
	private Entity ownerBody = null;

	private int directionIndex = bf;
	private int conditionIndex = rest;

	private static final int[][] direction = { { lurd, fb, ldru },
			{ rl, -1, lr },
			{ ruld, bf, rdlu } };
	private static final int ofs = 15;
	private static final int ofsX = 5;
	private static final int ofsY = 1;
	private static final int[][] OfsX = {
			{ -ofs - ofsX * 2, 0, ofs + ofsX * 2, -ofs - ofsX * 2, ofs + ofsX * 2, -ofs - ofsX * 2, 0, ofs + ofsX * 2 },
			{ -ofsX * 2, 0, ofsX * 2, -ofsX * 2, ofsX * 2, -ofsX * 2, 0, ofsX * 2 },
			{ 0, 0, 0, ofsX, -ofsX, 0, 0, 0 } };
	private static final int[][] OfsY = { { -ofsY, -ofsY * 3, -ofsY, 0, 0, ofsY, ofsY * 2, ofsY },
			{ 0, -ofsY * 2, 0, 0, 0, 0, ofsY, 0 },
			{ ofsY, -ofsY, ofsY, 0, 0, -ofsY, 0, -ofsY }, };

	private int destX = -1;
	private int destY = -1;
	private int vecX;
	private int vecY;
	private int speed = 400;

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		String[] tmp = { "_shadow", "1", "2", "3" };
		for (int i = 0; i < CONDITION_NUM; i++) {
			images[bf][i] = ModLoader.loadItemImage(loader,
					"sui" + File.separator + "sui_gray" + File.separator + "bf" + tmp[i] + ".png");
			images[fb][i] = ModLoader.loadItemImage(loader,
					"sui" + File.separator + "sui_gray" + File.separator + "fb" + tmp[i] + ".png");
			images[lr][i] = ModLoader.loadItemImage(loader,
					"sui" + File.separator + "sui_gray" + File.separator + "lr" + tmp[i] + ".png");
			images[rl][i] = ModLoader.flipImage(images[lr][i]);
			images[ldru][i] = ModLoader.loadItemImage(loader,
					"sui" + File.separator + "sui_gray" + File.separator + "ldru" + tmp[i] + ".png");
			images[lurd][i] = ModLoader.flipImage(images[ldru][i]);
			images[ruld][i] = ModLoader.loadItemImage(loader,
					"sui" + File.separator + "sui_gray" + File.separator + "ruld" + tmp[i] + ".png");
			images[rdlu][i] = ModLoader.flipImage(images[ruld][i]);
		}
		boundary.setWidth(images[0][0].getWidth(io));
		boundary.setHeight(images[0][0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() >> 1);
	}

	/** アイテム画像をレイヤー配列にセットし、使用レイヤー数を返す。 */
	@Override
	public int getImageLayer(BufferedImage[] layer) {
		layer[0] = images[directionIndex][conditionIndex];
		return 1;
	}

	/** アイテムの影画像を返す。 */
	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return images[directionIndex][shadow];
	}

	/**
	 * すぃーに乗る
	 * 
	 * @param b 乗るゆっくり
	 * @return 乗れたかどうか
	 */
	public boolean rideOn(Yukkuri b) {
		if (b == null) {
			return false;
		}
		if (b.isNyd()) {
			return false;
		}

		// すでに乗っているなら終了
		for (int i = 0; i < BIND_BODY_NUM; i++) {
			if (ridingBodies[i] == b) {
				return false;
			}
		}

		// すぃーの所有者が乗っていない場合、1枠あけておく
		if ((ownerBody != null) && (ownerBody != b)) {
			int passengerCount = 0;
			for (int i = 0; i < BIND_BODY_NUM; i++) {
				if (ridingBodies[i] == null) {
					passengerCount++;
				}
			}

			// 枠が空いていない
			if (passengerCount < 2) {
				return false;
			}
		}

		for (int i = 0; i < BIND_BODY_NUM; i++) {
			if (ridingBodies[i] == null) {
				ridingBodies[i] = b;
				b.setParentLinkId(this.objId);
				ridingBodies[i].setCalcX(x + OfsX[i][directionIndex]);
				ridingBodies[i].setCalcY(y + OfsY[i][directionIndex] + 10);
				ridingBodies[i].setCalcZ(1);
				// すいーの所有者がいないなら所有者になる
				if (ownerBody == null) {
					ownerBody = b;
					b.setMessage(GameMessages.getMessage(b, MessagePool.Action.GetSui), true);
					b.setFavoriteItem(FavItemType.SUI, this);
				} else {
					b.setMessage(GameMessages.getMessage(b, MessagePool.Action.RideSui), true);
				}
				break;
			}
		}
		// 乗客数カウントアップ
		boundBodyCount++;
		b.setShadowVisible(false);

		b.addMemories(5);

		// すぃーに乗ると目が覚める
		b.wakeup();
		return true;
	}

	/**
	 * すぃーの所有者が乗っているかどうか.
	 * 
	 * @return すぃーの所有者が乗っているかどうか
	 */
	@Transient
	public boolean isOwnerRiding() {
		if (ownerBody == null) {
			return false;
		}
		for (int i = 0; i < BIND_BODY_NUM; i++) {
			if (ridingBodies[i] == ownerBody) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定したゆっくりが乗っているかどうか.
	 * 
	 * @param rider 判定するゆっくり
	 * @return 指定したゆっくりが乗っているかどうか
	 */
	public boolean isRiddenBy(Yukkuri rider) {
		if (rider == null) {
			return false;
		}
		for (int i = 0; i < BIND_BODY_NUM; i++) {
			if (ridingBodies[i] == rider) {
				return true;
			}
		}
		return false;
	}

	/**
	 * すぃ～から降りる
	 * 
	 * @param rider 降りるゆっくり
	 */
	public void rideOff(Yukkuri rider) {
		if (rider == null) {
			return;
		}

		boolean isOwnerLeaving = false;
		for (int i = 0; i < BIND_BODY_NUM; i++) {
			if (ridingBodies[i] == rider) {
				if (ownerBody == rider) {
					isOwnerLeaving = true;
				}
			}
		}
		// 所有者が降りる場合
		if (isOwnerLeaving) {
			// 全員降ろす
			for (int i = 0; i < BIND_BODY_NUM; i++) {
				if (ridingBodies[i] != null) {
					ridingBodies[i].setParentLinkId(-1);
					ridingBodies[i] = null;
				}
			}
			boundBodyCount = 0;
		} else {
			// 対象だけ降ろす
			rider.setParentLinkId(-1);
			for (int i = 0; i < BIND_BODY_NUM; i++) {
				if (ridingBodies[i] == rider) {
					ridingBodies[i] = null;
				}
			}
			// 乗客を減らす
			boundBodyCount--;
		}
	}

	/**
	 * 所有者がいるかどうか.
	 * 
	 * @return 所有者がいるかどうか
	 */
	public boolean hasOwner() {
		return (ownerBody != null);
	}

	/** 境界線の取得 */
	public static Rectangle4y getBounding() {
		return boundary;
	}

	/** 衝突判定対象タイプを返す。 */
	@Override
	@Transient
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	/**
	 * Enable hit check.
	 *
	 * @return Enable hit check
	 */
	@Override
	public boolean enableHitCheck() {
		return true;
	}

	/** 衝突処理を行い、結果コードを返す。 */
	@Override
	public int objHitProcess(Entity o) {
		Yukkuri b = (Yukkuri) o;
		if (conditionIndex == out_of_control) {
			b.strikeByHammer();
			b.strikeByObject(0, 1000, false, vecX, vecY);
		}
		return 0;
	}

	/** 毎ティックの状態更新を行う。 */
	@Override
	public void upDate() {
		// 所有者がいるかつ消滅しているなら所有者をリセット
		if (ownerBody != null && ownerBody.isRemoved()) {
			ownerBody = null;
		}

		for (int i = 0; i < BIND_BODY_NUM; i++) {
			// System.out.println(i + ":" + boundBodyCount + ":" + ridingBodies[i]);
			if (ridingBodies[i] == null) {
				continue;
			}
			// 乗客がマウスでつかまれている場合は降りる
			if (ridingBodies[i].isGrabbed()) {
				ridingBodies[i].clearActions();
				rideOff(ridingBodies[i]);
				continue;
			}
			ridingBodies[i].setCalcX(x + OfsX[i][directionIndex]);
			ridingBodies[i].setCalcY(y + OfsY[i][directionIndex] + 10);
			ridingBodies[i].lookTo(destX, destY);
		}
	}

	/** ワールドからこのアイテムを除去する。 */
	@Override
	public void removeFromWorld() {
		GameWorld.get().getCurrentWorldState().getSuis().remove(objId);
	}

	/** 毎ティックの状態更新を行う。 */
	@Override
	public TickResult clockTick() {
		setAge(getAge() + TICK);
		if (isRemoved()) {
			for (Yukkuri r : ridingBodies) {
				if (r != null) {
					rideOff(r);
					r.removeFavoriteItem(FavItemType.SUI);
					r.clearActions();
				}
			}
			if (ownerBody instanceof Yukkuri) {
				((Yukkuri) ownerBody).removeFavoriteItem(FavItemType.SUI);
				ownerBody = null;
			}
			removeFromWorld();
			return TickResult.REMOVED;
		}

		upDate();

		if (grabbed) {
			return TickResult.NONE;
		}

		if (getZ() > 0) {
			z -= 5;
			if (z < 0) {
				z = 0;
			}
			return TickResult.NONE;
		}

		if (getAge() > 10) {
			setAge(0);
			if (destX == -1 && destY == -1) {
				speed = 400;
				if (isOwnerRiding()) {

					Yukkuri b = (Yukkuri) ownerBody;
					int bx = b.getDestX();
					int by = b.getDestY();
					if (bx == -1) {
						bx = x;
					}
					if (by == -1) {
						by = y;
					}
					if (b.isIdiot()) {
						bx = GameRandom.nextInt(Translate.getWorldWidth());
						by = GameRandom.nextInt(Translate.getWorldHeight() - boundary.getHeight() / 2);
					}
					if (GameRandom.nextInt(100) == 0 && b.getIntelligence() == Intelligence.FOOL) {
						speed = 1000;
					}
					moveTo(bx, by);
				}

			}
		} else {
			moveYukkuri();
		}

		return TickResult.NONE;
	}

	private void moveTo(int toX, int toY) {
		destX = Math.max(0, Math.min(toX, Translate.getWorldWidth()));
		destY = Math.max(0, Math.min(toY, (Translate.getWorldHeight() - 1)));
	}

	private int decideDirection(int curPos, int destPos, int range) {
		if (destPos - curPos > range) {
			return 1;
		} else if (curPos - destPos > range) {
			return -1;
		}
		return 0;
	}

	private void moveYukkuri() {

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
		if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.ITEM_BLOCK_FLAG)) {
			x -= vecX;
			y -= vecY;
			destX = -1;
			destY = -1;
			/*
			 * if(Math.abs(x - destX) > 5 && Math.abs(y - destY) > 5) {
			 * action = null;
			 * target = null;
			 * }
			 */
			return;
		}
		if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.NO_UNUN_BLOCK_FLAG)) {
			x -= vecX;
			y -= vecY;
			destX = -1;
			destY = -1;
			return;
		}
		if (vecX < 0 && x < destX) {
			x = destX;
		}
		if (vecX > 0 && x > destX) {
			x = destX;
		}
		if (vecY < 0 && y < destY) {
			y = destY;
		}
		if (vecY > 0 && y > destY) {
			y = destY;
		}

		if (x < 0) {
			x = 0;
			dirX = 1;
		} else if (x > Translate.getWorldWidth()) {
			x = Translate.getWorldWidth();
			dirX = -1;
		}
		if (y < 0) {
			y = 0;
			dirY = 1;
		} else if (y > Translate.getWorldHeight() - boundary.getHeight() / 2) {
			y = Translate.getWorldHeight() - boundary.getHeight() / 2;
			dirY = -1;
		}
		// update direction of the face
		int directiontmp = direction[dirY + 1][dirX + 1];
		if (directiontmp != -1) {
			directionIndex = directiontmp;
			if (speed > 900) {
				conditionIndex = out_of_control;
			} else {
				conditionIndex = yukkuri;
			}
		} else {
			conditionIndex = rest;

		}
	}

	/** コンストラクタ */
	public Sui(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		GameWorld.get().getCurrentWorldState().getSuis().put(objId, this);
		GameWorld.get().getCurrentWorldState().registerEntity(objId, this);
		objType = Type.OBJECT;
		worldEntityType = WorldEntityKind.SUI;

		moveTo(x, y);

		interval = 10;
		value = 20000;
		cost = 0;
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public Sui() {

	}

	/**
	 * Y 座標を上下にずらす.
	 * 
	 * @param moveDown 下方向にずらすかどうか
	 */
	public void shiftVerticalPosition(boolean moveDown) {
		if (moveDown) {
			setForceY(y + boundary.getHeight() / 2);
			for (Yukkuri b : ridingBodies) {
				if (b == null) {
					continue;
				}
				b.setForceY(b.getY() + boundary.getHeight());
			}
		} else {
			setForceY(y - boundary.getHeight() / 2);
			for (Yukkuri b : ridingBodies) {
				if (b == null) {
					continue;
				}
				b.setForceY(b.getY() - boundary.getHeight());
			}
		}
	}

	/** 現在バインドしているゆっくりの数を返す。 */
	public int getBoundBodyCount() {
		return boundBodyCount;
	}

	/** 現在バインドしているゆっくりの数をセットする。 */
	public void setBoundBodyCount(int boundBodyCount) {
		this.boundBodyCount = boundBodyCount;
	}

	/** 関連付けられているゆっくりを返す。 */
	public Yukkuri[] getBoundYukkuri() {
		return ridingBodies;
	}

	/** 関連付けるゆっくりをセットする。 */
	public void setBoundYukkuri(Yukkuri[] ridingBodies) {
		this.ridingBodies = ridingBodies;
	}

	/** 吸引対象のエンティティを返す。 */
	public Entity getOwnerBody() {
		return ownerBody;
	}

	/** 吸引対象のエンティティをセットする。 */
	public void setOwnerBody(Entity ownerBody) {
		this.ownerBody = ownerBody;
	}

	/** 現在の移動方向を返す。 */
	public int getDirectionIndex() {
		return directionIndex;
	}

	/** 現在の移動方向をセットする。 */
	public void setDirectionIndex(int directionIndex) {
		this.directionIndex = directionIndex;
	}

	/** 現在の動作状態を返す。 */
	public int getConditionIndex() {
		return conditionIndex;
	}

	/** 現在の動作状態をセットする。 */
	public void setConditionIndex(int conditionIndex) {
		this.conditionIndex = conditionIndex;
	}

	/** 移動目標の X 座標を返す。 */
	public int getDestX() {
		return destX;
	}

	/** 移動目標の X 座標をセットする。 */
	public void setDestX(int destX) {
		this.destX = destX;
	}

	/** 移動目標の Y 座標を返す。 */
	public int getDestY() {
		return destY;
	}

	/** 移動目標の Y 座標をセットする。 */
	public void setDestY(int destY) {
		this.destY = destY;
	}

	/** X 方向の移動ベクトルを返す。 */
	public int getVecX() {
		return vecX;
	}

	/** X 方向の移動ベクトルをセットする。 */
	public void setVecX(int vecX) {
		this.vecX = vecX;
	}

	/** Y 方向の移動ベクトルを返す。 */
	public int getVecY() {
		return vecY;
	}

	/** Y 方向の移動ベクトルをセットする。 */
	public void setVecY(int vecY) {
		this.vecY = vecY;
	}

	/** 移動速度を返す。 */
	public int getSpeed() {
		return speed;
	}

	/** 移動速度をセットする。 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

}
