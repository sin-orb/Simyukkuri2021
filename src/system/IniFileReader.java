package src.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;


/******************************************************************

	読み込み専用iniファイルアクセス



 */
public class IniFileReader {

	public static final String INI_SECTION = "Section";
	public static final String INI_KEY = "Key";
	public static final String INI_VALUE = "Value";

	private boolean isResource = false;
	private File file = null;
	private String jarPath = null;
	private BufferedReader reader = null;
	
	private String currentSection = null;
	private String currentKey = null;
	private String currentValue = null;

	public IniFileReader(File path, String jar) {
		file = path;
		jarPath = jar;
		
		if(jarPath != null) {
			isResource = true;
		}
	}
	
	public boolean open(ClassLoader loader) {
		boolean ret = true;
		try {
			if(isResource) {
				// jarからの読み込み
				InputStream is = loader.getResourceAsStream(jarPath);
				reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			} else {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			ret = false;
		} catch (FileNotFoundException e) {
			ret = false;
		} catch (NullPointerException e) {
			ret = false;
		}
		return ret;
	}
	
	public void close() {
		try {
			reader.close();
		} catch (IOException e) {
		}
	}

	public HashMap<String, String> readNext() {
		HashMap<String, String> ret = null;
		String strLine = null;
		String[] tmp = new String[2];

		try {
			while((strLine = reader.readLine()) != null) {
				strLine  =  strLine.trim();
				
				if(strLine.length() == 0) continue;
				if(strLine.indexOf("#") == 0) continue;
				
				if(strLine.indexOf("[") == 0) {
					int ep = strLine.lastIndexOf("]");
					if(ep == -1) continue;
					currentSection = strLine.substring(1, ep);
					continue;
				} else {
					if(currentSection == null || currentSection.length() == 0) continue;
					tmp = strLine.split("=");
					currentKey = tmp[0];
					currentValue = tmp[1];
					ret = new HashMap<String, String>();
					ret.put(INI_SECTION, currentSection);
					ret.put(INI_KEY, currentKey);
					ret.put(INI_VALUE, currentValue);
					break;
				}
			}
		} catch (IOException e) {
			ret = null;
		}
		return ret;
	}
}



