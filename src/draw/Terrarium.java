package src.draw;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import src.SimYukkuri;
import src.entity.core.Entity;
import src.entity.core.attachment.impl.Fire;
import src.entity.core.effect.Effect;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.world.bodylinked.Stalk;
import src.entity.core.world.item.AutoFeeder;
import src.entity.core.world.item.Diffuser;
import src.entity.core.world.item.Food;
import src.entity.core.world.item.GarbageChute;
import src.entity.core.world.item.GarbageStation;
import src.entity.core.world.item.HotPlate;
import src.entity.core.world.item.Mixer;
import src.entity.core.world.item.StickyPlate;
import src.entity.core.world.item.Sui;
import src.entity.core.world.item.Yunba;
import src.entity.core.world.mobile.Shit;
import src.entity.core.world.mobile.Vomit;
import src.enums.AgeState;
import src.enums.EffectType;
import src.enums.Numbering;
import src.enums.YukkuriType;
import src.field.impl.Barrier;
import src.system.MainCommandUI;
import src.system.MapPlaceData;
import src.util.GameView;
import src.util.GameWorld;

/***************************************************
 * 各種オブジェクトの更新命令の発令所
 * <br>
 * 各種オブジェクトのインスタンス生成
 * <br>
 * ゲーム内環境の保持
 */
public class Terrarium implements Serializable {

	private static final long serialVersionUID = 7825541796890014097L;

	/**
	 * 昼夜の状態
	 */
	public static enum DayState {
		/** 朝 */
		MORNING,
		/** 昼 */
		DAY,
		/** 夕方 */
		EVENING,
		/** 夜 */
		NIGHT
	};

	/**
	 * 互換用の環境ミラー。
	 * <p>
	 * 既存テストが reflection で Terrarium 直下のフィールド名を参照しているため、
	 * 実体は TerrariumEnvironment に置いたまま、ここには同期用の鏡だけ残す。
	 */
	private static int operationTime = TerrariumEnvironment.getOperationTime();
	private static final int dayTime = TerrariumEnvironment.getDayTime();
	private static final int nightTime = TerrariumEnvironment.getNightTime();
	private static final int TICK = TerrariumEnvironment.getTick();
	private static boolean humid = TerrariumEnvironment.isHumid();
	private static boolean antifungalSteam = TerrariumEnvironment.isAntifungalSteam();
	private static boolean orangeSteam = TerrariumEnvironment.isOrangeSteam();
	private static boolean ageBoostSteam = TerrariumEnvironment.isAgeBoostSteam();
	private static boolean ageStopSteam = TerrariumEnvironment.isAgeStopSteam();
	private static boolean antidosSteam = TerrariumEnvironment.isAntidosSteam();
	private static boolean poisonSteam = TerrariumEnvironment.isPoisonSteam();
	private static boolean predatorSteam = TerrariumEnvironment.isPredatorSteam();
	private static boolean sugerSteam = TerrariumEnvironment.isSugerSteam();
	private static boolean noSleepSteam = TerrariumEnvironment.isNoSleepSteam();
	private static boolean hybridSteam = TerrariumEnvironment.isHybridSteam();
	private static boolean rapidPregnantSteam = TerrariumEnvironment.isRapidPregnantSteam();
	private static boolean antiNonYukkuriDiseaseSteam = TerrariumEnvironment.isAntiNonYukkuriDiseaseSteam();
	private static boolean endlessFurifuriSteam = TerrariumEnvironment.isEndlessFurifuriSteam();
	private static int intervalCount = TerrariumEnvironment.getInterval();

	public static int getOperationTime() {
		return operationTime;
	}

	public static int getDayTime() {
		return TerrariumEnvironment.getDayTime();
	}

	public static int getNightTime() {
		return TerrariumEnvironment.getNightTime();
	}

	public static boolean isHumid() {
		return humid;
	}

