package src.entity.living;

import java.beans.Transient;
import java.util.LinkedList;
import java.util.List;
import src.base.Entity;
import src.enums.Attitude;
import src.enums.BodyRank;
import src.enums.Happiness;
import src.enums.Intelligence;
import src.enums.LovePlayer;
import src.enums.Parent;
import src.enums.PublicRank;
import src.logic.BodyRelations;

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
	protected BodyRank bodyRank = BodyRank.KAIYU;
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
	/** 興奮抑制 */
	protected int excitingDiscipline = 0;
	/** ふりふり抑制 */
	protected int furifuriDiscipline = 0;
	/** おしゃべり抑制 */
	protected int speechDiscipline = 0;
	/** あまあまへの慣れ具合 */
	protected int amaamaDiscipline = 0;

	/** 性格 counter indicating shithead/nicehead etc. を取得する. @return 性格 counter indicating shithead/nicehead etc. */
	public Attitude getAttitude() { return attitude; }
	/** 性格 counter indicating shithead/nicehead etc. を設定する. @param attitude 性格 counter indicating shithead/nicehead etc. */
	public void setAttitude(Attitude attitude) { this.attitude = attitude; }

	/** 知性 を取得する. @return 知性 */
	public Intelligence getIntelligence() { return intelligence; }
	/** 知性 を設定する. @param intelligence 知性 */
	public void setIntelligence(Intelligence intelligence) { this.intelligence = intelligence; }

	/** 幸福度 を取得する. @return 幸福度 */
	public Happiness getHappiness() { return happiness; }
	/** 幸福度 を設定する. @param happiness 幸福度 */
	public void setHappiness(Happiness happiness) { this.happiness = happiness; }
	/** 喜んでいるか. @return 喜んでいるかどうか */
	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isHappy() { return !dead && (happiness == Happiness.HAPPY || happiness == Happiness.VERY_HAPPY); }
	/** 悲しんでいるか. @return 悲しんでいるかどうか */
	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isSad() { return !dead && happiness == Happiness.SAD; }
	/** とても悲しんでいるか. @return とても悲しんでいるかどうか */
	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isVerySad() { return !dead && happiness == Happiness.VERY_SAD; }
	/** 悲しんでいるか（SAD or VERY_SAD）. @return 悲しんでいるかどうか */
	@com.fasterxml.jackson.annotation.JsonIgnore
	public boolean isUnhappy() { return !dead && (happiness == Happiness.SAD || happiness == Happiness.VERY_SAD); }

	/** プレイヤーへのなつき度 を取得する. @return プレイヤーへのなつき度 */
	public int getLovePlayer() { return lovePlayer; }
	/** プレイヤーへのなつき度 を設定する. @param lovePlayer プレイヤーへのなつき度 */
	public void setLovePlayer(int lovePlayer) { this.lovePlayer = lovePlayer; }

	/** プレイヤーへのなつき度概算 を取得する. @return プレイヤーへのなつき度概算 */
	public LovePlayer getLovePlayerState() { return lovePlayerState; }
	/** プレイヤーへのなつき度概算 を設定する. @param lovePlayerState プレイヤーへのなつき度概算 */
	public void setLovePlayerState(LovePlayer lovePlayerState) { this.lovePlayerState = lovePlayerState; }

	/** 飼いゆ、野良ゆなどのランク を取得する. @return 飼いゆ、野良ゆなどのランク */
	public BodyRank getBodyRank() { return bodyRank; }
	/** 飼いゆ、野良ゆなどのランク を設定する. @param bodyRank 飼いゆ、野良ゆなどのランク */
	public void setBodyRank(BodyRank bodyRank) { this.bodyRank = bodyRank; }

	/** 群れ内のうんうん奴隷などのランク を取得する. @return 群れ内のうんうん奴隷などのランク */
	public PublicRank getPublicRank() { return publicRank; }
	/** 群れ内のうんうん奴隷などのランク を設定する. @param publicRank 群れ内のうんうん奴隷などのランク */
	public void setPublicRank(PublicRank publicRank) { this.publicRank = publicRank; }

	/** 思い出（悪夢関連） を取得する. @return 思い出（悪夢関連） */
	public int getMemories() { return memories; }
	/** 思い出（悪夢関連） を設定する. @param memories 思い出（悪夢関連） */
	public void setMemories(int memories) { this.memories = memories; }

	/** レイパー化有無 を取得する. @return レイパー化有無 */
	public boolean isRapist() { return rapist; }
	/** レイパー化有無 を設定する. @param rapist レイパー化有無 */
	public void setRapist(boolean rapist) { this.rapist = rapist; }

	/** バイゆグラでレイパーになる、すーぱーれいぱー状態 を取得する. @return バイゆグラでレイパーになる、すーぱーれいぱー状態 */
	public boolean isSuperRapist() { return superRapist; }
	/** バイゆグラでレイパーになる、すーぱーれいぱー状態 を設定する. @param superRapist バイゆグラでレイパーになる、すーぱーれいぱー状態 */
	public void setSuperRapist(boolean superRapist) { this.superRapist = superRapist; }

	/** うんうん抑制 を取得する. @return うんうん抑制 */
	public int getShittingDiscipline() { return shittingDiscipline; }
	/** うんうん抑制 を設定する. @param shittingDiscipline うんうん抑制 */
	public void setShittingDiscipline(int shittingDiscipline) { this.shittingDiscipline = shittingDiscipline; }

	/** 興奮抑制 を取得する. @return 興奮抑制 */
	public int getExcitingDiscipline() { return excitingDiscipline; }
	/** 興奮抑制 を設定する. @param excitingDiscipline 興奮抑制 */
	public void setExcitingDiscipline(int excitingDiscipline) { this.excitingDiscipline = excitingDiscipline; }

	/** ふりふり抑制 を取得する. @return ふりふり抑制 */
	public int getFurifuriDiscipline() { return furifuriDiscipline; }
	/** ふりふり抑制 を設定する. @param furifuriDiscipline ふりふり抑制 */
	public void setFurifuriDiscipline(int furifuriDiscipline) { this.furifuriDiscipline = furifuriDiscipline; }

	/** おしゃべり抑制 を取得する. @return おしゃべり抑制 */
	public int getSpeechDiscipline() { return speechDiscipline; }
	/** おしゃべり抑制 を設定する. @param speechDiscipline おしゃべり抑制 */
	public void setSpeechDiscipline(int speechDiscipline) { this.speechDiscipline = speechDiscipline; }

	/** あまあまへの慣れ具合 を取得する. @return あまあまへの慣れ具合 */
	public int getAmaamaDiscipline() { return amaamaDiscipline; }
	/** あまあまへの慣れ具合 を設定する. @param amaamaDiscipline あまあまへの慣れ具合 */
	public void setAmaamaDiscipline(int amaamaDiscipline) { this.amaamaDiscipline = amaamaDiscipline; }

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
	public int getPartner() { return partner; }
	/** パートナーのID を設定する. @param partner パートナーのID */
	public void setPartner(int partner) { this.partner = partner; }

	/** 親のIDペア を取得する. @return 親のIDペア */
	public int[] getParents() { return parents; }
	/** 親のIDペア を設定する. @param parents 親のIDペア */
	public void setParents(int[] parents) { this.parents = parents; }

	/** 子供のIDリスト を取得する. @return 子供のIDリスト */
	public List<Integer> getChildrenList() { return childrenList; }
	/** 子供のIDリスト を設定する. @param childrenList 子供のIDリスト */
	public void setChildrenList(List<Integer> childrenList) { this.childrenList = childrenList; }

	/** 姉ゆのIDリスト を取得する. @return 姉ゆのIDリスト */
	public List<Integer> getElderSisterList() { return elderSisterList; }
	/** 姉ゆのIDリスト を設定する. @param elderSisterList 姉ゆのIDリスト */
	public void setElderSisterList(List<Integer> elderSisterList) { this.elderSisterList = elderSisterList; }

	/** 妹ゆのIDリスト を取得する. @return 妹ゆのIDリスト */
	public List<Integer> getSisterList() { return sisterList; }
	/** 妹ゆのIDリスト を設定する. @param sisterList 妹ゆのIDリスト */
	public void setSisterList(List<Integer> sisterList) { this.sisterList = sisterList; }

	/** 先祖のIDリスト を取得する. @return 先祖のIDリスト */
	public List<Integer> getAncestorList() { return ancestorList; }
	/** 先祖のIDリスト を設定する. @param ancestorList 先祖のIDリスト */
	public void setAncestorList(List<Integer> ancestorList) { this.ancestorList = ancestorList; }

	/** 父親にレイプされた経験があるか を取得する. @return 父親にレイプされた経験があるか */
	public boolean isFatherRaper() { return fatherRaper; }
	/** 父親にレイプされた経験があるか を設定する. @param fatherRaper 父親にレイプされた経験があるか */
	public void setFatherRaper(boolean fatherRaper) { this.fatherRaper = fatherRaper; }

	/** 親リンクのID を取得する. @return 親リンクのID */
	public int getParentLinkId() { return parentLinkId; }
	/** 親リンクのID を設定する. @param parentLinkId 親リンクのID */
	public void setParentLinkId(int parentLinkId) { this.parentLinkId = parentLinkId; }

	/** 対象を呼び止めるほど強い動機を持っているかどうか */
	protected boolean targetBind = false;
	/** 対象を呼び止めるほど強い動機を持っているかどうか を取得する. @return 対象を呼び止めるほど強い動機を持っているかどうか */
	public boolean isTargetBind() { return targetBind; }
	/** 対象を呼び止めるほど強い動機を持っているかどうか を設定する. @param targetBind 対象を呼び止めるほど強い動機を持っているかどうか */
	public void setTargetBind(boolean targetBind) { this.targetBind = targetBind; }
	/** ぺろぺろ中 */
	protected boolean peropero = false;
	/** ぺろぺろ中 を取得する. @return ぺろぺろ中 */
	public boolean isPeropero() { return !dead && peropero; }
	/** ぺろぺろ中 を設定する. @param peropero ぺろぺろ中 */
	public void setPeropero(boolean peropero) { this.peropero = peropero; }
	/** 親を呼んで泣き叫び中 */
	protected boolean callingParents = false;
	/** 親を呼んで泣き叫び中 を取得する. @return 親を呼んで泣き叫び中 */
	public boolean isCallingParents() { return !dead && callingParents; }
	/** 親を呼んで泣き叫び中 を設定する. @param callingParents 親を呼んで泣き叫び中 */
	public void setCallingParents(boolean callingParents) { this.callingParents = callingParents; }
	/** 表情の強制設定 */
	protected int forceFace = -1;
	/** 表情の強制設定 を取得する. @return 表情の強制設定 */
	public int getForceFace() { return forceFace; }
	/** 表情の強制設定 を設定する. @param forceFace 表情の強制設定 */
	public void setForceFace(int forceFace) { this.forceFace = forceFace; }

	// ===== Step6-2: BodyAttributes から移動したメソッド群 =====

	public void addLovePlayer(int val) {
		if (isNYD()) {
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

	public final void addMemories(int memoryDelta) {
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
	 * SocialEntity レイヤーのフィールドを to へコピーする.
	 * 関係リスト (parents/childrenList 等) は深くコピーする.
	 */
	@Override
	public void copyStateTo(src.base.Entity to) {
		super.copyStateTo(to);
		SocialEntity s = (SocialEntity) to;
		s.setAttitude(attitude); s.setIntelligence(intelligence); s.setHappiness(happiness);
		s.setLovePlayer(lovePlayer); s.setLovePlayerState(lovePlayerState);
		s.setBodyRank(bodyRank); s.setPublicRank(publicRank);
		s.setMemories(memories); s.setRapist(rapist); s.setSuperRapist(superRapist);
		s.setShittingDiscipline(shittingDiscipline); s.setExcitingDiscipline(excitingDiscipline);
		s.setFurifuriDiscipline(furifuriDiscipline); s.setSpeechDiscipline(speechDiscipline);
		s.setAmaamaDiscipline(amaamaDiscipline);
		s.setPartner(partner);
		s.setParents(parents != null ? parents.clone() : null);
		s.setChildrenList(childrenList != null ? new LinkedList<>(childrenList) : null);
		s.setElderSisterList(elderSisterList != null ? new LinkedList<>(elderSisterList) : null);
		s.setSisterList(sisterList != null ? new LinkedList<>(sisterList) : null);
		s.setAncestorList(ancestorList != null ? new LinkedList<>(ancestorList) : null);
		s.setFatherRaper(fatherRaper); s.setParentLinkId(parentLinkId);
		s.setTargetBind(targetBind); s.setPeropero(peropero);
		s.setCallingParents(callingParents); s.setForceFace(forceFace);
	}

	// --- 性格種別判定 ---

	/** 足りないゆタイプかどうか (デフォルトfalse; サブクラスでオーバーライド) */
	@Transient
	public boolean isIdiot() { return false; }

	/** ドゲスか */
	@Transient
	public boolean isVeryRude() { return attitude == Attitude.SUPER_SHITHEAD; }

	/** ゲスまたはドゲスか */
	@Transient
	public boolean isRude() { return attitude == Attitude.SHITHEAD || attitude == Attitude.SUPER_SHITHEAD; }

	/** 普通か */
	@Transient
	public boolean isNormal() { return attitude == Attitude.AVERAGE; }

	/** 善良または超善良か */
	@Transient
	public boolean isSmart() { return attitude == Attitude.VERY_NICE || attitude == Attitude.NICE; }

	// --- れいぱー状態 ---

	/** れいぱーかどうか */
	@Transient
	public final boolean isRaper() {
		if (isUnBirth()) return false;
		return isRapist();
	}

	/** れいぱーを設定する */
	public final void setRaper(boolean b) {
		if (isPenipeniCutted()) setRapist(false);
		else setRapist(b);
	}

	/** スーパーれいぱーかどうか */
	@Transient
	public final boolean isSuperRaper() {
		if (isUnBirth()) return false;
		if (isPenipeniCutted()) setSuperRapist(false);
		return isSuperRapist();
	}

	/** すーぱーれいぱーを設定する */
	public final void setSuperRaper(boolean b) {
		if (isPenipeniCutted()) setSuperRapist(false);
		else setSuperRapist(b);
	}

	// --- プレイヤー好感度 ---

	/** プレイヤーが好きか嫌いかを返却する */
	public final LovePlayer checkLovePlayerState() {
		if (getLovePlayer() < -1 * getLovePlayerLimitBase() / 2) return LovePlayer.BAD;
		if (getLovePlayerLimitBase() / 2 < getLovePlayer()) return LovePlayer.GOOD;
		return LovePlayer.NONE;
	}

	// --- ゲス度管理 ---

	/** 強制的にゲス度をいじる */
	public final void plusAttitude(int p) {
		if (isNotChangeCharacter()) return;
		setAttitudePoint(getAttitudePoint() + p);
	}

	/** ゲス度によって性格変更 */
	public final void checkAttitude() {
		if (isNYD() || isIdiot()) { setAttitudePoint(0); return; }
		if (getAttitude() == Attitude.VERY_NICE) { setAttitudePoint(0); return; }
		if (getAttitude() == Attitude.SUPER_SHITHEAD) {
			if (getAttitudePoint() >= getNiceLimit()[0]) { setAttitude(Attitude.SHITHEAD); setAttitudePoint(0); }
			return;
		}
		if (getAttitude() == Attitude.SHITHEAD) {
			if (getAttitudePoint() >= getNiceLimit()[0]) { setAttitude(Attitude.AVERAGE); setAttitudePoint(0); }
		}
	}

	/** 通常時の躾 */
	public final void teachManner(int p) {
		disclipline(p * 5);
		boolean flag = false;
		if (isFurifuri() || (isSukkiri() && !isRaper())) flag = true;
		if (isRude()) { if (isTalking()) flag = true; }
		if (flag) plusAttitude(p);
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

	// --- 家族関係 ---

	/** 父親IDを返す */
	@Transient
	public int getFather() { return getParents()[Parent.PAPA.ordinal()]; }

	/** 母親IDを返す */
	@Transient
	public int getMother() { return getParents()[Parent.MAMA.ordinal()]; }

	/** 2体の間に家族関係があるか */
	public final boolean isFamily(SocialEntity other) { return BodyRelations.isFamily(this, other); }

	/** other の親か */
	public final boolean isParent(SocialEntity other) { return BodyRelations.isParent(this, other); }

	/** other の父親か */
	public final boolean isFather(SocialEntity other) { return BodyRelations.isFather(this, other); }

	/** other の母親か */
	public final boolean isMother(SocialEntity other) { return BodyRelations.isMother(this, other); }

	/** other の子か */
	public final boolean isChild(SocialEntity other) { return BodyRelations.isChild(this, other); }

	/** other が番か */
	public final boolean isPartner(SocialEntity other) { return BodyRelations.isPartner(this, other); }

	/** other が姉妹か */
	public final boolean isSister(SocialEntity other) { return BodyRelations.isSister(this, other); }

	/** other が妹か (自分が年上) */
	public final boolean isElderSister(SocialEntity other) { return BodyRelations.isElderSister(this, other); }

	// --- 家族リスト操作 ---

	@Transient
	public int getSisterListSize() { return getSisterList().size(); }

	@Transient
	public int getElderSisterListSize() { return getElderSisterList().size(); }

	@Transient
	public int getChildrenListSize() {
		if (getChildrenList() == null) return 0;
		return getChildrenList().size();
	}

	public void addChildrenList(SocialEntity at) {
		if (at != null) getChildrenList().add(at.getUniqueID());
	}

	public void removeChildrenList(SocialEntity target) {
		BodyRelations.removeChildrenList(this, target);
	}

	public void addElderSisterList(SocialEntity at) {
		if (at != null) getElderSisterList().add(at.getUniqueID());
	}

	public void removeElderSisterList(SocialEntity target) {
		BodyRelations.removeElderSisterList(this, target);
	}

	public void addSisterList(SocialEntity at) {
		if (at != null) getSisterList().add(at.getUniqueID());
	}

	public void removeSisterList(SocialEntity target) {
		BodyRelations.removeSisterList(this, target);
	}

}
