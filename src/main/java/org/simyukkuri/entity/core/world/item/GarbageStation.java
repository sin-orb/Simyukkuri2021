package org.simyukkuri.entity.core.world.item;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.simyukkuri.command.GadgetAction;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.entity.core.world.item.Food.FoodType;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.GameView;
import org.simyukkuri.util.GameWorld;

/**
 * ゴミ捨て場
 */
public class GarbageStation extends WorldEntity {

	private static final long serialVersionUID = -3307339525828240055L;

	/** 出す餌の種類テーブル */
	public static enum GomiType {
		WASTE(GameText.read("command_food_garbage"), FoodType.WASTE_NORA),
		BITTER(GameText.read("command_food_bitter"), FoodType.BITTER_NORA),
		HOT(GameText.read("command_food_hot"), FoodType.HOT_NORA),
		LEMON_POP(GameText.read("command_food_ramune"), FoodType.LEMONPOP_NORA),
		VIYUGRA(GameText.read("command_food_viagra"), FoodType.VIYUGRA_NORA),
		NORMAL(GameText.read("command_food_normal"), FoodType.FOOD_NORA),
		SWEETS1(GameText.read("command_food_sweet1"), FoodType.SWEETS_NORA1),
		SWEETS2(GameText.read("command_food_sweet2"), FoodType.SWEETS_NORA2),
		;

		private final String name;
		private final FoodType foodType;

		GomiType(String name, FoodType type) {
			this.name = name;
			this.foodType = type;
		}

		/** ゴミ種別の表示名を返す。 */
		public String getName() {
			return name;
		}

		/** 対応する食べ物タイプを返す。 */
		public FoodType getFoodType() {
			return foodType;
		}

		/** enum 名の文字列表現を返す。 */
		public String toString() {
			return name;
		}
	}

	/** ランダムテーブル(模擬的) */
	private static GomiType[] rndTable = {
			GomiType.WASTE,
			GomiType.NORMAL,
			GomiType.NORMAL,
			GomiType.BITTER,
			GomiType.WASTE,
			GomiType.NORMAL,
			GomiType.NORMAL,
			GomiType.LEMON_POP,
			GomiType.WASTE,
			GomiType.NORMAL,
			GomiType.NORMAL,
			GomiType.HOT,
			GomiType.WASTE,
			GomiType.NORMAL,
			GomiType.NORMAL,
			GomiType.VIYUGRA,
			GomiType.WASTE,
			GomiType.NORMAL,
			GomiType.NORMAL,
			GomiType.SWEETS1,
			GomiType.WASTE,
			GomiType.NORMAL,
			GomiType.NORMAL,
			GomiType.SWEETS2,
			GomiType.WASTE,
			GomiType.NORMAL,
			GomiType.NORMAL,
			GomiType.WASTE,
	};
	/** 画像の入れもの */
	public static final int hitCheckObjType = 0;
	private static BufferedImage[] images = new BufferedImage[3];
	private static Rectangle4y boundary = new Rectangle4y();

