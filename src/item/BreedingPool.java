package src.item;
import src.util.GameView;
import src.util.GameMessages;
import src.util.GameText;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import src.SimYukkuri;
import src.util.GameRandom;
import src.util.GameWorld;
import src.base.Yukkuri;
import src.base.Entity;
import src.base.WorldEntity;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.enums.Happiness;
import src.enums.WorldEntityKind;
import src.enums.Type;
import src.game.Dna;
import src.system.Cash;
import src.system.MessagePool;
import src.system.ResourceUtil;
import src.yukkuri.Ayaya;
import src.yukkuri.Deibu;
import src.yukkuri.DosMarisa;
import src.yukkuri.Kimeemaru;
import src.yukkuri.Marisa;
import src.yukkuri.MarisaKotatsumuri;
import src.yukkuri.MarisaTsumuri;
import src.yukkuri.Reimu;
import src.yukkuri.Tarinai;
import src.yukkuri.TarinaiReimu;
import src.yukkuri.WasaReimu;

/***************************************************
 * 養殖プール
 */
public class BreedingPool extends WorldEntity {

	private static final long serialVersionUID = -2544191380264314199L;

	/** 稼働タイプ */
	public static enum PoolType {
		LOW(GameText.read("item_cheap")),
		RAPID(GameText.read("item_normalbreed")),
		PRO(GameText.read("item_forpro")),
		INDUSTRY(GameText.read("item_indust")),
		LOWS(GameText.read("item_cheapstalk")),
		RAPIDS(GameText.read("item_normalstalk")),
		PROS(GameText.read("item_forprostalk")),
		INDUSTRYS(GameText.read("item_induststalk")),
		;

		private String name;

		PoolType(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/** 処理対象(ゆっくり) */
	public static final int hitCheckObjType = WorldEntity.YUKKURI;
	private static final int images_num = 4; // このクラスの総使用画像数
	private static BufferedImage[] images = new BufferedImage[images_num];
	private static Rectangle4y boundary = new Rectangle4y();

	private boolean highQuality;
	private boolean stalkPool;
	private static int[] value = { 1000, 5000, 50000, 450000, 1000, 5000, 50000, 600000 };
	private static int[] cost = { 10, 50, 50, 1500, 10, 50, 50, 1500 };
	/** プールの上で死亡して、精子餡に混ざった種類のDNA */
	public int liquidYukkuriType = -1;
	private int lastSelected = 0;

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		for (int i = 0; i < images_num; i++) {
			images[i] = ModLoader.loadItemImage(loader,
					"breedingpool" + File.separator + "breedingpool" + String.format("%03d", i + 1) + ".png");
		}
		boundary.setWidth(images[0].getWidth(io));
		boundary.setHeight(images[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() >> 1);
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (enabled) {
			if (liquidYukkuriType == 2) {
				layer[0] = images[2];
			} else if (liquidYukkuriType != -1) {
				layer[0] = images[1];
			} else {
				layer[0] = images[0];
			}
		} else {
			layer[0] = images[3];
		}
		return 1;
	}

	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return null;
	}

	/** 境界線の取得 */
	public static Rectangle4y getBounding() {
		return boundary;
	}

