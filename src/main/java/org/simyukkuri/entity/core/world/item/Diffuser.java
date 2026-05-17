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

import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.entity.core.effect.Effect;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.enums.EffectType;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.system.Cash;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.GameView;
import org.simyukkuri.util.GameWorld;

/***************************************************
 * ディヒューザー
 */
public class Diffuser extends WorldEntity {

	private static final long serialVersionUID = -1780241956081220439L;

	/**
	 * SteamType enum type.
	 */
	public static enum SteamType {
		ANTI_FUNGAL(GameText.read("item_preventionmold"), 0),
		STEAM(GameText.read("item_water"), 1),
		ORANGE(GameText.read("item_orange"), 2),
		AGE_BOOST(GameText.read("item_accel"), 3),
		AGE_STOP(GameText.read("item_stop"), 4),
		ANTI_DOS(GameText.read("item_preventiondos"), 5),
		ANTI_YU(GameText.read("item_extermonation"), 6),
		PREDATOR(GameText.read("item_preventionpredation"), 7),
		SUGER(GameText.read("item_sugarwater"), 8),
		NOSLEEP(GameText.read("item_preventionsleep"), 9),
		HYBRID(GameText.read("item_hybridize"), 9),
		RAPIDPREGNANT(GameText.read("item_prophylactic"), 9),
		ANTI_NONYUKKURI(GameText.read("item_antinyd"), 9),
		ENDLESS_FURIFURI(GameText.read("item_infimorun"), 9),
		;

		private String name;
		private int steamColor;

		SteamType(String name, int col) {
			this.name = name;
			this.steamColor = col;
		}

		/** enum 名の文字列表現を返す。 */
		public String toString() {
			return name;
		}

		/** スチームの色コードを返す。 */
		public int getColor() {
			return steamColor;
		}
	}

	public static final int hitCheckObjType = 0;
	private static BufferedImage images[] = new BufferedImage[3];
	private static Rectangle4y boundary = new Rectangle4y();

	private boolean[] steamType = new boolean[SteamType.values().length];
	private int steamNum = 0;

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "diffuser" + File.separator + "diffuser.png");
		images[1] = ModLoader.loadItemImage(loader, "diffuser" + File.separator + "diffuser_off.png");
		images[2] = ModLoader.loadItemImage(loader, "diffuser" + File.separator + "shadow.png");
		boundary.setWidth(images[0].getWidth(io));
		boundary.setHeight(images[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() - 1);
	}

	/** アイテム画像をレイヤー配列にセットし、使用レイヤー数を返す。 */
	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (enabled) {
			layer[0] = images[0];
		} else {
			layer[0] = images[1];
		}
		return 1;
	}

	/** アイテムの影画像を返す。 */
	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return images[2];
	}

	/** 境界線の取得 */
	public static Rectangle4y getBounding() {
		return boundary;
	}

	/** 毎ティックの状態更新を行う。 */
	@Override
	public void upDate() {
		if (!enabled)
			return;
		if (getAge() % 2400 == 0) {
			Cash.addCash(-getCost());
		}

		if (getAge() % 40 == 0) {
			if (steamType[steamNum]) {
				Effect e = GameView.addEffect(EffectType.STEAMED, x, y, z + getH() >> 3,
						0, 0, -1, false, 30, 0, false, false, false);

				e.setAnimeFrame(SteamType.values()[steamNum].getColor());
			}
			do {
				steamNum++;
				if (steamNum == steamType.length) {
					steamNum = 0;
					break;
				}
			} while (!steamType[steamNum]);
		}
	}

	/** ワールドからこのアイテムを除去する。 */
	@Override
	public void removeFromWorld() {
		GameWorld.get().getCurrentWorldState().getDiffusers().remove(objId);
	}

	/**
	 * 蒸気タイプを取得する.
	 * 
	 * @return 蒸気タイプ
	 */
	public boolean[] getSteamType() {
		return steamType;
	}

	/** コンストラクタ */
	public Diffuser(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), 8);

		GameWorld.get().getCurrentWorldState().getDiffusers().put(objId, this);
		GameWorld.get().getCurrentWorldState().registerEntity(objId, this);
		objType = Type.OBJECT;
		worldEntityType = WorldEntityKind.DIFFUSER;
		value = 15000;
		cost = 100;

		boolean ret = setupDiffuser(this, false);
		if (!ret) {
			GameWorld.get().getCurrentWorldState().getDiffusers().remove(objId);
		}
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public Diffuser() {

	}

	/** 設定メニュー */
	public static boolean setupDiffuser(Diffuser d, boolean init) {

		JPanel mainPanel = new JPanel();
		JCheckBox[] checkBox = new JCheckBox[SteamType.values().length];
		boolean ret = false;

		mainPanel.setLayout(new GridLayout(7, 2));
		mainPanel.setPreferredSize(new Dimension(260, 180));

		for (int i = 0; i < checkBox.length; i++) {
			JPanel panel = new JPanel();
			checkBox[i] = new JCheckBox(SteamType.values()[i].toString());

			panel.setLayout(new FlowLayout(FlowLayout.LEFT));
			panel.add(checkBox[i]);
			if (init)
				checkBox[i].setSelected(d.steamType[i]);

			mainPanel.add(panel);
		}

		int dlgRet = JOptionPane.showConfirmDialog(GameView.getDialogParent(), mainPanel,
				GameText.read("item_diffusersettings"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (dlgRet == JOptionPane.OK_OPTION) {
			for (int i = 0; i < checkBox.length; i++) {
				d.steamType[i] = checkBox[i].isSelected();
			}
			ret = true;
		}
		return ret;
	}

	/** 同時に噴出するスチーム数を返す。 */
	public int getSteamNum() {
		return steamNum;
	}

	/** 同時に噴出するスチーム数をセットする。 */
	public void setSteamNum(int steamNum) {
		this.steamNum = steamNum;
	}

	/** 各スチームタイプの有効フラグ配列をセットする。 */
	public void setSteamType(boolean[] steamType) {
		this.steamType = steamType;
	}

}
