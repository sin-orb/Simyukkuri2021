package src.field.impl;

import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.util.List;

import src.SimYukkuri;
import src.util.GameRandom;
import src.util.GameWorld;
import src.base.Yukkuri;
import src.base.Entity;
import src.command.GadgetAction;
import src.draw.ModLoader;
import src.draw.Point4y;
import src.draw.Translate;
import src.enums.BurialState;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.field.FieldShape;
import src.system.ItemMenu.ShapeMenu;
import src.system.ItemMenu.ShapeMenuTarget;
import src.system.MapPlaceData;

/***************************************************
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

	@Override
	public ShapeMenuTarget hasShapePopup() {
		return ShapeMenuTarget.FARM;
	}

	@Override
	public void executeShapePopup(ShapeMenu menu) {

		List<Farm> farmList = GameWorld.get().getCurrentMap().getFarm();
		int currentIndex;

		switch (menu) {
			case SETUP:
				break;
			case HERVEST:
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
		}
	}

	@Override
	@Transient
	public int getAttribute() {
		return FIELD_FARM;
	}

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

	@Override
	public void drawShape(Graphics2D g2) {
		Translate.getPolygonPoint(fieldSX, fieldSY, fieldEX, fieldEY, polygonX, polygonY);
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
		Point4y startPoint = Translate.getFieldLimitForMap(fsx, fsy);
		Point4y endPoint = Translate.getFieldLimitForMap(fex, fey);
		fieldSX = startPoint.getX();
		fieldSY = startPoint.getY();
		fieldEX = endPoint.getX();
		fieldEY = endPoint.getY();

		int[] basePolygonX = new int[2];
		int[] basePolygonY = new int[2];
		Translate.getMovedPoint(fieldSX, fieldSY, fieldEX, fieldEY, 0, 0, 0, 0, basePolygonX, basePolygonY);

		// フィールド座標が渡ってくるのでマップ座標も計算しておく
		Point4y mapStart = Translate.invertLimit(basePolygonX[0], basePolygonY[0]);
		mapSX = Math.max(0, Math.min(mapStart.getX(), Translate.getMapW()));
		mapSY = Math.max(0, Math.min(mapStart.getY(), Translate.getMapH()));

		Point4y mapEnd = Translate.invertLimit(basePolygonX[1], basePolygonY[1]);
		mapEX = Math.max(0, Math.min(mapEnd.getX(), Translate.getMapW()));
		mapEY = Math.max(0, Math.min(mapEnd.getY(), Translate.getMapH()));

		// 規定サイズと位置へ合わせる
		if ((mapEX - mapSX) < MIN_SIZE)
			mapEX = mapSX + MIN_SIZE;
		if ((mapEY - mapSY) < MIN_SIZE)
			mapEY = mapSY + MIN_SIZE;
		if (mapEX > Translate.getMapW()) {
			mapSX -= (mapEX - Translate.getMapW());
			mapEX -= (mapEX - Translate.getMapW());
		}
		if (mapEY > Translate.getMapH()) {
			mapSY -= (mapEY - Translate.getMapH());
			mapEY -= (mapEY - Translate.getMapH());
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

		GameWorld.get().getCurrentMap().getFarm().add(this);
		MapPlaceData.setFiledFlag(GameWorld.get().getCurrentMap().getFieldMap(), mapSX, mapSY, mapW, mapH, true,
				FIELD_FARM);
	}

	public Farm() {

	}

	/** フィールド座標にあるシェイプ取得 */
	public static Farm getFarm(int fx, int fy) {

		for (Farm targetFarm : GameWorld.get().getCurrentMap().getFarm()) {
			if (targetFarm.fieldSX <= fx && fx <= targetFarm.fieldEX
					&& targetFarm.fieldSY <= fy && fy <= targetFarm.fieldEY) {
				return targetFarm;
			}
		}
		return null;
	}

	/** 削除 */
	public static void deleteFarm(Farm farm) {
		MapPlaceData.setFiledFlag(GameWorld.get().getCurrentMap().getFieldMap(), farm.mapSX, farm.mapSY, farm.mapW, farm.mapH,
				false,
				FIELD_FARM);
		GameWorld.get().getCurrentMap().getFarm().remove(farm);
		// 重なってた部分の復元
		for (Farm targetFarm : GameWorld.get().getCurrentMap().getFarm()) {
			MapPlaceData.setFiledFlag(GameWorld.get().getCurrentMap().getFieldMap(), targetFarm.mapSX, targetFarm.mapSY, targetFarm.mapW,
					targetFarm.mapH,
					true,
					FIELD_FARM);
		}
	}

	/**
	 * ある点が畑の範囲内かどうか
	 * 
	 * @param inX      ある点のX座標
	 * @param inY      ある点Y座標
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

		Point4y posFirst = Translate.invertLimit(polygonX[0], polygonY[0]);
		Point4y posSecond = Translate.invertLimit(polygonX[2], polygonY[2]);
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
		// エリア内
		return true;
	}

	/** 当たり判定されたオブジェクトへの処理 */
	public int objHitProcess(Entity o) {
		if (o == null) {
			return 0;
		}
		// 空中は無視
		int zCoord = o.getZ();
		if (o instanceof Yukkuri) {
			Yukkuri body = (Yukkuri) o;
			if (0 < zCoord) {
				if (body.getBurialState() != BurialState.NONE) {
					o.setMostDepth(0);
					body.setLockmove(false);
					body.setBurialState(BurialState.NONE);
					return 1;
				}
			}
		} else {
			if (0 < zCoord) {
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
				body.eatBody(fertilizerAmount);
				// 潰れてたり溶けてたらもう1回
				if (body.isCrushed() || body.isMelt()) {
					amount += fertilizerAmount;
					body.eatBody(fertilizerAmount);
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
			if (body.getBurialState() == BurialState.NEARLY_ALL ||
					body.getBurialState() == BurialState.ALL) {
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
						Stalk stalk = (Stalk) GadgetAction.putObjEX(Stalk.class, body.getX(), body.getY(),
								body.getDirection().ordinal());
						GameWorld.get().getCurrentMap().getStalk().put(stalk.objId, stalk);
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
								Stalk stalk = (Stalk) GadgetAction.putObjEX(Stalk.class, body.getX(), body.getY(),
										body.getDirection().ordinal());
								GameWorld.get().getCurrentMap().getStalk().put(stalk.objId, stalk);
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

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int[] getPolygonX() {
		return polygonX;
	}

	public void setPolygonX(int[] polygonX) {
		this.polygonX = polygonX;
	}

	public int[] getPolygonY() {
		return polygonY;
	}

	public void setPolygonY(int[] polygonY) {
		this.polygonY = polygonY;
	}

}
