package src.draw;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import src.base.Obj;
import src.system.IniFileReader;
import src.system.MapWindow;



/***********************************************************

	背景データクラス

*/
public class TerrainField {

	// iniファイルのキー
	public static final String SECTION_ASSET = "Asset";
	public static final String SECTION_ENV = "Environment";
	public static final String SECTION_OBJ = "Object";
	public static final String SECTION_FLOOR = "Floor";
	public static final String SECTION_CEIL = "Ceiling";
	public static final String SECTION_OWNER = "Owner";


	public static final String ASSET_IMG = "img";

	public static final String ENV_BASE = "base";
	public static final String ENV_TOP_MORNING = "morning_top_rgba";
	public static final String ENV_BOTTOM_MORNING = "morning_bottom_rgba";
	public static final String ENV_TOP_DAY = "day_top_rgba";
	public static final String ENV_BOTTOM_DAY = "day_bottom_rgba";
	public static final String ENV_TOP_EVENING = "evening_top_rgba";
	public static final String ENV_BOTTOM_EVENING = "evening_bottom_rgba";
	public static final String ENV_TOP_NIGHT = "night_top_rgba";
	public static final String ENV_BOTTOM_NIGHT = "night_bottom_rgba";
	public static final String OWN_PERS = "perspective";
	public static final String OWN_OWNER = "owner";

	private static final String BG_FILE_NAME = "bg.ini";
	private static final String OLD_BG_NAME = "back.jpg";

	public enum DrawMode {
		FAST_DRAW_MODE,
		LOW_MEM_MODE
	}

	// 最低BGサイズ
	private static final int BG_W = 900;
	private static final int BG_H = 700;

	// 時間帯空気色
	private static Color[][] defaultDayColor = {
		{new Color(  0,   0,  71,  12), new Color(  0,   0,  73,  57)},	// 朝
		{null, null},													// 昼	
		{new Color(255,  54,  19,  37), new Color(177,  72,  49,  21)},	// 夕
		{new Color(  0,   0,  20, 113), new Color(  0,   0,  40,  70)},	// 夜
	};
	
	private static DrawMode mode;
	
	private static Map<String, BufferedImage> assetMap; // 全画像の実体

	private static BufferedImage backGround;	// 下地
	private static AffineTransform bgXform;	// 下地のリサイズ

	private static LinearGradientPaint[] skyColor;	// 空の色

	private static List<Obj> floorList;
	private static List<Obj> structList;
	private static List<Obj> ceilingList;

	private static boolean isPers;
	private static int ownerType;
	
	private static double scaleRateW;
	private static double scaleRateH;
	
