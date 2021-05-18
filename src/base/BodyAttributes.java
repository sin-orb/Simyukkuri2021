package src.base;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import src.Const;
import src.SimYukkuri;
import src.attachment.Ants;
import src.enums.AgeState;
import src.enums.Attitude;
import src.enums.BaryInUGState;
import src.enums.BodyBake;
import src.enums.BodyRank;
import src.enums.Burst;
import src.enums.CoreAnkoState;
import src.enums.CriticalDamegeType;
import src.enums.Damage;
import src.enums.Direction;
import src.enums.Event;
import src.enums.FavItemType;
import src.enums.FootBake;
import src.enums.HairState;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.enums.LovePlayer;
import src.enums.Pain;
import src.enums.PanicType;
import src.enums.Parent;
import src.enums.PlayStyle;
import src.enums.PredatorType;
import src.enums.PublicRank;
import src.enums.PurposeOfMoving;
import src.enums.TakeoutItemType;
import src.enums.TangType;
import src.enums.Trauma;
import src.enums.YukkuriType;
import src.game.Dna;
import src.game.Stalk;
import src.system.BasicStrokeEX;
import src.system.BodyLayer;
import src.system.Sprite;

/**
 * ゆっくり本体の抽象クラスの属性/状態の取得を抜き出したクラス.
 * 属性を増やしたらYukkuriUtil.NOCOPY_FIELDでコピーしたくない属性であれば定義する。
 * コピーしたくない属性かどうかは、れいむ→でいぶ、まりさ→ドスまりさとなったときに
 * もととなるゆっくりから変異後のゆっくりにコピーしたい属性かどうかで決める。
 * 例えば、子供リスト等はコピーしたいが、自分の呼称やあんこ量等はコピーしたくない。
 * （コピーしてもその後のINIファイル取得で上書きされるものもある）
 * そこはいわゆる”ゆ虐の設定”に従うこと。
 */
public abstract class BodyAttributes extends Obj implements Serializable {
	/** ランダムのもと */
	public static final Random RND = new Random();

	/** ゆっくりのタイプ。まりさなら0、れいむなら1、等々ユニークなタイプを表す。 */
	public abstract int getType();

	/** ゆっくりの日本語名称を返却する */
	public abstract String getNameJ();

	/** ゆっくりの英語名称を返却する */
	public abstract String getNameE();

	/** ハイブリッドなゆっくりの2番めの日本語名称を返却する */
	public abstract String getNameJ2();

	/** ハイブリッドなゆっくりの2番めの英語名称を返却する */
	public abstract String getNameE2();

	/** ゆっくりが自分をどう呼称するかを返却する */
	public abstract String getMyName();

	/** ゆっくりがダメージを負っているとき自分をどう呼称するかを返却する */
	public abstract String getMyNameD();

	/** ゆっくりのタイプ、向き、レイヤー、indexからイメージ画像を取得する */
	public abstract int getImage(int type, int direction, BodyLayer layer, int index);

	/** 各ゆっくりのパラメータを作成時に調整する */
	public abstract void tuneParameters();

	/** 画像がロード済みかどうかを返却する */
	public abstract boolean isImageLoaded();

	/** 各ゆっくり用iniファイルからマウントポイントを取得する */
	public abstract Point[] getMountPoint(String key);

	/**
	 * 非ゆっくり症のチェックメソッド.
	 * Bodyでのオーバーライド.
	 * @return 非ゆっくり症かどうか
	 */
	public abstract int checkNonYukkuriDiseaseTolerance();

	// public variables
	/** 各ゆっくりに特有の画像読み込みのためのファイル名 */
	private String baseBodyFileName;
	/** 赤ゆの一人称 */
	private String[] anBabyName;
	/** 子ゆの一人称 */
	private String[] anChildName;
	/** 大人ゆの一人称 */
	private String[] anAdultName;
	/** [0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称 */
	protected String[] anMyName = new String[3];
	/** 赤ゆの一人称（ダメージ時） */
	private String[] anBabyNameD;
	/** 子ゆの一人称（ダメージ時） */
	private String[] anChildNameD;
	/** 大人ゆの一人称（ダメージ時） */
	private String[] anAdultNameD;
	/** ダメージ時の、[0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称 */
	protected String[] anMyNameD = new String[3];

	/**移動目的*/
	protected PurposeOfMoving purposeOfMoving = PurposeOfMoving.NONE;

	// Used in image loading.
	private static BufferedImage[] shadowImages = new BufferedImage[3];
	/**影画像のサイズ定義*/
	protected static int[] shadowImgW = new int[3], shadowImgH = new int[3];
	/**影画像の中心定義*/
	protected static int[] shadowPivX = new int[3], shadowPivY = new int[3];
	/**本体のスプライト定義*/
	protected Sprite[] bodySpr = new Sprite[3];
	/**拡幅分のスプライト定義*/
	protected Sprite[] expandSpr = new Sprite[3];
	/**おさげのスプライト定義*/
	protected Sprite[] braidSpr = new Sprite[3];

	// .INIファイルで変更可能な各ゆっくりのパラメータ.
	/** 一回の食事量 */
	protected int EATAMOUNT[] = { 100 * 6, 100 * 12, 100 * 24 };
	/** 体重 */
	protected int WEIGHT[] = { 100, 300, 600 };
	/** 空腹限界 */
	protected int HUNGRYLIMIT[] = { 100 * 24, 100 * 24 * 2, 100 * 24 * 4 };
	/** うんうん限界 */
	protected int SHITLIMIT[] = { 100 * 12, 100 * 24, 100 * 24 };
	/** ダメージ限界 */
	protected int DAMAGELIMIT[] = { 100 * 24, 100 * 24 * 3, 100 * 24 * 7 };
	/** ストレス限界 */
	private int STRESSLIMIT[] = { 100 * 24, 100 * 24 * 3, 100 * 24 * 7 };
	/** なつき度限界 */
	private int LOVEPLAYERLIMIT = 1000;
	/** 味覚レベル */
	private int TANGLEVEL[] = { 300, 600, 1000 };
	/** 赤ゆ期間 */
	protected int BABYLIMIT = 100 * 24 * 7;
	/** 子ゆ期間 */
	protected int CHILDLIMIT = 100 * 24 * 21;
	/** 寿命 */
	protected int LIFELIMIT = 100 * 24 * 365;
	/** 腐敗日数 */
	private int ROTTINGTIME = 100 * 24 * 3;
	/** 足の速さ */
	private int STEP[] = { 1, 2, 4 };
	/** リラックス状態の期間 */
	protected int RELAXPERIOD = 100 * 1;
	/** 発情状態の期間 */
	protected int EXCITEPERIOD = 100 * 3;
	/** 妊娠期間 */
	protected int PREGPERIOD = 100 * 24;
	/** 睡眠時間 */
	protected int SLEEPPERIOD = 100 * 3;
	/** アクティブな期間 */
	protected int ACTIVEPERIOD = 100 * 6;
	/** 怒り期間 */
	private int ANGRYPERIOD = 100 * 1;
	/** 恐怖期間 */
	private int SCAREPERIOD = 100 * 1;
	/** 同一方向に動き続ける */
	protected int sameDest = 30;
	/** ゲーム内12分、衝動の抑制のための変数 */
	protected int DECLINEPERIOD = 20;
	/** 壁等にブロックされた回数の限界（怒りだす等） */
	private int BLOCKEDLIMIT = 60;
	/** 汚れ限界（超えるとゆかび状態） */
	private int DIRTYPERIOD = 300;
	/** 視界の広さ */
	protected int EYESIGHT = 4000 * 4000;
	/** 赤ゆ、子ゆ、成ゆの攻撃力 */
	protected int STRENGTH[] = { 500, 1000, 3000 };
	/** ゆかびの潜伏期間 */
	protected int INCUBATIONPERIOD = 100 * 12;
	/** 攻撃された際のぴこぴこ破壊確率。0だと破壊されない */
	private int nBreakBraidRand = 0;
	/** 何回のうち1回の確率ですりすり事故で妊娠するかの値 */
	private int SurisuriAccidentProb = 200;
	/** 何回のうち1回の確率で路上で車に轢かれるかの値 */
	private int CarAccidentProb = 10000;
	/** 何回のうち1回の確率であんよが傷ついているとあんよが破壊されるかの確率 */
	private int BreakBodyByShitProb = 100;
	/** 何回のうち1回の確率で苦いフードを食べた際にゆ下痢になるかの確率 */
	private int diarrheaProb = 5;
	/** 何回のうち１回の確率で発情するかの確率 */
	private int exciteProb = 1;
	/** 固有の免疫力（個体値。これは仮） */
	protected int ROBUSTNESS = 1;
	/** 免疫力(左から順に赤ゆ、子ゆ、成ゆ、老ゆ) */
	private int immunity[] = { 1, 2, 3, 0 };
	/** 性格変化の切り替え */
	private boolean notChangeCharacter = false;
	/** ゲスポイント */
	protected int AttitudePoint = 0;
	/** ゲス限界 */
	private int RudeLimit[] = { -100, -250 };
	/** 善良限界 */
	private int NiceLimit[] = { 100, 500 };
	/** 妊娠限界 */
	protected int PregnantLimit = 1000;
	/** よりリアルな妊娠限界 */
	private boolean realPregnantLimit = true;

	// individual state variables for each Yukkuri.
	/** 画像がまりちゃ流しか */
	private boolean bImageNagasiMode = false;
	/** 飼いゆ、野良ゆなどのランク */
	protected BodyRank bodyRank = BodyRank.KAIYU;
	/** 群れ内のうんうん奴隷などのランク */
	private PublicRank publicRank = PublicRank.NONE;
	/** 移動先目標 destination */
	protected int destX = -1;
	protected int destY = -1;
	protected int destZ = -1;
	/** 移動量 how many steps to same direction */
	protected int countX = 0;
	protected int countY = 0;
	protected int countZ = 0;
	/** 移動方向 direction to move on */
	protected int dirX = 0;
	protected int dirY = 0;
	protected int dirZ = 0;
	/** 顔の向き direction of face */
	private Direction direction = Direction.LEFT;
	/** 蓄積ダメージ counter indicating damage */
	protected int damage = 0;
	/** 蓄積ストレス */
	protected int stress = 0;
	/** 舌の肥え */
	private int tang = 500;
	/** ダメージ外観 */
	@SuppressWarnings("unused")
	private Damage damageState = Damage.NONE;
	/** 空腹度 counter indicating how hungry */
	protected int hungry;
	/** 性格 counter indicating shithead/nicehead etc. */
	private Attitude attitude = Attitude.AVERAGE;
	/** 知性 */
	private Intelligence intelligence = Intelligence.AVERAGE;
	/** 幸福度 */
	private Happiness happiness = Happiness.AVERAGE;
	/** プレイヤーへのなつき度 */
	protected int nLovePlayer = 0;
	/** プレイヤーへのなつき度概算 */
	private LovePlayer eLovePlayerState = LovePlayer.NONE;
	/** 髪の状態 */
	private HairState eHairState = HairState.DEFAULT;
	/** うんうんの溜まり具合 */
	protected int shit = 0;
	/** 思い出（悪夢関連） */
	protected int memories = 0;
	/** トラウマ */
	private Trauma trauma = Trauma.NONE;
	/** おかざり種別 */
	private Okazari okazari = null;
	/** 体の前後のどこにお飾りを持っているか(0は両方、1は前方のみ、2は後方のみ) */
	private int okazariPosition = 0;
	/** おさげ、羽、尻尾有無 種族として何も持っていないものはtrue */
	private boolean hasBraid = true;
	/** おくるみ有無 true if having pants */
	private boolean hasPants = false;
	/** 胎生妊娠有無 having baby or not */
	private boolean hasBaby = false;
	/** 茎妊娠有無 having baby or not */
	private boolean hasStalk = false;
	/** あにゃるふさぎ有無 */
	protected boolean analClose = false;
	/** 胎生去勢有無 */
	protected boolean bodyCastration = false;
	/** 茎去勢有無 */
	protected boolean stalkCastration = false;
	/** ぺにぺにの去勢有無 */
	private boolean bPenipeniCutted = false;
	/** フェロモンの有無 */
	private boolean bPheromone = false;
	/** 胎生ゆのリスト */
	private List<Dna> babyTypes = new LinkedList<Dna>();
	/** 実ゆのリスト */
	private List<Dna> stalkBabyTypes = new LinkedList<Dna>();
	/** 茎のリスト */
	private List<Stalk> stalks = new LinkedList<Stalk>();
	/** 自分がぶらさがっている茎 */
	private Stalk bindStalk = null;
	/** 死亡フラグdead of alive */
	private boolean dead = false;
	/** うまれて初めての地面か */
	private boolean bFirstGround = true;
	/** うまれて初めての食事か */
	private boolean bFirstEatStalk = true;
	/** 死体が損壊されているか */
	private boolean crushed = false;
	/** 死体が焼損されているか */
	private boolean burned = false;
	/** 中枢餡の状態（非ゆっくり症フラグ */
	private CoreAnkoState eCoreAnkoState = CoreAnkoState.DEFAULT;
	/** 発情フラグ want to sukkiri or not */
	private boolean exciting = false;
	/** 強制発情フラグ want to sukkiri or not */
	private boolean bForceExciting = false;
	/** ゆっくりしてるかどうか */
	private boolean relax = false;
	/** 睡眠中かどうか */
	private boolean sleeping = false;
	/** 悪夢を見るかどうか */
	private boolean nightmare = false;
	/** 前回起きた時間 */
	protected long wakeUpTime;
	/** 汚れ有無 */
	protected boolean dirty = false;
	/** 頑固な汚れ有無 */
	private boolean stubbornlyDirty = false;
	/** 針の有無 */
	private boolean bNeedled = false;
	/** レイパー化有無 */
	private boolean rapist = false;
	/** バイゆグラでレイパーになる、すーぱーれいぱー状態 */
	private boolean superRapist = false;
	/** 濡れ状態 */
	private boolean wet = false;
	/** 水に溶けた状態 */
	private boolean melt = false;
	/** 皮をむいた状態 */
	private boolean pealed = false;
	/** 饅頭にされた状態 */
	private boolean packed = false;
	/** アマギられた状態 */
	private boolean blind = false;
	/** おかざりがなくなっていることに気がついているか */
	private boolean bNoticeNoOkazari = false;
	/** パニック種別 */
	private PanicType panicType = null;
	/** 致命傷種別 */
	private CriticalDamegeType criticalDamege = null;
	/** つがい */
	private Body partner = null;
	/** 親 */
	private Body parents[] = { null, null };
	/** 子供のリスト */
	private List<Body> childrenList = new LinkedList<Body>();
	/** 姉のリスト */
	private List<Body> elderSisterList = new LinkedList<Body>();
	/** 妹のリスト */
	private List<Body> sisterList = new LinkedList<Body>();
	/** 先祖のリスト */
	private List<Integer> ancestorList = new LinkedList<Integer>();
	/** 自分がレイプでできた子か */
	private boolean fatherRaper = false;
	/** うんうん抑制 */
	protected int shittingDiscipline = 0;
	/** 興奮抑制 */
	protected int excitingDiscipline = 0;
	/** ふりふり抑制 */
	protected int furifuriDiscipline = 0;
	/** おしゃべり抑制 */
	protected int messageDiscipline = 0;
	/** あまあまへの慣れ具合 */
	protected int amaamaDiscipline = 0;
	/** 自身の持っているアタッチメント */
	private List<Attachment> attach = new LinkedList<Attachment>();
	/** なにかのオブジェクト（すぃー、親ゆなど）に載せられている等のリンクが有る際のそのオブジェクト */
	private Obj linkParent = null;
	/** 移動不可ベルトコンベアの有無 */
	private boolean bOnDontMoveBeltconveyor = false;
	/** 埋まり状態 */
	private BaryInUGState baryState = BaryInUGState.NONE;
	/** 希少種か */
	private boolean rareType = false;
	/** 苦いえさが好きか */
	private boolean likeBitterFood = false;
	/** 辛いえさが好きか */
	private boolean likeHotFood = false;
	/** 水が平気か */
	private boolean likeWater = false;
	/** 空を飛ぶか */
	private boolean flyingType = false;
	/** 種族としてお下げ、羽、尻尾を持つか */
	private boolean braidType = true;
	/** 捕食種タイプ */
	private PredatorType predatorType = null;
	/** 動けないかどうか */
	private boolean lockmove = false;
	/** ひっぱり、押しつぶし可能か */
	private boolean pullAndPush = false;
	/** 動けない期間（押さえられてる等で） */
	protected int lockmovePeriod = 0;
	/** 外圧 */
	protected int extForce = 0;
	/** まばたき、同じ表情の時にカウント */
	private int mabatakiCnt = 0;
	/** まばたき、表情の値を代入 */
	private int mabatakiType = 0;
	/** プレイヤーにすりすりされているか */
	private boolean bSurisuriFromPlayer = false;
	/** ぷるぷる震えているか */
	private boolean bPurupuru = false;
	/** 粘着板で背中を固定されている */
	private boolean fixBack = false;
	/** ダメージを受けていない期間 */
	protected int noDamagePeriod = 0;
	/** 飢餓状態になっていない期間 */
	protected int noHungryPeriod = 0;
	/** スーパーむーしゃむーしゃタイムのおかげで飢餓状態にならない期間 */
	protected int noHungrybySupereatingTimePeriod = 0;
	/** 妊娠期間 */
	protected int pregnantPeriod = 0;
	/** 発情期間 */
	protected int excitingPeriod = 0;
	/** 睡眠期間 */
	protected int sleepingPeriod = 0;
	/** 汚れている期間 */
	protected int dirtyPeriod = 0;
	/** 汚れて泣き叫ぶ期間 */
	protected int dirtyScreamPeriod = 0;
	/** ゆかびに侵されている期間 */
	protected int sickPeriod = 0;
	/** 怒っている期間 */
	protected int angryPeriod = 0;
	/** 怖がっている期間 */
	protected int scarePeriod = 0;
	/** 悲しんでいる期間 */
	protected int sadPeriod = 0;
	/** 濡れている期間 */
	protected int wetPeriod = 0;
	/** パニック状態の期間 */
	protected int panicPeriod = 0;
	/** 足焼きされている期間 */
	protected int footBakePeriod = 0;
	/** 焼かれている期間 */
	protected int bodyBakePeriod = 0;
	/** 非ゆっくり症にかかっている期間 */
	protected int nonYukkuriDiseasePeriod = 0;
	/** 死んでからの期間 */
	private int deadPeriod = 0;
	/** 最後にプレイヤーにすりすりしてもらった時間 */
	protected long lnLastTimeSurisuri = 0;
	/** 最後にプレイヤーがアクションを行った時間 */
	private long inLastActionTime = 0;
	/** 出産期間のブースト（この分だけ早まる） */
	protected int pregnantPeriodBoost = 0;
	/** 発情期間のブースト（この分だけ早まる） */
	private int excitingPeriodBoost = 0;
	/** うんうんブースト */
	protected int shitBoost = 0;
	/** 移動対象（移動先） */
	protected Obj moveTarget = null;
	/** 移動対象のX座標オフセット */
	private int targetPosOfsX = 0;
	/** 移動対象のY座標オフセット */
	private int targetPosOfsY = 0;
	/** 対象を呼び止めるほど強い動機を持っているかどうか */
	private boolean targetBind = false;
	/** アイテムを出し入れする動作フラグ */
	private boolean inOutTakeoutItem = false;
	/** 待機アクション中 */
	private boolean staying = false;
	/** うんうんアクション中 */
	private boolean shitting = false;
	/** 誕生済みか否か */
	private boolean birth = false;
	/** 怒っているか否か */
	private boolean angry = false;
	/** ふりふりアクション中 */
	private boolean furifuri = false;
	/** 攻撃アクション中 */
	private boolean strike = false;
	/** 何かを食べ中 */
	private boolean eating = false;
	/** ぺろぺろ中 */
	private boolean peropero = false;
	/** すっきり中 */
	private boolean sukkiri = false;
	/** 怖がり中 */
	private boolean scare = false;
	/** うんうんを食べ中 */
	private boolean eatingShit = false;
	/** 沈黙フラグ */
	private boolean silent = false;
	/** 口ふさがれ中 */
	private boolean shutmouth = false;
	/** のびのび中 */
	private boolean nobinobi = false;
	/** キリッ！中 */
	private boolean beVain = false;
	/** ぴこぴこ中 */
	private boolean pikopiko = false;
	/** ぷるぷる中 */
	private boolean purupuru = false;
	/** 親を呼んで泣き叫び中 */
	private boolean callingParents = false;
	/** ゆんやぁ中 */
	private boolean yunnyaa = false;
	/** 命乞い中 */
	private boolean begging = false;
	/** 何で遊んでいるか */
	private PlayStyle playing = null;
	/** 遊び時間上限 */
	protected int playingLimit = 0;
	/** メッセージのバッファ */
	private String messageBuf;
	/** いくつメッセージが溜まってるか */
	protected int messageCount = 0;
	/** その場に留まってる回数 */
	private int staycount = 0;
	/** とどまる限界 */
	protected int stayTime = Const.STAYLIMIT;
	/** 落下ダメージ */
	protected int falldownDamage = 0;
	/** あんこ量 */
	private int bodyAmount = 0;
	/** 壁に引っかかった回数 */
	private int blockedCount = 0;
	/** 死なない期間 */
	protected int cantDiePeriod = 0;
	/** 実ゆかどうか */
	protected boolean unBirth = false;
	/** 喋れる状態かどうか */
	private boolean canTalk = true;
	/** メッセージラインの色 */
	private Color messageLineColor;
	/** メッセージボックスの色 */
	private Color messageBoxColor;
	/** メッセージテキストの色 */
	private Color messageTextColor;
	// TODO:使途不明
	private BasicStrokeEX messageWindowStroke;
	/** メッセージテキストのサイズ */
	private int messageTextSize;
	/** 強制的に誕生時メッセージを言わされるかどうか */
	private boolean forceBirthMessage = false;
	/** ゆっくりのオブジェクトのユニークID*/
	private int uniqueID = 0;
	/** どのゆっくり的なメッセージを言うか */
	private YukkuriType msgType = null;
	/** どのゆっくり的なうんうんをするか */
	private YukkuriType shitType = null;
	/** 右ペインメニューのピン留めをされているかどうか */
	private boolean pin = false;
	/** ゆっくりの移動速度 */
	protected int speed = 100;
	/** 次の落下でダメージを受けないかどうか */
	private boolean bNoDamageNextFall = false;
	/** この個体に対して発行されたイベントのリスト */
	private List<EventPacket> eventList = new LinkedList<EventPacket>();
	/** 現在実行中のイベント */
	private EventPacket currentEvent = null;
	/** 表情の強制設定 */
	protected int forceFace = -1;
	/** 影の表示有無 */
	private boolean dropShadow = true;
	/** イベントで設定されたアクション */
	private Event eventResultAction = Event.DONOTHING;
	/** ゆ虐神拳により 膨らんでいるか/伸ばされているか/押さえつけられているか */
	private boolean[] abFlagGodHand = { false, false, false };
	/** TODO:ゆ虐神拳の回数？ */
	private int[] anGodHandPoint = { 0, 0, 0 };
	/** お気に入りアイテム */
	private HashMap<FavItemType, Obj> favItem = new HashMap<FavItemType, Obj>();
	/** 持ち歩きアイテム */
	private HashMap<TakeoutItemType, Obj> takeoutItem = new HashMap<TakeoutItemType, Obj>();
	/** ゆっくり本体の購入基本額 */
	private int Ycost = 200;
	/** ゆっくり本体、中身の売却基本額　飼いゆとしての価値/加工品としての価値 */
	private int saleValue[] = { 50, 100 };
	/** たかっているアリの数 */
	protected int numOfAnts = 0;
	/** うにょ機能を使用するかどうかのフラグ */
	private int unyoFlg = 1;
	/** うにょの高さ方向 */
	protected int unyoForceH = 0;
	/** うにょの横方向 */
	protected int unyoForceW = 0;
	/** うにょの動きの強さ */
	public final static int UNYOSTRENGTH[] = { 4, 7, 10 };

