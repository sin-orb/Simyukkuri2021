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
import src.base.Effect;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.enums.EffectType;
import src.enums.ObjEXType;
import src.enums.Type;
import src.system.Cash;
import src.system.ResourceUtil;

/***************************************************
 * ディヒューザー
 */
public class Diffuser extends ObjEX {

	private static final long serialVersionUID = -1780241956081220439L;

	public static enum SteamType {
		ANTI_FUNGAL(ResourceUtil.getInstance().read("item_preventionmold"), 0),
		STEAM(ResourceUtil.getInstance().read("item_water"), 1),
		ORANGE(ResourceUtil.getInstance().read("item_orange"), 2),
		AGE_BOOST(ResourceUtil.getInstance().read("item_accel"), 3),
		AGE_STOP(ResourceUtil.getInstance().read("item_stop"), 4),
		ANTI_DOS(ResourceUtil.getInstance().read("item_preventiondos"), 5),
		ANTI_YU(ResourceUtil.getInstance().read("item_extermonation"), 6),
		PREDATOR(ResourceUtil.getInstance().read("item_preventionpredation"), 7),
		SUGER(ResourceUtil.getInstance().read("item_sugarwater"), 8),
		NOSLEEP(ResourceUtil.getInstance().read("item_preventionsleep"), 9),
		HYBRID(ResourceUtil.getInstance().read("item_hybridize"), 9),
		RAPIDPREGNANT(ResourceUtil.getInstance().read("item_prophylactic"), 9),
		ANTI_NONYUKKURI(ResourceUtil.getInstance().read("item_antinyd"), 9),
		ENDLESS_FURIFURI(ResourceUtil.getInstance().read("item_infimorun"), 9),
		;

		private String name;
		private int steamColor;

		SteamType(String name, int col) {
			this.name = name;
			this.steamColor = col;
		}

		public String toString() {
			return name;
		}

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
		return images[2];
	}

	/** 境界線の取得 */
	public static Rectangle4y getBounding() {
		return boundary;
	}

	@Override
	public void upDate() {
		if (!enabled)
			return;
		if (getAge() % 2400 == 0) {
			Cash.addCash(-getCost());
		}

		if (getAge() % 40 == 0) {
			if (steamType[steamNum]) {
				Effect e = SimYukkuri.mypane.getTerrarium().addEffect(EffectType.STEAM, x, y, z + getH() >> 3,
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

	@Override
	public void removeListData() {
		SimYukkuri.world.getCurrentMap().getDiffuser().remove(objId);
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

		SimYukkuri.world.getCurrentMap().getDiffuser().put(objId, this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.DIFFUSER;
		value = 15000;
		cost = 100;

		boolean ret = setupDiffuser(this, false);
		if (!ret) {
			SimYukkuri.world.getCurrentMap().getDiffuser().remove(objId);
		}
	}

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

		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel,
				ResourceUtil.getInstance().read("item_diffusersettings"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (dlgRet == JOptionPane.OK_OPTION) {
			for (int i = 0; i < checkBox.length; i++) {
				d.steamType[i] = checkBox[i].isSelected();
			}
			ret = true;
		}
		return ret;
	}

	public int getSteamNum() {
		return steamNum;
	}

	public void setSteamNum(int steamNum) {
		this.steamNum = steamNum;
	}

	public void setSteamType(boolean[] steamType) {
		this.steamType = steamType;
	}

}
