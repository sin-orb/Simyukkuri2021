package org.simyukkuri.entity.core.world.item;

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
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.Cash;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.GameView;
import org.simyukkuri.util.GameWorld;

/** 養殖プール */
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

		/** enum 名の文字列表現を返す。 */
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

	/** アイテム画像をレイヤー配列にセットし、使用レイヤー数を返す。 */
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

	/** アイテムの影画像を返す。 */
	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return null;
	}

	/** 境界線の取得 */
	public static Rectangle4y getBounding() {
		return boundary;
	}

	/** 衝突判定対象タイプを返す。 */
	@Override
	@Transient
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	/** 衝突処理を行い、結果コードを返す。 */
	@Override
	public int objHitProcess(Entity targetObject) {
		if (!enabled) {
			return 0;
		}
		if (targetObject.getObjType() == Type.YUKKURI) {
			Yukkuri body = (Yukkuri) targetObject;
			// 避妊されてたら妊娠しない
			if (body.isCastrated() && !stalkPool) {
				return 0;
			}
			if (body.isStalkCastration() && stalkPool) {
				return 0;
			}

			// 工業用専用の特殊処理
			if (option == 3 || option == 7) {
				// 一日ごとのコスト
				if (getAge() % 2400 == 0) {
					Cash.addCash(-getCost());
				}
				// 赤ゆには茎を実らせない
				if (stalkPool) {
					if (body.isBaby()) {
						return 0;
					}
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
					YukkuriType babyType;
					if (liquidYukkuriType == -1) {
						babyType = body.getType();
					} else if (!body.isHybrid() && liquidYukkuriType < 10000 && (GameRandom.nextInt(50) == 0)) {
						babyType = body.getHybridType(YukkuriType.fromTypeId(liquidYukkuriType));
					} else if (GameRandom.nextBoolean()) {
						babyType = YukkuriType.fromTypeId(liquidYukkuriType);
					} else {
						babyType = body.getType();
					}
					// ドスまりさはただのまりさに変換
					if (babyType == YukkuriType.DOSMARISA) {
						babyType = YukkuriType.MARISA;
					}
					if (babyType == YukkuriType.DEIBU) {
						babyType = YukkuriType.REIMU;
					}
					// 突然変異
					if ((babyType == YukkuriType.REIMU) && GameRandom.nextInt(20) == 0) {
						babyType = YukkuriType.WASAREIMU;
					} else if ((babyType == YukkuriType.WASAREIMU) && GameRandom.nextInt(20) != 0) {
						babyType = YukkuriType.REIMU;
					} else if ((babyType == YukkuriType.MARISA || babyType == YukkuriType.MARISAKOTATSUMURI)
							&& GameRandom.nextInt(20) == 0) {
						babyType = YukkuriType.MARISAKOTATSUMURI;
					} else if ((babyType == YukkuriType.MARISA || babyType == YukkuriType.MARISATSUMURI)
							&& GameRandom.nextInt(20) == 0) {
						babyType = YukkuriType.MARISAKOTATSUMURI;
					} else if ((babyType == YukkuriType.MARISATSUMURI || babyType == YukkuriType.MARISAKOTATSUMURI)
							&& GameRandom.nextInt(20) != 0) {
						babyType = YukkuriType.MARISA;
					} else if ((babyType == YukkuriType.KIMEEMARU) && GameRandom.nextInt(20) != 0) {
						babyType = YukkuriType.AYAYA;
					} else if ((babyType == YukkuriType.AYAYA) && GameRandom.nextInt(20) == 0) {
						babyType = YukkuriType.KIMEEMARU;
					}
					if (body.isSick() || body.isDamaged() || body.isOverPregnantLimit()
							|| (!highQuality && GameRandom.nextInt(500) == 0)) {
						if (GameRandom.nextBoolean()
								&& (babyType == YukkuriType.REIMU || babyType == YukkuriType.WASAREIMU)) {
							babyType = YukkuriType.TARINAIREIMU;
						} else {
							babyType = YukkuriType.TARINAI;
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
				liquidYukkuriType = body.getType().getTypeId();
				body.remove();
			}
		}
		return 0;
	}

	/** アイテムの購入価格を返す。 */
	@Override
	public int getValue() {
		return value[option];
	}

	/** アイテムの設置コストを返す。 */
	@Override
	public int getCost() {
		return cost[option];
	}

	/** ワールドからこのアイテムを除去する。 */
	@Override
	public void removeFromWorld() {
		GameWorld.get().getCurrentWorldState().getBreedingPools().remove(objId);
	}

	/** プール上のゆっくりを泣かせる処理 */
	public void cry(Yukkuri body) {
		if (body.hasBabyOrStalk()) {
			if (body.isNyd()) {
				body.setNydMessage(GameMessages.getMessage(body, MessagePool.Action.NonYukkuriDisease), false);
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

		GameWorld.get().getCurrentWorldState().getBreedingPools().put(objId, this);
		GameWorld.get().getCurrentWorldState().registerEntity(objId, this);

		objType = Type.PLATFORM;
		worldEntityType = WorldEntityKind.BREEDINGPOOL;

		interval = 1;

		boolean setupSucceeded = setupPool(this, false);
		if (!setupSucceeded) {
			GameWorld.get().getCurrentWorldState().getBreedingPools().remove(objId);
		}
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public BreedingPool() {

	}

	/** 繁殖プールの設定ダイアログを表示し、変更があれば true を返す。 */
	public static boolean setupPool(BreedingPool pool, boolean init) {

		JPanel mainPanel = new JPanel();
		JRadioButton[] buttons = new JRadioButton[PoolType.values().length];

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
			if (buttons[0].isSelected()) {
				// 廉価版
				pool.highQuality = false;
				pool.stalkPool = false;
				pool.lastSelected = 0;
				pool.option = 0;
			} else if (buttons[1].isSelected()) {
				// 通常版
				pool.highQuality = false;
				pool.stalkPool = false;
				pool.lastSelected = 1;
				pool.option = 1;
			} else if (buttons[2].isSelected()) {
				// プロ用
				pool.highQuality = true;
				pool.stalkPool = false;
				pool.lastSelected = 2;
				pool.option = 2;
			} else if (buttons[3].isSelected()) {
				// 工業用
				pool.highQuality = true;
				pool.stalkPool = false;
				pool.lastSelected = 3;
				pool.option = 3;
			} else if (buttons[4].isSelected()) {
				// 廉価版(茎)
				pool.highQuality = false;
				pool.stalkPool = true;
				pool.lastSelected = 4;
				pool.option = 4;
			} else if (buttons[5].isSelected()) {
				// 通常版(茎)
				pool.highQuality = false;
				pool.stalkPool = true;
				pool.lastSelected = 5;
				pool.option = 5;
			} else if (buttons[6].isSelected()) {
				// プロ用(茎)
				pool.highQuality = true;
				pool.stalkPool = true;
				pool.lastSelected = 6;
				pool.option = 6;
			} else if (buttons[7].isSelected()) {
				// 工業用(茎)
				pool.highQuality = true;
				pool.stalkPool = true;
				pool.lastSelected = 7;
				pool.option = 7;
			}
			return true;
		}
		return false;
	}

	/** 高品質モードかどうかを返す。 */
	public boolean isHighQuality() {
		return highQuality;
	}

	/** 高品質モードをセットする。 */
	public void setHighQuality(boolean highQuality) {
		this.highQuality = highQuality;
	}

	/** 茎プールモードかどうかを返す。 */
	public boolean isStalkPool() {
		return stalkPool;
	}

	/** 茎プールモードをセットする。 */
	public void setStalkPool(boolean stalkPool) {
		this.stalkPool = stalkPool;
	}

	/** 液状化ゆっくりの種別インデックスを返す。 */
	public int getLiquidYukkuriType() {
		return liquidYukkuriType;
	}

	/** 液状化ゆっくりの種別インデックスをセットする。 */
	public void setLiquidYukkuriType(int liquidYukkuriType) {
		this.liquidYukkuriType = liquidYukkuriType;
	}

	/** 最後に選択されたプールタイプのインデックスを返す。 */
	public int getLastSelected() {
		return lastSelected;
	}

	/** 最後に選択されたプールタイプのインデックスをセットする。 */
	public void setLastSelected(int lastSelected) {
		this.lastSelected = lastSelected;
	}

}
