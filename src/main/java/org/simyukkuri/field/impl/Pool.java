package org.simyukkuri.field.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.BeltconveyorObj;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.field.FieldShape;
import org.simyukkuri.system.ItemMenu.ShapeMenu;
import org.simyukkuri.system.ItemMenu.ShapeMenuTarget;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;

/**
 * 池
 */
public class Pool extends FieldShape {

	private static final long serialVersionUID = 745411694776554936L;
	/** 池のふちどりの色 */
	public static final Color ROCK_COLOR = new Color(200, 140, 30);
	private static final int MIN_SIZE = 8;

	private static BufferedImage images;
	private static TexturePaint texture;
	private int[] waterPolygonX = new int[4];
	private int[] waterPolygonY = new int[4];

	/** 池に捕まってるオブジェクトのリスト */
	List<Entity> bindObjList = new LinkedList<Entity>();

	/** 池の深さの列挙 */
	public enum Depth {
		NONE, // エリア外
		EDGE, // 角でまだ入ってない
		SHALLOW, // 浅い
		DEEP, // 深い
	}

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		images = ModLoader.loadItemImage(loader, "pool" + File.separator + "pool.png");
		texture = new TexturePaint(images, new Rectangle2D.Float(0, 0, images.getWidth(), images.getHeight()));
	}

	/** シェイプポップアップを持つかを返す（常に POOL）。 */
	@Override
	public ShapeMenuTarget hasShapePopup() {
		return ShapeMenuTarget.POOL;
	}

	/** シェイプポップアップのメニューアクションを実行する。 */
	@Override
	public void executeShapePopup(ShapeMenu menu) {

		List<Pool> list = GameWorld.get().getCurrentWorldState().getPools();
		int pos;

		switch (menu) {
			case SETUP:
				break;
			case TOP:
				list.remove(this);
				list.add(0, this);
				break;
			case UP:
				pos = list.indexOf(this);
				if (pos > 0) {
					list.remove(this);
					list.add(pos - 1, this);
				}
				break;
			case DOWN:
				pos = list.indexOf(this);
				if (pos < (list.size() - 1)) {
					list.remove(this);
					list.add(pos + 1, this);
				}
				break;
			case BOTTOM:
				list.remove(this);
				list.add(this);
				break;
			default:
				break;
		}
	}

	/** フィールドの属性値を返す。 */
	@Override
	@Transient
	public int getAttribute() {
		return FIELD_POOL;
	}

	/** バリアの最小サイズを返す。 */
	@Override
	@Transient
	public int getMinimumSize() {
		return MIN_SIZE;
	}

	/** プレビューラインの描画 */
	public static void drawPreview(Graphics2D g2, int sx, int sy, int ex, int ey) {
		int[] polygonX = new int[4];
		int[] polygonY = new int[4];
		Translate.getPolygonPoint(sx, sy, ex, ey, polygonX, polygonY);

		g2.drawPolygon(polygonX, polygonY, 4);
	}

	/** シェイプの外形を描画する。 */
	@Override
	public void drawShape(Graphics2D g2) {
		int[] polygonX = new int[4];
		int[] polygonY = new int[4];
		Translate.getPolygonPoint(fieldSx, fieldSy, fieldEx, fieldEy, polygonX, polygonY);

		g2.setPaint(ROCK_COLOR);
		g2.fillPolygon(polygonX, polygonY, 4);

		Translate.getPolygonPoint(fieldSx + 8, fieldSy + 8, fieldEx - 8, fieldEy - 8, waterPolygonX, waterPolygonY);
		g2.setPaint(texture);
		g2.fillPolygon(waterPolygonX, waterPolygonY, 4);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param fsx 設置起点のX座標
	 * @param fsy 設置起点のY座標
	 * @param fex 設置終点のX座標
	 * @param fey 設置終点のY座標
	 */
	public Pool(int fsx, int fsy, int fex, int fey) {
		Point4y start = Translate.getFieldLimitForWorld(fsx, fsy);
		Point4y end = Translate.getFieldLimitForWorld(fex, fey);
		fieldSx = start.getX();
		fieldSy = start.getY();
		fieldEx = end.getX();
		fieldEy = end.getY();

		int[] basePolygonX = new int[2];
		int[] basePolygonY = new int[2];
		Translate.getMovedPoint(fieldSx, fieldSy, fieldEx, fieldEy, 0, 0, 0, 0, basePolygonX, basePolygonY);

		// フィールド座標が渡ってくるのでマップ座標も計算しておく
		Point4y pos = Translate.invertLimit(basePolygonX[0], basePolygonY[0]);
		mapSx = Math.max(0, Math.min(pos.getX(), Translate.getWorldWidth()));
		mapSy = Math.max(0, Math.min(pos.getY(), Translate.getWorldHeight()));

		pos = Translate.invertLimit(basePolygonX[1], basePolygonY[1]);
		mapEx = Math.max(0, Math.min(pos.getX(), Translate.getWorldWidth()));
		mapEy = Math.max(0, Math.min(pos.getY(), Translate.getWorldHeight()));

		// 規定サイズと位置へ合わせる
		if ((mapEx - mapSx) < MIN_SIZE) {
			mapEx = mapSx + MIN_SIZE;
		}
		if ((mapEy - mapSy) < MIN_SIZE) {
			mapEy = mapSy + MIN_SIZE;
		}
		if (mapEx > Translate.getWorldWidth()) {
			mapSx -= (mapEx - Translate.getWorldWidth());
			mapEx -= (mapEx - Translate.getWorldWidth());
		}
		if (mapEy > Translate.getWorldHeight()) {
			mapSy -= (mapEy - Translate.getWorldHeight());
			mapEy -= (mapEy - Translate.getWorldHeight());
		}

		Point4y f = new Point4y();
		Translate.translate(mapSx, mapSy, f);
		fieldSx = f.getX();
		fieldSy = f.getY();
		Translate.translate(mapEx, mapEy, f);
		fieldEx = f.getX();
		fieldEy = f.getY();

		fieldW = fieldEx - fieldSx + 1;
		fieldH = fieldEy - fieldSy + 1;
		mapW = mapEx - mapSx + 1;
		mapH = mapEy - mapSy + 1;

		GameWorld.get().getCurrentWorldState().getPools().add(this);
		WorldState.setFieldFlag(GameWorld.get().getCurrentWorldState().getFieldGrid(), mapSx, mapSy, mapW, mapH, true,
				FIELD_POOL);
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public Pool() {

	}

	/** フィールド座標にあるシェイプ取得 */
	public static Pool getPool(int fx, int fy) {

		for (Pool bc : GameWorld.get().getCurrentWorldState().getPools()) {
			if (bc.fieldSx <= fx && fx <= bc.fieldEx
					&& bc.fieldSy <= fy && fy <= bc.fieldEy) {
				return bc;
			}
		}
		return null;
	}

	/** 削除 */
	public static void deletePool(Pool b) {
		WorldState.setFieldFlag(GameWorld.get().getCurrentWorldState().getFieldGrid(), b.mapSx, b.mapSy, b.mapW, b.mapH,
				false, FIELD_POOL);
		GameWorld.get().getCurrentWorldState().getPools().remove(b);
		// 重なってた部分の復元
		for (Pool bc : GameWorld.get().getCurrentWorldState().getPools()) {
			WorldState.setFieldFlag(GameWorld.get().getCurrentWorldState().getFieldGrid(), bc.mapSx, bc.mapSy, bc.mapW,
					bc.mapH, true, FIELD_POOL);
		}
	}

	/**
	 * ある点が畑の範囲内かどうか
	 * 
	 * @param inX     ある点のX座標
	 * @param inY     ある点Y座標
	 * @param isField 渡された座標がフィールド座標かどうか
	 */
	public boolean checkContain(int inX, int inY, boolean isField) {
		int xcord = inX;
		int ycord = inY;
		if (isField) {
			Point4y pos = Translate.invertLimit(inX, inY);
			xcord = pos.getX();
			ycord = pos.getY();
		}

		Point4y posFirst = Translate.invertLimit(waterPolygonX[0], waterPolygonY[0]);
		Point4y posSecond = Translate.invertLimit(waterPolygonX[2], waterPolygonY[2]);
		if (posFirst != null && posSecond != null) {
			if (posFirst.getX() <= xcord && xcord <= posSecond.getX() && posFirst.getY() <= ycord
					&& ycord <= posSecond.getY()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 渡されたオブジェクトが畑の中にあるかを判定
	 * <br>
	 * 動作はobjHitProcess( Entity o )で
	 */
	public boolean checkHitObj(Entity o) {
		if (o == null) {
			return false;
		}

		if (!checkContain(o.getX(), o.getY(), false)) {
			return false;
		}

		List<BeltconveyorObj> beltList = new LinkedList<>(
				GameWorld.get().getCurrentWorldState().getBeltconveyorObjects().values());
		if (beltList != null && beltList.size() != 0) {
			for (BeltconveyorObj belt : beltList) {
				// ベルトコンベア上なら池にまだ入ってない
				if (belt.checkContain(o.getX(), o.getY(), false)) {
					return false;
				}
			}
		}
		// エリア内
		return true;
	}

	/** 当たり判定されたオブジェクトへの処理 */
	public int objHitProcess(Entity o) {
		// 空中は無視
		int zcord = o.getZ();
		if (0 < zcord) {
			return 0;
		}

		boolean isInWater = false;
		o.setInPool(true);
		Depth depth = checkArea(o.getX(), o.getY());
		switch (depth) {
			case EDGE:
				o.setFallingUnderGround(false);
				o.setMostDepth(0);
				if (zcord < 0) {
					o.setCalcZ(0);
				}
				break;
			case SHALLOW:
				isInWater = true;
				// すこし沈む
				if (!o.isFallingUnderGround()) {
					o.setMostDepth(-1);
				}

				if (zcord == 0) {
					o.setCalcZ(-1);
				}
				break;
			case DEEP:
				isInWater = true;
				// もうすこし沈む
				if (!o.isFallingUnderGround()) {
					o.setMostDepth(-2);
				}
				if (zcord == 0 || zcord == -1) {
					o.setCalcZ(-2);
				}
				break;
			default:
				break;
		}

		if (o instanceof Yukkuri) {
			Yukkuri bodyTarget = (Yukkuri) o;
			AgeState ageState = bodyTarget.getAgeState();
			boolean likesWater = bodyTarget.isLikeWater();
			int depthLimit = -2;

			switch (ageState) {
				case BABY:
					depthLimit = 1;
					break;
				case CHILD:
					depthLimit = 2;
					break;
				case ADULT:
					depthLimit = 3;
					break;
				default:
					break;
			}

			switch (depth) {
				case SHALLOW:
					isInWater = true;
					if (GameRandom.nextInt(70) == 0 || !bodyTarget.isWet()) {
						bodyTarget.inWater(depth);
					}
					break;
				case DEEP:
					isInWater = true;
					if (GameRandom.nextInt(40) == 0 || !bodyTarget.isWet()) {
						bodyTarget.inWater(depth);
					}
					break;
				default:
					break;
			}

			if (isInWater) {
				int tz = Translate.translateZ(zcord - 1);
				int objectHeight = o.getH();

				if (!likesWater) {
					// ある程度沈むと大ダメージ
					if (tz < -objectHeight / 3 && GameRandom.nextInt(10 + depthLimit * 5) == 0) {
						bodyTarget.addDamage(bodyTarget.getDamageLimit() / 4);
					}

					// 水深が深いと動けなくなる
					if (zcord < -depthLimit) {
						bodyTarget.setLockmove(true);
					}

					int deepWaterChance = 50;
					// 溶けている場合、沈む確率UP
					if (bodyTarget.isMelt()) {
						deepWaterChance = deepWaterChance / 2;
					}

					// 死んでいる場合、沈む確率UP
					if (bodyTarget.isDead()) {
						deepWaterChance = deepWaterChance / 2;
					}

					if (GameRandom.nextInt(deepWaterChance) == 0) {
						bodyTarget.setFallingUnderGround(true);
						o.setMostDepth(zcord - 1);
						o.setCalcZ(zcord - 1);
					}
				}

				// 溶けて消える
				if (tz < -objectHeight) {
					o.remove();
				}
			}
		} else {
			// 溶ける
			if (Translate.translateZ(zcord) < -10) {
				o.remove();
			}
		}

		return 0;
	}

	/**
	 * ある点の池の深さを取得
	 * 
	 * @param x ある点のX座標
	 * @param y ある点のY座標
	 * @return ある点の池の深さ
	 */
	public Depth checkArea(int x, int y) {
		Depth depthW = Depth.NONE;
		int edgeWidth = 10;
		if (mapEx - mapSx < edgeWidth) {
			edgeWidth = 0;
		}

		int edgeHeight = 5;
		if (mapEy - mapSy < edgeHeight) {
			edgeHeight = 0;
		}

		// --------------------------------------
		// 左右判定
		if (x < mapSx || mapEx < x) {
			depthW = Depth.NONE;
		} else if ((mapSx <= x && x < mapSx + edgeWidth) || (mapEx - edgeWidth < x && x <= mapEx)) {
			depthW = Depth.EDGE;
		} else if ((mapSx + edgeWidth <= x && x < mapSx + edgeWidth * 2)
				|| (mapEx - edgeWidth * 2 < x && x <= mapEx - edgeWidth)) {
			depthW = Depth.SHALLOW;
		} else if (mapSx + edgeWidth * 2 <= x && x < mapEx - edgeWidth * 2) {
			depthW = Depth.DEEP;
		}
		// --------------------------------------
		// 上下判定
		Depth depthH = Depth.NONE;
		if (y < mapSy || mapEy < y) {
			depthH = Depth.NONE;
		} else if ((mapSy <= y && y < mapSy + edgeHeight) || (mapEy - edgeHeight < y && y <= mapEy)) {
			depthH = Depth.EDGE;
		} else if ((mapSy + edgeHeight <= y && y < mapSy + edgeHeight * 2)
				|| (mapEy - edgeHeight * 2 < y && y <= mapEy - edgeHeight)) {
			depthH = Depth.SHALLOW;
		} else if (mapSy + edgeHeight * 2 <= y && y < mapEy - edgeHeight * 2) {
			depthH = Depth.DEEP;
		}

		// 小さい方(浅い方)にあわせる
		Depth depthResult;
		if (depthW == depthH || depthW.ordinal() < depthH.ordinal()) {
			depthResult = depthW;
		} else {
			depthResult = depthH;
		}

		return depthResult;
	}

	/** 水面領域の X 頂点座標配列を返す。 */
	public int[] getWaterPolygonX() {
		return waterPolygonX;
	}

	/** 水面領域の X 頂点座標配列をセットする。 */
	public void setWaterPolygonX(int[] waterPolygonX) {
		this.waterPolygonX = waterPolygonX;
	}

	/** 水面領域の Y 頂点座標配列を返す。 */
	public int[] getWaterPolygonY() {
		return waterPolygonY;
	}

	/** 水面領域の Y 頂点座標配列をセットする。 */
	public void setWaterPolygonY(int[] waterPolygonY) {
		this.waterPolygonY = waterPolygonY;
	}

	/** プールにバインドされているエンティティリストを返す。 */
	public List<Entity> getBoundObjects() {
		return bindObjList;
	}

	/** プールにバインドされているエンティティリストをセットする。 */
	public void setBoundObjects(List<Entity> boundObjects) {
		this.bindObjList = boundObjects;
	}

}
