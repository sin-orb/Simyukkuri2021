package src.item;

import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.command.GadgetAction;
import src.draw.ModLoader;
import src.draw.Point4y;
import src.draw.Translate;
import src.enums.BaryInUGState;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.system.FieldShapeBase;
import src.system.ItemMenu.ShapeMenu;
import src.system.ItemMenu.ShapeMenuTarget;
import src.system.MapPlaceData;

/***************************************************
 * 畑
 * <br>
 * これはほかのアイテムと違い、ObjEXを継承していないので注意。
 */
public class Farm extends FieldShapeBase implements Serializable {

	private static final long serialVersionUID = 2194998702502315898L;

	private static final int MIN_SIZE = 8;

	private static BufferedImage images;
	private static TexturePaint texture;
	private int amount = 1000;
	private int[] anPointX = new int[4];
	private int[] anPointY = new int[4];

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

		List<Farm> list = SimYukkuri.world.getCurrentMap().farm;
		int pos;

		switch (menu) {
			case SETUP:
				break;
			case HERVEST:
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
		int[] anPointX = new int[4];
		int[] anPointY = new int[4];
		Translate.getPolygonPoint(sx, sy, ex, ey, anPointX, anPointY);

		g2.drawPolygon(anPointX, anPointY, 4);
	}

