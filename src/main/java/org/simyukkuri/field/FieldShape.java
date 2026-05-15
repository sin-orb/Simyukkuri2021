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



/*******************************************************
 * 
 *	フィールドに配置される図形型アイテムの抽象クラス
 *<br>継承先：壁、ベルコン、畑、池
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
	public static final int BABY_BLOCK_FLAG = 1, CHILD_BLOCK_FLAG = 2, ADULT_BLOCK_FLAG = 4, ITEM_BLOCK_FLAG = 8;
	/** 各種壁の通過可否で使用されるビットフラグ2*/
	public static final int NO_UNUN_BLOCK_FLAG = 32, KEKKAI_BLOCK_FLAG = 1024;
	/** 各種壁の通過可否で使用されるビットフラグ3*/
	public static final int[] BODY_BLOCK_FLAGS = {BABY_BLOCK_FLAG, CHILD_BLOCK_FLAG, ADULT_BLOCK_FLAG};
	/**各種壁の通過可否を決めるビットフラグ1
	 * <br>段差用*/
	public static final int BARRIER_GAP_MINI = BABY_BLOCK_FLAG, BARRIER_GAP_BIG = BABY_BLOCK_FLAG + CHILD_BLOCK_FLAG;
	/**各種壁の通過可否を決めるビットフラグ2
	 * <br>金網用*/
	public static final int BARRIER_NET_MINI = CHILD_BLOCK_FLAG + ADULT_BLOCK_FLAG, BARRIER_NET_BIG = ADULT_BLOCK_FLAG;
	/**各種壁の通過可否を決めるビットフラグ3
	 * <br>壁用*/
	public static final int BARRIER_WALL = BABY_BLOCK_FLAG + CHILD_BLOCK_FLAG + ADULT_BLOCK_FLAG + ITEM_BLOCK_FLAG;
	/**各種壁の通過可否を決めるビットフラグ4
	 * <br>特定オブジェクト禁止用*/
	public static final int BARRIER_ITEM = ITEM_BLOCK_FLAG, BARRIER_YUKKURI = BABY_BLOCK_FLAG + CHILD_BLOCK_FLAG + ADULT_BLOCK_FLAG,BARRIER_NOUNUN = NO_UNUN_BLOCK_FLAG;
	/**各種壁の通過可否を決めるビットフラグ5
	 * <br>結界()用*/
	public static final int BARRIER_KEKKAI = KEKKAI_BLOCK_FLAG;
	
	/** コンベア、池、畑で使用されるビットフラグ
	 * <br> 床置きオブジェクトのフラグもあるのはゆっくりの危険回避マップとしても使うため*/
	public static final int FIELD_PLAT = 1,FIELD_BELT = 2,FIELD_FARM = 4,FIELD_POOL = 8;

	/** マップ座標 (= 図形型アイテムの内部処理用座標) 1
	 * <br>設置起点の座標*/
	protected int mapSX,mapSY;
	/** マップ座標 (= 図形型アイテムの内部処理用座標) 2
	 * <br>設置終点の座標*/
	protected int mapEX,mapEY;
	/** マップ座標 (= 図形型アイテムの内部処理用座標) 3
	 * <br>アイテムの横幅と縦幅*/
	protected int mapW,mapH;

	/** フィールド座標 (= 図形型アイテムの描画時にマウスを置いている座標 = 図形型アイテムの描画用座標)1
	 * <br>設置起点の座標*/
	protected int fieldSX,fieldSY;
	/** フィールド座標 (= 図形型アイテムの描画時にマウスを置いている座標 = 図形型アイテムの描画用座標)2
	 * <br>設置終点の座標*/
	protected int fieldEX,fieldEY;
	/** フィールド座標 (= 図形型アイテムの描画時にマウスを置いている座標 = 図形型アイテムの描画用座標)3
	 * <br>アイテムの横幅と縦幅*/
	protected int fieldW,fieldH;
	/**設置からの経過時間*/
	protected long age = 0;
	/**除去されているか*/
	protected boolean removed = false;
	/**マップ座標の設置起点のX座標ゲッター*/
	public int getStartX() {
		return mapSX;
	}
	/**マップ座標の設置起点のY座標ゲッター*/
	public int getStartY() {
		return mapSY;
	}
	/**マップ座標の設置終点のX座標ゲッター*/
	public int getEndX() {
		return mapEX;
	}
	/**マップ座標の設置終点のY座標ゲッター*/
	public int getEndY() {
		return mapEY;
	}
	/**マップ座標セッター*/
	public void setBounds(int sx, int sy, int ex, int ey) {
		mapSX = sx;
		mapSY = sy;
		mapEX = ex;
		mapEY = ey;
	}
	/**
	 * 渡された座標の点が図形型アイテムの範囲内に入っているかどうか
	 * <br>マップ座標で判断するVer
	 * @param mx 渡す点のX座標
	 * @param my 渡す点のY座標
	 * @return 入っているかいないか
	 */
	public boolean mapContains(int mx, int my) {
		if(mapSX <= mx && mx <= mapEX
				&& mapSY <= my && my <= mapEY) {
			return true;
		}
		return false;
	}
	/**フィールド座標の設置起点のX座標のゲッター*/
	public int getFieldSX() {
		return fieldSX;
	}
	/**フィールド座標の設置起点のY座標のゲッター*/
	public int getFieldSY() {
		return fieldSY;
	}
	/**フィールド座標の設置終点のY座標のゲッター*/
	public int getFieldEY() {
		return fieldEY;
	}
	/**フィールド座標の設置終点のX座標のゲッター*/
	public int getFieldEX() {
		return fieldEX;
	}
	/**フィールド座標のセッター*/
	public void setFieldPos(int sx, int sy, int ex, int ey) {
		fieldSX = sx;
		fieldSY = sy;
		fieldEX = ex;
		fieldEY = ey;
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
		if(fieldSX <= fx && fx <= fieldEX
				&& fieldSY <= fy && fy <= fieldEY) {
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
	public TickResult clockTick()
	{
		age += TICK;
		if (removed) {
			return TickResult.REMOVED;
		}
		return TickResult.NONE;
	}
	/**各種属性の汎用ゲッター*/
	abstract public int getAttribute();
	/**最小サイズゲッター*/
	abstract public int getMinimumSize();
	/**配置時の境界線を描く*/
	abstract public void drawShape(Graphics2D g2);
	public int getWorldWidth() {
		return mapW;
	}
	public void setWorldWidth(int worldWidth) {
		this.mapW = worldWidth;
	}
	public int getWorldHeight() {
		return mapH;
	}
	public void setWorldHeight(int worldHeight) {
		this.mapH = worldHeight;
	}
	public int getFieldW() {
		return fieldW;
	}
	public void setFieldW(int fieldW) {
		this.fieldW = fieldW;
	}
	public int getFieldH() {
		return fieldH;
	}
	public void setFieldH(int fieldH) {
		this.fieldH = fieldH;
	}
	public long getAge() {
		return age;
	}
	public void setAge(long age) {
		this.age = age;
	}
	public void setStartX(int mapSX) {
		this.mapSX = mapSX;
	}
	public void setStartY(int mapSY) {
		this.mapSY = mapSY;
	}
	public void setEndX(int mapEX) {
		this.mapEX = mapEX;
	}
	public void setEndY(int mapEY) {
		this.mapEY = mapEY;
	}
	public void setFieldSX(int fieldSX) {
		this.fieldSX = fieldSX;
	}
	public void setFieldSY(int fieldSY) {
		this.fieldSY = fieldSY;
	}
	public void setFieldEX(int fieldEX) {
		this.fieldEX = fieldEX;
	}
	public void setFieldEY(int fieldEY) {
		this.fieldEY = fieldEY;
	}
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
}

