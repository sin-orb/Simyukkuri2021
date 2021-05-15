package src.system;

import java.io.Serializable;
import java.util.ArrayList;
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
import src.item.Generator;
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

	1つのマップ内のオブジェクトなど
	従来のセーブデータ内容をまとめている

*/
public class MapPlaceData implements Serializable {
	static final long serialVersionUID = 1L;

	// このマップのインデックス
	public int mapIndex;

	public Random rnd;
	public int alarmPeriod;
	public boolean alarm;

	// シーン内の各オブジェクトリスト
	public ArrayList<Body> body;
	public ArrayList<Shit> shit;
	public ArrayList<Vomit> vomit;
	public ArrayList<Barrier> barrier;
	public ArrayList<EventPacket> event;
	public ArrayList<Effect> sortEffect;
	public ArrayList<Effect> frontEffect;
	public ArrayList<Food> food;
	public ArrayList<Toilet> toilet;
	public ArrayList<Bed> bed;
	public ArrayList<Toy> toy;
	public ArrayList<Stone> stone;
	public ArrayList<Trampoline> trampoline;
	public ArrayList<BreedingPool> breedingPool;
	public ArrayList<GarbageChute> garbagechute;
	public ArrayList<FoodMaker> foodmaker;
	public ArrayList<OrangePool> orangePool;
	public ArrayList<ProductChute> productchute;
	public ArrayList<StickyPlate> stickyPlate;
	public ArrayList<HotPlate> hotPlate;
	public ArrayList<ProcesserPlate> processerPlate;
	public ArrayList<Mixer> mixer;
	public ArrayList<AutoFeeder> autofeeder;
	public ArrayList<MachinePress> machinePress;
	public ArrayList<Stalk> stalk;
	public ArrayList<Diffuser> diffuser;
	public ArrayList<Yunba> yunba;
	public ArrayList<Sui> sui;
	public ArrayList<Trash> trash;
	public ArrayList<GarbageStation> garbageStation;
	public ArrayList<House> house;
	public ArrayList<BeltconveyorObj> beltconveyorObj;
	public ArrayList<Beltconveyor> beltconveyor;
	public ArrayList<Pool> pool;
	public ArrayList<Farm> farm;
	public ArrayList<Okazari> okazari;
	public ArrayList<Generator> generator;
	private volatile boolean hasDos;

	public int wallMap[][];
	public int fieldMap[][];

	public MapPlaceData(int idx) {

		mapIndex = idx;

		rnd = new Random();
		alarmPeriod = 0;
		alarm = false;

		body = new ArrayList<Body>();
		shit = new ArrayList<Shit>();
		vomit = new ArrayList<Vomit>();
		barrier = new ArrayList<Barrier>();
		event = new ArrayList<EventPacket>();
		sortEffect = new ArrayList<Effect>();
		frontEffect = new ArrayList<Effect>();
		food = new ArrayList<Food>();
		toilet = new ArrayList<Toilet>();
		bed = new ArrayList<Bed>();
		toy = new ArrayList<Toy>();
		stone = new ArrayList<Stone>();
		trampoline = new ArrayList<Trampoline>();
		breedingPool = new ArrayList<BreedingPool>();
		garbagechute = new ArrayList<GarbageChute>();
		foodmaker = new ArrayList<FoodMaker>();
		orangePool = new ArrayList<OrangePool>();
		productchute = new ArrayList<ProductChute>();
		stickyPlate = new ArrayList<StickyPlate>();
		hotPlate = new ArrayList<HotPlate>();
		processerPlate = new ArrayList<ProcesserPlate>();
		mixer = new ArrayList<Mixer>();
		autofeeder = new ArrayList<AutoFeeder>();
		machinePress = new ArrayList<MachinePress>();
		stalk = new ArrayList<Stalk>();
		diffuser = new ArrayList<Diffuser>();
		yunba = new ArrayList<Yunba>();
		sui = new ArrayList<Sui>();
		trash = new ArrayList<Trash>();
		garbageStation = new ArrayList<GarbageStation>();
		house = new ArrayList<House>();
		beltconveyorObj = new ArrayList<BeltconveyorObj>();
		beltconveyor = new ArrayList<Beltconveyor>();
		pool = new ArrayList<Pool>();
		farm = new ArrayList<Farm>();
		okazari = new ArrayList<Okazari>();
		generator = new ArrayList<Generator>();

		int mapW = Translate.mapW;
		int mapH = Translate.mapH;

		wallMap = new int[mapW+1][mapH+1];
		clearMap(wallMap);
		fieldMap = new int[mapW+1][mapH+1];
		clearMap(fieldMap);
	}

	// フラグマップ処理もろもろ
	public static void clearMap(int[][] map) {
		for(int x = 0; x < map.length; x++) {
			for(int y = 0; y < map[x].length; y++) {
				map[x][y] = 0;
			}
		}
	}

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



