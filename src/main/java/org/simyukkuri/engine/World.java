package org.simyukkuri.engine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.beans.Transient;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.effect.Effect;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.HybridYukkuri;
import org.simyukkuri.entity.core.meta.Player;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.field.FieldShape;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.ui.WorldSelectionWindow;
import org.simyukkuri.util.GameView;

/**
 * 全ワールドデータ
 * プレイヤーと複数のマップ情報を保持
 * セーブデータになる
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
	/** うにょ有効フラグ */
	private boolean unyo = true;

	private int maxObjId = 0;
	private int maxUniqueId = 0;

	/** マップ移動予定値。すぐ切り替えずメインのスレッド動作に衝突しないようにタイミングを図るため */
	private int nextMap;
	/** ワールド内のマップリスト */
	private List<WorldState> mapList;

	/**
	 * 現在表示中のマップインデックスを返す。
	 *
	 * @return 現在のマップインデックス
	 */
	public int getCurrentWorldStateIndex() {
		return currentMapIdx;
	}

	/**
	 * 現在表示中のマップインデックスをセットする。
	 *
	 * @param currentMapIdx 新しいマップインデックス
	 */
	public void setCurrentWorldStateIndex(int currentMapIdx) {
		this.currentMapIdx = currentMapIdx;
	}

	/**
	 * ウィンドウタイプを返す。
	 *
	 * @return ウィンドウタイプ
	 */
	public int getWindowType() {
		return windowType;
	}

	/**
	 * ウィンドウタイプをセットする。
	 *
	 * @param windowType 新しいウィンドウタイプ
	 */
	public void setWindowType(int windowType) {
		this.windowType = windowType;
	}

	/**
	 * テラリウムサイズインデックスを返す。
	 *
	 * @return テラリウムサイズインデックス
	 */
	public int getTerrariumSizeIndex() {
		return terrariumSizeIndex;
	}

	/**
	 * テラリウムサイズインデックスをセットする。
	 *
	 * @param terrariumSizeIndex 新しいサイズインデックス
	 */
	public void setTerrariumSizeIndex(int terrariumSizeIndex) {
		this.terrariumSizeIndex = terrariumSizeIndex;
	}

	/** うにょ有効フラグを返す。 */
	public boolean isUnyo() {
		return unyo;
	}

	/** うにょ有効フラグをセットする。 */
	public void setUnyo(boolean unyo) {
		this.unyo = unyo;
	}

	/**
	 * 全ワールドステートリストを返す。
	 *
	 * @return ワールドステートリスト
	 */
	public List<WorldState> getWorldStates() {
		return mapList;
	}

	/**
	 * ワールドステートリストをセットする。
	 *
	 * @param mapList 新しいワールドステートリスト
	 */
	public void setWorldStates(List<WorldState> mapList) {
		this.mapList = mapList;
	}

	/**
	 * シリアルバージョン UID を返す。
	 *
	 * @return シリアルバージョン UID
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * プレイヤーをセットする。
	 *
	 * @param player 新しいプレイヤー
	 */
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
		mapList = new LinkedList<WorldState>();

		windowType = winType;
		terrariumSizeIndex = sizeIndex;

		recalcWorldSize();

		for (int i = 0; i < WorldSelectionWindow.WorldSelection.values().length; i++) {
			mapList.add(new WorldState(i));
		}
	}

	/**
	 * Jackson デシリアライズ用デフォルトコンストラクタ。
	 */
	public World() {
		player = new Player();
		nextMap = -1;
		mapList = new LinkedList<WorldState>();

		recalcWorldSize();

		for (int i = 0; i < WorldSelectionWindow.WorldSelection.values().length; i++) {
			mapList.add(new WorldState(i));
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
	public WorldState getCurrentWorldState() {
		return mapList.get(currentMapIdx);
	}

	/**
	 * 次のマップを取得する.
	 * 
	 * @return 次のマップ
	 */
	public int getNextWorldStateIndex() {
		return nextMap;
	}

	/**
	 * 次のマップを設定する.
	 * 
	 * @param idx 次のマップ（予定）
	 */
	public void setNextWorldStateIndex(int idx) {
		nextMap = idx;
	}

	/**
	 * マップを切り替える.
	 * 
	 * @return マップ情報
	 */
	public WorldState changeWorldState() {
		currentMapIdx = nextMap;
		nextMap = -1;
		return mapList.get(currentMapIdx);
	}

	/**
	 * マップサイズの計算
	 * ゲーム開始時のダイアログ、データロード時のみ使われる
	 */
	public void recalcWorldSize() {
		int terrariumSizeParcent = SimYukkuri.fieldScaleData[terrariumSizeIndex];
		int maxX = SimYukkuri.DEFAULT_MAP_X[windowType] * terrariumSizeParcent / 100;
		int maxY = SimYukkuri.DEFAULT_MAP_Y[windowType] * terrariumSizeParcent / 100;
		int maxZ = SimYukkuri.DEFAULT_MAP_Z[windowType] * terrariumSizeParcent / 100;
		Translate.setWorldScale(terrariumSizeParcent);
		Translate.setWorldSize(maxX, maxY, maxZ);
	}

	/**
	 * 全マップのゆっくりリストをスキャンして遅延読み込みの復元
	 */
	public void loadInterYukkuriImage() {
		if (GameView.getPane() == null) {
			return;
		}
		// 遅延読み込みの復元
		for (WorldState m : mapList) {
			for (Map.Entry<Integer, Yukkuri> entry : m.getYukkuriRegistry().entrySet()) {
				reinitYukkuri(entry.getValue());
			}
		}
		// プレイヤーインベントリ内の Yukkuri も再初期化
		Player player = getPlayer();
		if (player != null && player.getItemForSave() != null) {
			for (Entity item : player.getItemForSave()) {
				if (item instanceof Yukkuri) {
					reinitYukkuri((Yukkuri) item);
				}
			}
		}
	}

	private void reinitYukkuri(Yukkuri b) {
		if (b.getType() == HybridYukkuri.type) {
			HybridYukkuri hb = (HybridYukkuri) b;
			GameView.loadYukkuriImage(org.simyukkuri.enums.YukkuriType
					.fromClassName(hb.getBaseYukkuri(0).getClass().getSimpleName()));
			GameView.loadYukkuriImage(org.simyukkuri.enums.YukkuriType
					.fromClassName(hb.getBaseYukkuri(1).getClass().getSimpleName()));
			GameView.loadYukkuriImage(org.simyukkuri.enums.YukkuriType
					.fromClassName(hb.getBaseYukkuri(2).getClass().getSimpleName()));
			GameView.loadYukkuriImage(org.simyukkuri.enums.YukkuriType
					.fromClassName(hb.getBaseYukkuri(3).getClass().getSimpleName()));
			try {
				hb.reinitializeAfterLoad();
			} catch (java.io.IOException e) {
				e.printStackTrace();
			}
		} else {
			GameView.loadYukkuriImage(org.simyukkuri.enums.YukkuriType.fromClassName(b.getClass().getSimpleName()));
			b.reinitializeBoundary();
		}
	}

	/**
	 * ゆっくり/うんうん/吐餡/おかざりのリストを取得する.
	 * 
	 * @return ゆっくり/うんうん/吐餡/おかざりのリスト
	 */
	@Transient
	public List<Entity> getWorldEntities() {
		List<Entity> yukkuriGroupList = new LinkedList<>();
		for (Map.Entry<Integer, Yukkuri> entry : mapList.get(currentMapIdx).getYukkuriRegistry().entrySet()) {
			yukkuriGroupList.add(entry.getValue());
		}
		yukkuriGroupList.addAll(new LinkedList<Entity>(mapList.get(currentMapIdx).getShit().values()));
		yukkuriGroupList.addAll(new LinkedList<Entity>(mapList.get(currentMapIdx).getVomit().values()));
		yukkuriGroupList.addAll(new LinkedList<Entity>(mapList.get(currentMapIdx).getOkazaris().values()));
		return yukkuriGroupList;
	}

	/**
	 * 床設置オブジェクトのリストを取得する.
	 * 
	 * @return 床設置オブジェクトのリスト
	 */
	@Transient
	public List<WorldEntity> getPlatforms() {
		List<WorldEntity> platformGroupList = new LinkedList<>();
		platformGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getToilets().values()));
		platformGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getBeds().values()));
		platformGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getBreedingPools().values()));
		platformGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getGarbageChutes().values()));
		platformGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getGarbageStations().values()));
		platformGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getFoodMakers().values()));
		platformGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getOrangePools().values()));
		platformGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getProductChutes().values()));
		platformGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getStickyPlates().values()));
		platformGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getHotPlates().values()));
		platformGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getProcessorPlates().values()));
		platformGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getMixers().values()));
		platformGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getAutoFeeders().values()));
		platformGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getHouses().values()));
		platformGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getBeltconveyorObjects().values()));
		// platformGroupList.addAll(currentMap.generator);
		return platformGroupList;
	}

	/**
	 * プレス機/ゴミ収集所のリストを取得する.
	 * 
	 * @return プレス機/ゴミ収集所のリスト
	 */
	@Transient
	public List<WorldEntity> getFixedObjects() {
		List<WorldEntity> fixObjGroupList = new LinkedList<>();
		fixObjGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getMachinePresses().values()));
		fixObjGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getGarbageStations().values()));
		return fixObjGroupList;
	}

	/**
	 * フード/おもちゃ/茎/ディフューザー/ゆんば/すぃー/がらくた/トランポリン/石のリストを取得する.
	 * 
	 * @return フード/おもちゃ/茎/ディフューザー/ゆんば/すぃー/がらくた/トランポリン/石のリスト
	 */
	@Transient
	public List<WorldEntity> getObjects() {
		List<WorldEntity> objectGroupList = new LinkedList<>();
		objectGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getFoods().values()));
		objectGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getToys().values()));
		objectGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getStalks().values()));
		objectGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getDiffusers().values()));
		objectGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getYunbas().values()));
		objectGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getSuis().values()));
		objectGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getTrashObjects().values()));
		objectGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getTrampolines().values()));
		objectGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getStones().values()));
		return objectGroupList;
	}

	/**
	 * ソートされたエフェクトのリストを取得する.
	 * 
	 * @return ソートされたエフェクトのリスト
	 */
	@Transient
	public List<Effect> getSortedEffects() {
		return new LinkedList<Effect>(mapList.get(currentMapIdx).getSortedEffects().values());
	}

	/**
	 * フロントのエフェクトのリストを取得する.
	 * 
	 * @return フロントのエフェクトのリスト
	 */
	@Transient
	public List<Effect> getFrontEffects() {
		return new LinkedList<Effect>(mapList.get(currentMapIdx).getFrontEffects().values());
	}

	/**
	 * ベルトコンベア/畑/池のリストを取得する.
	 * 
	 * @return ベルトコンベア/畑/池のリスト
	 */
	@Transient
	public List<FieldShape> getFieldShapes() {
		List<FieldShape> fieldShapeGroupList = new LinkedList<>();
		fieldShapeGroupList.addAll(mapList.get(currentMapIdx).getBeltconveyors());
		fieldShapeGroupList.addAll(mapList.get(currentMapIdx).getFarms());
		fieldShapeGroupList.addAll(mapList.get(currentMapIdx).getPools());
		return fieldShapeGroupList;
	}

	/**
	 * 当たり判定のあるオブジェクトのリストを取得する.
	 * 
	 * @return トイレ/ベッド/養殖プール/ダストシュート/ゴミ収集所/フードメイカー/オレンジプール/製品投入口/粘着板
	 *         ホットプレート/フードプロセッサ/ミキサー/おうち/プレス機/すぃー/ベルトコンベア/自動給餌器/トランポリン/発電機 のリスト
	 */
	@Transient
	public List<WorldEntity> getCollisionBases() {
		List<WorldEntity> hitBaseGroupList = new LinkedList<>();
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getToilets().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getBeds().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getBreedingPools().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getGarbageChutes().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getGarbageStations().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getFoodMakers().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getOrangePools().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getProductChutes().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getStickyPlates().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getHotPlates().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getProcessorPlates().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getMixers().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getHouses().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getMachinePresses().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getSuis().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getBeltconveyorObjects().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getAutoFeeders().values()));
		hitBaseGroupList.addAll(new LinkedList<WorldEntity>(mapList.get(currentMapIdx).getTrampolines().values()));
		// hitBaseGroupList.addAll(currentMap.generator);
		return hitBaseGroupList;
	}

	/**
	 * ゆっくり/うんうん/吐餡/ふーど/茎/おもちゃ/石/おかざりのリストを取得する.
	 * 
	 * @return ゆっくり/うんうん/吐餡/ふーど/茎/おもちゃ/石/おかざりのリスト
	 */
	@Transient
	public List<Entity> getCollisionTargets() {
		List<Entity> hitTargetGroupList = new LinkedList<>();
		for (Map.Entry<Integer, Yukkuri> entry : mapList.get(currentMapIdx).getYukkuriRegistry().entrySet()) {
			hitTargetGroupList.add(entry.getValue());
		}
		hitTargetGroupList.addAll(new LinkedList<Entity>(mapList.get(currentMapIdx).getShit().values()));
		hitTargetGroupList.addAll(new LinkedList<Entity>(mapList.get(currentMapIdx).getVomit().values()));
		hitTargetGroupList.addAll(new LinkedList<Entity>(mapList.get(currentMapIdx).getFoods().values()));
		hitTargetGroupList.addAll(new LinkedList<Entity>(mapList.get(currentMapIdx).getStalks().values()));
		hitTargetGroupList.addAll(new LinkedList<Entity>(mapList.get(currentMapIdx).getToys().values()));
		hitTargetGroupList.addAll(new LinkedList<Entity>(mapList.get(currentMapIdx).getStones().values()));
		hitTargetGroupList.addAll(new LinkedList<Entity>(mapList.get(currentMapIdx).getOkazaris().values()));
		return hitTargetGroupList;
	}

	/**
	 * 割り当て済みオブジェクト ID の最大値を返す。
	 *
	 * @return オブジェクト ID 最大値
	 */
	public int getMaxObjId() {
		return maxObjId;
	}

	/**
	 * オブジェクト ID 最大値をセットする。
	 *
	 * @param maxObjId 新しいオブジェクト ID 最大値
	 */
	public void setMaxObjId(int maxObjId) {
		this.maxObjId = maxObjId;
	}

	/**
	 * 割り当て済みユニーク ID の最大値を返す。
	 *
	 * @return ユニーク ID 最大値
	 */
	public int getMaxUniqueId() {
		return maxUniqueId;
	}

	/**
	 * ユニーク ID 最大値をセットする。
	 *
	 * @param maxUniqueId 新しいユニーク ID 最大値
	 */
	public void setMaxUniqueId(int maxUniqueId) {
		this.maxUniqueId = maxUniqueId;
	}

}
