package src.entity.core.living.yukkuri;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import src.Const;
import src.SimYukkuri;
import src.draw.Dimension4y;
import src.draw.MyPane;
import src.draw.Point4y;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.entity.core.Entity;
import src.entity.core.attachment.Attachment;
import src.entity.core.attachment.impl.AccelAmpoule;
import src.entity.core.attachment.impl.StopAmpoule;
import src.entity.core.living.SocialEntity;
import src.entity.core.world.bodylinked.Okazari;
import src.entity.core.world.bodylinked.Okazari.OkazariType;
import src.entity.core.world.bodylinked.Stalk;
import src.entity.core.world.item.Food;
import src.enums.AgeState;
import src.enums.Attitude;
import src.enums.BodyRank;
import src.enums.BurialState;
import src.enums.Burst;
import src.enums.CoreAnkoState;
import src.enums.CriticalDamegeType;
import src.enums.Damage;
import src.enums.Direction;
import src.enums.Event;
import src.enums.FavItemType;
import src.enums.FootBake;
import src.enums.HairState;
import src.enums.PredatorType;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.enums.Numbering;
import src.enums.PanicType;
import src.enums.Parent;
import src.enums.PublicRank;
import src.enums.PurposeOfMoving;
import src.enums.TakeoutItemType;
import src.enums.TangType;
import src.enums.Type;
import src.enums.UnbirthBabyState;
import src.enums.Where;
import src.enums.WindowType;
import src.enums.YukkuriType;
import src.event.EventPacket;
import src.field.impl.Pool;
import src.logic.BodyCoreStateRule;
import src.logic.BodyEventState;
import src.logic.BodyExcretionRule;
import src.logic.BodyMovement;
import src.logic.BodyRelations;
import src.system.BodyLayer;
import src.system.ItemMenu.GetMenuTarget;
import src.system.ItemMenu.UseMenuTarget;
import src.system.MainCommandUI;
import src.system.MessagePool;
import src.system.Sprite;
import src.util.GameEnvironment;
import src.util.GameLocale;
import src.util.GameMessages;
import src.util.GameRandom;
import src.util.GameText;
import src.util.GameView;
import src.util.GameWorld;
import src.util.IniFileUtil;
import src.util.ListUtil;

/*********************************************************
 * ゆっくり本体の元となる抽象クラス（動作のみ。）
 * 属性に関しては親クラスのBodyAttributesにすべて定義。
 *
 * まりさとれいむは特殊なため各クラスで"public int getBodyBaseImage(BodyLayer
 * layer)"をオーバーライドしているため要確認
 * 暇なときの挙動は"public void
 * killTime()"にまとめてあるので、各種ごとに固有の挙動を追加したいときはそれを各種classでオーバーライドしてください。
 * (現在オーバーライドしている種：れいむ、ありす、ふらん、れみりゃ)
 * キホン～logic系のループは"Terrarium.java"にあり
 * "ToyLogic"をobjループで呼び出すだけだと頻度が低いので、"ToyLogic"をインポート、"killtime()"内で一定確率で"ToyLogic"内のおもちゃで遊ぶ処理を呼び出すようにしてある。objループからは削除
 */
@JsonTypeInfo(use = Id.CLASS)
public abstract class Yukkuri extends SocialEntity {
	private static final long serialVersionUID = 8856385435939508588L;

	@JsonIgnore
	private transient YukkuriSprite spriteDelegate;

	@JsonIgnore
	private transient YukkuriMessage messageDelegate;

	@JsonIgnore
	private transient YukkuriPlayerRelation playerRelationDelegate;

	@JsonIgnore
	private transient YukkuriOtherRelationDelegate otherRelationDelegate;

	@JsonIgnore
	private transient YukkuriDamageDelegate damageDelegate;

	@JsonIgnore
	private transient YukkuriShitDelegate shitDelegate;

	@JsonIgnore
	private transient YukkuriCarryDelegate carryDelegate;

	@JsonIgnore
	private transient YukkuriAdornmentDelegate adornmentDelegate;

	@JsonIgnore
	private transient YukkuriEmotionDelegate emotionDelegate;

	@JsonIgnore
	private transient YukkuriMoveDelegate moveDelegate;

	@JsonIgnore
	private transient YukkuriStateDelegate stateDelegate;

	@JsonIgnore
	private transient YukkuriNydDelegate nydDelegate;

	@JsonIgnore
	private transient YukkuriSexualDelegate sexualDelegate;

	@JsonIgnore
	private transient YukkuriEventDelegate eventDelegate;

	@JsonIgnore
	private transient YukkuriStalkDelegate stalkDelegate;

	@JsonIgnore
	private transient YukkuriAbuseDelegate abuseDelegate;

	@JsonIgnore
	private transient YukkuriFamilyDelegate familyDelegate;

	// Delegate の lazy init はスレッドセーフではない。
	// このゲームはシングルスレッド前提（Swingイベントスレッドのみ）なので意図的に synchronized なし。

	private YukkuriSprite spriteDelegate() {
		if (spriteDelegate == null) {
			spriteDelegate = new YukkuriSprite(this);
		}
		return spriteDelegate;
	}

	private YukkuriMessage messageDelegate() {
		if (messageDelegate == null) {
			messageDelegate = new YukkuriMessage(this);
		}
		return messageDelegate;
	}

	private YukkuriPlayerRelation playerRelationDelegate() {
		if (playerRelationDelegate == null) {
			playerRelationDelegate = new YukkuriPlayerRelation(this);
		}
		return playerRelationDelegate;
	}

	private YukkuriOtherRelationDelegate otherRelationDelegate() {
		if (otherRelationDelegate == null) {
			otherRelationDelegate = new YukkuriOtherRelationDelegate(this);
		}
		return otherRelationDelegate;
	}

	private YukkuriDamageDelegate damageDelegate() {
		if (damageDelegate == null) {
			damageDelegate = new YukkuriDamageDelegate(this);
		}
		return damageDelegate;
	}

	private YukkuriShitDelegate shitDelegate() {
		if (shitDelegate == null) {
			shitDelegate = new YukkuriShitDelegate(this);
		}
		return shitDelegate;
	}

	private YukkuriCarryDelegate carryDelegate() {
		if (carryDelegate == null) {
			carryDelegate = new YukkuriCarryDelegate(this);
		}
		return carryDelegate;
	}

	private YukkuriAdornmentDelegate adornmentDelegate() {
		if (adornmentDelegate == null) {
			adornmentDelegate = new YukkuriAdornmentDelegate(this);
		}
		return adornmentDelegate;
	}

	private YukkuriEmotionDelegate emotionDelegate() {
		if (emotionDelegate == null) {
			emotionDelegate = new YukkuriEmotionDelegate(this);
		}
		return emotionDelegate;
	}

	private YukkuriMoveDelegate moveDelegate() {
		if (moveDelegate == null) {
			moveDelegate = new YukkuriMoveDelegate(this);
		}
		return moveDelegate;
	}

	private YukkuriStateDelegate stateDelegate() {
		if (stateDelegate == null) {
			stateDelegate = new YukkuriStateDelegate(this);
		}
		return stateDelegate;
	}

	private YukkuriNydDelegate nydDelegate() {
		if (nydDelegate == null) {
			nydDelegate = new YukkuriNydDelegate(this);
		}
		return nydDelegate;
	}

	private YukkuriSexualDelegate sexualDelegate() {
		if (sexualDelegate == null) {
			sexualDelegate = new YukkuriSexualDelegate(this);
		}
		return sexualDelegate;
	}

	private YukkuriEventDelegate eventDelegate() {
		if (eventDelegate == null) {
			eventDelegate = new YukkuriEventDelegate(this);
		}
		return eventDelegate;
	}

	private YukkuriStalkDelegate stalkDelegate() {
		if (stalkDelegate == null) {
			stalkDelegate = new YukkuriStalkDelegate(this);
		}
		return stalkDelegate;
	}

	private YukkuriAbuseDelegate abuseDelegate() {
		if (abuseDelegate == null) {
			abuseDelegate = new YukkuriAbuseDelegate(this);
		}
		return abuseDelegate;
	}

	private YukkuriFamilyDelegate familyDelegate() {
		if (familyDelegate == null) {
			familyDelegate = new YukkuriFamilyDelegate(this);
		}
		return familyDelegate;
	}

	/**
	 * ゆ虐神拳を受けてドス等にトランスフォーム可能かどうかを返却する.
	 * まりさ/れいむの一部でtrue.
	 * トランスフォーム可能な子クラスでオーバーライドする.
	 * 
	 * @return ゆ虐神拳を受けてドス等にトランスフォーム可能かどうか
	 */
	public boolean judgeCanTransForGodHand() {
		return false;
	}

	/**
	 * (ゆ虐神拳を受けて)ドス等にトランスフォームする。
	 * まりさ/れいむで実行可.
	 * トランスフォーム可能な子クラスでオーバーライドする.
	 */
	public void execTransform() {
		return;
	}

	/**
	 * ハイブリッドのタイプを返却する.
	 * 子クラスでオーバーライドする.
	 * 
	 * @param partnerType パートナーのタイプ
	 * @return ハイブリッドのタイプ
	 */
	public YukkuriType getHybridType(YukkuriType partnerType) {
		return getType();
	}

	/**
	 * 突然変異チェックをする.
	 * 現状 れいむ→でいぶ/まりさ→どす のみ.
	 * 突然変異可能な子クラスでオーバーライドする.
	 * 
	 * @return 突然変異する際のゆっくりのインスタンス
	 */
	public Yukkuri checkTransform() {
		return null;
	}

	@Override
	public String toString() {
		String name = GameLocale.isJapanese() ? getNameJ() : getNameE();
		StringBuilder ret = new StringBuilder(name);
		if (isUnBirth()) {
			ret.append("(" + GameText.read("base_fruit") + ")");
		} else {
			ret.append(" (" + getBodyAgeState().getName() + ")");
		}

		return ret.toString();
	}

	/**
	 * うにょ機能が使用されるゆっくりのアクション
	 *
	 * @return 現在の状態でうにょ機能を適用できるかどうか
	 */
	@Transient
	public boolean isUnyoActionAll() {
		return spriteDelegate().isUnyoActionAll();
	}

	/**
	 * Adjusts unyo offsets based on input deltas.
	 *
	 * @param x x offset
	 * @param y y offset
	 * @param z z offset
	 */
	public void changeUnyo(int x, int y, int z) {
		spriteDelegate().changeUnyo(x, y, z);
	}

	/**
	 * Eases unyo offsets back toward neutral.
	 */
	public void changeReUnyo() {
		spriteDelegate().changeReUnyo();
	}

	/**
	 * Resets unyo offsets to neutral.
	 */
	public void resetUnyo() {
		spriteDelegate().resetUnyo();
	}

	/**
	 * 行動・イベントの取り消し
	 */
	public void clearActions() {
		eventDelegate().clearActions();
	}

	/**
	 * イベントをクリアする.
	 */
	public void clearEvent() {
		eventDelegate().clearEvent();
	}

	/**
	 * イベントのためのアクションのみのクリア
	 */
	public void clearActionsForEvent() {
		eventDelegate().clearActionsForEvent();
	}

	/**
	 * 強制的に寝かせる.
	 */
	public void forceToSleep() {
		eventDelegate().forceToSleep();
	}

	/**
	 * うんうん関連処理.
	 * trueを返すとゆっくりは動かない。直後うんうん動作をしたりするときに使用する.
	 * shitting = trueでうんうん動作をし、shit=0 にするとうんうんが出される.
	 * また、うんうんの時間経過加算もここで行う.
	 * 
	 * @return このあと動くかどうか
	 */
	public boolean checkShit() {
		return shitDelegate().checkShit();
	}

	/**
	 * プレイヤーにすりすりされたときの処理.
	 * 
	 * 
	 * @return 感情処理を終えるかどうか
	 */
	public boolean doSurisuriByPlayer() {
		return playerRelationDelegate().doSurisuriByPlayer();
	}

	/**
	 * 自身の状態に対する反応を記述する.
	 */
	public void checkEmotion() {
		emotionDelegate().checkEmotion();
	}

	/**
	 * 非ゆっくり症チェック
	 * 
	 * @return その後の処理をキャンセルするかどうか
	 */
	public boolean checkNonYukkuriDisease() {
		return nydDelegate().checkNonYukkuriDisease();
	}

	/**
	 * ゆっくりしてる時のアクション.
	 * 個別の動作がある種ははこれをオーバーライドしているので注意.
	 */
	public void killTime() {
		eventDelegate().killTime();
	}

	/**
	 * ゆかびに感染している際の基本反応
	 */
	public void checkSick() {
		stateDelegate().checkSick();
	}