	public static boolean isAntifungalSteam() {
		return antifungalSteam;
	}

	public static boolean isOrangeSteam() {
		return orangeSteam;
	}

	public static boolean isAgeBoostSteam() {
		return ageBoostSteam;
	}

	public static boolean isAgeStopSteam() {
		return ageStopSteam;
	}

	public static boolean isAntidosSteam() {
		return antidosSteam;
	}

	public static boolean isPoisonSteam() {
		return poisonSteam;
	}

	public static boolean isPredatorSteam() {
		return predatorSteam;
	}

	public static boolean isSugerSteam() {
		return sugerSteam;
	}

	public static boolean isNoSleepSteam() {
		return noSleepSteam;
	}

	public static boolean isHybridSteam() {
		return hybridSteam;
	}

	public static boolean isRapidPregnantSteam() {
		return rapidPregnantSteam;
	}

	public static boolean isAntiNonYukkuriDiseaseSteam() {
		return antiNonYukkuriDiseaseSteam;
	}

	public static boolean isEndlessFurifuriSteam() {
		return endlessFurifuriSteam;
	}

	public static int getTick() {
		return TICK;
	}

	/** ゆっくりのリスト */
	private static List<Yukkuri> babyList = new LinkedList<Yukkuri>();
	/** マップ全体が警戒モードになる時間 */
	private final static int ALARM_PERIOD = 300; // 30 seconds
	/** 汎用長方形 */
	private static Rectangle4y tmpRect = new Rectangle4y();

	/**
	 * セーブの実行部
	 * 
	 * @param file ファイル
	 * @throws IOException IO例外
	 */
	public static void saveState(File file) throws IOException {
		GameWorld.get().setMaxUniqueId(Numbering.INSTANCE.getYukkuriID());
		GameWorld.get().setMaxObjId(Numbering.INSTANCE.getObjId());
		Enumeration<Entity> enu = GameWorld.get().getPlayer().getItemList().elements();
		while (enu.hasMoreElements()) {
			GameWorld.get().getPlayer().getItemForSave().add(enu.nextElement());
		}
		SaveDataCodec.save(GameWorld.get(), file);
	}

