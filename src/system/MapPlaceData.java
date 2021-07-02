package src.system;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import src.SimYukkuri;
import src.base.Body;
import src.base.Effect;
import src.base.EventPacket;
import src.base.Okazari;
import src.draw.Translate;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.item.AutoFeeder;
import src.item.Barrier;
import src.item.Bed;
import src.item.Beltconveyor;
import src.item.BeltconveyorObj;
import src.item.BreedingPool;
import src.item.Diffuser;
import src.item.Farm;
import src.item.Food;
import src.item.FoodMaker;
import src.item.GarbageChute;
import src.item.GarbageStation;
import src.item.HotPlate;
import src.item.House;
import src.item.MachinePress;
import src.item.Mixer;
import src.item.OrangePool;
import src.item.Pool;
import src.item.ProcesserPlate;
import src.item.ProductChute;
import src.item.StickyPlate;
import src.item.Stone;
import src.item.Sui;
import src.item.Toilet;
import src.item.Toy;
import src.item.Trampoline;
import src.item.Trash;
import src.item.Yunba;



/**************************************************
 * 1つのマップ内のオブジェクトなど
 * 従来のセーブデータ内容をまとめている
 */
public class MapPlaceData implements Serializable {
	static final long serialVersionUID = 1L;

	/** このマップのインデックス */
	public int mapIndex;
	/** アラーム時間 */
	public int alarmPeriod;
	/** アラーム */
	public boolean alarm;

	// シーン内の各オブジェクトリスト
	/** ゆっくりのリスト */
	public Map<Integer, Body> body;
	/** うんうんリスト */
	public Map<Integer, Shit> shit;
	/** 吐餡リスト */
	public Map<Integer, Vomit> vomit;
	/** 壁リスト */
	public List<Barrier> barrier;
	/** イベントリスト */
	public List<EventPacket> event;
	/** 裏のエフェクト */
	public Map<Integer, Effect> sortEffect;
	/** 表のエフェクト */
	public Map<Integer, Effect> frontEffect;
	/** 食べ物リスト */
	public Map<Integer, Food> food;
	/** お持ち帰り中の食べ物リスト */
	public Map<Integer, Food> takenOutFood;
	/** お持ち帰り中のうんうんリスト */
	public Map<Integer, Shit> takenOutShit;
	/** トイレリスト */
	public Map<Integer, Toilet> toilet;
	/** ベッドリスト */
	public Map<Integer, Bed> bed;
	/** おもちゃリスト */
	public Map<Integer, Toy> toy;
	/** 小石リスト */
	public Map<Integer, Stone> stone;
	/** トランポリンリスト */
	public Map<Integer, Trampoline> trampoline;
	/** 養殖プールリスト */
	public Map<Integer, BreedingPool> breedingPool;
	/** ダストシュートリスト */
	public Map<Integer, GarbageChute> garbagechute;
	/** フードメーカーリスト */
	public Map<Integer, FoodMaker> foodmaker;
	/** オレンジプールリスト */
	public Map<Integer, OrangePool> orangePool;
	/** 製品投入口リスト */
	public Map<Integer, ProductChute> productchute;
	/** 粘着板リスト */
	public Map<Integer, StickyPlate> stickyPlate;
	/** ホットプレートリスト */
	public Map<Integer, HotPlate> hotPlate;
	/** 加工プレートリスト */
	public Map<Integer, ProcesserPlate> processerPlate;
	/** ミキサーリスト */
	public Map<Integer, Mixer> mixer;
	/** 自動給餌器リスト */
	public Map<Integer, AutoFeeder> autofeeder;
	/** プレス機リスト */
	public Map<Integer, MachinePress> machinePress;
	/** 茎リスト */
	public Map<Integer, Stalk> stalk;
	/** ディフューザーリスト */
	public Map<Integer, Diffuser> diffuser;
	/** ゆばリスト */
	public Map<Integer, Yunba> yunba;
	/** すぃ～リスト */
	public Map<Integer, Sui> sui;
	/** ガラクタリスト */
	public Map<Integer, Trash> trash;
	/** ゴミ収集所リスト */
	public Map<Integer, GarbageStation> garbageStation;
	/** おうちリスト */
	public Map<Integer, House> house;
	/** ベルコンオブジェリスト */
	public Map<Integer, BeltconveyorObj> beltconveyorObj;
	/** ベルコンリスト */
	public List<Beltconveyor> beltconveyor;
	/** 池リスト */
	public List<Pool> pool;
	/** 畑リスト */
	public List<Farm> farm;
	/** おかざりリスト */
	public Map<Integer, Okazari> okazari;
	/** 発電機リスト */
	//public List<Generator> generator;
	/** マップにドスがいるかどうかのフラグ */
	private volatile boolean hasDos;
	/** 壁 */
	public int wallMap[][];
	/** フィールド */
	public int fieldMap[][];
	/**
	 * コンストラクタ
	 * @param idx インデックス(0：部屋)
	 */
	public MapPlaceData(int idx) {

		mapIndex = idx;
		alarmPeriod = 0;
		alarm = false;

		body = new HashMap<Integer, Body>();
		shit = new HashMap<Integer, Shit>();
		vomit = new HashMap<Integer, Vomit>();
		barrier = new LinkedList<Barrier>();
		event = new LinkedList<EventPacket>();
		sortEffect = new HashMap<Integer, Effect>();
		frontEffect = new HashMap<Integer, Effect>();
		food = new HashMap<Integer, Food>();
		toilet = new HashMap<Integer, Toilet>();
		bed = new HashMap<Integer, Bed>();
		toy = new HashMap<Integer, Toy>();
		stone = new HashMap<Integer, Stone>();
		trampoline = new HashMap<Integer, Trampoline>();
		breedingPool = new HashMap<Integer, BreedingPool>();
		garbagechute = new HashMap<Integer, GarbageChute>();
		foodmaker = new HashMap<Integer, FoodMaker>();
		orangePool = new HashMap<Integer, OrangePool>();
		productchute = new HashMap<Integer, ProductChute>();
		stickyPlate = new HashMap<Integer, StickyPlate>();
		hotPlate = new HashMap<Integer, HotPlate>();
		processerPlate = new HashMap<Integer, ProcesserPlate>();
		mixer = new HashMap<Integer, Mixer>();
		autofeeder = new HashMap<Integer, AutoFeeder>();
		machinePress = new HashMap<Integer, MachinePress>();
		stalk = new HashMap<Integer, Stalk>();
		diffuser = new HashMap<Integer, Diffuser>();
		yunba = new HashMap<Integer, Yunba>();
		sui = new HashMap<Integer, Sui>();
		trash = new HashMap<Integer, Trash>();
		garbageStation = new HashMap<Integer, GarbageStation>();
		house = new HashMap<Integer, House>();
		beltconveyorObj = new HashMap<Integer, BeltconveyorObj>();
		beltconveyor = new LinkedList<Beltconveyor>();
		pool = new LinkedList<Pool>();
		farm = new LinkedList<Farm>();
		okazari = new HashMap<Integer, Okazari>();
		takenOutFood = new HashMap<>();
		takenOutShit = new HashMap<>();
		//generator = new LinkedList<Generator>();

		int mapW = Translate.mapW;
		int mapH = Translate.mapH;

		wallMap = new int[mapW+1][mapH+1];
		clearMap(wallMap);
		fieldMap = new int[mapW+1][mapH+1];
		clearMap(fieldMap);
	}
	
