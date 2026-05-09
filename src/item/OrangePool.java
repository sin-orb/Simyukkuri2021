package src.item;
import src.util.GameView;
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
import src.util.GameWorld;
import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.enums.FootBake;
import src.enums.ObjEXType;
import src.enums.Type;
import src.system.Cash;
import src.system.ResourceUtil;

/***************************************************
 * オレンジプレート
 */
public class OrangePool extends ObjEX {
	private static final long serialVersionUID = -5312430078640748031L;

	/** タイプ */
	public static enum OrangeType {
		NORMAL(GameText.read("item_coolwater")),
		RESCUE(GameText.read("item_lifesaving")),
		;

		private String name;

		OrangeType(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/** 処理対象(ゆっくり) */
	public static final int hitCheckObjType = ObjEX.YUKKURI;
	private static final int IMAGE_COUNT = 6; // このクラスの総使用画像数
	private static BufferedImage[] imageLayers = new BufferedImage[IMAGE_COUNT];
	private static Rectangle4y boundary = new Rectangle4y();
	private boolean rescue;
	private static int[] value = { 500, 10000 };
	private static int[] cost = { 5, 100 };

	private ItemRank itemRank;

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		imageLayers[0] = ModLoader.loadItemImage(loader, "orangepool" + File.separator + "orangepool.png");
		imageLayers[1] = ModLoader.loadItemImage(loader, "orangepool" + File.separator + "orangepool_off.png");
		imageLayers[2] = ModLoader.loadItemImage(loader,
				"orangepool" + File.separator + "orangepool" + ModLoader.getYkWordNora() + ".png");
		imageLayers[3] = ModLoader.loadItemImage(loader,
				"orangepool" + File.separator + "orangepool" + ModLoader.getYkWordNora() + "_off.png");
		imageLayers[4] = ModLoader.loadItemImage(loader,
				"orangepool" + File.separator + "orangepool" + ModLoader.getYkWordYasei() + ".png");
		imageLayers[5] = ModLoader.loadItemImage(loader,
				"orangepool" + File.separator + "orangepool" + ModLoader.getYkWordYasei() + "_off.png");
		boundary.setWidth(imageLayers[0].getWidth(io));
		boundary.setHeight(imageLayers[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() >> 1);
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (itemRank == ItemRank.HOUSE) {
			if (enabled)
				layer[0] = imageLayers[0];
			else
				layer[0] = imageLayers[1];
		} else if (itemRank == ItemRank.NORA) {
			if (enabled)
				layer[0] = imageLayers[2];
			else
				layer[0] = imageLayers[3];
		} else {
			if (enabled)
				layer[0] = imageLayers[4];
			else
				layer[0] = imageLayers[5];
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
	public int objHitProcess(Obj targetObject) {
		if (!enabled)
			return 0;
		if (targetObject.getObjType() == Type.YUKKURI) {
			Body body = (Body) targetObject;
			body.giveJuice();
			if (body.isDirty()) {
				body.setDirtyFlag(false);
			}
			if (rescue) {
				if (body.isDead() && !body.isCrushed() && !body.isBurned()) {
					body.revival();
				}
				if (body.getFootBakeLevel() != FootBake.CRITICAL) {
					body.setFootBakePeriod(0);
				}
				if (body.isBurst() == false) {
					body.setCantDie();
				}
			}
			Cash.addCash(-getCost());
		}
		return 0;
	}

	@Override
	@Transient
	public int getValue() {
		if (itemRank == ItemRank.HOUSE) {
			if (rescue)
				return value[1];
			else
				return value[0];
		} else {
			return 0;
		}
	}

	@Override
	@Transient
	public int getCost() {
		if (itemRank == ItemRank.HOUSE) {
			if (rescue)
				return cost[1];
			else
				return cost[0];
		} else {
			return 0;
		}
	}

	@Override
	public void removeListData() {
		GameWorld.get().getCurrentMap().getOrangePool().remove(objId);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param initX      x座標
	 * @param initY      y座標
	 * @param initOption 0:飼い用、1:野良用
	 */
	public OrangePool(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());

		GameWorld.get().getCurrentMap().getOrangePool().put(objId, this);

		objType = Type.PLATFORM;
		objEXType = ObjEXType.ORANGEPOOL;
		interval = 3;

		boolean setupSucceeded = setupOrange(this, false);
		if (setupSucceeded) {
			itemRank = ItemRank.values()[initOption];
			// 森なら野生に変更
			if (GameWorld.get().getCurrentMap().getMapIndex() == 5
					|| GameWorld.get().getCurrentMap().getMapIndex() == 6) {
				if (itemRank == ItemRank.HOUSE) {
					itemRank = ItemRank.YASEI;
				}
			}
		} else {
			GameWorld.get().getCurrentMap().getOrangePool().remove(objId);
		}
	}

	public OrangePool() {

	}

	/** 設定メニュー */
	public static boolean setupOrange(OrangePool orangePool, boolean init) {

		JPanel mainPanel = new JPanel();
		JRadioButton[] buttons = new JRadioButton[OrangeType.values().length];
		boolean result = false;

		mainPanel.setLayout(new GridLayout(2, 1));
		mainPanel.setPreferredSize(new Dimension(150, 100));
		ButtonGroup buttonGroup = new ButtonGroup();

		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new JRadioButton(OrangeType.values()[i].toString());
			buttonGroup.add(buttons[i]);

			mainPanel.add(buttons[i]);
		}
		if (!init) {
			buttons[0].setSelected(true);
		} else {
			if (!orangePool.rescue) {
				buttons[0].setSelected(true);
			} else {
				buttons[1].setSelected(true);
			}
		}

		int dialogResult = JOptionPane.showConfirmDialog(GameView.getDialogParent(), mainPanel, "オレンジプール設定",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (dialogResult == JOptionPane.OK_OPTION) {
			if (buttons[0].isSelected())
				orangePool.rescue = false;
			else
				orangePool.rescue = true;
			result = true;
		}
		return result;
	}

	public boolean isRescue() {
		return rescue;
	}

	public void setRescue(boolean rescue) {
		this.rescue = rescue;
	}

	public ItemRank getItemRank() {
		return itemRank;
	}

	public void setItemRank(ItemRank itemRank) {
		this.itemRank = itemRank;
	}

}
