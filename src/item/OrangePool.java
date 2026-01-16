package src.item;

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
		NORMAL(ResourceUtil.getInstance().read("item_coolwater")),
		RESCUE(ResourceUtil.getInstance().read("item_lifesaving")),
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
	private static final int images_num = 6; // このクラスの総使用画像数
	private static BufferedImage[] images = new BufferedImage[images_num];
	private static Rectangle4y boundary = new Rectangle4y();
	private boolean rescue;
	private static int[] value = { 500, 10000 };
	private static int[] cost = { 5, 100 };

	private ItemRank itemRank;

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "orangepool" + File.separator + "orangepool.png");
		images[1] = ModLoader.loadItemImage(loader, "orangepool" + File.separator + "orangepool_off.png");
		images[2] = ModLoader.loadItemImage(loader,
				"orangepool" + File.separator + "orangepool" + ModLoader.getYkWordNora() + ".png");
		images[3] = ModLoader.loadItemImage(loader,
				"orangepool" + File.separator + "orangepool" + ModLoader.getYkWordNora() + "_off.png");
		images[4] = ModLoader.loadItemImage(loader,
				"orangepool" + File.separator + "orangepool" + ModLoader.getYkWordYasei() + ".png");
		images[5] = ModLoader.loadItemImage(loader,
				"orangepool" + File.separator + "orangepool" + ModLoader.getYkWordYasei() + "_off.png");
		boundary.setWidth(images[0].getWidth(io));
		boundary.setHeight(images[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() >> 1);
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (itemRank == ItemRank.HOUSE) {
			if (enabled)
				layer[0] = images[0];
			else
				layer[0] = images[1];
		} else if (itemRank == ItemRank.NORA) {
			if (enabled)
				layer[0] = images[2];
			else
				layer[0] = images[3];
		} else {
			if (enabled)
				layer[0] = images[4];
			else
				layer[0] = images[5];
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
	public int objHitProcess(Obj o) {
		if (!enabled)
			return 0;
		if (o.getObjType() == Type.YUKKURI) {
			Body b = (Body) o;
			b.giveJuice();
			if (b.isDirty()) {
				b.setDirtyFlag(false);
			}
			if (rescue) {
				if (b.isDead() && !b.isCrushed() && !b.isBurned()) {
					b.revival();
				}
				if (b.getFootBakeLevel() != FootBake.CRITICAL) {
					b.setFootBakePeriod(0);
				}
				if (b.isBurst() == false) {
					b.setCantDie();
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
		SimYukkuri.world.getCurrentMap().getOrangePool().remove(objId);
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

		SimYukkuri.world.getCurrentMap().getOrangePool().put(objId, this);

		objType = Type.PLATFORM;
		objEXType = ObjEXType.ORANGEPOOL;
		interval = 3;

		boolean ret = setupOrange(this, false);
		if (ret) {
			itemRank = ItemRank.values()[initOption];
			// 森なら野生に変更
			if (SimYukkuri.world.getCurrentMap().getMapIndex() == 5
					|| SimYukkuri.world.getCurrentMap().getMapIndex() == 6) {
				if (itemRank == ItemRank.HOUSE) {
					itemRank = ItemRank.YASEI;
				}
			}
		} else {
			SimYukkuri.world.getCurrentMap().getOrangePool().remove(objId);
		}
	}

	public OrangePool() {

	}

	/** 設定メニュー */
	public static boolean setupOrange(OrangePool o, boolean init) {

		JPanel mainPanel = new JPanel();
		JRadioButton[] but = new JRadioButton[OrangeType.values().length];
		boolean ret = false;

		mainPanel.setLayout(new GridLayout(2, 1));
		mainPanel.setPreferredSize(new Dimension(150, 100));
		ButtonGroup bg = new ButtonGroup();

		for (int i = 0; i < but.length; i++) {
			but[i] = new JRadioButton(OrangeType.values()[i].toString());
			bg.add(but[i]);

			mainPanel.add(but[i]);
		}
		if (!init) {
			but[0].setSelected(true);
		} else {
			if (!o.rescue) {
				but[0].setSelected(true);
			} else {
				but[1].setSelected(true);
			}
		}

		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, "オレンジプール設定",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (dlgRet == JOptionPane.OK_OPTION) {
			if (but[0].isSelected())
				o.rescue = false;
			else
				o.rescue = true;
			ret = true;
		}
		return ret;
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