	public MapPlaceData() {

		alarmPeriod = 0;
		alarm = false;

		body = new HashMap<Integer, Body>();
		shit = new HashMap<Integer, Shit>();
		vomit = new HashMap<Integer, Vomit>();
		barrier = new LinkedList<Barrier>();
		event = new LinkedList<EventPacket>();
		sortEffect = new HashMap<Integer, Effect>();
		frontEffect = new HashMap<Integer, Effect>();
		food = new HashMap<Integer, Food>();
		toilet = new HashMap<Integer, Toilet>();
		bed = new HashMap<Integer, Bed>();
		toy = new HashMap<Integer, Toy>();
		stone = new HashMap<Integer, Stone>();
		trampoline = new HashMap<Integer, Trampoline>();
		breedingPool = new HashMap<Integer, BreedingPool>();
		garbagechute = new HashMap<Integer, GarbageChute>();
		foodmaker = new HashMap<Integer, FoodMaker>();
		orangePool = new HashMap<Integer, OrangePool>();
		productchute = new HashMap<Integer, ProductChute>();
		stickyPlate = new HashMap<Integer, StickyPlate>();
		hotPlate = new HashMap<Integer, HotPlate>();
		processerPlate = new HashMap<Integer, ProcesserPlate>();
		mixer = new HashMap<Integer, Mixer>();
		autofeeder = new HashMap<Integer, AutoFeeder>();
		machinePress = new HashMap<Integer, MachinePress>();
		stalk = new HashMap<Integer, Stalk>();
		diffuser = new HashMap<Integer, Diffuser>();
		yunba = new HashMap<Integer, Yunba>();
		sui = new HashMap<Integer, Sui>();
		trash = new HashMap<Integer, Trash>();
		garbageStation = new HashMap<Integer, GarbageStation>();
		house = new HashMap<Integer, House>();
		beltconveyorObj = new HashMap<Integer, BeltconveyorObj>();
		beltconveyor = new LinkedList<Beltconveyor>();
		pool = new LinkedList<Pool>();
		farm = new LinkedList<Farm>();
		okazari = new HashMap<Integer, Okazari>();
		takenOutFood = new HashMap<>();
		takenOutShit = new HashMap<>();
		//generator = new LinkedList<Generator>();

		int mapW = Translate.mapW;
		int mapH = Translate.mapH;

		wallMap = new int[mapW+1][mapH+1];
		clearMap(wallMap);
		fieldMap = new int[mapW+1][mapH+1];
		clearMap(fieldMap);
	}

