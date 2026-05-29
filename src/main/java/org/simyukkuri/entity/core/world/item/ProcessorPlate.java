package org.simyukkuri.entity.core.world.item;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.impl.Fire;
import org.simyukkuri.entity.core.effect.Effect;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.EffectType;
import org.simyukkuri.enums.FootBake;
import org.simyukkuri.enums.HairState;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.system.Cash;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.GameView;
import org.simyukkuri.util.GameWorld;

/**
 * 加工プレート.
 */
public class ProcessorPlate extends WorldEntity {
	private static final long serialVersionUID = -32909400197144018L;
	/** 処理対象(ゆっくり) */
	public static final int hitCheckObjType = WorldEntity.YUKKURI;
	private static BufferedImage[] imageLayers = new BufferedImage[2];
	private static Rectangle4y boundary = new Rectangle4y();

	/** 加工対象のリスト */
	protected List<Yukkuri> activeBodies = new LinkedList<Yukkuri>();
	/** 加工エフェクトのリスト */
	protected List<Effect> activeEffects = new LinkedList<Effect>();
	/** 加工するタイプ */
	protected ProcessType processType;
	/** ランニングコスト */
	protected int[] runningCost = { 500, 800, 2000, 3500 };

	/** 加工モード */
	public static enum ProcessMode {
		HOTPLATE, // ホットプレート
		PAIN, // 痛い
		BAIBAI_OKAZARI, // お飾り自動削除
		PEALING, // 皮むき
		BLINDING, // 目潰し
		ACCELERATE, // 成長加速
		SHUTMOUTH, // 口封じ
		PLUCKING, // むしる
		PACKING // パッキング
	}

	/** 加工モード(詳細) */
	public static enum ProcessType {
		HOTPLATE_MIN(GameText.read("item_hotplatemin"), ProcessMode.HOTPLATE, 50),
		HOTPLATE_LOW(GameText.read("item_hotplatelow"), ProcessMode.HOTPLATE, 500),
		HOTPLATE_MIDDLE(GameText.read("item_hotplatemiddle"), ProcessMode.HOTPLATE, 1000),
		HOTPLATE_HIGH(GameText.read("item_hotplatehigh"), ProcessMode.HOTPLATE, 5000),
		HOTPLATE_MAX(GameText.read("item_hotplatemax"), ProcessMode.HOTPLATE, 20000),
		PAIN(GameText.read("item_hotplatepain"), ProcessMode.PAIN, 1),
		BAIBAI_OKAZARI_WITH_FIRE(GameText.read("item_autoremoval"), ProcessMode.BAIBAI_OKAZARI, 1),
		PEALING(GameText.read("command_peal"), ProcessMode.PEALING, 1),
		BLINDING(GameText.read("command_eyeball"), ProcessMode.BLINDING, 1),
		ACCELERATE(GameText.read("item_hotplateaccel"), ProcessMode.ACCELERATE, 1),
		SHUTMOUTH(GameText.read("command_mouthshut"), ProcessMode.SHUTMOUTH, 1),
		PLUCKING(GameText.read("command_manju"), ProcessMode.PLUCKING, 1),
		PACKING(GameText.read("item_hotplatepack"), ProcessMode.PACKING, 1);

		private String name;
		private ProcessMode mode;
		private int parameter;

		ProcessType(String name, ProcessMode mode, int parameter) {
			this.name = name;
			this.mode = mode;
			this.parameter = parameter;
		}

		/** プロセスタイプ名の文字列表現を返す。 */
		public String toString() {
			return name;
		}
	}

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		imageLayers[0] = ModLoader.loadItemImage(loader, "ProcessorPlate" + File.separator + "ProcessorPlate.png");
		imageLayers[1] = ModLoader.loadItemImage(loader, "ProcessorPlate" + File.separator + "ProcessorPlate_off.png");
		boundary.setWidth(imageLayers[0].getWidth(io));
		boundary.setHeight(imageLayers[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() >> 1);
	}

