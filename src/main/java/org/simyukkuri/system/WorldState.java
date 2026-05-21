package org.simyukkuri.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
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

/**
 * テラリウム1ステージ分のゲーム世界状態を保持するクラス.
 * ゆっくりレジストリ・各種エンティティリポジトリ・フィールドデータを一括管理し、
 * セーブ／ロード時の Jackson シリアライズ単位となる.
 */
public class WorldState implements Serializable {

	private static final long serialVersionUID = -7909654211347203362L;

	private int mapIndex;
	private int alarmPeriod;
	private boolean alarm;

	private Map<Integer, Yukkuri> yukkuriMap;
	private volatile boolean hasDos;

	@JsonIgnore
	private FoodRepository foodRepo = new FoodRepository();

	@JsonIgnore
	private ItemRepository itemRepo = new ItemRepository();

	@JsonIgnore
	private MachineRepository machineRepo = new MachineRepository();

	@JsonIgnore
	private FieldRepository fieldRepo = new FieldRepository();

	@JsonIgnore
	private EffectRepository effectRepo = new EffectRepository();

	private transient Map<Integer, Entity> entityIndex;

	/**
	 * マップインデックスを指定してワールド状態を初期化する.
	 *
	 * @param idx マップインデックス
	 */
	public WorldState(int idx) {
		mapIndex = idx;
		alarmPeriod = 0;
		alarm = false;
		yukkuriMap = new HashMap<>();
		initGrids();
	}

	/**
	 * Jackson デシリアライズ用デフォルトコンストラクタ.
	 */
	public WorldState() {
		alarmPeriod = 0;
		alarm = false;
		yukkuriMap = new HashMap<>();
		initGrids();
	}

	private void initGrids() {
		int mapW = Translate.getWorldWidth();
		int mapH = Translate.getWorldHeight();
		int[][] wallMap = new int[mapW + 1][mapH + 1];
		clearGrid(wallMap);
		int[][] fieldMap = new int[mapW + 1][mapH + 1];
		clearGrid(fieldMap);
		fieldRepo.setWallGrid(wallMap);
		fieldRepo.setFieldGrid(fieldMap);
	}

