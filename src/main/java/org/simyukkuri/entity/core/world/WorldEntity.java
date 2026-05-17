package org.simyukkuri.entity.core.world;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.beans.Transient;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.Numbering;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.util.GameWorld;

/*********************************************************
 * ゆっくり以外のゲーム内オブジェクトの元となるクラス
 */
@JsonTypeInfo(use = Id.CLASS)
public abstract class WorldEntity extends Entity {
	/** オブジェクトタイプ */
	protected WorldEntityKind worldEntityType;
	/** 追加情報用の汎用定数 */
	protected int option;

	/** 排他的論理和でフラグ管理している ex) 0101 ならゆっくりと食物 */
	public static final int YUKKURI = (int) Math.pow(2, 0),
			SHIT = (int) Math.pow(2, 1),
			FOOD = (int) Math.pow(2, 2),
			TOILET = (int) Math.pow(2, 3),
			TOY = (int) Math.pow(2, 4),
			PLATFORM = (int) Math.pow(2, 5),
			FIX_OBJECT = (int) Math.pow(2, 6),
			OBJECT = (int) Math.pow(2, 7),
			VOMIT = (int) Math.pow(2, 8),
			STALK = (int) Math.pow(2, 9);
	/**
	 * 処理対象
	 * <br>
	 * デフォルトは0(処理対象無し)
	 */
	public static final int hitCheckObjType = 0;

	/** アイテムのランク */
	public enum ItemRank {
		HOUSE, NORA, YASEI
	}

	/** 標準用長方形型境界線 */
	protected static Rectangle4y boundary = new Rectangle4y();

	/** 親オブジェクト */
	protected int parentLinkId = -1;
	/** ゆっくりに対しての見た目(FOODでのみ使用) */
	protected int looks = 0;
	/** 負荷分散のためのインターバル値 */
	protected int interval = 1;
	/** 汎用スイッチ */
	protected boolean enabled = true;
	/**
	 * 原点からの当たり判定範囲
	 * <br>
	 * 画像=判定ならpivXYでOK プレス機など設置地面付近なら要調整
	 */
	protected int colW = 0, colH = 0;
	/** 処理対象の位置 */
	protected Point4y tmpPos = new Point4y();

	/** 画像レイヤー */
	abstract public int getImageLayer(BufferedImage[] layer);

	/** 影の画像 */
	@Transient
	abstract public BufferedImage getShadowImage();

	/** リストから除去 */
	abstract public void removeFromWorld();

	/** オブジェクトのタイプのゲッター */
	public WorldEntityKind getWorldEntityType() {
		return worldEntityType;
	}

	/** オプションのゲッター */
	public int getOption() {
		return option;
	}

	/** オプションのセッター */
	public void setOption(int setOption) {
		option = setOption;
	}

	/** セットアップのためのメニューの有無 */
	public boolean hasSetupMenu() {
		return false;
	}

	/** 親オブジェクトのゲッター */
	public int getParentLinkId() {
		return parentLinkId;
	}

	/** 見た目 */
	public int getLooks() {
		return looks;
	}

	/** チェックされるべき時かどうか */
	public boolean checkInterval(int cnt) {
		return ((cnt % interval) == 0);
	}

	/** 稼働中か否か */
	public boolean getEnabled() {
		return enabled;
	}

	/** 稼働非稼働の切り替え */
	public void setEnabled(boolean enb) {
		enabled = enb;
	}

	/** 稼働状況逆転 */
	public void invertEnabled() {
		enabled = !enabled;
	}

	/** 当たり判定の大きさセッター */
	protected void setCollisionSize(int halfW, int halfH) {
		colW = halfW;
		colH = halfH;
	}

	/**
	 * 当たり判定の大きさゲッター
	 * (Revtangle Ver)
	 */
	public Rectangle getCollisionRect(Rectangle r) {
		int x = getScreenPivot().getX();
		int y = getScreenPivot().getY();
		r.x = x - colW;
		r.y = y - colH;
		r.width = colW << 1;
		r.height = colH << 1;
		return r;
	}

	/** あたり判定の可否 */
	public boolean enableHitCheck() {
		return true;
	}

	/** 当たり判定されているオブジェクトのタイプ取得 */
	@Transient
	public int getHitCheckObjType() {
		return hitCheckObjType;
	};

