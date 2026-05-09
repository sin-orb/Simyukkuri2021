package src.base;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.beans.Transient;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import src.Const;
import src.SimYukkuri;
import src.logic.BodyRelations;
import src.util.GameRandom;
import src.util.GameWorld;
import src.attachment.Ants;
import src.draw.Color4y;
import src.draw.Point4y;
import src.enums.AgeState;
import src.enums.Attitude;
import src.enums.BurialState;
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
import src.logic.BodyDependencyRule;
import src.logic.BodyAppearanceRule;
import src.logic.BodyActivityRule;
import src.logic.BodyAttitudeRule;
import src.logic.BodyExcitementRule;
import src.logic.BodyBehaviorRule;
import src.logic.BodyFallRule;
import src.logic.BodyDisplayRule;
import src.logic.BodyExpressionRule;
import src.logic.BodyHungerRule;
import src.logic.BodyMovementGoalRule;
import src.logic.BodyStressRule;
import src.logic.BodyAgeRule;
import src.logic.BodyAgeCategoryRule;
import src.logic.BodyAnimalRule;
import src.logic.BodySpeechRule;
import src.logic.BodyVitals;
import src.logic.BodyRelations;
import src.logic.BodyStyleRule;
import src.logic.BodyFlagRule;
import src.logic.BodySurisuriRule;
import src.logic.BodyNeedleRule;
import src.logic.BodyBurnRule;
import src.logic.BodyBirthRule;
import src.logic.BodyBurstRule;
import src.logic.BodyDamageRule;
import src.logic.BodyExcretionRule;
import src.logic.BodySpecialTypeRule;
import src.logic.BodyConditionRule;
import src.logic.BodyActionStateRule;
import src.logic.BodyControlRule;
import src.logic.BodyHungerStateRule;
import src.logic.BodyPresentationRule;
import src.logic.BodyPreferenceRule;
import src.logic.BodyCoreStateRule;
import src.logic.BodyTraitRule;
import src.logic.BodyStructureRule;
import src.logic.BodyTimingRule;
import src.logic.BodyStatRule;
import src.system.BasicStrokeEX;
import src.system.BodyLayer;
import src.system.MapPlaceData;
import src.system.Sprite;
/**
 * ゆっくり本体の抽象クラスの属性/状態の取得を抜き出したクラス.
 * 属性を増やしたらTransformationBodyCopierのコピー除外リストに反映する。
 * コピーしたくない属性かどうかは、れいむ→でいぶ、まりさ→ドスまりさとなったときに
 * もととなるゆっくりから変異後のゆっくりにコピーしたい属性かどうかで決める。
 * 例えば、子供リスト等はコピーしたいが、自分の呼称やあんこ量等はコピーしたくない。
 * （コピーしてもその後のINIファイル取得で上書きされるものもある）
 * そこはいわゆる”ゆ虐の設定”に従うこと。
 */
@JsonTypeInfo(use = Id.CLASS)
public abstract class BodyAttributes extends Obj {
	private static final long serialVersionUID = 4867243705470540257L;

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
	public abstract Point4y[] getMountPoint(String key);

	/**
	 * 非ゆっくり症のチェックメソッド.
	 * Bodyでのオーバーライド.
	 * 
	 * @return 非ゆっくり症かどうか
	 */
	public abstract int checkNonYukkuriDiseaseTolerance();

	// public variables
	/** 名前関連データをまとめた値オブジェクト */
	@JsonIgnore
	private final BodyNameSet bodyNameSet = new BodyNameSet();
	/** スプライト関連データをまとめた値オブジェクト */
	@JsonIgnore
	private final BodySpriteSet bodySpriteSet = new BodySpriteSet();

	/** 移動目的 */
	protected PurposeOfMoving purposeOfMoving = PurposeOfMoving.NONE;

	// Used in image loading.
	private static BufferedImage[] shadowImages = new BufferedImage[3];
	/** 影画像のサイズ定義 */
	protected static int[] shadowImgW = new int[3], shadowImgH = new int[3];
	/** 影画像の中心定義 */
	protected static int[] shadowPivX = new int[3], shadowPivY = new int[3];
	// .INIファイルで変更可能な各ゆっくりのパラメータ.
	/** 統計/調整パラメータ群 */
	@JsonIgnore
	private final BodyStatProfile bodyStatProfile = new BodyStatProfile();
	/** 行動・性格・繁殖寄りの調整値をまとめた値オブジェクト */
	@JsonIgnore
	private final BodyBehaviorProfile bodyBehaviorProfile = new BodyBehaviorProfile();
	/** 時刻・閾値の統計データをまとめた値オブジェクト */
	@JsonIgnore
	private final BodyTimingProfile bodyTimingProfile = new BodyTimingProfile();
	// individual state variables for each Yukkuri.
	/** 画像がまりちゃ流しか */
	private boolean imageNagasiMode = false;
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
	protected int lovePlayer = 0;
	/** プレイヤーへのなつき度概算 */
	private LovePlayer lovePlayerState = LovePlayer.NONE;
	/** 髪の状態 */
	private HairState hairState = HairState.DEFAULT;
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
	private boolean penipeniCutted = false;
	/** フェロモンの有無 */
	private boolean pheromone = false;
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
	private boolean firstGround = true;
	/** うまれて初めての食事か */
	private boolean firstEatStalk = true;
	/** 死体が損壊されているか */
	private boolean crushed = false;
	/** 死体が焼損されているか */
	private boolean burned = false;
	/** 中枢餡の状態（非ゆっくり症フラグ */
	private CoreAnkoState coreAnkoState = CoreAnkoState.DEFAULT;
	/** 発情フラグ want to sukkiri or not */
	private boolean exciting = false;
	/** 強制発情フラグ want to sukkiri or not */
	private boolean forceExciting = false;
	/** ゆっくりしてるかどうか */
	private boolean relax = false;
	/** 睡眠中かどうか */
	@JsonProperty
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
	private boolean needled = false;
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
	private boolean noticeNoOkazari = false;
	/** パニック種別 */
	private PanicType panicType = null;
	/** 致命傷種別 */
	private CriticalDamegeType criticalDamege = null;
	/** つがい */
	private int partner = -1;
	/** 親 */
	private int[] parents = { -1, -1 };
	/** 子供のリスト */
	private List<Integer> childrenList = new LinkedList<Integer>();
	/** 姉のリスト */
	private List<Integer> elderSisterList = new LinkedList<Integer>();
	/** 妹のリスト */
	private List<Integer> sisterList = new LinkedList<Integer>();
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
	protected int speechDiscipline = 0;
	/** あまあまへの慣れ具合 */
	protected int amaamaDiscipline = 0;
	/** 自身の持っているアタッチメント */
	private List<Attachment> attach = new LinkedList<Attachment>();
	/** なにかのオブジェクト（すぃー、親ゆなど）に載せられている等のリンクが有る際のそのオブジェクト */
	private int parentLinkId = -1;
	/** 移動不可ベルトコンベアの有無 */
	private boolean nonMovingConveyor = false;
	/** 埋まり状態 */
	private BurialState burialState = BurialState.NONE;
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
	private int blinkCount = 0;
	/** まばたき、表情の値を代入 */
	private int blinkType = 0;
	/** プレイヤーにすりすりされているか */
	private boolean surisuriFromPlayer = false;
	/** ぷるぷるアニメーションの位相(左右揺れのトグル) */
	private boolean shakePhase = false;
	/** 粘着板で背中を固定されている */
	private boolean fixBack = false;
	/** ダメージを受けていない期間 */
	protected int noDamagePeriod = 0;
	/** 飢餓状態になっていない期間 */
	protected int noHungryPeriod = 0;
	/** スーパーむーしゃむーしゃタイムのおかげで飢餓状態にならない期間 */
	protected int superEatingNoHungryPeriod = 0;
	/** 妊娠期間 */
	protected int pregnantPeriod = 0;
	/** 発情期間 */
	protected int excitingPeriod = 0;
	/** 睡眠期間 */
	@JsonProperty
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
	protected long lastSurisuriTime = 0;
	/** 最後にプレイヤーがアクションを行った時間 */
	private long lastActionTime = 0;
	/** 出産期間のブースト（この分だけ早まる） */
	protected int pregnancyPeriodBoost = 0;
	/** 発情期間のブースト（この分だけ早まる） */
	private int excitementPeriodBoost = 0;
	/** うんうんブースト */
	protected int excretionBoost = 0;
	/** 移動対象（移動先） */
	protected int moveTargetId = -1;
	/** 移動対象のX座標オフセット */
	private int targetOffsetX = 0;
	/** 移動対象のY座標オフセット */
	private int targetOffsetY = 0;
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
	/** 生まれてからの基準年齢 */
	private long birthAge = -1;
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
	private String messageBuffer;
	/** いくつメッセージが溜まってるか */
	protected int messageTicks = 0;
	/** その場に留まってる回数 */
	private int stayTicks = 0;
	/** とどまる限界 */
	protected int stayTime = Const.STAYLIMIT;
	/** 落下ダメージ */
	protected int falldownDamage = 0;
	/** あんこ量 */
	private int ankoAmount = 0;
	/** 壁に引っかかった回数 */
	private int blockedTicks = 0;
	/** 死なない期間 */
	protected int cantDiePeriod = 0;
	/** 実ゆかどうか */
	@JsonProperty("unBirth")
	protected boolean unBirth = false;
	/** 喋れる状態かどうか */
	private boolean canTalk = true;
	/** メッセージラインの色 */
	private Color4y messageLineColor;
	/** メッセージボックスの色 */
	private Color4y messageBoxColor;
	/** メッセージテキストの色 */
	private Color4y messageTextColor;
	/** メッセージストローク */
	private BasicStrokeEX messageWindowStroke;
	/** メッセージテキストのサイズ */
	private int messageTextSize;
	/** 強制的に誕生時メッセージを言わされるかどうか */
	private boolean birthMessageForced = false;
	/** ゆっくりのオブジェクトのユニークID */
	private int uniqueID = 0;
	/** どのゆっくり的なメッセージを言うか */
	private YukkuriType msgType = null;
	/** どのゆっくり的なうんうんをするか */
	private YukkuriType shitType = null;
	/** 右ペインメニューのピン留めをされているかどうか */
	private boolean isPinned = false;
	/** ゆっくりの移動速度 */
	protected int speed = 100;
	/** 次の落下でダメージを受けないかどうか */
	private boolean noDamageNextFall = false;
	/** この個体に対して発行されたイベントのリスト */
	private List<EventPacket> eventList = new LinkedList<EventPacket>();
	/** 現在実行中のイベント */
	private EventPacket currentEvent = null;
	/** 表情の強制設定 */
	protected int forceFace = -1;
	/** 影の表示有無 */
	private boolean shadowVisible = true;
	/** イベントで設定されたアクション */
	private Event eventResult = Event.DONOTHING;
	/** ゆ虐神拳により 膨らんでいるか/伸ばされているか/押さえつけられているか */
	private boolean[] abFlagGodHand = { false, false, false };
	/** ゆ虐神拳の回数 */
	private int godHandHoldCount = 0;
	private int godHandStretchCount = 0;
	private int godHandCompressCount = 0;
	/** お気に入りアイテム */
	private HashMap<FavItemType, Integer> favoriteItems = new HashMap<FavItemType, Integer>();
	/** 持ち歩きアイテム */
	private HashMap<TakeoutItemType, Integer> carryItems = new HashMap<TakeoutItemType, Integer>();
	/** ゆっくり本体の購入基本額 */
	private int cost = 200;
	/** ゆっくり本体、中身の売却基本額 飼いゆとしての価値/加工品としての価値 */
	private int saleValues[] = { 50, 100 };
	/** たかっているアリの数 */
	protected int antCount = 0;
	/** うにょ機能を使用するかどうかのフラグ */
	private int unyoMode = 1;
	/** うにょの高さ方向 */
	protected int unyoOffsetH = 0;
	/** うにょの横方向 */
	protected int unyoOffsetW = 0;
	/** うにょの動きの強さ */
	public final static int UNYOSTRENGTH[] = { 4, 7, 10 };
	/** 「取る」で取られているかどうか */
	private boolean taken = false;

	/**
	 * 各ゆっくりに特有の画像読み込みのためのファイル名を取得する.
	 * 
	 * @return 各ゆっくりに特有の画像読み込みのためのファイル名
	 */
	@JsonProperty
	public String getBaseBodyFileName() {
		return bodyNameSet.getBaseBodyFileName();
	}

	/**
	 * 各ゆっくりに特有の画像読み込みのためのファイル名を設定する.
	 * 
	 * @param baseBodyFileName 各ゆっくりに特有の画像読み込みのためのファイル名
	 */
	@JsonProperty
	public void setBaseBodyFileName(String baseBodyFileName) {
		bodyNameSet.setBaseBodyFileName(baseBodyFileName);
	}

	/**
	 * 赤ゆの一人称を取得する.
	 * 
	 * @return 赤ゆの一人称
	 */
	@JsonProperty
	public String[] getBabyNames() {
		return bodyNameSet.getBabyNames();
	}

	/**
	 * 赤ゆの一人称を設定する.
	 * 
	 * @param babyNames 赤ゆの一人称
	 */
	@JsonProperty
	public void setBabyNames(String[] babyNames) {
		bodyNameSet.setBabyNames(babyNames);
	}

	/**
	 * 子ゆの一人称を取得する.
	 * 
	 * @return 子ゆの一人称
	 */
	@JsonProperty
	public String[] getChildNames() {
		return bodyNameSet.getChildNames();
	}

	/**
	 * 子ゆの一人称を設定する.
	 * 
	 * @param childNames 子ゆの一人称
	 */
	@JsonProperty
	public void setChildNames(String[] childNames) {
		bodyNameSet.setChildNames(childNames);
	}

	/**
	 * 成ゆの一人称を取得する.
	 * 
	 * @return 成ゆの一人称
	 */
	@JsonProperty
	public String[] getAdultNames() {
		return bodyNameSet.getAdultNames();
	}

	/**
	 * 成ゆの一人称を設定する.
	 * 
	 * @param adultNames 成ゆの一人称
	 */
	@JsonProperty
	public void setAdultNames(String[] adultNames) {
		bodyNameSet.setAdultNames(adultNames);
	}

	/**
	 * [0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称 を取得する.
	 * 
	 * @return [0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称
	 */
	@JsonProperty
	public String[] getMyNames() {
		return bodyNameSet.getMyNames();
	}

	/**
	 * [0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称 を設定する.
	 * 
	 * @param myNames [0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称
	 */
	@JsonProperty
	public void setMyNames(String[] myNames) {
		bodyNameSet.setMyNames(myNames);
	}

	/**
	 * 赤ゆの一人称（ダメージ時）を返却する.
	 * 
	 * @return 赤ゆの一人称（ダメージ時）
	 */
	@JsonProperty
	public String[] getBabyNamesDamaged() {
		return bodyNameSet.getBabyNamesDamaged();
	}

	/**
	 * 赤ゆの一人称（ダメージ時）を設定する.
	 * 
	 * @param babyNamesDamaged 赤ゆの一人称（ダメージ時）
	 */
	@JsonProperty
	public void setBabyNamesDamaged(String[] babyNamesDamaged) {
		bodyNameSet.setBabyNamesDamaged(babyNamesDamaged);
	}

	/**
	 * 子ゆの一人称（ダメージ時）を取得する.
	 * 
	 * @return 子ゆの一人称（ダメージ時）
	 */
	@JsonProperty
	public String[] getChildNamesDamaged() {
		return bodyNameSet.getChildNamesDamaged();
	}

	/**
	 * 子ゆの一人称（ダメージ時）を設定する.
	 * 
	 * @param childNamesDamaged 子ゆの一人称（ダメージ時）
	 */
	@JsonProperty
	public void setChildNamesDamaged(String[] childNamesDamaged) {
		bodyNameSet.setChildNamesDamaged(childNamesDamaged);
	}

	/**
	 * 大人ゆの一人称（ダメージ時）を取得する.
	 * 
	 * @return 大人ゆの一人称（ダメージ時）
	 */
	@JsonProperty
	public String[] getAdultNamesDamaged() {
		return bodyNameSet.getAdultNamesDamaged();
	}

	/**
	 * 大人ゆの一人称（ダメージ時） を設定する.
	 * 
	 * @param adultNamesDamaged 大人ゆの一人称（ダメージ時）
	 */
	@JsonProperty
	public void setAdultNamesDamaged(String[] adultNamesDamaged) {
		bodyNameSet.setAdultNamesDamaged(adultNamesDamaged);
	}

	/**
	 * ダメージ時の、[0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称を取得する.
	 * 
	 * @return ダメージ時の、[0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称
	 */
	@JsonProperty
	public String[] getMyNamesDamaged() {
		return bodyNameSet.getMyNamesDamaged();
	}

	/**
	 * ダメージ時の、[0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称 を設定する.
	 * 
	 * @param myNamesDamaged ダメージ時の、[0]:赤ゆの一人称 [1]:子ゆの一人称 [2]:大人ゆの一人称
	 */
	@JsonProperty
	public void setMyNamesDamaged(String[] myNamesDamaged) {
		bodyNameSet.setMyNamesDamaged(myNamesDamaged);
	}

	/**
	 * 名前関連データを他の BodyAttributes から深く複製する.
	 *
	 * @param from 複製元
	 */
	public void copyBodyNameSetFrom(BodyAttributes from) {
		if (from == null) {
			return;
		}
		bodyNameSet.copyFrom(from.bodyNameSet);
	}

	public static BufferedImage[] getShadowImages() {
		return shadowImages;
	}

	public static void setShadowImages(BufferedImage[] shadowImages) {
		BodyAttributes.shadowImages = shadowImages;
	}

	/**
	 * 影画像のサイズ定義を取得する.
	 * 
	 * @return 影画像のサイズ定義
	 */
	public static int[] getShadowImgW() {
		return shadowImgW;
	}

	/**
	 * 影画像のサイズ定義を設定する.
	 * 
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
	 * 
	 * @return 影画像の中心定義
	 */
	public static int[] getShadowPivX() {
		return shadowPivX;
	}

	/**
	 * 影画像の中心定義を設定する.
	 * 
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
	 * 
	 * @return 本体のスプライト定義
	 */
	@JsonProperty
	public Sprite[] getBodySpr() {
		return bodySpriteSet.getBodySpr();
	}

	/**
	 * 本体のスプライト定義を設定する.
	 * 
	 * @param bodySpr 本体のスプライト定義
	 */
	@JsonProperty
	public void setBodySpr(Sprite[] bodySpr) {
		bodySpriteSet.setBodySpr(bodySpr);
	}

	/**
	 * 拡幅分のスプライト定義を取得する.
	 * 
	 * @return 拡幅分のスプライト定義
	 */
	@JsonProperty
	public Sprite[] getExpandSpr() {
		return bodySpriteSet.getExpandSpr();
	}

	/**
	 * 拡幅分のスプライト定義を設定する.
	 * 
	 * @param expandSpr 拡幅分のスプライト定義
	 */
	@JsonProperty
	public void setExpandSpr(Sprite[] expandSpr) {
		bodySpriteSet.setExpandSpr(expandSpr);
	}

	/**
	 * おさげのスプライト定義を取得する.
	 * 
	 * @return おさげのスプライト定義
	 */
	@JsonProperty
	public Sprite[] getBraidSpr() {
		return bodySpriteSet.getBraidSpr();
	}

	/**
	 * おさげのスプライト定義を設定する.
	 * 
	 * @param braidSpr おさげのスプライト定義
	 */
	@JsonProperty
	public void setBraidSpr(Sprite[] braidSpr) {
		bodySpriteSet.setBraidSpr(braidSpr);
	}

	/**
	 * スプライト関連データを他の BodyAttributes から深く複製する.
	 *
	 * @param from 複製元
	 */
	public void copyBodySpriteSetFrom(BodyAttributes from) {
		if (from == null) {
			return;
		}
		bodySpriteSet.copyFrom(from.bodySpriteSet);
	}

	/**
	 * 配列型の統計データを他の BodyAttributes から深く複製する.
	 *
	 * @param from 複製元
	 */
	public void copyBodyStatSetFrom(BodyAttributes from) {
		if (from == null) {
			return;
		}
		bodyStatProfile.copyFrom(from.bodyStatProfile);
	}

	/**
	 * 時刻・閾値系データを他の BodyAttributes から深く複製する.
	 *
	 * @param from 複製元
	 */
	public void copyBodyTimingSetFrom(BodyAttributes from) {
		if (from == null) {
			return;
		}
		bodyTimingProfile.copyFrom(from.bodyTimingProfile);
	}

	/**
	 * 行動・性格・繁殖寄りのデータを他の BodyAttributes から深く複製する.
	 *
	 * @param from 複製元
	 */
	public void copyBodyBehaviorSetFrom(BodyAttributes from) {
		if (from == null) {
			return;
		}
		bodyBehaviorProfile.copyFrom(from.bodyBehaviorProfile);
	}

	/**
	 * 一回の食事量 を取得する.
	 * 
	 * @return 一回の食事量
	 */
	@JsonProperty
	public int[] getEatAmountBase() {
		return BodyStatRule.getEatAmountBase(this);
	}

	/**
	 * 一回の食事量 を設定する.
	 * 
	 * @param eatAmount 一回の食事量
	 */
	@JsonProperty
	public void setEatAmountBase(int[] eatAmount) {
		BodyStatRule.setEatAmountBase(this, eatAmount);
	}

	/**
	 * 体重を取得する.
	 * 
	 * @return 体重
	 */
	@JsonProperty
	public int[] getWeightBase() {
		return BodyStatRule.getWeightBase(this);
	}

	/**
	 * 体重を設定する.
	 * 
	 * @param weight 体重
	 */
	@JsonProperty
	public void setWeightBase(int[] weight) {
		BodyStatRule.setWeightBase(this, weight);
	}

	/**
	 * 空腹限界を取得する.
	 * 
	 * @return 空腹限界
	 */
	@JsonProperty
	public int[] getHungryLimitBase() {
		return BodyStatRule.getHungryLimitBase(this);
	}

	/**
	 * 空腹限界を設定する.
	 * 
	 * @param hungryLimit 空腹限界
	 */
	@JsonProperty
	public void setHungryLimitBase(int[] hungryLimit) {
		BodyStatRule.setHungryLimitBase(this, hungryLimit);
	}

	/**
	 * うんうん限界を取得する.
	 * 
	 * @return うんうん限界
	 */
	@JsonProperty
	public int[] getShitLimitBase() {
		return BodyStatRule.getShitLimitBase(this);
	}

	/**
	 * うんうん限界を設定する.
	 * 
	 * @param shitLimit うんうん限界
	 */
	@JsonProperty
	public void setShitLimitBase(int[] shitLimit) {
		BodyStatRule.setShitLimitBase(this, shitLimit);
	}