	/**
	 * うにょ機能。
	 * ゆっくりがうにょうにょ動く機能、のようだ。
	 * 重いため、シムゆっくり起動時にチェックボックスで機能をONにするかどうかを決めることができる。
	 */
	public void checkUnyo() {
		if (SimYukkuri.UNYO) {
			if (getAge() % 9 == 0) {
				if (!isDead() && !isLockmove()) {
					if (getCriticalDamegeType() != CriticalDamegeType.CUT && !grabbed && !isPealed() && !isPacked()) {
						if (!isUnyoActionAll() && !isSleeping()) {
							if (!canflyCheck()) {
								if (getFootBakeLevel() == FootBake.NONE &&
										!isDamaged() && !isSick() && !isFeelPain()
										&& takeMappedObj(getParentLinkId()) == null
										&& !isPeropero() && !(isEating() && !isPikopiko())) {
									changeUnyo(0, 0,
											(int) (GameRandom
													.nextInt(((int) UNYOSTRENGTH[getBodyAgeState().ordinal()] / 3)))
													+ UNYOSTRENGTH[getBodyAgeState().ordinal()]);
								}
							} else if (z == 0) {
								if (getFootBakeLevel() == FootBake.NONE &&
										!isDamaged() && !isSick() && !isFeelPain()
										&& takeMappedObj(getParentLinkId()) == null
										&& !isPeropero() && !(isEating() && !isPikopiko())) {
									changeUnyo(0, 0,
											(int) (GameRandom
													.nextInt(((int) UNYOSTRENGTH[getBodyAgeState().ordinal()] / 3)))
													+ UNYOSTRENGTH[getBodyAgeState().ordinal()]);
								}
							}
						}
					}
				}
			}
			if (GameRandom.nextInt(30) == 0 && (isSleeping() ? GameRandom.nextBoolean() : true)) {
				changeUnyo((int) (GameRandom.nextInt(2)), (int) (GameRandom.nextInt(2)),
						(int) (GameRandom.nextInt(2)));
			}
			if (isDamaged() ? (GameRandom.nextInt(5) == 0) : true) {
				changeReUnyo();
			}
		}
	}

	@Override
	protected void onPealed() { stateDelegate().onPealed(); }

	@Override
	protected void onPacked() { stateDelegate().onPacked(); }

	@Override
	protected void onCarAccident() { abuseDelegate().strikeByPress(); }

	@Override
	protected void onPoisonSteam() { stateDelegate().onPoisonSteam(); }

	@Override
	protected void onCutDamageReaction() { stateDelegate().onCutDamageReaction(); }

	@Override
	protected void onInjuredScream(int x, int y) { stateDelegate().onInjuredScream(x, y); }

	@Override
	protected void onNightmare(boolean nightmare) { stateDelegate().onNightmare(nightmare); }

	@Override
	protected void onWakeByHunger() { stateDelegate().onWakeByHunger(); }

	@Override
	protected void onWakeupNaturally() { stateDelegate().onWakeupNaturally(); }

	@Override
	public void onChildStateNotify(UnbirthBabyState state, boolean childDead) {
		stalkDelegate().onChildStateNotify(state, childDead);
	}

	/**
	 * メッセージを出すかどうか.
	 */
	public void checkMessage() {
		stateDelegate().checkMessage();
	}

	/**
	 * ランダムに方向を決定する.
	 * 
	 * @param curDir 現在の方向の数字
	 * @return 方向の数字
	 */
	public final int randomDirection(int curDir) {
		return moveDelegate().randomDirection(curDir);
	}

	/**
	 * 行動範囲と比べ、方向を決定する.
	 * 
	 * @param curPos  現在の位置
	 * @param destPos 目的地の位置
	 * @param range   範囲
	 * @return よいかどうかの数値
	 */
	public int decideDirection(int curPos, int destPos, int range) {
		return moveDelegate().decideDirection(curPos, destPos, range);
	}

	/**
	 * 茎の位置等を更新する.
	 */
	public void upDate() {
		moveDelegate().upDate();
	}

	/**
	 * ゆっくりを動かす.
	 * 
	 * @param dontMove 動けない場合
	 */
	public void moveBody(boolean dontMove) {
		moveDelegate().moveBody(dontMove);
	}

	/**
	 * 標準のメッセージ表示
	 * 
	 * @param message メッセージ
	 */
	public void setMessage(String message) {
		messageDelegate().setMessage(message);
	}

	/**
	 * ピコピコメッセージ表示
	 * 
	 * @param message   メッセージ
	 * @param interrupt 現在メッセージ中でも割り込むかどうか
	 */
	public void setPikoMessage(String message, boolean interrupt) {
		messageDelegate().setPikoMessage(message, interrupt);
	}

	/**
	 * ピコピコメッセージ表示(時間指定)
	 * 
	 * @param message   メッセージ
	 * @param count     メッセージ時間
	 * @param interrupt 現在メッセージ中でも割り込むかどうか
	 */
	public void setPikoMessage(String message, int count, boolean interrupt) {
		messageDelegate().setPikoMessage(message, count, interrupt);
	}

	/**
	 * 時間指定メッセージ表示
	 * 
	 * @param message メッセージ
	 * @param count   メッセージ時間
	 */
	public void setMessage(String message, int count) {
		messageDelegate().setMessage(message, count);
	}

	/**
	 * 割り込み指定メッセージ表示
	 * 
	 * @param message   メッセージ
	 * @param interrupt 現在メッセージ中でも割り込むかどうか
	 */
	public void setMessage(String message, boolean interrupt) {
		messageDelegate().setMessage(message, interrupt);
	}

	/**
	 * 全指定メッセージ表示
	 * 
	 * @param message   メッセージ
	 * @param count     メッセージ時間
	 * @param interrupt 現在メッセージ中でも割り込むかどうか
	 * @param piko      ピコピコするかどうか
	 */
	public void setMessage(String message, int count, boolean interrupt, boolean piko) {
		messageDelegate().setMessage(message, count, interrupt, piko);
	}

	/**
	 * ワールドイベント発生メッセージ
	 * 
	 * @param message メッセージ
	 * @param count   メッセージ時間
	 */
	public void setWorldEventSendMessage(String message, int count) {
		messageDelegate().setWorldEventSendMessage(message, count);
	}

	/**
	 * ワールドイベント応答メッセージ
	 * 
	 * @param message   メッセージ
	 * @param count     メッセージ時間
	 * @param interrupt 現在メッセージ中でも割り込むかどうか
	 * @param piko      ピコピコするかどうか
	 */
	public void setWorldEventResMessage(String message, int count, boolean interrupt, boolean piko) {
		messageDelegate().setWorldEventResMessage(message, count, interrupt, piko);
	}

	/**
	 * 個体イベント発生メッセージ
	 * 
	 * @param message メッセージ
	 * @param count   メッセージ時間
	 */
	public void setBodyEventSendMessage(String message, int count) {
		messageDelegate().setBodyEventSendMessage(message, count);
	}

	/**
	 * 個体イベント応答メッセージ
	 * 
	 * @param message   メッセージ
	 * @param count     メッセージ時間
	 * @param interrupt 現在メッセージ中でも割り込むかどうか
	 * @param piko      ピコピコするかどうか
	 */
	public void setBodyEventResMessage(String message, int count, boolean interrupt, boolean piko) {
		messageDelegate().setBodyEventResMessage(message, count, interrupt, piko);
	}

	/**
	 * 非ゆっくり症＆口封じ用メッセージ
	 * 
	 * @param message メッセージ
	 * @param piko    ピコピコするかどうか
	 */
	public void setNYDMessage(String message, boolean piko) {
		messageDelegate().setNYDMessage(message, piko);
	}

	/**
	 * メッセージの実行部
	 * 
	 * @param message   メッセージ
	 * @param type      ウィンドウのタイプ
	 * @param count     メッセージ時間
	 * @param interrupt 現在メッセージ中でも割り込むかどうか
	 * @param piko      ピコピコするかどうか
	 * @param NYD       非ゆっくり症
	 */
	public void setMessage(String message, WindowType type, int count, boolean interrupt, boolean piko, boolean NYD) {
		messageDelegate().setMessage(message, type, count, interrupt, piko, NYD);
	}

	/**
	 * ねぎぃメッセージを出す
	 * 
	 * @param message メッセージ
	 * @param piko    ピコピコするかどうか
	 */
	public void setNegiMessage(String message, boolean piko) {
		messageDelegate().setNegiMessage(message, piko);
	}

	/**
	 * ねぎぃメッセージを出す
	 * 
	 * @param message メッセージ
	 * @param count   メッセージ時間
	 * @param piko    ピコピコするかどうか
	 */
	public void setNegiMessage(String message, int count, boolean piko) {
		messageDelegate().setNegiMessage(message, count, piko);
	}

	protected void addCrushedVomit(int x, int y, int z) {
		GameView.addCrushedVomit(x, y, z, this, getShitType());
	}

	/**
	 * 茎に触ったときの反応.
	 */
	public final void touchStalk() {
		stalkDelegate().touchStalk();
	}

	/**
	 * 未誕生フラグを設定する.
	 */
	@JsonIgnore
	public void setUnBirth(boolean flag) {
		stalkDelegate().setUnBirth(flag);
	}

	@JsonProperty("unBirth")
	public void setUnBirthForLoad(boolean flag) {
		stalkDelegate().setUnBirthForLoad(flag);
	}

	/**
	 * 未誕生フラグの基底状態を設定する.
	 *
	 * @param flag 未誕生かどうか
	 */
	public void setUnBirthState(boolean flag) {
		super.setUnBirth(flag);
		setEnableWall(!flag);
		setCanTalk(!flag);
		setFirstGround(true);
		if (flag) {
			setMessage(null);
			forceToSleep();
			setBirthAge(-1);
			setBirthEventBlockedTicks(0);
		} else {
			if (getBodyAgeState() == AgeState.BABY) {
				setAge(0);
				setBirthAge(getAge());
				setBirthEventBlockedTicks(300);
			}
			wakeup();
		}
	}

	/**
	 * ロード時向けに未誕生フラグの基底状態を設定する.
	 *
	 * @param flag 未誕生かどうか
	 */
	public void setUnBirthStateForLoad(boolean flag) {
		super.setUnBirth(flag);
		setEnableWall(!flag);
		setCanTalk(!flag);
		setFirstGround(true);
	}

	/**
	 * この個体のDNAを取得する.
	 * 
	 * @return この個体のDNA
	 */
	@Transient
	public Dna getDna() {
		Dna ret = new Dna(getType(), getAttitude(), getIntelligence(), false);
		ret.setFather(getUniqueID());
		return ret;
	}

	/**
	 * あまあましか受け付けないかどうかを返却する.
	 * 
	 * @return あまあましか受け付けないかどうか
	 */
	@Transient
	public final boolean isOnlyAmaama() {
		// 動けない
		if (getFootBakeLevel() == FootBake.CRITICAL && !canflyCheck())
			return false;
		boolean frag = false;
		// 肥えた舌状態の時のみ
		if (getTangType() == TangType.GOURMET && !isIdiot()) {
			// 知性によって変わる
			switch (getIntelligence()) {
				case WISE:
					if (isNoDamaged() && amaamaDiscipline >= 40)
						frag = true;
					else if (amaamaDiscipline == 100)
						frag = true;
					break;
				case AVERAGE:
					if (!isDamaged() && amaamaDiscipline >= 30)
						frag = true;
					else if (amaamaDiscipline >= 70)
						frag = true;
					break;
				case FOOL:
					if (!isDamagedHeavily() && amaamaDiscipline >= 20)
						frag = true;
					else if (amaamaDiscipline >= 50)
						frag = true;
					break;
			}
		}
		return frag;
	}

	/**
	 * スプライト画像のサイズ初期設定をする.
	 * 
	 * @param body
	 * @param braid
	 */
	public void setBoundary(Dimension4y[] body, Dimension4y[] braid) {
		spriteDelegate().setBoundary(body, braid);
	}

	/**
	 * スプライト画像サイズのアップデートをする.
	 */
	public final void updateSpriteSize() {
		spriteDelegate().updateSpriteSize();
	}

	public final void getBoundaryShape(Rectangle r) {
		spriteDelegate().getBoundaryShape(r);
	}

	public final void getExpandShape(Rectangle4y r) {
		spriteDelegate().getExpandShape(r);
	}

	/**
	 * 移動目標を取得する.
	 */
	public final Entity takeMoveTarget() {
		// 移動対象が床からなくなっていた場合は初期化
		Entity o = takeMappedObj(moveTargetId);
		if (o != null && o.getWhere() != Where.ON_FLOOR) {
			setMoveTargetId(-1);
			return null;
		}
		return o;
	}