	/**
	 * 各ゆっくりに特有の画像読み込みのためのファイル名を取得する.
	 * @return 各ゆっくりに特有の画像読み込みのためのファイル名
	 */
	public String getBaseBodyFileName() {
		return baseBodyFileName;
	}

	/**
	 * 各ゆっくりに特有の画像読み込みのためのファイル名を設定する.
	 * @param baseBodyFileName 各ゆっくりに特有の画像読み込みのためのファイル名
	 */
	public void setBaseBodyFileName(String baseBodyFileName) {
		this.baseBodyFileName = baseBodyFileName;
	}

	/**
	 * 赤ゆの一人称を取得する.
	 * @return 赤ゆの一人称
	 */
	public String[] getAnBabyName() {
		return anBabyName;
	}

	/**
	 * 赤ゆの一人称を設定する.
	 * @param anBabyName 赤ゆの一人称
	 */
	public void setAnBabyName(String[] anBabyName) {
		this.anBabyName = anBabyName;
	}

	/**
	 * 子ゆの一人称を取得する.
	 * @return 子ゆの一人称
	 */
	public String[] getAnChildName() {
		return anChildName;
	}

	/**
	 * 子ゆの一人称を設定する.
	 * @param anChildName 子ゆの一人称
	 */
	public void setAnChildName(String[] anChildName) {
		this.anChildName = anChildName;
	}

	/**
	 * 成ゆの一人称を取得する.
	 * @return 成ゆの一人称
	 */
	public String[] getAnAdultName() {
		return anAdultName;
	}

	/**
	 * 成ゆの一人称を設定する.
	 * @param anAdultName 成ゆの一人称
	 */
	public void setAnAdultName(String[] anAdultName) {
		this.anAdultName = anAdultName;
	}

	/**
	 * [0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称 を取得する.
	 * @return [0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称 
	 */
	public String[] getAnMyName() {
		return anMyName;
	}

	/**
	 * [0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称 を設定する.
	 * @param anMyName [0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称 
	 */
	public void setAnMyName(String[] anMyName) {
		this.anMyName = anMyName;
	}

	/**
	 * 赤ゆの一人称（ダメージ時）を返却する.
	 * @return 赤ゆの一人称（ダメージ時）
	 */
	public String[] getAnBabyNameD() {
		return anBabyNameD;
	}

	/**
	 * 赤ゆの一人称（ダメージ時）を設定する.
	 * @param anBabyNameD 赤ゆの一人称（ダメージ時）
	 */
	public void setAnBabyNameD(String[] anBabyNameD) {
		this.anBabyNameD = anBabyNameD;
	}

	/**
	 * 子ゆの一人称（ダメージ時）を取得する.
	 * @return 子ゆの一人称（ダメージ時）
	 */
	public String[] getAnChildNameD() {
		return anChildNameD;
	}

	/**
	 * 子ゆの一人称（ダメージ時）を設定する.
	 * @param anChildNameD 子ゆの一人称（ダメージ時）
	 */
	public void setAnChildNameD(String[] anChildNameD) {
		this.anChildNameD = anChildNameD;
	}

	/**
	 * 大人ゆの一人称（ダメージ時）を取得する.
	 * @return 大人ゆの一人称（ダメージ時）
	 */
	public String[] getAnAdultNameD() {
		return anAdultNameD;
	}

	/**
	 * 大人ゆの一人称（ダメージ時） を設定する.
	 * @param anAdultNameD 大人ゆの一人称（ダメージ時）
	 */
	public void setAnAdultNameD(String[] anAdultNameD) {
		this.anAdultNameD = anAdultNameD;
	}

	/**
	 * ダメージ時の、[0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称を取得する.
	 * @return ダメージ時の、[0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称
	 */
	public String[] getAnMyNameD() {
		return anMyNameD;
	}

	/**
	 * ダメージ時の、[0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称 を設定する.
	 * @param anMyNameD ダメージ時の、[0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称
	 */
	public void setAnMyNameD(String[] anMyNameD) {
		this.anMyNameD = anMyNameD;
	}

	public static BufferedImage[] getShadowImages() {
		return shadowImages;
	}

	public static void setShadowImages(BufferedImage[] shadowImages) {
		BodyAttributes.shadowImages = shadowImages;
	}

	/**
	 * 影画像のサイズ定義を取得する.
	 * @return 影画像のサイズ定義
	 */
	public static int[] getShadowImgW() {
		return shadowImgW;
	}

	/**
	 * 影画像のサイズ定義を設定する.
	 * @param shadowImgW 影画像のサイズ定義
	 */
	public static void setShadowImgW(int[] shadowImgW) {
		BodyAttributes.shadowImgW = shadowImgW;
	}

	public static int[] getShadowImgH() {
		return shadowImgH;
	}

	public static void setShadowImgH(int[] shadowImgH) {
		BodyAttributes.shadowImgH = shadowImgH;
	}

	/**
	 * 影画像の中心定義を取得する.
	 * @return 影画像の中心定義
	 */
	public static int[] getShadowPivX() {
		return shadowPivX;
	}

	/**
	 * 影画像の中心定義を設定する.
	 * @param shadowPivX 影画像の中心定義
	 */
	public static void setShadowPivX(int[] shadowPivX) {
		BodyAttributes.shadowPivX = shadowPivX;
	}

	public static int[] getShadowPivY() {
		return shadowPivY;
	}

	public static void setShadowPivY(int[] shadowPivY) {
		BodyAttributes.shadowPivY = shadowPivY;
	}

	/**
	 * 本体のスプライト定義を取得する.
	 * @return 本体のスプライト定義
	 */
	public Sprite[] getBodySpr() {
		return bodySpr;
	}

	/**
	 * 本体のスプライト定義を設定する.
	 * @param bodySpr 本体のスプライト定義
	 */
	public void setBodySpr(Sprite[] bodySpr) {
		this.bodySpr = bodySpr;
	}

	/**
	 * 拡幅分のスプライト定義を取得する.
	 * @return 拡幅分のスプライト定義
	 */
	public Sprite[] getExpandSpr() {
		return expandSpr;
	}

	/**
	 * 拡幅分のスプライト定義を設定する.
	 * @param expandSpr 拡幅分のスプライト定義
	 */
	public void setExpandSpr(Sprite[] expandSpr) {
		this.expandSpr = expandSpr;
	}

	/**
	 * おさげのスプライト定義を取得する.
	 * @return おさげのスプライト定義
	 */
	public Sprite[] getBraidSpr() {
		return braidSpr;
	}

	/**
	 * おさげのスプライト定義を設定する.
	 * @param braidSpr おさげのスプライト定義
	 */
	public void setBraidSpr(Sprite[] braidSpr) {
		this.braidSpr = braidSpr;
	}

	/**
	 * 一回の食事量 を取得する.
	 * @return 一回の食事量
	 */
	public int[] getEATAMOUNT() {
		return EATAMOUNT;
	}

	/**
	 * 一回の食事量 を設定する.
	 * @param eATAMOUNT 一回の食事量 
	 */
	public void setEATAMOUNT(int[] eATAMOUNT) {
		EATAMOUNT = eATAMOUNT;
	}

	/**
	 * 体重を取得する.
	 * @return 体重
	 */
	public int[] getWEIGHT() {
		return WEIGHT;
	}

	/**
	 * 体重を設定する.
	 * @param wEIGHT 体重
	 */
	public void setWEIGHT(int[] wEIGHT) {
		WEIGHT = wEIGHT;
	}

	/**
	 * 空腹限界を取得する.
	 * @return 空腹限界
	 */
	public int[] getHUNGRYLIMIT() {
		return HUNGRYLIMIT;
	}

	/**
	 * 空腹限界を設定する.
	 * @param hUNGRYLIMIT 空腹限界
	 */
	public void setHUNGRYLIMIT(int[] hUNGRYLIMIT) {
		HUNGRYLIMIT = hUNGRYLIMIT;
	}

	/**
	 * うんうん限界を取得する.
	 * @return うんうん限界
	 */
	public int[] getSHITLIMIT() {
		return SHITLIMIT;
	}

	/**
	 * うんうん限界を設定する.
	 * @param sHITLIMIT うんうん限界
	 */
	public void setSHITLIMIT(int[] sHITLIMIT) {
		SHITLIMIT = sHITLIMIT;
	}

	/**
	 * ダメージ限界を取得する.
	 * @return ダメージ限界
	 */
	public int[] getDAMAGELIMIT() {
		return DAMAGELIMIT;
	}

	/**
	 * ダメージ限界を設定する.
	 * @param dAMAGELIMIT ダメージ限界
	 */
	public void setDAMAGELIMIT(int[] dAMAGELIMIT) {
		DAMAGELIMIT = dAMAGELIMIT;
	}

	/**
	 * ストレス限界を取得する.
	 * @return ストレス限界
	 */
	public int[] getSTRESSLIMIT() {
		return STRESSLIMIT;
	}

	/**
	 * ストレス限界を設定する.
	 * @param sTRESSLIMIT ストレス限界
	 */
	public void setSTRESSLIMIT(int[] sTRESSLIMIT) {
		STRESSLIMIT = sTRESSLIMIT;
	}

	/**
	 * なつき度限界を取得する.
	 * @return なつき度限界
	 */
	public int getLOVEPLAYERLIMIT() {
		return LOVEPLAYERLIMIT;
	}

	/**
	 * なつき度限界を設定する. 
	 * @param lOVEPLAYERLIMIT なつき度限界 
	 */
	public void setLOVEPLAYERLIMIT(int lOVEPLAYERLIMIT) {
		LOVEPLAYERLIMIT = lOVEPLAYERLIMIT;
	}

	/**
	 * 味覚レベル を取得する.
	 * @return 味覚レベル 
	 */
	public int[] getTANGLEVEL() {
		return TANGLEVEL;
	}

	/**
	 * 味覚レベル を設定する.
	 * @param tANGLEVEL 味覚レベル
	 */
	public void setTANGLEVEL(int[] tANGLEVEL) {
		TANGLEVEL = tANGLEVEL;
	}

	/**
	 * 赤ゆ期間 を取得する.
	 * @return 赤ゆ期間
	 */
	public int getBABYLIMIT() {
		return BABYLIMIT;
	}

	/**
	 * 赤ゆ期間 を設定する.
	 * @param bABYLIMIT 赤ゆ期間 
	 */
	public void setBABYLIMIT(int bABYLIMIT) {
		BABYLIMIT = bABYLIMIT;
	}

	/**
	 * 子ゆ期間 を取得する.
	 * @return 子ゆ期間 
	 */
	public int getCHILDLIMIT() {
		return CHILDLIMIT;
	}

	/**
	 * 子ゆ期間 を設定する.
	 * @param cHILDLIMIT 子ゆ期間 
	 */
	public void setCHILDLIMIT(int cHILDLIMIT) {
		CHILDLIMIT = cHILDLIMIT;
	}

	/**
	 * 寿命を取得する.
	 * @return 寿命
	 */
	public int getLIFELIMIT() {
		return LIFELIMIT;
	}

	/**
	 * 寿命を設定する.
	 * @param lIFELIMIT 寿命
	 */
	public void setLIFELIMIT(int lIFELIMIT) {
		LIFELIMIT = lIFELIMIT;
	}

	/**
	 * 腐敗日数 を取得する.
	 * @return 腐敗日数
	 */
	public int getROTTINGTIME() {
		return ROTTINGTIME;
	}

	/**
	 * 腐敗日数 を設定する.
	 * @param rOTTINGTIME 腐敗日数 
	 */
	public void setROTTINGTIME(int rOTTINGTIME) {
		ROTTINGTIME = rOTTINGTIME;
	}

	/**
	 * 足の速さを取得する.
	 * @return 足の速さ
	 */
	public int[] getSTEP() {
		return STEP;
	}

	/**
	 * 足の速さを設定する.
	 * @param sTEP 足の速さ
	 */
	public void setSTEP(int[] sTEP) {
		STEP = sTEP;
	}

	/**
	 * リラックス状態の期間を取得する.
	 * @return リラックス状態の期間
	 */
	public int getRELAXPERIOD() {
		return RELAXPERIOD;
	}

	/**
	 * リラックス状態の期間 を設定する.
	 * @param rELAXPERIOD リラックス状態の期間
	 */
	public void setRELAXPERIOD(int rELAXPERIOD) {
		RELAXPERIOD = rELAXPERIOD;
	}

	/**
	 * 発情状態の期間 を取得する.
	 * @return 発情状態の期間
	 */
	public int getEXCITEPERIOD() {
		return EXCITEPERIOD;
	}

	/**
	 * 発情状態の期間 を設定する.
	 * @param eXCITEPERIOD 発情状態の期間 
	 */
	public void setEXCITEPERIOD(int eXCITEPERIOD) {
		EXCITEPERIOD = eXCITEPERIOD;
	}

	/**
	 * 妊娠期間 を取得する.
	 * @return 妊娠期間 
	 */
	public int getPREGPERIOD() {
		return PREGPERIOD;
	}

	/**
	 * 妊娠期間 を設定する.
	 * @param pREGPERIOD 妊娠期間 
	 */
	public void setPREGPERIOD(int pREGPERIOD) {
		PREGPERIOD = pREGPERIOD;
	}

	/**
	 * 睡眠時間 を取得する.
	 * @return 睡眠時間 
	 */
	public int getSLEEPPERIOD() {
		return SLEEPPERIOD;
	}

	/**
	 * 睡眠時間 を設定する.
	 * @param sLEEPPERIOD 睡眠時間 
	 */
	public void setSLEEPPERIOD(int sLEEPPERIOD) {
		SLEEPPERIOD = sLEEPPERIOD;
	}

	/**
	 * アクティブな期間 を取得する.
	 * @return アクティブな期間 
	 */
	public int getACTIVEPERIOD() {
		return ACTIVEPERIOD;
	}

	/**
	 * アクティブな期間 を設定する.
	 * @param aCTIVEPERIOD アクティブな期間 
	 */
	public void setACTIVEPERIOD(int aCTIVEPERIOD) {
		ACTIVEPERIOD = aCTIVEPERIOD;
	}

	/**
	 * 怒り期間 を取得する.
	 * @return 怒り期間 
	 */
	public int getANGRYPERIOD() {
		return ANGRYPERIOD;
	}

	/**
	 * 怒り期間 を設定する.
	 * @param aNGRYPERIOD 怒り期間 
	 */
	public void setANGRYPERIOD(int aNGRYPERIOD) {
		ANGRYPERIOD = aNGRYPERIOD;
	}

	/**
	 * 恐怖期間 を取得する.
	 * @return 恐怖期間 
	 */
	public int getSCAREPERIOD() {
		return SCAREPERIOD;
	}

	/**
	 * 恐怖期間 を設定する.
	 * @param sCAREPERIOD 恐怖期間 
	 */
	public void setSCAREPERIOD(int sCAREPERIOD) {
		SCAREPERIOD = sCAREPERIOD;
	}

	/**
	 * 同一方向に動き続けるかを取得する.
	 * @return 同一方向に動き続けるか
	 */
	public int getSameDest() {
		return sameDest;
	}

	/**
	 * 同一方向に動き続けるかを設定する.
	 * @param sameDest 同一方向に動き続けるか
	 */
	public void setSameDest(int sameDest) {
		this.sameDest = sameDest;
	}

	/**
	 * ゲーム内12分、衝動の抑制のための変数 を取得する.
	 * @return ゲーム内12分、衝動の抑制のための変数
	 */
	public int getDECLINEPERIOD() {
		return DECLINEPERIOD;
	}

	/**
	 * ゲーム内12分、衝動の抑制のための変数 を設定する.
	 * @param dECLINEPERIOD ゲーム内12分、衝動の抑制のための変数 
	 */
	public void setDECLINEPERIOD(int dECLINEPERIOD) {
		DECLINEPERIOD = dECLINEPERIOD;
	}

	/**
	 * 壁等にブロックされた回数の限界（怒りだす等） を取得する.
	 * @return 壁等にブロックされた回数の限界（怒りだす等） 
	 */
	public int getBLOCKEDLIMIT() {
		return BLOCKEDLIMIT;
	}

	/**
	 * 壁等にブロックされた回数の限界（怒りだす等） を設定する.
	 * @param bLOCKEDLIMIT 壁等にブロックされた回数の限界（怒りだす等） 
	 */
	public void setBLOCKEDLIMIT(int bLOCKEDLIMIT) {
		BLOCKEDLIMIT = bLOCKEDLIMIT;
	}

	/**
	 * 汚れ限界（超えるとゆかび状態） を取得する.
	 * @return 汚れ限界（超えるとゆかび状態） 
	 */
	public int getDIRTYPERIOD() {
		return DIRTYPERIOD;
	}

	/**
	 * 汚れ限界（超えるとゆかび状態） を設定する.
	 * @param dIRTYPERIOD 汚れ限界（超えるとゆかび状態） 
	 */
	public void setDIRTYPERIOD(int dIRTYPERIOD) {
		DIRTYPERIOD = dIRTYPERIOD;
	}

	/**
	 * 視界を取得する.
	 * @return 視界
	 */
	public int getEYESIGHT() {
		return EYESIGHT;
	}

	/**
	 * 視界を設定する.
	 * @param eYESIGHT 視界
	 */
	public void setEYESIGHT(int eYESIGHT) {
		EYESIGHT = eYESIGHT;
	}

	/**
	 * 強さを取得する.
	 * @return 強さ
	 */
	public int[] getSTRENGTH() {
		return STRENGTH;
	}

	/**
	 * 強さを設定する.
	 * @param sTRENGTH 強さ
	 */
	public void setSTRENGTH(int[] sTRENGTH) {
		STRENGTH = sTRENGTH;
	}

	/**
	 * ゆかびの潜伏期間 を取得する.
	 * @return ゆかびの潜伏期間 
	 */
	public int getINCUBATIONPERIOD() {
		return INCUBATIONPERIOD;
	}

	/**
	 * ゆかびの潜伏期間 を設定する.
	 * @param iNCUBATIONPERIOD ゆかびの潜伏期間 
	 */
	public void setINCUBATIONPERIOD(int iNCUBATIONPERIOD) {
		INCUBATIONPERIOD = iNCUBATIONPERIOD;
	}

	/**
	 * 攻撃された際のぴこぴこ破壊確率。0だと破壊されない を取得する.
	 * @return 攻撃された際のぴこぴこ破壊確率。0だと破壊されない 
	 */
	public int getnBreakBraidRand() {
		return nBreakBraidRand;
	}

	/**
	 * 攻撃された際のぴこぴこ破壊確率。0だと破壊されない を設定する.
	 * @param nBreakBraidRand 攻撃された際のぴこぴこ破壊確率。0だと破壊されない 
	 */
	public void setnBreakBraidRand(int nBreakBraidRand) {
		this.nBreakBraidRand = nBreakBraidRand;
	}

