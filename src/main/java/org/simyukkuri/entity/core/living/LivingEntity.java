package org.simyukkuri.entity.core.living;

import java.awt.Rectangle;
import java.beans.Transient;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.simyukkuri.Const;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Color4y;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.Attachment;
import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Okazari;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.item.Bed;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.YukkuriBake;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.Burst;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.Damage;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.FavItemType;
import org.simyukkuri.enums.FootBake;
import org.simyukkuri.enums.HairState;
import org.simyukkuri.enums.Pain;
import org.simyukkuri.enums.PanicType;
import org.simyukkuri.enums.PlayStyle;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.enums.PurposeOfMoving;
import org.simyukkuri.enums.TakeoutItemType;
import org.simyukkuri.enums.TangType;
import org.simyukkuri.enums.Trauma;
import org.simyukkuri.enums.WindowType;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.logic.AntInfestationPolicy;
import org.simyukkuri.logic.YukkuriRelations;
import org.simyukkuri.system.BasicStrokeEX;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;

/**
 * 生命を持つゲーム内エンティティの抽象基底クラス。
 * 生物的・装備的な状態フィールドを保持する。
 */
public abstract class LivingEntity extends Entity {
	private static final long serialVersionUID = -6112543281740502011L;

	// --- Delegate lazy init（シングルスレッド前提）---
	private transient LivingEntityBodyDamageDelegate bodyDamageDelegate;
	private transient LivingEntitySleepDelegate sleepDelegate;
	private transient LivingEntityPanicDelegate panicDelegate;
	private transient LivingEntityHungerDelegate hungerDelegate;
	private transient LivingEntityBodyConditionDelegate bodyConditionDelegate;
	private transient LivingEntityActionDelegate actionDelegate;

	LivingEntityBodyDamageDelegate bodyDamageDelegate() {
		if (bodyDamageDelegate == null)
			bodyDamageDelegate = new LivingEntityBodyDamageDelegate(this);
		return bodyDamageDelegate;
	}

	LivingEntitySleepDelegate sleepDelegate() {
		if (sleepDelegate == null)
			sleepDelegate = new LivingEntitySleepDelegate(this);
		return sleepDelegate;
	}

	LivingEntityPanicDelegate panicDelegate() {
		if (panicDelegate == null)
			panicDelegate = new LivingEntityPanicDelegate(this);
		return panicDelegate;
	}

	LivingEntityHungerDelegate hungerDelegate() {
		if (hungerDelegate == null)
			hungerDelegate = new LivingEntityHungerDelegate(this);
		return hungerDelegate;
	}

	LivingEntityBodyConditionDelegate bodyConditionDelegate() {
		if (bodyConditionDelegate == null)
			bodyConditionDelegate = new LivingEntityBodyConditionDelegate(this);
		return bodyConditionDelegate;
	}

	LivingEntityActionDelegate actionDelegate() {
		if (actionDelegate == null)
			actionDelegate = new LivingEntityActionDelegate(this);
		return actionDelegate;
	}

	/** 蓄積ダメージ counter indicating damage */
	protected int damage = 0;
	/** ダメージ外観 */
	protected Damage damageState = Damage.NONE;
	/** 蓄積ストレス */
	protected int stress = 0;
	/** 空腹度 counter indicating how hungry */
	protected int hungry;
	/** うんうんの溜まり具合 */
	protected int shit = 0;
	/** 舌の肥え */
	protected int tang = 500;
	/** 死亡フラグdead of alive */
	protected boolean dead = false;
	/** 死んでからの期間 */
	protected int deadPeriod = 0;
	/** 中枢餡の状態（非ゆっくり症フラグ */
	protected CoreAnkoState coreAnkoState = CoreAnkoState.NORMAL;
	/** 睡眠中かどうか */
	protected boolean sleeping = false;
	/** 悪夢を見るかどうか */
	protected boolean nightmare = false;
	/** 前回起きた時間 */
	protected long wakeUpTime;
	/** ゆっくりしてるかどうか */
	protected boolean relax = false;
	/** 汚れ有無 */
	protected boolean dirty = false;
	/** 頑固な汚れ有無 */
	protected boolean stubbornlyDirty = false;
	/** 汚れている期間 */
	protected int dirtyPeriod = 0;
	/** 汚れて泣き叫ぶ期間 */
	protected int dirtyScreamPeriod = 0;
	/** 濡れ状態 */
	protected boolean wet = false;
	/** 濡れている期間 */
	protected int wetPeriod = 0;
	/** 水に溶けた状態 */
	protected boolean melt = false;
	/** 皮をむいた状態 */
	protected boolean pealed = false;
	/** 饅頭にされた状態 */
	protected boolean packed = false;
	/** アマギられた状態 */
	protected boolean blind = false;
	/** 針の有無 */
	protected boolean needled = false;
	/** 死体が焼損されているか */
	protected boolean burned = false;
	/** 死体が損壊されているか */
	protected boolean crushed = false;
	/** フェロモンの有無 */
	protected boolean pheromone = false;
	/** おさげ、羽、尻尾有無 種族として何も持っていないものはtrue */
	protected boolean hasBraid = true;
	/** 髪の状態 */
	protected HairState hairState = HairState.DEFAULT;
	/** 誕生済みか否か */
	protected boolean birth = false;
	/** 生まれてからの基準年齢 */
	protected long birthAge = -1;
	/** 出生直後イベントから除外する残り tick 数 */
	protected int birthEventBlockedTicks = 0;
	/** 実ゆかどうか */
	protected boolean unBirth = false;
	/** 妊娠期間 */
	protected int pregnantPeriod = 0;
	/** パニック種別 */
	protected PanicType panicType = null;
	/** パニック状態の期間 */
	protected int panicPeriod = 0;
	/** 致命傷種別 */
	protected CriticalDamageType criticalDamege = null;
	/** トラウマ */
	protected Trauma trauma = Trauma.NONE;
	/** ダメージを受けていない期間 */
	protected int noDamagePeriod = 0;
	/** 飢餓状態になっていない期間 */
	protected int noHungryPeriod = 0;
	/** スーパーむーしゃむーしゃタイムのおかげで飢餓状態にならない期間 */
	protected int superEatingNoHungryPeriod = 0;
	/** 足焼きされている期間 */
	protected int footBakePeriod = 0;
	/** 焼かれている期間 */
	protected int bodyBakePeriod = 0;
	/** 非ゆっくり症にかかっている期間 */
	protected int nonYukkuriDiseasePeriod = 0;
	/** 怒っている期間 */
	protected int angryPeriod = 0;
	/** 怖がっている期間 */
	protected int scarePeriod = 0;
	/** 悲しんでいる期間 */
	protected int sadPeriod = 0;
	/** 睡眠期間 */
	protected int sleepingPeriod = 0;
	/** ゆかびに侵されている期間 */
	protected int sickPeriod = 0;
	/** 落下ダメージ */
	protected int falldownDamage = 0;
	/** 次の落下でダメージを受けないかどうか */
	protected boolean noDamageNextFall = false;
	/** 死なない期間 */
	protected int cantDiePeriod = 0;
	/** 埋まり状態 */
	protected BurialState burialState = BurialState.NONE;

	/**
	 * 通常メッセージを出す.
	 *
	 * @param message メッセージ
	 */
	public void setMessage(String message) {
	}

	/**
	 * ピコピコメッセージを出す.
	 *
	 * @param message   メッセージ
	 * @param interrupt 割り込み可否
	 */
	public void setPikoMessage(String message, boolean interrupt) {
	}

	/**
	 * ピコピコメッセージを出す.
	 *
	 * @param message   メッセージ
	 * @param count     表示時間
	 * @param interrupt 割り込み可否
	 */
	public void setPikoMessage(String message, int count, boolean interrupt) {
	}

	/**
	 * 時間指定メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count   表示時間
	 */
	public void setMessage(String message, int count) {
	}

	/**
	 * 割り込み可否付きでメッセージを出す.
	 *
	 * @param message   メッセージ
	 * @param interrupt 割り込み可否
	 */
	public void setMessage(String message, boolean interrupt) {
	}

	/**
	 * 全指定メッセージを出す.
	 *
	 * @param message   メッセージ
	 * @param count     表示時間
	 * @param interrupt 割り込み可否
	 * @param piko      ピコピコ可否
	 */
	public void setMessage(String message, int count, boolean interrupt, boolean piko) {
	}

	/**
	 * ワールドイベント発生メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count   表示時間
	 */
	public void setWorldEventSendMessage(String message, int count) {
	}

	/**
	 * ワールドイベント応答メッセージを出す.
	 *
	 * @param message   メッセージ
	 * @param count     表示時間
	 * @param interrupt 割り込み可否
	 * @param piko      ピコピコ可否
	 */
	public void setWorldEventResMessage(String message, int count, boolean interrupt, boolean piko) {
	}

	/**
	 * 個体イベント発生メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count   表示時間
	 */
	public void setEventSendMessage(String message, int count) {
	}

	/**
	 * 個体イベント応答メッセージを出す.
	 *
	 * @param message   メッセージ
	 * @param count     表示時間
	 * @param interrupt 割り込み可否
	 * @param piko      ピコピコ可否
	 */
	public void setEventResMessage(String message, int count, boolean interrupt, boolean piko) {
	}

	/**
	 * ウィンドウ種別付きメッセージを出す.
	 *
	 * @param message   メッセージ
	 * @param type      ウィンドウ種別
	 * @param count     表示時間
	 * @param interrupt 割り込み可否
	 * @param piko      ピコピコ可否
	 * @param NYD       非ゆっくり症かどうか
	 */
	public void setMessage(String message, WindowType type, int count, boolean interrupt, boolean piko,
			boolean NYD) {
	}

	/**
	 * ダメージ状態が軽いかを返す.
	 *
	 * @return ダメージ状態が軽いか
	 */
	@Transient
	public boolean isDamagedLightly() {
		return getDamageState() == Damage.SOME || getDamageState() == Damage.VERY || getDamageState() == Damage.TOOMUCH;
	}

	/**
	 * 痛み状態を返す.
	 *
	 * @return 痛み状態
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
	 * 痛みを感じているかを返す.
	 *
	 * @return 痛みを感じているか
	 */
	@Transient
	public boolean isFeelPain() {
		return getPainState() == Pain.VERY || getPainState() == Pain.SOME;
	}

	/**
	 * 激しい痛みを感じているかを返す.
	 *
	 * @return 激しい痛みを感じているか
	 */
	@Transient
	public boolean isFeelHardPain() {
		return getPainState() == Pain.VERY;
	}

	/**
	 * 出生からしばらく経っていないかどうかを返す.
	 *
	 * @return 出生直後ならtrue
	 */
	@Transient
	public boolean isNewborn() {
		return birthAge >= 0 && (getAge() - birthAge) < 300;
	}

	/**
	 * 群れ内ランクを返す.
	 *
	 * @return 群れ内ランク
	 */
	public PublicRank getPublicRank() {
		return null;
	}

	/**
	 * ぺろぺろ状態を設定する.
	 *
	 * @param peropero ぺろぺろ状態
	 */
	public void setPeropero(boolean peropero) {
	}

	/**
	 * かしこいかどうかを返す.
	 *
	 * @return かしこいかどうか
	 */
	public boolean isSmart() {
		return false;
	}

	/**
	 * 行動を消す.
	 */
	public void clearActions() {
	}

	/**
	 * イベントを消す.
	 */
	public void clearEvent() {
	}

	/**
	 * 致命傷種別を返す.
	 *
	 * @return 致命傷種別
	 */
	public CriticalDamageType getCriticalDamageType() {
		return null;
	}

	/**
	 * 蓄積ダメージ counter indicating damage を取得する. @return 蓄積ダメージ counter indicating
	 * damage
	 */
	public int getDamage() {
		return damage;
	}

	/**
	 * 蓄積ダメージ counter indicating damage を設定する. @param damage 蓄積ダメージ counter
	 * indicating damage
	 */
	public void setDamage(int damage) {
		this.damage = damage;
	}

	/** ダメージを追加する（死亡時は無効）. @param amount 追加量 */
	public final void addDamage(int amount) {
		if (isDead())
			return;
		damage += amount;
	}

	/** ダメージ外観 を取得する. @return ダメージ外観 */
	public Damage getDamageState() {
		return damageState;
	}

	/** ダメージ外観 を設定する. @param damageState ダメージ外観 */
	public void setDamageState(Damage damageState) {
		this.damageState = damageState;
	}

	/**
	 * ストレス値に加える.
	 *
	 * @param s ストレス値に加えたい値
	 */
	public void addStress(int s) {
		if (dead) {
			return;
		}
		if (s > 0 && coreAnkoState == CoreAnkoState.NORMAL && getBurstState() != Burst.HALF) {
			plusShit(s / 5);
		}
		stress += TICK * s;
		if (stress < 0) {
			stress = 0;
		}
	}

	/** ダメージ更新を行う. */
	public void checkDamage() {
		bodyDamageDelegate().checkDamage();
	}

	/** 皮を剥がされているときの反応（メッセージ・感情等）。サブクラスで override して実装する. */
	protected void onPealed() {
	}

	/** 詰め込まれているときの反応（メモリ減少等）。サブクラスで override して実装する. */
	protected void onPacked() {
	}

	/** 車の事故に遭ったときの反応。サブクラスで override して実装する. */
	protected void onCarAccident() {
	}

	/** 毒スチーム被曝時の反応（行動停止・感情・メッセージ）。サブクラスで override して実装する. */
	protected void onPoisonSteam() {
	}

	/** 致命傷(CUT)時のメッセージ反応。サブクラスで override して実装する. */
	protected void onCutDamageReaction() {
	}

