package org.simyukkuri.field;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.Serializable;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.system.ItemMenu.ShapeMenu;
import org.simyukkuri.system.ItemMenu.ShapeMenuTarget;

/**
 * フィールドに配置される図形型アイテムの抽象クラス
 * <br>継承先：壁、ベルコン、畑、池
 */
public abstract class FieldShape implements Serializable {
	private static final long serialVersionUID = 7126507907121745508L;
	/**時定数*/
	public static final int TICK = SimYukkuri.TICK;
	/**ストロークの定義*/
	public static final Stroke PREVIEW_STROKE = new BasicStroke(3.0f);
	/**プレビューストロークの色*/
	public static final Color PREVIEW_COLOR = Color.WHITE;

	/** 各種壁の通過可否で使用されるビットフラグ*/
	public static final int BABY_BLOCK_FLAG = 1;
	public static final int CHILD_BLOCK_FLAG = 2;
	public static final int ADULT_BLOCK_FLAG = 4;
	public static final int ITEM_BLOCK_FLAG = 8;
	/** 各種壁の通過可否で使用されるビットフラグ2*/
	public static final int NO_UNUN_BLOCK_FLAG = 32;
	public static final int KEKKAI_BLOCK_FLAG = 1024;
	/** 各種壁の通過可否で使用されるビットフラグ3*/
	public static final int[] BODY_BLOCK_FLAGS = {BABY_BLOCK_FLAG, CHILD_BLOCK_FLAG, ADULT_BLOCK_FLAG};
	/**各種壁の通過可否を決めるビットフラグ1
	 * <br>段差用*/
	public static final int BARRIER_GAP_MINI = BABY_BLOCK_FLAG;
	public static final int BARRIER_GAP_BIG = BABY_BLOCK_FLAG + CHILD_BLOCK_FLAG;
	/**各種壁の通過可否を決めるビットフラグ2
	 * <br>金網用*/
	public static final int BARRIER_NET_MINI = CHILD_BLOCK_FLAG + ADULT_BLOCK_FLAG;
	public static final int BARRIER_NET_BIG = ADULT_BLOCK_FLAG;
	/**各種壁の通過可否を決めるビットフラグ3
	 * <br>壁用*/
	public static final int BARRIER_WALL = BABY_BLOCK_FLAG + CHILD_BLOCK_FLAG + ADULT_BLOCK_FLAG + ITEM_BLOCK_FLAG;
	/**各種壁の通過可否を決めるビットフラグ4
	 * <br>特定オブジェクト禁止用*/
	public static final int BARRIER_ITEM = ITEM_BLOCK_FLAG;
	public static final int BARRIER_YUKKURI = BABY_BLOCK_FLAG + CHILD_BLOCK_FLAG + ADULT_BLOCK_FLAG;
	public static final int BARRIER_NOUNUN = NO_UNUN_BLOCK_FLAG;
	/**各種壁の通過可否を決めるビットフラグ5
	 * <br>結界()用*/
	public static final int BARRIER_KEKKAI = KEKKAI_BLOCK_FLAG;
	
	/** コンベア、池、畑で使用されるビットフラグ
	 * <br> 床置きオブジェクトのフラグもあるのはゆっくりの危険回避マップとしても使うため*/
	public static final int FIELD_PLAT = 1;
	public static final int FIELD_BELT = 2;
	public static final int FIELD_FARM = 4;
	public static final int FIELD_POOL = 8;

	/** マップ座標 (= 図形型アイテムの内部処理用座標) 1
	 * <br>設置起点の座標*/
	protected int mapSx;
	protected int mapSy;
	/** マップ座標 (= 図形型アイテムの内部処理用座標) 2
	 * <br>設置終点の座標*/
	protected int mapEx;
	protected int mapEy;
	/** マップ座標 (= 図形型アイテムの内部処理用座標) 3
	 * <br>アイテムの横幅と縦幅*/
	protected int mapW;
	protected int mapH;

	/** フィールド座標 (= 図形型アイテムの描画時にマウスを置いている座標 = 図形型アイテムの描画用座標)1
	 * <br>設置起点の座標*/
	protected int fieldSx;
	protected int fieldSy;
	/** フィールド座標 (= 図形型アイテムの描画時にマウスを置いている座標 = 図形型アイテムの描画用座標)2
	 * <br>設置終点の座標*/
	protected int fieldEx;
	protected int fieldEy;
	/** フィールド座標 (= 図形型アイテムの描画時にマウスを置いている座標 = 図形型アイテムの描画用座標)3
	 * <br>アイテムの横幅と縦幅*/
	protected int fieldW;
	protected int fieldH;
	/**設置からの経過時間*/
	protected long age = 0;
	/**除去されているか*/
	protected boolean removed = false;
	/**マップ座標の設置起点のX座標ゲッター*/

	public int getStartX() {
		return mapSx;
	}
	/**マップ座標の設置起点のY座標ゲッター*/

	public int getStartY() {
		return mapSy;
	}
	/**マップ座標の設置終点のX座標ゲッター*/

	public int getEndX() {
		return mapEx;
	}
	/**マップ座標の設置終点のY座標ゲッター*/

	public int getEndY() {
		return mapEy;
	}
	/**マップ座標セッター*/

