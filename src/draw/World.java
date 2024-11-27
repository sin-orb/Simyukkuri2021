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

	全ワールドデータ
	プレイヤーと複数のマップ情報を保持
	セーブデータになる

*/
@JsonIgnoreProperties(ignoreUnknown=true)
public class World implements Serializable {

	private static final long serialVersionUID = 1586355554271822104L;
	/** プレイヤー情報 ワールド内に1つのみ存在できる */
	public Player player;
	/** 現在の画面のindex */
	public int currentMapIdx = 0;
	/**  ウィンドウ情報 */
	public int windowType;
	/** マップサイズ情報 */
	public int terrariumSizeIndex;

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
	 * @param winType Windowタイプ
	 * @param sizeIndex サイズ
	 */
	public World(int winType, int sizeIndex) {

		player = new Player();
		nextMap = -1;
		mapList = new LinkedList<MapPlaceData>();

		windowType = winType;
		terrariumSizeIndex = sizeIndex;

		recalcMapSize();

		for(int i = 0; i < MapWindow.MAP.values().length; i++) {
			mapList.add(new MapPlaceData(i));
		}
	}
	
	public World() {
		player = new Player();
		nextMap = -1;
		mapList = new LinkedList<MapPlaceData>();

		recalcMapSize();

		for(int i = 0; i < MapWindow.MAP.values().length; i++) {
			mapList.add(new MapPlaceData(i));
		}
	}
	
	/**
	 * プレイヤーを取得する.
	 * @return プレイヤー
	 */
	public Player getPlayer() {
		return player;
	}
	/**
	 * 現在のマップを取得する.
	 * @return 現在のマップ
	 */
	@Transient
	public MapPlaceData getCurrentMap() {
		return mapList.get(currentMapIdx);
	}
	/**
	 * 次のマップを取得する.
	 * @return 次のマップ
	 */
	public int getNextMap() {
		return nextMap;
	}
	/**
	 * 次のマップを設定する.
	 * @param idx 次のマップ（予定）
	 */
	public void setNextMap(int idx) {
		nextMap = idx;
	}
	/**
	 * マップを切り替える.
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
		Translate.mapScale = terrariumSizeParcent;
		Translate.setMapSize(MAX_X, MAX_Y, MAX_Z);
	}

	/**
	 * 全マップのゆっくりリストをスキャンして遅延読み込みの復元
	 */
	public void loadInterBodyImage() {
		// 遅延読み込みの復元
		for(MapPlaceData m :mapList) {
			for(Map.Entry<Integer, Body> entry : m.body.entrySet()) {
				Body b = entry.getValue();
				if(b.getType() == HybridYukkuri.type) {
					HybridYukkuri hb = (HybridYukkuri)b;
					SimYukkuri.mypane.loadBodyImage(YukkuriUtil.getYukkuriType(hb.getBaseBody(0).getClass().getSimpleName()));
					SimYukkuri.mypane.loadBodyImage(YukkuriUtil.getYukkuriType(hb.getBaseBody(1).getClass().getSimpleName()));
					SimYukkuri.mypane.loadBodyImage(YukkuriUtil.getYukkuriType(hb.getBaseBody(2).getClass().getSimpleName()));
					SimYukkuri.mypane.loadBodyImage(YukkuriUtil.getYukkuriType(hb.getBaseBody(3).getClass().getSimpleName()));
				} else {
					SimYukkuri.mypane.loadBodyImage(YukkuriUtil.getYukkuriType(b.getClass().getSimpleName()));
				}
			}
		}
	}

