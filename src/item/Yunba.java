package src.item;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.base.Okazari.OkazariType;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.enums.Attitude;
import src.enums.Event;
import src.enums.Intelligence;
import src.enums.ObjEXType;
import src.enums.Type;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.system.Cash;
import src.system.MapPlaceData;
import src.system.ResourceUtil;

/***************************************************
 * ゆんば
 */
public class Yunba extends ObjEX {

	private static final long serialVersionUID = 3347291782760455512L;

	/** アクションのテーブル */
	public static enum Action {
		CLEAN(ResourceUtil.getInstance().read("command_clean"), ""),
		HEAL(ResourceUtil.getInstance().read("item_yunbarecovery"), ""),
		KABI(ResourceUtil.getInstance().read("item_yunbakillmold"), ""),
		RUDE(ResourceUtil.getInstance().read("item_yunbacorrection"), ""),
		OKAZARI(ResourceUtil.getInstance().read("item_yunbaaccessory"), ""),
		DESTROY(ResourceUtil.getInstance().read("item_yunbaindiscriminate"), ""),
		BODY_REMOVE(ResourceUtil.getInstance().read("item_yunbacleandead"), ""),
		BODY_OKAZARI(ResourceUtil.getInstance().read("item_yunbaconfiscation"), ""),
		SHIT(ResourceUtil.getInstance().read("item_yunbacleanunun"), ""),
		STALK(ResourceUtil.getInstance().read("item_yunbacleanstalk"), ""), // 追加
		WALLTHROUGH(ResourceUtil.getInstance().read("item_yunbathroughwall"), ""),
		NORND(ResourceUtil.getInstance().read("item_yunbasaveenergy"), ""),
		KILL(ResourceUtil.getInstance().read("item_yunbaattackup"), ""),
		MINEUTI(ResourceUtil.getInstance().read("item_yunbaallowance"), ""),
		NODAMAGE_FALL(ResourceUtil.getInstance().read("item_yunbanofalldamage"), ""),
		EMPFOOD(ResourceUtil.getInstance().read("item_yunbadish"), ""),
		ANTIRAPER(ResourceUtil.getInstance().read("item_yunbakillraper"), ""),
		WITHOUT_AND(ResourceUtil.getInstance().read("item_yunbacondition"), ""),
		;

		private String name;

		Action(String nameJ, String nameE) {
			this.name = ResourceUtil.IS_JP ? nameJ : nameE;
		}

		public String toString() {
			return name;
		}
	}

	/** 色のテーブル */
	private static final String[] COL_LIST = {
			ResourceUtil.getInstance().read("yellow"),
			ResourceUtil.getInstance().read("white"),
			ResourceUtil.getInstance().read("red"),
			ResourceUtil.getInstance().read("pink"),
			ResourceUtil.getInstance().read("purple"),
			ResourceUtil.getInstance().read("green"),
			ResourceUtil.getInstance().read("gray"),
			ResourceUtil.getInstance().read("blue"),
			ResourceUtil.getInstance().read("black"),
	};

	public static final int hitCheckObjType = 0;
	private static BufferedImage bodyImages[][] = new BufferedImage[10][2];
	private static BufferedImage images[][] = new BufferedImage[6][2];
	private static Rectangle4y boundary = new Rectangle4y();

	private static JCheckBox[][] checkBox;
	private static JCheckBox[][] checkBox2;
	private static JCheckBox[][] checkBox3;
	private static boolean[][] defaultSetFlags = new boolean[Action.values().length][3];
	private static boolean[][] defaultSetFlags2 = new boolean[1][5];
	private static boolean[][] defaultSetFlags3 = new boolean[1][3];
	@SuppressWarnings("rawtypes")
	private static JComboBox colorBox;
	private static int defaultColor = 0;

	private ItemRank itemRank;

	private int color;
	private int direction;
	private boolean[][] actionFlags;
	private boolean[][] actionFlags2;
	private boolean[][] actionFlags3;
	private boolean bodyCheck;
	private boolean shitCheck;
	private boolean stalkCheck; // 追加
	private boolean norndCheck;
	private boolean killCheck;
	private boolean mineutiCheck;
	private boolean noDamageFallCheck;
	private boolean foodCheck;
	private int[] drawLayer;
	private int layerCount;
	private Action action = null;
	private Obj target = null;

	private int destX;
	private int destY;
	private int speed = 400;

	private int defaultX;
	private int defaultY;

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		bodyImages[0][0] = ModLoader.loadItemImage(loader, "yunba" + File.separator + "body_yellow_left.png");
		bodyImages[1][0] = ModLoader.loadItemImage(loader, "yunba" + File.separator + "body_white_left.png");
		bodyImages[2][0] = ModLoader.loadItemImage(loader, "yunba" + File.separator + "body_red_left.png");
		bodyImages[3][0] = ModLoader.loadItemImage(loader, "yunba" + File.separator + "body_pink_left.png");
		bodyImages[4][0] = ModLoader.loadItemImage(loader, "yunba" + File.separator + "body_purple_left.png");
		bodyImages[5][0] = ModLoader.loadItemImage(loader, "yunba" + File.separator + "body_green_left.png");
		bodyImages[6][0] = ModLoader.loadItemImage(loader, "yunba" + File.separator + "body_gray_left.png");
		bodyImages[7][0] = ModLoader.loadItemImage(loader, "yunba" + File.separator + "body_blue_left.png");
		bodyImages[8][0] = ModLoader.loadItemImage(loader, "yunba" + File.separator + "body_black_left.png");
		bodyImages[9][0] = ModLoader.loadItemImage(loader, "yunba" + File.separator + "body_nora_left.png");
		for (int i = 0; i < bodyImages.length; i++) {
			bodyImages[i][1] = ModLoader.flipImage(bodyImages[i][0]);
		}

