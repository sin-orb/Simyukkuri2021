package src.draw;

import java.beans.Transient;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import src.SimYukkuri;
import src.base.Body;
import src.base.Effect;
import src.base.Obj;
import src.base.ObjEX;
import src.game.Player;
import src.system.FieldShapeBase;
import src.system.MapPlaceData;
import src.system.MapWindow;
import src.util.YukkuriUtil;
import src.yukkuri.HybridYukkuri;

/***********************************************
 * 
 * 全ワールドデータ
 * プレイヤーと複数のマップ情報を保持
 * セーブデータになる
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class World implements Serializable {

	private static final long serialVersionUID = 1586355554271822104L;
	/** プレイヤー情報 ワールド内に1つのみ存在できる */
	private Player player;
	/** 現在の画面のindex */
	private int currentMapIdx = 0;
	/** ウィンドウ情報 */
	private int windowType;
	/** マップサイズ情報 */
	private int terrariumSizeIndex;

	private int maxObjId = 0;
	private int maxUniqueId = 0;

	/** マップ移動予定値。すぐ切り替えずメインのスレッド動作に衝突しないようにタイミングを図るため */
	private int nextMap;
	/** ワールド内のマップリスト */
	private List<MapPlaceData> mapList;

	public int getCurrentMapIdx() {
		return currentMapIdx;
	}

	public void setCurrentMapIdx(int currentMapIdx) {
		this.currentMapIdx = currentMapIdx;
	}

	public int getWindowType() {
		return windowType;
	}

	public void setWindowType(int windowType) {
		this.windowType = windowType;
	}

	public int getTerrariumSizeIndex() {
		return terrariumSizeIndex;
	}

	public void setTerrariumSizeIndex(int terrariumSizeIndex) {
		this.terrariumSizeIndex = terrariumSizeIndex;
	}

	public List<MapPlaceData> getMapList() {
		return mapList;
	}

	public void setMapList(List<MapPlaceData> mapList) {
		this.mapList = mapList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * コンストラクタ.
	 * 
	 * @param winType   Windowタイプ
	 * @param sizeIndex サイズ
	 */
	public World(int winType, int sizeIndex) {

		player = new Player();
		nextMap = -1;
		mapList = new LinkedList<MapPlaceData>();

		windowType = winType;
		terrariumSizeIndex = sizeIndex;

		recalcMapSize();

		for (int i = 0; i < MapWindow.MAP.values().length; i++) {
			mapList.add(new MapPlaceData(i));
		}
	}

	public World() {
		player = new Player();
		nextMap = -1;
		mapList = new LinkedList<MapPlaceData>();

		recalcMapSize();

		for (int i = 0; i < MapWindow.MAP.values().length; i++) {
			mapList.add(new MapPlaceData(i));
		}
	}

	/**
	 * プレイヤーを取得する.
	 * 
	 * @return プレイヤー
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * 現在のマップを取得する.
	 * 
	 * @return 現在のマップ
	 */
	@Transient
	public MapPlaceData getCurrentMap() {
		return mapList.get(currentMapIdx);
	}

	/**
	 * 次のマップを取得する.
	 * 
	 * @return 次のマップ
	 */
	public int getNextMap() {
		return nextMap;
	}

	/**
	 * 次のマップを設定する.
	 * 
	 * @param idx 次のマップ（予定）
	 */
	public void setNextMap(int idx) {
		nextMap = idx;
	}

	/**
	 * マップを切り替える.
	 * 
	 * @return マップ情報
	 */
	public MapPlaceData changeMap() {
		currentMapIdx = nextMap;
		nextMap = -1;
		return mapList.get(currentMapIdx);
	}

	/**
	 * マップサイズの計算
	 * ゲーム開始時のダイアログ、データロード時のみ使われる
	 */
	public void recalcMapSize() {
		int terrariumSizeParcent = SimYukkuri.fieldScaleData[terrariumSizeIndex];
		int MAX_X = SimYukkuri.DEFAULT_MAP_X[windowType] * terrariumSizeParcent / 100;
		int MAX_Y = SimYukkuri.DEFAULT_MAP_Y[windowType] * terrariumSizeParcent / 100;
		int MAX_Z = SimYukkuri.DEFAULT_MAP_Z[windowType] * terrariumSizeParcent / 100;
		Translate.setMapScale(terrariumSizeParcent);
		Translate.setMapSize(MAX_X, MAX_Y, MAX_Z);
	}

	/**
	 * 全マップのゆっくりリストをスキャンして遅延読み込みの復元
	 */
	public void loadInterBodyImage() {
		// 遅延読み込みの復元
		for (MapPlaceData m : mapList) {
			for (Map.Entry<Integer, Body> entry : m.getBody().entrySet()) {
				Body b = entry.getValue();
				if (b.getType() == HybridYukkuri.type) {
					HybridYukkuri hb = (HybridYukkuri) b;
					SimYukkuri.mypane
							.loadBodyImage(YukkuriUtil.getYukkuriType(hb.getBaseBody(0).getClass().getSimpleName()));
					SimYukkuri.mypane
							.loadBodyImage(YukkuriUtil.getYukkuriType(hb.getBaseBody(1).getClass().getSimpleName()));
					SimYukkuri.mypane
							.loadBodyImage(YukkuriUtil.getYukkuriType(hb.getBaseBody(2).getClass().getSimpleName()));
					SimYukkuri.mypane
							.loadBodyImage(YukkuriUtil.getYukkuriType(hb.getBaseBody(3).getClass().getSimpleName()));
				} else {
					SimYukkuri.mypane.loadBodyImage(YukkuriUtil.getYukkuriType(b.getClass().getSimpleName()));
				}
			}
		}
	}

	/**
	 * ゆっくり/うんうん/吐餡/おかざりのリストを取得する.
	 * 
	 * @return ゆっくり/うんうん/吐餡/おかざりのリスト
	 */
	@Transient
	public List<Obj> getYukkuriList() {
		List<Obj> yukkuriGroupList = new LinkedList<>();
		for (Map.Entry<Integer, Body> entry : mapList.get(currentMapIdx).getBody().entrySet()) {
			yukkuriGroupList.add(entry.getValue());
		}
		yukkuriGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).getShit().values()));
		yukkuriGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).getVomit().values()));
		yukkuriGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).getOkazari().values()));
		return yukkuriGroupList;
	}

	/**
	 * 床設置オブジェクトのリストを取得する.
	 * 
	 * @return 床設置オブジェクトのリスト
	 */
	@Transient
	public List<ObjEX> getPlatformList() {
		List<ObjEX> platformGroupList = new LinkedList<>();
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getToilet().values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getBed().values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getBreedingPool().values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getGarbagechute().values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getGarbageStation().values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getFoodmaker().values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getOrangePool().values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getProductchute().values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getStickyPlate().values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getHotPlate().values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getProcesserPlate().values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getMixer().values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getAutofeeder().values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getHouse().values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getBeltconveyorObj().values()));
		// platformGroupList.addAll(currentMap.generator);
		return platformGroupList;
	}

	/**
	 * プレス機/ゴミ収集所のリストを取得する.
	 * 
	 * @return プレス機/ゴミ収集所のリスト
	 */
	@Transient
	public List<ObjEX> getFixObjList() {
		List<ObjEX> fixObjGroupList = new LinkedList<>();
		fixObjGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getMachinePress().values()));
		fixObjGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getGarbageStation().values()));
		return fixObjGroupList;
	}

	/**
	 * フード/おもちゃ/茎/ディフューザー/ゆんば/すぃー/がらくた/トランポリン/石のリストを取得する.
	 * 
	 * @return フード/おもちゃ/茎/ディフューザー/ゆんば/すぃー/がらくた/トランポリン/石のリスト
	 */
	@Transient
	public List<ObjEX> getObjectList() {
		List<ObjEX> objectGroupList = new LinkedList<>();
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getFood().values()));
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getToy().values()));
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getStalk().values()));
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getDiffuser().values()));
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getYunba().values()));
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getSui().values()));
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getTrash().values()));
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getTrampoline().values()));
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getStone().values()));
		return objectGroupList;
	}

	/**
	 * ソートされたエフェクトのリストを取得する.
	 * 
	 * @return ソートされたエフェクトのリスト
	 */
	@Transient
	public List<Effect> getSortEffectList() {
		return new LinkedList<Effect>(mapList.get(currentMapIdx).getSortEffect().values());
	}

	/**
	 * フロントのエフェクトのリストを取得する.
	 * 
	 * @return フロントのエフェクトのリスト
	 */
	@Transient
	public List<Effect> getFrontEffectList() {
		return new LinkedList<Effect>(mapList.get(currentMapIdx).getFrontEffect().values());
	}

	/**
	 * ベルトコンベア/畑/池のリストを取得する.
	 * 
	 * @return ベルトコンベア/畑/池のリスト
	 */
	@Transient
	public List<FieldShapeBase> getFieldShapeList() {
		List<FieldShapeBase> fieldShapeGroupList = new LinkedList<>();
		fieldShapeGroupList.addAll(mapList.get(currentMapIdx).getBeltconveyor());
		fieldShapeGroupList.addAll(mapList.get(currentMapIdx).getFarm());
		fieldShapeGroupList.addAll(mapList.get(currentMapIdx).getPool());
		return fieldShapeGroupList;
	}

	/**
	 * 当たり判定のあるオブジェクトのリストを取得する.
	 * 
	 * @return トイレ/ベッド/養殖プール/ダストシュート/ゴミ収集所/フードメイカー/オレンジプール/製品投入口/粘着板
	 *         ホットプレート/フードプロセッサ/ミキサー/おうち/プレス機/すぃー/ベルトコンベア/自動給餌器/トランポリン/発電機 のリスト
	 */
	@Transient
	public List<ObjEX> getHitBaseList() {
		List<ObjEX> hitBaseGroupList = new LinkedList<>();
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getToilet().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getBed().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getBreedingPool().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getGarbagechute().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getGarbageStation().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getFoodmaker().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getOrangePool().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getProductchute().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getStickyPlate().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getHotPlate().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getProcesserPlate().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getMixer().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getHouse().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getMachinePress().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getSui().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getBeltconveyorObj().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getAutofeeder().values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).getTrampoline().values()));
		// hitBaseGroupList.addAll(currentMap.generator);
		return hitBaseGroupList;
	}

	/**
	 * ゆっくり/うんうん/吐餡/ふーど/茎/おもちゃ/石/おかざりのリストを取得する.
	 * 
	 * @return ゆっくり/うんうん/吐餡/ふーど/茎/おもちゃ/石/おかざりのリスト
	 */
	@Transient
	public List<Obj> getHitTargetList() {
		List<Obj> hitTargetGroupList = new LinkedList<>();
		for (Map.Entry<Integer, Body> entry : mapList.get(currentMapIdx).getBody().entrySet()) {
			hitTargetGroupList.add(entry.getValue());
		}
		hitTargetGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).getShit().values()));
		hitTargetGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).getVomit().values()));
		hitTargetGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).getFood().values()));
		hitTargetGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).getStalk().values()));
		hitTargetGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).getToy().values()));
		hitTargetGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).getStone().values()));
		hitTargetGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).getOkazari().values()));
		return hitTargetGroupList;
	}

	public int getMaxObjId() {
		return maxObjId;
	}

	public void setMaxObjId(int maxObjId) {
		this.maxObjId = maxObjId;
	}

	public int getMaxUniqueId() {
		return maxUniqueId;
	}

	public void setMaxUniqueId(int maxUniqueId) {
		this.maxUniqueId = maxUniqueId;
	}

}