	/**
	 * 強制的に表情を変更する.
	 */
	public final void setForceFace(int f) {
		// 非ゆっくり症個体、皮むき済み個体は顔変化なし
		if (isPealed() || isNYD())
			return;
		else if (isRaperExcitingFace(f)) {
			forceFace = ImageCode.EXCITING_raper.ordinal();
		} else
			forceFace = f;
	}

	/**
	 * れいぱーかつありすかつ興奮顔かどうかを返却する.
	 * 
	 * @param f ImageCodeのordinal
	 * @returns れいぱーかつありすかつ興奮顔かどうか
	 */
	protected boolean isRaperExcitingFace(int f) {
		return false;
	};

	/**
	 * 非ゆっくり症の表情を設定する.
	 * 
	 * @param f 表情の数字
	 */
	public final void setNYDForceFace(int f) {
		// 非ゆっくり症未発症個体、皮むき済み個体は顔変化なし
		if (isPealed() || isNotNYD())
			return;
		else
			setForceFace(f);
	}

	// お持ち帰り関連
	/**
	 * アイテムを持つ.
	 * 
	 * @param key アイテムのタイプ
	 * @param val 持つアイテムのオブジェクト
	 */
	public void setCarryItem(TakeoutItemType key, Entity val) {
		carryDelegate().setCarryItem(key, val);
	}

	/**
	 * 持っていたアイテムを落とす.
	 * 
	 * @param key アイテムのタイプ
	 * @return 持っていたアイテムのオブジェクト
	 */
	public Entity dropTakeoutItem(TakeoutItemType key) {
		return carryDelegate().dropTakeoutItem(key);
	}

	/**
	 * 全部落とす.
	 */
	public void dropAllTakeoutItem() {
		carryDelegate().dropAllTakeoutItem();
	}

	/**
	 * 影のロードを行う
	 * 
	 * @param loader
	 * @param io
	 * @throws IOException
	 */
	public static void loadShadowImages(ClassLoader loader, ImageObserver io) throws IOException {
		YukkuriSprite.loadShadowImages(loader, io);
	}

	/**
	 * 怒らせる.
	 */
	public final void setAngry() {
		stateDelegate().setAngry();
	}

	/**
	 * 汚れさせる.
	 */
	public final void makeDirty(boolean flag) {
		stateDelegate().makeDirty(flag);
	}

	/**
	 * 無反応で汚れを設定する.
	 * 
	 * @param b 汚れ
	 */
	public final void setDirtyFlag(boolean b) {
		makeDirty(b);
	}

	// ゆんやー関連
	/**
	 * ゆんやーする.
	 * 
	 * @param TF メッセージの有無
	 */
	public final void doYunnyaa(boolean TF) {
		// できない状態ならしない
		if (!canAction())
			return;
		if (TF)
			setMessage(GameMessages.getMessage(this, MessagePool.Action.Yunnyaa), 50, true, true);
		setYunnyaa(true);
		stay(40);
	}

	/**
	 * 命乞いをする.
	 */
	public void begForLife() {
		eventDelegate().begForLife();
	}

	/**
	 * 命乞いをする.
	 * 
	 * @param Ffrag 強制命乞いフラグ
	 */
	public void begForLife(boolean Ffrag) {
		eventDelegate().begForLife(Ffrag);
	}

	/**
	 * 先祖に加える
	 * 
	 * @param inAnc 加えたいゆっくりのUniqueID
	 */
	public final void addAncestorList(int inAnc) {
		getAncestorList().add(inAnc);
	}

	/**
	 * 先祖に加える
	 * 
	 * @param iAncList 先祖に加えたいリスト
	 */
	public final void addAncestorList(List<Integer> iAncList) {
		getAncestorList().addAll(iAncList);
	}

	// 飛行種かどうか
	// 種族としてのフラグを返すので現在飛べるかはcanflyCheckでチェック

	/**
	 * おさげを破壊する.
	 */
	public void takeBraid() {
		abuseDelegate().takeBraid();
	}

	/**
	 * 皮むきまたは皮修復（トグル）
	 */
	public void peal() {
		abuseDelegate().peal();
	}

	/**
	 * 茎を去勢する.
	 */
	public void invStalkCastration() {
		boolean stalkCastration = !isStalkCastration();
		castrateStalk(stalkCastration);
	}

	/**
	 * 胎生去勢をする.
	 */
	public final void invBodyCastration() {
		boolean bodyCastration = !isBodyCastration();
		castrateBody(bodyCastration);
	}

	/**
	 * あにゃる閉鎖を設定する.
	 * 
	 * @param flag あにゃる閉鎖かどうか
	 */
	public void setForceAnalClose(boolean flag) {
		closeAnal(flag);
	}

	/**
	 * 饅頭化または治す（トグル）
	 */
	public void pack() {
		abuseDelegate().pack();
	}

	/**
	 * 目破壊または修復（トグル）
	 */
	public void breakeyes() {
		abuseDelegate().breakeyes();
	}

	/**
	 * 口ふさぎまたは修復（トグル）
	 */
	public void ShutMouth() {
		abuseDelegate().ShutMouth();
	}

	/**
	 * むしる.
	 */
	public void pickHair() {
		abuseDelegate().pickHair();
	}

	/**
	 * 茎を引っこ抜く
	 * 
	 * @param s 茎のインスタンス
	 */
	public final void removeStalk(Stalk s) {
		stalkDelegate().removeStalk(s);
	}

	/**
	 * 茎をすべて掃除する.
	 * 右クリの「取る」でも茎は取り除かれてゆっくりのみ取れる。
	 * 茎を「取る」ことはできないが、実ゆは個別に「取る」で取ることができる。
	 */
	public void removeAllStalks() {
		stalkDelegate().removeAllStalks();
	}

	// 妊娠関連
	/**
	 * すっきりを行う
	 * 
	 * @param p すっきり相手
	 */
	public void doSukkiri(Yukkuri p) {
		sexualDelegate().doSukkiri(p);
	}

	/**
	 * 早いすっきり抑制を行う
	 */
	public void rapidExcitingDiscipline() {
		sexualDelegate().rapidExcitingDiscipline();
	}

	/**
	 * れいぽぅする
	 * 
	 * @param p れいぽぅ相手
	 */
	public void doRape(Yukkuri p) {
		sexualDelegate().doRape(p);
	}

	/**
	 * オナニーする.
	 */
	public void doOnanism() {
		sexualDelegate().doOnanism(null);
	}

	/**
	 * オナニー本体処理
	 * 
	 * @param p 相手（死体など
	 */
	public void doOnanism(Yukkuri p) {
		sexualDelegate().doOnanism(p);
	}

	/**
	 * 精子餡注入
	 * 
	 * @param dna DNA
	 */
	public void injectInto(Dna dna) {
		sexualDelegate().injectInto(dna);
	}

	/**
	 * 精子餡滴下
	 * 
	 * @param dna DNA
	 */
	public void dripSperm(Dna dna) {
		sexualDelegate().dripSperm(dna);
	}

	// 妊娠限界関連
	/**
	 * 強制的に発情させる.
	 */
	public void forceToExcite() {
		sexualDelegate().forceToExcite();
	}

	/**
	 * ぺにぺに切断のトグル
	 */
	public void cutPenipeni() {
		sexualDelegate().cutPenipeni();
	}

	/**
	 * れいぱー発情させる.
	 * 
	 * @param raper れいぱーかどうか
	 */
	public void forceToRaperExcite(boolean raper) {
		sexualDelegate().forceToRaperExcite(raper);
	}

	/**
	 * フェロモン状態をトグルする.
	 */
	public final void invPheromone() {
		setPheromone(!isPheromone());
	}

	/**
	 * すりすりする.
	 * 
	 * @param p すりすり相手
	 */
	public void doSurisuri(Yukkuri p) {
		otherRelationDelegate().doSurisuri(p);
	}

	/**
	 * ぺろぺろする.
	 * 
	 * @param p ぺろぺろ対象
	 */
	public void doPeropero(Yukkuri p) {
		otherRelationDelegate().doPeropero(p);
	}

	/**
	 * 母が子供の針をぐーりぐーりする
	 * 
	 * @param p 子供
	 */
	public void doGuriguri(Yukkuri p) {
		familyDelegate().doGuriguri(p);
	}

	/**
	 * ゆっくりの向きを制御する
	 * 
	 * @param b        相手
	 * @param alignDir 向き
	 */
	public final void constraintDirection(Yukkuri b, boolean alignDir) {
		if (alignDir) {
			// 自分と同じ方向を向かせる
			setDirection(b.getDirection());
		} else {
			// 向き合うように変更
			if (getX() < b.getX()) {
				setDirection(Direction.RIGHT);
				b.setDirection(Direction.LEFT);
			} else {
				setDirection(Direction.LEFT);
				b.setDirection(Direction.RIGHT);
			}
		}
	}

	/**
	 * 指定の座標まで動く
	 * 
	 * @param toX X座標
	 * @param toY Y座標
	 */
	public final void moveTo(int toX, int toY) {
		moveTo(toX, toY, 0);
	}

	/**
	 * 指定の座標まで動く.
	 * 
	 * @param toX X座標
	 * @param toY Y座標
	 * @param toZ Z座標
	 */
	public final void moveTo(int toX, int toY, int toZ) {
		BodyMovement.moveTo(this, toX, toY, toZ);
	}

	public final void setTargetMoveOffset(int ox, int oy) {
		moveDelegate().setTargetMoveOffset(ox, oy);
	}

	/**
	 * ごはんの方に動く
	 * 
	 * @param target ターゲットのメシ
	 * @param type   メシの種類
	 * @param toX    X座標
	 * @param toY    Y座標
	 */
	public final void moveToFood(Entity target, Food.FoodType type, int toX, int toY) {
		moveToFood(target, type, toX, toY, 0);
	}

	/**
	 * ごはんの方に動く
	 * 
	 * @param target ターゲットのメシ
	 * @param type   メシの種類
	 * @param toX    X座標
	 * @param toY    Y座標
	 * @param toZ    Z座標
	 */
	public final void moveToFood(Entity target, Food.FoodType type, int toX, int toY, int toZ) {
		clearActions();
		purposeOfMoving = PurposeOfMoving.TAKEOUT;
		setToFood(true);
		setMoveTargetId(target.objId);
		moveTo(toX, toY, toZ);
	}

	/**
	 * すっきりのために動く
	 * 
	 * @param target ターゲットのゆっくり
	 * @param toX    X座標
	 * @param toY    Y座標
	 */
	public final void moveToSukkiri(Entity target, int toX, int toY) {
		moveToSukkiri(target, toX, toY, 0);
	}

	/**
	 * すっきりのために動く
	 * 
	 * @param target ターゲットのゆっくり
	 * @param toX    X座標
	 * @param toY    Y座標
	 * @param toZ    Z座標
	 */
	public final void moveToSukkiri(Entity target, int toX, int toY, int toZ) {
		clearActions();
		setToSukkiri(true);
		setMoveTargetId(target.objId);
		moveTo(toX, toY, toZ);
	}

	/**
	 * トイレの方向に動く
	 * 
	 * @param target トイレ
	 * @param toX    X座標
	 * @param toY    Y座標
	 */
	public final void moveToToilet(Entity target, int toX, int toY) {
		moveToToilet(target, toX, toY, 0);
	}

	/**
	 * トイレの方向に動く
	 * 
	 * @param target トイレ
	 * @param toX    X座標
	 * @param toY    Y座標
	 * @param toZ    Z座標
	 */
	public final void moveToToilet(Entity target, int toX, int toY, int toZ) {
		clearActions();
		setToShit(true);
		setMoveTargetId(target.objId);
		moveTo(toX, toY, toZ);
	}

	/**
	 * ベッドの方に動く
	 * 
	 * @param target ベッド
	 * @param toX    X座標
	 * @param toY    Y座標
	 */
	public final void moveToBed(Entity target, int toX, int toY) {
		moveToBed(target, toX, toY, 0);
	}

	/**
	 * ベッドの方に動く
	 * 
	 * @param target ベッド
	 * @param toX    X座標
	 * @param toY    Y座標
	 * @param toZ    Z座標
	 */
	public final void moveToBed(Entity target, int toX, int toY, int toZ) {
		clearActions();
		setToBed(true);
		setMoveTargetId(target.objId);
		moveTo(toX, toY, toZ);
	}

	/**
	 * なんかの方向に動く
	 * 
	 * @param target ターゲットのなんか
	 * @param toX    X座標
	 * @param toY    Y座標
	 */
	public final void moveToBody(Entity target, int toX, int toY) {
		moveToBody(target, toX, toY, 0);
	}

	/**
	 * なんかの方向に動く
	 * 
	 * @param target ターゲットのなんか
	 * @param toX    X座標
	 * @param toY    Y座標
	 * @param toZ    Z座標
	 */
	public final void moveToBody(Entity target, int toX, int toY, int toZ) {
		BodyMovement.moveToBody(this, target, toX, toY, toZ);
	}