		images[0][0] = null;
		images[1][0] = ModLoader.loadItemImage(loader, "yunba" + File.separator + "brush_left.png");
		images[2][0] = ModLoader.loadItemImage(loader, "yunba" + File.separator + "spike_left.png");
		images[3][0] = ModLoader.loadItemImage(loader, "yunba" + File.separator + "shadow.png");
		images[4][0] = ModLoader.loadItemImage(loader, "yunba" + File.separator + "brush_left_nora.png");
		images[5][0] = ModLoader.loadItemImage(loader, "yunba" + File.separator + "spike_left_nora.png");

		images[0][1] = null;
		for (int i = 1; i < 6; i++) {
			images[i][1] = ModLoader.flipImage(images[i][0]);
		}

		boundary.setWidth(bodyImages[0][0].getWidth(io));
		boundary.setHeight(bodyImages[0][0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() - 1);

		defaultSetFlags[Action.SHIT.ordinal()][0] = true;
		defaultSetFlags[Action.EMPFOOD.ordinal()][0] = true;
		defaultColor = 0;
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		for (int i = 0; i < layerCount; i++) {
			if (drawLayer[i] == 0) {
				if (itemRank == ItemRank.HOUSE)
					layer[i] = bodyImages[color][direction];
				else
					layer[i] = bodyImages[9][direction];
			} else {
				if (itemRank == ItemRank.HOUSE)
					layer[i] = images[drawLayer[i]][direction];
				else
					layer[i] = images[drawLayer[i] + 3][direction];
			}
		}
		return layerCount;
	}

	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return images[3][direction];
	}

	@Override
	public boolean hasSetupMenu() {
		return true;
	}

	/** 境界線の取得 */
	public static Rectangle4y getBounding() {
		return boundary;
	}

	@Override
	public void upDate() {
		if (getAge() % 2400 == 0) {
			Cash.addCash(-getCost());
		}
	}

	/**
	 * 値段を取得する.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * コストを取得する.
	 */
	public int getCost() {
		return cost;
	}

	@Override
	public void removeListData() {
		SimYukkuri.world.getCurrentMap().getYunba().remove(objId);
	}

	@Override
	public Event clockTick() {
		setAge(getAge() + TICK);
		if (isRemoved()) {
			action = null;
			target = null;
			removeListData();
			return Event.REMOVED;
		}
		upDate();
		if (grabbed) {
			action = null;
			target = null;
			return Event.DONOTHING;
		}

		if (getZ() > 0) {
			z -= 5;
			if (z < 0)
				z = 0;
			action = null;
			target = null;
			return Event.DONOTHING;
		}

		if (action == null && (getAge() > 10 || norndCheck)) { // 追加
			MapPlaceData curMap = SimYukkuri.world.getCurrentMap();
			setAge(0);

			if (shitCheck) {
				for (Map.Entry<Integer, Shit> entry : curMap.getShit().entrySet()) {
					Shit o = entry.getValue();
					if (SimYukkuri.RND.nextBoolean())
						continue;
					// 追加
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), o.getX(), o.getY(), Barrier.MAP_ITEM)) {
						continue;
					}
					// 追加
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), o.getX(), o.getY(), Barrier.MAP_NOUNUN)) {
						continue;
					}

					boolean bIsShitOnToilet = false;
					// トイレの上のうんうんは無視。空中もチェック
					List<Toilet> toiletList = new LinkedList<>(SimYukkuri.world.getCurrentMap().getToilet().values());
					for (Toilet t : toiletList) {
						// Hitするなら終了
						if (t.checkHitObj(o, true)) {
							bIsShitOnToilet = true;
							break;
						}
					}
					// トイレの上にある
					if (bIsShitOnToilet) {
						continue;
					}

					// 他のゆんばのターゲットならスキップ
					if (!cheackOtherYunbaTarget(o)) {
						continue;
					}

					// うんうん掃除決定
					action = Action.SHIT;
					target = o;
					break;
				}
				for (Map.Entry<Integer, Vomit> entry : curMap.getVomit().entrySet()) {
					Vomit o = entry.getValue();
					if (SimYukkuri.RND.nextBoolean())
						continue;
					// 追加
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), o.getX(), o.getY(), Barrier.MAP_ITEM)) {
						continue;
					}
					// 追加
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), o.getX(), o.getY(), Barrier.MAP_NOUNUN)) {
						continue;
					}

					// 他のゆんばのターゲットならスキップ
					if (!cheackOtherYunbaTarget(o)) {
						continue;
					}

					// うんうん掃除決定
					action = Action.SHIT;
					target = o;
					break;
				}
			}
			if (stalkCheck && action == null) { // 追加
				if (curMap.getStalk() != null) {
					for (Map.Entry<Integer, Stalk> entry : curMap.getStalk().entrySet()) {
						Stalk s = entry.getValue();
						if (norndCheck == false && SimYukkuri.RND.nextBoolean())
							continue;
						int id = s.getPlantYukkuri();
						if (SimYukkuri.world.getCurrentMap().getBody().get(id) != null)
							continue;
						if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
								&& Barrier.acrossBarrier(getX(), getY(), s.getX(), s.getY(), Barrier.MAP_ITEM)) {
							continue;
						}
						if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
								&& Barrier.acrossBarrier(getX(), getY(), s.getX(), s.getY(), Barrier.MAP_NOUNUN)) {
							continue;
						}

						// 他のゆんばのターゲットならスキップ
						if (!cheackOtherYunbaTarget(s)) {
							continue;
						}

						// 茎掃除決定
						action = Action.STALK;
						target = s;
						break;
					}
				}

				if (curMap.getFood() != null) {
					for (Map.Entry<Integer, Food> entry : curMap.getFood().entrySet()) {
						Food f = entry.getValue();
						if (f.getFoodType() != Food.FoodType.STALK)
							continue;
						if (SimYukkuri.RND.nextBoolean())
							continue;
						if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
								&& Barrier.acrossBarrier(getX(), getY(), f.getX(), f.getY(), Barrier.MAP_ITEM)) {
							continue;
						}
						if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
								&& Barrier.acrossBarrier(getX(), getY(), f.getX(), f.getY(), Barrier.MAP_NOUNUN)) {
							continue;
						}

						// 他のゆんばのターゲットならスキップ
						if (!cheackOtherYunbaTarget(f)) {
							continue;
						}
						// 掃除決定
						action = Action.EMPFOOD;
						target = f;
						break;
					}
				}
			}

			if (bodyCheck && action == null) {
				for (Map.Entry<Integer, Body> entry : SimYukkuri.world.getCurrentMap().getBody().entrySet()) {
					Body b = entry.getValue();
					if (norndCheck == false && SimYukkuri.RND.nextBoolean())
						continue;
					// 茎にぶら下がってる固体はスルー
					if (b.isbindStalk() || b.getZ() > 0)
						continue;

					// 追加
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), b.getX(), b.getY(), Barrier.MAP_ITEM)) {
						continue;
					}
					// 追加
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), b.getX(), b.getY(), Barrier.MAP_NOUNUN)) {
						continue;
					}

					// ---------------------------
					// 性格
					// ---------------------------
					boolean bWithoutFlagAtt = false;
					// 超善良
					if ((b.getAttitude() == Attitude.VERY_NICE && actionFlags2[0][0])) {
						bWithoutFlagAtt = true;
					}
					// 善良
					if (b.getAttitude() == Attitude.NICE && actionFlags2[0][1]) {
						bWithoutFlagAtt = true;
					}
					// 普通
					if (b.getAttitude() == Attitude.AVERAGE && actionFlags2[0][2]) {
						bWithoutFlagAtt = true;
					}
					// ゲス
					if (b.getAttitude() == Attitude.SHITHEAD && actionFlags2[0][3]) {
						bWithoutFlagAtt = true;
					}
					// ドゲス
					if ((b.getAttitude() == Attitude.SUPER_SHITHEAD && actionFlags2[0][4])) {
						bWithoutFlagAtt = true;
					}
					// ---------------------------
					// 知性
					// ---------------------------
					boolean bWithoutFlagInt = false;
					// バッジ級
					if (b.getIntelligence() == Intelligence.WISE && actionFlags3[0][0]) {
						bWithoutFlagInt = true;
					}
					// 普通
					if (b.getIntelligence() == Intelligence.AVERAGE && actionFlags3[0][1]) {
						bWithoutFlagInt = true;
					}
					// あんこ脳
					if ((b.getIntelligence() == Intelligence.FOOL && actionFlags3[0][2])) {
						bWithoutFlagInt = true;
					}
					// ---------------------------
					// 性格、知能の両方を同時にチェックする
					if (actionFlags[Action.WITHOUT_AND.ordinal()][0]) {
						// 性格、知能の両方を同時にチェックする
						if (bWithoutFlagAtt && bWithoutFlagInt) {
							continue;
						}
					} else {
						if (bWithoutFlagAtt || bWithoutFlagInt) {
							continue;
						}
					}
					// ---------------------------

					if (b.isDead()) {
						if (actionFlags[Action.BODY_REMOVE.ordinal()][0]) {

							// 他のゆんばのターゲットならスキップ
							if (!cheackOtherYunbaTarget(b)) {
								continue;
							}

							// 死体掃除
							action = Action.BODY_REMOVE;
							target = b;
							break;
						} else if (b.hasOkazari() && actionFlags[Action.BODY_OKAZARI.ordinal()][0]) {

							// 他のゆんばのターゲットならスキップ
							if (!cheackOtherYunbaTarget(b)) {
								continue;
							}

							// 死体おかざり
							action = Action.BODY_OKAZARI;
							target = b;
							break;
						}
					} else {
						if (b.isDirty() && actionFlags[Action.CLEAN.ordinal()][b.getBodyAgeState().ordinal()]) {

							// 他のゆんばのターゲットならスキップ
							if (!cheackOtherYunbaTarget(b)) {
								continue;
							}

							// 清掃
							action = Action.CLEAN;
							target = b;
							break;
						} else if (b.isDamaged() && actionFlags[Action.HEAL.ordinal()][b.getBodyAgeState().ordinal()]) {

							// 他のゆんばのターゲットならスキップ
							if (!cheackOtherYunbaTarget(b)) {
								continue;
							}

							// 回復
							action = Action.HEAL;
							target = b;
							break;
						} else if (b.isSick() && actionFlags[Action.KABI.ordinal()][b.getBodyAgeState().ordinal()]) {

							// 他のゆんばのターゲットならスキップ
							if (!cheackOtherYunbaTarget(b)) {
								continue;
							}

							// かび処分
							action = Action.KABI;
							target = b;
							break;
						}
						// ゲスの場合必ず実行
						else if (b.isRude() && (b.isFurifuri() || b.getFurifuriDiscipline() != 0)
								&& actionFlags[Action.RUDE.ordinal()][b.getBodyAgeState().ordinal()]) {

							// 他のゆんばのターゲットならスキップ
							if (!cheackOtherYunbaTarget(b)) {
								continue;
							}

							// ゲス矯正
							action = Action.RUDE;
							target = b;
							break;
						} else if (b.hasOkazari() && (b.getOkazari().getOkazariType() == OkazariType.DEFAULT)
								&& actionFlags[Action.OKAZARI.ordinal()][b.getBodyAgeState().ordinal()]) {

							// 他のゆんばのターゲットならスキップ
							if (!cheackOtherYunbaTarget(b)) {
								continue;
							}

							// おかざり没収
							action = Action.OKAZARI;
							target = b;
							break;
						} else if (SimYukkuri.RND.nextBoolean()
								&& actionFlags[Action.DESTROY.ordinal()][b.getBodyAgeState().ordinal()]) {

							// 他のゆんばのターゲットならスキップ
							if (!cheackOtherYunbaTarget(b)) {
								continue;
							}

							// 攻撃
							action = Action.DESTROY;
							target = b;
							break;
						} else if (b.isRaper() && actionFlags[Action.ANTIRAPER.ordinal()][0]) {

							// 他のゆんばのターゲットならスキップ
							if (!cheackOtherYunbaTarget(b)) {
								continue;
							}

							// れいぱー駆除
							action = Action.DESTROY;
							target = b;
							break;
						}
					}
				}
			}

			// 空の餌皿掃除
			if (foodCheck && action == null) {
				for (Map.Entry<Integer, Food> entry : curMap.getFood().entrySet()) {
					Food f = entry.getValue();
					if (SimYukkuri.RND.nextBoolean())
						continue;
					if (!f.isEmpty())
						continue;
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), f.getX(), f.getY(), Barrier.MAP_ITEM)) {
						continue;
					}
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), f.getX(), f.getY(), Barrier.MAP_NOUNUN)) {
						continue;
					}

					// 他のゆんばのターゲットならスキップ
					if (!cheackOtherYunbaTarget(f)) {
						continue;
					}
					// 掃除決定
					action = Action.EMPFOOD;
					target = f;
					break;
				}
			}

			if (action == null) {
				if (destX == -1 && destY == -1) {
					moveTo(defaultX, defaultY);
					// moveTo(SimYukkuri.RND.nextInt(Translate.getMapW()),
					// SimYukkuri.RND.nextInt(Translate.getMapH()));
					speed = 400;
				}
			} else {
				moveTo(target.getX(), target.getY());
				speed = 600;
			}
		} else {
			int vecX = destX - x;
			int vecY = destY - y;
			moveBody();
			boolean bNear = false;
			if (target != null) {
				// 距離が20以内なら掃除する
				if (distance(x, y, target.getX(), target.getY()) < 20) {
					bNear = true;
				}
			}

			// 目的地到着
			if ((destX == -1 && destY == -1) || bNear) {
				if (action == null || target == null)
					return Event.DONOTHING;
				if (target.isRemoved() || target.getZ() > 0) {
					action = null;
					target = null;
					return Event.DONOTHING;
				}
				if (!bNear) {
					moveTo(target.getX(), target.getY());
					return Event.DONOTHING;
				}

				switch (action) {
					case CLEAN:
						((Body) target).setCleaning();
						break;
					case HEAL:
						((Body) target).giveJuice();
						break;
					case KABI:
						((Body) target).remove();
						break;
					case RUDE:
						((Body) target).strikeByPunish();
						break;
					case OKAZARI:
						((Body) target).takeOkazari(false);
						break;
					case DESTROY:
						if (noDamageFallCheck) {
							((Body) target).setNoDamageNextFall();
						}
						if (killCheck) {// 追加
							((Body) target).strikeByPress();
						} else {
							if (vecX > 5)
								vecX = 5;
							else if (vecX < -5)
								vecX = -5;

							if (vecY > 5)
								vecY = 5;
							else if (vecY < -5)
								vecY = -5;
							((Body) target).strikeByObject(1500, 500, mineutiCheck, vecX, vecY);
						}
						break;
					case BODY_REMOVE:
						target.remove();
						break;
					case BODY_OKAZARI:
						((Body) target).takeOkazari(false);
						break;
					case SHIT:
						if (target instanceof Shit) {
							((Shit) target).remove();
						} else if (target instanceof Vomit) {
							((Vomit) target).remove();
						}
						break;
					case STALK:
						((Stalk) target).remove();
						break;
					case EMPFOOD:
						((Food) target).remove();
						break;
					default:
						break;
				}
				action = null;
				target = null;
			} else {
				if (target != null) {
					// 対象が壁の向こうに移動したらリセット
					// 追加
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), target.getX(), target.getY(), Barrier.MAP_ITEM)) {

						action = null;
						target = null;
						return Event.DONOTHING;
					}
					// 追加
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0] && Barrier.acrossBarrier(getX(), getY(),
							target.getX(), target.getY(), Barrier.MAP_NOUNUN)) {

						action = null;
						target = null;
						return Event.DONOTHING;
					}
				}
			}
		}
		return Event.DONOTHING;
	}

	private int distance(int x1, int y1, int x2, int y2) {
		return ((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	private void moveTo(int toX, int toY) {
		destX = Math.max(-10, Math.min(toX, Translate.getMapW() + 10));
		destY = Math.max(-10, Math.min(toY, Translate.getMapH() + 10));
	}

	private int decideDirection(int curPos, int destPos, int range) {
		if (destPos - curPos > range) {
			return 1;
		} else if (curPos - destPos > range) {
			return -1;
		}
		return 0;
	}

	private void moveBody() {

		int step = 1;
		int dirX = 0;
		int dirY = 0;
		// calculate x direction
		if (destX >= 0) {
			dirX = decideDirection(x, destX, step);
			if (dirX == 0) {
				destX = -1;
			}
		}
		// calculate y direction
		if (destY >= 0) {
			dirY = decideDirection(y, destY, step);
			if (dirY == 0) {
				destY = -1;
			}
		}
		// move to the direction
		int vecX = dirX * step * speed / 100;
		int vecY = dirY * step * speed / 100;
		x += vecX;
		y += vecY;
		if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
				&& Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.MAP_ITEM)) {
			// 壁付近は半分の速度で動く 2015/05/25
			x -= vecX / 2;
			y -= vecY / 2;

			return;
		}
		if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
				&& Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.MAP_NOUNUN)) {
			// 壁付近は半分の速度で動く 2015/05/25
			x -= vecX / 2;
			y -= vecY / 2;

			return;
		}

		if (vecX < 0 && x < destX)
			x = destX;
		if (vecX > 0 && x > destX)
			x = destX;
		if (vecY < 0 && y < destY)
			y = destY;
		if (vecY > 0 && y > destY)
			y = destY;

		int maxX = Translate.getMapW();
		int maxY = Translate.getMapH();

		if (x < 0) {
			x = 0;
			dirX = 1;
		} else if (x > maxX) {
			x = maxX;
			dirX = -1;
		}
		if (y < 0) {
			y = 0;
			dirY = 1;
		} else if (y > maxY) {
			y = maxY;
			dirY = -1;
		}
		// update direction of the face
		if (dirX == -1) {
			direction = 0;
		} else if (dirX == 1) {
			direction = 1;
		}
	}

	/**
	 * コンストラクタ
	 * 
	 * @param initX      x座標
	 * @param initY      y座標
	 * @param initOption 0:飼い用、1;野良用
	 */
	public Yunba(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());

		SimYukkuri.world.getCurrentMap().getYunba().put(objId, this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.YUNBA;
		interval = 5;
		value = 30000;
		cost = 200;

		actionFlags = new boolean[Action.values().length][3];
		actionFlags2 = new boolean[1][5];
		actionFlags3 = new boolean[1][3];

		boolean ret = setupYunba(this, false);
		if (ret) {
			moveTo(SimYukkuri.RND.nextInt(Translate.getMapW()), SimYukkuri.RND.nextInt(Translate.getMapH()));
			itemRank = ItemRank.values()[initOption];
			// 森なら野生に変更
			if (SimYukkuri.world.getCurrentMap().getMapIndex() == 5
					|| SimYukkuri.world.getCurrentMap().getMapIndex() == 6) {
				if (itemRank == ItemRank.HOUSE) {
					itemRank = ItemRank.YASEI;
				}
			}
		} else {
			SimYukkuri.world.getCurrentMap().getYunba().remove(objId);
		}
	}

	public Yunba() {

	}

	// 設定メニュー
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean setupYunba(Yunba y, boolean init) {

		JPanel mainPanel = new JPanel();
		JPanel topPanel = new JPanel();
		JPanel westPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel southPanel = new JPanel();
		Action[] action = Action.values();
		checkBox = new JCheckBox[action.length][3];
		boolean ret = false;

		mainPanel.setLayout(new BorderLayout());

		topPanel.setLayout(new BorderLayout());
		westPanel.setLayout(new GridLayout(7, 1));
		centerPanel.setLayout(new GridLayout(7, 3));
		southPanel.setLayout(new GridLayout(8, 2)); // 追加

		JLabel l1 = new JLabel("");
		westPanel.add(l1);
		JLabel l2 = new JLabel(ResourceUtil.getInstance().read("enums_babyyu"));
		centerPanel.add(l2);
		JLabel l3 = new JLabel(ResourceUtil.getInstance().read("enums_childyu"));
		centerPanel.add(l3);
		JLabel l4 = new JLabel(ResourceUtil.getInstance().read("enums_adultyu"));
		centerPanel.add(l4);

		ButtonListener buttonListener = new ButtonListener();

		for (int i = 0; i < 6; i++) {
			JButton but = new JButton(action[i].toString());
			but.setActionCommand(action[i].name());
			but.addActionListener(buttonListener);
			westPanel.add(but);
			for (int j = 0; j < 3; j++) {
				checkBox[i][j] = new JCheckBox("");
				if (init)
					checkBox[i][j].setSelected(y.actionFlags[i][j]);
				else
					checkBox[i][j].setSelected(defaultSetFlags[i][j]);
				centerPanel.add(checkBox[i][j]);
			}
		}

		// --------------------------------------------
		JPanel middlePanel = new JPanel();
		JPanel westPanel2 = new JPanel();
		JPanel centerPanel2 = new JPanel();
		middlePanel.setLayout(new BorderLayout());
		westPanel2.setLayout(new GridLayout(2, 1));
		centerPanel2.setLayout(new GridLayout(2, 5));
		checkBox2 = new JCheckBox[1][5];

		JLabel lw2_1 = new JLabel("");
		westPanel2.add(lw2_1);
		JLabel lw2_2 = new JLabel(ResourceUtil.getInstance().read("item_noprocesstarget"));
		westPanel2.add(lw2_2);

		JLabel lc2_1 = new JLabel(ResourceUtil.getInstance().read("attitude_verynice"));
		centerPanel2.add(lc2_1);
		JLabel lc2_2 = new JLabel(ResourceUtil.getInstance().read("attitude_nice"));
		centerPanel2.add(lc2_2);
		JLabel lc2_3 = new JLabel(ResourceUtil.getInstance().read("attitude_normal"));
		centerPanel2.add(lc2_3);
		JLabel lc2_4 = new JLabel(ResourceUtil.getInstance().read("attitude_shithead"));
		centerPanel2.add(lc2_4);
		JLabel lc2_5 = new JLabel(ResourceUtil.getInstance().read("attitude_supershithead"));
		centerPanel2.add(lc2_5);
		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < 5; j++) {
				checkBox2[i][j] = new JCheckBox("");
				if (init)
					checkBox2[i][j].setSelected(y.actionFlags2[i][j]);
				else
					checkBox2[i][j].setSelected(defaultSetFlags2[i][j]);
				centerPanel2.add(checkBox2[i][j]);
			}
		}
		// --------------------------------------------
		JPanel middlePanel2 = new JPanel();
		JPanel westPanel3 = new JPanel();
		JPanel centerPanel3 = new JPanel();
		middlePanel2.setLayout(new BorderLayout());
		westPanel3.setLayout(new GridLayout(2, 1));
		centerPanel3.setLayout(new GridLayout(2, 3));
		checkBox3 = new JCheckBox[1][3];

		JLabel lw3_1 = new JLabel("");
		westPanel3.add(lw3_1);
		JLabel lw3_2 = new JLabel(ResourceUtil.getInstance().read("item_noprocesstarget"));
		westPanel3.add(lw3_2);

		JLabel lc3_1 = new JLabel(ResourceUtil.getInstance().read("intel_badge"));
		centerPanel3.add(lc3_1);
		JLabel lc3_2 = new JLabel(ResourceUtil.getInstance().read("intel_normal"));
		centerPanel3.add(lc3_2);
		JLabel lc3_3 = new JLabel(ResourceUtil.getInstance().read("intel_fool"));
		centerPanel3.add(lc3_3);

		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < 3; j++) {
				checkBox3[i][j] = new JCheckBox("");
				if (init)
					checkBox3[i][j].setSelected(y.actionFlags3[i][j]);
				else
					checkBox3[i][j].setSelected(defaultSetFlags3[i][j]);
				centerPanel3.add(checkBox3[i][j]);
			}
		}

		// --------------------------------------------
		for (int i = 0; i < Action.values().length - 6; i++) {// 追加
			checkBox[6 + i][0] = new JCheckBox(action[6 + i].toString());
			if (init)
				checkBox[6 + i][0].setSelected(y.actionFlags[6 + i][0]);
			else
				checkBox[6 + i][0].setSelected(defaultSetFlags[6 + i][0]);
			southPanel.add(checkBox[6 + i][0]);
		}
		colorBox = new JComboBox(COL_LIST);
		if (init)
			colorBox.setSelectedIndex(y.color);
		else
			colorBox.setSelectedIndex(defaultColor);
		southPanel.add(new JLabel(""));
		southPanel.add(colorBox);

		JPanel topPanel2 = new JPanel();
		topPanel2.setLayout(new BorderLayout());
		JPanel topPanel3 = new JPanel();
		topPanel3.setLayout(new BorderLayout());

		topPanel.setPreferredSize(new Dimension(350, 200));
		westPanel.setPreferredSize(new Dimension(120, 200));
		topPanel.add(BorderLayout.LINE_START, westPanel);
		topPanel.add(BorderLayout.CENTER, centerPanel);

		middlePanel.setPreferredSize(new Dimension(350, 40));
		westPanel2.setPreferredSize(new Dimension(70, 40));
		middlePanel.add(BorderLayout.LINE_START, westPanel2);
		middlePanel.add(BorderLayout.CENTER, centerPanel2);

		middlePanel2.setPreferredSize(new Dimension(350, 40));
		westPanel3.setPreferredSize(new Dimension(70, 40));
		middlePanel2.add(BorderLayout.LINE_START, westPanel3);
		middlePanel2.add(BorderLayout.CENTER, centerPanel3);

		topPanel3.add(BorderLayout.LINE_START, middlePanel);
		topPanel3.add(BorderLayout.SOUTH, middlePanel2);
		// 枠線設定
		LineBorder border = new LineBorder(Color.BLACK, 1, true);
		middlePanel.setBorder(border);
		middlePanel2.setBorder(border);
		southPanel.setBorder(border);

		topPanel2.add(BorderLayout.LINE_START, topPanel);
		topPanel2.add(BorderLayout.SOUTH, topPanel3);

		mainPanel.setPreferredSize(new Dimension(350, 400));
		topPanel2.setPreferredSize(new Dimension(350, 350));
		southPanel.setPreferredSize(new Dimension(350, 150));
		mainPanel.add(BorderLayout.LINE_START, topPanel2);
		mainPanel.add(BorderLayout.SOUTH, southPanel);

		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel,
				ResourceUtil.getInstance().read("item_yunbasettings"), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (dlgRet == JOptionPane.OK_OPTION) {
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 3; j++) {
					y.actionFlags[i][j] = checkBox[i][j].isSelected();
					defaultSetFlags[i][j] = checkBox[i][j].isSelected();
				}
			}
			// 性格
			for (int i = 0; i < 1; i++) {
				for (int j = 0; j < 5; j++) {
					y.actionFlags2[i][j] = checkBox2[i][j].isSelected();
					defaultSetFlags2[i][j] = checkBox2[i][j].isSelected();
				}
			}
			// 知性
			for (int i = 0; i < 1; i++) {
				for (int j = 0; j < 3; j++) {
					y.actionFlags3[i][j] = checkBox3[i][j].isSelected();
					defaultSetFlags3[i][j] = checkBox3[i][j].isSelected();
				}
			}
			for (int i = 0; i < Action.values().length - 6; i++) {// 追加
				y.actionFlags[6 + i][0] = checkBox[6 + i][0].isSelected();
				defaultSetFlags[6 + i][0] = checkBox[6 + i][0].isSelected();
			}
			y.color = colorBox.getSelectedIndex();
			defaultColor = colorBox.getSelectedIndex();

			y.bodyCheck = false;
			y.shitCheck = false;
			y.stalkCheck = false; // 追加
			y.norndCheck = false;
			y.killCheck = false;
			y.mineutiCheck = false;
			y.noDamageFallCheck = false;
			y.foodCheck = false;
			for (int i = 0; i < y.actionFlags.length; i++) {
				for (int j = 0; j < 3; j++) {
					if (i == Action.SHIT.ordinal()) {
						if (y.actionFlags[i][0])
							y.shitCheck = true;
					} else if (i == Action.STALK.ordinal()) {
						if (y.actionFlags[i][0])
							y.stalkCheck = true;
					} else if (i == Action.NORND.ordinal()) {
						if (y.actionFlags[i][0])
							y.norndCheck = true;
					} else if (i == Action.KILL.ordinal()) {
						if (y.actionFlags[i][0])
							y.killCheck = true;
					} else if (i == Action.MINEUTI.ordinal()) {
						if (y.actionFlags[i][0])
							y.mineutiCheck = true;
					} else if (i == Action.NODAMAGE_FALL.ordinal()) {
						if (y.actionFlags[i][0])
							y.noDamageFallCheck = true;
					} else if (i == Action.EMPFOOD.ordinal()) {
						if (y.actionFlags[i][0])
							y.foodCheck = true;
					} else {
						if (y.actionFlags[i][j])
							y.bodyCheck = true;
					}
				}
			}

			boolean brush = false;
			boolean spike = false;
			if (y.actionFlags[Action.CLEAN.ordinal()][0]
					|| y.actionFlags[Action.CLEAN.ordinal()][1]
					|| y.actionFlags[Action.CLEAN.ordinal()][2])
				brush = true;

			if (y.actionFlags[Action.OKAZARI.ordinal()][0]
					|| y.actionFlags[Action.OKAZARI.ordinal()][1]
					|| y.actionFlags[Action.OKAZARI.ordinal()][2])
				brush = true;

			if (y.actionFlags[Action.BODY_REMOVE.ordinal()][0]
					|| y.actionFlags[Action.BODY_REMOVE.ordinal()][1]
					|| y.actionFlags[Action.BODY_REMOVE.ordinal()][2])
				brush = true;

			if (y.actionFlags[Action.BODY_OKAZARI.ordinal()][0]
					|| y.actionFlags[Action.BODY_OKAZARI.ordinal()][1]
					|| y.actionFlags[Action.BODY_OKAZARI.ordinal()][2])
				brush = true;

			if (y.actionFlags[Action.SHIT.ordinal()][0]
					|| y.actionFlags[Action.SHIT.ordinal()][1]
					|| y.actionFlags[Action.SHIT.ordinal()][2])
				brush = true;

			if (y.actionFlags[Action.KABI.ordinal()][0]
					|| y.actionFlags[Action.KABI.ordinal()][1]
					|| y.actionFlags[Action.KABI.ordinal()][2])
				spike = true;

			if (y.actionFlags[Action.RUDE.ordinal()][0]
					|| y.actionFlags[Action.RUDE.ordinal()][1]
					|| y.actionFlags[Action.RUDE.ordinal()][2])
				spike = true;

			if (y.actionFlags[Action.DESTROY.ordinal()][0]
					|| y.actionFlags[Action.DESTROY.ordinal()][1]
					|| y.actionFlags[Action.DESTROY.ordinal()][2])
				spike = true;

			if (y.actionFlags[Action.ANTIRAPER.ordinal()][0]
					|| y.actionFlags[Action.ANTIRAPER.ordinal()][1]
					|| y.actionFlags[Action.ANTIRAPER.ordinal()][2])
				spike = true;

			y.layerCount = 1;
			if (brush)
				y.layerCount++;
			if (spike)
				y.layerCount++;

			y.drawLayer = new int[y.layerCount];
			int i = 0;
			if (brush) {
				y.drawLayer[i] = 1;
				i++;
			}
			y.drawLayer[i] = 0;
			i++;
			if (spike) {
				y.drawLayer[i] = 2;
			}

			y.defaultX = y.getX();
			y.defaultY = y.getY();

			ret = true;
		}
		y.action = null;
		y.target = null;
		return ret;
	}

	public static class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			Action sel = Action.valueOf(command);

			if (checkBox[sel.ordinal()][0].isSelected()) {
				checkBox[sel.ordinal()][0].setSelected(false);
				checkBox[sel.ordinal()][1].setSelected(false);
				checkBox[sel.ordinal()][2].setSelected(false);
			} else {
				checkBox[sel.ordinal()][0].setSelected(true);
				checkBox[sel.ordinal()][1].setSelected(true);
				checkBox[sel.ordinal()][2].setSelected(true);
			}
		}
	}

	// 他のゆんばのターゲットになっているか
	public boolean cheackOtherYunbaTarget(Obj o) {
		for (Map.Entry<Integer, Yunba> entry : SimYukkuri.world.getCurrentMap().getYunba().entrySet()) {
			Yunba yunba = entry.getValue();
			if (yunba == this) {
				continue;
			}
			if (((Yunba) yunba).target == o) {
				return false;
			}
		}
		return true;
	}

	public ItemRank getItemRank() {
		return itemRank;
	}

	public void setItemRank(ItemRank itemRank) {
		this.itemRank = itemRank;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public boolean[][] getActionFlags() {
		return actionFlags;
	}

	public void setActionFlags(boolean[][] actionFlags) {
		this.actionFlags = actionFlags;
	}

	public boolean[][] getActionFlags2() {
		return actionFlags2;
	}

	public void setActionFlags2(boolean[][] actionFlags2) {
		this.actionFlags2 = actionFlags2;
	}

	public boolean[][] getActionFlags3() {
		return actionFlags3;
	}

	public void setActionFlags3(boolean[][] actionFlags3) {
		this.actionFlags3 = actionFlags3;
	}

	public boolean isBodyCheck() {
		return bodyCheck;
	}

	public void setBodyCheck(boolean bodyCheck) {
		this.bodyCheck = bodyCheck;
	}

	public boolean isShitCheck() {
		return shitCheck;
	}

	public void setShitCheck(boolean shitCheck) {
		this.shitCheck = shitCheck;
	}

	public boolean isStalkCheck() {
		return stalkCheck;
	}

	public void setStalkCheck(boolean stalkCheck) {
		this.stalkCheck = stalkCheck;
	}

	public boolean isNorndCheck() {
		return norndCheck;
	}

	public void setNorndCheck(boolean norndCheck) {
		this.norndCheck = norndCheck;
	}

	public boolean isKillCheck() {
		return killCheck;
	}

	public void setKillCheck(boolean killCheck) {
		this.killCheck = killCheck;
	}

	public boolean isMineutiCheck() {
		return mineutiCheck;
	}

	public void setMineutiCheck(boolean mineutiCheck) {
		this.mineutiCheck = mineutiCheck;
	}

	public boolean isNoDamageFallCheck() {
		return noDamageFallCheck;
	}

	public void setNoDamageFallCheck(boolean noDamageFallCheck) {
		this.noDamageFallCheck = noDamageFallCheck;
	}

	public boolean isFoodCheck() {
		return foodCheck;
	}

	public void setFoodCheck(boolean foodCheck) {
		this.foodCheck = foodCheck;
	}

	public int[] getDrawLayer() {
		return drawLayer;
	}

	public void setDrawLayer(int[] drawLayer) {
		this.drawLayer = drawLayer;
	}

	public int getLayerCount() {
		return layerCount;
	}

	public void setLayerCount(int layerCount) {
		this.layerCount = layerCount;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Obj getTarget() {
		return target;
	}

	public void setTarget(Obj target) {
		this.target = target;
	}

	public int getDestX() {
		return destX;
	}

	public void setDestX(int destX) {
		this.destX = destX;
	}

	public int getDestY() {
		return destY;
	}

	public void setDestY(int destY) {
		this.destY = destY;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getDefaultX() {
		return defaultX;
	}

	public void setDefaultX(int defaultX) {
		this.defaultX = defaultX;
	}

	public int getDefaultY() {
		return defaultY;
	}

	public void setDefaultY(int defaultY) {
		this.defaultY = defaultY;
	}

}
