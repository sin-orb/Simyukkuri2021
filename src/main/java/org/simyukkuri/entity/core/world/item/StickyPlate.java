package org.simyukkuri.entity.core.world.item;

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

import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.GameView;
import org.simyukkuri.util.GameWorld;

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

		/** enum 名の文字列表現を返す。 */
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

	/** アイテム画像をレイヤー配列にセットし、使用レイヤー数を返す。 */
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

	/** 衝突判定対象タイプを返す。 */
	@Override
	@Transient
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	@Override
	/**
	 * Enable hit check.
	 *
	 * @return Enable hit check
	 */
	public boolean enableHitCheck() {
		if (bindBody != null)
			return false;
		return true;
	}

	/** 関連付けられているゆっくりを返す。 */
	public Yukkuri getBoundYukkuri() {
		return bindBody;
	}

	/** 衝突処理を行い、結果コードを返す。 */
	@Override
	public int objHitProcess(Entity o) {
		if (((Yukkuri) o).isDead())
			return 0;
		if (((Yukkuri) o).getCriticalDamageType() == CriticalDamageType.CUT)
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

	/** 毎ティックの状態更新を行う。 */
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

	/** ワールドからこのアイテムを除去する。 */
	@Override
	public void removeFromWorld() {
		if (bindBody != null) {
			bindBody.setLockmove(false);
			bindBody.setCanPullOrPush(false);
			bindBody = null;
		}
		GameWorld.get().getCurrentWorldState().getStickyPlates().remove(objId);
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
		GameWorld.get().getCurrentWorldState().getStickyPlates().put(objId, this);
		GameWorld.get().getCurrentWorldState().registerEntity(objId, this);
		objType = Type.PLATFORM;
		worldEntityType = WorldEntityKind.STICKYPLATE;
		interval = 5;
		if (!setupStickyPlate(this)) {
			GameWorld.get().getCurrentWorldState().getStickyPlates().remove(objId);
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

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
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

	/** 粘着板の裏面固定かを返す。 */
	public boolean isFixBack() {
		return fixBack;
	}

	/** ゆっくりを後ろ向きに固定するかをセットする。 */
	public void setFixBack(boolean fixBack) {
		this.fixBack = fixBack;
	}

	/** アイテムのランク（品質）を返す。 */
	public ItemRank getItemRank() {
		return itemRank;
	}

	/** アイテムのランク（品質）をセットする。 */
	public void setItemRank(ItemRank itemRank) {
		this.itemRank = itemRank;
	}

	/** 関連付けるゆっくりをセットする。 */
	public void setBoundYukkuri(Yukkuri bindBody) {
		this.bindBody = bindBody;
	}

}