	/**
	 * イベント時の移動(通常種用)
	 * 
	 * @param e   イベント
	 * @param toX X座標
	 * @param toY Y座標
	 */
	public final void moveToEvent(EventPacket e, int toX, int toY) {
		moveToEvent(e, toX, toY, 0);
	}

	/**
	 * 同上(飛行種用)
	 * 
	 * @param e   イベント
	 * @param toX X座標
	 * @param toY Y座標
	 * @param toZ Z座標
	 */
	public final void moveToEvent(EventPacket e, int toX, int toY, int toZ) {
		if (isDead()) {
			return;
		}
		e.setToX(toX);
		e.setToY(toY);
		e.setToZ(toZ);
		destX = Math.max(0, Math.min(toX, Translate.getMapW()));
		destY = Math.max(0, Math.min(toY, Translate.getMapH()));
		destZ = Math.max(0, Math.min(toZ, Translate.getMapZ()));
	}

	/**
	 * 指定の方向を見る
	 * 
	 * @param toX X座標
	 * @param toY Y座標
	 */
	public final void lookTo(int toX, int toY) {
		if (isDead() || isSleeping()) {
			return;
		}
		if (toX > x) {
			setDirection(Direction.RIGHT);
		} else if (toX < x) {
			setDirection(Direction.LEFT);
		}
		stay();
	}

	/**
	 * メシを食う
	 * 
	 * @param amount メシの量
	 */
	public final void eatFood(int amount) {
		hungry += amount;
		plusShit(amount / 10);
		if (hungry < 0) {
			hungry = 0;
		}
		setAngry(false);
		setScare(false);
		setEating(true);
		stay();
	}

	/**
	 * 食べられる処理
	 * 
	 * @param amount 食われる量
	 */
	public void eatBody(int amount) {
		damageDelegate().eatBody(amount);
	}

	/**
	 * 他のゆっくりから食べられる
	 * 
	 * @param amount 食べられる量
	 * @param eater  食べてくるゆっくり
	 */
	public void eatBody(int amount, Yukkuri eater) {
		damageDelegate().eatBody(amount, eater);
	}

	/**
	 * ゆっくり以外から食べられる（現在はアリのみ）
	 * 
	 * @param amount 食われる量
	 * @param P      アリなら0
	 * @param AV     食べられた際に吐くかどうか
	 */
	public void beEaten(int amount, int P, boolean AV) {
		damageDelegate().beEaten(amount, P, AV);
	}

	/**
	 * 強制給餌
	 */
	public final void feed() {
		if (hungry <= getHungryLimit()) {
			setPikoMessage(GameMessages.getMessage(this, MessagePool.Action.Eating), true);
			setHappiness(Happiness.HAPPY);
			addLovePlayer(30);
		} else {
			setPikoMessage(GameMessages.getMessage(this, MessagePool.Action.Inflation), true);
			setHappiness(Happiness.VERY_SAD);
			addLovePlayer(-40);
		}
		eatFood(1500);
	}

	/**
	 * 打撃を受ける
	 * 
	 * @param amount ダメージ量
	 */
	public final void strike(int amount) {
		damageDelegate().strike(amount);
	}

	/**
	 * お仕置き
	 */
	public void strikeByPunish() {
		abuseDelegate().strikeByPunish();
	}

	/**
	 * ハンマー
	 */
	public void strikeByHammer() {
		abuseDelegate().strikeByHammer();
	}

	/**
	 * 押さえつけ
	 */
	public void strikeByPress() {
		abuseDelegate().strikeByPress();
	}

	/**
	 * パンチ
	 */
	public void strikeByPunch() {
		abuseDelegate().strikeByPunch();
	}

	/**
	 * ゆっくりから攻撃を受けた時の処理
	 * 
	 * @param enemy          攻撃してきたゆっくり
	 * @param e              イベント
	 * @param allowDamageCap 手加減ありの場合
	 */
	public void strikeByYukkuri(Yukkuri enemy, EventPacket event, boolean allowDamageCap) {
		damageDelegate().strikeByYukkuri(enemy, event, allowDamageCap);
	}

	/**
	 * 攻撃力の基準
	 * 
	 * @return 攻撃力
	 */
	@Transient
	public final int getStrength() {
		return getStrengthBase()[getBodyAgeState().ordinal()];
	}

	/**
	 * 何かで衝撃を加えられたとき
	 * 
	 * @param ap             基本ダメージ量
	 * @param weight         体重
	 * @param allowDamageCap 手加減あり
	 * @param vecX           X方向のベクトル
	 * @param vecY           Y方向のベクトル
	 */
	public void strikeByObject(int ap, int weight, boolean allowDamageCap, int vecX, int vecY) {
		damageDelegate().strikeByObject(ap, weight, allowDamageCap, vecX, vecY);
	}

	/**
	 * 体の爆発
	 */
	public void bodyBurst() {
		stateDelegate().bodyBurst();
	}

	/**
	 * 体の切断
	 */
	public void bodyCut() {
		stateDelegate().bodyCut();
	}

	/**
	 * 体のケガ
	 */
	public void bodyInjure() {
		stateDelegate().bodyInjure();
	}

	/**
	 * キックされた
	 */
	public final void kick() {
		// 土に埋まっていないなら吹っ飛ぶ
		if (getBurialState() == BurialState.NONE) {
			int blowLevel[] = { -4, -3, -2 };
			kick(0, blowLevel[getBodyAgeState().ordinal()] * 2, blowLevel[getBodyAgeState().ordinal()]);
		}
		strikeByPunish();
		begForLife();
	}

	/**
	 * お飾りが無いことを認識
	 */
	public void noticeNoOkazari() {
		adornmentDelegate().noticeNoOkazari();
	}

	/**
	 * お飾りを取られる
	 * 
	 * @param takenByPlayer プレイヤーに取られたかどうか
	 */
	public void takeOkazari(boolean takenByPlayer) {
		adornmentDelegate().takeOkazari(takenByPlayer);
	}

	/**
	 * お飾りを落とす(未使用)
	 */
	public void dropOkazari() {
		adornmentDelegate().dropOkazari();
	}

	/**
	 * お飾りをあげたときの反応を記述する.
	 * たりないゆは別なのでオーバーライドする.
	 * 
	 * @param type お飾りのタイプ
	 */
	public void giveOkazari(OkazariType type) {
		adornmentDelegate().giveOkazari(type);
	}

	/**
	 * おくるみをあげる
	 */
	public void givePants() {
		adornmentDelegate().givePants();
	}

	/**
	 * ジュース
	 */
	public void giveJuice() {
		stateDelegate().giveJuice();
	}

	/**
	 * ジュース注入
	 */
	public void injectJuice() {
		stateDelegate().injectJuice();
	}

	/**
	 * 復活
	 */
	public final void revival() {
		if (isDead()) {
			setDead(false);
			setCrushed(false);
			setSilent(false);
			giveJuice();
		}
		if (SimYukkuri.UNYO) {
			unyoOffsetH = 0;
			unyoOffsetW = 0;
		}
	}

	/**
	 * あにゃる閉鎖のトグル
	 */
	public final void invAnalClose() {
		closeAnal(!isAnalClose());
	}

	/**
	 * あにゃる閉鎖する.
	 */
	public final void closeAnal(boolean flag) {
		abuseDelegate().closeAnal(flag);
	}

	/**
	 * あにゃる閉鎖を設定する.
	 * 
	 * @param flag あにゃる閉鎖するか否か
	 */
	public final void setAnalClose(boolean flag) {
		this.analClose = flag;
	}

	/**
	 * Shiftキー押下での動作.
	 * 
	 * @return 茎去勢有無
	 */
	public final boolean getStalkCastration() {
		return isStalkCastration();
	}

	/**
	 * Shiftキー押下での動作.
	 * 
	 * @return 胎生去勢有無
	 */
	public final boolean getBodyCastration() {
		return isBodyCastration();
	}

	/**
	 * 茎去勢を設定する.
	 */
	public void castrateStalk(boolean flag) {
		abuseDelegate().castrateStalk(flag);
	}

	/**
	 * 胎生去勢を設定する.
	 */
	public void castrateBody(boolean flag) {
		abuseDelegate().castrateBody(flag);
	}

	/**
	 * 火をつける
	 */
	public void giveFire() {
		abuseDelegate().giveFire();
	}

	/**
	 * 針を刺す（トグル
	 */
	public final void invNeedle() {
		setNeedle(!isNeedled());
	}

	/**
	 * 針に刺さっているかどうかを取得（Shiftキーでの針に対応）.
	 * 
	 * @return 針に刺さっているかどうか
	 */
	public final boolean getNeedle() {
		return isNeedled();
	}

	/**
	 * 針刺しを設定する.
	 * 
	 * @param needleOn 針刺し
	 */
	public void setNeedle(boolean needleOn) {
		abuseDelegate().setNeedle(needleOn);
	}

	/**
	 * ゆ虐神拳を進捗させる（お前はもう死んでいる→ゆべら！）
	 */
	public void plusGodHand() {
		if (getAbFlagGodHand()[0]) {
			if (getBurstState() != Burst.NEAR) {
				// 爆発直前まで膨らませる
				setGodHandHoldCount(getGodHandHoldCount() + 1);
			}
		}
		if (getAbFlagGodHand()[1]) {
			// 伸ばす
			if (getGodHandStretchCount() < Const.EXT_FORCE_PULL_LIMIT[getBodyAgeState().ordinal()]) {
				setGodHandStretchCount(getGodHandStretchCount() + 1);
			}
			lockSetZ(getGodHandStretchCount());
		} else if (getAbFlagGodHand()[2]) {
			// 縮める
			if (Const.EXT_FORCE_PUSH_LIMIT[getBodyAgeState().ordinal()] < getGodHandCompressCount()) {
				setGodHandCompressCount(getGodHandCompressCount() - 1);
			}
			lockSetZ(getGodHandCompressCount());
		}
	}

	/**
	 * 水をかける
	 */
	public void giveWater() {
		stateDelegate().giveWater();
	}

	/**
	 * 水の中にいる
	 * 
	 * @param depth 深さ
	 */
	public void inWater(Pool.DEPTH depth) {
		stateDelegate().inWater(depth);
	}

	/**
	 * 土に埋める
	 */
	public void baryInUnderGround() {
		stateDelegate().baryInUnderGround();
	}

	/**
	 * 環境によるパニック状態の設定
	 * 
	 * @param flag  すでにパニック状態か
	 * @param pType パニックのタイプ
	 */
	public void setPanic(boolean flag, PanicType pType) {
		stateDelegate().setPanic(flag, pType);
	}

	/**
	 * 声掛け
	 * 
	 * @param type 声掛けタイプ（0:ゆっくりしていってね 1:ゆっくりしないで死んでね 2:もるんもるんしてね）
	 */
	public void voiceReaction(int type) {
		emotionDelegate().voiceReaction(type);
	}

	/**
	 * 持つ
	 */
	public void Hold() {
		abuseDelegate().Hold();
	}

	/**
	 * 押さえつけ
	 * 
	 * @param force 強制かどうか
	 */
	public void lockSetZ(int force) {
		abuseDelegate().lockSetZ(force);
	}

	/**
	 * 押さえつけを放す
	 */
	public void releaseLockNobinobi() {
		if (externalPressure == 0)
			return;
		if (externalPressure < 0) {
			externalPressure = 0;
		} else if (externalPressure * 2 / 3 < getSize()) {
			strike(externalPressure * 100 * 24 / getSize());
			externalPressure = 0;
		} else {
			strikeByHammer();
			makeDirty(true);
			externalPressure = 0;
		}
		return;
	}

	/**
	 * 汚れ、濡れを解除する.
	 */
	public void setCleaning() {
		makeDirty(false);
		setWet(false);
		wetPeriod = 0;
	}

	/**
	 * 逃げる
	 * 
	 * @param fromX X座標
	 * @param fromY Y座標
	 */
	public void runAway(int fromX, int fromY) {
		BodyMovement.runAway(this, fromX, fromY);
	}
	// ------------------------------------------

