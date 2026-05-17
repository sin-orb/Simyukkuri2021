package org.simyukkuri.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * ImageSource interface.
 */
public interface ImageSource {
	BufferedImage read(InputStream input) throws IOException;

	BufferedImage read(File file) throws IOException;
}