	/**
	 * 何回のうち1回の確率ですりすり事故で妊娠するかの値 を取得する.
	 * @return 何回のうち1回の確率ですりすり事故で妊娠するかの値 
	 */
	public int getSurisuriAccidentProb() {
		return SurisuriAccidentProb;
	}

	/**
	 * 何回のうち1回の確率ですりすり事故で妊娠するかの値 を設定する.
	 * @param surisuriAccidentProb 何回のうち1回の確率ですりすり事故で妊娠するかの値 
	 */
	public void setSurisuriAccidentProb(int surisuriAccidentProb) {
		SurisuriAccidentProb = surisuriAccidentProb;
	}

	/**
	 * 何回のうち1回の確率で路上で車に轢かれるかの値 を取得する.
	 * @return 何回のうち1回の確率で路上で車に轢かれるかの値 
	 */
	public int getCarAccidentProb() {
		return CarAccidentProb;
	}

	/**
	 * 何回のうち1回の確率で路上で車に轢かれるかの値 を設定する.
	 * @param carAccidentProb 何回のうち1回の確率で路上で車に轢かれるかの値 
	 */
	public void setCarAccidentProb(int carAccidentProb) {
		CarAccidentProb = carAccidentProb;
	}

	/**
	 * 何回のうち1回の確率であんよが傷ついているとあんよが破壊されるかの確率 を取得する.
	 * @return 何回のうち1回の確率であんよが傷ついているとあんよが破壊されるかの確率
	 */
	public int getBreakBodyByShitProb() {
		return BreakBodyByShitProb;
	}

	/**
	 * 何回のうち1回の確率であんよが傷ついているとあんよが破壊されるかの確率 を設定する.
	 * @param breakBodyByShitProb 何回のうち1回の確率であんよが傷ついているとあんよが破壊されるかの確率 
	 */
	public void setBreakBodyByShitProb(int breakBodyByShitProb) {
		BreakBodyByShitProb = breakBodyByShitProb;
	}

	/**
	 * 何回のうち1回の確率で苦いフードを食べた際にゆ下痢になるかの確率 を取得する.
	 * @return 何回のうち1回の確率で苦いフードを食べた際にゆ下痢になるかの確率 
	 */
	public int getDiarrheaProb() {
		return diarrheaProb;
	}

	/**
	 * 何回のうち1回の確率で苦いフードを食べた際にゆ下痢になるかの確率 を設定する.
	 * @param diarrheaProb 何回のうち1回の確率で苦いフードを食べた際にゆ下痢になるかの確率 
	 */
	public void setDiarrheaProb(int diarrheaProb) {
		this.diarrheaProb = diarrheaProb;
	}

	/**
	 * 何回のうち１回の確率で発情するかの確率 を取得する.
	 * @return 何回のうち１回の確率で発情するかの確率 
	 */
	public int getExciteProb() {
		return exciteProb;
	}

	/**
	 * 何回のうち１回の確率で発情するかの確率 を設定する.
	 * @param exciteProb 何回のうち１回の確率で発情するかの確率 
	 */
	public void setExciteProb(int exciteProb) {
		this.exciteProb = exciteProb;
	}

	/**
	 * 固有の免疫力（個体値。これは仮） を取得する.
	 * @return 固有の免疫力（個体値。これは仮） 
	 */
	public int getROBUSTNESS() {
		return ROBUSTNESS;
	}

	/**
	 * 固有の免疫力（個体値。これは仮） を設定する.
	 * @param rOBUSTNESS 固有の免疫力（個体値。これは仮） 
	 */
	public void setROBUSTNESS(int rOBUSTNESS) {
		ROBUSTNESS = rOBUSTNESS;
	}

	/**
	 * 免疫力(左から順に赤ゆ、子ゆ、成ゆ、老ゆ) を取得する.
	 * @return 免疫力(左から順に赤ゆ、子ゆ、成ゆ、老ゆ) 
	 */
	public int[] getImmunity() {
		return immunity;
	}

	/**
	 * 免疫力(左から順に赤ゆ、子ゆ、成ゆ、老ゆ) を設定する.
	 * @param immunity 免疫力(左から順に赤ゆ、子ゆ、成ゆ、老ゆ) 
	 */
	public void setImmunity(int[] immunity) {
		this.immunity = immunity;
	}

	/**
	 * 性格変化の切り替え を取得する.
	 * @return 性格変化の切り替え 
	 */
	public boolean isNotChangeCharacter() {
		return notChangeCharacter;
	}

	/**
	 * 性格変化の切り替え を設定する.
	 * @param notChangeCharacter 性格変化の切り替え 
	 */
	public void setNotChangeCharacter(boolean notChangeCharacter) {
		this.notChangeCharacter = notChangeCharacter;
	}

	/**
	 * ゲスポイント を取得する.
	 * @return ゲスポイント 
	 */
	public int getAttitudePoint() {
		return AttitudePoint;
	}

	/**
	 * ゲスポイント を設定する.
	 * @param attitudePoint ゲスポイント 
	 */
	public void setAttitudePoint(int attitudePoint) {
		AttitudePoint = attitudePoint;
	}

	/**
	 * ゲス限界 を取得する.
	 * @return ゲス限界 
	 */
	public int[] getRudeLimit() {
		return RudeLimit;
	}

	/**
	 * ゲス限界 を設定する.
	 * @param rudeLimit ゲス限界 
	 */
	public void setRudeLimit(int[] rudeLimit) {
		RudeLimit = rudeLimit;
	}

	/**
	 * 善良限界 を取得する.
	 * @return 善良限界 
	 */
	public int[] getNiceLimit() {
		return NiceLimit;
	}

	/**
	 * 善良限界 を設定する.
	 * @param niceLimit 善良限界 
	 */
	public void setNiceLimit(int[] niceLimit) {
		NiceLimit = niceLimit;
	}

	/**
	 * 妊娠限界 を取得する.
	 * @return 妊娠限界 
	 */
	public int getPregnantLimit() {
		return PregnantLimit;
	}

	/**
	 * 妊娠限界 を設定する.
	 * @param pregnantLimit 妊娠限界 
	 */
	public void setPregnantLimit(int pregnantLimit) {
		PregnantLimit = pregnantLimit;
	}

	/**
	 * よりリアルな妊娠限界かどうか を取得する.
	 * @return よりリアルな妊娠限界かどうか 
	 */
	public boolean isRealPregnantLimit() {
		return realPregnantLimit;
	}

	/**
	 * よりリアルな妊娠限界かどうかを返却する.
	 * @param realPregnantLimit よりリアルな妊娠限界かどうか
	 */
	public void setRealPregnantLimit(boolean realPregnantLimit) {
		this.realPregnantLimit = realPregnantLimit;
	}

	/**
	 * 画像がまりちゃ流しか を設定する.
	 * @return 画像がまりちゃ流しか 
	 */
	public boolean isbImageNagasiMode() {
		return bImageNagasiMode;
	}

	/**
	 * 画像がまりちゃ流しか を設定する.
	 * @param bImageNagasiMode 画像がまりちゃ流しか 
	 */
	public void setbImageNagasiMode(boolean bImageNagasiMode) {
		this.bImageNagasiMode = bImageNagasiMode;
	}

	/**
	 * 飼いゆ、野良ゆなどのランク を取得する.
	 * @return 飼いゆ、野良ゆなどのランク 
	 */
	public BodyRank getBodyRank() {
		return bodyRank;
	}

	/**
	 * 飼いゆ、野良ゆなどのランク を設定する.
	 * @param bodyRank 飼いゆ、野良ゆなどのランク 
	 */
	public void setBodyRank(BodyRank bodyRank) {
		this.bodyRank = bodyRank;
	}

	/**
	 * 群れ内のうんうん奴隷などのランク を取得する.
	 * @return 群れ内のうんうん奴隷などのランク 
	 */
	public PublicRank getPublicRank() {
		return publicRank;
	}

	/**
	 * 群れ内のうんうん奴隷などのランク を設定する.
	 * @param publicRank 群れ内のうんうん奴隷などのランク 
	 */
	public void setPublicRank(PublicRank publicRank) {
		this.publicRank = publicRank;
	}

	/**
	 * 移動先目標 destination X座標を取得する.
	 * @return 移動先目標 destination X座標 
	 */
	public int getDestX() {
		return destX;
	}

	/**
	 * 移動先目標 destination X座標を設定する.
	 * @param destX 移動先目標 destination X座標
	 */
	public void setDestX(int destX) {
		this.destX = destX;
	}

	/**
	 * 移動先目標 destination Y座標を取得する.
	 * @return 移動先目標 destination Y座標
	 */
	public int getDestY() {
		return destY;
	}

	/**
	 * 移動先目標 destination Y座標を設定する.
	 * @param destY 移動先目標 destination Y座標
	 */
	public void setDestY(int destY) {
		this.destY = destY;
	}

	/**
	 * 移動先目標 destination Z座標を取得する.
	 * @return 移動先目標 destination Z座標
	 */
	public int getDestZ() {
		return destZ;
	}

	/**
	 * 移動先目標 destination Z座標を設定する.
	 * @param destZ 移動先目標 destination Z座標座標
	 */
	public void setDestZ(int destZ) {
		this.destZ = destZ;
	}

	/**
	 * 移動量 how many steps to same direction を取得する.
	 * @return 移動量 how many steps to same direction 
	 */
	public int getCountX() {
		return countX;
	}

	/**
	 * 移動量 how many steps to same direction X座標を設定する.
	 * @param countX 移動量 how many steps to same direction X座標 
	 */
	public void setCountX(int countX) {
		this.countX = countX;
	}

	/**
	 * 移動量 how many steps to same direction Y座標を取得する.
	 * @return 移動量 how many steps to same direction Y座標
	 */
	public int getCountY() {
		return countY;
	}

	/**
	 * 移動量 how many steps to same direction Y座標を設定する.
	 * @param countY 移動量 how many steps to same direction Y座標
	 */
	public void setCountY(int countY) {
		this.countY = countY;
	}

	/**
	 * 移動量 how many steps to same direction Z座標を取得する.
	 * @return 移動量 how many steps to same direction Z座標
	 */
	public int getCountZ() {
		return countZ;
	}

	/**
	 * 移動量 how many steps to same direction Z座標を設定する.
	 * @param countZ 移動量 how many steps to same direction Z座標
	 */
	public void setCountZ(int countZ) {
		this.countZ = countZ;
	}

	/**
	 * 移動方向 direction to move on X座標を取得する.
	 * @return 移動方向 direction to move on X座標
	 */
	public int getDirX() {
		return dirX;
	}

	/**
	 * 移動方向 direction to move on X座標を設定する.
	 * @param dirX 移動方向 direction to move on X座標
	 */
	public void setDirX(int dirX) {
		this.dirX = dirX;
	}

	/**
	 * 移動方向 direction to move on Y座標を取得する.
	 * @return 移動方向 direction to move on Y座標
	 */
	public int getDirY() {
		return dirY;
	}

	/**
	 * 移動方向 direction to move on Y座標を設定する.
	 * @param dirY 移動方向 direction to move on Y座標
	 */
	public void setDirY(int dirY) {
		this.dirY = dirY;
	}

	/**
	 * 移動方向 direction to move on Z座標を取得する.
	 * @return 移動方向 direction to move on Z座標
	 */
	public int getDirZ() {
		return dirZ;
	}

	/**
	 * 移動方向 direction to move on Z座標を設定する/
	 * @param dirZ 移動方向 direction to move on Z座標
	 */
	public void setDirZ(int dirZ) {
		this.dirZ = dirZ;
	}

	/**
	 * 顔の向き direction of face を取得する.
	 * @return 顔の向き direction of face 
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * 顔の向き direction of face を設定する.
	 * @param direction 顔の向き direction of face 
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * 蓄積ダメージ counter indicating damage を取得する.
	 * @return 蓄積ダメージ counter indicating damage 
	 */
	public int getDamage() {
		return damage;
	}

	/**
	 * 蓄積ダメージ counter indicating damage を設定する.
	 * @param damage 蓄積ダメージ counter indicating damage 
	 */
	public void setDamage(int damage) {
		this.damage = damage;
	}

	/**
	 * 蓄積ストレス を取得する.
	 * @return 蓄積ストレス 
	 */
	public int getStress() {
		return stress;
	}

	/**
	 * 舌の肥え を取得する.
	 * @return 舌の肥え 
	 */
	public int getTang() {
		return tang;
	}

	/**
	 * 舌の肥え を設定する.
	 * @param tang 舌の肥え 
	 */
	public void setTang(int tang) {
		this.tang = tang;
	}

	/**
	 * ダメージ外観 を設定する.
	 * @param damageState ダメージ外観 
	 */
	public void setDamageState(Damage damageState) {
		this.damageState = damageState;
	}

	/**
	 * 性格 counter indicating shithead/nice etc. を取得する. 
	 * @return 性格 counter indicating shithead/nice etc.
	 */
	public Attitude getAttitude() {
		return attitude;
	}

	/**
	 * 性格 counter indicating shithead/nice etc.を設定する. 
	 * @param attitude 性格 counter indicating shithead/nice etc. 
	 */
	public void setAttitude(Attitude attitude) {
		this.attitude = attitude;
	}

	/**
	 * 知性 を取得する.
	 * @return 知性 
	 */
	public Intelligence getIntelligence() {
		return intelligence;
	}

	/**
	 * 知性 を設定する.
	 * @param intelligence 知性 
	 */
	public void setIntelligence(Intelligence intelligence) {
		this.intelligence = intelligence;
	}

	/**
	 * 幸福度 を取得する.
	 * @return 幸福度 
	 */
	public Happiness getHappiness() {
		return happiness;
	}

	/**
	 * プレイヤーへのなつき度  を設定する.
	 * @return プレイヤーへのなつき度  
	 */
	public int getnLovePlayer() {
		return nLovePlayer;
	}

	/**
	 * プレイヤーへのなつき度 を設定する.
	 * @param nLovePlayer プレイヤーへのなつき度 
	 */
	public void setnLovePlayer(int nLovePlayer) {
		this.nLovePlayer = nLovePlayer;
	}

	/**
	 * プレイヤーへのなつき度概算 を取得する.
	 * @return プレイヤーへのなつき度概算 
	 */
	public LovePlayer geteLovePlayerState() {
		return eLovePlayerState;
	}

	/**
	 * プレイヤーへのなつき度概算 を設定する.
	 * @param eLovePlayerState プレイヤーへのなつき度概算 
	 */
	public void seteLovePlayerState(LovePlayer eLovePlayerState) {
		this.eLovePlayerState = eLovePlayerState;
	}

	/**
	 * 髪の状態 を取得する.
	 * @return 髪の状態 
	 */
	public HairState geteHairState() {
		return eHairState;
	}

	/**
	 * 髪の状態 を設定する.
	 * @param eHairState 髪の状態 
	 */
	public void seteHairState(HairState eHairState) {
		this.eHairState = eHairState;
	}

	/**
	 * うんうんの溜まり具合 を取得する.
	 * @return うんうんの溜まり具合 
	 */
	public int getShit() {
		return shit;
	}

	/**
	 * うんうんの溜まり具合 を設定する.
	 * @param shit うんうんの溜まり具合 
	 */
	public void setShit(int shit) {
		this.shit = shit;
	}

	/**
	 * 思い出（悪夢関連） を取得する.
	 * @return 思い出（悪夢関連） 
	 */
	public int getMemories() {
		return memories;
	}

	/**
	 * 思い出（悪夢関連） を設定する.
	 * @param memories 思い出（悪夢関連） 
	 */
	public void setMemories(int memories) {
		this.memories = memories;
	}

	/**
	 * トラウマ を取得する.
	 * @return トラウマ 
	 */
	public Trauma getTrauma() {
		return trauma;
	}

	/**
	 * トラウマ を設定する.
	 * @param trauma トラウマ 
	 */
	public void setTrauma(Trauma trauma) {
		this.trauma = trauma;
	}

	/**
	 * おかざり種別 を取得する.
	 * @return おかざり種別 
	 */
	public Okazari getOkazari() {
		return okazari;
	}

	/**
	 * おかざり種別 を設定する.
	 * @param okazari おかざり種別 
	 */
	public void setOkazari(Okazari okazari) {
		this.okazari = okazari;
	}

	/**
	 * 体の前後のどこにお飾りを持っているか(0は両方、1は前方のみ、2は後方のみ) を取得する.
	 * @return 体の前後のどこにお飾りを持っているか(0は両方、1は前方のみ、2は後方のみ) 
	 */
	public int getOkazariPosition() {
		return okazariPosition;
	}

	/**
	 * 体の前後のどこにお飾りを持っているか(0は両方、1は前方のみ、2は後方のみ) を設定する.
	 * @param okazariPosition 体の前後のどこにお飾りを持っているか(0は両方、1は前方のみ、2は後方のみ) 
	 */
	public void setOkazariPosition(int okazariPosition) {
		this.okazariPosition = okazariPosition;
	}

	/**
	 * おさげ、羽、尻尾有無 種族として何も持っていないものはtrue を取得する.
	 * @return おさげ、羽、尻尾有無 種族として何も持っていないものはtrue 
	 */
	public boolean isHasBraid() {
		return hasBraid;
	}

	/**
	 * おさげ、羽、尻尾有無 種族として何も持っていないものはtrue を設定する.
	 * @param hasBraid おさげ、羽、尻尾有無 種族として何も持っていないものはtrue 
	 */
	public void setHasBraid(boolean hasBraid) {
		this.hasBraid = hasBraid;
	}

	/**
	 * おくるみ有無 true if having pants を取得する.
	 * @return おくるみ有無 true if having pants 
	 */
	public boolean isHasPants() {
		return hasPants;
	}

	/**
	 * おくるみ有無 true if having pants を設定する.
	 * @param hasPants おくるみ有無 true if having pants 
	 */
	public void setHasPants(boolean hasPants) {
		this.hasPants = hasPants;
	}

	/**
	 * 胎生妊娠有無 having baby or not を取得する.
	 * @return 胎生妊娠有無 having baby or not 
	 */
	public boolean isHasBaby() {
		return hasBaby;
	}

	/**
	 * 胎生妊娠有無 having baby or not を設定する.
	 * @param hasBaby 胎生妊娠有無 having baby or not 
	 */
	public void setHasBaby(boolean hasBaby) {
		this.hasBaby = hasBaby;
	}

	/**
	 * 茎妊娠有無 having baby or not を取得する.
	 * @return 茎妊娠有無 having baby or not 
	 */
	public boolean isHasStalk() {
		return hasStalk;
	}

	/**
	 * 茎妊娠有無 having baby or not を設定する.
	 * @param hasStalk 茎妊娠有無 having baby or not 
	 */
	public void setHasStalk(boolean hasStalk) {
		this.hasStalk = hasStalk;
	}

	/**
	 * あにゃるふさぎ有無 を取得する.
	 * @return あにゃるふさぎ有無 
	 */
	public boolean isAnalClose() {
		return analClose;
	}

	/**
	 * あにゃるふさぎ有無 を設定する.
	 * @param analClose あにゃるふさぎ有無 
	 */
	public void setAnalClose(boolean analClose) {
		this.analClose = analClose;
	}

	/**
	 * 胎生去勢有無 を取得する.
	 * @return 胎生去勢有無 
	 */
	public boolean isBodyCastration() {
		return bodyCastration;
	}

	/**
	 * 胎生去勢有無 を設定する.
	 * @param bodyCastration 胎生去勢有無 
	 */
	public void setBodyCastration(boolean bodyCastration) {
		this.bodyCastration = bodyCastration;
	}

	/**
	 * 茎去勢有無 を取得する.
	 * @return 茎去勢有無 
	 */
	public boolean isStalkCastration() {
		return stalkCastration;
	}

	/**
	 * 茎去勢有無 を設定する.
	 * @param stalkCastration 茎去勢有無 
	 */
	public void setStalkCastration(boolean stalkCastration) {
		this.stalkCastration = stalkCastration;
	}

	/**
	 * ぺにぺにの去勢有無 を取得する.
	 * @return ぺにぺにの去勢有無 
	 */
	public boolean isbPenipeniCutted() {
		return bPenipeniCutted;
	}

	/**
	 * ぺにぺにの去勢有無 を設定する.
	 * @param bPenipeniCutted ぺにぺにの去勢有無 
	 */
	public void setbPenipeniCutted(boolean bPenipeniCutted) {
		this.bPenipeniCutted = bPenipeniCutted;
	}

	/**
	 * フェロモンの有無 を取得する.
	 * @return フェロモンの有無 
	 */
	public boolean isbPheromone() {
		return bPheromone;
	}

	/**
	 * フェロモンの有無 を設定する.
	 * @param bPheromone フェロモンの有無 
	 */
	public void setbPheromone(boolean bPheromone) {
		this.bPheromone = bPheromone;
	}

	/**
	 * 胎生ゆのリスト を取得する.
	 * @return 胎生ゆのリスト 
	 */
	public List<Dna> getBabyTypes() {
		return babyTypes;
	}

	/**
	 * 胎生ゆのリスト を設定する.
	 * @param babyTypes 胎生ゆのリスト 
	 */
	public void setBabyTypes(List<Dna> babyTypes) {
		this.babyTypes = babyTypes;
	}

	/**
	 * 実ゆのリスト を取得する.
	 * @return 実ゆのリスト 
	 */
	public List<Dna> getStalkBabyTypes() {
		return stalkBabyTypes;
	}

