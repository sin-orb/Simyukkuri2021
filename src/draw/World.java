package src.draw;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
public class World implements Serializable {
	static final long serialVersionUID = 1L;

	/** プレイヤー情報 ワールド内に1つのみ存在できる */
	public Player player;
	/** 現在画面に表示しているマップのデータ */
	public MapPlaceData currentMap;

	/**  ウィンドウ情報 */
	public int windowType;
	/** マップサイズ情報 */
	public int terrariumSizeIndex;

	/** マップ移動予定値。すぐ切り替えずメインのスレッド動作に衝突しないようにタイミングを図るため */
	private int nextMap;
	/** ワールド内のマップリスト */
	private List<MapPlaceData> mapList;

	// 当たり判定やソート用オブジェクトリストのグループ分け
	/** ゆっくり/うんうん/吐餡/おかざりのリスト */
	public ArrayList<Obj> yukkuriGroupList;
	/** 床設置オブジェクトのリスト */
	public ArrayList<ObjEX> platformGroupList;
	/** プレス機/ゴミ収集所のリスト */
	public ArrayList<ObjEX> fixObjGroupList;
	/** フード/おもちゃ/茎/ディフューザー/ゆんば/すぃー/がらくた/トランポリン/石のリスト */
	public ArrayList<ObjEX> objectGroupList;
	//public ArrayList<Obj> sortEffectGroupList;
	//public ArrayList<Obj> frontEffectGroupList;
	/** ベルトコンベア/畑/池のリスト */
	public ArrayList<FieldShapeBase> fieldShapeGroupList;
	/**
	 *  トイレ/ベッド/養殖プール/ダストシュート/ゴミ収集所/フードメイカー/オレンジプール/製品投入口/粘着板
	 *  ホットプレート/フードプロセッサ/ミキサー/おうち/プレス機/すぃー/ベルトコンベア/自動給餌器/トランポリン/発電機
	 *  のリスト
	 */
	public ArrayList<ObjEX> hitBaseGroupList;
	/** ゆっくり/うんうん/吐餡/ふーど/茎/おもちゃ/石/おかざりのリスト */
	public ArrayList<Obj> hitTargetGroupList;