	@Override
	@Transient
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	@Override
	public int objHitProcess(Entity targetObject) {
		if (!enabled)
			return 0;
		if (targetObject.getObjType() == Type.YUKKURI) {
			Yukkuri body = (Yukkuri) targetObject;
			// 避妊されてたら妊娠しない
			if (body.isBodyCastration() && !stalkPool)
				return 0;
			if (body.isStalkCastration() && stalkPool)
				return 0;

			// 工業用専用の特殊処理
			if (option == 3 || option == 7) {
				// 一日ごとのコスト
				if (getAge() % 2400 == 0) {
					Cash.addCash(-getCost());
				}
				// 赤ゆには茎を実らせない
				if (stalkPool) {
					if (body.isBaby())
						return 0;
				}
				// ゆっくりが膨らんでる時の糞抜き
				if (body.isInfration()) {
					body.setShit(0, false);
					// 胎生妊娠プールの場合爆発寸前でストップ
					if (!stalkPool && body.isAboutToBurst()) {
						cry(body);
						return 0;
					}
				}
				// 母体の自動回復
				if (body.isDamaged()) {
					body.injectJuice();
				}
			}

			if (!body.isDead()
					&& (int) getAge() % (((highQuality == true) ? 5 : 10) * ((stalkPool == true) ? 2 : 1)) == 0) {
				// 赤ゆのDNA決定
				for (int i = 0; i < 5; i++) {
					int babyType;
					if (liquidYukkuriType == -1) {
						babyType = body.getType();
					} else if (!body.isHybrid() && liquidYukkuriType < 10000 && (GameRandom.nextInt(50) == 0)) {
						babyType = body.getHybridType(liquidYukkuriType);
					} else if (GameRandom.nextBoolean()) {
						babyType = liquidYukkuriType;
					} else {
						babyType = body.getType();
					}
					// ドスまりさはただのまりさに変換
					if (babyType == DosMarisa.type) {
						babyType = Marisa.type;
					}
					if (babyType == Deibu.type) {
						babyType = Reimu.type;
					}
					// 突然変異
					if ((babyType == Reimu.type) && GameRandom.nextInt(20) == 0) {
						babyType = WasaReimu.type;
					} else if ((babyType == WasaReimu.type) && GameRandom.nextInt(20) != 0) {
						babyType = Reimu.type;
					} else if ((babyType == Marisa.type || babyType == MarisaKotatsumuri.type)
							&& GameRandom.nextInt(20) == 0) {
						babyType = MarisaTsumuri.type;
					} else if ((babyType == Marisa.type || babyType == MarisaTsumuri.type)
							&& GameRandom.nextInt(20) == 0) {
						babyType = MarisaKotatsumuri.type;
					} else if ((babyType == MarisaTsumuri.type || babyType == MarisaKotatsumuri.type)
							&& GameRandom.nextInt(20) != 0) {
						babyType = Marisa.type;
					} else if ((babyType == Kimeemaru.type) && GameRandom.nextInt(20) != 0) {
						babyType = Ayaya.type;
					} else if ((babyType == Ayaya.type) && GameRandom.nextInt(20) == 0) {
						babyType = Kimeemaru.type;
					}
					if (body.isSick() || body.isDamaged() || body.isOverPregnantLimit()
							|| (!highQuality && GameRandom.nextInt(500) == 0)) {
						if (GameRandom.nextBoolean() && (babyType == Reimu.type || babyType == WasaReimu.type)) {
							babyType = TarinaiReimu.type;
						} else {
							babyType = Tarinai.type;
						}
					}
					// 実らせる
					if (stalkPool) {
						cry(body);
						body.setHappiness(Happiness.VERY_SAD);
						body.addStress(50);
						// p.addMemories(-10);
						body.getStalkBabyTypes()
								.add((GameRandom.nextBoolean() ? new Dna(babyType, null, null, false) : null));
						body.setHasStalk(true);
					} else {
						cry(body);
						body.setHappiness(Happiness.VERY_SAD);
						body.getBabyTypes().add(new Dna(babyType, null, null, false));
						body.setHasBaby(true);
						body.addStress(50);
						// p.addMemories(-10);
						break;
					}
				}
				body.subtractPregnantLimit();
				// 廉価版では成長促進効果なし
				if (option != 0 && option != 4) {
					body.rapidPregnantPeriod();
				}
				// 一匹一匹でコストになるのは工業用以外
				if (option != 3 && option != 7) {
					Cash.addCash(-getCost());
				}
			} else if (body.isDead() && liquidYukkuriType == -1 && body.isCrushed()) {
				liquidYukkuriType = body.getType();
				body.remove();
			}
		}
		return 0;
	}

	@Override
	public int getValue() {
		return value[option];
	}

	@Override
	public int getCost() {
		return cost[option];
	}

	@Override
	public void removeListData() {
		GameWorld.get().getCurrentMap().getBreedingPool().remove(objId);
	}

