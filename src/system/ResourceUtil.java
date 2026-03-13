package src.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Singleton class to read localized "properties" files.
 */
public class ResourceUtil {
	private static Locale thisLocale = Locale.getDefault();
	private static Locale enLocale = new Locale("en");
	private static Locale jaLocale = new Locale("ja");
	public static final boolean IS_JP = thisLocale.getLanguage().equals(jaLocale.getLanguage());
	private static ResourceUtil instance;
	private static Map<String, Map<String, String>> strings = new HashMap<String, Map<String, String>>();

	public static ResourceUtil getInstance() {
		if (null == instance) {
			instance = new ResourceUtil();
		}
		return instance;
	}

	private ResourceUtil() {
		String classpath = System.getProperty("java.class.path");
		ClassLoader loader = this.getClass().getClassLoader();
		BufferedReader reader;

		/* Read from JAR via ClassLoader */
		reader = this.getReader(loader, jaLocale.getLanguage());
		strings.put(jaLocale.getLanguage(), this.readStrings(reader));
		if (reader != null) {
			try {
				reader.close();
			} catch (Exception e) {
			}
		}

		reader = this.getReader(loader, enLocale.getLanguage());
		strings.put(enLocale.getLanguage(), this.readStrings(reader));
		if (reader != null) {
			try {
				reader.close();
			} catch (Exception e) {
			}
		}

		/* Read from file system via classpath entries */
		String[] classpaths = classpath.split(java.io.File.pathSeparator);
		if (classpaths.length == 0 || (classpaths.length == 1 && classpaths[0].trim().length() == 0)) {
			CodeSource codeSource = ResourceUtil.class.getProtectionDomain().getCodeSource();
			if (codeSource != null && codeSource.getLocation() != null) {
				try {
					File jarFile = new File(codeSource.getLocation().toURI().getPath());
					if (jarFile.getParentFile() != null) {
						classpaths = new String[] { jarFile.getParentFile().getPath() };
					}
				} catch (URISyntaxException e) {
				}
			}
		}

		for (String cp : classpaths) {
			if (cp == null || cp.trim().isEmpty())
				continue;
			File f = new File(cp);
			String path = f.isFile() ? f.getParent() : cp;
			if (path == null)
				continue;

			try (BufferedReader br = this.getReader(path, thisLocale.getLanguage())) {
				if (null != br) {
					Map<String, String> map = this.readStrings(br);
					if (map != null) {
						strings.put(thisLocale.getLanguage(), map);
					}
				}
			} catch (Exception e) {
			}
		}
	}

	private BufferedReader getReader(ClassLoader loader, String lang) {
		try {
			String path = "resources/simyukkuri." + lang + ".properties";
			InputStream input = loader.getResourceAsStream(path);
			if (input == null)
				return null;
			return new BufferedReader(new InputStreamReader(input, "UTF-8"));
		} catch (Exception e) {
			return null;
		}
	}

	private BufferedReader getReader(String path, String lang) {
		try {
			String p = path + "/resources/simyukkuri." + lang + ".properties";
			File f = new File(p);
			if (!f.exists())
				return null;
			InputStream input = new FileInputStream(p);
			return new BufferedReader(new InputStreamReader(input, "UTF-8"));
		} catch (Exception e) {
			return null;
		}
	}

	private Map<String, String> readStrings(BufferedReader reader) {
		Map<String, String> props = new HashMap<String, String>();
		if (reader == null)
			return props;
		try {
			for (String line = reader.readLine(); null != line; line = reader.readLine()) {
				String[] split = line.split("=");
				if (split.length < 2)
					continue;
				String key = split[0].trim();
				String value = String.join("=", Arrays.copyOfRange(split, 1, split.length));
				props.put(key, value);
			}
		} catch (Exception e) {
		}
		return props;
	}

	public String read(String property) {
		String lang = thisLocale.getLanguage();
		Map<String, String> props = strings.get(lang);
		Map<String, String> en = strings.get(enLocale.getLanguage());
		Map<String, String> ja = strings.get(jaLocale.getLanguage());
		if (null == props)
			return IS_JP ? ja.get(property) : en.get(property);
		String v = props.get(property);
		if (null == v)
			return IS_JP ? ja.get(property) : en.get(property);
		return v;
	}
}