	/**
	 * ロードの実行部
	 * 
	 * @param file ファイル
	 * @throws IOException            IO例外
	 * @throws ClassNotFoundException クラスの存在しない場合の例外
	 */
	@SuppressWarnings("unchecked")
	public static void loadState(File file) throws IOException, ClassNotFoundException {
		World tmpWorld = SaveDataCodec.load(file);

		Numbering.INSTANCE.setYukkuriID(tmpWorld.getMaxUniqueId());
		Numbering.INSTANCE.setObjId(tmpWorld.getMaxObjId());
		tmpWorld.getPlayer().getItemList().clear();
		List<Integer> _list = new ArrayList<Integer>();
		for (Entity o : tmpWorld.getPlayer().getItemForSave()) {
			int id = o.getObjId();
			if (!_list.contains(id)) {
				_list.add(id);
				tmpWorld.getPlayer().getItemList().addElement(o);
			}
		}
		// 持ち物を復元
		if (MainCommandUI.getItemWindow() != null && MainCommandUI.getItemWindow().getItemList() != null) {
			MainCommandUI.getItemWindow().getItemList().setModel(tmpWorld.getPlayer().getItemList());
		}

		// ウィンドウサイズを復元
		tmpWorld.recalcMapSize();
		GameWorld.set(tmpWorld);

		if (SimYukkuri.simYukkuri != null) {
			if (GameWorld.get().getWindowType() != 2) {
				SimYukkuri.simYukkuri.setWindowMode(GameWorld.get().getWindowType(),
						GameWorld.get().getTerrariumSizeIndex());
			} else {
				SimYukkuri.simYukkuri.setFullScreenMode(GameWorld.get().getTerrariumSizeIndex());
			}
		}

		// マップの復元
		GameWorld.get().setNextMap(GameWorld.get().getCurrentMap().getMapIndex());
		if (GameView.getPane() != null) {
			GameView.loadTerrainFile();
		}
		GameWorld.get().changeMap();
		if (GameView.getPane() != null) {
			GameView.createBackBuffer();
		}
		Translate.createTransTable(TerrainField.isPers());

		// 遅延読み込みの復元
		GameWorld.get().loadInterBodyImage();

		// 茎と実ゆの参照を復元
		for (MapPlaceData mpd : GameWorld.get().getMapList()) {
			for (Stalk s : mpd.getStalk().values()) {
				Yukkuri b = mpd.getBody().get(s.getPlantYukkuri());
				if (b != null) {
					// ゾンビ除去: ID一致のStalkを除去してから、Map側のインスタンスで上書き
					b.getStalks().removeIf(z -> z.getStalkId().equals(s.getStalkId()));
					b.getStalks().add(s);
				}
			}
			for (Stalk s : mpd.getStalk().values()) {
				Yukkuri parent = mpd.getBody().get(s.getPlantYukkuri());

				for (Integer babyId : s.getBindBabies()) {
					Yukkuri baby = mpd.getBody().get(babyId);

					if (baby != null && parent != null && baby.isUnBirth()) {
						baby.setParentLinkId(parent.getUniqueID());
						baby.setBindStalk(s);
						int i = s.getBindBabies().indexOf(babyId);
						int babyZ = ((i % 5) * -2 + 14);

						int actualZ = s.getZ() + babyZ;

						baby.setZ(actualZ);
						baby.setCalcZ(actualZ);
					}
				}
			}
		}

		// すぃーと乗客の参照を復元
		for (MapPlaceData mpd : GameWorld.get().getMapList()) {
			for (Sui sui : mpd.getSui().values()) {
				Yukkuri[] previousBindBodies = sui.getBindBody();
				Yukkuri[] restoredBindBodies = new Yukkuri[previousBindBodies == null ? 3 : previousBindBodies.length];
				Yukkuri restoredOwner = null;
				int restoredBindCount = 0;

				if (sui.getBindobj() instanceof Yukkuri
						&& ((Yukkuri) sui.getBindobj()).getParentLinkId() == sui.getObjId()) {
					restoredOwner = (Yukkuri) sui.getBindobj();
				}

				for (Yukkuri b : mpd.getBody().values()) {
					if (b.getParentLinkId() != sui.getObjId()) {
						continue;
					}
					if (restoredBindCount >= restoredBindBodies.length) {
						break;
					}
					restoredBindBodies[restoredBindCount++] = b;
					b.setShadowVisible(false);
					if (restoredOwner == null) {
						restoredOwner = b;
					}
				}

				sui.setBindBody(restoredBindBodies);
				sui.setCurrent_bindbody_num(restoredBindCount);
				sui.setBindobj(restoredOwner);
				sui.upDate();
			}
		}

		// 自動えさやり機の生成物参照を復元
		for (MapPlaceData mpd : GameWorld.get().getMapList()) {
			for (AutoFeeder feeder : mpd.getAutofeeder().values()) {
				Entity food = feeder.getFood();
				if (food == null) {
					continue;
				}

				Entity restoredFood = null;
				if (food instanceof Yukkuri) {
					restoredFood = mpd.getBody().values().stream()
							.filter(b -> b.getObjId() == food.getObjId())
							.findFirst()
							.orElse(null);
				} else if (food instanceof Food) {
					restoredFood = mpd.getFood().get(food.getObjId());
				}

				if (restoredFood != null) {
					feeder.setFood(restoredFood);
				}
			}
		}

		// ホットプレートの拘束中個体と煙参照を復元
		for (MapPlaceData mpd : GameWorld.get().getMapList()) {
			for (HotPlate hotPlate : mpd.getHotPlate().values()) {
				Yukkuri bindBody = hotPlate.getBindBody();
				if (bindBody != null) {
					Yukkuri restoredBody = mpd.getBody().values().stream()
							.filter(b -> b.getObjId() == bindBody.getObjId())
							.findFirst()
							.orElse(null);
					hotPlate.setBindBody(restoredBody);
				}

				Effect smoke = hotPlate.getSmoke();
				if (smoke != null) {
					Effect restoredSmoke = mpd.getSortEffect().get(smoke.getObjId());
					if (restoredSmoke == null) {
						restoredSmoke = mpd.getFrontEffect().get(smoke.getObjId());
					}
					hotPlate.setSmoke(restoredSmoke);
				}
			}
		}

		// 粘着板の拘束中個体を復元
		for (MapPlaceData mpd : GameWorld.get().getMapList()) {
			for (StickyPlate stickyPlate : mpd.getStickyPlate().values()) {
				Yukkuri bindBody = stickyPlate.getBindBody();
				if (bindBody != null) {
					Yukkuri restoredBody = mpd.getBody().values().stream()
							.filter(b -> b.getObjId() == bindBody.getObjId())
							.findFirst()
							.orElse(null);
					stickyPlate.setBindBody(restoredBody);
				}
			}
		}

		// ミキサーの攪拌中個体とエフェクトを復元
		for (MapPlaceData mpd : GameWorld.get().getMapList()) {
			for (Mixer mixer : mpd.getMixer().values()) {
				Effect mix = mixer.getMix();
				if (mix != null) {
					Effect restoredMix = mpd.getSortEffect().get(mix.getObjId());
					if (restoredMix == null) {
						restoredMix = mpd.getFrontEffect().get(mix.getObjId());
					}
					mixer.setMix(restoredMix);
				}
			}
		}

		// ダストシュートの拘束中オブジェクト参照を復元
		for (MapPlaceData mpd : GameWorld.get().getMapList()) {
			for (GarbageChute garbageChute : mpd.getGarbagechute().values()) {
				List<Entity> restoredBindObjList = new ArrayList<>();
				if (garbageChute.getBindObjList() != null) {
					for (Entity bindObj : garbageChute.getBindObjList()) {
						if (bindObj == null) {
							continue;
						}
						Entity restoredObj = null;
						if (bindObj instanceof Yukkuri) {
							restoredObj = mpd.getBody().values().stream()
									.filter(b -> b.getObjId() == bindObj.getObjId())
									.findFirst()
									.orElse(null);
						} else if (bindObj instanceof Food) {
							restoredObj = mpd.getFood().get(bindObj.getObjId());
						} else if (bindObj instanceof Shit) {
							restoredObj = mpd.getShit().get(bindObj.getObjId());
						} else if (bindObj instanceof Vomit) {
							restoredObj = mpd.getVomit().get(bindObj.getObjId());
						} else if (bindObj instanceof Stalk) {
							restoredObj = mpd.getStalk().get(bindObj.getObjId());
						}
						if (restoredObj != null) {
							restoredBindObjList.add(restoredObj);
						}
					}
				}
				garbageChute.setBindObjList(restoredBindObjList);

				Yukkuri bindBody = garbageChute.getBindBody();
				if (bindBody != null) {
					Yukkuri restoredBody = mpd.getBody().values().stream()
							.filter(b -> b.getObjId() == bindBody.getObjId())
							.findFirst()
							.orElse(null);
					garbageChute.setBindBody(restoredBody);
				}
			}
		}

		// ゴミ捨て場の中身参照を復元
		for (MapPlaceData mpd : GameWorld.get().getMapList()) {
			for (GarbageStation garbageStation : mpd.getGarbageStation().values()) {
				Entity[] foods = garbageStation.getFood();
				if (foods == null) {
					continue;
				}
				Entity[] restoredFoods = new Entity[foods.length];
				for (int i = 0; i < foods.length; i++) {
					Entity food = foods[i];
					if (food == null) {
						continue;
					}
					restoredFoods[i] = mpd.getFood().get(food.getObjId());
				}
				garbageStation.setFood(restoredFoods);
			}
		}

		// ゆんばの作業対象参照を復元
		for (MapPlaceData mpd : GameWorld.get().getMapList()) {
			for (Yunba yunba : mpd.getYunba().values()) {
				Entity target = yunba.getTarget();
				if (target == null) {
					continue;
				}

				Entity restoredTarget = null;
				if (target instanceof Yukkuri) {
					restoredTarget = mpd.getBody().values().stream()
							.filter(b -> b.getObjId() == target.getObjId())
							.findFirst()
							.orElse(null);
				} else if (target instanceof Food) {
					restoredTarget = mpd.getFood().get(target.getObjId());
				} else if (target instanceof Shit) {
					restoredTarget = mpd.getShit().get(target.getObjId());
				} else if (target instanceof Vomit) {
					restoredTarget = mpd.getVomit().get(target.getObjId());
				} else if (target instanceof Stalk) {
					restoredTarget = mpd.getStalk().get(target.getObjId());
				}

				if (restoredTarget != null) {
					yunba.setTarget(restoredTarget);
				}
			}
		}

		System.gc();
	}