	/**
	 *  フラグマップ処理もろもろ
	 * @param map フラグマップ
	 */
	public static void clearMap(int[][] map) {
		for(int x = 0; x < map.length; x++) {
			for(int y = 0; y < map[x].length; y++) {
				map[x][y] = 0;
			}
		}
	}
	/**
	 * フィールドフラグを設定する.
	 * @param map フラグマップ
	 * @param x X座標
	 * @param y Y座標
	 * @param w 横
	 * @param h 縦
	 * @param setFlag 追加モードフラグ
	 * @param attribute 属性
	 */
	public static void setFiledFlag(int[][] map, int x, int y, int w, int h, boolean setFlag, int attribute) {
		MapPlaceData tmp = SimYukkuri.world.getCurrentMap();
		int sx = Math.max(0, Math.min(x, Translate.mapW));
		int sy = Math.max(0, Math.min(y, Translate.mapH));
		if (setFlag) {
			// 追加モード
			int ex = x + w;
			int ey = y + h;
			ex = Math.max(0, Math.min(ex, Translate.mapW));
			ey = Math.max(0, Math.min(ey, Translate.mapH));
			for(int py = sy; py < ey; py++) {
				for(int px = sx; px < ex; px++) {
					tmp.fieldMap[px][py] = tmp.fieldMap[px][py] | attribute;
				}
			}
		} else {
			// 削除モード
			int ex = x + w;
			int ey = y + h;
			ex = Math.max(0, Math.min(ex, Translate.mapW));
			ey = Math.max(0, Math.min(ey, Translate.mapH));
			for(int py = sy; py < ey; py++) {
				for(int px = sx; px < ex; px++) {
					tmp.fieldMap[px][py] = tmp.fieldMap[px][py] & (~attribute);
				}
			}
		}
	}
	/**
	 * 壁ラインを設定する.
	 * @param map フラグマップ
	 * @param x1 始まりのX座標
	 * @param y1 始まりのY座標
	 * @param x2 終わりのX座標
	 * @param y2 終わりのY座標
	 * @param setFlag 追加フラグ
	 * @param attribute 属性
	 */
	public static void setWallLine(int[][] map, int x1, int y1, int x2, int y2, boolean setFlag, int attribute) {
		int distance = (int)Math.sqrt(Translate.distance(x1, y1, x2, y2));
		double deltaX = (double)(x2 - x1) / (double)distance;
		double deltaY = (double)(y2 - y1) / (double)distance;
		int sX = x1;
		int sY = y1;
		if(setFlag) {
			for (int t = 0; t <= distance; t++) {
				int x = sX + (int)(deltaX * t);
				int y = sY + (int)(deltaY * t);
				int nx = Math.min(x+1, Translate.mapW);
				int ny = Math.min(y+1, Translate.mapH);

					map[x][y] = map[x][y] | attribute;
					map[nx][y] = map[nx][y] | attribute;
					map[x][ny] = map[x][ny] | attribute;
			}
		} else {
			for (int t = 0; t <= distance; t++) {
				int x = sX + (int)(deltaX * t);
				int y = sY + (int)(deltaY * t);
				int nx = Math.min(x+1, Translate.mapW);
				int ny = Math.min(y+1, Translate.mapH);

				map[x][y] = map[x][y] & (~attribute);
				map[nx][y] = map[nx][y] & (~attribute);
				map[x][ny] = map[x][ny] & (~attribute);
			}
		}
	}
	
	/**
	 * ドスを作る/殺す
	 * @param make 作る場合true、殺す場合false
	 * @return ドス作成、または殺害に成功した場合true
	 */
	public boolean makeOrKillDos(boolean make) {
		if (hasDos) {
			if (make) {
				return false;
			} else {
				hasDos = false;
				return true;
			}
		} else {
			if (make) {
				hasDos = true;
				return true;
			} else {
				return false;
			}
		}
	}
}