	// --------------------------------------------------
	@Override
	public void remove() {
		synchronized (SimYukkuri.lock) {
			setRemoved(true);
			int[] is = { -1, -1 };
			setParents(is);
			Yukkuri pa = BodyRelations.getPartnerBody(this);
			if (pa != null)
				pa.setPartner(-1);
			setPartner(-1);
			removeAllStalks();
			setStalks(null);
			if (GameWorld.get().getCurrentMap().getBody().containsKey(this.getUniqueID())) {
				GameWorld.get().getCurrentMap().getBody().remove(this.getUniqueID());
			}
			getChildrenList().clear();
			getElderSisterList().clear();
			getSisterList().clear();
			List<Yukkuri> bodies = new LinkedList<Yukkuri>(GameWorld.get().getCurrentMap().getBody().values());
			for (Yukkuri b : bodies) {
				if (b.getChildrenList() != null) {
					ListUtil.removeContent(b.getChildrenList(), getUniqueID());
				}
				if (b.getElderSisterList() != null) {
					ListUtil.removeContent(b.getElderSisterList(), getUniqueID());
				}
				if (b.getSisterList() != null) {
					ListUtil.removeContent(b.getSisterList(), getUniqueID());
				}
			}
			getAttach().clear();
			setOkazari(null);
			getBabyTypes().clear();
			getStalkBabyTypes().clear();
			getAncestorList().clear();
			setParentLinkId(-1);
			setMoveTargetId(-1);
			getEventList().clear();
			setCurrentEvent(null);
			getFavoriteItems().clear();
			getCarryItems().clear();
		}
	}

	/**
	 * 親子関係をなくす
	 */
	public void clearRelation() {
		familyDelegate().clearRelation();
	}

	/**
	 * 妊娠期間を早める
	 */
	public void rapidPregnantPeriod() {
		if (hasBabyOrStalk()) {
			pregnancyPeriodBoost += TICK;
		}
	}

	/**
	 * うんうんを素早く貯めさせる
	 */
	public void rapidShit() {
		excretionBoost += TICK * 5;
	}

	/**
	 * （死んだときとかに）茎とゆっくりのバインドを解く.
	 * 茎をゲームから取り除くわけではなく、何らかの形で残したい場合に使用する.
	 * 茎を完全に取り除きたい場合はremoveAllStalks()を使用する.
	 */
	public void disPlantStalks() {
		stalkDelegate().disPlantStalks();
	}

	/**
	 * 胴体のベースグラフィックを返す
	 * まりケツは特殊なためまりさ(とそれを継承している、つむりまりさ＆れいむまりさ)は各クラスでオーバーライドしているため要確認
	 * れいむ(とそれを継承している、わさ＆まりされいむ)もゆんやぁぁが特殊なため、同様
	 * 
	 * @param layer レイヤ
	 * @return index
	 */
	public int getBodyBaseImage(BodyLayer layer) {
		return spriteDelegate().getBodyBaseImage(layer);
	}

	/**
	 * 切断等の通常ではないボディイメージ
	 * 
	 * @param layer レイヤ
	 * @return index
	 */
	public int getAbnormalBodyImage(BodyLayer layer) {
		return spriteDelegate().getAbnormalBodyImage(layer);
	}

	/**
	 * おかざりグラフィックを返す。
	 * 
	 * @param layer レイヤ
	 * @param type  0だと前方、1だと後方の分を返す
	 * @return index
	 */
	public int getOlazariImage(BodyLayer layer, int type) {
		return spriteDelegate().getOlazariImage(layer, type);
	}

	/**
	 * 汚れなどの体表エフェクトグラフィックを返す
	 * 
	 * @param layer レイヤ
	 * @return index
	 */
	public int getEffectImage(BodyLayer layer) {
		return spriteDelegate().getEffectImage(layer);
	}

	/**
	 * 顔グラフィックを返す
	 * 
	 * @param layer レイヤ
	 * @return index
	 */
	public int getFaceImage(BodyLayer layer) {
		return spriteDelegate().getFaceImage(layer);
	}

	/**
	 * 描画補助から Alice 判定を参照するための bridge.
	 *
	 * @return Alice れいぱーかどうか
	 */
	@JsonIgnore
	public final boolean isAliceRaperForRender() {
		return spriteDelegate().isAliceRaperForRender();
	}

	/**
	 * ありすかつれいぱーかどうかを返却する.
	 * 
	 * @return ありすかつれいぱーかどうか
	 */
	@Transient
	protected boolean isAliceRaper() {
		return false;
	};

	/**
	 * まばたき画像が用意されているかチェック、Trueで対応している
	 * 
	 * @return まばたき画像が用意されているか
	 */
	private boolean mabatakiNormalImageCheck() {
		// normal2.png normal3.png
		// ハイブリット以外
		// 画像を用意したゆっくりのみ対応、追加する時はModLoaderの条件も変更する
		// int i = getType();
		// if(i == 0 || i == 1 || i == 2 || i == 4 || i == 10001 || i == 1009 || i ==
		// 1001 ||
		// i == 1006 || i == 3002 || i == 1010 || i == 1000 || i == 2001 || i == 1003 ||
		// i == 1002 || i == 2007 || i == 2000 || i == 1005 || i == 1011 || i == 3000 ||
		// i == 1008 || i == 3 || i == 1002 || i == 5 || i == 1004 || i == 10000 ||
		// i == 2006 || i == 2003 || i == 3001 || i == 1007 || i == 2005 ){return true;}
		// return false;
		// ハイブリッドのみエラーが出るので除外
		if (getType() == YukkuriType.HYBRIDYUKKURI) {
			return false;
		}
		return true; // 画像をすべて用意したのでtrueで確定
	}

	/**
	 * おさげ、羽、尻尾のグラフィックを返す。
	 * 
	 * @param layer レイヤ
	 * @param type  0だと手前側の分、1だと奥側の分が返される
	 * @return index
	 */
	public int getBraidImage(BodyLayer layer, int type) {
		return spriteDelegate().getBraidImage(layer, type);
	}

	@Override
	public void grab() {
		stalkDelegate().grab();
	}

	/**
	 * 茎からこの個体を切り離す.
	 * <p>
	 * 子側の bindStalk だけでなく、茎側の bindBabies からも自分自身を消す。
	 * これをしないと Stalk.upDate() が次tickで再結合してしまう。
	 * </p>
	 */
	public final void detachFromStalk() {
		stalkDelegate().detachFromStalk();
	}

	/**
	 * Tick処理本体
	 */
	@Override
	public Event clockTick() {
		if (GameEnvironment.getOperationTime() % 100 == 0) {
			checkRemovedFamilyList();
		}
		// if removed, remove body
		if (isRemoved()) {
			removeAllStalks();
			remove();
			return Event.REMOVED;
		}

		int i = 0;
		Attachment at;
		while (i < getAttach().size()) {
			at = getAttach().get(i);
			if (at == null || at.isRemoved()) {
				continue;
			}
			if (at.clockTick() == Event.REMOVED) {
				getAttach().remove(i);
			} else {
				i++;
			}
		}
		// if partner and parents are removed, clean relationship.
		clearRelation();

		// 死亡処理 if dead, do nothing.
		if (isDead()) {
			dropAllTakeoutItem();
			clearActions();
			moveBody(true); // for falling the body
			if (isUnBirth()) {
				setMessageTicks(0);
				setMessageBuffer(null);
			}
			checkMessage();
			if (SimYukkuri.UNYO) {
				resetUnyo();
			}
			setSilent(true);
			setDeadPeriod(getDeadPeriod() + 1);
			// 死後3日
			if (getRottingTimeBase() < getDeadPeriod()) {
				if (!isCrushed()) {
					// 初回は潰れる
					setCrushed(true);
					setDeadPeriod(0);
				} else {
					// うんうんと吐餡に変わって消える
					GameView.addCrushedVomit(x, y, z, this, getShitType());
					GameView.addCrushedShit(x, y, z, this, getShitType());
					remove();
					disPlantStalks();
					return Event.REMOVED;
				}
			}
			return Event.DEAD;
		}

		// 爆発処理
		if (isBurst()) {
			toDead();
			moveBody(true); // for falling the body
			checkMessage();
			if (isDead()) {
				bodyBurst();
				return Event.DEAD;
			}
		}

		Event retval = Event.DONOTHING;
		boolean stopAgeSteamAmple = false;
		if (getAttachmentSize(StopAmpoule.class) != 0) {
			stopAgeSteamAmple = true;
		}

		boolean accelAgeSteamAmple = false;
		if (getAttachmentSize(AccelAmpoule.class) != 0) {
			accelAgeSteamAmple = true;
		}

		if (GameEnvironment.getInterval() == 0) {
			if (GameEnvironment.isAgeBoostSteam() && getBodyAgeState() != AgeState.ADULT)
				addAge(10000);
			if (GameEnvironment.isAgeStopSteam() && !accelAgeSteamAmple)
				addAge(-256);
		}

		if (getBurialState() == BurialState.NONE || getBurialState() == BurialState.HALF) {
			if (GameEnvironment.isRapidPregnantSteam())
				rapidPregnantPeriod();
		}

		// check age
		// ageが変化しないと状態が変化しないロジックになっているのでそっとしておく
		setAge(getAge() + TICK);
		if (birthEventBlockedTicks > 0) {
			birthEventBlockedTicks--;
		}

		if (getAge() > getLifeLimitBase()) {
			toDead();
			moveBody(true); // for falling the body
			checkMessage();
			if (isDead()) {
				return Event.DEAD;
			}
		}

		// 年齢チェック
		AgeState curAge = getBodyAgeState();
		FootBake foot = getFootBakeLevel();
		if (curAge.ordinal() < getBodyAgeState().ordinal()) {
			// 状態変更有かつ成長抑制されている場合は強制的に元に戻す。成長促進アンプルが刺さっていたら成長する
			if (((GameEnvironment.isAgeStopSteam()) || (stopAgeSteamAmple)) && !accelAgeSteamAmple) {
				setAgeState(curAge);
				setAge(getAge() + TICK);
			} else {
				// 加齢
				initAmount(getBodyAgeState());
				resetAttachmentBoundary();
				// DamageLimitを流用してるパラメータは状態を維持するためここで再計算
				switch (foot) {
					case MIDIUM:
						footBakePeriod = (getDamageLimitBase()[getBodyAgeState().ordinal()] >> 1) + 1;
						break;
					case CRITICAL:
						footBakePeriod = getDamageLimitBase()[getBodyAgeState().ordinal()] + 1;
						break;
					default:
						break;
				}
			}
		}
		// ゆ虐神拳カウント
		plusGodHand();

		boolean dontMove = false;
		if (getCoreAnkoState() == CoreAnkoState.NonYukkuriDisease ||
				isOnNonMovingConveyor() || isSurisuriFromPlayer() || isPealed() || isPacked()) {
			dontMove = true;
		}

		// 無限もるんもるん
		if (GameEnvironment.isEndlessFurifuriSteam()) {
			clearActions();
			checkMessage();
			if (canFurifuri()) {
				setMessage(GameMessages.getMessage(this, MessagePool.Action.FuriFuri), 30);
				setFurifuri(true);
			} else if (isNotNYD()) {
				setMessage(GameMessages.getMessage(this, MessagePool.Action.CantMove), 30);
				setHappiness(Happiness.VERY_SAD);
			} else {
				setNYDMessage(GameMessages.getMessage(this, MessagePool.Action.NonYukkuriDisease), false);
			}
			shit = 0;
			hungry = getHungryLimit();
			if (damage > getDamageLimitBase()[getBodyAgeState().ordinal()] * 80 / 100) {
				damage = getDamageLimitBase()[getBodyAgeState().ordinal()] * 80 / 100;
			}
			stay();
			checkDamage();
			checkAnts();
			checkUnyo();
			checkStress();
			checkSick();
			checkCantDie();
			moveBody(true);
			return Event.DONOTHING;
		}
		// check status
		checkHungry();
		checkDamage();
		checkAnts();
		checkUnyo();
		checkStress();
		checkSick();
		checkCantDie();
		// check relax and excitement
		checkEmotion();
		// check discipline level
		checkDiscipline();
		// check wet
		checkWet();
		// 8秒に一回顔を
		if (GameRandom.nextInt(80) == 0) {
			setForceFace(-1);
			checkAttitude();
		}
		// 妊娠状況チェック
		boolean oldHasBaby = hasBabyOrStalk();
		if (checkChildbirth()) {
			dontMove = true;
		}
		if ((oldHasBaby && !hasBabyOrStalk()) || (!hasBabyOrStalk() && isBirth())) {
			if (getStalks() != null && getStalks().size() <= 0 && isHasStalk()) {
				setHasStalk(false);
			}
			// 出産に失敗するとfalseになるのでリセット
			if (getBabyTypes().size() != 0) {
				setHasBaby(true);
			}
			if (getStalks() != null && getStalks().size() != 0) {
				setHasStalk(true);
			}
			return Event.BIRTHBABY;
		}
		// 出産に失敗するとfalseになるのでリセット
		if (getBabyTypes().size() != 0) {
			setHasBaby(true);
		}
		if (getStalks() != null && getStalks().size() != 0) {
			setHasStalk(true);
		}

		// パニック時はただ走る
		if (getPanicType() != null && !isNeedled() && isNotNYD()) {
			retval = checkFear();
			if (isLockmove())
				dontMove = true;
			if (isMelt())
				dontMove = true;
			if (getCriticalDamege() != null)
				dontMove = true;
			if (getFootBakeLevel() == FootBake.CRITICAL && !canflyCheck())
				dontMove = true;
			if (isNeedled())
				dontMove = true;
			if (isUnBirth())
				dontMove = true;
			setHappiness(Happiness.VERY_SAD);
			moveBody(dontMove);
			return retval;
		}

		noticeNoOkazari();

		// check can move or not
		if (getCriticalDamegeType() == CriticalDamegeType.CUT ||
				(getFootBakeLevel() == FootBake.CRITICAL && !canflyCheck()) ||
				isNeedled() || getBurialState() != BurialState.NONE || isUnBirth()) {
			dontMove = true;
		}

		// check events
		// check shit
		int oldShit = shit;
		// stop moving
		if (checkShit())
			dontMove = true;
		// うんうん処理
		if (!isShitting() && oldShit != 0 && shit == 0) {
			if (!isHasPants()) {
				if (!isAnalClose() && !(isFixBack() && isNeedled())) {
					// 寝ているか粘着床についているか針が刺さっていたら体勢をかえられずに漏らす
					if ((isLockmove() && isFixBack()) || isSleeping() || isNeedled() ||
							getBurialState() != BurialState.NONE) {
						retval = Event.DOCRUSHEDSHIT;
					} else {
						if (isNotNYD()) {
							retval = Event.DOSHIT;
						} else {
							// 非ゆっくり症
							retval = Event.DOCRUSHEDSHIT;
						}
					}
					// 300%を肥えてたらうんうん量を増やす
					if (300 < 100 * oldShit / getShitLimit()) {
						rapidShit();
					}
				}
			}
			// あんよが傷ついていた場合、一定確率であんよが爆ぜる
			if (getCriticalDamegeType() == CriticalDamegeType.INJURED && getBreakBodyByShitProb() != 0
					&& GameRandom.nextInt(getBreakBodyByShitProb()) == 0) {
				bodyCut();
			}
		}

		// 以下3項目のまとめ
		if (checkSleep() || isLockmove() || isMelt() || isFurifuri() || isEating() || isUnBirth()) {
			dontMove = true;
		}

		if (isStaying()) {
			setStayTicks(getStayTicks() + TICK);
			if (getStayTicks() > stayTime) {
				setStayTicks(0);
				setStaying(false);
				setPurupuru(false);
			} else {
				dontMove = true;
				if (isPurupuru()) {
					doPurupuru();
				}
			}
		}

		// イベントに反応できる状態かチェック
		BodyEventState.processPendingEvents(this);

		// move to destination
		// if there is no destination, walking randomly.
		if (getCoreAnkoState() == CoreAnkoState.NonYukkuriDiseaseNear) {
			// 非ゆっくり症初期の場合はあまり動かない
			if (GameRandom.nextInt(5) == 0) {
				moveBody(true);
			} else {
				moveBody(dontMove);
			}
		} else {
			moveBody(dontMove);
		}

		checkMessage();

		// イベントで処理が設定された場合に実行する
		retval = BodyEventState.resolveEventResultAction(this, retval);
		calcPos();
		moveDelegate().calcMoveTarget();
		return retval;
	}

