package org.simyukkuri.entity.core.living;

import java.beans.Transient;
import java.util.LinkedList;
import java.util.List;
import org.simyukkuri.Const;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.FootBake;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.LovePlayer;
import org.simyukkuri.enums.PanicType;
import org.simyukkuri.enums.Parent;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.enums.Trauma;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.logic.YukkuriIllnessRule;
import org.simyukkuri.logic.YukkuriLogic;
import org.simyukkuri.logic.YukkuriRelations;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;

/**
 * 社会的行動を持つ生命エンティティの抽象基底クラス。
 * 家族・社会的関係フィールドを保持する。
 */
public abstract class SocialEntity extends LivingEntity {
	private static final long serialVersionUID = 3048671834219406712L;

	/** 性格 counter indicating shithead/nicehead etc. */
	protected Attitude attitude = Attitude.AVERAGE;
	/** 知性 */
	protected Intelligence intelligence = Intelligence.AVERAGE;
	/** 幸福度 */
	protected Happiness happiness = Happiness.AVERAGE;
	/** プレイヤーへのなつき度 */
	protected int lovePlayer = 0;
	/** プレイヤーへのなつき度概算 */
	protected LovePlayer lovePlayerState = LovePlayer.NONE;
	/** 飼いゆ、野良ゆなどのランク */
	protected YukkuriRank bodyRank = YukkuriRank.KAIYU;
	/** 群れ内のうんうん奴隷などのランク */
	protected PublicRank publicRank = PublicRank.NONE;
	/** 思い出（悪夢関連） */
	protected int memories = 0;
	/** レイパー化有無 */
	protected boolean rapist = false;
	/** バイゆグラでレイパーになる、すーぱーれいぱー状態 */
	protected boolean superRapist = false;
	/** うんうん抑制 */
	protected int shittingDiscipline = 0;
	/** ぺにぺにの去勢有無 */
	protected boolean penipeniCutted = false;
	/** 水が平気か */
	protected boolean likeWater = false;
	/** 興奮抑制 */
	protected int excitingDiscipline = 0;
	/** ふりふり抑制 */
	protected int furifuriDiscipline = 0;
	/** おしゃべり抑制 */
	protected int speechDiscipline = 0;
	/** あまあまへの慣れ具合 */
	protected int amaamaDiscipline = 0;
	/** パニック種別 */
	protected PanicType panicType = null;
	/** パニック状態の期間 */
	protected int panicPeriod = 0;
	/** トラウマ */
	protected Trauma trauma = Trauma.NONE;

	/** パニックタイプを返す。 */
	@Override
	public PanicType getPanicType() {
		return panicType;
	}

	/** パニックタイプをセットする。 */
	@Override
	public void setPanicType(PanicType panicType) {
		this.panicType = panicType;
	}

	/** パニック継続時間を返す。 */
	@Override
	public int getPanicPeriod() {
		return panicPeriod;
	}

	/** パニック継続時間をセットする。 */
	@Override
	public void setPanicPeriod(int panicPeriod) {
		this.panicPeriod = panicPeriod;
	}

	/** トラウマの種別を返す。 */
	@Override
	public Trauma getTrauma() {
		return trauma;
	}

	/** トラウマの種別をセットする。 */
	@Override
	public void setTrauma(Trauma trauma) {
		this.trauma = trauma;
	}

	/** 性格（クズ・良いゆ等）の区分を返す。 */
	public Attitude getAttitude() {
		return attitude;
	}

	/** 性格の区分をセットする。 */
	public void setAttitude(Attitude attitude) {
		this.attitude = attitude;
	}

	/** 知性 を取得する. @return 知性 */
	public Intelligence getIntelligence() {
		return intelligence;
	}

	/** 知性 を設定する. @param intelligence 知性 */
	public void setIntelligence(Intelligence intelligence) {
		this.intelligence = intelligence;
	}

	/** 幸福度 を取得する. @return 幸福度 */
	public Happiness getHappiness() {
		return happiness;
	}

	/** 幸福度 を設定する. @param happiness 幸福度 */
	public void setHappiness(Happiness happiness) {
		if (isDead() || isIdiot()) {
			this.happiness = Happiness.AVERAGE;
			return;
		}
		if (isNyd()) {
			this.happiness = Happiness.VERY_SAD;
			sadPeriod = 1200 + GameRandom.nextInt(400) - 200;
			return;
		}
		if (happiness == Happiness.SAD) {
			if (getHappiness() != Happiness.VERY_SAD) {
				sadPeriod = 0;
				this.happiness = happiness;
			}
		} else if (happiness == Happiness.HAPPY) {
			if (getHappiness() != Happiness.VERY_HAPPY) {
				sadPeriod = 0;
				this.happiness = happiness;
			}
		} else {
			if (happiness == Happiness.VERY_SAD) {
				sadPeriod = 1200 + GameRandom.nextInt(400) - 200;
			} else {
				sadPeriod = 0;
			}
			this.happiness = happiness;
		}
		if (getHappiness() == Happiness.HAPPY || getHappiness() == Happiness.VERY_HAPPY) {
			setScare(false);
			setAngry(false);
		} else if (getHappiness() == Happiness.SAD || getHappiness() == Happiness.VERY_SAD) {
			setAngry(false);
		}
	}

	/** 喜んでいるか. @return 喜んでいるかどうか */
	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isHappy() {
		return !dead && (happiness == Happiness.HAPPY || happiness == Happiness.VERY_HAPPY);
	}

	/** 悲しんでいるか. @return 悲しんでいるかどうか */
	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isSad() {
		return !dead && happiness == Happiness.SAD;
	}

