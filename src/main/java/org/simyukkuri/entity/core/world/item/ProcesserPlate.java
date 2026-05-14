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

import org.simyukkuri.draw.ModLoader;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.impl.Fire;
import org.simyukkuri.entity.core.effect.Effect;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.enums.CriticalDamegeType;
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

/***************************************************
 * 加工プレート
 */
public class ProcesserPlate extends WorldEntity {
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
	protected int runningCost[] = { 500, 800, 2000, 3500 };

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
	}

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		imageLayers[0] = ModLoader.loadItemImage(loader, "ProcesserPlate" + File.separator + "ProcesserPlate.png");
		imageLayers[1] = ModLoader.loadItemImage(loader, "ProcesserPlate" + File.separator + "ProcesserPlate_off.png");
		boundary.setWidth(imageLayers[0].getWidth(io));
		boundary.setHeight(imageLayers[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() >> 1);
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (enabled) {
			layer[0] = imageLayers[0];
		} else {
			layer[0] = imageLayers[1];
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

	@Override
	public int objHitProcess(Entity targetObject) {
		if (!enabled)
			return 0;
		if (targetObject == null)
			return 0;
		if (targetObject instanceof Yukkuri) {
			Yukkuri targetBody = (Yukkuri) targetObject;
			// 切断されていたら何も起きない
			if (targetBody.getCriticalDamegeType() == CriticalDamegeType.CUT)
				return 0;
			// 初回
			if (!activeBodies.contains(targetBody)) {
				Effect effect;
				switch (processType.mode) {
					case HOTPLATE:
						effect = GameView.addEffect(EffectType.BAKE, targetBody.getX(),
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
						if (targetBody.isSleeping())
							targetBody.wakeup();
						if (processType != ProcessType.HOTPLATE_MIDDLE
								|| targetBody.getFootBakeLevel() != FootBake.MIDIUM) {
							// 中火の場合は完全に足を焼かない
							targetBody.addFootBakePeriod(processType.parameter);
						}
						targetBody.addDamage(20);
						if (targetBody.isPealed())
							targetBody.addStress(400);
						else
							targetBody.addStress(40);
						if (targetBody.isNotNYD()) {
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
						if (targetBody.isSleeping())
							targetBody.wakeup();
						targetBody.addDamage(5);
						if (targetBody.isPealed())
							targetBody.addStress(400);
						else
							targetBody.addStress(30);
						if (targetBody.isNotNYD()) {
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
					if (targetBody.isSleeping())
						targetBody.wakeup();
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
					if (targetBody.isSleeping())
						targetBody.wakeup();
					targetBody.breakeyes();
					break;
				case ACCELERATE:
					// 潰れ、死体の除外
					if (targetBody.isCrushed() || targetBody.isDead()) {
						break;
					}
					if (targetBody.isSleeping())
						targetBody.wakeup();
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
						if (targetBody.isSleeping())
							targetBody.wakeup();
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
					if (targetBody.isSleeping())
						targetBody.wakeup();
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
		}
		return 0;
	}

	@Override
	public void removeListData() {
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
		GameWorld.get().getCurrentMap().getProcesserPlate().remove(objId);
	}

	/** 設定メニュー */
	public static boolean setupProcesserPlate(ProcesserPlate plate) {

		JPanel mainPanel = new JPanel();
		JRadioButton[] buttons = new JRadioButton[ProcessType.values().length];
		boolean setupSucceeded = false;

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
			if (buttons[0].isSelected())
				plate.processType = ProcessType.HOTPLATE_MIN;
			if (buttons[1].isSelected())
				plate.processType = ProcessType.HOTPLATE_LOW;
			if (buttons[2].isSelected())
				plate.processType = ProcessType.HOTPLATE_MIDDLE;
			if (buttons[3].isSelected())
				plate.processType = ProcessType.HOTPLATE_HIGH;
			if (buttons[4].isSelected())
				plate.processType = ProcessType.HOTPLATE_MAX;
			if (buttons[5].isSelected())
				plate.processType = ProcessType.PAIN;
			if (buttons[6].isSelected())
				plate.processType = ProcessType.BAIBAI_OKAZARI_WITH_FIRE;
			if (buttons[7].isSelected())
				plate.processType = ProcessType.PEALING;
			if (buttons[8].isSelected())
				plate.processType = ProcessType.BLINDING;
			if (buttons[9].isSelected())
				plate.processType = ProcessType.ACCELERATE;
			if (buttons[10].isSelected())
				plate.processType = ProcessType.SHUTMOUTH;
			if (buttons[11].isSelected())
				plate.processType = ProcessType.PLUCKING;
			if (buttons[12].isSelected())
				plate.processType = ProcessType.PACKING;
			setupSucceeded = true;
		}
		return setupSucceeded;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param initX      X座標
	 * @param initY      Y座標
	 * @param initOption 未使用
	 */
	public ProcesserPlate(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		GameWorld.get().getCurrentMap().getProcesserPlate().put(objId, this);
		// objType = Type.PLATFORM;
		worldEntityType = WorldEntityKind.PROCESSERPLATE;
		interval = 5;
		value = 250000;
		readIniFile();
		boolean setupSucceeded = setupProcesserPlate(this);
		if (!setupSucceeded) {
			GameWorld.get().getCurrentMap().getProcesserPlate().remove(objId);
		}
	}

	public ProcesserPlate() {

	}

	/** iniファイル読み込み */
	public void readIniFile() {
		ClassLoader loader = this.getClass().getClassLoader();
		int iniValue = 0;
		// 自動お仕置きプレートコスト
		iniValue = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataItemIniDir(), "ProcesserPlate",
				"MachineCost");
		runningCost[0] = iniValue;
		// 軽加工プレートコスト
		iniValue = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataItemIniDir(), "ProcesserPlate",
				"LightProcessCost");
		runningCost[1] = iniValue;
		// 中加工プレートコスト
		iniValue = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataItemIniDir(), "ProcesserPlate",
				"MidiumProcessCost");
		runningCost[2] = iniValue;
		// 重加工プレートコスト
		iniValue = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataItemIniDir(), "ProcesserPlate",
				"HeavyProcessCost");
		runningCost[3] = iniValue;
	}

	public List<Yukkuri> getProcessedBodyList() {
		return activeBodies;
	}

	public void setProcessedBodyList(List<Yukkuri> processedBodyList) {
		this.activeBodies = processedBodyList;
	}

	public List<Effect> getProcessedBodyEffectList() {
		return activeEffects;
	}

	public void setProcessedBodyEffectList(List<Effect> processedBodyEffectList) {
		this.activeEffects = processedBodyEffectList;
	}

	public ProcessType getEnumProcessType() {
		return processType;
	}

	public void setEnumProcessType(ProcessType enumProcessType) {
		this.processType = enumProcessType;
	}

	public int[] getRunningCost() {
		return runningCost;
	}

	public void setRunningCost(int[] runningCost) {
		this.runningCost = runningCost;
	}
}
