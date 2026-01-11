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

	private static final long serialVersionUID = -7909654211347203362L;
	/** このマップのインデックス */
	private int mapIndex;
	/** アラーム時間 */
	private int alarmPeriod;
	/** アラーム */
	private boolean alarm;

	// シーン内の各オブジェクトリスト
	/** ゆっくりのリスト */
	private Map<Integer, Body> body;
	/** うんうんリスト */
	private Map<Integer, Shit> shit;
	/** 吐餡リスト */
	private Map<Integer, Vomit> vomit;
	/** 壁リスト */
	private List<Barrier> barrier;
	/** イベントリスト */
	private List<EventPacket> event;
	/** 裏のエフェクト */
	private Map<Integer, Effect> sortEffect;
	/** 表のエフェクト */
	private Map<Integer, Effect> frontEffect;
	/** 食べ物リスト */
	private Map<Integer, Food> food;
	/** お持ち帰り中の食べ物リスト */
	private Map<Integer, Food> takenOutFood;
	/** お持ち帰り中のうんうんリスト */
	private Map<Integer, Shit> takenOutShit;
	/** トイレリスト */
	private Map<Integer, Toilet> toilet;
	/** ベッドリスト */
	private Map<Integer, Bed> bed;
	/** おもちゃリスト */
	private Map<Integer, Toy> toy;
	/** 小石リスト */
	private Map<Integer, Stone> stone;
	/** トランポリンリスト */
	private Map<Integer, Trampoline> trampoline;
	/** 養殖プールリスト */
	private Map<Integer, BreedingPool> breedingPool;
	/** ダストシュートリスト */
	private Map<Integer, GarbageChute> garbagechute;
	/** フードメーカーリスト */
	private Map<Integer, FoodMaker> foodmaker;
	/** オレンジプールリスト */
	private Map<Integer, OrangePool> orangePool;
	/** 製品投入口リスト */
	private Map<Integer, ProductChute> productchute;
	/** 粘着板リスト */
	private Map<Integer, StickyPlate> stickyPlate;
	/** ホットプレートリスト */
	private Map<Integer, HotPlate> hotPlate;
	/** 加工プレートリスト */
	private Map<Integer, ProcesserPlate> processerPlate;
	/** ミキサーリスト */
	private Map<Integer, Mixer> mixer;
	/** 自動給餌器リスト */
	private Map<Integer, AutoFeeder> autofeeder;
	/** プレス機リスト */
	private Map<Integer, MachinePress> machinePress;
	/** 茎リスト */
	private Map<Integer, Stalk> stalk;
	/** ディフューザーリスト */
	private Map<Integer, Diffuser> diffuser;
	/** ゆばリスト */
	private Map<Integer, Yunba> yunba;
	/** すぃ～リスト */
	private Map<Integer, Sui> sui;
	/** ガラクタリスト */
	private Map<Integer, Trash> trash;
	/** ゴミ収集所リスト */
	private Map<Integer, GarbageStation> garbageStation;
	/** おうちリスト */
	private Map<Integer, House> house;
	/** ベルコンオブジェリスト */
	private Map<Integer, BeltconveyorObj> beltconveyorObj;
	/** ベルコンリスト */
	private List<Beltconveyor> beltconveyor;
	/** 池リスト */
	private List<Pool> pool;
	/** 畑リスト */
	private List<Farm> farm;
	/** おかざりリスト */
	private Map<Integer, Okazari> okazari;
	/** 発電機リスト */
	//public List<Generator> generator;
	/** マップにドスがいるかどうかのフラグ */
	private volatile boolean hasDos;
	/** 壁 */
	private int wallMap[][];
	/** フィールド */
	private int fieldMap[][];
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

		int mapW = Translate.getMapW();
		int mapH = Translate.getMapH();

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

		int mapW = Translate.getMapW();
		int mapH = Translate.getMapH();

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
		int sx = Math.max(0, Math.min(x, Translate.getMapW()));
		int sy = Math.max(0, Math.min(y, Translate.getMapH()));
		if (setFlag) {
			// 追加モード
			int ex = x + w;
			int ey = y + h;
			ex = Math.max(0, Math.min(ex, Translate.getMapW()));
			ey = Math.max(0, Math.min(ey, Translate.getMapH()));
			for(int py = sy; py < ey; py++) {
				for(int px = sx; px < ex; px++) {
					tmp.getFieldMap()[px][py] = tmp.getFieldMap()[px][py] | attribute;
				}
			}
		} else {
			// 削除モード
			int ex = x + w;
			int ey = y + h;
			ex = Math.max(0, Math.min(ex, Translate.getMapW()));
			ey = Math.max(0, Math.min(ey, Translate.getMapH()));
			for(int py = sy; py < ey; py++) {
				for(int px = sx; px < ex; px++) {
					tmp.getFieldMap()[px][py] = tmp.getFieldMap()[px][py] & (~attribute);
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
				int nx = Math.min(x+1, Translate.getMapW());
				int ny = Math.min(y+1, Translate.getMapH());

					map[x][y] = map[x][y] | attribute;
					map[nx][y] = map[nx][y] | attribute;
					map[x][ny] = map[x][ny] | attribute;
			}
		} else {
			for (int t = 0; t <= distance; t++) {
				int x = sX + (int)(deltaX * t);
				int y = sY + (int)(deltaY * t);
				int nx = Math.min(x+1, Translate.getMapW());
				int ny = Math.min(y+1, Translate.getMapH());

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

	public int getMapIndex() {
		return mapIndex;
	}

	public void setMapIndex(int mapIndex) {
		this.mapIndex = mapIndex;
	}

	public int getAlarmPeriod() {
		return alarmPeriod;
	}

	public void setAlarmPeriod(int alarmPeriod) {
		this.alarmPeriod = alarmPeriod;
	}

	public boolean isAlarm() {
		return alarm;
	}

	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}

	public Map<Integer, Body> getBody() {
		return body;
	}

	public void setBody(Map<Integer, Body> body) {
		this.body = body;
	}

	public Map<Integer, Shit> getShit() {
		return shit;
	}

	public void setShit(Map<Integer, Shit> shit) {
		this.shit = shit;
	}

	public Map<Integer, Vomit> getVomit() {
		return vomit;
	}

	public void setVomit(Map<Integer, Vomit> vomit) {
		this.vomit = vomit;
	}

	public List<Barrier> getBarrier() {
		return barrier;
	}

	public void setBarrier(List<Barrier> barrier) {
		this.barrier = barrier;
	}

	public List<EventPacket> getEvent() {
		return event;
	}

	public void setEvent(List<EventPacket> event) {
		this.event = event;
	}

	public Map<Integer, Effect> getSortEffect() {
		return sortEffect;
	}

	public void setSortEffect(Map<Integer, Effect> sortEffect) {
		this.sortEffect = sortEffect;
	}

	public Map<Integer, Effect> getFrontEffect() {
		return frontEffect;
	}

	public void setFrontEffect(Map<Integer, Effect> frontEffect) {
		this.frontEffect = frontEffect;
	}

	public Map<Integer, Food> getFood() {
		return food;
	}

	public void setFood(Map<Integer, Food> food) {
		this.food = food;
	}

	public Map<Integer, Food> getTakenOutFood() {
		return takenOutFood;
	}

	public void setTakenOutFood(Map<Integer, Food> takenOutFood) {
		this.takenOutFood = takenOutFood;
	}

	public Map<Integer, Shit> getTakenOutShit() {
		return takenOutShit;
	}

	public void setTakenOutShit(Map<Integer, Shit> takenOutShit) {
		this.takenOutShit = takenOutShit;
	}

	public Map<Integer, Toilet> getToilet() {
		return toilet;
	}

	public void setToilet(Map<Integer, Toilet> toilet) {
		this.toilet = toilet;
	}

	public Map<Integer, Bed> getBed() {
		return bed;
	}

	public void setBed(Map<Integer, Bed> bed) {
		this.bed = bed;
	}

	public Map<Integer, Toy> getToy() {
		return toy;
	}

	public void setToy(Map<Integer, Toy> toy) {
		this.toy = toy;
	}

	public Map<Integer, Stone> getStone() {
		return stone;
	}

	public void setStone(Map<Integer, Stone> stone) {
		this.stone = stone;
	}

	public Map<Integer, Trampoline> getTrampoline() {
		return trampoline;
	}

	public void setTrampoline(Map<Integer, Trampoline> trampoline) {
		this.trampoline = trampoline;
	}

	public Map<Integer, BreedingPool> getBreedingPool() {
		return breedingPool;
	}

	public void setBreedingPool(Map<Integer, BreedingPool> breedingPool) {
		this.breedingPool = breedingPool;
	}

	public Map<Integer, GarbageChute> getGarbagechute() {
		return garbagechute;
	}

	public void setGarbagechute(Map<Integer, GarbageChute> garbagechute) {
		this.garbagechute = garbagechute;
	}

	public Map<Integer, FoodMaker> getFoodmaker() {
		return foodmaker;
	}

	public void setFoodmaker(Map<Integer, FoodMaker> foodmaker) {
		this.foodmaker = foodmaker;
	}

	public Map<Integer, OrangePool> getOrangePool() {
		return orangePool;
	}

	public void setOrangePool(Map<Integer, OrangePool> orangePool) {
		this.orangePool = orangePool;
	}

	public Map<Integer, ProductChute> getProductchute() {
		return productchute;
	}

	public void setProductchute(Map<Integer, ProductChute> productchute) {
		this.productchute = productchute;
	}

	public Map<Integer, StickyPlate> getStickyPlate() {
		return stickyPlate;
	}

	public void setStickyPlate(Map<Integer, StickyPlate> stickyPlate) {
		this.stickyPlate = stickyPlate;
	}

	public Map<Integer, HotPlate> getHotPlate() {
		return hotPlate;
	}

	public void setHotPlate(Map<Integer, HotPlate> hotPlate) {
		this.hotPlate = hotPlate;
	}

	public Map<Integer, ProcesserPlate> getProcesserPlate() {
		return processerPlate;
	}

	public void setProcesserPlate(Map<Integer, ProcesserPlate> processerPlate) {
		this.processerPlate = processerPlate;
	}

	public Map<Integer, Mixer> getMixer() {
		return mixer;
	}

	public void setMixer(Map<Integer, Mixer> mixer) {
		this.mixer = mixer;
	}

	public Map<Integer, AutoFeeder> getAutofeeder() {
		return autofeeder;
	}

	public void setAutofeeder(Map<Integer, AutoFeeder> autofeeder) {
		this.autofeeder = autofeeder;
	}

	public Map<Integer, MachinePress> getMachinePress() {
		return machinePress;
	}

	public void setMachinePress(Map<Integer, MachinePress> machinePress) {
		this.machinePress = machinePress;
	}

	public Map<Integer, Stalk> getStalk() {
		return stalk;
	}

	public void setStalk(Map<Integer, Stalk> stalk) {
		this.stalk = stalk;
	}

	public Map<Integer, Diffuser> getDiffuser() {
		return diffuser;
	}

	public void setDiffuser(Map<Integer, Diffuser> diffuser) {
		this.diffuser = diffuser;
	}

	public Map<Integer, Yunba> getYunba() {
		return yunba;
	}

	public void setYunba(Map<Integer, Yunba> yunba) {
		this.yunba = yunba;
	}

	public Map<Integer, Sui> getSui() {
		return sui;
	}

	public void setSui(Map<Integer, Sui> sui) {
		this.sui = sui;
	}

	public Map<Integer, Trash> getTrash() {
		return trash;
	}

	public void setTrash(Map<Integer, Trash> trash) {
		this.trash = trash;
	}

	public Map<Integer, GarbageStation> getGarbageStation() {
		return garbageStation;
	}

	public void setGarbageStation(Map<Integer, GarbageStation> garbageStation) {
		this.garbageStation = garbageStation;
	}

	public Map<Integer, House> getHouse() {
		return house;
	}

	public void setHouse(Map<Integer, House> house) {
		this.house = house;
	}

	public Map<Integer, BeltconveyorObj> getBeltconveyorObj() {
		return beltconveyorObj;
	}

	public void setBeltconveyorObj(Map<Integer, BeltconveyorObj> beltconveyorObj) {
		this.beltconveyorObj = beltconveyorObj;
	}

	public List<Beltconveyor> getBeltconveyor() {
		return beltconveyor;
	}

	public void setBeltconveyor(List<Beltconveyor> beltconveyor) {
		this.beltconveyor = beltconveyor;
	}

	public List<Pool> getPool() {
		return pool;
	}

	public void setPool(List<Pool> pool) {
		this.pool = pool;
	}

	public List<Farm> getFarm() {
		return farm;
	}

	public void setFarm(List<Farm> farm) {
		this.farm = farm;
	}

	public Map<Integer, Okazari> getOkazari() {
		return okazari;
	}

	public void setOkazari(Map<Integer, Okazari> okazari) {
		this.okazari = okazari;
	}

	public boolean isHasDos() {
		return hasDos;
	}

	public void setHasDos(boolean hasDos) {
		this.hasDos = hasDos;
	}

	public int[][] getWallMap() {
		return wallMap;
	}

	public void setWallMap(int[][] wallMap) {
		this.wallMap = wallMap;
	}

	public int[][] getFieldMap() {
		return fieldMap;
	}

	public void setFieldMap(int[][] fieldMap) {
		this.fieldMap = fieldMap;
	}
	
}




