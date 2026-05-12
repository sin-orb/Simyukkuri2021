package src.entity.core.world.item;

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

import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.entity.core.Entity;
import src.entity.core.world.WorldEntity;
import src.enums.Type;
import src.enums.WorldEntityKind;
import src.system.Cash;
import src.util.GameText;
import src.util.GameView;
import src.util.GameWorld;

/***************************************************
 * トイレ
 */
public class Toilet extends WorldEntity {
	private static final long serialVersionUID = -484401473340388552L;

	/** トイレのタイプ */
	public static enum ToiletType {
		NORMAL(GameText.read("item_toiletcheap")),
		CLEAN(GameText.read("item_toiletautoclean")),
		SLAVE(GameText.read("item_toiletununsalve")),
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
	public static final int hitCheckObjType = WorldEntity.SHIT;
	private static BufferedImage[] images = new BufferedImage[6];
	private static Rectangle4y boundary = new Rectangle4y();

	private ItemRank itemRank;

	private boolean autoClean;
	private boolean forSlave = false;

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
			else if (forSlave)
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

	public boolean checkHitObj(Entity targetObject) {
		Rectangle tmpRect = new Rectangle();
		getCollisionRect(tmpRect);
		// 対象の座標をフィールド座標に変換
		Translate.translate(targetObject.getX(), targetObject.getY(), tmpPos);
		// 点が描画矩形に入ったかの判定
		if (tmpRect.contains(new java.awt.Point(tmpPos.getX(), tmpPos.getY()))) {
			return true;
		}
		return false;
	}

	@Override
	public boolean checkHitObj(Rectangle collisionRect, Entity targetObject) {
		Rectangle tmpRect = new Rectangle();
		getCollisionRect(tmpRect);
		// 対象の座標をフィールド座標に変換
		Translate.translate(targetObject.getX(), targetObject.getY(), tmpPos);
		// 点が描画矩形に入ったかの判定
		if (tmpRect.contains(new java.awt.Point(tmpPos.getX(), tmpPos.getY()))) {
			if (autoClean) {
				targetObject.remove();
			}
			return true;
		}
		return false;
	}

	@Override
	public void removeListData() {
		GameWorld.get().getCurrentMap().getToilet().remove(objId);
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
		return forSlave;
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
		GameWorld.get().getCurrentMap().getToilet().put(objId, this);
		objType = Type.PLATFORM;
		worldEntityType = WorldEntityKind.TOILET;
		interval = 30;

		boolean setupSucceeded = setupToilet(this);
		if (setupSucceeded) {
			itemRank = ItemRank.values()[initOption];
			// 森なら野生に変更
			if (GameWorld.get().getCurrentMap().getMapIndex() == 5
					|| GameWorld.get().getCurrentMap().getMapIndex() == 6) {
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
			GameWorld.get().getCurrentMap().getToilet().remove(objId);
		}
	}

	public Toilet() {

	}

	public int objHitProcess(Entity targetObject) {
		targetObject.remove();
		Cash.addCash(-getCost());
		return 1;
	}

	/** 設定メニュー */
	public static boolean setupToilet(Toilet toilet) {

		JPanel mainPanel = new JPanel();
		JRadioButton[] buttons = new JRadioButton[ToiletType.values().length];
		boolean setupSucceeded = false;

		mainPanel.setLayout(new GridLayout(3, 1));
		mainPanel.setPreferredSize(new Dimension(150, 100));
		ButtonGroup buttonGroup = new ButtonGroup();

		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new JRadioButton(ToiletType.values()[i].toString());
			buttonGroup.add(buttons[i]);

			mainPanel.add(buttons[i]);
		}

		buttons[0].setSelected(true);

		int dialogResult = JOptionPane.showConfirmDialog(GameView.getDialogParent(), mainPanel, "トイレ設定",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (dialogResult == JOptionPane.OK_OPTION) {
			if (buttons[0].isSelected())
				toilet.autoClean = false;
			if (buttons[1].isSelected())
				toilet.autoClean = true;
			if (buttons[2].isSelected()) {
				toilet.forSlave = true;
				toilet.autoClean = false;
			}
			setupSucceeded = true;
		}
		return setupSucceeded;
	}

	public ItemRank getItemRank() {
		return itemRank;
	}

	public void setItemRank(ItemRank itemRank) {
		this.itemRank = itemRank;
	}

	// public boolean isForSlave() {
	// return forSlave;
	// }
	//
	// public void setForSlave(boolean forSlave) {
	// this.forSlave = forSlave;
	// }

	public void setForSlave(boolean forSlave) {
		this.forSlave = forSlave;
	}

	public void setAutoClean(boolean autoClean) {
		this.autoClean = autoClean;
	}

}
