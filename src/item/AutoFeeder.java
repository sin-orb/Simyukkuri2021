package src.item;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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
import src.enums.AgeState;
import src.enums.ObjEXType;
import src.enums.Type;
import src.item.Food.FoodType;
import src.system.Cash;


/***************************************************
	自動給餌機
 */
public class AutoFeeder extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static enum FeedType {
        NORMAL("ふつう"),
        BITTER("苦い"),
        LEMON_POP("ラムネ"),
        HOT("辛い"),
        VIYUGRA("バイゆグラ"),
        SWEETS2("あまあま(普通)"),
        SWEETS1("あまあま(高級)"),
        WASTE("生ゴミ"),
        BODY("無加工生餌"),
        PROCESSED_BODY("加工済生餌"),
		;
        private String name;
        FeedType(String name) { this.name = name; }
        public String toString() { return name; }
	}
	public static enum FeedMode {
        NORMAL_MODE("自動給餌"),
        REGULAR_MODE("定期給餌"),
		;
        private String name;
        FeedMode(String name) { this.name = name; }
        public String toString() { return name; }
	}

	public static final int hitCheckObjType = 0;
	private static final int images_num = 2;
	private static BufferedImage[] images = new BufferedImage[images_num];
	private static Rectangle boundary = new Rectangle();

	private Random rnd = new Random();
	private int type = 0;
	private int mode = 0;
	private int feedingInterval =6*100;
	private int feedingP = 2;
	private Obj food = null;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "autofeeder" + File.separator + "autofeed.png");
		images[1] = ModLoader.loadItemImage(loader, "autofeeder" + File.separator + "autofeed_off.png");
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if(enabled) {
			layer[0] = images[0];
		} else {
			layer[0] = images[1];
		}
		return 1;
	}

	@Override
	public BufferedImage getShadowImage() {
		return null;
	}

	public static Rectangle getBounding() {
		return boundary;
	}

	@Override
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
	public void removeListData(){
		SimYukkuri.world.currentMap.autofeeder.remove(this);
	}

	@Override
	public void upDate() {
		if(!enabled) return;

		if((getAge() % 20) != 0) return;

		// お持ち帰りされていたりしたら初期化
		if( !SimYukkuri.world.currentMap.food.contains(food)  &&
			!SimYukkuri.world.currentMap.body.contains(food) ){
			food = null;
		}

		if(food != null) {
			if(type == FeedType.BODY.ordinal() || type == FeedType.PROCESSED_BODY.ordinal()) {
				Body b = (Body)food;
				if(b.isDead()) {
					b.remove();
				}
				if(b.isRemoved()) {
					food = null;
				}
			}
			else {
				Food f = (Food)food;
				if(f.isRemoved()) {
					food = null;
				}
				else if(f.isEmpty()) {
					f.remove();
				}
			}
		}
		else if( mode == 0 || (getAge() % feedingInterval) == 0 && rnd.nextInt(feedingP)== 0){
			if(type == FeedType.PROCESSED_BODY.ordinal()) {
				food = SimYukkuri.mypane.terrarium.addBody(getX(), getY(), 0, rnd.nextInt(6), AgeState.BABY, null, null);
				Cash.buyYukkuri((Body)food);
				Cash.addCash(-getCost());
				// レイパーは生まれないようにする
				((Body)food).setRaper(false);
				//静音仕様
				((Body)food).setShutmouth(true);
				//糞害防止
				((Body)food).setForceAnalClose(true);
			}
			else if(type == FeedType.BODY.ordinal()) {
				food = SimYukkuri.mypane.terrarium.addBody(getX(), getY(), 0, rnd.nextInt(6), AgeState.BABY, null, null);
				Cash.buyYukkuri((Body)food);
				Cash.addCash(-getCost()+5);
			}
			else {
				FoodType f = FoodType.FOOD;
				switch(type) {
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
				Cash.buyItem(food);
				Cash.addCash(-getCost());
			}
		}
	}

	public AutoFeeder(int initX, int initY, int initOption) {

		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		ArrayList<AutoFeeder> list = SimYukkuri.world.currentMap.autofeeder;
		list.add(this);

		objType = Type.PLATFORM;
		objEXType = ObjEXType.AUTOFEEDER;

		boolean ret = setupFeeder(this, false);
		if(setupFeederMode(this, false)) {
			readIniFile();
			ret =true;
		}
		else ret=false;
		if(!ret) {
			list.remove(this);
		}
		value = 10000;
		cost = 30;
	}

	// 設定メニュー
	public static boolean setupFeeder(AutoFeeder o, boolean init) {

		JPanel mainPanel = new JPanel();
		JRadioButton[] but = new JRadioButton[FeedType.values().length];
		boolean ret = false;

		mainPanel.setLayout(new GridLayout(5, 2));
		mainPanel.setPreferredSize(new Dimension(250, 150));
		ButtonGroup bg = new ButtonGroup();

		for(int i = 0; i < but.length; i++) {
			but[i] = new JRadioButton(FeedType.values()[i].toString());
			bg.add(but[i]);

			mainPanel.add(but[i]);
		}
		but[0].setSelected(true);
		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, "自動給餌設定", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if(dlgRet == JOptionPane.OK_OPTION) {
			for(int i = 0; i < but.length; i++) {
				if(but[i].isSelected()) {
					o.type = i;
					break;
				}
			}
			ret = true;
		}
		return ret;
	}

	// 設定メニュー02
	public static boolean setupFeederMode(AutoFeeder o, boolean init) {

		JPanel mainPanel = new JPanel();
		JRadioButton[] but = new JRadioButton[FeedMode.values().length];
		boolean ret = false;

		mainPanel.setLayout(new GridLayout(5, 2));
		mainPanel.setPreferredSize(new Dimension(250, 150));
		ButtonGroup bg = new ButtonGroup();

		for(int i = 0; i < but.length; i++) {
			but[i] = new JRadioButton(FeedMode.values()[i].toString());
			bg.add(but[i]);

			mainPanel.add(but[i]);
		}
		but[0].setSelected(true);
		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, "稼働モード設定", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if(dlgRet == JOptionPane.OK_OPTION) {
			for(int i = 0; i < but.length; i++) {
				if(but[i].isSelected()) {
					o.mode = i;
					break;
				}
			}
		ret = true;
		}
		return ret;
	}

	public void readIniFile(){
		ClassLoader loader = this.getClass().getClassLoader();
		int nTemp = 0;
		//間隔
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_ITEM_INI_DIR, "AutoFeeder", "FeedingInterval");
		if(nTemp != 0)feedingInterval = nTemp;
		//確率
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_ITEM_INI_DIR, "AutoFeeder", "FeedingProbability");
		if(nTemp != 0)feedingP = nTemp;
	}

}


