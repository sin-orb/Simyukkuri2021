package org.simyukkuri.field.impl;

import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.simyukkuri.command.GadgetAction;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.field.FieldShape;
import org.simyukkuri.system.ItemMenu.ShapeMenu;
import org.simyukkuri.system.ItemMenu.ShapeMenuTarget;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;

/**
 * 畑
 * <br>
 * これはほかのアイテムと違い、ObjEXを継承していないので注意。
 */
public class Farm extends FieldShape {

	private static final long serialVersionUID = 2194998702502315898L;

	private static final int MIN_SIZE = 8;

	private static BufferedImage images;
	private static TexturePaint texture;
	private int amount = 1000;
	private int[] polygonX = new int[4];
	private int[] polygonY = new int[4];

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		images = ModLoader.loadItemImage(loader, "farm" + File.separator + "farm.png");
		texture = new TexturePaint(images, new Rectangle2D.Float(0, 0, images.getWidth(), images.getHeight()));
	}

	/** シェイプポップアップを持つかを返す（常に FARM）。 */
	@Override
	public ShapeMenuTarget hasShapePopup() {
		return ShapeMenuTarget.FARM;
	}

	/** シェイプポップアップのメニューアクションを実行する。 */
	@Override
	public void executeShapePopup(ShapeMenu menu) {

		List<Farm> farmList = GameWorld.get().getCurrentWorldState().getFarms();
		int currentIndex;

		switch (menu) {
			case SETUP:
				break;
			case HARVEST:
				break;
			case TOP:
				farmList.remove(this);
				farmList.add(0, this);
				break;
			case UP:
				currentIndex = farmList.indexOf(this);
				if (currentIndex > 0) {
					farmList.remove(this);
					farmList.add(currentIndex - 1, this);
				}
				break;
			case DOWN:
				currentIndex = farmList.indexOf(this);
				if (currentIndex < (farmList.size() - 1)) {
					farmList.remove(this);
					farmList.add(currentIndex + 1, this);
				}
				break;
			case BOTTOM:
				farmList.remove(this);
				farmList.add(this);
				break;
			default:
				break;
		}
	}

	/** フィールドの属性値を返す。 */
	@Override
	@Transient
	public int getAttribute() {
		return FIELD_FARM;
	}

	/** バリアの最小サイズを返す。 */
	@Override
	@Transient
	public int getMinimumSize() {
		return MIN_SIZE;
	}

	/** プレビューラインの描画 */
	public static void drawPreview(Graphics2D g2, int sx, int sy, int ex, int ey) {
		int[] previewPolygonX = new int[4];
		int[] previewPolygonY = new int[4];
		Translate.getPolygonPoint(sx, sy, ex, ey, previewPolygonX, previewPolygonY);

		g2.drawPolygon(previewPolygonX, previewPolygonY, 4);
	}

	/** シェイプの外形を描画する。 */
	@Override
	public void drawShape(Graphics2D g2) {
		Translate.getPolygonPoint(fieldSx, fieldSy, fieldEx, fieldEy, polygonX, polygonY);
		g2.setPaint(texture);
		g2.fillPolygon(polygonX, polygonY, 4);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param fsx 設置起点のX座標
	 * @param fsy 設置起点のY座標
	 * @param fex 設置終点のX座標
	 * @param fey 設置終点のY座標
	 */
	public Farm(int fsx, int fsy, int fex, int fey) {
		Point4y startPoint = Translate.getFieldLimitForWorld(fsx, fsy);
		Point4y endPoint = Translate.getFieldLimitForWorld(fex, fey);
		fieldSx = startPoint.getX();
		fieldSy = startPoint.getY();
		fieldEx = endPoint.getX();
		fieldEy = endPoint.getY();

		int[] basePolygonX = new int[2];
		int[] basePolygonY = new int[2];
		Translate.getMovedPoint(fieldSx, fieldSy, fieldEx, fieldEy, 0, 0, 0, 0, basePolygonX, basePolygonY);

		// フィールド座標が渡ってくるのでマップ座標も計算しておく
		Point4y mapStart = Translate.invertLimit(basePolygonX[0], basePolygonY[0]);
		mapSx = Math.max(0, Math.min(mapStart.getX(), Translate.getWorldWidth()));
		mapSy = Math.max(0, Math.min(mapStart.getY(), Translate.getWorldHeight()));

		Point4y mapEnd = Translate.invertLimit(basePolygonX[1], basePolygonY[1]);
		mapEx = Math.max(0, Math.min(mapEnd.getX(), Translate.getWorldWidth()));
		mapEy = Math.max(0, Math.min(mapEnd.getY(), Translate.getWorldHeight()));

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

		GameWorld.get().getCurrentWorldState().getFarms().add(this);
		WorldState.setFieldFlag(GameWorld.get().getCurrentWorldState().getFieldGrid(), mapSx, mapSy, mapW, mapH, true,
				FIELD_FARM);
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public Farm() {

	}

	/** フィールド座標にあるシェイプ取得 */
	public static Farm getFarm(int fx, int fy) {

		for (Farm targetFarm : GameWorld.get().getCurrentWorldState().getFarms()) {
			if (targetFarm.fieldSx <= fx && fx <= targetFarm.fieldEx
					&& targetFarm.fieldSy <= fy && fy <= targetFarm.fieldEy) {
				return targetFarm;
			}
		}
		return null;
	}

	/** 削除 */
	public static void deleteFarm(Farm farm) {
		WorldState.setFieldFlag(GameWorld.get().getCurrentWorldState().getFieldGrid(), farm.mapSx, farm.mapSy, farm.mapW,
				farm.mapH,
				false,
				FIELD_FARM);
		GameWorld.get().getCurrentWorldState().getFarms().remove(farm);
		// 重なってた部分の復元
		for (Farm targetFarm : GameWorld.get().getCurrentWorldState().getFarms()) {
			WorldState.setFieldFlag(GameWorld.get().getCurrentWorldState().getFieldGrid(), targetFarm.mapSx, targetFarm.mapSy,
					targetFarm.mapW,
					targetFarm.mapH,
					true,
					FIELD_FARM);
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

		Point4y posFirst = Translate.invertLimit(polygonX[0], polygonY[0]);
		Point4y posSecond = Translate.invertLimit(polygonX[2], polygonY[2]);
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
		// エリア内
		return true;
	}

	/** 当たり判定されたオブジェクトへの処理 */
	public int objHitProcess(Entity o) {
		if (o == null) {
			return 0;
		}
		// 空中は無視
		int zcord = o.getZ();
		if (o instanceof Yukkuri) {
			Yukkuri body = (Yukkuri) o;
			if (0 < zcord) {
				if (body.getBurialState() != BurialState.NONE) {
					o.setMostDepth(0);
					body.setLockmove(false);
					body.setBurialState(BurialState.NONE);
					return 1;
				}
			}
		} else {
			if (0 < zcord) {
				if (o.getMostDepth() != 0) {
					o.setMostDepth(0);
				}
				return 1;
			}
		}

		if (o instanceof Stalk) {
			o.setMostDepth(0);
			o.setCalcZ(0);
			return 1;
		}

		if (GameRandom.nextInt(20) != 0) {
			return 1;
		}
		// 肥料取得
		getAmount(o);

		// 肥料を与える
		giveAmount(o);

		return 1;
	}

	/** 農場の産出量を返す。 */
	public int getAmount() {
		return amount;
	}

	/** あるオブジェクトを肥料に変換する際に、そのオブジェクトの目方の減りを計算＆実行 */
	public void getAmount(Entity o) {
		if (o == null) {
			return;
		}

		if (0 < o.getZ()) {
			return;
		}

		int fertilizerAmount = 100;
		if (o instanceof Shit) {
			Shit shit = (Shit) o;
			amount += fertilizerAmount;
			shit.eatShit(fertilizerAmount);
		}

		if (o instanceof Vomit) {
			Vomit vomit = (Vomit) o;
			amount += fertilizerAmount;
			vomit.eatVomit(fertilizerAmount);
		}

		if (o instanceof Yukkuri) {
			Yukkuri body = (Yukkuri) o;
			if (body.isDead()) {
				amount += fertilizerAmount;
				body.eatYukkuri(fertilizerAmount);
				// 潰れてたり溶けてたらもう1回
				if (body.isCrushed() || body.isMelt()) {
					amount += fertilizerAmount;
					body.eatYukkuri(fertilizerAmount);
				}
			}

			int bodyShit = body.getShit();
			// 体内のうんうんも吸う
			if (o.getZ() < 0 && fertilizerAmount < bodyShit) {
				body.setShit(bodyShit - fertilizerAmount, false);
			}
		}
	}

	/** 畑の中のゆっくりに対し肥料を与え、茎を生やす */
	public void giveAmount(Entity o) {
		if (o == null) {
			return;
		}
		int fertilizerAmount = 100;
		if (o instanceof Yukkuri) {
			Yukkuri body = (Yukkuri) o;
			if (body.isDead() || body.isRemoved()) {
				return;
			}

			// 土にかなり埋まってたら茎がはえる
			if (body.getBurialState() == BurialState.NEARLY_ALL
					|| body.getBurialState() == BurialState.ALL) {
				// 茎が生えていたら救済モード(10%回復)
				if (body.isHasStalk() && fertilizerAmount <= amount) {
					if (body.isSoHungry()) {
						amount -= fertilizerAmount;
						body.addHungry(body.getHungryLimit() / 10);
					}

					if (body.isDamaged()) {
						amount -= fertilizerAmount;
						body.addDamage(-body.getDamageLimit() / 10);
					}
				}

				if (!body.isHasStalk() && 1000 < amount) {
					Stalk stalk = (Stalk) GadgetAction.putObjEx(Stalk.class, body.getX(), body.getY(),
							body.getDirection().ordinal());
					GameWorld.get().getCurrentWorldState().getStalks().put(stalk.objId, stalk);
					if (body.getStalks() != null) {
						body.getStalks().add(stalk);
						stalk.setPlantYukkuri(body);
						body.setHasStalk(true);
						amount -= 200;
					}
				} else {
					// 余裕がありそうならランダムで茎を生やす
					if (3000 < amount && !body.isDamaged()) {
						if (GameRandom.nextInt(100) == 0) {
							Stalk stalk = (Stalk) GadgetAction.putObjEx(Stalk.class, body.getX(), body.getY(),
									body.getDirection().ordinal());
							GameWorld.get().getCurrentWorldState().getStalks().put(stalk.objId, stalk);
							if (body.getStalks() != null) {
								body.getStalks().add(stalk);
								stalk.setPlantYukkuri(body);
								body.setHasStalk(true);
							}
							amount -= 200;
						}
					}
				}
			}
		}
	}

	/** 農場の産出量をセットする。 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/** フィールド形状の X 頂点座標配列を返す。 */
	public int[] getPolygonX() {
		return polygonX;
	}

	/** フィールド形状の X 頂点座標配列をセットする。 */
	public void setPolygonX(int[] polygonX) {
		this.polygonX = polygonX;
	}

	/** フィールド形状の Y 頂点座標配列を返す。 */
	public int[] getPolygonY() {
		return polygonY;
	}

	/** フィールド形状の Y 頂点座標配列をセットする。 */
	public void setPolygonY(int[] polygonY) {
		this.polygonY = polygonY;
	}

}