	/**
	 * 致命傷(INJURED)時の悲鳴反応。サブクラスで override して実装する. @param x 吐瀉物X座標 @param y 吐瀉物Y座標
	 */
	protected void onInjuredScream(int x, int y) {
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isDamaged() {
		return getDamageState() == Damage.VERY || getDamageState() == Damage.TOOMUCH;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isDamagedHeavily() {
		return getDamageState() == Damage.TOOMUCH;
	}

	/** 蓄積ストレス を取得する. @return 蓄積ストレス */
	public int getStress() {
		return stress;
	}

	/** 蓄積ストレス を設定する. @param stress 蓄積ストレス */
	public void setStress(int stress) {
		this.stress = stress;
	}

	/**
	 * 空腹度 counter indicating how hungry を取得する. @return 空腹度 counter indicating how
	 * hungry
	 */
	public int getHungry() {
		return hungry;
	}

	/**
	 * 空腹度 counter indicating how hungry を設定する. @param hungry 空腹度 counter indicating
	 * how hungry
	 */
	public void setHungry(int hungry) {
		this.hungry = hungry;
	}

	/** うんうんの溜まり具合 を取得する. @return うんうんの溜まり具合 */
	public int getShit() {
		return shit;
	}

	/** うんうんの溜まり具合 を設定する. @param shit うんうんの溜まり具合 */
	public void setShit(int shit) {
		this.shit = shit;
	}

	/** 舌の肥え を取得する. @return 舌の肥え */
	public int getTang() {
		return tang;
	}

	/** 舌の肥え を設定する. @param tang 舌の肥え */
	public void setTang(int tang) {
		this.tang = tang;
	}

	/** 死亡フラグdead of alive を取得する. @return 死亡フラグdead of alive */
	public boolean isDead() {
		return dead;
	}

	/** 死亡フラグdead of alive を設定する. @param dead 死亡フラグdead of alive */
	public void setDead(boolean dead) {
		this.dead = dead;
	}

	/**
	 * 死に向かわせる.
	 */
	public void toDead() {
		if (!isCantDie() && !dead) {
			dead = true;
			godHandHoldCount = 0;// 死んだらゆ虐神拳1をリセット
		}
	}

	/** 死んでからの期間 を取得する. @return 死んでからの期間 */
	public int getDeadPeriod() {
		return deadPeriod;
	}

	/** 死んでからの期間 を設定する. @param deadPeriod 死んでからの期間 */
	public void setDeadPeriod(int deadPeriod) {
		this.deadPeriod = deadPeriod;
	}

	/** 中枢餡の状態（非ゆっくり症フラグ を取得する. @return 中枢餡の状態（非ゆっくり症フラグ */
	public CoreAnkoState getCoreAnkoState() {
		return coreAnkoState;
	}

	/** 中枢餡の状態（非ゆっくり症フラグ を設定する. @param coreAnkoState 中枢餡の状態（非ゆっくり症フラグ */
	public void setCoreAnkoState(CoreAnkoState coreAnkoState) {
		this.coreAnkoState = coreAnkoState;
	}

	/** 睡眠中かどうか を取得する. @return 睡眠中かどうか */
	public boolean isSleeping() {
		return !dead && sleeping;
	}

	/** 睡眠中かどうか を設定する. @param sleeping 睡眠中かどうか */
	public void setSleeping(boolean sleeping) {
		this.sleeping = sleeping;
	}

	/** 悪夢を見るかどうか を取得する. @return 悪夢を見るかどうか */
	public boolean isNightmare() {
		return nightmare;
	}

	/** 悪夢を見るかどうか を設定する. @param nightmare 悪夢を見るかどうか */
	public void setNightmare(boolean nightmare) {
		this.nightmare = nightmare;
	}

	/** 前回起きた時間 を取得する. @return 前回起きた時間 */
	public long getWakeUpTime() {
		return wakeUpTime;
	}

	/** 前回起きた時間 を設定する. @param wakeUpTime 前回起きた時間 */
	public void setWakeUpTime(long wakeUpTime) {
		this.wakeUpTime = wakeUpTime;
	}

	/** 発情フラグ want to sukkiri or not を取得する. @return 発情フラグ want to sukkuri or not */
	public boolean isExciting() {
		return false;
	}

	/** 発情フラグ want to sukkiri or not を設定する. */
	public void setExciting(boolean exciting) {
	}

	/**
	 * 強制発情フラグ want to sukkiri or not を取得する. @return 強制発情フラグ want to sukkuri or not
	 */
	public boolean isForceExciting() {
		return false;
	}

	/** 強制発情フラグ want to sukkiri or not を設定する. */
	public void setForceExciting(boolean forceExciting) {
	}

	public void setCalm() {
	}

	/** ゆっくりしてるかどうか を取得する. @return ゆっくりしてるかどうか */
	public boolean isRelax() {
		return relax;
	}

	/** ゆっくりしてるかどうか を設定する. @param relax ゆっくりしてるかどうか */
	public void setRelax(boolean relax) {
		this.relax = relax;
	}

	/** 汚れ有無 を取得する. @return 汚れ有無 */
	public boolean isDirty() {
		return !dead && (dirty || stubbornlyDirty);
	}

	/** 通常の汚れ有無 を取得する. @return 通常の汚れ有無 */
	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isNormalDirty() {
		return !dead && dirty;
	}

	/** 汚れ有無 を設定する. @param dirty 汚れ有無 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/** 頑固な汚れ有無 を取得する. @return 頑固な汚れ有無 */
	public boolean isStubbornlyDirty() {
		return !dead && stubbornlyDirty;
	}

	/** 頑固な汚れ有無 を設定する. @param stubbornlyDirty 頑固な汚れ有無 */
	public void setStubbornlyDirty(boolean stubbornlyDirty) {
		this.stubbornlyDirty = stubbornlyDirty;
	}

	/** 汚れている期間 を取得する. @return 汚れている期間 */
	public int getDirtyPeriod() {
		return dirtyPeriod;
	}

	/** 汚れている期間 を設定する. @param dirtyPeriod 汚れている期間 */
	public void setDirtyPeriod(int dirtyPeriod) {
		this.dirtyPeriod = dirtyPeriod;
	}

	/** 汚れている期間 を進ませる. */
	public final void advanceDirtyPeriod(boolean humid, boolean wetOrMelt, boolean stubbornlyDirty) {
		bodyConditionDelegate().advanceDirtyPeriod(humid, wetOrMelt, stubbornlyDirty);
	}

	/**
	 * 汚れからゆかびへ進行させる.
	 *
	 * @return ゆかびに進行したか
	 */
	public final boolean promoteDirtyToSickIfNeeded() {
		return bodyConditionDelegate().promoteDirtyToSickIfNeeded();
	}

	/** 汚れて泣き叫ぶ期間 を取得する. @return 汚れて泣き叫ぶ期間 */
	public int getDirtyScreamPeriod() {
		return dirtyScreamPeriod;
	}

	/** 汚れて泣き叫ぶ期間 を設定する. @param dirtyScreamPeriod 汚れて泣き叫ぶ期間 */
	public void setDirtyScreamPeriod(int dirtyScreamPeriod) {
		this.dirtyScreamPeriod = dirtyScreamPeriod;
	}

	/** 濡れ状態 を取得する. @return 濡れ状態 */
	public boolean isWet() {
		return wet;
	}

	/** 濡れ状態 を設定する. @param wet 濡れ状態 */
	public void setWet(boolean wet) {
		this.wet = wet;
	}

	/** 濡れている期間 を取得する. @return 濡れている期間 */
	public int getWetPeriod() {
		return wetPeriod;
	}

	/** 濡れている期間 を設定する. @param wetPeriod 濡れている期間 */
	public void setWetPeriod(int wetPeriod) {
		this.wetPeriod = wetPeriod;
	}

	/** 水に溶けた状態 を取得する. @return 水に溶けた状態 */
	public boolean isMelt() {
		return melt;
	}

	/** 水に溶けた状態 を設定する. @param melt 水に溶けた状態 */
	public void setMelt(boolean melt) {
		this.melt = melt;
	}

	/** 皮をむいた状態 を取得する. @return 皮をむいた状態 */
	public boolean isPealed() {
		return pealed;
	}

	/** 皮をむいた状態 を設定する. @param pealed 皮をむいた状態 */
	public void setPealed(boolean pealed) {
		this.pealed = pealed;
	}

	/** 饅頭にされた状態 を取得する. @return 饅頭にされた状態 */
	public boolean isPacked() {
		return packed;
	}

	/** 饅頭にされた状態 を設定する. @param packed 饅頭にされた状態 */
	public void setPacked(boolean packed) {
		this.packed = packed;
	}

	/** アマギられた状態 を取得する. @return アマギられた状態 */
	public boolean isBlind() {
		return blind;
	}

	/** アマギられた状態 を設定する. @param blind アマギられた状態 */
	public void setBlind(boolean blind) {
		this.blind = blind;
	}

	/**
	 * 盲目時の生理ペナルティを適用する.
	 *
	 * @return 盲目なら true, それ以外は false
	 */
	protected boolean applyBlindnessPenalty() {
		return actionDelegate().applyBlindnessPenalty();
	}

	/**
	 * 口封じ時の生理ペナルティを適用する.
	 *
	 * @return 口封じ中なら true, それ以外は false
	 */
	protected boolean applyCantSpeakPenalty() {
		return actionDelegate().applyCantSpeakPenalty();
	}

	/**
	 * 動けない時の前提条件を更新する.
	 *
	 * @return 反応を継続できるなら true, 条件未満なら false
	 */
	protected boolean beginLockmoveEmotion() {
		return actionDelegate().beginLockmoveEmotion();
	}

	/**
	 * 足焼き時の前提条件を更新する.
	 *
	 * @return 反応を継続できるなら true, 条件未満なら false
	 */
	protected boolean beginFootBakeEmotion() {
		return actionDelegate().beginFootBakeEmotion();
	}

	/**
	 * おかざり、ぴこぴこなし時の前提条件を確認する.
	 *
	 * @return 反応を継続できるなら true, 条件未満なら false
	 */
	protected boolean beginNoOkazariEmotion() {
		return actionDelegate().beginNoOkazariEmotion();
	}

	/** 針の有無 を取得する. @return 針の有無 */
	public boolean isNeedled() {
		return !dead && needled;
	}

	/** 針の有無 を設定する. @param needled 針の有無 */
	public void setNeedled(boolean needled) {
		this.needled = needled;
	}

	/** 死体が焼損されているか を取得する. @return 死体が焼損されているか */
	public boolean isBurned() {
		return burned;
	}

	/** 死体が焼損されているか を設定する. @param burned 死体が焼損されているか */
	public void setBurned(boolean burned) {
		this.burned = burned;
	}

	/** 死体が損壊されているか を取得する. @return 死体が損壊されているか */
	public boolean isCrushed() {
		return crushed;
	}

	/** 死体が損壊されているか を設定する. @param crushed 死体が損壊されているか */
	public void setCrushed(boolean crushed) {
		this.crushed = crushed;
	}

	/** フェロモンの有無 を取得する. @return フェロモンの有無 */
	public boolean isPheromone() {
		return pheromone;
	}

	/** フェロモンの有無 を設定する. @param pheromone フェロモンの有無 */
	public void setPheromone(boolean pheromone) {
		this.pheromone = pheromone;
	}

	/**
	 * おさげ、羽、尻尾有無 種族として何も持っていないものはtrue を取得する. @return おさげ、羽、尻尾有無
	 * 種族として何も持っていないものはtrue
	 */
	public boolean isHasBraid() {
		return hasBraid;
	}

	/**
	 * おさげ、羽、尻尾有無 種族として何も持っていないものはtrue を設定する. @param hasBraid おさげ、羽、尻尾有無
	 * 種族として何も持っていないものはtrue
	 */
	public void setHasBraid(boolean hasBraid) {
		this.hasBraid = hasBraid;
	}

	/** 髪の状態 を取得する. @return 髪の状態 */
	public HairState getHairState() {
		return hairState;
	}

	/** 髪の状態 を設定する. @param hairState 髪の状態 */
	public void setHairState(HairState hairState) {
		this.hairState = hairState;
	}

	/** ぺにぺにの去勢有無 を取得する. @return ぺにぺにの去勢有無 */
	public boolean isPenipeniCutted() {
		return false;
	}

	/** ぺにぺにの去勢有無 を設定する. */
	public void setPenipeniCutted(boolean penipeniCutted) {
	}

	/** 水が平気か を取得する. @return 水が平気か */
	public boolean isLikeWater() {
		return false;
	}

	/** 水が平気か を設定する. */
	public void setLikeWater(boolean likeWater) {
	}

	/** 空を飛ぶか を取得する. @return 空を飛ぶか */
	public boolean isFlyingType() {
		return false;
	}

	/** 空を飛ぶか を設定する. */
	public void setFlyingType(boolean flyingType) {
	}

	/** 捕食種（れみりゃ・ふらん）であれば {@code true}. */
	@JsonIgnore
	public boolean isPredator() {
		return false;
	}

	/** ハイブリッドゆっくりであれば {@code true}. */
	@JsonIgnore
	public boolean isHybrid() {
		return false;
	}

	/** 捕食種に仕える従者種（咲夜・美鈴）であれば {@code true}. */
	@JsonIgnore
	public boolean isServant() {
		return false;
	}

	/**
	 * 指定した主種族に仕える従者種であれば {@code true}.
	 *
	 * @param masterType 主の種族ID
	 * @return 指定種族の従者かどうか
	 */
	@JsonIgnore
	public boolean isServantOf(YukkuriType masterType) {
		return false;
	}

	/** 誕生済みか否か を取得する. @return 誕生済みか否か */
	public boolean isBirth() {
		return !dead && birth;
	}

	/** 誕生済みか否か を設定する. @param birth 誕生済みか否か */
	public void setBirth(boolean birth) {
		this.birth = birth;
	}

	/** 生まれてからの基準年齢 を取得する. @return 生まれてからの基準年齢 */
	public long getBirthAge() {
		return birthAge;
	}

	/** 生まれてからの基準年齢 を設定する. @param birthAge 生まれてからの基準年齢 */
	public void setBirthAge(long birthAge) {
		this.birthAge = birthAge;
	}

	/** 実ゆかどうか を取得する. @return 実ゆかどうか */
	public boolean isUnBirth() {
		return unBirth;
	}

	/** 実ゆかどうか を設定する. @param unBirth 実ゆかどうか */
	public void setUnBirth(boolean unBirth) {
		this.unBirth = unBirth;
		if (unBirth) {
			birthEventBlockedTicks = 0;
		}
	}

	/** 出生直後イベントから除外する残り tick 数 を取得する. @return 出生直後イベントから除外する残り tick 数 */
	public int getBirthEventBlockedTicks() {
		return birthEventBlockedTicks;
	}

	/**
	 * 出生直後イベントから除外する残り tick 数 を設定する. @param birthEventBlockedTicks 出生直後イベントから除外する残り
	 * tick 数
	 */
	public void setBirthEventBlockedTicks(int birthEventBlockedTicks) {
		this.birthEventBlockedTicks = birthEventBlockedTicks;
	}

	/** 妊娠期間 を取得する. @return 妊娠期間 */
	public int getPregnantPeriod() {
		return pregnantPeriod;
	}

	/** 妊娠期間 を設定する. @param pregnantPeriod 妊娠期間 */
	public void setPregnantPeriod(int pregnantPeriod) {
		this.pregnantPeriod = pregnantPeriod;
	}

	/** パニック種別 を取得する. @return パニック種別 */
	public PanicType getPanicType() {
		return panicType;
	}

	/** パニック種別 を設定する. @param panicType パニック種別 */
	public void setPanicType(PanicType panicType) {
		this.panicType = panicType;
	}

	/** パニック状態の期間 を取得する. @return パニック状態の期間 */
	public int getPanicPeriod() {
		return panicPeriod;
	}

	/** パニック状態の期間 を設定する. @param panicPeriod パニック状態の期間 */
	public void setPanicPeriod(int panicPeriod) {
		this.panicPeriod = panicPeriod;
	}

	/** パニック状態を解除する. */
	protected final void clearPanic() {
		panicDelegate().clearPanic();
	}

	/** パニック解除時の感情反応。サブクラスで override して実装する. */
	protected void onClearPanic() {
	}

	/**
	 * 怖がり判定.
	 *
	 * @return 何もしない
	 */
	public TickResult checkFear() {
		return panicDelegate().checkFear();
	}

	/**
	 * メッセージ消費と共通の前処理を行う.
	 *
	 * @return この後の個体固有メッセージ分岐を続けるか
	 */
	public final boolean updateMessageCommon() {
		--messageTicks;
		if (messageTicks <= 5) {
			// stop to show the message 0.5 sec. before.
			setMessageBuffer(null);
		}
		if (messageTicks <= 0) {
			messageTicks = 0;
			setFurifuri(false);
			setStrike(false);
			setEating(false);
			setEatingShit(false);
			setPeropero(false);
			setSukkiri(false);
			setNobinobi(false);
			setBeVain(false);
			setPikopiko(false);
			setYunnyaa(false);
			setInOutTakeoutItem(false);
		}
		// しゃべれないor生まれていないor非ゆっくり症
		if (isSilent() || isUnBirth() || isNYD()) {
			if (isUnBirth()) {
				setMessageTicks(0);
				setMessageBuffer(null);
			}
			return false;
		}

		if (isDead()) {
			if (!isSilent()) {
				String messages = GameMessages.getMessage(this, MessagePool.Action.Dead);
				setMessage(messages);
				setBirthMessageForced(false);
				if (getMessageBuffer() == messages) {
					// if the message is set successfully, be silent.
					setSilent(true);
				}
			}
			return false;
		}
		return true;
	}

	/** 致命傷種別 を取得する. @return 致命傷種別 */
	public CriticalDamageType getCriticalDamege() {
		return criticalDamege;
	}

	/** 致命傷種別 を設定する. @param criticalDamege 致命傷種別 */
	public void setCriticalDamege(CriticalDamageType criticalDamege) {
		this.criticalDamege = criticalDamege;
	}

	/** トラウマ を取得する. @return トラウマ */
	public Trauma getTrauma() {
		return trauma;
	}

	/** トラウマ を設定する. @param trauma トラウマ */
	public void setTrauma(Trauma trauma) {
		this.trauma = trauma;
	}

	/** ダメージを受けていない期間 を取得する. @return ダメージを受けていない期間 */
	public int getNoDamagePeriod() {
		return noDamagePeriod;
	}

	/** ダメージを受けていない期間 を設定する. @param noDamagePeriod ダメージを受けていない期間 */
	public void setNoDamagePeriod(int noDamagePeriod) {
		this.noDamagePeriod = noDamagePeriod;
	}

	/** 飢餓状態になっていない期間 を取得する. @return 飢餓状態になっていない期間 */
	public int getNoHungryPeriod() {
		return noHungryPeriod;
	}

	/** 飢餓状態になっていない期間 を設定する. @param noHungryPeriod 飢餓状態になっていない期間 */
	public void setNoHungryPeriod(int noHungryPeriod) {
		this.noHungryPeriod = noHungryPeriod;
	}

	/**
	 * スーパーむーしゃむーしゃタイムのおかげで飢餓状態にならない期間 を取得する. @return
	 * スーパーむーしゃむーしゃタイムのおかげで飢餓状態にならない期間
	 */
	public int getSuperEatingNoHungryPeriod() {
		return superEatingNoHungryPeriod;
	}

	/**
	 * スーパーむーしゃむーしゃタイムのおかげで飢餓状態にならない期間 を設定する. @param v
	 * スーパーむーしゃむーしゃタイムのおかげで飢餓状態にならない期間
	 */
	public void setSuperEatingNoHungryPeriod(int v) {
		this.superEatingNoHungryPeriod = v;
	}

	/** 足焼きされている期間 を取得する. @return 足焼きされている期間 */
	public int getFootBakePeriod() {
		return footBakePeriod;
	}

	/** 足焼きされている期間 を設定する. @param footBakePeriod 足焼きされている期間 */
	public void setFootBakePeriod(int footBakePeriod) {
		this.footBakePeriod = footBakePeriod;
	}

	/** 焼かれている期間 を取得する. @return 焼かれている期間 */
	public int getBakePeriod() {
		return bodyBakePeriod;
	}

	/** 焼かれている期間 を設定する. @param bodyBakePeriod 焼かれている期間 */
	public void setBakePeriod(int bodyBakePeriod) {
		this.bodyBakePeriod = bodyBakePeriod;
	}

	/** 非ゆっくり症にかかっている期間 を取得する. @return 非ゆっくり症にかかっている期間 */
	public int getNonYukkuriDiseasePeriod() {
		return nonYukkuriDiseasePeriod;
	}

	/** 非ゆっくり症にかかっている期間 を設定する. @param v 非ゆっくり症にかかっている期間 */
	public void setNonYukkuriDiseasePeriod(int v) {
		this.nonYukkuriDiseasePeriod = v;
	}

	/** 怒っている期間 を取得する. @return 怒っている期間 */
	public int getAngryPeriod() {
		return angryPeriod;
	}

	/** 怒っている期間 を設定する. @param angryPeriod 怒っている期間 */
	public void setAngryPeriod(int angryPeriod) {
		this.angryPeriod = angryPeriod;
	}

	/** 怖がっている期間 を取得する. @return 怖がっている期間 */
	public int getScarePeriod() {
		return scarePeriod;
	}

	/** 怖がっている期間 を設定する. @param scarePeriod 怖がっている期間 */
	public void setScarePeriod(int scarePeriod) {
		this.scarePeriod = scarePeriod;
	}

	/** 悲しんでいる期間 を取得する. @return 悲しんでいる期間 */
	public int getSadPeriod() {
		return sadPeriod;
	}

	/** 悲しんでいる期間 を設定する. @param sadPeriod 悲しんでいる期間 */
	public void setSadPeriod(int sadPeriod) {
		this.sadPeriod = sadPeriod;
	}

	/** 発情期間 を取得する. @return 発情期間 */
	public int getExcitingPeriod() {
		return 0;
	}

	/** 発情期間 を設定する. */
	public void setExcitingPeriod(int excitingPeriod) {
	}

	/** 睡眠期間 を取得する. @return 睡眠期間 */
	public int getSleepingPeriod() {
		return sleepingPeriod;
	}

	/** 睡眠期間 を設定する. @param sleepingPeriod 睡眠期間 */
	public void setSleepingPeriod(int sleepingPeriod) {
		this.sleepingPeriod = sleepingPeriod;
	}

	/** 寝ているエンティティを起こす. */
	public void wakeup() {
		setSleepingPeriod(0);
		setSleeping(false);
		setNightmare(false);
		setWakeUpTime(getAge());
	}

	/**
	 * 指定の座標まで動く.
	 *
	 * @param toZ Z座標
	 */
	public final void moveToZ(int toZ) {
		if (isDead()) {
			return;
		}
		destZ = Math.max(0, Math.min(toZ, Translate.getWorldDepth()));
	}

	/**
	 * 眠いかどうかを取得する.
	 *
	 * @return 眠いかどうか
	 */
	@JsonIgnore
	public boolean isSleepy() {
		return !isSleeping() && getWakeUpTime() + getActivePeriodBase() < getAge();
	}

	/**
	 * 睡眠状態を更新する.
	 *
	 * @return 睡眠中かどうか
	 */
	public boolean checkSleep() {
		return sleepDelegate().checkSleep();
	}

	/**
	 * 悪夢状態になった/解除されたときの顔変化。nightmare=true なら悪夢顔、false なら通常睡眠顔。サブクラスで override
	 * して実装する.
	 */
	protected void onNightmare(boolean nightmare) {
	}

	/** 飢餓で強制起床したときのメッセージ・感情反応。サブクラスで override して実装する. */
	protected void onWakeByHunger() {
	}

	/** 自然に目覚めたときのメッセージ反応。サブクラスで override して実装する. */
	protected void onWakeupNaturally() {
	}

	/** 眠りについたときの感情設定。サブクラスで override して実装する. */
	protected void onStartSleeping() {
	}

	/** 動かなくする. */
	public final void stay() {
		setStaying(true);
		stayTime = Const.STAYLIMIT;
	}

	/** time 分だけ動かなくする. */
	public final void stay(int time) {
		setStaying(true);
		stayTime = time;
	}

	/** time だけぷるぷるする. */
	public final void stayPurupuru(int time) {
		setStaying(true);
		stayTime = time;
		setPurupuru(true);
	}

	/** ぷるぷる振動アニメーションを更新する. */
	public final void doPurupuru() {
		if (!isShakePhase()) {
			setShakePhase(true);
			setOfsXY(1, ofsY);
		} else {
			setShakePhase(false);
			setOfsXY(0, ofsY);
		}
	}

	/** ゆかびに侵されている期間 を取得する. @return ゆかびに侵されている期間 */
	public int getSickPeriod() {
		return sickPeriod;
	}

	/** ゆかびに侵されている期間 を設定する. @param sickPeriod ゆかびに侵されている期間 */
	public void setSickPeriod(int sickPeriod) {
		this.sickPeriod = sickPeriod;
	}

	/** ゆかびに侵されている期間 を進ませる. */
	public void addSickPeriod(int i) {
		sickPeriod += i;
	}

	/** 落下ダメージ を取得する. @return 落下ダメージ */
	public int getFalldownDamage() {
		return falldownDamage;
	}

	/** 落下ダメージ を設定する. @param falldownDamage 落下ダメージ */
	public void setFalldownDamage(int falldownDamage) {
		this.falldownDamage = falldownDamage;
	}

	/** 次の落下でダメージを受けないかどうか を取得する. @return 次の落下でダメージを受けないかどうか */
	public boolean isNoDamageNextFall() {
		return noDamageNextFall;
	}

	/** 次の落下でダメージを受けないかどうか を設定する. @param noDamageNextFall 次の落下でダメージを受けないかどうか */
	public void setNoDamageNextFall(boolean noDamageNextFall) {
		this.noDamageNextFall = noDamageNextFall;
	}

	/** 死なない期間 を取得する. @return 死なない期間 */
	public int getCantDiePeriod() {
		return cantDiePeriod;
	}

	/** 死なない期間 を設定する. @param cantDiePeriod 死なない期間 */
	public void setCantDiePeriod(int cantDiePeriod) {
		this.cantDiePeriod = cantDiePeriod;
	}

	/** 埋まり状態 を取得する. @return 埋まり状態 */
	public BurialState getBurialState() {
		return burialState;
	}

	/** 埋まり状態 を設定する. @param burialState 埋まり状態 */
	public void setBurialState(BurialState burialState) {
		this.burialState = burialState;
	}

	/** おかざりオブジェクト */
	private Okazari okazari = null;
	/** おかざりの位置 */
	private int okazariPosition = 0;
	/** 赤ちゃんを持っているか */
	private boolean hasBaby = false;
	/** 茎を持っているか */
	private boolean hasStalk = false;
	/** 子供のDNAリスト */
	private List<Dna> babyTypes = new LinkedList<Dna>();
	/** 茎育ちの子供のDNAリスト */
	private List<Dna> stalkBabyTypes = new LinkedList<Dna>();
	/** 茎リスト */
	private List<Stalk> stalks = new LinkedList<Stalk>();
	/** 結びついている茎 */
	private Stalk bindStalk = null;
	/** アタッチメントリスト */
	private List<Attachment> attach = new LinkedList<Attachment>();
	/** 餡子の量 */
	private int ankoAmount = 0;
	/** お気に入りアイテムマップ */
	private HashMap<FavItemType, Integer> favoriteItems = new HashMap<FavItemType, Integer>();
	/** 持ち物マップ */
	private HashMap<TakeoutItemType, Integer> carryItems = new HashMap<TakeoutItemType, Integer>();

	/** おかざりオブジェクト を取得する. @return おかざりオブジェクト */
	public Okazari getOkazaris() {
		return okazari;
	}

	/** おかざりオブジェクト を設定する. @param okazari おかざりオブジェクト */
	public void setOkazaris(Okazari okazari) {
		this.okazari = okazari;
	}

	/** おかざりの位置 を取得する. @return おかざりの位置 */
	public int getOkazariPosition() {
		return okazariPosition;
	}

	/** おかざりの位置 を設定する. @param okazariPosition おかざりの位置 */
	public void setOkazariPosition(int okazariPosition) {
		this.okazariPosition = okazariPosition;
	}

	/** 赤ちゃんを持っているか を取得する. @return 赤ちゃんを持っているか */
	public boolean isHasBaby() {
		return hasBaby;
	}

	/** 赤ちゃんを持っているか を設定する. @param hasBaby 赤ちゃんを持っているか */
	public void setHasBaby(boolean hasBaby) {
		this.hasBaby = hasBaby;
	}

	/** 茎を持っているか を取得する. @return 茎を持っているか */
	public boolean isHasStalk() {
		return hasStalk;
	}

	/** 茎を持っているか を設定する. @param hasStalk 茎を持っているか */
	public void setHasStalk(boolean hasStalk) {
		this.hasStalk = hasStalk;
	}

	/** 子供のDNAリスト を取得する. @return 子供のDNAリスト */
	public List<Dna> getBabyTypes() {
		return babyTypes;
	}

	/** 子供のDNAリスト を設定する. @param babyTypes 子供のDNAリスト */
	public void setBabyTypes(List<Dna> babyTypes) {
		this.babyTypes = babyTypes;
	}

	/** 茎育ちの子供のDNAリスト を取得する. @return 茎育ちの子供のDNAリスト */
	public List<Dna> getStalkBabyTypes() {
		return stalkBabyTypes;
	}

	/** 茎育ちの子供のDNAリスト を設定する. @param stalkBabyTypes 茎育ちの子供のDNAリスト */
	public void setStalkBabyTypes(List<Dna> stalkBabyTypes) {
		this.stalkBabyTypes = stalkBabyTypes;
	}

	/** 茎リスト を取得する. @return 茎リスト */
	public List<Stalk> getStalks() {
		return stalks;
	}

	/** 茎リスト を設定する. @param stalks 茎リスト */
	public void setStalks(List<Stalk> stalks) {
		this.stalks = stalks;
	}

	/** 結びついている茎 を取得する. @return 結びついている茎 */
	public Stalk getBindStalk() {
		return bindStalk;
	}

	/** 結びついている茎 を設定する. @param bindStalk 結びついている茎 */
	public void setBindStalk(Stalk bindStalk) {
		this.bindStalk = bindStalk;
	}

	/** アタッチメントリスト を取得する. @return アタッチメントリスト */
	public List<Attachment> getAttach() {
		return attach;
	}

	/** アタッチメントリスト を設定する. @param attach アタッチメントリスト */
	public void setAttach(List<Attachment> attach) {
		this.attach = attach;
	}

	/** 餡子の量 を取得する. @return 餡子の量 */
	public int getAnkoAmount() {
		return ankoAmount;
	}

	/** 餡子の量 を設定する. @param ankoAmount 餡子の量 */
	public void setAnkoAmount(int ankoAmount) {
		this.ankoAmount = ankoAmount;
	}

	/** お気に入りアイテムマップ を取得する. @return お気に入りアイテムマップ */
	public HashMap<FavItemType, Integer> getFavoriteItems() {
		return favoriteItems;
	}

	/** お気に入りアイテムマップ を設定する. @param favoriteItems お気に入りアイテムマップ */
	public void setFavoriteItems(HashMap<FavItemType, Integer> favoriteItems) {
		this.favoriteItems = favoriteItems;
	}

	/** 持ち物マップ を取得する. @return 持ち物マップ */
	public HashMap<TakeoutItemType, Integer> getCarryItems() {
		return carryItems;
	}

	/** 持ち物マップ を設定する. @param carryItems 持ち物マップ */
	public void setCarryItems(HashMap<TakeoutItemType, Integer> carryItems) {
		this.carryItems = carryItems;
	}

	/** 移動先目標 destination */
	protected int destX = -1;

	/** 移動先目標 destination を取得する. @return 移動先目標 destination */
	public int getDestX() {
		return destX;
	}

	/** 移動先目標 destination を設定する. @param destX 移動先目標 destination */
	public void setDestX(int destX) {
		this.destX = destX;
	}

	/** 移動先目標 destination */
	protected int destY = -1;

	/** 移動先目標 destination を取得する. @return 移動先目標 destination */
	public int getDestY() {
		return destY;
	}

	/** 移動先目標 destination を設定する. @param destY 移動先目標 destination */
	public void setDestY(int destY) {
		this.destY = destY;
	}

	/** 移動先目標 destination */
	protected int destZ = -1;

	/** 移動先目標 destination を取得する. @return 移動先目標 destination */
	public int getDestZ() {
		return destZ;
	}

	/** 移動先目標 destination を設定する. @param destZ 移動先目標 destination */
	public void setDestZ(int destZ) {
		this.destZ = destZ;
	}

	/** 移動量 how many steps to same direction */
	protected int countX = 0;

	/**
	 * 移動量 how many steps to same direction を取得する. @return 移動量 how many steps to
	 * same direction
	 */
	public int getCountX() {
		return countX;
	}

	/**
	 * 移動量 how many steps to same direction を設定する. @param countX 移動量 how many steps
	 * to same direction
	 */
	public void setCountX(int countX) {
		this.countX = countX;
	}

	/** 移動量 how many steps to same direction */
	protected int countY = 0;

	/**
	 * 移動量 how many steps to same direction を取得する. @return 移動量 how many steps to
	 * same direction
	 */
	public int getCountY() {
		return countY;
	}

	/**
	 * 移動量 how many steps to same direction を設定する. @param countY 移動量 how many steps
	 * to same direction
	 */
	public void setCountY(int countY) {
		this.countY = countY;
	}

	/** 移動量 how many steps to same direction */
	protected int countZ = 0;

	/**
	 * 移動量 how many steps to same direction を取得する. @return 移動量 how many steps to
	 * same direction
	 */
	public int getCountZ() {
		return countZ;
	}

	/**
	 * 移動量 how many steps to same direction を設定する. @param countZ 移動量 how many steps
	 * to same direction
	 */
	public void setCountZ(int countZ) {
		this.countZ = countZ;
	}

	/** 移動方向 direction to move on */
	protected int dirX = 0;

	/** 移動方向 direction to move on を取得する. @return 移動方向 direction to move on */
	public int getDirX() {
		return dirX;
	}

	/** 移動方向 direction to move on を設定する. @param dirX 移動方向 direction to move on */
	public void setDirX(int dirX) {
		this.dirX = dirX;
	}

	/** 移動方向 direction to move on */
	protected int dirY = 0;

	/** 移動方向 direction to move on を取得する. @return 移動方向 direction to move on */
	public int getDirY() {
		return dirY;
	}

	/** 移動方向 direction to move on を設定する. @param dirY 移動方向 direction to move on */
	public void setDirY(int dirY) {
		this.dirY = dirY;
	}

	/** 移動方向 direction to move on */
	protected int dirZ = 0;

	/** 移動方向 direction to move on を取得する. @return 移動方向 direction to move on */
	public int getDirZ() {
		return dirZ;
	}

	/** 移動方向 direction to move on を設定する. @param dirZ 移動方向 direction to move on */
	public void setDirZ(int dirZ) {
		this.dirZ = dirZ;
	}

	/** 顔の向き direction of face */
	protected Direction direction = Direction.LEFT;

	/** 顔の向き direction of face を取得する. @return 顔の向き direction of face */
	public Direction getDirection() {
		return direction;
	}

	/** 顔の向き direction of face を設定する. @param direction 顔の向き direction of face */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/** 移動目的 */
	protected PurposeOfMoving purposeOfMoving = PurposeOfMoving.NONE;

	/** 移動目的 を取得する. @return 移動目的 */
	public PurposeOfMoving getPurposeOfMoving() {
		return purposeOfMoving;
	}

	/** 移動目的 を設定する. @param purposeOfMoving 移動目的 */
	public void setPurposeOfMoving(PurposeOfMoving purposeOfMoving) {
		this.purposeOfMoving = purposeOfMoving;
	}

	/** ひっぱり、押しつぶし可能か */
	protected boolean canPullOrPush = false;

	/** ひっぱり、押しつぶし可能か を取得する. @return ひっぱり、押しつぶし可能か */
	public boolean canPullOrPush() {
		return canPullOrPush;
	}

	/** ひっぱり、押しつぶし可能か を設定する. @param canPullOrPush ひっぱり、押しつぶし可能か */
	public void setCanPullOrPush(boolean canPullOrPush) {
		this.canPullOrPush = canPullOrPush;
	}

	/** ゆっくりの移動速度 */
	protected int speed = 100;

	/** ゆっくりの移動速度 を取得する. @return ゆっくりの移動速度 */
	public int getSpeed() {
		return speed;
	}

	/** ゆっくりの移動速度 を設定する. @param speed ゆっくりの移動速度 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/** 外圧 */
	protected int externalPressure = 0;

	/** 外圧 を取得する. @return 外圧 */
	public int getExternalPressure() {
		return externalPressure;
	}

	/** 外圧 を設定する. @param externalPressure 外圧 */
	public void setExternalPressure(int externalPressure) {
		this.externalPressure = externalPressure;
	}

	/**
	 * 動けないかどうかを返却する.
	 *
	 * @return 動けないかどうか
	 */
	@Transient
	public final boolean isDontMove() {
		if (isDead()
				|| isRemoved()
				|| isUnBirth()
				|| isSleeping()
				|| isNeedled()
				|| getFootBakeLevel() == FootBake.CRITICAL
				|| isLockmove()
				|| isMelt()
				|| getBurialState() != BurialState.NONE
				|| isBirth()
				|| isGrabbed()
				|| isOnNonMovingConveyor()
				|| getCoreAnkoState() == CoreAnkoState.NON_YUKKURI_DISEASE
				|| getCriticalDamageType() == CriticalDamageType.CUT
				|| isPealed()
				|| isBlind()
				|| isPacked()) {
			return true;
		}
		return false;
	}

	/**
	 * 大丈夫じゃないかどうかを返却する.
	 *
	 * @return 大丈夫じゃないかどうか
	 */
	@Transient
	public final boolean isNotAllright() {
		if (isDead()
				|| isRemoved()
				|| isUnBirth()
				|| isNeedled()
				|| isLockmove()
				|| isMelt()
				|| getBurialState() != BurialState.NONE
				|| isBirth()
				|| isOnNonMovingConveyor()
				|| getCoreAnkoState() == CoreAnkoState.NON_YUKKURI_DISEASE
				|| getCriticalDamageType() == CriticalDamageType.CUT
				|| isPealed()
				|| isBlind()
				|| isPacked()) {
			return true;
		}
		return false;
	}

	/**
	 * ピョンピョンできないかどうかを返却する.
	 *
	 * @return ピョンピョンできないかどうか
	 */
	@Transient
	public final boolean isDontJump() {
		if (isDontMove()) {
			return true;
		}
		if (getCriticalDamageType() != null) {
			return true;
		}
		if (hasBabyOrStalk()) {
			return true;
		}
		if (isNYD()) {
			return true;
		}
		if (isGotBurnedHeavily()) {
			return true;
		}
		if (getAttachmentSize(Ants.class) != 0) {
			return true;
		}
		if (!isFlyingType()) {
			if (isDamaged()) {
				return true;
			}
			if (isSickHeavily()) {
				return true;
			}
			if (isFeelPain()) {
				return true;
			}
		}
		return false;
	}

	/** 移動対象（移動先） */
	protected int moveTargetId = -1;

	/** 移動対象（移動先） を取得する. @return 移動対象（移動先） */
	public int getMoveTargetId() {
		return moveTargetId;
	}

	/** 移動対象（移動先） を設定する. @param moveTargetId 移動対象（移動先） */
	public void setMoveTargetId(int moveTargetId) {
		this.moveTargetId = moveTargetId;
	}

	/** 移動対象のX座標オフセット */
	protected int targetOffsetX = 0;

	/** 移動対象のX座標オフセット を取得する. @return 移動対象のX座標オフセット */
	public int getTargetOffsetX() {
		return targetOffsetX;
	}

	/** 移動対象のX座標オフセット を設定する. @param targetOffsetX 移動対象のX座標オフセット */
	public void setTargetOffsetX(int targetOffsetX) {
		this.targetOffsetX = targetOffsetX;
	}

	/** 移動対象のY座標オフセット */
	protected int targetOffsetY = 0;

	/** 移動対象のY座標オフセット を取得する. @return 移動対象のY座標オフセット */
	public int getTargetOffsetY() {
		return targetOffsetY;
	}

	/** 移動対象のY座標オフセット を設定する. @param targetOffsetY 移動対象のY座標オフセット */
	public void setTargetOffsetY(int targetOffsetY) {
		this.targetOffsetY = targetOffsetY;
	}

	/** うんうんアクション中 */
	protected boolean shitting = false;

	/** うんうんアクション中 を取得する. @return うんうんアクション中 */
	public boolean isShitting() {
		return !dead && shitting;
	}

	/** うんうんアクション中 を設定する. @param shitting うんうんアクション中 */
	public void setShitting(boolean shitting) {
		this.shitting = shitting;
	}

	/** 何かを食べ中 */
	protected boolean eating = false;

	/** 何かを食べ中 を取得する. @return 何かを食べ中 */
	public boolean isEating() {
		return !dead && eating;
	}

	/** 何かを食べ中 を設定する. @param eating 何かを食べ中 */
	public void setEating(boolean eating) {
		this.eating = eating;
	}

	/** うんうんを食べ中 */
	protected boolean eatingShit = false;

	/** うんうんを食べ中 を取得する. @return うんうんを食べ中 */
	public boolean isEatingShit() {
		return !dead && eatingShit;
	}

	/** うんうんを食べ中 を設定する. @param eatingShit うんうんを食べ中 */
	public void setEatingShit(boolean eatingShit) {
		this.eatingShit = eatingShit;
	}

	/** すっきり中 */
	protected boolean sukkiri = false;

	/** すっきり中 を取得する. @return すっきり中 */
	public boolean isSukkiri() {
		return !dead && sukkiri;
	}

	/** すっきり中 を設定する. @param sukkiri すっきり中 */
	public void setSukkiri(boolean sukkiri) {
		this.sukkiri = sukkiri;
	}

	/** 怖がり中 */
	protected boolean scare = false;

	/** 怖がり中 を取得する. @return 怖がり中 */
	public boolean isScare() {
		return !dead && scare;
	}

	/** 怖がり中 を設定する. @param scare 怖がり中 */
	public void setScare(boolean scare) {
		this.scare = scare;
	}

	/** 怒っているか否か */
	protected boolean angry = false;

	/** 怒っているか否か を取得する. @return 怒っているか否か */
	public boolean isAngry() {
		return !dead && angry;
	}

	/** 怒っているか否か を設定する. @param angry 怒っているか否か */
	public void setAngry(boolean angry) {
		this.angry = angry;
	}

	/** ふりふりアクション中 */
	protected boolean furifuri = false;

	/** ふりふりアクション中 を取得する. @return ふりふりアクション中 */
	public boolean isFurifuri() {
		return !dead && furifuri;
	}

	/** ふりふりアクション中 を設定する. @param furifuri ふりふりアクション中 */
	public void setFurifuri(boolean furifuri) {
		this.furifuri = furifuri;
	}

	/** 攻撃アクション中 */
	protected boolean strike = false;

	/** 攻撃アクション中 を取得する. @return 攻撃アクション中 */
	public boolean isStrike() {
		return !dead && strike;
	}

	/** 攻撃アクション中 を設定する. @param strike 攻撃アクション中 */
	public void setStrike(boolean strike) {
		this.strike = strike;
	}

	/** のびのび中 */
	protected boolean nobinobi = false;

	/** のびのび中 を取得する. @return のびのび中 */
	public boolean isNobinobi() {
		return !dead && nobinobi;
	}

	/** のびのび中 を設定する. @param nobinobi のびのび中 */
	public void setNobinobi(boolean nobinobi) {
		this.nobinobi = nobinobi;
	}

	/** キリッ！中 */
	protected boolean beVain = false;

	/** キリッ！中 を取得する. @return キリッ！中 */
	public boolean isBeVain() {
		return !dead && beVain;
	}

	/** キリッ！中かどうか（isBeVain の別名）. @return キリッ！中かどうか */
	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isVain() {
		return !dead && beVain;
	}

	/** キリッ！中 を設定する. @param beVain キリッ！中 */
	public void setBeVain(boolean beVain) {
		this.beVain = beVain;
	}

	/** ぴこぴこ中 */
	protected boolean pikopiko = false;

	/** ぴこぴこ中 を取得する. @return ぴこぴこ中 */
	public boolean isPikopiko() {
		return pikopiko;
	}

	/** ぴこぴこ中 を設定する. @param pikopiko ぴこぴこ中 */
	public void setPikopiko(boolean pikopiko) {
		this.pikopiko = pikopiko;
	}

	/** ぷるぷる中 */
	protected boolean purupuru = false;

	/** ぷるぷる中 を取得する. @return ぷるぷる中 */
	public boolean isPurupuru() {
		return purupuru;
	}

	/** ぷるぷる中 を設定する. @param purupuru ぷるぷる中 */
	public void setPurupuru(boolean purupuru) {
		this.purupuru = purupuru;
	}

	/** ゆんやぁ中 */
	protected boolean yunnyaa = false;

	/** ゆんやぁ中 を取得する. @return ゆんやぁ中 */
	public boolean isYunnyaa() {
		return !dead && yunnyaa;
	}

	/** ゆんやぁ中 を設定する. @param yunnyaa ゆんやぁ中 */
	public void setYunnyaa(boolean yunnyaa) {
		this.yunnyaa = yunnyaa;
	}

	/** 沈黙フラグ */
	protected boolean silent = false;

	/** 沈黙フラグ を取得する. @return 沈黙フラグ */
	public boolean isSilent() {
		return silent;
	}

	/** 沈黙フラグ を設定する. @param silent 沈黙フラグ */
	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	/** 口ふさがれ中 */
	protected boolean shutmouth = false;

	/** 口ふさがれ中 を取得する. @return 口ふさがれ中 */
	public boolean isShutmouth() {
		return shutmouth;
	}

	/** 口ふさがれ中 を設定する. @param shutmouth 口ふさがれ中 */
	public void setShutmouth(boolean shutmouth) {
		this.shutmouth = shutmouth;
	}

	/** 命乞い中 */
	protected boolean begging = false;

	/** 命乞い中 を取得する. @return 命乞い中 */
	public boolean isBegging() {
		return begging;
	}

	/** 命乞い中（死亡時はfalse） を取得する. @return 命乞い中 */
	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isBeggingForLife() {
		return !dead && begging;
	}

	/** 命乞い中 を設定する. @param begging 命乞い中 */
	public void setBegging(boolean begging) {
		this.begging = begging;
	}

	/** 何で遊んでいるか */
	protected PlayStyle playing = null;

	/** 何で遊んでいるか を取得する. @return 何で遊んでいるか */
	public PlayStyle getPlaying() {
		return playing;
	}

	/** 何で遊んでいるか を設定する. @param playing 何で遊んでいるか */
	public void setPlaying(PlayStyle playing) {
		this.playing = playing;
	}

	/** 遊び時間上限 */
	protected int playingLimit = 0;

	/** 遊び時間上限 を取得する. @return 遊び時間上限 */
	public int getPlayingLimit() {
		return playingLimit;
	}

	/** 遊び時間上限 を設定する. @param playingLimit 遊び時間上限 */
	public void setPlayingLimit(int playingLimit) {
		this.playingLimit = playingLimit;
	}

	/** 遊ぶのをやめる. */
	public void stopPlaying() {
		setPlaying(null);
		setPlayingLimit(0);
	}

	/** ぷるぷるアニメーションの位相(左右揺れのトグル) */
	protected boolean shakePhase = false;

	/** ぷるぷるアニメーションの位相(左右揺れのトグル) を取得する. @return ぷるぷるアニメーションの位相(左右揺れのトグル) */
	public boolean isShakePhase() {
		return shakePhase;
	}

	/**
	 * ぷるぷるアニメーションの位相(左右揺れのトグル) を設定する. @param shakePhase ぷるぷるアニメーションの位相(左右揺れのトグル)
	 */
	public void setShakePhase(boolean shakePhase) {
		this.shakePhase = shakePhase;
	}

	/** 粘着板で背中を固定されている */
	protected boolean fixBack = false;

	/** 粘着板で背中を固定されている を取得する. @return 粘着板で背中を固定されている */
	public boolean isFixBack() {
		return fixBack;
	}

	/** 粘着板で背中を固定されている を設定する. @param fixBack 粘着板で背中を固定されている */
	public void setFixBack(boolean fixBack) {
		this.fixBack = fixBack;
	}

	/** 待機アクション中 */
	protected boolean staying = false;

	/** 待機アクション中 を取得する. @return 待機アクション中 */
	public boolean isStaying() {
		return staying;
	}

	/** 待機アクション中 を設定する. @param staying 待機アクション中 */
	public void setStaying(boolean staying) {
		this.staying = staying;
	}

	/** その場に留まってる回数 */
	protected int stayTicks = 0;

	/** その場に留まってる回数 を取得する. @return その場に留まってる回数 */
	public int getStayTicks() {
		return stayTicks;
	}

	/** その場に留まってる回数 を設定する. @param stayTicks その場に留まってる回数 */
	public void setStayTicks(int stayTicks) {
		this.stayTicks = stayTicks;
	}

	/** とどまる限界 */
	protected int stayTime = Const.STAYLIMIT;

	/** とどまる限界 を取得する. @return とどまる限界 */
	public int getStayTime() {
		return stayTime;
	}

	/** とどまる限界 を設定する. @param stayTime とどまる限界 */
	public void setStayTime(int stayTime) {
		this.stayTime = stayTime;
	}

	/** メッセージのバッファ */
	protected String messageBuffer;

	/** メッセージのバッファ を取得する. @return メッセージのバッファ */
	public String getMessageBuffer() {
		return messageBuffer;
	}

	/** メッセージのバッファ を設定する. @param messageBuffer メッセージのバッファ */
	public void setMessageBuffer(String messageBuffer) {
		this.messageBuffer = messageBuffer;
	}

	/** いくつメッセージが溜まってるか */
	protected int messageTicks = 0;

	/** いくつメッセージが溜まってるか を取得する. @return いくつメッセージが溜まってるか */
	public int getMessageTicks() {
		return messageTicks;
	}

	/** いくつメッセージが溜まってるか を設定する. @param messageTicks いくつメッセージが溜まってるか */
	public void setMessageTicks(int messageTicks) {
		this.messageTicks = messageTicks;
	}

	/** メッセージラインの色 */
	protected Color4y messageLineColor;

	/** メッセージラインの色 を取得する. @return メッセージラインの色 */
	public Color4y getMessageLineColor() {
		return messageLineColor;
	}

	/** メッセージラインの色 を設定する. @param messageLineColor メッセージラインの色 */
	public void setMessageLineColor(Color4y messageLineColor) {
		this.messageLineColor = messageLineColor;
	}

	/** メッセージボックスの色 */
	protected Color4y messageBoxColor;

	/** メッセージボックスの色 を取得する. @return メッセージボックスの色 */
	public Color4y getMessageBoxColor() {
		return messageBoxColor;
	}

	/** メッセージボックスの色 を設定する. @param messageBoxColor メッセージボックスの色 */
	public void setMessageBoxColor(Color4y messageBoxColor) {
		this.messageBoxColor = messageBoxColor;
	}

	/** メッセージテキストの色 */
	protected Color4y messageTextColor;

	/** メッセージテキストの色 を取得する. @return メッセージテキストの色 */
	public Color4y getMessageTextColor() {
		return messageTextColor;
	}

	/** メッセージテキストの色 を設定する. @param messageTextColor メッセージテキストの色 */
	public void setMessageTextColor(Color4y messageTextColor) {
		this.messageTextColor = messageTextColor;
	}

	/** メッセージストローク */
	protected BasicStrokeEX messageWindowStroke;

	/** メッセージストローク を取得する. @return メッセージストローク */
	public BasicStrokeEX getMessageWindowStroke() {
		return messageWindowStroke;
	}

	/** メッセージストローク を設定する. @param messageWindowStroke メッセージストローク */
	public void setMessageWindowStroke(BasicStrokeEX messageWindowStroke) {
		this.messageWindowStroke = messageWindowStroke;
	}

	/** メッセージテキストのサイズ */
	protected int messageTextSize;

	/** メッセージテキストのサイズ を取得する. @return メッセージテキストのサイズ */
	public int getMessageTextSize() {
		return messageTextSize;
	}

	/** メッセージテキストのサイズ を設定する. @param messageTextSize メッセージテキストのサイズ */
	public void setMessageTextSize(int messageTextSize) {
		this.messageTextSize = messageTextSize;
	}

	/** 強制的に誕生時メッセージを言わされるかどうか */
	protected boolean birthMessageForced = false;

	/** 強制的に誕生時メッセージを言わされるかどうか を取得する. @return 強制的に誕生時メッセージを言わされるかどうか */
	public boolean isBirthMessageForced() {
		return birthMessageForced;
	}

	/**
	 * 強制的に誕生時メッセージを言わされるかどうか を設定する. @param birthMessageForced
	 * 強制的に誕生時メッセージを言わされるかどうか
	 */
	public void setBirthMessageForced(boolean birthMessageForced) {
		this.birthMessageForced = birthMessageForced;
	}

	/**
	 * 実ゆの親が存在し、実ゆの状態に気が付けるならそのインスタンスを取得する
	 *
	 * @return 気づいた実ゆの親インスタンス
	 */
	@Transient
	public final int getBindStalkMotherCanNotice() {
		// 実ゆの場合、親が反応する
		if (getBindStalk() != null) {
			int id = getBindStalk().getPlantYukkuri();
			Yukkuri bodyMother = GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(id);
			if (bodyMother != null) {
				if (!bodyMother.isDead() || bodyMother.isSleeping()) {
					return bodyMother.getUniqueID();
				}
			}
		}
		return -1;
	}

	/**
	 * 茎のあるゆっくりの基本反応
	 *
	 * @param state 実ゆの状態
	 */
	public void checkReactionStalkMother(org.simyukkuri.enums.UnbirthBabyState state) {
		Yukkuri bodyMother = GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(getBindStalkMotherCanNotice());
		if (bodyMother == null) {
			return;
		}

		if (bodyMother.isDead() || bodyMother.isSleeping() || bodyMother.isBurned() || bodyMother.isShitting()
				|| bodyMother.isBirth() || bodyMother.nearToBirth()) {
			return;
		}

		// 非ゆっくり症の場合
		if (bodyMother.isNYD()) {
			return;
		}

		bodyMother.onChildStateNotify(state, isDead());
	}

	/** 茎の実ゆの状態を母親が感知したときの感情・メッセージ反応。サブクラスで override して実装する. */
	public void onChildStateNotify(org.simyukkuri.enums.UnbirthBabyState state, boolean childDead) {
	}

	/**
	 * 実ゆ（自分）が茎で生きている親につながっているかを返却する.
	 *
	 * @return 実ゆ（自分）が茎で生きている親につながっているか
	 */
	@Transient
	public final boolean isPlantForUnbirthChild() {
		if (isUnBirth()) {
			// 茎があって親が生きてる
			if (getBindStalk() != null) {
				int id = getBindStalk().getPlantYukkuri();
				Entity oBind = GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(id);
				if (oBind != null && oBind instanceof Yukkuri) {
					Yukkuri bodyBind = (Yukkuri) oBind;
					if (bodyBind != null && !bodyBind.isDead() && !bodyBind.isRemoved()) {
						return true;
					}
				}
			}
			// 救命オレンジプール上にいる
			for (org.simyukkuri.entity.core.world.item.OrangePool pool : GameWorld.get().getCurrentWorldState().getOrangePools()
					.values()) {
				if (!pool.isRescue())
					continue;
				org.simyukkuri.draw.Rectangle4y b = org.simyukkuri.entity.core.world.item.OrangePool.getBounding();
				int halfW = b.getWidth() >> 1;
				int halfH = b.getHeight() >> 1;
				if (Math.abs(getX() - pool.getX()) <= halfW && Math.abs(getY() - pool.getY()) <= halfH) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * ベッドの上にいるかどうかをチェックする.
	 *
	 * @return ベッドの上にいるかどうか
	 */
	public boolean checkOnBed() {
		Rectangle r = takeScreenRect();
		for (java.util.Map.Entry<Integer, Bed> entry : GameWorld.get().getCurrentWorldState().getBeds().entrySet()) {
			Bed bd = entry.getValue();
			if (takeScreenRect(bd.getScreenRect()).intersects(r))
				return true;
		}
		return false;
	}

	private Rectangle takeScreenRect(Rectangle4y screenRect) {
		return new Rectangle(screenRect.getX(), screenRect.getY(), screenRect.getWidth(), screenRect.getHeight());
	}

	private Rectangle takeScreenRect() {
		return new Rectangle(screenRect.getX(), screenRect.getY(), screenRect.getWidth(), screenRect.getHeight());
	}

	/**
	 * 死ねない期間かどうかをチェックする.
	 * TICKで期間を1減らす.
	 */
	public void checkCantDie() {
		bodyConditionDelegate().checkCantDie();
	}

	/** 死ねない期間中かどうかを取得する. */
	@Transient
	public boolean isCantDie() {
		return getCantDiePeriod() > 0;
	}

	/** 茎または腹ではらんでいるかどうかを取得する. */
	@Transient
	public boolean hasBabyOrStalk() {
		return isHasBaby() || isHasStalk();
	}

	/** おかざりがあるかどうかを取得する. */
	@Transient
	public final boolean hasOkazari() {
		return getOkazaris() != null;
	}

	/** 動物（というか現在はアリ一択か）に食べられてるか */
	@Transient
	public boolean isEatenByAnimals() {
		return getAttachmentSize(Ants.class) != 0;
	}

	/** アリを除去する. */
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

	/** ダメージなしかどうかを返却する. */
	@Transient
	public boolean isNoDamaged() {
		return getDamageState() == Damage.NONE;
	}

	/** 現在飛行可能か */
	@Transient
	public final boolean canflyCheck() {
		return (isFlyingType() && isHasBraid() && !isDead() && !isSleeping() && !isNeedled()
				&& getCriticalDamege() == null);
	}

	/**
	 * 移動目的がフードかどうかを取得する.
	 *
	 * @return 移動目的がフードかどうか
	 */
	@JsonIgnore
	public boolean isToFood() {
		return getPurposeOfMoving() == PurposeOfMoving.FOOD;
	}

	/**
	 * checkといいつつ空腹操作をしているメソッド.
	 * ゆっくりの様々な状態に応じて腹を減らしている。
	 */
	public void checkHungry() {
		hungerDelegate().checkHungry();
	}

	/**
	 * 自身が突然変異可能な状態かどうかチェック
	 *
	 * @return 自身が突然変異可能な状態かどうか
	 */
	public boolean canTransform() {
		if (isDead())
			return false;
		if (getStress() > 0)
			return false;
		if (getTangType() == TangType.POOR)
			return false;
		if (isDamaged())
			return false;
		if (isFeelPain())
			return false;
		if (isUnBirth())
			return false;
		if (getPublicRank() == PublicRank.UNUN_SLAVE)
			return false;
		if (isNYD())
			return false;
		if (isBlind() || isPealed() || isPacked() || isShutmouth())
			return false;
		if (getHairState() != HairState.DEFAULT)
			return false;
		return true;
	}

	/**
	 * 行動できる状態かチェックする
	 * ここでは動いたら見た目におかしくなる状況のみチェック
	 *
	 * @return
	 */
	public final boolean canAction() {
		if (isDead() || getCriticalDamege() == CriticalDamageType.CUT || isPealed() ||
				isPacked() || isSleeping() || isShitting() || isBirth() || isSukkiri() || isNeedled() ||
				getCurrentEvent() != null || isNYD() ||
				getBurialState() != BurialState.NONE) {
			return false;
		}
		return true;
	}

	/**
	 * 行動できる状態かチェックする
	 * ここでは動いたら見た目におかしくなる状況のみチェック
	 *
	 * @return
	 */
	public final boolean canActionForEvent() {
		if (isDead() || getCriticalDamege() == CriticalDamageType.CUT || isPealed() ||
				isPacked() || isSleeping() || isShitting() || isBirth() || isSukkiri() || isNeedled() ||
				isNYD() || getBurialState() != BurialState.NONE) {
			return false;
		}
		return true;
	}

	/**
	 * アリ関連の処理.
	 */
	public void checkAnts() {
		if (isCrushed() || GameEnvironment.isEndlessFurifuriSteam()) {
			removeAnts();
			return;
		}
		if (getAttachmentSize(Ants.class) != 0 && getAge() % 5 == 0) {
			antCount += TICK * 2;
			clearEvent();
			return;
		}
		if (GameWorld.get().getCurrentWorldState().getWorldIndex() == 0 || getZ() != 0) {
			return;
		}
		AntInfestationPolicy.judgeNewAnt((Yukkuri) this);
	}

	/** 従者/支配種かどうかを返却する. */
	@Transient
	public boolean isRaper() {
		return false;
	}

	/** ストレスフルかどうかを取得する. */
	@Transient
	public boolean isStressful() {
		return false;
	}

	/** とてもストレスフルかどうかを取得する. */
	@Transient
	public boolean isVeryStressful() {
		return false;
	}

	/** とても悲しいかどうかを取得する. */
	@Transient
	public boolean isVerySad() {
		return false;
	}

	/** 現在Braidがちぎられてないか */
	@Transient
	public final boolean hasBraidCheck() {
		return isHasBraid();
	}

	/** 破裂しているかどうかを取得する. */
	@Transient
	public boolean isBurst() {
		return getBurstState() == Burst.BURST;
	}

	/** まさに破裂するところかどうかを取得する. */
	@Transient
	public boolean isAboutToBurst() {
		return getBurstState() == Burst.NEAR;
	}

	/** 破裂状態が通常でないかどうかを取得する. */
	@Transient
	public boolean isInfration() {
		return getBurstState() != Burst.NONE;
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
		if (getSpriteSet() == null) {
			return 0;
		}
		Sprite spr = getSpriteSet()[getAgeState().ordinal()];
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
		if (getSpriteSet() == null) {
			return 0;
		}
		Sprite spr = getSpriteSet()[getAgeState().ordinal()];
		if (spr == null) {
			return 0;
		}
		return spr.getImageW();
	}

	/**
	 * 妊娠、うんうん、過食などによる横方向の体型のふくらみ取得
	 */
	@Transient
	public int getExpandSizeW() {
		int OE = 100 * hungry / getHungryLimit();
		if (OE <= 5)
			OE = 85;
		else if (OE <= 20) {
			OE += 80;
		} else if (OE <= 100)
			OE = 100;
		return (20 - 20 / (getBabyTypes().size() + 1)) + getBabyTypes().size() * 2
				+ ((shit * 4 / 5) / getShitLimitBase()[getAgeState().ordinal()]) * 5
				+ getSpriteSet()[getAgeState().ordinal()].getImageW() * (OE - 100) / 100
				+ getGodHandHoldCount() / 2;
	}

	/**
	 * 妊娠、うんうんなどによる縦方向の体型のふくらみ取得
	 */
	@Transient
	public int getExpandSizeH() {
		return (20 - 20 / (getBabyTypes().size() + 1)) + getBabyTypes().size() * 2
				+ ((shit * 4 / 5) / getShitLimitBase()[getAgeState().ordinal()]) * 5
				+ getGodHandHoldCount() / 2;
	}

	/**
	 * ひっぱり、押しつぶしによる体型の変形を取得する.
	 *
	 * @return ひっぱり、押しつぶしによる体型の変形
	 */
	@Transient
	public int getExternalForceW() {
		int ret = 0;
		if (externalPressure < 0)
			ret = -externalPressure;
		return ret;
	}

	@Transient
	public int getExternalForceH() {
		int ret = 0;
		if (externalPressure > 0) {
			ret = externalPressure * 6;
		} else if (externalPressure < 0) {
			ret = externalPressure * 2;
		}
		return ret;
	}

	/** 影の表示有無 */
	protected boolean shadowVisible = true;

	/** 影の表示有無 を取得する. @return 影の表示有無 */
	public boolean isShadowVisible() {
		return shadowVisible;
	}

	/** 影の表示有無 を設定する. @param shadowVisible 影の表示有無 */
	public void setShadowVisible(boolean shadowVisible) {
		this.shadowVisible = shadowVisible;
	}

	/** 画像がまりちゃ流しか */
	protected boolean imageNagasiMode = false;

	/** 画像がまりちゃ流しか を取得する. @return 画像がまりちゃ流しか */
	public boolean isImageNagasiMode() {
		return imageNagasiMode;
	}

	/** 画像がまりちゃ流しか を設定する. @param imageNagasiMode 画像がまりちゃ流しか */
	public void setImageNagasiMode(boolean imageNagasiMode) {
		this.imageNagasiMode = imageNagasiMode;
	}

	/** まばたき、同じ表情の時にカウント */
	protected int blinkCount = 0;

	/** まばたき、同じ表情の時にカウント を取得する. @return まばたき、同じ表情の時にカウント */
	public int getBlinkCount() {
		return blinkCount;
	}

	/** まばたき、同じ表情の時にカウント を設定する. @param blinkCount まばたき、同じ表情の時にカウント */
	public void setBlinkCount(int blinkCount) {
		this.blinkCount = blinkCount;
	}

	/** まばたき、表情の値を代入 */
	protected int blinkType = 0;

	/** まばたき、表情の値を代入 を取得する. @return まばたき、表情の値を代入 */
	public int getBlinkType() {
		return blinkType;
	}

	/** まばたき、表情の値を代入 を設定する. @param blinkType まばたき、表情の値を代入 */
	public void setBlinkType(int blinkType) {
		this.blinkType = blinkType;
	}

	/** うにょ機能を使用するかどうかのフラグ */
	protected int unyoMode = 1;

	/** うにょの強さ */
	protected static final int UNYOSTRENGTH[] = { 4, 7, 10 };

	/** うにょ機能を使用するかどうかのフラグ を取得する. @return うにょ機能を使用するかどうかのフラグ */
	public int getUnyoMode() {
		return unyoMode;
	}

	/** うにょ機能を使用するかどうかのフラグ を設定する. @param unyoMode うにょ機能を使用するかどうかのフラグ */
	public void setUnyoMode(int unyoMode) {
		this.unyoMode = unyoMode;
	}

	/** うにょの高さ方向 */
	protected int unyoOffsetH = 0;

	/** うにょの高さ方向 を取得する. @return うにょの高さ方向 */
	public int getUnyoOffsetH() {
		return unyoOffsetH;
	}

	/** うにょの高さ方向 を設定する. @param unyoOffsetH うにょの高さ方向 */
	public void setUnyoOffsetH(int unyoOffsetH) {
		this.unyoOffsetH = unyoOffsetH;
	}

	/** うにょの横方向 */
	protected int unyoOffsetW = 0;

	/** うにょの横方向 を取得する. @return うにょの横方向 */
	public int getUnyoOffsetW() {
		return unyoOffsetW;
	}

	/** うにょの横方向 を設定する. @param unyoOffsetW うにょの横方向 */
	public void setUnyoOffsetW(int unyoOffsetW) {
		this.unyoOffsetW = unyoOffsetW;
	}

	/** この個体に対して発行されたイベントのリスト */
	protected List<EventPacket> eventList = new LinkedList<EventPacket>();

	/** この個体に対して発行されたイベントのリスト を取得する. @return この個体に対して発行されたイベントのリスト */
	public List<EventPacket> getEvents() {
		return eventList;
	}

	/** この個体に対して発行されたイベントのリスト を設定する. @param eventList この個体に対して発行されたイベントのリスト */
	public void setEvents(List<EventPacket> eventList) {
		this.eventList = eventList;
	}

	/** 現在実行中のイベント */
	protected EventPacket currentEvent = null;

	/** 現在実行中のイベント を取得する. @return 現在実行中のイベント */
	public EventPacket getCurrentEvent() {
		return currentEvent;
	}

	/** 現在実行中のイベント を設定する. @param currentEvent 現在実行中のイベント */
	public void setCurrentEvent(EventPacket currentEvent) {
		this.currentEvent = currentEvent;
	}

	/** イベントで設定されたアクション */
	protected TickResult eventResult = TickResult.NONE;

	/** イベントで設定されたアクション を取得する. @return イベントで設定されたアクション */
	public TickResult getEventResult() {
		return eventResult;
	}

	/** イベントで設定されたアクション を設定する. @param eventResult イベントで設定されたアクション */
	public void setEventResult(TickResult eventResult) {
		this.eventResult = eventResult;
	}

	/** ゆっくりのオブジェクトのユニークID */
	protected int uniqueID = 0;

	/** ゆっくりのオブジェクトのユニークID を取得する. @return ゆっくりのオブジェクトのユニークID */
	public int getUniqueID() {
		return uniqueID;
	}

	/** ゆっくりのオブジェクトのユニークID を設定する. @param uniqueID ゆっくりのオブジェクトのユニークID */
	public void setUniqueID(int uniqueID) {
		this.uniqueID = uniqueID;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof LivingEntity)) {
			return false;
		}
		LivingEntity other = (LivingEntity) o;
		return getUniqueID() == other.getUniqueID();
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
		if (!(o instanceof LivingEntity)) {
			return 0;
		}
		LivingEntity other = (LivingEntity) o;
		return getUniqueID() - other.getUniqueID();
	}

	/** どのゆっくり的なメッセージを言うか */
	protected YukkuriType msgType = null;

	/** どのゆっくり的なメッセージを言うか を取得する. @return どのゆっくり的なメッセージを言うか */
	public YukkuriType getMsgType() {
		return msgType;
	}

	/** どのゆっくり的なメッセージを言うか を設定する. @param msgType どのゆっくり的なメッセージを言うか */
	public void setMsgType(YukkuriType msgType) {
		this.msgType = msgType;
	}

	/** どのゆっくり的なうんうんをするか */
	protected YukkuriType shitType = null;

	/** どのゆっくり的なうんうんをするか を取得する. @return どのゆっくり的なうんうんをするか */
	public YukkuriType getShitType() {
		return shitType;
	}

	/** どのゆっくり的なうんうんをするか を設定する. @param shitType どのゆっくり的なうんうんをするか */
	public void setShitType(YukkuriType shitType) {
		this.shitType = shitType;
	}

	/** ゆっくり本体の購入基本額 */
	protected int cost = 200;

	/** ゆっくり本体の購入基本額 を取得する. @return ゆっくり本体の購入基本額 */
	public int getCost() {
		return cost;
	}

	/** ゆっくり本体の購入基本額 を設定する. @param cost ゆっくり本体の購入基本額 */
	public void setCost(int cost) {
		this.cost = cost;
	}

	/** ゆっくり本体、中身の売却基本額 飼いゆとしての価値/加工品としての価値 */
	protected int[] saleValues = { 50, 100 };

	/**
	 * ゆっくり本体、中身の売却基本額 飼いゆとしての価値/加工品としての価値 を取得する. @return ゆっくり本体、中身の売却基本額
	 * 飼いゆとしての価値/加工品としての価値
	 */
	public int[] getSaleValues() {
		return saleValues;
	}

	/**
	 * ゆっくり本体、中身の売却基本額 飼いゆとしての価値/加工品としての価値 を設定する. @param saleValues ゆっくり本体、中身の売却基本額
	 * 飼いゆとしての価値/加工品としての価値
	 */
	public void setSaleValues(int[] saleValues) {
		this.saleValues = saleValues;
	}

	/** 「取る」で取られているかどうか */
	protected boolean taken = false;

	/** 「取る」で取られているかどうか を取得する. @return 「取る」で取られているかどうか */
	public boolean isTaken() {
		return taken;
	}

	/** 「取る」で取られているかどうか を設定する. @param taken 「取る」で取られているかどうか */
	public void setTaken(boolean taken) {
		this.taken = taken;
	}

	/** 右ペインメニューのピン留めをされているかどうか */
	protected boolean isPinned = false;

	/** 右ペインメニューのピン留めをされているかどうか を取得する. @return 右ペインメニューのピン留めをされているかどうか */
	public boolean isPinned() {
		return isPinned;
	}

	/** 右ペインメニューのピン留めをされているかどうか を設定する. @param isPinned 右ペインメニューのピン留めをされているかどうか */
	public void setPinned(boolean isPinned) {
		this.isPinned = isPinned;
	}

	/** プレイヤーにすりすりされているか */
	protected boolean surisuriFromPlayer = false;

	/** プレイヤーにすりすりされているか を取得する. @return プレイヤーにすりすりされているか */
	public boolean isSurisuriFromPlayer() {
		return surisuriFromPlayer;
	}

	/** プレイヤーにすりすりされているか を設定する. @param surisuriFromPlayer プレイヤーにすりすりされているか */
	public void setSurisuriFromPlayer(boolean surisuriFromPlayer) {
		this.surisuriFromPlayer = surisuriFromPlayer;
	}

	/** 最後にプレイヤーにすりすりしてもらった時間 */
	protected long lastSurisuriTime = 0;

	/** 最後にプレイヤーにすりすりしてもらった時間 を取得する. @return 最後にプレイヤーにすりすりしてもらった時間 */
	public long getLastSurisuriTime() {
		return lastSurisuriTime;
	}

	/**
	 * 最後にプレイヤーにすりすりしてもらった時間 を設定する. @param lastSurisuriTime 最後にプレイヤーにすりすりしてもらった時間
	 */
	public void setLastSurisuriTime(long lastSurisuriTime) {
		this.lastSurisuriTime = lastSurisuriTime;
	}

	/** アイテムを出し入れする動作フラグ */
	protected boolean inOutTakeoutItem = false;

	/** アイテムを出し入れする動作フラグ を取得する. @return アイテムを出し入れする動作フラグ */
	public boolean isInOutTakeoutItem() {
		return inOutTakeoutItem;
	}

	/** アイテムを出し入れする動作フラグ を設定する. @param inOutTakeoutItem アイテムを出し入れする動作フラグ */
	public void setInOutTakeoutItem(boolean inOutTakeoutItem) {
		this.inOutTakeoutItem = inOutTakeoutItem;
	}

	/** ゆ虐神拳により 膨らんでいるか/伸ばされているか/押さえつけられているか */
	protected boolean[] abFlagGodHand = { false, false, false };

	/**
	 * ゆ虐神拳により 膨らんでいるか/伸ばされているか/押さえつけられているか を取得する. @return ゆ虐神拳により
	 * 膨らんでいるか/伸ばされているか/押さえつけられているか
	 */
	public boolean[] getAbFlagGodHand() {
		return abFlagGodHand;
	}

	/**
	 * ゆ虐神拳により 膨らんでいるか/伸ばされているか/押さえつけられているか を設定する. @param abFlagGodHand ゆ虐神拳により
	 * 膨らんでいるか/伸ばされているか/押さえつけられているか
	 */
	public void setAbFlagGodHand(boolean[] abFlagGodHand) {
		this.abFlagGodHand = abFlagGodHand;
	}

	/** ゆ虐神拳の回数 */
	protected int godHandHoldCount = 0;

	/** ゆ虐神拳の回数 を取得する. @return ゆ虐神拳の回数 */
	public int getGodHandHoldCount() {
		return godHandHoldCount;
	}

	/** ゆ虐神拳の回数 を設定する. @param godHandHoldCount ゆ虐神拳の回数 */
	public void setGodHandHoldCount(int godHandHoldCount) {
		this.godHandHoldCount = godHandHoldCount;
	}

	/** ゆ虐神拳の伸ばし回数 */
	protected int godHandStretchCount = 0;

	/** ゆ虐神拳の伸ばし回数 を取得する. @return ゆ虐神拳の伸ばし回数 */
	public int getGodHandStretchCount() {
		return godHandStretchCount;
	}

	/** ゆ虐神拳の伸ばし回数 を設定する. @param godHandStretchCount ゆ虐神拳の伸ばし回数 */
	public void setGodHandStretchCount(int godHandStretchCount) {
		this.godHandStretchCount = godHandStretchCount;
	}

	/** ゆ虐神拳の押しつぶし回数 */
	protected int godHandCompressCount = 0;

	/** ゆ虐神拳の押しつぶし回数 を取得する. @return ゆ虐神拳の押しつぶし回数 */
	public int getGodHandCompressCount() {
		return godHandCompressCount;
	}

	/** ゆ虐神拳の押しつぶし回数 を設定する. @param godHandCompressCount ゆ虐神拳の押しつぶし回数 */
	public void setGodHandCompressCount(int godHandCompressCount) {
		this.godHandCompressCount = godHandCompressCount;
	}

	/** たかっているアリの数 */
	protected int antCount = 0;

	/** たかっているアリの数 を取得する. @return たかっているアリの数 */
	public int getAntCount() {
		return antCount;
	}

	/** たかっているアリの数 を設定する. @param antCount たかっているアリの数 */
	public void setAntCount(int antCount) {
		this.antCount = antCount;
	}

	/** 移動不可ベルトコンベアの有無 */
	protected boolean nonMovingConveyor = false;

	/** 移動不可ベルトコンベアの有無 を取得する. @return 移動不可ベルトコンベアの有無 */
	public boolean isNonMovingConveyor() {
		return nonMovingConveyor;
	}

	/** 移動不可ベルトコンベアの有無 を取得する. @return 移動不可ベルトコンベアの有無 */
	public final boolean isOnNonMovingConveyor() {
		return isNonMovingConveyor();
	}

	/** 移動不可ベルトコンベアの有無 を設定する. @param nonMovingConveyor 移動不可ベルトコンベアの有無 */
	public void setNonMovingConveyor(boolean nonMovingConveyor) {
		this.nonMovingConveyor = nonMovingConveyor;
	}

	/** 移動不可ベルトコンベアの有無 を設定する. @param nonMovingConveyor 移動不可ベルトコンベアの有無 */
	public final void setOnNonMovingConveyor(boolean nonMovingConveyor) {
		setNonMovingConveyor(nonMovingConveyor);
	}

	/** 最後にプレイヤーがアクションを行った時間 */
	protected long lastActionTime = 0;

	/** 最後にプレイヤーがアクションを行った時間 を取得する. @return 最後にプレイヤーがアクションを行った時間 */
	public long getLastActionTime() {
		return lastActionTime;
	}

	/** 最後にプレイヤーがアクションを行った時間 を設定する. @param lastActionTime 最後にプレイヤーがアクションを行った時間 */
	public void setLastActionTime(long lastActionTime) {
		this.lastActionTime = lastActionTime;
	}

	/** 最後にプレイヤーがアクションを行った時間を現在時刻で更新する. */
	public void setLastActionTime() {
		this.lastActionTime = System.currentTimeMillis();
	}

	/** 壁に引っかかった回数 */
	protected int blockedTicks = 0;

	/** 壁に引っかかった回数 を取得する. @return 壁に引っかかった回数 */
	public int getBlockedTicks() {
		return blockedTicks;
	}

	/** 壁に引っかかった回数 を設定する. @param blockedTicks 壁に引っかかった回数 */
	public void setBlockedTicks(int blockedTicks) {
		this.blockedTicks = blockedTicks;
	}

	/** 出産期間のブースト（この分だけ早まる） */
	protected int pregnancyPeriodBoost = 0;

	/** 出産期間のブースト（この分だけ早まる） を取得する. @return 出産期間のブースト（この分だけ早まる） */
	public int getPregnancyPeriodBoost() {
		return pregnancyPeriodBoost;
	}

	/**
	 * 出産期間のブースト（この分だけ早まる） を設定する. @param pregnancyPeriodBoost 出産期間のブースト（この分だけ早まる）
	 */
	public void setPregnancyPeriodBoost(int pregnancyPeriodBoost) {
		this.pregnancyPeriodBoost = pregnancyPeriodBoost;
	}

	/** 発情期間のブースト（この分だけ早まる） */
	protected int excitementPeriodBoost = 0;

	/** 発情期間のブースト（この分だけ早まる） を取得する. @return 発情期間のブースト（この分だけ早まる） */
	public int getExcitementPeriodBoost() {
		return excitementPeriodBoost;
	}

	/**
	 * 発情期間のブースト（この分だけ早まる） を設定する. @param excitementPeriodBoost 発情期間のブースト（この分だけ早まる）
	 */
	public void setExcitementPeriodBoost(int excitementPeriodBoost) {
		this.excitementPeriodBoost = excitementPeriodBoost;
	}

	/** うんうんブースト */
	protected int excretionBoost = 0;

	/** うんうんブースト を取得する. @return うんうんブースト */
	public int getExcretionBoost() {
		return excretionBoost;
	}

	/** うんうんブースト を設定する. @param excretionBoost うんうんブースト */
	public void setExcretionBoost(int excretionBoost) {
		this.excretionBoost = excretionBoost;
	}

	/** うまれて初めての地面か */
	protected boolean firstGround = true;

	/** うまれて初めての地面か を取得する. @return うまれて初めての地面か */
	public boolean isFirstGround() {
		return firstGround;
	}

	/** うまれて初めての地面か を設定する. @param firstGround うまれて初めての地面か */
	public void setFirstGround(boolean firstGround) {
		this.firstGround = firstGround;
	}

	/** うまれて初めての食事か */
	protected boolean firstEatStalk = true;

	/** うまれて初めての食事か を取得する. @return うまれて初めての食事か */
	public boolean isFirstEatStalk() {
		return firstEatStalk;
	}

	/** うまれて初めての食事か を設定する. @param firstEatStalk うまれて初めての食事か */
	public void setFirstEatStalk(boolean firstEatStalk) {
		this.firstEatStalk = firstEatStalk;
	}

	/** 動けないかどうか */
	protected boolean lockmove = false;

	/** 動けないかどうか を取得する. @return 動けないかどうか */
	public boolean isLockmove() {
		return lockmove;
	}

	/** 動けないかどうか を設定する. @param lockmove 動けないかどうか */
	public void setLockmove(boolean lockmove) {
		this.lockmove = lockmove;
	}

	/** 動けない期間（押さえられてる等で） */
	protected int lockmovePeriod = 0;

	/** 動けない期間（押さえられてる等で） を取得する. @return 動けない期間（押さえられてる等で） */
	public int getLockmovePeriod() {
		return lockmovePeriod;
	}

	/** 動けない期間（押さえられてる等で） を設定する. @param lockmovePeriod 動けない期間（押さえられてる等で） */
	public void setLockmovePeriod(int lockmovePeriod) {
		this.lockmovePeriod = lockmovePeriod;
	}

	/** 喋れる状態かどうか */
	protected boolean canTalk = true;

	/** 喋れる状態かどうか を取得する. @return 喋れる状態かどうか */
	public boolean isCanTalk() {
		return canTalk;
	}

	/** 喋れる状態かどうか を設定する. @param canTalk 喋れる状態かどうか */
	public void setCanTalk(boolean canTalk) {
		this.canTalk = canTalk;
	}

	// --- BodyStatProfile fields ---
	/** 一回の食事量（年齢別） */
	protected int[] eatAmountBase = { 100 * 6, 100 * 12, 100 * 24 };

	/** 一回の食事量（年齢別） を取得する. @return 一回の食事量（年齢別） */
	public int[] getEatAmountBase() {
		return eatAmountBase;
	}

	/** 一回の食事量（年齢別） を設定する. @param eatAmountBase 一回の食事量（年齢別） */
	public void setEatAmountBase(int[] eatAmountBase) {
		this.eatAmountBase = eatAmountBase;
	}

	/** 体重（年齢別） */
	protected int[] weightBase = { 100, 300, 600 };

	/** 体重（年齢別） を取得する. @return 体重（年齢別） */
	public int[] getWeightBase() {
		return weightBase;
	}

	/** 体重（年齢別） を設定する. @param weightBase 体重（年齢別） */
	public void setWeightBase(int[] weightBase) {
		this.weightBase = weightBase;
	}

	/** 空腹限界（年齢別） */
	protected int[] hungryLimitBase = { 100 * 24, 100 * 24 * 2, 100 * 24 * 4 };

	/** 空腹限界（年齢別） を取得する. @return 空腹限界（年齢別） */
	public int[] getHungryLimitBase() {
		return hungryLimitBase;
	}

	/** 空腹限界（年齢別） を設定する. @param hungryLimitBase 空腹限界（年齢別） */
	public void setHungryLimitBase(int[] hungryLimitBase) {
		this.hungryLimitBase = hungryLimitBase;
	}

	/** うんうん限界（年齢別） */
	protected int[] shitLimitBase = { 100 * 12, 100 * 24, 100 * 24 };

	/** うんうん限界（年齢別） を取得する. @return うんうん限界（年齢別） */
	public int[] getShitLimitBase() {
		return shitLimitBase;
	}

	/** うんうん限界（年齢別） を設定する. @param shitLimitBase うんうん限界（年齢別） */
	public void setShitLimitBase(int[] shitLimitBase) {
		this.shitLimitBase = shitLimitBase;
	}

	/** ダメージ限界（年齢別） */
	protected int[] damageLimitBase = { 100 * 24, 100 * 24 * 3, 100 * 24 * 7 };

	/** ダメージ限界（年齢別） を取得する. @return ダメージ限界（年齢別） */
	public int[] getDamageLimitBase() {
		return damageLimitBase;
	}

	/** ダメージ限界（年齢別） を設定する. @param damageLimitBase ダメージ限界（年齢別） */
	public void setDamageLimitBase(int[] damageLimitBase) {
		this.damageLimitBase = damageLimitBase;
	}

	/** ストレス限界（年齢別） */
	protected int[] stressLimitBase = { 100 * 24, 100 * 24 * 3, 100 * 24 * 7 };

	/** ストレス限界（年齢別） を取得する. @return ストレス限界（年齢別） */
	public int[] getStressLimitBase() {
		return stressLimitBase;
	}

	/** ストレス限界（年齢別） を設定する. @param stressLimitBase ストレス限界（年齢別） */
	public void setStressLimitBase(int[] stressLimitBase) {
		this.stressLimitBase = stressLimitBase;
	}

	/** 味覚レベル（年齢別） */
	protected int[] tangLevelBase = { 300, 600, 1000 };

	/** 味覚レベル（年齢別） を取得する. @return 味覚レベル（年齢別） */
	public int[] getTangLevelBase() {
		return tangLevelBase;
	}

	/** 味覚レベル（年齢別） を設定する. @param tangLevelBase 味覚レベル（年齢別） */
	public void setTangLevelBase(int[] tangLevelBase) {
		this.tangLevelBase = tangLevelBase;
	}

	/** 足の速さ（年齢別） */
	protected int[] stepBase = { 1, 2, 4 };

	/** 足の速さ（年齢別） を取得する. @return 足の速さ（年齢別） */
	public int[] getStepBase() {
		return stepBase;
	}

	/** 足の速さ（年齢別） を設定する. @param stepBase 足の速さ（年齢別） */
	public void setStepBase(int[] stepBase) {
		this.stepBase = stepBase;
	}

	/** 攻撃力（年齢別） */
	protected int[] strengthBase = { 500, 1000, 3000 };

	/** 攻撃力（年齢別） を取得する. @return 攻撃力（年齢別） */
	public int[] getStrengthBase() {
		return strengthBase;
	}

	/** 攻撃力（年齢別） を設定する. @param strengthBase 攻撃力（年齢別） */
	public void setStrengthBase(int[] strengthBase) {
		this.strengthBase = strengthBase;
	}

	/** 免疫力（年齢別、老ゆ含む） */
	protected int[] immunity = { 1, 2, 3, 0 };

	/** 免疫力（年齢別、老ゆ含む） を取得する. @return 免疫力（年齢別、老ゆ含む） */
	public int[] getImmunity() {
		return immunity;
	}

	/** 免疫力（年齢別、老ゆ含む） を設定する. @param immunity 免疫力（年齢別、老ゆ含む） */
	public void setImmunity(int[] immunity) {
		this.immunity = immunity;
	}

	/** ゲスポイントの下限 */
	protected int[] rudeLimit = { -100, -250 };

	/** ゲスポイントの下限 を取得する. @return ゲスポイントの下限 */
	public int[] getRudeLimit() {
		return rudeLimit;
	}

	/** ゲスポイントの下限 を設定する. @param rudeLimit ゲスポイントの下限 */
	public void setRudeLimit(int[] rudeLimit) {
		this.rudeLimit = rudeLimit;
	}

	/** 善良限界 */
	protected int[] niceLimit = { 100, 500 };

	/** 善良限界 を取得する. @return 善良限界 */
	public int[] getNiceLimit() {
		return niceLimit;
	}

	/** 善良限界 を設定する. @param niceLimit 善良限界 */
	public void setNiceLimit(int[] niceLimit) {
		this.niceLimit = niceLimit;
	}

	/** 自主洗浄失敗確率（賢い個体・年齢別） */
	protected int[] cleaningFailProbWise = { 10, 5, 2 };

	/** 自主洗浄失敗確率（賢い個体・年齢別） を取得する. @return 自主洗浄失敗確率（賢い個体・年齢別） */
	public int[] getCleaningFailProbWise() {
		return cleaningFailProbWise;
	}

	/** 自主洗浄失敗確率（賢い個体・年齢別） を設定する. @param cleaningFailProbWise 自主洗浄失敗確率（賢い個体・年齢別） */
	public void setCleaningFailProbWise(int[] cleaningFailProbWise) {
		this.cleaningFailProbWise = cleaningFailProbWise;
	}

	/** 自主洗浄失敗確率（普通個体・年齢別） */
	protected int[] cleaningFailProbAverage = { 25, 8, 3 };

	/** 自主洗浄失敗確率（普通個体・年齢別） を取得する. @return 自主洗浄失敗確率（普通個体・年齢別） */
	public int[] getCleaningFailProbAverage() {
		return cleaningFailProbAverage;
	}

	/**
	 * 自主洗浄失敗確率（普通個体・年齢別） を設定する. @param cleaningFailProbAverage 自主洗浄失敗確率（普通個体・年齢別）
	 */
	public void setCleaningFailProbAverage(int[] cleaningFailProbAverage) {
		this.cleaningFailProbAverage = cleaningFailProbAverage;
	}

	/** 自主洗浄失敗確率（餡子脳個体・年齢別） */
	protected int[] cleaningFailProbFool = { 50, 10, 5 };

	/** 自主洗浄失敗確率（餡子脳個体・年齢別） を取得する. @return 自主洗浄失敗確率（餡子脳個体・年齢別） */
	public int[] getCleaningFailProbFool() {
		return cleaningFailProbFool;
	}

	/**
	 * 自主洗浄失敗確率（餡子脳個体・年齢別） を設定する. @param cleaningFailProbFool 自主洗浄失敗確率（餡子脳個体・年齢別）
	 */
	public void setCleaningFailProbFool(int[] cleaningFailProbFool) {
		this.cleaningFailProbFool = cleaningFailProbFool;
	}

	// --- BodyTimingProfile fields ---
	/** 赤ゆ期間 */
	protected int babyLimitBase = 100 * 24 * 7;

	/** 赤ゆ期間 を取得する. @return 赤ゆ期間 */
	public int getBabyLimitBase() {
		return babyLimitBase;
	}

	/** 赤ゆ期間 を設定する. @param babyLimitBase 赤ゆ期間 */
	public void setBabyLimitBase(int babyLimitBase) {
		this.babyLimitBase = babyLimitBase;
	}

	/** 子ゆ期間 */
	protected int childLimitBase = 100 * 24 * 21;

	/** 子ゆ期間 を取得する. @return 子ゆ期間 */
	public int getChildLimitBase() {
		return childLimitBase;
	}

	/** 子ゆ期間 を設定する. @param childLimitBase 子ゆ期間 */
	public void setChildLimitBase(int childLimitBase) {
		this.childLimitBase = childLimitBase;
	}

	/** 寿命 */
	protected int lifeLimitBase = 100 * 24 * 365;

	/** 寿命 を取得する. @return 寿命 */
	public int getLifeLimitBase() {
		return lifeLimitBase;
	}

	/** 寿命 を設定する. @param lifeLimitBase 寿命 */
	public void setLifeLimitBase(int lifeLimitBase) {
		this.lifeLimitBase = lifeLimitBase;
	}

	/** 腐敗日数 */
	protected int rottingTimeBase = 100 * 24 * 3;

	/** 腐敗日数 を取得する. @return 腐敗日数 */
	public int getRottingTimeBase() {
		return rottingTimeBase;
	}

	/** 腐敗日数 を設定する. @param rottingTimeBase 腐敗日数 */
	public void setRottingTimeBase(int rottingTimeBase) {
		this.rottingTimeBase = rottingTimeBase;
	}

	/** リラックス状態の期間 */
	protected int relaxPeriodBase = 100 * 1;

	/** リラックス状態の期間 を取得する. @return リラックス状態の期間 */
	public int getRelaxPeriodBase() {
		return relaxPeriodBase;
	}

	/** リラックス状態の期間 を設定する. @param relaxPeriodBase リラックス状態の期間 */
	public void setRelaxPeriodBase(int relaxPeriodBase) {
		this.relaxPeriodBase = relaxPeriodBase;
	}

	/** 発情状態の期間 */
	protected int excitePeriodBase = 100 * 3;

	/** 発情状態の期間 を取得する. @return 発情状態の期間 */
	public int getExcitePeriodBase() {
		return excitePeriodBase;
	}

	/** 発情状態の期間 を設定する. @param excitePeriodBase 発情状態の期間 */
	public void setExcitePeriodBase(int excitePeriodBase) {
		this.excitePeriodBase = excitePeriodBase;
	}

	/** 妊娠期間 */
	protected int pregPeriodBase = 100 * 24;

	/** 妊娠期間 を取得する. @return 妊娠期間 */
	public int getPregPeriodBase() {
		return pregPeriodBase;
	}

	/** 妊娠期間 を設定する. @param pregPeriodBase 妊娠期間 */
	public void setPregPeriodBase(int pregPeriodBase) {
		this.pregPeriodBase = pregPeriodBase;
	}

	/** 睡眠時間 */
	protected int sleepPeriodBase = 100 * 3;

	/** 睡眠時間 を取得する. @return 睡眠時間 */
	public int getSleepPeriodBase() {
		return sleepPeriodBase;
	}

	/** 睡眠時間 を設定する. @param sleepPeriodBase 睡眠時間 */
	public void setSleepPeriodBase(int sleepPeriodBase) {
		this.sleepPeriodBase = sleepPeriodBase;
	}

	/** アクティブな期間 */
	protected int activePeriodBase = 100 * 6;

	/** アクティブな期間 を取得する. @return アクティブな期間 */
	public int getActivePeriodBase() {
		return activePeriodBase;
	}

	/** アクティブな期間 を設定する. @param activePeriodBase アクティブな期間 */
	public void setActivePeriodBase(int activePeriodBase) {
		this.activePeriodBase = activePeriodBase;
	}

	/** 怒り期間 */
	protected int angryPeriodBase = 100 * 1;

	/** 怒り期間 を取得する. @return 怒り期間 */
	public int getAngryPeriodBase() {
		return angryPeriodBase;
	}

	/** 怒り期間 を設定する. @param angryPeriodBase 怒り期間 */
	public void setAngryPeriodBase(int angryPeriodBase) {
		this.angryPeriodBase = angryPeriodBase;
	}

	/** 恐怖期間 */
	protected int scarePeriodBase = 100 * 1;

	/** 恐怖期間 を取得する. @return 恐怖期間 */
	public int getScarePeriodBase() {
		return scarePeriodBase;
	}

	/** 恐怖期間 を設定する. @param scarePeriodBase 恐怖期間 */
	public void setScarePeriodBase(int scarePeriodBase) {
		this.scarePeriodBase = scarePeriodBase;
	}

	/** 衝動抑制期間 */
	protected int declinePeriodBase = 20;

	/** 衝動抑制期間 を取得する. @return 衝動抑制期間 */
	public int getDeclinePeriodBase() {
		return declinePeriodBase;
	}

	/** 衝動抑制期間 を設定する. @param declinePeriodBase 衝動抑制期間 */
	public void setDeclinePeriodBase(int declinePeriodBase) {
		this.declinePeriodBase = declinePeriodBase;
	}

	/** 壁等にブロックされた回数の限界 */
	protected int blockedLimitBase = 60;

	/** 壁等にブロックされた回数の限界 を取得する. @return 壁等にブロックされた回数の限界 */
	public int getBlockedLimitBase() {
		return blockedLimitBase;
	}

	/** 壁等にブロックされた回数の限界 を設定する. @param blockedLimitBase 壁等にブロックされた回数の限界 */
	public void setBlockedLimitBase(int blockedLimitBase) {
		this.blockedLimitBase = blockedLimitBase;
	}

	/** 汚れ限界（超えるとゆかび状態） */
	protected int dirtyPeriodBase = 300;

	/** 汚れ限界（超えるとゆかび状態） を取得する. @return 汚れ限界（超えるとゆかび状態） */
	public int getDirtyPeriodBase() {
		return dirtyPeriodBase;
	}

	/** 汚れ限界（超えるとゆかび状態） を設定する. @param dirtyPeriodBase 汚れ限界（超えるとゆかび状態） */
	public void setDirtyPeriodBase(int dirtyPeriodBase) {
		this.dirtyPeriodBase = dirtyPeriodBase;
	}

	/** 視界の広さ（二乗距離） */
	protected int eyesightBase = 4000 * 4000;

	/** 視界の広さ（二乗距離） を取得する. @return 視界の広さ（二乗距離） */
	public int getEyesightBase() {
		return eyesightBase;
	}

	/** 視界の広さ（二乗距離） を設定する. @param eyesightBase 視界の広さ（二乗距離） */
	public void setEyesightBase(int eyesightBase) {
		this.eyesightBase = eyesightBase;
	}

	/** ゆかびの潜伏期間 */
	protected int incubationPeriodBase = 100 * 12;

	/** ゆかびの潜伏期間 を取得する. @return ゆかびの潜伏期間 */
	public int getIncubationPeriodBase() {
		return incubationPeriodBase;
	}

	/** ゆかびの潜伏期間 を設定する. @param incubationPeriodBase ゆかびの潜伏期間 */
	public void setIncubationPeriodBase(int incubationPeriodBase) {
		this.incubationPeriodBase = incubationPeriodBase;
	}

	// --- BodyBehaviorProfile fields ---
	/** なつき度限界 */
	protected int lovePlayerLimitBase = 1000;

	/** なつき度限界 を取得する. @return なつき度限界 */
	public int getLovePlayerLimitBase() {
		return lovePlayerLimitBase;
	}

	/** なつき度限界 を設定する. @param lovePlayerLimitBase なつき度限界 */
	public void setLovePlayerLimitBase(int lovePlayerLimitBase) {
		this.lovePlayerLimitBase = lovePlayerLimitBase;
	}

	/** おさげ破壊確率 */
	protected int braidBreakChance = 0;

	/** おさげ破壊確率 を取得する. @return おさげ破壊確率 */
	public int getBraidBreakChance() {
		return braidBreakChance;
	}

	/** おさげ破壊確率 を設定する. @param braidBreakChance おさげ破壊確率 */
	public void setBraidBreakChance(int braidBreakChance) {
		this.braidBreakChance = braidBreakChance;
	}

	/** すりすり事故妊娠確率（N回に1回） */
	protected int surisuriAccidentProb = 200;

	/** すりすり事故妊娠確率（N回に1回） を取得する. @return すりすり事故妊娠確率（N回に1回） */
	public int getSurisuriAccidentProb() {
		return surisuriAccidentProb;
	}

	/** すりすり事故妊娠確率（N回に1回） を設定する. @param surisuriAccidentProb すりすり事故妊娠確率（N回に1回） */
	public void setSurisuriAccidentProb(int surisuriAccidentProb) {
		this.surisuriAccidentProb = surisuriAccidentProb;
	}

	/** 車轢き確率（N回に1回） */
	protected int carAccidentProb = 10000;

	/** 車轢き確率（N回に1回） を取得する. @return 車轢き確率（N回に1回） */
	public int getCarAccidentProb() {
		return carAccidentProb;
	}

	/** 車轢き確率（N回に1回） を設定する. @param carAccidentProb 車轢き確率（N回に1回） */
	public void setCarAccidentProb(int carAccidentProb) {
		this.carAccidentProb = carAccidentProb;
	}

	/** うんうんによるあんよ破壊確率（N回に1回） */
	protected int breakBodyByShitProb = 100;

	/** うんうんによるあんよ破壊確率（N回に1回） を取得する. @return うんうんによるあんよ破壊確率（N回に1回） */
	public int getBreakByShitProb() {
		return breakBodyByShitProb;
	}

	/**
	 * うんうんによるあんよ破壊確率（N回に1回） を設定する. @param breakBodyByShitProb うんうんによるあんよ破壊確率（N回に1回）
	 */
	public void setBreakByShitProb(int breakBodyByShitProb) {
		this.breakBodyByShitProb = breakBodyByShitProb;
	}

	/** 苦いフードでゆ下痢になる確率（N回に1回） */
	protected int diarrheaProb = 5;

	/** 苦いフードでゆ下痢になる確率（N回に1回） を取得する. @return 苦いフードでゆ下痢になる確率（N回に1回） */
	public int getDiarrheaProb() {
		return diarrheaProb;
	}

	/** 苦いフードでゆ下痢になる確率（N回に1回） を設定する. @param diarrheaProb 苦いフードでゆ下痢になる確率（N回に1回） */
	public void setDiarrheaProb(int diarrheaProb) {
		this.diarrheaProb = diarrheaProb;
	}

	/** 発情確率（N回に1回） */
	protected int exciteProb = 1;

	/** 発情確率（N回に1回） を取得する. @return 発情確率（N回に1回） */
	public int getExciteProb() {
		return exciteProb;
	}

	/** 発情確率（N回に1回） を設定する. @param exciteProb 発情確率（N回に1回） */
	public void setExciteProb(int exciteProb) {
		this.exciteProb = exciteProb;
	}

	/** 性格変化の切り替え */
	protected boolean notChangeCharacter = false;

	/** 性格変化の切り替え を取得する. @return 性格変化の切り替え */
	public boolean isNotChangeCharacter() {
		return notChangeCharacter;
	}

	/** 性格変化の切り替え を設定する. @param notChangeCharacter 性格変化の切り替え */
	public void setNotChangeCharacter(boolean notChangeCharacter) {
		this.notChangeCharacter = notChangeCharacter;
	}

	/** ゲスポイント */
	protected int attitudePoint = 0;

	/** ゲスポイント を取得する. @return ゲスポイント */
	public int getAttitudePoint() {
		return attitudePoint;
	}

	/** ゲスポイント を設定する. @param attitudePoint ゲスポイント */
	public void setAttitudePoint(int attitudePoint) {
		this.attitudePoint = attitudePoint;
	}

	/** 同一方向移動継続係数 */
	protected int sameDirectionFactor = 30;

	/** 同一方向移動継続係数 を取得する. @return 同一方向移動継続係数 */
	public int getSameDirectionFactor() {
		return sameDirectionFactor;
	}

	/** 同一方向移動継続係数 を設定する. @param sameDirectionFactor 同一方向移動継続係数 */
	public void setSameDirectionFactor(int sameDirectionFactor) {
		this.sameDirectionFactor = sameDirectionFactor;
	}

	/** 固有の免疫力（個体値） */
	protected int immunityStrength = 1;

	/** 固有の免疫力（個体値） を取得する. @return 固有の免疫力（個体値） */
	public int getImmunityStrength() {
		return immunityStrength;
	}

	/** 固有の免疫力（個体値） を設定する. @param immunityStrength 固有の免疫力（個体値） */
	public void setImmunityStrength(int immunityStrength) {
		this.immunityStrength = immunityStrength;
	}

	/** 妊娠限界 */
	protected int pregnantLimit = 1000;

	/** 妊娠限界 を取得する. @return 妊娠限界 */
	public int getPregnantLimit() {
		return pregnantLimit;
	}

	/** 妊娠限界 を設定する. @param pregnantLimit 妊娠限界 */
	public void setPregnantLimit(int pregnantLimit) {
		this.pregnantLimit = pregnantLimit;
	}

	/**
	 * 妊娠限界かどうか
	 *
	 * @return 妊娠限界かどうか
	 */
	@JsonIgnore
	public boolean isOverPregnantLimit() {
		if (isUseRealPregnantLimit()) {
			if (getPregnantLimit() <= 0) {
				return GameRandom.nextInt(20) != 0;
			}
			int tarinaiFactor = getPregnantLimit() > 100 ? 100 : getPregnantLimit();
			return GameRandom.nextInt(tarinaiFactor) == 0;
		}
		return getPregnantLimit() <= 0;
	}

	/**
	 * 妊娠限界を一つ早める.
	 * すでに妊娠限界の場合は何もしない.
	 */
	public void subtractPregnantLimit() {
		if (pregnantLimit > 0) {
			pregnantLimit--;
		} else if (pregnantLimit < 0) {
			pregnantLimit = 0;
		}
	}

	/**
	 * 生まれてる赤ゆの数から、最大で何人まで持てるかを取得する.
	 *
	 * @return 最大子数
	 */
	@JsonIgnore
	public int getMaxHaveBaby() {
		return getDamageLimit() / 300;
	}

	/** よりリアルな妊娠限界を使用するか */
	protected boolean useRealPregnantLimit = true;

	/** よりリアルな妊娠限界を使用するか を取得する. @return よりリアルな妊娠限界を使用するか */
	public boolean isUseRealPregnantLimit() {
		return useRealPregnantLimit;
	}

	/** よりリアルな妊娠限界を使用するか を設定する. @param useRealPregnantLimit よりリアルな妊娠限界を使用するか */
	public void setUseRealPregnantLimit(boolean useRealPregnantLimit) {
		this.useRealPregnantLimit = useRealPregnantLimit;
	}

	// --- BodySpriteSet fields ---
	/** 本体のスプライト定義（年齢別） */
	protected Sprite[] bodySpr = new Sprite[3];

	/** 本体のスプライト定義（年齢別） を取得する. @return 本体のスプライト定義（年齢別） */
	public Sprite[] getSpriteSet() {
		return bodySpr;
	}

	/** 本体のスプライト定義（年齢別） を設定する. @param bodySpr 本体のスプライト定義（年齢別） */
	public void setSpriteSet(Sprite[] bodySpr) {
		this.bodySpr = bodySpr;
	}

	/** 拡幅分のスプライト定義（年齢別） */
	protected Sprite[] expandSpr = new Sprite[3];

	/** 拡幅分のスプライト定義（年齢別） を取得する. @return 拡幅分のスプライト定義（年齢別） */
	public Sprite[] getExpandSpr() {
		return expandSpr;
	}

	/** 拡幅分のスプライト定義（年齢別） を設定する. @param expandSpr 拡幅分のスプライト定義（年齢別） */
	public void setExpandSpr(Sprite[] expandSpr) {
		this.expandSpr = expandSpr;
	}

	// ===== Step6-1: BodyAttributes から移動したメソッド群 =====

	@com.fasterxml.jackson.annotation.JsonIgnore
	public AgeState getAgeState() {
		if (getAge() < getBabyLimitBase()) {
			return AgeState.BABY;
		} else if (getAge() < getChildLimitBase()) {
			return AgeState.CHILD;
		}
		return AgeState.ADULT;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public int getHungryLimit() {
		return getHungryLimitBase()[getAgeState().ordinal()];
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isOverEating() {
		return !dead && hungry >= getHungryLimit() * 1.3f;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isTooFull() {
		return !dead && hungry >= getHungryLimit();
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isFull() {
		return !dead && hungry >= getHungryLimit() * 0.8f;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isHungry() {
		return !dead && hungry <= getHungryLimit() / 2;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isSoHungry() {
		return !dead && hungry <= getHungryLimit() * 0.2f;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isVeryHungry() {
		return !dead && hungry <= 0;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isTooHungry() {
		return !dead && hungry <= 0 && getDamageState() != Damage.NONE;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isStarving() {
		return !dead && hungry <= 0 && getDamageState() == Damage.TOOMUCH;
	}

	public void addHungry(int val) {
		hungry += (TICK * val);
	}

	/** ストレスが負の場合0にリセットする. */
	public final void checkStress() {
		hungerDelegate().checkStress();
	}

	/** バカ舌値を上下限にクランプする. */
	public final void checkTang() {
		hungerDelegate().checkTang();
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public int getJkHung() {
		return hungry;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public void setJkHung(int j) {
		this.hungry = j;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isAdult() {
		return getAgeState() == AgeState.ADULT;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isChild() {
		return getAgeState() == AgeState.CHILD;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isBaby() {
		return getAgeState() == AgeState.BABY;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public int getDamageLimit() {
		return getDamageLimitBase()[getAgeState().ordinal()];
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public int getStressLimit() {
		return getStressLimitBase()[getAgeState().ordinal()];
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isSick() {
		return sickPeriod > getIncubationPeriodBase();
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isSickHeavily() {
		return sickPeriod > getIncubationPeriodBase() * 8;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isSickTooHeavily() {
		return sickPeriod > getIncubationPeriodBase() * 32 && isDamaged();
	}

	public final void forceSetSick() {
		sickPeriod = (getIncubationPeriodBase() * 32) + 2;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public final boolean isNYD() {
		return coreAnkoState != CoreAnkoState.NORMAL;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public final boolean isNotNYD() {
		return coreAnkoState == CoreAnkoState.NORMAL;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public final boolean isOld() {
		return getAge() > (getLifeLimitBase() * 9 / 10);
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public final boolean isTalking() {
		return messageTicks > 0;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public int getEatAmount() {
		return getEatAmountBase()[getAgeState().ordinal()];
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public final YukkuriBake getBakeLevel() {
		YukkuriBake ret = YukkuriBake.NONE;
		if (bodyBakePeriod < 0) {
			footBakePeriod = 0;
		}
		if (bodyBakePeriod > getDamageLimitBase()[getAgeState().ordinal()] * 3 / 4) {
			ret = YukkuriBake.CRITICAL;
		} else if (bodyBakePeriod > (getDamageLimitBase()[getAgeState().ordinal()] * 2 / 5)) {
			ret = YukkuriBake.MEDIUM;
		}
		return ret;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public FootBake getFootBakeLevel() {
		FootBake ret = FootBake.NONE;
		if (footBakePeriod < 0) {
			footBakePeriod = 0;
		}
		if (footBakePeriod > getDamageLimitBase()[getAgeState().ordinal()]) {
			ret = FootBake.CRITICAL;
		} else if (footBakePeriod > (getDamageLimitBase()[getAgeState().ordinal()] >> 1)) {
			ret = FootBake.MEDIUM;
		}
		return ret;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isGotBurned() {
		return getFootBakeLevel() != FootBake.NONE || getBakeLevel() != YukkuriBake.NONE;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isGotBurnedHeavily() {
		return getFootBakeLevel() != FootBake.NONE || getBakeLevel() == YukkuriBake.CRITICAL;
	}

	public void addBakePeriod(int s) {
		footBakePeriod += (s / 5);
		bodyBakePeriod += s;
	}

	public void addFootBakePeriod(int s) {
		footBakePeriod += s;
	}

	public void addTang(int val) {
		setTang(getTang() + val);
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
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

	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean hasTrauma() {
		return trauma != null;
	}

	public void addAttachment(Attachment at) {
		getAttach().add(at);
	}

	public int getAttachmentSize(Class<?> type) {
		int ret = 0;
		for (Attachment at : getAttach()) {
			if (at.getClass().equals(type)) {
				ret++;
			}
		}
		return ret;
	}

	public void removeAttachment(Class<?> type) {
		Attachment[] attachments = getAttach().toArray(new Attachment[0]);
		getAttach().clear();
		for (Attachment attachment : attachments) {
			if (!attachment.getClass().equals(type)) {
				getAttach().add(attachment);
			}
		}
	}

	public void resetAttachmentBoundary() {
		if (getAttach() != null && getAttach().size() != 0) {
			for (Attachment at : getAttach()) {
				at.resetBoundary();
			}
		}
	}

	public Entity getFavoriteItem(FavItemType key) {
		return getFavoriteItems().get(key) == null ? null : takeMappedObj(getFavoriteItems().get(key));
	}

	public void setFavoriteItem(FavItemType key, Entity val) {
		getFavoriteItems().put(key, val == null ? -1 : val.objId);
	}

	public void removeFavoriteItem(FavItemType key) {
		getFavoriteItems().remove(key);
	}

	public Entity getCarryItem(TakeoutItemType key) {
		if (getCarryItems() == null) {
			return null;
		}
		if (getCarryItems().get(key) == null) {
			return null;
		}
		WorldState m = GameWorld.get().getCurrentWorldState();
		if (m.getTakenOutFoods().containsKey(getCarryItems().get(key))) {
			return m.getTakenOutFoods().get(getCarryItems().get(key));
		}
		if (m.getTakenOutShits().containsKey(getCarryItems().get(key))) {
			return m.getTakenOutShits().get(getCarryItems().get(key));
		}
		return YukkuriRelations.findYukkuriByObjId(getCarryItems().get(key));
	}

	public void removeCarryItem(TakeoutItemType key) {
		getCarryItems().remove(key);
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public int getShitLimit() {
		return getShitLimitBase()[getAgeState().ordinal()];
	}

	public void plusShit(int s) {
		if (shit == 0 || s <= 0)
			return;
		shit += s;
	}

	public void setShit(int inShit, boolean ibVeryShit) {
		if (shitting)
			return;
		if (ibVeryShit) {
			if (shit < getShitLimitBase()[getAgeState().ordinal()]) {
				shit = getShitLimitBase()[getAgeState().ordinal()] - inShit;
			}
		} else {
			shit = inShit;
		}
	}

	/**
	 * LivingEntity レイヤーの状態フィールドを to へコピーする.
	 * 種族固有ベースパラメータ (xxxBase 配列, speed, cost 等) とスプライトはコピーしない.
	 */
	@Override
	public void copyStateTo(Entity to) {
		super.copyStateTo(to);
		LivingEntity l = (LivingEntity) to;
		// --- 基本状態 ---
		l.setDamage(damage);
		l.setDamageState(damageState);
		l.setStress(stress);
		l.setHungry(hungry);
		l.setShit(shit);
		l.setTang(tang);
		l.setDead(dead);
		l.setDeadPeriod(deadPeriod);
		l.setCoreAnkoState(coreAnkoState);
		l.setSleeping(sleeping);
		l.setNightmare(nightmare);
		l.setWakeUpTime(wakeUpTime);
		l.setRelax(relax);
		l.setDirty(dirty);
		l.setStubbornlyDirty(stubbornlyDirty);
		l.setDirtyPeriod(dirtyPeriod);
		l.setDirtyScreamPeriod(dirtyScreamPeriod);
		l.setWet(wet);
		l.setWetPeriod(wetPeriod);
		l.setMelt(melt);
		l.setPealed(pealed);
		l.setPacked(packed);
		l.setBlind(blind);
		l.setNeedled(needled);
		l.setBurned(burned);
		l.setCrushed(crushed);
		l.setPheromone(pheromone);
		l.setHasBraid(hasBraid);
		l.setHairState(hairState);
		l.setBirth(birth);
		l.setBirthAge(birthAge);
		l.setBirthEventBlockedTicks(birthEventBlockedTicks);
		l.setUnBirth(unBirth);
		l.setPregnantPeriod(pregnantPeriod);
		l.setPanicType(panicType);
		l.setPanicPeriod(panicPeriod);
		l.setCriticalDamege(criticalDamege);
		l.setTrauma(trauma);
		l.setNoDamagePeriod(noDamagePeriod);
		l.setNoHungryPeriod(noHungryPeriod);
		l.setSuperEatingNoHungryPeriod(superEatingNoHungryPeriod);
		l.setFootBakePeriod(footBakePeriod);
		l.setBakePeriod(bodyBakePeriod);
		l.setNonYukkuriDiseasePeriod(nonYukkuriDiseasePeriod);
		l.setAngryPeriod(angryPeriod);
		l.setScarePeriod(scarePeriod);
		l.setSadPeriod(sadPeriod);
		l.setSleepingPeriod(sleepingPeriod);
		l.setSickPeriod(sickPeriod);
		l.setFalldownDamage(falldownDamage);
		l.setNoDamageNextFall(noDamageNextFall);
		l.setCantDiePeriod(cantDiePeriod);
		l.setBurialState(burialState);
		// --- インベントリ ---
		l.setOkazaris(okazari);
		l.setOkazariPosition(okazariPosition);
		l.setHasBaby(hasBaby);
		l.setHasStalk(hasStalk);
		l.setBabyTypes(babyTypes != null ? new LinkedList<>(babyTypes) : null);
		l.setStalkBabyTypes(stalkBabyTypes != null ? new LinkedList<>(stalkBabyTypes) : null);
		l.setStalks(stalks != null ? new LinkedList<>(stalks) : null);
		l.setBindStalk(bindStalk);
		l.setAttach(attach != null ? new LinkedList<>(attach) : null);
		l.setAnkoAmount(ankoAmount);
		l.setFavoriteItems(favoriteItems != null ? new HashMap<>(favoriteItems) : null);
		l.setCarryItems(carryItems != null ? new HashMap<>(carryItems) : null);
		// --- 移動・アクション状態 ---
		l.setDestX(destX);
		l.setDestY(destY);
		l.setDestZ(destZ);
		l.setCountX(countX);
		l.setCountY(countY);
		l.setCountZ(countZ);
		l.setDirX(dirX);
		l.setDirY(dirY);
		l.setDirZ(dirZ);
		l.setDirection(direction);
		l.setPurposeOfMoving(purposeOfMoving);
		l.setCanPullOrPush(canPullOrPush);
		l.setExternalPressure(externalPressure);
		l.setMoveTargetId(moveTargetId);
		l.setTargetOffsetX(targetOffsetX);
		l.setTargetOffsetY(targetOffsetY);
		l.setShitting(shitting);
		l.setEating(eating);
		l.setEatingShit(eatingShit);
		l.setSukkiri(sukkiri);
		l.setScare(scare);
		l.setAngry(angry);
		l.setFurifuri(furifuri);
		l.setStrike(strike);
		l.setNobinobi(nobinobi);
		l.setBeVain(beVain);
		l.setPikopiko(pikopiko);
		l.setPurupuru(purupuru);
		l.setYunnyaa(yunnyaa);
		l.setSilent(silent);
		l.setShutmouth(shutmouth);
		l.setBegging(begging);
		l.setPlaying(playing);
		l.setPlayingLimit(playingLimit);
		l.setShakePhase(shakePhase);
		l.setFixBack(fixBack);
		l.setStaying(staying);
		l.setStayTicks(stayTicks);
		l.setStayTime(stayTime);
		l.setMessageBuffer(messageBuffer);
		l.setMessageTicks(messageTicks);
		l.setMessageLineColor(messageLineColor);
		l.setMessageBoxColor(messageBoxColor);
		l.setMessageTextColor(messageTextColor);
		l.setMessageWindowStroke(messageWindowStroke);
		l.setMessageTextSize(messageTextSize);
		l.setBirthMessageForced(birthMessageForced);
		l.setShadowVisible(shadowVisible);
		l.setImageNagasiMode(imageNagasiMode);
		l.setBlinkCount(blinkCount);
		l.setBlinkType(blinkType);
		l.setUnyoMode(unyoMode);
		l.setUnyoOffsetH(unyoOffsetH);
		l.setUnyoOffsetW(unyoOffsetW);
		l.setEvents(eventList != null ? new LinkedList<>(eventList) : null);
		l.setCurrentEvent(currentEvent);
		l.setEventResult(eventResult);
		// --- ID・識別 ---
		l.setUniqueID(uniqueID);
		l.setMsgType(msgType);
		l.setShitType(shitType);
		// --- その他状態 ---
		l.setTaken(taken);
		l.setPinned(isPinned);
		l.setSurisuriFromPlayer(surisuriFromPlayer);
		l.setLastSurisuriTime(lastSurisuriTime);
		l.setInOutTakeoutItem(inOutTakeoutItem);
		l.setAbFlagGodHand(abFlagGodHand != null ? abFlagGodHand.clone() : null);
		l.setGodHandHoldCount(godHandHoldCount);
		l.setGodHandStretchCount(godHandStretchCount);
		l.setGodHandCompressCount(godHandCompressCount);
		l.setAntCount(antCount);
		l.setNonMovingConveyor(nonMovingConveyor);
		l.setLastActionTime(lastActionTime);
		l.setBlockedTicks(blockedTicks);
		l.setPregnancyPeriodBoost(pregnancyPeriodBoost);
		l.setExcitementPeriodBoost(excitementPeriodBoost);
		l.setExcretionBoost(excretionBoost);
		l.setFirstGround(firstGround);
		l.setFirstEatStalk(firstEatStalk);
		l.setLockmove(lockmove);
		l.setLockmovePeriod(lockmovePeriod);
		l.setCanTalk(canTalk);
		// --- 個体値 ---
		l.setAttitudePoint(attitudePoint);
		l.setImmunityStrength(immunityStrength);
		// --- 種族固有ベースパラメータ (変身時は readYukkuriIniFile で上書きされる) ---
		l.setEatAmountBase(eatAmountBase != null ? eatAmountBase.clone() : null);
		l.setWeightBase(weightBase != null ? weightBase.clone() : null);
		l.setHungryLimitBase(hungryLimitBase != null ? hungryLimitBase.clone() : null);
		l.setShitLimitBase(shitLimitBase != null ? shitLimitBase.clone() : null);
		l.setDamageLimitBase(damageLimitBase != null ? damageLimitBase.clone() : null);
		l.setStressLimitBase(stressLimitBase != null ? stressLimitBase.clone() : null);
		l.setTangLevelBase(tangLevelBase != null ? tangLevelBase.clone() : null);
		l.setStepBase(stepBase != null ? stepBase.clone() : null);
		l.setStrengthBase(strengthBase != null ? strengthBase.clone() : null);
		l.setImmunity(immunity != null ? immunity.clone() : null);
		l.setRudeLimit(rudeLimit != null ? rudeLimit.clone() : null);
		l.setNiceLimit(niceLimit != null ? niceLimit.clone() : null);
		l.setCleaningFailProbWise(cleaningFailProbWise != null ? cleaningFailProbWise.clone() : null);
		l.setCleaningFailProbAverage(cleaningFailProbAverage != null ? cleaningFailProbAverage.clone() : null);
		l.setCleaningFailProbFool(cleaningFailProbFool != null ? cleaningFailProbFool.clone() : null);
		l.setBabyLimitBase(babyLimitBase);
		l.setChildLimitBase(childLimitBase);
		l.setLifeLimitBase(lifeLimitBase);
		l.setRottingTimeBase(rottingTimeBase);
		l.setRelaxPeriodBase(relaxPeriodBase);
		l.setExcitePeriodBase(excitePeriodBase);
		l.setPregPeriodBase(pregPeriodBase);
		l.setSleepPeriodBase(sleepPeriodBase);
		l.setActivePeriodBase(activePeriodBase);
		l.setAngryPeriodBase(angryPeriodBase);
		l.setScarePeriodBase(scarePeriodBase);
		l.setDeclinePeriodBase(declinePeriodBase);
		l.setBlockedLimitBase(blockedLimitBase);
		l.setDirtyPeriodBase(dirtyPeriodBase);
		l.setEyesightBase(eyesightBase);
		l.setIncubationPeriodBase(incubationPeriodBase);
		l.setLovePlayerLimitBase(lovePlayerLimitBase);
		l.setBraidBreakChance(braidBreakChance);
		l.setSurisuriAccidentProb(surisuriAccidentProb);
		l.setCarAccidentProb(carAccidentProb);
		l.setBreakByShitProb(breakBodyByShitProb);
		l.setDiarrheaProb(diarrheaProb);
		l.setExciteProb(exciteProb);
		l.setNotChangeCharacter(notChangeCharacter);
		l.setSameDirectionFactor(sameDirectionFactor);
		l.setPregnantLimit(pregnantLimit);
		l.setUseRealPregnantLimit(useRealPregnantLimit);
		l.setSpeed(speed);
	}

}