	/**
	 * 実ゆのリスト を設定する.
	 * @param stalkBabyTypes 実ゆのリスト 
	 */
	public void setStalkBabyTypes(List<Dna> stalkBabyTypes) {
		this.stalkBabyTypes = stalkBabyTypes;
	}

	/**
	 * 茎のリスト を取得する.
	 * @return 茎のリスト 
	 */
	public List<Stalk> getStalks() {
		return stalks;
	}

	/**
	 * 茎のリスト を設定する.
	 * @param stalks 茎のリスト 
	 */
	public void setStalks(List<Stalk> stalks) {
		this.stalks = stalks;
	}

	/**
	 * 自分がぶらさがっている茎 を取得する.
	 * @return 自分がぶらさがっている茎 
	 */
	public Stalk getBindStalk() {
		return bindStalk;
	}

	/**
	 * 自分がぶらさがっている茎 を設定する.
	 * @param bindStalk 自分がぶらさがっている茎 
	 */
	public void setBindStalk(Stalk bindStalk) {
		this.bindStalk = bindStalk;
	}

	/**
	 * 死亡フラグdead of alive を取得する.
	 * @return 死亡フラグdead of alive 
	 */
	public boolean isDead() {
		return dead;
	}

	/**
	 * 死亡フラグdead of alive を設定する.
	 * @param dead 死亡フラグdead of alive 
	 */
	public void setDead(boolean dead) {
		this.dead = dead;
	}

	/**
	 * うまれて初めての地面か を取得する.
	 * @return うまれて初めての地面か 
	 */
	public boolean isbFirstGround() {
		return bFirstGround;
	}

	/**
	 * うまれて初めての地面か を設定する.
	 * @param bFirstGround うまれて初めての地面か 
	 */
	public void setbFirstGround(boolean bFirstGround) {
		this.bFirstGround = bFirstGround;
	}

	/**
	 * うまれて初めての食事か を取得する.
	 * @return うまれて初めての食事か 
	 */
	public boolean isbFirstEatStalk() {
		return bFirstEatStalk;
	}

	/**
	 * うまれて初めての食事か を設定する.
	 * @param bFirstEatStalk うまれて初めての食事か 
	 */
	public void setbFirstEatStalk(boolean bFirstEatStalk) {
		this.bFirstEatStalk = bFirstEatStalk;
	}

	/**
	 * 死体が損壊されているか を取得する.
	 * @return 死体が損壊されているか 
	 */
	public boolean isCrushed() {
		return crushed;
	}

	/**
	 * 死体が損壊されているか を設定する.
	 * @param crushed 死体が損壊されているか 
	 */
	public void setCrushed(boolean crushed) {
		this.crushed = crushed;
	}

	/**
	 * 死体が焼損されているか を取得する.
	 * @return 死体が焼損されているか 
	 */
	public boolean isBurned() {
		return burned;
	}

	/**
	 * 中枢餡の状態（非ゆっくり症フラグ を取得する.
	 * @return 中枢餡の状態（非ゆっくり症フラグ 
	 */
	public CoreAnkoState geteCoreAnkoState() {
		return eCoreAnkoState;
	}

	/**
	 * 中枢餡の状態（非ゆっくり症フラグ を設定する.
	 * @param eCoreAnkoState 中枢餡の状態（非ゆっくり症フラグ 
	 */
	public void seteCoreAnkoState(CoreAnkoState eCoreAnkoState) {
		this.eCoreAnkoState = eCoreAnkoState;
	}

	/**
	 * 発情フラグ want to sukkiri or not を設定する.
	 * @param exciting 発情フラグ want to sukkiri or not 
	 */
	public void setExciting(boolean exciting) {
		this.exciting = exciting;
	}

	/**
	 * 強制発情フラグ want to sukkiri or not を取得する.
	 * @return 強制発情フラグ want to sukkiri or not 
	 */
	public boolean isbForceExciting() {
		return bForceExciting;
	}

	/**
	 * 強制発情フラグ want to sukkiri or not を設定する.
	 * @param bForceExciting 強制発情フラグ want to sukkiri or not 
	 */
	public void setbForceExciting(boolean bForceExciting) {
		this.bForceExciting = bForceExciting;
	}

	/**
	 * ゆっくりしてるかどうか を取得する.
	 * @return ゆっくりしてるかどうか 
	 */
	public boolean isRelax() {
		return relax;
	}

	/**
	 * ゆっくりしてるかどうか を設定する.
	 * @param relax ゆっくりしてるかどうか 
	 */
	public void setRelax(boolean relax) {
		this.relax = relax;
	}

	/**
	 * 睡眠中かどうか を設定する.
	 * @param sleeping 睡眠中かどうか 
	 */
	public void setSleeping(boolean sleeping) {
		this.sleeping = sleeping;
	}

	/**
	 * 悪夢を見るかどうか を取得する.
	 * @return 悪夢を見るかどうか 
	 */
	public boolean isNightmare() {
		return nightmare;
	}

	/**
	 * 悪夢を見るかどうか を設定する.
	 * @param nightmare 悪夢を見るかどうか 
	 */
	public void setNightmare(boolean nightmare) {
		this.nightmare = nightmare;
	}

	/**
	 * 前回起きた時間 を取得する.
	 * @return 前回起きた時間 
	 */
	public long getWakeUpTime() {
		return wakeUpTime;
	}

	/**
	 * 前回起きた時間 を設定する.
	 * @param wakeUpTime 前回起きた時間 
	 */
	public void setWakeUpTime(long wakeUpTime) {
		this.wakeUpTime = wakeUpTime;
	}

	/**
	 * 汚れ有無 を設定する.
	 * @param dirty 汚れ有無 
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * 頑固な汚れ有無 を設定する.
	 * @param stubbornlyDirty 頑固な汚れ有無 
	 */
	public void setStubbornlyDirty(boolean stubbornlyDirty) {
		this.stubbornlyDirty = stubbornlyDirty;
	}

	/**
	 * 針の有無 を取得する.
	 * @return 針の有無 
	 */
	public boolean isbNeedled() {
		return bNeedled;
	}

	/**
	 * 針の有無 を設定する.
	 * @param bNeedled 針の有無 
	 */
	public void setbNeedled(boolean bNeedled) {
		this.bNeedled = bNeedled;
	}

	/**
	 * レイパー化有無 を取得する.
	 * @return レイパー化有無 
	 */
	public boolean isRapist() {
		return rapist;
	}

	/**
	 * レイパー化有無 を設定する.
	 * @param rapist レイパー化有無 
	 */
	public void setRapist(boolean rapist) {
		this.rapist = rapist;
	}

	/**
	 * バイゆグラでレイパーになる、すーぱーれいぱー状態 を取得する.
	 * @return バイゆグラでレイパーになる、すーぱーれいぱー状態 
	 */
	public boolean isSuperRapist() {
		return superRapist;
	}

	/**
	 * バイゆグラでレイパーになる、すーぱーれいぱー状態 を設定する.
	 * @param superRapist バイゆグラでレイパーになる、すーぱーれいぱー状態 
	 */
	public void setSuperRapist(boolean superRapist) {
		this.superRapist = superRapist;
	}

	/**
	 * 濡れ状態 を取得する.
	 * @return 濡れ状態 
	 */
	public boolean isWet() {
		return wet;
	}

	/**
	 * 濡れ状態 を設定する.
	 * @param wet 濡れ状態 
	 */
	public void setWet(boolean wet) {
		this.wet = wet;
	}

	/**
	 * 水に溶けた状態 を取得する.
	 * @return 水に溶けた状態 
	 */
	public boolean isMelt() {
		return melt;
	}

	/**
	 * 水に溶けた状態 を設定する.
	 * @param melt 水に溶けた状態 
	 */
	public void setMelt(boolean melt) {
		this.melt = melt;
	}

	/**
	 * 皮をむいた状態 を取得する.
	 * @return 皮をむいた状態 
	 */
	public boolean isPealed() {
		return pealed;
	}

	/**
	 * 皮をむいた状態 を設定する.
	 * @param pealed 皮をむいた状態 
	 */
	public void setPealed(boolean pealed) {
		this.pealed = pealed;
	}

	/**
	 * 饅頭にされた状態 を取得する.
	 * @return 饅頭にされた状態 
	 */
	public boolean isPacked() {
		return packed;
	}

	/**
	 * 饅頭にされた状態 を設定する.
	 * @param packed 饅頭にされた状態 
	 */
	public void setPacked(boolean packed) {
		this.packed = packed;
	}

	/**
	 * アマギられた状態 かどうかを取得する.
	 * @return アマギられた状態 かどうか
	 */
	public boolean isBlind() {
		return blind;
	}

	/**
	 * アマギられた状態 かどうかを設定する.
	 * @param blind アマギられた状態 かどうか
	 */
	public void setBlind(boolean blind) {
		this.blind = blind;
	}

	/**
	 * おかざりがなくなっていることに気がついているか を取得する.
	 * @return おかざりがなくなっていることに気がついているか 
	 */
	public boolean isbNoticeNoOkazari() {
		return bNoticeNoOkazari;
	}

	/**
	 * おかざりがなくなっていることに気がついているか を設定する.
	 * @param bNoticeNoOkazari おかざりがなくなっていることに気がついているか 
	 */
	public void setbNoticeNoOkazari(boolean bNoticeNoOkazari) {
		this.bNoticeNoOkazari = bNoticeNoOkazari;
	}

	/**
	 * パニック種別 を取得する.
	 * @return パニック種別 
	 */
	public PanicType getPanicType() {
		return panicType;
	}

	/**
	 * パニック種別 を設定する.
	 * @param panicType パニック種別 
	 */
	public void setPanicType(PanicType panicType) {
		this.panicType = panicType;
	}

	/**
	 * 致命傷種別 を取得する.
	 * @return 致命傷種別 
	 */
	public CriticalDamegeType getCriticalDamege() {
		return criticalDamege;
	}

	/**
	 * 致命傷種別 を設定する.
	 * @param criticalDamege 致命傷種別 
	 */
	public void setCriticalDamege(CriticalDamegeType criticalDamege) {
		this.criticalDamege = criticalDamege;
	}

	/**
	 * つがい を取得する.
	 * @return つがい 
	 */
	public Body getPartner() {
		return partner;
	}

	/**
	 * つがい を設定する.
	 * @param partner つがい 
	 */
	public void setPartner(Body partner) {
		this.partner = partner;
	}

	/**
	 * 親を取得する.
	 * @return 親
	 */
	public Body[] getParents() {
		return parents;
	}

	/**
	 * 親を設定する.
	 * @param parents 親
	 */
	public void setParents(Body[] parents) {
		this.parents = parents;
	}

	/**
	 * 子供のリスト を取得する.
	 * @return 子供のリスト 
	 */
	public List<Body> getChildrenList() {
		return childrenList;
	}

	/**
	 * 子供のリスト を設定する.
	 * @param childrenList 子供のリスト 
	 */
	public void setChildrenList(List<Body> childrenList) {
		this.childrenList = childrenList;
	}

	/**
	 * 姉のリスト を取得する.
	 * @return 姉のリスト 
	 */
	public List<Body> getElderSisterList() {
		return elderSisterList;
	}

	/**
	 * 姉のリスト を設定する.
	 * @param elderSisterList 姉のリスト 
	 */
	public void setElderSisterList(List<Body> elderSisterList) {
		this.elderSisterList = elderSisterList;
	}

	/**
	 * 妹のリスト を取得する.
	 * @return 妹のリスト 
	 */
	public List<Body> getSisterList() {
		return sisterList;
	}

	/**
	 * 妹のリスト を設定する.
	 * @param sisterList 妹のリスト 
	 */
	public void setSisterList(List<Body> sisterList) {
		this.sisterList = sisterList;
	}

	/**
	 * 先祖のリスト を取得する.
	 * @return 先祖のリスト 
	 */
	public List<Integer> getAncestorList() {
		return ancestorList;
	}

	/**
	 * 先祖のリスト を設定する.
	 * @param ancestorList 先祖のリスト 
	 */
	public void setAncestorList(List<Integer> ancestorList) {
		this.ancestorList = ancestorList;
	}

	/**
	 * 自分がレイプでできた子か を取得する.
	 * @return 自分がレイプでできた子か 
	 */
	public boolean isFatherRaper() {
		return fatherRaper;
	}

	/**
	 * 自分がレイプでできた子か を設定する.
	 * @param fatherRaper 自分がレイプでできた子か 
	 */
	public void setFatherRaper(boolean fatherRaper) {
		this.fatherRaper = fatherRaper;
	}

	/**
	 * うんうん抑制 を取得する.
	 * @return うんうん抑制 
	 */
	public int getShittingDiscipline() {
		return shittingDiscipline;
	}

	/**
	 * うんうん抑制 を設定する.
	 * @param shittingDiscipline うんうん抑制 
	 */
	public void setShittingDiscipline(int shittingDiscipline) {
		this.shittingDiscipline = shittingDiscipline;
	}

	/**
	 * 興奮抑制 を取得する.
	 * @return 興奮抑制 
	 */
	public int getExcitingDiscipline() {
		return excitingDiscipline;
	}

	/**
	 * 興奮抑制 を設定する.
	 * @param excitingDiscipline 興奮抑制 
	 */
	public void setExcitingDiscipline(int excitingDiscipline) {
		this.excitingDiscipline = excitingDiscipline;
	}

	/**
	 * ふりふり抑制 を設定する.
	 * @param furifuriDiscipline ふりふり抑制 
	 */
	public void setFurifuriDiscipline(int furifuriDiscipline) {
		this.furifuriDiscipline = furifuriDiscipline;
	}

	/**
	 * おしゃべり抑制 を取得する.
	 * @return おしゃべり抑制 
	 */
	public int getMessageDiscipline() {
		return messageDiscipline;
	}

	/**
	 * おしゃべり抑制 を設定する.
	 * @param messageDiscipline おしゃべり抑制 
	 */
	public void setMessageDiscipline(int messageDiscipline) {
		this.messageDiscipline = messageDiscipline;
	}

	/**
	 * あまあまへの慣れ具合 を取得する.
	 * @return あまあまへの慣れ具合 
	 */
	public int getAmaamaDiscipline() {
		return amaamaDiscipline;
	}

	/**
	 * あまあまへの慣れ具合 を設定する.
	 * @param amaamaDiscipline あまあまへの慣れ具合 
	 */
	public void setAmaamaDiscipline(int amaamaDiscipline) {
		this.amaamaDiscipline = amaamaDiscipline;
	}

	/**
	 * 自身の持っているアタッチメント を取得する.
	 * @return 自身の持っているアタッチメント 
	 */
	public List<Attachment> getAttach() {
		return attach;
	}

	/**
	 * 自身の持っているアタッチメント を設定する.
	 * @param attach 自身の持っているアタッチメント 
	 */
	public void setAttach(List<Attachment> attach) {
		this.attach = attach;
	}

	/**
	 * なにかのオブジェクト（すぃー、親ゆなど）に載せられている等のリンクが有る際のそのオブジェクト を取得する.
	 * @return なにかのオブジェクト（すぃー、親ゆなど）に載せられている等のリンクが有る際のそのオブジェクト 
	 */
	public Obj getLinkParent() {
		return linkParent;
	}

	/**
	 * なにかのオブジェクト（すぃー、親ゆなど）に載せられている等のリンクが有る際のそのオブジェクト を設定する.
	 * @param linkParent なにかのオブジェクト（すぃー、親ゆなど）に載せられている等のリンクが有る際のそのオブジェクト 
	 */
	public void setLinkParent(Obj linkParent) {
		this.linkParent = linkParent;
	}

	/**
	 * 移動不可ベルトコンベアの有無 を取得する.
	 * @return 移動不可ベルトコンベアの有無 
	 */
	public boolean isbOnDontMoveBeltconveyor() {
		return bOnDontMoveBeltconveyor;
	}

	/**
	 * 移動不可ベルトコンベアの有無 を設定する.
	 * @param bOnDontMoveBeltconveyor 移動不可ベルトコンベアの有無 
	 */
	public void setbOnDontMoveBeltconveyor(boolean bOnDontMoveBeltconveyor) {
		this.bOnDontMoveBeltconveyor = bOnDontMoveBeltconveyor;
	}

	/**
	 * 埋まり状態 を取得する.
	 * @return 埋まり状態 
	 */
	public BaryInUGState getBaryState() {
		return baryState;
	}

	/**
	 * 埋まり状態 を設定する.
	 * @param baryState 埋まり状態 
	 */
	public void setBaryState(BaryInUGState baryState) {
		this.baryState = baryState;
	}

	/**
	 * 希少種か を取得する.
	 * @return 希少種か 
	 */
	public boolean isRareType() {
		return rareType;
	}

	/**
	 * 希少種か を設定する.
	 * @param rareType 希少種か 
	 */
	public void setRareType(boolean rareType) {
		this.rareType = rareType;
	}

	/**
	 * 苦いえさが好きか を取得する.
	 * @return 苦いえさが好きか 
	 */
	public boolean isLikeBitterFood() {
		return likeBitterFood;
	}

	/**
	 * 苦いえさが好きか を設定する.
	 * @param likeBitterFood 苦いえさが好きか 
	 */
	public void setLikeBitterFood(boolean likeBitterFood) {
		this.likeBitterFood = likeBitterFood;
	}

	/**
	 * 辛いえさが好きか を取得する.
	 * @return 辛いえさが好きか 
	 */
	public boolean isLikeHotFood() {
		return likeHotFood;
	}

	/**
	 * 辛いえさが好きか を設定する.
	 * @param likeHotFood 辛いえさが好きか 
	 */
	public void setLikeHotFood(boolean likeHotFood) {
		this.likeHotFood = likeHotFood;
	}

	/**
	 * 水が平気か を取得する.
	 * @return 水が平気か 
	 */
	public boolean isLikeWater() {
		return likeWater;
	}

	/**
	 * 水が平気か を設定する.
	 * @param likeWater 水が平気か 
	 */
	public void setLikeWater(boolean likeWater) {
		this.likeWater = likeWater;
	}

	/**
	 * 空を飛ぶか を取得する.
	 * @return 空を飛ぶか 
	 */
	public boolean isFlyingType() {
		return flyingType;
	}

	/**
	 * 空を飛ぶか を設定する.
	 * @param flyingType 空を飛ぶか 
	 */
	public void setFlyingType(boolean flyingType) {
		this.flyingType = flyingType;
	}

	/**
	 * 種族としてお下げ、羽、尻尾を持つか を取得する.
	 * @return 種族としてお下げ、羽、尻尾を持つか 
	 */
	public boolean isBraidType() {
		return braidType;
	}

	/**
	 * 種族としてお下げ、羽、尻尾を持つか を設定する.
	 * @param braidType 種族としてお下げ、羽、尻尾を持つか 
	 */
	public void setBraidType(boolean braidType) {
		this.braidType = braidType;
	}

	/**
	 * 捕食種タイプ を取得する.
	 * @return 捕食種タイプ 
	 */
	public PredatorType getPredatorType() {
		return predatorType;
	}

	/**
	 * 捕食種タイプ を設定する.
	 * @param predatorType 捕食種タイプ 
	 */
	public void setPredatorType(PredatorType predatorType) {
		this.predatorType = predatorType;
	}

	/**
	 * 動けないかどうか を取得する.
	 * @return 動けないかどうか 
	 */
	public boolean isLockmove() {
		return lockmove;
	}

	/**
	 * 動けないかどうか を設定する.
	 * @param lockmove 動けないかどうか 
	 */
	public void setLockmove(boolean lockmove) {
		this.lockmove = lockmove;
	}

	/**
	 * ひっぱり、押しつぶし可能か を取得する.
	 * @return ひっぱり、押しつぶし可能か 
	 */
	public boolean isPullAndPush() {
		return pullAndPush;
	}

	/**
	 * ひっぱり、押しつぶし可能か を設定する.
	 * @param pullAndPush ひっぱり、押しつぶし可能か 
	 */
	public void setPullAndPush(boolean pullAndPush) {
		this.pullAndPush = pullAndPush;
	}

	/**
	 * 動けない期間（押さえられてる等で） を取得する.
	 * @return 動けない期間（押さえられてる等で） 
	 */
	public int getLockmovePeriod() {
		return lockmovePeriod;
	}

	/**
	 * 動けない期間（押さえられてる等で） を設定する.
	 * @param lockmovePeriod 動けない期間（押さえられてる等で） 
	 */
	public void setLockmovePeriod(int lockmovePeriod) {
		this.lockmovePeriod = lockmovePeriod;
	}

	/**
	 * 外圧 を取得する.
	 * @return 外圧 
	 */
	public int getExtForce() {
		return extForce;
	}

	/**
	 * 外圧 を設定する.
	 * @param extForce 外圧 
	 */
	public void setExtForce(int extForce) {
		this.extForce = extForce;
	}

	/**
	 * まばたき、同じ表情の時にカウント を取得する.
	 * @return まばたき、同じ表情の時にカウント 
	 */
	public int getMabatakiCnt() {
		return mabatakiCnt;
	}

	/**
	 * まばたき、同じ表情の時にカウント を設定する.
	 * @param mabatakiCnt まばたき、同じ表情の時にカウント 
	 */
	public void setMabatakiCnt(int mabatakiCnt) {
		this.mabatakiCnt = mabatakiCnt;
	}

	/**
	 * まばたき、表情の値を代入 を取得する.
	 * @return まばたき、表情の値を代入 
	 */
	public int getMabatakiType() {
		return mabatakiType;
	}