	/**
	 * ダメージ限界を取得する.
	 * 
	 * @return ダメージ限界
	 */
	@JsonProperty
	public int[] getDamageLimitBase() {
		return BodyStatRule.getDamageLimitBase(this);
	}

	/**
	 * ダメージ限界を設定する.
	 * 
	 * @param damageLimit ダメージ限界
	 */
	@JsonProperty
	public void setDamageLimitBase(int[] damageLimit) {
		BodyStatRule.setDamageLimitBase(this, damageLimit);
	}

	/**
	 * ストレス限界を取得する.
	 * 
	 * @return ストレス限界
	 */
	@JsonProperty
	public int[] getStressLimitBase() {
		return BodyStatRule.getStressLimitBase(this);
	}

	/**
	 * ストレス限界を設定する.
	 * 
	 * @param stressLimit ストレス限界
	 */
	@JsonProperty
	public void setStressLimitBase(int[] stressLimit) {
		BodyStatRule.setStressLimitBase(this, stressLimit);
	}

	/**
	 * なつき度限界を取得する.
	 * 
	 * @return なつき度限界
	 */
	@JsonProperty
	public int getLovePlayerLimitBase() {
		return BodyPreferenceRule.getLovePlayerLimitBase(this);
	}

	/**
	 * なつき度限界を設定する.
	 * 
	 * @param lovePlayerLimit なつき度限界
	 */
	@JsonProperty
	public void setLovePlayerLimitBase(int lovePlayerLimit) {
		BodyPreferenceRule.setLovePlayerLimitBase(this, lovePlayerLimit);
	}

	/**
	 * 味覚レベル を取得する.
	 * 
	 * @return 味覚レベル
	 */
	@JsonProperty
	public int[] getTangLevelBase() {
		return BodyStatRule.getTangLevelBase(this);
	}

	/**
	 * 味覚レベル を設定する.
	 * 
	 * @param tangLevel 味覚レベル
	 */
	@JsonProperty
	public void setTangLevelBase(int[] tangLevel) {
		BodyStatRule.setTangLevelBase(this, tangLevel);
	}

	/**
	 * 同一方向に動き続けるかを取得する.
	 * 
	 * @return 同一方向に動き続ける倍率
	 */
	@JsonProperty
	public int getSameDirectionFactor() {
		return BodyPreferenceRule.getSameDirectionFactor(this);
	}

	/**
	 * 同一方向に動き続ける倍率を設定する.
	 * 
	 * @param sameDirectionFactor 同一方向に動き続ける倍率
	 */
	@JsonProperty
	public void setSameDirectionFactor(int sameDirectionFactor) {
		BodyPreferenceRule.setSameDirectionFactor(this, sameDirectionFactor);
	}

	/**
	 * ゲーム内12分、衝動の抑制のための変数 を取得する.
	 * 
	 * @return ゲーム内12分、衝動の抑制のための変数
	 */
	@JsonProperty
	public int getDeclinePeriodBase() {
		return BodyTimingRule.getDeclinePeriodBase(this);
	}

	/**
	 * ゲーム内12分、衝動の抑制のための変数 を設定する.
	 * 
	 * @param declinePeriod ゲーム内12分、衝動の抑制のための変数
	 */
	@JsonProperty
	public void setDeclinePeriodBase(int declinePeriod) {
		BodyTimingRule.setDeclinePeriodBase(this, declinePeriod);
	}

	/**
	 * 壁等にブロックされた回数の限界（怒りだす等） を取得する.
	 * 
	 * @return 壁等にブロックされた回数の限界（怒りだす等）
	 */
	@JsonProperty
	public int getBlockedLimitBase() {
		return BodyTimingRule.getBlockedLimitBase(this);
	}

	/**
	 * 壁等にブロックされた回数の限界（怒りだす等） を設定する.
	 * 
	 * @param blockedLimit 壁等にブロックされた回数の限界（怒りだす等）
	 */
	@JsonProperty
	public void setBlockedLimitBase(int blockedLimit) {
		BodyTimingRule.setBlockedLimitBase(this, blockedLimit);
	}

	/**
	 * 汚れ限界（超えるとゆかび状態） を取得する.
	 * 
	 * @return 汚れ限界（超えるとゆかび状態）
	 */
	@JsonProperty
	public int getDirtyPeriodBase() {
		return BodyTimingRule.getDirtyPeriodBase(this);
	}

	/**
	 * 汚れ限界（超えるとゆかび状態） を設定する.
	 * 
	 * @param dirtyPeriod 汚れ限界（超えるとゆかび状態）
	 */
	@JsonProperty
	public void setDirtyPeriodBase(int dirtyPeriod) {
		BodyTimingRule.setDirtyPeriodBase(this, dirtyPeriod);
	}

	/**
	 * 視界を取得する.
	 * 
	 * @return 視界
	 */
	@JsonProperty
	public int getEyesightBase() {
		return BodyTimingRule.getEyesightBase(this);
	}

	/**
	 * 視界を設定する.
	 * 
	 * @param eyesight 視界
	 */
	@JsonProperty
	public void setEyesightBase(int eyesight) {
		BodyTimingRule.setEyesightBase(this, eyesight);
	}

	/**
	 * 強さを取得する.
	 * 
	 * @return 強さ
	 */
	@JsonProperty
	public int[] getStrengthBase() {
		return BodyStatRule.getStrengthBase(this);
	}

	/**
	 * 強さを設定する.
	 * 
	 * @param strength 強さ
	 */
	@JsonProperty
	public void setStrengthBase(int[] strength) {
		BodyStatRule.setStrengthBase(this, strength);
	}

	/**
	 * ゆかびの潜伏期間 を取得する.
	 * 
	 * @return ゆかびの潜伏期間
	 */
	@JsonProperty
	public int getIncubationPeriodBase() {
		return BodyTimingRule.getIncubationPeriodBase(this);
	}

	/**
	 * ゆかびの潜伏期間 を設定する.
	 * 
	 * @param incubationPeriod ゆかびの潜伏期間
	 */
	@JsonProperty
	public void setIncubationPeriodBase(int incubationPeriod) {
		BodyTimingRule.setIncubationPeriodBase(this, incubationPeriod);
	}

	@JsonIgnore
	public BodyTimingProfile getBodyTimingProfileRaw() {
		return bodyTimingProfile;
	}

	@JsonIgnore
	public BodyStatProfile getBodyStatProfileRaw() {
		return bodyStatProfile;
	}

	@JsonIgnore
	public BodyBehaviorProfile getBodyBehaviorProfileRaw() {
		return bodyBehaviorProfile;
	}

	/**
	 * 赤ゆ期間 を取得する.
	 *
	 * @return 赤ゆ期間
	 */
	@JsonProperty
	public int getBabyLimitBase() {
		return BodyTimingRule.getBabyLimitBase(this);
	}

	/**
	 * 赤ゆ期間 を設定する.
	 *
	 * @param babyLimit 赤ゆ期間
	 */
	@JsonProperty
	public void setBabyLimitBase(int babyLimit) {
		BodyTimingRule.setBabyLimitBase(this, babyLimit);
	}

	/**
	 * 子ゆ期間 を取得する.
	 *
	 * @return 子ゆ期間
	 */
	@JsonProperty
	public int getChildLimitBase() {
		return BodyTimingRule.getChildLimitBase(this);
	}

	/**
	 * 子ゆ期間 を設定する.
	 *
	 * @param cHILDLIMIT 子ゆ期間
	 */
	@JsonProperty
	public void setChildLimitBase(int cHILDLIMIT) {
		BodyTimingRule.setChildLimitBase(this, cHILDLIMIT);
	}

	/**
	 * 寿命を取得する.
	 *
	 * @return 寿命
	 */
	@JsonProperty
	public int getLifeLimitBase() {
		return BodyTimingRule.getLifeLimitBase(this);
	}

	/**
	 * 寿命を設定する.
	 *
	 * @param lIFELIMIT 寿命
	 */
	@JsonProperty
	public void setLifeLimitBase(int lIFELIMIT) {
		BodyTimingRule.setLifeLimitBase(this, lIFELIMIT);
	}

	/**
	 * 腐敗日数 を取得する.
	 *
	 * @return 腐敗日数
	 */
	@JsonProperty
	public int getRottingTimeBase() {
		return BodyTimingRule.getRottingTimeBase(this);
	}

	/**
	 * 腐敗日数 を設定する.
	 *
	 * @param rOTTINGTIME 腐敗日数
	 */
	@JsonProperty
	public void setRottingTimeBase(int rOTTINGTIME) {
		BodyTimingRule.setRottingTimeBase(this, rOTTINGTIME);
	}

	/**
	 * 足の速さを取得する.
	 *
	 * @return 足の速さ
	 */
	@JsonProperty
	public int[] getStepBase() {
		return BodyTimingRule.getStepBase(this);
	}

	/**
	 * 足の速さを設定する.
	 *
	 * @param step 足の速さ
	 */
	@JsonProperty
	public void setStepBase(int[] step) {
		BodyTimingRule.setStepBase(this, step);
	}

	/**
	 * リラックス状態の期間を取得する.
	 *
	 * @return リラックス状態の期間
	 */
	@JsonProperty
	public int getRelaxPeriodBase() {
		return BodyTimingRule.getRelaxPeriodBase(this);
	}

	/**
	 * リラックス状態の期間 を設定する.
	 *
	 * @param relaxPeriod リラックス状態の期間
	 */
	@JsonProperty
	public void setRelaxPeriodBase(int relaxPeriod) {
		BodyTimingRule.setRelaxPeriodBase(this, relaxPeriod);
	}

	/**
	 * 発情状態の期間 を取得する.
	 *
	 * @return 発情状態の期間
	 */
	@JsonProperty
	public int getExcitePeriodBase() {
		return BodyTimingRule.getExcitePeriodBase(this);
	}

	/**
	 * 発情状態の期間 を設定する.
	 *
	 * @param excitePeriod 発情状態の期間
	 */
	@JsonProperty
	public void setExcitePeriodBase(int excitePeriod) {
		BodyTimingRule.setExcitePeriodBase(this, excitePeriod);
	}

	/**
	 * 妊娠期間 を取得する.
	 *
	 * @return 妊娠期間
	 */
	@JsonProperty
	public int getPregPeriodBase() {
		return BodyTimingRule.getPregPeriodBase(this);
	}

	/**
	 * 妊娠期間 を設定する.
	 *
	 * @param pregPeriod 妊娠期間
	 */
	@JsonProperty
	public void setPregPeriodBase(int pregPeriod) {
		BodyTimingRule.setPregPeriodBase(this, pregPeriod);
	}

	/**
	 * 睡眠時間 を取得する.
	 *
	 * @return 睡眠時間
	 */
	@JsonProperty
	public int getSleepPeriodBase() {
		return BodyTimingRule.getSleepPeriodBase(this);
	}

	/**
	 * 睡眠時間 を設定する.
	 *
	 * @param sleepPeriod 睡眠時間
	 */
	@JsonProperty
	public void setSleepPeriodBase(int sleepPeriod) {
		BodyTimingRule.setSleepPeriodBase(this, sleepPeriod);
	}

	/**
	 * アクティブな期間 を取得する.
	 *
	 * @return アクティブな期間
	 */
	@JsonProperty
	public int getActivePeriodBase() {
		return BodyTimingRule.getActivePeriodBase(this);
	}

	/**
	 * アクティブな期間 を設定する.
	 *
	 * @param activePeriod アクティブな期間
	 */
	@JsonProperty
	public void setActivePeriodBase(int activePeriod) {
		BodyTimingRule.setActivePeriodBase(this, activePeriod);
	}

	/**
	 * 怒り期間 を取得する.
	 *
	 * @return 怒り期間
	 */
	@JsonProperty
	public int getAngryPeriodBase() {
		return BodyTimingRule.getAngryPeriodBase(this);
	}

	/**
	 * 怒り期間 を設定する.
	 *
	 * @param angryPeriod 怒り期間
	 */
	@JsonProperty
	public void setAngryPeriodBase(int angryPeriod) {
		BodyTimingRule.setAngryPeriodBase(this, angryPeriod);
	}

	/**
	 * 恐怖期間 を取得する.
	 *
	 * @return 恐怖期間
	 */
	@JsonProperty
	public int getScarePeriodBase() {
		return BodyTimingRule.getScarePeriodBase(this);
	}

	/**
	 * 恐怖期間 を設定する.
	 *
	 * @param scarePeriod 恐怖期間
	 */
	@JsonProperty
	public void setScarePeriodBase(int scarePeriod) {
		BodyTimingRule.setScarePeriodBase(this, scarePeriod);
	}

	/**
	 * 攻撃された際のぴこぴこ破壊確率。0だと破壊されない を取得する.
	 * 
	 * @return 攻撃された際のぴこぴこ破壊確率。0だと破壊されない
	 */
	public int getBraidBreakChance() {
		return BodyBehaviorRule.getBraidBreakChance(this);
	}

	/**
	 * 攻撃された際のぴこぴこ破壊確率。0だと破壊されない を設定する.
	 * 
	 * @param braidBreakChance 攻撃された際のぴこぴこ破壊確率。0だと破壊されない
	 */
	public void setBraidBreakChance(int braidBreakChance) {
		BodyBehaviorRule.setBraidBreakChance(this, braidBreakChance);
	}

	/**
	 * 何回のうち1回の確率ですりすり事故で妊娠するかの値 を取得する.
	 * 
	 * @return 何回のうち1回の確率ですりすり事故で妊娠するかの値
	 */
	public int getSurisuriAccidentProb() {
		return BodyBehaviorRule.getSurisuriAccidentProb(this);
	}

	/**
	 * 何回のうち1回の確率ですりすり事故で妊娠するかの値 を設定する.
	 * 
	 * @param surisuriAccidentProb 何回のうち1回の確率ですりすり事故で妊娠するかの値
	 */
	public void setSurisuriAccidentProb(int surisuriAccidentProb) {
		BodyBehaviorRule.setSurisuriAccidentProb(this, surisuriAccidentProb);
	}

	/**
	 * 何回のうち1回の確率で路上で車に轢かれるかの値 を取得する.
	 * 
	 * @return 何回のうち1回の確率で路上で車に轢かれるかの値
	 */
	public int getCarAccidentProb() {
		return BodyBehaviorRule.getCarAccidentProb(this);
	}

	/**
	 * 何回のうち1回の確率で路上で車に轢かれるかの値 を設定する.
	 * 
	 * @param carAccidentProb 何回のうち1回の確率で路上で車に轢かれるかの値
	 */
	public void setCarAccidentProb(int carAccidentProb) {
		BodyBehaviorRule.setCarAccidentProb(this, carAccidentProb);
	}

	/**
	 * 何回のうち1回の確率であんよが傷ついているとあんよが破壊されるかの確率 を取得する.
	 * 
	 * @return 何回のうち1回の確率であんよが傷ついているとあんよが破壊されるかの確率
	 */
	public int getBreakBodyByShitProb() {
		return BodyBehaviorRule.getBreakBodyByShitProb(this);
	}

	/**
	 * 何回のうち1回の確率であんよが傷ついているとあんよが破壊されるかの確率 を設定する.
	 * 
	 * @param breakBodyByShitProb 何回のうち1回の確率であんよが傷ついているとあんよが破壊されるかの確率
	 */
	public void setBreakBodyByShitProb(int breakBodyByShitProb) {
		BodyBehaviorRule.setBreakBodyByShitProb(this, breakBodyByShitProb);
	}

	/**
	 * 何回のうち1回の確率で苦いフードを食べた際にゆ下痢になるかの確率 を取得する.
	 * 
	 * @return 何回のうち1回の確率で苦いフードを食べた際にゆ下痢になるかの確率
	 */
	public int getDiarrheaProb() {
		return BodyBehaviorRule.getDiarrheaProb(this);
	}

	/**
	 * 何回のうち1回の確率で苦いフードを食べた際にゆ下痢になるかの確率 を設定する.
	 * 
	 * @param diarrheaProb 何回のうち1回の確率で苦いフードを食べた際にゆ下痢になるかの確率
	 */
	public void setDiarrheaProb(int diarrheaProb) {
		BodyBehaviorRule.setDiarrheaProb(this, diarrheaProb);
	}

	/**
	 * 自主洗浄失敗確率（賢い）を取得する.
	 *
	 * @return 自主洗浄失敗確率配列 [0]:赤ゆ [1]:子ゆ [2]:成ゆ
	 */
	public int[] getCleaningFailProbWise() {
		return BodyPreferenceRule.getCleaningFailProbWise(this);
	}

	/**
	 * 自主洗浄失敗確率（賢い）を設定する.
	 *
	 * @param cleaningFailProbWise 自主洗浄失敗確率配列 [0]:赤ゆ [1]:子ゆ [2]:成ゆ
	 */
	public void setCleaningFailProbWise(int[] cleaningFailProbWise) {
		BodyPreferenceRule.setCleaningFailProbWise(this, cleaningFailProbWise);
	}

	/**
	 * 自主洗浄失敗確率（普通）を取得する.
	 *
	 * @return 自主洗浄失敗確率配列 [0]:赤ゆ [1]:子ゆ [2]:成ゆ
	 */
	public int[] getCleaningFailProbAverage() {
		return BodyPreferenceRule.getCleaningFailProbAverage(this);
	}

	/**
	 * 自主洗浄失敗確率（普通）を設定する.
	 *
	 * @param cleaningFailProbAverage 自主洗浄失敗確率配列 [0]:赤ゆ [1]:子ゆ [2]:成ゆ
	 */
	public void setCleaningFailProbAverage(int[] cleaningFailProbAverage) {
		BodyPreferenceRule.setCleaningFailProbAverage(this, cleaningFailProbAverage);
	}

	/**
	 * 自主洗浄失敗確率（餡子脳）を取得する.
	 *
	 * @return 自主洗浄失敗確率配列 [0]:赤ゆ [1]:子ゆ [2]:成ゆ
	 */
	public int[] getCleaningFailProbFool() {
		return BodyPreferenceRule.getCleaningFailProbFool(this);
	}

	/**
	 * 自主洗浄失敗確率（餡子脳）を設定する.
	 *
	 * @param cleaningFailProbFool 自主洗浄失敗確率配列 [0]:赤ゆ [1]:子ゆ [2]:成ゆ
	 */
	public void setCleaningFailProbFool(int[] cleaningFailProbFool) {
		BodyPreferenceRule.setCleaningFailProbFool(this, cleaningFailProbFool);
	}

	/**
	 * 何回のうち１回の確率で発情するかの確率 を取得する.
	 * 
	 * @return 何回のうち１回の確率で発情するかの確率
	 */
	public int getExciteProb() {
		return BodyBehaviorRule.getExciteProb(this);
	}

	/**
	 * 何回のうち１回の確率で発情するかの確率 を設定する.
	 * 
	 * @param exciteProb 何回のうち１回の確率で発情するかの確率
	 */
	public void setExciteProb(int exciteProb) {
		BodyBehaviorRule.setExciteProb(this, exciteProb);
	}

	/**
	 * 固有の免疫力（個体値。これは仮） を取得する.
	 * 
	 * @return 固有の免疫力（個体値。これは仮）
	 */
	public int getImmunityStrength() {
		return BodyBehaviorRule.getImmunityStrength(this);
	}

	/**
	 * 固有の免疫力（個体値。これは仮） を設定する.
	 * 
	 * @param immunityStrength 固有の免疫力（個体値。これは仮）
	 */
	public void setImmunityStrength(int immunityStrength) {
		BodyBehaviorRule.setImmunityStrength(this, immunityStrength);
	}

	/**
	 * 免疫力(左から順に赤ゆ、子ゆ、成ゆ、老ゆ) を取得する.
	 * 
	 * @return 免疫力(左から順に赤ゆ、子ゆ、成ゆ、老ゆ)
	 */
	@JsonProperty
	public int[] getImmunity() {
		return BodyStatRule.getImmunity(this);
	}

	/**
	 * 免疫力(左から順に赤ゆ、子ゆ、成ゆ、老ゆ) を設定する.
	 * 
	 * @param immunity 免疫力(左から順に赤ゆ、子ゆ、成ゆ、老ゆ)
	 */
	@JsonProperty
	public void setImmunity(int[] immunity) {
		BodyStatRule.setImmunity(this, immunity);
	}

	/**
	 * 性格変化の切り替え を取得する.
	 * 
	 * @return 性格変化の切り替え
	 */
	public boolean isNotChangeCharacter() {
		return BodyPreferenceRule.isNotChangeCharacter(this);
	}

	@JsonIgnore
	public boolean isNotChangeCharacterRaw() {
		return bodyBehaviorProfile.isNotChangeCharacter();
	}

	/**
	 * 性格変化の切り替え を設定する.
	 * 
	 * @param notChangeCharacter 性格変化の切り替え
	 */
	public void setNotChangeCharacter(boolean notChangeCharacter) {
		BodyPreferenceRule.setNotChangeCharacter(this, notChangeCharacter);
	}

	/**
	 * ゲスポイント を取得する.
	 * 
	 * @return ゲスポイント
	 */
	public int getAttitudePoint() {
		return BodyBehaviorRule.getAttitudePoint(this);
	}

	/**
	 * ゲスポイント を設定する.
	 * 
	 * @param attitudePoint ゲスポイント
	 */
	public void setAttitudePoint(int attitudePoint) {
		BodyBehaviorRule.setAttitudePoint(this, attitudePoint);
	}

	/**
	 * ゲス限界 を取得する.
	 * 
	 * @return ゲス限界
	 */
	@JsonProperty
	public int[] getRudeLimit() {
		return BodyStatRule.getRudeLimit(this);
	}

	/**
	 * ゲス限界 を設定する.
	 * 
	 * @param rudeLimit ゲス限界
	 */
	@JsonProperty
	public void setRudeLimit(int[] rudeLimit) {
		BodyStatRule.setRudeLimit(this, rudeLimit);
	}

	/**
	 * 善良限界 を取得する.
	 * 
	 * @return 善良限界
	 */
	@JsonProperty
	public int[] getNiceLimit() {
		return BodyPreferenceRule.getNiceLimit(this);
	}

	/**
	 * 善良限界 を設定する.
	 * 
	 * @param niceLimit 善良限界
	 */
	@JsonProperty
	public void setNiceLimit(int[] niceLimit) {
		BodyPreferenceRule.setNiceLimit(this, niceLimit);
	}

	/**
	 * 妊娠限界 を取得する.
	 * 
	 * @return 妊娠限界
	 */
	public int getPregnantLimit() {
		return BodyBehaviorRule.getPregnantLimit(this);
	}

	/**
	 * 妊娠限界 を設定する.
	 * 
	 * @param pregnantLimit 妊娠限界
	 */
	public void setPregnantLimit(int pregnantLimit) {
		BodyBehaviorRule.setPregnantLimit(this, pregnantLimit);
	}

