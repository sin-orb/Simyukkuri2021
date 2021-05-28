package src.draw;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;

import src.Const;
import src.enums.ImageCode;
import src.system.IniFileReader;

/*****************************************************
	データの読み込み拡張
	ゲーム開始時に指定したフォルダからの読み込みとエラー処理を行う
*/

public class ModLoader
{
	public static final String DEFAULT_IMG_ROOT_DIR = "images/";
	public static final String DEFAULT_DATA_DIR = "data/";
	public static final String MOD_ROOT_DIR = "mod";
	public static final String MOD_BACK_DIR = "back";
	public static final String MOD_ITEM_DIR = "item";
	public static final String MOD_BODY_DIR = "yukkuri";
	public static final String DATA_DEV_DIR  = "develop";
	public static final String DATA_MSG_DIR  = "yukkuri_msg";
	public static final String DATA_INI_DIR  = "yukkuri_ini";
	public static final String DATA_ITEM_INI_DIR  = "item_ini";
	public static final String DATA_WORLD_INI_DIR  = "world_ini";

	public static final String YK_WORD_NORA = "_nora";
	public static final String YK_WORD_YASEI = "_yasei";

	public static final String YK_WORD_NAGASI = "_nagasi";	// 画像をまりちゃ流しモードのものにする
	// jarファイルのパス
	private static String jarPath = "";
	
	// 選択したテーマ  nullならjar内のデータを使用
	private static String backTheme = null;
	private static String itemTheme = null;
	private static String bodyTheme = null;
	
	private static String developRoot = null;
	//表情の別バージョンの最大数
	public static final int nMaxImgOtherVer = 6;
	
	/**
	 *  jarファイルのパスを取得して設定
	 */
	public static void setJarPath()
	{
		String tmp = System.getProperty("java.class.path");
		jarPath = tmp.substring(0, tmp.lastIndexOf(File.separator)+1);
		developRoot = jarPath + MOD_ROOT_DIR + File.separator + DATA_DEV_DIR;
		System.out.println("jarPath : " + jarPath);
	}
	/**
	 * jarファイルのパスを取得.
	 * @return jarファイルのパス
	 */
	public static String getJarPath()
	{
		return jarPath;
	}

	/**
	 *  mod/back/内のフォルダ一覧を作成して返す
	 * @return mod/back/内のフォルダ一覧
	 */
	public static Vector<String> getBackThemeList()
	{
		return createThemeList(jarPath + MOD_ROOT_DIR + File.separator + MOD_BACK_DIR);
	}

	/**
	 *  mod/item/内のフォルダ一覧を作成して返す
	 * @return mod/item/内のフォルダ一覧
	 */
	public static Vector<String> getItemThemeList()
	{
		return createThemeList(jarPath + MOD_ROOT_DIR + File.separator + MOD_ITEM_DIR);
	}

	/**
	 *  mod/yukkuri/内のフォルダ一覧を作成して返す
	 * @return mod/yukkuri/内のフォルダ一覧
	 */
	public static Vector<String> getBodyThemeList(){
		return createThemeList(jarPath + MOD_ROOT_DIR + File.separator + MOD_BODY_DIR);
	}

