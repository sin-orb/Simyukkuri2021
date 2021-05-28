package src.item;


import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import src.SimYukkuri;
import src.base.Effect;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.enums.EffectType;
import src.enums.ObjEXType;
import src.enums.Type;
import src.system.Cash;

/***************************************************
ディヒューザー
*/
public class Diffuser extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static enum SteamType {
        ANTI_FUNGAL("防カビ剤", 0),
        STEAM("真水", 1),
        ORANGE("オレンジ", 2),
        AGE_BOOST("成長促進", 3),
        AGE_STOP("成長抑制", 4),
        ANTI_DOS("ドス化抑制", 5),
        ANTI_YU("ゆっくり駆除剤", 6),
        PREDATOR("捕食防止", 7),
        SUGER("砂糖水", 8),
        NOSLEEP("睡眠妨害", 9),
        HYBRID("ハイブリッド化薬", 9),
        RAPIDPREGNANT("早産化薬", 9),
        ANTI_NONYUKKURI("非ゆっくり症防止薬", 9),
        ENDLESS_FURIFURI("無限もるんもるん", 9),
		;
        private String name;
        private int steamColor;
        SteamType(String name, int col) { this.name = name; this.steamColor = col; }
        public String toString() { return name; }
        public int getColor() { return steamColor; }
	}

	public static final int hitCheckObjType = 0;
	private static BufferedImage images[] = new BufferedImage[3];
	private static Rectangle boundary = new Rectangle();

	private boolean[] steamType = new boolean[SteamType.values().length];
	private int steamNum = 0;
	/** 画像ロード */
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "diffuser" + File.separator + "diffuser.png");
		images[1] = ModLoader.loadItemImage(loader, "diffuser" + File.separator + "diffuser_off.png");
		images[2] = ModLoader.loadItemImage(loader, "diffuser" + File.separator + "shadow.png");		
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height - 1;
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
		return images[2];
	}
	/**境界線の取得*/
	public static Rectangle getBounding() {
		return boundary;
	}

	@Override
	public void upDate() {
		if(!enabled) return;
		if ( getAge() % 2400 == 0 ) {
			Cash.addCash(-getCost());
		}
		
		if ( getAge() % 40 == 0 ) {
			if(steamType[steamNum]) {
				Effect e = SimYukkuri.mypane.terrarium.addEffect(EffectType.STEAM, x, y, z + getH() >> 3,
						0, 0, -1, false, 30, 0, false, false, false);
					
				e.setAnimeFrame(SteamType.values()[steamNum].getColor());
			}
			do {
				steamNum++;
				if(steamNum == steamType.length) {
					steamNum = 0;
					break;
				}
			} while(!steamType[steamNum]);
		}
	}

	@Override
	public void removeListData(){
		SimYukkuri.world.getCurrentMap().diffuser.remove(this);
	}
	/**
	 * 蒸気タイプを取得する.
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
		
		List<Diffuser> list = SimYukkuri.world.getCurrentMap().diffuser;
		list.add(this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.DIFFUSER;
		value = 15000;
		cost = 100;

		boolean ret = setupDiffuser(this, false);
		if(!ret) {
			list.remove(this);
		}
	}

	/** 設定メニュー */
	public static boolean setupDiffuser(Diffuser d, boolean init) {
		
		JPanel mainPanel = new JPanel();
		JCheckBox[] checkBox = new JCheckBox[SteamType.values().length];
		boolean ret = false;
		
		mainPanel.setLayout(new GridLayout(7, 2));
		mainPanel.setPreferredSize(new Dimension(260, 180));
		
		for(int i = 0; i < checkBox.length; i++) {
			JPanel panel = new JPanel();
			checkBox[i] = new JCheckBox(SteamType.values()[i].toString());
			
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));
			panel.add(checkBox[i]);
			if(init) checkBox[i].setSelected(d.steamType[i]);
			
			mainPanel.add(panel);
		}

		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, "ディフューザー設定", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		if(dlgRet == JOptionPane.OK_OPTION) {
			for(int i = 0; i < checkBox.length; i++) {
				d.steamType[i] = checkBox[i].isSelected();
			}
			ret = true;
		}
		return ret;
	}
}