	/**
	 * 妊娠限界を厳密に扱うかどうかを取得する.
	 * 
	 * @return 妊娠限界を厳密に扱うかどうか
	 */
	public boolean isUseRealPregnantLimit() {
		return BodyPreferenceRule.isUseRealPregnantLimit(this);
	}

	@JsonIgnore
	public boolean isUseRealPregnantLimitRaw() {
		return bodyBehaviorProfile.isUseRealPregnantLimit();
	}

	/**
	 * 妊娠限界を厳密に扱うかどうかを設定する.
	 * 
	 * @param useRealPregnantLimit 妊娠限界を厳密に扱うかどうか
	 */
	public void setUseRealPregnantLimit(boolean useRealPregnantLimit) {
		BodyPreferenceRule.setUseRealPregnantLimit(this, useRealPregnantLimit);
	}

	/**
	 * 画像がまりちゃ流しか を設定する.
	 * 
	 * @return 画像がまりちゃ流しか
	 */
	public boolean isImageNagasiMode() {
		return BodyDisplayRule.isImageNagasiMode(this);
	}

	@JsonIgnore
	public boolean isImageNagasiModeRaw() {
		return imageNagasiMode;
	}

	/**
	 * 画像がまりちゃ流しか を設定する.
	 * 
	 * @param imageNagasiMode 画像がまりちゃ流しか
	 */
	public void setImageNagasiMode(boolean imageNagasiMode) {
		this.imageNagasiMode = imageNagasiMode;
	}

	/**
	 * 飼いゆ、野良ゆなどのランク を取得する.
	 * 
	 * @return 飼いゆ、野良ゆなどのランク
	 */
	public BodyRank getBodyRank() {
		return bodyRank;
	}

	/**
	 * 飼いゆ、野良ゆなどのランク を設定する.
	 * 
	 * @param bodyRank 飼いゆ、野良ゆなどのランク
	 */
	public void setBodyRank(BodyRank bodyRank) {
		this.bodyRank = bodyRank;
	}

	/**
	 * 群れ内のうんうん奴隷などのランク を取得する.
	 * 
	 * @return 群れ内のうんうん奴隷などのランク
	 */
	public PublicRank getPublicRank() {
		return publicRank;
	}

	/**
	 * 群れ内のうんうん奴隷などのランク を設定する.
	 * 
	 * @param publicRank 群れ内のうんうん奴隷などのランク
	 */
	public void setPublicRank(PublicRank publicRank) {
		this.publicRank = publicRank;
	}

	/**
	 * 移動先目標 destination X座標を取得する.
	 * 
	 * @return 移動先目標 destination X座標
	 */
	public int getDestX() {
		return destX;
	}

	/**
	 * 移動先目標 destination X座標を設定する.
	 * 
	 * @param destX 移動先目標 destination X座標
	 */
	public void setDestX(int destX) {
		this.destX = destX;
	}

	/**
	 * 移動先目標 destination Y座標を取得する.
	 * 
	 * @return 移動先目標 destination Y座標
	 */
	public int getDestY() {
		return destY;
	}

	/**
	 * 移動先目標 destination Y座標を設定する.
	 * 
	 * @param destY 移動先目標 destination Y座標
	 */
	public void setDestY(int destY) {
		this.destY = destY;
	}

	/**
	 * 移動先目標 destination Z座標を取得する.
	 * 
	 * @return 移動先目標 destination Z座標
	 */
	public int getDestZ() {
		return destZ;
	}

	/**
	 * 移動先目標 destination Z座標を設定する.
	 * 
	 * @param destZ 移動先目標 destination Z座標座標
	 */
	public void setDestZ(int destZ) {
		this.destZ = destZ;
	}

	/**
	 * 移動量 how many steps to same direction を取得する.
	 * 
	 * @return 移動量 how many steps to same direction
	 */
	public int getCountX() {
		return countX;
	}

	/**
	 * 移動量 how many steps to same direction X座標を設定する.
	 * 
	 * @param countX 移動量 how many steps to same direction X座標
	 */
	public void setCountX(int countX) {
		this.countX = countX;
	}

	/**
	 * 移動量 how many steps to same direction Y座標を取得する.
	 * 
	 * @return 移動量 how many steps to same direction Y座標
	 */
	public int getCountY() {
		return countY;
	}

	/**
	 * 移動量 how many steps to same direction Y座標を設定する.
	 * 
	 * @param countY 移動量 how many steps to same direction Y座標
	 */
	public void setCountY(int countY) {
		this.countY = countY;
	}

	/**
	 * 移動量 how many steps to same direction Z座標を取得する.
	 * 
	 * @return 移動量 how many steps to same direction Z座標
	 */
	public int getCountZ() {
		return countZ;
	}

	/**
	 * 移動量 how many steps to same direction Z座標を設定する.
	 * 
	 * @param countZ 移動量 how many steps to same direction Z座標
	 */
	public void setCountZ(int countZ) {
		this.countZ = countZ;
	}

	/**
	 * 移動方向 direction to move on X座標を取得する.
	 * 
	 * @return 移動方向 direction to move on X座標
	 */
	public int getDirX() {
		return dirX;
	}

	/**
	 * 移動方向 direction to move on X座標を設定する.
	 * 
	 * @param dirX 移動方向 direction to move on X座標
	 */
	public void setDirX(int dirX) {
		this.dirX = dirX;
	}

	/**
	 * 移動方向 direction to move on Y座標を取得する.
	 * 
	 * @return 移動方向 direction to move on Y座標
	 */
	public int getDirY() {
		return dirY;
	}

	/**
	 * 移動方向 direction to move on Y座標を設定する.
	 * 
	 * @param dirY 移動方向 direction to move on Y座標
	 */
	public void setDirY(int dirY) {
		this.dirY = dirY;
	}

	/**
	 * 移動方向 direction to move on Z座標を取得する.
	 * 
	 * @return 移動方向 direction to move on Z座標
	 */
	public int getDirZ() {
		return dirZ;
	}

	/**
	 * 移動方向 direction to move on Z座標を設定する/
	 * 
	 * @param dirZ 移動方向 direction to move on Z座標
	 */
	public void setDirZ(int dirZ) {
		this.dirZ = dirZ;
	}

	/**
	 * 顔の向き direction of face を取得する.
	 * 
	 * @return 顔の向き direction of face
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * 顔の向き direction of face を設定する.
	 * 
	 * @param direction 顔の向き direction of face
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * 蓄積ダメージ counter indicating damage を取得する.
	 * 
	 * @return 蓄積ダメージ counter indicating damage
	 */
	@JsonProperty
	public int getDamage() {
		return BodyCoreStateRule.getDamage(this);
	}

	/**
	 * 蓄積ダメージ counter indicating damage を設定する.
	 * 
	 * @param damage 蓄積ダメージ counter indicating damage
	 */
	@JsonProperty
	public void setDamage(int damage) {
		BodyCoreStateRule.setDamage(this, damage);
	}

	/**
	 * 蓄積ストレス を取得する.
	 * 
	 * @return 蓄積ストレス
	 */
	public int getStress() {
		return BodyCoreStateRule.getStress(this);
	}

	/**
	 * 舌の肥え を取得する.
	 * 
	 * @return 舌の肥え
	 */
	public int getTang() {
		return BodyCoreStateRule.getTang(this);
	}

	/**
	 * 舌の肥え を設定する.
	 * 
	 * @param tang 舌の肥え
	 */
	public void setTang(int tang) {
		BodyCoreStateRule.setTang(this, tang);
	}

	/**
	 * ダメージ外観 を設定する.
	 * 
	 * @param damageState ダメージ外観
	 */
	public void setDamageState(Damage damageState) {
		BodyCoreStateRule.setDamageState(this, damageState);
	}

	/**
	 * 性格 counter indicating shithead/nice etc. を取得する.
	 * 
	 * @return 性格 counter indicating shithead/nice etc.
	 */
	public Attitude getAttitude() {
		return BodyCoreStateRule.getAttitude(this);
	}

	@JsonIgnore
	public Attitude getAttitudeRaw() {
		return attitude;
	}

	/**
	 * 性格 counter indicating shithead/nice etc.を設定する.
	 * 
	 * @param attitude 性格 counter indicating shithead/nice etc.
	 */
	public void setAttitude(Attitude attitude) {
		BodyCoreStateRule.setAttitude(this, attitude);
	}

	@JsonIgnore
	public void setAttitudeRaw(Attitude attitude) {
		this.attitude = attitude;
	}

	/**
	 * 知性 を取得する.
	 * 
	 * @return 知性
	 */
	public Intelligence getIntelligence() {
		return BodyCoreStateRule.getIntelligence(this);
	}

	/**
	 * 知性 を設定する.
	 * 
	 * @param intelligence 知性
	 */
	public void setIntelligence(Intelligence intelligence) {
		BodyCoreStateRule.setIntelligence(this, intelligence);
	}

	@JsonIgnore
	public int getDamageRaw() {
		return damage;
	}

	@JsonIgnore
	public void setDamageRaw(int damage) {
		this.damage = damage;
	}

	@JsonIgnore
	public int getStressRaw() {
		return stress;
	}

	@JsonIgnore
	public void setStressRaw(int stress) {
		this.stress = stress;
	}

	@JsonIgnore
	public int getTangRaw() {
		return tang;
	}

	@JsonIgnore
	public void setTangRaw(int tang) {
		this.tang = tang;
	}

	@JsonIgnore
	public Damage getDamageStateRaw() {
		return damageState;
	}

	@JsonIgnore
	public void setDamageStateRaw(Damage damageState) {
		this.damageState = damageState;
	}

	@JsonIgnore
	public Intelligence getIntelligenceRaw() {
		return intelligence;
	}

	@JsonIgnore
	public void setIntelligenceRaw(Intelligence intelligence) {
		this.intelligence = intelligence;
	}

	@JsonIgnore
	public int getShitRaw() {
		return shit;
	}

	@JsonIgnore
	public void setShitRaw(int shit) {
		this.shit = shit;
	}

	@JsonIgnore
	public int getMemoriesRaw() {
		return memories;
	}

	@JsonIgnore
	public void setMemoriesRaw(int memories) {
		this.memories = memories;
	}

	@JsonIgnore
	public void setTraumaRaw(Trauma trauma) {
		this.trauma = trauma;
	}

	@JsonIgnore
	public int getLovePlayerRaw() {
		return lovePlayer;
	}

	@JsonIgnore
	public void setLovePlayerRaw(int lovePlayer) {
		this.lovePlayer = lovePlayer;
	}

	@JsonIgnore
	public LovePlayer getLovePlayerStateRaw() {
		return lovePlayerState;
	}

	@JsonIgnore
	public void setLovePlayerStateRaw(LovePlayer lovePlayerState) {
		this.lovePlayerState = lovePlayerState;
	}

	@JsonIgnore
	public HairState getHairStateRaw() {
		return hairState;
	}

	@JsonIgnore
	public void setHairStateRaw(HairState hairState) {
		this.hairState = hairState;
	}

	/**
	 * 幸福度 を取得する.
	 * 
	 * @return 幸福度
	 */
	public Happiness getHappiness() {
		return happiness;
	}

	/**
	 * プレイヤーへのなつき度 を設定する.
	 * 
	 * @return プレイヤーへのなつき度
	 */
	public int getLovePlayer() {
		return BodyCoreStateRule.getLovePlayer(this);
	}

	/**
	 * プレイヤーへのなつき度 を設定する.
	 * 
	 * @param lovePlayer プレイヤーへのなつき度
	 */
	public void setLovePlayer(int lovePlayer) {
		BodyCoreStateRule.setLovePlayer(this, lovePlayer);
	}

	/**
	 * プレイヤーへのなつき度概算 を取得する.
	 * 
	 * @return プレイヤーへのなつき度概算
	 */
	public LovePlayer getLovePlayerState() {
		return BodyCoreStateRule.getLovePlayerState(this);
	}

	/**
	 * プレイヤーへのなつき度概算 を設定する.
	 * 
	 * @param lovePlayerState プレイヤーへのなつき度概算
	 */
	public void setLovePlayerState(LovePlayer lovePlayerState) {
		BodyCoreStateRule.setLovePlayerState(this, lovePlayerState);
	}

	/**
	 * 髪の状態 を取得する.
	 * 
	 * @return 髪の状態
	 */
	public HairState getHairState() {
		return BodyCoreStateRule.getHairState(this);
	}

	/**
	 * 髪の状態 を設定する.
	 * 
	 * @param hairState 髪の状態
	 */
	public void setHairState(HairState hairState) {
		BodyCoreStateRule.setHairState(this, hairState);
	}

	/**
	 * うんうんの溜まり具合 を取得する.
	 * 
	 * @return うんうんの溜まり具合
	 */
	public int getShit() {
		return BodyCoreStateRule.getShit(this);
	}

	/**
	 * うんうんの溜まり具合 を設定する.
	 * 
	 * @param shit うんうんの溜まり具合
	 */
	public void setShit(int shit) {
		BodyCoreStateRule.setShit(this, shit);
	}

	/**
	 * 思い出（悪夢関連） を取得する.
	 * 
	 * @return 思い出（悪夢関連）
	 */
	public int getMemories() {
		return BodyCoreStateRule.getMemories(this);
	}

	/**
	 * 思い出（悪夢関連） を設定する.
	 * 
	 * @param memories 思い出（悪夢関連）
	 */
	public void setMemories(int memories) {
		BodyCoreStateRule.setMemories(this, memories);
	}

	/**
	 * トラウマ を取得する.
	 * 
	 * @return トラウマ
	 */
	public Trauma getTrauma() {
		return BodyCoreStateRule.getTrauma(this);
	}

	/**
	 * トラウマ を設定する.
	 * 
	 * @param trauma トラウマ
	 */
	public void setTrauma(Trauma trauma) {
		BodyCoreStateRule.setTrauma(this, trauma);
	}

	/**
	 * おかざり種別 を取得する.
	 * 
	 * @return おかざり種別
	 */
	public Okazari getOkazari() {
		return okazari;
	}

	/**
	 * おかざり種別 を設定する.
	 * 
	 * @param okazari おかざり種別
	 */
	public void setOkazari(Okazari okazari) {
		this.okazari = okazari;
	}

	/**
	 * 体の前後のどこにお飾りを持っているか(0は両方、1は前方のみ、2は後方のみ) を取得する.
	 * 
	 * @return 体の前後のどこにお飾りを持っているか(0は両方、1は前方のみ、2は後方のみ)
	 */
	public int getOkazariPosition() {
		return okazariPosition;
	}

	/**
	 * 体の前後のどこにお飾りを持っているか(0は両方、1は前方のみ、2は後方のみ) を設定する.
	 * 
	 * @param okazariPosition 体の前後のどこにお飾りを持っているか(0は両方、1は前方のみ、2は後方のみ)
	 */
	public void setOkazariPosition(int okazariPosition) {
		this.okazariPosition = okazariPosition;
	}

	/**
	 * おさげ、羽、尻尾有無 種族として何も持っていないものはtrue を取得する.
	 * 
	 * @return おさげ、羽、尻尾有無 種族として何も持っていないものはtrue
	 */
	public boolean isHasBraid() {
		return BodyStructureRule.isHasBraid(this);
	}

	@JsonIgnore
	public boolean isHasBraidRaw() {
		return hasBraid;
	}

	/**
	 * おさげ、羽、尻尾有無 種族として何も持っていないものはtrue を設定する.
	 * 
	 * @param hasBraid おさげ、羽、尻尾有無 種族として何も持っていないものはtrue
	 */
	public void setHasBraid(boolean hasBraid) {
		this.hasBraid = hasBraid;
	}

	/**
	 * おくるみ有無 true if having pants を取得する.
	 * 
	 * @return おくるみ有無 true if having pants
	 */
	public boolean isHasPants() {
		return BodyStructureRule.isHasPants(this);
	}

	@JsonIgnore
	public boolean isHasPantsRaw() {
		return hasPants;
	}

	/**
	 * おくるみ有無 true if having pants を設定する.
	 * 
	 * @param hasPants おくるみ有無 true if having pants
	 */
	public void setHasPants(boolean hasPants) {
		this.hasPants = hasPants;
	}

	/**
	 * 胎生妊娠有無 having baby or not を取得する.
	 * 
	 * @return 胎生妊娠有無 having baby or not
	 */
	public boolean isHasBaby() {
		return BodyStructureRule.isHasBaby(this);
	}

	@JsonIgnore
	public boolean isHasBabyRaw() {
		return hasBaby;
	}

	/**
	 * 胎生妊娠有無 having baby or not を設定する.
	 * 
	 * @param hasBaby 胎生妊娠有無 having baby or not
	 */
	public void setHasBaby(boolean hasBaby) {
		this.hasBaby = hasBaby;
	}

	/**
	 * 茎妊娠有無 having baby or not を取得する.
	 * 
	 * @return 茎妊娠有無 having baby or not
	 */
	public boolean isHasStalk() {
		return BodyStructureRule.isHasStalk(this);
	}

	@JsonIgnore
	public boolean isHasStalkRaw() {
		return hasStalk;
	}

	/**
	 * 茎妊娠有無 having baby or not を設定する.
	 * 
	 * @param hasStalk 茎妊娠有無 having baby or not
	 */
	public void setHasStalk(boolean hasStalk) {
		this.hasStalk = hasStalk;
	}

	/**
	 * あにゃるふさぎ有無 を取得する.
	 * 
	 * @return あにゃるふさぎ有無
	 */
	public boolean isAnalClose() {
		return BodyStructureRule.isAnalClose(this);
	}

	@JsonIgnore
	public boolean isAnalCloseRaw() {
		return analClose;
	}

	/**
	 * あにゃるふさぎ有無 を設定する.
	 * 
	 * @param analClose あにゃるふさぎ有無
	 */
	public void setAnalClose(boolean analClose) {
		this.analClose = analClose;
	}

	/**
	 * 胎生去勢有無 を取得する.
	 * 
	 * @return 胎生去勢有無
	 */
	public boolean isBodyCastration() {
		return BodyStructureRule.isBodyCastration(this);
	}

	@JsonIgnore
	public boolean isBodyCastrationRaw() {
		return bodyCastration;
	}

	/**
	 * 胎生去勢有無 を設定する.
	 * 
	 * @param bodyCastration 胎生去勢有無
	 */
	public void setBodyCastration(boolean bodyCastration) {
		this.bodyCastration = bodyCastration;
	}

	/**
	 * 茎去勢有無 を取得する.
	 * 
	 * @return 茎去勢有無
	 */
	public boolean isStalkCastration() {
		return BodyStructureRule.isStalkCastration(this);
	}

	@JsonIgnore
	public boolean isStalkCastrationRaw() {
		return stalkCastration;
	}

	/**
	 * 茎去勢有無 を設定する.
	 * 
	 * @param stalkCastration 茎去勢有無
	 */
	public void setStalkCastration(boolean stalkCastration) {
		this.stalkCastration = stalkCastration;
	}

	/**
	 * ぺにぺにの去勢有無 を取得する.
	 * 
	 * @return ぺにぺにの去勢有無
	 */
	public boolean isPenipeniCutted() {
		return BodyFlagRule.isPenipeniCutted(this);
	}

	@JsonIgnore
	public boolean isPenipeniCuttedRaw() {
		return penipeniCutted;
	}

	/**
	 * ぺにぺにの去勢有無 を設定する.
	 * 
	 * @param penipeniCutted ぺにぺにの去勢有無
	 */
	@JsonProperty("penipeniCutted")
	public void setPenipeniCutted(boolean penipeniCutted) {
		this.penipeniCutted = penipeniCutted;
	}

	/**
	 * フェロモンの有無 を取得する.
	 * 
	 * @return フェロモンの有無
	 */
	public boolean isPheromone() {
		return BodyFlagRule.isPheromone(this);
	}

	@JsonIgnore
	public boolean isPheromoneRaw() {
		return pheromone;
	}

	/**
	 * フェロモンの有無 を設定する.
	 * 
	 * @param pheromone フェロモンの有無
	 */
	@JsonProperty("pheromone")
	public void setPheromone(boolean pheromone) {
		this.pheromone = pheromone;
	}

	/**
	 * 胎生ゆのリスト を取得する.
	 * 
	 * @return 胎生ゆのリスト
	 */
	public List<Dna> getBabyTypes() {
		return babyTypes;
	}

	/**
	 * 胎生ゆのリスト を設定する.
	 * 
	 * @param babyTypes 胎生ゆのリスト
	 */
	public void setBabyTypes(List<Dna> babyTypes) {
		this.babyTypes = babyTypes;
	}

	/**
	 * 実ゆのリスト を取得する.
	 * 
	 * @return 実ゆのリスト
	 */
	public List<Dna> getStalkBabyTypes() {
		return stalkBabyTypes;
	}

	/**
	 * 実ゆのリスト を設定する.
	 * 
	 * @param stalkBabyTypes 実ゆのリスト
	 */
	public void setStalkBabyTypes(List<Dna> stalkBabyTypes) {
		this.stalkBabyTypes = stalkBabyTypes;
	}

	/**
	 * 茎のリスト を取得する.
	 * 
	 * @return 茎のリスト
	 */
	public List<Stalk> getStalks() {
		return stalks;
	}

	/**
	 * 茎のリスト を設定する.
	 * 
	 * @param stalks 茎のリスト
	 */
	public void setStalks(List<Stalk> stalks) {
		this.stalks = stalks;
	}

	/**
	 * 自分がぶらさがっている茎 を取得する.
	 * 
	 * @return 自分がぶらさがっている茎
	 */
	public Stalk getBindStalk() {
		return bindStalk;
	}

	/**
	 * 自分がぶらさがっている茎 を設定する.
	 * 
	 * @param bindStalk 自分がぶらさがっている茎
	 */
	public void setBindStalk(Stalk bindStalk) {
		this.bindStalk = bindStalk;
	}

	/**
	 * 死亡フラグdead of alive を取得する.
	 * 
	 * @return 死亡フラグdead of alive
	 */
	public boolean isDead() {
		return BodyConditionRule.isDead(this);
	}

	@JsonIgnore
	public boolean isDeadRaw() {
		return dead;
	}

	/**
	 * 死亡フラグdead of alive を設定する.
	 * 
	 * @param dead 死亡フラグdead of alive
	 */
	public void setDead(boolean dead) {
		this.dead = dead;
	}

	/**
	 * うまれて初めての地面か を取得する.
	 * 
	 * @return うまれて初めての地面か
	 */
	@JsonIgnore
	public boolean isFirstGround() {
		return BodyConditionRule.isFirstGround(this);
	}

	@JsonIgnore
	public boolean isFirstGroundRaw() {
		return firstGround;
	}

	/**
	 * うまれて初めての地面か を設定する.
	 * 
	 * @param firstGround うまれて初めての地面か
	 */
	@JsonProperty("firstGround")
	public void setFirstGround(boolean firstGround) {
		this.firstGround = firstGround;
	}