	/**
	 * まばたき、表情の値を代入 を設定する.
	 * @param mabatakiType まばたき、表情の値を代入 
	 */
	public void setMabatakiType(int mabatakiType) {
		this.mabatakiType = mabatakiType;
	}

	/**
	 * プレイヤーにすりすりされているか を取得する.
	 * @return プレイヤーにすりすりされているか 
	 */
	public boolean isbSurisuriFromPlayer() {
		return bSurisuriFromPlayer;
	}

	/**
	 * プレイヤーにすりすりされているか を設定する.
	 * @param bSurisuriFromPlayer プレイヤーにすりすりされているか 
	 */
	public void setbSurisuriFromPlayer(boolean bSurisuriFromPlayer) {
		this.bSurisuriFromPlayer = bSurisuriFromPlayer;
	}

	/**
	 * ぷるぷる震えているか を取得する.
	 * @return ぷるぷる震えているか 
	 */
	public boolean isbPurupuru() {
		return bPurupuru;
	}

	/**
	 * ぷるぷる震えているか を設定する.
	 * @param bPurupuru ぷるぷる震えているか 
	 */
	public void setbPurupuru(boolean bPurupuru) {
		this.bPurupuru = bPurupuru;
	}

	/**
	 * 粘着板で背中を固定されているかを取得する.
	 * @return 粘着板で背中を固定されているか
	 */
	public boolean isFixBack() {
		return fixBack;
	}

	/**
	 * 粘着板で背中を固定されているかを設定する.
	 * @param 粘着板で背中を固定されているか
	 */
	public void setFixBack(boolean bFixBack) {
		this.fixBack = bFixBack;
	}

	/**
	 * ダメージを受けていない期間 を取得する.
	 * @return ダメージを受けていない期間 
	 */
	public int getNoDamagePeriod() {
		return noDamagePeriod;
	}

	/**
	 * ダメージを受けていない期間 を設定する.
	 * @param noDamagePeriod ダメージを受けていない期間 
	 */
	public void setNoDamagePeriod(int noDamagePeriod) {
		this.noDamagePeriod = noDamagePeriod;
	}

	/**
	 * 飢餓状態になっていない期間を取得する. 
	 * @return 飢餓状態になっていない期間 
	 */
	public int getNoHungryPeriod() {
		return noHungryPeriod;
	}

	/**
	 * 飢餓状態になっていない期間 を設定する.
	 * @param noHungryPeriod 飢餓状態になっていない期間 
	 */
	public void setNoHungryPeriod(int noHungryPeriod) {
		this.noHungryPeriod = noHungryPeriod;
	}

	/**
	 * スーパーむーしゃむーしゃタイムのおかげで飢餓状態にならない期間 を取得する.
	 * @return スーパーむーしゃむーしゃタイムのおかげで飢餓状態にならない期間 
	 */
	public int getNoHungrybySupereatingTimePeriod() {
		return noHungrybySupereatingTimePeriod;
	}

	/**
	 * スーパーむーしゃむーしゃタイムのおかげで飢餓状態にならない期間 を設定する.
	 * @param noHungrybySupereatingTimePeriod スーパーむーしゃむーしゃタイムのおかげで飢餓状態にならない期間 
	 */
	public void setNoHungrybySupereatingTimePeriod(int noHungrybySupereatingTimePeriod) {
		this.noHungrybySupereatingTimePeriod = noHungrybySupereatingTimePeriod;
	}

	/**
	 * 妊娠期間 を取得する.
	 * @return 妊娠期間 
	 */
	public int getPregnantPeriod() {
		return pregnantPeriod;
	}

	/**
	 * 妊娠期間 を設定する.
	 * @param pregnantPeriod 妊娠期間 
	 */
	public void setPregnantPeriod(int pregnantPeriod) {
		this.pregnantPeriod = pregnantPeriod;
	}

	/**
	 * 発情期間 を取得する.
	 * @return 発情期間 
	 */
	public int getExcitingPeriod() {
		return excitingPeriod;
	}

	/**
	 * 発情期間 を設定する.
	 * @param excitingPeriod 発情期間 
	 */
	public void setExcitingPeriod(int excitingPeriod) {
		this.excitingPeriod = excitingPeriod;
	}

	/**
	 * 睡眠期間 を取得する.
	 * @return 睡眠期間 
	 */
	public int getSleepingPeriod() {
		return sleepingPeriod;
	}

	/**
	 * 睡眠期間 を設定する.
	 * @param sleepingPeriod 睡眠期間 
	 */
	public void setSleepingPeriod(int sleepingPeriod) {
		this.sleepingPeriod = sleepingPeriod;
	}

	/**
	 * 汚れている期間 を取得する.
	 * @return 汚れている期間 
	 */
	public int getDirtyPeriod() {
		return dirtyPeriod;
	}

	/**
	 * 汚れている期間 を設定する.
	 * @param dirtyPeriod 汚れている期間 
	 */
	public void setDirtyPeriod(int dirtyPeriod) {
		this.dirtyPeriod = dirtyPeriod;
	}

	/**
	 * 汚れて泣き叫ぶ期間 を取得する.
	 * @return 汚れて泣き叫ぶ期間 
	 */
	public int getDirtyScreamPeriod() {
		return dirtyScreamPeriod;
	}

	/**
	 * 汚れて泣き叫ぶ期間 を設定する.
	 * @param dirtyScreamPeriod 汚れて泣き叫ぶ期間 
	 */
	public void setDirtyScreamPeriod(int dirtyScreamPeriod) {
		this.dirtyScreamPeriod = dirtyScreamPeriod;
	}

	/**
	 * ゆかびに侵されている期間 を取得する.
	 * @return ゆかびに侵されている期間 
	 */
	public int getSickPeriod() {
		return sickPeriod;
	}

	/**
	 * ゆかびに侵されている期間 を設定する.
	 * @param sickPeriod ゆかびに侵されている期間 
	 */
	public void setSickPeriod(int sickPeriod) {
		this.sickPeriod = sickPeriod;
	}

	/**
	 * 怒っている期間 を取得する.
	 * @return 怒っている期間 
	 */
	public int getAngryPeriod() {
		return angryPeriod;
	}

	/**
	 * 怒っている期間 を設定する.
	 * @param angryPeriod 怒っている期間 
	 */
	public void setAngryPeriod(int angryPeriod) {
		this.angryPeriod = angryPeriod;
	}

	/**
	 * 怖がっている期間 を取得する.
	 * @return 怖がっている期間 
	 */
	public int getScarePeriod() {
		return scarePeriod;
	}

	/**
	 * 怖がっている期間 を設定する.
	 * @param scarePeriod 怖がっている期間 
	 */
	public void setScarePeriod(int scarePeriod) {
		this.scarePeriod = scarePeriod;
	}

	/**
	 * 悲しんでいる期間 を取得する.
	 * @return 悲しんでいる期間 
	 */
	public int getSadPeriod() {
		return sadPeriod;
	}

	/**
	 * 悲しんでいる期間 を設定する.
	 * @param sadPeriod 悲しんでいる期間 
	 */
	public void setSadPeriod(int sadPeriod) {
		this.sadPeriod = sadPeriod;
	}

	/**
	 * 濡れている期間 
	 * @return 濡れている期間 を取得する.
	 */
	public int getWetPeriod() {
		return wetPeriod;
	}

	/**
	 * 濡れている期間 を設定する.
	 * @param wetPeriod 濡れている期間 
	 */
	public void setWetPeriod(int wetPeriod) {
		this.wetPeriod = wetPeriod;
	}

	/**
	 * パニック状態の期間 を取得する.
	 * @return パニック状態の期間 
	 */
	public int getPanicPeriod() {
		return panicPeriod;
	}

	/**
	 * パニック状態の期間 を設定する.
	 * @param panicPeriod パニック状態の期間 
	 */
	public void setPanicPeriod(int panicPeriod) {
		this.panicPeriod = panicPeriod;
	}

	/**
	 * 足焼きされている期間 を取得する.
	 * @return 足焼きされている期間 
	 */
	public int getFootBakePeriod() {
		return footBakePeriod;
	}

	/**
	 * 足焼きされている期間 を設定する.
	 * @param footBakePeriod 足焼きされている期間 
	 */
	public void setFootBakePeriod(int footBakePeriod) {
		this.footBakePeriod = footBakePeriod;
	}

	/**
	 * 背中を焼かれている期間 を取得する。
	 * @return 背中を焼かれている期間 
	 */
	public int getBodyBakePeriod() {
		return bodyBakePeriod;
	}

	/**
	 * 背中を焼かれている期間を取得する. 
	 * @param bodyBakePeriod 背中を焼かれている期間 
	 */
	public void setBodyBakePeriod(int bodyBakePeriod) {
		this.bodyBakePeriod = bodyBakePeriod;
	}

	/**
	 * 非ゆっくり症にかかっている期間 を取得する.
	 * @return 非ゆっくり症にかかっている期間 
	 */
	public int getNonYukkuriDiseasePeriod() {
		return nonYukkuriDiseasePeriod;
	}

	/**
	 * 非ゆっくり症にかかっている期間 を設定する.
	 * @param nonYukkuriDiseasePeriod 非ゆっくり症にかかっている期間 
	 */
	public void setNonYukkuriDiseasePeriod(int nonYukkuriDiseasePeriod) {
		this.nonYukkuriDiseasePeriod = nonYukkuriDiseasePeriod;
	}

	/**
	 * 死んでからの期間 を取得する.
	 * @return 死んでからの期間 
	 */
	public int getDeadPeriod() {
		return deadPeriod;
	}

	/**
	 * 死んでからの期間 を設定する.
	 * @param deadPeriod 死んでからの期間 
	 */
	public void setDeadPeriod(int deadPeriod) {
		this.deadPeriod = deadPeriod;
	}

	/**
	 * 最後にプレイヤーにすりすりしてもらった時間 を取得する.
	 * @return 最後にプレイヤーにすりすりしてもらった時間 
	 */
	public long getLnLastTimeSurisuri() {
		return lnLastTimeSurisuri;
	}

	/**
	 * 最後にプレイヤーにすりすりしてもらった時間 を設定する.
	 * @param lnLastTimeSurisuri 最後にプレイヤーにすりすりしてもらった時間 
	 */
	public void setLnLastTimeSurisuri(long lnLastTimeSurisuri) {
		this.lnLastTimeSurisuri = lnLastTimeSurisuri;
	}

	/**
	 * 最後にプレイヤーがアクションを行った時間 を取得する.
	 * @return 最後にプレイヤーがアクションを行った時間 
	 */
	public long getInLastActionTime() {
		return inLastActionTime;
	}

	/**
	 * 最後にプレイヤーがアクションを行った時間 を設定する.
	 * @param inLastActionTime 最後にプレイヤーがアクションを行った時間 
	 */
	public void setInLastActionTime(long inLastActionTime) {
		this.inLastActionTime = inLastActionTime;
	}

	/**
	 * 出産期間のブースト（この分だけ早まる） を取得する.
	 * @return 出産期間のブースト（この分だけ早まる） 
	 */
	public int getPregnantPeriodBoost() {
		return pregnantPeriodBoost;
	}

	/**
	 * 出産期間のブースト（この分だけ早まる） を設定する.
	 * @param pregnantPeriodBoost 出産期間のブースト（この分だけ早まる） 
	 */
	public void setPregnantPeriodBoost(int pregnantPeriodBoost) {
		this.pregnantPeriodBoost = pregnantPeriodBoost;
	}

	/**
	 * 発情期間のブースト（この分だけ早まる） を取得する.
	 * @return 発情期間のブースト（この分だけ早まる） 
	 */
	public int getExcitingPeriodBoost() {
		return excitingPeriodBoost;
	}

	/**
	 * 発情期間のブースト（この分だけ早まる） を設定する.
	 * @param excitingPeriodBoost 発情期間のブースト（この分だけ早まる） 
	 */
	public void setExcitingPeriodBoost(int excitingPeriodBoost) {
		this.excitingPeriodBoost = excitingPeriodBoost;
	}

	/**
	 * うんうんブースト を取得する.
	 * @return うんうんブースト 
	 */
	public int getShitBoost() {
		return shitBoost;
	}

	/**
	 * うんうんブースト を設定する.
	 * @param shitBoost うんうんブースト 
	 */
	public void setShitBoost(int shitBoost) {
		this.shitBoost = shitBoost;
	}

	/**
	 * 移動対象（移動先） を取得する.
	 * @return 移動対象（移動先） 
	 */
	public Obj getMoveTarget() {
		return moveTarget;
	}

	/**
	 * 移動対象（移動先） を設定する.
	 * @param moveTarget 移動対象（移動先） 
	 */
	public void setMoveTarget(Obj moveTarget) {
		this.moveTarget = moveTarget;
	}

	/**
	 * 移動対象のX座標オフセット を取得する.
	 * @return 移動対象のX座標オフセット 
	 */
	public int getTargetPosOfsX() {
		return targetPosOfsX;
	}

	/**
	 * 移動対象のX座標オフセット を設定する.
	 * @param targetPosOfsX 移動対象のX座標オフセット 
	 */
	public void setTargetPosOfsX(int targetPosOfsX) {
		this.targetPosOfsX = targetPosOfsX;
	}

	/**
	 * 移動対象のY座標オフセット を取得する.
	 * @return 移動対象のY座標オフセット 
	 */
	public int getTargetPosOfsY() {
		return targetPosOfsY;
	}

	/**
	 * 移動対象のY座標オフセット を設定する.
	 * @param targetPosOfsY 移動対象のY座標オフセット 
	 */
	public void setTargetPosOfsY(int targetPosOfsY) {
		this.targetPosOfsY = targetPosOfsY;
	}

	/**
	 * 対象を呼び止めるほど強い動機を持っているかどうか を取得する.
	 * @return 対象を呼び止めるほど強い動機を持っているかどうか 
	 */
	public boolean isTargetBind() {
		return targetBind;
	}

	/**
	 * 対象を呼び止めるほど強い動機を持っているかどうか を設定する.
	 * @param targetBind 対象を呼び止めるほど強い動機を持っているかどうか 
	 */
	public void setTargetBind(boolean targetBind) {
		this.targetBind = targetBind;
	}

	/**
	 * 移動目的がフードかどうかを取得する.
	 * @return 移動目的がフードかどうか
	 */
	public boolean isToFood() {
		return purposeOfMoving == PurposeOfMoving.FOOD;
	}

	/**
	 * 移動目的がフードかどうかを設定する.
	 * @param b 移動目的がフードかどうか
	 */
	public void setToFood(boolean b) {
		if (b) {
			purposeOfMoving = PurposeOfMoving.FOOD;
		} else if (purposeOfMoving == PurposeOfMoving.FOOD) {
			purposeOfMoving = PurposeOfMoving.NONE;
		}
	}

	/**
	 * 移動目的がすっきりかどうかを取得する.
	 * @return 移動目的がすっきりかどうか
	 */
	public boolean isToSukkiri() {
		return purposeOfMoving == PurposeOfMoving.SUKKIRI;
	}

	/**
	 * 移動目的がすっきりかどうかを設定する.
	 * @param b 移動目的がすっきりかどうか
	 */
	public void setToSukkiri(boolean b) {
		if (b) {
			purposeOfMoving = PurposeOfMoving.SUKKIRI;
		} else if (purposeOfMoving == PurposeOfMoving.SUKKIRI) {
			purposeOfMoving = PurposeOfMoving.NONE;
		}
	}

	/**
	 * 移動目的がうんうんかどうかを取得する.
	 * @return 移動目的がうんうんかどうか
	 */
	public boolean isToShit() {
		return purposeOfMoving == PurposeOfMoving.SHIT;
	}

	/**
	 * 移動目的がうんうんかどうかを設定する.
	 * @param flag 移動目的がうんうんかどうか
	 */
	public void setToShit(boolean flag) {
		if (flag) {
			purposeOfMoving = PurposeOfMoving.SHIT;
		} else if (purposeOfMoving == PurposeOfMoving.SHIT) {
			purposeOfMoving = PurposeOfMoving.NONE;
		}
	}

	/**
	 * 移動目的がベッドかどうかを取得する.
	 * @return 移動目的がベッドかどうか
	 */
	public boolean isToBed() {
		return purposeOfMoving == PurposeOfMoving.BED;
	}

	/**
	 * 移動目的がベッドかどうかを設定する.
	 * @param flag 移動目的がベッドかどうか
	 */
	public void setToBed(boolean flag) {
		if (flag) {
			purposeOfMoving = PurposeOfMoving.BED;
		} else if (purposeOfMoving == PurposeOfMoving.BED) {
			purposeOfMoving = PurposeOfMoving.NONE;
		}
	}

	/**
	 * 移動目的が他のゆっくりかどうかを取得する.
	 * @return 移動目的が他のゆっくりかどうか
	 */
	public boolean isToBody() {
		return purposeOfMoving == PurposeOfMoving.YUKKURI;
	}

	/**
	 * 移動目的が他のゆっくりかどうかを設定する.
	 * @param flag 移動目的が他のゆっくりかどうか
	 */
	public void setToBody(boolean flag) {
		if (flag) {
			purposeOfMoving = PurposeOfMoving.YUKKURI;
		} else if (purposeOfMoving == PurposeOfMoving.YUKKURI) {
			purposeOfMoving = PurposeOfMoving.NONE;
		}
	}

	/**
	 * 移動目的がおかざりを盗むためかどうかを取得する.
	 * @return 移動目的がおかざりを盗むためかどうか
	 */
	public boolean isToSteal() {
		return purposeOfMoving == PurposeOfMoving.STEAL;
	}

	/**
	 * 移動目的がおかざりを盗むためかどうかを設定する.
	 * @param flag 移動目的がおかざりを盗むためかどうか
	 */
	public void setToSteal(boolean flag) {
		if (flag) {
			purposeOfMoving = PurposeOfMoving.STEAL;
		} else if (purposeOfMoving == PurposeOfMoving.STEAL) {
			purposeOfMoving = PurposeOfMoving.NONE;
		}
	}

	/**
	 * 移動目的がアイテムを持つことかどうかを取得する.
	 * @return 移動目的がアイテムを持つことかどうか
	 */
	public boolean isToTakeout() {
		return purposeOfMoving == PurposeOfMoving.TAKEOUT;
	}

	/**
	 * 移動目的がアイテムを持つことかどうかを設定する.
	 * @param flag 移動目的がアイテムを持つことかどうか
	 */
	public void setToTakeout(boolean flag) {
		if (flag) {
			purposeOfMoving = PurposeOfMoving.TAKEOUT;
		} else if (purposeOfMoving == PurposeOfMoving.TAKEOUT) {
			purposeOfMoving = PurposeOfMoving.NONE;
		}
	}

	/**移動目標のみキャンセル*/
	public final void clearTargets() {
		purposeOfMoving = PurposeOfMoving.NONE;
		stopStaying();
	}

	/**とどまるのをやめる
	 * @see #stay()
	 * @see #stay(int)
	 * @see ##stayPurupuru()
	 * @see #stayPurupuru(int)
	 * @see #isStaying()
	 * @see #stopStaying()
	 * */
	public final void stopStaying() {
		staycount = 0;
	}

	/**
	 * アイテムを出し入れする動作フラグ を取得する.
	 * @return アイテムを出し入れする動作フラグ 
	 */
	public boolean isInOutTakeoutItem() {
		return inOutTakeoutItem;
	}

	/**
	 * アイテムを出し入れする動作フラグ を設定する.
	 * @param bIsInOutTakeoutItem アイテムを出し入れする動作フラグ 
	 */
	public void setInOutTakeoutItem(boolean bIsInOutTakeoutItem) {
		this.inOutTakeoutItem = bIsInOutTakeoutItem;
	}

	/**
	 * 待機アクション中かどうかを取得する.
	 * @return 待機アクション中かどうか 
	 */
	public boolean isStaying() {
		return staying;
	}

	/**
	 * 待機アクション中かどうかを設定する.
	 * @param staying 待機アクション中かどうか
	 */
	public void setStaying(boolean staying) {
		this.staying = staying;
	}

	/**
	 * うんうんアクション中 かどうかを設定する.
	 * @param shitting うんうんアクション中 かどうか
	 */
	public void setShitting(boolean shitting) {
		this.shitting = shitting;
	}

	/**
	 * 誕生済みか否か を設定する.
	 * @param birth 誕生済みか否か 
	 */
	public void setBirth(boolean birth) {
		this.birth = birth;
	}

	/**
	 * 怒っているか否か を設定する.
	 * @param angry 怒っているか否か 
	 */
	public void setAngry(boolean angry) {
		this.angry = angry;
	}

	/**
	 * ふりふりアクション中 かどうかを設定する.
	 * @param furifuri ふりふりアクション中 かどうか
	 */
	public void setFurifuri(boolean furifuri) {
		this.furifuri = furifuri;
	}

	/**
	 * 攻撃アクション中 かどうかを設定する.
	 * @param strike 攻撃アクション中 かどうか
	 */
	public void setStrike(boolean strike) {
		this.strike = strike;
	}

	/**
	 * 何かを食べ中 かどうかを設定する.
	 * @param eating 何かを食べ中 かどうか
	 */
	public void setEating(boolean eating) {
		this.eating = eating;
	}

	/**
	 * ぺろぺろ中 かどうかを設定する.
	 * @param peropero ぺろぺろ中 かどうか
	 */
	public void setPeropero(boolean peropero) {
		this.peropero = peropero;
	}

