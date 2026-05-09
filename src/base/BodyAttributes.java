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
import src.entity.living.SocialEntity;
import src.SimYukkuri;
import src.logic.BodyRelations;
import src.util.GameRandom;
import src.util.GameWorld;
import src.attachment.Attachment;
import src.attachment.Ants;
import src.entity.world.bodylinked.Okazari;
import src.event.EventPacket;
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
import src.draw.Rectangle4y;
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
public abstract class BodyAttributes extends SocialEntity {
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

	// BodyNameSet — 各ゆっくり固有の名前データ（Yukkuri で実装）
	public abstract String getBaseBodyFileName();
	public abstract void setBaseBodyFileName(String v);
	public abstract String[] getBabyNames();
	public abstract void setBabyNames(String[] v);
	public abstract String[] getChildNames();
	public abstract void setChildNames(String[] v);
	public abstract String[] getAdultNames();
	public abstract void setAdultNames(String[] v);
	public abstract String[] getMyNames();
	public abstract void setMyNames(String[] v);
	public abstract String[] getBabyNamesDamaged();
	public abstract void setBabyNamesDamaged(String[] v);
	public abstract String[] getChildNamesDamaged();
	public abstract void setChildNamesDamaged(String[] v);
	public abstract String[] getAdultNamesDamaged();
	public abstract void setAdultNamesDamaged(String[] v);
	public abstract String[] getMyNamesDamaged();
	public abstract void setMyNamesDamaged(String[] v);

	/**
	 * 名前関連データを他の BodyAttributes から深く複製する.
	 * @param from 複製元
	 */
	public void copyBodyNameSetFrom(BodyAttributes from) {
		if (from == null) {
			return;
		}
		setBaseBodyFileName(from.getBaseBodyFileName());
		setBabyNames(from.getBabyNames() != null ? from.getBabyNames().clone() : null);
		setChildNames(from.getChildNames() != null ? from.getChildNames().clone() : null);
		setAdultNames(from.getAdultNames() != null ? from.getAdultNames().clone() : null);
		setMyNames(from.getMyNames() != null ? from.getMyNames().clone() : null);
		setBabyNamesDamaged(from.getBabyNamesDamaged() != null ? from.getBabyNamesDamaged().clone() : null);
		setChildNamesDamaged(from.getChildNamesDamaged() != null ? from.getChildNamesDamaged().clone() : null);
		setAdultNamesDamaged(from.getAdultNamesDamaged() != null ? from.getAdultNamesDamaged().clone() : null);
		setMyNamesDamaged(from.getMyNamesDamaged() != null ? from.getMyNamesDamaged().clone() : null);
	}

	/**
	 * スプライト関連データを他の BodyAttributes から深く複製する.
	 * @param from 複製元
	 */
	public void copyBodySpriteSetFrom(BodyAttributes from) {
		if (from == null) {
			return;
		}
		setBodySpr(copySprites(from.getBodySpr()));
		setExpandSpr(copySprites(from.getExpandSpr()));
		setBraidSpr(copySprites(from.getBraidSpr()));
	}

	private static Sprite[] copySprites(Sprite[] src) {
		if (src == null) {
			return null;
		}
		Sprite[] ret = new Sprite[src.length];
		for (int i = 0; i < src.length; i++) {
			ret[i] = copySprite(src[i]);
		}
		return ret;
	}

	private static Sprite copySprite(Sprite src) {
		if (src == null) {
			return null;
		}
		Sprite ret = new Sprite();
		ret.setOriginalW(src.getOriginalW());
		ret.setOriginalH(src.getOriginalH());
		ret.setImageW(src.getImageW());
		ret.setImageH(src.getImageH());
		ret.setPivotX(src.getPivotX());
		ret.setPivotY(src.getPivotY());
		ret.setPivotType(src.getPivotType());
		Rectangle4y[] rect = src.getScreenRect();
		if (rect != null) {
			Rectangle4y[] rectCopy = new Rectangle4y[rect.length];
			for (int i = 0; i < rect.length; i++) {
				Rectangle4y r = rect[i];
				rectCopy[i] = (r == null) ? null : new Rectangle4y(r.getX(), r.getY(), r.getWidth(), r.getHeight());
			}
			ret.setScreenRect(rectCopy);
		}
		return ret;
	}

	// public variables