	/**
	 * うまれて初めての食事か を取得する.
	 * 
	 * @return うまれて初めての食事か
	 */
	public boolean isFirstEatStalk() {
		return BodyBirthRule.isFirstEatStalk(this);
	}

	@JsonIgnore
	public boolean isFirstEatStalkRaw() {
		return firstEatStalk;
	}

	/**
	 * うまれて初めての食事か を設定する.
	 * 
	 * @param firstEatStalk うまれて初めての食事か
	 */
	@JsonProperty("firstEatStalk")
	public void setFirstEatStalk(boolean firstEatStalk) {
		this.firstEatStalk = firstEatStalk;
	}

	/**
	 * 死体が損壊されているか を取得する.
	 * 
	 * @return 死体が損壊されているか
	 */
	public boolean isCrushed() {
		return BodyConditionRule.isCrushed(this);
	}

	@JsonIgnore
	public boolean isCrushedRaw() {
		return crushed;
	}

	/**
	 * 死体が損壊されているか を設定する.
	 * 
	 * @param crushed 死体が損壊されているか
	 */
	public void setCrushed(boolean crushed) {
		this.crushed = crushed;
	}

	/**
	 * 死体が焼損されているか を取得する.
	 * 
	 * @return 死体が焼損されているか
	 */
	public boolean isBurned() {
		return BodyConditionRule.isBurned(this);
	}

	@JsonIgnore
	public boolean isBurnedRaw() {
		return burned;
	}

	/**
	 * 中枢餡の状態（非ゆっくり症フラグ を取得する.
	 * 
	 * @return 中枢餡の状態（非ゆっくり症フラグ
	 */
	public CoreAnkoState getCoreAnkoState() {
		return coreAnkoState;
	}

	/**
	 * 中枢餡の状態（非ゆっくり症フラグ を設定する.
	 * 
	 * @param coreAnkoState 中枢餡の状態（非ゆっくり症フラグ
	 */
	public void setCoreAnkoState(CoreAnkoState coreAnkoState) {
		this.coreAnkoState = coreAnkoState;
	}

	/**
	 * 発情フラグ want to sukkiri or not を設定する.
	 * 
	 * @param exciting 発情フラグ want to sukkiri or not
	 */
	public void setExciting(boolean exciting) {
		this.exciting = exciting;
	}

	/**
	 * 強制発情フラグ want to sukkiri or not を取得する.
	 * 
	 * @return 強制発情フラグ want to sukkiri or not
	 */
	@JsonIgnore
	public boolean isForceExciting() {
		return BodyActivityRule.isForceExciting(this);
	}

	@JsonIgnore
	public boolean isForceExcitingRaw() {
		return forceExciting;
	}

	/**
	 * 強制発情フラグ want to sukkiri or not を設定する.
	 * 
	 * @param forceExciting 強制発情フラグ want to sukkiri or not
	 */
	public void setForceExciting(boolean forceExciting) {
		this.forceExciting = forceExciting;
	}

	/**
	 * ゆっくりしてるかどうか を取得する.
	 * 
	 * @return ゆっくりしてるかどうか
	 */
	public boolean isRelax() {
		return BodyConditionRule.isRelax(this);
	}

	@JsonIgnore
	public boolean isRelaxRaw() {
		return relax;
	}

	/**
	 * ゆっくりしてるかどうか を設定する.
	 * 
	 * @param relax ゆっくりしてるかどうか
	 */
	public void setRelax(boolean relax) {
		this.relax = relax;
	}

	/**
	 * 睡眠中かどうか を設定する.
	 * 
	 * @param sleeping 睡眠中かどうか
	 */
	@JsonProperty
	public void setSleeping(boolean sleeping) {
		this.sleeping = sleeping;
	}

	/**
	 * 悪夢を見るかどうか を取得する.
	 * 
	 * @return 悪夢を見るかどうか
	 */
	public boolean isNightmare() {
		return BodyConditionRule.isNightmare(this);
	}

	@JsonIgnore
	public boolean isNightmareRaw() {
		return nightmare;
	}

	/**
	 * 悪夢を見るかどうか を設定する.
	 * 
	 * @param nightmare 悪夢を見るかどうか
	 */
	public void setNightmare(boolean nightmare) {
		this.nightmare = nightmare;
	}

	/**
	 * 前回起きた時間 を取得する.
	 * 
	 * @return 前回起きた時間
	 */
	public long getWakeUpTime() {
		return wakeUpTime;
	}

	/**
	 * 前回起きた時間 を設定する.
	 * 
	 * @param wakeUpTime 前回起きた時間
	 */
	public void setWakeUpTime(long wakeUpTime) {
		this.wakeUpTime = wakeUpTime;
	}

	/**
	 * 汚れ有無 を設定する.
	 * 
	 * @param dirty 汚れ有無
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * 頑固な汚れ有無 を設定する.
	 * 
	 * @param stubbornlyDirty 頑固な汚れ有無
	 */
	public void setStubbornlyDirty(boolean stubbornlyDirty) {
		this.stubbornlyDirty = stubbornlyDirty;
	}

	/**
	 * 針の有無 を取得する.
	 * 
	 * @return 針の有無
	 */
	@JsonIgnore
	public boolean isNeedled() {
		return BodyActionStateRule.isNeedled(this);
	}

	@JsonIgnore
	public boolean isNeedledRaw() {
		return needled;
	}

	/**
	 * 針の有無 を設定する.
	 * 
	 * @param needled 針の有無
	 */
	public void setNeedled(boolean needled) {
		this.needled = needled;
	}

	/**
	 * レイパー化有無 を取得する.
	 * 
	 * @return レイパー化有無
	 */
	public boolean isRapist() {
		return BodyConditionRule.isRapist(this);
	}

	@JsonIgnore
	public boolean isRapistRaw() {
		return rapist;
	}

	/**
	 * レイパー化有無 を設定する.
	 * 
	 * @param rapist レイパー化有無
	 */
	public void setRapist(boolean rapist) {
		this.rapist = rapist;
	}

	/**
	 * バイゆグラでレイパーになる、すーぱーれいぱー状態 を取得する.
	 * 
	 * @return バイゆグラでレイパーになる、すーぱーれいぱー状態
	 */
	public boolean isSuperRapist() {
		return BodyConditionRule.isSuperRapist(this);
	}

	@JsonIgnore
	public boolean isSuperRapistRaw() {
		return superRapist;
	}

	/**
	 * バイゆグラでレイパーになる、すーぱーれいぱー状態 を設定する.
	 * 
	 * @param superRapist バイゆグラでレイパーになる、すーぱーれいぱー状態
	 */
	public void setSuperRapist(boolean superRapist) {
		this.superRapist = superRapist;
	}

	/**
	 * 濡れ状態 を取得する.
	 * 
	 * @return 濡れ状態
	 */
	public boolean isWet() {
		return BodyConditionRule.isWet(this);
	}

	@JsonIgnore
	public boolean isWetRaw() {
		return wet;
	}

	/**
	 * 濡れ状態 を設定する.
	 * 
	 * @param wet 濡れ状態
	 */
	public void setWet(boolean wet) {
		this.wet = wet;
	}

	/**
	 * 水に溶けた状態 を取得する.
	 * 
	 * @return 水に溶けた状態
	 */
	public boolean isMelt() {
		return BodyConditionRule.isMelt(this);
	}

	@JsonIgnore
	public boolean isMeltRaw() {
		return melt;
	}

	/**
	 * 水に溶けた状態 を設定する.
	 * 
	 * @param melt 水に溶けた状態
	 */
	public void setMelt(boolean melt) {
		this.melt = melt;
	}

	/**
	 * 皮をむいた状態 を取得する.
	 * 
	 * @return 皮をむいた状態
	 */
	public boolean isPealed() {
		return BodyConditionRule.isPealed(this);
	}

	@JsonIgnore
	public boolean isPealedRaw() {
		return pealed;
	}

	/**
	 * 皮をむいた状態 を設定する.
	 * 
	 * @param pealed 皮をむいた状態
	 */
	public void setPealed(boolean pealed) {
		this.pealed = pealed;
	}

	/**
	 * 饅頭にされた状態 を取得する.
	 * 
	 * @return 饅頭にされた状態
	 */
	public boolean isPacked() {
		return BodyConditionRule.isPacked(this);
	}

	@JsonIgnore
	public boolean isPackedRaw() {
		return packed;
	}

	/**
	 * 饅頭にされた状態 を設定する.
	 * 
	 * @param packed 饅頭にされた状態
	 */
	public void setPacked(boolean packed) {
		this.packed = packed;
	}

	/**
	 * アマギられた状態 かどうかを取得する.
	 * 
	 * @return アマギられた状態 かどうか
	 */
	public boolean isBlind() {
		return BodyConditionRule.isBlind(this);
	}

	@JsonIgnore
	public boolean isBlindRaw() {
		return blind;
	}

	/**
	 * アマギられた状態 かどうかを設定する.
	 * 
	 * @param blind アマギられた状態 かどうか
	 */
	public void setBlind(boolean blind) {
		this.blind = blind;
	}

	/**
	 * おかざりがなくなっていることに気がついているか を取得する.
	 * 
	 * @return おかざりがなくなっていることに気がついているか
	 */
	public boolean isNoticeNoOkazari() {
		return BodyFlagRule.isNoticeNoOkazari(this);
	}

	@JsonIgnore
	public boolean isNoticeNoOkazariRaw() {
		return noticeNoOkazari;
	}

	/**
	 * おかざりがなくなっていることに気がついているか を設定する.
	 * 
	 * @param noticeNoOkazari おかざりがなくなっていることに気がついているか
	 */
	@JsonProperty("noticeNoOkazari")
	public void setNoticeNoOkazari(boolean noticeNoOkazari) {
		this.noticeNoOkazari = noticeNoOkazari;
	}

	/**
	 * パニック種別 を取得する.
	 * 
	 * @return パニック種別
	 */
	public PanicType getPanicType() {
		return panicType;
	}

	/**
	 * パニック種別 を設定する.
	 * 
	 * @param panicType パニック種別
	 */
	public void setPanicType(PanicType panicType) {
		this.panicType = panicType;
	}

	/**
	 * 致命傷種別 を取得する.
	 * 
	 * @return 致命傷種別
	 */
	public CriticalDamegeType getCriticalDamege() {
		return criticalDamege;
	}

	/**
	 * 致命傷種別 を設定する.
	 * 
	 * @param criticalDamege 致命傷種別
	 */
	public void setCriticalDamege(CriticalDamegeType criticalDamege) {
		this.criticalDamege = criticalDamege;
	}

	/**
	 * つがい を取得する.
	 * 
	 * @return つがい
	 */
	public int getPartner() {
		return partner;
	}

	/**
	 * つがい を設定する.
	 * 
	 * @param partner つがい
	 */
	public void setPartner(int partner) {
		this.partner = partner;
	}

	/**
	 * 親を取得する.
	 * 
	 * @return 親
	 */
	public int[] getParents() {
		return parents;
	}

	/**
	 * 親を設定する.
	 * 
	 * @param parents 親
	 */
	public void setParents(int[] parents) {
		this.parents = parents;
	}

	/**
	 * 子供のリスト を取得する.
	 * 
	 * @return 子供のリスト
	 */
	public List<Integer> getChildrenList() {
		return childrenList;
	}

	/**
	 * 子供のリスト を設定する.
	 * 
	 * @param childrenList 子供のリスト
	 */
	public void setChildrenList(List<Integer> childrenList) {
		this.childrenList = childrenList;
	}

	/**
	 * 姉のリスト を取得する.
	 * 
	 * @return 姉のリスト
	 */
	public List<Integer> getElderSisterList() {
		return elderSisterList;
	}

	/**
	 * 姉のリスト を設定する.
	 * 
	 * @param elderSisterList 姉のリスト
	 */
	public void setElderSisterList(List<Integer> elderSisterList) {
		this.elderSisterList = elderSisterList;
	}

	/**
	 * 妹のリスト を取得する.
	 * 
	 * @return 妹のリスト
	 */
	public List<Integer> getSisterList() {
		return sisterList;
	}

	/**
	 * 妹のリスト を設定する.
	 * 
	 * @param sisterList 妹のリスト
	 */
	public void setSisterList(List<Integer> sisterList) {
		this.sisterList = sisterList;
	}

	/**
	 * 先祖のリスト を取得する.
	 * 
	 * @return 先祖のリスト
	 */
	public List<Integer> getAncestorList() {
		return ancestorList;
	}

	/**
	 * 先祖のリスト を設定する.
	 * 
	 * @param ancestorList 先祖のリスト
	 */
	public void setAncestorList(List<Integer> ancestorList) {
		this.ancestorList = ancestorList;
	}

	/**
	 * 自分がレイプでできた子か を取得する.
	 * 
	 * @return 自分がレイプでできた子か
	 */
	public boolean isFatherRaper() {
		return BodyConditionRule.isFatherRaper(this);
	}

	@JsonIgnore
	public boolean isFatherRaperRaw() {
		return fatherRaper;
	}

	/**
	 * 自分がレイプでできた子か を設定する.
	 * 
	 * @param fatherRaper 自分がレイプでできた子か
	 */
	public void setFatherRaper(boolean fatherRaper) {
		this.fatherRaper = fatherRaper;
	}

	/**
	 * うんうん抑制 を取得する.
	 * 
	 * @return うんうん抑制
	 */
	public int getShittingDiscipline() {
		return shittingDiscipline;
	}

	/**
	 * うんうん抑制 を設定する.
	 * 
	 * @param shittingDiscipline うんうん抑制
	 */
	public void setShittingDiscipline(int shittingDiscipline) {
		this.shittingDiscipline = shittingDiscipline;
	}

	/**
	 * 興奮抑制 を取得する.
	 * 
	 * @return 興奮抑制
	 */
	public int getExcitingDiscipline() {
		return excitingDiscipline;
	}

	/**
	 * 興奮抑制 を設定する.
	 * 
	 * @param excitingDiscipline 興奮抑制
	 */
	public void setExcitingDiscipline(int excitingDiscipline) {
		this.excitingDiscipline = excitingDiscipline;
	}

	/**
	 * ふりふり抑制 を設定する.
	 * 
	 * @param furifuriDiscipline ふりふり抑制
	 */
	public void setFurifuriDiscipline(int furifuriDiscipline) {
		this.furifuriDiscipline = furifuriDiscipline;
	}

	/**
	 * おしゃべり抑制 を取得する.
	 * 
	 * @return おしゃべり抑制
	 */
	public int getSpeechDiscipline() {
		return speechDiscipline;
	}

	/**
	 * おしゃべり抑制 を設定する.
	 * 
	 * @param speechDiscipline おしゃべり抑制
	 */
	public void setSpeechDiscipline(int speechDiscipline) {
		this.speechDiscipline = speechDiscipline;
	}

	/**
	 * あまあまへの慣れ具合 を取得する.
	 * 
	 * @return あまあまへの慣れ具合
	 */
	public int getAmaamaDiscipline() {
		return amaamaDiscipline;
	}

	/**
	 * あまあまへの慣れ具合 を設定する.
	 * 
	 * @param amaamaDiscipline あまあまへの慣れ具合
	 */
	public void setAmaamaDiscipline(int amaamaDiscipline) {
		this.amaamaDiscipline = amaamaDiscipline;
	}

	/**
	 * 自身の持っているアタッチメント を取得する.
	 * 
	 * @return 自身の持っているアタッチメント
	 */
	public List<Attachment> getAttach() {
		return attach;
	}

	/**
	 * 自身の持っているアタッチメント を設定する.
	 * 
	 * @param attach 自身の持っているアタッチメント
	 */
	public void setAttach(List<Attachment> attach) {
		this.attach = attach;
	}

	/**
	 * なにかのオブジェクト（すぃー、親ゆなど）に載せられている等のリンクが有る際のそのオブジェクト を取得する.
	 * 
	 * @return なにかのオブジェクト（すぃー、親ゆなど）に載せられている等のリンクが有る際のそのオブジェクト
	 */
	public int getParentLinkId() {
		return parentLinkId;
	}

	/**
	 * なにかのオブジェクト（すぃー、親ゆなど）に載せられている等のリンクが有る際のそのオブジェクト を設定する.
	 * 
	 * @param parentLinkId なにかのオブジェクト（すぃー、親ゆなど）に載せられている等のリンクが有る際のそのオブジェクト
	 */
	public void setParentLinkId(int parentLinkId) {
		this.parentLinkId = parentLinkId;
	}

	/**
	 * 移動不可ベルトコンベアの有無 を取得する.
	 * 
	 * @return 移動不可ベルトコンベアの有無
	 */
	public boolean isOnNonMovingConveyor() {
		return BodyFlagRule.isOnNonMovingConveyor(this);
	}

	@JsonIgnore
	public boolean isOnDontMoveBeltconveyorRaw() {
		return nonMovingConveyor;
	}

	/**
	 * 移動不可ベルトコンベアの有無 を設定する.
	 * 
	 * @param nonMovingConveyor 移動不可ベルトコンベアの有無
	 */
	@JsonProperty("nonMovingConveyor")
	public void setOnNonMovingConveyor(boolean nonMovingConveyor) {
		this.nonMovingConveyor = nonMovingConveyor;
	}

	/**
	 * 埋まり状態 を取得する.
	 * 
	 * @return 埋まり状態
	 */
	public BurialState getBurialState() {
		return burialState;
	}

	/**
	 * 埋まり状態 を設定する.
	 * 
	 * @param burialState 埋まり状態
	 */
	public void setBurialState(BurialState burialState) {
		this.burialState = burialState;
	}

	/**
	 * 希少種か を取得する.
	 * 
	 * @return 希少種か
	 */
	public boolean isRareType() {
		return BodyTraitRule.isRareType(this);
	}

	@JsonIgnore
	public boolean isRareTypeRaw() {
		return rareType;
	}

	/**
	 * 希少種か を設定する.
	 * 
	 * @param rareType 希少種か
	 */
	public void setRareType(boolean rareType) {
		BodyTraitRule.setRareType(this, rareType);
	}

	@JsonIgnore
	public void setRareTypeRaw(boolean rareType) {
		this.rareType = rareType;
	}

	/**
	 * 苦いえさが好きか を取得する.
	 * 
	 * @return 苦いえさが好きか
	 */
	public boolean isLikeBitterFood() {
		return BodyTraitRule.isLikeBitterFood(this);
	}

	@JsonIgnore
	public boolean isLikeBitterFoodRaw() {
		return likeBitterFood;
	}

	/**
	 * 苦いえさが好きか を設定する.
	 * 
	 * @param likeBitterFood 苦いえさが好きか
	 */
	public void setLikeBitterFood(boolean likeBitterFood) {
		BodyTraitRule.setLikeBitterFood(this, likeBitterFood);
	}

	@JsonIgnore
	public void setLikeBitterFoodRaw(boolean likeBitterFood) {
		this.likeBitterFood = likeBitterFood;
	}

	/**
	 * 辛いえさが好きか を取得する.
	 * 
	 * @return 辛いえさが好きか
	 */
	public boolean isLikeHotFood() {
		return BodyTraitRule.isLikeHotFood(this);
	}

	@JsonIgnore
	public boolean isLikeHotFoodRaw() {
		return likeHotFood;
	}

	/**
	 * 辛いえさが好きか を設定する.
	 * 
	 * @param likeHotFood 辛いえさが好きか
	 */
	public void setLikeHotFood(boolean likeHotFood) {
		BodyTraitRule.setLikeHotFood(this, likeHotFood);
	}

	@JsonIgnore
	public void setLikeHotFoodRaw(boolean likeHotFood) {
		this.likeHotFood = likeHotFood;
	}

	/**
	 * 水が平気か を取得する.
	 * 
	 * @return 水が平気か
	 */
	public boolean isLikeWater() {
		return BodyTraitRule.isLikeWater(this);
	}

	@JsonIgnore
	public boolean isLikeWaterRaw() {
		return likeWater;
	}

	/**
	 * 水が平気か を設定する.
	 * 
	 * @param likeWater 水が平気か
	 */
	public void setLikeWater(boolean likeWater) {
		BodyTraitRule.setLikeWater(this, likeWater);
	}

	@JsonIgnore
	public void setLikeWaterRaw(boolean likeWater) {
		this.likeWater = likeWater;
	}

	/**
	 * 空を飛ぶか を取得する.
	 * 
	 * @return 空を飛ぶか
	 */
	public boolean isFlyingType() {
		return BodyTraitRule.isFlyingType(this);
	}

	@JsonIgnore
	public boolean isFlyingTypeRaw() {
		return flyingType;
	}

	/**
	 * 空を飛ぶか を設定する.
	 * 
	 * @param flyingType 空を飛ぶか
	 */
	public void setFlyingType(boolean flyingType) {
		BodyTraitRule.setFlyingType(this, flyingType);
	}

	@JsonIgnore
	public void setFlyingTypeRaw(boolean flyingType) {
		this.flyingType = flyingType;
	}

	/**
	 * 種族としてお下げ、羽、尻尾を持つか を取得する.
	 * 
	 * @return 種族としてお下げ、羽、尻尾を持つか
	 */
	public boolean isBraidType() {
		return BodyTraitRule.isBraidType(this);
	}

	@JsonIgnore
	public boolean isBraidTypeRaw() {
		return braidType;
	}

	/**
	 * 種族としてお下げ、羽、尻尾を持つか を設定する.
	 * 
	 * @param braidType 種族としてお下げ、羽、尻尾を持つか
	 */
	public void setBraidType(boolean braidType) {
		BodyTraitRule.setBraidType(this, braidType);
	}

	@JsonIgnore
	public void setBraidTypeRaw(boolean braidType) {
		this.braidType = braidType;
	}

	/**
	 * 捕食種タイプ を取得する.
	 * 
	 * @return 捕食種タイプ
	 */
	public PredatorType getPredatorType() {
		return predatorType;
	}

	/**
	 * 捕食種タイプ を設定する.
	 * 
	 * @param predatorType 捕食種タイプ
	 */
	public void setPredatorType(PredatorType predatorType) {
		this.predatorType = predatorType;
	}

	/**
	 * 動けないかどうか を取得する.
	 * 
	 * @return 動けないかどうか
	 */
	public boolean isLockmove() {
		return BodyMovementGoalRule.isLockmove(this);
	}

	@JsonIgnore
	public boolean isLockmoveRaw() {
		return lockmove;
	}

	/**
	 * 動けないかどうか を設定する.
	 * 
	 * @param lockmove 動けないかどうか
	 */
	public void setLockmove(boolean lockmove) {
		this.lockmove = lockmove;
	}

	/**
	 * ひっぱり、押しつぶし可能か を取得する.
	 * 
	 * @return ひっぱり、押しつぶし可能か
	 */
	public boolean isPullAndPush() {
		return BodyMovementGoalRule.isPullAndPush(this);
	}