	/** アイテム画像をレイヤー配列にセットし、使用レイヤー数を返す。 */
	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (enabled) {
			layer[0] = imageLayers[0];
		} else {
			layer[0] = imageLayers[1];
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

	/**
	 * Enable hit check.
	 *
	 * @return Enable hit check.
	 */
	@Override
	public boolean enableHitCheck() {
		return true;
	}

	/**
	 * 当たり判定されるオブジェクトのチェック
	 * <br>
	 * 動作はobjHitProcess( Entity o )で
	 * <br>
	 * これは例外的に、エフェクトを外す作業もここでやってる
	 */
	public boolean checkHitObj(Rectangle collisionRect, Entity targetObject) {
		if (targetObject.getZ() == 0) {
			Translate.translate(targetObject.getX(), targetObject.getY(), tmpPos);
			if (collisionRect.contains(new java.awt.Point(tmpPos.getX(), tmpPos.getY()))) {
				objHitProcess(targetObject);
				return true;
			} else {
				if (targetObject != null && activeBodies.contains(targetObject)) {
					if (targetObject instanceof Yukkuri) {
						Yukkuri targetBody = (Yukkuri) targetObject;
						int activeIndex = activeBodies.indexOf(targetBody);
						Effect effect = activeEffects.get(activeIndex);
						if (effect != null) {
							effect.remove();
						}
						targetBody.setForceFace(-1);
						targetBody.setShadowVisible(true);
						targetBody.setLockmove(false);
						targetBody.setForcePanicClear();
						activeBodies.remove(targetBody);
						activeEffects.remove(effect);
					}
				}
			}
		}
		return false;
	}

	/**
	 * 衝突処理を行い、結果コードを返す。
	 *
	 * @param targetObject target object.
	 * @return result code.
	 */
	@Override
	public int objHitProcess(Entity targetObject) {
		if (!enabled) {
			return 0;
		}
		if (targetObject == null) {
			return 0;
		}
		if (targetObject instanceof Yukkuri) {
			Yukkuri targetBody = (Yukkuri) targetObject;
			// 切断されていたら何も起きない
			if (targetBody.getCriticalDamageType() == CriticalDamageType.CUT) {
				return 0;
			}
			// 初回
			if (!activeBodies.contains(targetBody)) {
				Effect effect;
				switch (processType.mode) {
					case HOTPLATE:
						effect = GameView.addEffect(EffectType.BAKED, targetBody.getX(),
								targetBody.getY() + 1,
								-2, 0, 0, 0, false, -1, -1, false, false, false);
						break;
					default:
						// 空エフェクト
						effect = null;
						break;
				}
				activeBodies.add(targetBody);
				activeEffects.add(effect);
			}
		}
		return 1;
	}

	/** 毎ティックの状態更新を行う。 */
	@Override
	public void upDate() {
		if (!enabled) {
			if (activeBodies == null || activeEffects == null) {
				return;
			}
			for (int i = activeBodies.size() - 1; 0 <= i; i--) {
				Yukkuri targetBody = activeBodies.get(i);
				Effect effect = activeEffects.get(i);
				if (effect != null) {
					effect.remove();
				}
				targetBody.setForceFace(-1);
				targetBody.setShadowVisible(true);
				activeBodies.remove(i);
				activeEffects.remove(i);
			}
			return;
		}

		if (getAge() % 2400 == 0) {
			Cash.addCash(-getCost());
		}
		if (activeBodies == null || activeEffects == null) {
			return;
		}
		for (int i = activeBodies.size() - 1; 0 <= i; i--) {
			Yukkuri targetBody = activeBodies.get(i);
			Effect effect = activeEffects.get(i);

			// 対象がいないor除去されたor飛んでいるときを除外
			if (targetBody == null || targetBody.isRemoved() || targetBody.getZ() >= 10) {
				if (effect != null) {
					effect.remove();
				}
				targetBody.setForceFace(-1);
				targetBody.setShadowVisible(true);
				activeBodies.remove(i);
				activeEffects.remove(i);
				continue;
			}
			if (effect != null) {
				effect.setCalcX(targetBody.getX());
				effect.setCalcY(targetBody.getY() + 2);
			}
			targetBody.clearActions();
			targetBody.setShadowVisible(false);
			switch (processType.mode) {
				case HOTPLATE:
					if (!targetBody.isDead()) {
						if (targetBody.isSleeping()) {
							targetBody.wakeup();
						}
						if (processType != ProcessType.HOTPLATE_MIDDLE
								|| targetBody.getFootBakeLevel() != FootBake.MEDIUM) {
							// 中火の場合は完全に足を焼かない
							targetBody.addFootBakePeriod(processType.parameter);
						}
						targetBody.addDamage(20);
						if (targetBody.isPealed()) {
							targetBody.addStress(400);
						} else {
							targetBody.addStress(40);
						}
						if (targetBody.isNotNyd()) {
							targetBody.setHappiness(Happiness.VERY_SAD);
							targetBody.setForceFace(ImageCode.PAIN.ordinal());
							if (GameRandom.nextInt(10) == 0) {
								targetBody.setMessage(GameMessages.getMessage(targetBody, MessagePool.Action.Burning),
										40,
										true, true);
							}
						}
					}
					break;
				case PAIN:
					if (!targetBody.isDead()) {
						if (targetBody.isSleeping()) {
							targetBody.wakeup();
						}
						targetBody.addDamage(5);
						if (targetBody.isPealed()) {
							targetBody.addStress(400);
						} else {
							targetBody.addStress(30);
						}
						if (targetBody.isNotNyd()) {
							targetBody.setHappiness(Happiness.VERY_SAD);
							targetBody.setForceFace(ImageCode.PAIN.ordinal());
							if (GameRandom.nextInt(15) == 0) {
								targetBody.setMessage(GameMessages.getMessage(targetBody, MessagePool.Action.Scream),
										40, true,
										true);
							}
						}
					}
					break;
				case BAIBAI_OKAZARI:// お飾り除去（燃やす）
					// 潰れていたらそのまま流す
					if (targetBody.isCrushed()) {
						break;
					}
					if (targetBody.hasOkazari()) {
						if (targetBody.getAttachmentSize(Fire.class) == 0) {
							targetBody.setForcePanicClear();
							targetBody.giveFire();
						}
						targetBody.setLockmove(true);
						if (!targetBody.isDead()) {
							// 死にそうなら回復する
							if (targetBody.getDamage() >= targetBody.getDamageLimit() * 60 / 100) {
								targetBody.addDamage(-TICK * 100);
							}
						}
					} else {
						if (targetBody.getAttachmentSize(Fire.class) != 0) {
							targetBody.removeAttachment(Fire.class);
							targetBody.setForcePanicClear();
						}
					}
					break;
				case PEALING:
					// 潰れ、加工済み除外
					if (targetBody.isCrushed() || targetBody.isPealed()) {
						break;
					}
					// ゲームバランス調整用。お飾り付、おさげ付の個体は処理しない
					if (targetBody.hasOkazari() || (targetBody.hasBraidCheck() && targetBody.isBraidType())) {
						break;
					}
					if (targetBody.isSleeping()) {
						targetBody.wakeup();
					}
					targetBody.cutHair();
					targetBody.peal();
					if (!targetBody.isDead()) {
						// 死にそうなら回復する
						if (targetBody.isDamagedHeavily()) {
							targetBody.addDamage(-TICK * 100);
						}
					}
					break;
				case BLINDING:
					// 潰れ、加工済み除外
					if (targetBody.isCrushed() || targetBody.isBlind()) {
						break;
					}
					if (targetBody.isSleeping()) {
						targetBody.wakeup();
					}
					targetBody.breakeyes();
					break;
				case ACCELERATE:
					// 潰れ、死体の除外
					if (targetBody.isCrushed() || targetBody.isDead()) {
						break;
					}
					if (targetBody.isSleeping()) {
						targetBody.wakeup();
					}
					// 赤、子ゆのみ
					if (!targetBody.isAdult()) {
						targetBody.setHappiness(Happiness.VERY_SAD);
						targetBody.setForceFace(ImageCode.PAIN.ordinal());
						targetBody.addAge(TICK * 1000);
						targetBody.setMessage(GameMessages.getMessage(targetBody, MessagePool.Action.Inflation), 40,
								false,
								true);
					}
					break;
				case SHUTMOUTH:
					if (targetBody.isCrushed() || targetBody.isPealed()) {
						break;
					}
					if (!targetBody.isShutmouth()) {
						if (targetBody.isSleeping()) {
							targetBody.wakeup();
						}
						targetBody.setMessage(GameMessages.getMessage(targetBody, MessagePool.Action.CantTalk), 40,
								true,
								true);
						targetBody.setHappiness(Happiness.SAD);
						targetBody.setShutmouth(true);
					}
					break;
				case PLUCKING:
					// 潰れ、加工済み除外
					if (targetBody.isCrushed() || targetBody.getHairState() == HairState.BALDHEAD) {
						break;
					}
					// ゲームバランス調整用。お飾り付は処理しない
					if (targetBody.hasOkazari()) {
						break;
					}
					if (targetBody.isSleeping()) {
						targetBody.wakeup();
					}
					if (targetBody.hasBraidCheck()) {
						targetBody.takeBraid();
					}
					targetBody.setHappiness(Happiness.VERY_SAD);
					targetBody.setForceFace(ImageCode.PAIN.ordinal());
					if (GameRandom.nextInt(3) == 0) {
						targetBody.setMessage(GameMessages.getMessage(targetBody, MessagePool.Action.Scream), 40, true,
								true);
					} else {
						targetBody.setMessage(GameMessages.getMessage(targetBody, MessagePool.Action.PLUNCKING), 40,
								true,
								true);
					}
					targetBody.cutHair();
					break;
				case PACKING:
					// 潰れ、死体、加工済み除外
					if (targetBody.isCrushed() || targetBody.isDead() || targetBody.isPacked()) {
						break;
					}
					// ゲームバランス調整用。お飾り付、目有、髪付き、おさげ付、口未加工は処理しない
					if (targetBody.hasOkazari() || !targetBody.isBlind()
							|| targetBody.getHairState() != HairState.BALDHEAD
							|| (targetBody.hasBraidCheck() && targetBody.isBraidType()) || !targetBody.isShutmouth()) {
						break;
					}
					targetBody.pack();
					break;
				default:
					break;
			}
		}
	}

	/**
	 * アイテムの設置コストを返す。
	 *
	 * @return 設置コスト。
	 */
	@Override
	@Transient
	public int getCost() {
		switch (processType.mode) {
			case PAIN:
				return runningCost[0];
			case HOTPLATE:
			case ACCELERATE:
				return runningCost[1];
			case BAIBAI_OKAZARI:// お飾り除去（燃やす）
			case BLINDING:
			case SHUTMOUTH:
				return runningCost[2];
			case PEALING:
			case PLUCKING:
			case PACKING:
				return runningCost[3];
			default:
				return 0;
		}
	}

	/** ワールドからこのアイテムを除去する。 */
	@Override
	public void removeFromWorld() {
		if (activeBodies != null && activeEffects != null) {
			for (int i = activeBodies.size() - 1; 0 <= i; i--) {
				Yukkuri targetBody = activeBodies.get(i);
				targetBody.setForceFace(-1);
				targetBody.setLockmove(false);
				Effect effect = activeEffects.get(i);
				if (effect != null) {
					effect.remove();
				}
			}
			activeBodies.clear();
			activeEffects.clear();
		}
		GameWorld.get().getCurrentWorldState().getProcessorPlates().remove(objId);
	}

	/** 設定メニュー */
	public static boolean setupProcessorPlate(ProcessorPlate plate) {

		JPanel mainPanel = new JPanel();
		JRadioButton[] buttons = new JRadioButton[ProcessType.values().length];

		mainPanel.setLayout(new GridLayout(7, 1));
		mainPanel.setPreferredSize(new Dimension(150, 100));
		ButtonGroup buttonGroup = new ButtonGroup();

		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new JRadioButton(ProcessType.values()[i].name);
			buttonGroup.add(buttons[i]);

			mainPanel.add(buttons[i]);
		}

		buttons[0].setSelected(true);

		int dialogResult = JOptionPane.showConfirmDialog(GameView.getDialogParent(), mainPanel, "加工設定",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (dialogResult == JOptionPane.OK_OPTION) {
			if (buttons[0].isSelected()) {
				plate.processType = ProcessType.HOTPLATE_MIN;
			}
			if (buttons[1].isSelected()) {
				plate.processType = ProcessType.HOTPLATE_LOW;
			}
			if (buttons[2].isSelected()) {
				plate.processType = ProcessType.HOTPLATE_MIDDLE;
			}
			if (buttons[3].isSelected()) {
				plate.processType = ProcessType.HOTPLATE_HIGH;
			}
			if (buttons[4].isSelected()) {
				plate.processType = ProcessType.HOTPLATE_MAX;
			}
			if (buttons[5].isSelected()) {
				plate.processType = ProcessType.PAIN;
			}
			if (buttons[6].isSelected()) {
				plate.processType = ProcessType.BAIBAI_OKAZARI_WITH_FIRE;
			}
			if (buttons[7].isSelected()) {
				plate.processType = ProcessType.PEALING;
			}
			if (buttons[8].isSelected()) {
				plate.processType = ProcessType.BLINDING;
			}
			if (buttons[9].isSelected()) {
				plate.processType = ProcessType.ACCELERATE;
			}
			if (buttons[10].isSelected()) {
				plate.processType = ProcessType.SHUTMOUTH;
			}
			if (buttons[11].isSelected()) {
				plate.processType = ProcessType.PLUCKING;
			}
			if (buttons[12].isSelected()) {
				plate.processType = ProcessType.PACKING;
			}
			return true;
		}
		return false;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param initX      X座標
	 * @param initY      Y座標
	 * @param initOption 未使用
	 */
	public ProcessorPlate(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		if (GameWorld.get() != null) {
			GameWorld.get().getCurrentWorldState().getProcessorPlates().put(objId, this);
			GameWorld.get().getCurrentWorldState().registerEntity(objId, this);
		}
		// objType = Type.PLATFORM;
		worldEntityType = WorldEntityKind.PROCESSORPLATE;
		interval = 5;
		value = 250000;
		readIniFile();
		boolean setupSucceeded = setupProcessorPlate(this);
		if (!setupSucceeded) {
			GameWorld.get().getCurrentWorldState().getProcessorPlates().remove(objId);
		}
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public ProcessorPlate() {

	}

	/** iniファイル読み込み */
	public void readIniFile() {
		ClassLoader loader = this.getClass().getClassLoader();
		int iniValue = 0;
		// 自動お仕置きプレートコスト
		iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataItemIniDir(), "ProcessorPlate",
				"MachineCost");
		runningCost[0] = iniValue;
		// 軽加工プレートコスト
		iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataItemIniDir(), "ProcessorPlate",
				"LightProcessCost");
		runningCost[1] = iniValue;
		// 中加工プレートコスト
		iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataItemIniDir(), "ProcessorPlate",
				"MidiumProcessCost");
		runningCost[2] = iniValue;
		// 重加工プレートコスト
		iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataItemIniDir(), "ProcessorPlate",
				"HeavyProcessCost");
		runningCost[3] = iniValue;
	}

	/** 現在プレート上で処理中のゆっくりリストを返す。 */
	public List<Yukkuri> getActiveBodies() {
		return activeBodies;
	}

	/** 処理中のゆっくりリストをセットする。 */
	public void setActiveBodies(List<Yukkuri> activeBodies) {
		this.activeBodies = activeBodies;
	}

	/** 現在プレートに適用中のエフェクトリストを返す。 */
	public List<Effect> getActiveEffects() {
		return activeEffects;
	}

	/** 適用中のエフェクトリストをセットする。 */
	public void setActiveEffects(List<Effect> activeEffects) {
		this.activeEffects = activeEffects;
	}

	/** プレートの処理種別（加工タイプ）を返す。 */
	public ProcessType getEnumProcessType() {
		return processType;
	}

	/** プレートの処理種別をセットする。 */
	public void setEnumProcessType(ProcessType enumProcessType) {
		this.processType = enumProcessType;
	}

	/** 各処理タイプの稼働コスト配列を返す。 */
	public int[] getRunningCost() {
		return runningCost;
	}

	/** 稼働コスト配列をセットする。 */
	public void setRunningCost(int[] runningCost) {
		this.runningCost = runningCost;
	}
}
