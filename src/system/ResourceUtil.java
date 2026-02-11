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

import src.SimYukkuri;

/**
 * Singleton class to read localized "properties" files.
 *
 * Each "properties" file contains the localized strings used by the
 * game for menus, etc.
 *
 * These files are read from the local file system, in a directory
 * called "resources" located in the same place as the game's JAR
 * file.
 * 
 * If a string is not found, it is searched in the fallback locales,
 * so that something is always shown, even when if in a different
 * language.
 * 
 * The fallbacks are read from the files provided directly inside the
 * final JAR file, but they can be overridden by "properties" files
 * for those fallback locales.
 */
public class ResourceUtil {
	/**
	 * The current locale.
	 */
	private static Locale thisLocale = Locale.getDefault();

	/**
	 * The English locale, used as the first fallback.
	 */
	private static Locale enLocale = new Locale("en");

	/**
	 * The Japanese locale, the default (= complete) locale used as
	 * the last resort fallback.
	 */
	private static Locale jaLocale = new Locale("ja");

	/**
	 * If the current locale is the Japanese one.
	 */
	public static final boolean IS_JP = thisLocale.getLanguage().equals(jaLocale.getLanguage());

	/**
	 * The singleton instance.
	 */
	private static ResourceUtil instance;

	/**
	 * Map of maps mapping a locale name to a map of localized strings.
	 */
	private static Map<String, Map<String, String>> strings = new HashMap<String, Map<String, String>>();

	/**
	 * @return the singleton instance
	 */
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

		/* Read the Japanese strings from the JAR file. */
		reader = this.getReader(loader, jaLocale.getLanguage());
		strings.put(jaLocale.getLanguage(), this.readStrings(reader));
		if (reader != null) {
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/* Read the English strings from the JAR file. */
		reader = this.getReader(loader, enLocale.getLanguage());
		strings.put(enLocale.getLanguage(), this.readStrings(reader));
		if (reader != null) {
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/* Read the current locale's strings from the file system. */
		classpath = classpath.substring(0, classpath.lastIndexOf(java.io.File.separator) + 1);
		// classpaths may contain multiple "classpaths" separated by ";".
		String[] classpaths = classpath.split(";");
		if (classpaths.length == 0 || classpaths[0].trim().length() == 0) {
			CodeSource codeSource = SimYukkuri.class.getProtectionDomain().getCodeSource();
			File jarFile = null;
			try {
				jarFile = new File(codeSource.getLocation().toURI().getPath());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			String[] tmp = {jarFile.getParentFile().getPath()};
			classpaths = tmp;
		}
		for (String cp : classpaths) {
			try (BufferedReader br = this.getReader(cp, thisLocale.getLanguage())){
				if (null != br) {
					Map<String, String> map = this.readStrings(br);
					if (map != null) {
						strings.put(thisLocale.getLanguage(), map);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Open a reader to a resource located inside the JAR file.
	 *
	 * @param loader used to open a stream to a location inside the JAR
	 * @param lang the locale to read for
	 * @return a new buffered reader
	 */
	private BufferedReader getReader(ClassLoader loader, String lang) {
		try {
			String path = "resources/simyukkuri." + lang + ".properties";
			InputStream input = loader.getResourceAsStream(path);
			if (input == null) {
				return null;
			}
			return new BufferedReader(new InputStreamReader(input, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Open a reader to a resource located on the file system
	 *
	 * @param path location of the "resources" directory
	 * @param lang the locale to read for
	 * @return a new buffered reader or null if an error occurred
	 */
	private BufferedReader getReader(String path, String lang) {
		try {
			String p = path + "/resources/simyukkuri." + lang + ".properties";
			InputStream input = new FileInputStream(p);
			return new BufferedReader(new InputStreamReader(input, "UTF-8"));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Read all the strings located in the "properties" file.
	 *
	 * @param reader the reader to use to read each string
	 * @return a map of strings
	 */
	private Map<String, String> readStrings(BufferedReader reader) {
		Map<String, String> props = new HashMap<String, String>();

		if (reader == null) {
			return props;
		}

		try {
			for (String line = reader.readLine(); null != line; line = reader.readLine()) {
				String[] split = line.split("=");
				if (split.length < 2) {
					continue;
				}

				String key = split[0].trim();
				String value = String.join("=", Arrays.copyOfRange(split, 1, split.length));

				props.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return props;
	}

	/**
	 * Fetch the value of a certain string for the current locale, or
	 * a fallback if missing.
	 *
	 * @param property the string to read
	 * @return the localized string
	 */
	public String read(String property) {
		String lang = thisLocale.getLanguage();
		Map<String, String> props = strings.get(lang);
		Map<String, String> en = strings.get(enLocale.getLanguage());
		Map<String, String> ja = strings.get(jaLocale.getLanguage());

		if (null == props) {
			if (IS_JP) {
				return ja.get(property); 
			}
			return en.get(property);
		}

		String v = props.get(property);
		if (null == v) {
			if (IS_JP) {
				return ja.get(property); 
			}
			return en.get(property);
		}
		return v;
	}
}