	/**
	 * コンストラクタ.
	 * @param winType Windowタイプ
	 * @param sizeIndex サイズ
	 */
	public World(int winType, int sizeIndex) {

		player = new Player();
		nextMap = -1;
		mapList = new ArrayList<MapPlaceData>();

		windowType = winType;
		terrariumSizeIndex = sizeIndex;

		recalcMapSize();

		for(int i = 0; i < MapWindow.MAP.values().length; i++) {
			mapList.add(new MapPlaceData(i));
		}
		currentMap = mapList.get(0);

		yukkuriGroupList = new ArrayList<Obj>();
		platformGroupList = new ArrayList<ObjEX>();
		fixObjGroupList = new ArrayList<ObjEX>();
		objectGroupList = new ArrayList<ObjEX>();
		fieldShapeGroupList = new ArrayList<FieldShapeBase>();
		hitBaseGroupList = new ArrayList<ObjEX>();
		hitTargetGroupList = new ArrayList<Obj>();
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
	public MapPlaceData getCurrentMap() {
		return currentMap;
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
		currentMap = mapList.get(nextMap);
		nextMap = -1;
		return currentMap;
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
			for(Body b :m.body) {
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
	public List<Obj> getYukkuriList(){
		yukkuriGroupList.clear();
		yukkuriGroupList.addAll(currentMap.body);
		yukkuriGroupList.addAll(currentMap.shit);
		yukkuriGroupList.addAll(currentMap.vomit);
		yukkuriGroupList.addAll(currentMap.okazari);
		return yukkuriGroupList;
	 }
	/**
	 * 床設置オブジェクトのリストを取得する.
	 * @return 床設置オブジェクトのリスト
	 */
	public List<ObjEX> getPlatformList(){
		platformGroupList.clear();
		platformGroupList.addAll(currentMap.toilet);
		platformGroupList.addAll(currentMap.bed);
		platformGroupList.addAll(currentMap.breedingPool);
		platformGroupList.addAll(currentMap.garbagechute);
		platformGroupList.addAll(currentMap.garbageStation);
		platformGroupList.addAll(currentMap.foodmaker);
		platformGroupList.addAll(currentMap.orangePool);
		platformGroupList.addAll(currentMap.productchute);
		platformGroupList.addAll(currentMap.stickyPlate);
		platformGroupList.addAll(currentMap.hotPlate);
		platformGroupList.addAll(currentMap.processerPlate);
		platformGroupList.addAll(currentMap.mixer);
		platformGroupList.addAll(currentMap.autofeeder);
		platformGroupList.addAll(currentMap.house);
		platformGroupList.addAll(currentMap.beltconveyorObj);
		platformGroupList.addAll(currentMap.generator);
		return platformGroupList;
	}
	/**
	 * プレス機/ゴミ収集所のリストを取得する.
	 * @return プレス機/ゴミ収集所のリスト
	 */
	public List<ObjEX> getFixObjList(){
		fixObjGroupList.clear();
		fixObjGroupList.addAll(currentMap.machinePress);
		fixObjGroupList.addAll(currentMap.garbageStation);
		return fixObjGroupList;
	}
	/**
	 * フード/おもちゃ/茎/ディフューザー/ゆんば/すぃー/がらくた/トランポリン/石のリストを取得する.
	 * @return フード/おもちゃ/茎/ディフューザー/ゆんば/すぃー/がらくた/トランポリン/石のリスト 
	 */
	public List<ObjEX> getObjectList(){
		objectGroupList.clear();
		objectGroupList.addAll(currentMap.food);
		objectGroupList.addAll(currentMap.toy);
		objectGroupList.addAll(currentMap.stalk);
		objectGroupList.addAll(currentMap.diffuser);
		objectGroupList.addAll(currentMap.yunba);
		objectGroupList.addAll(currentMap.sui);
		objectGroupList.addAll(currentMap.trash);
		objectGroupList.addAll(currentMap.trampoline);
		objectGroupList.addAll(currentMap.stone);
		return objectGroupList;
	}
	/**
	 * ソートされたエフェクトのリストを取得する.
	 * @return ソートされたエフェクトのリスト
	 */
	public List<Effect> getSortEffectList(){
		return currentMap.sortEffect;
	}
	/**
	 * フロントのエフェクトのリストを取得する.
	 * @return フロントのエフェクトのリスト
	 */
	public List<Effect> getFrontEffectList(){
		return currentMap.frontEffect;
	}
	/**
	 * ベルトコンベア/畑/池のリストを取得する.
	 * @return ベルトコンベア/畑/池のリスト
	 */
	public List<FieldShapeBase> getFieldShapeList(){
		fieldShapeGroupList.clear();
		fieldShapeGroupList.addAll(currentMap.beltconveyor);
		fieldShapeGroupList.addAll(currentMap.farm);
		fieldShapeGroupList.addAll(currentMap.pool);
		return fieldShapeGroupList;
	}
	/**
	 * 当たり判定のあるオブジェクトのリストを取得する.
	 * @return トイレ/ベッド/養殖プール/ダストシュート/ゴミ収集所/フードメイカー/オレンジプール/製品投入口/粘着板 ホットプレート/フードプロセッサ/ミキサー/おうち/プレス機/すぃー/ベルトコンベア/自動給餌器/トランポリン/発電機 のリスト
	 */
	public List<ObjEX> getHitBaseList(){
		hitBaseGroupList.clear();
		hitBaseGroupList.addAll(currentMap.toilet);
		hitBaseGroupList.addAll(currentMap.bed);
		hitBaseGroupList.addAll(currentMap.breedingPool);
		hitBaseGroupList.addAll(currentMap.garbagechute);
		hitBaseGroupList.addAll(currentMap.garbageStation);
		hitBaseGroupList.addAll(currentMap.foodmaker);
		hitBaseGroupList.addAll(currentMap.orangePool);
		hitBaseGroupList.addAll(currentMap.productchute);
		hitBaseGroupList.addAll(currentMap.stickyPlate);
		hitBaseGroupList.addAll(currentMap.hotPlate);
		hitBaseGroupList.addAll(currentMap.processerPlate);
		hitBaseGroupList.addAll(currentMap.mixer);
		hitBaseGroupList.addAll(currentMap.house);
		hitBaseGroupList.addAll(currentMap.machinePress);
		hitBaseGroupList.addAll(currentMap.sui);
		hitBaseGroupList.addAll(currentMap.beltconveyorObj);
		hitBaseGroupList.addAll(currentMap.autofeeder);
		hitBaseGroupList.addAll(currentMap.trampoline);
		hitBaseGroupList.addAll(currentMap.generator);
		return hitBaseGroupList;
	}
	/**
	 * ゆっくり/うんうん/吐餡/ふーど/茎/おもちゃ/石/おかざりのリストを取得する.
	 * @return ゆっくり/うんうん/吐餡/ふーど/茎/おもちゃ/石/おかざりのリスト 
	 */
	public List<Obj> getHitTargetList() {
		hitTargetGroupList.clear();
		hitTargetGroupList.addAll(currentMap.body);
		hitTargetGroupList.addAll(currentMap.shit);
		hitTargetGroupList.addAll(currentMap.vomit);
		hitTargetGroupList.addAll(currentMap.food);
		hitTargetGroupList.addAll(currentMap.stalk);
		hitTargetGroupList.addAll(currentMap.toy);
		hitTargetGroupList.addAll(currentMap.stone);
		hitTargetGroupList.addAll(currentMap.okazari);
		return hitTargetGroupList;
	}
}