	/** とても悲しんでいるか. @return とても悲しんでいるかどうか */
	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isVerySad() {
		return !dead && happiness == Happiness.VERY_SAD;
	}

	/** 悲しんでいるか（SAD or VERY_SAD）. @return 悲しんでいるかどうか */
	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isUnhappy() {
		return !dead && (happiness == Happiness.SAD || happiness == Happiness.VERY_SAD);
	}

	/** プレイヤーへのなつき度 を取得する. @return プレイヤーへのなつき度 */
	public int getLovePlayer() {
		return lovePlayer;
	}

	/** プレイヤーへのなつき度 を設定する. @param lovePlayer プレイヤーへのなつき度 */
	public void setLovePlayer(int lovePlayer) {
		this.lovePlayer = lovePlayer;
	}

	/** プレイヤーへのなつき度概算 を取得する. @return プレイヤーへのなつき度概算 */
	public LovePlayer getLovePlayerState() {
		return lovePlayerState;
	}

	/** プレイヤーへのなつき度概算 を設定する. @param lovePlayerState プレイヤーへのなつき度概算 */
	public void setLovePlayerState(LovePlayer lovePlayerState) {
		this.lovePlayerState = lovePlayerState;
	}

	/** 飼いゆ、野良ゆなどのランク を取得する. @return 飼いゆ、野良ゆなどのランク */
	public YukkuriRank getRank() {
		return bodyRank;
	}

	/** 飼いゆ、野良ゆなどのランク を設定する. @param bodyRank 飼いゆ、野良ゆなどのランク */
	public void setRank(YukkuriRank bodyRank) {
		this.bodyRank = bodyRank;
	}

	/** 群れ内のうんうん奴隷などのランク を取得する. @return 群れ内のうんうん奴隷などのランク */
	public PublicRank getPublicRank() {
		return publicRank;
	}

	/** 群れ内のうんうん奴隷などのランク を設定する. @param publicRank 群れ内のうんうん奴隷などのランク */
	public void setPublicRank(PublicRank publicRank) {
		this.publicRank = publicRank;
	}

	/** 思い出（悪夢関連） を取得する. @return 思い出（悪夢関連） */
	public int getMemories() {
		return memories;
	}

	/** 思い出（悪夢関連） を設定する. @param memories 思い出（悪夢関連） */
	public void setMemories(int memories) {
		this.memories = memories;
	}

	/** レイパー化有無 を取得する. @return レイパー化有無 */
	public boolean isRapist() {
		return rapist;
	}

	/** レイパー化有無 を設定する. @param rapist レイパー化有無 */
	public void setRapist(boolean rapist) {
		this.rapist = rapist;
	}

	/** バイゆグラでレイパーになる、すーぱーれいぱー状態 を取得する. @return バイゆグラでレイパーになる、すーぱーれいぱー状態 */
	public boolean isSuperRapist() {
		return superRapist;
	}

	/**
	 * バイゆグラでレイパーになる、すーぱーれいぱー状態 を設定する. @param superRapist バイゆグラでレイパーになる、すーぱーれいぱー状態
	 */
	public void setSuperRapist(boolean superRapist) {
		this.superRapist = superRapist;
	}

	/** うんうん抑制 を取得する. @return うんうん抑制 */
	public int getShittingDiscipline() {
		return shittingDiscipline;
	}

	/** うんうん抑制 を設定する. @param shittingDiscipline うんうん抑制 */
	public void setShittingDiscipline(int shittingDiscipline) {
		this.shittingDiscipline = shittingDiscipline;
	}

	/** ぺにぺにの去勢有無 を取得する. @return ぺにぺにの去勢有無 */
	@Override
	public boolean isPenipeniCutted() {
		return penipeniCutted;
	}

	/** ぺにぺにの去勢有無 を設定する. */
	@Override
	public void setPenipeniCutted(boolean penipeniCutted) {
		this.penipeniCutted = penipeniCutted;
	}

	/** 水が平気か を取得する. @return 水が平気か */
	@Override
	public boolean isLikeWater() {
		return likeWater;
	}

	/** 水が平気か を設定する. */
	@Override
	public void setLikeWater(boolean likeWater) {
		this.likeWater = likeWater;
	}

	/** 発情フラグ want to sukkiri or not を取得する. @return 発情フラグ want to sukkuri or not */
	@Override
	public abstract boolean isExciting();

	/** 発情フラグ want to sukkiri or not を設定する. */
	@Override
	public abstract void setExciting(boolean exciting);

	/** 強制発情フラグ want to sukkiri or not を取得する. @return 強制発情フラグ want to sukkuri or not */
	@Override
	public abstract boolean isForceExciting();

	/** 強制発情フラグ want to sukkiri or not を設定する. */
	@Override
	public abstract void setForceExciting(boolean forceExciting);

	/** 興奮・発情状態をリセットしてゆっくり状態に戻す。 */
	@Override
	public void setCalm() {
		setForceExciting(false);
		setExciting(false);
	}

	/** 発情期間 を取得する. @return 発情期間 */
	@Override
	public abstract int getExcitingPeriod();

	/** 発情期間 を設定する. */
	@Override
	public abstract void setExcitingPeriod(int excitingPeriod);

	/** 興奮抑制 を取得する. @return 興奮抑制 */
	public int getExcitingDiscipline() {
		return excitingDiscipline;
	}

	/** 興奮抑制 を設定する. @param excitingDiscipline 興奮抑制 */
	public void setExcitingDiscipline(int excitingDiscipline) {
		this.excitingDiscipline = excitingDiscipline;
	}

	/** ふりふり抑制 を取得する. @return ふりふり抑制 */
	public int getFurifuriDiscipline() {
		return furifuriDiscipline;
	}