	/**
	 * 2次元グリッドを全要素 0 にリセットする.
	 *
	 * @param map リセット対象のグリッド
	 */
	public static void clearGrid(int[][] map) {
		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map[x].length; y++) {
				map[x][y] = 0;
			}
		}
	}

	/**
	 * フィールドグリッドの指定矩形領域にビットフラグを設定または解除する.
	 * {@code setFlag=true} のとき OR で属性を付加し、{@code false} のとき AND NOT で除去する.
	 *
	 * @param map       フィールドグリッド（現在は内部で getCurrentWorldState() を使用）
	 * @param x         矩形の左端 X 座標
	 * @param y         矩形の上端 Y 座標
	 * @param w         矩形の幅
	 * @param h         矩形の高さ
	 * @param setFlag   true でフラグ付加、false で除去
	 * @param attribute 付加または除去するビット属性値
	 */
	public static void setFieldFlag(int[][] map, int x, int y, int w, int h, boolean setFlag, int attribute) {
		WorldState tmp = GameWorld.get().getCurrentWorldState();
		int sx = Math.max(0, Math.min(x, Translate.getWorldWidth()));
		int sy = Math.max(0, Math.min(y, Translate.getWorldHeight()));
		if (setFlag) {
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
	 * 2点間の直線に沿って壁グリッドのビットフラグを設定または解除する.
	 * 線幅は 1 ピクセル分太くなる（隣接グリッドにも書き込む）.
	 *
	 * @param map       書き込み対象の壁グリッド
	 * @param x1        始点 X 座標
	 * @param y1        始点 Y 座標
	 * @param x2        終点 X 座標
	 * @param y2        終点 Y 座標
	 * @param setFlag   true でフラグ付加、false で除去
	 * @param attribute 付加または除去するビット属性値
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
	 * DOS 状態を開始する.既に DOS 状態なら何もせず false を返す.
	 *
	 * @return DOS 開始に成功した場合 true、既に実行中なら false
	 */
	public boolean makeDos() {
		if (hasDos) {
			return false;
		}
		hasDos = true;
		return true;
	}

	/**
	 * DOS 状態を終了する.DOS 状態でなければ何もせず false を返す.
	 *
	 * @return DOS 終了に成功した場合 true、実行中でなければ false
	 */
	public boolean killDos() {
		if (!hasDos) {
			return false;
		}
		hasDos = false;
		return true;
	}

	/**
	 * このワールドのマップインデックスを返す.
	 *
	 * @return マップインデックス
	 */
	public int getWorldIndex() {
		return mapIndex;
	}

	/**
	 * マップインデックスをセットする.
	 *
	 * @param mapIndex 新しいマップインデックス
	 */
	public void setWorldIndex(int mapIndex) {
		this.mapIndex = mapIndex;
	}

	/**
	 * アラーム発動までの残りティック数を返す.
	 *
	 * @return アラーム残りティック数
	 */
	public int getAlarmPeriod() {
		return alarmPeriod;
	}

	/**
	 * アラーム発動タイマーをセットする.
	 *
	 * @param alarmPeriod 残りティック数
	 */
	public void setAlarmPeriod(int alarmPeriod) {
		this.alarmPeriod = alarmPeriod;
	}

	/**
	 * アラームが発動中かどうかを返す.
	 *
	 * @return アラーム中なら true
	 */
	public boolean isAlarm() {
		return alarm;
	}

	/**
	 * アラーム発動フラグをセットする.
	 *
	 * @param alarm true でアラーム発動
	 */
	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}

	/**
	 * DOS（ゆっくりが大量にいる時の特殊処理）が実行中かどうかを返す.
	 *
	 * @return DOS 実行中なら true
	 */
	public boolean isHasDos() {
		return hasDos;
	}

	/**
	 * DOS フラグをセットする.
	 *
	 * @param hasDos true で DOS 実行中
	 */
	public void setHasDos(boolean hasDos) {
		this.hasDos = hasDos;
	}

	/**
	 * ゆっくり個体 ID からゆっくりインスタンスへのマップを返す.
	 *
	 * @return ゆっくりレジストリ
	 */
	public Map<Integer, Yukkuri> getYukkuriRegistry() {
		return yukkuriMap;
	}

	/**
	 * ゆっくりレジストリをセットする.
	 *
	 * @param yukkuriMap 新しいゆっくりレジストリ
	 */
	public void setYukkuriRegistry(Map<Integer, Yukkuri> yukkuriMap) {
		this.yukkuriMap = yukkuriMap;
	}

	/**
	 * 食べ物マップを返す.
	 *
	 * @return 食べ物マップ
	 */
	public Map<Integer, Food> getFoods() {
		return foodRepo.getFoods();
	}

	/**
	 * 食べ物マップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param food セットする食べ物マップ
	 */
	public void setFoods(Map<Integer, Food> food) {
		foodRepo.setFoods(food);
	}

	/**
	 * 取り出し中の食べ物マップを返す.
	 *
	 * @return 取り出し中食べ物マップ
	 */
	public Map<Integer, Food> getTakenOutFoods() {
		return foodRepo.getTakenOutFoods();
	}

	/**
	 * 取り出し中の食べ物マップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param takenOutFood セットする取り出し中食べ物マップ
	 */
	public void setTakenOutFoods(Map<Integer, Food> takenOutFood) {
		foodRepo.setTakenOutFoods(takenOutFood);
	}

	/**
	 * フンマップを返す.
	 *
	 * @return フンマップ
	 */
	public Map<Integer, Shit> getShit() {
		return foodRepo.getShit();
	}

	/**
	 * フンマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param shit セットするフンマップ
	 */
	public void setShit(Map<Integer, Shit> shit) {
		foodRepo.setShit(shit);
	}

	/**
	 * 取り出し中のフンマップを返す.
	 *
	 * @return 取り出し中フンマップ
	 */
	public Map<Integer, Shit> getTakenOutShits() {
		return foodRepo.getTakenOutShits();
	}

	/**
	 * 取り出し中のフンマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param takenOutShit セットする取り出し中フンマップ
	 */
	public void setTakenOutShits(Map<Integer, Shit> takenOutShit) {
		foodRepo.setTakenOutShits(takenOutShit);
	}

	/**
	 * 嘔吐物マップを返す.
	 *
	 * @return 嘔吐物マップ
	 */
	public Map<Integer, Vomit> getVomit() {
		return foodRepo.getVomit();
	}

	/**
	 * 嘔吐物マップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param vomit セットする嘔吐物マップ
	 */
	public void setVomit(Map<Integer, Vomit> vomit) {
		foodRepo.setVomit(vomit);
	}

	/**
	 * トイレマップを返す.
	 *
	 * @return トイレマップ
	 */
	public Map<Integer, Toilet> getToilets() {
		return itemRepo.getToilets();
	}

	/**
	 * トイレマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param toilet セットするトイレマップ
	 */
	public void setToilets(Map<Integer, Toilet> toilet) {
		itemRepo.setToilets(toilet);
	}

	/**
	 * ベッドマップを返す.
	 *
	 * @return ベッドマップ
	 */
	public Map<Integer, Bed> getBeds() {
		return itemRepo.getBeds();
	}

	/**
	 * ベッドマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param bed セットするベッドマップ
	 */
	public void setBeds(Map<Integer, Bed> bed) {
		itemRepo.setBeds(bed);
	}

	/**
	 * おもちゃマップを返す.
	 *
	 * @return おもちゃマップ
	 */
	public Map<Integer, Toy> getToys() {
		return itemRepo.getToys();
	}

	/**
	 * おもちゃマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param toy セットするおもちゃマップ
	 */
	public void setToys(Map<Integer, Toy> toy) {
		itemRepo.setToys(toy);
	}

	/**
	 * 小石マップを返す.
	 *
	 * @return 小石マップ
	 */
	public Map<Integer, Stone> getStones() {
		return itemRepo.getStones();
	}

	/**
	 * 小石マップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param stone セットする小石マップ
	 */
	public void setStones(Map<Integer, Stone> stone) {
		itemRepo.setStones(stone);
	}

	/**
	 * トランポリンマップを返す.
	 *
	 * @return トランポリンマップ
	 */
	public Map<Integer, Trampoline> getTrampolines() {
		return itemRepo.getTrampolines();
	}

	/**
	 * トランポリンマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param trampoline セットするトランポリンマップ
	 */
	public void setTrampolines(Map<Integer, Trampoline> trampoline) {
		itemRepo.setTrampolines(trampoline);
	}

	/**
	 * 交配池マップを返す.
	 *
	 * @return 交配池マップ
	 */
	public Map<Integer, BreedingPool> getBreedingPools() {
		return itemRepo.getBreedingPools();
	}

	/**
	 * 交配池マップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param breedingPool セットする交配池マップ
	 */
	public void setBreedingPools(Map<Integer, BreedingPool> breedingPool) {
		itemRepo.setBreedingPools(breedingPool);
	}

	/**
	 * 茎マップを返す.
	 *
	 * @return 茎マップ
	 */
	public Map<Integer, Stalk> getStalks() {
		return itemRepo.getStalks();
	}

	/**
	 * 茎マップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param stalk セットする茎マップ
	 */
	public void setStalks(Map<Integer, Stalk> stalk) {
		itemRepo.setStalks(stalk);
	}

	/**
	 * 酢（乗り物アイテム）マップを返す.
	 *
	 * @return 酢マップ
	 */
	public Map<Integer, Sui> getSuis() {
		return itemRepo.getSuis();
	}

	/**
	 * 酢マップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param sui セットする酢マップ
	 */
	public void setSuis(Map<Integer, Sui> sui) {
		itemRepo.setSuis(sui);
	}

	/**
	 * ゴミ箱マップを返す.
	 *
	 * @return ゴミ箱マップ
	 */
	public Map<Integer, Trash> getTrashObjects() {
		return itemRepo.getTrashObjects();
	}

	/**
	 * ゴミ箱マップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param trash セットするゴミ箱マップ
	 */
	public void setTrashObjects(Map<Integer, Trash> trash) {
		itemRepo.setTrashObjects(trash);
	}

	/**
	 * 家マップを返す.
	 *
	 * @return 家マップ
	 */
	public Map<Integer, House> getHouses() {
		return itemRepo.getHouses();
	}

	/**
	 * 家マップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param house セットする家マップ
	 */
	public void setHouses(Map<Integer, House> house) {
		itemRepo.setHouses(house);
	}

	/**
	 * おかざりマップを返す.
	 *
	 * @return おかざりマップ
	 */
	public Map<Integer, Okazari> getOkazaris() {
		return itemRepo.getOkazaris();
	}

	/**
	 * おかざりマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param okazari セットするおかざりマップ
	 */
	public void setOkazaris(Map<Integer, Okazari> okazari) {
		itemRepo.setOkazaris(okazari);
	}

	/**
	 * 自動エサ箱マップを返す.
	 *
	 * @return 自動エサ箱マップ
	 */
	public Map<Integer, AutoFeeder> getAutoFeeders() {
		return machineRepo.getAutoFeeders();
	}

	/**
	 * 自動エサ箱マップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param autofeeder セットする自動エサ箱マップ
	 */
	public void setAutoFeeders(Map<Integer, AutoFeeder> autofeeder) {
		machineRepo.setAutoFeeders(autofeeder);
	}

	/**
	 * 食べ物製造機マップを返す.
	 *
	 * @return 食べ物製造機マップ
	 */
	public Map<Integer, FoodMaker> getFoodMakers() {
		return machineRepo.getFoodMakers();
	}

	/**
	 * 食べ物製造機マップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param foodmaker セットする食べ物製造機マップ
	 */
	public void setFoodMakers(Map<Integer, FoodMaker> foodmaker) {
		machineRepo.setFoodMakers(foodmaker);
	}

	/**
	 * ミキサーマップを返す.
	 *
	 * @return ミキサーマップ
	 */
	public Map<Integer, Mixer> getMixers() {
		return machineRepo.getMixers();
	}

	/**
	 * ミキサーマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param mixer セットするミキサーマップ
	 */
	public void setMixers(Map<Integer, Mixer> mixer) {
		machineRepo.setMixers(mixer);
	}

	/**
	 * ディフューザーマップを返す.
	 *
	 * @return ディフューザーマップ
	 */
	public Map<Integer, Diffuser> getDiffusers() {
		return machineRepo.getDiffusers();
	}

	/**
	 * ディフューザーマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param diffuser セットするディフューザーマップ
	 */
	public void setDiffusers(Map<Integer, Diffuser> diffuser) {
		machineRepo.setDiffusers(diffuser);
	}

	/**
	 * プレス機マップを返す.
	 *
	 * @return プレス機マップ
	 */
	public Map<Integer, MachinePress> getMachinePresses() {
		return machineRepo.getMachinePresses();
	}

	/**
	 * プレス機マップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param machinePress セットするプレス機マップ
	 */
	public void setMachinePresses(Map<Integer, MachinePress> machinePress) {
		machineRepo.setMachinePresses(machinePress);
	}

	/**
	 * ホットプレートマップを返す.
	 *
	 * @return ホットプレートマップ
	 */
	public Map<Integer, HotPlate> getHotPlates() {
		return machineRepo.getHotPlates();
	}

	/**
	 * ホットプレートマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param hotPlate セットするホットプレートマップ
	 */
	public void setHotPlates(Map<Integer, HotPlate> hotPlate) {
		machineRepo.setHotPlates(hotPlate);
	}

	/**
	 * 粘着プレートマップを返す.
	 *
	 * @return 粘着プレートマップ
	 */
	public Map<Integer, StickyPlate> getStickyPlates() {
		return machineRepo.getStickyPlates();
	}

	/**
	 * 粘着プレートマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param stickyPlate セットする粘着プレートマップ
	 */
	public void setStickyPlates(Map<Integer, StickyPlate> stickyPlate) {
		machineRepo.setStickyPlates(stickyPlate);
	}

	/**
	 * 加工プレートマップを返す.
	 *
	 * @return 加工プレートマップ
	 */
	public Map<Integer, ProcessorPlate> getProcessorPlates() {
		return machineRepo.getProcessorPlates();
	}

	/**
	 * 加工プレートマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param processorPlate セットする加工プレートマップ
	 */
	public void setProcessorPlates(Map<Integer, ProcessorPlate> processorPlate) {
		machineRepo.setProcessorPlates(processorPlate);
	}

	/**
	 * ゴミシュートマップを返す.
	 *
	 * @return ゴミシュートマップ
	 */
	public Map<Integer, GarbageChute> getGarbageChutes() {
		return machineRepo.getGarbageChutes();
	}

	/**
	 * ゴミシュートマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param garbagechute セットするゴミシュートマップ
	 */
	public void setGarbageChutes(Map<Integer, GarbageChute> garbagechute) {
		machineRepo.setGarbageChutes(garbagechute);
	}

	/**
	 * ゴミ処理ステーションマップを返す.
	 *
	 * @return ゴミ処理ステーションマップ
	 */
	public Map<Integer, GarbageStation> getGarbageStations() {
		return machineRepo.getGarbageStations();
	}

	/**
	 * ゴミ処理ステーションマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param garbageStation セットするゴミ処理ステーションマップ
	 */
	public void setGarbageStations(Map<Integer, GarbageStation> garbageStation) {
		machineRepo.setGarbageStations(garbageStation);
	}

	/**
	 * 製品シュートマップを返す.
	 *
	 * @return 製品シュートマップ
	 */
	public Map<Integer, ProductChute> getProductChutes() {
		return machineRepo.getProductChutes();
	}

	/**
	 * 製品シュートマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param productchute セットする製品シュートマップ
	 */
	public void setProductChutes(Map<Integer, ProductChute> productchute) {
		machineRepo.setProductChutes(productchute);
	}

	/**
	 * オレンジプールマップを返す.
	 *
	 * @return オレンジプールマップ
	 */
	public Map<Integer, OrangePool> getOrangePools() {
		return machineRepo.getOrangePools();
	}

	/**
	 * オレンジプールマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param orangePool セットするオレンジプールマップ
	 */
	public void setOrangePools(Map<Integer, OrangePool> orangePool) {
		machineRepo.setOrangePools(orangePool);
	}

	/**
	 * 雲母（空を飛べる乗り物アイテム）マップを返す.
	 *
	 * @return 雲母マップ
	 */
	public Map<Integer, Yunba> getYunbas() {
		return machineRepo.getYunbas();
	}

	/**
	 * 雲母マップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param yunba セットする雲母マップ
	 */
	public void setYunbas(Map<Integer, Yunba> yunba) {
		machineRepo.setYunbas(yunba);
	}

	/**
	 * ベルトコンベアオブジェクトマップを返す.
	 *
	 * @return ベルトコンベアオブジェクトマップ
	 */
	public Map<Integer, BeltconveyorObj> getBeltconveyorObjects() {
		return machineRepo.getBeltconveyorObjects();
	}

	/**
	 * ベルトコンベアオブジェクトマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param beltconveyorObj セットするベルトコンベアオブジェクトマップ
	 */
	public void setBeltconveyorObjects(Map<Integer, BeltconveyorObj> beltconveyorObj) {
		machineRepo.setBeltconveyorObjects(beltconveyorObj);
	}

	/**
	 * ベルトコンベアリストを返す.
	 *
	 * @return ベルトコンベアリスト
	 */
	public List<Beltconveyor> getBeltconveyors() {
		return machineRepo.getBeltconveyors();
	}

	/**
	 * ベルトコンベアリストをセットする.主にデシリアライズ時に使用.
	 *
	 * @param beltconveyor セットするベルトコンベアリスト
	 */
	public void setBeltconveyors(List<Beltconveyor> beltconveyor) {
		machineRepo.setBeltconveyors(beltconveyor);
	}

	/**
	 * 壁グリッドを返す.非ゼロのセルが壁を示す.
	 *
	 * @return 壁グリッド
	 */
	public int[][] getWallGrid() {
		return fieldRepo.getWallGrid();
	}

	/**
	 * 壁グリッドをセットする.主にデシリアライズ時に使用.
	 *
	 * @param wallMap セットする壁グリッド
	 */
	public void setWallGrid(int[][] wallMap) {
		fieldRepo.setWallGrid(wallMap);
	}

	/**
	 * フィールドグリッドを返す.各セルはビットフラグで地形属性を保持する.
	 *
	 * @return フィールドグリッド
	 */
	public int[][] getFieldGrid() {
		return fieldRepo.getFieldGrid();
	}

	/**
	 * フィールドグリッドをセットする.主にデシリアライズ時に使用.
	 *
	 * @param fieldMap セットするフィールドグリッド
	 */
	public void setFieldGrid(int[][] fieldMap) {
		fieldRepo.setFieldGrid(fieldMap);
	}

	/**
	 * バリアリストを返す.
	 *
	 * @return バリアリスト
	 */
	public List<Barrier> getBarriers() {
		return fieldRepo.getBarriers();
	}

	/**
	 * バリアリストをセットする.主にデシリアライズ時に使用.
	 *
	 * @param barrier セットするバリアリスト
	 */
	public void setBarriers(List<Barrier> barrier) {
		fieldRepo.setBarriers(barrier);
	}

	/**
	 * 水場リストを返す.
	 *
	 * @return 水場リスト
	 */
	public List<Pool> getPools() {
		return fieldRepo.getPools();
	}

	/**
	 * 水場リストをセットする.主にデシリアライズ時に使用.
	 *
	 * @param pool セットする水場リスト
	 */
	public void setPools(List<Pool> pool) {
		fieldRepo.setPools(pool);
	}

	/**
	 * 農場リストを返す.
	 *
	 * @return 農場リスト
	 */
	public List<Farm> getFarms() {
		return fieldRepo.getFarms();
	}

	/**
	 * 農場リストをセットする.主にデシリアライズ時に使用.
	 *
	 * @param farm セットする農場リスト
	 */
	public void setFarms(List<Farm> farm) {
		fieldRepo.setFarms(farm);
	}

	/**
	 * z ソートされた通常エフェクトマップを返す.
	 *
	 * @return 通常エフェクトマップ
	 */
	public Map<Integer, Effect> getSortedEffects() {
		return effectRepo.getSortedEffects();
	}

	/**
	 * 通常エフェクトマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param sortEffect セットする通常エフェクトマップ
	 */
	public void setSortedEffects(Map<Integer, Effect> sortEffect) {
		effectRepo.setSortedEffects(sortEffect);
	}

	/**
	 * 常に最前面に描画されるエフェクトマップを返す.
	 *
	 * @return 最前面エフェクトマップ
	 */
	public Map<Integer, Effect> getFrontEffects() {
		return effectRepo.getFrontEffects();
	}

	/**
	 * 最前面エフェクトマップをセットする.主にデシリアライズ時に使用.
	 *
	 * @param frontEffect セットする最前面エフェクトマップ
	 */
	public void setFrontEffects(Map<Integer, Effect> frontEffect) {
		effectRepo.setFrontEffects(frontEffect);
	}

	/**
	 * アクティブなイベントパケットリストを返す.
	 *
	 * @return イベントパケットリスト
	 */
	public List<EventPacket> getEvents() {
		return effectRepo.getEvents();
	}

	/**
	 * イベントパケットリストをセットする.主にデシリアライズ時に使用.
	 *
	 * @param event セットするイベントパケットリスト
	 */
	public void setEvents(List<EventPacket> event) {
		effectRepo.setEvents(event);
	}

	/**
	 * エンティティを ID とともにインデックスに登録する.
	 *
	 * @param id エンティティ ID
	 * @param e  登録するエンティティ
	 */
	public void registerEntity(int id, Entity e) {
		if (entityIndex == null) {
			entityIndex = new HashMap<>();
		}
		entityIndex.put(id, e);
	}

	/**
	 * 指定 ID のエンティティをインデックスから削除する.
	 *
	 * @param id 削除するエンティティ ID
	 */
	public void unregisterEntity(int id) {
		if (entityIndex != null) {
			entityIndex.remove(id);
		}
	}

	/**
	 * エンティティ ID から全エンティティへの逆引きインデックスを返す.
	 * 未構築の場合は再構築してから返す.
	 *
	 * @return エンティティインデックス
	 */
	@JsonIgnore
	public Map<Integer, Entity> getEntityIndex() {
		if (entityIndex == null) {
			rebuildEntityIndex();
		}
		return entityIndex;
	}

	/**
	 * 全リポジトリを走査してエンティティインデックスを再構築する.
	 * セーブロード後などに呼ぶ.
	 */
	public void rebuildEntityIndex() {
		entityIndex = new HashMap<>();
		for (Map.Entry<Integer, Yukkuri> e : yukkuriMap.entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Shit> e : foodRepo.getShit().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Vomit> e : foodRepo.getVomit().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Effect> e : effectRepo.getSortedEffects().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Effect> e : effectRepo.getFrontEffects().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Food> e : foodRepo.getFoods().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Food> e : foodRepo.getTakenOutFoods().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Shit> e : foodRepo.getTakenOutShits().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Toilet> e : itemRepo.getToilets().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Bed> e : itemRepo.getBeds().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Toy> e : itemRepo.getToys().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Stone> e : itemRepo.getStones().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Trampoline> e : itemRepo.getTrampolines().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, BreedingPool> e : itemRepo.getBreedingPools().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, GarbageChute> e : machineRepo.getGarbageChutes().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, FoodMaker> e : machineRepo.getFoodMakers().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, OrangePool> e : machineRepo.getOrangePools().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, ProductChute> e : machineRepo.getProductChutes().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, StickyPlate> e : machineRepo.getStickyPlates().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, HotPlate> e : machineRepo.getHotPlates().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, ProcessorPlate> e : machineRepo.getProcessorPlates().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Mixer> e : machineRepo.getMixers().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, AutoFeeder> e : machineRepo.getAutoFeeders().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, MachinePress> e : machineRepo.getMachinePresses().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Stalk> e : itemRepo.getStalks().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Diffuser> e : machineRepo.getDiffusers().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Yunba> e : machineRepo.getYunbas().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Sui> e : itemRepo.getSuis().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Trash> e : itemRepo.getTrashObjects().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, GarbageStation> e : machineRepo.getGarbageStations().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, House> e : itemRepo.getHouses().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, BeltconveyorObj> e : machineRepo.getBeltconveyorObjects().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Integer, Okazari> e : itemRepo.getOkazaris().entrySet()) {
			entityIndex.put(e.getKey(), e.getValue());
		}
	}
}
