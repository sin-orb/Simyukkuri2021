package src.draw;

import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import src.SimYukkuri;
import src.attachment.Fire;
import src.base.Body;
import src.base.Effect;
import src.base.Obj;
import src.base.ObjEX;
import src.base.Okazari;
import src.command.GadgetAction;
import src.effect.BakeSmoke;
import src.effect.Hit;
import src.effect.Mix;
import src.effect.Steam;
import src.enums.AgeState;
import src.enums.EffectType;
import src.enums.Event;
import src.enums.Numbering;
import src.enums.ObjEXType;
import src.enums.PanicType;
import src.enums.YukkuriType;
import src.game.Dna;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.item.Barrier;
import src.item.Beltconveyor;
import src.item.Diffuser;
import src.item.Farm;
import src.item.Food;
import src.item.Pool;
import src.item.Toilet;
import src.item.Toy;
import src.logic.BedLogic;
import src.logic.BodyLogic;
import src.logic.EventLogic;
import src.logic.FamilyActionLogic;
import src.logic.FoodLogic;
import src.logic.StoneLogic;
import src.logic.ToiletLogic;
import src.system.FieldShapeBase;
import src.system.MainCommandUI;
import src.system.MapPlaceData;
import src.util.YukkuriUtil;
import src.yukkuri.Alice;
import src.yukkuri.Ayaya;
import src.yukkuri.Chen;
import src.yukkuri.Chiruno;
import src.yukkuri.Deibu;
import src.yukkuri.DosMarisa;
import src.yukkuri.Eiki;
import src.yukkuri.Fran;
import src.yukkuri.HybridYukkuri;
import src.yukkuri.Kimeemaru;
import src.yukkuri.Marisa;
import src.yukkuri.MarisaKotatsumuri;
import src.yukkuri.MarisaReimu;
import src.yukkuri.MarisaTsumuri;
import src.yukkuri.Meirin;
import src.yukkuri.Myon;
import src.yukkuri.Nitori;
import src.yukkuri.Patch;
import src.yukkuri.Ran;
import src.yukkuri.Reimu;
import src.yukkuri.ReimuMarisa;
import src.yukkuri.Remirya;
import src.yukkuri.Sakuya;
import src.yukkuri.Suwako;
import src.yukkuri.Tarinai;
import src.yukkuri.TarinaiReimu;
import src.yukkuri.Tenko;
import src.yukkuri.Udonge;
import src.yukkuri.WasaReimu;
import src.yukkuri.Yurusanae;
import src.yukkuri.Yuuka;
import src.yukkuri.Yuyuko;

/***************************************************
 * 各種オブジェクトの更新命令の発令所
 * <br>各種オブジェクトのインスタンス生成
 * <br>ゲーム内環境の保持
*/
public class Terrarium implements Serializable{

	private static final long serialVersionUID = 7825541796890014097L;
	/**起動時間*/
	public static int operationTime = 0;
	/** 昼の時間 */
	public static final int dayTime = 100 * 24 * 2 / 3;
	/** 夜の時間 */
	public static final int nightTime = 100 * 24 - dayTime;

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

	/**ディヒューザーの出している蒸気の有無*/
	public static boolean humid = false;
	public static boolean antifungalSteam = false;
	public static boolean orangeSteam = false;
	public static boolean ageBoostSteam = false;
	public static boolean ageStopSteam = false;
	public static boolean antidosSteam = false;
	public static boolean poisonSteam = false;
	public static boolean predatorSteam = false;
	public static boolean sugerSteam = false;
	public static boolean noSleepSteam = false;
	public static boolean hybridSteam = false;
	public static boolean rapidPregnantSteam = false;
	public static boolean antiNonYukkuriDiseaseSteam = false;
	public static boolean endlessFurifuriSteam = false;
	/** 処理の最小時間単位*/
	public static final int TICK = 1;
	/**ゆっくりのリスト*/
	private static List<Body> babyList = new LinkedList<Body>();
	/**マップ全体が警戒モードになる時間*/
	private final static int ALARM_PERIOD = 300; // 30 seconds
	/**処理インターバル(軽量化のため)*/
	private static int intervalCount = 0;
	/**汎用長方形*/
	private static Rectangle4y tmpRect = new Rectangle4y();
	
