package org.simyukkuri.util;

/**
 * TextSource interface.
 */
public interface TextSource {
	/**
	 * 指定キーの文字列を返す。
	 *
	 * @param property 参照するキー
	 * @return キーに対応する文字列
	 */
	String read(String property);
}