	/** ふりふり抑制 を設定する. @param furifuriDiscipline ふりふり抑制 */
	public void setFurifuriDiscipline(int furifuriDiscipline) {
		this.furifuriDiscipline = furifuriDiscipline;
	}

	/** おしゃべり抑制 を取得する. @return おしゃべり抑制 */
	public int getSpeechDiscipline() {
		return speechDiscipline;
	}

	/** おしゃべり抑制 を設定する. @param speechDiscipline おしゃべり抑制 */
	public void setSpeechDiscipline(int speechDiscipline) {
		this.speechDiscipline = speechDiscipline;
	}

	/** あまあまへの慣れ具合 を取得する. @return あまあまへの慣れ具合 */
	public int getAmaamaDiscipline() {
		return amaamaDiscipline;
	}

	/** あまあまへの慣れ具合 を設定する. @param amaamaDiscipline あまあまへの慣れ具合 */
	public void setAmaamaDiscipline(int amaamaDiscipline) {
		this.amaamaDiscipline = amaamaDiscipline;
	}

	/** パートナーのID */
	private int partner = -1;
	/** 親のIDペア */
	private int[] parents = { -1, -1 };
	/** 子供のIDリスト */
	private List<Integer> childrenList = new LinkedList<Integer>();
	/** 姉ゆのIDリスト */
	private List<Integer> elderSisterList = new LinkedList<Integer>();
	/** 妹ゆのIDリスト */
	private List<Integer> sisterList = new LinkedList<Integer>();
	/** 先祖のIDリスト */
	private List<Integer> ancestorList = new LinkedList<Integer>();
	/** 父親にレイプされた経験があるか */
	private boolean fatherRaper = false;
	/** 親リンクのID */
	private int parentLinkId = -1;

	/** パートナーのID を取得する. @return パートナーのID */
	public int getPartner() {
		return partner;
	}

	/** パートナーのID を設定する. @param partner パートナーのID */
	public void setPartner(int partner) {
		this.partner = partner;
	}

	/** 親のIDペア を取得する. @return 親のIDペア */
	public int[] getParents() {
		return parents;
	}

	/** 親のIDペア を設定する. @param parents 親のIDペア */
	public void setParents(int[] parents) {
		this.parents = parents;
	}

	/** 子供のIDリスト を取得する. @return 子供のIDリスト */
	public List<Integer> getChildren() {
		return childrenList;
	}

	/** 子供のIDリスト を設定する. @param childrenList 子供のIDリスト */
	public void setChildren(List<Integer> childrenList) {
		this.childrenList = childrenList;
	}

	/** 姉ゆのIDリスト を取得する. @return 姉ゆのIDリスト */
	public List<Integer> getElderSisters() {
		return elderSisterList;
	}

	/** 姉ゆのIDリスト を設定する. @param elderSisterList 姉ゆのIDリスト */
	public void setElderSisters(List<Integer> elderSisterList) {
		this.elderSisterList = elderSisterList;
	}

	/** 妹ゆのIDリスト を取得する. @return 妹ゆのIDリスト */
	public List<Integer> getSisters() {
		return sisterList;
	}

	/** 妹ゆのIDリスト を設定する. @param sisterList 妹ゆのIDリスト */
	public void setSisters(List<Integer> sisterList) {
		this.sisterList = sisterList;
	}

	/** 先祖のIDリスト を取得する. @return 先祖のIDリスト */
	public List<Integer> getAncestors() {
		return ancestorList;
	}

	/** 先祖のIDリスト を設定する. @param ancestorList 先祖のIDリスト */
	public void setAncestors(List<Integer> ancestorList) {
		this.ancestorList = ancestorList;
	}

	/** 父親にレイプされた経験があるか を取得する. @return 父親にレイプされた経験があるか */
	public boolean isFatherRaper() {
		return fatherRaper;
	}

	/** 父親にレイプされた経験があるか を設定する. @param fatherRaper 父親にレイプされた経験があるか */
	public void setFatherRaper(boolean fatherRaper) {
		this.fatherRaper = fatherRaper;
	}

	/** 親リンクのID を取得する. @return 親リンクのID */
	public int getParentLinkId() {
		return parentLinkId;
	}

	/** 親リンクのID を設定する. @param parentLinkId 親リンクのID */
	public void setParentLinkId(int parentLinkId) {
		this.parentLinkId = parentLinkId;
	}

	/**
	 * 濡れているときの基本反応.
	 */
	public void checkWet() {
		// 濡れても溶けてもないなら抜ける
		if (!isWet() && !isMelt()) {
			return;
		}

		wetPeriod += TICK;
		if (wetPeriod > 300) {
			setWet(false);
			wetPeriod = 0;
		}
		if (!isLikeWater()) {
			// 50%以上のダメージ中か、皮がない時に濡れたら溶ける
			if (isDamaged() || isPealed()) {
				setMelt(true);
			}
			damage += TICK * 5;
			getDamageState();
			if (getAge() % 5 == 0) {
				addStress(20);
			}
		}
	}