	public void setBounds(int sx, int sy, int ex, int ey) {
		mapSx = sx;
		mapSy = sy;
		mapEx = ex;
		mapEy = ey;
	}
	/**
	 * 渡された座標の点が図形型アイテムの範囲内に入っているかどうか
	 * <br>マップ座標で判断するVer
	 * @param mx 渡す点のX座標
	 * @param my 渡す点のY座標
	 * @return 入っているかいないか
	 */

	public boolean mapContains(int mx, int my) {
		if (mapSx <= mx && mx <= mapEx
				&& mapSy <= my && my <= mapEy) {
			return true;
		}
		return false;
	}
	/**フィールド座標の設置起点のX座標のゲッター*/

	public int getFieldSx() {
		return fieldSx;
	}
	/**フィールド座標の設置起点のY座標のゲッター*/

	public int getFieldSy() {
		return fieldSy;
	}
	/**フィールド座標の設置終点のY座標のゲッター*/

	public int getFieldEy() {
		return fieldEy;
	}
	/**フィールド座標の設置終点のX座標のゲッター*/

	public int getFieldEx() {
		return fieldEx;
	}
	/**フィールド座標のセッター*/

	public void setFieldPos(int sx, int sy, int ex, int ey) {
		fieldSx = sx;
		fieldSy = sy;
		fieldEx = ex;
		fieldEy = ey;
	}
	/**
	 * 渡された座標の点が図形型アイテムの範囲内に入っているかどうか
	 * <br>フィールドマップ座標で判断するVer
	 * <br>未使用
	 * @param fx 渡す点のX座標
	 * @param fy 渡す点のY座標
	 * @return 入っているかいないか
	 */

	public boolean fieldContains(int fx, int fy) {
		if (fieldSx <= fx && fx <= fieldEx
				&& fieldSy <= fy && fy <= fieldEy) {
			return true;
		}
		return false;
	}
	/**カーソルを近づけたときにポップアップがあるかどうか*/

	public ShapeMenuTarget hasShapePopup() {
		return ShapeMenuTarget.NONE;
	}
	/**ポップアップの実行部*/

	public void executeShapePopup(ShapeMenu menu) {}
	/**除去*/

	public void remove() {
		removed = true;
	}
	/**除去されたかどうか*/

	public boolean isRemoved() {
		return removed;
	}
	/**毎ティックごとの処理 **/

	public TickResult clockTick() {
		age += TICK;
		if (removed) {
			return TickResult.REMOVED;
		}
		return TickResult.NONE;
	}
	/**各種属性の汎用ゲッター*/

	public abstract int getAttribute();
	/**最小サイズゲッター*/

	public abstract int getMinimumSize();
	/**配置時の境界線を描く*/

	public abstract void drawShape(Graphics2D g2);
	/** ワールドの横幅（マップ座標）を返す。 */

	public int getWorldWidth() {
		return mapW;
	}
	/** ワールドの横幅をセットする。 */

	public void setWorldWidth(int worldWidth) {
		this.mapW = worldWidth;
	}
	/** ワールドの縦幅（マップ座標）を返す。 */

	public int getWorldHeight() {
		return mapH;
	}
	/** ワールドの縦幅をセットする。 */

	public void setWorldHeight(int worldHeight) {
		this.mapH = worldHeight;
	}
	/** フィールド描画幅を返す。 */

	public int getFieldW() {
		return fieldW;
	}
	/** フィールド描画幅をセットする。 */

	public void setFieldW(int fieldW) {
		this.fieldW = fieldW;
	}
	/** フィールド描画高さを返す。 */

	public int getFieldH() {
		return fieldH;
	}
	/** フィールド描画高さをセットする。 */

	public void setFieldH(int fieldH) {
		this.fieldH = fieldH;
	}
	/** フィールドオブジェクトの経過ティック数を返す。 */

	public long getAge() {
		return age;
	}
	/** フィールドオブジェクトの経過ティック数をセットする。 */

	public void setAge(long age) {
		this.age = age;
	}
	/** 配置起点の X 座標（マップ座標）をセットする。 */

	public void setStartX(int mapSx) {
		this.mapSx = mapSx;
	}
	/** 配置起点の Y 座標（マップ座標）をセットする。 */

	public void setStartY(int mapSy) {
		this.mapSy = mapSy;
	}
	/** 配置終点の X 座標（マップ座標）をセットする。 */

	public void setEndX(int mapEx) {
		this.mapEx = mapEx;
	}
	/** 配置終点の Y 座標（マップ座標）をセットする。 */

	public void setEndY(int mapEy) {
		this.mapEy = mapEy;
	}
	/** フィールド起点の X 座標をセットする。 */

	public void setFieldSx(int fieldSx) {
		this.fieldSx = fieldSx;
	}
	/** フィールド起点の Y 座標をセットする。 */

	public void setFieldSy(int fieldSy) {
		this.fieldSy = fieldSy;
	}
	/** フィールド終点の X 座標をセットする。 */

	public void setFieldEx(int fieldEx) {
		this.fieldEx = fieldEx;
	}
	/** フィールド終点の Y 座標をセットする。 */

	public void setFieldEy(int fieldEy) {
		this.fieldEy = fieldEy;
	}
	/** 削除済みフラグをセットする。 */

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
}

