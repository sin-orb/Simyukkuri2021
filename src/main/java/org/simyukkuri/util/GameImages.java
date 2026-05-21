package org.simyukkuri.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * GameImages.
 */
public final class GameImages {
	private static ImageSource override;

	private GameImages() {
	}

	/** InputStream から画像を読み込んで返す。 */
	public static BufferedImage read(InputStream input) throws IOException {
		if (override != null) {
			return override.read(input);
		}
		return ImageIO.read(input);
	}

	/** ファイルから画像を読み込んで返す。 */
	public static BufferedImage read(File file) throws IOException {
		if (override != null) {
			return override.read(file);
		}
		return ImageIO.read(file);
	}

	/** テスト用の注入ソースをセットする。 */
	public static void setOverride(ImageSource source) {
		override = source;
	}

	/** テスト用注入ソースをクリアする。 */
	public static void clearOverride() {
		override = null;
	}
}