	private boolean[] enable = null;
	private Entity[] food = null;
	private int throwingTime = 100; // ゴミ捨て時刻
	private int gettingP = 1;

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "garbagestation" + File.separator + "garbagestation_base.png");
		images[1] = ModLoader.loadItemImage(loader, "garbagestation" + File.separator + "garbagestation_l_close.png");
		images[2] = ModLoader.loadItemImage(loader, "garbagestation" + File.separator + "garbagestation_off.png");
		boundary.setWidth(images[0].getWidth(io));
		boundary.setHeight(images[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() - 1);
	}

	/** アイテム画像をレイヤー配列にセットし、使用レイヤー数を返す。 */
	@Override
	public int getImageLayer(BufferedImage[] layer) {
		int ret = 2;

		layer[0] = images[0];
		layer[1] = images[1];
		if (!enabled) {
			layer[2] = images[2];
			ret++;
		}
		return ret;
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

	/** 毎ティックの状態更新を行う。 */
	@Override
	public void upDate() {
		if (!enabled) {
			return;
		}
		if ((GameEnvironment.getOperationTime() - throwingTime) % 2400 == 0) {
			for (int i = 0; i < 2; i++) {
				if (GameRandom.nextInt(gettingP) == 0) {
					feedAction(i);
				}
			}
		}
	}

	/** 餌を出す */
	private void feedAction(int idx) {
		if (food[idx] != null) {
			Food f = (Food) food[idx];
			if (f.isRemoved()) {
				food[idx] = null;
				feedAction(idx);
			} else if (f.isEmpty()) {
				f.remove();
				feedAction(idx);
			}
		} else {
			int type = 0;
			do {
				type = GameRandom.nextInt(rndTable.length);
				if (!enable[rndTable[type].ordinal()]) {
					type = -1;
				}
			} while (type < 0);
			FoodType f = rndTable[type].getFoodType();
			int px = (idx == 0 ? -20 : 20);
			food[idx] = GadgetAction.putObjEx(Food.class, getX() + px, getY(), f.ordinal());
			GameWorld.get().getCurrentWorldState().getFoods().put(food[idx].objId, (Food) food[idx]);
		}
	}

	/** ワールドからこのアイテムを除去する。 */
	@Override
	public void removeFromWorld() {
		GameWorld.get().getCurrentWorldState().getGarbageStations().remove(objId);
	}

	/** コンストラクタ */
	public GarbageStation(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), 8);

		GameWorld.get().getCurrentWorldState().getGarbageStations().put(objId, this);
		GameWorld.get().getCurrentWorldState().registerEntity(objId, this);
		objType = Type.OBJECT;
		worldEntityType = WorldEntityKind.GARBAGESTATION;
		enable = new boolean[rndTable.length];
		food = new Entity[2];
		value = 0;
		cost = 0;

		boolean ret = setupGarbageSt(this);
		readIniFile();
		if (!ret) {
			GameWorld.get().getCurrentWorldState().getGarbageStations().remove(objId);
		}
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public GarbageStation() {

	}

	/** 設定メニュー */
	public static boolean setupGarbageSt(GarbageStation d) {
		JPanel mainPanel = new JPanel();
		JCheckBox[] checkBox = new JCheckBox[GomiType.values().length];

		mainPanel.setLayout(new GridLayout(4, 2));
		mainPanel.setPreferredSize(new Dimension(240, 150));

		for (int i = 0; i < checkBox.length; i++) {
			JPanel panel = new JPanel();
			checkBox[i] = new JCheckBox(GomiType.values()[i].toString());
			if (i == 0) {
				checkBox[i].setSelected(true);
				checkBox[i].setEnabled(false);
			}
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));
			panel.add(checkBox[i]);
			mainPanel.add(panel);
		}
		int dlgRet = JOptionPane.showConfirmDialog(GameView.getDialogParent(), mainPanel, "ゴミ捨て場設定",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		boolean ret = false;
		if (dlgRet == JOptionPane.OK_OPTION) {
			for (int i = 0; i < checkBox.length; i++) {
				d.enable[i] = checkBox[i].isSelected();
			}
			ret = true;
		}
		return ret;
	}

	/** iniファイル読み込み */
	public void readIniFile() {
		ClassLoader loader = this.getClass().getClassLoader();
		int iniValue = 0;
		// 時間
		iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataItemIniDir(), "GarbageStation",
				"throwingTime");
		if (iniValue >= 6) {
			throwingTime = iniValue * 100 - 600;
		} else if (iniValue >= 0) {
			throwingTime = iniValue * 100 + 1800;
		}
		// 確率
		iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataItemIniDir(), "GarbageStation",
				"gettingProbability");
		if (iniValue != 0) {
			gettingP = iniValue;
		}
	}

	/** 各ゴミ種別の有効フラグ配列を返す。 */
	public boolean[] getEnable() {
		return enable;
	}

	/** 各ゴミ種別の有効フラグ配列をセットする。 */
	public void setEnable(boolean[] enable) {
		this.enable = enable;
	}

	/** ステージ上の食べ物リストを返す。 */
	public Entity[] getFoods() {
		return food;
	}

	/** ステージ上の食べ物配列をセットする。 */
	public void setFoods(Entity[] food) {
		this.food = food;
	}

	/** ゴミ投下間隔（ティック数）を返す。 */
	public int getThrowingTime() {
		return throwingTime;
	}

	/** ゴミ投下間隔をセットする。 */
	public void setThrowingTime(int throwingTime) {
		this.throwingTime = throwingTime;
	}

	/** 投下先ポイントインデックスを返す。 */
	public int getGettingP() {
		return gettingP;
	}

	/** 投下先ポイントインデックスをセットする。 */
	public void setGettingP(int gettingP) {
		this.gettingP = gettingP;
	}

}
