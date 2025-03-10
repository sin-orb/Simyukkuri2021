package src.item;

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

import src.SimYukkuri;
import src.base.Obj;
import src.base.ObjEX;
import src.command.GadgetAction;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.draw.Terrarium;
import src.enums.ObjEXType;
import src.enums.Type;
import src.item.Food.FoodType;
import src.system.ResourceUtil;

/***************************************************
 * ゴミ捨て場
 */
public class GarbageStation extends ObjEX implements java.io.Serializable {

	private static final long serialVersionUID = -3307339525828240055L;

	/**出す餌の種類テーブル*/
	public static enum GomiType {
		WASTE(ResourceUtil.getInstance().read("command_food_garbage"), FoodType.WASTE_NORA),
		BITTER(ResourceUtil.getInstance().read("command_food_bitter"), FoodType.BITTER_NORA), 
		HOT(ResourceUtil.getInstance().read("command_food_hot"), FoodType.HOT_NORA), 
		LEMON_POP(ResourceUtil.getInstance().read("command_food_ramune"), FoodType.LEMONPOP_NORA), 
		VIYUGRA(ResourceUtil.getInstance().read("command_food_viagra"), FoodType.VIYUGRA_NORA),
		NORMAL(ResourceUtil.getInstance().read("command_food_normal"),FoodType.FOOD_NORA),
		SWEETS1(ResourceUtil.getInstance().read("command_food_sweet1"),FoodType.SWEETS_NORA1),
		SWEETS2(ResourceUtil.getInstance().read("command_food_sweet2"), FoodType.SWEETS_NORA2),
								;

		public String name;
		public FoodType foodType;

		GomiType(String name, FoodType type) {
			this.name = name;
			this.foodType = type;
		}

		public String toString() {
			return name;
		}
	}

	/**ランダムテーブル(模擬的)*/
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
	/**画像の入れもの*/
	public static final int hitCheckObjType = 0;
	private static BufferedImage images[] = new BufferedImage[3];
	private static Rectangle4y boundary = new Rectangle4y();

	private boolean[] enable = null;
	private Obj[] food = null;
	private int throwingTime = 100;//ゴミ捨て時刻
	private int gettingP = 1;

	/**画像ロード*/
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "garbagestation" + File.separator + "garbagestation_base.png");
		images[1] = ModLoader.loadItemImage(loader, "garbagestation" + File.separator + "garbagestation_l_close.png");
		images[2] = ModLoader.loadItemImage(loader, "garbagestation" + File.separator + "garbagestation_off.png");
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height - 1;
	}

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

	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return null;
	}

	/**境界線の取得*/
	public static Rectangle4y getBounding() {
		return boundary;
	}

	@Override
	public void upDate() {
		if (!enabled)
			return;
		if ((Terrarium.operationTime - throwingTime) % 2400 == 0) {
			for (int i = 0; i < 2; i++) {
				if (SimYukkuri.RND.nextInt(gettingP) == 0) {
					feedAction(i);
				}
			}
		}
	}

	/**餌を出す*/
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
				type = SimYukkuri.RND.nextInt(rndTable.length);
				if (!enable[rndTable[type].ordinal()]) {
					type = -1;
				}
			} while (type < 0);
			FoodType f = rndTable[type].foodType;
			int px = (idx == 0 ? -20 : 20);
			food[idx] = GadgetAction.putObjEX(Food.class, getX() + px, getY(), f.ordinal());
			SimYukkuri.world.getCurrentMap().food.put(food[idx].objId, (Food)food[idx]);
		}
	}

	@Override
	public void removeListData() {
		SimYukkuri.world.getCurrentMap().garbageStation.remove(objId);
	}

	/**コンストラクタ*/
	public GarbageStation(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), 8);

		SimYukkuri.world.getCurrentMap().garbageStation.put(objId, this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.GARBAGESTATION;
		enable = new boolean[rndTable.length];
		food = new Obj[2];
		value = 0;
		cost = 0;

		boolean ret = setupGarbageSt(this);
		readIniFile();
		if (!ret) {
			SimYukkuri.world.getCurrentMap().garbageStation.remove(objId);
		}
	}
	public GarbageStation() {
		
	}

	/** 設定メニュー*/
	public static boolean setupGarbageSt(GarbageStation d) {
		JPanel mainPanel = new JPanel();
		JCheckBox[] checkBox = new JCheckBox[GomiType.values().length];
		boolean ret = false;

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
		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, "ゴミ捨て場設定",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (dlgRet == JOptionPane.OK_OPTION) {
			for (int i = 0; i < checkBox.length; i++) {
				d.enable[i] = checkBox[i].isSelected();
			}
			ret = true;
		}
		return ret;
	}

	/**iniファイル読み込み*/
	public void readIniFile() {
		ClassLoader loader = this.getClass().getClassLoader();
		int nTemp = 0;
		//時間
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_ITEM_INI_DIR, "GarbageStation", "throwingTime");
		if (nTemp >= 6)
			throwingTime = nTemp * 100 - 600;
		else if (nTemp >= 0)
			throwingTime = nTemp * 100 + 1800;
		//確率
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_ITEM_INI_DIR, "GarbageStation",
				"gettingProbability");
		if (nTemp != 0)
			gettingP = nTemp;
	}

	public boolean[] getEnable() {
		return enable;
	}

	public void setEnable(boolean[] enable) {
		this.enable = enable;
	}

	public Obj[] getFood() {
		return food;
	}

	public void setFood(Obj[] food) {
		this.food = food;
	}

	public int getThrowingTime() {
		return throwingTime;
	}

	public void setThrowingTime(int throwingTime) {
		this.throwingTime = throwingTime;
	}

	public int getGettingP() {
		return gettingP;
	}

	public void setGettingP(int gettingP) {
		this.gettingP = gettingP;
	}
	
}