	private static Vector<String> createThemeList(String root)
	{
		Vector<String> list = new Vector<String>();
		list.add("デフォルト");
		
		// フォルダ一覧取得
		File dir = new File(root);
		System.out.println("MOD search path : " + dir.getAbsolutePath());
	    File[] files = dir.listFiles();
	    if(files != null)
	    {
	    	for (int i = 0; i < files.length; i++)
	    	{
	    		if(files[i].isDirectory())
	    		{
	    			list.add(files[i].getName());
	    		}
	    	}
	    }
	    return list;
	}
	/**
	 * バックテーマパスを取得する.
	 * @return バックテーマパス
	 */
	public static String getBackThemePath()
	{
		return backTheme;
	}
	/**
	 * バックテーマパスを設定する.
	 * @param path バックテーマパス
	 */
	public static void setBackThemePath(String path)
	{
		if(path == null)
		{
			backTheme = null;
		}
		else
		{
			backTheme = jarPath + MOD_ROOT_DIR + File.separator + MOD_BACK_DIR + File.separator + path + File.separator;
		}
	}
	/**
	 * アイテムテーマパスを取得する.
	 * @return アイテムテーマパス
	 */
	public static String getItemThemePath()
	{
		return itemTheme;
	}
	/**
	 * アイテムテーマパスを設定する.
	 * @param path アイテムテーマパス
	 */
	public static void setItemThemePath(String path)
	{
		if(path == null)
		{
			itemTheme = null;
		}
		else
		{
			itemTheme = jarPath + MOD_ROOT_DIR + File.separator + MOD_ITEM_DIR + File.separator + path + File.separator;
		}
	}
	/**
	 * ボディテーマパスを取得する.
	 * @return ボディテーマパス
	 */
	public static String getBodyThemePath()
	{
		return bodyTheme;
	}
	/**
	 * ボディテーマパスを設定する.
	 * @param path ボディテーマパス
	 */
	public static void setBodyThemePath(String path)
	{
		if(path == null)
		{
			bodyTheme = null;
		}
		else
		{
			bodyTheme = jarPath + MOD_ROOT_DIR + File.separator + MOD_BODY_DIR + File.separator + path + File.separator;
		}
	}

	/**
	 *  旧背景読み込み
	 * @param loader ローダ
	 * @param mapName マップ名
	 * @param fileName ファイル名
	 * @return バッファイメージ
	 * @throws IOException IO例外
	 */
	public static BufferedImage loadBackImage(ClassLoader loader, String mapName, String fileName) throws IOException
	{
		BufferedImage ret = null;
		boolean jarTry = false;
		if(backTheme != null) {
			try {
				ret = loadModImage(backTheme, mapName + File.separator + fileName);
			} catch(IOException e) {
				jarTry = true;
			}
		}
		if(backTheme == null || jarTry) {
			ret = loadJarImage(loader, "back/" + mapName + "/" + fileName);
		}
		
		return ret;
	}

	/**
	 *  新背景定義ファイル読み込み
	 * @param loader ローダ
	 * @param mapName マップ名
	 * @param iniName INIファイル名
	 * @param bgName 背景名
	 * @return INIファイルリーダ
	 * @throws IOException IO例外
	 */
	public static IniFileReader loadTerrainData(ClassLoader loader, String mapName, String iniName, String bgName) throws IOException
	{
		IniFileReader ret = null;
		boolean jarTry = true;
		
		// MOD読み込み
		if(backTheme != null) {
			File iniFile = new File(backTheme + mapName + File.separator + iniName);
			if(iniFile.exists()) {
				// bg.iniがあったら読み込み
				ret = new IniFileReader(iniFile, null);
				jarTry = false;
			} else {
				File bgFile = new File(backTheme + mapName + File.separator + bgName);
				if(bgFile.exists()) {
					// back.jpgで確定
					ret = null;
					jarTry = false;
				}
			}
		}
		// jar読み込み
		if(backTheme == null || jarTry) {
			String iniPath = DEFAULT_IMG_ROOT_DIR + "back/" + mapName + "/" + iniName;
			if(loader.getResource(iniPath) != null) {
				// bg.iniがあったら読み込み
				ret = new IniFileReader(null, iniPath);
			} else {
				// back.jpgで確定
				ret = null;
			}
		}
		
		return ret;
	}

