package src.draw;

import java.io.Serializable;
import java.util.LinkedList;
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
	/** 現在の画面のindex */
	public int currentMapIdx = 0;
	/**  ウィンドウ情報 */
	public int windowType;
	/** マップサイズ情報 */
	public int terrariumSizeIndex;

	/** マップ移動予定値。すぐ切り替えずメインのスレッド動作に衝突しないようにタイミングを図るため */
	private int nextMap;
	/** ワールド内のマップリスト */
	private List<MapPlaceData> mapList;

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
		List<Obj> yukkuriGroupList = new LinkedList<>();
		yukkuriGroupList.addAll(mapList.get(currentMapIdx).body);
		yukkuriGroupList.addAll(mapList.get(currentMapIdx).shit);
		yukkuriGroupList.addAll(mapList.get(currentMapIdx).vomit);
		yukkuriGroupList.addAll(mapList.get(currentMapIdx).okazari);
		return yukkuriGroupList;
	 }
	/**
	 * 床設置オブジェクトのリストを取得する.
	 * @return 床設置オブジェクトのリスト
	 */
	public List<ObjEX> getPlatformList(){
		List<ObjEX>  platformGroupList = new LinkedList<>();
		platformGroupList.addAll(mapList.get(currentMapIdx).toilet);
		platformGroupList.addAll(mapList.get(currentMapIdx).bed);
		platformGroupList.addAll(mapList.get(currentMapIdx).breedingPool);
		platformGroupList.addAll(mapList.get(currentMapIdx).garbagechute);
		platformGroupList.addAll(mapList.get(currentMapIdx).garbageStation);
		platformGroupList.addAll(mapList.get(currentMapIdx).foodmaker);
		platformGroupList.addAll(mapList.get(currentMapIdx).orangePool);
		platformGroupList.addAll(mapList.get(currentMapIdx).productchute);
		platformGroupList.addAll(mapList.get(currentMapIdx).stickyPlate);
		platformGroupList.addAll(mapList.get(currentMapIdx).hotPlate);
		platformGroupList.addAll(mapList.get(currentMapIdx).processerPlate);
		platformGroupList.addAll(mapList.get(currentMapIdx).mixer);
		platformGroupList.addAll(mapList.get(currentMapIdx).autofeeder);
		platformGroupList.addAll(mapList.get(currentMapIdx).house);
		platformGroupList.addAll(mapList.get(currentMapIdx).beltconveyorObj);
		//platformGroupList.addAll(currentMap.generator);
		return platformGroupList;
	}
	/**
	 * プレス機/ゴミ収集所のリストを取得する.
	 * @return プレス機/ゴミ収集所のリスト
	 */
	public List<ObjEX> getFixObjList(){
		List<ObjEX> fixObjGroupList = new LinkedList<>();
		fixObjGroupList.addAll(mapList.get(currentMapIdx).machinePress);
		fixObjGroupList.addAll(mapList.get(currentMapIdx).garbageStation);
		return fixObjGroupList;
	}
	/**
	 * フード/おもちゃ/茎/ディフューザー/ゆんば/すぃー/がらくた/トランポリン/石のリストを取得する.
	 * @return フード/おもちゃ/茎/ディフューザー/ゆんば/すぃー/がらくた/トランポリン/石のリスト 
	 */
	public List<ObjEX> getObjectList(){
		List<ObjEX> objectGroupList = new LinkedList<>();
		objectGroupList.addAll(mapList.get(currentMapIdx).food);
		objectGroupList.addAll(mapList.get(currentMapIdx).toy);
		objectGroupList.addAll(mapList.get(currentMapIdx).stalk);
		objectGroupList.addAll(mapList.get(currentMapIdx).diffuser);
		objectGroupList.addAll(mapList.get(currentMapIdx).yunba);
		objectGroupList.addAll(mapList.get(currentMapIdx).sui);
		objectGroupList.addAll(mapList.get(currentMapIdx).trash);
		objectGroupList.addAll(mapList.get(currentMapIdx).trampoline);
		objectGroupList.addAll(mapList.get(currentMapIdx).stone);
		return objectGroupList;
	}
	/**
	 * ソートされたエフェクトのリストを取得する.
	 * @return ソートされたエフェクトのリスト
	 */
	public List<Effect> getSortEffectList(){
		return mapList.get(currentMapIdx).sortEffect;
	}
	/**
	 * フロントのエフェクトのリストを取得する.
	 * @return フロントのエフェクトのリスト
	 */
	public List<Effect> getFrontEffectList(){
		return mapList.get(currentMapIdx).frontEffect;
	}
	/**
	 * ベルトコンベア/畑/池のリストを取得する.
	 * @return ベルトコンベア/畑/池のリスト
	 */
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
	public List<ObjEX> getHitBaseList(){
		List<ObjEX> hitBaseGroupList = new LinkedList<>();
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).toilet);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).bed);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).breedingPool);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).garbagechute);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).garbageStation);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).foodmaker);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).orangePool);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).productchute);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).stickyPlate);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).hotPlate);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).processerPlate);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).mixer);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).house);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).machinePress);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).sui);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).beltconveyorObj);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).autofeeder);
		hitBaseGroupList.addAll(mapList.get(currentMapIdx).trampoline);
		//hitBaseGroupList.addAll(currentMap.generator);
		return hitBaseGroupList;
	}
	/**
	 * ゆっくり/うんうん/吐餡/ふーど/茎/おもちゃ/石/おかざりのリストを取得する.
	 * @return ゆっくり/うんうん/吐餡/ふーど/茎/おもちゃ/石/おかざりのリスト 
	 */
	public List<Obj> getHitTargetList() {
		List<Obj> hitTargetGroupList = new LinkedList<>();
		hitTargetGroupList.addAll(mapList.get(currentMapIdx).body);
		hitTargetGroupList.addAll(mapList.get(currentMapIdx).shit);
		hitTargetGroupList.addAll(mapList.get(currentMapIdx).vomit);
		hitTargetGroupList.addAll(mapList.get(currentMapIdx).food);
		hitTargetGroupList.addAll(mapList.get(currentMapIdx).stalk);
		hitTargetGroupList.addAll(mapList.get(currentMapIdx).toy);
		hitTargetGroupList.addAll(mapList.get(currentMapIdx).stone);
		hitTargetGroupList.addAll(mapList.get(currentMapIdx).okazari);
		return hitTargetGroupList;
	}
}

