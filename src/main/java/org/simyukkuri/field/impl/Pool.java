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

import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.draw.Translate;
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

/***************************************************
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
	public enum DEPTH {
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

	@Override
	public ShapeMenuTarget hasShapePopup() {
		return ShapeMenuTarget.POOL;
	}

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

	@Override
	@Transient
	public int getAttribute() {
		return FIELD_POOL;
	}

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

	@Override
	public void drawShape(Graphics2D g2) {
		int[] polygonX = new int[4];
		int[] polygonY = new int[4];
		Translate.getPolygonPoint(fieldSX, fieldSY, fieldEX, fieldEY, polygonX, polygonY);

		g2.setPaint(ROCK_COLOR);
		g2.fillPolygon(polygonX, polygonY, 4);

		Translate.getPolygonPoint(fieldSX + 8, fieldSY + 8, fieldEX - 8, fieldEY - 8, waterPolygonX, waterPolygonY);
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
		fieldSX = start.getX();
		fieldSY = start.getY();
		fieldEX = end.getX();
		fieldEY = end.getY();

		int[] basePolygonX = new int[2];
		int[] basePolygonY = new int[2];
		Translate.getMovedPoint(fieldSX, fieldSY, fieldEX, fieldEY, 0, 0, 0, 0, basePolygonX, basePolygonY);

		// フィールド座標が渡ってくるのでマップ座標も計算しておく
		Point4y pos = Translate.invertLimit(basePolygonX[0], basePolygonY[0]);
		mapSX = Math.max(0, Math.min(pos.getX(), Translate.getWorldWidth()));
		mapSY = Math.max(0, Math.min(pos.getY(), Translate.getWorldHeight()));

		pos = Translate.invertLimit(basePolygonX[1], basePolygonY[1]);
		mapEX = Math.max(0, Math.min(pos.getX(), Translate.getWorldWidth()));
		mapEY = Math.max(0, Math.min(pos.getY(), Translate.getWorldHeight()));

		// 規定サイズと位置へ合わせる
		if ((mapEX - mapSX) < MIN_SIZE)
			mapEX = mapSX + MIN_SIZE;
		if ((mapEY - mapSY) < MIN_SIZE)
			mapEY = mapSY + MIN_SIZE;
		if (mapEX > Translate.getWorldWidth()) {
			mapSX -= (mapEX - Translate.getWorldWidth());
			mapEX -= (mapEX - Translate.getWorldWidth());
		}
		if (mapEY > Translate.getWorldHeight()) {
			mapSY -= (mapEY - Translate.getWorldHeight());
			mapEY -= (mapEY - Translate.getWorldHeight());
		}

		Point4y f = new Point4y();
		Translate.translate(mapSX, mapSY, f);
		fieldSX = f.getX();
		fieldSY = f.getY();
		Translate.translate(mapEX, mapEY, f);
		fieldEX = f.getX();
		fieldEY = f.getY();

		fieldW = fieldEX - fieldSX + 1;
		fieldH = fieldEY - fieldSY + 1;
		mapW = mapEX - mapSX + 1;
		mapH = mapEY - mapSY + 1;

		GameWorld.get().getCurrentWorldState().getPools().add(this);
		WorldState.setFieldFlag(GameWorld.get().getCurrentWorldState().getFieldGrid(), mapSX, mapSY, mapW, mapH, true,
				FIELD_POOL);
	}

	public Pool() {

	}

	/** フィールド座標にあるシェイプ取得 */
	public static Pool getPool(int fx, int fy) {

		for (Pool bc : GameWorld.get().getCurrentWorldState().getPools()) {
			if (bc.fieldSX <= fx && fx <= bc.fieldEX
					&& bc.fieldSY <= fy && fy <= bc.fieldEY) {
				return bc;
			}
		}
		return null;
	}

	/** 削除 */
	public static void deletePool(Pool b) {
		WorldState.setFieldFlag(GameWorld.get().getCurrentWorldState().getFieldGrid(), b.mapSX, b.mapSY, b.mapW, b.mapH,
				false, FIELD_POOL);
		GameWorld.get().getCurrentWorldState().getPools().remove(b);
		// 重なってた部分の復元
		for (Pool bc : GameWorld.get().getCurrentWorldState().getPools()) {
			WorldState.setFieldFlag(GameWorld.get().getCurrentWorldState().getFieldGrid(), bc.mapSX, bc.mapSY, bc.mapW,
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
		int xCoord = inX;
		int yCoord = inY;
		if (isField) {
			Point4y pos = Translate.invertLimit(inX, inY);
			xCoord = pos.getX();
			yCoord = pos.getY();
		}

		Point4y posFirst = Translate.invertLimit(waterPolygonX[0], waterPolygonY[0]);
		Point4y posSecond = Translate.invertLimit(waterPolygonX[2], waterPolygonY[2]);
		if (posFirst != null && posSecond != null) {
			if (posFirst.getX() <= xCoord && xCoord <= posSecond.getX() && posFirst.getY() <= yCoord
					&& yCoord <= posSecond.getY()) {
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
		int zCoord = o.getZ();
		if (0 < zCoord) {
			return 0;
		}

		boolean isInWater = false;
		o.setInPool(true);
		DEPTH depth = checkArea(o.getX(), o.getY());
		switch (depth) {
			case EDGE:
				o.setFallingUnderGround(false);
				o.setMostDepth(0);
				if (zCoord < 0) {
					o.setCalcZ(0);
				}
				break;
			case SHALLOW:
				isInWater = true;
				// すこし沈む
				if (!o.isFallingUnderGround()) {
					o.setMostDepth(-1);
				}

				if (zCoord == 0) {
					o.setCalcZ(-1);
				}
				break;
			case DEEP:
				isInWater = true;
				// もうすこし沈む
				if (!o.isFallingUnderGround()) {
					o.setMostDepth(-2);
				}
				if (zCoord == 0 || zCoord == -1) {
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
				int tz = Translate.translateZ(zCoord - 1);
				int objectHeight = o.getH();

				if (!likesWater) {
					// ある程度沈むと大ダメージ
					if (tz < -objectHeight / 3 && GameRandom.nextInt(10 + depthLimit * 5) == 0) {
						bodyTarget.addDamage(bodyTarget.getDamageLimit() / 4);
					}

					// 水深が深いと動けなくなる
					if (zCoord < -depthLimit) {
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
						o.setMostDepth(zCoord - 1);
						o.setCalcZ(zCoord - 1);
					}
				}

				// 溶けて消える
				if (tz < -objectHeight) {
					o.remove();
				}
			}
		} else {
			// 溶ける
			if (Translate.translateZ(zCoord) < -10) {
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
	public DEPTH checkArea(int x, int y) {
		DEPTH depthW = DEPTH.NONE;
		DEPTH depthH = DEPTH.NONE;
		DEPTH depthResult = DEPTH.NONE;
		int edgeWidth = 10;
		if (mapEX - mapSX < edgeWidth) {
			edgeWidth = 0;
		}

		int edgeHeight = 5;
		if (mapEY - mapSY < edgeHeight) {
			edgeHeight = 0;
		}

		// --------------------------------------
		// 左右判定
		if (x < mapSX || mapEX < x) {
			depthW = DEPTH.NONE;
		} else if ((mapSX <= x && x < mapSX + edgeWidth) || (mapEX - edgeWidth < x && x <= mapEX)) {
			depthW = DEPTH.EDGE;
		} else if ((mapSX + edgeWidth <= x && x < mapSX + edgeWidth * 2)
				|| (mapEX - edgeWidth * 2 < x && x <= mapEX - edgeWidth)) {
			depthW = DEPTH.SHALLOW;
		} else if (mapSX + edgeWidth * 2 <= x && x < mapEX - edgeWidth * 2) {
			depthW = DEPTH.DEEP;
		}
		// --------------------------------------
		// 上下判定
		if (y < mapSY || mapEY < y) {
			depthH = DEPTH.NONE;
		} else if ((mapSY <= y && y < mapSY + edgeHeight) || (mapEY - edgeHeight < y && y <= mapEY)) {
			depthH = DEPTH.EDGE;
		} else if ((mapSY + edgeHeight <= y && y < mapSY + edgeHeight * 2)
				|| (mapEY - edgeHeight * 2 < y && y <= mapEY - edgeHeight)) {
			depthH = DEPTH.SHALLOW;
		} else if (mapSY + edgeHeight * 2 <= y && y < mapEY - edgeHeight * 2) {
			depthH = DEPTH.DEEP;
		}

		// 小さい方(浅い方)にあわせる
		if (depthW == depthH || depthW.ordinal() < depthH.ordinal()) {
			depthResult = depthW;
		} else {
			depthResult = depthH;
		}

		return depthResult;
	}

	public int[] getWaterPolygonX() {
		return waterPolygonX;
	}

	public void setWaterPolygonX(int[] waterPolygonX) {
		this.waterPolygonX = waterPolygonX;
	}

	public int[] getWaterPolygonY() {
		return waterPolygonY;
	}

	public void setWaterPolygonY(int[] waterPolygonY) {
		this.waterPolygonY = waterPolygonY;
	}

	public List<Entity> getBoundObjects() {
		return bindObjList;
	}

	public void setBoundObjects(List<Entity> boundObjects) {
		this.bindObjList = boundObjects;
	}

}