	/**
	 *  道具読み込み
	 * @param loader ローダ
	 * @param fileName ファイル名
	 * @return バッファイメージ
	 * @throws IOException IO例外
	 */
	public static BufferedImage loadItemImage(ClassLoader loader, String fileName) throws IOException
	{
		return loadImage(loader, itemTheme, fileName);
	}
	/**
	 * イメージをロードする.
	 * @param loader ローダ
	 * @param root ルートパス
	 * @param fileName ファイル名
	 * @return バッファイメージ
	 * @throws IOException IO例外
	 */
	private static BufferedImage loadImage(ClassLoader loader, String root, String fileName) throws IOException
	{
		BufferedImage img = null;
		boolean jarTry = false;

		if(root != null) {
			// 外部ファイル読み込み
			try {
				img = loadModImage(root, fileName);
			}
			catch(IOException e)
			{
				jarTry = true;
			}
		}
		if(root == null || jarTry) {
			// jarリソース内のデフォルトファイル読み込み
			String path = fileName.replace(File.separatorChar, '/');
			img = loadJarImage(loader, path);
		}
		return img;
	}
	
	/**
	 * 外部ファイルの読み込み
	 * @param root ルートパス
	 * @param fileName ファイル名
	 * @return バッファイメージ
	 * @throws IOException IO例外
	 */
	private static BufferedImage loadModImage(String root, String fileName) throws IOException
	{
		File file = new File(root + fileName);
		return ImageIO.read(file);
	}
	
	/**
	 *  jarリソースからの読み込み
	 * @param loader ローダ
	 * @param fileName ファイル名
	 * @return バッファイメージ
	 * @throws IOException IO例外
	 */
	private static BufferedImage loadJarImage(ClassLoader loader, String fileName) throws IOException
	{
		return ImageIO.read( loader.getResourceAsStream(DEFAULT_IMG_ROOT_DIR + fileName) );
	}
	
	/**
	 *  メッセージファイルを開く
	 * @param loader ローダ
	 * @param path ファイルパス
	 * @param name ファイル名
	 * @param errStop エラーで止まるフラグ
	 * @return バッファリーダ
	 */
	public static BufferedReader openMessageFile(ClassLoader loader, String path, String name, boolean errStop)
	{
		BufferedReader br = null;
		boolean jarTry = true;
		
		// 開発データのチェック
		File file = new File(developRoot + File.separator + path + File.separator + name);
		if(file.exists()) {
			try
			{
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
				jarTry = false;
			} catch (IOException e) {
				
			}
		}
		if(jarTry) {
			// ファイルが無かったらjarから読む
			InputStream is = loader.getResourceAsStream(DEFAULT_DATA_DIR + path + "/" + name);
			try {
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			} catch (Exception e1) {
				if(errStop) {
					e1.printStackTrace();
				} else {
					br = null;
				}
			}
		}
		return br;
	}
	
	/**
	 *  ゆっくり用iniファイルの読み込み
	 * @param loader ローダ
	 * @param path ファイルパス
	 * @param name ファイル名
	 * @return INIファイルマップ
	 */
	public static Map<String, Point[]> loadBodyIniMap(ClassLoader loader, String path, String name) {
		
		Map<String, Point[]> ret = null;
		IniFileReader iniFile = null;
		boolean jarTry = true;
		
		// 開発モード読み込み
		File file = new File(developRoot + File.separator + path + File.separator + name + ".ini");
		if(file.exists()) {
			// ファイルがあったら読み込み
			iniFile = new IniFileReader(file, null);
			jarTry = false;
		}
		// jar読み込み
		if(jarTry) {
			String iniPath = DEFAULT_DATA_DIR + path + "/" + name + ".ini";
			iniFile = new IniFileReader(null, iniPath);
		}

		boolean err = iniFile.open(loader);
		if(!err) return null;

		ret = new HashMap<String, Point[]>();
		HashMap<String, String> map = null;
		int x,y;
		Point[] pnt = null;
		String[] keyArr = null;
		String[] valArr = null;
		while((map = iniFile.readNext()) != null) {
//		String section = map.get(IniFileReader.INI_SECTION);
			String key = map.get(IniFileReader.INI_KEY);
			String value = map.get(IniFileReader.INI_VALUE);
			
			key = key.trim();
			value = value.trim();
			valArr = value.split(",");
			if( valArr.length < 2){
				continue;
			}
			x = Integer.valueOf(valArr[0]);
			y = Integer.valueOf(valArr[1]);

			// キーから年齢を切り離す
			keyArr = key.split("\\.");
			if("baby".equals(keyArr[1])) {
				pnt = new Point[3];
				pnt[0] = new Point();
				pnt[0].x = x;
				pnt[0].y = y;
			}
			else if("child".equals(keyArr[1])) {
				pnt[1] = new Point();
				pnt[1].x = x;
				pnt[1].y = y;
			}
			else if("adult".equals(keyArr[1])) {
				pnt[2] = new Point();
				pnt[2].x = x;
				pnt[2].y = y;
				ret.put(keyArr[0], pnt);
			}
		}
		iniFile.close();

		return ret;
	}
	