	/**
	 * 当たり判定
	 * 
	 * @param obj    判定を受けるオブジェクト
	 * @param checkZ 空中のものもチェックするかどうか
	 * @return 当たっているかどうか
	 */
	public boolean checkHitObj(Entity obj, boolean checkZ) {
		if (obj == null) {
			return false;
		}
		int objZ = obj.getZ();
		if ((!checkZ || objZ == 0)) {
			// フラグ非設定時は空中の物はチェックしない
			Rectangle tmpRect = new Rectangle();
			getCollisionRect(tmpRect);
			// 対象の座標をフィールド座標に変換
			Translate.translate(obj.getX(), obj.getY(), tmpPos);
			// 点が描画矩形に入ったかの判定
			if (tmpRect.contains(new java.awt.Point(tmpPos.getX(), tmpPos.getY()))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 当たり判定されるオブジェクトのチェック
	 * <br>
	 * 動作はobjHitProcess( Entity o )で
	 * 
	 * @param colRect 判定用長方形
	 * @param o       対象オブジェクト
	 */
	public boolean checkHitObj(Rectangle colRect, Entity o) {
		if (o == null) {
			return false;
		}
		int objZ = o.getZ();
		if (objZ == 0) { // 空中の物は移動させない
			// 対象の座標をフィールド座標に変換
			Translate.translate(o.getX(), o.getY(), tmpPos);
			// 点が描画矩形に入ったかの判定
			if (colRect.contains(new java.awt.Point(tmpPos.getX(), tmpPos.getY()))) {
				objHitProcess(o);
				return false;
			}
		}
		return true;
	}

	/** 当たり判定されたオブジェクトへの処理 */
	public int objHitProcess(Entity o) {
		return 0;
	};

	@Override
	/**
	 * 毎ティックごとの処理
	 * <br>
	 * 主に移動系
	 */
	public TickResult clockTick() {
		setAge(getAge() + TICK);
		if (isRemoved()) {
			removeFromWorld();
			GameWorld.get().getCurrentWorldState().unregisterEntity(objId);
			return TickResult.REMOVED;
		}

		int mapX = Translate.getWorldWidth();
		int mapY = Translate.getWorldHeight();

		if (!grabbed) {
			int mx = vx + getMotionX();
			int my = vy + getMotionY();
			int mz = vz + getMotionZ();

			if (mx != 0) {
				x += mx;
				if (x < 0) {
					x = 0;
					vx *= -1;
				} else if (x > mapX) {
					x = mapX;
					vx *= -1;
				} else if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.ITEM_BLOCK_FLAG)) {
					x -= vx;
					vx = 0;
				}
			}
			if (my != 0) {
				y += my;
				if (y < 0) {
					y = 0;
					vy *= -1;
				} else if (y > mapY) {
					y = mapY;
					vy *= -1;
				} else if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.ITEM_BLOCK_FLAG)) {
					y -= vy;
					vy = 0;
				}
			}
			if (z != 0 || mz != 0) {
				mz += 1;
				vz += 1;
				z -= mz;
				if (!fallingUnderGround || objType == Type.PLATFORM) {
					if (z <= mostDepth || objType == Type.PLATFORM) {
						z = mostDepth;
						vx = 0;
						vy = 0;
						vz = 0;
					}
				}
			}
		}
		upDate();
		setMotionX(0);
		setMotionY(0);
		setMotionZ(0);
		calcPos();
		return TickResult.NONE;
	}

	/**
	 * 毎ティックごとの処理
	 * <br>
	 * メインが長いときの分割分
	 */
	public void upDate() {
		// 処理なし
	};

	/** 初期設定 */
	public WorldEntity(int initX, int initY, int initOption) {
		objId = Numbering.INSTANCE.numberingObjId();
		objType = Type.PLATFORM;
		x = initX;
		y = initY;
		z = 0;
		option = initOption;
		enabled = true;
	}

	/** アニメーション更新間隔（ティック）を返す。 */
	public int getInterval() {
		return interval;
	}

	/** アニメーション更新間隔（ティック）をセットする。 */
	public void setInterval(int interval) {
		this.interval = interval;
	}

	/** 衝突判定の幅を返す。 */
	public int getColW() {
		return colW;
	}

	/** 衝突判定の幅をセットする。 */
	public void setColW(int colW) {
		this.colW = colW;
	}

	/** 衝突判定の高さを返す。 */
	public int getColH() {
		return colH;
	}

	/** 衝突判定の高さをセットする。 */
	public void setColH(int colH) {
		this.colH = colH;
	}

	/** 仮座標を返す。 */
	public Point4y getTmpPos() {
		return tmpPos;
	}

	/** 仮座標をセットする。 */
	public void setTmpPos(Point4y tmpPos) {
		this.tmpPos = tmpPos;
	}

	/** ワールドエンティティの種別をセットする。 */
	public void setObjEXType(WorldEntityKind worldEntityType) {
		this.worldEntityType = worldEntityType;
	}

	/** 親リンク ID をセットする。 */
	public void setParentLinkId(int parentLinkId) {
		this.parentLinkId = parentLinkId;
	}

	/** 外見バリアントをセットする。 */
	public void setLooks(int looks) {
		this.looks = looks;
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public WorldEntity() {

	}
}
