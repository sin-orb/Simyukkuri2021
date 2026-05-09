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
import src.util.GameRandom;
import src.util.GameWorld;
import src.base.Yukkuri;
import src.base.Entity;
import src.base.WorldEntity;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.enums.CriticalDamegeType;
import src.enums.WorldEntityKind;
import src.enums.Type;
import src.system.ResourceUtil;

/***************************************************
 * 粘着板
 */
public class StickyPlate extends WorldEntity {

	private static final long serialVersionUID = -4372169494877309751L;

	/** どこをくっつけるか */
	public static enum StickyType {
		UNDER(GameText.read("item_footsticky")),
		BACK(GameText.read("item_backsticky")),
		;

		private String name;

		StickyType(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/** 処理対象(ゆっくり) */
	public static final int hitCheckObjType = WorldEntity.YUKKURI;
	private static BufferedImage[] images = new BufferedImage[4];
	private static Rectangle4y boundary = new Rectangle4y();

	private Yukkuri bindBody = null;
	private boolean fixBack = false;

	private ItemRank itemRank;

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "stickyplate" + File.separator + "stickyplate.png");
		images[1] = ModLoader.loadItemImage(loader, "stickyplate" + File.separator + "stickyplate_off.png");
		images[2] = ModLoader.loadItemImage(loader,
				"stickyplate" + File.separator + "stickyplate" + ModLoader.getYkWordNora() + ".png");
		images[3] = ModLoader.loadItemImage(loader,
				"stickyplate" + File.separator + "stickyplate" + ModLoader.getYkWordNora() + "_off.png");
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
		} else {
			if (enabled)
				layer[0] = images[2];
			else
				layer[0] = images[3];
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
		if (bindBody != null)
			return false;
		return true;
	}

	public Yukkuri getBindBody() {
		return bindBody;
	}

	@Override
	public int objHitProcess(Entity o) {
		if (((Yukkuri) o).isDead())
			return 0;
		if (((Yukkuri) o).getCriticalDamegeType() == CriticalDamegeType.CUT)
			return 0;

		if (bindBody != (Yukkuri) o) {
			// 入れ替える場合
			if (bindBody != null) {
				bindBody.setCanPullOrPush(false);

				// 針が刺さっていない、死んでる
				if (!bindBody.isNeedled() || bindBody.isDead()) {
					bindBody.setLockmove(false);
					bindBody.setFixBack(false);
				}
			}
			bindBody = (Yukkuri) o;
			bindBody.clearActions();
		}
		bindBody.setCalcX(x);
		bindBody.setCalcY(y);
		bindBody.setLockmove(true);
			if (!fixBack || bindBody.isPealed()) {
			bindBody.setCanPullOrPush(true);
		} else {
			bindBody.setFixBack(true);
		}
		return 0;
	}

	@Override
	public void upDate() {
		if (!enabled && bindBody != null) {
			bindBody.setLockmove(false);
			return;
		}
		if (bindBody != null) {
			if (fixBack) {
				// 針が刺さっていない
				if (!bindBody.isNeedled() && !bindBody.isSleeping()) {
					if (GameRandom.nextInt(10) == 0) {
						bindBody.setFurifuri(true);
					}
				}
			}

			if (grabbed) {
				bindBody.setCalcX(x);
				bindBody.setCalcY(y);

			} else {
				// ぷるぷる以外が原因で座標がずれている、死んでいる場合は初期化
				if (((bindBody.getX() != x || bindBody.getY() != y) && !bindBody.isPurupuru()) ||
						(bindBody.isRemoved() || bindBody.isDead())) {
					bindBody.setCanPullOrPush(false);
					// 針が刺さっていない、死んでる
					if (!bindBody.isNeedled() || bindBody.isDead()) {
						bindBody.setLockmove(false);
						bindBody.setFixBack(false);
					}
					bindBody = null;
				}
			}
		}
	}

	@Override
	public void removeListData() {
		if (bindBody != null) {
			bindBody.setLockmove(false);
			bindBody.setCanPullOrPush(false);
			bindBody = null;
		}
		GameWorld.get().getCurrentMap().getStickyPlate().remove(objId);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param initX      x座標
	 * @param initY      y座標
	 * @param initOption 0:飼い用、1;野良用
	 */
	public StickyPlate(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		GameWorld.get().getCurrentMap().getStickyPlate().put(objId, this);
		objType = Type.PLATFORM;
		worldEntityType = WorldEntityKind.STICKYPLATE;
		interval = 5;
		if (!setupStickyPlate(this)) {
			GameWorld.get().getCurrentMap().getStickyPlate().remove(objId);
			return;
		}
		itemRank = ItemRank.values()[initOption];
		if (itemRank == ItemRank.HOUSE) {
			value = 2000;
			cost = 0;
		} else {
			value = 0;
			cost = 0;
		}
	}

	public StickyPlate() {

	}

	/** 設定メニュー */
	public static boolean setupStickyPlate(StickyPlate s) {

		JPanel mainPanel = new JPanel();
		JRadioButton[] but = new JRadioButton[StickyType.values().length];
		boolean ret = false;

		mainPanel.setLayout(new GridLayout(2, 1));
		mainPanel.setPreferredSize(new Dimension(100, 100));
		ButtonGroup bg = new ButtonGroup();

		for (int i = 0; i < but.length; i++) {
			but[i] = new JRadioButton(StickyType.values()[i].toString());
			bg.add(but[i]);

			mainPanel.add(but[i]);
		}

		but[0].setSelected(true);

		int dlgRet = JOptionPane.showConfirmDialog(GameView.getDialogParent(), mainPanel,
				GameText.read("item_stickysettings"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (dlgRet == JOptionPane.OK_OPTION) {
			if (but[0].isSelected())
				s.fixBack = false;
			if (but[1].isSelected())
				s.fixBack = true;
			ret = true;
		}
		return ret;
	}

	public boolean isFixBack() {
		return fixBack;
	}

	public void setFixBack(boolean fixBack) {
		this.fixBack = fixBack;
	}

	public ItemRank getItemRank() {
		return itemRank;
	}

	public void setItemRank(ItemRank itemRank) {
		this.itemRank = itemRank;
	}

	public void setBindBody(Yukkuri bindBody) {
		this.bindBody = bindBody;
	}

}
