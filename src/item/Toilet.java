package src.item;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
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
import src.base.Obj;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.enums.ObjEXType;
import src.enums.Type;
import src.system.Cash;
import src.system.ResourceUtil;

/***************************************************
 * トイレ
 */
public class Toilet extends ObjEX implements java.io.Serializable {
	private static final long serialVersionUID = -484401473340388552L;

	/** トイレのタイプ */
	public static enum ToiletType {
		NORMAL(ResourceUtil.getInstance().read("item_toiletcheap")),
		CLEAN(ResourceUtil.getInstance().read("item_toiletautoclean")),
		SLAVE(ResourceUtil.getInstance().read("item_toiletununsalve")),
		;

		private String name;

		ToiletType(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/** 処理対象(うんうん) */
	public static final int hitCheckObjType = ObjEX.SHIT;
	private static BufferedImage[] images = new BufferedImage[6];
	private static Rectangle4y boundary = new Rectangle4y();

	private ItemRank itemRank;

	private boolean autoClean;
	private boolean bForSlave = false;

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		// 上から順に普通&うんうん奴隷、自動清掃

		images[0] = ModLoader.loadItemImage(loader, "toilet" + File.separator + "toilet.png");
		images[1] = ModLoader.loadItemImage(loader, "toilet" + File.separator + "toilet2.png");

		images[2] = ModLoader.loadItemImage(loader,
				"toilet" + File.separator + "toilet" + ModLoader.getYkWordNora() + ".png");
		images[3] = ModLoader.loadItemImage(loader,
				"toilet" + File.separator + "toilet" + ModLoader.getYkWordNora() + "2.png");

		images[4] = ModLoader.loadItemImage(loader,
				"toilet" + File.separator + "toilet" + ModLoader.getYkWordYasei() + ".png");
		images[5] = ModLoader.loadItemImage(loader,
				"toilet" + File.separator + "toilet" + ModLoader.getYkWordYasei() + "2.png");

		boundary.setWidth(images[0].getWidth(io));
		boundary.setHeight(images[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() >> 1);
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (itemRank == ItemRank.HOUSE) {
			if (autoClean)
				layer[0] = images[1];
			else if (bForSlave)
				layer[0] = images[4];
			else
				layer[0] = images[0];
		} else if (itemRank == ItemRank.NORA) {
			if (autoClean)
				layer[0] = images[3];
			else
				layer[0] = images[2];
		} else {
			if (autoClean)
				layer[0] = images[5];
			else
				layer[0] = images[4];
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
		if (autoClean)
			return hitCheckObjType;
		return 0;
	}

	public boolean checkHitObj(Obj o) {
		Rectangle tmpRect = new Rectangle();
		getCollisionRect(tmpRect);
		// 対象の座標をフィールド座標に変換
		Translate.translate(o.getX(), o.getY(), tmpPos);
		// 点が描画矩形に入ったかの判定
		if (tmpRect.contains(new java.awt.Point(tmpPos.getX(), tmpPos.getY()))) {
			return true;
		}
		return false;
	}

	@Override
	public boolean checkHitObj(Rectangle colRect, Obj o) {
		Rectangle tmpRect = new Rectangle();
		getCollisionRect(tmpRect);
		// 対象の座標をフィールド座標に変換
		Translate.translate(o.getX(), o.getY(), tmpPos);
		// 点が描画矩形に入ったかの判定
		if (tmpRect.contains(new java.awt.Point(tmpPos.getX(), tmpPos.getY()))) {
			if (autoClean) {
				o.remove();
			}
			return true;
		}
		return false;
	}

	@Override
	public void removeListData() {
		SimYukkuri.world.getCurrentMap().getToilet().remove(objId);
	}

	/**
	 * 自動で掃除するかどうか
	 * 
	 * @return 自動で掃除するかどうか
	 */
	public boolean getAutoClean() {
		return autoClean;
	}

	/**
	 * うんうんどれい用トイレかどうか
	 * 
	 * @return うんうんどれい用トイレかどうか
	 */
	@Transient
	public boolean isForSlave() {
		return bForSlave;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param initX      x座標
	 * @param initY      y座標
	 * @param initOption 0:飼い用、1;野良用
	 */
	public Toilet(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().getToilet().put(objId, this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.TOILET;
		interval = 30;

		boolean ret = setupToilet(this);
		if (ret) {
			itemRank = ItemRank.values()[initOption];
			// 森なら野生に変更
			if (SimYukkuri.world.getCurrentMap().getMapIndex() == 5 || SimYukkuri.world.getCurrentMap().getMapIndex() == 6) {
				if (itemRank == ItemRank.HOUSE) {
					itemRank = ItemRank.YASEI;
				}
			}

			if (itemRank == ItemRank.HOUSE) {
				if (autoClean) {
					value = 5000;
					cost = 50;
				} else {
					value = 1000;
					cost = 0;
				}
			} else {
				value = 0;
				cost = 0;
			}
		} else {
			SimYukkuri.world.getCurrentMap().getToilet().remove(objId);
		}
	}

	public Toilet() {

	}

	public int objHitProcess(Obj o) {
		o.remove();
		Cash.addCash(-getCost());
		return 1;
	}

	/** 設定メニュー */
	public static boolean setupToilet(Toilet t) {

		JPanel mainPanel = new JPanel();
		JRadioButton[] but = new JRadioButton[ToiletType.values().length];
		boolean ret = false;

		mainPanel.setLayout(new GridLayout(3, 1));
		mainPanel.setPreferredSize(new Dimension(150, 100));
		ButtonGroup bg = new ButtonGroup();

		for (int i = 0; i < but.length; i++) {
			but[i] = new JRadioButton(ToiletType.values()[i].toString());
			bg.add(but[i]);

			mainPanel.add(but[i]);
		}

		but[0].setSelected(true);

		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, "トイレ設定", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (dlgRet == JOptionPane.OK_OPTION) {
			if (but[0].isSelected())
				t.autoClean = false;
			if (but[1].isSelected())
				t.autoClean = true;
			if (but[2].isSelected()) {
				t.bForSlave = true;
				t.autoClean = false;
			}
			ret = true;
		}
		return ret;
	}

	public ItemRank getItemRank() {
		return itemRank;
	}

	public void setItemRank(ItemRank itemRank) {
		this.itemRank = itemRank;
	}

	// public boolean isbForSlave() {
	// return bForSlave;
	// }
	//
	// public void setbForSlave(boolean bForSlave) {
	// this.bForSlave = bForSlave;
	// }

	public boolean isBForSlave() {
		return bForSlave;
	}

	public void setBForSlave(boolean bForSlave) {
		this.bForSlave = bForSlave;
	}

	public void setAutoClean(boolean autoClean) {
		this.autoClean = autoClean;
	}

}