	/**
	 * セーブの実行部
	 * @param file ファイル
	 * @throws IOException IO例外
	 */
	public static void saveState(File file) throws IOException {
		SimYukkuri.world.setMaxUniqueId(Numbering.INSTANCE.getYukkuriID());
		SimYukkuri.world.setMaxObjId(Numbering.INSTANCE.getObjId());
		Enumeration<Obj> enu = SimYukkuri.world.player.getItemList().elements();
		while (enu.hasMoreElements()) {
			SimYukkuri.world.player.getItemForSave().add(enu.nextElement());
		}
		ObjectOutputStream out = new ObjectOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(file)));
		try {
			out.writeUTF(Terrarium.class.getCanonicalName());
			out.writeObject(SimYukkuri.world);
			out.flush();
		} finally {
			out.close();
		}
	}
	
	/**
	 * セーブの実行部
	 * @param file ファイル
	 * @throws IOException IO例外
	 */
	public static void saveStateTemporary(File file) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		SimYukkuri.world.setMaxUniqueId(Numbering.INSTANCE.getYukkuriID());
		SimYukkuri.world.setMaxObjId(Numbering.INSTANCE.getObjId());
		Enumeration<Obj> enu = SimYukkuri.world.player.getItemList().elements();
		while (enu.hasMoreElements()) {
			SimYukkuri.world.player.getItemForSave().add(enu.nextElement());
		}
		String json = mapper.writeValueAsString(SimYukkuri.world);
		try {
			// JSON文字列をバイト配列に変換
			byte[] jsonBytes = json.getBytes("UTF-8");

			// GZIP形式で圧縮して保存
			try (FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile());
				 GZIPOutputStream gos = new GZIPOutputStream(fos)) {
				gos.write(jsonBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ロードの実行部
	 * @param file ファイル
	 * @throws IOException IO例外
	 * @throws ClassNotFoundException クラスの存在しない場合の例外
	 */
	@SuppressWarnings("unchecked")
	public static void loadState(File file) throws IOException, ClassNotFoundException {
		World tmpWorld = null;
		ObjectInputStream in = new ObjectInputStream(
				new BufferedInputStream(
						new FileInputStream(file)));
		try {
			String s = in.readUTF();
			if (!Terrarium.class.getCanonicalName().equals(s)) {
				String errMsg = "Bad save: " + s;
				throw new IOException(errMsg);
			}
			tmpWorld = (World) in.readObject();

		} finally {
			in.close();
		}
		Numbering.INSTANCE.setYukkuriID(tmpWorld.getMaxUniqueId());
		Numbering.INSTANCE.setObjId(tmpWorld.getMaxObjId());
		tmpWorld.player.getItemList().clear();
		List<Integer> _list = new ArrayList<Integer>();
		for (Obj o : tmpWorld.player.getItemForSave()) {
			int id = o.getObjId();
			if (!_list.contains(id)) {
				_list.add(id);
				tmpWorld.player.getItemList().addElement(o);
			}
		}
		// 持ち物を復元
		MainCommandUI.itemWindow.itemList.setModel(tmpWorld.player.getItemList());

		// ウィンドウサイズを復元
		tmpWorld.recalcMapSize();
		SimYukkuri.world = tmpWorld;

		if (SimYukkuri.world.windowType != 2) {
			SimYukkuri.simYukkuri.setWindowMode(SimYukkuri.world.windowType, SimYukkuri.world.terrariumSizeIndex);
		} else {
			SimYukkuri.simYukkuri.setFullScreenMode(SimYukkuri.world.terrariumSizeIndex);
		}

		// マップの復元
		SimYukkuri.world.setNextMap(SimYukkuri.world.getCurrentMap().mapIndex);
		SimYukkuri.mypane.loadTerrainFile();
		SimYukkuri.world.changeMap();

		SimYukkuri.mypane.createBackBuffer();
		Translate.createTransTable(TerrainField.isPers());

		// 遅延読み込みの復元
		SimYukkuri.world.loadInterBodyImage();

		System.gc();
	}
	
	@SuppressWarnings("unchecked")
	public static void loadStateTemporary(File file) throws IOException, ClassNotFoundException {
		World tmpWorld = null;
		String json =decompressGzipToString(file.getAbsolutePath());
		ObjectMapper mapper = new ObjectMapper();
		tmpWorld = mapper.readValue(json,World.class);

		Numbering.INSTANCE.setYukkuriID(tmpWorld.getMaxUniqueId());
		Numbering.INSTANCE.setObjId(tmpWorld.getMaxObjId());
		tmpWorld.player.getItemList().clear();
		List<Integer> _list = new ArrayList<Integer>();
		for (Obj o : tmpWorld.player.getItemForSave()) {
			int id = o.getObjId();
			if (!_list.contains(id)) {
				_list.add(id);
				tmpWorld.player.getItemList().addElement(o);
			}
		}
		// 持ち物を復元
		MainCommandUI.itemWindow.itemList.setModel(tmpWorld.player.getItemList());

		// ウィンドウサイズを復元
		tmpWorld.recalcMapSize();
		SimYukkuri.world = tmpWorld;

		if (SimYukkuri.world.windowType != 2) {
			SimYukkuri.simYukkuri.setWindowMode(SimYukkuri.world.windowType, SimYukkuri.world.terrariumSizeIndex);
		} else {
			SimYukkuri.simYukkuri.setFullScreenMode(SimYukkuri.world.terrariumSizeIndex);
		}

		// マップの復元
		SimYukkuri.world.setNextMap(SimYukkuri.world.getCurrentMap().mapIndex);
		SimYukkuri.mypane.loadTerrainFile();
		SimYukkuri.world.changeMap();

		SimYukkuri.mypane.createBackBuffer();
		Translate.createTransTable(TerrainField.isPers());

		// 遅延読み込みの復元
		SimYukkuri.world.loadInterBodyImage();

		System.gc();
	}

	 // GZIPファイルを解凍して文字列として返すメソッド
	public static String decompressGzipToString(String filePath) throws IOException {
		try (FileInputStream fis = new FileInputStream(filePath);
			 GZIPInputStream gis = new GZIPInputStream(fis);
			 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			// バッファを使ってデータを読み込む
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = gis.read(buffer)) != -1) {
				baos.write(buffer, 0, bytesRead);
			}
			// バイト配列をUTF-8エンコードの文字列に変換
			return baos.toString("UTF-8");
		}
	}
	
	/**
	 *  パニック時の挙動
	 * @param b ゆっくり
	 */
	private void checkPanic(Body b) {
		if (b.isDead() || b.isPealed()) {
			return;
		}
		int minDistance;

		// 全ゆっくりに対してチェック
		for (Map.Entry<Integer, Body> entry : SimYukkuri.world.getCurrentMap().body.entrySet()) {
			Body p = entry.getValue();
			// 自分同士のチェックは無意味なのでスキップ
			if (p == b) {
				continue;
			}
			// 相手との間に壁があればスキップ
			if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(),
					Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				continue;
			}
			minDistance = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());

			/*
			// 相手が宙に浮いてたら無視
			if (p.getZ() != 0) {
				continue;
			}*/

			// パニックの伝播
			if (minDistance <= p.getEYESIGHTorg()) {
				// 恐怖同士で伝播の無限ループに入らないように制限。れいぱー覚醒してたら恐れない
				if (b.getPanicType() == PanicType.BURN && !p.isRaper()) {
					p.setPanic(true, PanicType.FEAR);
				}
			}
		}
	}

	/**
	 *  引火処理
	 * @param b ゆっくり
	 */
	private void checkFire(Body b) {
		int minDistance;
		// 燃えてないなら終了
		if (b.getAttachmentSize(Fire.class) == 0) {
			return;
		}
		// 全ゆっくりに対してチェック
		for (Map.Entry<Integer, Body> entry : SimYukkuri.world.getCurrentMap().body.entrySet()) {
			Body p = entry.getValue();
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

	/**赤ゆの追加(胎生出産時用)
	 *
	 * @param x 発生場所X座標
	 * @param y 発生場所Y座標
	 * @param z 発生場所Z座標
	 * @param dna 赤ゆのDNA
	 * @param p1 母親
	 * @param p2 父親
	 */
	private void addBaby(int x, int y, int z, Dna dna, Body p1, Body p2) {
		babyList.add(makeBody(x, y, z + 1, dna, AgeState.BABY, p1, p2));
		babyList.get(babyList.size() - 1).kick(0, 5, -2);

	}

	/**赤ゆの追加(茎式出産時用)
	 *
	 * @param x 発生場所X座標
	 * @param y 発生場所Y座標
	 * @param z 発生場所Z座標
	 * @param dna 赤ゆのDNA
	 * @param p1 母親
	 * @param p2 父親
	 * @param stalk 出生もとの茎(なければnull)
	 */
	private void addBaby(int x, int y, int z, Dna dna, Body p1, Body p2, Stalk stalk) {
		babyList.add(makeBody(x, y, z, dna, AgeState.BABY, p1, p2));
		Body b = babyList.get(babyList.size() - 1);
		stalk.setBindBaby(b);
		b.setBindStalk(stalk);
		b.setUnBirth(true);
		b.setDropShadow(false);
	}

	/**赤ゆの追加(主に爆発四散時用)
	 *
	  * @param x 発生場所X座標
	 * @param y 発生場所Y座標
	 * @param z 発生場所Z座標
	 * @param vx 初速X成分
	 * @param vy 初速Y成分
	 * @param vz 初速Z成分
	 * @param dna 赤ゆのDNA
	 * @param p1 母親
	 * @param p2 父親
	 */
	private void addBaby(int x, int y, int z, int vx, int vy, int vz, Dna dna, Body p1, Body p2) {
		babyList.add(makeBody(x, y, z + 1, dna, AgeState.BABY, p1, p2));
		babyList.get(babyList.size() - 1).kick(vx, vy, vz);

	}

	/**ゆっくりの追加(出産用ショートカット)
	 *
	 * @param x 発生場所X座標
	 * @param y 発生場所Y座標
	 * @param z 発生場所Z座標
	 * @param dna ゆっくりのDNA
	 * @param age 追加時の年齢
	 * @param p1 母親
	 * @param p2 父親
	 * @return 生成したゆっくり
	 */
	public Body makeBody(int x, int y, int z, Dna dna, AgeState age, Body p1, Body p2) {
		return makeBody(x, y, z, dna.getType(), dna, age, p1, p2, true);
	}

	/**ゆっくりの追加実行部
	 *
	 * @param x 発生場所X座標
	 * @param y 発生場所Y座標
	 * @param z 発生場所Z座標
	 * @param dna 赤ゆのDNA
	 * @param type ゆっくりの種類
	 * @param age 追加時の年齢
	 * @param p1 母親
	 * @param p2 父親
	 * @param buildNewFamily 家族を作成するかどうか
	 * @return 生成したゆっくり
	 */
	public Body makeBody(int x, int y, int z, int type, Dna dna, AgeState age, Body p1, Body p2,
			boolean buildNewFamily) {

		Body b;
		Body papa = p2;
		Body mama = p1;
		if (papa == null && dna != null) {
			papa = YukkuriUtil.getBodyInstance(dna.getFather());
		}

		switch (type) {
		case Marisa.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.MARISA);
			b = new Marisa(x, y, z, age, mama, papa);
			break;
		case Reimu.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.REIMU);
			b = new Reimu(x, y, z, age, mama, papa);
			break;
		case Alice.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.ALICE);
			b = new Alice(x, y, z, age, mama, papa);
			break;
		case Patch.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.PATCH);
			b = new Patch(x, y, z, age, mama, papa);
			break;
		case Chen.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.CHEN);
			b = new Chen(x, y, z, age, mama, papa);
			break;
		case Myon.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.MYON);
			b = new Myon(x, y, z, age, mama, papa);
			break;
		case WasaReimu.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.REIMU);
			SimYukkuri.mypane.loadBodyImage(YukkuriType.WASAREIMU);
			b = new WasaReimu(x, y, z, age, mama, papa);
			break;
		case MarisaTsumuri.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.MARISA);
			SimYukkuri.mypane.loadBodyImage(YukkuriType.MARISATSUMURI);
			b = new MarisaTsumuri(x, y, z, age, mama, papa);
			break;
		case MarisaKotatsumuri.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.MARISAKOTATSUMURI);
			b = new MarisaKotatsumuri(x, y, z, age, mama, papa);
			break;
		case Deibu.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.REIMU);
			SimYukkuri.mypane.loadBodyImage(YukkuriType.DEIBU);
			b = new Deibu(x, y, z, age, mama, papa);
			break;
		case DosMarisa.type:
			if (SimYukkuri.world.getCurrentMap().makeOrKillDos(true)) {
				SimYukkuri.mypane.loadBodyImage(YukkuriType.DOSMARISA);
				b = new DosMarisa(x, y, z, age, mama, papa);
			} else {
				SimYukkuri.mypane.loadBodyImage(YukkuriType.MARISA);
				b = new Marisa(x, y, z, age, mama, papa);
			}
			break;
		case Tarinai.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.TARINAI);
			b = new Tarinai(x, y, z, age, mama, papa);
			break;
		case TarinaiReimu.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.TARINAI);
			SimYukkuri.mypane.loadBodyImage(YukkuriType.TARINAIREIMU);
			b = new TarinaiReimu(x, y, z, age, mama, papa);
			break;
		case MarisaReimu.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.REIMU);
			SimYukkuri.mypane.loadBodyImage(YukkuriType.MARISAREIMU);
			b = new MarisaReimu(x, y, z, age, mama, papa);
			break;
		case ReimuMarisa.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.MARISA);
			SimYukkuri.mypane.loadBodyImage(YukkuriType.REIMUMARISA);
			b = new ReimuMarisa(x, y, z, age, mama, papa);
			break;
		case HybridYukkuri.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.HYBRIDYUKKURI);
			b = new HybridYukkuri(x, y, z, age, mama, papa);
			break;
		case Remirya.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.REMIRYA);
			b = new Remirya(x, y, z, age, mama, papa);
			break;
		case Fran.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.FRAN);
			b = new Fran(x, y, z, age, mama, papa);
			break;
		case Ayaya.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.AYAYA);
			b = new Ayaya(x, y, z, age, mama, papa);
			break;
		case Chiruno.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.CHIRUNO);
			b = new Chiruno(x, y, z, age, mama, papa);
			break;
		case Eiki.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.EIKI);
			b = new Eiki(x, y, z, age, mama, papa);
			break;
		case Kimeemaru.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.KIMEEMARU);
			b = new Kimeemaru(x, y, z, age, mama, papa);
			break;
		case Meirin.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.MEIRIN);
			b = new Meirin(x, y, z, age, mama, papa);
			break;
		case Nitori.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.NITORI);
			b = new Nitori(x, y, z, age, mama, papa);
			break;
		case Ran.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.RAN);
			b = new Ran(x, y, z, age, mama, papa);
			break;
		case Suwako.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.SUWAKO);
			b = new Suwako(x, y, z, age, mama, papa);
			break;
		case Tenko.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.TENKO);
			b = new Tenko(x, y, z, age, mama, papa);
			break;
		case Udonge.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.UDONGE);
			b = new Udonge(x, y, z, age, mama, papa);
			break;
		case Yurusanae.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.YURUSANAE);
			b = new Yurusanae(x, y, z, age, mama, papa);
			break;
		case Yuyuko.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.YUYUKO);
			b = new Yuyuko(x, y, z, age, mama, papa);
			break;
		case Yuuka.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.YUUKA);
			b = new Yuuka(x, y, z, age, mama, papa);
			break;
		case Sakuya.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.SAKUYA);
			b = new Sakuya(x, y, z, age, mama, papa);
			break;
		default:
			throw new RuntimeException("Unknown yukkuri type.");
		}

		// DNA情報が渡されてたらステータス上書き
		if (dna != null) {
			if (dna.getAttitude() != null)
				b.setAttitude(dna.getAttitude());
			if (dna.getIntelligence() != null)
				b.setIntelligence(dna.getIntelligence());
		}
		// 共存環境の場合
		if (SimYukkuri.NAGASI_MODE == 2) {
			int nCount = 0;
			// 母がまりちゃ流しか
			if (mama != null) {
				if (mama.isbImageNagasiMode()) {
					nCount++;
				}
			}
			if (papa != null) {
				if (papa.isbImageNagasiMode()) {
					nCount++;
				}
			}
			if (nCount == 0) {
				if (SimYukkuri.RND.nextInt(20) == 0) {
					b.setbImageNagasiMode(true);
				}
			} else if (nCount == 1) {
				// 片親がまりちゃ流しなら1/2
				if (SimYukkuri.RND.nextBoolean()) {
					b.setbImageNagasiMode(true);
				}
			} else {
				if (SimYukkuri.RND.nextInt(20) != 0) {
					b.setbImageNagasiMode(true);
				}
			}
		} else {
			// 母親にあわせる
			if (mama != null && mama.isbImageNagasiMode()) {
				b.setbImageNagasiMode(true);
			}
		}
		if (buildNewFamily) {
			// 家族の関係を設定
			setNewFamily(mama, papa, b);
		}
		return b;
	}

	/**ゆっくりの追加(一般用ショートカット)
	 *
	 * @param x 発生場所X座標
	 * @param y 発生場所Y座標
	 * @param z 発生場所Z座標
	 * @param type ゆっくりの種類
	 * @param age 追加時の年齢
	 * @param p1 母親
	 * @param p2 父親
	 * @return 生成したゆっくり
	 */
	public Body addBody(int x, int y, int z, int type, AgeState age, Body p1, Body p2) {
		Body ret = makeBody(x, y, z, type, null, age, p1, p2, true);
		SimYukkuri.world.getCurrentMap().body.put(ret.getUniqueID(), ret);
		return ret;
	}

	/**ゆっくりをリストに登録*/
	public void addBody(Body b) {
		SimYukkuri.world.getCurrentMap().body.put(b.getUniqueID(), b);
	}

	/**
	 * うんうんの追加＆リスト登録
	 *
	 * @param x 発生場所X座標
	 * @param y 発生場所Y座標
	 * @param z 発生場所Z座標
	 * @param b 主
	 * @param type 種類
	 */
	public int addShit(int x, int y, int z, Body b, YukkuriType type) {
		Shit shit = new Shit(x, y, z, b, type);
		SimYukkuri.world.getCurrentMap().shit.put(shit.objId, shit);
		return shit.objId;
	}

	/**
	 * ゆ下痢の追加＆リスト登録
	 *
	 * @param x 発生場所X座標
	 * @param y 発生場所Y座標
	 * @param z 発生場所Z座標
	 * @param b 主
	 * @param type 種類
	 */
	public void addCrushedShit(int x, int y, int z, Body b, YukkuriType type) {
		Shit s = new Shit(x, y, z, b, type);
		s.crushShit();
		if (b != null && b.getMostDepth() < 0) {
			s.setMostDepth(b.getMostDepth());
			s.setMostDepth(b.getZ());
		}
		SimYukkuri.world.getCurrentMap().shit.put(s.objId, s);
	}

	/**
	 * 吐餡追加＆リスト登録
	 *
	 * @param x 発生場所X座標
	 * @param y 発生場所Y座標
	 * @param z 発生場所Z座標
	 * @param b 主
	 * @param type 種類
	 * @return 生成した吐餡
	 */
	public Vomit addVomit(int x, int y, int z, Body body, YukkuriType type) {
		Vomit v = new Vomit(x, y, z, body, type);
		SimYukkuri.world.getCurrentMap().vomit.put(v.objId, v);
		if (body != null && body.getMostDepth() < 0) {
			v.setMostDepth(body.getMostDepth());
			v.setMostDepth(body.getZ());
		}
		return v;
	}

	/**
	 * つぶれ吐餡追加＆リスト登録
	 *
	 * @param x 発生場所X座標
	 * @param y 発生場所Y座標
	 * @param z 発生場所Z座標
	 * @param b 主
	 * @param type 種類
	 */
	public void addCrushedVomit(int x, int y, int z, Body body, YukkuriType type) {
		Vomit v = new Vomit(x, y, z, body, type);
		v.crushVomit();
		if (body != null && body.getMostDepth() < 0) {
			v.setMostDepth(body.getMostDepth());
			v.setMostDepth(body.getZ());
		}
		SimYukkuri.world.getCurrentMap().vomit.put(v.objId, v);
	}

	/**エフェクト追加
	 *
	 * @param type エフェクトの種類の指定
	 * @param x 発生場所X座標
	 * @param y 発生場所Y座標
	 * @param z 発生場所Z座標
	 * @param vx 初期の移動量ベクトルX成分
	 * @param vy 初期の移動量ベクトルY成分
	 * @param vz 初期の移動量ベクトルZ成分
	 * @param invert 初期の向き(0で左、1で右)
	 * @param life 継続時間
	 * @param loop アニメのループの有無
	 * @param end ループが一周したら消えるか否か
	 * @param grav 重力の影響の有無
	 * @param front エフェクトが親オブジェクトの前後どっちか(trueが前)
	 * @return できたエフェクト
	 */
	public Effect addEffect(EffectType type, int x, int y, int z, int vx, int vy, int vz,
			boolean invert, int life, int loop, boolean end, boolean grav, boolean front) {
		Effect ret = null;
		switch (type) {
		case BAKE:
			ret = new BakeSmoke(x, y, z, vx, vy, vz, invert, life, loop, end, grav, front);
			break;
		case HIT:
			ret = new Hit(x, y, z, vx, vy, vz, invert, life, loop, end, grav, front);
			break;
		case MIX:
			ret = new Mix(x, y, z, vx, vy, vz, invert, life, loop, end, grav, front);
			break;
		case STEAM:
			ret = new Steam(x, y, z, vx, vy, vz, invert, life, loop, end, grav, front);
			break;
		}
		return ret;
	}

	/**マップ全体を危険と認知させる*/
	public static void setAlarm() {
		SimYukkuri.world.getCurrentMap().alarm = true;
		SimYukkuri.world.getCurrentMap().alarmPeriod = ALARM_PERIOD;
	}

	/**マップ全体で危険か否かを取得する.*/
	public static boolean getAlarm() {
		return SimYukkuri.world.getCurrentMap().alarm;
	}

	/**
	 * 一日の明るさを管理
	 * <br>ゲーム開始時を昼にしている都合上12時間ずれている
	 * <br>0～6時：昼、6～8時：夕方、8～19時：夜、19～20時：朝、20～24時：昼*/
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

	/**ディヒューザーによる影響のリセット*/
	public static void resetTerrariumEnvironment() {
		antifungalSteam = false;
		humid = false;
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

	/**稼働インターバル取得*/
	public static int getInterval() {
		return intervalCount;
	}

	/** 全オブジェクトの更新 スレッドと紛らわしいので名前変更*/
	public void stepRun() {
		//マップ状況を取得
		MapPlaceData curMap = SimYukkuri.world.getCurrentMap();
		intervalCount = (++intervalCount) & 255;
		//マップ上での緊張状態の経過
		if (curMap.alarmPeriod >= 0) {
			curMap.alarmPeriod--;
			if (curMap.alarmPeriod <= 0) {
				curMap.alarmPeriod = 0;
				curMap.alarm = false;
			}
		}
		/*
			地面に貼りついてるガジェットとオブジェクトの当たり判定
			床置きと他シェイプの優先順は以下で固定
		
			↑ 表面
			床置き
			(以下の中からヒットした１つ＋床置きでチェック)
			ベルトコンベア
			畑
			池
			↓ 地面
		*/
		// 床置きの判定
		Event ret = Event.DONOTHING;
		//このリストに登録してないと接触物に対し処理がなされないので注意
		List<ObjEX> platformList = SimYukkuri.world.getHitBaseList();
		//このリストに登録してないと処理がなされないので注意
		List<Obj> objList = SimYukkuri.world.getHitTargetList();

		for (Iterator<ObjEX> i = platformList.iterator(); i.hasNext();) {
			ObjEX platform = i.next();
			ret = platform.clockTick();
			if (ret == Event.REMOVED) {
				i.remove();
				continue;
			}

			if (!platform.getEnabled())
				continue;
			if (platform.getHitCheckObjType() == 0)
				continue;
			if (!platform.enableHitCheck())
				continue;

			// 毎フレームチェックは重いのでインターバルで数フレームに一度のチェックにする
			if (!platform.checkInterval(intervalCount))
				continue;

			Rectangle re = platform.getCollisionRect(translateRectangles(tmpRect));
			tmpRect.x = re.x;
			tmpRect.y = re.y;
			tmpRect.width = re.width;
			tmpRect.height = re.height;

			for (Obj o : objList) {
				int objType = 0;
				if (o == null) {
					continue;
				}
				switch (o.getObjType()) {
				case YUKKURI:
					objType = ObjEX.YUKKURI;
					break;
				case SHIT:
					objType = ObjEX.SHIT;
					break;
				case PLATFORM:
					objType = ObjEX.PLATFORM;
					break;
				case FIX_OBJECT:
					objType = ObjEX.FIX_OBJECT;
					break;
				case OBJECT:
					if (o instanceof Food)
						objType = ObjEX.FOOD;
					else if (o instanceof Toilet)
						objType = ObjEX.TOILET;
					else if (o instanceof Toy)
						objType = ObjEX.TOY;
					else if (o instanceof Stalk)
						objType = ObjEX.STALK;
					else
						objType = ObjEX.OBJECT;
					break;
				case VOMIT:
					objType = ObjEX.VOMIT;
					break;
				default:
					break;
				}
				if ((objType & platform.getHitCheckObjType()) != 0) {
					platform.checkHitObj(translateRectangles(tmpRect), o);
				}
			}
		}

		// コンベアの判定
		// 最前面のひとつだけに反応するのでターゲットを外ループに
		List<Beltconveyor> beltList = curMap.beltconveyor;
		objList = SimYukkuri.world.getHitTargetList();
		for (Obj o : objList) {
			if (beltList == null || beltList.size() == 0)
				break;
			if (o == null || o.isRemoved())
				continue;
			if ((Translate.getCurrentFieldMapNum(o.getX(), o.getY()) & FieldShapeBase.FIELD_BELT) == 0)
				continue;
			for (Iterator<Beltconveyor> i = beltList.iterator(); i.hasNext();) {
				Beltconveyor belt = i.next();
				ret = belt.clockTick();
				if (ret == Event.REMOVED) {
					i.remove();
						continue;
					}
				if (!belt.mapContains(o.getX(), o.getY()))
					continue;
				if (belt.checkHitObj(o)) {
					belt.processHitObj(o);
					break;
				}
			}
		}

		// プールの判定
		// 最前面のひとつだけに反応するのでターゲットを外ループに
		List<Pool> poolList = curMap.pool;
		objList = SimYukkuri.world.getHitTargetList();
		for (Obj o : objList) {
			if (poolList == null || poolList.size() == 0) {
				// プール内から外に移動していた場合
				if (o.getInPool()) {
					o.setInPool(false);
					o.setMostDepth(0);
					o.setInPool(false);
					o.setFallingUnderGround(false);
					if (o.getZ() < 0) {
						o.setCalcZ(0);
					}
				}
				continue;
			}
			if (o == null || o.isRemoved())
				continue;
			if ((Translate.getCurrentFieldMapNum(o.getX(), o.getY()) & FieldShapeBase.FIELD_POOL) == 0) {
				// プール内から外に移動していた場合
				if (o.getInPool()) {
					if (o instanceof Body) {
						((Body) o).setLockmove(false);
					}
					o.setInPool(false);
					o.setMostDepth(0);
					o.setInPool(false);
					o.setFallingUnderGround(false);
					if (o.getZ() < 0) {
						o.setCalcZ(0);
					}
				}
				continue;
			}
			//if((curMap.fieldMap[o.getX()][o.getY()] & FieldShapeBase.FIELD_BELT) == 0) continue;
			for (Iterator<Pool> i = poolList.iterator(); i.hasNext();) {
				Pool pool = i.next();
				ret = pool.clockTick();
				if (ret == Event.REMOVED) {
					i.remove();
					continue;
				}
				if (pool.checkHitObj(o)) {
					pool.objHitProcess(o);
					break;
				}
			}
		}

		// 畑の判定
		// 最前面のひとつだけに反応するのでターゲットを外ループに
		List<Farm> farmList = curMap.farm;
		objList = SimYukkuri.world.getHitTargetList();

		for (Obj o : objList) {
			if (farmList == null || farmList.size() == 0) {
				break;
			}
			if (o == null || o.isRemoved())
				continue;

			if ((Translate.getCurrentFieldMapNum(o.getX(), o.getY()) & FieldShapeBase.FIELD_FARM) == 0) {
				continue;
			}
			// 畑更新
			for (Iterator<Farm> i = farmList.iterator(); i.hasNext();) {
				Farm farm = i.next();
				ret = farm.clockTick();
				if (ret == Event.REMOVED) {
					i.remove();
					continue;
				}
				if (farm.checkHitObj(o)) {
					farm.objHitProcess(o);
					break;
				}
			}
		}
		// オブジェクト更新
		List<ObjEX> objectList = SimYukkuri.world.getObjectList();
		resetTerrariumEnvironment();
		for (Iterator<ObjEX> i = objectList.iterator(); i.hasNext();) {
			ObjEX oex = i.next();
			ret = oex.clockTick();
			if (ret == Event.REMOVED) {
				i.remove();
			}
			//ディフューザーの更新
			if (oex.getObjEXType() == ObjEXType.DIFFUSER && oex.getEnabled()) {
				boolean[] flags = ((Diffuser) oex).getSteamType();
				if (flags[Diffuser.SteamType.ANTI_FUNGAL.ordinal()])
					antifungalSteam = true;
				if (flags[Diffuser.SteamType.STEAM.ordinal()])
					humid = true;
				if (flags[Diffuser.SteamType.ORANGE.ordinal()])
					orangeSteam = true;
				if (flags[Diffuser.SteamType.AGE_BOOST.ordinal()])
					ageBoostSteam = true;
				if (flags[Diffuser.SteamType.AGE_STOP.ordinal()])
					ageStopSteam = true;
				if (flags[Diffuser.SteamType.ANTI_DOS.ordinal()])
					antidosSteam = true;
				if (flags[Diffuser.SteamType.ANTI_YU.ordinal()])
					poisonSteam = true;
				if (flags[Diffuser.SteamType.PREDATOR.ordinal()])
					predatorSteam = true;
				if (flags[Diffuser.SteamType.SUGER.ordinal()])
					sugerSteam = true;
				if (flags[Diffuser.SteamType.NOSLEEP.ordinal()])
					noSleepSteam = true;
				if (flags[Diffuser.SteamType.HYBRID.ordinal()])
					hybridSteam = true;
				if (flags[Diffuser.SteamType.RAPIDPREGNANT.ordinal()])
					rapidPregnantSteam = true;
				if (flags[Diffuser.SteamType.ANTI_NONYUKKURI.ordinal()])
					antiNonYukkuriDiseaseSteam = true;
				if (flags[Diffuser.SteamType.ENDLESS_FURIFURI.ordinal()])
					endlessFurifuriSteam = true;
			}
		}

		// うんうん更新
		List<Shit> shits = new LinkedList<Shit>();
		for (Map.Entry<Integer, Shit> entry : curMap.shit.entrySet()) {
			Shit s = entry.getValue();
			ret = s.clockTick();
			if (ret != Event.REMOVED) {
				shits.add(s);
			}
		}
		curMap.shit.clear();
		for (Shit shit : shits) {
			curMap.shit.put(shit.objId, shit);
		}

		// 吐餡更新
		List<Vomit> vomits = new LinkedList<Vomit>();
		for (Map.Entry<Integer, Vomit> entry : curMap.vomit.entrySet()) {
			Vomit v = entry.getValue();
			ret = v.clockTick();
			if (ret != Event.REMOVED) {
				vomits.add(v);
			}
		}
		curMap.vomit.clear();
		for (Vomit vomit : vomits) {
			curMap.vomit.put(vomit.objId, vomit);
		}

		// おかざり更新
		List<Okazari> okazaris = new LinkedList<Okazari>();
		for (Map.Entry<Integer, Okazari> entry : curMap.okazari.entrySet()) {
			Okazari o = entry.getValue();
			ret = o.clockTick();
			if (ret != Event.REMOVED) {
				okazaris.add(o);
			}
		}
		curMap.okazari.clear();
		for (Okazari o : okazaris) {
			curMap.okazari.put(o.objId, o);
		}

		boolean transCheck = (operationTime % 60 == 0);
		Body transBody = null;
		List<Body> bodies = new LinkedList<Body>(curMap.body.values());
		if (Terrarium.getInterval() == 0) {
			Collections.shuffle(bodies);
		}
		for (Body b : bodies) {
			ret = b.clockTick();
			switch (ret) {
			case DEAD:
				if (b.isInfration()) {
					int burstPower = (b.getSize() - b.getOriginSize()) * 3 / 4;
					for (Dna babyTypes : b.getBabyTypes()) {
						addBaby(b.getX(), b.getY(), b.getZ() + b.getSize() / 20,
								SimYukkuri.RND.nextInt(burstPower / 4 + 1) - burstPower / 8,
								SimYukkuri.RND.nextInt(burstPower / 4 + 1) - burstPower / 8,
								SimYukkuri.RND.nextInt(burstPower / 5 + 1) - burstPower / 10 - 1, babyTypes, b,
								YukkuriUtil.getBodyInstance(b.getPartner()));
					}
					b.getBabyTypes().clear();
					if (b.getStalks() != null) {
						for (Stalk s : b.getStalks()) {
							if (s != null) {
								s.kick(SimYukkuri.RND.nextInt(burstPower / 4 + 1) - burstPower / 8,
										SimYukkuri.RND.nextInt(burstPower / 4 + 1) - burstPower / 8,
										SimYukkuri.RND.nextInt(burstPower / 5 + 1) - burstPower / 10 - 1);
							}
						}
					}
					b.disPlantStalks();
					if (b.getShit() > b.getSHITLIMITorg()[b.getBodyAgeState().ordinal()]) {
						for (int j = 0; b.getShit() / b.getSHITLIMITorg()[b.getBodyAgeState().ordinal()] > j; j++) {
							int i = addShit(b.getX(), b.getY(), b.getZ() + b.getSize() / 15, b, b.getShitType());
							curMap.shit.get(i).kick(
									SimYukkuri.RND.nextInt(burstPower / 4 + 1) - burstPower / 8,
									SimYukkuri.RND.nextInt(burstPower / 4 + 1) - burstPower / 8,
									SimYukkuri.RND.nextInt(burstPower / 5 + 1) - burstPower / 10 - 1);
						}
					}
					b.setShit(0);
					if (!b.isCrushed()) {
						b.strikeByPress();
					}
				} else if (b.isCrushed()) {
					b.disPlantStalks();
				}
				b.upDate();
				continue;
			case BIRTHBABY:
				if (b.getAge() % 10 == 0) {
					if (!b.isHasPants()) {
						Dna babyType = b.getBabyTypesDequeue();
						if (babyType != null) {
							addBaby(b.getX(), b.getY(), b.getZ() + b.getSize() / 15, babyType, b,
									YukkuriUtil.getBodyInstance(b.getPartner()));
						}
					}
				}
				if (b.getStalks() != null) {
					for (Stalk s : b.getStalks()) {
						if (s != null) {
							for (Integer bab : s.getBindBabies()) {
								if (bab == null) {
									continue;
								}
								Body ba = YukkuriUtil.getBodyInstance(bab);
								if (ba != null) {
									ba.setUnBirth(false);
									ba.setDropShadow(true);
									ba.setBindStalk(null);
									// 赤ゆなら胎生妊娠と合わせるため年齢リセット
									if (ba.isBaby()) {
										ba.setAgeState(AgeState.BABY);
									}
									ba.kick(0, 0, 0);
								}
							}
							s.getBindBabies().clear();
							s.setPlantYukkuri(null);
							// 正常な出産時は茎をフード化
							int fx, fy;
							for (int f = 0; f < 5; f++) {
								fx = s.getX() - 6 + (f * 7);
								fy = s.getY() - 5 + SimYukkuri.RND.nextInt(10);
								fx = Math.max(0, fx);
								fx = Math.min(fx, Translate.mapW);
								fy = Math.max(0, fy);
								fy = Math.min(fy, Translate.mapH);
								Food food = (Food) GadgetAction.putObjEX(Food.class, fx, fy,
										Food.FoodType.STALK.ordinal());
								SimYukkuri.world.getCurrentMap().food.put(food.objId, food);
							}
							s.remove();
						}
					}
					b.removeAllStalks();
				}
				if (b.getBabyTypes().size() == 0 || b.getStalks().size() == 0) {
					b.setHasBaby(false);
					b.setHasStalk(false);
				}
				break;
			case DOSHIT:
				int objId = addShit(b.getX(), b.getY(), b.getZ() + b.getSize() / 15, b, b.getShitType());
				curMap.shit.get(objId).kick(0, 1, 1);
				break;
			case DOCRUSHEDSHIT:
				// 漏らした場合
				addCrushedShit(b.getX(), b.getY(), b.getZ(), b, b.getShitType());
				break;
			case DOVOMIT:
				addVomit(b.getX(), b.getY(), b.getZ(), b, b.getShitType());
				break;
			case REMOVED:
				b.upDate();
				b.remove();
				continue;
			default:
				break;
			}
			// 引火判定
			checkFire(b);
			// 石判定
			StoneLogic.checkPubble(b);
			// パニック時は別処理
			if (b.getPanicType() != null && !b.isUnBirth() && !b.isDamagedHeavily()) {
				checkPanic(b);
			} else {
				// イベント処理
				if (b.getCurrentEvent() != null) {
					EventLogic.eventUpdate(b);
				}

				//　子供のリストに生きている子供がいるか
				boolean bHasChildren = false;
				List<Body> childrenList = BodyLogic.createActiveChildList(b, true);
				if (childrenList != null && childrenList.size() != 0) {
					bHasChildren = true;
				}

				//logic周り
				boolean bCheck = true;
				if (b.getBlockedCount() == 0)
					bCheck = true;
				else
					bCheck = false;

				// 子供がいるなら家族イベントを最優先する
				if (bHasChildren) {
					if (bCheck)
						if (FamilyActionLogic.checkFamilyAction(b))
							bCheck = false;
						else
							bCheck = true;
				}

				// check Food
				if (bCheck) {
					if (FoodLogic.checkFood(b))
						bCheck = false;
					else
						bCheck = true;
				}

				// check Sukkiri
				if (bCheck) {
					if (BodyLogic.checkPartner(b))
						bCheck = false;
					else
						bCheck = true;
				}

				// check shit
				if (bCheck) {
					if (ToiletLogic.checkShit(b))
						bCheck = false;
					else
						bCheck = true;
				}

				// check toilet
				if (bCheck) {
					if (ToiletLogic.checkToilet(b))
						bCheck = false;
					else
						bCheck = true;
				}

				// check sleep
				if (bCheck) {
					if (BedLogic.checkBed(b))
						bCheck = false;
					else
						bCheck = true;
				}

				if (!bHasChildren) {
					// 子供がいないなら家族イベントの優先度は最低
					if (bCheck) {
						if (!FamilyActionLogic.checkFamilyAction(b))
							bCheck = true;
						else
							bCheck = false;
					}
				}

			}

			if (b.getStalkBabyTypes().size() > 0) {
				int j = 0;
				Stalk s = null;
				for (Dna babyTypes : b.getStalkBabyTypes()) {
					if (j % 5 == 0) {
						s = (Stalk)GadgetAction.putObjEX(Stalk.class, b.getX(), b.getY(), b.getDirection().ordinal());
						b.getStalks().add(s);
						s.setPlantYukkuri(b);
					}
					if (babyTypes != null) {
						addBaby(b.getX(), b.getY(), 0, babyTypes, b, YukkuriUtil.getBodyInstance(b.getPartner()),
								s);
						babyList.get(babyList.size() - 1).setBindStalk(s);
					} else {
						s.setBindBaby(null);
					}
					j++;
				}
				b.getStalkBabyTypes().clear();
			}
			b.upDate();

			// 突然変異チェック
			// ループ内でリストをいじると例外が出るのでここでは候補の取り出しのみ
			if (transCheck && transBody == null) {
				transBody = b.checkTransform();
			}
		}
		// add babies.
		if (!babyList.isEmpty()) {
			for (Body baby : babyList) {
				curMap.body.put(baby.getUniqueID(), baby);
			}
			babyList.clear();
		}

		// エフェクト
		List<Effect> effects = new LinkedList<Effect>();
		for (Map.Entry<Integer, Effect> entry : curMap.sortEffect.entrySet()) {
			Effect ef = entry.getValue();
			ret = ef.clockTick();
			if (ret != Event.REMOVED) {
				effects.add(ef);
			}
		}
		curMap.sortEffect.clear();
		for (Effect e : effects) {
			curMap.sortEffect.put(e.objId, e);
		}
		
		effects.clear();
		for (Map.Entry<Integer, Effect> entry : curMap.frontEffect.entrySet()) {
			Effect ef = entry.getValue();
			ret = ef.clockTick();
			if (ret != Event.REMOVED) {
				effects.add(ef);
			}
		}
		curMap.frontEffect.clear();
		for (Effect e : effects) {
			curMap.frontEffect.put(e.objId, e);
		}
		// イベントリストの有効期間チェック
		EventLogic.clockWorldEvent();

		// 突然変異実行
		if (transBody != null) {
			transBody.execTransform();
		}
		operationTime += TICK;
	}

	private Rectangle translateRectangles(Rectangle4y r) {
		return new Rectangle(r.x, r.y, r.width, r.height);
	}

	/** 家族の関係を設定
	 *
	 * @param b 対象ゆっくり
	 * @param p 対象のつがい
	 * @param bodyNewChild 新たに家族に加える新しい個体
	 */
	public void setNewFamily(Body b, Body p, Body bodyNewChild) {
		if (b == null) {
			return;
		}
		List<Integer> childrenListOld = b.getChildrenList(); //　子供のリスト

		// 子供のリスト
		Iterator<Integer> itr = childrenListOld.iterator();
		while (itr.hasNext()) {
			Body child = YukkuriUtil.getBodyInstance(itr.next());
			if (child == null) {
				continue;
			}
			// 子供に姉のリストに追加
			bodyNewChild.addElderSisterList(child);
			// 子供がいる場合は各子供の妹のリストに追加
			child.addSisterList(bodyNewChild);
		}

		// 子供をリストに追加
		b.addChildrenList(bodyNewChild);
		if ((p != null) && (p != b)) {
			// つがいにも子供をリストに追加
			setNewFamily(p, null, bodyNewChild);
		}
	}
}
