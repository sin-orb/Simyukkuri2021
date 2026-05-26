package org.simyukkuri.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * ImageSource interface.
 */
public interface ImageSource {
	/**
	 * @param input 入力ストリーム
	 * @return 読み込んだ画像
	 * @throws IOException 読み込み失敗時
	 */
	BufferedImage read(InputStream input) throws IOException;

	/**
	 * @param file 画像ファイル
	 * @return 読み込んだ画像
	 * @throws IOException 読み込み失敗時
	 */
	BufferedImage read(File file) throws IOException;
}
