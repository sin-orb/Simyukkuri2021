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

	// プレイヤー情報 ワールド内に1つのみ存在できる
	public Player player;
	// 現在画面に表示しているマップのデータ
	public MapPlaceData currentMap;

	// ウィンドウ、マップサイズ情報
	public int windowType;
	public int terrariumSizeIndex;

	// マップ移動予定値
	// すぐ切り替えずメインのスレッド動作に衝突しないようにタイミングを図るため
	private int nextMap;
	// ワールド内のマップリスト
	private List<MapPlaceData> mapList;

	// 当たり判定やソート用オブジェクトリストのグループ分け
	public ArrayList<Obj> yukkuriGroupList;
	public ArrayList<ObjEX> platformGroupList;
	public ArrayList<ObjEX> fixObjGroupList;
	public ArrayList<ObjEX> objectGroupList;
	public ArrayList<Obj> sortEffectGroupList;
	public ArrayList<Obj> frontEffectGroupList;
	public ArrayList<FieldShapeBase> fieldShapeGroupList;
	public ArrayList<ObjEX> hitBaseGroupList;
	public ArrayList<Obj> hitTargetGroupList;

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
//		sortEffectGroupList = new ArrayList<Obj>();  今はリストが1つだけなのでベースをそのまま返す
//		frontEffectGroupList = new ArrayList<Obj>();
		fieldShapeGroupList = new ArrayList<FieldShapeBase>();
		hitBaseGroupList = new ArrayList<ObjEX>();
		hitTargetGroupList = new ArrayList<Obj>();
	}

	public Player getPlayer() {
		return player;
	}

	public MapPlaceData getCurrentMap() {
		return currentMap;
	}

	public int getNextMap() {
		return nextMap;
	}

	public void setNextMap(int idx) {
		nextMap = idx;
	}

	public MapPlaceData changeMap() {
		currentMap = mapList.get(nextMap);
		nextMap = -1;
		return currentMap;
	}

	// マップサイズの計算
	// ゲーム開始時のダイアログ、データロード時のみ使われる
	public void recalcMapSize() {
		int terrariumSizeParcent = SimYukkuri.fieldScaleData[terrariumSizeIndex];
		int MAX_X = SimYukkuri.DEFAULT_MAP_X[windowType] * terrariumSizeParcent / 100;
		int MAX_Y = SimYukkuri.DEFAULT_MAP_Y[windowType] * terrariumSizeParcent / 100;
		int MAX_Z = SimYukkuri.DEFAULT_MAP_Z[windowType] * terrariumSizeParcent / 100;
		Translate.mapScale = terrariumSizeParcent;
		Translate.setMapSize(MAX_X, MAX_Y, MAX_Z);
	}

	// 全マップのゆっくりリストをスキャンして遅延読み込みの復元
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

	// 描画や当たり判定といった目的別に結合した各種グループリスト
	public List<Obj> getYukkuriList(){
		yukkuriGroupList.clear();
		yukkuriGroupList.addAll(currentMap.body);
		yukkuriGroupList.addAll(currentMap.shit);
		yukkuriGroupList.addAll(currentMap.vomit);
		yukkuriGroupList.addAll(currentMap.okazari);
		return yukkuriGroupList;
	 }

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

	public List<ObjEX> getFixObjList(){
		fixObjGroupList.clear();
		fixObjGroupList.addAll(currentMap.machinePress);
		fixObjGroupList.addAll(currentMap.garbageStation);
		return fixObjGroupList;
	}

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

	public List<Effect> getSortEffectList(){
		return currentMap.sortEffect;
	}

	public List<Effect> getFrontEffectList(){
		return currentMap.frontEffect;
	}

	public List<FieldShapeBase> getFieldShapeList(){
		fieldShapeGroupList.clear();
		fieldShapeGroupList.addAll(currentMap.beltconveyor);
		fieldShapeGroupList.addAll(currentMap.farm);
		fieldShapeGroupList.addAll(currentMap.pool);
		return fieldShapeGroupList;
	}

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

