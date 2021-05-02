package src.base;


import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import src.draw.Translate;
import src.enums.Event;
import src.enums.ObjEXType;
import src.enums.Type;
import src.item.Barrier;

/*********************************************************
 *  ゆっくり以外のゲーム内オブジェクトの元となるクラス
 */
public abstract class ObjEX extends Obj implements java.io.Serializable {
	static final long serialVersionUID = 1L;
	/**オブジェクトタイプ*/
	protected ObjEXType objEXType;
	/**追加情報用の汎用定数*/
	protected int option;

	/** 排他的論理和でフラグ管理している ex) 0101 ならゆっくりと食物*/
	public static final int
			YUKKURI 	= (int)Math.pow(2, 0),
			SHIT			= (int)Math.pow(2, 1),
			FOOD 		= (int)Math.pow(2, 2),
			TOILET		= (int)Math.pow(2, 3),
			TOY			= (int)Math.pow(2, 4),
			PLATFORM	= (int)Math.pow(2, 5),
			FIX_OBJECT	= (int)Math.pow(2, 6),
			OBJECT		= (int)Math.pow(2, 7),
			VOMIT		= (int)Math.pow(2, 8),
			STALK		= (int)Math.pow(2, 9);
	/**処理対象
	 * <br>デフォルトは0(処理対象無し)*/
	public static final int hitCheckObjType = 0;

	/**アイテムのランク*/
	public enum ItemRank {
		HOUSE,
		NORA,
		YASEI
	}
	/**標準用長方形型境界線*/
	protected static Rectangle boundary = new Rectangle();
	protected Random rnd = new Random();


	/**親オブジェクト*/
	protected Obj linkParent = null;
	/**ゆっくりに対しての見た目(FOODでのみ使用)*/
	protected int looks = 0;
	/**負荷分散のためのインターバル値*/
	protected int interval = 1;
	/**汎用スイッチ*/
	protected boolean enabled = true;
	/**原点からの当たり判定範囲
	 * <br>画像=判定ならpivXYでOK プレス機など設置地面付近なら要調整*/
	protected int colW = 0,colH = 0 ;
	/**処理対象の位置*/
	protected Point tmpPos = new Point();

	/**画像レイヤー*/
	abstract public int getImageLayer(BufferedImage[] layer);

	/**影の画像*/
	abstract public BufferedImage getShadowImage();

	/**リストから除去*/
	abstract public void removeListData();

	/**オブジェクトのタイプのゲッター*/
	public ObjEXType getObjEXType() {
		return objEXType;
	}

	/**オプションのゲッター*/
	public int getOption() {
		return option;
	}

	/**オプションのセッター*/
	public void setOption( int setOption ) {
		option = setOption;
	}

	/**セットアップのためのメニューの有無*/
	public boolean hasSetupMenu() {
		return false;
	}

	/**親オブジェクトのゲッター*/
	public Obj getParent() {
		return linkParent;
	}

	/**見た目*/
	public int getLooks() {
		return looks;
	}

	/**チェックされるべき時かどうか*/
	public boolean checkInterval(int cnt) {
		return ((cnt % interval) == 0);
	}

	/**稼働中か否か*/
	public boolean getEnabled() {
		return enabled;
	}
	/**稼働非稼働の切り替え*/
	public void setEnabled(boolean enb) {
		enabled = enb;
	}
	/**稼働状況逆転*/
	public void invertEnabled() {
		enabled = !enabled;
	}


	/**当たり判定の大きさセッター*/
	protected void setCollisionSize(int halfW, int halfH) {
		colW = halfW;
		colH = halfH;
	}
	/**当たり判定の大きさゲッター
	 * (Revtangle Ver)*/
	public void getCollisionRect(Rectangle r) {
		int x = getScreenPivot().x;
		int y = getScreenPivot().y;
		r.x = x - colW;
		r.y = y - colH;
		r.width = colW << 1;
		r.height = colH << 1;
	}

	/**あたり判定の可否*/
	public boolean enableHitCheck() {
		return true;
	}

	/**当たり判定されているオブジェクトのタイプ取得*/
	public int getHitCheckObjType() {
		return hitCheckObjType;
	};

	/** 当たり判定
	 * @param o 判定を受けるオブジェクト
	 * @param bCheckZ 空中のものもチェックするかどうか
	 * @return 当たっているかどうか
	 */
	public boolean checkHitObj(Obj o, boolean bCheckZ) {
		if( o == null ){
			return false;
		}
		int objZ = o.getZ();
		if ( (!bCheckZ || objZ == 0) ) {
			// フラグ非設定時は空中の物はチェックしない
			Rectangle tmpRect = new Rectangle();
			getCollisionRect(tmpRect);
			// 対象の座標をフィールド座標に変換
			Translate.translate(o.getX(), o.getY(), tmpPos);
			// 点が描画矩形に入ったかの判定
			if(tmpRect.contains(tmpPos)) {
				return true;
			}
		}
		return false;
	}

	/** 当たり判定されるオブジェクトのチェック
	 * <br>動作はobjHitProcess( Obj o )で
	 * @param colRect 判定用長方形
	 * @param o 対象オブジェクト
	 */
	public boolean checkHitObj(Rectangle colRect, Obj o) {
		if( o == null ){
			return false;
		}
		int objZ = o.getZ();
		if ( objZ == 0 ) {	//空中の物は移動させない
			// 対象の座標をフィールド座標に変換
			Translate.translate(o.getX(), o.getY(), tmpPos);
			// 点が描画矩形に入ったかの判定
			if(colRect.contains(tmpPos)) {
				objHitProcess( o );
				return false;
			}
		}
		return true;
	}

	/**当たり判定されたオブジェクトへの処理*/
	public int objHitProcess( Obj o ) {
		return 0;
	};

	@Override
	/**毎ティックごとの処理
	 * <br>主に移動系*/
	public Event clockTick(){
		setAge(getAge() + TICK);
		if (isRemoved()) {
			removeListData();
			return Event.REMOVED;
		}

		int mapX = Translate.mapW;
		int mapY = Translate.mapH;

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
				else if (x > mapX) {
					x = mapX;
					vx *= -1;
				}
				else if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.MAP_ITEM)) {
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
				else if (y > mapY) {
					y = mapY;
					vy *= -1;
				}
				else if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.MAP_ITEM)) {
					y -= vy;
					vy = 0;
				}
			}
			if (z != 0 || mz != 0) {
				mz += 1;
				vz += 1;
				z -= mz;
				if( !bFallingUnderGround || objType == Type.PLATFORM ){
					if (z <= nMostDepth || objType == Type.PLATFORM ) {
						z = nMostDepth;
						vx = 0;
						vy = 0;
						vz = 0;
					}
				}
			}
		}
		upDate();
		setBx(0);
		setBy(0);
		bz = 0;
		return Event.DONOTHING;
	}

	/**毎ティックごとの処理
	 * <br>メインが長いときの分割分*/
	public void upDate() {
		//処理なし
	};

	/**初期設定*/
	public ObjEX(int initX, int initY, int initOption) {
		objType = Type.PLATFORM;
		x = initX;
		y = initY;
		z = 0;
		option = initOption;
		enabled = true;
	}
}