	/**
	 * パニック時の挙動
	 * 
	 * @param b ゆっくり
	 */
	private void checkPanic(Yukkuri b) {
		TerrariumWorldLogic.checkPanic(b);
	}

	/**
	 * 引火処理
	 * 
	 * @param b ゆっくり
	 */
	private void checkFire(Yukkuri b) {
		int minDistance;
		// 燃えてないなら終了
		if (b.getAttachmentSize(Fire.class) == 0) {
			return;
		}
		// 全ゆっくりに対してチェック
		for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			Yukkuri p = entry.getValue();
			// 自分同士のチェックは無意味なのでスキップ
			if (p == b) {
				continue;
			}
			if (b.isRemoved()) {
				continue;
			}
			// 相手との間に壁があればスキップ
			if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(),
					Barrier.MAP_BODY[b.getBodyAgeState().ordinal()])) {
				continue;
			}

			minDistance = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());
			if (minDistance <= Translate.distance(0, 0, b.getStep() * 2, b.getStep() * 2)) {
				// 接触状態で自分が燃えていたら飛び火
				p.giveFire();
			}
		}
	}

	/**
	 * 赤ゆの追加(胎生出産時用)
	 *
	 * @param x   発生場所X座標
	 * @param y   発生場所Y座標
	 * @param z   発生場所Z座標
	 * @param dna 赤ゆのDNA
	 * @param p1  母親
	 * @param p2  父親
	 */
	private void addBaby(int x, int y, int z, Dna dna, Yukkuri p1, Yukkuri p2) {
		babyList.add(makeBody(x, y, z + 1, dna, AgeState.BABY, p1, p2));
		babyList.get(babyList.size() - 1).kick(0, 5, -2);

	}

	/**
	 * 赤ゆの追加(茎式出産時用)
	 *
	 * @param x     発生場所X座標
	 * @param y     発生場所Y座標
	 * @param z     発生場所Z座標
	 * @param dna   赤ゆのDNA
	 * @param p1    母親
	 * @param p2    父親
	 * @param stalk 出生もとの茎(なければnull)
	 */
	private void addBaby(int x, int y, int z, Dna dna, Yukkuri p1, Yukkuri p2, Stalk stalk) {
		babyList.add(makeBody(x, y, z, dna, AgeState.BABY, p1, p2));
		Yukkuri b = babyList.get(babyList.size() - 1);
		stalk.setBindBaby(b);
		b.setBindStalk(stalk);
		b.setUnBirth(true);
		b.setShadowVisible(false);
	}

	/**
	 * 赤ゆの追加(主に爆発四散時用)
	 *
	 * @param x   発生場所X座標
	 * @param y   発生場所Y座標
	 * @param z   発生場所Z座標
	 * @param vx  初速X成分
	 * @param vy  初速Y成分
	 * @param vz  初速Z成分
	 * @param dna 赤ゆのDNA
	 * @param p1  母親
	 * @param p2  父親
	 */
	private void addBaby(int x, int y, int z, int vx, int vy, int vz, Dna dna, Yukkuri p1, Yukkuri p2) {
		babyList.add(makeBody(x, y, z + 1, dna, AgeState.BABY, p1, p2));
		babyList.get(babyList.size() - 1).kick(vx, vy, vz);

	}

	/**
	 * ゆっくりの追加(出産用ショートカット)
	 *
	 * @param x   発生場所X座標
	 * @param y   発生場所Y座標
	 * @param z   発生場所Z座標
	 * @param dna ゆっくりのDNA
	 * @param age 追加時の年齢
	 * @param p1  母親
	 * @param p2  父親
	 * @return 生成したゆっくり
	 */
	public Yukkuri makeBody(int x, int y, int z, Dna dna, AgeState age, Yukkuri p1, Yukkuri p2) {
		return makeBody(x, y, z, dna.getType(), dna, age, p1, p2, true);
	}

	/**
	 * ゆっくりの追加実行部
	 *
	 * @param x              発生場所X座標
	 * @param y              発生場所Y座標
	 * @param z              発生場所Z座標
	 * @param dna            赤ゆのDNA
	 * @param type           ゆっくりの種類
	 * @param age            追加時の年齢
	 * @param p1             母親
	 * @param p2             父親
	 * @param buildNewFamily 家族を作成するかどうか
	 * @return 生成したゆっくり
	 */
	public Yukkuri makeBody(int x, int y, int z, YukkuriType type, Dna dna, AgeState age, Yukkuri p1, Yukkuri p2,
			boolean buildNewFamily) {
		Yukkuri b = BodyFactory.create(x, y, z, type, dna, age, p1, p2, buildNewFamily,
				TerrariumViewBridge::loadBodyImageSafe,
				() -> GameWorld.get().getCurrentMap().makeOrKillDos(true),
				TerrariumWorldLogic::setNewFamily);
		return b;
	}

	/**
	 * ゆっくりの追加(一般用ショートカット)
	 *
	 * @param x    発生場所X座標
	 * @param y    発生場所Y座標
	 * @param z    発生場所Z座標
	 * @param type ゆっくりの種類
	 * @param age  追加時の年齢
	 * @param p1   母親
	 * @param p2   父親
	 * @return 生成したゆっくり
	 */
	public Yukkuri addBody(int x, int y, int z, YukkuriType type, AgeState age, Yukkuri p1, Yukkuri p2) {
		Yukkuri ret = makeBody(x, y, z, type, null, age, p1, p2, true);
		TerrariumBodyRegistry.register(ret);
		return ret;
	}

	/** ゆっくりをリストに登録 */
	public void addBody(Yukkuri b) {
		TerrariumBodyRegistry.register(b);
	}

	/**
	 * うんうんの追加＆リスト登録
	 *
	 * @param x    発生場所X座標
	 * @param y    発生場所Y座標
	 * @param z    発生場所Z座標
	 * @param b    主
	 * @param type 種類
	 */
	public int addShit(int x, int y, int z, Yukkuri b, YukkuriType type) {
		return TerrariumObjectFactory.addShit(x, y, z, b, type);
	}

	/**
	 * ゆ下痢の追加＆リスト登録
	 *
	 * @param x    発生場所X座標
	 * @param y    発生場所Y座標
	 * @param z    発生場所Z座標
	 * @param b    主
	 * @param type 種類
	 */
	public void addCrushedShit(int x, int y, int z, Yukkuri b, YukkuriType type) {
		TerrariumObjectFactory.addCrushedShit(x, y, z, b, type);
	}

	/**
	 * 吐餡追加＆リスト登録
	 *
	 * @param x    発生場所X座標
	 * @param y    発生場所Y座標
	 * @param z    発生場所Z座標
	 * @param b    主
	 * @param type 種類
	 * @return 生成した吐餡
	 */
	public Vomit addVomit(int x, int y, int z, Yukkuri body, YukkuriType type) {
		return TerrariumObjectFactory.addVomit(x, y, z, body, type);
	}

	/**
	 * つぶれ吐餡追加＆リスト登録
	 *
	 * @param x    発生場所X座標
	 * @param y    発生場所Y座標
	 * @param z    発生場所Z座標
	 * @param b    主
	 * @param type 種類
	 */
	public void addCrushedVomit(int x, int y, int z, Yukkuri body, YukkuriType type) {
		TerrariumObjectFactory.addCrushedVomit(x, y, z, body, type);
	}

	/**
	 * エフェクト追加
	 *
	 * @param type   エフェクトの種類の指定
	 * @param x      発生場所X座標
	 * @param y      発生場所Y座標
	 * @param z      発生場所Z座標
	 * @param vx     初期の移動量ベクトルX成分
	 * @param vy     初期の移動量ベクトルY成分
	 * @param vz     初期の移動量ベクトルZ成分
	 * @param invert 初期の向き(0で左、1で右)
	 * @param life   継続時間
	 * @param loop   アニメのループの有無
	 * @param end    ループが一周したら消えるか否か
	 * @param grav   重力の影響の有無
	 * @param front  エフェクトが親オブジェクトの前後どっちか(trueが前)
	 * @return できたエフェクト
	 */
	public Effect addEffect(EffectType type, int x, int y, int z, int vx, int vy, int vz,
			boolean invert, int life, int loop, boolean end, boolean grav, boolean front) {
		return TerrariumEffectFactory.addEffect(type, x, y, z, vx, vy, vz, invert, life, loop, end, grav, front);
	}

	/** マップ全体を危険と認知させる */
	public static void setAlarm() {
		TerrariumAlarmLogic.setAlarm(ALARM_PERIOD);
	}

	/** マップ全体で危険か否かを取得する. */
	public static boolean getAlarm() {
		return TerrariumAlarmLogic.getAlarm();
	}

	/**
	 * 一日の明るさを管理
	 * <br>
	 * ゲーム開始時を昼にしている都合上12時間ずれている
	 * <br>
	 * 0～6時：昼、6～8時：夕方、8～19時：夜、19～20時：朝、20～24時：昼
	 */
	public static DayState getDayState() {
		if ((operationTime) % (dayTime + nightTime) < nightTime / 5) {
			return DayState.MORNING;
		} else if ((operationTime) % (dayTime + nightTime) < dayTime - nightTime / 5) {
			return DayState.DAY;
		} else if ((operationTime) % (dayTime + nightTime) < dayTime) {
			return DayState.EVENING;
		} else {
			return DayState.NIGHT;
		}
	}

	/** ディヒューザーによる影響のリセット */
	public static void resetTerrariumEnvironment() {
		TerrariumEnvironment.resetTerrariumEnvironment();
		humid = false;
		antifungalSteam = false;
		orangeSteam = false;
		ageBoostSteam = false;
		ageStopSteam = false;
		antidosSteam = false;
		poisonSteam = false;
		predatorSteam = false;
		sugerSteam = false;
		noSleepSteam = false;
		hybridSteam = false;
		rapidPregnantSteam = false;
		antiNonYukkuriDiseaseSteam = false;
		endlessFurifuriSteam = false;
	}

	static void applyDiffuserSteamFlags(boolean[] flags) {
		TerrariumEnvironment.applyDiffuserSteamFlags(flags);
		if (flags[Diffuser.SteamType.ANTI_FUNGAL.ordinal()]) {
			antifungalSteam = true;
		}
		if (flags[Diffuser.SteamType.STEAM.ordinal()]) {
			humid = true;
		}
		if (flags[Diffuser.SteamType.ORANGE.ordinal()]) {
			orangeSteam = true;
		}
		if (flags[Diffuser.SteamType.AGE_BOOST.ordinal()]) {
			ageBoostSteam = true;
		}
		if (flags[Diffuser.SteamType.AGE_STOP.ordinal()]) {
			ageStopSteam = true;
		}
		if (flags[Diffuser.SteamType.ANTI_DOS.ordinal()]) {
			antidosSteam = true;
		}
		if (flags[Diffuser.SteamType.ANTI_YU.ordinal()]) {
			poisonSteam = true;
		}
		if (flags[Diffuser.SteamType.PREDATOR.ordinal()]) {
			predatorSteam = true;
		}
		if (flags[Diffuser.SteamType.SUGER.ordinal()]) {
			sugerSteam = true;
		}
		if (flags[Diffuser.SteamType.NOSLEEP.ordinal()]) {
			noSleepSteam = true;
		}
		if (flags[Diffuser.SteamType.HYBRID.ordinal()]) {
			hybridSteam = true;
		}
		if (flags[Diffuser.SteamType.RAPIDPREGNANT.ordinal()]) {
			rapidPregnantSteam = true;
		}
		if (flags[Diffuser.SteamType.ANTI_NONYUKKURI.ordinal()]) {
			antiNonYukkuriDiseaseSteam = true;
		}
		if (flags[Diffuser.SteamType.ENDLESS_FURIFURI.ordinal()]) {
			endlessFurifuriSteam = true;
		}
	}

	/** 稼働インターバル取得 */
	public static int getInterval() {
		return intervalCount;
	}

	/** 全オブジェクトの更新 スレッドと紛らわしいので名前変更 */
	public void stepRun() {
		MapPlaceData curMap = GameWorld.get().getCurrentMap();
		int intervalCount = advanceInterval();
		TerrariumAlarmLogic.advanceAlarm(curMap);
		TerrariumTickProcessor.processMapTicks(curMap, intervalCount);
		boolean transCheckNow = (operationTime % 60 == 0);
		Yukkuri transBodyNow = null;
		List<Yukkuri> bodiesNow = new LinkedList<Yukkuri>(curMap.getBody().values());
		if (Terrarium.getInterval() == 0) {
			Collections.shuffle(bodiesNow);
		}
		for (Yukkuri b : bodiesNow) {
			Yukkuri candidate = BodyTickProcessor.processBody(this, curMap, b, babyList, transCheckNow);
			if (transCheckNow && transBodyNow == null && candidate != null) {
				transBodyNow = candidate;
			}
		}
		if (!babyList.isEmpty()) {
			for (Yukkuri baby : babyList) {
				curMap.getBody().put(baby.getUniqueID(), baby);
			}
			babyList.clear();
		}
		if (transBodyNow != null) {
			transBodyNow.execTransform();
		}
		advanceOperationTime();
	}

	private static int advanceInterval() {
		intervalCount = TerrariumEnvironment.advanceInterval();
		return intervalCount;
	}

	private static void advanceOperationTime() {
		TerrariumEnvironment.advanceOperationTime();
		operationTime = TerrariumEnvironment.getOperationTime();
	}

	private Rectangle translateRectangles(Rectangle4y r) {
		return new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * 家族の関係を設定
	 *
	 * @param b            対象ゆっくり
	 * @param p            対象のつがい
	 * @param bodyNewChild 新たに家族に加える新しい個体
	 */
	public void setNewFamily(Yukkuri b, Yukkuri p, Yukkuri bodyNewChild) {
		TerrariumWorldLogic.setNewFamily(b, p, bodyNewChild);
	}
}