	/**
	 *  ゆっくり用iniファイルの読み込み(数値)
	 * @param loader ローダ
	 * @param path ファイルパス
	 * @param name ファイル名
	 * @param inKey 目的のキー
	 * @return キーから取得されたINIファイル上の数値
	 */
	public static int loadBodyIniMapForInt(ClassLoader loader, String path, String name, String inKey) {
		int nRet = 0;
		IniFileReader iniFile = null;
		boolean jarTry = true;
		
		// 開発モード読み込み
		File file = new File(developRoot + File.separator + path + File.separator + name + ".ini");
		if(file.exists()) {
			// ファイルがあったら読み込み
			iniFile = new IniFileReader(file, null);
			jarTry = false;
		}
		// jar読み込み
		if(jarTry) {
			String iniPath = DEFAULT_DATA_DIR + path + "/" + name + ".ini";
			iniFile = new IniFileReader(null, iniPath);
		}

		boolean err = iniFile.open(loader);
		if(!err) return nRet;

    	HashMap<String, String> map = null;
    	while((map = iniFile.readNext()) != null) {
    		String key = map.get(IniFileReader.INI_KEY);
    		String value = map.get(IniFileReader.INI_VALUE);
    		if( key.equals(inKey))
    		{
    			nRet = Integer.valueOf(value);
    			break;
    		}
    	}
    	iniFile.close();

		return nRet;
	}

	/**
	 *  ゆっくり用iniファイルの読み込み(文字配列)
	 * @param loader ローダ
	 * @param path ファイルパス
	 * @param name ファイル名
	 * @param inKey 目的のキー
	 * @return キーから取得されたINIファイル上の文字列
	 */
	public static String[] loadBodyIniMapForArrayString(ClassLoader loader, String path, String name, String inKey) {
		IniFileReader iniFile = null;
		boolean jarTry = true;
		
		// 開発モード読み込み
		File file = new File(developRoot + File.separator + path + File.separator + name + ".ini");
		if(file.exists()) {
			// ファイルがあったら読み込み
			iniFile = new IniFileReader(file, null);
			jarTry = false;
		}
		// jar読み込み
		if(jarTry) {
			String iniPath = DEFAULT_DATA_DIR + path + "/" + name + ".ini";
			iniFile = new IniFileReader(null, iniPath);
		}

		boolean err = iniFile.open(loader);
		if(!err) return null;

		String strTemp = new String();
    	HashMap<String, String> map = null;
    	while((map = iniFile.readNext()) != null) {
    		String key = map.get(IniFileReader.INI_KEY);
    		String value = map.get(IniFileReader.INI_VALUE);
    		if( key.equals(inKey))
    		{
    			strTemp = value;
    			break;
    		}
    	}
    	
    	String[] anStr = strTemp.split(":",0);
    	
    	iniFile.close();

		return anStr;
	}
	
