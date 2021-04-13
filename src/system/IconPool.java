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

	各種アイコン、システム画像の保持


*/
public class IconPool {

	// ルートパス MODは非対応
	private static final String IMAGE_PATH = "images/icon/";

	// UIスキン
	public enum UISkin {
		NINE_SLICE_UP("button0.png"),
		NINE_SLICE_DOWN("button1.png"),
		;
		public String fileName;
		UISkin(String str) {
			fileName = str;
		}
	}

	// ボタンアイコン
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

	// ステータスアイコン
	public enum StatusIcon {
		PREDATOR("predator.png", "捕食種"),
		RAPER("raper.png", "れいぱー"),
		PENIPENICUT("penipenicut.png", "ぺにぺに去勢"),
		SPERM("sperm.png", "精子餡"),
		UNBABY("unbaby.png", "胎生去勢"),
		UNSHIT("unshit.png", "あにゃる閉鎖"),
		UNSTALK("unstalk.png", "茎去勢"),
		PHEROMONE("pheromone.png", "フェロモン"),
		UNUNSLAVE("ununslave.png", "うんうん奴隷")
		;
		public String fileName;
		public String help;
		StatusIcon(String f, String h) {
			fileName = f;
			help = h;
		}
	}

	// カーソル
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

	// ヘルプ
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

	public static BufferedImage[] getUISkinImageArray() {
		return uiSkinImage;
	}

	public static BufferedImage[] getButtonIconImageArray() {
		return buttonIconImage;
	}

	public static ImageIcon[] getStatusIconImageArray() {
		return statusIconImage;
	}

	public static BufferedImage[] getCursorIconImageArray() {
		return cursorIconImage;
	}

	public static BufferedImage[] getHelpIconImageArray() {
		return helpIconImage;
	}



	public static BufferedImage getUISkinImage(int idx) {
		return uiSkinImage[idx];
	}

	public static BufferedImage getButtonIconImage(int idx) {
		return buttonIconImage[idx];
	}

	public static ImageIcon getStatusIconImage(int idx) {
		return statusIconImage[idx];
	}

	public static BufferedImage getCursorIconImage(int idx) {
		return cursorIconImage[idx];
	}

	public static BufferedImage getHelpIconImage(int idx) {
		return helpIconImage[idx];
	}
}