	/**
	 * すっきり中 かどうかを設定する.
	 * @param sukkiri すっきり中 かどうか
	 */
	public void setSukkiri(boolean sukkiri) {
		this.sukkiri = sukkiri;
	}

	/**
	 * 怖がり中 かどうかを設定する.
	 * @param scare 怖がり中 かどうか
	 */
	public void setScare(boolean scare) {
		this.scare = scare;
	}

	/**
	 * うんうんを食べ中 かどうかを設定する.
	 * @param eatingShit うんうんを食べ中 かどうか
	 */
	public void setEatingShit(boolean eatingShit) {
		this.eatingShit = eatingShit;
	}

	/**
	 * 沈黙フラグ を取得する.
	 * @return 沈黙フラグ 
	 */
	public boolean isSilent() {
		return silent;
	}

	/**
	 * 沈黙フラグ を設定する.
	 * @param silent 沈黙フラグ 
	 */
	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	/**
	 * 口ふさがれ中 かどうかを取得する.
	 * @return 口ふさがれ中 かどうか
	 */
	public boolean isShutmouth() {
		return shutmouth;
	}

	/**
	 * 口ふさがれ中 かどうかを設定する.
	 * @param shutmouth 口ふさがれ中 かどうか
	 */
	public void setShutmouth(boolean shutmouth) {
		this.shutmouth = shutmouth;
	}

	/**
	 * キリッ！中 かどうかを設定する.
	 * @param beVain キリッ！中 かどうか
	 */
	public void setBeVain(boolean beVain) {
		this.beVain = beVain;
	}

	/**
	 * ぴこぴこ中 かどうかを取得する.
	 * @return ぴこぴこ中 かどうか
	 */
	public boolean isPikopiko() {
		return pikopiko;
	}

	/**
	 * ぴこぴこ中 かどうかを設定する.
	 * @param pikopiko ぴこぴこ中 かどうか
	 */
	public void setPikopiko(boolean pikopiko) {
		this.pikopiko = pikopiko;
	}

	/**
	 * ぷるぷる中 かどうかを取得する.
	 * @return ぷるぷる中 かどうか
	 */
	public boolean isPurupuru() {
		return purupuru;
	}

	/**
	 * ぷるぷる中 かどうかを設定する.
	 * @param purupuru ぷるぷる中 かどうか
	 */
	public void setPurupuru(boolean purupuru) {
		this.purupuru = purupuru;
	}

	/**
	 * 親を呼んで泣き叫び中 かどうかを設定する.
	 * @param callingParents 親を呼んで泣き叫び中 かどうか
	 */
	public void setCallingParents(boolean callingParents) {
		this.callingParents = callingParents;
	}

	/**
	 * ゆんやぁ中 かどうかを設定する.
	 * @param yunnyaa ゆんやぁ中 かどうか
	 */
	public void setYunnyaa(boolean yunnyaa) {
		this.yunnyaa = yunnyaa;
	}

	/**
	 * 何で遊んでいるか を取得する.
	 * @return 何で遊んでいるか 
	 */
	public PlayStyle getPlaying() {
		return playing;
	}

	/**
	 * 何で遊んでいるか を設定する.
	 * @param playing 何で遊んでいるか 
	 */
	public void setPlaying(PlayStyle playing) {
		this.playing = playing;
	}

	/**
	 * 遊び時間上限 を取得する.
	 * @return 遊び時間上限 
	 */
	public int getPlayingLimit() {
		return playingLimit;
	}

	/**
	 * 遊び時間上限 を設定する.
	 * @param playingLimit 遊び時間上限 
	 */
	public void setPlayingLimit(int playingLimit) {
		this.playingLimit = playingLimit;
	}

	/**
	 * メッセージのバッファ を取得する.
	 * @return メッセージのバッファ 
	 */
	public String getMessageBuf() {
		return messageBuf;
	}

	/**
	 * メッセージのバッファ を設定する.
	 * @param messageBuf メッセージのバッファ 
	 */
	public void setMessageBuf(String messageBuf) {
		this.messageBuf = messageBuf;
	}

	/**
	 * いくつメッセージが溜まってるか を取得する.
	 * @return いくつメッセージが溜まってるか 
	 */
	public int getMessageCount() {
		return messageCount;
	}

