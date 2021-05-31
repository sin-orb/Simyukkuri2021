package src.system;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import src.draw.MyPane;



/**********************************************
 * 各種アイコン、システム画像の保持
 */
public class IconPool {

	// ルートパス MODは非対応
	private static final String IMAGE_PATH = "images/icon/";

	/** UIスキン */
	public enum UISkin {
		NINE_SLICE_UP("button0.png"),
		NINE_SLICE_DOWN("button1.png"),
		;
		public String fileName;
		UISkin(String str) {
			fileName = str;
		}
	}

	/** ボタンアイコン */
	public enum ButtonIcon {
		HELP_OFF("help_off.png"),
		OPTION("option.png"),
		PIN("pin.png"),
		POPUP_OFF("popup_off.png"),
		TARGET("target.png")
		;
		public String fileName;
		ButtonIcon(String str) {
			fileName = str;
		}
	}

	/** ステータスアイコン */
	public enum StatusIcon {
		PREDATOR("predator.png", ResourceUtil.getInstance().read("system_predator")),
		RAPER("raper.png", ResourceUtil.getInstance().read("system_raper")),
		PENIPENICUT("penipenicut.png", ResourceUtil.getInstance().read("system_penicas")),
		SPERM("sperm.png", ResourceUtil.getInstance().read("system_sperm")),
		UNBABY("unbaby.png", ResourceUtil.getInstance().read("system_babycas")),
		UNSHIT("unshit.png", ResourceUtil.getInstance().read("system_analclose")),
		UNSTALK("unstalk.png", ResourceUtil.getInstance().read("system_stalkcas")),
		PHEROMONE("pheromone.png", ResourceUtil.getInstance().read("system_pheromone")),
		UNUNSLAVE("ununslave.png", ResourceUtil.getInstance().read("system_ununslave"))
		;
		public String fileName;
		public String help;
		StatusIcon(String f, String h) {
			fileName = f;
			help = h;
		}
	}

	/** カーソル */
	public enum CursorIcon {
		CTRL("ctrl.png"),
		CUR_LB("cur_lb.png"),
		CUR_LT("cur_lt.png"),
		CUR_RB("cur_rb.png"),
		CUR_RT("cur_rt.png"),
		MOUSE_L("mouse_l.png"),
		MOUSE_R("mouse_r.png"),
		SEL_0("sel_0.png"),
		SEL_1("sel_1.png"),
		SEL_2("sel_2.png"),
		SEL_3("sel_3.png"),
		SHIFT("shift.png")
		;
		public String fileName;
		CursorIcon(String str) {
			fileName = str;
		}
	}

	/** ヘルプ */
	public enum HelpIcon {
		CTRL("ctrl.png"),
		MOUSE_L("mouse_l.png"),
		MOUSE_R("mouse_r.png"),
		SHIFT("shift.png")
		;
		public String fileName;
		HelpIcon(String str) {
			fileName = str;
		}
	}


	private static BufferedImage[] uiSkinImage = new BufferedImage[UISkin.values().length];
	private static BufferedImage[] buttonIconImage = new BufferedImage[ButtonIcon.values().length];
	private static ImageIcon[] statusIconImage = new ImageIcon[StatusIcon.values().length];
	private static BufferedImage[] cursorIconImage = new BufferedImage[CursorIcon.values().length];
	private static BufferedImage[] helpIconImage = new BufferedImage[HelpIcon.values().length];
	/** イメージのロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		MediaTracker mt = new MediaTracker((MyPane)io);

		for(UISkin i :UISkin.values()) {
			uiSkinImage[i.ordinal()] = ImageIO.read(loader.getResourceAsStream(IMAGE_PATH + i.fileName));
			mt.addImage(uiSkinImage[i.ordinal()], 0);
		}
		for(ButtonIcon i :ButtonIcon.values()) {
			buttonIconImage[i.ordinal()] = ImageIO.read(loader.getResourceAsStream(IMAGE_PATH + i.fileName));
			mt.addImage(buttonIconImage[i.ordinal()], 0);
		}
		for(StatusIcon i :StatusIcon.values()) {
			Image img = ImageIO.read(loader.getResourceAsStream(IMAGE_PATH + i.fileName));
			statusIconImage[i.ordinal()] = new ImageIcon(img);
			mt.addImage(img, 0);
		}
		for(CursorIcon i :CursorIcon.values()) {
			cursorIconImage[i.ordinal()] = ImageIO.read(loader.getResourceAsStream(IMAGE_PATH + i.fileName));
			mt.addImage(cursorIconImage[i.ordinal()], 0);
		}
		for(HelpIcon i :HelpIcon.values()) {
			helpIconImage[i.ordinal()] = ImageIO.read(loader.getResourceAsStream(IMAGE_PATH + i.fileName));
			mt.addImage(helpIconImage[i.ordinal()], 0);
		}

		try {
			mt.waitForAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * UIスキンイメージの取得
	 * @return UIスキンイメージ
	 */
	public static BufferedImage[] getUISkinImageArray() {
		return uiSkinImage;
	}
	/**
	 * ボタンアイコンイメージの取得
	 * @return ボタンアイコンイメージ
	 */
	public static BufferedImage[] getButtonIconImageArray() {
		return buttonIconImage;
	}
	/**
	 * ステータスアイコンのイメージの取得
	 * @return ステータスアイコンのイメージ
	 */
	public static ImageIcon[] getStatusIconImageArray() {
		return statusIconImage;
	}
	/**
	 * カーソルアイコンのイメージの取得
	 * @return カーソルアイコンのイメージ
	 */
	public static BufferedImage[] getCursorIconImageArray() {
		return cursorIconImage;
	}
	/**
	 * ヘルプアイコンのイメージの取得
	 * @return ヘルプアイコンのイメージ
	 */
	public static BufferedImage[] getHelpIconImageArray() {
		return helpIconImage;
	}
	/**
	 * UIスキンイメージの取得
	 * @param idx インデックス
	 * @return UIスキンイメージ
	 */
	public static BufferedImage getUISkinImage(int idx) {
		return uiSkinImage[idx];
	}
	/**
	 * ボタンアイコンのイメージの取得
	 * @param idx インデックス
	 * @return ボタンアイコンのイメージ
	 */
	public static BufferedImage getButtonIconImage(int idx) {
		return buttonIconImage[idx];
	}
	/**
	 * ステータスアイコンのイメージの取得
	 * @param idx インデックス
	 * @return ステータスアイコンのイメージ
	 */
	public static ImageIcon getStatusIconImage(int idx) {
		return statusIconImage[idx];
	}
	/**
	 * カーソルアイコンのイメージの取得
	 * @param idx インデックス
	 * @return カーソルアイコンのイメージ
	 */
	public static BufferedImage getCursorIconImage(int idx) {
		return cursorIconImage[idx];
	}
	/**
	 * ヘルプアイコンのイメージの取得
	 * @param idx インデックス
	 * @return ヘルプアイコンのイメージ
	 */
	public static BufferedImage getHelpIconImage(int idx) {
		return helpIconImage[idx];
	}
}



