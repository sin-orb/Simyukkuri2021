package src.draw;

import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
import src.system.MapPlaceData;
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


public class Terrarium {

	public static int operationTime = 0;
	public static final int dayTime = 100*24 *2/3;
	public static final int nightTime = 100*24 - dayTime;
	public static enum DayState { MORNING,DAY,EVENING,NIGHT };

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
	
	public static final int TICK = 1;
	
	private static ArrayList<Body> babyList = new ArrayList<Body>();
	private final static int ALARM_PERIOD = 300; // 30 seconds
	private static int intervalCount = 0;
	private static Rectangle tmpRect = new Rectangle();

	protected static Random rnd = new Random();
	
	public static void saveState(File file) throws IOException {
		ObjectOutputStream out =
				new ObjectOutputStream(
						new BufferedOutputStream(
								new FileOutputStream(file)));
		try {
			out.writeUTF(Terrarium.class.getCanonicalName());
			out.writeObject(Numbering.INSTANCE.getYukkuriID());
			out.writeObject(SimYukkuri.world);
			out.flush();
		}
		finally {
			out.close();
		}
	}

	public static void loadState(File file) throws IOException, ClassNotFoundException {
		ObjectInputStream in =
				new ObjectInputStream(
						new BufferedInputStream(
								new FileInputStream(file)));
		World tmpWorld;

		try {
			String s = in.readUTF();
			if(!Terrarium.class.getCanonicalName().equals(s)) {
				String errMsg = "Bad save: "+s;
				throw new IOException(errMsg);
			}
			Numbering.INSTANCE.setYukkuriID(((Integer)in.readObject()).intValue());
			tmpWorld = (World)in.readObject();

		} finally {
			in.close();
		}
		
		// ウィンドウサイズを復元
		tmpWorld.recalcMapSize();
		SimYukkuri.world = tmpWorld;

		if ( SimYukkuri.world.windowType != 2 ){
			SimYukkuri.simYukkuri.setWindowMode(SimYukkuri.world.windowType, SimYukkuri.world.terrariumSizeIndex);
		}else{
			SimYukkuri.simYukkuri.setFullScreenMode(SimYukkuri.world.terrariumSizeIndex);
		}
		
		// マップの復元
		SimYukkuri.world.setNextMap(SimYukkuri.world.currentMap.mapIndex);
		SimYukkuri.mypane.loadTerrainFile();
		SimYukkuri.world.currentMap = SimYukkuri.world.changeMap();

		SimYukkuri.mypane.createBackBuffer();
		Translate.createTransTable(TerrainField.isPers());
	
		// 遅延読み込みの復元
		SimYukkuri.world.loadInterBodyImage();

		System.gc();
	}