	@JsonIgnore
	public boolean isPullAndPushRaw() {
		return pullAndPush;
	}

	/**
	 * ひっぱり、押しつぶし可能か を設定する.
	 * 
	 * @param pullAndPush ひっぱり、押しつぶし可能か
	 */
	public void setPullAndPush(boolean pullAndPush) {
		this.pullAndPush = pullAndPush;
	}

	/**
	 * 動けない期間（押さえられてる等で） を取得する.
	 * 
	 * @return 動けない期間（押さえられてる等で）
	 */
	public int getLockmovePeriod() {
		return lockmovePeriod;
	}

	/**
	 * 動けない期間（押さえられてる等で） を設定する.
	 * 
	 * @param lockmovePeriod 動けない期間（押さえられてる等で）
	 */
	public void setLockmovePeriod(int lockmovePeriod) {
		this.lockmovePeriod = lockmovePeriod;
	}

	/**
	 * 外圧 を取得する.
	 * 
	 * @return 外圧
	 */
	public int getExtForce() {
		return extForce;
	}

	/**
	 * 外圧 を設定する.
	 * 
	 * @param extForce 外圧
	 */
	public void setExtForce(int extForce) {
		this.extForce = extForce;
	}

	/**
	 * まばたき、同じ表情の時にカウント を取得する.
	 * 
	 * @return まばたき、同じ表情の時にカウント
	 */
	public int getBlinkCount() {
		return blinkCount;
	}

	/**
	 * まばたき、同じ表情の時にカウント を設定する.
	 * 
	 * @param blinkCount まばたき、同じ表情の時にカウント
	 */
	public void setBlinkCount(int blinkCount) {
		this.blinkCount = blinkCount;
	}

	/**
	 * まばたき、表情の値を代入 を取得する.
	 * 
	 * @return まばたき、表情の値を代入
	 */
	public int getBlinkType() {
		return blinkType;
	}

	/**
	 * まばたき、表情の値を代入 を設定する.
	 * 
	 * @param blinkType まばたき、表情の値を代入
	 */
	public void setBlinkType(int blinkType) {
		this.blinkType = blinkType;
	}

	/**
	 * プレイヤーにすりすりされているか を取得する.
	 * 
	 * @return プレイヤーにすりすりされているか
	 */
	public boolean isSurisuriFromPlayer() {
		return BodySurisuriRule.isSurisuriFromPlayer(this);
	}

	@JsonIgnore
	public boolean isSurisuriFromPlayerRaw() {
		return surisuriFromPlayer;
	}

	/**
	 * プレイヤーにすりすりされているか を設定する.
	 * 
	 * @param surisuriFromPlayer プレイヤーにすりすりされているか
	 */
	@JsonProperty("surisuriFromPlayer")
	public void setSurisuriFromPlayer(boolean surisuriFromPlayer) {
		this.surisuriFromPlayer = surisuriFromPlayer;
	}

	/**
	 * ぷるぷるアニメーション位相 を取得する.
	 * 
	 * @return ぷるぷるアニメーション位相
	 */
	@JsonIgnore
	public boolean isShakePhase() {
		return BodyExpressionRule.isShakePhase(this);
	}

	@JsonIgnore
	public boolean isShakePhaseRaw() {
		return shakePhase;
	}

	/**
	 * ぷるぷるアニメーション位相 を設定する.
	 *
	 * @param shakePhase ぷるぷるアニメーション位相
	 */
	public void setShakePhase(boolean shakePhase) {
		this.shakePhase = shakePhase;
	}

	/**
	 * 粘着板で背中を固定されているかを取得する.
	 * 
	 * @return 粘着板で背中を固定されているか
	 */
	public boolean isFixBack() {
		return BodyControlRule.isFixBack(this);
	}

	@JsonIgnore
	public boolean isFixBackRaw() {
		return fixBack;
	}

	/**
	 * 粘着板で背中を固定されているかを設定する.
	 * 
	 * @param 粘着板で背中を固定されているか
	 */
	public void setFixBack(boolean fixBack) {
		this.fixBack = fixBack;
	}

	/**
	 * ダメージを受けていない期間 を取得する.
	 * 
	 * @return ダメージを受けていない期間
	 */
	public int getNoDamagePeriod() {
		return noDamagePeriod;
	}

	/**
	 * ダメージを受けていない期間 を設定する.
	 * 
	 * @param noDamagePeriod ダメージを受けていない期間
	 */
	public void setNoDamagePeriod(int noDamagePeriod) {
		this.noDamagePeriod = noDamagePeriod;
	}

	/**
	 * 飢餓状態になっていない期間を取得する.
	 * 
	 * @return 飢餓状態になっていない期間
	 */
	public int getNoHungryPeriod() {
		return noHungryPeriod;
	}

	/**
	 * 飢餓状態になっていない期間 を設定する.
	 * 
	 * @param noHungryPeriod 飢餓状態になっていない期間
	 */
	public void setNoHungryPeriod(int noHungryPeriod) {
		this.noHungryPeriod = noHungryPeriod;
	}

	/**
	 * スーパーむーしゃむーしゃタイムのおかげで飢餓状態にならない期間 を取得する.
	 * 
	 * @return スーパーむーしゃむーしゃタイムのおかげで飢餓状態にならない期間
	 */
	public int getSuperEatingNoHungryPeriod() {
		return superEatingNoHungryPeriod;
	}

	/**
	 * スーパーむーしゃむーしゃタイムのおかげで飢餓状態にならない期間 を設定する.
	 * 
	 * @param superEatingNoHungryPeriod スーパーむーしゃむーしゃタイムのおかげで飢餓状態にならない期間
	 */
	public void setSuperEatingNoHungryPeriod(int superEatingNoHungryPeriod) {
		this.superEatingNoHungryPeriod = superEatingNoHungryPeriod;
	}

	/**
	 * 妊娠期間 を取得する.
	 * 
	 * @return 妊娠期間
	 */
	public int getPregnantPeriod() {
		return pregnantPeriod;
	}

	/**
	 * 妊娠期間 を設定する.
	 * 
	 * @param pregnantPeriod 妊娠期間
	 */
	public void setPregnantPeriod(int pregnantPeriod) {
		this.pregnantPeriod = pregnantPeriod;
	}

	/**
	 * 発情期間 を取得する.
	 * 
	 * @return 発情期間
	 */
	public int getExcitingPeriod() {
		return excitingPeriod;
	}

	/**
	 * 発情期間 を設定する.
	 * 
	 * @param excitingPeriod 発情期間
	 */
	public void setExcitingPeriod(int excitingPeriod) {
		this.excitingPeriod = excitingPeriod;
	}

	/**
	 * 睡眠期間 を取得する.
	 * 
	 * @return 睡眠期間
	 */
	public int getSleepingPeriod() {
		return sleepingPeriod;
	}

	/**
	 * 睡眠期間 を設定する.
	 * 
	 * @param sleepingPeriod 睡眠期間
	 */
	public void setSleepingPeriod(int sleepingPeriod) {
		this.sleepingPeriod = sleepingPeriod;
	}

	/**
	 * 汚れている期間 を取得する.
	 * 
	 * @return 汚れている期間
	 */
	public int getDirtyPeriod() {
		return dirtyPeriod;
	}

	/**
	 * 汚れている期間 を設定する.
	 * 
	 * @param dirtyPeriod 汚れている期間
	 */
	public void setDirtyPeriod(int dirtyPeriod) {
		this.dirtyPeriod = dirtyPeriod;
	}

	/**
	 * 汚れて泣き叫ぶ期間 を取得する.
	 * 
	 * @return 汚れて泣き叫ぶ期間
	 */
	public int getDirtyScreamPeriod() {
		return dirtyScreamPeriod;
	}

	/**
	 * 汚れて泣き叫ぶ期間 を設定する.
	 * 
	 * @param dirtyScreamPeriod 汚れて泣き叫ぶ期間
	 */
	public void setDirtyScreamPeriod(int dirtyScreamPeriod) {
		this.dirtyScreamPeriod = dirtyScreamPeriod;
	}

	/**
	 * ゆかびに侵されている期間 を取得する.
	 * 
	 * @return ゆかびに侵されている期間
	 */
	@JsonProperty
	public int getSickPeriod() {
		return sickPeriod;
	}

	/**
	 * ゆかびに侵されている期間 を設定する.
	 * 
	 * @param sickPeriod ゆかびに侵されている期間
	 */
	@JsonProperty
	public void setSickPeriod(int sickPeriod) {
		this.sickPeriod = sickPeriod;
	}

	/**
	 * 怒っている期間 を取得する.
	 * 
	 * @return 怒っている期間
	 */
	public int getAngryPeriod() {
		return angryPeriod;
	}

	/**
	 * 怒っている期間 を設定する.
	 * 
	 * @param angryPeriod 怒っている期間
	 */
	public void setAngryPeriod(int angryPeriod) {
		this.angryPeriod = angryPeriod;
	}

	/**
	 * 怖がっている期間 を取得する.
	 * 
	 * @return 怖がっている期間
	 */
	public int getScarePeriod() {
		return scarePeriod;
	}

	/**
	 * 怖がっている期間 を設定する.
	 * 
	 * @param scarePeriod 怖がっている期間
	 */
	public void setScarePeriod(int scarePeriod) {
		this.scarePeriod = scarePeriod;
	}

	/**
	 * 悲しんでいる期間 を取得する.
	 * 
	 * @return 悲しんでいる期間
	 */
	public int getSadPeriod() {
		return sadPeriod;
	}

	/**
	 * 悲しんでいる期間 を設定する.
	 * 
	 * @param sadPeriod 悲しんでいる期間
	 */
	public void setSadPeriod(int sadPeriod) {
		this.sadPeriod = sadPeriod;
	}

	/**
	 * 濡れている期間
	 * 
	 * @return 濡れている期間 を取得する.
	 */
	public int getWetPeriod() {
		return wetPeriod;
	}

	/**
	 * 濡れている期間 を設定する.
	 * 
	 * @param wetPeriod 濡れている期間
	 */
	public void setWetPeriod(int wetPeriod) {
		this.wetPeriod = wetPeriod;
	}

	/**
	 * パニック状態の期間 を取得する.
	 * 
	 * @return パニック状態の期間
	 */
	public int getPanicPeriod() {
		return panicPeriod;
	}

	/**
	 * パニック状態の期間 を設定する.
	 * 
	 * @param panicPeriod パニック状態の期間
	 */
	public void setPanicPeriod(int panicPeriod) {
		this.panicPeriod = panicPeriod;
	}

	/**
	 * 足焼きされている期間 を取得する.
	 * 
	 * @return 足焼きされている期間
	 */
	public int getFootBakePeriod() {
		return footBakePeriod;
	}

	/**
	 * 足焼きされている期間 を設定する.
	 * 
	 * @param footBakePeriod 足焼きされている期間
	 */
	public void setFootBakePeriod(int footBakePeriod) {
		this.footBakePeriod = footBakePeriod;
	}

	/**
	 * 背中を焼かれている期間 を取得する。
	 * 
	 * @return 背中を焼かれている期間
	 */
	public int getBodyBakePeriod() {
		return bodyBakePeriod;
	}

	/**
	 * 背中を焼かれている期間を取得する.
	 * 
	 * @param bodyBakePeriod 背中を焼かれている期間
	 */
	public void setBodyBakePeriod(int bodyBakePeriod) {
		this.bodyBakePeriod = bodyBakePeriod;
	}

	/**
	 * 非ゆっくり症にかかっている期間 を取得する.
	 * 
	 * @return 非ゆっくり症にかかっている期間
	 */
	public int getNonYukkuriDiseasePeriod() {
		return nonYukkuriDiseasePeriod;
	}

	/**
	 * 非ゆっくり症にかかっている期間 を設定する.
	 * 
	 * @param nonYukkuriDiseasePeriod 非ゆっくり症にかかっている期間
	 */
	public void setNonYukkuriDiseasePeriod(int nonYukkuriDiseasePeriod) {
		this.nonYukkuriDiseasePeriod = nonYukkuriDiseasePeriod;
	}

	/**
	 * 死んでからの期間 を取得する.
	 * 
	 * @return 死んでからの期間
	 */
	public int getDeadPeriod() {
		return deadPeriod;
	}

	/**
	 * 死んでからの期間 を設定する.
	 * 
	 * @param deadPeriod 死んでからの期間
	 */
	public void setDeadPeriod(int deadPeriod) {
		this.deadPeriod = deadPeriod;
	}

	/**
	 * 最後にプレイヤーにすりすりしてもらった時間 を取得する.
	 * 
	 * @return 最後にプレイヤーにすりすりしてもらった時間
	 */
	public long getLastSurisuriTime() {
		return lastSurisuriTime;
	}

	/**
	 * 最後にプレイヤーにすりすりしてもらった時間 を設定する.
	 * 
	 * @param lastSurisuriTime 最後にプレイヤーにすりすりしてもらった時間
	 */
	public void setLastSurisuriTime(long lastSurisuriTime) {
		this.lastSurisuriTime = lastSurisuriTime;
	}

	/**
	 * 最後にプレイヤーがアクションを行った時間 を取得する.
	 * 
	 * @return 最後にプレイヤーがアクションを行った時間
	 */
	public long getLastActionTime() {
		return lastActionTime;
	}

	/**
	 * 最後にプレイヤーがアクションを行った時間 を設定する.
	 * 
	 * @param lastActionTime 最後にプレイヤーがアクションを行った時間
	 */
	public void setLastActionTime(long lastActionTime) {
		this.lastActionTime = lastActionTime;
	}

	/**
	 * 出産期間のブースト（この分だけ早まる） を取得する.
	 * 
	 * @return 出産期間のブースト（この分だけ早まる）
	 */
	public int getPregnancyPeriodBoost() {
		return pregnancyPeriodBoost;
	}

	/**
	 * 出産期間のブースト（この分だけ早まる） を設定する.
	 * 
	 * @param pregnancyPeriodBoost 出産期間のブースト（この分だけ早まる）
	 */
	public void setPregnancyPeriodBoost(int pregnancyPeriodBoost) {
		this.pregnancyPeriodBoost = pregnancyPeriodBoost;
	}

	/**
	 * 発情期間のブースト（この分だけ早まる） を取得する.
	 * 
	 * @return 発情期間のブースト（この分だけ早まる）
	 */
	public int getExcitementPeriodBoost() {
		return excitementPeriodBoost;
	}

	/**
	 * 発情期間のブースト（この分だけ早まる） を設定する.
	 * 
	 * @param excitementPeriodBoost 発情期間のブースト（この分だけ早まる）
	 */
	public void setExcitementPeriodBoost(int excitementPeriodBoost) {
		this.excitementPeriodBoost = excitementPeriodBoost;
	}

	/**
	 * うんうんブースト を取得する.
	 * 
	 * @return うんうんブースト
	 */
	public int getExcretionBoost() {
		return excretionBoost;
	}

	/**
	 * うんうんブースト を設定する.
	 * 
	 * @param excretionBoost うんうんブースト
	 */
	public void setExcretionBoost(int excretionBoost) {
		this.excretionBoost = excretionBoost;
	}

	/**
	 * 移動対象（移動先） を取得する.
	 * 
	 * @return 移動対象（移動先）
	 */
	public int getMoveTargetId() {
		return moveTargetId;
	}

	/**
	 * 移動対象（移動先） を設定する.
	 * 
	 * @param moveTargetId 移動対象（移動先）
	 */
	public void setMoveTargetId(int moveTargetId) {
		this.moveTargetId = moveTargetId;
	}

	/**
	 * 移動対象のX座標オフセット を取得する.
	 * 
	 * @return 移動対象のX座標オフセット
	 */
	public int getTargetOffsetX() {
		return targetOffsetX;
	}

	/**
	 * 移動対象のX座標オフセット を設定する.
	 * 
	 * @param targetOffsetX 移動対象のX座標オフセット
	 */
	public void setTargetOffsetX(int targetOffsetX) {
		this.targetOffsetX = targetOffsetX;
	}

	/**
	 * 移動対象のY座標オフセット を取得する.
	 * 
	 * @return 移動対象のY座標オフセット
	 */
	public int getTargetOffsetY() {
		return targetOffsetY;
	}

	/**
	 * 移動対象のY座標オフセット を設定する.
	 * 
	 * @param targetOffsetY 移動対象のY座標オフセット
	 */
	public void setTargetOffsetY(int targetOffsetY) {
		this.targetOffsetY = targetOffsetY;
	}

	/**
	 * 対象を呼び止めるほど強い動機を持っているかどうか を取得する.
	 * 
	 * @return 対象を呼び止めるほど強い動機を持っているかどうか
	 */
	public boolean isTargetBind() {
		return BodyMovementGoalRule.isTargetBind(this);
	}

	@JsonIgnore
	public boolean isTargetBindRaw() {
		return targetBind;
	}

	/**
	 * 対象を呼び止めるほど強い動機を持っているかどうか を設定する.
	 * 
	 * @param targetBind 対象を呼び止めるほど強い動機を持っているかどうか
	 */
	public void setTargetBind(boolean targetBind) {
		this.targetBind = targetBind;
	}

	/**
	 * 移動目的がフードかどうかを取得する.
	 * 
	 * @return 移動目的がフードかどうか
	 */
	public boolean isToFood() {
		return BodyMovementGoalRule.isToFood(this);
	}

	/**
	 * 移動目的がフードかどうかを設定する.
	 * 
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
	 * 
	 * @return 移動目的がすっきりかどうか
	 */
	public boolean isToSukkiri() {
		return BodyMovementGoalRule.isToSukkiri(this);
	}

	/**
	 * 移動目的がすっきりかどうかを設定する.
	 * 
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
	 * 
	 * @return 移動目的がうんうんかどうか
	 */
	public boolean isToShit() {
		return BodyMovementGoalRule.isToShit(this);
	}

	/**
	 * 移動目的がうんうんかどうかを設定する.
	 * 
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
	 * 
	 * @return 移動目的がベッドかどうか
	 */
	public boolean isToBed() {
		return BodyMovementGoalRule.isToBed(this);
	}

	/**
	 * 移動目的がベッドかどうかを設定する.
	 * 
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
	 * 
	 * @return 移動目的が他のゆっくりかどうか
	 */
	public boolean isToBody() {
		return BodyMovementGoalRule.isToBody(this);
	}

	/**
	 * 移動目的が他のゆっくりかどうかを設定する.
	 * 
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
	 * 
	 * @return 移動目的がおかざりを盗むためかどうか
	 */
	public boolean isToSteal() {
		return BodyMovementGoalRule.isToSteal(this);
	}

	/**
	 * 移動目的がおかざりを盗むためかどうかを設定する.
	 * 
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
	 * 
	 * @return 移動目的がアイテムを持つことかどうか
	 */
	public boolean isToTakeout() {
		return BodyMovementGoalRule.isToTakeout(this);
	}

	/**
	 * 移動目的がアイテムを持つことかどうかを設定する.
	 * 
	 * @param flag 移動目的がアイテムを持つことかどうか
	 */
	public void setToTakeout(boolean flag) {
		if (flag) {
			purposeOfMoving = PurposeOfMoving.TAKEOUT;
		} else if (purposeOfMoving == PurposeOfMoving.TAKEOUT) {
			purposeOfMoving = PurposeOfMoving.NONE;
		}
	}

	/** 移動目標のみキャンセル */
	public final void clearTargets() {
		purposeOfMoving = PurposeOfMoving.NONE;
		stopStaying();
	}

	/**
	 * とどまるのをやめる
	 * 
	 * @see #stay()
	 * @see #stay(int)
	 * @see ##stayPurupuru()
	 * @see #stayPurupuru(int)
	 * @see #isStaying()
	 * @see #stopStaying()
	 */
	public final void stopStaying() {
		stayTicks = 0;
	}

	/**
	 * アイテムを出し入れする動作フラグ を取得する.
	 * 
	 * @return アイテムを出し入れする動作フラグ
	 */
	public boolean isInOutTakeoutItem() {
		return BodyControlRule.isInOutTakeoutItem(this);
	}

	@JsonIgnore
	public boolean isInOutTakeoutItemRaw() {
		return inOutTakeoutItem;
	}

	/**
	 * アイテムを出し入れする動作フラグ を設定する.
	 * 
	 * @param inOutTakeoutItem アイテムを出し入れする動作フラグ
	 */
	public void setInOutTakeoutItem(boolean inOutTakeoutItem) {
		this.inOutTakeoutItem = inOutTakeoutItem;
	}

	/**
	 * 待機アクション中かどうかを取得する.
	 * 
	 * @return 待機アクション中かどうか
	 */
	public boolean isStaying() {
		return BodyControlRule.isStaying(this);
	}

	@JsonIgnore
	public boolean isStayingRaw() {
		return staying;
	}

	/**
	 * 待機アクション中かどうかを設定する.
	 * 
	 * @param staying 待機アクション中かどうか
	 */
	public void setStaying(boolean staying) {
		this.staying = staying;
	}

	/**
	 * うんうんアクション中 かどうかを設定する.
	 * 
	 * @param shitting うんうんアクション中 かどうか
	 */
	public void setShitting(boolean shitting) {
		this.shitting = shitting;
	}

	/**
	 * 誕生済みか否か を設定する.
	 * 
	 * @param birth 誕生済みか否か
	 */
	public void setBirth(boolean birth) {
		this.birth = birth;
	}

	/**
	 * 怒っているか否か を設定する.
	 * 
	 * @param angry 怒っているか否か
	 */
	public void setAngry(boolean angry) {
		this.angry = angry;
	}

	/**
	 * ふりふりアクション中 かどうかを設定する.
	 * 
	 * @param furifuri ふりふりアクション中 かどうか
	 */
	public void setFurifuri(boolean furifuri) {
		this.furifuri = furifuri;
	}

	/**
	 * 攻撃アクション中 かどうかを設定する.
	 * 
	 * @param strike 攻撃アクション中 かどうか
	 */
	public void setStrike(boolean strike) {
		this.strike = strike;
	}

	/**
	 * 何かを食べ中 かどうかを設定する.
	 * 
	 * @param eating 何かを食べ中 かどうか
	 */
	public void setEating(boolean eating) {
		this.eating = eating;
	}

	/**
	 * ぺろぺろ中 かどうかを設定する.
	 * 
	 * @param peropero ぺろぺろ中 かどうか
	 */
	public void setPeropero(boolean peropero) {
		this.peropero = peropero;
	}

	/**
	 * すっきり中 かどうかを設定する.
	 * 
	 * @param sukkiri すっきり中 かどうか
	 */
	public void setSukkiri(boolean sukkiri) {
		this.sukkiri = sukkiri;
	}

	/**
	 * 怖がり中 かどうかを設定する.
	 * 
	 * @param scare 怖がり中 かどうか
	 */
	public void setScare(boolean scare) {
		this.scare = scare;
	}