	/** プール上のゆっくりを泣かせる処理 */
	public void cry(Yukkuri body) {
		if (body.hasBabyOrStalk()) {
			if (body.isNYD()) {
				body.setNYDMessage(GameMessages.getMessage(body, MessagePool.Action.NonYukkuriDisease), false);
			} else if (GameRandom.nextInt(40) == 0) {
				body.setPikoMessage(GameMessages.getMessage(body, MessagePool.Action.PoolSukkiri), true);
			} else {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.PoolSukkiri));
			}
		}
	}

	/** コンストラクタ */
	public BreedingPool(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());

		GameWorld.get().getCurrentMap().getBreedingPool().put(objId, this);

		objType = Type.PLATFORM;
		worldEntityType = WorldEntityKind.BREEDINGPOOL;

		interval = 1;

		boolean setupSucceeded = setupPool(this, false);
		if (!setupSucceeded) {
			GameWorld.get().getCurrentMap().getBreedingPool().remove(objId);
		}
	}

	public BreedingPool() {

	}

	// 設定メニュー
	public static boolean setupPool(BreedingPool pool, boolean init) {

		JPanel mainPanel = new JPanel();
		JRadioButton[] buttons = new JRadioButton[PoolType.values().length];
		boolean setupSucceeded = false;

		mainPanel.setLayout(new GridLayout(4, 2));
		mainPanel.setPreferredSize(new Dimension(350, 150));
		ButtonGroup buttonGroup = new ButtonGroup();

		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new JRadioButton(PoolType.values()[i].toString());
			buttonGroup.add(buttons[i]);

			mainPanel.add(buttons[i]);
		}

		if (!init) {
			buttons[0].setSelected(true);
		} else {
			buttons[pool.lastSelected].setSelected(true);
		}
		int dialogResult = JOptionPane.showConfirmDialog(GameView.getDialogParent(), mainPanel,
				GameText.read("item_poolsettings"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (dialogResult == JOptionPane.OK_OPTION) {
			// 廉価版
			if (buttons[0].isSelected()) {
				pool.highQuality = false;
				// o.rapidGrowth = false;
				pool.stalkPool = false;
				// o.industrial = false;
				pool.lastSelected = 0;
				pool.option = 0;
			}
			// 通常版
			else if (buttons[1].isSelected()) {
				pool.highQuality = false;
				// o.rapidGrowth = true;
				pool.stalkPool = false;
				// o.industrial = false;
				pool.lastSelected = 1;
				pool.option = 1;
			}
			// プロ用
			else if (buttons[2].isSelected()) {
				pool.highQuality = true;
				// o.rapidGrowth = true;
				pool.stalkPool = false;
				// o.industrial = false;
				pool.lastSelected = 2;
				pool.option = 2;
			}
			// 工業用
			else if (buttons[3].isSelected()) {
				pool.highQuality = true;
				// o.rapidGrowth = true;
				pool.stalkPool = false;
				// o.industrial = true;
				pool.lastSelected = 3;
				pool.option = 3;
			}
			// 廉価版(茎)
			else if (buttons[4].isSelected()) {
				pool.highQuality = false;
				// o.rapidGrowth = false;
				pool.stalkPool = true;
				// o.industrial = false;
				pool.lastSelected = 4;
				pool.option = 4;
			}
			// 通常版(茎)
			else if (buttons[5].isSelected()) {
				pool.highQuality = false;
				// o.rapidGrowth = true;
				pool.stalkPool = true;
				// o.industrial = false;
				pool.lastSelected = 5;
				pool.option = 5;
			}
			// プロ用(茎)
			else if (buttons[6].isSelected()) {
				pool.highQuality = true;
				// o.rapidGrowth = true;
				pool.stalkPool = true;
				// o.industrial = false;
				pool.lastSelected = 6;
				pool.option = 6;
			}
			// 工業用(茎)
			else if (buttons[7].isSelected()) {
				pool.highQuality = true;
				// o.rapidGrowth = true;
				pool.stalkPool = true;
				// o.industrial = true;
				pool.lastSelected = 7;
				pool.option = 7;
			}
			setupSucceeded = true;
		}
		return setupSucceeded;
	}

	public boolean isHighQuality() {
		return highQuality;
	}

	public void setHighQuality(boolean highQuality) {
		this.highQuality = highQuality;
	}

	public boolean isStalkPool() {
		return stalkPool;
	}

	public void setStalkPool(boolean stalkPool) {
		this.stalkPool = stalkPool;
	}

	public int getLiquidYukkuriType() {
		return liquidYukkuriType;
	}

	public void setLiquidYukkuriType(int liquidYukkuriType) {
		this.liquidYukkuriType = liquidYukkuriType;
	}

	public int getLastSelected() {
		return lastSelected;
	}

	public void setLastSelected(int lastSelected) {
		this.lastSelected = lastSelected;
	}

}