	/**
	 * ふりふり可能な状態かどうかを返却する.
	 *
	 * @return ふりふり可能な状態かどうか
	 */
	public boolean canFurifuri() {
		if (getFootBakeLevel() != FootBake.CRITICAL && getCoreAnkoState() == CoreAnkoState.NORMAL) {
			return true;
		}
		return false;
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

	/** 対象を呼び止めるほど強い動機を持っているかどうか */
	protected boolean targetBind = false;

	/** 対象を呼び止めるほど強い動機を持っているかどうか を取得する. @return 対象を呼び止めるほど強い動機を持っているかどうか */
	public boolean isTargetBind() {
		return targetBind;
	}

	/**
	 * 対象を呼び止めるほど強い動機を持っているかどうか を設定する. @param targetBind 対象を呼び止めるほど強い動機を持っているかどうか
	 */
	public void setTargetBind(boolean targetBind) {
		this.targetBind = targetBind;
	}

	/** ぺろぺろ中 */
	protected boolean peropero = false;

	/** ぺろぺろ中 を取得する. @return ぺろぺろ中 */
	public boolean isPeropero() {
		return !dead && peropero;
	}

	/** ぺろぺろ中 を設定する. @param peropero ぺろぺろ中 */
	public void setPeropero(boolean peropero) {
		this.peropero = peropero;
	}

	/** 親を呼んで泣き叫び中 */
	protected boolean callingParents = false;

	/** 親を呼んで泣き叫び中 を取得する. @return 親を呼んで泣き叫び中 */
	public boolean isCallingParents() {
		return !dead && callingParents;
	}

	/** 親を呼んで泣き叫び中 を設定する. @param callingParents 親を呼んで泣き叫び中 */
	public void setCallingParents(boolean callingParents) {
		this.callingParents = callingParents;
	}

	/** 表情の強制設定 */
	protected int forceFace = -1;

	/** 表情の強制設定 を取得する. @return 表情の強制設定 */
	public int getForceFace() {
		return forceFace;
	}

	/** 表情の強制設定 を設定する. @param forceFace 表情の強制設定 */
	public void setForceFace(int forceFace) {
		this.forceFace = forceFace;
	}

	// ===== Step6-2: BodyAttributes から移動したメソッド群 =====

	/** @param val プレイヤーへの好感度に加算する値（上限・下限でクランプ） */
	public void addLovePlayer(int val) {
		if (isNyd()) {
			lovePlayer = -1 * getLovePlayerLimitBase();
			return;
		}
		lovePlayer += (TICK * val);
		if (lovePlayer < -1 * getLovePlayerLimitBase()) {
			lovePlayer = -1 * getLovePlayerLimitBase();
		} else if (getLovePlayerLimitBase() < lovePlayer) {
			lovePlayer = getLovePlayerLimitBase();
		}
	}

	/** @param memoryDelta 記憶量に加算する値（0以下にはならない） */
	public final void addMemories(int memoryDelta) {
		switch (getIntelligence()) {
			case WISE:
				memories += memoryDelta / 2;
				break;
			case FOOL:
				if (memoryDelta < 0) {
					memories += memoryDelta / 2;
				} else {
					memories += memoryDelta * 2;
				}
				break;
			default:
				if (memoryDelta < 0) {
					memories += memoryDelta;
				} else {
					memories += memoryDelta * 2;
				}
				break;
		}
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
		// ストレスに応じてうんうん増加
		if (s > 0 && getCoreAnkoState() == CoreAnkoState.NORMAL && getBurstState() != org.simyukkuri.enums.Burst.HALF) {
			plusShit(s / 5);
		}
		stress += TICK * s;
		if (stress < 0) {
			stress = 0;
		}
	}

	/** @param val あまあましつけ度に加算する値 */
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
	 * 障害の有無を返却する.
	 *
	 * @return 障害の有無
	 */
	public final boolean hasDisorder() {
		if (isIdiot()) {
			return true;
		}
		if (isNyd()) {
			return true;
		}
		if (!hasOkazari()) {
			return true;
		}
		if (!hasBraidCheck()) {
			return true;
		}
		if (isBlind()) {
			return true;
		}
		if (isPacked()) {
			return true;
		}
		if (isGotBurned()) {
			return true;
		}
		if (getCriticalDamege() == CriticalDamageType.CUT) {
			return true;
		}

		return false;
	}

	/**
	 * 子ゆっくりが病気かどうかを判定する.
	 *
	 * @param b 判定対象
	 * @return 病気ならtrue
	 */
	public boolean findSick(Yukkuri b) {
		return YukkuriIllnessRule.findSick(getIntelligence(), b);
	}

	/**
	 * 非ゆっくり症耐性を計算する.
	 *
	 * @return 耐性値
	 */
	@com.fasterxml.jackson.annotation.JsonIgnore
	public int getNonYukkuriDiseaseTolerance() {
		int tolerance = 100;
		if (isIdiot()) {
			tolerance += 50000;
		}
		if (getPublicRank() == PublicRank.UNUN_SLAVE) {
			tolerance += 10000;
		}
		switch (getIntelligence()) {
			case WISE:
				tolerance += 5;
				break;
			case FOOL:
				tolerance += 10;
				break;
			default:
				break;
		}
		switch (getAttitude()) {
			case VERY_NICE:
				tolerance += 5;
				break;
			case NICE:
				tolerance += 10;
				break;
			case SHITHEAD:
				tolerance += 30;
				break;
			case SUPER_SHITHEAD:
				tolerance += 50;
				break;
			default:
				break;
		}
		switch (getAgeState()) {
			case BABY:
				break;
			case CHILD:
				tolerance += 30;
				break;
			case ADULT:
				tolerance += 50;
				break;
			default:
				break;
		}
		if (isRapist()) {
			tolerance += 5000;
		}
		if (isSoHungry()) {
			if (isVeryHungry()) {
				tolerance -= 5;
			} else {
				tolerance -= 3;
			}
		}
		switch (getFootBakeLevel()) {
			case MEDIUM:
				tolerance -= 30;
				break;
			case CRITICAL:
				tolerance -= 50;
				break;
			default:
				break;
		}
		switch (getBakeLevel()) {
			case MEDIUM:
				tolerance -= 15;
				break;
			case CRITICAL:
				tolerance -= 25;
				break;
			default:
				break;
		}
		if (isSick()) {
			tolerance -= 15;
		}
		if (!hasOkazari()) {
			tolerance -= 25;
		}
		if (!isHasBraid()) {
			tolerance -= 10;
		}
		if (isPenipeniCutted()) {
			tolerance -= 20;
		}
		if (isDirty()) {
			tolerance -= 5;
		}
		if (isLockmove()) {
			tolerance -= 5;
		}
		if (isBlind()) {
			tolerance -= 20;
		}
		if (isShutmouth()) {
			tolerance -= 10;
		}
		if (getCriticalDamege() == CriticalDamageType.INJURED) {
			tolerance -= 10;
		}
		if (getChildren() != null) {
			for (int childId : getChildren()) {
				Yukkuri childBody = YukkuriRelations.getYukkuriById(childId);
				if (childBody == null || childBody.isAdult()) {
					continue;
				}
				if (childBody.isRemoved() || childBody.isDead()) {
					tolerance -= 10;
					continue;
				}
				if (childBody.isCrushed() || childBody.isDamaged() || childBody.isBurned() || findSick(childBody)
						|| childBody.isTooHungry()) {
					tolerance -= 3;
					continue;
				}
				if (hasDisorder()) {
					tolerance -= 5;
					continue;
				}
				tolerance += 10;
			}
		}
		tolerance += memories;
		if (tolerance <= -1) {
			tolerance = -1;
		}
		return tolerance;
	}

	/**
	 * 盲目時の基本反応.
	 *
	 * @return その後の処理をキャンセルするかどうか
	 */
	public boolean checkEmotionBlind() {
		if (applyBlindnessPenalty()) {
			setHappiness(Happiness.SAD);
			if (GameRandom.nextInt(40) <= 5) {
				setMessage(GameMessages.getMessage(this, MessagePool.Action.CANTSEE));
			} else if (GameRandom.nextInt(40) == 20) {
				setMessage(GameMessages.getMessage(this, MessagePool.Action.LamentNoYukkuri));
			}
			return true;
		}
		return false;
	}

	/**
	 * 口封じ時の基本反応.
	 *
	 * @return その後の処理をキャンセルするかどうか
	 */
	public boolean checkEmotionCantSpeak() {
		if (applyCantSpeakPenalty()) {
			setHappiness(Happiness.SAD);
			if (GameRandom.nextInt(80) == 0 && !isSleeping()) {
				setMessage(GameMessages.getMessage(this, MessagePool.Action.CantTalk));
			}
			return true;
		}
		return false;
	}

	/**
	 * 動けない時の基本反応.
	 *
	 * @return その後の処理をキャンセルするかどうか
	 */
	public boolean checkEmotionLockmove() {
		if (!beginLockmoveEmotion()) {
			return false;
		}

		if (getLockmovePeriod() < 400) {
			if (GameRandom.nextInt(15) == 0) {
				clearActions();
				if (getBurialState() == BurialState.ALL || getBurialState() == BurialState.NEARLY_ALL) {
					setHappiness(Happiness.VERY_SAD);
					setMessage(GameMessages.getMessage(this, MessagePool.Action.BaryInUnderGround));
					stay();
				} else {
					setMessage(GameMessages.getMessage(this, MessagePool.Action.CantMove));
					setAngry(true);
					if (GameRandom.nextInt(10) == 0) {
						setNobinobi(true);
					}
				}
				return true;
			}
			if (isHungry() && GameRandom.nextInt(50) == 0) {
				setMessage(GameMessages.getMessage(this, MessagePool.Action.Hungry), 30);
				setHappiness(Happiness.SAD);
				stay(30);
			} else if (GameRandom.nextInt(15) == 0) {
				clearActions();
				if (getBurialState() == BurialState.ALL || getBurialState() == BurialState.NEARLY_ALL) {
					setHappiness(Happiness.VERY_SAD);
					setMessage(GameMessages.getMessage(this, MessagePool.Action.BaryInUnderGround));
					stay();
				} else {
					setAngry(true);
					setHappiness(Happiness.VERY_SAD);
					setMessage(GameMessages.getMessage(this, MessagePool.Action.CantMove2));
					if (GameRandom.nextInt(10) == 0) {
						setNobinobi(true);
					}
				}
				return true;
			}
		}
		return true;
	}

	/**
	 * 足焼き済みの基本反応
	 *
	 * @return その後の処理をキャンセルするかどうか
	 */
	public boolean checkEmotionFootbake() {
		if (!beginFootBakeEmotion()) {
			return false;
		}

		if (getFootBakeLevel() == FootBake.MEDIUM) {
			if (GameRandom.nextInt(15) == 0) {
				clearActions();
				setAngry(true);
				setHappiness(Happiness.SAD);
				setMessage(GameMessages.getMessage(this, MessagePool.Action.LamentLowYukkuri));
				return true;
			}
			if (isHungry() && GameRandom.nextInt(400) == 0) {
				setMessage(GameMessages.getMessage(this, MessagePool.Action.Hungry), 30);
				setHappiness(Happiness.SAD);
				return true;
			}
		} else if (getFootBakeLevel() == FootBake.CRITICAL) {
			if (lockmovePeriod < 300) {
				if (GameRandom.nextInt(15) == 0) {
					clearActions();
					setAngry(true);
					setHappiness(Happiness.SAD);
					if (GameRandom.nextInt(5) == 0) {
						setMessage(GameMessages.getMessage(this, MessagePool.Action.LamentLowYukkuri));
					} else {
						setMessage(GameMessages.getMessage(this, MessagePool.Action.CantMove));
					}
					return true;
				}
				if (isHungry() && GameRandom.nextInt(50) == 0) {
					setMessage(GameMessages.getMessage(this, MessagePool.Action.Hungry), 30);
					setHappiness(Happiness.VERY_SAD);
					stay();
					return true;
				}
			} else {
				if (GameRandom.nextInt(15) == 0) {
					clearActions();
					setAngry(true);
					setHappiness(Happiness.VERY_SAD);
					if (GameRandom.nextInt(5) != 0) {
						setMessage(GameMessages.getMessage(this, MessagePool.Action.CantMove2));
					} else {
						setMessage(GameMessages.getMessage(this, MessagePool.Action.LamentNoYukkuri));
					}
					return true;
				}
			}
		}
		return true;
	}

	/**
	 * おかざり、ぴこぴこなしのときの基本反応
	 *
	 * @return その後の処理をキャンセルするかどうか
	 */
	public boolean checkEmotionNoOkazariPikopiko() {
		if (!beginNoOkazariEmotion()) {
			return false;
		}
		if (GameRandom.nextInt(50) == 0) {
			clearActions();
			setAngry(true);
			setHappiness(Happiness.SAD);
			setForceFace(ImageCode.TIRED.ordinal());
			setMessage(GameMessages.getMessage(this, MessagePool.Action.LamentLowYukkuri));
			stay();
			return true;
		}
		return true;
	}

	/**
	 * 自主的洗浄を行う.
	 */
	public final void cleaningItself() {
		stay();
		setMessage(GameMessages.getMessage(this, MessagePool.Action.CleanItself));
		if (!isAdult()) {
			setHappiness(Happiness.SAD);
			setForceFace(ImageCode.TIRED.ordinal());
		}
		if (canFurifuri()) {
			setFurifuri(true);
		}
		makeDirty(false);
		int ageIndex = isBaby() ? 0 : (isChild() ? 1 : 2);
		int prob = 1;
		switch (getIntelligence()) {
			case WISE:
				prob = getCleaningFailProbWise()[ageIndex];
				break;
			case AVERAGE:
				prob = getCleaningFailProbAverage()[ageIndex];
				break;
			case FOOL:
				prob = getCleaningFailProbFool()[ageIndex];
				break;
			default:
				break;
		}
		if (prob <= 0) {
			prob = 1;
		}
		if (GameRandom.nextInt(prob) != 0) {
			setStubbornlyDirty(true);
		}
	}

	/**
	 * 親を呼んで泣きわめく.
	 */
	public final void callParent() {
		if (!canAction()) {
			dirtyScreamPeriod = 0;
			setCallingParents(false);
			return;
		}

		if (getAttachmentSize(Ants.class) != 0) {
			setHappiness(Happiness.VERY_SAD);
			YukkuriLogic.checkNearParent(this);
			setCallingParents(true);
		}

		if (isDirty()) {
			if (isVeryHungry() || isDamagedHeavily() || isGotBurnedHeavily()) {
				dirtyScreamPeriod = 0;
				setCallingParents(false);
				return;
			}

			boolean kusogaki = (isRude() && isBaby()) || (isChild() && isVeryRude());
			int c = kusogaki ? 20 : 40;
			if (getAge() % c != 0) {
				return;
			}
			if (kusogaki) {
				setHappiness(Happiness.VERY_SAD);
				setPikoMessage(GameMessages.getMessage(this, MessagePool.Action.Dirty), false);
				YukkuriLogic.checkNearParent(this);
				setCallingParents(true);
			} else {
				setHappiness(Happiness.SAD);
				setMessage(GameMessages.getMessage(this, MessagePool.Action.Dirty));
				YukkuriLogic.checkNearParent(this);
				setCallingParents(true);
			}
			dirtyScreamPeriod--;
			if (dirtyScreamPeriod <= 0) {
				cleaningItself();
			}
		}
	}

	/**
	 * うんうんがしたいかどうかを返却する.
	 *
	 * @return うんうんがしたいかどうか
	 */
	public boolean wantToShit() {
		int step = (!isHungry() ? TICK * 2 : TICK);
		int adjust = 50 * (isRude() ? 1 : 2) * shittingDiscipline / (isBaby() ? 2 : 1);
		return (getShitLimitBase()[getAgeState().ordinal()] - shit) < (Const.DIAGONAL * step + adjust);
	}

	/**
	 * 生まれそうかどうかを返却する.
	 *
	 * @return 生まれそうかどうか
	 */
	public boolean nearToBirth() {
		int step = (!isHungry() ? TICK * 2 : TICK);
		int adjust = 100 * (isRude() ? 1 : 2);
		int limit = getPregPeriodBase() - getPregnantPeriod() - (pregnancyPeriodBoost / 2);
		int diagonal = Const.DIAGONAL * step + adjust;
		return limit < diagonal && hasBabyOrStalk();
	}

	/** ストレスフルかどうかを返却する. */
	@Transient
	public boolean isStressful() {
		return getStressLimit() * getNonYukkuriDiseaseTolerance() / 100 * 2 / 5 < getStress();
	}

	/** とてもストレスフルかどうかを返却する. */
	@Transient
	public boolean isVeryStressful() {
		return getStressLimit() * getNonYukkuriDiseaseTolerance() / 100 * 3 / 5 < getStress();
	}

	/**
	 * SocialEntity レイヤーのフィールドを to へコピーする.
	 * 関係リスト (parents/childrenList 等) は深くコピーする.
	 */
	@Override
	public void copyStateTo(Entity to) {
		super.copyStateTo(to);
		SocialEntity s = (SocialEntity) to;
		s.setAttitude(attitude);
		s.setIntelligence(intelligence);
		s.setHappiness(happiness);
		s.setLovePlayer(lovePlayer);
		s.setLovePlayerState(lovePlayerState);
		s.setRank(bodyRank);
		s.setPublicRank(publicRank);
		s.setMemories(memories);
		s.setRapist(rapist);
		s.setSuperRapist(superRapist);
		s.setPenipeniCutted(penipeniCutted);
		s.setLikeWater(likeWater);
		s.setShittingDiscipline(shittingDiscipline);
		s.setExcitingDiscipline(excitingDiscipline);
		s.setFurifuriDiscipline(furifuriDiscipline);
		s.setSpeechDiscipline(speechDiscipline);
		s.setAmaamaDiscipline(amaamaDiscipline);
		s.setPanicType(panicType);
		s.setPanicPeriod(panicPeriod);
		s.setTrauma(trauma);
		s.setPartner(partner);
		s.setParents(parents != null ? parents.clone() : null);
		s.setChildren(childrenList != null ? new LinkedList<>(childrenList) : null);
		s.setElderSisters(elderSisterList != null ? new LinkedList<>(elderSisterList) : null);
		s.setSisters(sisterList != null ? new LinkedList<>(sisterList) : null);
		s.setAncestors(ancestorList != null ? new LinkedList<>(ancestorList) : null);
		s.setFatherRaper(fatherRaper);
		s.setParentLinkId(parentLinkId);
		s.setTargetBind(targetBind);
		s.setPeropero(peropero);
		s.setCallingParents(callingParents);
		s.setForceFace(forceFace);
	}

	/** 汚れ状態を設定する。Yukkuri でオーバーライドして実装する. */
	protected void makeDirty(boolean flag) {}

	/** 睡眠開始時に幸福度を AVERAGE に設定する。 */
	@Override
	protected void onStartSleeping() {
		setHappiness(Happiness.AVERAGE);
	}

	/** パニック解除時に幸福度を SAD に設定する。 */
	@Override
	protected void onClearPanic() {
		setHappiness(Happiness.SAD);
	}

	// --- 性格種別判定 ---

	/** 足りないゆタイプかどうか (デフォルトfalse; サブクラスでオーバーライド) */
	@Transient
	public boolean isIdiot() {
		return false;
	}

	/** ドゲスか */
	@Transient
	public boolean isVeryRude() {
		return attitude == Attitude.SUPER_SHITHEAD;
	}

	/** ゲスまたはドゲスか */
	@Transient
	public boolean isRude() {
		return attitude == Attitude.SHITHEAD || attitude == Attitude.SUPER_SHITHEAD;
	}

	/** 普通か */
	@Transient
	public boolean isNormal() {
		return attitude == Attitude.AVERAGE;
	}

	/** 善良または超善良か */
	@Transient
	public boolean isSmart() {
		return attitude == Attitude.VERY_NICE || attitude == Attitude.NICE;
	}

	// --- れいぱー状態 ---

	/** れいぱーかどうか */
	@Transient
	public final boolean isRaper() {
		if (isUnBirth()) {
			return false;
		}
		return isRapist();
	}

	/** れいぱーを設定する */
	public final void setRaper(boolean b) {
		if (isPenipeniCutted()) {
			setRapist(false);
		} else {
			setRapist(b);
		}
	}

	/** スーパーれいぱーかどうか */
	@Transient
	public final boolean isSuperRaper() {
		if (isUnBirth()) {
			return false;
		}
		if (isPenipeniCutted()) {
			setSuperRapist(false);
		}
		return isSuperRapist();
	}

	/** すーぱーれいぱーを設定する */
	public final void setSuperRaper(boolean b) {
		if (isPenipeniCutted()) {
			setSuperRapist(false);
		} else {
			setSuperRapist(b);
		}
	}

	// --- プレイヤー好感度 ---

	/** プレイヤーが好きか嫌いかを返却する */
	public final LovePlayer checkLovePlayerState() {
		if (getLovePlayer() < -1 * getLovePlayerLimitBase() / 2) {
			return LovePlayer.BAD;
		}
		if (getLovePlayerLimitBase() / 2 < getLovePlayer()) {
			return LovePlayer.GOOD;
		}
		return LovePlayer.NONE;
	}

	// --- ゲス度管理 ---

	/** 強制的にゲス度をいじる */
	public final void plusAttitude(int p) {
		if (isNotChangeCharacter()) {
			return;
		}
		setAttitudePoint(getAttitudePoint() + p);
	}

	/** ゲス度によって性格変更 */
	public final void checkAttitude() {
		if (isNyd() || isIdiot()) {
			setAttitudePoint(0);
			return;
		}
		if (getAttitude() == Attitude.VERY_NICE) {
			setAttitudePoint(0);
			return;
		}
		if (getAttitude() == Attitude.SUPER_SHITHEAD) {
			if (getAttitudePoint() >= getNiceLimit()[0]) {
				setAttitude(Attitude.SHITHEAD);
				setAttitudePoint(0);
			}
			return;
		}
		if (getAttitude() == Attitude.SHITHEAD) {
			if (getAttitudePoint() >= getNiceLimit()[0]) {
				setAttitude(Attitude.AVERAGE);
				setAttitudePoint(0);
			}
		}
	}

	/** 通常時の躾 */
	public final void teachManner(int p) {
		if (!beginDisciplineEmotion()) {
			return;
		}
		disclipline(p * 5);
		boolean flag = false;
		if (isFurifuri() || (isSukkiri() && !isRaper())) {
			flag = true;
		}
		if (isRude()) {
			if (isTalking()) {
				flag = true;
			}
		}
		if (flag) {
			plusAttitude(p);
		}
	}

	/** うんうん、興奮、ふりふり、セリフの抑制をする */
	public void disclipline(int p) {
		if (isExciting() && !isRaper()) {
			excitingDiscipline = excitingDiscipline + (p * 10);
			setCalm();
		} else if (isShitting()) {
			shittingDiscipline = shittingDiscipline + p;
			setShitting(false);
			shit -= getAngryPeriodBase() * 2;
		} else if (isFurifuri()) {
			furifuriDiscipline = furifuriDiscipline + p;
			setFurifuri(false);
		} else if (getMessageBuffer() != null) {
			speechDiscipline = speechDiscipline + (p / 2);
			setMessageBuffer(null);
		}
	}

	/**
	 * しつけ反応の前提条件を確認する.
	 *
	 * @return 反応を継続できるなら true, ふりふりに吸われたなら false
	 */
	private boolean beginDisciplineEmotion() {
		if (isRude() && GameRandom.nextInt(getFurifuriDiscipline() + 1) == 0 && canFurifuri()) {
			setFurifuri(true);
			return false;
		}
		return true;
	}

	/**
	 * しつけ値を経年減衰させる.
	 */
	public final void checkDiscipline() {
		// ゲス餡子脳は自制しない
		if (isRude() && getIntelligence() == Intelligence.FOOL) {
			setShittingDiscipline(0);
			setExcitingDiscipline(0);
			setFurifuriDiscipline(0);
			setSpeechDiscipline(0);
			return;
		}
		// 基本ゲーム内時間12分に1回
		int period = getDeclinePeriodBase();
		// int period = (isRude() ? 1 : 2) * DECLINEPERIOD;
		// 知性による補正
		switch (getIntelligence()) {
			case WISE:
				period = period * 3 / 2;
				break;
			case FOOL:
				period = period * 2;
				break;
			default:
				break;
		}
		// 減衰
		if (getAge() % period == 0) {
			shittingDiscipline--;
			excitingDiscipline--;
			furifuriDiscipline--;
			speechDiscipline--;
			if (shittingDiscipline < 0) {
				shittingDiscipline = 0;
			}
			if (shittingDiscipline > 20) {
				shittingDiscipline = 20;
			}
			if (excitingDiscipline < 0) {
				excitingDiscipline = 0;
			}
			if (furifuriDiscipline < 0) {
				furifuriDiscipline = 0;
			}
			if (furifuriDiscipline > 20) {
				furifuriDiscipline = 20;
			}
			if (speechDiscipline < 0) {
				speechDiscipline = 0;
			}
			if (speechDiscipline > 20) {
				speechDiscipline = 20;
			}
		}
	}

	/**
	 * キリッ！する.
	 *
	 * @param showMessage キリッ！メッセージを出すかどうか
	 */
	public void getInVain(boolean showMessage) {
		if (showMessage) {
			setMessage(GameMessages.getMessage(this, MessagePool.Action.BeVain), 30);
		}
		if (isRude() && GameRandom.nextBoolean()) {
			setForceFace(ImageCode.RUDE.ordinal());
		}
		setBeVain(true);
		addStress(-90);
		stayPurupuru(10);
	}

	// --- 家族関係 ---

	/** 父親IDを返す */
	@Transient
	public int getFather() {
		return getParents()[Parent.PAPA.ordinal()];
	}

	/** 母親IDを返す */
	@Transient
	public int getMother() {
		return getParents()[Parent.MAMA.ordinal()];
	}

	/** 2体の間に家族関係があるか */
	public final boolean isFamily(SocialEntity other) {
		return YukkuriRelations.isFamily(this, other);
	}

	/** other の親か */
	public final boolean isParent(SocialEntity other) {
		return YukkuriRelations.isParent(this, other);
	}

	/** other の父親か */
	public final boolean isFather(SocialEntity other) {
		return YukkuriRelations.isFather(this, other);
	}

	/** other の母親か */
	public final boolean isMother(SocialEntity other) {
		return YukkuriRelations.isMother(this, other);
	}

	/** other の子か */
	public final boolean isChild(SocialEntity other) {
		return YukkuriRelations.isChild(this, other);
	}

	/** other が番か */
	public final boolean isPartner(SocialEntity other) {
		return YukkuriRelations.isPartner(this, other);
	}

	/** other が姉妹か */
	public final boolean isSister(SocialEntity other) {
		return YukkuriRelations.isSister(this, other);
	}

	/** other が妹か (自分が年上) */
	public final boolean isElderSister(SocialEntity other) {
		return YukkuriRelations.isElderSister(this, other);
	}

	// --- 家族リスト操作 ---

	/** 姉妹数を返す。 */
	@Transient
	public int getSistersCount() {
		return getSisters().size();
	}

	/** 姉数を返す。 */
	@Transient
	public int getElderSistersCount() {
		return getElderSisters().size();
	}

	/** 子ゆっくり数を返す。 */
	@Transient
	public int getChildrenCount() {
		if (getChildren() == null) {
			return 0;
		}
		return getChildren().size();
	}

	/** 子ゆっくりをリストに追加する。 */
	public void addChild(SocialEntity at) {
		if (at != null) {
			getChildren().add(at.getUniqueId());
		}
	}

	/** 子ゆっくりをリストから除去する。 */
	public void removeChild(SocialEntity target) {
		YukkuriRelations.removeChild(this, target);
	}

	/** 姉をリストに追加する。 */
	public void addElderSister(SocialEntity at) {
		if (at != null) {
			getElderSisters().add(at.getUniqueId());
		}
	}

	/** 姉をリストから除去する。 */
	public void removeElderSister(SocialEntity target) {
		YukkuriRelations.removeElderSister(this, target);
	}

	/** 姉妹をリストに追加する。 */
	public void addSister(SocialEntity at) {
		if (at != null) {
			getSisters().add(at.getUniqueId());
		}
	}

	/** 姉妹をリストから除去する。 */
	public void removeSister(SocialEntity target) {
		YukkuriRelations.removeSister(this, target);
	}

}