	/**
	 * うんうんを食べ中 かどうかを設定する.
	 * 
	 * @param eatingShit うんうんを食べ中 かどうか
	 */
	public void setEatingShit(boolean eatingShit) {
		this.eatingShit = eatingShit;
	}

	/**
	 * 沈黙フラグ を取得する.
	 * 
	 * @return 沈黙フラグ
	 */
	public boolean isSilent() {
		return BodyExpressionRule.isSilent(this);
	}

	@JsonIgnore
	public boolean isSilentRaw() {
		return silent;
	}

	/**
	 * 沈黙フラグ を設定する.
	 * 
	 * @param silent 沈黙フラグ
	 */
	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	/**
	 * 口ふさがれ中 かどうかを取得する.
	 * 
	 * @return 口ふさがれ中 かどうか
	 */
	public boolean isShutmouth() {
		return BodyExpressionRule.isShutmouth(this);
	}

	@JsonIgnore
	public boolean isShutmouthRaw() {
		return shutmouth;
	}

	/**
	 * 口ふさがれ中 かどうかを設定する.
	 * 
	 * @param shutmouth 口ふさがれ中 かどうか
	 */
	public void setShutmouth(boolean shutmouth) {
		this.shutmouth = shutmouth;
	}

	/**
	 * キリッ！中 かどうかを設定する.
	 * 
	 * @param beVain キリッ！中 かどうか
	 */
	public void setBeVain(boolean beVain) {
		this.beVain = beVain;
	}

	/**
	 * ぴこぴこ中 かどうかを取得する.
	 * 
	 * @return ぴこぴこ中 かどうか
	 */
	public boolean isPikopiko() {
		return BodyExpressionRule.isPikopiko(this);
	}

	@JsonIgnore
	public boolean isPikopikoRaw() {
		return pikopiko;
	}

	/**
	 * ぴこぴこ中 かどうかを設定する.
	 * 
	 * @param pikopiko ぴこぴこ中 かどうか
	 */
	public void setPikopiko(boolean pikopiko) {
		this.pikopiko = pikopiko;
	}

	/**
	 * ぷるぷる中 かどうかを取得する.
	 * 
	 * @return ぷるぷる中 かどうか
	 */
	public boolean isPurupuru() {
		return BodyExpressionRule.isPurupuru(this);
	}

	@JsonIgnore
	public boolean isPurupuruRaw() {
		return purupuru;
	}

	/**
	 * ぷるぷる中 かどうかを設定する.
	 * 
	 * @param purupuru ぷるぷる中 かどうか
	 */
	public void setPurupuru(boolean purupuru) {
		this.purupuru = purupuru;
	}

	/**
	 * 親を呼んで泣き叫び中 かどうかを設定する.
	 * 
	 * @param callingParents 親を呼んで泣き叫び中 かどうか
	 */
	public void setCallingParents(boolean callingParents) {
		this.callingParents = callingParents;
	}

	/**
	 * ゆんやぁ中 かどうかを設定する.
	 * 
	 * @param yunnyaa ゆんやぁ中 かどうか
	 */
	public void setYunnyaa(boolean yunnyaa) {
		this.yunnyaa = yunnyaa;
	}

	/**
	 * 何で遊んでいるか を取得する.
	 * 
	 * @return 何で遊んでいるか
	 */
	public PlayStyle getPlaying() {
		return playing;
	}

	/**
	 * 何で遊んでいるか を設定する.
	 * 
	 * @param playing 何で遊んでいるか
	 */
	public void setPlaying(PlayStyle playing) {
		this.playing = playing;
	}

	/**
	 * 遊び時間上限 を取得する.
	 * 
	 * @return 遊び時間上限
	 */
	public int getPlayingLimit() {
		return playingLimit;
	}

	/**
	 * 遊び時間上限 を設定する.
	 * 
	 * @param playingLimit 遊び時間上限
	 */
	public void setPlayingLimit(int playingLimit) {
		this.playingLimit = playingLimit;
	}

	/**
	 * メッセージのバッファ を取得する.
	 * 
	 * @return メッセージのバッファ
	 */
	public String getMessageBuffer() {
		return messageBuffer;
	}

	/**
	 * メッセージのバッファ を設定する.
	 * 
	 * @param messageBuffer メッセージのバッファ
	 */
	public void setMessageBuffer(String messageBuffer) {
		this.messageBuffer = messageBuffer;
	}

	/**
	 * いくつメッセージが溜まってるか を取得する.
	 * 
	 * @return いくつメッセージが溜まってるか
	 */
	public int getMessageTicks() {
		return messageTicks;
	}

	/**
	 * いくつメッセージが溜まってるか を設定する.
	 * 
	 * @param messageTicks いくつメッセージが溜まってるか
	 */
	public void setMessageTicks(int messageTicks) {
		this.messageTicks = messageTicks;
	}

	/**
	 * その場に留まってる回数 を取得する.
	 * 
	 * @return その場に留まってる回数
	 */
	public int getStayTicks() {
		return stayTicks;
	}

	/**
	 * その場に留まってる回数 を設定する.
	 * 
	 * @param stayTicks その場に留まってる回数
	 */
	public void setStayTicks(int stayTicks) {
		this.stayTicks = stayTicks;
	}

	/**
	 * とどまる限界 を取得する.
	 * 
	 * @return とどまる限界
	 */
	public int getStayTime() {
		return stayTime;
	}

	/**
	 * とどまる限界 を設定する.
	 * 
	 * @param stayTime とどまる限界
	 */
	public void setStayTime(int stayTime) {
		this.stayTime = stayTime;
	}

	/**
	 * 落下ダメージ を取得する.
	 * 
	 * @return 落下ダメージ
	 */
	public int getFalldownDamage() {
		return falldownDamage;
	}

	/**
	 * 落下ダメージ を設定する.
	 * 
	 * @param falldownDamage 落下ダメージ
	 */
	public void setFalldownDamage(int falldownDamage) {
		this.falldownDamage = falldownDamage;
	}

	/**
	 * あんこ量 を取得する.
	 * 
	 * @return あんこ量
	 */
	public int getAnkoAmount() {
		return ankoAmount;
	}

	/**
	 * あんこ量 を設定する.
	 * 
	 * @param ankoAmount あんこ量
	 */
	public void setAnkoAmount(int ankoAmount) {
		this.ankoAmount = ankoAmount;
	}

	/**
	 * 壁に引っかかった回数 を取得する.
	 * 
	 * @return 壁に引っかかった回数
	 */
	public int getBlockedTicks() {
		return blockedTicks;
	}

	/**
	 * 壁に引っかかった回数 を設定する.
	 * 
	 * @param blockedTicks 壁に引っかかった回数
	 */
	public void setBlockedTicks(int blockedTicks) {
		this.blockedTicks = blockedTicks;
	}

	/**
	 * 死なない期間 を取得する.
	 * 
	 * @return 死なない期間
	 */
	public int getCantDiePeriod() {
		return cantDiePeriod;
	}

	/**
	 * 死なない期間 を設定する.
	 * 
	 * @param cantDiePeriod 死なない期間
	 */
	public void setCantDiePeriod(int cantDiePeriod) {
		this.cantDiePeriod = cantDiePeriod;
	}

	/**
	 * 実ゆかどうか を取得する.
	 * 
	 * @return 実ゆかどうか
	 */
	public boolean isUnBirth() {
		return BodyBirthRule.isUnBirth(this);
	}

	@JsonIgnore
	public boolean isUnBirthRaw() {
		return unBirth;
	}

	/**
	 * 実ゆかどうか を設定する.
	 * 
	 * @param unBirth 実ゆかどうか
	 */
	public void setUnBirth(boolean unBirth) {
		this.unBirth = unBirth;
	}

	/**
	 * 喋れる状態かどうか を取得する.
	 * 
	 * @return 喋れる状態かどうか
	 */
	public boolean isCanTalk() {
		return BodySpeechRule.isCanTalk(this);
	}

	@JsonIgnore
	public boolean isCanTalkRaw() {
		return canTalk;
	}

	/**
	 * 喋れる状態かどうか を設定する.
	 * 
	 * @param canTalk 喋れる状態かどうか
	 */
	public void setCanTalk(boolean canTalk) {
		this.canTalk = canTalk;
	}

	/**
	 * メッセージラインの色 を取得する.
	 * 
	 * @return メッセージラインの色
	 */
	public Color4y getMessageLineColor() {
		return messageLineColor;
	}

	/**
	 * メッセージラインの色 を設定する.
	 * 
	 * @param messageLineColor メッセージラインの色
	 */
	public void setOrigMessageLineColor(Color messageLineColor) {
		this.messageLineColor = new Color4y(messageLineColor.getRed(), messageLineColor.getGreen(),
				messageLineColor.getBlue(), messageLineColor.getAlpha());
	}

	/**
	 * メッセージラインの色 を設定する.
	 * 
	 * @param messageLineColor メッセージラインの色
	 */
	public void setMessageLineColor(Color4y messageLineColor) {
		this.messageLineColor = messageLineColor;
	}

	/**
	 * メッセージボックスの色 を取得する.
	 * 
	 * @return メッセージボックスの色
	 */
	public Color4y getMessageBoxColor() {
		return messageBoxColor;
	}

	/**
	 * メッセージボックスの色 を設定する.
	 * 
	 * @param messageBoxColor メッセージボックスの色
	 */
	public void setMessageBoxColor(Color4y messageBoxColor) {
		this.messageBoxColor = messageBoxColor;
	}

	/**
	 * メッセージボックスの色 を設定する.
	 * 
	 * @param messageBoxColor メッセージボックスの色
	 */
	public void setOrigMessageBoxColor(Color messageBoxColor) {
		this.messageBoxColor = new Color4y(messageBoxColor.getRed(), messageBoxColor.getGreen(),
				messageBoxColor.getBlue(), messageBoxColor.getAlpha());
	}

	/**
	 * メッセージテキストの色 を取得する.
	 * 
	 * @return メッセージテキストの色
	 */
	public Color4y getMessageTextColor() {
		return messageTextColor;
	}

	/**
	 * メッセージテキストの色 を設定する.
	 * 
	 * @param messageTextColor メッセージテキストの色
	 */
	public void setMessageTextColor(Color4y messageTextColor) {
		this.messageTextColor = messageTextColor;
	}

	/**
	 * メッセージテキストの色 を設定する.
	 * 
	 * @param messageTextColor メッセージテキストの色
	 */
	public void setOrigMessageTextColor(Color messageTextColor) {
		this.messageTextColor = new Color4y(messageTextColor.getRed(), messageTextColor.getGreen(),
				messageTextColor.getBlue(), messageTextColor.getAlpha());
	}

	/**
	 * メッセージウィンドウの枠線のスタイル を取得する.
	 * 
	 * @return メッセージウィンドウの枠線のスタイル
	 */
	public BasicStrokeEX getMessageWindowStroke() {
		return messageWindowStroke;
	}

	/**
	 * メッセージウィンドウの枠線のスタイル を設定する.
	 * 
	 * @param messageWindowStroke メッセージウィンドウの枠線のスタイル
	 */
	public void setMessageWindowStroke(BasicStrokeEX messageWindowStroke) {
		this.messageWindowStroke = messageWindowStroke;
	}

	/**
	 * メッセージテキストのサイズ を取得する.
	 * 
	 * @return メッセージテキストのサイズ
	 */
	public int getMessageTextSize() {
		return messageTextSize;
	}

	/**
	 * メッセージテキストのサイズ を設定する.
	 * 
	 * @param messageTextSize メッセージテキストのサイズ
	 */
	public void setMessageTextSize(int messageTextSize) {
		this.messageTextSize = messageTextSize;
	}

	/**
	 * 強制的に誕生時メッセージを言わされるかどうか を取得する.
	 * 
	 * @return 強制的に誕生時メッセージを言わされるかどうか
	 */
	public boolean isBirthMessageForced() {
		return BodyBirthRule.isBirthMessageForced(this);
	}

	@JsonIgnore
	public boolean isForceBirthMessageRaw() {
		return birthMessageForced;
	}

	/**
	 * 強制的に誕生時メッセージを言わされるかどうか を設定する.
	 * 
	 * @param birthMessageForced 強制的に誕生時メッセージを言わされるかどうか
	 */
	public void setBirthMessageForced(boolean birthMessageForced) {
		this.birthMessageForced = birthMessageForced;
	}

	/**
	 * ゆっくりのオブジェクトのユニークIDを取得する.
	 * 
	 * @return ゆっくりのオブジェクトのユニークID
	 */
	public int getUniqueID() {
		return uniqueID;
	}

	/**
	 * ゆっくりのオブジェクトのユニークIDを設定する.
	 * 
	 * @param uniqueID ゆっくりのオブジェクトのユニークID
	 */
	public void setUniqueID(int uniqueID) {
		this.uniqueID = uniqueID;
	}

	/**
	 * どのゆっくり的なメッセージを言うか を取得する.
	 * 
	 * @return どのゆっくり的なメッセージを言うか
	 */
	public YukkuriType getMsgType() {
		return msgType;
	}

	/**
	 * どのゆっくり的なメッセージを言うか を設定する.
	 * 
	 * @param msgType どのゆっくり的なメッセージを言うか
	 */
	public void setMsgType(YukkuriType msgType) {
		this.msgType = msgType;
	}

	/**
	 * どのゆっくり的なうんうんをするか を取得する.
	 * 
	 * @return どのゆっくり的なうんうんをするか
	 */
	public YukkuriType getShitType() {
		return shitType;
	}

	/**
	 * どのゆっくり的なうんうんをするか を設定する.
	 * 
	 * @param shitType どのゆっくり的なうんうんをするか
	 */
	public void setShitType(YukkuriType shitType) {
		this.shitType = shitType;
	}

	/**
	 * 右ペインメニューのピン留めをされているかどうかを取得する.
	 * 
	 * @return 右ペインメニューのピン留めをされているかどうか
	 */
	public boolean isPinned() {
		return BodyControlRule.isPinned(this);
	}

	@JsonIgnore
	public boolean isPinRaw() {
		return isPinned;
	}

	/**
	 * 右ペインメニューのピン留めをされているかどうか を設定する.
	 * 
	 * @param isPinned 右ペインメニューのピン留めをされているかどうか
	 */
	public void setPinned(boolean isPinned) {
		this.isPinned = isPinned;
	}

	/**
	 * ゆっくりの移動速度 を取得する.
	 * 
	 * @return ゆっくりの移動速度
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * ゆっくりの移動速度 を設定する.
	 * 
	 * @param speed ゆっくりの移動速度
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/**
	 * 次の落下でダメージを受けないかどうか を取得する.
	 * 
	 * @return 次の落下でダメージを受けないかどうか
	 */
	public boolean isNoDamageNextFall() {
		return BodyControlRule.isNoDamageNextFall(this);
	}

	@JsonIgnore
	public boolean isNoDamageNextFallRaw() {
		return noDamageNextFall;
	}

	/**
	 * 次の落下でダメージを受けないかどうか を設定する.
	 * 
	 * @param noDamageNextFall 次の落下でダメージを受けないかどうか
	 */
	@JsonProperty("noDamageNextFall")
	public void setNoDamageNextFall(boolean noDamageNextFall) {
		this.noDamageNextFall = noDamageNextFall;
	}

	/**
	 * この個体に対して発行されたイベントのリスト を取得する.
	 * 
	 * @return この個体に対して発行されたイベントのリスト
	 */
	public List<EventPacket> getEventList() {
		return eventList;
	}

	/**
	 * この個体に対して発行されたイベントのリスト を設定する.
	 * 
	 * @param eventList この個体に対して発行されたイベントのリスト
	 */
	public void setEventList(List<EventPacket> eventList) {
		this.eventList = eventList;
	}

	/**
	 * 現在実行中のイベント を取得する.
	 * 
	 * @return 現在実行中のイベント
	 */
	public EventPacket getCurrentEvent() {
		return currentEvent;
	}

	/**
	 * 現在実行中のイベント を設定する.
	 * 
	 * @param currentEvent 現在実行中のイベント
	 */
	public void setCurrentEvent(EventPacket currentEvent) {
		this.currentEvent = currentEvent;
	}

	/**
	 * 表情の強制設定 を取得する.
	 * 
	 * @return 表情の強制設定
	 */
	public int getForceFace() {
		return forceFace;
	}

	/**
	 * 表情の強制設定 を設定する.
	 * 
	 * @param forceFace 表情の強制設定
	 */
	public void setForceFace(int forceFace) {
		this.forceFace = forceFace;
	}

	/**
	 * 影の表示有無 を取得する.
	 * 
	 * @return 影の表示有無
	 */
	public boolean isShadowVisible() {
		return BodyDisplayRule.isShadowVisible(this);
	}

	@JsonIgnore
	public boolean isDropShadowRaw() {
		return shadowVisible;
	}

	/**
	 * 影の表示有無 を設定する.
	 * 
	 * @param shadowVisible 影の表示有無
	 */
	public void setShadowVisible(boolean shadowVisible) {
		this.shadowVisible = shadowVisible;
	}

	/**
	 * イベントで設定されたアクション を取得する.
	 * 
	 * @return イベントで設定されたアクション
	 */
	public Event getEventResult() {
		return eventResult;
	}

	/**
	 * イベントで設定されたアクション を設定する.
	 * 
	 * @param eventResult イベントで設定されたアクション
	 */
	public void setEventResult(Event eventResult) {
		this.eventResult = eventResult;
	}

	/**
	 * ゆ虐神拳により 膨らんでいるか/伸ばされているか/押さえつけられているか を取得する.
	 * 
	 * @return ゆ虐神拳により 膨らんでいるか/伸ばされているか/押さえつけられているか
	 */
	public boolean[] getAbFlagGodHand() {
		return abFlagGodHand;
	}

	/**
	 * ゆ虐神拳により 膨らんでいるか/伸ばされているか/押さえつけられているか を設定する.
	 * 
	 * @param abFlagGodHand ゆ虐神拳により 膨らんでいるか/伸ばされているか/押さえつけられているか
	 */
	public void setAbFlagGodHand(boolean[] abFlagGodHand) {
		this.abFlagGodHand = abFlagGodHand;
	}

	/**
	 * ゆ虐神拳の回数を取得する.
	 * 
	 * @return ゆ虐神拳の回数
	 */
	public int getGodHandHoldCount() {
		return godHandHoldCount;
	}

	/**
	 * ゆ虐神拳の回数（押さえ）を設定する.
	 * 
	 * @param v ゆ虐神拳の回数（押さえ）
	 */
	public void setGodHandHoldCount(int v) {
		this.godHandHoldCount = v;
	}

	/**
	 * ゆ虐神拳の回数（伸ばし）を取得する.
	 * 
	 * @return ゆ虐神拳の回数（伸ばし）
	 */
	public int getGodHandStretchCount() {
		return godHandStretchCount;
	}

	/**
	 * ゆ虐神拳の回数（伸ばし）を設定する.
	 * 
	 * @param v ゆ虐神拳の回数（伸ばし）
	 */
	public void setGodHandStretchCount(int v) {
		this.godHandStretchCount = v;
	}

	/**
	 * ゆ虐神拳の回数（押さえ）を取得する.
	 * 
	 * @return ゆ虐神拳の回数（押さえ）
	 */
	public int getGodHandCompressCount() {
		return godHandCompressCount;
	}

	/**
	 * ゆ虐神拳の回数（押さえ）を設定する.
	 * 
	 * @param v ゆ虐神拳の回数（押さえ）
	 */
	public void setGodHandCompressCount(int v) {
		this.godHandCompressCount = v;
	}

	/**
	 * お気に入りアイテム を取得する.
	 * 
	 * @return お気に入りアイテム
	 */
	public HashMap<FavItemType, Integer> getFavoriteItems() {
		return favoriteItems;
	}

	/**
	 * お気に入りアイテム を設定する.
	 * 
	 * @param favoriteItems お気に入りアイテム
	 */
	public void setFavoriteItems(HashMap<FavItemType, Integer> favoriteItems) {
		this.favoriteItems = favoriteItems;
	}

	/**
	 * 持ち歩きアイテム を取得する.
	 * 
	 * @return 持ち歩きアイテム
	 */
	public HashMap<TakeoutItemType, Integer> getCarryItems() {
		return carryItems;
	}

	/**
	 * 持ち歩きアイテム を設定する.
	 * 
	 * @param carryItems 持ち歩きアイテム
	 */
	public void setCarryItems(HashMap<TakeoutItemType, Integer> carryItems) {
		this.carryItems = carryItems;
	}

	/**
	 * ゆっくり本体の購入基本額 を取得する.
	 * 
	 * @return ゆっくり本体の購入基本額
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * ゆっくり本体の購入基本額 を設定する.
	 * 
	 * @param costValue ゆっくり本体の購入基本額
	 */
	public void setCost(int costValue) {
		cost = costValue;
	}

	/**
	 * ゆっくり本体、中身の売却基本額 飼いゆとしての価値/加工品としての価値 を取得する.
	 * 
	 * @return ゆっくり本体、中身の売却基本額 飼いゆとしての価値/加工品としての価値
	 */
	public int[] getSaleValues() {
		return saleValues;
	}

	/**
	 * ゆっくり本体、中身の売却基本額 飼いゆとしての価値/加工品としての価値 を設定する.
	 * 
	 * @param value
	 */
	public void setSaleValues(int[] values) {
		saleValues = values;
	}

	/**
	 * たかっているアリの数 を取得する.
	 * 
	 * @return たかっているアリの数
	 */
	public int getAntCount() {
		return antCount;
	}

	/**
	 * たかっているアリの数 を設定する.
	 * 
	 * @param antCount たかっているアリの数
	 */
	public void setAntCount(int antCount) {
		if (antCount < 0) {
			this.antCount = 0;
		} else {
			this.antCount = antCount;
		}
	}

	/**
	 * うにょ機能を使用するかどうかのフラグ を取得する.
	 * 
	 * @return うにょ機能を使用するかどうかのフラグ
	 */
	public int getUnyoMode() {
		return unyoMode;
	}

	/**
	 * うにょ機能を使用するかどうかのフラグ を設定する.
	 * 
	 * @param unyoMode うにょ機能を使用するかどうかのフラグ
	 */
	public void setUnyoMode(int unyoMode) {
		this.unyoMode = unyoMode;
	}

	/**
	 * うにょの高さ方向 を取得する.
	 * 
	 * @return うにょの高さ方向
	 */
	public int getUnyoOffsetH() {
		return unyoOffsetH;
	}

	/**
	 * うにょの高さ方向 を設定する.
	 * 
	 * @param unyoOffsetH うにょの高さ方向
	 */
	public void setUnyoOffsetH(int unyoOffsetH) {
		this.unyoOffsetH = unyoOffsetH;
	}