	@Override
	public void drawShape(Graphics2D g2) {
		Translate.getPolygonPoint(fieldSX, fieldSY, fieldEX, fieldEY, anPointX, anPointY);
		g2.setPaint(texture);
		g2.fillPolygon(anPointX, anPointY, 4);
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
		Point4y pS = Translate.getFieldLimitForMap(fsx, fsy);
		Point4y pE = Translate.getFieldLimitForMap(fex, fey);
		fieldSX = pS.getX();
		fieldSY = pS.getY();
		fieldEX = pE.getX();
		fieldEY = pE.getY();

		int[] anPointBaseX = new int[2];
		int[] anPointBaseY = new int[2];
		Translate.getMovedPoint(fieldSX, fieldSY, fieldEX, fieldEY, 0, 0, 0, 0, anPointBaseX, anPointBaseY);

		// フィールド座標が渡ってくるのでマップ座標も計算しておく
		Point4y pos = Translate.invertLimit(anPointBaseX[0], anPointBaseY[0]);
		mapSX = Math.max(0, Math.min(pos.getX(), Translate.mapW));
		mapSY = Math.max(0, Math.min(pos.getY(), Translate.mapH));

		pos = Translate.invertLimit(anPointBaseX[1], anPointBaseY[1]);
		mapEX = Math.max(0, Math.min(pos.getX(), Translate.mapW));
		mapEY = Math.max(0, Math.min(pos.getY(), Translate.mapH));

		// 規定サイズと位置へ合わせる
		if ((mapEX - mapSX) < MIN_SIZE)
			mapEX = mapSX + MIN_SIZE;
		if ((mapEY - mapSY) < MIN_SIZE)
			mapEY = mapSY + MIN_SIZE;
		if (mapEX > Translate.mapW) {
			mapSX -= (mapEX - Translate.mapW);
			mapEX -= (mapEX - Translate.mapW);
		}
		if (mapEY > Translate.mapH) {
			mapSY -= (mapEY - Translate.mapH);
			mapEY -= (mapEY - Translate.mapH);
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

		SimYukkuri.world.getCurrentMap().farm.add(this);
		MapPlaceData.setFiledFlag(SimYukkuri.world.getCurrentMap().fieldMap, mapSX, mapSY, mapW, mapH, true,
				FIELD_FARM);
	}

	public Farm() {

	}

	/** フィールド座標にあるシェイプ取得 */
	public static Farm getFarm(int fx, int fy) {

		for (Farm bc : SimYukkuri.world.getCurrentMap().farm) {
			if (bc.fieldSX <= fx && fx <= bc.fieldEX
					&& bc.fieldSY <= fy && fy <= bc.fieldEY) {
				return bc;
			}
		}
		return null;
	}

	/** 削除 */
	public static void deleteFarm(Farm b) {
		MapPlaceData.setFiledFlag(SimYukkuri.world.getCurrentMap().fieldMap, b.mapSX, b.mapSY, b.mapW, b.mapH, false,
				FIELD_FARM);
		SimYukkuri.world.getCurrentMap().farm.remove(b);
		// 重なってた部分の復元
		for (Farm bc : SimYukkuri.world.getCurrentMap().farm) {
			MapPlaceData.setFiledFlag(SimYukkuri.world.getCurrentMap().fieldMap, bc.mapSX, bc.mapSY, bc.mapW, bc.mapH,
					true,
					FIELD_FARM);
		}
	}

	/**
	 * ある点が畑の範囲内かどうか
	 * 
	 * @param inX      ある点のX座標
	 * @param inY      ある点Y座標
	 * @param bIsField 渡された座標がフィールド座標かどうか
	 */
	public boolean checkContain(int inX, int inY, boolean bIsField) {
		int nX = inX;
		int nY = inY;
		if (bIsField) {
			Point4y pos = Translate.invertLimit(inX, inY);
			nX = pos.getX();
			nY = pos.getY();
		}

		Point4y posFirst = Translate.invertLimit(anPointX[0], anPointY[0]);
		Point4y posSecond = Translate.invertLimit(anPointX[2], anPointY[2]);
		if (posFirst != null && posSecond != null) {
			if (posFirst.getX() <= nX && nX <= posSecond.getX() && posFirst.getY() <= nY && nY <= posSecond.getY()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 渡されたオブジェクトが畑の中にあるかを判定
	 * <br>
	 * 動作はobjHitProcess( Obj o )で
	 */
	public boolean checkHitObj(Obj o) {
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
	public int objHitProcess(Obj o) {
		if (o == null) {
			return 0;
		}
		// 空中は無視
		int nZ = o.getZ();
		if (o instanceof Body) {
			Body b = (Body) o;
			// int nH = b.getCollisionY();
			if (0 < nZ) {
				if (b.getBaryState() != BaryInUGState.NONE) {
					o.setMostDepth(0);
					b.setLockmove(false);
					b.setBaryState(BaryInUGState.NONE);
					return 1;
				}
			}
		} else {
			if (0 < nZ) {
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

		if (SimYukkuri.RND.nextInt(20) != 0) {
			return 1;
		}
		// 肥料取得
		getAmount(o);

		// 肥料を与える
		giveAmount(o);

		return 1;
	}

	/** あるオブジェクトを肥料に変換する際に、そのオブジェクトの目方の減りを計算＆実行 */
	public void getAmount(Obj o) {
		if (o == null) {
			return;
		}

		if (0 < o.getZ()) {
			return;
		}

		int nTempAmount = 100;
		if (o instanceof Shit) {
			Shit s = (Shit) o;
			amount += nTempAmount;
			s.eatShit(nTempAmount);
		}

		if (o instanceof Vomit) {
			Vomit v = (Vomit) o;
			amount += nTempAmount;
			v.eatVomit(nTempAmount);
		}

		if (o instanceof Body) {
			Body b = (Body) o;
			if (b.isDead()) {
				amount += nTempAmount;
				b.eatBody(nTempAmount);
				// 潰れてたり溶けてたらもう1回
				if (b.isCrushed() || b.isMelt()) {
					amount += nTempAmount;
					b.eatBody(nTempAmount);
				}
			}

			int nShit = b.getShit();
			// 体内のうんうんも吸う
			if (o.getZ() < 0 && nTempAmount < nShit) {
				b.setShit(nShit - nTempAmount, false);
			}
		}
	}

	/** 畑の中のゆっくりに対し肥料を与え、茎を生やす */
	public void giveAmount(Obj o) {
		if (o == null) {
			return;
		}
		int nTempAmount = 100;
		if (o instanceof Body) {
			Body b = (Body) o;
			if (b.isDead() || b.isRemoved()) {
				return;
			}

			// 土にかなり埋まってたら茎がはえる
			if (b.getBaryState() == BaryInUGState.NEARLY_ALL ||
					b.getBaryState() == BaryInUGState.ALL) {
				// 茎が生えていたら救済モード(10%回復)
				if (b.isHasStalk() && nTempAmount <= amount) {
					if (b.isSoHungry()) {
						amount -= nTempAmount;
						b.addHungry(b.getHungryLimit() / 10);
					}

					if (b.isDamaged()) {
						amount -= nTempAmount;
						b.addDamage(-b.getDamageLimit() / 10);
					}
				}

				if (!b.isHasStalk() && 1000 < amount) {
					Stalk s = (Stalk) GadgetAction.putObjEX(Stalk.class, b.getX(), b.getY(),
							b.getDirection().ordinal());
					SimYukkuri.world.getCurrentMap().stalk.put(s.objId, s);
					if (b.getStalks() != null) {
						b.getStalks().add(s);
						s.setPlantYukkuri(b);
						b.setHasStalk(true);
						amount -= 200;
					}
				} else {
					// 余裕がありそうならランダムで茎を生やす
					if (3000 < amount && !b.isDamaged()) {
						if (SimYukkuri.RND.nextInt(100) == 0) {
							Stalk s = (Stalk) GadgetAction.putObjEX(Stalk.class, b.getX(), b.getY(),
									b.getDirection().ordinal());
							SimYukkuri.world.getCurrentMap().stalk.put(s.objId, s);
							if (b.getStalks() != null) {
								b.getStalks().add(s);
								s.setPlantYukkuri(b);
								b.setHasStalk(true);
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

	public int[] getAnPointX() {
		return anPointX;
	}

	public void setAnPointX(int[] anPointX) {
		this.anPointX = anPointX;
	}

	public int[] getAnPointY() {
		return anPointY;
	}

	public void setAnPointY(int[] anPointY) {
		this.anPointY = anPointY;
	}

}