	/**
	 * いくつメッセージが溜まってるか を設定する.
	 * @param messageCount いくつメッセージが溜まってるか 
	 */
	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}

	/**
	 * その場に留まってる回数 を取得する.
	 * @return その場に留まってる回数 
	 */
	public int getStaycount() {
		return staycount;
	}

	/**
	 * その場に留まってる回数 を設定する.
	 * @param staycount その場に留まってる回数 
	 */
	public void setStaycount(int staycount) {
		this.staycount = staycount;
	}

	/**
	 * とどまる限界 を取得する.
	 * @return とどまる限界 
	 */
	public int getStayTime() {
		return stayTime;
	}

	/**
	 * とどまる限界 を設定する.
	 * @param stayTime とどまる限界 
	 */
	public void setStayTime(int stayTime) {
		this.stayTime = stayTime;
	}

	/**
	 * 落下ダメージ を取得する.
	 * @return 落下ダメージ 
	 */
	public int getFalldownDamage() {
		return falldownDamage;
	}

	/**
	 * 落下ダメージ を設定する.
	 * @param falldownDamage 落下ダメージ 
	 */
	public void setFalldownDamage(int falldownDamage) {
		this.falldownDamage = falldownDamage;
	}

	/**
	 * あんこ量 を取得する.
	 * @return あんこ量 
	 */
	public int getBodyAmount() {
		return bodyAmount;
	}

	/**
	 * あんこ量 を設定する.
	 * @param bodyAmount あんこ量 
	 */
	public void setBodyAmount(int bodyAmount) {
		this.bodyAmount = bodyAmount;
	}

	/**
	 * 壁に引っかかった回数 を取得する.
	 * @return 壁に引っかかった回数 
	 */
	public int getBlockedCount() {
		return blockedCount;
	}

	/**
	 * 壁に引っかかった回数 を設定する.
	 * @param blockedCount 壁に引っかかった回数 
	 */
	public void setBlockedCount(int blockedCount) {
		this.blockedCount = blockedCount;
	}

	/**
	 * 死なない期間 を取得する.
	 * @return 死なない期間 
	 */
	public int getCantDiePeriod() {
		return cantDiePeriod;
	}

	/**
	 * 死なない期間 を設定する.
	 * @param cantDiePeriod 死なない期間 
	 */
	public void setCantDiePeriod(int cantDiePeriod) {
		this.cantDiePeriod = cantDiePeriod;
	}

	/**
	 * 実ゆかどうか を取得する.
	 * @return 実ゆかどうか 
	 */
	public boolean isUnBirth() {
		return unBirth;
	}

	/**
	 * 実ゆかどうか を設定する.
	 * @param unBirth 実ゆかどうか 
	 */
	public void setUnBirth(boolean unBirth) {
		this.unBirth = unBirth;
	}

	/**
	 * 喋れる状態かどうか を取得する.
	 * @return 喋れる状態かどうか 
	 */
	public boolean isCanTalk() {
		return canTalk;
	}

	/**
	 * 喋れる状態かどうか を設定する.
	 * @param canTalk 喋れる状態かどうか 
	 */
	public void setCanTalk(boolean canTalk) {
		this.canTalk = canTalk;
	}

	/**
	 * メッセージラインの色 を取得する.
	 * @return メッセージラインの色 
	 */
	public Color getMessageLineColor() {
		return messageLineColor;
	}

	/**
	 * メッセージラインの色 を設定する.
	 * @param messageLineColor メッセージラインの色 
	 */
	public void setMessageLineColor(Color messageLineColor) {
		this.messageLineColor = messageLineColor;
	}

	/**
	 * メッセージボックスの色 を取得する.
	 * @return メッセージボックスの色
	 */
	public Color getMessageBoxColor() {
		return messageBoxColor;
	}

	/**
	 * メッセージボックスの色 を設定する.
	 * @param messageBoxColor メッセージボックスの色 
	 */
	public void setMessageBoxColor(Color messageBoxColor) {
		this.messageBoxColor = messageBoxColor;
	}

	/**
	 * メッセージテキストの色 を取得する.
	 * @return メッセージテキストの色 
	 */
	public Color getMessageTextColor() {
		return messageTextColor;
	}

	/**
	 * メッセージテキストの色 を設定する.
	 * @param messageTextColor メッセージテキストの色 
	 */
	public void setMessageTextColor(Color messageTextColor) {
		this.messageTextColor = messageTextColor;
	}

	public BasicStrokeEX getMessageWindowStroke() {
		return messageWindowStroke;
	}

	public void setMessageWindowStroke(BasicStrokeEX messageWindowStroke) {
		this.messageWindowStroke = messageWindowStroke;
	}

	/**
	 * メッセージテキストのサイズ を取得する.
	 * @return メッセージテキストのサイズ 
	 */
	public int getMessageTextSize() {
		return messageTextSize;
	}

	/**
	 * メッセージテキストのサイズ を設定する.
	 * @param messageTextSize メッセージテキストのサイズ 
	 */
	public void setMessageTextSize(int messageTextSize) {
		this.messageTextSize = messageTextSize;
	}

	/**
	 * 強制的に誕生時メッセージを言わされるかどうか を取得する.
	 * @return 強制的に誕生時メッセージを言わされるかどうか 
	 */
	public boolean isForceBirthMessage() {
		return forceBirthMessage;
	}

	/**
	 * 強制的に誕生時メッセージを言わされるかどうか を設定する.
	 * @param forceBirthMessage 強制的に誕生時メッセージを言わされるかどうか 
	 */
	public void setForceBirthMessage(boolean forceBirthMessage) {
		this.forceBirthMessage = forceBirthMessage;
	}

	/**
	 * ゆっくりのオブジェクトのユニークIDを取得する.
	 * @return ゆっくりのオブジェクトのユニークID
	 */
	public int getUniqueID() {
		return uniqueID;
	}

	/**
	 * ゆっくりのオブジェクトのユニークIDを設定する.
	 * @param uniqueID ゆっくりのオブジェクトのユニークID
	 */
	public void setUniqueID(int uniqueID) {
		this.uniqueID = uniqueID;
	}

	/**
	 * どのゆっくり的なメッセージを言うか を取得する.
	 * @return どのゆっくり的なメッセージを言うか 
	 */
	public YukkuriType getMsgType() {
		return msgType;
	}

	/**
	 * どのゆっくり的なメッセージを言うか を設定する.
	 * @param msgType どのゆっくり的なメッセージを言うか 
	 */
	public void setMsgType(YukkuriType msgType) {
		this.msgType = msgType;
	}

	/**
	 * どのゆっくり的なうんうんをするか を取得する.
	 * @return どのゆっくり的なうんうんをするか 
	 */
	public YukkuriType getShitType() {
		return shitType;
	}

	/**
	 * どのゆっくり的なうんうんをするか を設定する.
	 * @param shitType どのゆっくり的なうんうんをするか 
	 */
	public void setShitType(YukkuriType shitType) {
		this.shitType = shitType;
	}

	/**
	 * 右ペインメニューのピン留めをされているかどうかを取得する.
	 * @return 右ペインメニューのピン留めをされているかどうか 
	 */
	public boolean isPin() {
		return pin;
	}

	/**
	 * 右ペインメニューのピン留めをされているかどうか を設定する.
	 * @param pin 右ペインメニューのピン留めをされているかどうか 
	 */
	public void setPin(boolean pin) {
		this.pin = pin;
	}

	/**
	 * ゆっくりの移動速度 を取得する.
	 * @return ゆっくりの移動速度 
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * ゆっくりの移動速度 を設定する.
	 * @param speed ゆっくりの移動速度 
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/**
	 * 次の落下でダメージを受けないかどうか を取得する.
	 * @return 次の落下でダメージを受けないかどうか 
	 */
	public boolean isbNoDamageNextFall() {
		return bNoDamageNextFall;
	}

	/**
	 * 次の落下でダメージを受けないかどうか を設定する.
	 * @param bNoDamageNextFall 次の落下でダメージを受けないかどうか 
	 */
	public void setbNoDamageNextFall(boolean bNoDamageNextFall) {
		this.bNoDamageNextFall = bNoDamageNextFall;
	}

	/**
	 * この個体に対して発行されたイベントのリスト を取得する.
	 * @return この個体に対して発行されたイベントのリスト 
	 */
	public List<EventPacket> getEventList() {
		return eventList;
	}

	/**
	 * この個体に対して発行されたイベントのリスト を設定する.
	 * @param eventList この個体に対して発行されたイベントのリスト 
	 */
	public void setEventList(List<EventPacket> eventList) {
		this.eventList = eventList;
	}

	/**
	 * 現在実行中のイベント を取得する.
	 * @return 現在実行中のイベント 
	 */
	public EventPacket getCurrentEvent() {
		return currentEvent;
	}

	/**
	 * 現在実行中のイベント を設定する.
	 * @param currentEvent 現在実行中のイベント 
	 */
	public void setCurrentEvent(EventPacket currentEvent) {
		this.currentEvent = currentEvent;
	}

	/**
	 * 表情の強制設定 を取得する.
	 * @return 表情の強制設定 
	 */
	public int getForceFace() {
		return forceFace;
	}

	/**
	 * 表情の強制設定 を設定する.
	 * @param forceFace 表情の強制設定 
	 */
	public void setForceFace(int forceFace) {
		this.forceFace = forceFace;
	}

	/**
	 * 影の表示有無 を取得する.
	 * @return 影の表示有無 
	 */
	public boolean isDropShadow() {
		return dropShadow;
	}

	/**
	 * 影の表示有無 を設定する.
	 * @param dropShadow 影の表示有無 
	 */
	public void setDropShadow(boolean dropShadow) {
		this.dropShadow = dropShadow;
	}

	/**
	 * イベントで設定されたアクション を取得する.
	 * @return イベントで設定されたアクション 
	 */
	public Event getEventResultAction() {
		return eventResultAction;
	}

	/**
	 * イベントで設定されたアクション を設定する.
	 * @param eventResultAction イベントで設定されたアクション 
	 */
	public void setEventResultAction(Event eventResultAction) {
		this.eventResultAction = eventResultAction;
	}

	/**
	 * ゆ虐神拳により 膨らんでいるか/伸ばされているか/押さえつけられているか を取得する.
	 * @return ゆ虐神拳により 膨らんでいるか/伸ばされているか/押さえつけられているか 
	 */
	public boolean[] getAbFlagGodHand() {
		return abFlagGodHand;
	}

	/**
	 * ゆ虐神拳により 膨らんでいるか/伸ばされているか/押さえつけられているか を設定する.
	 * @param abFlagGodHand ゆ虐神拳により 膨らんでいるか/伸ばされているか/押さえつけられているか 
	 */
	public void setAbFlagGodHand(boolean[] abFlagGodHand) {
		this.abFlagGodHand = abFlagGodHand;
	}

	/**
	 * TODO:ゆ虐神拳の回数？ を取得する.
	 * @return TODO:ゆ虐神拳の回数？ 
	 */
	public int[] getAnGodHandPoint() {
		return anGodHandPoint;
	}

	/**
	 * TODO:ゆ虐神拳の回数？ を設定する.
	 * @param anGodHandPoint TODO:ゆ虐神拳の回数？ 
	 */
	public void setAnGodHandPoint(int[] anGodHandPoint) {
		this.anGodHandPoint = anGodHandPoint;
	}

	/**
	 * お気に入りアイテム を取得する.
	 * @return お気に入りアイテム 
	 */
	public HashMap<FavItemType, Obj> getFavItem() {
		return favItem;
	}

	/**
	 * お気に入りアイテム を設定する.
	 * @param favItem お気に入りアイテム 
	 */
	public void setFavItem(HashMap<FavItemType, Obj> favItem) {
		this.favItem = favItem;
	}

	/**
	 * 持ち歩きアイテム を取得する.
	 * @return 持ち歩きアイテム 
	 */
	public HashMap<TakeoutItemType, Obj> getTakeoutItem() {
		return takeoutItem;
	}

	/**
	 * 持ち歩きアイテム を設定する.
	 * @param takeoutItem 持ち歩きアイテム 
	 */
	public void setTakeoutItem(HashMap<TakeoutItemType, Obj> takeoutItem) {
		this.takeoutItem = takeoutItem;
	}

	/**
	 * ゆっくり本体の購入基本額 を取得する.
	 * @return ゆっくり本体の購入基本額 
	 */
	public int getYcost() {
		return Ycost;
	}

	/**
	 * ゆっくり本体の購入基本額 を設定する.
	 * @param ycost ゆっくり本体の購入基本額 
	 */
	public void setYcost(int ycost) {
		Ycost = ycost;
	}

	/**
	 * ゆっくり本体、中身の売却基本額　飼いゆとしての価値/加工品としての価値 を取得する.
	 * @return ゆっくり本体、中身の売却基本額　飼いゆとしての価値/加工品としての価値 
	 */
	public int[] getSaleValue() {
		return saleValue;
	}

	/**
	 * ゆっくり本体、中身の売却基本額　飼いゆとしての価値/加工品としての価値 を設定する.
	 * @param value
	 */
	public void setSaleValue(int[] value) {
		saleValue = value;
	}

	/**
	 * たかっているアリの数 を取得する.
	 * @return たかっているアリの数 
	 */
	public int getNumOfAnts() {
		return numOfAnts;
	}

	/**
	 * たかっているアリの数 を設定する.
	 * @param numOfAnts たかっているアリの数 
	 */
	public void setNumOfAnts(int numOfAnts) {
		if (numOfAnts < 0) {
			this.numOfAnts = 0;
		} else {
			this.numOfAnts = numOfAnts;
		}
	}

	/**
	 * うにょ機能を使用するかどうかのフラグ を取得する.
	 * @return うにょ機能を使用するかどうかのフラグ 
	 */
	public int getUnyoFlg() {
		return unyoFlg;
	}

	/**
	 * うにょ機能を使用するかどうかのフラグ を設定する.
	 * @param unyoFlg うにょ機能を使用するかどうかのフラグ 
	 */
	public void setUnyoFlg(int unyoFlg) {
		this.unyoFlg = unyoFlg;
	}

	/**
	 * うにょの高さ方向 を取得する.
	 * @return うにょの高さ方向 
	 */
	public int getUnyoForceH() {
		return unyoForceH;
	}

	/**
	 * うにょの高さ方向 を設定する.
	 * @param unyoForceH うにょの高さ方向 
	 */
	public void setUnyoForceH(int unyoForceH) {
		this.unyoForceH = unyoForceH;
	}

	/**
	 * うにょの横方向 を取得する.
	 * @return うにょの横方向 
	 */
	public int getUnyoForceW() {
		return unyoForceW;
	}

	/**
	 * うにょの横方向 を設定する.
	 * @param unyoForceW うにょの横方向 
	 */
	public void setUnyoForceW(int unyoForceW) {
		this.unyoForceW = unyoForceW;
	}

	/**
	 * ランダムのもと を取得する.
	 * @return ランダムのもと 
	 */
	public static Random getRnd() {
		return RND;
	}

	/**
	 * うにょの動きの強さ を取得する.
	 * @return
	 */
	public static int[] getUnyostrength() {
		return UNYOSTRENGTH;
	}

	/**
	 *  最後に行動した時間を設定する.
	 */
	public void setLastActionTime() {
		long lnNowTime = System.currentTimeMillis();
		inLastActionTime = lnNowTime;
	}

	/**
	 * 売却額を返却する.
	 * @param F 0が飼いゆ、1が加工品
	 * @return 売却額
	 */
	public int getSellingPrice(int F) {
		//Fが0だと飼いゆとして、1だと加工品としての価値を返す
		return saleValue[F];
	}

	/**
	 * 死ねないように設定する.
	 * 具体的には、死ねない期間に3を設定する.
	 */
	public void setCantDie() {
		cantDiePeriod = 3;
	}

	/**
	 *  妹の数を取得する.
	 * @return 妹の数
	 */
	public int getSisterListSize() {
		return sisterList.size();
	}

	/**
	 * 妹のインスタンスを取得する.
	 * @param nIndex 何番目の妹か
	 * @return 妹のインスタンス
	 */
	public Body getSister(int nIndex) {
		return sisterList.get(nIndex);
	}

	/**
	 *  姉の数を取得する.
	 * @return 姉の数
	 */
	public int getElderSisterListSize() {
		return elderSisterList.size();
	}

	/**
	 *  姉のインスタンスを取得する.
	 * @param nIndex 何番目の姉か
	 * @return 姉のインスタンス
	 */
	public Body getElderSister(int nIndex) {
		return elderSisterList.get(nIndex);
	}

	/**
	 *  子供の数を取得する.
	 * @return 子供の数
	 */
	public int getChildrenListSize() {
		if (childrenList == null) {
			return 0;
		}
		return childrenList.size();
	}

	/**
	 *  子のインスタンスを取得する.
	 * @param nIndex 何番目の子か
	 * @return 子のインスタンス
	 */
	public Body getChildren(int nIndex) {
		if (childrenList == null) {
			return null;
		}
		return childrenList.get(nIndex);
	}

	/**
	 * 身体年齢を設定し、同時に存続期間を設定する.
	 * @param setAgeState ゆっくりの成長段階
	 */
	public void setAgeState(AgeState setAgeState) {
		if (setAgeState == AgeState.BABY) {
			setAge(0);
		} else if (setAgeState == AgeState.CHILD) {
			setAge(BABYLIMIT);
		} else if (setAgeState == AgeState.ADULT) {
			setAge(CHILDLIMIT);
		}
	}

	/**
	 * ゆ下痢かどうかを返却する.
	 * @return ゆ下痢かどうか
	 */
	public boolean getDiarrhea() {
		//飼いゆだったら無条件で下す
		if (getBodyRank() == BodyRank.KAIYU)
			return true;
		int P = diarrheaProb;
		//かび、ダメージ有なら確率2倍
		if (isSick() || isDamaged())
			P /= 2;
		if (P < 1)
			P = 1;
		return (RND.nextInt(P) == 0);
	}

	/**
	 * ダメージなしかどうかを返却する.
	 * @return ダメージなしかどうか
	 */
	public boolean isNoDamaged() {
		return (getDamageState() == Damage.NONE);
	}

	/**
	 * 軽いダメージかどうかを返却する.
	 * @return 軽いダメージかどうか
	 */
	public boolean isDamagedLightly() {
		return (getDamageState() == Damage.SOME || getDamageState() == Damage.VERY
				|| getDamageState() == Damage.TOOMUCH);
	}

	/**
	 * ダメージを受けているかどうかを返却する.
	 * @return ダメージを受けているかどうか
	 */
	public boolean isDamaged() {
		return (getDamageState() == Damage.VERY || getDamageState() == Damage.TOOMUCH);
	}

	/**
	 * 重いダメージを受けているかどうかを返却する.
	 * @return 重いダメージかどうか
	 */
	public boolean isDamagedHeavily() {
		return (getDamageState() == Damage.TOOMUCH);
	}

	/**
	 * 命乞い中かどうかを設定する.
	 * 埋まっていないときが条件。
	 * @param k 命乞い中かどうか
	 */
	public void setBegging(boolean k) {
		if (baryState == BaryInUGState.NONE)
			begging = k;
	}

	/**
	 * 命乞い中かどうかを取得する.
	 * 死んでいないことが条件。
	 * @return 命乞い中かどうか
	 */
	public boolean isBeggingForLife() {
		return (!dead && begging);
	}

	/**
	 * 攻撃アクション中かどうかを取得する.
	 * 死んでいないことが条件.
	 * @return 攻撃アクション中かどうか
	 */
	public boolean isStrike() {
		return (!dead && strike);
	}

	/**
	 * 痛みを感じているかどうかを返却する.
	 * @return 痛みを感じているか
	 */
	public boolean isFeelPain() {
		return (getPainState() == Pain.VERY || getPainState() == Pain.SOME);
	}

	/**
	 * 激しい痛みを感じているかどうかを取得する.
	 * @return 激しい痛みを感じているかどうか
	 */
	public boolean isFeelHardPain() {
		return (getPainState() == Pain.VERY);
	}

	/**
	 * 誕生済みか否かを返却する.
	 * 死んでいないことが条件.
	 * @return 誕生済みか否か
	 */
	public boolean isBirth() {
		return (!dead && birth);
	}

	/**
	 * 食事中か否かを返却する.
	 * 死んでいないことが条件.
	 * @return 食事中か否か
	 */
	public boolean isEating() {
		return (!dead && eating);
	}

	/**
	 * うんうん食い中か否かを返却する.
	 * 死んでいないことが条件.
	 * @return うんうん食い中か否か
	 */
	public boolean isEatingShit() {
		return (!dead && eatingShit);
	}

	/**
	 * ぺろぺろ中か否かを返却する.
	 * @return ぺろぺろ中か否か
	 */
	public boolean isPeroPero() {
		return (!dead && peropero);
	}

	/**
	 * すっきり中か否かを返却する.
	 * 死んでいないことが条件.
	 * @return すっきり中か否か
	 */
	public boolean isSukkiri() {
		return (!dead && sukkiri);
	}

	/**
	 * 針でさされ中か否かを返却する.
	 * 死んでいないことが条件.
	 * @return 針でさされ中か否か
	 */
	public boolean isNeedled() {
		return (!dead && bNeedled);
	}

	/**
	 * ドゲスか否かを返却する.
	 * @return ドゲスか否か
	 */
	public boolean isVeryRude() {
		return (attitude == Attitude.SUPER_SHITHEAD);
	}

	/**
	 * ゲスまたはドゲスか否かを返却する.
	 * @return ゲスまたはドゲスか否か
	 */
	public boolean isRude() {
		return (attitude == Attitude.SHITHEAD || attitude == Attitude.SUPER_SHITHEAD);
	}

	/**
	 * ゲス/善良の区分で普通か否かを返却する.
	 * @return ゲス/善良の区分で普通か否か
	 */
	public boolean isNormal() {
		return (attitude == Attitude.AVERAGE);
	}

	/**
	 * 善良または超善良か否かを返却する.
	 * @return 善良または超善良か否か
	 */
	public boolean isSmart() {
		return (attitude == Attitude.VERY_NICE || attitude == Attitude.NICE);
	}

	/**
	 *  父親を取得
	 * @return 父親のインスタンス
	 */
	public Body getFather() {
		return parents[Parent.PAPA.ordinal()];
	}

	/**
	 * 母親を取得
	 * @return 母親のインスタンス
	 */
	public Body getMother() {
		return parents[Parent.MAMA.ordinal()];
	}

	/**
	 * この個体の痛みを感じている程度から、相当するPain(Enum)を返却する.
	 * @return この個体に相当するPain
	 */
	public Pain getPainState() {
		if (getBurstState() == Burst.NEAR || getBurstState() == Burst.BURST || isNeedled()) {
			return Pain.VERY;
		}
		if (getBurstState() == Burst.HALF || criticalDamege != null) {
			return Pain.SOME;
		}
		return Pain.NONE;
	}

	/**
	 * この個体がどれくらい破裂しそうか、相当するBurst(Enum)を返却する.
	 * @return この個体に相当するBurst
	 */
	public Burst getBurstState() {
		if (getSize() * 4 / getOriginSize() >= 8) {
			return Burst.BURST;
		} else if (getSize() * 4 / getOriginSize() >= 7) {
			return Burst.NEAR;
		} else if (getSize() * 4 / getOriginSize() >= 6) {
			return Burst.HALF;
		} else if (getSize() * 4 / getOriginSize() >= 5) {
			return Burst.SAFE;
		}
		return Burst.NONE;
	}

	/**
	 * 画像上のゆっくりの大きさを取得する.
	 * @return 画像上のゆっくりの大きさ
	 */
	public int getSize() {
		if (SimYukkuri.UNYO) {
			return bodySpr[getBodyAgeState().ordinal()].imageW + getExpandSizeW() + unyoForceW;
		}
		return bodySpr[getBodyAgeState().ordinal()].imageW + getExpandSizeW();
	}

	/**
	 * 画像上のゆっくりのオリジナルサイズを取得する.
	 * @return 画像上のゆっくりのオリジナルサイズ
	 */
	public int getOriginSize() {
		return bodySpr[getBodyAgeState().ordinal()].imageW;
	}

	/**
	 * 妊娠、うんうん、過食などによる横方向の体型のふくらみ取得
	 * @return 妊娠、うんうん、過食などによる横方向の体型のふくらみ
	 */
	public int getExpandSizeW() {
		return 0;
	}

	/**
	 * 妊娠、うんうんなどによる縦方向の体型のふくらみ取得
	 * @return 妊娠、うんうんなどによる縦方向の体型のふくらみ
	 */
	public int getExpandSizeH() {
		return 0;
	}

	/**
	 * この個体の属する、体のAgeState(Enum)を返却する.
	 * @return この個体の属する、体のAgeState
	 */
	public AgeState getBodyAgeState() {
		if (getAge() < BABYLIMIT) {
			return AgeState.BABY;
		} else if (getAge() < CHILDLIMIT) {
			return AgeState.CHILD;
		}
		return AgeState.ADULT;
	}

	/**
	 * この個体の属する、精神のAgeState(Enum)を返却する.
	 * @return この個体の属する、精神のAgeState
	 */
	public AgeState getMindAgeState() {
		return getBodyAgeState();
	}

	/**
	 * この個体のダメージ具合から、相当するDamage(Enum)を返却する.
	 * @return この個体のダメージ具合から計算した、相当するDamage(Enum)
	 */
	public Damage getDamageState() {
		if (damage > DAMAGELIMIT[getBodyAgeState().ordinal()]) {
			toDead();
			return Damage.TOOMUCH;
		}
		if (damage >= DAMAGELIMIT[getBodyAgeState().ordinal()] * 3 / 4) {
			return Damage.TOOMUCH;
		}
		if (damage >= DAMAGELIMIT[getBodyAgeState().ordinal()] / 2) {
			return Damage.VERY;
		}
		return Damage.NONE;
	}

	/**
	 * 死に向かわせる
	 */
	public void toDead() {
		if (!isCantDie() && !dead) {
			dead = true;
			anGodHandPoint[0] = 0;// 死んだらゆ虐神拳1をリセット
		}
	}

	/**
	 * 死ねない期間中かどうかを取得する.
	 * @return 死ねない期間中かどうか
	 */
	public boolean isCantDie() {
		return (cantDiePeriod > 0);
	}

	/**
	 * 破裂しているかどうかを取得する.
	 * @return 破裂しているかどうか
	 */
	public boolean isBurst() {
		return (getBurstState() == Burst.BURST);
	}

	/**
	 * まさに破裂するところかどうかを取得する.
	 * @return まさに破裂するところかどうか
	 */
	public boolean isAboutToBurst() {
		return (getBurstState() == Burst.NEAR);
	}

	/**
	 * 破裂状態が通常でないかどうかを取得する.
	 * @return 破裂状態が通常でないかどうか
	 */
	public boolean isInfration() {
		return (getBurstState() != Burst.NONE);
	}

	/**
	 * 燃やされているかどうかを設定する.
	 * 同時にパニック状態をクリアする.
	 * @param b 燃やされているかどうか
	 */
	public void setBurned(boolean b) {
		burned = b;
		setForcePanicClear();
	}

	/**
	 * 茎が生えているかどうかを取得する.
	 * @return 茎が生えているかどうか
	 */
	public boolean isbindStalk() {
		return (bindStalk != null);
	}

	/**
	 * たりないゆかどうかを判定する.
	 * たりないゆ、たりないれいむクラスでオーバーライドする.
	 * @return たりないゆかどうか
	 */
	public boolean isIdiot() {
		return false;
	}

	/**
	 * パニック状態をクリアする.
	 */
	public void setForcePanicClear() {
		panicType = null;
		panicPeriod = 0;
	}

	/**
	 * 怒っているかどうかを返却する.
	 * 死んでいないことが条件.
	 * @return 怒っているかどうか
	 */
	public boolean isAngry() {
		return (!dead && angry);
	}

	/**
	 * 怯えているかどうかを返却する.
	 * 死んでいないことが条件.
	 * @return 怯えているかどうか
	 */
	public boolean isScare() {
		return (!dead && scare);
	}

	/**
	 * 悲しんでいるかどうかを返却する.
	 * 死んでいないことが条件.
	 * @return 悲しんでいるかどうか
	 */
	public boolean isSad() {
		return (!dead && happiness == Happiness.SAD);
	}

	/**
	 * とても悲しんでいるかどうかを返却する.
	 * 死んでいないことが条件.
	 * @return とても悲しんでいるかどうか
	 */
	public boolean isVerySad() {
		return (!dead && happiness == Happiness.VERY_SAD);
	}

	/**
	 * 喜んでいるかどうかを返却する.
	 * 死んでいないことが条件.
	 * @return 喜んでいるかどうか
	 */
	public boolean isHappy() {
		return (!dead && (happiness == Happiness.HAPPY || happiness == Happiness.VERY_HAPPY));
	}

	/**
	 * 悲しんでいるか、もしくはとても悲しんでいるかを返却する.
	 * 死んでいないことが条件.
	 * @return 悲しんでいるか、もしくはとても悲しんでいるか
	 */
	public boolean isUnhappy() {
		return (!dead && (happiness == Happiness.SAD || happiness == Happiness.VERY_SAD));
	}

	/**
	 * 食べ過ぎかどうかを取得する.
	 * 死んでいないことが条件.
	 * @return  食べ過ぎかどうか
	 */
	public boolean isOverEating() {
		return (!dead && (hungry >= HUNGRYLIMIT[getBodyAgeState().ordinal()] * 1.3f));
	}

	/**
	 * お腹いっぱいかどうかを取得する.
	 * 死んでいないことが条件.
	 * @return お腹いっぱいかどうか
	 */
	public boolean isTooFull() {
		return (!dead && hungry >= HUNGRYLIMIT[getBodyAgeState().ordinal()]);
	}

	/**
	 * お腹いっぱい気味かどうかを取得する.
	 * 死んでいないことが条件.
	 * @return お腹いっぱい気味かどうか
	 */
	public boolean isFull() {
		return (!dead && (hungry >= HUNGRYLIMIT[getBodyAgeState().ordinal()] * 0.8f));
	}

	/**
	 * お腹へってきているかどうかを取得する.
	 * 死んでいないことが条件.
	 * @return お腹へってきているかどうか
	 */
	public boolean isHungry() {
		return (!dead && (hungry <= HUNGRYLIMIT[getBodyAgeState().ordinal()] / 2));
	}

	/**
	 * お腹減り気味かどうかを取得する.
	 * 死んでいないことが条件.
	 * @return お腹減り気味かどうか
	 */
	public boolean isSoHungry() {
		return (!dead && (hungry <= HUNGRYLIMIT[getBodyAgeState().ordinal()] * 0.2f));
	}

	/**
	 * お腹が減っているかどうかを取得する.
	 * 死んでいないことが条件.
	 * @return お腹が減っているかどうか
	 */
	public boolean isVeryHungry() {
		return (!dead && hungry <= 0);
	}

	/**
	 * お腹が減りすぎているかどうかを取得する.
	 * 死んでいないことが条件.
	 * @return お腹が減りすぎているかどうか
	 */
	public boolean isTooHungry() {
		return (!dead && hungry <= 0 && getDamageState() != Damage.NONE);
	}

	/**
	 * 餓死寸前かどうかを取得する.
	 * 死んでいないことが条件.
	 * @return 餓死寸前かどうか
	 */
	public boolean isStarving() {
		return (!dead && hungry <= 0 && getDamageState() == Damage.TOOMUCH);
	}

	/**
	 * 満腹度を操作する.
	 * @param val 空腹度に足し引きする数値
	 */
	public void addHungry(int val) {
		hungry += (TICK * val);
	}

	/**
	 * 満腹度を取得する.
	 * @return 満腹度
	 */
	public int getHungry() {
		return hungry;
	}

	/**
	 * 満腹度を設定する.
	 * @param val 満腹度
	 */
	public void setHungry(int val) {
		hungry = val;
	}

	/**
	 * 段階別（赤/子/成）の飢餓状態限界を取得する.
	 * @return 段階別（赤/子/成）の飢餓状態限界
	 */
	public int getHungryLimit() {
		return HUNGRYLIMIT[getBodyAgeState().ordinal()];
	}

	/**
	 * 成ゆかどうかを取得する.
	 * @return 成ゆかどうか
	 */
	public boolean isAdult() {
		return (getBodyAgeState() == AgeState.ADULT);
	}

	/**
	 * 子ゆかどうかを取得する.
	 * @return 子ゆかどうか
	 */
	public boolean isChild() {
		return (getBodyAgeState() == AgeState.CHILD);
	}

	/**
	 * 赤ゆかどうかを取得する.
	 * @return 赤ゆかどうか
	 */
	public boolean isBaby() {
		return (getBodyAgeState() == AgeState.BABY);
	}

	/**
	 * 睡眠中かどうかを取得する.
	 * 死んでいないことが条件.
	 * @return 睡眠中かどうか
	 */
	public boolean isSleeping() {
		return (!dead && sleeping);
	}

	/**
	 * 眠いかどうかを取得する.
	 * 死んでいないことが条件.
	 * @return 眠いかどうか
	 */
	public boolean isSleepy() {
		if (!sleeping && wakeUpTime + ACTIVEPERIOD < getAge()) {
			return true;
		}
		return false;
	}

	/**
	 * うんうん中かどうかを取得する.
	 * 死んでいないことが条件.
	 * @return うんうん中かどうか
	 */
	public boolean isShitting() {
		return (!dead && shitting);
	}

	/**
	 * 発情中かどうかを取得する.
	 * 死んでいないことが条件.
	 * @return 発情中かどうか
	 */
	public boolean isExciting() {
		return (!dead && exciting);
	}

	/**
	 * 発情状態を設定する.
	 * @param bTemp 発情状態
	 */
	public void setExciting(Boolean bTemp) {
		if (bTemp)
			setForceFace(ImageCode.EXCITING.ordinal());
		exciting = bTemp;
	}

	/**
	 * 強制発情状態かどうかを取得する.
	 * 死んでいないことが条件.
	 * @return 強制発情状態かどうか
	 */
	public boolean isForceExciting() {
		return (!dead && exciting && bForceExciting);
	}

	/**
	 * 発情を落ち着かせる.
	 */
	public void setCalm() {
		bForceExciting = false;
		exciting = false;
	}

	/**
	 * ゆんやあしているかどうかを取得する.
	 * 死んでいないことが条件.
	 * @return ゆんやあしているかどうか
	 */
	public boolean isYunnyaa() {
		return (!dead && yunnyaa);
	}

	/**
	 * 親を呼んで泣き叫び中かどうかを取得する.
	 * 死んでいないことが条件.
	 * @return 親を呼んで泣き叫び中かどうか
	 */
	public boolean isCallingParents() {
		return (!dead && callingParents);
	}

	/**
	 * 汚れている期間を追加する.
	 * @param val 追加する期間
	 */
	public void addDirtyPeriod(int val) {
		dirtyPeriod += val;
	}

	/**
	 * ハイブリッドかどうかを取得する.
	 * ハイブリッドのクラスでオーバーライドする.
	 * @return ハイブリッドかどうか
	 */
	public boolean isHybrid() {
		return false;
	}

	/**
	 *  汚れ、濡れを解除する.
	 */
	public void setCleaning() {
		setDirty(false);
		wet = false;
		wetPeriod = 0;
	}

	/**
	 * 汚れているかどうかを返却する.
	 * 死んでいないことが条件.
	 * @return 汚れているかどうか
	 */
	public boolean isDirty() {
		return (!dead && (dirty || stubbornlyDirty));
	}

	/**
	 * 普通の汚れかどうかを返却する.
	 * 死んでいないことが条件.
	 * @return 普通の汚れかどうか
	 */
	public boolean isNormalDirty() {
		return (!dead && dirty);
	}

	/**
	 * 運んでいるアイテムを取得する.
	 * @param key アイテムのキー
	 * @return 運んでいるアイテム
	 */
	public Obj getTakeoutItem(TakeoutItemType key) {
		return takeoutItem.get(key);
	}

	/**
	 * 運んでいるアイテムをクリアする.
	 * @param key アイテムのキー
	 */
	public void removeTakeoutItem(TakeoutItemType key) {
		takeoutItem.remove(key);
	}

	/**
	 * 動物（というか現在はアリ一択か）に食べられてるかを返却する.
	 * @return 動物（というか現在はアリ一択か）に食べられてるか
	 */
	public boolean isEatenByAnimals() {
		if (getAttachmentSize(Ants.class) != 0)
			return true;
		else
			return false;
	}

	/**
	 * アリを除去する.
	 */
	public void removeAnts() {
		removeAttachment(Ants.class, true);
		numOfAnts = 0;
	}

	/**
	 * アリの数を減らす.
	 * @param A 減らしたいアリの数
	 */
	public void substractNumOfAnts(int A) {
		numOfAnts -= A;
		if (numOfAnts < 0) {
			numOfAnts = 0;
		}
	}

	/**
	 * アタッチメントを追加する.
	 * @param at アタッチメント
	 */
	public void addAttachment(Attachment at) {
		attach.add(at);
	}

	/**
	 *  指定クラスのアタッチメント数を取得する.
	 * @param type 指定クラス
	 * @return アタッチメント数
	 */
	public int getAttachmentSize(Class<?> type) {
		int ret = 0;
		for (Attachment at : attach) {
			if (at.getClass().equals(type)) {
				ret++;
			}
		}
		return ret;
	}

	/**
	 *  指定クラスのアタッチメント除去
	 * @param type 指定クラス
	 * @param all すべて除去かどうか
	 */
	public void removeAttachment(Class<?> type, boolean all) {
		Iterator<Attachment> itr = attach.iterator();
		while (itr.hasNext()) {
			Attachment at = itr.next();
			if (at.getClass().equals(type)) {
				itr.remove();
				if (!all)
					break;
			}
		}
	}

	/**
	 *  全アタッチメントのサイズを調整する.
	 */
	public void resetAttachmentBoundary() {
		if (attach != null && attach.size() != 0) {
			for (Attachment at : attach) {
				at.resetBoundary();
			}
		}
	}

	//------------------------------------------
	/**
	 *  子供を追加する.
	 * @param at 子のインスタンス
	 */
	public void addChildrenList(Body at) {
		if (childrenList == null) {
			childrenList = new LinkedList<>();
		}
		if (at != null) {
			childrenList.add(at);
		}
	}

	/**
	 * 指定の子供をリストから除去する.
	 * @param bTarget 子のインスタンス
	 */
	public void removeChildrenList(Body bTarget) {
		if (bTarget == null) {
			return;
		}
		Iterator<Body> itr = childrenList.iterator();
		while (itr.hasNext()) {
			Body at = itr.next();
			if (at == bTarget) {
				itr.remove();
			}
		}
	}

	//------------------------------------------
	/**
	 *  姉を追加する.
	 * @param at 姉のインスタンス
	 */
	public void addElderSisterList(Body at) {
		if (at != null) {
			elderSisterList.add(at);
		}
	}

	/**
	 *  指定の姉をリストから除去する.
	 * @param bTarget 姉インスタンス
	 */
	public void removeElderSisterList(Body bTarget) {
		if (bTarget == null) {
			return;
		}
		Iterator<Body> itr = elderSisterList.iterator();
		while (itr.hasNext()) {
			Body at = itr.next();
			if (at == bTarget) {
				itr.remove();
			}
		}
	}
	//------------------------------------------

	/**
	 * 妹を追加する.
	 * @param at 妹のインスタンス
	 */
	public void addSisterList(Body at) {
		if (at != null) {
			sisterList.add(at);
		}
	}

	/**
	 *  指定の妹をリストから除去する.
	 * @param bTarget 妹のインスタンス
	 */
	public void removeSisterList(Body bTarget) {
		if (bTarget == null) {
			return;
		}
		Iterator<Body> itr = sisterList.iterator();
		while (itr.hasNext()) {
			Body at = itr.next();
			if (at == bTarget) {
				itr.remove();
			}
		}
	}

	/**
	 * うんうん限界を返却する.
	 * @return うんうん限界
	 */
	public int getShitLimit() {
		return SHITLIMIT[getBodyAgeState().ordinal()];
	}

	/**
	 * 便意を追加する.
	 * @param s 追加分
	 */
	public void plusShit(int s) {
		if (shit == 0 || s <= 0)
			return;
		shit += s;
	}

	/**
	 * 便意を設定する.
	 * うんうん中は設定できない.
	 * @param inShit 設定したい便意
	 * @param ibVeryShit 便意強制MAXフラグ。これが立っていると、inShitはMAXまでの猶予となる。
	 */
	public void setShit(int inShit, boolean ibVeryShit) {
		if (shitting)
			return;
		if (ibVeryShit) {
			if (shit < SHITLIMIT[getBodyAgeState().ordinal()]) {
				shit = SHITLIMIT[getBodyAgeState().ordinal()] - inShit;
			}
		} else {
			shit = inShit;
		}
	}

	/**
	 * 胎生妊娠してる赤ゆを取得
	 * <br>出産時に、順番に生んでゆくときの処理に使われている
	 * @return 胎生妊娠してる赤ゆのDNA
	 **/
	public Dna getBabyTypesDequeue() {
		Dna babyType = null;
		if (babyTypes.size() > 0) {
			babyType = babyTypes.get(0);
			babyTypes.remove(0);
		}
		return babyType;
	}

	/**
	 * 茎妊娠してる茎を取得
	 * <br>出産時に、順番に生んでゆくときの処理に使われている
	 * @return 茎妊娠してる赤ゆのDNA
	 **/
	public Stalk getStalksDequeue() {
		Stalk stalk = null;
		if (stalks.size() > 0) {
			stalk = stalks.get(0);
			stalks.remove(0);
		}
		return stalk;
	}

	/**
	 * ダメージ限界を返却する.
	 * @return 赤/子/成ゆのダメージ限界
	 */
	public int getDamageLimit() {
		return getDAMAGELIMIT()[getBodyAgeState().ordinal()];
	}

	/**
	 * ストレスを設定する.
	 * @param s ストレス値
	 */
	public void setStress(int s) {
		if (s > 0)
			stress = s;
	}

	/**
	 * ストレス値に加える.
	 * @param s ストレス値に加えたい値
	 */
	public void addStress(int s) {
		if (dead)
			return;
		//ストレスに応じてうんうん増加
		if (s > 0 && eCoreAnkoState == CoreAnkoState.DEFAULT && getBurstState() != Burst.HALF)
			plusShit(s / 5);
		stress += TICK * s;
		if (stress < 0)
			stress = 0;
	}

	/**
	 * ストレス値の限界を取得する.
	 * @return 赤/子/成ゆのストレス値の限界
	 */
	public int getStressLimit() {
		return STRESSLIMIT[getBodyAgeState().ordinal()];
	}

	/**
	 * ストレスフルかどうかを返却する.
	 * @return  ストレスフルかどうか
	 */
	public boolean isStressful() {
		// ストレス限界の40%を超えている場合
		if (STRESSLIMIT[getBodyAgeState().ordinal()] * checkNonYukkuriDiseaseTolerance() / 100 * 2 / 5 < stress) {
			return true;
		}
		return false;
	}

	/**
	 * とてもストレスフルかどうかを返却する.
	 * @return とてもストレスフルかどうか
	 */
	public boolean isVeryStressful() {
		// ストレス限界の60%を超えている場合
		if (STRESSLIMIT[getBodyAgeState().ordinal()] * checkNonYukkuriDiseaseTolerance() / 100 * 3 / 5 < stress) {
			return true;
		}
		return false;
	}

	/**
	 * トラウマもちかどうかを返却する.
	 * @return トラウマ持ちかどうか
	 */
	public boolean hasTrauma() {
		if (trauma != null)
			return true;
		else
			return false;
	}

	/**
	 * ふりふりしているかどうかを返却する.
	 * @return ふりふりしているかどうか
	 */
	public boolean isFurifuri() {
		return (!isDead() && furifuri);
	}

	/**
	 * 自主的にふりふりするかどうかを返却する.
	 * @return 自主的にふりふりするかどうか
	 */
	public boolean willingFurifuri() {
		if (isRude() && RND.nextInt(furifuriDiscipline + 1) == 0 && canFurifuri()) {
			return true;
		}
		return false;
	}

	/**
	 * ふりふり可能な状態かどうかを返却する.
	 * @return ふりふり可能な状態かどうか
	 */
	public boolean canFurifuri() {
		if (getFootBakeLevel() != FootBake.CRITICAL && eCoreAnkoState == CoreAnkoState.DEFAULT) {
			return true;
		}
		return false;
	}

	/**
	 * ふりふりのしつけの値を取得する.
	 * @return ふりふりのしつけの値
	 */
	public int getFurifuriDiscipline() {
		return furifuriDiscipline;
	}

	/**
	 * のびのびをしているかどうかを取得する.
	 * @return のびのびをしているかどうか
	 */
	public boolean isNobinobi() {
		return (!dead && nobinobi);
	}

	/**
	 * のびのびをしているかどうかを設定する.
	 * @param b のびのびをしているかどうか
	 */
	public void setNobinobi(boolean b) {
		nobinobi = b;
	}

	/**
	 * 脚焼きされている期間を加える.
	 * @param s 加えたい足焼き期間
	 */
	public void addFootBakePeriod(int s) {
		footBakePeriod += s;
	}

	/**
	 * 足焼きレベルを取得する.
	 * @return 足焼きレベル
	 */
	public FootBake getFootBakeLevel() {
		FootBake ret = FootBake.NONE;
		if (footBakePeriod < 0) {
			footBakePeriod = 0;
		}
		if (footBakePeriod > DAMAGELIMIT[getBodyAgeState().ordinal()]) {
			ret = FootBake.CRITICAL;
		} else if (footBakePeriod > (DAMAGELIMIT[getBodyAgeState().ordinal()] >> 1)) {
			ret = FootBake.MIDIUM;
		}
		return ret;
	}

	/**
	 * キリッ！かどうかを取得する.
	 * @return キリッ！かどうか
	 */
	public boolean isVain() {
		return (!dead && beVain);
	}

	/**
	 * あんこ量を加える.
	 * @param val 加えたいあんこ量
	 * @return あんこ量がなくなったかどうか
	 */
	public boolean addAmount(int val) {
		bodyAmount += val;
		if (bodyAmount <= 0) {
			bodyAmount = 0;
			return true;
		}
		return false;
	}

	/**
	 * あんこ量を初期化する.
	 * @param val 成長段階
	 */
	public void initAmount(AgeState val) {
		bodyAmount = DAMAGELIMIT[val.ordinal()];
	}

	public int getCollisionX() {
		return (bodySpr[getBodyAgeState().ordinal()].imageW + getExpandSizeW()) >> 1;
	}

	public int getCollisionY() {
		return (bodySpr[getBodyAgeState().ordinal()].imageH + getExpandSizeH()) >> 1;
	}

	/**
	 * 足の速さを取得する.
	 * @return 足の速さ
	 */
	public int getStep() {
		return (STEP[getBodyAgeState().ordinal()]);
	}

	public int getStepDist() {
		int p = (STEP[getBodyAgeState().ordinal()]) * (STEP[getBodyAgeState().ordinal()]);
		return p;
	}

	public Sprite getBodyBaseSpr() {
		return bodySpr[getBodyAgeState().ordinal()];
	}

	public Sprite getBodyExpandSpr() {
		return expandSpr[getBodyAgeState().ordinal()];
	}

	public Sprite getBraidSprite() {
		return braidSpr[getBodyAgeState().ordinal()];
	}

	public BufferedImage getShadowImage() {
		return shadowImages[getBodyAgeState().ordinal()];
	}

	public int getShadowH() {
		return shadowImgH[getBodyAgeState().ordinal()];
	}

	public int getW() {
		return bodySpr[getBodyAgeState().ordinal()].imageW;
	}

	public int getH() {
		return bodySpr[getBodyAgeState().ordinal()].imageH;
	}

	public int getPivotX() {
		return bodySpr[getBodyAgeState().ordinal()].pivotX;
	}

	public int getPivotY() {
		return bodySpr[getBodyAgeState().ordinal()].pivotY;
	}

	public int getBraidW() {
		return braidSpr[getBodyAgeState().ordinal()].imageW;
	}

	public int getBraidH() {
		return braidSpr[getBodyAgeState().ordinal()].imageH;
	}

	public int getMaxHaveBaby() {
		return getDamageLimit() / 300;
	}

	/**
	 * 体重を取得する.
	 * 外力を算出するときに使用する.
	 * @return 体重
	 */
	public int getWeight() {
		return (WEIGHT[getBodyAgeState().ordinal()] + (babyTypes.size() + stalkBabyTypes.size()) * 50);
	}

	/**
	 * 致命傷種別を取得する.
	 * @return 致命傷種別
	 */
	public CriticalDamegeType getCriticalDamegeType() {
		return criticalDamege;
	}

	/**
	 * 致命傷種別を設定する.
	 * @param type 致命傷種別
	 */
	public void setCriticalDamegeType(CriticalDamegeType type) {
		criticalDamege = type;
	}

	/**
	 * お気に入りアイテムを取得する.
	 * @param key お気に入りアイテムのタイプ
	 * @return お気に入りアイテムのインスタンス
	 */
	public Obj getFavItem(FavItemType key) {
		return favItem.get(key);
	}

	/**
	 * お気に入りアイテムを設定する.
	 * @param key お気に入りアイテムのタイプ
	 * @param val お気に入りアイテムのインスタンス
	 */
	public void setFavItem(FavItemType key, Obj val) {
		favItem.put(key, val);
	}

	/**
	 * お気に入りアイテムを取り除く.
	 * @param key 取り除くアイテムのタイプ
	 */
	public void removeFavItem(FavItemType key) {
		favItem.remove(key);
	}

	/**
	 * はげまんじゅうにする.
	 */
	public void cutHair() {
		eHairState = HairState.BALDHEAD;
	}

	/**
	 * 捕食種かどうかを取得する.
	 * @return
	 */
	public boolean isPredatorType() {
		return (predatorType != null);
	}

	/**
	 * 茎または腹ではらんでいるかどうかを取得する.
	 * @return 茎または腹ではらんでいるかどうか
	 */
	public boolean hasBabyOrStalk() {
		return (hasBaby || hasStalk);
	}

	/**
	 * おかざりがあるかどうかを取得する.
	 * @return おかざりがあるかどうか
	 */
	public final boolean hasOkazari() {
		return (okazari != null);
	}

	/**
	 * 次の落下でのダメージをなくす.
	 */
	public final void setNoDamageNextFall() {
		setbNoDamageNextFall(true);
	}

	/**
	 * おくるみを取る.
	 */
	public void takePants() {
		setHasPants(false);
	}

	/**
	 * あにゃる閉鎖を設定する.
	 * @param flag あにゃる閉鎖かどうか
	 */
	public void setForceAnalClose(boolean flag) {
		setAnalClose(flag);
	}

	/**
	 * 茎を去勢する.
	 */
	public void invStalkCastration() {
		boolean stalkCastration = !isStalkCastration();
		setStalkCastration(stalkCastration);
	}

	/**
	 * 胎生去勢をする.
	 */
	public final void invBodyCastration() {
		boolean bodyCastration = !isBodyCastration();
		setBodyCastration(bodyCastration);
	}

	/**
	 * 思い出を追加する.
	 * 思い出量は非ゆっくり症チェックで使用する.
	 * @param nAdd 追加する思い出量
	 */
	public final void addMemories(int nAdd) {
		/*知能による補正
		賢いと補正無
		普通だと、マイナスの場合のみ1/2に
		餡子脳だとマイナスの場合に1/2,プラスの場合は2倍に
		*/
		switch (getIntelligence()) {
		case WISE:
			memories += nAdd / 2;
			break;
		case FOOL:
			if (nAdd < 0)
				memories += nAdd / 2;
			else
				memories += nAdd * 2;
			break;
		default:
			if (nAdd < 0)
				memories += nAdd;
			else
				memories += nAdd * 2;
			break;
		}
	}

	/**
	 * かび判定を行う
	 * @return かびているかどうか
	 */
	public boolean findSick(BodyAttributes b) {
		switch (getIntelligence()) {
		case WISE:
			if (b.isSick())
				return true;
			break;
		case AVERAGE:
			if (b.isSick())
				return true;
			break;
		case FOOL:
			if (b.isSickHeavily())
				return true;
			break;
		}
		return false;
	}

	/**
	 * ゆかび第一段階(自覚症状)
	 * @return ゆかび第一段階以上になっているかどうか
	 */
	public boolean isSick() {
		if (sickPeriod > INCUBATIONPERIOD) {
			return true;
		} else
			return false;
	}

	/**
	 * ゆかび第二段階
	 * @return ゆかび第二段階になっているかどうか
	 */
	public boolean isSickHeavily() {
		if (sickPeriod > (INCUBATIONPERIOD * 8)) {
			return true;
		} else
			return false;
	}

	/**
	 * ゆかび第三段階、かつダメージ有
	 * @return ゆかび第三段階になっているかどうか
	 */
	public boolean isSickTooHeavily() {
		if (sickPeriod > (INCUBATIONPERIOD * 32) && isDamaged()) {
			return true;
		} else
			return false;
	}

	/**
	 * 強制的にゆかびにする.
	 */
	public final void forceSetSick() {
		sickPeriod = (INCUBATIONPERIOD * 32) + 2;
	}

	/**
	 * 老ゆかどうかを取得する.
	 * @return 老ゆかどうか
	 */
	public final boolean isOld() {
		return getAge() > (getLIFELIMIT() * 9 / 10);
	}

	/**
	 * 喋っているかどうかを取得する.
	 * @return 喋っているかどうか
	 */
	public final boolean isTalking() {
		return (messageCount > 0);
	}

	/**
	 * 幸福度を設定する.
	 * @param happy 幸福度
	 */
	public void setHappiness(Happiness happy) {
		if (isDead() || isIdiot()) {
			happiness = Happiness.AVERAGE;
			return;
		}
		if (isNYD()) {
			happiness = Happiness.VERY_SAD;
			sadPeriod = 1200 + RND.nextInt(400) - 200;
			return;
		}
		if (happy == Happiness.SAD) {
			if (getHappiness() != Happiness.VERY_SAD) {
				sadPeriod = 0;
				happiness = happy;
			}
		} else if (happy == Happiness.HAPPY) {
			if (getHappiness() != Happiness.VERY_HAPPY) {
				sadPeriod = 0;
				happiness = happy;
			}
		} else {
			if (happy == Happiness.VERY_SAD) {
				sadPeriod = 1200 + RND.nextInt(400) - 200;
			} else {
				sadPeriod = 0;
			}
			happiness = happy;
		}
		if (getHappiness() == Happiness.HAPPY || getHappiness() == Happiness.VERY_HAPPY) {
			setScare(false);
			setAngry(false);
		} else if (getHappiness() == Happiness.SAD || getHappiness() == Happiness.VERY_SAD) {
			setAngry(false);
		}
	}

	/**
	 * 足焼きレベルを取得する．
	 * @return 足焼きレベル
	 */
	public final BodyBake getBodyBakeLevel() {
		BodyBake ret = BodyBake.NONE;
		if (bodyBakePeriod < 0) {
			footBakePeriod = 0;
		}
		if (bodyBakePeriod > getDAMAGELIMIT()[getBodyAgeState().ordinal()] * 3 / 4) {
			ret = BodyBake.CRITICAL;
		} else if (bodyBakePeriod > (getDAMAGELIMIT()[getBodyAgeState().ordinal()] * 2 / 5)) {
			ret = BodyBake.MIDIUM;
		}
		return ret;
	}

	/**
	 * やけどの有無を取得する.
	 * @return やけどの有無
	 */
	public boolean isGotBurned() {
		if (getFootBakeLevel() == FootBake.NONE && getBodyBakeLevel() == BodyBake.NONE) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 深刻なやけどの有無を取得する.
	 * @return 深刻なやけどの有無
	 */
	public boolean isGotBurnedHeavily() {
		if (getFootBakeLevel() != FootBake.NONE || getBodyBakeLevel() == BodyBake.CRITICAL) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * プレイヤーに対する愛を加える.
	 * @param val 加えたい愛（マイナスもあり）
	 */
	public void addLovePlayer(int val) {
		//非ゆっくり症発症個体は常にプレイヤーを嫌いに
		if (isNYD()) {
			nLovePlayer = -1 * getLOVEPLAYERLIMIT();
			return;
		}
		nLovePlayer += (TICK * val);
		if (nLovePlayer < -1 * getLOVEPLAYERLIMIT()) {
			// 下限設定
			nLovePlayer = -1 * getLOVEPLAYERLIMIT();
		} else if (getLOVEPLAYERLIMIT() < nLovePlayer) {
			// 上限設定
			nLovePlayer = getLOVEPLAYERLIMIT();
		}
	}

	/**
	 * 体の焦げ具合を追加する.
	 * @param s 加えたい体の焦げ具合
	 */
	public void addBodyBakePeriod(int s) {
		footBakePeriod += (s / 5);
		bodyBakePeriod += s;
	}

	/**
	 * 舌の肥え度を加える.
	 * @param val 加えたい舌の肥え度
	 */
	public void addTang(int val) {
		setTang(getTang() + val);
	}

	/**
	 * 舌の肥度合いを取得する.
	 * @return 舌の肥度合い
	 */
	public TangType getTangType() {
		TangType ret;
		if (getTang() < getTANGLEVEL()[0]) {
			ret = TangType.POOR;
		} else if (getTang() < getTANGLEVEL()[1]) {
			ret = TangType.NORMAL;
		} else {
			ret = TangType.GOURMET;
		}
		return ret;
	}

	/**
	 * 一回の食事量を取得する.
	 * @return 一回の食事量
	 */
	public int getEatAmount() {
		return getEATAMOUNT()[getBodyAgeState().ordinal()];
	}

	/**
	 * あまあまへの慣れを増減する.
	 * @param val あまあまへの慣れ
	 */
	public final void addAmaamaDiscipline(int val) {
		amaamaDiscipline += val;
		if (amaamaDiscipline > 100) {
			amaamaDiscipline = 100;
		}
		if (amaamaDiscipline < 0) {
			amaamaDiscipline = 0;
		}
	}

	/**
	 * 頑固な汚れかどうかを返却する.
	 * 死んでいる場合は「頑固な汚れじゃない」.
	 * @return 頑固な汚れかどうか
	 */
	public final boolean isStubbornlyDirty() {
		return (!isDead() && stubbornlyDirty);
	}

	/**
	 * 非ゆっくり症(間近も含む)かどうかを返却する.
	 * @return 非ゆっくり症(間近も含む)かどうか
	 */
	public final boolean isNYD() {
		return geteCoreAnkoState() != CoreAnkoState.DEFAULT;
	}

	/**
	 * 非ゆっくり症ではないどうかを返却する.
	 * @return 非ゆっくり症ではないかどうか
	 */
	public final boolean isNotNYD() {
		return geteCoreAnkoState() == CoreAnkoState.DEFAULT;
	}

	/**
	 * 行動目的を取得する.
	 * @return 行動目的
	 */
	public PurposeOfMoving getPurposeOfMoving() {
		return purposeOfMoving;
	}

	/**
	 * 行動目的を設定する.
	 * @param purposeOfMoving 行動目的
	 */
	public void setPurposeOfMoving(PurposeOfMoving purposeOfMoving) {
		this.purposeOfMoving = purposeOfMoving;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof BodyAttributes)) {
			return false;
		}
		BodyAttributes dest = (BodyAttributes) o;
		if (getUniqueID() == dest.getUniqueID()) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getUniqueID() * 13;
	}

	@Override
	public int compareTo(Object o) {
		if (o == null) {
			return 0;
		}
		if (!(o instanceof BodyAttributes)) {
			return 0;
		}
		BodyAttributes b = (BodyAttributes) o;
		return getUniqueID() - b.getUniqueID();
	}
}
