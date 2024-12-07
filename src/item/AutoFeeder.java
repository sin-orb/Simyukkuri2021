package src.item;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.command.GadgetAction;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.enums.AgeState;
import src.enums.ObjEXType;
import src.enums.TakeoutItemType;
import src.enums.Type;
import src.item.Food.FoodType;
import src.system.Cash;
import src.system.ResourceUtil;
import src.util.YukkuriUtil;

/***************************************************
	自動給餌機
 */
public class AutoFeeder extends ObjEX implements java.io.Serializable {

	private static final long serialVersionUID = -1132194333169381556L;

	/** 出てくるエサタイプ */
	public static enum FeedType {
		NORMAL(ResourceUtil.getInstance().read("command_food_normal")), 
		BITTER(ResourceUtil.getInstance().read("command_food_bitter")), 
		LEMON_POP(ResourceUtil.getInstance().read("command_food_ramune")), 
		HOT(ResourceUtil.getInstance().read("command_food_hot")), 
		VIYUGRA(ResourceUtil.getInstance().read("command_food_viagra")), 
		SWEETS2(ResourceUtil.getInstance().read("command_food_sweet1")), 
		SWEETS1(ResourceUtil.getInstance().read("command_food_sweet2")), 
		WASTE(ResourceUtil.getInstance().read("command_food_garbage")), 
		BODY(ResourceUtil.getInstance().read("item_noprocess")), 
		PROCESSED_BODY(ResourceUtil.getInstance().read("item_processed")),
				;

		private String name;

		FeedType(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/** 給餌モード */
	public static enum FeedMode {
		NORMAL_MODE(ResourceUtil.getInstance().read("command_food_auto")), 
		REGULAR_MODE(ResourceUtil.getInstance().read("item_fixedterm")),
		;

		private String name;

		FeedMode(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/** 当たり判定 */
	public static final int hitCheckObjType = 0;
	private static final int images_num = 2;
	private static BufferedImage[] images = new BufferedImage[images_num];
	private static Rectangle4y boundary = new Rectangle4y();
	private int type = 0;
	private int mode = 0;
	private int feedingInterval = 6 * 100;
	private int feedingP = 2;
	private Obj food = null;

	/**
	 * イメージをロードする.
	 * @param loader ローダ
	 * @param io イメージオブザーバ
	 * @throws IOException IO例外
	 */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "autofeeder" + File.separator + "autofeed.png");
		images[1] = ModLoader.loadItemImage(loader, "autofeeder" + File.separator + "autofeed_off.png");
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (enabled) {
			layer[0] = images[0];
		} else {
			layer[0] = images[1];
		}
		return 1;
	}

	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return null;
	}

	/**
	 * 境界を取得する.
	 * @return 境界
	 */
	public static Rectangle4y getBounding() {
		return boundary;
	}

	@Override
	@Transient
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public int getCost() {
		return cost;
	}

	@Override
	public void removeListData() {
		SimYukkuri.world.getCurrentMap().autofeeder.remove(objId);
	}

	@Override
	public void upDate() {
		if (!enabled)
			return;

		if ((getAge() % 20) != 0)
			return;

		// お持ち帰りされていたりしたら初期化
		if (food != null && !SimYukkuri.world.getCurrentMap().food.containsValue(food) &&
			isTakenOut()) {
			food = null;
		}

		if (food != null) {
			if (type == FeedType.BODY.ordinal() || type == FeedType.PROCESSED_BODY.ordinal()) {
				Body b = (Body) food;
				if (b.isDead()) {
					b.remove();
				}
				if (b.isRemoved()) {
					food = null;
				}
			} else {
				Food f = (Food) food;
				if (f.isRemoved()) {
					food = null;
				} else if (f.isEmpty()) {
					f.remove();
				}
			}
		} else if (mode == 0 || (getAge() % feedingInterval) == 0 && SimYukkuri.RND.nextInt(feedingP) == 0) {
			if (type == FeedType.PROCESSED_BODY.ordinal()) {
				// オートフィーダで出るゆっくりのタイプを決める。
				int type = makeRandomType();
				food = SimYukkuri.mypane.getTerrarium().addBody(getX(), getY(), 0, type, AgeState.BABY, null, null);
				Cash.buyYukkuri((Body) food);
				Cash.addCash(-getCost());
				// レイパーは生まれないようにする
				((Body) food).setRaper(false);
				//静音仕様
				((Body) food).setShutmouth(true);
				//糞害防止
				((Body) food).setForceAnalClose(true);
			} else if (type == FeedType.BODY.ordinal()) {
				// オートフィーダで出るゆっくりのタイプを決める。
				int type = makeRandomType();
				food = SimYukkuri.mypane.getTerrarium().addBody(getX(), getY(), 0, type, AgeState.BABY, null, null);
				Cash.buyYukkuri((Body) food);
				Cash.addCash(-getCost() + 5);
			} else {
				FoodType f = FoodType.FOOD;
				switch (type) {
				case 0:
					f = FoodType.FOOD;
					break;
				case 1:
					f = FoodType.BITTER;
					break;
				case 2:
					f = FoodType.LEMONPOP;
					break;
				case 3:
					f = FoodType.HOT;
					break;
				case 4:
					f = FoodType.VIYUGRA;
					break;
				case 5:
					f = FoodType.SWEETS1;
					break;
				case 6:
					f = FoodType.SWEETS2;
					break;
				case 7:
					f = FoodType.WASTE;
					break;
				}
				food = GadgetAction.putObjEX(Food.class, getX(), getY(), f.ordinal());
				SimYukkuri.world.getCurrentMap().food.put(food.objId, (Food)food);
				Cash.buyItem(food);
				Cash.addCash(-getCost());
			}
		}
	}