	/**
	 * moveTargetIdが範囲外のとき、範囲内に収める.
	 */
	public void calcMoveTarget() {
		moveDelegate().calcMoveTarget();
	}

	/**
	 * Removeされたゆっくりが姉妹リスト、子リストにいたら削除する
	 */
	private void checkRemovedFamilyList() {
		familyDelegate().checkRemovedFamilyList();
	}

	/**
	 * ユニークIDのlistからゆっくりのインスタンスの配列を返却する.
	 * 
	 * @param list ユニークIDのlist
	 * @return ゆっくりの配列
	 */
	public Yukkuri[] getArrayOfBody(List<Integer> list) {
		List<Yukkuri> bodies = new LinkedList<Yukkuri>();
		for (int i : list) {
			bodies.add(BodyRelations.getBody(i));
		}
		return bodies.toArray(new Yukkuri[0]);
	}

	@Override
	public GetMenuTarget hasGetPopup() {
		return GetMenuTarget.BODY;
	}

	@Override
	public UseMenuTarget hasUsePopup() {
		return UseMenuTarget.BODY;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param initX        初期X座標
	 * @param initY        初期Y座標
	 * @param initZ        初期Z座標
	 * @param initAgeState 初期時代
	 * @param mama         母
	 * @param papa         父
	 */
	public Yukkuri(int initX, int initY, int initZ, AgeState initAgeState, Yukkuri mama, Yukkuri papa) {
		objType = Type.YUKKURI;
		objId = Numbering.INSTANCE.numberingObjId();
		x = initX;
		y = initY;
		z = initZ;
		if (z == 0) {
			setFirstGround(false);
		} else {
			setFirstGround(true);
		}
		getParents()[Parent.PAPA.ordinal()] = papa == null ? -1 : papa.getUniqueID();
		getParents()[Parent.MAMA.ordinal()] = mama == null ? -1 : mama.getUniqueID();
		setRemoved(false);
		if (GameRandom.nextBoolean()) {
			setAttitude((papa != null ? papa.getAttitude() : null));
		} else {
			setAttitude((mama != null ? mama.getAttitude() : null));
		}
		if (getAttitude() == null) {
			setAttitude(getRandomAttitude());
		}
		switch (GameRandom.nextInt(6)) {
			case 0:
			case 1:
				setIntelligence(Intelligence.FOOL);
				break;
			case 5:
				setIntelligence(Intelligence.WISE);
				break;
			default:
				setIntelligence(Intelligence.AVERAGE);
				break;
		}

		if (papa != null && mama != null) {
			if (papa.getIntelligence() == Intelligence.FOOL && mama.getIntelligence() == Intelligence.FOOL
					&& GameRandom.nextBoolean()) {
				setIntelligence(Intelligence.FOOL);
			} else if (papa.getIntelligence() == Intelligence.WISE && mama.getIntelligence() == Intelligence.WISE
					&& GameRandom.nextInt(5) <= 1) {
				setIntelligence(Intelligence.FOOL);
			}
		}

		if (papa != null && papa.getFavoriteItem(FavItemType.BED) != null) {
			setFavoriteItem(FavItemType.BED, papa.getFavoriteItem(FavItemType.BED));
		} else if (mama != null && mama.getFavoriteItem(FavItemType.BED) != null) {
			setFavoriteItem(FavItemType.BED, mama.getFavoriteItem(FavItemType.BED));
		}

		setOkazari(new Okazari(this, OkazariType.DEFAULT));

		IniFileUtil.readIniFile(this, false); // iniファイル読み込み
		tuneParameters(); // Update individual parameters.

		// 年齢補正
		switch (initAgeState) {
			case BABY:
				setAge(0);
				break;
			case CHILD:
				setAge(getBabyLimitBase());
				break;
			case ADULT:
			default:
				setAge(getChildLimitBase());
				break;
		}
		setAge(getAge() + GameRandom.nextInt(100));
		getBodyAgeState();
		getMindAgeState();
		initAmount(initAgeState);
		wakeUpTime = getAge();
		shit = GameRandom.nextInt(getShitLimitBase()[getBodyAgeState().ordinal()] / 2);
		if (getBodyAgeState() == AgeState.BABY) {
			if (mama != null) {
				if (mama.isDamaged()) {
					damage = GameRandom.nextInt(mama.damage) * getDamageLimitBase()[Const.BABY_INDEX]
							/ mama.getDamageLimit();
					getBodyAgeState();
					getDamageState();
				}
				if (mama.isSick()) {
					addSickPeriod(100);
				}
				if (mama.isDead()) {
					damage += getDamageLimitBase()[Const.BABY_INDEX] / 4 * 3
							+ GameRandom.nextInt(getDamageLimitBase()[Const.BABY_INDEX]);
				}
				setBirthMessageForced(true);
			}
			setBirthAge(getAge());
		}
		dirX = randomDirection(dirX);
		dirY = randomDirection(dirY);
		setMessageTextSize(12);
		setUniqueID(Numbering.INSTANCE.numberingYukkuriID());
		// 生い立ちの設定
		BodyRank bodyRank = BodyRank.KAIYU;
		PublicRank publicRank = PublicRank.NONE;
		if (mama != null) {
			bodyRank = mama.getBodyRank();
			// 階級の設定
			PublicRank motherPublicRank = mama.getPublicRank();
			// 母親のランクに応じて変更
			switch (motherPublicRank) {
				case NONE:
					publicRank = PublicRank.NONE;
					break;
				case UnunSlave:// うんうん奴隷
					publicRank = PublicRank.UnunSlave;
					break;
				default:
					break;
			}
		} else if (GameWorld.get() != null) {
			if (GameWorld.get().getCurrentMap().getMapIndex() == 5
					|| GameWorld.get().getCurrentMap().getMapIndex() == 6)
				bodyRank = BodyRank.YASEIYU;
		}
		// 生い立ちを設定
		setBodyRank(bodyRank);
		setPublicRank(publicRank);

		// 先祖の情報を引き継ぐ
		if (mama != null) {
			List<Integer> ancestorList = mama.getAncestorList();
			YukkuriType ancestorType = mama.getType();
			addAncestorList(ancestorList);
			addAncestorList(ancestorType.getTypeID());
		}
		if (papa != null) {
			List<Integer> ancestorList = papa.getAncestorList();
			YukkuriType ancestorType = papa.getType();
			addAncestorList(ancestorList);
			addAncestorList(ancestorType.getTypeID());
		}

		hungry = getHungryLimitBase()[getBodyAgeState().ordinal()] + (100 * getBodyAgeState().ordinal());

	}

	public Yukkuri() {
		objType = Type.YUKKURI;
		if (z == 0) {
			setFirstGround(false);
		} else {
			setFirstGround(true);
		}
		setRemoved(false);
		if (getAttitude() == null) {
			setAttitude(getRandomAttitude());
		}
		switch (GameRandom.nextInt(6)) {
			case 0:
			case 1:
				setIntelligence(Intelligence.FOOL);
				break;
			case 5:
				setIntelligence(Intelligence.WISE);
				break;
			default:
				setIntelligence(Intelligence.AVERAGE);
				break;
		}

		setOkazari(new Okazari(this, OkazariType.DEFAULT));

		IniFileUtil.readIniFile(this, false); // iniファイル読み込み

		setAge(getAge() + GameRandom.nextInt(100));
		getBodyAgeState();
		getMindAgeState();
		wakeUpTime = getAge();
		shit = GameRandom.nextInt(getShitLimitBase()[getBodyAgeState().ordinal()] / 2);
		dirX = randomDirection(dirX);
		dirY = randomDirection(dirY);
		setMessageTextSize(12);
		setUniqueID(Numbering.INSTANCE.numberingYukkuriID());
		// 生い立ちの設定
		BodyRank bodyRank = BodyRank.KAIYU;
		PublicRank publicRank = PublicRank.NONE;
		// 生い立ちを設定
		setBodyRank(bodyRank);
		setPublicRank(publicRank);

		hungry = getHungryLimitBase()[getBodyAgeState().ordinal()] + (100 * getBodyAgeState().ordinal());
	}

	@Transient
	private Attitude getRandomAttitude() {
		switch (GameRandom.nextInt(9)) {
			case 0:
				return Attitude.VERY_NICE;
			case 1:
			case 2:
				return Attitude.NICE;
			case 3:
			case 4:
				return Attitude.SHITHEAD;
			case 5:
				return Attitude.SUPER_SHITHEAD;
			default:
				return Attitude.AVERAGE;
		}
	}

	/**
	 * 待つ
	 * 
	 * @param waitTime 待ち時間
	 * @return 待ち時間が過ぎたらtrue
	 */
	public final boolean checkWait(int waitTime) {
		long nowTimeMillis = System.currentTimeMillis();
		long lastActionTimeMillis = getLastActionTime();
		int speed = 100; // default NORMAL
		String osName = System.getProperty("os.name", "").toLowerCase();
		boolean hasDisplay = System.getenv("DISPLAY") != null;
		boolean isWindows = osName.contains("windows");
		if (hasDisplay || isWindows) {
			try {
				speed = MyPane.getGameSpeed()[MainCommandUI.getSelectedGameSpeed()];
			} catch (Throwable ignore) {
				speed = 100;
			}
		}
		if (nowTimeMillis - lastActionTimeMillis < waitTime * speed / 100) {
			return false;
		}
		// setLastActionTime(nowTimeMillis);
		return true;
	}

	/**
	 * ゆかびのトグル.
	 * かびていれば治療し、そうでなければかびさせる.
	 */
	public void moldToggle() {
		if (sickPeriod > 0) {
			sickPeriod = 0;
		} else {
			setMessage(GameMessages.getMessage(this, MessagePool.Action.Moldy));
			forceSetSick();
		}
	}

	/**
	 * れいぱーのトグル.
	 * れいぱーであれば治療し、そうでなければれいぱー覚醒させる.
	 */
	public void raperToggle() {
		setRaper(!isRaper());
	}

	/** おかざりがなくなっていることに気がついているか */
	protected boolean noticeNoOkazari = false;

	// ===== Step 7-1: LivingEntity から移動した種族固有フィールド =====

	/** 種族としてお下げ、羽、尻尾を持つか */
	protected boolean braidType = true;
	/** あにゃるふさぎ有無 */
	protected boolean analClose = false;

	/** あにゃるふさぎ有無 を取得する. @return あにゃるふさぎ有無 */
	public boolean isAnalClose() {
		return analClose;
	}

	/** 胎生去勢有無 */
	protected boolean bodyCastration = false;
	/** 茎去勢有無 */
	protected boolean stalkCastration = false;
	/** 希少種か */
	protected boolean rareType = false;
	/** 捕食種タイプ */
	protected PredatorType predatorType = null;
	/** 苦いえさが好きか */
	protected boolean likeBitterFood = false;
	/** 辛いえさが好きか */
	protected boolean likeHotFood = false;

	public boolean isNoticeNoOkazari() {
		return noticeNoOkazari;
	}

	public void setNoticeNoOkazari(boolean noticeNoOkazari) {
		this.noticeNoOkazari = noticeNoOkazari;
	}

	// ===== Step 7-1: 種族固有フィールドの getter/setter =====

	/** 種族としてお下げ、羽、尻尾を持つか を取得する. @return 種族としてお下げ、羽、尻尾を持つか */
	public boolean isBraidType() {
		return braidType;
	}

	/** 種族としてお下げ、羽、尻尾を持つか を設定する. @param braidType 種族としてお下げ、羽、尻尾を持つか */
	public void setBraidType(boolean braidType) {
		this.braidType = braidType;
	}

	/** 胎生去勢有無 を取得する. @return 胎生去勢有無 */
	public boolean isBodyCastration() {
		return bodyCastration;
	}

	/** 胎生去勢有無 を設定する. @param bodyCastration 胎生去勢有無 */
	public void setBodyCastration(boolean bodyCastration) {
		this.bodyCastration = bodyCastration;
	}

	/** 茎去勢有無 を取得する. @return 茎去勢有無 */
	public boolean isStalkCastration() {
		return stalkCastration;
	}

	/** 茎去勢有無 を設定する. @param stalkCastration 茎去勢有無 */
	public void setStalkCastration(boolean stalkCastration) {
		this.stalkCastration = stalkCastration;
	}

	/** 希少種か を取得する. @return 希少種か */
	public boolean isRareType() {
		return rareType;
	}

	/** 希少種か を設定する. @param rareType 希少種か */
	public void setRareType(boolean rareType) {
		this.rareType = rareType;
	}

	/** 捕食種タイプ を取得する. @return 捕食種タイプ */
	public PredatorType getPredatorType() {
		return predatorType;
	}

	/** 捕食種タイプ を設定する. @param predatorType 捕食種タイプ */
	public void setPredatorType(PredatorType predatorType) {
		this.predatorType = predatorType;
	}

	/** 捕食種かどうかを取得する. */
	@JsonIgnore
	public boolean isPredatorType() {
		return getPredatorType() != null;
	}

	/** 苦いえさが好きか を取得する. @return 苦いえさが好きか */
	public boolean isLikeBitterFood() {
		return likeBitterFood;
	}

	/** 苦いえさが好きか を設定する. @param likeBitterFood 苦いえさが好きか */
	public void setLikeBitterFood(boolean likeBitterFood) {
		this.likeBitterFood = likeBitterFood;
	}

	/** 辛いえさが好きか を取得する. @return 辛いえさが好きか */
	public boolean isLikeHotFood() {
		return likeHotFood;
	}

	/** 辛いえさが好きか を設定する. @param likeHotFood 辛いえさが好きか */
	public void setLikeHotFood(boolean likeHotFood) {
		this.likeHotFood = likeHotFood;
	}

	/** 空を飛ぶか */
	protected boolean flyingType = false;
	/** ぱんつ着用状態か */
	protected boolean hasPants = false;

	/** 空を飛ぶか を取得する. @return 空を飛ぶか */
	@Override
	public boolean isFlyingType() { return flyingType; }

	/** 空を飛ぶか を設定する. */
	@Override
	public void setFlyingType(boolean flyingType) { this.flyingType = flyingType; }

	/** ぱんつ着用状態か を取得する. @return ぱんつ着用状態か */
	public boolean isHasPants() {
		return hasPants;
	}

	/** ぱんつ着用状態か を設定する. @param hasPants ぱんつ着用状態か */
	public void setHasPants(boolean hasPants) {
		this.hasPants = hasPants;
	}

	// --- BodyNameSet fields ---
	/** 種族固有の画像ファイルベース名 */
	protected String baseBodyFileName;

	/** 種族固有の画像ファイルベース名 を取得する. @return 種族固有の画像ファイルベース名 */
	public String getBaseBodyFileName() {
		return baseBodyFileName;
	}

	/** 種族固有の画像ファイルベース名 を設定する. @param baseBodyFileName 種族固有の画像ファイルベース名 */
	public void setBaseBodyFileName(String baseBodyFileName) {
		this.baseBodyFileName = baseBodyFileName;
	}

	/** 赤ゆの一人称 */
	protected String[] babyNames;

	/** 赤ゆの一人称 を取得する. @return 赤ゆの一人称 */
	public String[] getBabyNames() {
		return babyNames;
	}

	/** 赤ゆの一人称 を設定する. @param babyNames 赤ゆの一人称 */
	public void setBabyNames(String[] babyNames) {
		this.babyNames = babyNames;
	}

	/** 子ゆの一人称 */
	protected String[] childNames;

	/** 子ゆの一人称 を取得する. @return 子ゆの一人称 */
	public String[] getChildNames() {
		return childNames;
	}

	/** 子ゆの一人称 を設定する. @param childNames 子ゆの一人称 */
	public void setChildNames(String[] childNames) {
		this.childNames = childNames;
	}

	/** 大人ゆの一人称 */
	protected String[] adultNames;

	/** 大人ゆの一人称 を取得する. @return 大人ゆの一人称 */
	public String[] getAdultNames() {
		return adultNames;
	}

	/** 大人ゆの一人称 を設定する. @param adultNames 大人ゆの一人称 */
	public void setAdultNames(String[] adultNames) {
		this.adultNames = adultNames;
	}

	/** 年齢別の一人称配列 */
	protected String[] myNames = new String[3];

	/** 年齢別の一人称配列 を取得する. @return 年齢別の一人称配列 */
	public String[] getMyNames() {
		return myNames;
	}

	/** 年齢別の一人称配列 を設定する. @param myNames 年齢別の一人称配列 */
	public void setMyNames(String[] myNames) {
		this.myNames = myNames;
	}

	/** 赤ゆの一人称（ダメージ時） */
	protected String[] babyNamesDamaged;

	/** 赤ゆの一人称（ダメージ時） を取得する. @return 赤ゆの一人称（ダメージ時） */
	public String[] getBabyNamesDamaged() {
		return babyNamesDamaged;
	}

	/** 赤ゆの一人称（ダメージ時） を設定する. @param babyNamesDamaged 赤ゆの一人称（ダメージ時） */
	public void setBabyNamesDamaged(String[] babyNamesDamaged) {
		this.babyNamesDamaged = babyNamesDamaged;
	}

	/** 子ゆの一人称（ダメージ時） */
	protected String[] childNamesDamaged;

	/** 子ゆの一人称（ダメージ時） を取得する. @return 子ゆの一人称（ダメージ時） */
	public String[] getChildNamesDamaged() {
		return childNamesDamaged;
	}

	/** 子ゆの一人称（ダメージ時） を設定する. @param childNamesDamaged 子ゆの一人称（ダメージ時） */
	public void setChildNamesDamaged(String[] childNamesDamaged) {
		this.childNamesDamaged = childNamesDamaged;
	}

	/** 大人ゆの一人称（ダメージ時） */
	protected String[] adultNamesDamaged;

	/** 大人ゆの一人称（ダメージ時） を取得する. @return 大人ゆの一人称（ダメージ時） */
	public String[] getAdultNamesDamaged() {
		return adultNamesDamaged;
	}

	/** 大人ゆの一人称（ダメージ時） を設定する. @param adultNamesDamaged 大人ゆの一人称（ダメージ時） */
	public void setAdultNamesDamaged(String[] adultNamesDamaged) {
		this.adultNamesDamaged = adultNamesDamaged;
	}

	/** 年齢別の一人称配列（ダメージ時） */
	protected String[] myNamesDamaged = new String[3];

	/** 年齢別の一人称配列（ダメージ時） を取得する. @return 年齢別の一人称配列（ダメージ時） */
	public String[] getMyNamesDamaged() {
		return myNamesDamaged;
	}

	/** 年齢別の一人称配列（ダメージ時） を設定する. @param myNamesDamaged 年齢別の一人称配列（ダメージ時） */
	public void setMyNamesDamaged(String[] myNamesDamaged) {
		this.myNamesDamaged = myNamesDamaged;
	}

	/**
	 * Yukkuri レイヤーのフィールドを to へコピーする.
	 */
	@Override
	public void copyStateTo(Entity to) {
		super.copyStateTo(to);
		Yukkuri y = (Yukkuri) to;
		y.setNoticeNoOkazari(noticeNoOkazari);
		y.setBraidType(braidType);
		y.setAnalClose(analClose);
		y.setBodyCastration(bodyCastration);
		y.setStalkCastration(stalkCastration);
		y.setRareType(rareType);
		y.setPredatorType(predatorType);
		y.setLikeBitterFood(likeBitterFood);
		y.setLikeHotFood(likeHotFood);
		y.setFlyingType(flyingType);
		y.setHasPants(hasPants);
		y.copyBodyNameSetFrom(this);
		y.copyBodySpriteSetFrom(this);
	}

	/**
	 * 出産チェックをする.
	 * @return このあと動かなくなるフラグ
	 */
	public boolean checkChildbirth() {
		boolean cantMove = false;
		if (hasBabyOrStalk() || (!hasBabyOrStalk() && isBirth())) {
			pregnantPeriod += TICK + (pregnancyPeriodBoost / 2);
			if (pregnantPeriod > getPregPeriodBase() - TICK * 100) {
				if (!isBirth() && hasBabyOrStalk()) {
					setMessage(GameMessages.getMessage(this, MessagePool.Action.Breed), true);
					wakeup();
				}
				cantMove = true;
				setBirth(true);
				pregnancyPeriodBoost = 0;
			}
			if (pregnantPeriod > getPregPeriodBase() && isPealed()) {
				damage += 40000;
				toDead();
			} else if (pregnantPeriod > getPregPeriodBase()) {
				wakeup();
				setHasBaby(false);
				setHasStalk(false);
				if (getBabyTypes().size() <= 0) {
					setBirth(false);
					pregnantPeriod = 0;
					if (isNotNYD()) {
						setMessage(GameMessages.getMessage(this, MessagePool.Action.Breed2), true);
						if (!isHasPants()) {
							if (willingFurifuri()) {
								setFurifuri(true);
							}
							stay();
						}
					}
					return cantMove;
				}
				cantMove = true;
				boolean birthAllowed = true;
				if (isHasPants() || (isFixBack() && isNeedled())) {
					birthAllowed = false;
				}
				if ((isLockmove() && (!isFixBack() || getCoreAnkoState() != CoreAnkoState.NonYukkuriDisease))
						&& !isShitting()) {
					birthAllowed = false;
				}
				if (!birthAllowed) {
					getBabyTypes().clear();
					makeDirty(true);
					if (isNotNYD()) {
						if (isLockmove() && !isHasPants()) {
							setHasPants(true);
							setMessage(GameMessages.getMessage(this, MessagePool.Action.Breed2), true);
							setHasPants(false);
						} else {
							setMessage(GameMessages.getMessage(this, MessagePool.Action.Breed2), true);
						}
					}
					setBirth(false);
					pregnantPeriod = 0;
					pregnancyPeriodBoost = 0;
					setHappiness(Happiness.VERY_SAD);
				}
			}
		}
		return cantMove;
	}

	// ===== Step6-3: BodyAttributes から移動したメソッド群 =====

	/** ゆっくりのタイプ。まりさなら0、れいむなら1、等々ユニークなタイプを表す。 */
	public abstract YukkuriType getType();

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

	// BodyNameSet — 各ゆっくり固有の名前データ（Yukkuri で実装）

	/**
	 * 名前関連データを他の Yukkuri から深く複製する.
	 * 
	 * @param from 複製元
	 */
	public void copyBodyNameSetFrom(Yukkuri from) {
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
	 * スプライト関連データを他の Yukkuri から深く複製する.
	 * 
	 * @param from 複製元
	 */
	public void copyBodySpriteSetFrom(Yukkuri from) {
		spriteDelegate().copyBodySpriteSetFrom(from);
	}

	// public variables
	// .INIファイルで変更可能な各ゆっくりのパラメータ.
	/** うにょの動きの強さ */
	public final static int UNYOSTRENGTH[] = { 4, 7, 10 };

	public static BufferedImage[] getShadowImages() {
		return YukkuriSprite.getShadowImages();
	}

	public static void setShadowImages(BufferedImage[] shadowImages) {
		YukkuriSprite.setShadowImages(shadowImages);
	}

	/**
	 * 影画像のサイズ定義を取得する.
	 * 
	 * @return 影画像のサイズ定義
	 */
	public static int[] getShadowImgW() {
		return YukkuriSprite.getShadowImgW();
	}

	/**
	 * 影画像のサイズ定義を設定する.
	 * 
	 * @param shadowImgW 影画像のサイズ定義
	 */
	public static void setShadowImgW(int[] shadowImgW) {
		YukkuriSprite.setShadowImgW(shadowImgW);
	}

	public static int[] getShadowImgH() {
		return YukkuriSprite.getShadowImgH();
	}

	public static void setShadowImgH(int[] shadowImgH) {
		YukkuriSprite.setShadowImgH(shadowImgH);
	}

	/**
	 * 影画像の中心定義を取得する.
	 * 
	 * @return 影画像の中心定義
	 */
	public static int[] getShadowPivX() {
		return YukkuriSprite.getShadowPivX();
	}

	/**
	 * 影画像の中心定義を設定する.
	 * 
	 * @param shadowPivX 影画像の中心定義
	 */
	public static void setShadowPivX(int[] shadowPivX) {
		YukkuriSprite.setShadowPivX(shadowPivX);
	}

	public static int[] getShadowPivY() {
		return YukkuriSprite.getShadowPivY();
	}

	public static void setShadowPivY(int[] shadowPivY) {
		YukkuriSprite.setShadowPivY(shadowPivY);
	}

	/** おさげのスプライト定義（年齢別） */
	protected Sprite[] braidSpr = new Sprite[3];

	/** おさげのスプライト定義（年齢別） を取得する. @return おさげのスプライト定義（年齢別） */
	public Sprite[] getBraidSpr() {
		return braidSpr;
	}

	/** おさげのスプライト定義（年齢別） を設定する. @param braidSpr おさげのスプライト定義（年齢別） */
	public void setBraidSpr(Sprite[] braidSpr) {
		this.braidSpr = braidSpr;
	}

	/**
	 * BodyAttributes レイヤーのフィールドを to へコピーする (名前・スプライトセット).
	 */
	@Override

	/**
	 * 画像がまりちゃ流しか を設定する.
	 * 
	 * @return 画像がまりちゃ流しか
	 */
	public boolean isImageNagasiMode() {
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
	public void setStress(int s) {
		if (s > 0) {
			this.stress = s;
		}
	}

	/**
	 * 強制発情フラグ want to sukkiri or not を取得する.
	 * 
	 * @return 強制発情フラグ want to sukkiri or not
	 */

	/**
	 * 針の有無 を取得する.
	 * 
	 * @return 針の有無
	 */

	/**
	 * 動けないかどうか を設定する.
	 * 
	 * @param lockmove 動けないかどうか
	 */
	public void setLockmove(boolean lockmove) {
		this.lockmove = lockmove;
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
	 * プレイヤーにすりすりされているか を取得する.
	 * 
	 * @return プレイヤーにすりすりされているか
	 */
	public boolean isSurisuriFromPlayer() {
		return surisuriFromPlayer;
	}

	/**
	 * ぷるぷるアニメーション位相 を取得する.
	 * 
	 * @return ぷるぷるアニメーション位相
	 */
	@JsonIgnore
	public boolean isShakePhase() {
		return shakePhase;
	}

	/**
	 * 対象を呼び止めるほど強い動機を持っているかどうか を取得する.
	 * 
	 * @return 対象を呼び止めるほど強い動機を持っているかどうか
	 */
	public boolean isTargetBind() {
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
	 * 移動目的がフードかどうかを設定する.
	 * 
	 * @param b 移動目的がフードかどうか
	 */
	public void setToFood(boolean b) {
		moveDelegate().setToFood(b);
	}

	/**
	 * 移動目的がすっきりかどうかを取得する.
	 * 
	 * @return 移動目的がすっきりかどうか
	 */
	public boolean isToSukkiri() {
		return getPurposeOfMoving() == PurposeOfMoving.SUKKIRI;
	}

	/**
	 * 移動目的がすっきりかどうかを設定する.
	 * 
	 * @param b 移動目的がすっきりかどうか
	 */
	public void setToSukkiri(boolean b) {
		moveDelegate().setToSukkiri(b);
	}

	/**
	 * 移動目的がうんうんかどうかを取得する.
	 * 
	 * @return 移動目的がうんうんかどうか
	 */
	public boolean isToShit() {
		return getPurposeOfMoving() == PurposeOfMoving.SHIT;
	}

	/**
	 * 移動目的がうんうんかどうかを設定する.
	 * 
	 * @param flag 移動目的がうんうんかどうか
	 */
	public void setToShit(boolean flag) {
		moveDelegate().setToShit(flag);
	}

	/**
	 * 移動目的がベッドかどうかを取得する.
	 * 
	 * @return 移動目的がベッドかどうか
	 */
	public boolean isToBed() {
		return getPurposeOfMoving() == PurposeOfMoving.BED;
	}

	/**
	 * 移動目的がベッドかどうかを設定する.
	 * 
	 * @param flag 移動目的がベッドかどうか
	 */
	public void setToBed(boolean flag) {
		moveDelegate().setToBed(flag);
	}

	/**
	 * 移動目的が他のゆっくりかどうかを取得する.
	 * 
	 * @return 移動目的が他のゆっくりかどうか
	 */
	public boolean isToBody() {
		return getPurposeOfMoving() == PurposeOfMoving.YUKKURI;
	}

	/**
	 * 移動目的が他のゆっくりかどうかを設定する.
	 * 
	 * @param flag 移動目的が他のゆっくりかどうか
	 */
	public void setToBody(boolean flag) {
		moveDelegate().setToBody(flag);
	}

	/**
	 * 移動目的がおかざりを盗むためかどうかを取得する.
	 * 
	 * @return 移動目的がおかざりを盗むためかどうか
	 */
	public boolean isToSteal() {
		return getPurposeOfMoving() == PurposeOfMoving.STEAL;
	}

	/**
	 * 移動目的がおかざりを盗むためかどうかを設定する.
	 * 
	 * @param flag 移動目的がおかざりを盗むためかどうか
	 */
	public void setToSteal(boolean flag) {
		moveDelegate().setToSteal(flag);
	}

	/**
	 * 移動目的がアイテムを持つことかどうかを取得する.
	 * 
	 * @return 移動目的がアイテムを持つことかどうか
	 */
	public boolean isToTakeout() {
		return getPurposeOfMoving() == PurposeOfMoving.TAKEOUT;
	}

	/**
	 * 移動目的がアイテムを持つことかどうかを設定する.
	 * 
	 * @param flag 移動目的がアイテムを持つことかどうか
	 */
	public void setToTakeout(boolean flag) {
		moveDelegate().setToTakeout(flag);
	}

	/** 移動目標のみキャンセル */
	public final void clearTargets() {
		moveDelegate().clearTargets();
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
	 * 待機アクション中かどうかを設定する.
	 * 
	 * @param staying 待機アクション中かどうか
	 */
	public void setStaying(boolean staying) {
		this.staying = staying;
	}

	/**
	 * 怒っているか否か を設定する.
	 * 
	 * @param angry 怒っているか否か
	 */

	/**
	 * 壁に引っかかった回数 を設定する.
	 * 
	 * @param blockedTicks 壁に引っかかった回数
	 */

	/**
	 * メッセージラインの色 を設定する.
	 * 
	 * @param messageLineColor メッセージラインの色
	 */
	public void setOrigMessageLineColor(Color messageLineColor) {
		messageDelegate().setOrigMessageLineColor(messageLineColor);
	}

	/**
	 * メッセージボックスの色 を設定する.
	 * 
	 * @param messageBoxColor メッセージボックスの色
	 */
	public void setOrigMessageBoxColor(Color messageBoxColor) {
		messageDelegate().setOrigMessageBoxColor(messageBoxColor);
	}

	/**
	 * メッセージテキストの色 を設定する.
	 * 
	 * @param messageTextColor メッセージテキストの色
	 */
	public void setOrigMessageTextColor(Color messageTextColor) {
		messageDelegate().setOrigMessageTextColor(messageTextColor);
	}

	/**
	 * ゆっくりの移動速度 を設定する.
	 * 
	 * @param speed ゆっくりの移動速度
	 */

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
	 * 妹のインスタンスを取得する.
	 * 
	 * @param sisterIndex 何番目の妹か
	 * @return 妹のインスタンス
	 */
	public Yukkuri getSister(int sisterIndex) {
		return BodyRelations.getSister(this, sisterIndex);
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
	 * 痛みを感じているかどうかを返却する.
	 * 
	 * @return 痛みを感じているか
	 */
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
	/**
	 * 死ねない期間中かどうかを取得する.
	 * 
	 * @return 死ねない期間中かどうか
	 */
	@Transient
	public boolean isCantDie() {
		return getCantDiePeriod() > 0;
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
		return getBindStalk() != null;
	}

	/**
	 * パニック状態をクリアする.
	 */
	public void setForcePanicClear() {
		panicType = null;
		panicPeriod = 0;
	}

	/**
	 * 睡眠中かどうかを取得する.
	 * 死んでいないことが条件.
	 * 
	 * @return 睡眠中かどうか
	 */
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
	/**
	 * 胎生妊娠してる赤ゆを取得
	 * <br>
	 * 出産時に、順番に生んでゆくときの処理に使われている
	 * 
	 * @return 胎生妊娠してる赤ゆのDNA
	 **/
	@Transient
	public Dna getBabyTypesDequeue() {
		return familyDelegate().getBabyTypesDequeue();
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
		return stalkDelegate().getStalksDequeue();
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
		return spriteDelegate().getCollisionX();
	}

	@Transient
	public int getCollisionY() {
		return spriteDelegate().getCollisionY();
	}

	/**
	 * 足の速さを取得する.
	 * 
	 * @return 足の速さ
	 */
	@Transient
	public int getStep() {
		return moveDelegate().getStep();
	}

	@Transient
	public int getStepDist() {
		return moveDelegate().getStepDist();
	}

	@Transient
	public Sprite getBodyBaseSpr() {
		return spriteDelegate().getBodyBaseSpr();
	}

	@Transient
	public Sprite getBodyExpandSpr() {
		return spriteDelegate().getBodyExpandSpr();
	}

	@Transient
	public Sprite getBraidSprite() {
		return spriteDelegate().getBraidSprite();
	}

	@Transient
	public BufferedImage getShadowImage() {
		return spriteDelegate().getShadowImage();
	}

	@Transient
	public int getShadowH() {
		return spriteDelegate().getShadowH();
	}

	@Transient
	public int getW() {
		return spriteDelegate().getW();
	}

	@Transient
	public int getH() {
		return spriteDelegate().getH();
	}

	@Transient
	public int getPivotX() {
		return spriteDelegate().getPivotX();
	}

	@Transient
	public int getPivotY() {
		return spriteDelegate().getPivotY();
	}

	@Transient
	public int getBraidW() {
		return spriteDelegate().getBraidW();
	}

	@Transient
	public int getBraidH() {
		return spriteDelegate().getBraidH();
	}

	/**
	 * 体重を取得する.
	 * 外力を算出するときに使用する.
	 * 
	 * @return 体重
	 */
	@Transient
	public int getWeight() {
		return (getWeightBase()[getBodyAgeState().ordinal()]
				+ (getBabyTypes().size() + getStalkBabyTypes().size()) * 50);
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
	 * 行動目的を設定する.
	 *
	 * @param purposeOfMoving 行動目的
	 */

	/**
	 * 取られているかどうかを設定する.
	 * 
	 * @param taken 取られているかどうか
	 */
	public void setTaken(boolean taken) {
		this.taken = taken;
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
	 * イベントに反応できる状態かチェックする
	 * イベントの重要度で寝ていても起きたりできるようにするため
	 * ここでは動いたら見た目におかしくなる状況のみチェック
	 *
	 * @return
	 */
	public final boolean canEventResponse() {
		return eventDelegate().canEventResponse();
	}

}
