package org.simyukkuri.entity.core.world.item;

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
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.entity.core.world.bodylinked.Okazari.OkazariType;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.system.Cash;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameLocale;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.GameView;
import org.simyukkuri.util.GameWorld;

/**
 * ゆんば
 */
public class Yunba extends WorldEntity {

	private static final long serialVersionUID = 3347291782760455512L;

	/** アクションのテーブル */
	public static enum Action {
		CLEAN(GameText.read("command_clean"), ""),
		HEAL(GameText.read("item_yunbarecovery"), ""),
		KABI(GameText.read("item_yunbakillmold"), ""),
		RUDE(GameText.read("item_yunbacorrection"), ""),
		OKAZARI(GameText.read("item_yunbaaccessory"), ""),
		DESTROY(GameText.read("item_yunbaindiscriminate"), ""),
		BODY_REMOVE(GameText.read("item_yunbacleandead"), ""),
		BODY_OKAZARI(GameText.read("item_yunbaconfiscation"), ""),
		SHIT(GameText.read("item_yunbacleanunun"), ""),
		STALK(GameText.read("item_yunbacleanstalk"), ""), // 追加
		WALLTHROUGH(GameText.read("item_yunbathroughwall"), ""),
		NORND(GameText.read("item_yunbasaveenergy"), ""),
		KILL(GameText.read("item_yunbaattackup"), ""),
		MINEUTI(GameText.read("item_yunbaallowance"), ""),
		NODAMAGE_FALL(GameText.read("item_yunbanofalldamage"), ""),
		EMPFOOD(GameText.read("item_yunbadish"), ""),
		ANTIRAPER(GameText.read("item_yunbakillraper"), ""),
		WITHOUT_AND(GameText.read("item_yunbacondition"), ""),
		;

		private String name;

		Action(String nameJ, String nameE) {
			this.name = GameLocale.isJapanese() ? nameJ : nameE;
		}

		/** アクション名の文字列表現を返す。 */
		public String toString() {
			return name;
		}
	}

	/** 色のテーブル */
	private static final String[] COL_LIST = {
			GameText.read("yellow"),
			GameText.read("white"),
			GameText.read("red"),
			GameText.read("pink"),
			GameText.read("purple"),
			GameText.read("green"),
			GameText.read("gray"),
			GameText.read("blue"),
			GameText.read("black"),
	};

	public static final int hitCheckObjType = 0;
	private static BufferedImage[][] bodyImages = new BufferedImage[10][2];
	private static BufferedImage[][] images = new BufferedImage[6][2];
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
	private Entity target = null;

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

	/** アイテム画像をレイヤー配列にセットし、使用レイヤー数を返す。 */
	@Override
	public int getImageLayer(BufferedImage[] layer) {
		for (int i = 0; i < layerCount; i++) {
			if (drawLayer[i] == 0) {
				if (itemRank == ItemRank.HOUSE) {
					layer[i] = bodyImages[color][direction];
				} else {
					layer[i] = bodyImages[9][direction];
				}
			} else {
				if (itemRank == ItemRank.HOUSE) {
					layer[i] = images[drawLayer[i]][direction];
				} else {
					layer[i] = images[drawLayer[i] + 3][direction];
				}
			}
		}
		return layerCount;
	}