	// パニック時の挙動
	private void checkPanic(Body b) {
		if (b.isDead() || b.isPealed()) {
			return;
		}
		int minDistance;

		// 全ゆっくりに対してチェック
		ArrayList<Body> bodyList = SimYukkuri.world.currentMap.body;
		
		for (Body p:bodyList) {
			// 自分同士のチェックは無意味なのでスキップ
			if (p == b) {
				continue;
			}
			// 相手との間に壁があればスキップ
			if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(), Barrier.MAP_BODY[b.getBodyAgeState().ordinal()]+Barrier.BARRIER_KEKKAI)) {
				continue;
			}
			minDistance = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());

			/*
			// 相手が宙に浮いてたら無視
			if (p.getZ() != 0) {
				continue;
			}*/
				
			// パニックの伝播
			if (minDistance <= p.getEYESIGHT()) {
				// 恐怖同士で伝播の無限ループに入らないように制限
				if(b.getPanicType() == PanicType.BURN) {
					p.setPanic(true, PanicType.FEAR);
				}
			}
		}
	}
	// 引火処理
	private void checkFire(Body b)
	{
		int minDistance;
		// 燃えてないなら終了
		if(b.getAttachmentSize(Fire.class) == 0) {
			return;
		}
		// 全ゆっくりに対してチェック
		ArrayList<Body> bodyList = SimYukkuri.world.currentMap.body;
		
		// 全ゆっくりに対してチェック
		for (Body p:bodyList) {
			// 自分同士のチェックは無意味なのでスキップ
			if (p == b) {
				continue;
			}
			if( b.isRemoved() )
			{
				continue;
			}
			// 相手との間に壁があればスキップ
			if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(), Barrier.MAP_BODY[b.getBodyAgeState().ordinal()])) {
				continue;
			}

			minDistance = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());
			if (minDistance <= Translate.distance(0, 0, b.getStep() * 2, b.getStep() * 2)) {
				// 接触状態で自分が燃えていたら飛び火
				p.giveFire();
			}
		}
	}

	private void addBaby(int x, int y, int z, Dna dna, Body p1, Body p2) {
		babyList.add(makeBody(x, y, z + 1, dna, AgeState.BABY, p1, p2));
		babyList.get(babyList.size()-1).kick(0,5,-2);
		
	}
	
	private void addBaby(int x, int y, int z, Dna dna, Body p1, Body p2, Stalk stalk) {
		babyList.add(makeBody(x, y, z, dna, AgeState.BABY, p1, p2));
		Body b = babyList.get( babyList.size()-1 );
		stalk.setBindBaby( b );
		b.setBindStalk(stalk);
		b.setUnBirth( true );
		b.setDropShadow(false);
	}
	
	private void addBaby(int x, int y, int z, int vx, int vy, int vz, Dna dna, Body p1, Body p2) {
		babyList.add(makeBody(x, y, z + 1, dna, AgeState.BABY, p1, p2));
		babyList.get(babyList.size()-1).kick(vx,vy,vz);
		
	}

	public Body makeBody(int x, int y, int z, Dna dna, AgeState age, Body p1, Body p2) {
		return makeBody(x, y, z, dna.type, dna, age, p1, p2);
	}

	public Body makeBody(int x, int y, int z, int type, Dna dna, AgeState age, Body p1, Body p2) {
		
		Body b;
		Body papa = p2;
		Body mama = p1;
		if( papa == null && dna != null ){
			papa = dna.father;
		}

		switch (type) {
		case Marisa.type:
			b = new Marisa(x, y, z, age, mama, papa);
			break;
		case Reimu.type:
			b = new Reimu(x, y, z, age, mama, papa);
			break;
		case Alice.type:
			b = new Alice(x, y, z, age, mama, papa);
			break;
		case Patch.type:
			b = new Patch(x, y, z, age, mama, papa);
			break;
		case Chen.type:
			b = new Chen(x, y, z, age, mama, papa);
			break;
		case Myon.type:
			b = new Myon(x, y, z, age, mama, papa);
			break;
		case WasaReimu.type:
			b = new WasaReimu(x, y ,z, age, mama, papa);
			break;
		case MarisaTsumuri.type:
			b = new MarisaTsumuri(x, y ,z, age,  mama, papa);
			break;
		case MarisaKotatsumuri.type:
			b = new MarisaKotatsumuri(x, y ,z, age, mama, papa);
			break;
		case Deibu.type:
			b = new Deibu(x, y ,z, age, mama, papa);
			break;
		case DosMarisa.type:
			b = new DosMarisa(x, y ,z, age, mama, papa);
			break;
		case Tarinai.type:
			b = new Tarinai(x, y, z, age, mama, papa);
			break;
		case TarinaiReimu.type:
			b = new TarinaiReimu(x, y, z, age, mama, papa);
			break;
		case MarisaReimu.type:
			b = new MarisaReimu(x, y, z, age, mama, papa);
			break;
		case ReimuMarisa.type:
			b = new ReimuMarisa(x, y, z, age, mama, papa);
			break;
		case HybridYukkuri.type:
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
		if(dna != null) {
			if(dna.attitude != null) b.setAttitude(dna.attitude);
			if(dna.intelligence != null) b.setIntelligence(dna.intelligence);
		}
		/*コンストラクタのほうに移植中
		// 生い立ちの設定
		Body.BodyRank eBodyRank = Body.BodyRank.KAIYU;
		Body.PublicRank ePublicRank = Body.PublicRank.NONE;
		if(mama != null){
			Body.BodyRank eMotherBodyRank = mama.getBodyRank();
			// 母親のランクに応じて変更
			switch(eMotherBodyRank){
				case KAIYU:// 飼いゆ
					eBodyRank = Body.BodyRank.KAIYU;
					break;
				case NORAYU:// 野良ゆ
					eBodyRank = Body.BodyRank.NORAYU;
					break;
				default:
					break;			
			}

			Body.PublicRank eMotherPubRank = mama.getPublicRank();
			// 母親のランクに応じて変更
			switch(eMotherPubRank){
				case NONE:
					ePublicRank = Body.PublicRank.NONE;
					break;
				case UnunSlave:// うんうん奴隷
					ePublicRank = Body.PublicRank.UnunSlave;
					break;
				default:
					break;			
			}
			/*
			//　141229時点で飼いゆと野良ゆしか機能していないので他の選択肢はコメントアウト
			switch(eMotherBodyRank)
			{
				case KAIYU:// 飼いゆ
					eBodyRank = Body.BodyRank.KAIYU;
					break;
				case SUTEYU:// 捨てゆ
					eBodyRank = Body.BodyRank.NORAYU_CLEAN;
					break;
				case NORAYU_CLEAN:// きれいな野良ゆ
					eBodyRank = Body.BodyRank.NORAYU_CLEAN;
					break;
				case NORAYU:// 野良ゆ
					eBodyRank = Body.BodyRank.NORAYU_CLEAN;
					break;
				case YASEIYU://　野生ゆ
					eBodyRank = Body.BodyRank.NORAYU_CLEAN;
					break;
				default:
					break;			
			}
			
		}
		// 生い立ちを設定
		b.setBodyRank(eBodyRank);
		b.setPublicRank(ePublicRank);

		// 先祖の情報を引き継ぐ
		if( mama != null ){
			ArrayList<Integer> anTempList = mama.getAncestorList();
			int nType = mama.getType();
			b.addAncestorList( anTempList );
			b.addAncestorList( nType );
		}
		if( papa != null ){
			ArrayList<Integer> anTempList = papa.getAncestorList();
			int nType = papa.getType();
			b.addAncestorList( anTempList );
			b.addAncestorList( nType );
		}*/
		
		// 共存環境の場合
		if( SimYukkuri.NAGASI_MODE == 2 ){
			int nCount = 0;
			// 母がまりちゃ流しか
			if( mama != null ){
				if( mama.isbImageNagasiMode() ){
					nCount++;	
				}
			}
			if( papa != null ){
				if( papa.isbImageNagasiMode() ){
					nCount++;	
				}
			}
			if( nCount == 0 ){
				if(rnd.nextInt(20) == 0 ){
					b.setbImageNagasiMode(true);
				}				
			}
			else if( nCount == 1 ){
				// 片親がまりちゃ流しなら1/2
				if( rnd.nextBoolean()){
					b.setbImageNagasiMode(true);
				}		
			}
			else{
				if(rnd.nextInt(20) != 0 ){
					b.setbImageNagasiMode(true);
				}				
			}
		}
		else{
			// 母親にあわせる
			if( mama != null && mama.isbImageNagasiMode() ){
				b.setbImageNagasiMode(true);
			}
		}
		// 家族の関係を設定
		setNewFamily(mama, papa, b);
		return b;
	}

	public Body addBody(int x, int y, int z, int type, AgeState age, Body p1, Body p2) {
		Body ret = makeBody(x, y, z, type, null, age, p1, p2);
		SimYukkuri.world.currentMap.body.add(ret);
		return ret;
	}
	
	public void addBody(Body b) {
		SimYukkuri.world.currentMap.body.add(b);
	}

	public void addShit(int x, int y, int z, Body b, YukkuriType type) {
		SimYukkuri.world.currentMap.shit.add(new Shit(x, y, z, b, type));
	}

	public void addCrushedShit(int x, int y, int z, Body b, YukkuriType type) {
		Shit s = new Shit(x, y, z, b, type);
		s.crushShit();
		if( b != null && b.getMostDepth() < 0 )
		{
			s.setMostDepth(b.getMostDepth());
			s.setMostDepth(b.getZ());
		}
		SimYukkuri.world.currentMap.shit.add(s);
	}

	public Vomit addVomit(int x, int y, int z, Body body, YukkuriType type) {
		Vomit v = new Vomit(x, y, z, body, type);
		SimYukkuri.world.currentMap.vomit.add(v);
		if( body != null && body.getMostDepth() < 0 )
		{
			v.setMostDepth(body.getMostDepth());
			v.setMostDepth(body.getZ());
		}
		return v;
	}

	public void addCrushedVomit(int x, int y, int z, Body body, YukkuriType type) {
		Vomit v = new Vomit(x, y, z, body, type);
		v.crushVomit();
		if( body != null && body.getMostDepth() < 0 )
		{
			v.setMostDepth(body.getMostDepth());
			v.setMostDepth(body.getZ());
		}
		SimYukkuri.world.currentMap.vomit.add(v);
	}

	public Effect addEffect(EffectType type, int x, int y, int z, int vx, int vy, int vz,
			boolean invert, int life, int loop, boolean end, boolean grav, boolean front) {
		Effect ret = null;
		switch(type) {
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

	public static void setAlarm() {
		SimYukkuri.world.currentMap.alarm = true;
		SimYukkuri.world.currentMap.alarmPeriod = ALARM_PERIOD;
	}

	public static boolean getAlarm() {
		return SimYukkuri.world.currentMap.alarm;
	}	

	public static DayState getDayState(){
		if ( (operationTime) % ( dayTime + nightTime ) < nightTime / 5 ) {
			return DayState.MORNING;
		}
		else if ( (operationTime) % ( dayTime + nightTime ) < dayTime - nightTime / 5 ){
			return DayState.DAY;
		}
		else if ( (operationTime) % ( dayTime + nightTime ) < dayTime){
			return DayState.EVENING;
		}
		else{
			return DayState.NIGHT;
		}
	}
	
	public static void resetTerrariumEnvironment(){
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
	
	public static int getInterval() {
		return intervalCount;
	}

	// 全オブジェクトの更新 スレッドと紛らわしいので名前変更
	public void stepRun() {

//		// ログファイル出力
//		if( SimYukkuri.DEBUG_OUTPUT )
//		{
//			String strLog = new String();StackTraceElement throwableStackTraceElement = new Throwable().getStackTrace()[0];strLog += throwableStackTraceElement.getClassName();strLog += " : ";strLog += throwableStackTraceElement.getMethodName();strLog += " : ";strLog += throwableStackTraceElement.getLineNumber();
//			strLog += " : Start stepRun";
//			LoggerYukkuri.outputLogFile(strLog);
//		}
		
		MapPlaceData curMap = SimYukkuri.world.currentMap;
		
		intervalCount = (++intervalCount) & 255;

		if (curMap.alarmPeriod >= 0) {
			curMap.alarmPeriod--;
			if (curMap.alarmPeriod <= 0) {
				curMap.alarmPeriod = 0;
				curMap.alarm = false;
			}
		}

		if(Terrarium.getInterval() == 0) {
			// シャッフル(シャッフルする意味がありそうなもののみ)
			Collections.shuffle(curMap.body);
			Collections.shuffle(curMap.food);
			Collections.shuffle(curMap.shit);
			Collections.shuffle(curMap.stalk);
			Collections.shuffle(curMap.vomit);
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
		List<ObjEX> platformList = SimYukkuri.world.getHitBaseList();
		List<Obj> objList = SimYukkuri.world.getHitTargetList();
		
		for (Iterator<ObjEX> i = platformList.iterator(); i.hasNext();) {
			ObjEX platform = i.next();
			ret = platform.clockTick();
			if (ret == Event.REMOVED) {
				i.remove();
				continue;
			}
			
			if(!platform.getEnabled()) continue;
			if(platform.getHitCheckObjType() == 0) continue;
			if(!platform.enableHitCheck()) continue;
			
			// 毎フレームチェックは重いのでインターバルで数フレームに一度のチェックにする
			if(!platform.checkInterval(intervalCount)) continue;
			
			platform.getCollisionRect(tmpRect);

			for (Obj o : objList) {
				int objType = 0;
				if( o == null){
					continue;
				}
				switch(o.getObjType()){
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
						if(o instanceof Food) objType = ObjEX.FOOD;
						else if(o instanceof Toilet) objType = ObjEX.TOILET;
						else if(o instanceof Toy) objType = ObjEX.TOY;
						else if(o instanceof Stalk) objType = ObjEX.STALK;
						else objType = ObjEX.OBJECT;
						break;
					case VOMIT:
						objType = ObjEX.VOMIT;
						break;
					default:
						break;
				}
				if ((objType & platform.getHitCheckObjType()) != 0){
					platform.checkHitObj(tmpRect, o);
				}
			}
		}		

		// コンベアの判定
		// 最前面のひとつだけに反応するのでターゲットを外ループに
		List<Beltconveyor> beltList = curMap.beltconveyor;
		objList = SimYukkuri.world.getHitTargetList();
		for (Obj o : objList) {
			if(beltList == null || beltList.size() == 0) break;
			if(o == null || o.isRemoved()) continue;
			if((Translate.getCurrentFieldMapNum(o.getX(), o.getY()) & FieldShapeBase.FIELD_BELT) == 0) continue;
			for (Iterator<Beltconveyor> i = beltList.iterator(); i.hasNext();) {
				Beltconveyor belt = i.next();
				ret = belt.clockTick();
			if (ret == Event.REMOVED) {
				i.remove();
				continue;
			}
				if(!belt.mapContains(o.getX(), o.getY())) continue;
				if(belt.checkHitObj(o)) {
					belt.processHitObj(o);
					break;
				}
			}
		}

		// プールの判定
		// 最前面のひとつだけに反応するのでターゲットを外ループに
		ArrayList<Pool> poolList = curMap.pool;
		objList = SimYukkuri.world.getHitTargetList();
		for (Obj o : objList) {
			if(poolList == null || poolList.size() == 0){
				// プール内から外に移動していた場合
				if( o.getInPool() ){
					o.setInPool(false);
					o.setMostDepth(0);
					o.setInPool(false);
					o.setFallingUnderGround(false);
					if(o.getZ() < 0 ){
						o.setZ(0);
					}
				}				
				continue;
			}
			if(o == null || o.isRemoved()) continue;
			if((Translate.getCurrentFieldMapNum(o.getX(), o.getY())  & FieldShapeBase.FIELD_POOL) == 0){
				// プール内から外に移動していた場合
				if( o.getInPool() ){
					if( o instanceof Body){
						((Body)o).setLockmove(false);
					}
					o.setInPool(false);
					o.setMostDepth(0);
					o.setInPool(false);
					o.setFallingUnderGround(false);
					if(o.getZ() < 0 ){
						o.setZ(0);
					}
				}
				continue;
			}
			//if((curMap.fieldMap[o.getX()][o.getY()] & FieldShapeBase.FIELD_BELT) == 0) continue;
			for (Iterator<Pool> i = poolList.iterator(); i.hasNext(); ){
				Pool pool = i.next();
				ret = pool.clockTick();
				if (ret == Event.REMOVED) {
					i.remove();
					continue;
				}
				if(pool.checkHitObj(o)) {
					pool.objHitProcess(o);
					break;
				}
			}
		}
		
		// 畑の判定
		// 最前面のひとつだけに反応するのでターゲットを外ループに
		ArrayList<Farm> farmList = curMap.farm;
		objList = SimYukkuri.world.getHitTargetList();
		
		for (Obj o : objList) {
			if(farmList == null || farmList.size() == 0){			
				break;
			}
			if(o == null || o.isRemoved()) continue;
			
			if((Translate.getCurrentFieldMapNum(o.getX(), o.getY())  & FieldShapeBase.FIELD_FARM) == 0)
			//if((curMap.fieldMap[(o.getX()<0)?0:(o.getX()>Translate.mapW?Translate.mapW:o.getX())][(o.getY()<0)?0:(o.getY()>Translate.mapH?Translate.mapH:o.getY())] & FieldShapeBase.FIELD_POOL) == 0)
			{
				continue;
			}
			//if((curMap.fieldMap[o.getX()][o.getY()] & FieldShapeBase.FIELD_BELT) == 0) continue;
			
			for (Iterator<Farm> i = farmList.iterator(); i.hasNext(); ){
				Farm farm = i.next();
				ret = farm.clockTick();
				if (ret == Event.REMOVED) {
					i.remove();
					continue;
				}
				if(farm.checkHitObj(o)) {
					farm.objHitProcess(o);
					break;
				}
			}
		}
/*
		List<Beltconveyor> beltList = curMap.beltconveyor;
		objList = SimYukkuri.world.getHitTargetList();
		
		for (Iterator<Beltconveyor> i = beltList.iterator(); i.hasNext();) {
			Beltconveyor belt = i.next();
			ret = belt.clockTick();
			if (ret == Obj.Event.REMOVED) {
				i.remove();
				continue;
			}

			for (Obj o : objList) {
				if(o.isRemoved()) continue;
				if((curMap.fieldMap[o.getX()][o.getY()] & FieldShapeBase.FIELD_BELT) == 0) continue;
				if(!belt.mapContains(o.getX(), o.getY())) continue;

				if(belt.checkHitObj(o)) {
					belt.processHitObj(o);
					continue;
				}
			}
		}
*/
		// オブジェクト更新
		List <ObjEX>objectList = SimYukkuri.world.getObjectList();
		resetTerrariumEnvironment();
		for (Iterator<ObjEX> i = objectList.iterator(); i.hasNext();) {
			ObjEX oex = i.next();
			ret = oex.clockTick();
			if (ret == Event.REMOVED) {
				i.remove();
			}
			//ディフューザーの更新
			if( oex.getObjEXType() == ObjEXType.DIFFUSER && oex.getEnabled()) {
				boolean[] flags = ((Diffuser)oex).getSteamType();
				if(flags[Diffuser.SteamType.ANTI_FUNGAL.ordinal()]) antifungalSteam = true;
				if(flags[Diffuser.SteamType.STEAM.ordinal()]) humid = true;
				if(flags[Diffuser.SteamType.ORANGE.ordinal()]) orangeSteam = true;
				if(flags[Diffuser.SteamType.AGE_BOOST.ordinal()]) ageBoostSteam = true;
				if(flags[Diffuser.SteamType.AGE_STOP.ordinal()]) ageStopSteam = true;
				if(flags[Diffuser.SteamType.ANTI_DOS.ordinal()]) antidosSteam = true;
				if(flags[Diffuser.SteamType.ANTI_YU.ordinal()]) poisonSteam = true;
				if(flags[Diffuser.SteamType.PREDATOR.ordinal()]) predatorSteam = true;
				if(flags[Diffuser.SteamType.SUGER.ordinal()]) sugerSteam = true;
				if(flags[Diffuser.SteamType.NOSLEEP.ordinal()]) noSleepSteam = true;
				if(flags[Diffuser.SteamType.HYBRID.ordinal()]) hybridSteam = true;
				if(flags[Diffuser.SteamType.RAPIDPREGNANT.ordinal()]) rapidPregnantSteam = true;
				if(flags[Diffuser.SteamType.ANTI_NONYUKKURI.ordinal()]) antiNonYukkuriDiseaseSteam = true;
				if(flags[Diffuser.SteamType.ENDLESS_FURIFURI.ordinal()]) endlessFurifuriSteam = true;
			}
		}

		// 畑更新
		
		// うんうん更新
		for (Iterator<Shit> i = curMap.shit.iterator(); i.hasNext();) {
			Shit s = i.next();
			ret = s.clockTick();
			if (ret == Event.REMOVED) {
				i.remove();
			}
		}
		
		// 吐餡更新
		for (Iterator<Vomit> i = curMap.vomit.iterator(); i.hasNext();) {
			Vomit v = i.next();
			ret = v.clockTick();
			if (ret == Event.REMOVED) {
				i.remove();
			}
		}
		
		// おかざり更新
		for (Iterator<Okazari> i = curMap.okazari.iterator(); i.hasNext();) {
			Okazari o = i.next();
			ret = o.clockTick();
			if (ret == Event.REMOVED) {
				i.remove();
			}
		}
		
		// ゆっくり更新
		boolean transCheck = (operationTime % 60 == 0);
		Body transBody = null;
		// ゆっくりの密度を環境ストレスとして算出
//		int stress = bodyList.size() * 10000 / (Terrarium.terrariumSizeParcent*Terrarium.terrariumSizeParcent);
		for (Iterator<Body> i = curMap.body.iterator(); i.hasNext();) {
			Body b = i.next();
//			b.putStress(stress); // Yukkuri is getting stress according as number of bodies.
			ret = b.clockTick();
			switch (ret) {
				case DEAD:
					if (b.isInfration()) {
						int burstPower =  (b.getSize() - b.getOriginSize())*3/4;
						for (Dna babyTypes: b.getBabyTypes()) {
							addBaby(b.getX(), b.getY(), b.getZ()+b.getSize()/20, curMap.rnd.nextInt(burstPower/4+1)-burstPower/8, curMap.rnd.nextInt(burstPower/4+1)-burstPower/8, curMap.rnd.nextInt(burstPower/5+1)-burstPower/10-1, babyTypes, b, b.getPartner());
						}
						b.getBabyTypes().clear();
						for ( Stalk s:b.getStalks() ){
							if ( s != null ) {
								s.kick(curMap.rnd.nextInt(burstPower/4+1)-burstPower/8, curMap.rnd.nextInt(burstPower/4+1)-burstPower/8, curMap.rnd.nextInt(burstPower/5+1)-burstPower/10-1);
							}
						}
						b.disPlantStalks();
						if ( b.getShit() > b.getSHITLIMIT()[b.getBodyAgeState().ordinal()] ){
							for ( int j = 0; b.getShit() / b.getSHITLIMIT()[b.getBodyAgeState().ordinal()] > j; j++ ){
								addShit(b.getX(), b.getY(), b.getZ()+b.getSize()/15, b, b.getShitType());
								curMap.shit.get(curMap.shit.size()-1).kick(curMap.rnd.nextInt(burstPower/4+1)-burstPower/8, curMap.rnd.nextInt(burstPower/4+1)-burstPower/8, curMap.rnd.nextInt(burstPower/5+1)-burstPower/10-1);
							}
						}
						b.setShit(0);
						if ( !b.isCrushed()) {
							b.strikeByPress();
						}
					}
					else if(b.isCrushed()) {
						b.disPlantStalks();
					}
					b.upDate();
	//				b.setForcePanicClear();
					continue;
				case BIRTHBABY:
					if ( b.getAge() % 10 == 0 ){
						if (!b.isHasPants()) {
							Dna babyType = b.getBabyTypesDequeue();
							if ( babyType != null ){
								addBaby(b.getX(), b.getY(), b.getZ()+b.getSize()/15, babyType, b, b.getPartner());
							}
						}
					}
					for ( Stalk s:b.getStalks() ){
						if ( s != null ) {
							for ( Body ba:s.getBindBaby() ){
								if ( ba != null ){
									ba.setUnBirth( false );
									ba.setDropShadow(true);
									ba.setBindStalk(null) ;
									// 赤ゆなら胎生妊娠と合わせるため年齢リセット
									if( ba.isBaby() )
									{
										ba.setAgeState(AgeState.BABY);
									}
									ba.kick(0,0,0);
								}
							}
							s.getBindBaby().clear();
							s.setPlantYukkuri( null );
							// 正常な出産時は茎をフード化
							int fx, fy;
							for(int f = 0; f < 5; f++) {
								fx = s.getX() - 6 + (f * 7);
								fy = s.getY() - 5 + curMap.rnd.nextInt(10);
								fx = Math.max(0, fx);
								fx = Math.min(fx, Translate.mapW);
								fy = Math.max(0, fy);
								fy = Math.min(fy, Translate.mapH);
								GadgetAction.putObjEX(Food.class, fx, fy, Food.FoodType.STALK.ordinal());
							}
							s.remove();
						}
					}
					b.getStalks().clear();
					break;
				case DOSHIT:					
					addShit(b.getX(), b.getY(), b.getZ()+b.getSize()/15, b, b.getShitType());
					curMap.shit.get(curMap.shit.size()-1).kick(0,1,1);
					break;
				case DOCRUSHEDSHIT:
					// 漏らした場合
					addCrushedShit(b.getX(), b.getY(), b.getZ(), b, b.getShitType());
					break;
				case DOVOMIT:
					addVomit(b.getX(), b.getY(), b.getZ(), b, b.getShitType());
					break;
				case REMOVED:
					i.remove();
					b.upDate();
					continue;
				default:
					break;
			}
			// 引火判定
			checkFire(b);
			// 石判定
			StoneLogic.checkPubble(b);
			// パニック時は別処理
			if(b.getPanicType() != null) {
				checkPanic(b);
			}
			else {
				// イベント処理
				if(b.getCurrentEvent() != null) {
					EventLogic.eventUpdate(b);
				}

				//　子供のリストに生きている子供がいるか
				boolean bHasChildren = false;
				ArrayList<Body>childrenList = BodyLogic.createActiveChildList(b, true);
				if( childrenList != null && childrenList.size() != 0){
					bHasChildren = true;
				}

				/*logic周り
				 * 
				 */
				boolean bCheck = true;
//				if( bCheck )
				if (b.getBlockedCount() == 0) bCheck = true;
				else bCheck = false;

				// 子供がいるなら家族イベントを最優先する
				if(bHasChildren){
					if(bCheck )
						if (FamilyActionLogic.checkFamilyAction(b)) bCheck = false;
						else bCheck = true;
				}

				// check Food
				if( bCheck ){
					if (FoodLogic.checkFood(b)) bCheck = false;
					else bCheck = true;
				}

				// check Sukkiri
				if( bCheck ){
					if (BodyLogic.checkPartner(b)) bCheck = false;
					else bCheck = true;
				}

				// check shit
				if( bCheck ){
					if (ToiletLogic.checkShit(b)) bCheck = false;
					else bCheck = true;
				}
				
				// check toilet
				if( bCheck ){
					if (ToiletLogic.checkToilet(b)) bCheck = false;
					else bCheck = true;
				}
				
				// check sleep
				if( bCheck ){
					if (BedLogic.checkBed(b)) bCheck = false;
					else bCheck = true;
				}
				
				if( !bHasChildren){
					// 子供がいないなら家族イベントの優先度は最低
					if( bCheck ){
						if (!FamilyActionLogic.checkFamilyAction(b)) bCheck = true;
						else bCheck = false;
					}
				}
				
			}

			if ( b.getStalkBabyTypes().size() > 0 ){
				int j = 0;
				Stalk currentStalk = null;
				for (Dna babyTypes: b.getStalkBabyTypes()) {
					if ( j % 5 == 0 ) {
						GadgetAction.putObjEX(Stalk.class, b.getX(), b.getY(), b.getDirection().ordinal());
						currentStalk = curMap.stalk.get(curMap.stalk.size() - 1);
						b.getStalks().add( currentStalk );
						currentStalk.setPlantYukkuri( b );
					}
					if ( babyTypes != null ) {
						addBaby(b.getX(), b.getY(), 0, babyTypes, b, b.getPartner(), currentStalk);
						babyList.get( babyList.size()-1 ).setBindStalk(currentStalk);
					}
					else{
						currentStalk.setBindBaby( null );
					}
					j++;
				}
				b.getStalkBabyTypes().clear();
			}
			b.upDate();	

			// 突然変異チェック
			// ループ内でリストをいじると例外が出るのでここでは候補の取り出しのみ
			if(transCheck && transBody == null) {
				transBody = b.checkTransform();
			}
		}
		// add babies.
		if (!babyList.isEmpty()) {
			curMap.body.addAll(babyList);
			babyList.clear();
		}

		// エフェクト
		for (Iterator<Effect> i = curMap.sortEffect.iterator(); i.hasNext();) {
			Effect ef = i.next();
			ret = ef.clockTick();
			if (ret == Event.REMOVED) {
				i.remove();
			}
		}
		for (Iterator<Effect> i = curMap.frontEffect.iterator(); i.hasNext();) {
			Effect ef = i.next();
			ret = ef.clockTick();
			if (ret == Event.REMOVED) {
				i.remove();
			}
		}
		// イベントリストの有効期間チェック
		EventLogic.clockWorldEvent();
		
		// 突然変異実行
		if(transBody != null) {
			transBody.execTransform();
		}
		operationTime += TICK;
	}

	// 家族の関係を設定
	public void setNewFamily(Body b, Body p, Body bodyNewChild)
	{
		if( b==null )
		{
			return;
		}
		ArrayList<Body> childrenListOld = b.getChildrenList();		//　子供のリスト
		ArrayList<Body> elderSisterListOld = b.getElderSisterList();	//　姉のリスト
		ArrayList<Body> sisterListOld = b.getSisterList();			//　妹のリスト	

		// 子供のリスト
		Iterator<Body> itr = childrenListOld.iterator();
		while(itr.hasNext()){
			Body child = itr.next();
			if( child == null ){
				continue;
			}
			// 子供に姉のリストに追加
			bodyNewChild.addElderSisterList(child);
			// 子供がいる場合は各子供の妹のリストに追加
			child.addSisterList(bodyNewChild);
		}

		// 子供をリストに追加
		b.addChildrenList(bodyNewChild);
		if( (p != null) && (p != b)){
			// つがいにも子供をリストに追加
			setNewFamily(p, null, bodyNewChild);
		}
	}	
}
