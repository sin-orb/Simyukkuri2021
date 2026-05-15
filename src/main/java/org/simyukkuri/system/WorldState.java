package org.simyukkuri.system;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.effect.Effect;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Okazari;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.item.AutoFeeder;
import org.simyukkuri.entity.core.world.item.Bed;
import org.simyukkuri.entity.core.world.item.BeltconveyorObj;
import org.simyukkuri.entity.core.world.item.BreedingPool;
import org.simyukkuri.entity.core.world.item.Diffuser;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.item.FoodMaker;
import org.simyukkuri.entity.core.world.item.GarbageChute;
import org.simyukkuri.entity.core.world.item.GarbageStation;
import org.simyukkuri.entity.core.world.item.HotPlate;
import org.simyukkuri.entity.core.world.item.House;
import org.simyukkuri.entity.core.world.item.MachinePress;
import org.simyukkuri.entity.core.world.item.Mixer;
import org.simyukkuri.entity.core.world.item.OrangePool;
import org.simyukkuri.entity.core.world.item.ProcessorPlate;
import org.simyukkuri.entity.core.world.item.ProductChute;
import org.simyukkuri.entity.core.world.item.StickyPlate;
import org.simyukkuri.entity.core.world.item.Stone;
import org.simyukkuri.entity.core.world.item.Sui;
import org.simyukkuri.entity.core.world.item.Toilet;
import org.simyukkuri.entity.core.world.item.Toy;
import org.simyukkuri.entity.core.world.item.Trampoline;
import org.simyukkuri.entity.core.world.item.Trash;
import org.simyukkuri.entity.core.world.item.Yunba;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.field.impl.Beltconveyor;
import org.simyukkuri.field.impl.Farm;
import org.simyukkuri.field.impl.Pool;
import org.simyukkuri.util.GameWorld;

/**************************************************
 * 1つのマップ内のオブジェクトなど
 * 従来のセーブデータ内容をまとめている
 */
public class WorldState implements Serializable {

	private static final long serialVersionUID = -7909654211347203362L;
	/** このマップのインデックス */
	private int mapIndex;
	/** アラーム時間 */
	private int alarmPeriod;
	/** アラーム */
	private boolean alarm;

	// シーン内の各オブジェクトリスト
	/** ゆっくりのリスト */
	private Map<Integer, Yukkuri> yukkuriMap;
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
	private Map<Integer, ProcessorPlate> processorPlate;
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
	// public List<Generator> generator;
	/** マップにドスがいるかどうかのフラグ */
	private volatile boolean hasDos;
	/** 壁 */
	private int wallMap[][];
	/** フィールド */
	private int fieldMap[][];

	/**
	 * コンストラクタ
	 * 
	 * @param idx インデックス(0：部屋)
	 */
	public WorldState(int idx) {

		mapIndex = idx;
		alarmPeriod = 0;
		alarm = false;

		yukkuriMap = new HashMap<Integer, Yukkuri>();
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
		processorPlate = new HashMap<Integer, ProcessorPlate>();
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
		// generator = new LinkedList<Generator>();

		int mapW = Translate.getWorldWidth();
		int mapH = Translate.getWorldHeight();

		wallMap = new int[mapW + 1][mapH + 1];
		clearGrid(wallMap);
		fieldMap = new int[mapW + 1][mapH + 1];
		clearGrid(fieldMap);
	}

	public WorldState() {

		alarmPeriod = 0;
		alarm = false;

		yukkuriMap = new HashMap<Integer, Yukkuri>();
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
		processorPlate = new HashMap<Integer, ProcessorPlate>();
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
		// generator = new LinkedList<Generator>();

		int mapW = Translate.getWorldWidth();
		int mapH = Translate.getWorldHeight();

		wallMap = new int[mapW + 1][mapH + 1];
		clearGrid(wallMap);
		fieldMap = new int[mapW + 1][mapH + 1];
		clearGrid(fieldMap);
	}