	/**
	 * ゆっくり/うんうん/吐餡/おかざりのリストを取得する.
	 * @return ゆっくり/うんうん/吐餡/おかざりのリスト 
	 */
	@Transient
	public List<Obj> getYukkuriList(){
		List<Obj> yukkuriGroupList = new LinkedList<>();
		for (Map.Entry<Integer, Body> entry : mapList.get(currentMapIdx).body.entrySet()) {
			yukkuriGroupList.add(entry.getValue());
		}
		yukkuriGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).shit.values()));
		yukkuriGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).vomit.values()));
		yukkuriGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).okazari.values()));
		return yukkuriGroupList;
	 }
	/**
	 * 床設置オブジェクトのリストを取得する.
	 * @return 床設置オブジェクトのリスト
	 */
	@Transient
	public List<ObjEX> getPlatformList(){
		List<ObjEX>  platformGroupList = new LinkedList<>();
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).toilet.values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).bed.values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).breedingPool.values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).garbagechute.values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).garbageStation.values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).foodmaker.values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).orangePool.values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).productchute.values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).stickyPlate.values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).hotPlate.values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).processerPlate.values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).mixer.values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).autofeeder.values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).house.values()));
		platformGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).beltconveyorObj.values()));
		//platformGroupList.addAll(currentMap.generator);
		return platformGroupList;
	}
	/**
	 * プレス機/ゴミ収集所のリストを取得する.
	 * @return プレス機/ゴミ収集所のリスト
	 */
	@Transient
	public List<ObjEX> getFixObjList(){
		List<ObjEX> fixObjGroupList = new LinkedList<>();
		fixObjGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).machinePress.values()));
		fixObjGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).garbageStation.values()));
		return fixObjGroupList;
	}
	/**
	 * フード/おもちゃ/茎/ディフューザー/ゆんば/すぃー/がらくた/トランポリン/石のリストを取得する.
	 * @return フード/おもちゃ/茎/ディフューザー/ゆんば/すぃー/がらくた/トランポリン/石のリスト 
	 */
	@Transient
	public List<ObjEX> getObjectList(){
		List<ObjEX> objectGroupList = new LinkedList<>();
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).food.values()));
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).toy.values()));
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).stalk.values()));
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).diffuser.values()));
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).yunba.values()));
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).sui.values()));
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).trash.values()));
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).trampoline.values()));
		objectGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).stone.values()));
		return objectGroupList;
	}
	/**
	 * ソートされたエフェクトのリストを取得する.
	 * @return ソートされたエフェクトのリスト
	 */
	@Transient
	public List<Effect> getSortEffectList(){
		return new LinkedList<Effect>(mapList.get(currentMapIdx).sortEffect.values());
	}
	/**
	 * フロントのエフェクトのリストを取得する.
	 * @return フロントのエフェクトのリスト
	 */
	@Transient
	public List<Effect> getFrontEffectList(){
		return new LinkedList<Effect>(mapList.get(currentMapIdx).frontEffect.values());
	}
	/**
	 * ベルトコンベア/畑/池のリストを取得する.
	 * @return ベルトコンベア/畑/池のリスト
	 */
	@Transient
	public List<FieldShapeBase> getFieldShapeList(){
		List<FieldShapeBase> fieldShapeGroupList = new LinkedList<>();
		fieldShapeGroupList.addAll(mapList.get(currentMapIdx).beltconveyor);
		fieldShapeGroupList.addAll(mapList.get(currentMapIdx).farm);
		fieldShapeGroupList.addAll(mapList.get(currentMapIdx).pool);
		return fieldShapeGroupList;
	}
	/**
	 * 当たり判定のあるオブジェクトのリストを取得する.
	 * @return トイレ/ベッド/養殖プール/ダストシュート/ゴミ収集所/フードメイカー/オレンジプール/製品投入口/粘着板 ホットプレート/フードプロセッサ/ミキサー/おうち/プレス機/すぃー/ベルトコンベア/自動給餌器/トランポリン/発電機 のリスト
	 */
	@Transient
	public List<ObjEX> getHitBaseList(){
		List<ObjEX> hitBaseGroupList = new LinkedList<>();
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).toilet.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).bed.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).breedingPool.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).garbagechute.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).garbageStation.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).foodmaker.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).orangePool.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).productchute.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).stickyPlate.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).hotPlate.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).processerPlate.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).mixer.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).house.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).machinePress.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).sui.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).beltconveyorObj.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).autofeeder.values()));
		hitBaseGroupList.addAll(new LinkedList<ObjEX>(mapList.get(currentMapIdx).trampoline.values()));
		//hitBaseGroupList.addAll(currentMap.generator);
		return hitBaseGroupList;
	}
	/**
	 * ゆっくり/うんうん/吐餡/ふーど/茎/おもちゃ/石/おかざりのリストを取得する.
	 * @return ゆっくり/うんうん/吐餡/ふーど/茎/おもちゃ/石/おかざりのリスト 
	 */
	@Transient
	public List<Obj> getHitTargetList() {
		List<Obj> hitTargetGroupList = new LinkedList<>();
		for (Map.Entry<Integer, Body> entry : mapList.get(currentMapIdx).body.entrySet()) {
			hitTargetGroupList.add(entry.getValue());
		}
		hitTargetGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).shit.values()));
		hitTargetGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).vomit.values()));
		hitTargetGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).food.values()));
		hitTargetGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).stalk.values()));
		hitTargetGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).toy.values()));
		hitTargetGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).stone.values()));
		hitTargetGroupList.addAll(new LinkedList<Obj>(mapList.get(currentMapIdx).okazari.values()));
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