	/**
	 * うにょの横方向 を取得する.
	 * 
	 * @return うにょの横方向
	 */
	public int getUnyoOffsetW() {
		return unyoOffsetW;
	}

	/**
	 * うにょの横方向 を設定する.
	 * 
	 * @param unyoOffsetW うにょの横方向
	 */
	public void setUnyoOffsetW(int unyoOffsetW) {
		this.unyoOffsetW = unyoOffsetW;
	}

	/**
	 * うにょの動きの強さ を取得する.
	 * 
	 * @return
	 */
	public static int[] getUnyostrength() {
		return UNYOSTRENGTH;
	}

	/**
	 * 最後に行動した時間を設定する.
	 */
	public void setLastActionTime() {
		long lnNowTime = System.currentTimeMillis();
		lastActionTime = lnNowTime;
	}

	/**
	 * 売却額を返却する.
	 * 
	 * @param F 0が飼いゆ、1が加工品
	 * @return 売却額
	 */
	public int getSellingPrice(int F) {
		// Fが0だと飼いゆとして、1だと加工品としての価値を返す
		return saleValues[F];
	}

	/**
	 * 死ねないように設定する.
	 * 具体的には、死ねない期間に3を設定する.
	 */
	public void setCantDie() {
		cantDiePeriod = 3;
	}

	/**
	 * 妹の数を取得する.
	 * 
	 * @return 妹の数
	 */
	@Transient
	public int getSisterListSize() {
		return sisterList.size();
	}

	/**
	 * 妹のインスタンスを取得する.
	 * 
	 * @param sisterIndex 何番目の妹か
	 * @return 妹のインスタンス
	 */
	public Body getSister(int sisterIndex) {
		return BodyRelations.getSister(this, sisterIndex);
	}

	/**
	 * 姉の数を取得する.
	 * 
	 * @return 姉の数
	 */
	@Transient
	public int getElderSisterListSize() {
		return elderSisterList.size();
	}

	/**
	 * 姉のインスタンスを取得する.
	 * 
	 * @param elderSisterIndex 何番目の姉か
	 * @return 姉のインスタンス
	 */
	public Body getElderSister(int elderSisterIndex) {
		return BodyRelations.getElderSister(this, elderSisterIndex);
	}

	/**
	 * 子供の数を取得する.
	 * 
	 * @return 子供の数
	 */
	@Transient
	public int getChildrenListSize() {
		if (childrenList == null) {
			return 0;
		}
		return childrenList.size();
	}

	/**
	 * 子のインスタンスを取得する.
	 * 
	 * @param childIndex 何番目の子か
	 * @return 子のインスタンス
	 */
	public Body getChildren(int childIndex) {
		if (childrenList == null) {
			return null;
		}
		return BodyRelations.getChildren(this, childIndex);
	}

	/**
	 * 身体年齢を設定し、同時に存続期間を設定する.
	 * 
	 * @param setAgeState ゆっくりの成長段階
	 */
	public void setAgeState(AgeState setAgeState) {
		if (setAgeState == AgeState.BABY) {
			setAge(0);
		} else if (setAgeState == AgeState.CHILD) {
			setAge(getBabyLimitBase());
		} else if (setAgeState == AgeState.ADULT) {
			setAge(getChildLimitBase());
		}
	}

	/**
	 * ゆ下痢かどうかを返却する.
	 * 
	 * @return ゆ下痢かどうか
	 */
	@Transient
	public boolean getDiarrhea() {
		return BodyExcretionRule.getDiarrhea(this);
	}

	/**
	 * ダメージなしかどうかを返却する.
	 * 
	 * @return ダメージなしかどうか
	 */
	@Transient
	public boolean isNoDamaged() {
		return BodyDamageRule.isNoDamaged(this);
	}

	/**
	 * 軽いダメージかどうかを返却する.
	 * 
	 * @return 軽いダメージかどうか
	 */
	@Transient
	public boolean isDamagedLightly() {
		return BodyDamageRule.isDamagedLightly(this);
	}

	/**
	 * ダメージを受けているかどうかを返却する.
	 * 
	 * @return ダメージを受けているかどうか
	 */
	@Transient
	public boolean isDamaged() {
		return BodyDamageRule.isDamaged(this);
	}

	/**
	 * 重いダメージを受けているかどうかを返却する.
	 * 
	 * @return 重いダメージかどうか
	 */
	@Transient
	public boolean isDamagedHeavily() {
		return BodyDamageRule.isDamagedHeavily(this);
	}

	/**
	 * 命乞い中かどうかを設定する.
	 * 埋まっていないときが条件。
	 * 
	 * @param k 命乞い中かどうか
	 */
	public void setBegging(boolean k) {
		if (burialState == BurialState.NONE)
			begging = k;
	}

	/**
	 * 命乞い中かどうかを取得する.
	 * 死んでいないことが条件。
	 * 
	 * @return 命乞い中かどうか
	 */
	@Transient
	public boolean isBeggingForLife() {
		return BodyActionStateRule.isBeggingForLife(this);
	}

	@JsonIgnore
	public boolean isBeggingRaw() {
		return begging;
	}

	/**
	 * 攻撃アクション中かどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return 攻撃アクション中かどうか
	 */
	public boolean isStrike() {
		return BodyActionStateRule.isStrike(this);
	}

	@JsonIgnore
	public boolean isStrikeRaw() {
		return strike;
	}

	/**
	 * 痛みを感じているかどうかを返却する.
	 * 
	 * @return 痛みを感じているか
	 */
	@Transient
	public boolean isFeelPain() {
		return BodyActionStateRule.isFeelPain(this);
	}

	/**
	 * 激しい痛みを感じているかどうかを取得する.
	 * 
	 * @return 激しい痛みを感じているかどうか
	 */
	@Transient
	public boolean isFeelHardPain() {
		return BodyActionStateRule.isFeelHardPain(this);
	}

	/**
	 * 誕生済みか否かを返却する.
	 * 死んでいないことが条件.
	 * 
	 * @return 誕生済みか否か
	 */
	public boolean isBirth() {
		return BodyActionStateRule.isBirth(this);
	}

	@JsonIgnore
	public boolean isBirthRaw() {
		return birth;
	}

	/**
	 * 生まれてからの基準年齢を取得する.
	 *
	 * @return 生まれてからの基準年齢
	 */
	public long getBirthAge() {
		return birthAge;
	}

	/**
	 * 生まれてからの基準年齢を設定する.
	 *
	 * @param birthAge 生まれてからの基準年齢
	 */
	public void setBirthAge(long birthAge) {
		this.birthAge = birthAge;
	}

	/**
	 * 出生からしばらく経っていないかどうかを返却する.
	 *
	 * @return 出生直後ならtrue
	 */
	@Transient
	public boolean isNewborn() {
		return BodyActionStateRule.isNewborn(this);
	}

	@JsonIgnore
	public boolean isNewbornRaw() {
		return birthAge >= 0 && (getAge() - birthAge) < 300;
	}

	/**
	 * 食事中か否かを返却する.
	 * 死んでいないことが条件.
	 * 
	 * @return 食事中か否か
	 */
	public boolean isEating() {
		return BodyActionStateRule.isEating(this);
	}

	@JsonIgnore
	public boolean isEatingRaw() {
		return eating;
	}

	/**
	 * うんうん食い中か否かを返却する.
	 * 死んでいないことが条件.
	 * 
	 * @return うんうん食い中か否か
	 */
	public boolean isEatingShit() {
		return BodyActionStateRule.isEatingShit(this);
	}

	@JsonIgnore
	public boolean isEatingShitRaw() {
		return eatingShit;
	}

	/**
	 * すっきり中か否かを返却する.
	 * 死んでいないことが条件.
	 * 
	 * @return すっきり中か否か
	 */
	public boolean isSukkiri() {
		return BodyActionStateRule.isSukkiri(this);
	}

	@JsonIgnore
	public boolean isSukkiriRaw() {
		return sukkiri;
	}

	/**
	 * 針でさされ中か否かを返却する.
	 * 死んでいないことが条件.
	 * 
	 * @return 針でさされ中か否か
	 */
	/**
	 * ドゲスか否かを返却する.
	 * 
	 * @return ドゲスか否か
	 */
	@Transient
	public boolean isVeryRude() {
		return BodyAttitudeRule.isVeryRude(this);
	}

	/**
	 * ゲスまたはドゲスか否かを返却する.
	 * 
	 * @return ゲスまたはドゲスか否か
	 */
	@Transient
	public boolean isRude() {
		return BodyAttitudeRule.isRude(this);
	}

	/**
	 * ゲス/善良の区分で普通か否かを返却する.
	 * 
	 * @return ゲス/善良の区分で普通か否か
	 */
	@Transient
	public boolean isNormal() {
		return BodyAttitudeRule.isNormal(this);
	}

	/**
	 * 善良または超善良か否かを返却する.
	 * 
	 * @return 善良または超善良か否か
	 */
	@Transient
	public boolean isSmart() {
		return BodyAttitudeRule.isSmart(this);
	}

	/**
	 * 父親を取得
	 * 
	 * @return 父親のインスタンス
	 */
	@Transient
	public int getFather() {
		return parents[Parent.PAPA.ordinal()];
	}

	/**
	 * 母親を取得
	 * 
	 * @return 母親のインスタンス
	 */
	@Transient
	public int getMother() {
		return parents[Parent.MAMA.ordinal()];
	}

	/**
	 * この個体の痛みを感じている程度から、相当するPain(Enum)を返却する.
	 * 
	 * @return この個体に相当するPain
	 */
	@Transient
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
	 * 
	 * @return この個体に相当するBurst
	 */
	@Transient
	public Burst getBurstState() {
		int origin = getOriginSize();
		if (origin <= 0) {
			return Burst.NONE;
		}
		if (getSize() * 4 / origin >= 8) {
			return Burst.BURST;
		} else if (getSize() * 4 / origin >= 7) {
			return Burst.NEAR;
		} else if (getSize() * 4 / origin >= 6) {
			return Burst.HALF;
		} else if (getSize() * 4 / origin >= 5) {
			return Burst.SAFE;
		}
		return Burst.NONE;
	}

	/**
	 * 画像上のゆっくりの大きさを取得する.
	 * 
	 * @return 画像上のゆっくりの大きさ
	 */
	@Transient
	public int getSize() {
		if (bodySpriteSet.getBodySpr() == null) {
			return 0;
		}
		Sprite spr = bodySpriteSet.getBodySpr()[getBodyAgeState().ordinal()];
		if (spr == null) {
			return 0;
		}
		if (SimYukkuri.UNYO) {
		return spr.getImageW() + getExpandSizeW() + unyoOffsetW;
		}
		return spr.getImageW() + getExpandSizeW();
	}

	/**
	 * 画像上のゆっくりのオリジナルサイズを取得する.
	 * 
	 * @return 画像上のゆっくりのオリジナルサイズ
	 */
	@Transient
	public int getOriginSize() {
		if (bodySpriteSet.getBodySpr() == null) {
			return 0;
		}
		Sprite spr = bodySpriteSet.getBodySpr()[getBodyAgeState().ordinal()];
		if (spr == null) {
			return 0;
		}
		return spr.getImageW();
	}

	/**
	 * 妊娠、うんうん、過食などによる横方向の体型のふくらみ取得
	 * 
	 * @return 妊娠、うんうん、過食などによる横方向の体型のふくらみ
	 */
	@Transient
	public int getExpandSizeW() {
		return 0;
	}

	/**
	 * 妊娠、うんうんなどによる縦方向の体型のふくらみ取得
	 * 
	 * @return 妊娠、うんうんなどによる縦方向の体型のふくらみ
	 */
	@Transient
	public int getExpandSizeH() {
		return 0;
	}

	/**
	 * この個体の属する、体のAgeState(Enum)を返却する.
	 * 
	 * @return この個体の属する、体のAgeState
	 */
	@Transient
	public AgeState getBodyAgeState() {
		if (getAge() < getBabyLimitBase()) {
			return AgeState.BABY;
		} else if (getAge() < getChildLimitBase()) {
			return AgeState.CHILD;
		}
		return AgeState.ADULT;
	}

	/**
	 * この個体の属する、精神のAgeState(Enum)を返却する.
	 * 
	 * @return この個体の属する、精神のAgeState
	 */
	@Transient
	public AgeState getMindAgeState() {
		return getBodyAgeState();
	}

	/**
	 * この個体のダメージ具合から、相当するDamage(Enum)を返却する.
	 * 
	 * @return この個体のダメージ具合から計算した、相当するDamage(Enum)
	 */
	@Transient
	public Damage getDamageState() {
		return BodyCoreStateRule.getDamageState(this);
	}

	/**
	 * 死に向かわせる
	 */
	public void toDead() {
		if (!isCantDie() && !dead) {
			dead = true;
			godHandHoldCount = 0;// 死んだらゆ虐神拳1をリセット
		}
	}

	/**
	 * 死ねない期間中かどうかを取得する.
	 * 
	 * @return 死ねない期間中かどうか
	 */
	@Transient
	public boolean isCantDie() {
		return BodyDamageRule.isCantDie(this);
	}

	/**
	 * 破裂しているかどうかを取得する.
	 * 
	 * @return 破裂しているかどうか
	 */
	@Transient
	public boolean isBurst() {
		return BodyDamageRule.isBurst(this);
	}

	/**
	 * まさに破裂するところかどうかを取得する.
	 * 
	 * @return まさに破裂するところかどうか
	 */
	@Transient
	public boolean isAboutToBurst() {
		return BodyDamageRule.isAboutToBurst(this);
	}

	/**
	 * 破裂状態が通常でないかどうかを取得する.
	 * 
	 * @return 破裂状態が通常でないかどうか
	 */
	@Transient
	public boolean isInfration() {
		return BodyDamageRule.isInfration(this);
	}

	/**
	 * 燃やされているかどうかを設定する.
	 * 同時にパニック状態をクリアする.
	 * 
	 * @param b 燃やされているかどうか
	 */
	public void setBurned(boolean b) {
		burned = b;
		setForcePanicClear();
	}

	/**
	 * 茎が生えているかどうかを取得する.
	 * 
	 * @return 茎が生えているかどうか
	 */
	@JsonIgnore
	public boolean hasBindStalk() {
		return BodyDependencyRule.hasBindStalk(this);
	}