	/**
	 * フラグマップ処理もろもろ
	 * 
	 * @param map フラグマップ
	 */
	public static void clearGrid(int[][] map) {
		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map[x].length; y++) {
				map[x][y] = 0;
			}
		}
	}

	/**
	 * フィールドフラグを設定する.
	 * 
	 * @param map       フラグマップ
	 * @param x         X座標
	 * @param y         Y座標
	 * @param w         横
	 * @param h         縦
	 * @param setFlag   追加モードフラグ
	 * @param attribute 属性
	 */
	public static void setFieldFlag(int[][] map, int x, int y, int w, int h, boolean setFlag, int attribute) {
		WorldState tmp = GameWorld.get().getCurrentWorldState();
		int sx = Math.max(0, Math.min(x, Translate.getWorldWidth()));
		int sy = Math.max(0, Math.min(y, Translate.getWorldHeight()));
		if (setFlag) {
			// 追加モード
			int ex = x + w;
			int ey = y + h;
			ex = Math.max(0, Math.min(ex, Translate.getWorldWidth()));
			ey = Math.max(0, Math.min(ey, Translate.getWorldHeight()));
			for (int py = sy; py < ey; py++) {
				for (int px = sx; px < ex; px++) {
					tmp.getFieldGrid()[px][py] = tmp.getFieldGrid()[px][py] | attribute;
				}
			}
		} else {
			// 削除モード
			int ex = x + w;
			int ey = y + h;
			ex = Math.max(0, Math.min(ex, Translate.getWorldWidth()));
			ey = Math.max(0, Math.min(ey, Translate.getWorldHeight()));
			for (int py = sy; py < ey; py++) {
				for (int px = sx; px < ex; px++) {
					tmp.getFieldGrid()[px][py] = tmp.getFieldGrid()[px][py] & (~attribute);
				}
			}
		}
	}

	/**
	 * 壁ラインを設定する.
	 * 
	 * @param map       フラグマップ
	 * @param x1        始まりのX座標
	 * @param y1        始まりのY座標
	 * @param x2        終わりのX座標
	 * @param y2        終わりのY座標
	 * @param setFlag   追加フラグ
	 * @param attribute 属性
	 */
	public static void setWallLine(int[][] map, int x1, int y1, int x2, int y2, boolean setFlag, int attribute) {
		int distance = (int) Math.sqrt(Translate.distance(x1, y1, x2, y2));
		double deltaX = (double) (x2 - x1) / (double) distance;
		double deltaY = (double) (y2 - y1) / (double) distance;
		int startX = x1;
		int startY = y1;
		if (setFlag) {
			for (int t = 0; t <= distance; t++) {
				int x = startX + (int) (deltaX * t);
				int y = startY + (int) (deltaY * t);
				int nx = Math.min(x + 1, Translate.getWorldWidth());
				int ny = Math.min(y + 1, Translate.getWorldHeight());

				map[x][y] = map[x][y] | attribute;
				map[nx][y] = map[nx][y] | attribute;
				map[x][ny] = map[x][ny] | attribute;
			}
		} else {
			for (int t = 0; t <= distance; t++) {
				int x = startX + (int) (deltaX * t);
				int y = startY + (int) (deltaY * t);
				int nx = Math.min(x + 1, Translate.getWorldWidth());
				int ny = Math.min(y + 1, Translate.getWorldHeight());

				map[x][y] = map[x][y] & (~attribute);
				map[nx][y] = map[nx][y] & (~attribute);
				map[x][ny] = map[x][ny] & (~attribute);
			}
		}
	}

	/**
	 * ドスを作る。
	 * 
	 * @return 作成に成功した場合true
	 */
	public boolean makeDos() {
		if (hasDos) {
			return false;
		}
		hasDos = true;
		return true;
	}

	/**
	 * ドスを殺す。
	 * 
	 * @return 殺害に成功した場合true
	 */
	public boolean killDos() {
		if (!hasDos) {
			return false;
		}
		hasDos = false;
		return true;
	}

	public int getWorldIndex() {
		return mapIndex;
	}

	public void setWorldIndex(int mapIndex) {
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

	public Map<Integer, Yukkuri> getYukkuriRegistry() {
		return yukkuriMap;
	}

	public void setYukkuriRegistry(Map<Integer, Yukkuri> yukkuriMap) {
		this.yukkuriMap = yukkuriMap;
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

	public List<Barrier> getBarriers() {
		return barrier;
	}

	public void setBarriers(List<Barrier> barrier) {
		this.barrier = barrier;
	}

	public List<EventPacket> getEvents() {
		return event;
	}

	public void setEvents(List<EventPacket> event) {
		this.event = event;
	}

	public Map<Integer, Effect> getSortedEffects() {
		return sortEffect;
	}

	public void setSortedEffects(Map<Integer, Effect> sortEffect) {
		this.sortEffect = sortEffect;
	}

	public Map<Integer, Effect> getFrontEffects() {
		return frontEffect;
	}

	public void setFrontEffects(Map<Integer, Effect> frontEffect) {
		this.frontEffect = frontEffect;
	}

	public Map<Integer, Food> getFoods() {
		return food;
	}

	public void setFoods(Map<Integer, Food> food) {
		this.food = food;
	}

	public Map<Integer, Food> getTakenOutFoods() {
		return takenOutFood;
	}

	public void setTakenOutFoods(Map<Integer, Food> takenOutFood) {
		this.takenOutFood = takenOutFood;
	}

	public Map<Integer, Shit> getTakenOutShits() {
		return takenOutShit;
	}

	public void setTakenOutShits(Map<Integer, Shit> takenOutShit) {
		this.takenOutShit = takenOutShit;
	}

	public Map<Integer, Toilet> getToilets() {
		return toilet;
	}

	public void setToilets(Map<Integer, Toilet> toilet) {
		this.toilet = toilet;
	}

	public Map<Integer, Bed> getBeds() {
		return bed;
	}

	public void setBeds(Map<Integer, Bed> bed) {
		this.bed = bed;
	}

	public Map<Integer, Toy> getToys() {
		return toy;
	}

	public void setToys(Map<Integer, Toy> toy) {
		this.toy = toy;
	}

	public Map<Integer, Stone> getStones() {
		return stone;
	}

	public void setStones(Map<Integer, Stone> stone) {
		this.stone = stone;
	}

	public Map<Integer, Trampoline> getTrampolines() {
		return trampoline;
	}

	public void setTrampolines(Map<Integer, Trampoline> trampoline) {
		this.trampoline = trampoline;
	}

	public Map<Integer, BreedingPool> getBreedingPools() {
		return breedingPool;
	}

	public void setBreedingPools(Map<Integer, BreedingPool> breedingPool) {
		this.breedingPool = breedingPool;
	}

	public Map<Integer, GarbageChute> getGarbageChutes() {
		return garbagechute;
	}

	public void setGarbageChutes(Map<Integer, GarbageChute> garbagechute) {
		this.garbagechute = garbagechute;
	}

	public Map<Integer, FoodMaker> getFoodMakers() {
		return foodmaker;
	}

	public void setFoodMakers(Map<Integer, FoodMaker> foodmaker) {
		this.foodmaker = foodmaker;
	}

	public Map<Integer, OrangePool> getOrangePools() {
		return orangePool;
	}

	public void setOrangePools(Map<Integer, OrangePool> orangePool) {
		this.orangePool = orangePool;
	}

	public Map<Integer, ProductChute> getProductChutes() {
		return productchute;
	}

	public void setProductChutes(Map<Integer, ProductChute> productchute) {
		this.productchute = productchute;
	}

	public Map<Integer, StickyPlate> getStickyPlates() {
		return stickyPlate;
	}

	public void setStickyPlates(Map<Integer, StickyPlate> stickyPlate) {
		this.stickyPlate = stickyPlate;
	}

	public Map<Integer, HotPlate> getHotPlates() {
		return hotPlate;
	}

	public void setHotPlates(Map<Integer, HotPlate> hotPlate) {
		this.hotPlate = hotPlate;
	}

	public Map<Integer, ProcessorPlate> getProcessorPlates() {
		return processorPlate;
	}

	public void setProcessorPlates(Map<Integer, ProcessorPlate> processorPlate) {
		this.processorPlate = processorPlate;
	}

	public Map<Integer, Mixer> getMixers() {
		return mixer;
	}

	public void setMixers(Map<Integer, Mixer> mixer) {
		this.mixer = mixer;
	}

	public Map<Integer, AutoFeeder> getAutoFeeders() {
		return autofeeder;
	}

	public void setAutoFeeders(Map<Integer, AutoFeeder> autofeeder) {
		this.autofeeder = autofeeder;
	}

	public Map<Integer, MachinePress> getMachinePresses() {
		return machinePress;
	}

	public void setMachinePresses(Map<Integer, MachinePress> machinePress) {
		this.machinePress = machinePress;
	}

	public Map<Integer, Stalk> getStalks() {
		return stalk;
	}

	public void setStalks(Map<Integer, Stalk> stalk) {
		this.stalk = stalk;
	}

	public Map<Integer, Diffuser> getDiffusers() {
		return diffuser;
	}

	public void setDiffusers(Map<Integer, Diffuser> diffuser) {
		this.diffuser = diffuser;
	}

	public Map<Integer, Yunba> getYunbas() {
		return yunba;
	}

	public void setYunbas(Map<Integer, Yunba> yunba) {
		this.yunba = yunba;
	}

	public Map<Integer, Sui> getSuis() {
		return sui;
	}

	public void setSuis(Map<Integer, Sui> sui) {
		this.sui = sui;
	}

	public Map<Integer, Trash> getTrashObjects() {
		return trash;
	}

	public void setTrashObjects(Map<Integer, Trash> trash) {
		this.trash = trash;
	}

	public Map<Integer, GarbageStation> getGarbageStations() {
		return garbageStation;
	}

	public void setGarbageStations(Map<Integer, GarbageStation> garbageStation) {
		this.garbageStation = garbageStation;
	}

	public Map<Integer, House> getHouses() {
		return house;
	}

	public void setHouses(Map<Integer, House> house) {
		this.house = house;
	}

	public Map<Integer, BeltconveyorObj> getBeltconveyorObjects() {
		return beltconveyorObj;
	}

	public void setBeltconveyorObjects(Map<Integer, BeltconveyorObj> beltconveyorObj) {
		this.beltconveyorObj = beltconveyorObj;
	}

	public List<Beltconveyor> getBeltconveyors() {
		return beltconveyor;
	}

	public void setBeltconveyors(List<Beltconveyor> beltconveyor) {
		this.beltconveyor = beltconveyor;
	}

	public List<Pool> getPools() {
		return pool;
	}

	public void setPools(List<Pool> pool) {
		this.pool = pool;
	}

	public List<Farm> getFarms() {
		return farm;
	}

	public void setFarms(List<Farm> farm) {
		this.farm = farm;
	}

	public Map<Integer, Okazari> getOkazaris() {
		return okazari;
	}

	public void setOkazaris(Map<Integer, Okazari> okazari) {
		this.okazari = okazari;
	}

	public boolean isHasDos() {
		return hasDos;
	}

	public void setHasDos(boolean hasDos) {
		this.hasDos = hasDos;
	}

	public int[][] getWallGrid() {
		return wallMap;
	}

	public void setWallGrid(int[][] wallMap) {
		this.wallMap = wallMap;
	}

	public int[][] getFieldGrid() {
		return fieldMap;
	}

	public void setFieldGrid(int[][] fieldMap) {
		this.fieldMap = fieldMap;
	}

}
