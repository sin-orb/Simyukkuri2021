package src.system;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
	/** 乱数 */
	public Random rnd;
	/** アラーム時間 */
	public int alarmPeriod;
	/** アラーム */
	public boolean alarm;

	// シーン内の各オブジェクトリスト
	/** ゆっくりのリスト */
	public List<Body> body;
	/** うんうんリスト */
	public List<Shit> shit;
	/** 吐餡リスト */
	public List<Vomit> vomit;
	/** 壁リスト */
	public List<Barrier> barrier;
	/** イベントリスト */
	public List<EventPacket> event;
	/** 裏のエフェクト */
	public List<Effect> sortEffect;
	/** 表のエフェクト */
	public List<Effect> frontEffect;
	/** 食べ物リスト */
	public List<Food> food;
	/** トイレリスト */
	public List<Toilet> toilet;
	/** ベッドリスト */
	public List<Bed> bed;
	/** おもちゃリスト */
	public List<Toy> toy;
	/** 小石リスト */
	public List<Stone> stone;
	/** トランポリンリスト */
	public List<Trampoline> trampoline;
	/** 養殖プールリスト */
	public List<BreedingPool> breedingPool;
	/** ダストシュートリスト */
	public List<GarbageChute> garbagechute;
	/** フードメーカーリスト */
	public List<FoodMaker> foodmaker;
	/** オレンジプールリスト */
	public List<OrangePool> orangePool;
	/** 製品投入口リスト */
	public List<ProductChute> productchute;
	/** 粘着板リスト */
	public List<StickyPlate> stickyPlate;
	/** ホットプレートリスト */
	public List<HotPlate> hotPlate;
	/** 加工プレートリスト */
	public List<ProcesserPlate> processerPlate;
	/** ミキサーリスト */
	public List<Mixer> mixer;
	/** 自動給餌器リスト */
	public List<AutoFeeder> autofeeder;
	/** プレス機リスト */
	public List<MachinePress> machinePress;
	/** 茎リスト */
	public List<Stalk> stalk;
	/** ディフューザーリスト */
	public List<Diffuser> diffuser;
	/** ゆばリスト */
	public List<Yunba> yunba;
	/** すぃ～リスト */
	public List<Sui> sui;
	/** ガラクタリスト */
	public List<Trash> trash;
	/** ゴミ収集所リスト */
	public List<GarbageStation> garbageStation;
	/** おうちリスト */
	public List<House> house;
	/** ベルコンオブジェリスト */
	public List<BeltconveyorObj> beltconveyorObj;
	/** ベルコンリスト */
	public List<Beltconveyor> beltconveyor;
	/** 池リスト */
	public List<Pool> pool;
	/** 畑リスト */
	public List<Farm> farm;
	/** おかざりリスト */
	public List<Okazari> okazari;
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

		rnd = new Random();
		alarmPeriod = 0;
		alarm = false;

		body = new LinkedList<Body>();
		shit = new LinkedList<Shit>();
		vomit = new LinkedList<Vomit>();
		barrier = new LinkedList<Barrier>();
		event = new LinkedList<EventPacket>();
		sortEffect = new LinkedList<Effect>();
		frontEffect = new LinkedList<Effect>();
		food = new LinkedList<Food>();
		toilet = new LinkedList<Toilet>();
		bed = new LinkedList<Bed>();
		toy = new LinkedList<Toy>();
		stone = new LinkedList<Stone>();
		trampoline = new LinkedList<Trampoline>();
		breedingPool = new LinkedList<BreedingPool>();
		garbagechute = new LinkedList<GarbageChute>();
		foodmaker = new LinkedList<FoodMaker>();
		orangePool = new LinkedList<OrangePool>();
		productchute = new LinkedList<ProductChute>();
		stickyPlate = new LinkedList<StickyPlate>();
		hotPlate = new LinkedList<HotPlate>();
		processerPlate = new LinkedList<ProcesserPlate>();
		mixer = new LinkedList<Mixer>();
		autofeeder = new LinkedList<AutoFeeder>();
		machinePress = new LinkedList<MachinePress>();
		stalk = new LinkedList<Stalk>();
		diffuser = new LinkedList<Diffuser>();
		yunba = new LinkedList<Yunba>();
		sui = new LinkedList<Sui>();
		trash = new LinkedList<Trash>();
		garbageStation = new LinkedList<GarbageStation>();
		house = new LinkedList<House>();
		beltconveyorObj = new LinkedList<BeltconveyorObj>();
		beltconveyor = new LinkedList<Beltconveyor>();
		pool = new LinkedList<Pool>();
		farm = new LinkedList<Farm>();
		okazari = new LinkedList<Okazari>();
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
		MapPlaceData tmp = SimYukkuri.world.currentMap;
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



