package org.simyukkuri.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public final class GameImages {
	private static ImageSource override;

	private GameImages() {
	}

	public static BufferedImage read(InputStream input) throws IOException {
		if (override != null) {
			return override.read(input);
		}
		return ImageIO.read(input);
	}

	public static BufferedImage read(File file) throws IOException {
		if (override != null) {
			return override.read(file);
		}
		return ImageIO.read(file);
	}

	public static void setOverride(ImageSource source) {
		override = source;
	}

	public static void clearOverride() {
		override = null;
	}
}