	/**
	 *  ゆっくりのパーツ画像読み込み
	 * @param loader ローダ
	 * @param images イメージ
	 * @param dirOfs オフセット
	 * @param suffix MODを読み込む接尾辞
	 * @param bodyName ゆっくり名
	 * @param io イメージオブザーバ
	 * @return 読み込めたかどうか
	 */
	public static boolean loadBodyImagePack(ClassLoader loader, BufferedImage[][][] images, int[][] dirOfs, String suffix, String bodyName, ImageObserver io)
	{
		int babyIndex = Const.BABY_INDEX;
		int childIndex = Const.CHILD_INDEX;
		int adultIndex = Const.ADULT_INDEX;

		MediaTracker mt = new MediaTracker((MyPane)io);

		// 画像の有無と反転処理の関係
		// left:なし right:なし -> エラー
		// left:あり right:なし -> rightはleftの反転描画、dirofsは0,1
		// left:あり right:あり -> rightそのまま、dirofsは1,0
		// left:あり right:ダミー -> right非表示、dirofsは1,0、imageをnullで返す
		// left:ダミー -> left非表示でrightは上記
		BodyImage tmp;
		ImageCode[] parts = ImageCode.values();
		int max = parts.length;
		for(int i = 0; i < max; i++) {
			tmp = loadBodyImage(loader, bodyTheme, suffix, bodyName, parts[i]);
			images[i][Const.LEFT][adultIndex] = tmp.img[Const.LEFT];
			images[i][Const.RIGHT][adultIndex] = tmp.img[Const.RIGHT];
			if(tmp.isFlip) {
				dirOfs[i][0] = 0;
				dirOfs[i][1] = 1;
			} else {
				dirOfs[i][0] = 1;
				dirOfs[i][1] = 0;
			}
		}

		int id = 0;
		for(BufferedImage[][] array2d : images) {	
			for(BufferedImage[] array : array2d) {
				for(BufferedImage image : array) {
					mt.addImage(image, id);
					id++;
				}
			}
		}
		try {
			mt.waitForAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		int sx, sy;

		// 子、赤ゆサイズの画像作成
		for(BufferedImage[][] array2d : images) {	
			for(BufferedImage[] array : array2d) {
				if(array[adultIndex] == null) {
					continue;
				}
				sx = (int)((float)array[adultIndex].getWidth(io) * Const.BODY_SIZE[1]);
				sy = (int)((float)array[adultIndex].getHeight(io) * Const.BODY_SIZE[1]);
				array[childIndex] = scaleImage(array[adultIndex], sx, sy);
				sx = (int)((float)array[adultIndex].getWidth(io) * Const.BODY_SIZE[0]);
				sy = (int)((float)array[adultIndex].getHeight(io) * Const.BODY_SIZE[0]);
				array[babyIndex] = scaleImage(array[adultIndex], sx, sy);
			}
		}
		
		// 胴体左が読めたかで成否を判定
		boolean ret = false;
		if(images[ImageCode.BODY.ordinal()][0][adultIndex] != null) {
			ret = true;
		}
		return ret;
	}

	/**
	 *  ゆっくりのパーツ画像読み込み
	 * @param loader ローダ
	 * @param images イメージ
	 * @param dirOfs オフセット
	 * @param suffix MODを読み込む接尾辞
	 * @param bodyName ゆっくり名
	 * @param io イメージオブザーバ
	 * @return 読み込めたかどうか
	 */
	public static boolean loadBodyImagePack(ClassLoader loader, BufferedImage[][][][] images, int[][] dirOfs, String suffix, String bodyName, ImageObserver io)
	{
		int babyIndex = Const.BABY_INDEX;
		int childIndex = Const.CHILD_INDEX;
		int adultIndex = Const.ADULT_INDEX;

		MediaTracker mt = new MediaTracker((MyPane)io);

		// 画像の有無と反転処理の関係
		// left:なし right:なし -> エラー
		// left:あり right:なし -> rightはleftの反転描画、dirofsは0,1
		// left:あり right:あり -> rightそのまま、dirofsは1,0
		// left:あり right:ダミー -> right非表示、dirofsは1,0、imageをnullで返す
		// left:ダミー -> left非表示でrightは上記
		BodyImage tmp;
		ImageCode[] parts = ImageCode.values();
		int max = parts.length;
		for(int i = 0; i < max; i++) {
			tmp = loadBodyImage(loader, bodyTheme, suffix, bodyName, parts[i]);
			images[i][Const.LEFT][adultIndex][0] = tmp.img[Const.LEFT];
			images[i][Const.RIGHT][adultIndex][0] = tmp.img[Const.RIGHT];

			for( int j=0; j<ModLoader.nMaxImgOtherVer; j++)
			{
				images[i][Const.LEFT][adultIndex][j+1] = tmp.imgOtherVer[Const.LEFT][j];
				images[i][Const.RIGHT][adultIndex][j+1] = tmp.imgOtherVer[Const.RIGHT][j];
				
			}
		
			if(tmp.isFlip) {
				dirOfs[i][0] = 0;
				dirOfs[i][1] = 1;
			} else {
				dirOfs[i][0] = 1;
				dirOfs[i][1] = 0;
			}
		}

		int id = 0;
		for(BufferedImage[][][] array3d : images) {	
			for(BufferedImage[][] array2d : array3d) {	
				for(BufferedImage[] array : array2d) {
					for(BufferedImage image : array) {
						mt.addImage(image, id);
						id++;
					}
				}
			}
		}

		try {
			mt.waitForAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		int sx, sy;

		// 子、赤ゆサイズの画像作成
		for(BufferedImage[][][] array3d : images) {	
			for(BufferedImage[][] array2d : array3d) {	
				for( int i=0; i<nMaxImgOtherVer+1; i++ )
				{
					if(array2d[adultIndex][i] == null) {
						continue;
					}
					sx = (int)((float)array2d[adultIndex][i].getWidth(io) * Const.BODY_SIZE[1]);
					sy = (int)((float)array2d[adultIndex][i].getHeight(io) * Const.BODY_SIZE[1]);
					array2d[childIndex][i] = scaleImage(array2d[adultIndex][i], sx, sy);
					sx = (int)((float)array2d[adultIndex][i].getWidth(io) * Const.BODY_SIZE[0]);
					sy = (int)((float)array2d[adultIndex][i].getHeight(io) * Const.BODY_SIZE[0]);
					array2d[babyIndex][i] = scaleImage(array2d[adultIndex][i], sx, sy);
				}
			}
		}
			
		// 胴体左が読めたかで成否を判定
		boolean ret = false;
		if(images[ImageCode.BODY.ordinal()][0][adultIndex] != null) {
			ret = true;
		}
		return ret;
	}
	
	/**
	 * 画像サイズ設定：通常用
	 * @param bodyImg イメージ
	 * @param bodyRect ゆっくり胴体の矩形
	 * @param braidRect ゆっくりのおさげの矩形
	 * @param io イメージオブザーバ
	 */
	public static void setImageSize(BufferedImage[][][] bodyImg, Dimension[] bodyRect, Dimension[] braidRect, ImageObserver io) {
		setImageSize(bodyImg,bodyRect,  braidRect,false, io);
	}
	/**
	 * 画像サイズ設定：飛行種(おさげが前方に出ることのない種はこっちを直接呼ぶ）
	 * @param bodyImg イメージ
	 * @param bodyRect ゆっくり胴体の矩形
	 * @param braidRect ゆっくりのおさげの矩形
	 * @param BB おさげが胴体の後ろかどうか
	 * @param io イメージオブザーバ
	 */
	public static void setImageSize(BufferedImage[][][] bodyImg, Dimension[] bodyRect, Dimension[] braidRect,boolean BB , ImageObserver io) {
		for(int i = 0; i < 3; i++) {
			bodyRect[i] = new Dimension();
			bodyRect[i].width = bodyImg[0][0][i].getWidth(io);
			bodyRect[i].height = bodyImg[0][0][i].getHeight(io);

			if(BB){
				if(bodyImg[ImageCode.BRAID_BACK.ordinal()][0][i] != null) {
					braidRect[i] = new Dimension(bodyImg[ImageCode.BRAID_BACK.ordinal()][0][i].getWidth(io), bodyImg[ImageCode.BRAID_BACK.ordinal()][0][i].getHeight(io));
				}
				else if(bodyImg[ImageCode.BRAID_BACK.ordinal()][1][i] != null) {
					braidRect[i] = new Dimension(bodyImg[ImageCode.BRAID_BACK.ordinal()][1][i].getWidth(io), bodyImg[ImageCode.BRAID_BACK.ordinal()][1][i].getHeight(io));
				}
			}
			else{
				if(bodyImg[ImageCode.BRAID.ordinal()][0][i] != null) {
					braidRect[i] = new Dimension(bodyImg[ImageCode.BRAID.ordinal()][0][i].getWidth(io), bodyImg[ImageCode.BRAID.ordinal()][0][i].getHeight(io));
				}
				else if(bodyImg[ImageCode.BRAID.ordinal()][1][i] != null) {
					braidRect[i] = new Dimension(bodyImg[ImageCode.BRAID.ordinal()][1][i].getWidth(io), bodyImg[ImageCode.BRAID.ordinal()][1][i].getHeight(io));
				}
			}
		}
	}

	// ボディパーツ1つ読み込み
	private static BodyImage loadBodyImage(ClassLoader loader, String root, String suffix, String bodyName, ImageCode parts) {
		BodyImage ret = new BodyImage();
		boolean jarTry = false;
		
		boolean[] sideFlag = {false, true};
		boolean[] flipFlag = {false, true};

		// 左右パーツ読み込み
		for(int i = 0; i < 2; i++) {
			jarTry = false;
			// 片方しかない画像はスキップ
			if(i == 1 && !parts.hasSecondary()) break;
			// MOD指定あり
			if(root != null) {
				String path;
				if(suffix != null) {
					path = root + MOD_BODY_DIR + suffix;
				} else {
					path = root + MOD_BODY_DIR;
				}
				// ダミーファイルの存在チェック
				File file = new File(path + File.separator + bodyName + File.separator + parts.getFilePath(sideFlag[i]) + ".txt");
				if(file.exists()) {
					// ダミーがある場合、画像はnullで返す
					ret.img[i] = null;
					ret.isDummy[i] = true;
					ret.isFlip = false;
				} else {
					// 実画像の読み込み
					file = new File(path + File.separator + bodyName + File.separator + parts.getFilePath(sideFlag[i]) + ".png");
					if(!file.exists()) {
						// ファイルが無い場合
						if(i == 1) {
							// 右側の場合は左の画像が読めていれば反転フラグ設定
							if(ret.img[0] != null || ret.isDummy[0]) {
								ret.img[i] = null;
								ret.isDummy[i] = false;
								ret.isFlip = flipFlag[i];
							}
						} else {
							// 左の場合はエラーケースでjar読み込みへ
							jarTry = true;
						}
					} else {
						try {
							ret.img[i] = ImageIO.read(file);
							ret.isDummy[i] = false;
							ret.isFlip = false;
						} catch(IOException ioe) {
							// 読み込めなかったらjar読み込み
							jarTry = true;
						}
					}
				}
			}
			// MOD指定なしかMOD読み込み失敗
			if(root == null || jarTry) {
				String path;
				if(suffix != null) {
					path = DEFAULT_IMG_ROOT_DIR + MOD_BODY_DIR + suffix;
				} else {
					path = DEFAULT_IMG_ROOT_DIR + MOD_BODY_DIR;
				}
				// ダミーファイルの存在チェック
				String dummPath = path + "/" + bodyName + "/" + parts.getJarPath(sideFlag[i]) + ".txt";
				if(loader.getResource(dummPath) != null) {
					// ダミーがある場合、画像はnullで返す
					ret.img[i] = null;
					ret.isDummy[i] = true;
					ret.isFlip = false;
				} else {
					String strBeforeTemp = path + "/" + bodyName + "/" + parts.getJarPath(sideFlag[i]);
					// 実画像の読み込み
					path = path + "/" + bodyName + "/" + parts.getJarPath(sideFlag[i]) + ".png";

					if(loader.getResource(path) == null) {
						// ファイルが無い場合
						if(i == 1) {
							// 右側の場合は左の画像が読めていれば反転フラグ設定
							if(ret.img[0] != null || ret.isDummy[0]) {
								ret.img[i] = null;
								ret.isDummy[i] = false;
								ret.isFlip = flipFlag[i];
							}
						} else {
							// 左の場合はエラーケースだがひとまずダミー扱い
							ret.img[i] = null;
							ret.isDummy[i] = false;
							ret.isFlip = false;
						}
					} else {
						try {
							ret.img[i] = ImageIO.read(loader.getResourceAsStream(path));
							ret.isDummy[i] = false;
							ret.isFlip = false;
						} catch(IOException ioe) {
							// 左の場合はエラーケースだがひとまずダミー扱い
							ret.img[i] = null;
							ret.isDummy[i] = false;
							ret.isFlip = false;
						}

						// ver違いの画像があった場合の採用チェック
						for( int j=0; j<ModLoader.nMaxImgOtherVer; j++)
						{
							int nTempVer = j+2;// v2スタート
							// v2,v3などが存在するか
							String strTempPath = strBeforeTemp +"_v"+ nTempVer + ".png";

							if(loader.getResource( strTempPath ) == null)
							{
								ret.imgOtherVer[i][j] = null;
								continue;
							}

							try {
								ret.imgOtherVer[i][j] = ImageIO.read(loader.getResourceAsStream( strTempPath ));
							} catch (IOException e) {
								e.printStackTrace();
								break;
							}
						}
						
					}
				}
			}
		}

		return ret;
	}

	/**
	 *  左右反転イメージを作成
	 * @param img イメージ
	 * @return 左右反転イメージ
	 */
	public static BufferedImage flipImage(BufferedImage img) {

		BufferedImage ret = null;

		int w = img.getWidth();
		int h = img.getHeight();
		ret = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = ret.createGraphics();
		g2.setBackground(new Color(0, 0, 0, 0));
		g2.clearRect(0, 0, w, h);
		g2.drawImage(img, w - 1, 0, -w, h, null);
		
		return ret;
	}

	/**
	 *  拡大縮小イメージを作成
	 * @param img イメージ
	 * @param w 幅
	 * @param h 高さ
	 * @return 幅と高さに合わせて拡大/縮小されたイメージ
	 */
	public static BufferedImage scaleImage(BufferedImage img, int w, int h) {

		BufferedImage ret = null;

		ret = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = ret.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.drawImage(img, 0, 0, w, h, null);

		return ret;
	}
}


/**
 *  ゆっくり画像のテンポラリクラス
 */
final class BodyImage {
	public BufferedImage[] img;	// 画像(左右)
	public boolean[] isDummy;	// ダミーファイルあり
	public boolean isFlip;	// 反転あり
	public BufferedImage[][] imgOtherVer;	// 別バージョン画像(左右)
	/**
	 * コンストラクタ.
	 */
	public BodyImage() {
		img = new BufferedImage[2];
		isDummy = new boolean[2];
		isFlip = false;
		imgOtherVer = new BufferedImage[2][ModLoader.nMaxImgOtherVer];
	}
}

