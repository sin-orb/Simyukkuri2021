package src.system;

/**
 * リソースを読み込むためのクラス.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import src.SimYukkuri;

public class ResourceUtil {

	private static String developRoot = null;
	/** リソースディレクトリ */
	public static final String DEFAULT_RESOURCE_DIR = "resources/";
	/** 日本語モードかどうか */
	public static final boolean IS_JP = Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage());
	
	private static ResourceUtil instance = new ResourceUtil();

	/**
	 * シングルトンを返却する.
	 * @return シングルトン
	 */
	public static ResourceUtil getInstance() {
		return instance;
	}

	private Map<String, String> property = new HashMap<String, String>();

	/**
	 * Singleton
	 */
	private ResourceUtil() {
		CodeSource codeSource = SimYukkuri.class.getProtectionDomain().getCodeSource();
		File jarFile = null;
		try {
			jarFile = new File(codeSource.getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		developRoot = jarFile.getParentFile().getPath();
		System.out.println("ResourceUtil.jarPath : " + developRoot);
		String top = "simyukkuri.";
		String bottom = ".properties";
		Locale locale = Locale.getDefault();
		String name = top + locale.getLanguage() + bottom;
		BufferedReader br = null;
		br = open(developRoot + File.separator + "/resources", name);
		// 各国語が読み込めてない場合英語を読み込む
		if (br == null) {
			name = top + "en" + bottom;
			br = open(developRoot+ File.separator + "/resources", name);
		}
		if (br == null) {
			System.out.println("Error Reading properties file.");
			return;
		}
		String line;
		try {
			while((line = br.readLine()) != null) {
				
				String[] lineSplitted = line.split("=");
				if (lineSplitted.length < 2) {
					continue;
				}
				String key = lineSplitted[0];
				String value = "";
				for (int i = 1; i < lineSplitted.length; i++) {
					value += lineSplitted[i];
					value += "=";
				}
				value = value.substring(0, value.length() - 1);
				property.put(key, value);
			};
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				//NOP.
			}
		}
	}

	private BufferedReader open(String path, String name) {
		BufferedReader br = null;
		boolean jarTry = true;
		// 開発データのチェック
		File file = new File(path + File.separator + name);
		if (file.exists()) {
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				jarTry = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (jarTry) {
			// ファイルが無かったらjarから読む
			InputStream is = SimYukkuri.class.getResourceAsStream(DEFAULT_RESOURCE_DIR + name);
			try {
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return br;
	}
	/**
	 * プロパティメッセージを取得する.
	 * @param key キー
	 * @return メッセージ
	 */
	public String read(String key) {
		return property.get(key);
	}
}