	private boolean isTakenOut() {
		for (Map.Entry<Integer, Body> entry : SimYukkuri.world.getCurrentMap().body.entrySet()) {
			Body b = entry.getValue();
			Integer i = b.getTakeoutItem().get(TakeoutItemType.FOOD);
			if (i == null) {
				continue;
			}
			if (i.intValue() == food.objId) {
				return true;
			}
		}
		return false;
	}

	private int makeRandomType() {
		return YukkuriUtil.getRandomYukkuriType(null);
	}

	/**
	 * コンストラクタ.
	 */
	public AutoFeeder(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().autofeeder.put(objId, this);

		objType = Type.PLATFORM;
		objEXType = ObjEXType.AUTOFEEDER;

		boolean ret = setupFeeder(this, false);
		if (setupFeederMode(this, false)) {
			readIniFile();
			ret = true;
		} else
			ret = false;
		if (!ret) {
			SimYukkuri.world.getCurrentMap().autofeeder.remove(objId);
		}
		value = 10000;
		cost = 30;
	}
	public AutoFeeder() {
		
	}

	/**
	 *  設定メニュー
	 * @param o 自動給餌器
	 * @param init 初期化するかどうか
	 * @return 設定されたかどうか
	 */
	public static boolean setupFeeder(AutoFeeder o, boolean init) {

		JPanel mainPanel = new JPanel();
		JRadioButton[] but = new JRadioButton[FeedType.values().length];
		boolean ret = false;

		mainPanel.setLayout(new GridLayout(5, 2));
		mainPanel.setPreferredSize(new Dimension(250, 150));
		ButtonGroup bg = new ButtonGroup();

		for (int i = 0; i < but.length; i++) {
			but[i] = new JRadioButton(FeedType.values()[i].toString());
			bg.add(but[i]);

			mainPanel.add(but[i]);
		}
		but[0].setSelected(true);
		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, 
				ResourceUtil.getInstance().read("item_autosetting"), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (dlgRet == JOptionPane.OK_OPTION) {
			for (int i = 0; i < but.length; i++) {
				if (but[i].isSelected()) {
					o.type = i;
					break;
				}
			}
			ret = true;
		}
		return ret;
	}

	/**
	 *  設定メニュー02
	 * @param o 自動給餌器
	 * @param init 初期化するか
	 * @return 設定されたかどうか
	 */
	public static boolean setupFeederMode(AutoFeeder o, boolean init) {

		JPanel mainPanel = new JPanel();
		JRadioButton[] but = new JRadioButton[FeedMode.values().length];
		boolean ret = false;

		mainPanel.setLayout(new GridLayout(5, 2));
		mainPanel.setPreferredSize(new Dimension(250, 150));
		ButtonGroup bg = new ButtonGroup();

		for (int i = 0; i < but.length; i++) {
			but[i] = new JRadioButton(FeedMode.values()[i].toString());
			bg.add(but[i]);

			mainPanel.add(but[i]);
		}
		but[0].setSelected(true);
		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, 
				ResourceUtil.getInstance().read("item_movetypesettings"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (dlgRet == JOptionPane.OK_OPTION) {
			for (int i = 0; i < but.length; i++) {
				if (but[i].isSelected()) {
					o.mode = i;
					break;
				}
			}
			ret = true;
		}
		return ret;
	}

	/**
	 * INIファイルを読む.
	 */
	public void readIniFile() {
		ClassLoader loader = this.getClass().getClassLoader();
		int nTemp = 0;
		//間隔
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_ITEM_INI_DIR, "AutoFeeder", "FeedingInterval");
		if (nTemp != 0)
			feedingInterval = nTemp;
		//確率
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_ITEM_INI_DIR, "AutoFeeder", "FeedingProbability");
		if (nTemp != 0)
			feedingP = nTemp;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getFeedingInterval() {
		return feedingInterval;
	}

	public void setFeedingInterval(int feedingInterval) {
		this.feedingInterval = feedingInterval;
	}

	public int getFeedingP() {
		return feedingP;
	}

	public void setFeedingP(int feedingP) {
		this.feedingP = feedingP;
	}

	public Obj getFood() {
		return food;
	}

	public void setFood(Obj food) {
		this.food = food;
	}

}
