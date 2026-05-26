package org.simyukkuri.system;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import javax.swing.ImageIcon;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.util.GameImages;
import org.simyukkuri.util.GameText;

/**
 * 各種アイコン、システム画像の保持。
 */
public class IconPool {

	// ルートパス MODは非対応
	private static final String IMAGE_PATH = "images/icon/";

	/** UIスキン */
	public enum UiSkin {
		NINE_SLICE_UP("button0.png"),
		NINE_SLICE_DOWN("button1.png"),
		;

		private final String fileName;

		UiSkin(String str) {
			fileName = str;
		}

		/**
		 * このUIスキン要素に対応する画像ファイル名を返す。
		 *
		 * @return 画像ファイル名（例: "button0.png"）
		 */
		public String getFileName() {
			return fileName;
		}
	}

	/** ボタンアイコン */
	public enum ButtonIcon {
		HELP_OFF("help_off.png"),
		OPTION("option.png"),
		PIN("pin.png"),
		POPUP_OFF("popup_off.png"),
		TARGET("target.png");

		private final String fileName;

		ButtonIcon(String str) {
			fileName = str;
		}

		/**
		 * このボタンアイコンに対応する画像ファイル名を返す。
		 *
		 * @return 画像ファイル名（例: "help_off.png"）
		 */
		public String getFileName() {
			return fileName;
		}
	}

	/** ステータスアイコン */
	public enum StatusIcon {
		PREDATOR("predator.png", GameText.read("system_predator")),
		RAPER("raper.png", GameText.read("system_raper")),
		PENIPENICUT("penipenicut.png", GameText.read("system_penicas")),
		SPERM("sperm.png", GameText.read("system_sperm")),
		UNBABY("unbaby.png", GameText.read("system_babycas")),
		UNSHIT("unshit.png", GameText.read("system_analclose")),
		UNSTALK("unstalk.png", GameText.read("system_stalkcas")),
		PHEROMONE("pheromone.png", GameText.read("system_pheromone")),
		UNUNSLAVE("ununslave.png", GameText.read("system_ununslave"));

		private final String fileName;
		private final String help;

		StatusIcon(String f, String h) {
			fileName = f;
			help = h;
		}

		/**
		 * このステータスアイコンに対応する画像ファイル名を返す。
		 *
		 * @return 画像ファイル名（例: "predator.png"）
		 */
		public String getFileName() {
			return fileName;
		}

		/**
		 * このステータスアイコンのツールチップ用説明文を返す。
		 *
		 * @return ローカライズ済みの説明文字列
		 */
		public String getHelp() {
			return help;
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
		SHIFT("shift.png");

		private final String fileName;

		CursorIcon(String str) {
			fileName = str;
		}

		/**
		 * このカーソルアイコンに対応する画像ファイル名を返す。
		 *
		 * @return 画像ファイル名（例: "ctrl.png"）
		 */
		public String getFileName() {
			return fileName;
		}
	}

	/** ヘルプ */
	public enum HelpIcon {
		CTRL("ctrl.png"),
		MOUSE_L("mouse_l.png"),
		MOUSE_R("mouse_r.png"),
		SHIFT("shift.png");

		private final String fileName;

		HelpIcon(String str) {
			fileName = str;
		}

		/**
		 * このヘルプアイコンに対応する画像ファイル名を返す。
		 *
		 * @return 画像ファイル名（例: "ctrl.png"）
		 */
		public String getFileName() {
			return fileName;
		}
	}

	private static BufferedImage[] uiSkinImage = new BufferedImage[UiSkin.values().length];
	private static BufferedImage[] buttonIconImage = new BufferedImage[ButtonIcon.values().length];
	private static ImageIcon[] statusIconImage = new ImageIcon[StatusIcon.values().length];
	private static BufferedImage[] cursorIconImage = new BufferedImage[CursorIcon.values().length];
	private static BufferedImage[] helpIconImage = new BufferedImage[HelpIcon.values().length];

	/** イメージのロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		MediaTracker mt = new MediaTracker((MyPane) io);

		for (UiSkin i : UiSkin.values()) {
			uiSkinImage[i.ordinal()] = GameImages.read(loader.getResourceAsStream(IMAGE_PATH + i.getFileName()));
			mt.addImage(uiSkinImage[i.ordinal()], 0);
		}
		for (ButtonIcon i : ButtonIcon.values()) {
			buttonIconImage[i.ordinal()] = GameImages.read(loader.getResourceAsStream(IMAGE_PATH + i.getFileName()));
			mt.addImage(buttonIconImage[i.ordinal()], 0);
		}
		for (StatusIcon i : StatusIcon.values()) {
			Image img = GameImages.read(loader.getResourceAsStream(IMAGE_PATH + i.getFileName()));
			statusIconImage[i.ordinal()] = new ImageIcon(img);
			mt.addImage(img, 0);
		}
		for (CursorIcon i : CursorIcon.values()) {
			cursorIconImage[i.ordinal()] = GameImages.read(loader.getResourceAsStream(IMAGE_PATH + i.getFileName()));
			mt.addImage(cursorIconImage[i.ordinal()], 0);
		}
		for (HelpIcon i : HelpIcon.values()) {
			helpIconImage[i.ordinal()] = GameImages.read(loader.getResourceAsStream(IMAGE_PATH + i.getFileName()));
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
	 * 
	 * @return UIスキンイメージ
	 */
	public static BufferedImage[] getUiSkinImageArray() {
		return uiSkinImage;
	}

	/**
	 * ボタンアイコンイメージの取得
	 * 
	 * @return ボタンアイコンイメージ
	 */
	public static BufferedImage[] getButtonIconImageArray() {
		return buttonIconImage;
	}

	/**
	 * ステータスアイコンのイメージの取得
	 * 
	 * @return ステータスアイコンのイメージ
	 */
	public static ImageIcon[] getStatusIconImageArray() {
		return statusIconImage;
	}

	/**
	 * カーソルアイコンのイメージの取得
	 * 
	 * @return カーソルアイコンのイメージ
	 */
	public static BufferedImage[] getCursorIconImageArray() {
		return cursorIconImage;
	}

	/**
	 * ヘルプアイコンのイメージの取得
	 * 
	 * @return ヘルプアイコンのイメージ
	 */
	public static BufferedImage[] getHelpIconImageArray() {
		return helpIconImage;
	}

	/**
	 * UIスキンイメージの取得
	 * 
	 * @param idx インデックス
	 * @return UIスキンイメージ
	 */
	public static BufferedImage getUiSkinImage(int idx) {
		return uiSkinImage[idx];
	}

	/**
	 * ボタンアイコンのイメージの取得
	 * 
	 * @param idx インデックス
	 * @return ボタンアイコンのイメージ
	 */
	public static BufferedImage getButtonIconImage(int idx) {
		return buttonIconImage[idx];
	}

	/**
	 * ステータスアイコンのイメージの取得
	 * 
	 * @param idx インデックス
	 * @return ステータスアイコンのイメージ
	 */
	public static ImageIcon getStatusIconImage(int idx) {
		return statusIconImage[idx];
	}

	/**
	 * カーソルアイコンのイメージの取得
	 * 
	 * @param idx インデックス
	 * @return カーソルアイコンのイメージ
	 */
	public static BufferedImage getCursorIconImage(int idx) {
		return cursorIconImage[idx];
	}

	/**
	 * ヘルプアイコンのイメージの取得
	 * 
	 * @param idx インデックス
	 * @return ヘルプアイコンのイメージ
	 */
	public static BufferedImage getHelpIconImage(int idx) {
		return helpIconImage[idx];
	}
}