	public static void loadTerrain(int index, ClassLoader loader, ImageObserver io) {
		
		IniFileReader reader = null;

		scaleRateW = (double)Translate.bufferW / (double)BG_W;
		scaleRateH = (double)Translate.bufferH / (double)BG_H;
		try {
			String mapName = MapWindow.MAP.values()[index].filePath;
			// 読み込みフォーマット判定
			reader = ModLoader.loadTerrainData(loader, mapName, BG_FILE_NAME, OLD_BG_NAME);

			if(reader == null) {
				// 旧フォーマット
				mode = DrawMode.LOW_MEM_MODE;
				backGround = ModLoader.loadBackImage(loader, mapName, OLD_BG_NAME);
				bgXform = new AffineTransform(scaleRateW, 0.0, 0.0, scaleRateH, 0.0, 0.0);
				skyColor = new LinearGradientPaint[4];
				skyColor[0] = null;
				skyColor[1] = null;
				skyColor[2] = null;
				skyColor[3] = null;
				floorList = new ArrayList<Obj>();
				structList = new ArrayList<Obj>();
				ceilingList = new ArrayList<Obj>();
				isPers = true;
				ownerType = 0;
			} else {
				// 新フォーマット
				assetMap = new HashMap<String, BufferedImage>();
				skyColor = new LinearGradientPaint[4];
				floorList = new ArrayList<Obj>();
				structList = new ArrayList<Obj>();
				ceilingList = new ArrayList<Obj>();
				loadTerrainAsset(loader, reader, mapName, io);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}

		// 未設定の空はデフォルトを設定
		float[] frac = {0.0f, 1.0f};
		Color[] col = new Color[2];
		for(int i = 0; i < 4; i++) {
			if(skyColor[i] != null) continue;

			if(defaultDayColor[i][0] == null) {
				skyColor[i] = null;
				continue;
			}
			col[0] = defaultDayColor[i][0];
			col[1] = defaultDayColor[i][1];
			skyColor[i] = new LinearGradientPaint(0.0f, 0.0f, 0.0f, (float)Translate.bufferH, frac, col);
		}
	}
	
	public static void drawBackGroundImage(Graphics2D g2, ImageObserver obs) {
		g2.drawImage(backGround, bgXform, obs);
	}
	
	public static LinearGradientPaint getSkyGrad(int idx) {
		return skyColor[idx];
	}
	
	public static List<Obj> getStructList() {
		return structList;
	}

	public static boolean isPers() {
		return isPers;
	}

	public int getOwnerType() {
		return ownerType;
	}

	public static void drawFloor(Graphics2D g2, ImageObserver obs) {
		for(Obj o :floorList) {
			TerrainBillboard b = (TerrainBillboard)o;
			b.draw(g2, obs);
		}
	}

	public static void drawCeiling(Graphics2D g2, ImageObserver obs) {
		for(Obj o :ceilingList) {
			TerrainBillboard b = (TerrainBillboard)o;
			b.draw(g2, obs);
		}
	}

	// 背景アセット読み込み
	private static void loadTerrainAsset(ClassLoader loader, IniFileReader ini, String mapName, ImageObserver io) {
		
		Color[] topCol = {null, null, null, null};
		Color[] botCol = {null, null, null, null};
		
		TerrainBillboard board = null;
		
		ini.open(loader);
		
    	HashMap<String, String> map = null;
    	while((map = ini.readNext()) != null) {
    		String section = map.get(IniFileReader.INI_SECTION);
    		String key = map.get(IniFileReader.INI_KEY);
    		String value = map.get(IniFileReader.INI_VALUE);
    		
    		switch(section) {
    			case SECTION_ASSET:
    				switch(key) {
    					case ASSET_IMG:
    						try {
    							BufferedImage img = ModLoader.loadBackImage(loader, mapName, value);
        						assetMap.put(value, img);
    						} catch(IOException e) {
    							e.printStackTrace();
    						}
    						break;
    					default:
    						break;
    				}
    				break;
    			case SECTION_ENV:
    				switch(key) {
						case ENV_BASE:
							backGround = assetMap.get(value);
							bgXform = new AffineTransform(scaleRateW, 0.0, 0.0, scaleRateH, 0.0, 0.0);
							break;
						case ENV_TOP_MORNING:
							setSkyColor(topCol, 0, value);
							break;
						case ENV_BOTTOM_MORNING:
							setSkyColor(botCol, 0, value);
							break;
						case ENV_TOP_DAY:
							setSkyColor(topCol, 1, value);
							break;
						case ENV_BOTTOM_DAY:
							setSkyColor(botCol, 1, value);
							break;
						case ENV_TOP_EVENING:
							setSkyColor(topCol, 2, value);
							break;
						case ENV_BOTTOM_EVENING:
							setSkyColor(botCol, 2, value);
							break;
						case ENV_TOP_NIGHT:
							setSkyColor(topCol, 3, value);
							break;
						case ENV_BOTTOM_NIGHT:
							setSkyColor(botCol, 3, value);
							break;
						default:
							break;
    				}
    				break;
    			case SECTION_OBJ:
    				board = createBillboard(0, key, value, io);
    				structList.add(board);
    				break;
    			case SECTION_FLOOR:
    				board = createBillboard(1, key, value, io);
    				floorList.add(board);
    				break;
    			case SECTION_CEIL:
    				board = createBillboard(2, key, value, io);
    				ceilingList.add(board);
    				break;
    			case SECTION_OWNER:
    				switch(key) {
						case OWN_PERS:
		    				setPers(key, value);
							break;
						case OWN_OWNER:
		    				setOwner(key, value);
							break;
						default:
							break;
    				}
    				break;
    		}
    	}
		ini.close();

		// 空のグラデ設定
		float[] frac = {0.0f, 1.0f};
		Color[] col = new Color[2];
		for(int i = 0; i < 4; i++) {
			if(topCol[i] == null || botCol[i] == null) {
				skyColor[i] = null;
				continue;
			}

			if(defaultDayColor[i][0] == null) {
				skyColor[i] = null;
				continue;
			}
			col[0] = topCol[i];
			col[1] = botCol[i];
			skyColor[i] = new LinearGradientPaint(0.0f, 0.0f, 0.0f, (float)Translate.bufferH, frac, col);
		}
		
	}

	// 空の色をデコード
	private static void setSkyColor(Color[] col, int i, String value) {
		String[] strCol = value.split(",");
		int r, g, b, a;
		
		r = Integer.valueOf(strCol[0]);
		g = Integer.valueOf(strCol[1]);
		b = Integer.valueOf(strCol[2]);
		a = Integer.valueOf(strCol[3]);
		col[i] = new Color(r, g, b, a);
	}

	// スプライトをデコード
	private static TerrainBillboard createBillboard(int type, String key, String value, ImageObserver io) {
		
		TerrainBillboard ret = null;
		
		String[] pos = value.split(",");
		double x,y,z;
		double pivX, pivY;
		int w, h;
		
		x = Double.valueOf(pos[0]);
		y = Double.valueOf(pos[1]);
		z = Double.valueOf(pos[2]);
		
		switch(type) {
			case 0:
				// ストラクチャは表示ソートがあるのでobjの座標も計算
				ret = new TerrainBillboard(assetMap.get(key));
				ret.scale(scaleRateW, scaleRateH);
				w = (int)((double)ret.getImage().getWidth(io) * scaleRateW);
				h = (int)((double)ret.getImage().getHeight(io) * scaleRateH);
				pivX = x * (double)Translate.bufferW - ((double)w * 0.5);
				pivY = (y * (double)Translate.bufferH - z * (double)Translate.bufferH) - ((double)h - 1.0);
				ret.trans(pivX, pivY);
				int oy = (int)(y * (double)Translate.bufferH);
				ret.setY(Translate.invertBgY(oy));
				break;
			case 1:
			case 2:
			default:
				// 最前面、最下面はソートの必要が無いので描画座標は固定
				ret = new TerrainBillboard(assetMap.get(key));
				ret.scale(scaleRateW, scaleRateH);
				w = (int)((double)ret.getImage().getWidth(io) * scaleRateW);
				h = (int)((double)ret.getImage().getHeight(io) * scaleRateH);
				pivX = x * (double)Translate.bufferW - ((double)w * 0.5);
				pivY = (y * (double)Translate.bufferH - z * (double)Translate.bufferH) - ((double)h * 0.5);
				ret.trans(pivX, pivY);
				break;
		}
		return ret;
	}

	private static void setPers(String key, String value) {
		boolean val = Boolean.valueOf(value);
		isPers = val;
	}

	private static void setOwner(String key, String value) {
		int val = Integer.valueOf(value);
		ownerType = val;
	}

}