	/**
	 * たりないゆかどうかを判定する.
	 * たりないゆ、たりないれいむクラスでオーバーライドする.
	 * 
	 * @return たりないゆかどうか
	 */
	@Transient
	public boolean isIdiot() {
		return BodySpecialTypeRule.isIdiot(this);
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
	 * 
	 * @return 怒っているかどうか
	 */
	public boolean isAngry() {
		return src.logic.BodyMoodRule.isAngry(this);
	}

	@JsonIgnore
	public boolean isAngryRaw() {
		return angry;
	}

	/**
	 * 怯えているかどうかを返却する.
	 * 死んでいないことが条件.
	 * 
	 * @return 怯えているかどうか
	 */
	public boolean isScare() {
		return src.logic.BodyMoodRule.isScare(this);
	}

	@JsonIgnore
	public boolean isScareRaw() {
		return scare;
	}

	/**
	 * 悲しんでいるかどうかを返却する.
	 * 死んでいないことが条件.
	 * 
	 * @return 悲しんでいるかどうか
	 */
	@Transient
	public boolean isSad() {
		return src.logic.BodyMoodRule.isSad(this);
	}

	/**
	 * とても悲しんでいるかどうかを返却する.
	 * 死んでいないことが条件.
	 * 
	 * @return とても悲しんでいるかどうか
	 */
	@Transient
	public boolean isVerySad() {
		return src.logic.BodyMoodRule.isVerySad(this);
	}

	/**
	 * 喜んでいるかどうかを返却する.
	 * 死んでいないことが条件.
	 * 
	 * @return 喜んでいるかどうか
	 */
	@Transient
	public boolean isHappy() {
		return src.logic.BodyMoodRule.isHappy(this);
	}

	/**
	 * 悲しんでいるか、もしくはとても悲しんでいるかを返却する.
	 * 死んでいないことが条件.
	 * 
	 * @return 悲しんでいるか、もしくはとても悲しんでいるか
	 */
	@Transient
	public boolean isUnhappy() {
		return src.logic.BodyMoodRule.isUnhappy(this);
	}

	/**
	 * 食べ過ぎかどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return 食べ過ぎかどうか
	 */
	@Transient
	public boolean isOverEating() {
		return BodyHungerRule.isOverEating(this);
	}

	/**
	 * お腹いっぱいかどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return お腹いっぱいかどうか
	 */
	@Transient
	public boolean isTooFull() {
		return BodyHungerRule.isTooFull(this);
	}

	/**
	 * お腹いっぱい気味かどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return お腹いっぱい気味かどうか
	 */
	@Transient
	public boolean isFull() {
		return BodyHungerStateRule.isFull(this);
	}

	/**
	 * お腹へってきているかどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return お腹へってきているかどうか
	 */
	@Transient
	public boolean isHungry() {
		return BodyHungerStateRule.isHungry(this);
	}

	/**
	 * お腹減り気味かどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return お腹減り気味かどうか
	 */
	@Transient
	public boolean isSoHungry() {
		return BodyHungerStateRule.isSoHungry(this);
	}

	/**
	 * お腹が減っているかどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return お腹が減っているかどうか
	 */
	@Transient
	public boolean isVeryHungry() {
		return BodyHungerStateRule.isVeryHungry(this);
	}

	/**
	 * お腹が減りすぎているかどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return お腹が減りすぎているかどうか
	 */
	@Transient
	public boolean isTooHungry() {
		return BodyHungerStateRule.isTooHungry(this);
	}

	/**
	 * 餓死寸前かどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return 餓死寸前かどうか
	 */
	@Transient
	public boolean isStarving() {
		return BodyHungerStateRule.isStarving(this);
	}

	/**
	 * 満腹度を操作する.
	 * 
	 * @param val 空腹度に足し引きする数値
	 */
	public void addHungry(int val) {
		hungry += (TICK * val);
	}

	/**
	 * 満腹度を取得する.
	 * 
	 * @return 満腹度
	 */
	public int getHungry() {
		return hungry;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public int getJkHung() {
		return hungry;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public void setJkHung(int j) {
		this.hungry = j;
	}

	/**
	 * 満腹度を設定する.
	 * 
	 * @param val 満腹度
	 */
	public void setHungry(int hungry) {
		this.hungry = hungry;
	}

	/**
	 * 段階別（赤/子/成）の飢餓状態限界を取得する.
	 * 
	 * @return 段階別（赤/子/成）の飢餓状態限界
	 */
	@Transient
	public int getHungryLimit() {
		return getHungryLimitBase()[getBodyAgeState().ordinal()];
	}

	/**
	 * 成ゆかどうかを取得する.
	 * 
	 * @return 成ゆかどうか
	 */
	@Transient
	public boolean isAdult() {
		return BodyAgeCategoryRule.isAdult(this);
	}

	/**
	 * 子ゆかどうかを取得する.
	 * 
	 * @return 子ゆかどうか
	 */
	@Transient
	public boolean isChild() {
		return BodyAgeCategoryRule.isChild(this);
	}

	/**
	 * 赤ゆかどうかを取得する.
	 * 
	 * @return 赤ゆかどうか
	 */
	@Transient
	public boolean isBaby() {
		return BodyAgeCategoryRule.isBaby(this);
	}

	/**
	 * 睡眠中かどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return 睡眠中かどうか
	 */
	@JsonProperty
	public boolean isSleeping() {
		return BodyActivityRule.isSleeping(this);
	}

	@JsonIgnore
	public boolean isSleepingRaw() {
		return sleeping;
	}

	/**
	 * 眠いかどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return 眠いかどうか
	 */
	@Transient
	public boolean isSleepy() {
		return BodyActivityRule.isSleepy(this);
	}

	/**
	 * うんうん中かどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return うんうん中かどうか
	 */
	public boolean isShitting() {
		return BodyActivityRule.isShitting(this);
	}

	@JsonIgnore
	public boolean isShittingRaw() {
		return shitting;
	}

	/**
	 * 発情中かどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return 発情中かどうか
	 */
	public boolean isExciting() {
		return BodyActivityRule.isExciting(this);
	}

	@JsonIgnore
	public boolean isExcitingRaw() {
		return exciting;
	}

	/**
	 * 発情状態を設定する.
	 * 
	 * @param temp 発情状態
	 */
	public void setExciting(Boolean temp) {
		if (temp)
			setForceFace(ImageCode.EXCITING.ordinal());
		exciting = temp;
	}

	/**
	 * 強制発情状態かどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return 強制発情状態かどうか
	 */
	/**
	 * 発情を落ち着かせる.
	 */
	public void setCalm() {
		forceExciting = false;
		exciting = false;
	}

	/**
	 * ゆんやあしているかどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return ゆんやあしているかどうか
	 */
	public boolean isYunnyaa() {
		return BodyActivityRule.isYunnyaa(this);
	}

	@JsonIgnore
	public boolean isYunnyaaRaw() {
		return yunnyaa;
	}

	/**
	 * 親を呼んで泣き叫び中かどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return 親を呼んで泣き叫び中かどうか
	 */
	public boolean isCallingParents() {
		return BodyActivityRule.isCallingParents(this);
	}

	@JsonIgnore
	public boolean isCallingParentsRaw() {
		return callingParents;
	}

	/**
	 * 汚れている期間を追加する.
	 * 
	 * @param val 追加する期間
	 */
	public void addDirtyPeriod(int val) {
		dirtyPeriod += val;
	}

	/**
	 * ハイブリッドかどうかを取得する.
	 * ハイブリッドのクラスでオーバーライドする.
	 * 
	 * @return ハイブリッドかどうか
	 */
	@Transient
	public boolean isHybrid() {
		return BodySpecialTypeRule.isHybrid(this);
	}

	/**
	 * 汚れているかどうかを返却する.
	 * 死んでいないことが条件.
	 * 
	 * @return 汚れているかどうか
	 */
	public boolean isDirty() {
		return BodyActivityRule.isDirty(this);
	}

	@JsonIgnore
	public boolean isDirtyRaw() {
		return dirty;
	}

	@JsonIgnore
	public boolean isStubbornlyDirtyRaw() {
		return stubbornlyDirty;
	}

	/**
	 * 普通の汚れかどうかを返却する.
	 * 死んでいないことが条件.
	 * 
	 * @return 普通の汚れかどうか
	 */
	@Transient
	public boolean isNormalDirty() {
		return BodyActivityRule.isNormalDirty(this);
	}

	/**
	 * 運んでいるアイテムを取得する.
	 * 
	 * @param key アイテムのキー
	 * @return 運んでいるアイテム
	 */
	public Obj getCarryItem(TakeoutItemType key) {
		if (carryItems == null) {
			return null;
		}
		if (carryItems.get(key) == null) {
			return null;
		}
		MapPlaceData m = GameWorld.get().getCurrentMap();
		if (m.getTakenOutFood().containsKey(carryItems.get(key))) {
			return m.getTakenOutFood().get(carryItems.get(key));
		}
		if (m.getTakenOutShit().containsKey(carryItems.get(key))) {
			return m.getTakenOutShit().get(carryItems.get(key));
		}
		return BodyRelations.getBodyFromObjId(carryItems.get(key));
	}

	/**
	 * 運んでいるアイテムをクリアする.
	 * 
	 * @param key アイテムのキー
	 */
	public void removeCarryItem(TakeoutItemType key) {
		carryItems.remove(key);
	}

	/**
	 * 動物（というか現在はアリ一択か）に食べられてるかを返却する.
	 * 
	 * @return 動物（というか現在はアリ一択か）に食べられてるか
	 */
	@Transient
	public boolean isEatenByAnimals() {
		return BodyAnimalRule.isEatenByAnimals(this);
	}

	/**
	 * アリを除去する.
	 */
	public void removeAnts() {
		removeAttachment(Ants.class);
		antCount = 0;
	}

	/**
	 * アリの数を減らす.
	 * 
	 * @param A 減らしたいアリの数
	 */
	public void substractNumOfAnts(int A) {
		antCount -= A;
		if (antCount < 0) {
			antCount = 0;
		}
	}

	/**
	 * アタッチメントを追加する.
	 * 
	 * @param at アタッチメント
	 */
	public void addAttachment(Attachment at) {
		attach.add(at);
	}

	/**
	 * 指定クラスのアタッチメント数を取得する.
	 * 
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
	 * 指定クラスのアタッチメント除去
	 * 
	 * @param type 指定クラス
	 */
	public void removeAttachment(Class<?> type) {
		Attachment[] attachments = attach.toArray(new Attachment[0]);
		attach.clear();
		for (Attachment attachment : attachments) {
			if (!attachment.getClass().equals(type)) {
				attach.add(attachment);
			}
		}
	}

	/**
	 * 全アタッチメントのサイズを調整する.
	 */
	public void resetAttachmentBoundary() {
		if (attach != null && attach.size() != 0) {
			for (Attachment at : attach) {
				at.resetBoundary();
			}
		}
	}

	// ------------------------------------------
	/**
	 * 子供を追加する.
	 * 
	 * @param at 子のインスタンス
	 */
	public void addChildrenList(Body at) {
		if (childrenList == null) {
			childrenList = new LinkedList<>();
		}
		if (at != null) {
			childrenList.add(at.getUniqueID());
		}
	}

	/**
	 * 指定の子供をリストから除去する.
	 * 
	 * @param target 子のインスタンス
	 */
	public void removeChildrenList(Body target) {
		BodyRelations.removeChildrenList(this, target);
	}

	// ------------------------------------------
	/**
	 * 姉を追加する.
	 * 
	 * @param at 姉のインスタンス
	 */
	public void addElderSisterList(Body at) {
		if (at != null) {
			elderSisterList.add(at.getUniqueID());
		}
	}

	/**
	 * 指定の姉をリストから除去する.
	 * 
	 * @param target 姉インスタンス
	 */
	public void removeElderSisterList(Body target) {
		BodyRelations.removeElderSisterList(this, target);
	}
	// ------------------------------------------

	/**
	 * 妹を追加する.
	 * 
	 * @param at 妹のインスタンス
	 */
	public void addSisterList(Body at) {
		if (at != null) {
			sisterList.add(at.getUniqueID());
		}
	}

	/**
	 * 指定の妹をリストから除去する.
	 * 
	 * @param target 妹のインスタンス
	 */
	public void removeSisterList(Body target) {
		BodyRelations.removeSisterList(this, target);
	}

	/**
	 * うんうん限界を返却する.
	 * 
	 * @return うんうん限界
	 */
	@Transient
	public int getShitLimit() {
		return getShitLimitBase()[getBodyAgeState().ordinal()];
	}

	/**
	 * 便意を追加する.
	 * 
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
	 * 
	 * @param inShit     設定したい便意
	 * @param ibVeryShit 便意強制MAXフラグ。これが立っていると、inShitはMAXまでの猶予となる。
	 */
	public void setShit(int inShit, boolean ibVeryShit) {
		if (shitting)
			return;
		if (ibVeryShit) {
			if (shit < getShitLimitBase()[getBodyAgeState().ordinal()]) {
				shit = getShitLimitBase()[getBodyAgeState().ordinal()] - inShit;
			}
		} else {
			shit = inShit;
		}
	}

	/**
	 * 胎生妊娠してる赤ゆを取得
	 * <br>
	 * 出産時に、順番に生んでゆくときの処理に使われている
	 * 
	 * @return 胎生妊娠してる赤ゆのDNA
	 **/
	@Transient
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
	 * <br>
	 * 出産時に、順番に生んでゆくときの処理に使われている
	 * 
	 * @return 茎
	 **/
	@Transient
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
	 * 
	 * @return 赤/子/成ゆのダメージ限界
	 */
	@Transient
	public int getDamageLimit() {
		return getDamageLimitBase()[getBodyAgeState().ordinal()];
	}

	/**
	 * ストレスを設定する.
	 * 
	 * @param s ストレス値
	 */
	public void setStress(int s) {
		BodyCoreStateRule.setStress(this, s);
	}

	/**
	 * ストレス値に加える.
	 * 
	 * @param s ストレス値に加えたい値
	 */
	public void addStress(int s) {
		if (dead)
			return;
		// ストレスに応じてうんうん増加
		if (s > 0 && coreAnkoState == CoreAnkoState.DEFAULT && getBurstState() != Burst.HALF)
			plusShit(s / 5);
		stress += TICK * s;
		if (stress < 0)
			stress = 0;
	}

	/**
	 * ストレス値の限界を取得する.
	 * 
	 * @return 赤/子/成ゆのストレス値の限界
	 */
	@Transient
	public int getStressLimit() {
		return getStressLimitBase()[getBodyAgeState().ordinal()];
	}

	/**
	 * ストレスフルかどうかを返却する.
	 * 
	 * @return ストレスフルかどうか
	 */
	@Transient
	public boolean isStressful() {
		return BodyStressRule.isStressful(this);
	}

	/**
	 * とてもストレスフルかどうかを返却する.
	 * 
	 * @return とてもストレスフルかどうか
	 */
	@Transient
	public boolean isVeryStressful() {
		return BodyStressRule.isVeryStressful(this);
	}

	/**
	 * トラウマもちかどうかを返却する.
	 * 
	 * @return トラウマ持ちかどうか
	 */
	public boolean hasTrauma() {
		return BodyFlagRule.hasTrauma(this);
	}

	@JsonIgnore
	public Trauma getTraumaRaw() {
		return trauma;
	}

	/**
	 * ふりふりしているかどうかを返却する.
	 * 
	 * @return ふりふりしているかどうか
	 */
	public boolean isFurifuri() {
		return BodyStyleRule.isFurifuri(this);
	}

	@JsonIgnore
	public boolean isFurifuriRaw() {
		return furifuri;
	}

	/**
	 * 自主的にふりふりするかどうかを返却する.
	 * 
	 * @return 自主的にふりふりするかどうか
	 */
	public boolean willingFurifuri() {
		if (isRude() && GameRandom.nextInt(furifuriDiscipline + 1) == 0 && canFurifuri()) {
			return true;
		}
		return false;
	}

	/**
	 * ふりふり可能な状態かどうかを返却する.
	 * 
	 * @return ふりふり可能な状態かどうか
	 */
	public boolean canFurifuri() {
		if (getFootBakeLevel() != FootBake.CRITICAL && coreAnkoState == CoreAnkoState.DEFAULT) {
			return true;
		}
		return false;
	}

	/**
	 * ふりふりのしつけの値を取得する.
	 * 
	 * @return ふりふりのしつけの値
	 */
	public int getFurifuriDiscipline() {
		return furifuriDiscipline;
	}

	/**
	 * のびのびをしているかどうかを取得する.
	 * 
	 * @return のびのびをしているかどうか
	 */
	public boolean isNobinobi() {
		return BodyStyleRule.isNobinobi(this);
	}

	@JsonIgnore
	public boolean isNobinobiRaw() {
		return nobinobi;
	}

	/**
	 * のびのびをしているかどうかを設定する.
	 * 
	 * @param b のびのびをしているかどうか
	 */
	public void setNobinobi(boolean b) {
		nobinobi = b;
	}

	/**
	 * 脚焼きされている期間を加える.
	 * 
	 * @param s 加えたい足焼き期間
	 */
	public void addFootBakePeriod(int s) {
		footBakePeriod += s;
	}

	/**
	 * 足焼きレベルを取得する.
	 * 
	 * @return 足焼きレベル
	 */
	@Transient
	public FootBake getFootBakeLevel() {
		FootBake ret = FootBake.NONE;
		if (footBakePeriod < 0) {
			footBakePeriod = 0;
		}
		if (footBakePeriod > getDamageLimitBase()[getBodyAgeState().ordinal()]) {
			ret = FootBake.CRITICAL;
		} else if (footBakePeriod > (getDamageLimitBase()[getBodyAgeState().ordinal()] >> 1)) {
			ret = FootBake.MIDIUM;
		}
		return ret;
	}

	/**
	 * キリッ！かどうかを取得する.
	 * 
	 * @return キリッ！かどうか
	 */
	@Transient
	public boolean isVain() {
		return BodyStyleRule.isVain(this);
	}

	@JsonIgnore
	public boolean isVainRaw() {
		return beVain;
	}

	/**
	 * キリッ！中かどうかを取得する.
	 *
	 * @return キリッ！中ならtrue
	 */
	@Transient
	public boolean isBeVain() {
		return BodyStyleRule.isBeVain(this);
	}

	@JsonIgnore
	public boolean isBeVainRaw() {
		return beVain;
	}

	/**
	 * あんこ量を加える.
	 * 
	 * @param val 加えたいあんこ量
	 * @return あんこ量がなくなったかどうか
	 */
	public boolean addAmount(int val) {
		ankoAmount += val;
		if (ankoAmount <= 0) {
			ankoAmount = 0;
			return true;
		}
		return false;
	}

	/**
	 * あんこ量を初期化する.
	 * 
	 * @param val 成長段階
	 */
	public void initAmount(AgeState val) {
		ankoAmount = getDamageLimitBase()[val.ordinal()];
	}

	@Transient
	public int getCollisionX() {
		return (bodySpriteSet.getBodySpr()[getBodyAgeState().ordinal()].getImageW() + getExpandSizeW()) >> 1;
	}

	@Transient
	public int getCollisionY() {
		return (bodySpriteSet.getBodySpr()[getBodyAgeState().ordinal()].getImageH() + getExpandSizeH()) >> 1;
	}

	/**
	 * 足の速さを取得する.
	 * 
	 * @return 足の速さ
	 */
	@Transient
	public int getStep() {
		return (getStepBase()[getBodyAgeState().ordinal()]);
	}

	@Transient
	public int getStepDist() {
		int p = (getStepBase()[getBodyAgeState().ordinal()]) * (getStepBase()[getBodyAgeState().ordinal()]);
		return p;
	}

	@Transient
	public Sprite getBodyBaseSpr() {
		return bodySpriteSet.getBodySpr()[getBodyAgeState().ordinal()];
	}

	@Transient
	public Sprite getBodyExpandSpr() {
		return bodySpriteSet.getExpandSpr()[getBodyAgeState().ordinal()];
	}

	@Transient
	public Sprite getBraidSprite() {
		return bodySpriteSet.getBraidSpr()[getBodyAgeState().ordinal()];
	}

	@Transient
	public BufferedImage getShadowImage() {
		return shadowImages[getBodyAgeState().ordinal()];
	}

	@Transient
	public int getShadowH() {
		return shadowImgH[getBodyAgeState().ordinal()];
	}

	@Transient
	public int getW() {
		return bodySpriteSet.getBodySpr()[getBodyAgeState().ordinal()].getImageW();
	}

	@Transient
	public int getH() {
		return bodySpriteSet.getBodySpr()[getBodyAgeState().ordinal()].getImageH();
	}

	@Transient
	public int getPivotX() {
		return bodySpriteSet.getBodySpr()[getBodyAgeState().ordinal()].getPivotX();
	}

	@Transient
	public int getPivotY() {
		return bodySpriteSet.getBodySpr()[getBodyAgeState().ordinal()].getPivotY();
	}

	@Transient
	public int getBraidW() {
		return bodySpriteSet.getBraidSpr()[getBodyAgeState().ordinal()].getImageW();
	}

	@Transient
	public int getBraidH() {
		return bodySpriteSet.getBraidSpr()[getBodyAgeState().ordinal()].getImageH();
	}

	@Transient
	public int getMaxHaveBaby() {
		return getDamageLimit() / 300;
	}

	/**
	 * 体重を取得する.
	 * 外力を算出するときに使用する.
	 * 
	 * @return 体重
	 */
	@Transient
	public int getWeight() {
		return (getWeightBase()[getBodyAgeState().ordinal()] + (babyTypes.size() + stalkBabyTypes.size()) * 50);
	}

	/**
	 * 致命傷種別を取得する.
	 * 
	 * @return 致命傷種別
	 */
	public CriticalDamegeType getCriticalDamegeType() {
		return criticalDamege;
	}

	/**
	 * 致命傷種別を設定する.
	 * 
	 * @param type 致命傷種別
	 */
	public void setCriticalDamegeType(CriticalDamegeType type) {
		criticalDamege = type;
	}

	/**
	 * お気に入りアイテムを取得する.
	 * 
	 * @param key お気に入りアイテムのタイプ
	 * @return お気に入りアイテムのインスタンス
	 */
	public Obj getFavoriteItem(FavItemType key) {
		return favoriteItems.get(key) == null ? null : takeMappedObj(favoriteItems.get(key));
	}

	/**
	 * お気に入りアイテムを設定する.
	 * 
	 * @param key お気に入りアイテムのタイプ
	 * @param val お気に入りアイテムのインスタンス
	 */
	public void setFavoriteItem(FavItemType key, Obj val) {
		favoriteItems.put(key, val == null ? -1 : val.objId);
	}

	/**
	 * お気に入りアイテムを取り除く.
	 * 
	 * @param key 取り除くアイテムのタイプ
	 */
	public void removeFavoriteItem(FavItemType key) {
		favoriteItems.remove(key);
	}

	/**
	 * はげまんじゅうにする.
	 */
	public void cutHair() {
		hairState = HairState.BALDHEAD;
	}

	/**
	 * 捕食種かどうかを取得する.
	 * 
	 * @return
	 */
	@Transient
	public boolean isPredatorType() {
		return BodyAppearanceRule.isPredatorType(this);
	}

	/**
	 * 茎または腹ではらんでいるかどうかを取得する.
	 * 
	 * @return 茎または腹ではらんでいるかどうか
	 */
	@Transient
	public boolean hasBabyOrStalk() {
		return BodyDependencyRule.hasBabyOrStalk(this);
	}

	/**
	 * おかざりがあるかどうかを取得する.
	 * 
	 * @return おかざりがあるかどうか
	 */
	@Transient
	public final boolean hasOkazari() {
		return BodyAppearanceRule.hasOkazari(this);
	}

	/**
	 * 次の落下でのダメージをなくす.
	 */
	public final void setNoDamageNextFall() {
		setNoDamageNextFall(true);
	}

	/**
	 * おくるみを取る.
	 */
	public void takePants() {
		setHasPants(false);
	}

	/**
	 * 思い出を追加する.
	 * 思い出量は非ゆっくり症チェックで使用する.
	 * 
	 * @param memoryDelta 追加する思い出量
	 */
	public final void addMemories(int memoryDelta) {
		/*
		 * 知能による補正
		 * 賢いと補正無
		 * 普通だと、マイナスの場合のみ1/2に
		 * 餡子脳だとマイナスの場合に1/2,プラスの場合は2倍に
		 */
		switch (getIntelligence()) {
			case WISE:
				memories += memoryDelta / 2;
				break;
			case FOOL:
				if (memoryDelta < 0)
					memories += memoryDelta / 2;
				else
					memories += memoryDelta * 2;
				break;
			default:
				if (memoryDelta < 0)
					memories += memoryDelta;
				else
					memories += memoryDelta * 2;
				break;
		}
	}

	/**
	 * かび判定を行う
	 * 
	 * @return かびているかどうか
	 */
	public boolean findSick(BodyAttributes b) {
		return src.logic.BodyIllnessRule.findSick(this, b);
	}

	/**
	 * ゆかび第一段階(自覚症状)
	 * 
	 * @return ゆかび第一段階以上になっているかどうか
	 */
	@Transient
	public boolean isSick() {
		return BodyVitals.isSick(this);
	}

	/**
	 * ゆかび第二段階
	 * 
	 * @return ゆかび第二段階になっているかどうか
	 */
	@Transient
	public boolean isSickHeavily() {
		return BodyVitals.isSickHeavily(this);
	}

	/**
	 * ゆかび第三段階、かつダメージ有
	 * 
	 * @return ゆかび第三段階になっているかどうか
	 */
	@Transient
	public boolean isSickTooHeavily() {
		return BodyVitals.isSickTooHeavily(this);
	}

	/**
	 * 強制的にゆかびにする.
	 */
	public final void forceSetSick() {
		sickPeriod = (getIncubationPeriodBase() * 32) + 2;
	}

	/**
	 * 老ゆかどうかを取得する.
	 * 
	 * @return 老ゆかどうか
	 */
	@Transient
	public final boolean isOld() {
		return BodyAgeRule.isOld(this);
	}

	/**
	 * 喋っているかどうかを取得する.
	 * 
	 * @return 喋っているかどうか
	 */
	@Transient
	public final boolean isTalking() {
		return BodySpeechRule.isTalking(this);
	}

	/**
	 * 幸福度を設定する.
	 * 
	 * @param happy 幸福度
	 */
	public void setHappiness(Happiness happy) {
		if (isDead() || isIdiot()) {
			happiness = Happiness.AVERAGE;
			return;
		}
		if (isNYD()) {
			happiness = Happiness.VERY_SAD;
			sadPeriod = 1200 + GameRandom.nextInt(400) - 200;
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
				sadPeriod = 1200 + GameRandom.nextInt(400) - 200;
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
	 * 
	 * @return 足焼きレベル
	 */
	@Transient
	public final BodyBake getBodyBakeLevel() {
		BodyBake ret = BodyBake.NONE;
		if (bodyBakePeriod < 0) {
			footBakePeriod = 0;
		}
		if (bodyBakePeriod > getDamageLimitBase()[getBodyAgeState().ordinal()] * 3 / 4) {
			ret = BodyBake.CRITICAL;
		} else if (bodyBakePeriod > (getDamageLimitBase()[getBodyAgeState().ordinal()] * 2 / 5)) {
			ret = BodyBake.MIDIUM;
		}
		return ret;
	}

	/**
	 * やけどの有無を取得する.
	 * 
	 * @return やけどの有無
	 */
	@Transient
	public boolean isGotBurned() {
		return BodyBurnRule.isGotBurned(this);
	}

	/**
	 * 深刻なやけどの有無を取得する.
	 * 
	 * @return 深刻なやけどの有無
	 */
	@Transient
	public boolean isGotBurnedHeavily() {
		return BodyBurnRule.isGotBurnedHeavily(this);
	}

	/**
	 * プレイヤーに対する愛を加える.
	 * 
	 * @param val 加えたい愛（マイナスもあり）
	 */
	public void addLovePlayer(int val) {
		// 非ゆっくり症発症個体は常にプレイヤーを嫌いに
		if (isNYD()) {
			lovePlayer = -1 * getLovePlayerLimitBase();
			return;
		}
		lovePlayer += (TICK * val);
		if (lovePlayer < -1 * getLovePlayerLimitBase()) {
			// 下限設定
			lovePlayer = -1 * getLovePlayerLimitBase();
		} else if (getLovePlayerLimitBase() < lovePlayer) {
			// 上限設定
			lovePlayer = getLovePlayerLimitBase();
		}
	}

	/**
	 * 体の焦げ具合を追加する.
	 * 
	 * @param s 加えたい体の焦げ具合
	 */
	public void addBodyBakePeriod(int s) {
		footBakePeriod += (s / 5);
		bodyBakePeriod += s;
	}

	/**
	 * 舌の肥え度を加える.
	 * 
	 * @param val 加えたい舌の肥え度
	 */
	public void addTang(int val) {
		setTang(getTang() + val);
	}

	/**
	 * 舌の肥度合いを取得する.
	 * 
	 * @return 舌の肥度合い
	 */
	@Transient
	public TangType getTangType() {
		TangType ret;
		if (getTang() < getTangLevelBase()[0]) {
			ret = TangType.POOR;
		} else if (getTang() < getTangLevelBase()[1]) {
			ret = TangType.NORMAL;
		} else {
			ret = TangType.GOURMET;
		}
		return ret;
	}

	/**
	 * 一回の食事量を取得する.
	 * 
	 * @return 一回の食事量
	 */
	@Transient
	public int getEatAmount() {
		return getEatAmountBase()[getBodyAgeState().ordinal()];
	}

	/**
	 * あまあまへの慣れを増減する.
	 * 
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
	 * 
	 * @return 頑固な汚れかどうか
	 */
	public final boolean isStubbornlyDirty() {
		return BodyFlagRule.isStubbornlyDirty(this);
	}

	/**
	 * 非ゆっくり症(間近も含む)かどうかを返却する.
	 * 
	 * @return 非ゆっくり症(間近も含む)かどうか
	 */
	@Transient
	public final boolean isNYD() {
		return BodyFlagRule.isNYD(this);
	}

	/**
	 * 非ゆっくり症ではないどうかを返却する.
	 * 
	 * @return 非ゆっくり症ではないかどうか
	 */
	@Transient
	public final boolean isNotNYD() {
		return BodyFlagRule.isNotNYD(this);
	}

	/**
	 * 行動目的を取得する.
	 * 
	 * @return 行動目的
	 */
	public PurposeOfMoving getPurposeOfMoving() {
		return purposeOfMoving;
	}

	/**
	 * 行動目的を設定する.
	 * 
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

	/**
	 * 取られているかどうかを取得する.
	 * 
	 * @return 取られているかどうか
	 */
	public boolean isTaken() {
		return BodyFlagRule.isTaken(this);
	}

	@JsonIgnore
	public boolean isTakenRaw() {
		return taken;
	}

	/**
	 * 取られているかどうかを設定する.
	 * 
	 * @param taken 取られているかどうか
	 */
	public void setTaken(boolean taken) {
		this.taken = taken;
	}

	public boolean isPeropero() {
		return BodyFlagRule.isPeropero(this);
	}

	@JsonIgnore
	public boolean isPeroperoRaw() {
		return peropero;
	}

	public boolean isBegging() {
		return BodyFlagRule.isBegging(this);
	}

}