	// Used in image loading.
	private static BufferedImage[] shadowImages = new BufferedImage[3];
	/** 影画像のサイズ定義 */
	protected static int[] shadowImgW = new int[3], shadowImgH = new int[3];
	/** 影画像の中心定義 */
	protected static int[] shadowPivX = new int[3], shadowPivY = new int[3];
	// .INIファイルで変更可能な各ゆっくりのパラメータ.
	/** うにょの動きの強さ */
	public final static int UNYOSTRENGTH[] = { 4, 7, 10 };

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
	 * BodyAttributes レイヤーのフィールドを to へコピーする (名前・スプライトセット).
	 */
	@Override
	public void copyStateTo(Entity to) {
		super.copyStateTo(to);
		BodyAttributes b = (BodyAttributes) to;
		b.copyBodyNameSetFrom(this);
		b.copyBodySpriteSetFrom(this);
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
	 * 蓄積ダメージ counter indicating damage を取得する.
	 * 
	 * @return 蓄積ダメージ counter indicating damage
	 */
	@JsonIgnore
	public Attitude getAttitudeRaw() {
		return attitude;
	}

	@JsonIgnore
	public void setAttitudeRaw(Attitude attitude) {
		this.attitude = attitude;
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

	@Override
	public void setStress(int s) {
		if (s > 0) {
			setStressRaw(s);
		}
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
	 * 胎生妊娠有無 having baby or not を取得する.
	 * 
	 * @return 胎生妊娠有無 having baby or not
	 */
	public boolean isHasBaby() {
		return BodyStructureRule.isHasBaby(this);
	}

	@JsonIgnore
	public boolean isHasBabyRaw() {
		return super.isHasBaby();
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
		return super.isHasStalk();
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
	 * おかざり消失に気づいているかを返す. サブクラス（Yukkuri）でオーバーライドされる.
	 *
	 * @return デフォルトはfalse
	 */
	public boolean isNoticeNoOkazari() {
		return false;
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
	 * 強制発情フラグ want to sukkiri or not を取得する.
	 * 
	 * @return 強制発情フラグ want to sukkiri or not
	 */
	@JsonIgnore
	public boolean isForceExcitingRaw() {
		return forceExciting;
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
	 * 針の有無 を取得する.
	 * 
	 * @return 針の有無
	 */
	@JsonIgnore
	public boolean isNeedledRaw() {
		return needled;
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
	 * 自分がレイプでできた子か を取得する.
	 * 
	 * @return 自分がレイプでできた子か
	 */
	public boolean isFatherRaper() {
		return BodyConditionRule.isFatherRaper(this);
	}

	@JsonIgnore
	public boolean isFatherRaperRaw() {
		return super.isFatherRaper();
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

	@JsonIgnore
	public boolean isRareTypeRaw() {
		return rareType;
	}

	@JsonIgnore
	public void setRareTypeRaw(boolean rareType) {
		this.rareType = rareType;
	}

	@JsonIgnore
	public boolean isLikeBitterFoodRaw() {
		return likeBitterFood;
	}

	@JsonIgnore
	public void setLikeBitterFoodRaw(boolean likeBitterFood) {
		this.likeBitterFood = likeBitterFood;
	}

	@JsonIgnore
	public boolean isLikeHotFoodRaw() {
		return likeHotFood;
	}

	@JsonIgnore
	public void setLikeHotFoodRaw(boolean likeHotFood) {
		this.likeHotFood = likeHotFood;
	}

	@JsonIgnore
	public boolean isLikeWaterRaw() {
		return likeWater;
	}

	@JsonIgnore
	public void setLikeWaterRaw(boolean likeWater) {
		this.likeWater = likeWater;
	}

	@JsonIgnore
	public boolean isFlyingTypeRaw() {
		return flyingType;
	}

	@JsonIgnore
	public void setFlyingTypeRaw(boolean flyingType) {
		this.flyingType = flyingType;
	}

	@JsonIgnore
	public boolean isBraidTypeRaw() {
		return braidType;
	}

	@JsonIgnore
	public void setBraidTypeRaw(boolean braidType) {
		this.braidType = braidType;
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
	public boolean canPullOrPush() {
		return BodyMovementGoalRule.canPullOrPush(this);
	}

	@JsonIgnore
	public boolean canPullOrPushRaw() {
		return canPullOrPush;
	}

	/**
	 * ひっぱり、押しつぶし可能か を設定する.
	 * 
	 * @param canPullOrPush ひっぱり、押しつぶし可能か
	 */
	public void setCanPullOrPush(boolean canPullOrPush) {
		this.canPullOrPush = canPullOrPush;
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
	 * 壁に引っかかった回数 を設定する.
	 * 
	 * @param blockedTicks 壁に引っかかった回数
	 */


	@JsonIgnore
	public boolean isUnBirthRaw() {
		return unBirth;
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
	 * メッセージラインの色 を設定する.
	 * 
	 * @param messageLineColor メッセージラインの色
	 */
	public void setOrigMessageLineColor(Color messageLineColor) {
		this.messageLineColor = new Color4y(messageLineColor.getRed(), messageLineColor.getGreen(),
				messageLineColor.getBlue(), messageLineColor.getAlpha());
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
	 * メッセージテキストの色 を設定する.
	 * 
	 * @param messageTextColor メッセージテキストの色
	 */
	public void setOrigMessageTextColor(Color messageTextColor) {
		this.messageTextColor = new Color4y(messageTextColor.getRed(), messageTextColor.getGreen(),
				messageTextColor.getBlue(), messageTextColor.getAlpha());
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
	 * ゆっくりの移動速度 を設定する.
	 * 
	 * @param speed ゆっくりの移動速度
	 */


	@JsonIgnore
	public boolean isNoDamageNextFallRaw() {
		return noDamageNextFall;
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
		return getSisterList().size();
	}

	/**
	 * 妹のインスタンスを取得する.
	 * 
	 * @param sisterIndex 何番目の妹か
	 * @return 妹のインスタンス
	 */
	public Yukkuri getSister(int sisterIndex) {
		return BodyRelations.getSister(this, sisterIndex);
	}

	/**
	 * 姉の数を取得する.
	 * 
	 * @return 姉の数
	 */
	@Transient
	public int getElderSisterListSize() {
		return getElderSisterList().size();
	}

	/**
	 * 姉のインスタンスを取得する.
	 * 
	 * @param elderSisterIndex 何番目の姉か
	 * @return 姉のインスタンス
	 */
	public Yukkuri getElderSister(int elderSisterIndex) {
		return BodyRelations.getElderSister(this, elderSisterIndex);
	}

	/**
	 * 子供の数を取得する.
	 * 
	 * @return 子供の数
	 */
	@Transient
	public int getChildrenListSize() {
		if (getChildrenList() == null) {
			return 0;
		}
		return getChildrenList().size();
	}

	/**
	 * 子のインスタンスを取得する.
	 * 
	 * @param childIndex 何番目の子か
	 * @return 子のインスタンス
	 */
	public Yukkuri getChildren(int childIndex) {
		if (getChildrenList() == null) {
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

	@JsonIgnore
	public boolean isBirthRaw() {
		return birth;
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
		return getParents()[Parent.PAPA.ordinal()];
	}

	/**
	 * 母親を取得
	 * 
	 * @return 母親のインスタンス
	 */
	@Transient
	public int getMother() {
		return getParents()[Parent.MAMA.ordinal()];
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
		if (getBodySpr() == null) {
			return 0;
		}
		Sprite spr = getBodySpr()[getBodyAgeState().ordinal()];
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
		if (getBodySpr() == null) {
			return 0;
		}
		Sprite spr = getBodySpr()[getBodyAgeState().ordinal()];
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
	 * 睡眠中かどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return 睡眠中かどうか
	 */
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

	// ------------------------------------------
	/**
	 * 子供を追加する.
	 * 
	 * @param at 子のインスタンス
	 */
	public void addChildrenList(Yukkuri at) {
		if (at != null) {
			getChildrenList().add(at.getUniqueID());
		}
	}

	/**
	 * 指定の子供をリストから除去する.
	 * 
	 * @param target 子のインスタンス
	 */
	public void removeChildrenList(Yukkuri target) {
		BodyRelations.removeChildrenList(this, target);
	}

	// ------------------------------------------
	/**
	 * 姉を追加する.
	 * 
	 * @param at 姉のインスタンス
	 */
	public void addElderSisterList(Yukkuri at) {
		if (at != null) {
			getElderSisterList().add(at.getUniqueID());
		}
	}

	/**
	 * 指定の姉をリストから除去する.
	 * 
	 * @param target 姉インスタンス
	 */
	public void removeElderSisterList(Yukkuri target) {
		BodyRelations.removeElderSisterList(this, target);
	}
	// ------------------------------------------

	/**
	 * 妹を追加する.
	 * 
	 * @param at 妹のインスタンス
	 */
	public void addSisterList(Yukkuri at) {
		if (at != null) {
			getSisterList().add(at.getUniqueID());
		}
	}

	/**
	 * 指定の妹をリストから除去する.
	 * 
	 * @param target 妹のインスタンス
	 */
	public void removeSisterList(Yukkuri target) {
		BodyRelations.removeSisterList(this, target);
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
		if (getBabyTypes().size() > 0) {
			babyType = getBabyTypes().get(0);
			getBabyTypes().remove(0);
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
		if (getStalks().size() > 0) {
			stalk = getStalks().get(0);
			getStalks().remove(0);
		}
		return stalk;
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
		int cur = getAnkoAmount() + val;
		if (cur <= 0) {
			setAnkoAmount(0);
			return true;
		}
		setAnkoAmount(cur);
		return false;
	}

	/**
	 * あんこ量を初期化する.
	 * 
	 * @param val 成長段階
	 */
	public void initAmount(AgeState val) {
		setAnkoAmount(getDamageLimitBase()[val.ordinal()]);
	}

	@Transient
	public int getCollisionX() {
		return (getBodySpr()[getBodyAgeState().ordinal()].getImageW() + getExpandSizeW()) >> 1;
	}

	@Transient
	public int getCollisionY() {
		return (getBodySpr()[getBodyAgeState().ordinal()].getImageH() + getExpandSizeH()) >> 1;
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
		return getBodySpr()[getBodyAgeState().ordinal()];
	}

	@Transient
	public Sprite getBodyExpandSpr() {
		return getExpandSpr()[getBodyAgeState().ordinal()];
	}

	@Transient
	public Sprite getBraidSprite() {
		return getBraidSpr()[getBodyAgeState().ordinal()];
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
		return getBodySpr()[getBodyAgeState().ordinal()].getImageW();
	}

	@Transient
	public int getH() {
		return getBodySpr()[getBodyAgeState().ordinal()].getImageH();
	}

	@Transient
	public int getPivotX() {
		return getBodySpr()[getBodyAgeState().ordinal()].getPivotX();
	}

	@Transient
	public int getPivotY() {
		return getBodySpr()[getBodyAgeState().ordinal()].getPivotY();
	}

	@Transient
	public int getBraidW() {
		return getBraidSpr()[getBodyAgeState().ordinal()].getImageW();
	}

	@Transient
	public int getBraidH() {
		return getBraidSpr()[getBodyAgeState().ordinal()].getImageH();
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
		return (getWeightBase()[getBodyAgeState().ordinal()] + (getBabyTypes().size() + getStalkBabyTypes().size()) * 50);
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
	 * かび判定を行う
	 * 
	 * @return かびているかどうか
	 */
	public boolean findSick(BodyAttributes b) {
		return src.logic.BodyIllnessRule.findSick(this, b);
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
	 * 行動目的を設定する.
	 *
	 * @param purposeOfMoving 行動目的
	 */


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


	// --- Restored rule-based overrides ---

	/**
	 * 性格 counter indicating shithead/nice etc. を取得する.
	 * 
	 * @return 性格 counter indicating shithead/nice etc.
	 */
	public Attitude getAttitude() {
		return BodyCoreStateRule.getAttitude(this);
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
	 * この個体のダメージ具合から、相当するDamage(Enum)を返却する.
	 * 
	 * @return この個体のダメージ具合から計算した、相当するDamage(Enum)
	 */
	@Transient
	public Damage getDamageState() {
		return BodyCoreStateRule.getDamageState(this);
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
	 * 知性 を取得する.
	 * 
	 * @return 知性
	 */
	public Intelligence getIntelligence() {
		return BodyCoreStateRule.getIntelligence(this);
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
	 * プレイヤーへのなつき度概算 を取得する.
	 * 
	 * @return プレイヤーへのなつき度概算
	 */
	public LovePlayer getLovePlayerState() {
		return BodyCoreStateRule.getLovePlayerState(this);
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
	 * うんうんの溜まり具合 を取得する.
	 * 
	 * @return うんうんの溜まり具合
	 */
	public int getShit() {
		return BodyCoreStateRule.getShit(this);
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
	 * トラウマ を取得する.
	 * 
	 * @return トラウマ
	 */
	public Trauma getTrauma() {
		return BodyCoreStateRule.getTrauma(this);
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

	/**
	 * 種族としてお下げ、羽、尻尾を持つか を取得する.
	 * 
	 * @return 種族としてお下げ、羽、尻尾を持つか
	 */
	public boolean isBraidType() {
		return BodyTraitRule.isBraidType(this);
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

	/**
	 * 発情中かどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return 発情中かどうか
	 */
	public boolean isExciting() {
		return BodyActivityRule.isExciting(this);
	}

	/**
	 * 空を飛ぶか を取得する.
	 * 
	 * @return 空を飛ぶか
	 */
	public boolean isFlyingType() {
		return BodyTraitRule.isFlyingType(this);
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

	/**
	 * 苦いえさが好きか を取得する.
	 * 
	 * @return 苦いえさが好きか
	 */
	public boolean isLikeBitterFood() {
		return BodyTraitRule.isLikeBitterFood(this);
	}

	/**
	 * 辛いえさが好きか を取得する.
	 * 
	 * @return 辛いえさが好きか
	 */
	public boolean isLikeHotFood() {
		return BodyTraitRule.isLikeHotFood(this);
	}

	/**
	 * 水が平気か を取得する.
	 * 
	 * @return 水が平気か
	 */
	public boolean isLikeWater() {
		return BodyTraitRule.isLikeWater(this);
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

	/**
	 * 次の落下でダメージを受けないかどうか を取得する.
	 * 
	 * @return 次の落下でダメージを受けないかどうか
	 */
	public boolean isNoDamageNextFall() {
		return BodyControlRule.isNoDamageNextFall(this);
	}

	/**
	 * 希少種か を取得する.
	 * 
	 * @return 希少種か
	 */
	public boolean isRareType() {
		return BodyTraitRule.isRareType(this);
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

	/**
	 * 実ゆかどうか を取得する.
	 * 
	 * @return 実ゆかどうか
	 */
	public boolean isUnBirth() {
		return BodyBirthRule.isUnBirth(this);
	}

	/**
	 * 性格 counter indicating shithead/nice etc.を設定する.
	 * 
	 * @param attitude 性格 counter indicating shithead/nice etc.
	 */
	public void setAttitude(Attitude attitude) {
		BodyCoreStateRule.setAttitude(this, attitude);
	}

	/**
	 * 種族としてお下げ、羽、尻尾を持つか を設定する.
	 * 
	 * @param braidType 種族としてお下げ、羽、尻尾を持つか
	 */
	public void setBraidType(boolean braidType) {
		BodyTraitRule.setBraidType(this, braidType);
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
	 * ダメージ外観 を設定する.
	 * 
	 * @param damageState ダメージ外観
	 */
	public void setDamageState(Damage damageState) {
		BodyCoreStateRule.setDamageState(this, damageState);
	}

	/**
	 * 空を飛ぶか を設定する.
	 * 
	 * @param flyingType 空を飛ぶか
	 */
	public void setFlyingType(boolean flyingType) {
		BodyTraitRule.setFlyingType(this, flyingType);
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
	 * 知性 を設定する.
	 * 
	 * @param intelligence 知性
	 */
	public void setIntelligence(Intelligence intelligence) {
		BodyCoreStateRule.setIntelligence(this, intelligence);
	}

	/**
	 * 苦いえさが好きか を設定する.
	 * 
	 * @param likeBitterFood 苦いえさが好きか
	 */
	public void setLikeBitterFood(boolean likeBitterFood) {
		BodyTraitRule.setLikeBitterFood(this, likeBitterFood);
	}

	/**
	 * 辛いえさが好きか を設定する.
	 * 
	 * @param likeHotFood 辛いえさが好きか
	 */
	public void setLikeHotFood(boolean likeHotFood) {
		BodyTraitRule.setLikeHotFood(this, likeHotFood);
	}

	/**
	 * 水が平気か を設定する.
	 * 
	 * @param likeWater 水が平気か
	 */
	public void setLikeWater(boolean likeWater) {
		BodyTraitRule.setLikeWater(this, likeWater);
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
	 * プレイヤーへのなつき度概算 を設定する.
	 * 
	 * @param lovePlayerState プレイヤーへのなつき度概算
	 */
	public void setLovePlayerState(LovePlayer lovePlayerState) {
		BodyCoreStateRule.setLovePlayerState(this, lovePlayerState);
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
	 * 希少種か を設定する.
	 * 
	 * @param rareType 希少種か
	 */
	public void setRareType(boolean rareType) {
		BodyTraitRule.setRareType(this, rareType);
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
	 * 舌の肥え を設定する.
	 * 
	 * @param tang 舌の肥え
	 */
	public void setTang(int tang) {
		BodyCoreStateRule.setTang(this, tang);
	}

	/**
	 * トラウマ を設定する.
	 * 
	 * @param trauma トラウマ
	 */
	public void setTrauma(Trauma trauma) {
		BodyCoreStateRule.setTrauma(this, trauma);
	}

}