	/** アイテムの影画像を返す。 */
	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return images[3][direction];
	}

	/** セットアップメニューを持つかを返す。 */
	@Override
	public boolean hasSetupMenu() {
		return true;
	}

	/** 境界線の取得 */
	public static Rectangle4y getBounding() {
		return boundary;
	}

	/** 毎ティックの状態更新を行う。 */
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

	/** ワールドからこのアイテムを除去する。 */
	@Override
	public void removeFromWorld() {
		GameWorld.get().getCurrentWorldState().getYunbas().remove(objId);
	}

	/** 毎ティックの状態更新を行う。 */
	@Override
	public TickResult clockTick() {
		setAge(getAge() + TICK);
		if (isRemoved()) {
			action = null;
			target = null;
			removeFromWorld();
			return TickResult.REMOVED;
		}
		upDate();
		if (grabbed) {
			action = null;
			target = null;
			return TickResult.NONE;
		}

		if (getZ() > 0) {
			z -= 5;
			if (z < 0) {
				z = 0;
			}
			action = null;
			target = null;
			return TickResult.NONE;
		}

		if (action == null && (getAge() > 10 || norndCheck)) { // 追加
			WorldState curMap = GameWorld.get().getCurrentWorldState();
			setAge(0);

			if (shitCheck) {
				for (Map.Entry<Integer, Shit> entry : curMap.getShit().entrySet()) {
					Shit o = entry.getValue();
					if (GameRandom.nextBoolean()) {
						continue;
					}
					// 追加
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), o.getX(), o.getY(), Barrier.ITEM_BLOCK_FLAG)) {
						continue;
					}
					// 追加
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), o.getX(), o.getY(), Barrier.NO_UNUN_BLOCK_FLAG)) {
						continue;
					}

					boolean shitOnToilet = false;
					// トイレの上のうんうんは無視。空中もチェック
					List<Toilet> toiletList = new LinkedList<>(GameWorld.get().getCurrentWorldState().getToilets().values());
					for (Toilet t : toiletList) {
						// Hitするなら終了
						if (t.checkHitObj(o, true)) {
							shitOnToilet = true;
							break;
						}
					}
					// トイレの上にある
					if (shitOnToilet) {
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
					if (GameRandom.nextBoolean()) {
						continue;
					}
					// 追加
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), o.getX(), o.getY(), Barrier.ITEM_BLOCK_FLAG)) {
						continue;
					}
					// 追加
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), o.getX(), o.getY(), Barrier.NO_UNUN_BLOCK_FLAG)) {
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
				if (curMap.getStalks() != null) {
					for (Map.Entry<Integer, Stalk> entry : curMap.getStalks().entrySet()) {
						Stalk s = entry.getValue();
						if (norndCheck == false && GameRandom.nextBoolean()) {
							continue;
						}
						int id = s.getPlantYukkuri();
						if (GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(id) != null) {
							continue;
						}
						if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
								&& Barrier.acrossBarrier(getX(), getY(), s.getX(), s.getY(),
										Barrier.ITEM_BLOCK_FLAG)) {
							continue;
						}
						if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
								&& Barrier.acrossBarrier(getX(), getY(), s.getX(), s.getY(),
										Barrier.NO_UNUN_BLOCK_FLAG)) {
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

				if (curMap.getFoods() != null) {
					for (Map.Entry<Integer, Food> entry : curMap.getFoods().entrySet()) {
						Food f = entry.getValue();
						if (f.getFoodType() != Food.FoodType.STALK) {
							continue;
						}
						if (GameRandom.nextBoolean()) {
							continue;
						}
						if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
								&& Barrier.acrossBarrier(getX(), getY(), f.getX(), f.getY(),
										Barrier.ITEM_BLOCK_FLAG)) {
							continue;
						}
						if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
								&& Barrier.acrossBarrier(getX(), getY(), f.getX(), f.getY(),
										Barrier.NO_UNUN_BLOCK_FLAG)) {
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
				for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentWorldState().getYukkuriRegistry().entrySet()) {
					Yukkuri b = entry.getValue();
					if (norndCheck == false && GameRandom.nextBoolean()) {
						continue;
					}
					// 茎にぶら下がってる固体はスルー
					if (b.hasBindStalk() || b.getZ() > 0) {
						continue;
					}

					// 追加
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), b.getX(), b.getY(), Barrier.ITEM_BLOCK_FLAG)) {
						continue;
					}
					// 追加
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), b.getX(), b.getY(), Barrier.NO_UNUN_BLOCK_FLAG)) {
						continue;
					}

					// ---------------------------
					// 性格
					// ---------------------------
					boolean skipByAttitude = false;
					// 超善良
					if ((b.getAttitude() == Attitude.VERY_NICE && actionFlags2[0][0])) {
						skipByAttitude = true;
					}
					// 善良
					if (b.getAttitude() == Attitude.NICE && actionFlags2[0][1]) {
						skipByAttitude = true;
					}
					// 普通
					if (b.getAttitude() == Attitude.AVERAGE && actionFlags2[0][2]) {
						skipByAttitude = true;
					}
					// ゲス
					if (b.getAttitude() == Attitude.SHITHEAD && actionFlags2[0][3]) {
						skipByAttitude = true;
					}
					// ドゲス
					if ((b.getAttitude() == Attitude.SUPER_SHITHEAD && actionFlags2[0][4])) {
						skipByAttitude = true;
					}
					// ---------------------------
					// 知性
					// ---------------------------
					boolean skipByIntelligence = false;
					// バッジ級
					if (b.getIntelligence() == Intelligence.WISE && actionFlags3[0][0]) {
						skipByIntelligence = true;
					}
					// 普通
					if (b.getIntelligence() == Intelligence.AVERAGE && actionFlags3[0][1]) {
						skipByIntelligence = true;
					}
					// あんこ脳
					if ((b.getIntelligence() == Intelligence.FOOL && actionFlags3[0][2])) {
						skipByIntelligence = true;
					}
					// ---------------------------
					// 性格、知能の両方を同時にチェックする
					if (actionFlags[Action.WITHOUT_AND.ordinal()][0]) {
						// 性格、知能の両方を同時にチェックする
						if (skipByAttitude && skipByIntelligence) {
							continue;
						}
					} else {
						if (skipByAttitude || skipByIntelligence) {
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
						if (b.isDirty() && actionFlags[Action.CLEAN.ordinal()][b.getAgeState().ordinal()]) {

							// 他のゆんばのターゲットならスキップ
							if (!cheackOtherYunbaTarget(b)) {
								continue;
							}

							// 清掃
							action = Action.CLEAN;
							target = b;
							break;
						} else if (b.isDamaged() && actionFlags[Action.HEAL.ordinal()][b.getAgeState().ordinal()]) {

							// 他のゆんばのターゲットならスキップ
							if (!cheackOtherYunbaTarget(b)) {
								continue;
							}

							// 回復
							action = Action.HEAL;
							target = b;
							break;
						} else if (b.isSick() && actionFlags[Action.KABI.ordinal()][b.getAgeState().ordinal()]) {

							// 他のゆんばのターゲットならスキップ
							if (!cheackOtherYunbaTarget(b)) {
								continue;
							}

							// かび処分
							action = Action.KABI;
							target = b;
							break;
						} else if (b.isRude() && (b.isFurifuri() || b.getFurifuriDiscipline() != 0) // ゲスの場合必ず実行
								&& actionFlags[Action.RUDE.ordinal()][b.getAgeState().ordinal()]) {

							// 他のゆんばのターゲットならスキップ
							if (!cheackOtherYunbaTarget(b)) {
								continue;
							}

							// ゲス矯正
							action = Action.RUDE;
							target = b;
							break;
						} else if (b.hasOkazari() && (b.getOkazaris().getOkazariType() == OkazariType.DEFAULT)
								&& actionFlags[Action.OKAZARI.ordinal()][b.getAgeState().ordinal()]) {

							// 他のゆんばのターゲットならスキップ
							if (!cheackOtherYunbaTarget(b)) {
								continue;
							}

							// おかざり没収
							action = Action.OKAZARI;
							target = b;
							break;
						} else if (GameRandom.nextBoolean()
								&& actionFlags[Action.DESTROY.ordinal()][b.getAgeState().ordinal()]) {

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
				for (Map.Entry<Integer, Food> entry : curMap.getFoods().entrySet()) {
					Food f = entry.getValue();
					if (GameRandom.nextBoolean()) {
						continue;
					}
					if (!f.isEmpty()) {
						continue;
					}
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), f.getX(), f.getY(), Barrier.ITEM_BLOCK_FLAG)) {
						continue;
					}
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
							&& Barrier.acrossBarrier(getX(), getY(), f.getX(), f.getY(), Barrier.NO_UNUN_BLOCK_FLAG)) {
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
					// moveTo(GameRandom.nextInt(Translate.getWorldWidth()),
					// GameRandom.nextInt(Translate.getWorldHeight()));
					speed = 400;
				}
			} else {
				moveTo(target.getX(), target.getY());
				speed = 600;
			}
		} else {
			int vecX = destX - x;
			int vecY = destY - y;
			moveYukkuri();
			boolean nearTarget = false;
			if (target != null) {
				// 距離が20以内なら掃除する
				if (distance(x, y, target.getX(), target.getY()) < 20) {
					nearTarget = true;
				}
			}

			// 目的地到着
			if ((destX == -1 && destY == -1) || nearTarget) {
				if (action == null || target == null) {
					return TickResult.NONE;
				}
				if (target.isRemoved() || target.getZ() > 0) {
					action = null;
					target = null;
					return TickResult.NONE;
				}
				if (!nearTarget) {
					moveTo(target.getX(), target.getY());
					return TickResult.NONE;
				}

				switch (action) {
					case CLEAN:
						((Yukkuri) target).setCleaning();
						break;
					case HEAL:
						((Yukkuri) target).giveJuice();
						break;
					case KABI:
						((Yukkuri) target).remove();
						break;
					case RUDE:
						((Yukkuri) target).strikeByPunish();
						break;
					case OKAZARI:
						((Yukkuri) target).takeOkazari(false);
						break;
					case DESTROY:
						if (noDamageFallCheck) {
							((Yukkuri) target).setNoDamageNextFall();
						}
						if (killCheck) { // 追加
							((Yukkuri) target).strikeByPress();
						} else {
							if (vecX > 5) {
								vecX = 5;
							} else if (vecX < -5) {
								vecX = -5;
							}

							if (vecY > 5) {
								vecY = 5;
							} else if (vecY < -5) {
								vecY = -5;
							}
							((Yukkuri) target).strikeByObject(1500, 500, mineutiCheck, vecX, vecY);
						}
						break;
					case BODY_REMOVE:
						target.remove();
						break;
					case BODY_OKAZARI:
						((Yukkuri) target).takeOkazari(false);
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
							&& Barrier.acrossBarrier(getX(), getY(), target.getX(), target.getY(),
									Barrier.ITEM_BLOCK_FLAG)) {

						action = null;
						target = null;
						return TickResult.NONE;
					}
					// 追加
					if (!actionFlags[Action.WALLTHROUGH.ordinal()][0] && Barrier.acrossBarrier(getX(), getY(),
							target.getX(), target.getY(), Barrier.NO_UNUN_BLOCK_FLAG)) {

						action = null;
						target = null;
						return TickResult.NONE;
					}
				}
			}
		}
		return TickResult.NONE;
	}

	private int distance(int x1, int y1, int x2, int y2) {
		return ((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	private void moveTo(int toX, int toY) {
		destX = Math.max(-10, Math.min(toX, Translate.getWorldWidth() + 10));
		destY = Math.max(-10, Math.min(toY, Translate.getWorldHeight() + 10));
	}

	private int decideDirection(int curPos, int destPos, int range) {
		if (destPos - curPos > range) {
			return 1;
		} else if (curPos - destPos > range) {
			return -1;
		}
		return 0;
	}

	private void moveYukkuri() {

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
				&& Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.ITEM_BLOCK_FLAG)) {
			// 壁付近は半分の速度で動く 2015/05/25
			x -= vecX / 2;
			y -= vecY / 2;

			return;
		}
		if (!actionFlags[Action.WALLTHROUGH.ordinal()][0]
				&& Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.NO_UNUN_BLOCK_FLAG)) {
			// 壁付近は半分の速度で動く 2015/05/25
			x -= vecX / 2;
			y -= vecY / 2;

			return;
		}

		if (vecX < 0 && x < destX) {
			x = destX;
		}
		if (vecX > 0 && x > destX) {
			x = destX;
		}
		if (vecY < 0 && y < destY) {
			y = destY;
		}
		if (vecY > 0 && y > destY) {
			y = destY;
		}

		int maxX = Translate.getWorldWidth();
		int maxY = Translate.getWorldHeight();

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

		if (GameWorld.get() != null) {
			GameWorld.get().getCurrentWorldState().getYunbas().put(objId, this);
			GameWorld.get().getCurrentWorldState().registerEntity(objId, this);
		}
		objType = Type.OBJECT;
		worldEntityType = WorldEntityKind.YUNBA;
		interval = 5;
		value = 30000;
		cost = 200;

		actionFlags = new boolean[Action.values().length][3];
		actionFlags2 = new boolean[1][5];
		actionFlags3 = new boolean[1][3];

		boolean ret = setupYunba(this, false);
		if (ret) {
			moveTo(GameRandom.nextInt(Translate.getWorldWidth()), GameRandom.nextInt(Translate.getWorldHeight()));
			itemRank = ItemRank.values()[initOption];
			// 森なら野生に変更
			if (GameWorld.get().getCurrentWorldState().getWorldIndex() == 5
					|| GameWorld.get().getCurrentWorldState().getWorldIndex() == 6) {
				if (itemRank == ItemRank.HOUSE) {
					itemRank = ItemRank.YASEI;
				}
			}
		} else {
			GameWorld.get().getCurrentWorldState().getYunbas().remove(objId);
		}
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public Yunba() {

	}

	// 設定メニュー
	/** ゆんばの設定ダイアログを表示し、変更があれば true を返す。 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean setupYunba(Yunba y, boolean init) {

		JPanel mainPanel = new JPanel();
		JPanel topPanel = new JPanel();
		final JPanel westPanel = new JPanel();
		final JPanel centerPanel = new JPanel();
		final JPanel southPanel = new JPanel();
		Action[] action = Action.values();
		checkBox = new JCheckBox[action.length][3];

		mainPanel.setLayout(new BorderLayout());

		topPanel.setLayout(new BorderLayout());
		westPanel.setLayout(new GridLayout(7, 1));
		centerPanel.setLayout(new GridLayout(7, 3));
		southPanel.setLayout(new GridLayout(8, 2)); // 追加

		JLabel l1 = new JLabel("");
		westPanel.add(l1);
		JLabel l2 = new JLabel(GameText.read("enums_babyyu"));
		centerPanel.add(l2);
		JLabel l3 = new JLabel(GameText.read("enums_childyu"));
		centerPanel.add(l3);
		JLabel l4 = new JLabel(GameText.read("enums_adultyu"));
		centerPanel.add(l4);

		ButtonListener buttonListener = new ButtonListener();

		for (int i = 0; i < 6; i++) {
			JButton but = new JButton(action[i].toString());
			but.setActionCommand(action[i].name());
			but.addActionListener(buttonListener);
			westPanel.add(but);
			for (int j = 0; j < 3; j++) {
				checkBox[i][j] = new JCheckBox("");
				if (init) {
					checkBox[i][j].setSelected(y.actionFlags[i][j]);
				} else {
					checkBox[i][j].setSelected(defaultSetFlags[i][j]);
				}
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

		JLabel lw21 = new JLabel("");
		westPanel2.add(lw21);
		JLabel lw22 = new JLabel(GameText.read("item_noprocesstarget"));
		westPanel2.add(lw22);

		JLabel lc21 = new JLabel(GameText.read("attitude_verynice"));
		centerPanel2.add(lc21);
		JLabel lc22 = new JLabel(GameText.read("attitude_nice"));
		centerPanel2.add(lc22);
		JLabel lc23 = new JLabel(GameText.read("attitude_normal"));
		centerPanel2.add(lc23);
		JLabel lc24 = new JLabel(GameText.read("attitude_shithead"));
		centerPanel2.add(lc24);
		JLabel lc25 = new JLabel(GameText.read("attitude_supershithead"));
		centerPanel2.add(lc25);
		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < 5; j++) {
				checkBox2[i][j] = new JCheckBox("");
				if (init) {
					checkBox2[i][j].setSelected(y.actionFlags2[i][j]);
				} else {
					checkBox2[i][j].setSelected(defaultSetFlags2[i][j]);
				}
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

		JLabel lw31 = new JLabel("");
		westPanel3.add(lw31);
		JLabel lw32 = new JLabel(GameText.read("item_noprocesstarget"));
		westPanel3.add(lw32);

		JLabel lc31 = new JLabel(GameText.read("intel_badge"));
		centerPanel3.add(lc31);
		JLabel lc32 = new JLabel(GameText.read("intel_normal"));
		centerPanel3.add(lc32);
		JLabel lc33 = new JLabel(GameText.read("intel_fool"));
		centerPanel3.add(lc33);

		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < 3; j++) {
				checkBox3[i][j] = new JCheckBox("");
				if (init) {
					checkBox3[i][j].setSelected(y.actionFlags3[i][j]);
				} else {
					checkBox3[i][j].setSelected(defaultSetFlags3[i][j]);
				}
				centerPanel3.add(checkBox3[i][j]);
			}
		}

		// --------------------------------------------
		for (int i = 0; i < Action.values().length - 6; i++) { // 追加
			checkBox[6 + i][0] = new JCheckBox(action[6 + i].toString());
			if (init) {
				checkBox[6 + i][0].setSelected(y.actionFlags[6 + i][0]);
			} else {
				checkBox[6 + i][0].setSelected(defaultSetFlags[6 + i][0]);
			}
			southPanel.add(checkBox[6 + i][0]);
		}
		colorBox = new JComboBox(COL_LIST);
		if (init) {
			colorBox.setSelectedIndex(y.color);
		} else {
			colorBox.setSelectedIndex(defaultColor);
		}
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

		int dlgRet = JOptionPane.showConfirmDialog(GameView.getDialogParent(), mainPanel,
				GameText.read("item_yunbasettings"), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		boolean ret = false;
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
			for (int i = 0; i < Action.values().length - 6; i++) { // 追加
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
						if (y.actionFlags[i][0]) {
							y.shitCheck = true;
						}
					} else if (i == Action.STALK.ordinal()) {
						if (y.actionFlags[i][0]) {
							y.stalkCheck = true;
						}
					} else if (i == Action.NORND.ordinal()) {
						if (y.actionFlags[i][0]) {
							y.norndCheck = true;
						}
					} else if (i == Action.KILL.ordinal()) {
						if (y.actionFlags[i][0]) {
							y.killCheck = true;
						}
					} else if (i == Action.MINEUTI.ordinal()) {
						if (y.actionFlags[i][0]) {
							y.mineutiCheck = true;
						}
					} else if (i == Action.NODAMAGE_FALL.ordinal()) {
						if (y.actionFlags[i][0]) {
							y.noDamageFallCheck = true;
						}
					} else if (i == Action.EMPFOOD.ordinal()) {
						if (y.actionFlags[i][0]) {
							y.foodCheck = true;
						}
					} else {
						if (y.actionFlags[i][j]) {
							y.bodyCheck = true;
						}
					}
				}
			}

			boolean brush = false;
			if (y.actionFlags[Action.CLEAN.ordinal()][0]
					|| y.actionFlags[Action.CLEAN.ordinal()][1]
					|| y.actionFlags[Action.CLEAN.ordinal()][2]) {
				brush = true;
			}

			if (y.actionFlags[Action.OKAZARI.ordinal()][0]
					|| y.actionFlags[Action.OKAZARI.ordinal()][1]
					|| y.actionFlags[Action.OKAZARI.ordinal()][2]) {
				brush = true;
			}

			if (y.actionFlags[Action.BODY_REMOVE.ordinal()][0]
					|| y.actionFlags[Action.BODY_REMOVE.ordinal()][1]
					|| y.actionFlags[Action.BODY_REMOVE.ordinal()][2]) {
				brush = true;
			}

			if (y.actionFlags[Action.BODY_OKAZARI.ordinal()][0]
					|| y.actionFlags[Action.BODY_OKAZARI.ordinal()][1]
					|| y.actionFlags[Action.BODY_OKAZARI.ordinal()][2]) {
				brush = true;
			}

			if (y.actionFlags[Action.SHIT.ordinal()][0]
					|| y.actionFlags[Action.SHIT.ordinal()][1]
					|| y.actionFlags[Action.SHIT.ordinal()][2]) {
				brush = true;
			}

			boolean spike = false;
			if (y.actionFlags[Action.KABI.ordinal()][0]
					|| y.actionFlags[Action.KABI.ordinal()][1]
					|| y.actionFlags[Action.KABI.ordinal()][2]) {
				spike = true;
			}

			if (y.actionFlags[Action.RUDE.ordinal()][0]
					|| y.actionFlags[Action.RUDE.ordinal()][1]
					|| y.actionFlags[Action.RUDE.ordinal()][2]) {
				spike = true;
			}

			if (y.actionFlags[Action.DESTROY.ordinal()][0]
					|| y.actionFlags[Action.DESTROY.ordinal()][1]
					|| y.actionFlags[Action.DESTROY.ordinal()][2]) {
				spike = true;
			}

			if (y.actionFlags[Action.ANTIRAPER.ordinal()][0]
					|| y.actionFlags[Action.ANTIRAPER.ordinal()][1]
					|| y.actionFlags[Action.ANTIRAPER.ordinal()][2]) {
				spike = true;
			}

			y.layerCount = 1;
			if (brush) {
				y.layerCount++;
			}
			if (spike) {
				y.layerCount++;
			}

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

	/**
	 * ButtonListener.
	 */
	public static class ButtonListener implements ActionListener {

		/** チェックボックスの全選択/全解除トグル処理。 */
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
	/** 他のゆんばのターゲットになっていない場合 true を返す。 */
	public boolean cheackOtherYunbaTarget(Entity o) {
		for (Map.Entry<Integer, Yunba> entry : GameWorld.get().getCurrentWorldState().getYunbas().entrySet()) {
			Yunba yunba = entry.getValue();
			if (yunba == this) {
				continue;
			}
			if (yunba.target == o) {
				return false;
			}
		}
		return true;
	}

	/** アイテムのランク（品質）を返す。 */
	public ItemRank getItemRank() {
		return itemRank;
	}

	/** アイテムのランク（品質）をセットする。 */
	public void setItemRank(ItemRank itemRank) {
		this.itemRank = itemRank;
	}

	/** ゆんばの色インデックスを返す。 */
	public int getColor() {
		return color;
	}

	/** ゆんばの色インデックスをセットする。 */
	public void setColor(int color) {
		this.color = color;
	}

	/** ゆんばの向きを返す。 */
	public int getDirection() {
		return direction;
	}

	/** ゆんばの向きをセットする。 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/** アクションフラグ（行動設定1）を返す。 */
	public boolean[][] getActionFlags() {
		return actionFlags;
	}

	/** アクションフラグ（行動設定1）をセットする。 */
	public void setActionFlags(boolean[][] actionFlags) {
		this.actionFlags = actionFlags;
	}

	/** アクションフラグ2（行動設定2）を返す。 */
	public boolean[][] getActionFlags2() {
		return actionFlags2;
	}

	/** アクションフラグ2（行動設定2）をセットする。 */
	public void setActionFlags2(boolean[][] actionFlags2) {
		this.actionFlags2 = actionFlags2;
	}

	/** アクションフラグ3（行動設定3）を返す。 */
	public boolean[][] getActionFlags3() {
		return actionFlags3;
	}

	/** アクションフラグ3（行動設定3）をセットする。 */
	public void setActionFlags3(boolean[][] actionFlags3) {
		this.actionFlags3 = actionFlags3;
	}

	/** ゆっくりをターゲット対象にするかを返す。 */
	public boolean isYukkuriCheck() {
		return bodyCheck;
	}

	/** ゆっくりをターゲット対象にするかをセットする。 */
	public void setYukkuriCheck(boolean bodyCheck) {
		this.bodyCheck = bodyCheck;
	}

	/** うんうんをターゲット対象にするかを返す。 */
	public boolean isShitCheck() {
		return shitCheck;
	}

	/** うんうんをターゲット対象にするかをセットする。 */
	public void setShitCheck(boolean shitCheck) {
		this.shitCheck = shitCheck;
	}

	/** 茎をターゲット対象にするかを返す。 */
	public boolean isStalkCheck() {
		return stalkCheck;
	}

	/** 茎をターゲット対象にするかをセットする。 */
	public void setStalkCheck(boolean stalkCheck) {
		this.stalkCheck = stalkCheck;
	}

	/** ノーランダム（乱数なし）モードかを返す。 */
	public boolean isNorndCheck() {
		return norndCheck;
	}

	/** ノーランダムモードをセットする。 */
	public void setNorndCheck(boolean norndCheck) {
		this.norndCheck = norndCheck;
	}

	/** 撃破フラグを返す。 */
	public boolean isKillCheck() {
		return killCheck;
	}

	/** 撃破フラグをセットする。 */
	public void setKillCheck(boolean killCheck) {
		this.killCheck = killCheck;
	}

	/** 身打ちチェックフラグを返す。 */
	public boolean isMineutiCheck() {
		return mineutiCheck;
	}

	/** 身打ちチェックフラグをセットする。 */
	public void setMineutiCheck(boolean mineutiCheck) {
		this.mineutiCheck = mineutiCheck;
	}

	/** 落下ダメージなしフラグを返す。 */
	public boolean isNoDamageFallCheck() {
		return noDamageFallCheck;
	}

	/** 落下ダメージなしフラグをセットする。 */
	public void setNoDamageFallCheck(boolean noDamageFallCheck) {
		this.noDamageFallCheck = noDamageFallCheck;
	}

	/** 食べ物をターゲット対象にするかを返す。 */
	public boolean isFoodCheck() {
		return foodCheck;
	}

	/** 食べ物をターゲット対象にするかをセットする。 */
	public void setFoodCheck(boolean foodCheck) {
		this.foodCheck = foodCheck;
	}

	/** 描画レイヤー配列を返す。 */
	public int[] getDrawLayer() {
		return drawLayer;
	}

	/** 描画レイヤー配列をセットする。 */
	public void setDrawLayer(int[] drawLayer) {
		this.drawLayer = drawLayer;
	}

	/** 使用レイヤー数を返す。 */
	public int getLayerCount() {
		return layerCount;
	}

	/** 使用レイヤー数をセットする。 */
	public void setLayerCount(int layerCount) {
		this.layerCount = layerCount;
	}

	/** 現在の行動（アクション種別）を返す。 */
	public Action getAction() {
		return action;
	}

	/** 現在の行動（アクション種別）をセットする。 */
	public void setAction(Action action) {
		this.action = action;
	}

	/** 現在の追跡・処理対象エンティティを返す。 */
	public Entity getTarget() {
		return target;
	}

	/** 現在の追跡・処理対象エンティティをセットする。 */
	public void setTarget(Entity target) {
		this.target = target;
	}

	/** 移動目標の X 座標を返す。 */
	public int getDestX() {
		return destX;
	}

	/** 移動目標の X 座標をセットする。 */
	public void setDestX(int destX) {
		this.destX = destX;
	}

	/** 移動目標の Y 座標を返す。 */
	public int getDestY() {
		return destY;
	}

	/** 移動目標の Y 座標をセットする。 */
	public void setDestY(int destY) {
		this.destY = destY;
	}

	/** 移動速度を返す。 */
	public int getSpeed() {
		return speed;
	}

	/** 移動速度をセットする。 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/** 初期配置の X 座標を返す。 */
	public int getDefaultX() {
		return defaultX;
	}

	/** 初期配置の X 座標をセットする。 */
	public void setDefaultX(int defaultX) {
		this.defaultX = defaultX;
	}

	/** 初期配置の Y 座標を返す。 */
	public int getDefaultY() {
		return defaultY;
	}

	/** 初期配置の Y 座標をセットする。 */
	public void setDefaultY(int defaultY) {
		this.defaultY = defaultY;
	}

}
