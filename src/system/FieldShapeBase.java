package src.system;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.Serializable;

import src.SimYukkuri;
import src.enums.Event;
import src.system.ItemMenu.ShapeMenu;
import src.system.ItemMenu.ShapeMenuTarget;



/*******************************************************
 * 
 *	フィールドに配置される図形型アイテムの抽象クラス
 *
 */
public abstract class FieldShapeBase implements Serializable {
	protected static final long serialVersionUID = 1L;

	public static final int TICK = SimYukkuri.TICK;

	public static final Stroke PREVIEW_STROKE = new BasicStroke(3.0f);
	public static final Color PREVIEW_COLOR = Color.WHITE;

	// 各種壁の通過可否で使用されるビットフラグ
	public static final int MAP_BABY = 1;
	public static final int MAP_CHILD = 2;
	public static final int MAP_ADULT = 4;
	public static final int MAP_ITEM =  8;
	public static final int MAP_NOUNUN = 32;	
	public static final int MAP_KEKKAI = 1024;
	public static final int[] MAP_BODY = {MAP_BABY, MAP_CHILD, MAP_ADULT};

	public static final int BARRIER_GAP_MINI = MAP_BABY;
	public static final int BARRIER_GAP_BIG = MAP_BABY + MAP_CHILD;
	public static final int BARRIER_NET_MINI = MAP_CHILD + MAP_ADULT;
	public static final int BARRIER_NET_BIG = MAP_ADULT;
	public static final int BARRIER_WALL = MAP_BABY + MAP_CHILD + MAP_ADULT + MAP_ITEM;
	public static final int BARRIER_ITEM = MAP_ITEM;
	public static final int BARRIER_YUKKURI = MAP_BABY + MAP_CHILD + MAP_ADULT;
	public static final int BARRIER_NOUNUN = MAP_NOUNUN;
	public static final int BARRIER_KEKKAI = MAP_KEKKAI;
	
	// コンベア、池、畑で使用されるビットフラグ
	// 床置きオブジェクトのフラグもあるのはゆっくりの危険回避マップとしても使うため
	public static final int FIELD_PLAT = 1;
	public static final int FIELD_BELT = 2;
	public static final int FIELD_FARM = 4;
	public static final int FIELD_POOL = 8;

	// マップ座標 = 内部処理座標
	protected int mapSX;
	protected int mapSY;
	protected int mapEX;
	protected int mapEY;
	protected int mapW;
	protected int mapH;

	// フィールド座標 = マウス座標 = 描画座標
	protected int fieldSX;
	protected int fieldSY;
	protected int fieldEX;
	protected int fieldEY;
	protected int fieldW;
	protected int fieldH;

	protected long age = 0;
	protected boolean removed = false;

	public int getMapSX() {
		return mapSX;
	}

	public int getMapSY() {
		return mapSY;
	}

	public int getMapEX() {
		return mapEX;
	}

	public int getMapEY() {
		return mapEY;
	}
	
	public void setMapPos(int sx, int sy, int ex, int ey) {
		mapSX = sx;
		mapSY = sy;
		mapEX = ex;
		mapEY = ey;
	}
	
	public boolean mapContains(int mx, int my) {
		if(mapSX <= mx && mx <= mapEX
				&& mapSY <= my && my <= mapEY) {
			return true;
		}
		return false;
	}

	public int getFieldSX() {
		return fieldSX;
	}

	public int getFieldSY() {
		return fieldSY;
	}

	public int getFieldEY() {
		return fieldEY;
	}

	public int getFieldEX() {
		return fieldEX;
	}

	public void setFieldPos(int sx, int sy, int ex, int ey) {
		fieldSX = sx;
		fieldSY = sy;
		fieldEX = ex;
		fieldEY = ey;
	}

	public boolean fieldContains(int fx, int fy) {
		if(fieldSX <= fx && fx <= fieldEX
				&& fieldSY <= fy && fy <= fieldEY) {
			return true;
		}
		return false;
	}

	public ShapeMenuTarget hasShapePopup() {
		return ShapeMenuTarget.NONE;
	}

	public void executeShapePopup(ShapeMenu menu) {}

	public void remove() {
		removed = true;
	}
	
	public boolean isRemoved() {
		return removed;
	}

	public Event clockTick()
	{
		age += TICK;
		if (removed) {
			return Event.REMOVED;
		}
		return Event.DONOTHING;
	}

	abstract public int getAttribute();
	abstract public int getMinimumSize();
	abstract public void drawShape(Graphics2D g2);
}


