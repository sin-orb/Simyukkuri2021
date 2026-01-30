package src.base;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import src.Const;
import src.SimYukkuri;
import src.attachment.ANYDAmpoule;
import src.attachment.AccelAmpoule;
import src.attachment.Ants;
import src.attachment.Badge;
import src.attachment.Fire;
import src.attachment.Needle;
import src.attachment.PoisonAmpoule;
import src.attachment.StopAmpoule;
import src.attachment.VeryShitAmpoule;
import src.base.Okazari.OkazariType;
import src.draw.Dimension4y;
import src.draw.ModLoader;
import src.draw.MyPane;
import src.draw.Rectangle4y;
import src.draw.Terrarium;
import src.draw.Translate;
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
import src.enums.EnumRelationMine;
import src.enums.Event;
import src.enums.FavItemType;
import src.enums.FootBake;
import src.enums.HairState;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.enums.LovePlayer;
import src.enums.Numbering;
import src.enums.PanicType;
import src.enums.Parent;
import src.enums.PlayStyle;
import src.enums.PublicRank;
import src.enums.PurposeOfMoving;
import src.enums.TakeoutItemType;
import src.enums.TangType;
import src.enums.Type;
import src.enums.UnbirthBabyState;
import src.enums.Where;
import src.enums.WindowType;
import src.event.AvoidMoldEvent;
import src.event.BegForLifeEvent;
import src.event.BreedEvent;
import src.event.CutPenipeniEvent;
import src.event.HateNoOkazariEvent;
import src.event.KillPredeatorEvent;
import src.event.PredatorsGameEvent;
import src.event.ProposeEvent;
import src.event.RaperReactionEvent;
import src.event.RaperWakeupEvent;
import src.event.RevengeAttackEvent;
import src.event.SuperEatingTimeEvent;
import src.game.Dna;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.item.Barrier;
import src.item.Bed;
import src.item.Food;
import src.item.Pool;
import src.item.StickyPlate;
import src.item.Sui;
import src.item.Toilet;
import src.item.Trampoline;
import src.logic.BodyLogic;
import src.logic.EventLogic;
import src.logic.FamilyActionLogic;
import src.logic.ToyLogic;
import src.logic.TrashLogic;
import src.system.BodyLayer;
import src.system.FieldShapeBase;
import src.system.ItemMenu.GetMenuTarget;
import src.system.ItemMenu.UseMenuTarget;
import src.system.MainCommandUI;
import src.system.MapPlaceData;
import src.system.MessagePool;
import src.system.ResourceUtil;
import src.system.Sprite;
import src.util.IniFileUtil;
import src.util.YukkuriUtil;

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
public abstract class Body extends BodyAttributes {
	private static final long serialVersionUID = 8856385435939508588L;

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
	public int getHybridType(int partnerType) {
		return getType();
	}

	/**
	 * 突然変異チェックをする.
	 * 現状 れいむ→でいぶ/まりさ→どす のみ.
	 * 突然変異可能な子クラスでオーバーライドする.
	 * 
	 * @return 突然変異する際のゆっくりのインスタンス
	 */
	public Body checkTransform() {
		return null;
	}

	@Override
	public String toString() {
		String name = ResourceUtil.IS_JP ? getNameJ() : getNameE();
		StringBuilder ret = new StringBuilder(name);
		if (isUnBirth()) {
			ret.append("(" + ResourceUtil.getInstance().read("base_fruit") + ")");
		} else {
			ret.append(" (" + getBodyAgeState().getName() + ")");
		}

		return ret.toString();
	}

	/**
	 * checkといいつつ空腹操作をしているメソッド.
	 * ゆっくりの様々な状態に応じて腹を減らしている。
	 */
	public void checkHungry() {
		// すーぱーむーしゃむーしゃたいむ実施後は一定期間お腹が減らない、というかむしろ腹いっぱいになっていく
		if (0 < getNoHungrybySupereatingTimePeriod()) {
			noHungrybySupereatingTimePeriod--;
			if (hungry <= getHungryLimit()) {
				hungry += TICK;
			}
			return;
		}

		// 皮がむかれている/饅頭化させられている場合は通常の1/7の速度で腹が減る
		if (isPealed() || isPacked()) {
			if (getAge() % 7 == 0)
				hungry -= TICK;
		}

		// 生まれていない場合
		if (isUnBirth()) {
			// 親と茎で繋がっていない場合のみ通常の100倍空腹になる
			if (!isPlantForUnbirthChild()) {
				hungry -= TICK * 100;
			}
		}
		// 寝ている場合の空腹は通常の1/2倍
		else if (isSleeping()) {
			if (getAge() % 2 == 0)
				hungry -= TICK;
		}
		// レイパーではないが発情している場合の空腹は通常の(抱えている胎生ゆの数+1)倍空腹になる
		else if (isExciting() && !isRaper()) {
			hungry -= TICK * (getBabyTypes().size() + 1);
		}
		// それ以外の場合は通常の腹減り
		else {
			hungry -= TICK;
		}

		// 茎が生えていると茎の数*5倍だけさらに腹が減る
		if (isHasStalk() && getStalks() != null) {
			hungry -= TICK * getStalks().size() * 5;
		}
		// 胎生ゆがいるとその分さらに腹が減る
		if (isHasBaby()) {
			hungry -= TICK * getBabyTypes().size();
		}
		// 満腹度が0になって腹が減るとその分ダメージになる
		if (hungry <= 0) {
			damage += (-hungry);
			hungry = 0;
		}
		// 腹が減っておらず、寝てもいない状態でこの1Tickを過ごすと「飢餓状態になっていない期間」を1増やす
		if (!isHungry() && !isSleeping()) {
			noHungryPeriod += TICK;
		} else {
			noHungryPeriod = 0;
		}
	}

	/**
	 * checkといいつつダメージ計算をしているメソッド.
	 */
	public void checkDamage() {
		// オレンジジュースや砂糖水などで体力が回復するかどうかのフラグ
		boolean bHealFlag = true;
		// 実ゆの場合、茎で生きている親につながっているなら回復フラグON
		if (isUnBirth()) {
			bHealFlag = isPlantForUnbirthChild();
		}

		// かびてる時のダメージ加算
		if (isSick()) {
			// かびている期間が潜伏期間の32倍を超え、かつダメージをヘビーに受けている場合
			if (getSickPeriod() > (INCUBATIONPERIODorg * 32) && isDamagedHeavily()) {
				// 追加ダメージは1/3の確率で1
				if (SimYukkuri.RND.nextInt(3) == 0)
					damage += TICK;
			}
			// かびている期間が潜伏期間の32倍を超えていて、ダメージがヘビーでない場合
			else if (getSickPeriod() > (INCUBATIONPERIODorg * 32)) {
				// 通常の3倍ダメージ
				damage += TICK * 3;
			}
			// かびている期間が潜伏期間の8倍を超えている場合
			else if (getSickPeriod() > (INCUBATIONPERIODorg * 8)) {
				// 通常の2倍のダメージ
				damage += TICK * 2;
			}
			// かびている期間が潜伏期間と同じ
			else if (getSickPeriod() > INCUBATIONPERIODorg) {
				// 通常ダメージ
				damage += TICK;
			}
		}
		// 非空腹状態では回復する(かびてるときは非適用)
		else if (!isHungry()) {
			damage -= TICK;
		}
		// 空腹による消耗（空腹時に更に腹がへることによって受けるダメージとは別。そちらはダメージ計算メソッドで行う。）
		if (hungry <= 0) {
			damage += TICK;
		}

		// ケガしてる時
		if (getCriticalDamege() != null) {
			// 切られてるとき
			if (getCriticalDamege() == CriticalDamegeType.CUT) {
				// 100倍のダメージ
				damage += TICK * 100;
				addStress(50);
				if (isSleeping())
					wakeup();
				Terrarium.setAlarm();
				// 1/50の確率でしゃべる
				if (SimYukkuri.RND.nextInt(50) == 0) {
					if (geteCoreAnkoState() != CoreAnkoState.NonYukkuriDiseaseNear)
						setNYDMessage(MessagePool.getMessage(this, MessagePool.Action.Dying2), false);
					else
						setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying2));
				}
			}
			// 傷を負っているとき
			else if (getCriticalDamege() == CriticalDamegeType.INJURED && !isSleeping()) {
				// 1/300の確率で餡子を漏らす
				if (SimYukkuri.RND.nextInt(300) == 0) {
					SimYukkuri.mypane.getTerrarium().addCrushedVomit(getX() + 3 - SimYukkuri.RND.nextInt(6), getY() - 2,
							0,
							this,
							getShitType());
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream));
					setForceFace(ImageCode.PAIN.ordinal());
					makeDirty(true);
					addStress(5);
					addDamage(50);
				}
				// お腹が一杯で、ダメージがないときに1/4800の確率で傷が治る
				if (isFull() && isNoDamaged() && SimYukkuri.RND.nextInt(4800) == 0) {
					setCriticalDamege(null);
				}
				// ダメージがヘビーでない場合は1/33600の確率で傷が治る
				else if (!isDamagedHeavily() && SimYukkuri.RND.nextInt(33600) == 0) {
					setCriticalDamege(null);
				}
			}
		}
		// 皮むき時の基本反応
		if (isPealed()) {
			if (isSleeping())
				wakeup();
			damage += TICK * 50;
			setSickPeriod(0);
			Terrarium.setAlarm();
			setPeropero(false);
			addStress(200);
			addMemories(-5);
			if (geteCoreAnkoState() == CoreAnkoState.NonYukkuriDiseaseNear) {
				setNYDMessage(MessagePool.getMessage(this, MessagePool.Action.Dying2), false);
			}
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying2));
		}
		// 饅頭化されたときの基本反応
		if (isPacked()) {
			Terrarium.setAlarm();
			setPeropero(false);
			addStress(50);
			addMemories(-2);
			setCanTalk(false);
			if (SimYukkuri.RND.nextInt(200) == 0)
				stayPurupuru(20);
		}

		// 路上だと、善良なバッジ付き以外は、一定確率で踏み潰される
		if (SimYukkuri.world.getCurrentMap().getMapIndex() == 2 && !(isSmart() && getAttachmentSize(Badge.class) != 0)
				&& getCarAccidentProb() != 0 && SimYukkuri.RND.nextInt(getCarAccidentProb()) == 0) {
			strikeByPress();
		}

		// ディヒューザーオレンジ
		if (Terrarium.isOrangeSteam()) {
			if (bHealFlag) {
				damage -= TICK * 50;
			}
		}
		// ディヒューザー砂糖水
		if (Terrarium.isSugerSteam()) {
			// ダメージ限界の8割以上の場合
			if (damage >= getDAMAGELIMITorg()[getBodyAgeState().ordinal()] * 80 / 100) {
				if (bHealFlag) {
					damage -= TICK * 100;
				}
			}
		}
		// ディヒューザー駆除剤
		if (Terrarium.isPoisonSteam()) {
			damage += TICK * 100;
			clearActions();
			setExciting(false);
			setShitting(false);
			setFurifuri(false);
			wakeup();
			if (isNotNYD()) {
				setHappiness(Happiness.VERY_SAD);
				if (getDamageState() != Damage.NONE) {
					setNegiMessage(MessagePool.getMessage(this, MessagePool.Action.PoisonDamage), true);
				} else {
					setMessage(MessagePool.getMessage(this, MessagePool.Action.PoisonDamage), Const.HOLDMESSAGE, false,
							true);
				}
			}
		}

		// 微調整
		if (damage < 0) {
			damage = 0;
		}
		Damage newDamageState = getDamageState();
		// ダメージ外観と今回判定含めたダメージ判定がノーダメの場合、寝ていない時間をノーダメージ期間に加算
		if (getDamageState() == Damage.NONE && newDamageState == Damage.NONE && !isSleeping()) {
			noDamagePeriod += TICK;
		} else {
			noDamagePeriod = 0;
		}
		// ダメージ外観と今回のダメージを合わせる
		setDamageState(newDamageState);
		if (getDamageState() == Damage.TOOMUCH && getCurrentEvent() != null
				&& getCurrentEvent().getPriority() != EventPacket.EventPriority.HIGH) {
			// ダメージおいすぎてる場合、イベント優先度が高でないすべてのイベントをクリア。
			clearEvent();
		}
	}

	/**
	 * 自身が突然変異可能な状態かどうかチェック
	 * 
	 * @return 自身が突然変異可能な状態かどうか
	 */
	public boolean canTransform() {
		if (isDead())
			return false; // 生きてて
		if (getStress() > 0)
			return false; // ストレスがなく
		if (getTangType() == TangType.POOR)
			return false; // バカ舌ではなく
		if (isDamaged())
			return false; // 致命的ダメージ無く
		if (isFeelPain())
			return false; // 破裂しかけていなく
		if (isUnBirth())
			return false; // 実ゆではなく
		if (getPublicRank() == PublicRank.UnunSlave)
			return false; // うんうん奴隷ではなく
		if (isNYD())
			return false; // 非ゆっくり症ではく
		if (isBlind() || isPealed() || isPacked() || isShutmouth())
			return false; // 目抜き/皮むき/あにゃる封印/口封じされておらず
		if (geteHairState() != HairState.DEFAULT)
			return false; // はげまんじゅうにされていない
		return true;// そのような場合のみ、突然変異可能
	}

	/**
	 * アリ関連の処理.
	 */
	public void checkAnts() {
		// すでに潰れてるかアリの数が0かディフューザー無限もるんもるんの場合、アリ解除
		if (isCrushed() || Terrarium.isEndlessFurifuriSteam()) {
			removeAnts();
			return;
		}
		// すでにアリがたかっていると、5回に1回、アリの数が2増える
		if (getAttachmentSize(Ants.class) != 0 && getAge() % 5 == 0) {
			numOfAnts += TICK * 2;
			// アリにたかられてたらイベントどころじゃないでしょ
			clearEvent();
			return;
		}
		// マップが部屋のとき、もしくは飛んでいるやつにアリはたからない
		if (SimYukkuri.world.getCurrentMap().getMapIndex() == 0 || getZ() != 0) {
			return;
		}
		// 新規でアリたかる？
		YukkuriUtil.judgeNewAnt(this);
	}

	/**
	 * うにょ機能。
	 * ゆっくりがうにょうにょ動く機能、のようだ。
	 * 重いため、シムゆっくり起動時にチェックボックスで機能をONにするかどうかを決めることができる。
	 */
	public void checkUnyo() {
		if (SimYukkuri.UNYO) {
			// 移動時にサイズを変更、z座標では管理しておらずlayerのmodeとage % 9で判定されているもよう
			// 条件式は顔画像のlayer条件を流用
			if (getAge() % 9 == 0) {
				if (!isDead() && !isLockmove()) {
					if (getCriticalDamegeType() != CriticalDamegeType.CUT && !grabbed && !isPealed() && !isPacked()) {
						if (!isUnyoActionAll() && !isSleeping()) {
							if (!canflyCheck()) {
								if (getFootBakeLevel() == FootBake.NONE &&
										!isDamaged() && !isSick() && !isFeelPain()
										&& takeMappedObj(getLinkParent()) == null
										&& !isPeropero() && !(isEating() && !isPikopiko())) {
									changeUnyo(0, 0,
											(int) (SimYukkuri.RND
													.nextInt(((int) UNYOSTRENGTH[getBodyAgeState().ordinal()] / 3)))
													+ UNYOSTRENGTH[getBodyAgeState().ordinal()]);
								}
							} else if (z == 0) {
								if (getFootBakeLevel() == FootBake.NONE &&
										!isDamaged() && !isSick() && !isFeelPain()
										&& takeMappedObj(getLinkParent()) == null
										&& !isPeropero() && !(isEating() && !isPikopiko())) {
									changeUnyo(0, 0,
											(int) (SimYukkuri.RND
													.nextInt(((int) UNYOSTRENGTH[getBodyAgeState().ordinal()] / 3)))
													+ UNYOSTRENGTH[getBodyAgeState().ordinal()]);
								}
							}
						}
					}
				}
			}
			// 常時ランダムで動く
			if (SimYukkuri.RND.nextInt(30) == 0 && (isSleeping() ? SimYukkuri.RND.nextBoolean() : true)) {
				changeUnyo((int) (SimYukkuri.RND.nextInt(2)), (int) (SimYukkuri.RND.nextInt(2)),
						(int) (SimYukkuri.RND.nextInt(2)));
			}
			if (isDamaged() ? (SimYukkuri.RND.nextInt(5) == 0) : true) {
				changeReUnyo();
			}
		}
	}

	/**
	 * うにょ機能が使用されるゆっくりのアクション
	 * 
	 * @return 現在の状態でうにょ機能を適用できるかどうか
	 */
	@Transient
	public boolean isUnyoActionAll() {
		return isShitting() || isBirth() || isFurifuri() || isEating() || isPeropero() || isSukkiri() ||
				isEatingShit() || isNobinobi() || isVain() || isPikopiko() || isYunnyaa();
		// return shitting || birth || furifuri || strike || eating || peropero ||
		// sukkiri ||
		// eatingShit || silent || nobinobi || pikopiko;
	}

	/**
	 * Adjusts unyo offsets based on input deltas.
	 * 
	 * @param x x offset
	 * @param y y offset
	 * @param z z offset
	 */
	public void changeUnyo(int x, int y, int z) {
		if (!isDead() && !isCrushed()) {
			if (x != 0) {
				unyoForceH += x;
				unyoForceW -= x;
			}
			if (y != 0) {
				unyoForceH -= y;
				unyoForceW += y;
			}
			if (z != 0) {
				unyoForceH -= z;
				unyoForceW += z;
			}
			// 限界を越えていると描画が裏返るので調整
			if (unyoForceH > Const.EXT_FORCE_PULL_LIMIT[getBodyAgeState().ordinal()])
				unyoForceH = Const.EXT_FORCE_PULL_LIMIT[getBodyAgeState().ordinal()];
			else if (unyoForceH < Const.EXT_FORCE_PUSH_LIMIT[getBodyAgeState().ordinal()])
				unyoForceH = Const.EXT_FORCE_PUSH_LIMIT[getBodyAgeState().ordinal()];
			if (unyoForceW < Const.EXT_FORCE_PUSH_LIMIT[getBodyAgeState().ordinal()])
				unyoForceW = Const.EXT_FORCE_PUSH_LIMIT[getBodyAgeState().ordinal()];
			else if (unyoForceW > Const.EXT_FORCE_PULL_LIMIT[getBodyAgeState().ordinal()])
				unyoForceW = Const.EXT_FORCE_PULL_LIMIT[getBodyAgeState().ordinal()];
		}
	}

	/**
	 * Eases unyo offsets back toward neutral.
	 */
	public void changeReUnyo() {
		if (unyoForceH == 0) {
		} else if (unyoForceH < Const.EXT_FORCE_PUSH_LIMIT[getBodyAgeState().ordinal()] * 0.6)
			unyoForceH += SimYukkuri.RND.nextInt(3) + 5;
		else if (unyoForceH < 0)
			unyoForceH += SimYukkuri.RND.nextInt(3) + 2;
		else if (unyoForceH > Const.EXT_FORCE_PULL_LIMIT[getBodyAgeState().ordinal()] * 0.6)
			unyoForceH -= SimYukkuri.RND.nextInt(3) + 5;
		else if (unyoForceH > 0)
			unyoForceH -= SimYukkuri.RND.nextInt(3) + 2;
		if (unyoForceW == 0) {
		} else if (unyoForceW < Const.EXT_FORCE_PUSH_LIMIT[getBodyAgeState().ordinal()] * 0.6)
			unyoForceW += SimYukkuri.RND.nextInt(3) + 5;
		else if (unyoForceW < 0)
			unyoForceW += SimYukkuri.RND.nextInt(3) + 2;
		else if (unyoForceW > Const.EXT_FORCE_PULL_LIMIT[getBodyAgeState().ordinal()] * 0.6)
			unyoForceW -= SimYukkuri.RND.nextInt(3) + 5;
		else if (unyoForceW > 0)
			unyoForceW -= SimYukkuri.RND.nextInt(3) + 2;
	}

	/**
	 * Resets unyo offsets to neutral.
	 */
	public void resetUnyo() {
		unyoForceH = 0;
		unyoForceW = 0;
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
		// 実ゆっくりの場合
		if (isUnBirth()) {
			// うんうんアンプルが刺さっている
			if (getAttachmentSize(VeryShitAmpoule.class) != 0) {
				// 限界を超えた場合のチェック
				if (shit > getSHITLIMITorg()[getBodyAgeState().ordinal()]) {
					int nNowDamage = 100 * damage / getDamageLimit();
					// 現在のダメージがダメージ限界の1/10以下ならダメージを与える
					if (nNowDamage < 10) {
						addDamage(Const.NEEDLE * 5);
					}
					// あなる閉鎖時
					if (isAnalClose() || (isFixBack() && isbNeedled())) {
						setHappiness(Happiness.VERY_SAD);
						// 破裂寸前までうんうんをためる
						if (getBurstState() != Burst.NEAR) {
							shit += TICK * 2 + (shitBoost * 20);
						}
					} else {
						// あなる未閉鎖
						makeDirty(true);
						// おくるみあり
						if (isHasPants()) {
							setHappiness(Happiness.VERY_SAD);
							shit = 1;
							clearActions();
						} else {
							setHappiness(Happiness.SAD);
							shit = 0;
							clearActions();
						}
					}
					setShitting(false);
					addStress(100);
					// 実ゆの場合、親が反応する
					if (SimYukkuri.RND.nextInt(20) == 0) {
						checkReactionStalkMother(UnbirthBabyState.SAD);
					}
				} else {
					shit += TICK * 2 + (shitBoost * 20);
				}
			}
			return true;
		}

		// うんうん無効判定
		// 溶けている場合,完全足焼きした場合,食事中、ぺろぺろ中、すっきり中はうんうんしない
		if ((getFootBakeLevel() == FootBake.CRITICAL && !isPealed()) ||
				isMelt() || isEating() || isPeropero() || isSukkiri() || isPacked()) {
			return false;
		}
		// レイパー発情中はうんうん無効
		if (isRaper() && isExciting()) {
			setShitting(false);
			shit--;
			if (purposeOfMoving == PurposeOfMoving.SHIT) {
				purposeOfMoving = null;
			}
			setStaying(false);
			return false;
		}
		// 実験 イベント中は空腹、睡眠、便意が増えないように
		if (getCurrentEvent() != null && getCurrentEvent().getPriority() != EventPacket.EventPriority.LOW) {
			return false;
		}

		// うんうん蓄積処理
		// うんうんの蓄積の減少度判定
		int nDown = 1;
		// うんうん奴隷
		if (getPublicRank() == PublicRank.UnunSlave) {
			if (!isShitting()) {
				nDown = 5;
			}
		}
		// 地中
		if (getBaryState() != BaryInUGState.NONE) {
			nDown = 10;
		}

		boolean cantMove = false;
		// 蓄積実行
		if (SimYukkuri.RND.nextInt(nDown) == 0) {
			if (isFull()) {
				shit += TICK * 2 + (shitBoost * 20);
			} else {
				shit += TICK + (shitBoost * 20);
			}
		}

		// ちぎれ状態の場合は餡子を漏らす
		if ((getCriticalDamege() == CriticalDamegeType.CUT || isPealed()) && getBaryState() == BaryInUGState.NONE) {
			if (shit > getSHITLIMITorg()[getBodyAgeState().ordinal()] - TICK * Const.SHITSTAY * 2) {
				SimYukkuri.mypane.getTerrarium().addCrushedVomit(getX() + 3 - SimYukkuri.RND.nextInt(6), getY() - 2, 0,
						this,
						getShitType());
				addDamage(Const.NEEDLE * 2);
				shit = 1;
				if (shitBoost > 0) {
					shitBoost--;
					strike(Const.NEEDLE * 2);
				}
				return true;
			}
		}

		// 寝ている場合はうんうん限界の1.5倍までは我慢できる
		if (isSleeping()) {
			if (shit < (getSHITLIMITorg()[getBodyAgeState().ordinal()] * 1.5f)) {
				setShitting(false);
				return false;
			}
		}

		// 非ゆっくり症ではない場合
		if (isNotNYD() && getBaryState() == BaryInUGState.NONE) {
			// うんうん奴隷ではない場合
			if (getPublicRank() != PublicRank.UnunSlave) {
				Obj oTarget = takeMoveTarget();
				// もしトイレに到着していたら即排泄へ
				if (isToShit() && oTarget instanceof Toilet) {
					if (((Toilet) oTarget).checkHitObj(this)) {
						if (shit < getSHITLIMITorg()[getBodyAgeState().ordinal()] - TICK * Const.SHITSTAY + 1) {
							shit = getSHITLIMITorg()[getBodyAgeState().ordinal()] - TICK * Const.SHITSTAY + 1;
						}
					} else if (checkOnBed()) {// トイレがある場合
						// 大人で寝てたなら起きる
						if (getBodyAgeState() == AgeState.ADULT && isSleeping()) {
							wakeup();
						}
						// トイレに到着していないかつベッドの上では我慢する
						if (shit < (getSHITLIMITorg()[getBodyAgeState().ordinal()] * 1.5f)) {
							setShitting(false);
							return false;
						}
					} else if ((getAttitude() == Attitude.NICE || getAttitude() == Attitude.VERY_NICE)
							|| (getAttitude() == Attitude.AVERAGE && getIntelligence() == Intelligence.WISE)) {
						// 性格が善良か普通でも知能が高ければトイレに着くまで150%まで我慢できる
						if (shit < (getSHITLIMITorg()[getBodyAgeState().ordinal()] * 1.5f)) {
							setShitting(false);
							return false;
						}
					}
				}
				// トイレがない場合
				else if (checkOnBed()) {
					// ベッドの上では我慢する
					if (shit < (getSHITLIMITorg()[getBodyAgeState().ordinal()] * 1.5f)) {
						setShitting(false);
						return false;
					}
				}
			}

			// 限界が近づいたら排泄チェック
			if (shit > getSHITLIMITorg()[getBodyAgeState().ordinal()] - TICK * Const.SHITSTAY) {
				// あなるがふさがれていない
				if (!isAnalClose() && !(isFixBack() && isbNeedled())) {
					// 寝ているか埋まっているか粘着床(あんよ固定)についているか針が刺さっていたら体勢をかえられずに漏らす
					if ((isLockmove() && !isFixBack()) || isSleeping() || isbNeedled()
							|| getBaryState() != BaryInUGState.NONE) {
						makeDirty(true);
						setHappiness(Happiness.VERY_SAD);
						addStress(150);
						shit = 0;
						clearActions();
						if (shitBoost > 0) {
							shitBoost--;
							addDamage(Const.NEEDLE * 2);
							addStress(400);
						}
						return true;
					}
				}

				// 排泄準備
				if (isHasPants()) {
					setHappiness(Happiness.SAD);
				}

				// あなるがふさがれていない
				if (!isAnalClose() && !(isFixBack() && isbNeedled())) {
					if (getAge() % 100 == 0) {
						if (!isShitting()) {
							setMessage(MessagePool.getMessage(this, MessagePool.Action.Shit), TICK * Const.SHITSTAY);
							stay();
							wakeup();
							setShitting(true);
							cantMove = true;
							return cantMove;
						}
					}
				}
				if (isShitting()) {
					cantMove = true;
				}
			} else {
				// While shitting is true, the yukkuri might grow up. So, these flags should be
				// clear.
				setShitting(false);
				cantMove = false;
			}
		}

		// 限界を超えた場合のチェック
		if (shit > getSHITLIMITorg()[getBodyAgeState().ordinal()]) {
			// 肛門が塞がれてなければ排泄
			if (!isAnalClose() && !(isFixBack() || isbNeedled()) && getBaryState() == BaryInUGState.NONE) {
				setShitting(false);
				clearActions();
				shit = 0;
				if (getBodyAgeState() == AgeState.BABY) {
					makeDirty(true);
					setHappiness(Happiness.SAD);
					addStress(200);
				}

				if (isHasPants() || isNYD()) {
					makeDirty(true);
					setHappiness(Happiness.VERY_SAD);
					addStress(400);
				}

				if (isNotNYD()) {
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Shit2));
					stay();
					if (!isHasPants()) {
						if (willingFurifuri()) {
							setFurifuri(true);
							addStress(-200);
						}
						stay();
						addStress(-100);
					}
				}

				if (shitBoost > 0) {
					shitBoost--;
					addDamage(Const.NEEDLE * 2);
					addStress(400);
				}
			} else {
				// 塞がってたら膨らんで破裂
				wakeup();
				if (isNotNYD()) {
					if (getBurstState() == Burst.NEAR) {
						if (SimYukkuri.RND.nextInt(10) == 0) {
							setMessage(MessagePool.getMessage(this, MessagePool.Action.Inflation));
							stay();
						}
					} else {
						if (SimYukkuri.RND.nextInt(10) == 0) {
							setMessage(MessagePool.getMessage(this, MessagePool.Action.CantShit), true);
							stay();
						}
					}
				}

				setHappiness(Happiness.SAD);
				// if(SimYukkuri.RND.nextInt(4) == 0){
				shit += TICK + (shitBoost * 10);
				// }

				if (!isAnalClose() || getAge() % 100 == 0) {
					setShitting(false);
				}
				addStress(1);
			}
		}
		return cantMove;
	}

	/**
	 * 出産関連チェック.
	 * 
	 * @return trueで出産に向けてゆっくりが動かなくなる。
	 */
	public boolean checkChildbirth() {
		// このあと動かなくなるフラグ
		boolean cantMove = false;
		if (hasBabyOrStalk() || (!hasBabyOrStalk() && isBirth())) {
			pregnantPeriod += TICK + (pregnantPeriodBoost / 2);
			// 出産直前
			if (pregnantPeriod > getPREGPERIODorg() - TICK * 100) {
				if (!isBirth() && hasBabyOrStalk()) {
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Breed), true);
					wakeup();
				}
				cantMove = true;
				setBirth(true);
				pregnantPeriodBoost = 0;
			}
			// 皮がない時の出産
			if (pregnantPeriod > getPREGPERIODorg() && isPealed()) {
				damage += 40000;
				toDead();
			}
			// 出産
			else if (pregnantPeriod > getPREGPERIODorg()) {
				// Keep babyType for generating baby.
				wakeup();
				setHasBaby(false);
				setHasStalk(false);
				if (getBabyTypes().size() <= 0) {
					setBirth(false);
					pregnantPeriod = 0;
					if (isNotNYD()) {
						setMessage(MessagePool.getMessage(this, MessagePool.Action.Breed2), true);
						if (!isHasPants()) {
							if (willingFurifuri()) {
								setFurifuri(true);
							}
							stay();
						}
					}
					return cantMove;
				} else {
					cantMove = true;
				}
				boolean bBirthFlag = true;
				// 穴がふさがれている
				if (isHasPants() || (isFixBack() && isbNeedled())) {
					bBirthFlag = false;
				}
				// 動けない
				if ((isLockmove() && (!isFixBack() || geteCoreAnkoState() != CoreAnkoState.NonYukkuriDisease))
						&& !isShitting()) {
					bBirthFlag = false;
				}
				// 非ゆっくり症
				// 20210415 削除。非ゆっくり症だって出産くらいするでしょ
				// if (isNYD()) {
				// bBirthFlag = false;
				// }
				if (!bBirthFlag) {
					// お腹の赤ゆだけクリア
					getBabyTypes().clear();
					makeDirty(true);
					if (isNotNYD()) {
						if (isLockmove() && !isHasPants()) {
							setHasPants(true);
							setMessage(MessagePool.getMessage(this, MessagePool.Action.Breed2), true);
							setHasPants(false);
						} else {
							setMessage(MessagePool.getMessage(this, MessagePool.Action.Breed2), true);
						}
					}
					setHappiness(Happiness.VERY_SAD);
				}
			}
		}
		return cantMove;
	}

	/**
	 * 睡眠チェックをする.
	 * 
	 * @return このあと動けなくなるフラグ
	 */
	public boolean checkSleep() {
		// ディフューザーで睡眠妨害されている場合、眠気をなくす
		if (Terrarium.isNoSleepSteam()) {
			// 正常な実ゆ以外なら
			if (!isUnBirth() || !isPlantForUnbirthChild()) {
				setSleepingPeriod(0);
				setSleeping(false);
				setNightmare(false);
				return false;
			}
		}

		// 飛行種で眠くなったら地面に降りる
		if (canflyCheck() && isSleepy()) {
			moveToZ(0);
			if (z != 0) {
				return false;
			}
		}

		if (isSleeping()) {
			// ストレスフルだと悪夢
			if (!isNightmare()
					&& ((isStressful() && SimYukkuri.RND.nextInt(75) == 0)
							|| (isVeryStressful() && SimYukkuri.RND.nextInt(25) == 0))) {
				setNightmare(true);
				setForceFace(ImageCode.NIGHTMARE.ordinal());
			} else if (!isStressful() || SimYukkuri.RND.nextInt(100) == 0) {
				setNightmare(false);
				setForceFace(ImageCode.SLEEPING.ordinal());
			}
		}

		// 飢餓状態の時は起きる
		if (isSleeping() && isStarving()) {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Hungry));
			setHappiness(Happiness.SAD);
			stay();
			wakeup();
		} else if (isSleeping()
				|| (wakeUpTime + getACTIVEPERIODorg() * 3 / 2 < getAge() && !isExciting() && !isScare() && !isVerySad()
						&& !isEating() && !isbNeedled() && !isTooHungry() && !(isVeryHungry() && isToFood()))
				|| (wakeUpTime + getACTIVEPERIODorg() * 3 < getAge() && !isExciting() && !isScare() && !isEating()
						&& !isbNeedled() && !(isTooHungry() && isToFood()))
				|| (isUnBirth() && !isbNeedled())) {
			clearActions();
			setSleeping(true);
			setAngry(false);
			setScare(false);
			damage -= TICK;
			if (!isUnBirth()) {
				setHappiness(Happiness.AVERAGE);
			} else {
				return isSleeping();
			}
			if (Terrarium.getDayState() == Terrarium.DayState.NIGHT) {
				if ((getAge() % (Terrarium.getNightTime() / getSLEEPPERIODorg() + 1)) == 0) {
					sleepingPeriod += TICK;
				}
			} else {
				sleepingPeriod += TICK;
			}
			if (sleepingPeriod > getSLEEPPERIODorg()) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Wakeup), true);
				stay();
				wakeup();
			}
		} else {
			// 実験 イベント中は空腹、睡眠、便意が増えないように
			if (getCurrentEvent() != null)
				return false;
			sleepingPeriod = 0;
			setSleeping(false);
			setNightmare(false);
			if (Terrarium.getDayState() == Terrarium.DayState.NIGHT) {
				wakeUpTime -= TICK * 3;
			}
		}

		return isSleeping();
	}

	/**
	 * ベッドの上にいるかどうかをチェックする.
	 * 
	 * @return ベッドの上にいるかどうか
	 */
	public boolean checkOnBed() {
		Rectangle r = takeScreenRect();
		for (Map.Entry<Integer, Bed> entry : SimYukkuri.world.getCurrentMap().getBed().entrySet()) {
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
		if (cantDiePeriod > 0) {
			cantDiePeriod -= TICK;
		}
	}

	/**
	 * 寝ているゆっくりを起こす.
	 * または寝ているゆっくりが起きる.
	 */
	public void wakeup() {
		setSleepingPeriod(0);
		setSleeping(false);
		setNightmare(false);
		setWakeUpTime(getAge());
	}

	/**
	 * プレイヤーにすりすりされたときの処理.
	 * 
	 * @return 感情処理を終えるかどうか
	 */
	public boolean doSurisuriByPlayer() {
		// プレイヤーにすりすりされていないなら終了
		if (!isbSurisuriFromPlayer()) {
			return false;
		}

		boolean bFlag = false;
		// 初回なら時間を初期化
		if (lnLastTimeSurisuri == 0) {
			lnLastTimeSurisuri = System.currentTimeMillis();
			bFlag = true;
		} else {
			// 二回目以降は前回より3秒以上経過してたら処理実行
			long lnTimeNow = System.currentTimeMillis();
			long lnSec = lnTimeNow - lnLastTimeSurisuri;
			if (2000 < lnSec) {
				lnLastTimeSurisuri = lnTimeNow;
				bFlag = true;
			}
		}

		if (!bFlag) {
			return false;
		}

		// 動けない場合,パニック中,すぃーに乗っている
		if ((isLockmove()) ||
				(getPanicType() != null) ||
				(isSleeping()) ||
				(takeMappedObj(getLinkParent()) instanceof Sui)) {
			return false;
		}

		// -----------------------------------------------------------
		// 処理を分けよう
		// 無反応：ホットプレート、ミキサー、足焼き、寝ている時、すぃーに乗っている、
		// すっきりー：興奮中
		// 痛み：針が刺さっている、足カット、痛みを感じている、瀕死
		// 拒絶：レイプされている、うんうん中、食事中、出産中、攻撃している、攻撃されている、
		// -----------------------------------------------------------
		if (isNYD()) {
			return false;
		}

		// すりすり実行
		// 興奮時
		if (isExciting()) {
			// すっきりー
			if (SimYukkuri.RND.nextInt(5) == 0) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Sukkiri), 60, true, false);
				setStress(0);
				stayPurupuru(60);
				setSukkiri(true);
				setExciting(false);
				setHappiness(Happiness.HAPPY);
				clearActions();
				// なつき度設定
				addLovePlayer(100);

				// おくるみはいてたら茎が生える
				if (isHasPants()) {
					dripSperm(getDna());
				}
			} else {
				stayPurupuru(30);
				// なつき度設定
				addLovePlayer(10);
				if (isRaper()) {
					setMessage(MessagePool.getMessage(this, MessagePool.Action.ExciteForRaper));
				} else {
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Excite));
				}
			}
			addMemories(1);
			return true;
		}

		// 切断されている場合
		if ((getCriticalDamege() == CriticalDamegeType.CUT) ||
				(getFootBakeLevel() == FootBake.CRITICAL) ||
				isDamaged() ||
				isPealed() ||
				isPacked()) {
			stayPurupuru(20);
			setHappiness(Happiness.VERY_SAD);
			addStress(100);
			// なつき度設定
			addLovePlayer(-20);
			setForceFace(ImageCode.PAIN.ordinal());
			clearActions();

			if (SimYukkuri.RND.nextInt(2) == 0) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying2), 30, true, false);
			} else {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying), 30, true, false);
			}
			return true;
		}

		// 針が刺さっている場合
		if (isbNeedled()) {
			stayPurupuru(40);
			setHappiness(Happiness.VERY_SAD);
			addStress(50);
			// なつき度設定
			addLovePlayer(-20);
			setForceFace(ImageCode.PAIN.ordinal());
			// ぐーりぐーりされた時のメッセージ
			if (SimYukkuri.RND.nextBoolean())
				setMessage(MessagePool.getMessage(this, MessagePool.Action.NeedlePain), 60, true, false);
			else
				setMessage(MessagePool.getMessage(this, MessagePool.Action.NeedlePain), 60, true, true);
			clearActions();
			return true;
		}

		// デフォルトすりすり
		addStress(-100);
		stay(40);
		// なつき度設定
		addLovePlayer(10);
		setHappiness(Happiness.VERY_HAPPY);
		setForceFace(ImageCode.CHEER.ordinal());
		setMessage(MessagePool.getMessage(this, MessagePool.Action.SuriSuriByPlayer), true);
		setNobinobi(true);
		addMemories(1);

		int nRnd = SimYukkuri.RND.nextInt(5);
		if (nRnd == 0) {
			setForceFace(ImageCode.SMILE.ordinal());
		} else if (0 < nRnd && nRnd < 3) {
			setForceFace(ImageCode.NORMAL.ordinal());
		} else {
			setForceFace(ImageCode.CHEER.ordinal());
		}

		clearActions();
		// 低確率で寝る
		if (SimYukkuri.RND.nextInt(20) == 0) {
			forceToSleep();
		}
		return true;
	}

	/**
	 * 自身の状態に対する反応を記述する.
	 */
	public void checkEmotion() {
		// 怒り状態の経過
		if (isAngry()) {
			angryPeriod += TICK;
			if (angryPeriod > getANGRYPERIODorg()) {
				angryPeriod = 0;
				setAngry(false);
			}
		}
		// 恐怖状態の経過
		if (isScare()) {
			scarePeriod += TICK;
			if (scarePeriod > getSCAREPERIODorg()) {
				scarePeriod = 0;
				setScare(false);
			}
		}
		// 落ち込み状態の経過
		if (getHappiness() == Happiness.VERY_SAD) {
			sadPeriod--;
			if (sadPeriod < 0) {
				sadPeriod = 0;
				setHappiness(Happiness.SAD);
			}
		}
		// お遊び状態の経過
		if (getPlaying() != null) {
			playingLimit--;
			boolean P = false;
			switch (getPlaying()) {
				case BALL:
					P = ToyLogic.checkToy(this);
					break;
				case SUI:
					P = ToyLogic.checkSui(this);
					break;
				case TRAMPOLINE:
					P = ToyLogic.checkTrampoline(this);
					break;
				default:
					P = false;
					break;
			}
			if (isSleeping() || playingLimit < 0 || !P) {
				stopPlaying();
			}
		}

		// 非ゆっくり症チェック
		if (checkNonYukkuriDisease()) {
			return;
		}
		// イベント中
		else if (getCurrentEvent() != null) {
			return;
		}
		// プレイヤーにすりすりされている
		else if (doSurisuriByPlayer()) {
			return;
		}

		// ゆんやー
		if (isYunnyaa() && !isSleeping()) {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Yunnyaa), 30, false, true);
			setYunnyaa(true);
			stay(40);
			setHappiness(Happiness.VERY_SAD);
			return;
		}
		// 加工中を想定した反応
		else if ((isDamaged() || hasDisorder()) && isbOnDontMoveBeltconveyor() && !hasBabyOrStalk() && !isPealed()) {
			if (SimYukkuri.RND.nextInt(80) == 0) {
				begForLife();
			} else if (SimYukkuri.RND.nextInt(40) == 0) {
				doYunnyaa(true);
			} else if (SimYukkuri.RND.nextInt(3) == 0) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.KilledInFactory), WindowType.NORMAL,
						Const.HOLDMESSAGE, true, SimYukkuri.RND.nextBoolean(), false);
			}
		}
		// 状態異常時
		// 足切断
		else if (getCriticalDamege() == CriticalDamegeType.CUT || isPealed() || isPacked()) {
			return;
		}
		// 盲目
		else if (checkEmotionBlind()) {
			return;
		}
		// しゃべれない
		else if (checkEmotionCantSpeak()) {
			return;
		}
		// 粘着系オブジェクトの貼り付け状態
		else if (checkEmotionLockmove()) {
			return;
		}
		// 足焼き済み
		else if (checkEmotionFootbake()) {
			return;
		}
		// 代替おかざりの捜索
		else if (TrashLogic.checkTrashOkazari(this)) {
			return;
		}
		// おかざり、ぴこぴこなし
		else if (checkEmotionNoOkazariPikopiko()) {
			return;
		}
		// 興奮時
		else if (isExciting()) {
			setRelax(false);
			return;
		}

		// 空腹時
		if (isHungry() && SimYukkuri.RND.nextInt(50) == 0) {
			if (isSoHungry())
				setHappiness(Happiness.SAD);
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Hungry), 30);
			stay();
		}

		// 通常時
		// うんうん奴隷の場合
		// 食事検索、トイレ検索時にもろもろのセリフを吐く
		if (getPublicRank() == PublicRank.UnunSlave || isMelt()) {
			setHappiness(Happiness.SAD);
			excitingPeriod = 0;
			// 強制発情ではない場合
			if ((!isVeryRude() || getIntelligence() != Intelligence.FOOL) && isExciting() && !isbForceExciting()) {
				setCalm();
				setForceFace(ImageCode.TIRED.ordinal());
				setMessage(MessagePool.getMessage(this, MessagePool.Action.CantUsePenipeni));
			}
			setRelax(false);
			setAngry(false);
			setScare(false);
			return;
		}

		// 汚れ時の反応
		if (isNormalDirty() && !isSleeping()) {
			// 大人と、善良子ゆは勝手にきれいにする
			if (isAdult() || (isChild() && isSmart())) {
				if (SimYukkuri.RND.nextInt(600) == 0)
					cleaningItself();
			} else {
				if (dirtyScreamPeriod == 0) {
					if (isRude())
						dirtyScreamPeriod = 10 + SimYukkuri.RND.nextInt(15);
					else
						dirtyScreamPeriod = 5 + SimYukkuri.RND.nextInt(10);
				} else
					callParent();
			}
		} else {
			setCallingParents(false);
			dirtyScreamPeriod = 0;
		}

		// ゆっくりしてるとき
		if (noHungryPeriod > getRELAXPERIODorg() && noDamagePeriod > getRELAXPERIODorg()
				&& !isSleeping() && !isShitting() && !isEating()
				&& !isSad() && !isVerySad() && !isFeelPain()
				&& getAttachmentSize(PoisonAmpoule.class) == 0) {
			// && moveTarget == null) {
			// すっきり発動条件
			if (!isExciting() && SimYukkuri.RND.nextInt(getExciteProb()) == 0) {
				int r = 1;
				int adjust = excitingDiscipline * (isRude() ? 1 : 2);
				if (isSuperRapist()) {
					r = SimYukkuri.RND.nextInt(1 + adjust);
				} else if (isRapist() && isRude()) {
					r = SimYukkuri.RND.nextInt(6 + adjust);
				} else if (isRapist() || isRude()) {
					r = SimYukkuri.RND.nextInt(12 + adjust);
				} else if (!isSoHungry() && !wantToShit()) {
					r = SimYukkuri.RND.nextInt(24 + adjust);
				}
				// すっきりーしにいく条件判定
				boolean bToExcite = false;
				// ぺにぺにがないとダメ
				if (isbPenipeniCutted()) {
					r = 1;
				}
				// 大人じゃないとやらない(ドゲスの子ゆ除く)
				if (!isAdult() && !(isChild() && isVeryRude())) {
					r = 1;
				}
				// 妊娠してるとしない
				if (hasBabyOrStalk() && !isRaper()) {
					r = 1;
				}
				// if (isRaper() && (isExciting() || isForceExciting())) {
				// setCurrentEvent(null);
				// }
				if (r == 0 && getCurrentEvent() == null) {
					List<Body> fianceList = BodyLogic.createActiveFianceeList(this, getBodyAgeState().ordinal());
					if (fianceList == null || fianceList.size() < 1) {
						setHappiness(Happiness.SAD);
						setMessage(MessagePool.getMessage(this, MessagePool.Action.WantPartner));
					}
					// 他にゆっくりがいる
					else {
						// レイパー
						if (isRapist()) {
							if (isRapist() && FamilyActionLogic.isRapeTarget()) {
								bToExcite = true;
							}
						} else {
							// 自分の通常の子ゆリスト作成
							List<Body> childrenList = BodyLogic.createActiveChildList(this, true);
							// パートナーがいる場合
							Body pa = YukkuriUtil.getBodyInstance(getPartner());
							if (pa != null) {
								if (isVeryRude()) {
									// ドゲスはすぐ興奮
									bToExcite = true;
								} else if (!pa.hasBabyOrStalk()) {
									if (childrenList == null || childrenList.size() == 0) {
										bToExcite = true;
									} else {
										switch (getIntelligence()) {
											case WISE:
												// 賢いのは3匹以下で子づくり
												if (childrenList.size() <= 3) {
													bToExcite = true;
												}
												break;
											case AVERAGE:
												// 普通の知能は10匹以下で子づくり
												if (childrenList.size() <= 10) {
													bToExcite = true;
												}
												break;
											case FOOL:
												// 餡子脳は子の数を気にしない
												bToExcite = true;
												break;
										}
									}
								}
							} else {
								// 独身orバツイチは、相手を探すために興奮する
								bToExcite = true;
							}
						}
					}
				}

				if (bToExcite) {
					clearActionsForEvent();
					setExciting(true);
					excitingPeriod = 0;
					if (isRaper()) {
						EventLogic.addWorldEvent(new RaperWakeupEvent(this, null, null, 1), this,
								MessagePool.getMessage(this, MessagePool.Action.ExciteForRaper));
					} else {
						setMessage(MessagePool.getMessage(this, MessagePool.Action.Excite));
					}
				} else {
					setRelax(true);
					excitingPeriod = 0;
					if (SimYukkuri.RND.nextInt(75) == 0)
						killTime();
				}
				setAngry(false);
				setScare(false);
			} else {
				excitingPeriod += TICK + getExcitingPeriodBoost();
				if (excitingPeriod > getEXCITEPERIODorg()) {
					excitingPeriod = 0;
					if (!isRaper()) {
						setCalm();
					}
					setRelax(false);
				}
				// 興奮している場合、たまにつぶやく
				if (isExciting()) {
					if (SimYukkuri.RND.nextInt(30) == 0) {
						if (isRaper()) {
							setMessage(MessagePool.getMessage(this, MessagePool.Action.ExciteForRaper));
						} else {
							setMessage(MessagePool.getMessage(this, MessagePool.Action.Excite));
						}
					}
				}
			}
		}
	}

	/**
	 * ゆっくりしてる時のアクション.
	 * 個別の動作がある種ははこれをオーバーライドしているので注意.
	 */
	public void killTime() {
		if (getCurrentEvent() != null)
			return;
		if (getPlaying() != null)
			return;
		int p = SimYukkuri.RND.nextInt(50);
		// 6/50でキリッ
		if (p <= 5) {
			getInVain(true);
		}
		// 6/50でのびのび
		else if (p <= 11) {
			// if yukkuri is not rude, she goes into her shell by discipline.
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Nobinobi), 40);
			setNobinobi(true);
			addStress(-50);
			stay(40);
		}
		// 6/50でふりふり
		else if (p <= 17 && willingFurifuri()) {
			// if yukkuri is rude, she will not do furifuri by discipline.
			setMessage(MessagePool.getMessage(this, MessagePool.Action.FuriFuri), 40);
			setFurifuri(true);
			addStress(-70);
			stay(30);
		}
		// 6/50で腹減った
		else if ((p <= 23 && isHungry()) || isSoHungry()) {
			// 空腹時
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Hungry), 30);
			stay(30);
		}
		// 6/50でおもちゃで遊ぶ
		else if (p <= 29) {
			if (ToyLogic.checkToy(this)) {
				setPlaying(PlayStyle.BALL);
				playingLimit = 150 + SimYukkuri.RND.nextInt(100) - 49;
				return;
			} else
				killTime();
		}
		// 6/50でトランポリンで遊ぶ
		else if (p <= 35) {
			if (ToyLogic.checkTrampoline(this)) {
				setPlaying(PlayStyle.TRAMPOLINE);
				playingLimit = 150 + SimYukkuri.RND.nextInt(100) - 49;
				return;
			} else
				killTime();
		}
		// 6/50ですいーで遊ぶ
		else if (p <= 41) {
			if (ToyLogic.checkSui(this)) {
				setPlaying(PlayStyle.SUI);
				playingLimit = 150 + SimYukkuri.RND.nextInt(100) - 49;
				return;
			} else
				killTime();
		} else {
			// おくるみありで汚れていない場合
			if ((isHasPants()) && !isDirty() && (SimYukkuri.RND.nextInt(10) == 0)) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.RelaxOkurumi));
			} else {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Relax));
			}
			addStress(-60);
			stay(30);
		}
	}

	/**
	 * 遊ぶのをやめる.
	 */
	public void stopPlaying() {
		setPlaying(null);
		playingLimit = 0;
	}

	/**
	 * キリッとする
	 * 
	 * @param TF キリッ！メッセージを出すかどうか
	 */
	public void getInVain(boolean TF) {
		if (TF)
			setMessage(MessagePool.getMessage(this, MessagePool.Action.BeVain), 30);
		if (isRude() && SimYukkuri.RND.nextBoolean())
			setForceFace(ImageCode.RUDE.ordinal());
		setBeVain(true);
		addStress(-90);
		stayPurupuru(10);
	}

	/**
	 * 非ゆっくり症チェックを行う。
	 * ・基礎
	 * 足りないゆはほぼ非ゆっくり症にならない
	 * うんうん奴隷は常に甘いもの（うんうん）を食べているのでほぼ非ゆっくり症にならない
	 * 善良＜ゲスで耐性高い
	 * 赤ゆ、実ゆ＜子供＜大人で耐性高い
	 * バッヂ級＜餡子脳で耐性高い
	 * レイパーは非ゆっくり症にならない
	 * ・環境
	 * 完全空腹の場合に耐性Down
	 * 足焼きの度合いに応じて耐性Down
	 * カビが生えると耐性Down
	 * お飾りがないと耐性Down
	 * ぴこぴこがないと耐性Down
	 * ぺにぺにがないと耐性超Down
	 * 汚れていると耐性Down
	 * 固定されていると耐性Down
	 * 盲目だと耐性Down
	 * 口がふさがれてると耐性Down
	 * ケガしてると耐性Down
	 * 生きている子供の数だけ耐性Up
	 * 死んでいる子供の数だけ耐性Down
	 * ・つらい思い出
	 * 他ゆの死体を見ると耐性Down
	 * 他ゆ食いで吐餡してたら耐性Down
	 * 生ごみ、辛い餌、苦い餌を食べると耐性Down
	 * 出産失敗で耐性超Down
	 * ・いい思い出
	 * すっきりすると耐性Up
	 * 出産時に応援する、応援されると耐性Up
	 * 出産成功で耐性Up
	 * 茎を食べると耐性超Up
	 * あまあまを食べると耐性超Up
	 * すりすりされると耐性Up
	 * ぺろぺろされると耐性Up
	 * うんうん体操に参加すると耐性Up
	 * すぃーにのると耐性Up
	 */
	@Override
	public int checkNonYukkuriDiseaseTolerance() {
		int nTolerance = 100;
		if (isIdiot()) {
			nTolerance += 50000;
		}
		if (getPublicRank() == PublicRank.UnunSlave) {
			nTolerance += 10000;
		}
		switch (getIntelligence()) {
			case WISE:
				nTolerance += 5;
				break;
			case FOOL:
				nTolerance += 10;
				break;
			default:
				break;
		}
		switch (getAttitude()) {
			case VERY_NICE:
				nTolerance += 5;
				break;
			case NICE:
				nTolerance += 10;
				break;
			case SHITHEAD:
				nTolerance += 30;
				break;
			case SUPER_SHITHEAD:
				nTolerance += 50;
				break;
			default:
				break;
		}
		switch (getBodyAgeState()) {
			case BABY:
				break;
			case CHILD:
				nTolerance += 30;
				break;
			case ADULT:
				nTolerance += 50;
				break;
		}
		if (isRapist()) {
			nTolerance += 5000;
		}
		if (isSoHungry()) {
			if (isVeryHungry())
				nTolerance -= 5;
			else
				nTolerance -= 3;
		}
		switch (getFootBakeLevel()) {
			case MIDIUM:
				nTolerance -= 30;
				break;
			case CRITICAL:
				nTolerance -= 50;
				break;
			default:
				break;
		}
		switch (getBodyBakeLevel()) {
			case MIDIUM:
				nTolerance -= 15;
				break;
			case CRITICAL:
				nTolerance -= 25;
				break;
			default:
				break;
		}
		if (isSick()) {
			nTolerance -= 15;
		}
		if (!hasOkazari()) {
			nTolerance -= 25;
		}
		if (!isHasBraid()) {
			nTolerance -= 10;
		}
		if (isbPenipeniCutted()) {
			nTolerance -= 20;
		}
		if (isDirty()) {
			nTolerance -= 5;
		}
		if (isLockmove()) {
			nTolerance -= 5;
		}
		if (isBlind()) {
			nTolerance -= 20;
		}
		if (isShutmouth()) {
			nTolerance -= 10;
		}
		if (getCriticalDamege() == CriticalDamegeType.INJURED) {
			nTolerance -= 10;
		}
		if (getChildrenList() != null) {
			for (int iChild : getChildrenList()) {
				Body bChild = YukkuriUtil.getBodyInstance(iChild);
				if (bChild == null || bChild.isAdult()) {
					continue;
				}
				// 死んでる
				if (bChild.isRemoved() || bChild.isDead()) {
					nTolerance -= 10;
					continue;
				}
				// 死にかけてる
				if (bChild.isCrushed() || bChild.isDamaged() || bChild.isBurned() || findSick(bChild)
						|| bChild.isTooHungry()) {
					nTolerance -= 3;
					continue;
				}
				if (hasDisorder()) {
					nTolerance -= 5;
					continue;
				}
				// 大丈夫っぽい
				nTolerance += 10;
			}
		}
		nTolerance += memories;
		if (nTolerance <= -1)
			nTolerance = -1;
		return nTolerance;
	}

	/**
	 * 非ゆっくり症チェック
	 * 
	 * @return その後の処理をキャンセルするかどうか
	 */
	private boolean checkNonYukkuriDisease() {
		// 非ゆっくり症防止ディフューザー、非ゆっくり症防止アンプルの際は非ゆっくり症にならない/治る
		if (Terrarium.isAntiNonYukkuriDiseaseSteam() || getAttachmentSize(ANYDAmpoule.class) != 0) {
			seteCoreAnkoState(CoreAnkoState.DEFAULT);
			return false;
		}

		int nStressLimit = getSTRESSLIMITorg()[getBodyAgeState().ordinal()];
		int nTolerance = checkNonYukkuriDiseaseTolerance();
		// ストレス限界を超えている場合
		if (nStressLimit * nTolerance / 100 < getStress()) {
			// 初回
			if (isNotNYD()) {
				seteCoreAnkoState(CoreAnkoState.NonYukkuriDiseaseNear);
				nonYukkuriDiseasePeriod = 0;
				speed = speed / 2;
			}
			// ストレス限界の2倍を超えている場合
			if (nStressLimit * nTolerance / 100 * 2 < getStress()) {
				// 初回
				if (geteCoreAnkoState() == CoreAnkoState.NonYukkuriDiseaseNear) {
					seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
					nonYukkuriDiseasePeriod = 0;
				}
			}
		} else {
			// 復帰時
			if (isNYD()) {
				speed = speed * 2;
			}
			seteCoreAnkoState(CoreAnkoState.DEFAULT);
		}

		// 通常のままなら終了
		if (isNotNYD()) {
			nonYukkuriDiseasePeriod = 0;
			return false;
		}

		// 生まれていないなら反応は示さないけど判定チェックにひかかる
		if (isUnBirth()) {
			return true;
		}
		int nRnd = 40;
		wakeup();
		setBirth(false);

		// 起こす
		if (isNYD() && isSleeping()) {
			wakeup();
		}
		// 非ゆっくり症初期
		if (geteCoreAnkoState() == CoreAnkoState.NonYukkuriDiseaseNear && SimYukkuri.RND.nextInt(nRnd) == 0) {
			switch (nonYukkuriDiseasePeriod) {
				case 0:
					if (SimYukkuri.RND.nextBoolean()) {
						nonYukkuriDiseasePeriod = 1;
					} else {
						nonYukkuriDiseasePeriod = 3;
					}
					if (!isFixBack()) {
						clearActions();
						if (nonYukkuriDiseasePeriod == 1) {
							setNYDForceFace(ImageCode.NYD_FRONT.ordinal());
						} else {
							setNYDForceFace(ImageCode.NYD_DOWN.ordinal());
							nonYukkuriDiseasePeriod = 3;
						}
					}
					break;
				case 1:
					nonYukkuriDiseasePeriod = 2;
					if (!isFixBack()) {
						clearActions();
						setNYDForceFace(ImageCode.NYD_FRONT_CRY1.ordinal());
					}
					break;
				case 2:
					nonYukkuriDiseasePeriod = 0;
					if (!isFixBack()) {
						clearActions();
						setNYDForceFace(ImageCode.NYD_FRONT_CRY2.ordinal());
					}
					stayPurupuru(20);
					break;
				case 3:
					nonYukkuriDiseasePeriod = 4;
					if (!isFixBack()) {
						clearActions();
						setNYDForceFace(ImageCode.NYD_DOWN_CRY1.ordinal());
					}
					break;
				case 4:
					nonYukkuriDiseasePeriod = 0;
					if (!isFixBack()) {
						clearActions();
						setNYDForceFace(ImageCode.NYD_DOWN_CRY2.ordinal());
					}
					stayPurupuru(20);
					break;
				default:
					break;
			}
			addStress(100);
			addMemories(-1);// 耐性が減っていく
			setHappiness(Happiness.VERY_SAD);
			setNYDMessage(MessagePool.getMessage(this, MessagePool.Action.NonYukkuriDiseaseNear), false);
		}
		nRnd = 20;
		// 非ゆっくり症
		if (geteCoreAnkoState() == CoreAnkoState.NonYukkuriDisease && SimYukkuri.RND.nextInt(nRnd) == 0) {
			switch (nonYukkuriDiseasePeriod) {
				case 0:
					if (SimYukkuri.RND.nextBoolean()) {
						nonYukkuriDiseasePeriod = 1;
						if (!isFixBack()) {
							clearActions();
							setNYDForceFace(ImageCode.NYD_UP.ordinal());
						}
					} else {
						nonYukkuriDiseasePeriod = 4;
						if (!isFixBack()) {
							clearActions();
							setNYDForceFace(ImageCode.NYD_FRONT_WIDE.ordinal());
						}
					}
					break;
				case 1:
					if (SimYukkuri.RND.nextBoolean()) {
						nonYukkuriDiseasePeriod = 2;
					}
					if (!isFixBack()) {
						clearActions();
						setNYDForceFace(ImageCode.NYD_UP.ordinal());
					}
					break;
				case 2:
					if (SimYukkuri.RND.nextBoolean()) {
						nonYukkuriDiseasePeriod = 3;
					}
					if (!isFixBack()) {
						clearActions();
						setNYDForceFace(ImageCode.NYD_UP_CRY1.ordinal());
					}
					break;
				case 3:
					nonYukkuriDiseasePeriod = 1;
					if (!isFixBack()) {
						clearActions();
						setNYDForceFace(ImageCode.NYD_UP_CRY2.ordinal());
					}
					stayPurupuru(20);
					break;
				case 4:
					if (SimYukkuri.RND.nextBoolean()) {
						nonYukkuriDiseasePeriod = 5;
					}
					if (!isFixBack()) {
						clearActions();
						setNYDForceFace(ImageCode.NYD_FRONT_WIDE.ordinal());
					}
					break;
				case 5:
					if (SimYukkuri.RND.nextBoolean()) {
						nonYukkuriDiseasePeriod = 6;
					}
					if (!isFixBack()) {
						clearActions();
						setNYDForceFace(ImageCode.NYD_FRONT_WIDE_CRY1.ordinal());
					}
					break;
				case 6:
					nonYukkuriDiseasePeriod = 4;
					if (!isFixBack()) {
						clearActions();
						setNYDForceFace(ImageCode.NYD_FRONT_WIDE_CRY2.ordinal());
					}
					stayPurupuru(20);
					break;
				default:
					break;
			}
			addStress(300);
			addMemories(-5);// 耐性が減っていく
			setHappiness(Happiness.VERY_SAD);
			setNYDMessage(MessagePool.getMessage(this, MessagePool.Action.NonYukkuriDisease), false);
			if (SimYukkuri.RND.nextInt(nRnd) == 0)
				nonYukkuriDiseasePeriod = 0;
		}
		return true;
	}

	/**
	 * 盲目時の基本反応.
	 * 
	 * @return その後の処理をキャンセルするかどうか
	 */
	public boolean checkEmotionBlind() {
		if (isBlind()) {
			EYESIGHTorg = 5 * 5;
			addStress(5);
			setHappiness(Happiness.SAD);
			if (SimYukkuri.RND.nextInt(40) <= 5) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.CANTSEE));
			} else if (SimYukkuri.RND.nextInt(40) == 20) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.LamentNoYukkuri));
			}
			return true;
		}
		return false;
	}

	/**
	 * 口封じ時の基本反応
	 * 
	 * @return その後の処理をキャンセルするかどうか
	 */
	public boolean checkEmotionCantSpeak() {
		//
		if (isShutmouth()) {
			addStress(2);
			setHappiness(Happiness.SAD);
			setPeropero(false);
			if (SimYukkuri.RND.nextInt(80) == 0 && !isSleeping()) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.CantTalk));
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
		// 動けるとき、すっきりしてる時、埋まってない時はリターン
		if (!isLockmove() || isSukkiri() || (getFootBakeLevel() != FootBake.NONE
				&& (getBaryState() == BaryInUGState.NONE || getBaryState() == BaryInUGState.HALF))) {
			return false;
		}

		// 寝てる時と掴まれてる時も
		if (isSleeping() || grabbed) {
			setLockmovePeriod(0);
			return false;
		}
		// イベント中も
		if (getCurrentEvent() != null) {
			setLockmovePeriod(0);
			return false;
		}

		lockmovePeriod++;
		if (isTalking()) {
			return false;
		}
		// 以下、しゃべってない時

		if (lockmovePeriod < 400) {
			if (SimYukkuri.RND.nextInt(15) == 0) {
				clearActions();
				// 土に埋まっている場合は苦しむ
				if (getBaryState() == BaryInUGState.ALL || getBaryState() == BaryInUGState.NEARLY_ALL) {
					setHappiness(Happiness.VERY_SAD);
					setMessage(MessagePool.getMessage(this, MessagePool.Action.BaryInUnderGround));
					stay();
				} else {
					setMessage(MessagePool.getMessage(this, MessagePool.Action.CantMove));
					setAngry();
					if (SimYukkuri.RND.nextInt(10) == 0) {
						setNobinobi(true);
					}
				}
				return true;
			}
			if (isHungry() && SimYukkuri.RND.nextInt(50) == 0) {
				// 空腹時
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Hungry), 30);
				setHappiness(Happiness.SAD);
				stay(30);
			} else if (SimYukkuri.RND.nextInt(15) == 0) {
				clearActions();
				// 土に埋まっている場合は苦しむ
				if (getBaryState() == BaryInUGState.ALL || getBaryState() == BaryInUGState.NEARLY_ALL) {
					setHappiness(Happiness.VERY_SAD);
					setMessage(MessagePool.getMessage(this, MessagePool.Action.BaryInUnderGround));
					stay();
				} else {
					setAngry();
					setHappiness(Happiness.VERY_SAD);
					setMessage(MessagePool.getMessage(this, MessagePool.Action.CantMove2));
					if (SimYukkuri.RND.nextInt(10) == 0) {
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
		// すっきり中と、足焼き無しは除外
		if (getFootBakeLevel() == FootBake.NONE || isSukkiri()) {
			return false;
		}
		// 寝ているとき、掴まれているときも除外
		if (isSleeping() || grabbed) {
			lockmovePeriod = 0;
			return false;
		}

		lockmovePeriod++;
		if (isTalking()) {
			return false;
		}

		// 足焼き（中）
		if (getFootBakeLevel() == FootBake.MIDIUM) {
			if (SimYukkuri.RND.nextInt(15) == 0) {
				clearActions();
				setAngry();
				setHappiness(Happiness.SAD);
				setMessage(MessagePool.getMessage(this, MessagePool.Action.LamentLowYukkuri));
				return true;
			}
			if (isHungry() && SimYukkuri.RND.nextInt(400) == 0) {
				// 空腹時
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Hungry), 30);
				setHappiness(Happiness.SAD);
				return true;
			}
		}
		// 足焼き(完全)
		else if (getFootBakeLevel() == FootBake.CRITICAL) {
			if (lockmovePeriod < 300) {
				if (SimYukkuri.RND.nextInt(15) == 0) {
					clearActions();
					setAngry();
					setHappiness(Happiness.SAD);
					if (SimYukkuri.RND.nextInt(5) == 0) {
						setMessage(MessagePool.getMessage(this, MessagePool.Action.LamentLowYukkuri));
					} else {
						setMessage(MessagePool.getMessage(this, MessagePool.Action.CantMove));
					}
					return true;
				}
				if (isHungry() && SimYukkuri.RND.nextInt(50) == 0) {
					// 空腹時
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Hungry), 30);
					setHappiness(Happiness.VERY_SAD);
					stay();
					return true;
				}
			} else {
				if (SimYukkuri.RND.nextInt(15) == 0) {
					clearActions();
					setAngry();
					setHappiness(Happiness.VERY_SAD);
					if (SimYukkuri.RND.nextInt(5) != 0) {
						setMessage(MessagePool.getMessage(this, MessagePool.Action.CantMove2));
					} else {
						setMessage(MessagePool.getMessage(this, MessagePool.Action.LamentNoYukkuri));
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
		// おかざりとぴこぴこあり、またはすっきり中は除外
		if ((hasOkazari() && isHasBraid()) || isSukkiri()) {
			return false;
		}
		// 寝ているとき、または掴まれているときも除外
		if (isSleeping() || grabbed) {
			lockmovePeriod = 0;
			return false;
		}
		// 喋っているときも除外
		if (isTalking()) {
			return false;
		}
		if (SimYukkuri.RND.nextInt(50) == 0) {
			clearActions();
			setAngry();
			setHappiness(Happiness.SAD);
			setForceFace(ImageCode.TIRED.ordinal());
			setMessage(MessagePool.getMessage(this, MessagePool.Action.LamentLowYukkuri));
			stay();
			return true;
		}
		return true;
	}

	/**
	 * ゆかびに感染している際の基本反応
	 */
	public void checkSick() {
		// （汚くてダメージを受けている、またはディフューザーで湿度が高まっていてダメージを受けている）、かつディフューザーでゆかび禁止になっていないとき
		if (((isDirty() && isDamaged()) || (Terrarium.isHumid() && damage > 0)) && !Terrarium.isAntifungalSteam()) {
			if (Terrarium.isHumid()) {
				dirtyPeriod += TICK * 4;
			} else {
				dirtyPeriod += TICK;
			}
			if (isWet() || isMelt()) {
				dirtyPeriod += TICK;
			}
			if (isStubbornlyDirty()) {
				dirtyPeriod += TICK;
			}
			if (dirtyPeriod > getDIRTYPERIODorg()) {
				addSickPeriod(100);
				dirtyPeriod = 0;
			}
		} else {
			dirtyPeriod = 0;
		}
		if (isSick()) {
			if (Terrarium.isHumid()) {
				sickPeriod += 4;
			} else {
				sickPeriod++;
			}
			if (sickPeriod > INCUBATIONPERIODorg * 32
					&& (damage >= getDAMAGELIMITorg()[getBodyAgeState().ordinal()] * 85 / 100) && !isTalking()) {
				if (isSleeping())
					wakeup();
				// 末期症状
				setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.MoldySeriousry), 40, true);
				addStress(TICK * 100);
				addMemories(-5);
				if (SimYukkuri.RND.nextInt(60) == 0) {
					setForceFace(ImageCode.PAIN.ordinal());
				}
				if (SimYukkuri.RND.nextInt(10) == 0) {
					if (SimYukkuri.RND.nextBoolean())
						doYunnyaa(false);
					else
						setNobinobi(true);
				}
				setHappiness(Happiness.VERY_SAD);
				if (getCurrentEvent() != null && getCurrentEvent().getPriority() != EventPacket.EventPriority.HIGH) {
					clearEvent();
				}
				return;
			}
			if (isSickHeavily() && SimYukkuri.RND.nextInt(600) == 0) {
				if (isSickTooHeavily())
					setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.Scream2), true);
				else
					setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.Scream), true);
				setForceFace(ImageCode.PAIN.ordinal());
			}
			if (isSick() && SimYukkuri.RND.nextInt(50) == 0) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Moldy));
			}
			setHappiness(Happiness.SAD);
			addStress(TICK);
			if (isSickTooHeavily() && getCurrentEvent() != null
					&& getCurrentEvent().getPriority() != EventPacket.EventPriority.HIGH) {
				clearEvent();
			}
		}
	}

	/**
	 * SickePeriodを進ませる
	 * 
	 * @param i 進ませる期間
	 */
	public void addSickPeriod(int i) {
		sickPeriod += i;
	}

	/**
	 * ただのパニック汎用
	 * 
	 * @return 何もしないイベント
	 */
	public Event checkFear() {
		if (isNYD() || isUnBirth()) {
			setPanic(false, null);
			return Event.DONOTHING;
		}
		if (!isDead()) {
			messageCount--;
			if (messageCount <= 0) {
				switch (getPanicType()) {
					case FEAR:
						setMessage(MessagePool.getMessage(this, MessagePool.Action.Fear));
						break;
					case REMIRYA:
						setMessage(MessagePool.getMessage(this, MessagePool.Action.EscapeFromRemirya));
						break;
					default:
						break;
				}
			}
		}
		if (getPanicType() != null && getPanicType() != PanicType.BURN) {
			panicPeriod += TICK;
		}
		if (panicPeriod > 50) {
			setPanic(false, null);
		}
		return Event.DONOTHING;
	}

	/**
	 * 濡れているときの基本反応.
	 */
	public void checkWet() {
		// 濡れても溶けてもないなら抜ける
		if (!isWet() && !isMelt())
			return;

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
	 * ストレスチェック.
	 * ストレスがマイナスなら0にする.
	 */
	public final void checkStress() {
		if (stress < 0)
			stress = 0;
	}

	/**
	 * バカ舌チェック.
	 */
	public final void checkTang() {
		if (getTang() < 0)
			setTang(0);
		if (getTang() > getTANGLEVELorg()[2])
			setTang(getTANGLEVELorg()[2]);
	}

	/**
	 * メッセージを出すかどうか.
	 */
	public void checkMessage() {
		--messageCount;
		if (messageCount <= 5) {
			// stop to show the message 0.5 sec. before.
			setMessageBuf(null);
		}
		if (messageCount <= 0) {
			messageCount = 0;
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
			return;
		}

		if (isDead()) {
			if (!isSilent()) {
				String messages = MessagePool.getMessage(this, MessagePool.Action.Dead);
				setMessage(messages);
				setForceBirthMessage(false);
				if (getMessageBuf() == messages) {
					// if the message is set successfully, be silent.
					setSilent(true);
				}
			}
			return;
		} else if (getMessageBuf() == null) {
			if (isSleeping()) {
				if (SimYukkuri.RND.nextInt(10) == 0) {
					if (isNightmare()) {
						setNYDMessage(MessagePool.getMessage(this, MessagePool.Action.Nightmare), false);
						addStress(20);
					} else {
						setNYDMessage(MessagePool.getMessage(this, MessagePool.Action.Sleep), false);
						addStress(-20);
					}
				}
			} else if (!isUnBirth() && isForceBirthMessage()) {
				setForceBirthMessage(false);
				setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.Birth), true);
				addMemories(10);
			} else if (!isFlyingType() && getZ() > 15 && getPanicType() == null && !isLockmove()
					&& getCriticalDamege() != CriticalDamegeType.CUT && !isPealed() && !isBlind()) {
				// 持ち上げたとき
				// 妊娠限界を超えている場合
				if (isStressful() && isOverPregnantLimit() && SimYukkuri.RND.nextBoolean()) {
					setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.DontThrowMeAway), true);
					setForceFace(ImageCode.CRYING.ordinal());
					addStress(100);
					// なつき度設定
					addLovePlayer(-10);
				}
				// おそらとんでるみたい！
				else {
					setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.Flying), true);
					addStress(-10);
					// なつき度設定
					addLovePlayer(10);
				}
			} else if (isStressful() && isOverPregnantLimit() && SimYukkuri.RND.nextBoolean() && grabbed) {
				setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.DontThrowMeAway), true);
				setForceFace(ImageCode.CRYING.ordinal());
				addStress(100);
				// なつき度設定
				addLovePlayer(-10);
			} else if (getBurstState() == Burst.NEAR) {
				if (isSleeping())
					wakeup();
				if (SimYukkuri.RND.nextInt(8) == 0) {
					setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.Inflation),
							SimYukkuri.RND.nextBoolean());
				}
			} else if (nearToBirth() && !isBirth()) {
				// if( baryState == Body.BaryInUGState.NONE ){
				if (!isTalking() && getBaryState() == BaryInUGState.NONE && SimYukkuri.RND.nextInt(8) == 0) {
					setMessage(MessagePool.getMessage(this, MessagePool.Action.NearToBirth));
					EventLogic.addWorldEvent(new BreedEvent(this, null, null, 2), null, null);
				}
				// }
			}
		}
	}

	/**
	 * 動かなくする.
	 */
	public final void stay() {
		setStaying(true);
		stayTime = Const.STAYLIMIT;
	}

	/**
	 * time分だけ動かなくする.
	 * 
	 * @param time 動かなくする時間
	 */
	public final void stay(int time) {
		setStaying(true);
		stayTime = time;
	}

	/**
	 * timeだけぷるぷるする.
	 * 
	 * @param time ぷるぷるする時間
	 */
	public final void stayPurupuru(int time) {
		setStaying(true);
		stayTime = time;
		setPurupuru(true);
	}

	/**
	 * ぷるぷるする.
	 */
	public final void doPurupuru() {
		if (!isbPurupuru()) {
			setbPurupuru(true);
			setOfsXY(1, ofsY);
		} else {
			setbPurupuru(false);
			setOfsXY(0, ofsY);
		}
	}

	/**
	 * ランダムに方向を決定する.
	 * 
	 * @param curDir 現在の方向の数字
	 * @return 方向の数字
	 */
	public final int randomDirection(int curDir) {
		switch (curDir) {
			case 0:
				curDir = (SimYukkuri.RND.nextBoolean() ? 1 : -1);
				break;
			case 1:
				curDir = (SimYukkuri.RND.nextBoolean() ? 0 : curDir);
				break;
			case -1:
				curDir = (SimYukkuri.RND.nextBoolean() ? 0 : curDir);
				break;
		}
		return curDir;
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
		if (destPos - curPos > range) {
			return 1;
		} else if (curPos - destPos > range) {
			return -1;
		}
		return 0;
	}

	/**
	 * 茎の位置等を更新する.
	 */
	public void upDate() {
		// Move Stalk
		if (getStalks() != null && getStalks().size() > 0) {
			int direction = getDirection().ordinal();
			int centerH = (getBodySpr()[getBodyAgeState().ordinal()].getImageH() + getExpandSizeW()
					+ getExternalForceW());
			// うにょ機能
			if (SimYukkuri.UNYO) {
				centerH = (getBodySpr()[getBodyAgeState().ordinal()].getImageH() + getExpandSizeH()
						+ getExternalForceW()
						+ unyoForceH);
			}
			int sX;
			int ofsX;
			int k = 0;
			for (Stalk stalk : getStalks()) {
				if (stalk != null) {
					sX = stalk.getPivotX() + Const.STALK_OF_S_X[k] - (int) ((3 - getBodyAgeState().ordinal()) * 8.75f);
					if (direction == Const.RIGHT) {
						stalk.setDirection(0);
					} else {
						stalk.setDirection(1);
						sX = -sX;
					}
					ofsX = Translate.invertX(sX, getY());
					ofsX = Translate.transSize(ofsX);
					stalk.setMostDepth(getMostDepth());
					stalk.setCalcX(getX() + ofsX);
					stalk.setCalcY(getY());
					// 完全に埋まっていたら茎だけ地上に出す
					if (getBaryState() == BaryInUGState.ALL) {
						stalk.setCalcZ(0);
					} else {
						stalk.setCalcZ(getZ() + (int) (centerH * 0.09f) + Const.STALK_OF_S_Y[k]);
					}
					stalk.upDate();
				}
				k = (k + 1) & 7;
			}
		}
	}

	/**
	 * ゆっくりを動かす.
	 * 
	 * @param dontMove 動けない場合
	 */
	public void moveBody(boolean dontMove) {
		if (grabbed || takeMappedObj(getLinkParent()) != null) {
			// if grabbed, it cannot move.
			setFalldownDamage(0);
			setbNoDamageNextFall(false);
			setBx(0);
			setBy(0);
			bz = 0;
			return;
		}
		int mx = vx + getBx();
		int my = vy + getBy();
		int mz = vz + bz;

		if (mx != 0) {
			x += mx;
			if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 3, Barrier.MAP_BODY[getBodyAgeState().ordinal()])) {
				x -= mx;
				vx = 0;
			} else if (x < 0) {
				falldownDamage += Math.abs(vx);
				x = 0;
				vx = 0;
			} else if (x > Translate.getMapW()) {
				falldownDamage += Math.abs(vx);
				x = Translate.getMapW();
				vx = 0;
			}
		}

		if (my != 0) {
			y += my;
			if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 3, Barrier.MAP_BODY[getBodyAgeState().ordinal()])) {
				y -= my;
				vy = 0;
			} else if (y < 0) {
				falldownDamage += Math.abs(vy);
				y = 0;
				vy = 0;
				dirY = 1;
			} else if (y > Translate.getMapH()) {
				falldownDamage += Math.abs(vy);
				y = Translate.getMapH();
				vy = 0;
				dirY = -1;
			}
		}

		// 空中なら落ちない
		if (0 < z) {
			bFallingUnderGround = false;
		}

		// 飛行できるゆっくりはvzによる外力以外では高度を保つ
		if ((mz != 0 || (!canflyCheck() && getMostDepth() != z && getBindStalk() == null)) && !bFallingUnderGround) {
			falldownDamage = (vz > 0 ? falldownDamage : 0);
			// if falling down, it cannot move to x-y axis
			mz += 1;
			vz += 1;
			z -= mz;
			falldownDamage += (vz > 0 ? vz : 0);
			if (z <= nMostDepth) {
				if (SimYukkuri.UNYO) {
					changeUnyo(0, 0, (int) (falldownDamage * 0.4 + 1));
				}
				falldownDamage += Math.abs(vy);
				z = nMostDepth;
				vz = 0;
				vy = 0;
				vx = 0;
				int jumpLevel[] = { 2, 2, 1 };
				int damageCut = 1;
				if (falldownDamage >= 8 / jumpLevel[getBodyAgeState().ordinal()]) {
					if (checkOnBed()) {
						damageCut = 4;
					} else {
						// ベッドの上以外で生まれた時にダメージを受けた場合、つらい思い出が残る(暫定で良い思いしてない時に落下ダメージ受けたら）
						if (isBFirstGround()) {
							addMemories(-20);
						}
					}

					if (damageCut != 4) {
						for (Map.Entry<Integer, Trampoline> entry : SimYukkuri.world.getCurrentMap().getTrampoline()
								.entrySet()) {
							Trampoline t = entry.getValue();
							if (t.checkHitObj(this)) {
								damageCut = 100;
								break;
							}
						}
					}

					if (isbNoDamageNextFall() && falldownDamage != 0) {
						setbNoDamageNextFall(false);
						falldownDamage = 0;
					}

					// 赤ゆならベッドの上ではノーダメージ
					if (!checkOnBed() || !isBaby()) {
						strike(falldownDamage * 100 * 24 * 3 / 100 / damageCut);
					}

					// 生まれて最初の挨拶
					if (isBFirstGround()) {
						setMessage(MessagePool.getMessage(this, MessagePool.Action.TakeItEasy));
						addStress(-400);
						addMemories(20);
					}
					// 地面についたのでフラグをリセット
					setBFirstGround(false);

					if (isPealed())
						toDead();
					if (isDead()) {
						setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying));
						stay();
						setCrushed(true);
					}
				}
			}
			setBx(0);
			setBy(0);
			bz = 0;
			return;
		}

		x = Math.max(0, x);
		x = Math.min(x, Translate.getMapW());
		y = Math.max(0, y);
		y = Math.min(y, Translate.getMapH());
		// z = Math.max(0, z);
		z = Math.min(z, Translate.getMapZ());

		if (dontMove || isLockmove()) {
			setBx(0);
			setBy(0);
			bz = 0;
			return;
		}
		// 仮の処理 コンベア移動中は動けなくする
		if ((getBx() + getBy() + bz) != 0) {
			setBx(0);
			setBy(0);
			bz = 0;
			return;
		}

		// moving
		int step = getSTEPorg()[getBodyAgeState().ordinal()];
		if (hasBabyOrStalk() || (isSoHungry() && !isPredatorType()) || getDamageState() != Damage.NONE
				|| isSick() || isFeelPain() || (isFlyingType() && !canflyCheck())
				|| (isGotBurnedHeavily() && !canflyCheck())) {
			step /= 2;
		}
		if (getAttachmentSize(Ants.class) != 0) {
			step /= 2;
		}
		if (isBlind()) {
			step /= 2;
		}

		// 家族でおでかけ中なら一番足が遅いものにあわせる
		if (getCurrentEvent() instanceof SuperEatingTimeEvent) {
			step = ((SuperEatingTimeEvent) getCurrentEvent()).getLowestStep();
		}

		if (step == 0) {
			step = 1;
		}

		int freq = getSTEPorg()[AgeState.ADULT.ordinal()] / step;
		if (getAge() % freq != 0) {
			setBx(0);
			setBy(0);
			bz = 0;
			return;
		}

		// calculate x direction
		if (destX >= 0) {
			dirX = decideDirection(x, destX, 0);
			if (dirX == 0) {
				destX = -1;
			}
		} else {
			if (countX++ >= getSameDest() * getSTEPorg()[getBodyAgeState().ordinal()]) {
				countX = 0;
				dirX = randomDirection(dirX);
				if (!hasOkazari() && (isSad() || isVerySad())) {
					if (SimYukkuri.RND.nextInt(10) == 0) {
						setMessage(MessagePool.getMessage(this, MessagePool.Action.NoAccessory));
					}
				}
			}
		}
		// calculate y direction
		if (destY >= 0) {
			dirY = decideDirection(y, destY, 0);
			if (dirY == 0) {
				destY = -1;
			}
		} else {
			if (countY++ >= getSameDest() * getSTEPorg()[getBodyAgeState().ordinal()]) {
				countY = 0;
				dirY = randomDirection(dirY);
				if (!hasOkazari() && (isSad() || isVerySad())) {
					if (SimYukkuri.RND.nextInt(10) == 0) {
						setMessage(MessagePool.getMessage(this, MessagePool.Action.NoAccessory));
					}
				}
			}
		}
		// calculate z direction
		if (canflyCheck()) {
			if (destZ >= 0) {
				dirZ = decideDirection(z, destZ, 0);
				if (dirZ == 0) {
					destZ = -1;
				}
			}
			// 目標が無ければ高度を保つように移動
			if (takeMoveTarget() == null && getCurrentEvent() == null) {
				destZ = Translate.getFlyHeightLimit();
			}
		}

		// move to the direction
		step = 1;
		if (isRaper() && isExciting())
			step = 2;

		int vecX = dirX * step * speed / 100;
		int vecY = dirY * step * speed / 100;
		int vecZ = dirZ * step * speed / 100;
		// 実験 speedの切り捨て部分の反映
		if (speed % 100 > 0) {
			if (SimYukkuri.RND.nextInt(100) < speed % 100) {
				vecX += dirX;
				vecY += dirY;
				vecZ += dirZ;
			}
		}

		// 明確な目的地がある場合は行き過ぎをチェック
		if (destX != -1) {
			if (dirX < 0) {
				if ((x + vecX) < destX) {
					x = destX;
				} else {
					x += vecX;
				}
			} else if (dirX > 0) {
				if ((x + vecX) > destX) {
					x = destX;
				} else {
					x += vecX;
				}
			}
		} else {
			x += vecX;
		}
		if (destY != -1) {
			if (dirY < 0) {
				if ((y + vecY) < destY) {
					y = destY;
				} else {
					y += vecY;
				}
			} else if (dirY > 0) {
				if ((y + vecY) > destY) {
					y = destY;
				} else {
					y += vecY;
				}
			}
		} else {
			y += vecY;
		}
		if (canflyCheck() && destZ != -1) {
			if (dirZ < 0) {
				if ((z + vecZ) < destZ) {
					z = destZ;
				} else {
					z += vecZ;
				}
			} else if (dirZ > 0) {
				if ((z + vecZ) > destZ) {
					z = destZ;
				} else {
					z += vecZ;
				}
			}
		} else {
			z += vecZ;
		}

		// 壁チェック
		if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 3, Barrier.MAP_BODY[getBodyAgeState().ordinal()])) {
			x -= vecX;
			y -= vecY;
			z -= vecZ;
			// 壁にひっかかったら方向転換
			if ((destX >= 0) || (destY >= 0) || (destZ >= 0)) {
				setBlockedCount(Math.min(getBlockedCount() + 1, getBLOCKEDLIMITorg() * 2));
				if (getBlockedCount() > getBLOCKEDLIMITorg()) {
					if (SimYukkuri.RND.nextBoolean()) {
						dirX = randomDirection(dirX);
					} else {
						dirY = randomDirection(dirY);
					}
					destX = -1;
					destY = -1;
					// イベント中の場合はイベントをクリアしない(壁衝突でうろうろし続ける問題の修正)
					if (getCurrentEvent() != null) {
						clearActionsForEvent();
					} else {
						clearActions();
					}
					if (getIntelligence() == Intelligence.FOOL && getPanicType() != null) {
						setHappiness(Happiness.VERY_SAD);
					}
				} else if (getBlockedCount() > getBLOCKEDLIMITorg() / 2 && getIntelligence() == Intelligence.FOOL
						&& getPanicType() != null) {
					if (isRude()) {
						setAngry();
					} else {
						setCalm();
						setHappiness(Happiness.SAD);
					}
				}
				if (getIntelligence() == Intelligence.FOOL && getPanicType() != null) {
					setMessage(MessagePool.getMessage(this, MessagePool.Action.BlockedByWall));
				}
			} else {
				dirX = randomDirection(dirX);
				dirY = randomDirection(dirY);
			}
		} else {
			setBlockedCount(Math.max(0, getBlockedCount() - 1));

			// プール外からプール内への移動チェック
			if ((Translate.getCurrentFieldMapNum(x, y) & FieldShapeBase.FIELD_POOL) != 0 &&
					(Translate.getCurrentFieldMapNum(x - vecX, y - vecY) & FieldShapeBase.FIELD_POOL) == 0) {
				// 水が嫌いなら近寄らない
				if (!isLikeWater()) {
					int nRandom = 1;
					// 事故率の設定
					switch (getIntelligence()) {
						case FOOL:
							nRandom = 10;
							break;
						case AVERAGE:
							nRandom = 30;
							break;
						case WISE:
							nRandom = 100;
							break;
					}

					if (SimYukkuri.RND.nextInt(nRandom) != 0) {
						x -= vecX;
						y -= vecY;
						z -= vecZ;

						dirX = randomDirection(dirX);
						dirY = randomDirection(dirY);
					}
				}
			}
		}

		if (x < 0) {
			x = 0;
			dirX = 1;
		} else if (x > Translate.getMapW()) {
			x = Translate.getMapW();
			dirX = -1;
		}
		if (y < 0) {
			y = 0;
			dirY = 1;
		} else if (y > Translate.getMapH()) {
			y = Translate.getMapH();
			dirY = -1;
		}
		if (z < 0) {
			// z = 0;
		} else if (z > Translate.getMapZ()) {
			z = Translate.getMapZ();
		}
		// update direction of the face
		if (dirX == -1) {
			setDirection(Direction.LEFT);
		} else if (dirX == 1) {
			setDirection(Direction.RIGHT);
		}
		setBx(0);
		setBy(0);
		bz = 0;
	}

	/**
	 * 標準のメッセージ表示
	 * 
	 * @param message メッセージ
	 */
	public void setMessage(String message) {
		if (message == null)
			return;

		if (message.length() == 0)
			return;

		// messageの長さで自動的に調整する
		int nSize = message.length();
		if (20 < nSize) {
			setMessage(message, WindowType.NORMAL, nSize, false, false, false);
		} else {
			setMessage(message, WindowType.NORMAL, Const.HOLDMESSAGE, false, false, false);
		}
	}

	/**
	 * ピコピコメッセージ表示
	 * 
	 * @param message   メッセージ
	 * @param interrupt 現在メッセージ中でも割り込むかどうか
	 */
	public void setPikoMessage(String message, boolean interrupt) {
		setMessage(message, WindowType.NORMAL, Const.HOLDMESSAGE, interrupt, true, false);
	}

	/**
	 * ピコピコメッセージ表示(時間指定)
	 * 
	 * @param message   メッセージ
	 * @param count     メッセージ時間
	 * @param interrupt 現在メッセージ中でも割り込むかどうか
	 */
	public void setPikoMessage(String message, int count, boolean interrupt) {
		setMessage(message, WindowType.NORMAL, count, interrupt, true, false);
	}

	/**
	 * 時間指定メッセージ表示
	 * 
	 * @param message メッセージ
	 * @param count   メッセージ時間
	 */
	public void setMessage(String message, int count) {
		setMessage(message, WindowType.NORMAL, count, false, false, false);
	}

	/**
	 * 割り込み指定メッセージ表示
	 * 
	 * @param message   メッセージ
	 * @param interrupt 現在メッセージ中でも割り込むかどうか
	 */
	public void setMessage(String message, boolean interrupt) {
		setMessage(message, WindowType.NORMAL, Const.HOLDMESSAGE, interrupt, false, false);
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
		setMessage(message, WindowType.NORMAL, count, interrupt, piko, false);
	}

	/**
	 * ワールドイベント発生メッセージ
	 * 
	 * @param message メッセージ
	 * @param count   メッセージ時間
	 */
	public void setWorldEventSendMessage(String message, int count) {
		setMessage(message, WindowType.WORLD_SEND, count, true, false, false);
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
		setMessage(message, WindowType.WORLD_RES, count, interrupt, piko, false);
	}

	/**
	 * 個体イベント発生メッセージ
	 * 
	 * @param message メッセージ
	 * @param count   メッセージ時間
	 */
	public void setBodyEventSendMessage(String message, int count) {
		setMessage(message, WindowType.BODY_SEND, count, true, false, false);
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
		setMessage(message, WindowType.BODY_RES, count, interrupt, piko, false);
	}

	/**
	 * 非ゆっくり症＆口封じ用メッセージ
	 * 
	 * @param message メッセージ
	 * @param piko    ピコピコするかどうか
	 */
	public void setNYDMessage(String message, boolean piko) {
		setMessage(message, WindowType.NORMAL, Const.HOLDMESSAGE, true, piko, true);
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
		if (!NYD
				&& (isNYD() /* || shutmouth */ || isSleeping())) {
			return;
		}

		// 死亡時
		if (isSilent())
			return;
		// 緊急時以外の自制時。静かにするよ！！と言ってしまう。
		if (!interrupt && SimYukkuri.RND.nextInt(messageDiscipline + 1) != 0
				&& getIntelligence() != Intelligence.WISE) {
			message = MessagePool.getMessage(this, MessagePool.Action.BeingQuiet);
			return;
		}
		// その他の要因
		if (!isCanTalk()) {
			messageCount = 0;
			setMessageBuf(null);
			return;
		}
		// メッセージ無効時
		if (message == null || message.length() == 0) {
			messageCount = 0;
			setMessageBuf(null);
			return;
		}

		// interruptがtrueなら現在メッセージ表示中でも割り込む
		if (interrupt || messageCount == 0) {
			messageCount = count;
			setMessageBuf(message);
			// reset actions.
			if (!isFixBack()) {
				setFurifuri(false);
			}
			setSukkiri(false);
			setBeVain(false);
			setNobinobi(false);
			setYunnyaa(false);
			setPikopiko(piko);
			setOrigMessageLineColor(Const.WINDOW_COLOR[type.ordinal()][0]);
			setOrigMessageBoxColor(Const.WINDOW_COLOR[type.ordinal()][1]);
			setOrigMessageTextColor(Const.WINDOW_COLOR[type.ordinal()][2]);
			setMessageWindowStroke(Const.WINDOW_STROKE[type.ordinal()]);
			switch (getBaryState()) {
				case NONE:
					setMessageTextSize(12);
					break;
				case HALF:
					setMessageTextSize(12);
					setFurifuri(false);
					break;
				case NEARLY_ALL:
					setMessageTextSize(8);
					setPikopiko(false);
					setFurifuri(false);
					setBeVain(false);
					setNobinobi(false);
					setPeropero(false);
					setYunnyaa(false);
					setBegging(false);
					setOrigMessageBoxColor(new Color(217, 128, 0, 200));
					break;
				case ALL:
					setMessageTextSize(7);
					setPikopiko(false);
					setFurifuri(false);
					setBeVain(false);
					setNobinobi(false);
					setPeropero(false);
					setYunnyaa(false);
					setBegging(false);
					setOrigMessageBoxColor(new Color(128, 54, 0, 200));
					break;
				default:
					setMessageTextSize(12);
					break;
			}
		}
	}

	/**
	 * ねぎぃメッセージを出す
	 * 
	 * @param message メッセージ
	 * @param piko    ピコピコするかどうか
	 */
	public void setNegiMessage(String message, boolean piko) {
		setNegiMessage(message, Const.HOLDMESSAGE, piko);
	}

	/**
	 * ねぎぃメッセージを出す
	 * 
	 * @param message メッセージ
	 * @param count   メッセージ時間
	 * @param piko    ピコピコするかどうか
	 */
	public void setNegiMessage(String message, int count, boolean piko) {
		if (!isCanTalk()) {
			messageCount = 0;
			setMessageBuf(null);
			return;
		}
		messageCount = count;
		setMessageBuf(message);
		setPikopiko(piko);
		// reset actions.
		if (!isFixBack()) {
			setFurifuri(false);
		}
		setStrike(false);
		setEating(false);
		setEatingShit(false);
		setPeropero(false);
		setSukkiri(false);
		setNobinobi(false);
		setBeVain(false);
		setYunnyaa(false);
		setInOutTakeoutItem(false);
		setOrigMessageLineColor(Const.NEGI_WINDOW_COLOR[0]);
		setOrigMessageBoxColor(Const.NEGI_WINDOW_COLOR[1]);
		setOrigMessageTextColor(Const.NEGI_WINDOW_COLOR[2]);
		setMessageWindowStroke(Const.WINDOW_STROKE[0]);
		setMessageTextSize(12);
	}

	/**
	 * 茎に触ったときの反応.
	 */
	public final void touchStalk() {
		setMessage(MessagePool.getMessage(this, MessagePool.Action.AbuseBaby));
		setHappiness(Happiness.SAD);
	}

	/**
	 * 未誕生フラグを設定する.
	 */
	public void setUnBirth(boolean flag) {
		unBirth = flag;
		enableWall = !flag;
		setCanTalk(!flag);
		setBFirstGround(true);
		if (flag) {
			setMessage(null);
			forceToSleep();
		} else {
			// うまれたての赤ゆならリセット
			if (getBodyAgeState() == AgeState.BABY) {
				setAge(0);
			}
			wakeup();
		}
	}

	/**
	 * 実ゆ（自分）が茎で生きている親につながっているかを返却する.
	 * 
	 * @return 実ゆ（自分）が茎で生きている親につながっているか
	 */
	@Transient
	public final boolean isPlantForUnbirthChild() {
		if (isUnBirth()) {
			// 茎がある
			if (getBindStalk() != null) {
				int id = getBindStalk().getPlantYukkuri();
				Obj oBind = SimYukkuri.world.getCurrentMap().getBody().get(id);
				if (oBind != null && oBind instanceof Body) {
					Body bodyBind = (Body) oBind;
					// 茎があって親が生きてる
					if (bodyBind != null && !bodyBind.isDead() && !bodyBind.isRemoved()) {
						return true;
					}
				}
			}
			return false;
		}
		return false;
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
	 * うんうん、興奮、ふりふり、セリフの抑制効果の減衰と確認をする.
	 */
	public final void checkDiscipline() {
		// ゲス餡子脳は自制しない
		if (isRude() && getIntelligence() == Intelligence.FOOL) {
			setShittingDiscipline(0);
			setExcitingDiscipline(0);
			setFurifuriDiscipline(0);
			setMessageDiscipline(0);
			return;
		}
		// 基本ゲーム内時間12分に1回
		int period = getDECLINEPERIODorg();
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
			messageDiscipline--;
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
			if (messageDiscipline < 0) {
				messageDiscipline = 0;
			}
			if (messageDiscipline > 20) {
				messageDiscipline = 20;
			}
		}
	}

	/**
	 * うんうん、興奮、ふりふり、セリフの抑制をする.
	 * 
	 * @param p 抑制ポイント
	 */
	public void disclipline(int p) {
		if (isExciting() && !isRaper()) {
			excitingDiscipline = excitingDiscipline + (p * 10);
			setCalm();
		} else if (isShitting()) {
			shittingDiscipline = shittingDiscipline + p;
			setShitting(false);
			shit -= getANGRYPERIODorg() * 2;
		} else if (isFurifuri()) {
			furifuriDiscipline = furifuriDiscipline + p;
			setFurifuri(false);
		} else if (getMessageBuf() != null) {
			messageDiscipline = messageDiscipline + (p / 2);
			setMessageBuf(null);
		}
	}

	/**
	 * ゲス度によって、性格変更。ゲス落ちor更生をする.
	 */
	public final void checkAttitude() {
		// 非ゆっくり症、足りないゆは変化しない
		if (isNYD() || isIdiot()) {
			AttitudePoint = 0;
			return;
		}
		// ドゲス、超善良は変化しない
		if (getAttitude() == Attitude.VERY_NICE || getAttitude() == Attitude.SUPER_SHITHEAD) {
			AttitudePoint = 0;
			return;
		}
		double Correction = 1;
		// 知性による補正
		switch (getIntelligence()) {
			case FOOL:
				Correction = 0.75;
				break;
			case WISE:
				Correction = 1.5;
				break;
			default:
				break;
		}
		// 性格変化実行
		switch (getAttitude()) {
			case NICE:
				if (AttitudePoint >= getNiceLimit()[1] && getIntelligence() != Intelligence.FOOL) {
					setAttitude(Attitude.VERY_NICE);
					AttitudePoint = 0;
				}
				if (AttitudePoint <= getRudeLimit()[0] * Correction) {
					setAttitude(Attitude.AVERAGE);
					AttitudePoint = 0;
				}
				break;
			case AVERAGE:
				if (AttitudePoint <= getRudeLimit()[0] * Correction) {
					setAttitude(Attitude.SHITHEAD);
					AttitudePoint = 0;
				}
				if (AttitudePoint >= getNiceLimit()[0]) {
					setAttitude(Attitude.NICE);
					AttitudePoint = 0;
				}
				break;
			case SHITHEAD:
				if (AttitudePoint <= getRudeLimit()[1] * Correction) {
					setAttitude(Attitude.SUPER_SHITHEAD);
					AttitudePoint = 0;
				}
				if (AttitudePoint >= getNiceLimit()[0]) {
					setAttitude(Attitude.AVERAGE);
					AttitudePoint = 0;
				}
				break;
			default:
				break;
		}
	}

	/**
	 * 通常時の躾(お仕置き成功条件の判断後にゲス度をいじる)
	 * 
	 * @param p 抑制ポイント
	 */
	public final void teachManner(int p) {
		disclipline(p * 5);
		boolean flag = false;
		// 通常、ゲス関係なく
		// ふりふりしてる時、すっきりしててかつれいぱーじゃない時
		if (isFurifuri() || (isSukkiri() && !isRaper())) {
			flag = true;
		}

		// ゲスの時
		if (isRude()) {
			// しゃべってる時
			if (isTalking()) {
				flag = true;
			}
		}

		if (flag) {
			plusAttitude(p);
		}
	}

	/**
	 * 強制的にゲス度をいじる（加える）.
	 * 
	 * @param p ゲス度
	 */
	public final void plusAttitude(int p) {
		if (isNotChangeCharacter())
			return;
		AttitudePoint += p;
	}

	/**
	 * プレイヤーが好きか嫌いかを返却する.
	 * 
	 * @return プレイヤーが好きか嫌いか
	 */
	public final LovePlayer checkLovePlayerState() {
		// -50%以下なら嫌い
		if (getnLovePlayer() < -1 * getLOVEPLAYERLIMITorg() / 2) {
			return LovePlayer.BAD;
		}
		// 50%以上なら好き
		if (getLOVEPLAYERLIMITorg() / 2 < getnLovePlayer()) {
			return LovePlayer.GOOD;
		}
		return LovePlayer.NONE;
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
	 * 妊娠、うんうん、過食などによる横方向の体型のふくらみ取得
	 */
	@Override
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
				+ ((shit * 4 / 5) / getSHITLIMITorg()[getBodyAgeState().ordinal()]) * 5
				+ getBodySpr()[getBodyAgeState().ordinal()].getImageW() * (OE - 100) / 100
				+ getGodHandHoldPoint() / 2;
	}

	/**
	 * 妊娠、うんうんなどによる縦方向の体型のふくらみ取得
	 */
	@Override
	@Transient
	public int getExpandSizeH() {
		return (20 - 20 / (getBabyTypes().size() + 1)) + getBabyTypes().size() * 2
				+ ((shit * 4 / 5) / getSHITLIMITorg()[getBodyAgeState().ordinal()]) * 5
				+ getGodHandHoldPoint() / 2;
	}

	/**
	 * ひっぱり、押しつぶしによる体型の変形を取得する.
	 * 
	 * @return ひっぱり、押しつぶしによる体型の変形
	 */
	@Transient
	private int getExternalForceW() {
		int ret = 0;
		// +ひっぱり -押しつぶし
		if (extForce < 0)
			ret = -extForce;
		return ret;
	}

	@Transient
	private int getExternalForceH() {
		int ret = 0;
		// +ひっぱり -押しつぶし
		if (extForce > 0) {
			ret = extForce * 6;
		} else if (extForce < 0) {
			ret = extForce * 2;
		}
		return ret;
	}

	/**
	 * スプライト画像のサイズ初期設定をする.
	 * 
	 * @param body
	 * @param braid
	 */
	public void setBoundary(Dimension4y[] body, Dimension4y[] braid) {
		for (int i = 0; i < body.length; i++) {
			getBodySpr()[i] = new Sprite(body[i].getWidth(), body[i].getHeight(), Sprite.PIVOT_CENTER_BOTTOM);
			getExpandSpr()[i] = new Sprite(body[i].getWidth(), body[i].getHeight(), Sprite.PIVOT_CENTER_BOTTOM);
		}
		for (int i = 0; i < braid.length; i++) {
			if (braid[i] != null) {
				getBraidSpr()[i] = new Sprite(braid[i].getWidth(), braid[i].getHeight(), Sprite.PIVOT_CENTER_BOTTOM);
			} else {
				getBraidSpr()[i] = new Sprite(0, 0, Sprite.PIVOT_CENTER_CENTER);
			}
		}
	}

	/**
	 * スプライト画像サイズのアップデートをする.
	 */
	public final void updateSpriteSize() {
		int forceW = getExternalForceW();
		int forceH = getExternalForceH();
		if (SimYukkuri.UNYO) {
			forceW = getExternalForceW() + unyoForceW;
			forceH = getExternalForceH() + unyoForceH;
		}

		int expSizeW = getExpandSizeW();
		int expSizeH = getExpandSizeH();
		getExpandSpr()[getBodyAgeState().ordinal()].addSpriteSize(forceW + expSizeW, forceH + expSizeH);
	}

	public final void getBoundaryShape(Rectangle r) {
		r.x = getBodySpr()[getBodyAgeState().ordinal()].getPivotX();
		r.y = getBodySpr()[getBodyAgeState().ordinal()].getPivotY();
		r.width = getBodySpr()[getBodyAgeState().ordinal()].getImageW();
		r.height = getBodySpr()[getBodyAgeState().ordinal()].getImageH();
	}

	public final void getExpandShape(Rectangle4y r) {
		r.setWidth(getExpandSpr()[getBodyAgeState().ordinal()].getScreenRect()[0].getWidth());
		r.setHeight(getExpandSpr()[getBodyAgeState().ordinal()].getScreenRect()[0].getHeight());
		if (SimYukkuri.UNYO) {
			r.setWidth(getExpandSpr()[getBodyAgeState().ordinal()].getScreenRect()[0].getWidth() + unyoForceW);
			r.setHeight(getExpandSpr()[getBodyAgeState().ordinal()].getScreenRect()[0].getHeight() + unyoForceH);
		}

		r.setX(r.getWidth() >> 1);
		r.setY(r.getHeight() - 1);
	}

	/**
	 * 移動目標を取得する.
	 */
	public final Obj takeMoveTarget() {
		// 移動対象が床からなくなっていた場合は初期化
		Obj o = takeMappedObj(moveTarget);
		if (o != null && o.getWhere() != Where.ON_FLOOR) {
			setMoveTarget(-1);
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
	public void setTakeoutItem(TakeoutItemType key, Obj val) {
		getTakeoutItem().put(key, val.objId);
		// 動作の表現と、フラグ管理
		setInOutTakeoutItem(true);
		setToTakeout(false);
		if (purposeOfMoving == PurposeOfMoving.TAKEOUT) {
			purposeOfMoving = PurposeOfMoving.NONE;
		}

		// val.remove();
		if (val instanceof Shit) {
			Map<Integer, Shit> shits = SimYukkuri.world.getCurrentMap().getShit();
			shits.remove(val.objId);
			SimYukkuri.world.getCurrentMap().getTakenOutShit().put(val.objId, (Shit) val);
			val.setWhere(Where.IN_YUKKURI);
		}

		if (val instanceof Food) {
			Map<Integer, Food> foods = SimYukkuri.world.getCurrentMap().getFood();
			foods.remove(val.objId);
			SimYukkuri.world.getCurrentMap().getTakenOutFood().put(val.objId, (Food) val);
			val.setWhere(Where.IN_YUKKURI);
		}
	}

	/**
	 * 持っていたアイテムを落とす.
	 * 
	 * @param key アイテムのタイプ
	 * @return 持っていたアイテムのオブジェクト
	 */
	public Obj dropTakeoutItem(TakeoutItemType key) {
		Obj val = takeTakenOutItem(key);
		if (val == null) {
			getTakeoutItem().remove(key);
			return null;
		}
		// 動作の表現
		setInOutTakeoutItem(true);
		setMessage(MessagePool.getMessage(this, MessagePool.Action.DropItem));

		// 落としたもの（うんうん）の処理
		if (val instanceof Shit) {
			Map<Integer, Shit> shits = SimYukkuri.world.getCurrentMap().getShit();
			shits.put(val.objId, (Shit) val);
			SimYukkuri.world.getCurrentMap().getTakenOutShit().remove(val.objId);
			val.setCalcX(x);
			if (y + 3 <= Translate.getMapH()) {
				val.setCalcY(y);
			} else {
				val.setCalcY(y + 3);
			}
			val.setCalcZ(z + 10);
			val.setWhere(Where.ON_FLOOR);
			getTakeoutItem().remove(key);
		}
		// 落としたもの(餌)の処理
		if (val instanceof Food) {
			Map<Integer, Food> foods = SimYukkuri.world.getCurrentMap().getFood();
			foods.put(val.objId, (Food) val);
			SimYukkuri.world.getCurrentMap().getTakenOutFood().remove(val.objId);
			val.setCalcX(x);
			if (y + 3 <= Translate.getMapH()) {
				val.setCalcY(y + 3);
			} else {
				val.setCalcY(y);
			}
			val.setCalcZ(z + 10);
			val.setWhere(Where.ON_FLOOR);
			getTakeoutItem().remove(key);
		}
		return val;
	}

	private Obj takeTakenOutItem(TakeoutItemType key) {
		Integer i = getTakeoutItem().get(key);
		if (i == null)
			return null;
		MapPlaceData m = SimYukkuri.world.getCurrentMap();
		if (m.getTakenOutFood().containsKey(i.intValue())) {
			return m.getTakenOutFood().get(i.intValue());
		}
		if (m.getTakenOutShit().containsKey(i.intValue())) {
			return m.getTakenOutShit().get(i.intValue());
		}
		return null;
	}

	/**
	 * 全部落とす.
	 */
	public void dropAllTakeoutItem() {
		// 運んでいるものがなかったらリターン
		if (getTakeoutItem() == null || getTakeoutItem().size() == 0) {
			return;
		}
		Set<TakeoutItemType> keyset = getTakeoutItem().keySet();
		// 運んでいるものすべてに対して落とす処理をする
		for (TakeoutItemType key : keyset) {
			dropTakeoutItem(key);
		}
	}

	/**
	 * 影のロードを行う
	 * 
	 * @param loader
	 * @param io
	 * @throws IOException
	 */
	public static void loadShadowImages(ClassLoader loader, ImageObserver io) throws IOException {
		final String path = "images/";
		int sx, sy;

		getShadowImages()[Const.ADULT_INDEX] = ImageIO.read(loader.getResourceAsStream(path + "shadow.png"));

		sx = (int) ((float) getShadowImages()[Const.ADULT_INDEX].getWidth(io) * Const.BODY_SIZE[1]);
		sy = (int) ((float) getShadowImages()[Const.ADULT_INDEX].getHeight(io) * Const.BODY_SIZE[1]);
		getShadowImages()[Const.CHILD_INDEX] = ModLoader.scaleImage(getShadowImages()[Const.ADULT_INDEX], sx, sy);
		sx = (int) ((float) getShadowImages()[Const.ADULT_INDEX].getWidth(io) * Const.BODY_SIZE[0]);
		sy = (int) ((float) getShadowImages()[Const.ADULT_INDEX].getHeight(io) * Const.BODY_SIZE[0]);
		getShadowImages()[Const.BABY_INDEX] = ModLoader.scaleImage(getShadowImages()[Const.ADULT_INDEX], sx, sy);

		for (int i = 0; i < 3; i++) {
			getShadowImgW()[i] = getShadowImages()[i].getWidth(io);
			getShadowImgH()[i] = getShadowImages()[i].getHeight(io);
			getShadowPivX()[i] = getShadowImgW()[i] >> 1;
			getShadowPivY()[i] = getShadowImgH()[i] >> 1;
		}
	}

	/**
	 * 怒らせる.
	 */
	public final void setAngry() {
		if (isDead() || isNYD() || isSleeping()) {
			return;
		}
		if (getDamageState() == Damage.NONE && !isVerySad()) {
			setAngry(true);
			setScare(false);
		}

		if (isFixBack() && isFurifuri()) {
			setFurifuri(true);
		} else {
			setFurifuri(false);
		}
		if (!isRaper()) {
			setExciting(false);
		}
		setbForceExciting(false);
		setRelax(false);
		setBeVain(false);
		setNobinobi(false);
		setYunnyaa(false);
		excitingPeriod = 0;
		noDamagePeriod = 0;
		noHungryPeriod = 0;
	}

	/**
	 * 汚れさせる.
	 */
	public final void makeDirty(boolean flag) {
		dirty = flag;
		// 死んでる場合はスキップ
		if (isDead()) {
			return;
		}
		if (isDirty()) {
			setHappiness(Happiness.SAD);
			addStress(50);
			checkReactionStalkMother(UnbirthBabyState.ATTAKED);
		} else {
			setStubbornlyDirty(false);
			setHappiness(Happiness.HAPPY);
			addStress(-50);
			// 起きてたらセリフ
			if (!isSleeping()) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Cleaned), 60);
				stay(60);
			}
			// 実ゆの場合、親が反応する
			checkReactionStalkMother(UnbirthBabyState.HAPPY);
		}
	}

	/**
	 * 無反応で汚れを設定する.
	 * 
	 * @param b 汚れ
	 */
	public final void setDirtyFlag(boolean b) {
		makeDirty(b);
	}

	/**
	 * 自主的洗浄を行う.
	 */
	public final void cleaningItself() {
		stay();
		setMessage(MessagePool.getMessage(this, MessagePool.Action.CleanItself));
		if (!isAdult()) {
			setHappiness(Happiness.SAD);
			setForceFace(ImageCode.TIRED.ordinal());
		}
		if (canFurifuri())
			setFurifuri(true);
		makeDirty(false);
		// 年齢インデックス: 0=赤ゆ, 1=子ゆ, 2=成ゆ
		int ageIndex = isBaby() ? 0 : (isChild() ? 1 : 2);
		int P = 1;
		switch (getIntelligence()) {
			case WISE:
				P = getCleaningFailProbWise()[ageIndex];
				break;
			case AVERAGE:
				P = getCleaningFailProbAverage()[ageIndex];
				break;
			case FOOL:
				P = getCleaningFailProbFool()[ageIndex];
				break;
		}
		// P が 0 以下の場合は失敗しない（安全対策）
		if (P <= 0) {
			P = 1;
		}
		// 汚れが残る
		if (SimYukkuri.RND.nextInt(P) != 0) {
			setStubbornlyDirty(true);
		}
	}

	/**
	 * 親を呼んで泣きわめく.
	 */
	public final void callParent() {
		// 死んでる、反応する余裕が無い場合はスキップ
		if (!canAction()) {
			dirtyScreamPeriod = 0;
			setCallingParents(false);
			return;
		}

		// ｱﾘに食べられてる時
		if (getAttachmentSize(Ants.class) != 0) {
			setHappiness(Happiness.VERY_SAD);
			// setPikoMessage(MessagePool.getMessage(this,
			// MessagePool.Action.HelpMe),false);
			BodyLogic.checkNearParent(this);
			setCallingParents(true);
		}

		// 汚れてる時
		if (isDirty()) {
			// 死んでる、反応する余裕が無い場合はスキップ
			if (isVeryHungry() || isDamagedHeavily() || isGotBurnedHeavily()) {
				dirtyScreamPeriod = 0;
				setCallingParents(false);
				return;
			}

			boolean kusogaki = (isRude() && isBaby()) || (isChild() && isVeryRude());
			int c = kusogaki ? 20 : 40;
			if (getAge() % c != 0)
				return;
			// 自分がゲス赤ゆか、ドゲス子ゆなら、短いスパンで泣き叫ぶ
			if (kusogaki) {
				setHappiness(Happiness.VERY_SAD);
				setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.Dirty), false);
				BodyLogic.checkNearParent(this);
				setCallingParents(true);
			}
			// 他は長いスパン
			else {
				setHappiness(Happiness.SAD);
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Dirty));
				BodyLogic.checkNearParent(this);
				setCallingParents(true);
			}
			dirtyScreamPeriod--;
			// 泣き疲れたら勝手にきれいにする
			if (dirtyScreamPeriod <= 0) {
				cleaningItself();
			}
		}

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
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Yunnyaa), 50, true, true);
		setYunnyaa(true);
		stay(40);
	}

	/**
	 * 命乞いをする.
	 */
	public void begForLife() {
		begForLife(false);
	}

	/**
	 * 命乞いをする.
	 * 
	 * @param Ffrag 強制命乞いフラグ
	 */
	public void begForLife(boolean Ffrag) {
		// 死体、非ゆっくり症を除外
		if (!canAction())
			return;
		// ダメージが50%を超えないと行われない
		if (!isDamaged() && !Ffrag)
			return;

		int NormalP = 2;
		int RudeP = 3;
		boolean frag = false;
		// 性格によって頑固さが決まる
		switch (getAttitude()) {
			case VERY_NICE:
				if (SimYukkuri.RND.nextInt(NormalP) == 0)
					frag = true;
				break;
			case NICE:
			case AVERAGE:
				if (isStressful() && SimYukkuri.RND.nextInt(NormalP) == 0)
					frag = true;
				break;
			case SHITHEAD:
				if (isStressful() && getIntelligence() != Intelligence.FOOL && SimYukkuri.RND.nextInt(NormalP) == 0) {
					frag = true;
				} else if (isVeryStressful() && SimYukkuri.RND.nextInt(RudeP) == 0) {
					frag = true;
				}
				break;
			case SUPER_SHITHEAD:
				if (isStressful() && getIntelligence() != Intelligence.FOOL && SimYukkuri.RND.nextInt(RudeP) == 0) {
					frag = true;
				} else if (isVeryStressful() && isDamagedHeavily() && SimYukkuri.RND.nextInt(RudeP) == 0) {
					frag = true;
				}
				break;
		}
		if (Ffrag || frag) {
			EventLogic.addBodyEvent(this, new BegForLifeEvent(this, null, null, 1), null, null);
			return;
		}
	}

	/**
	 * otherと何らかの家族関係にあるかを返却する.
	 * 
	 * @param other チェックしたいゆっくりのインスタンス
	 * @return otherと何らかの家族関係にあるか
	 */
	public final boolean isFamily(Body other) {
		if (isParent(other))
			return true;
		if (other.isParent(this))
			return true;
		if (isPartner(other))
			return true;
		if (isSister(other))
			return true;
		return false;
	}

	/**
	 * 自分がotherの親か
	 * 
	 * @param other 対象のゆっくりのインスタンス
	 * @return 自分がotherの親かどうか
	 */
	public final boolean isParent(Body other) {
		if (other == null) {
			return false;
		}
		return (YukkuriUtil.getBodyInstance(other.getParents()[Parent.PAPA.ordinal()]) == this ||
				YukkuriUtil.getBodyInstance(other.getParents()[Parent.MAMA.ordinal()]) == this);
	}

	/**
	 * 自分がotherの父親かどうか
	 * 
	 * @param other 対象のゆっくりのインスタンス
	 * @return 自分がotherの父親かどうか
	 */
	public final boolean isFather(Body other) {
		if (other == null) {
			return false;
		}
		return (YukkuriUtil.getBodyInstance(other.getParents()[Parent.PAPA.ordinal()]) == this);
	}

	/**
	 * 自分がotherの母親かどうか
	 * 
	 * @param other 対象のゆっくりのインスタンス
	 * @return 自分がotherの母親かどうか
	 */
	public final boolean isMother(Body other) {
		if (other == null) {
			return false;
		}
		return (YukkuriUtil.getBodyInstance(other.getParents()[Parent.MAMA.ordinal()]) == this);
	}

	/**
	 * otherが自分の子か
	 * 
	 * @param other 対象のゆっくりのインスタンス
	 * @return otherが自分の子かどうか
	 */
	public final boolean isChild(Body other) {
		if (other == null) {
			return false;
		}
		return other.isParent(this);
	}

	/**
	 * otherが自分のつがいか
	 * 
	 * @param other 対象のゆっくりのインスタンス
	 * @return otherがじぶんのつがいかどうか
	 */
	public final boolean isPartner(Body other) {
		if (other == null) {
			return false;
		}
		Body pa = YukkuriUtil.getBodyInstance(getPartner());
		return (pa != null && pa == other);
	}

	/**
	 * otherが自分の姉妹か
	 * 
	 * @param other 対象のゆっくりのインスタンス
	 * @return otherが自分の姉妹かどうか
	 */
	public final boolean isSister(Body other) {
		if (YukkuriUtil.getBodyInstance(getParents()[Parent.MAMA.ordinal()]) != null) {
			return (getParents()[Parent.MAMA.ordinal()] == other.getParents()[Parent.MAMA.ordinal()]);
		}
		if (YukkuriUtil.getBodyInstance(getParents()[Parent.PAPA.ordinal()]) != null) {
			return (getParents()[Parent.PAPA.ordinal()] == other.getParents()[Parent.PAPA.ordinal()]);
		}
		return false;
	}

	/**
	 * otherが自分の妹か
	 * 
	 * @param other 対象のゆっくりのインスタンス
	 * @return otherが自分の妹か
	 */
	public final boolean isElderSister(Body other) {
		return (isSister(other) && (getAge() >= other.getAge()));
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

	/**
	 * れいぱーかどうか
	 * 
	 * @return れいぱーかどうか
	 */
	@Transient
	public final boolean isRaper() {
		if (isUnBirth()) {
			return false;
		}
		return isRapist();
	}

	/**
	 * れいぱーを設定する.
	 * 
	 * @param b れいぱーかどうか
	 */
	public final void setRaper(boolean b) {
		if (isbPenipeniCutted()) {
			setRapist(false);
		} else {
			setRapist(b);
		}
	}

	/**
	 * スーパーれいぱーかどうか
	 * 
	 * @return スーパーれいぱーかどうか
	 */
	@Transient
	public final boolean isSuperRaper() {
		if (isUnBirth()) {
			return false;
		}
		if (isbPenipeniCutted()) {
			setSuperRapist(false);
		}
		return isSuperRapist();
	}

	/**
	 * すーぱーれいぱーを設定する.
	 * 
	 * @param b すーぱーれいぱーかどうか
	 */
	public final void setSuperRaper(boolean b) {
		if (isbPenipeniCutted()) {
			setSuperRapist(false);
		} else {
			setSuperRapist(b);
		}
	}

	/**
	 * 障害の有無を返却する.
	 * 
	 * @return 障害の有無
	 */
	public final boolean hasDisorder() {
		if (isIdiot())
			return true;
		if (isNYD())
			return true;
		if (!hasOkazari())
			return true;
		if (!hasBraidCheck())
			return true;
		if (isBlind())
			return true;
		if (isPacked())
			return true;
		if (isGotBurned())
			return true;
		if (getCriticalDamege() == CriticalDamegeType.CUT)
			return true;

		return false;
	}

	// 飛行種かどうか
	// 種族としてのフラグを返すので現在飛べるかはcanflyCheckでチェック

	/**
	 * 現在飛行可能か
	 * 
	 * @return 現在飛行可能か
	 */
	public final boolean canflyCheck() {
		return (isFlyingType() && isHasBraid() && !isDead() && !isSleeping() && !isbNeedled()
				&& getCriticalDamege() == null);
	}

	// 現在おさげ、羽、尻尾があるか
	/**
	 * 現在Braidがちぎられてないか
	 * 
	 * @return 現在Braidがちぎられてないか
	 */
	public final boolean hasBraidCheck() {
		return isHasBraid();
	}

	/**
	 * おさげを破壊する.
	 */
	public void takeBraid() {
		if (isDead() || !isBraidType())
			return;

		// なつき度設定
		wakeup();
		clearActions();
		if (isHasBraid()) {
			addLovePlayer(-300);
			addStress(1200);
			setHasBraid(false);
			setForceFace(ImageCode.CRYING.ordinal());
			setHappiness(Happiness.VERY_SAD);
			setMessage(MessagePool.getMessage(this, MessagePool.Action.BraidCut), true);
			// 実ゆの場合、親が反応する
			checkReactionStalkMother(UnbirthBabyState.SAD);
		} else {
			addLovePlayer(200);
			addStress(-1000);
			setHasBraid(true);
			setForceFace(ImageCode.EMBARRASSED.ordinal());
			setHappiness(Happiness.HAPPY);
		}
	}

	/**
	 * 皮むきまたは皮修復（トグル）
	 */
	public void peal() {
		if (isDead())
			return;
		if (isPealed()) {
			setMelt(false);
			setCriticalDamege(null);
			bodyBakePeriod = 0;
			setPealed(false);
			return;
		}
		if (isPacked())
			pack();
		// なつき度設定
		wakeup();
		clearActions();
		addLovePlayer(-300);
		setPealed(true);
		setShutmouth(false);
		seteHairState(HairState.BALDHEAD);
		setHappiness(Happiness.VERY_SAD);
		setMessage(MessagePool.getMessage(this, MessagePool.Action.PEALING), true);
		// 実ゆの場合、親が反応する
		checkReactionStalkMother(UnbirthBabyState.SAD);
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
		if (isDead())
			return;
		if (isPacked()) {
			setCanTalk(true);
			closeAnal(false);
			castrateBody(false);
			// stalkCastration = false;
			setPacked(false);
			peal();
			return;
		}
		// なつき度設定
		wakeup();
		clearActions();
		addLovePlayer(-300);
		setPealed(false);
		setOkazari(null);
		setHasBaby(false);
		closeAnal(true);
		castrateBody(true);
		setCanTalk(false);
		setBlind(true);
		seteHairState(HairState.BALDHEAD);
		if (isBraidType())
			setHasBraid(false);
		setPacked(true);
		setHappiness(Happiness.VERY_SAD);
		// 実ゆの場合、親が反応する
		checkReactionStalkMother(UnbirthBabyState.KILLED);
	}

	/**
	 * 目破壊または修復（トグル）
	 */
	public void breakeyes() {
		if (isDead())
			return;
		if (isBlind()) {
			setBlind(false);
			EYESIGHTorg = 400 * 400;
			return;
		}

		// なつき度設定
		if (isSleeping())
			wakeup();
		clearActions();
		addLovePlayer(-200);
		setBlind(true);
		setHappiness(Happiness.VERY_SAD);
		setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.BLINDING), true);
		// 実ゆの場合、親が反応する
		checkReactionStalkMother(UnbirthBabyState.SAD);
	}

	/**
	 * 口ふさぎまたは修復（トグル）
	 */
	public void ShutMouth() {
		if (isDead())
			return;
		if (isShutmouth()) {
			setShutmouth(false);
			return;
		}
		// なつき度設定
		wakeup();
		clearActions();
		addLovePlayer(-200);
		setShutmouth(true);
		setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.CantTalk), true);
		// eating = false;
		// eatingShit = false;
		setHappiness(Happiness.VERY_SAD);
		// 実ゆの場合、親が反応する
		checkReactionStalkMother(UnbirthBabyState.SAD);
	}

	/**
	 * むしる.
	 */
	public void pickHair() {
		if (isDead())
			return;
		switch (geteHairState()) {
			case BALDHEAD:
				seteHairState(HairState.DEFAULT);
				setHappiness(Happiness.HAPPY);
				addLovePlayer(100);
				return;
			case DEFAULT:
				seteHairState(HairState.BRINDLED1);
				break;
			case BRINDLED1:
				seteHairState(HairState.BRINDLED2);
				break;
			case BRINDLED2:
				seteHairState(HairState.BALDHEAD);
				break;
			default:
		}

		wakeup();
		clearActions();
		addLovePlayer(-200);
		addStress(500);
		if (SimYukkuri.RND.nextInt(3) == 0) {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream), true);
			setForceFace(ImageCode.PAIN.ordinal());
		} else {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.PLUNCKING), true);
			setForceFace(ImageCode.CRYING.ordinal());
		}

		stayPurupuru(30);
		setHappiness(Happiness.VERY_SAD);
		// 実ゆの場合、親が反応する
		checkReactionStalkMother(UnbirthBabyState.ATTAKED);
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
				|| isbNeedled()
				|| getFootBakeLevel() == FootBake.CRITICAL
				|| isLockmove()
				|| isMelt()
				|| getBaryState() != BaryInUGState.NONE
				|| isBirth()
				|| isGrabbed()
				|| isbOnDontMoveBeltconveyor()
				|| geteCoreAnkoState() == CoreAnkoState.NonYukkuriDisease
				|| getCriticalDamegeType() == CriticalDamegeType.CUT
				|| isPealed()
				|| isBlind()
				|| isPacked()) {
			return true;
		} else {
			return false;
		}
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
				|| isbNeedled()
				|| isLockmove()
				|| isMelt()
				|| getBaryState() != BaryInUGState.NONE
				|| isBirth()
				|| isbOnDontMoveBeltconveyor()
				|| geteCoreAnkoState() == CoreAnkoState.NonYukkuriDisease
				|| getCriticalDamegeType() == CriticalDamegeType.CUT
				|| isPealed()
				|| isBlind()
				|| isPacked()) {
			return true;
		} else
			return false;
	}

	/**
	 * ピョンピョンできないかどうかを返却する.
	 * 
	 * @return ピョンピョンできないかどうか
	 */
	@Transient
	public final boolean isDontJump() {
		if (isDontMove())
			return true;

		if (getCriticalDamegeType() != null)
			return true;
		if (hasBabyOrStalk())
			return true;
		if (isNYD())
			return true;
		if (isGotBurnedHeavily())
			return true;
		if (getAttachmentSize(Ants.class) != 0)
			return true;

		// 非飛行種用
		if (!isFlyingType()) {
			if (isDamaged())
				return true;
			if (isSickHeavily())
				return true;
			if (isFeelPain())
				return true;
		}
		return false;
	}

	/**
	 * うんうんがしたいかどうかを返却する.
	 * 
	 * @return うんうんがしたいかどうか
	 */
	public boolean wantToShit() {
		int step = (!isHungry() ? TICK * 2 : TICK);
		int adjust = 50 * (isRude() ? 1 : 2) * shittingDiscipline / (isBaby() ? 2 : 1);
		if ((getSHITLIMITorg()[getBodyAgeState().ordinal()] - shit) < (Const.DIAGONAL * step + adjust)) {
			return true;
		}
		return false;
	}

	/**
	 * 生まれそうかどうかを返却する.
	 * 
	 * @return 生まれそうかどうか
	 */
	public boolean nearToBirth() {
		int step = (!isHungry() ? TICK * 2 : TICK);
		int adjust = 100 * (isRude() ? 1 : 2);

		int limit = getPREGPERIODorg() - pregnantPeriod - (pregnantPeriodBoost / 2);
		int diagonal = Const.DIAGONAL * step + adjust;
		if (limit < diagonal && hasBabyOrStalk()) {
			return true;
		}
		return false;
	}

	/**
	 * 茎を引っこ抜く
	 * 
	 * @param s 茎のインスタンス
	 */
	public final void removeStalk(Stalk s) {
		if (!isDead() && !isSleeping()) {
			if (isNotNYD()) {
				setHappiness(Happiness.VERY_SAD);
				addStress(700);
				setMessage(MessagePool.getMessage(this, MessagePool.Action.AbuseBaby));
			}
		}
		if (getStalks() != null) {
			getStalks().remove(s);
			if (getStalks().size() == 0)
				setHasStalk(false);
		}
	}

	/**
	 * 茎をすべて掃除する.
	 * 右クリの「取る」でも茎は取り除かれてゆっくりのみ取れる。
	 * 茎を「取る」ことはできないが、実ゆは個別に「取る」で取ることができる。
	 */
	public void removeAllStalks() {
		setHasStalk(false);
		// 残念ながら茎だけ残して掃除することはできないので、茎も死んだ実ゆももろともに掃除される。
		if (getStalks() != null) {
			Iterator<Stalk> itr = getStalks().iterator();
			while (itr.hasNext()) {
				try {
					Stalk s = itr.next();
					Iterator<Integer> chit = s.getBindBabies().iterator();
					while (chit.hasNext()) {
						Body child = YukkuriUtil.getBodyInstance(chit.next());
						if (child != null && (child.isDead() || child.isRemoved())) {
							// まだ死んでない無い実ゆだけは茎から落ちる。
							child.remove();
						}
					}
					s.setPlantYukkuri(null);
					s.remove();
				} catch (Exception e) {
					continue;
				}
			}
			setStalks(new LinkedList<>());
		}
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
			Body bodyMother = SimYukkuri.world.getCurrentMap().getBody().get(id);
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
	 * @param eState 実ゆの状態
	 */
	public void checkReactionStalkMother(UnbirthBabyState eState) {
		Body bodyMother = SimYukkuri.world.getCurrentMap().getBody().get(getBindStalkMotherCanNotice());
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

		switch (eState) {
			case ATTAKED:// 実ゆが攻撃されている
				// 攻撃されて生きている場合
				if (!isDead()) {
					bodyMother.setHappiness(Happiness.SAD);
					bodyMother.setMessage(MessagePool.getMessage(bodyMother, MessagePool.Action.AbuseBaby));
					bodyMother.addStress(30);
					bodyMother.stay();
					break;
				}
				// 攻撃されて死んでいる場合はKilled
			case KILLED:// 実ゆが死んでる事に気がつく
				bodyMother.setHappiness(Happiness.VERY_SAD);
				bodyMother.setMessage(MessagePool.getMessage(bodyMother, MessagePool.Action.AbuseBabyKilled));
				bodyMother.addStress(500);
				bodyMother.stay();
				break;
			case SAD:// 実ゆが悲しんでいる、苦しんでいるのを心配する
				bodyMother.setHappiness(Happiness.SAD);
				bodyMother.setMessage(MessagePool.getMessage(bodyMother, MessagePool.Action.ConcernAboutChild));
				bodyMother.addStress(20);
				bodyMother.stay();
				break;
			case HAPPY:// 実ゆの状態を喜んでいる
				bodyMother.setHappiness(Happiness.VERY_HAPPY);
				bodyMother.setMessage(MessagePool.getMessage(bodyMother, MessagePool.Action.GladAboutChild));
				bodyMother.addStress(-100);
				bodyMother.stay();
				break;
			default:
				break;
		}

		return;
	}

	// 妊娠関連
	/**
	 * すっきりを行う
	 * 
	 * @param p すっきり相手
	 */
	public void doSukkiri(Body p) {
		if (isDead()) {
			return;
		}
		if (isNYD()) {
			return;
		}
		// change own state
		setMessage(MessagePool.getMessage(this, MessagePool.Action.Sukkiri), 60, true, false);
		setStress(0);
		addMemories(20);
		stay(60);
		clearActions();
		setSukkiri(true);
		setCalm();
		setHappiness(Happiness.HAPPY);
		hungry -= getHUNGRYLIMITorg()[AgeState.BABY.ordinal()];
		// hungryState = checkHungryState();
		// if it has pants, cannot get pregnant
		if (isHasPants() || p.isHasPants()) {
			if (isHasPants()) {
				makeDirty(true);
			} else {
				p.makeDirty(true);
			}
			return;
		}
		if (isSick() && SimYukkuri.RND.nextBoolean()) {
			p.addSickPeriod(100);
		}
		if (p.isSick() && SimYukkuri.RND.nextBoolean()) {
			addSickPeriod(100);
		}
		if (p.isDead()) {
			return;
		}
		if (p.isNotNYD()) {
			p.setStress(0);
			p.addMemories(20);
			// 相手の妊娠判定
			p.setMessage(MessagePool.getMessage(p, MessagePool.Action.Sukkiri), 60, true, false);
			p.clearActions();
			p.setSukkiri(true);
			p.setHappiness(Happiness.HAPPY);
		}
		p.stay(60);
		p.setCalm();
		p.hungry -= (getHUNGRYLIMITorg()[AgeState.BABY.ordinal()] * 2);

		// 妊娠タイプはランダムで決定
		boolean stalkMode = SimYukkuri.RND.nextBoolean();
		// 該当タイプが避妊されてたら妊娠失敗
		if ((stalkMode && p.isStalkCastration())
				|| (!stalkMode && p.isBodyCastration())
				|| (!stalkMode && p.getFootBakeLevel() == FootBake.CRITICAL)) {
			p.setHappiness(Happiness.VERY_SAD);
			p.setMessage(MessagePool.getMessage(p, MessagePool.Action.NoPregnancy));
			p.addStress(1000);
			return;
		}
		// 子供の生成
		if (stalkMode) {
			p.setHasStalk(true);
		} else {
			p.setHasBaby(true);
		}
		/*
		 * カップルの設定は結婚イベントでやるので、ここではなし
		 * if (isAdult() && p.isAdult()){
		 * partner = p;
		 * p.partner = this;
		 * }
		 */
		Dna baby;
		for (int i = 0; i < 5; i++) {
			baby = YukkuriUtil.createBabyDna(p, this, getType(), getAttitude(), getIntelligence(), false,
					(isSickHeavily() || isStarving()), i == 4);
			if (stalkMode) {
				p.getStalkBabyTypes().add(baby);
			} else {
				if (baby != null)
					p.getBabyTypes().add(baby);
			}
		}
		p.subtractPregnantLimit();
	}

	/**
	 * 早いすっきり抑制を行う
	 */
	public void rapidExcitingDiscipline() {
		if (excitingDiscipline > 0) {
			excitingDiscipline -= TICK;
		}
	}

	/**
	 * れいぽぅする
	 * 
	 * @param p れいぽぅ相手
	 */
	public void doRape(Body p) {
		if (isDead() || isSukkiri()) {
			return;
		}
		// 相手がレイパーなら何もしない
		if (p.isRaper()) {
			clearActions();
			return;
		}

		// change own state
		setMessage(MessagePool.getMessage(this, MessagePool.Action.SukkiriForRaper), 60, true, false);
		setStress(0);
		stay(65);
		addMemories(100);
		p.clearActions();
		p.addStress(500);
		p.stay(65);

		setSukkiri(true);
		setHappiness(Happiness.HAPPY);
		if (isRaper()) {
			hungry -= (getHUNGRYLIMITorg()[AgeState.BABY.ordinal()] / 4);
		} else {
			hungry -= (getHUNGRYLIMITorg()[AgeState.BABY.ordinal()] * 4);
		}
		// hungryState = checkHungryState();
		// if it has pants, cannot get pregnant
		if (isHasPants() || p.isHasPants()) {
			p.setMessage(MessagePool.getMessage(p, MessagePool.Action.ScareRapist));
			p.setHappiness(Happiness.SAD);
			if (isHasPants()) {
				makeDirty(true);
			} else {
				p.makeDirty(true);
			}
			return;
		}
		// ゆかび持ちとすっきりすると1/2の確率で伝染る
		if ((isSick() || p.isSick()) && SimYukkuri.RND.nextBoolean()) {
			p.addSickPeriod(100);
			addSickPeriod(100);
		}
		if (p.isDead()) {
			if (SimYukkuri.RND.nextInt(3) == 0) {
				p.setCrushed(true);
			}
			// 死体とすっきりすると死体がゆかび持ちでなくとも1/4の確率でゆかび感染
			if (SimYukkuri.RND.nextInt(4) == 0) {
				addSickPeriod(100);
			}
			return;
		}

		// 相手の妊娠判定
		p.wakeup();
		if (p.isNotNYD()) {
			p.setMessage(MessagePool.getMessage(p, MessagePool.Action.RaperSukkiri), 60, true, false);
			p.setSukkiri(true);
			setCalm();
			p.setHappiness(Happiness.VERY_SAD);
			p.setForceFace(ImageCode.CRYING.ordinal());
		}
		p.subtractPregnantLimit();
		p.hungry -= getHUNGRYLIMITorg()[AgeState.BABY.ordinal()];

		// 避妊されてたら妊娠失敗
		if (p.isStalkCastration()) {
			return;
		}

		// 子供の生成
		p.setHasStalk(true);
		Dna baby;
		for (int i = 0; i < 5; i++) {
			baby = YukkuriUtil.createBabyDna(p, this, getType(), getAttitude(), getIntelligence(), true,
					(isSickHeavily() || isStarving()), i == 4);
			p.getStalkBabyTypes().add(baby);
		}
		if (isRaper()) {
			// れいぱーは強制れいぽぅ
			forceToRaperExcite(true);
			EventLogic.addWorldEvent(new RaperWakeupEvent(this, null, null, 1), null, null);
		} else if (getAttitude() == Attitude.SUPER_SHITHEAD) {
			// ドゲスは婚姻関係を保ちつつもれいぽぅ
			forceToRaperExcite(false);
		}
		p.subtractPregnantLimit();
	}

	/**
	 * オナニーする.
	 */
	public void doOnanism() {
		doOnanism(null);
	}

	/**
	 * オナニー本体処理
	 * 
	 * @param p 相手（死体など
	 */
	public void doOnanism(Body p) {
		if (!canAction()) {
			return;
		}
		// change own state
		setMessage(MessagePool.getMessage(this, MessagePool.Action.Sukkiri), 60, true, false);
		addStress(-50);
		addMemories(5);
		stay(60);
		clearActions();
		setCalm();
		setHappiness(Happiness.HAPPY);
		hungry -= getHUNGRYLIMITorg()[AgeState.BABY.ordinal()];
		// hungryState = checkHungryState();
		// パンツは汚れる
		if (isHasPants()) {
			makeDirty(true);
		}
		if (p != null) {
			// 性病持ちと死姦はカビの原因
			if ((p.isDead() || p.isSick()) && SimYukkuri.RND.nextBoolean()) {
				addSickPeriod(100);
			}
			if (p.canAction()) {
				p.addStress(50);
				p.addMemories(-5);
				p.setMessage(MessagePool.getMessage(p, MessagePool.Action.Surprise), 60, true, false);
				p.clearActions();
				p.setHappiness(Happiness.SAD);
			}
			p.stay(60);
			p.setCalm();
		}
	}

	/**
	 * 精子餡注入
	 * 
	 * @param dna DNA
	 */
	public void injectInto(Dna dna) {
		if (isDead()) {
			return;
		}
		strikeByPunish();
		Terrarium.setAlarm();
		if (dna == null || isBodyCastration()) {
			return;
		}
		Dna baby = YukkuriUtil.createBabyDna(this, YukkuriUtil.getBodyInstance(dna.getFather()),
				dna.getType(), dna.getAttitude(), dna.getIntelligence(), false, false, true);
		getBabyTypes().add(baby);
		setHasBaby(true);
		subtractPregnantLimit();
	}

	/**
	 * 精子餡滴下
	 * 
	 * @param dna DNA
	 */
	public void dripSperm(Dna dna) {
		if (isDead()) {
			return;
		}
		if (dna == null || isStalkCastration()) {
			return;
		}
		for (int i = 0; i < 5; i++) {
			Dna baby = YukkuriUtil.createBabyDna(this, YukkuriUtil.getBodyInstance(dna.getFather()),
					dna.getType(), dna.getAttitude(), dna.getIntelligence(), false, false, true);
			getStalkBabyTypes().add((SimYukkuri.RND.nextBoolean() ? baby : null));
		}
		setHasStalk(true);
		subtractPregnantLimit();
	}

	// 妊娠限界関連
	/**
	 * 妊娠限界のチェック
	 * 
	 * @return 妊娠限界かどうか
	 */
	@Transient
	public boolean isOverPregnantLimit() {
		// 20210327
		// リアルな妊娠限界のとき
		if (isRealPregnantLimit()) {
			// 妊娠限界を超えてたら
			if (getPregnantLimit() <= 0) {
				// 1/20の確率でまともなゆっくり
				if (SimYukkuri.RND.nextInt(20) == 0) {
					return false;
				}
				// 19/20で足りないゆ
				return true;
			}
			// 妊娠限界に近づくにつれ、足りないゆ確率が高まる。
			// 妊娠限界が100以上の場合は、1/100で足りないゆ。
			int tarinaiFactor = getPregnantLimit() > 100 ? 100 : getPregnantLimit();
			// 1/100 または 1/妊娠限界 の確率で足りないゆ。
			if (SimYukkuri.RND.nextInt(tarinaiFactor) == 0) {
				return true;
			}
			return false;
		}
		// リアルでない妊娠限界のときは、妊娠限界を超えたら即足りないゆが生まれるのみ。
		if (getPregnantLimit() <= 0) {
			return true;
		}
		return false;
	}

	/**
	 * 妊娠限界を一つ早める.
	 * すでに妊娠限界の場合は何もしない.
	 */
	public void subtractPregnantLimit() {
		if (PregnantLimit > 0)
			PregnantLimit--;
		else
			PregnantLimit = 0;
	}

	/**
	 * 強制的に発情させる.
	 */
	public void forceToExcite() {
		if (isRaper() && !isDead()) {
			forceToRaperExcite(true);
			EventLogic.addWorldEvent(new RaperWakeupEvent(this, null, null, 1), this,
					MessagePool.getMessage(this, MessagePool.Action.ExciteForRaper));
			return;
		}

		if (isNYD() || isMelt()) {
			stayPurupuru(20);
			return;
		}

		// 興奮中にさらにバイブしたら強制発情
		if (isExciting()) {
			setbForceExciting(true);
		}

		// 興奮できる状態ではないなら終了
		if (!canAction()) {
			return;
		}

		// ぺにぺにが切断されている場合
		if (isbPenipeniCutted()) {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.PenipeniCutted));
			setHappiness(Happiness.VERY_SAD);
			setForceFace(ImageCode.TIRED.ordinal());
			stayPurupuru(20);
			addStress(30);
			// なつき度設定
			addLovePlayer(-50);
			return;
		}
		clearActionsForEvent();
		setToSukkiri(true);
		wakeup();
		setMessage(MessagePool.getMessage(this, MessagePool.Action.Excite));
		setForceFace(ImageCode.EXCITING.ordinal());
		setExciting(true);
		stayPurupuru(Const.STAYLIMIT);
	}

	/**
	 * ぺにぺに切断のトグル
	 */
	public void cutPenipeni() {
		// ぺにぺにがないなら復活
		if (isbPenipeniCutted()) {
			// Penipeni restoration happens immediately; consider event-based recovery if
			// needed.
			setbPenipeniCutted(false);
			return;
		}
		clearActions();
		setSleeping(false);
		EventLogic.addBodyEvent(this, new CutPenipeniEvent(this, null, null, 1), null, null);
		checkReactionStalkMother(UnbirthBabyState.ATTAKED);
	}

	/**
	 * れいぱー発情させる.
	 * 
	 * @param raper れいぱーかどうか
	 */
	public void forceToRaperExcite(boolean raper) {
		if (isDead() || isExciting() || isbPenipeniCutted()) {
			return;
		}
		wakeup();
		clearActions();
		setExciting(raper);
		setbForceExciting(raper);
		if (raper) {
			setPartner(-1);
		}
		stay();
	}

	/**
	 * 強制的に寝かせる.
	 */
	public void forceToSleep() {
		if (isDead()) {
			return;
		}
		// 矛盾が発生しそうな状況はここでチェック
		if (getPanicType() == PanicType.BURN || getCriticalDamegeType() == CriticalDamegeType.CUT) {
			return;
		}
		clearActions();
		setCalm();
		excitingPeriod = 0;
		setPanicType(null);
		panicPeriod = 0;
		sleepingPeriod = 0;
		setSleeping(true);
	}

	/**
	 * フェロモン状態をトグルする.
	 */
	public final void invPheromone() {
		setbPheromone(!isbPheromone());
	}

	/**
	 * すりすりする.
	 * 
	 * @param p すりすり相手
	 */
	public void doSurisuri(Body p) {
		if (isDead() || p.isDead()) {
			return;
		}
		if (isVeryHungry()) {
			return;
		}
		if (isPeropero()) {
			return;
		}
		if (!canAction()) {
			return;
		}
		// 自分との関係
		EnumRelationMine eRelation = BodyLogic.checkMyRelation(this, p);
		if (findSick(p) || p.isFeelHardPain() || p.isDamaged()) {
			switch (eRelation) {
				case FATHER: // 父
				case MOTHER: // 母
					// 子供を治そうとする
					setMessage(MessagePool.getMessage(this, MessagePool.Action.TreatChildBySurisuri));
					break;
				case PARTNAR: // つがい
					// つがいを治そうとする
					setMessage(MessagePool.getMessage(this, MessagePool.Action.TreatPartnerBySurisuri));
					break;
				case CHILD_FATHER: // 父の子供
					// 父を治そうとする
					setMessage(MessagePool.getMessage(this, MessagePool.Action.TreatFatherBySurisuri));
					break;
				case CHILD_MOTHER: // 母の子供
					// 母を治そうとする
					setMessage(MessagePool.getMessage(this, MessagePool.Action.TreatMotherBySurisuri));
					break;
				case ELDERSISTER: // 姉
					// 妹を治そうとする
					setMessage(MessagePool.getMessage(this, MessagePool.Action.TreatSisterBySurisuri));
					break;
				case YOUNGSISTER: // 妹
					// 姉を治そうとする
					setMessage(MessagePool.getMessage(this, MessagePool.Action.TreatElderSisterBySurisuri));
					break;
				default: // 他人
					break;
			}
			setHappiness(Happiness.SAD);
			addStress(-50);
			p.addStress(-50);
		} else {
			// 相手によってセリフを変えるようにする処理。現在はセリフ制作時に作者の血管が切れそうなのでオミット
			/*
			 * switch( eRelation ){
			 * case FATHER: // 父
			 * case MOTHER: // 母
			 * // 子供とすりすり
			 * setMessage(MessagePool.getMessage(this,
			 * MessagePool.Action.surisuriWithChild));
			 * break;
			 * case PARTNAR: // つがい
			 * // つがいとすりすり
			 * setMessage(MessagePool.getMessage(this,
			 * MessagePool.Action.surisuriWithPartner));
			 * break;
			 * case CHILD_FATHER: // 父の子供
			 * // 父とすりすり
			 * setMessage(MessagePool.getMessage(this,
			 * MessagePool.Action.surisuriWithFather));
			 * break;
			 * case CHILD_MOTHER: // 母の子供
			 * // 母とすりすり
			 * setMessage(MessagePool.getMessage(this,
			 * MessagePool.Action.surisuriWithMother));
			 * break;
			 * case ELDERSISTER: // 姉
			 * // 妹とすりすり
			 * setMessage(MessagePool.getMessage(this,
			 * MessagePool.Action.surisuriWithElderSister));
			 * break;
			 * case YOUNGSISTER: // 妹
			 * // 姉とすりすり
			 * setMessage(MessagePool.getMessage(this,
			 * MessagePool.Action.surisuriWithSister));
			 * break;
			 * default : // 他人
			 * break;
			 * }
			 */
			setMessage(MessagePool.getMessage(this, MessagePool.Action.SuriSuri));
			addStress(-100);
			p.addStress(-100);
			setHappiness(Happiness.VERY_HAPPY);
			if (isNotNYD()) {
				p.setHappiness(Happiness.VERY_HAPPY);
			}
		}
		// 確率ですりすりしてる方にもアリ伝染る
		if (p.getAttachmentSize(Ants.class) > 0 && SimYukkuri.RND.nextInt(200) == 0) {
			if (getNumOfAnts() <= 0) {
				setNumOfAnts(0);
				addAttachment(new Ants(this));
				addStress(50);
				setHappiness(Happiness.VERY_SAD);
				addMemories(-1);
			}
			setNumOfAnts(getNumOfAnts() + 10);
		}
		setNobinobi(true);
		stay(40);
		p.stay(40);
		if (getIntelligence() != Intelligence.WISE && getSurisuriAccidentProb() != 0
				&& SimYukkuri.RND.nextInt(getSurisuriAccidentProb()) == 0) {
			// すりすり事故、すっきりーになる
			doSukkiri(p);
		}
		if (isSick() && SimYukkuri.RND.nextInt(5) == 0) {
			p.addSickPeriod(100);
		}
		if (p.isSick() && SimYukkuri.RND.nextInt(5) == 0) {
			addSickPeriod(100);
		}
	}

	/**
	 * ぺろぺろする.
	 * 
	 * @param p ぺろぺろ対象
	 */
	public void doPeropero(Body p) {
		if (isDead() || p.isDead()) {
			return;
		}
		if (isNobinobi() || isShutmouth()) {
			return;
		}
		if (!canAction()) {
			return;
		}
		if (isSleeping())
			return;

		// 自分との関係
		EnumRelationMine eRelation = BodyLogic.checkMyRelation(this, p);
		if (findSick(p) || p.isFeelHardPain() || p.isDamaged() || p.getAttachmentSize(Ants.class) != 0) {
			switch (eRelation) {
				case FATHER: // 父
				case MOTHER: // 母
					// 子供を治そうとする
					setMessage(MessagePool.getMessage(this, MessagePool.Action.TreatChildByPeropero));
					break;
				case PARTNAR: // つがい
					// つがいを治そうとする
					setMessage(MessagePool.getMessage(this, MessagePool.Action.TreatPartnerByPeropero));
					break;
				case CHILD_FATHER: // 父の子供
					// 父を治そうとする
					setMessage(MessagePool.getMessage(this, MessagePool.Action.TreatFatherBySurisuri));
					break;
				case CHILD_MOTHER: // 母の子供
					// 母を治そうとする
					setMessage(MessagePool.getMessage(this, MessagePool.Action.TreatMotherBySurisuri));
					break;
				case ELDERSISTER: // 姉
					// 妹を治そうとする
					setMessage(MessagePool.getMessage(this, MessagePool.Action.TreatSisterByPeropero));
					break;
				case YOUNGSISTER: // 妹
					// 姉を治そうとする
					setMessage(MessagePool.getMessage(this, MessagePool.Action.TreatElderSisterByPeropero));
					break;
				default: // 他人
					break;
			}
			setHappiness(Happiness.SAD);
			p.addMemories(1);
			p.addStress(-75);
		} else {
			// 相手によってセリフを変えるようにする処理。現在はセリフ制作時に作者の血管が切れそうなのでオミット
			/*
			 * switch( eRelation ){
			 * case FATHER: // 父
			 * case MOTHER: // 母
			 * // 子供とぺろぺろ
			 * setMessage(MessagePool.getMessage(this, MessagePool.Action.PeroPero));
			 * break;
			 * case PARTNAR: // つがい
			 * // つがいとぺろぺろ
			 * setMessage(MessagePool.getMessage(this, MessagePool.Action.PeroPero));
			 * break;
			 * case CHILD_FATHER: // 父の子供
			 * // 父とぺろぺろ
			 * setMessage(MessagePool.getMessage(this, MessagePool.Action.PeroperoFather));
			 * break;
			 * case CHILD_MOTHER: // 母の子供
			 * // 母とぺろぺろ
			 * setMessage(MessagePool.getMessage(this, MessagePool.Action.PeroperoMother));
			 * break;
			 * case ELDERSISTER: // 姉
			 * // 妹とぺろぺろ
			 * setMessage(MessagePool.getMessage(this, MessagePool.Action.PeroPero));
			 * break;
			 * case YOUNGSISTER: // 妹
			 * //姉 とぺろぺろ
			 * setMessage(MessagePool.getMessage(this, MessagePool.Action.PeroPero));
			 * break;
			 * default : // 他人
			 * break;
			 * }
			 */
			setMessage(MessagePool.getMessage(this, MessagePool.Action.PeroPero));

			setHappiness(Happiness.VERY_HAPPY);
			addStress(-50);
			p.setHappiness(Happiness.VERY_HAPPY);
			p.addMemories(1);
			p.addStress(-200);
		}
		// アリ減少
		int ant = p.getNumOfAnts();
		ant -= 40;
		if (ant <= 0) {
			ant = 0;
			p.removeAnts();
		}
		p.setNumOfAnts(ant);
		// しかし確率でぺろぺろしてる方にもアリ伝染る
		if (ant > 0 && SimYukkuri.RND.nextInt(200) == 0) {
			if (getNumOfAnts() <= 0) {
				addAttachment(new Ants(this));
				addStress(50);
				setHappiness(Happiness.VERY_SAD);
				addMemories(-1);
			}
			setNumOfAnts(getNumOfAnts() + 10);
		}

		setPeropero(true);
		stay(40);
		p.stay(40);
		p.addDamage(-10);
		if (p.getAttachmentSize(Ants.class) == 0) {
			substractNumOfAnts(10 * getBodyAgeState().ordinal() * getBodyAgeState().ordinal());
		}
		if (!p.isHasPants()) {
			p.makeDirty(false);
		}
		if (isSick() && SimYukkuri.RND.nextBoolean()) {
			p.addSickPeriod(100);
		}
		if (p.isSick() && SimYukkuri.RND.nextBoolean()) {
			addSickPeriod(100);
		}
	}

	/**
	 * 母が子供の針をぐーりぐーりする
	 * 
	 * @param p 子供
	 */
	public void doGuriguri(Body p) {
		if (isDead() || p.isDead()) {
			return;
		}

		if (!canAction()) {
			return;
		}

		// ぐーりぐーり時のメッセージ
		if (p.isAdult()) {
			// つがいが対象
			setMessage(MessagePool.getMessage(this, MessagePool.Action.ExtractingNeedlePartner));
		} else {
			// 子供が対象
			setMessage(MessagePool.getMessage(this, MessagePool.Action.ExtractingNeedleChild));
		}

		if (p.isNotNYD()) {
			// ぐーりぐーりされた時のメッセージ
			p.setMessage(MessagePool.getMessage(p, MessagePool.Action.NeedlePain), 60, true, false);
			p.stayPurupuru(40);
			p.setHappiness(Happiness.VERY_SAD);
			p.setForceFace(ImageCode.PAIN.ordinal());
		}
		p.addStress(80);
		stay(40);
		setHappiness(Happiness.VERY_SAD);
		addStress(30);
		setForceFace(ImageCode.CRYING.ordinal());
	}

	/**
	 * ゆっくりの向きを制御する
	 * 
	 * @param b        相手
	 * @param alignDir 向き
	 */
	public final void constraintDirection(Body b, boolean alignDir) {
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
		if (isDead()) {
			return;
		}
		if (getBlockedCount() != 0) {
			return;
		}
		destX = Math.max(0, Math.min(toX, Translate.getMapW()));
		destY = Math.max(0, Math.min(toY, Translate.getMapH()));
		destZ = Math.max(0, Math.min(toZ, Translate.getMapZ()));
	}

	/**
	 * 指定の座標まで動く
	 * 
	 * @param toZ Z座標
	 */
	public final void moveToZ(int toZ) {
		if (isDead()) {
			return;
		}
		destZ = Math.max(0, Math.min(toZ, Translate.getMapZ()));
	}

	public final void setTargetMoveOffset(int ox, int oy) {
		setTargetPosOfsX(ox);
		setTargetPosOfsY(oy);
	}

	/**
	 * ごはんの方に動く
	 * 
	 * @param target ターゲットのメシ
	 * @param type   メシの種類
	 * @param toX    X座標
	 * @param toY    Y座標
	 */
	public final void moveToFood(Obj target, Food.FoodType type, int toX, int toY) {
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
	public final void moveToFood(Obj target, Food.FoodType type, int toX, int toY, int toZ) {
		clearActions();
		purposeOfMoving = PurposeOfMoving.TAKEOUT;
		setToFood(true);
		setMoveTarget(target.objId);
		moveTo(toX, toY, toZ);
	}

	/**
	 * すっきりのために動く
	 * 
	 * @param target ターゲットのゆっくり
	 * @param toX    X座標
	 * @param toY    Y座標
	 */
	public final void moveToSukkiri(Obj target, int toX, int toY) {
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
	public final void moveToSukkiri(Obj target, int toX, int toY, int toZ) {
		clearActions();
		setToSukkiri(true);
		setMoveTarget(target.objId);
		moveTo(toX, toY, toZ);
	}

	/**
	 * トイレの方向に動く
	 * 
	 * @param target トイレ
	 * @param toX    X座標
	 * @param toY    Y座標
	 */
	public final void moveToToilet(Obj target, int toX, int toY) {
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
	public final void moveToToilet(Obj target, int toX, int toY, int toZ) {
		clearActions();
		setToShit(true);
		setMoveTarget(target.objId);
		moveTo(toX, toY, toZ);
	}

	/**
	 * ベッドの方に動く
	 * 
	 * @param target ベッド
	 * @param toX    X座標
	 * @param toY    Y座標
	 */
	public final void moveToBed(Obj target, int toX, int toY) {
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
	public final void moveToBed(Obj target, int toX, int toY, int toZ) {
		clearActions();
		setToBed(true);
		setMoveTarget(target.objId);
		moveTo(toX, toY, toZ);
	}

	/**
	 * なんかの方向に動く
	 * 
	 * @param target ターゲットのなんか
	 * @param toX    X座標
	 * @param toY    Y座標
	 */
	public final void moveToBody(Obj target, int toX, int toY) {
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
	public final void moveToBody(Obj target, int toX, int toY, int toZ) {
		clearActions();
		setToBody(true);
		setMoveTarget(target.objId);
		moveTo(toX, toY, toZ);
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
		setBodyAmount(getBodyAmount() - amount);
		if (isDead()) {
			// 死体食べ
			if (getBodyAmount() <= getDAMAGELIMITorg()[getBodyAgeState().ordinal()] / 2) {
				setCrushed(true);
				if (getBodyAmount() <= 0) {
					remove();
					setBodyAmount(0);
				}
			}
		} else {
			// 生きたまま食べられる
			addHungry(-amount);
			if (hungry <= 0) {
				addDamage(amount);
			}
			wakeup();
			if (getBodyAmount() <= getDAMAGELIMITorg()[getBodyAgeState().ordinal()] / 2) {
				bodyCut();
				if (getBodyAmount() <= 0) {
					toDead();
					setCrushed(true);
					setBodyAmount(1);
				}
			}
		}
		// 実ゆの場合、親が反応する
		checkReactionStalkMother(UnbirthBabyState.ATTAKED);
	}

	/**
	 * 他のゆっくりから食べられる
	 * 
	 * @param amount 食べられる量
	 * @param eater  食べてくるゆっくり
	 */
	public void eatBody(int amount, Body eater) {
		eatBody(amount);
		if (isDead())
			return;
		if (isUnBirth())
			return;
		Vomit v = SimYukkuri.mypane.getTerrarium().addVomit(getX(), getY(), getZ(), this, getShitType());
		v.crushVomit();
		if (isNotNYD()) {
			if (isSmart() || getBodyAgeState().ordinal() < eater.getBodyAgeState().ordinal() || isLockmove()
					|| isGotBurnedHeavily()) {
				// 善良か動けない状態か自分より大きい相手は逃げる
				setMessage(MessagePool.getMessage(this, MessagePool.Action.EatenByBody));
				setHappiness(Happiness.VERY_SAD);
				runAway(getX(), getY());
			} else {
				// 反撃
				setAngry();
				setMessage(MessagePool.getMessage(this, MessagePool.Action.EatenByBody));
				EventLogic.addBodyEvent(this, new RevengeAttackEvent(this, eater, null, 1), null, null);
			}
		}
	}

	/**
	 * ゆっくり以外から食べられる（現在はアリのみ）
	 * 
	 * @param amount 食われる量
	 * @param P      アリなら0
	 * @param AV     食べられた際に吐くかどうか
	 */
	public void beEaten(int amount, int P, boolean AV) {
		eatBody(amount);
		makeDirty(true);
		if (isDead())
			return;
		if (isUnBirth())
			return;
		if (AV) {
			Vomit v = SimYukkuri.mypane.getTerrarium().addVomit(getX(), getY(), getZ(), this, getShitType());
			v.crushVomit();
		}
		if (isNotNYD()) {
			// アリの場合の反応
			if (P == 0) {
				setHappiness(Happiness.VERY_SAD);
				if (SimYukkuri.RND.nextInt(4) == 0) {
					if (!isAdult() && SimYukkuri.RND.nextInt(4) == 0) {
						callParent();
					}
					if (SimYukkuri.RND.nextInt(3) == 0) {
						setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream));
						setForceFace(ImageCode.PAIN.ordinal());
					} else {
						setMessage(MessagePool.getMessage(this, MessagePool.Action.EatenByAnts));
					}
					if (isDamaged() || isLockmove() || isGotBurnedHeavily()) {
						// 動けない状態
						stayPurupuru(10);
					} else {
						// 反撃
						if (SimYukkuri.RND.nextInt(3) == 0) {
							switch (SimYukkuri.RND.nextInt(3)) {
								case 0:
									if (!isShutmouth()) {
										setPeropero(true);
										substractNumOfAnts(10);
									}
									break;
								case 1:
									setNobinobi(true);
									substractNumOfAnts(5);
									break;
								case 2:
									if (canFurifuri()) {
										setFurifuri(true);
										substractNumOfAnts(35);
									}
								default:
									// NOP.
							}
							stay();
							setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.RevengeAnts), true);
						}
					}
				}
			}
		}
	}

	/**
	 * 強制給餌
	 */
	public final void feed() {
		if (hungry <= getHungryLimit()) {
			setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.Eating), true);
			setHappiness(Happiness.HAPPY);
			addLovePlayer(30);
		} else {
			setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.Inflation), true);
			setHappiness(Happiness.VERY_SAD);
			addLovePlayer(-40);
		}
		eatFood(1500);
	}

	/**
	 * ダメージ追加
	 * 
	 * @param amount ダメージ量
	 */
	public final void addDamage(int amount) {
		if (isDead()) {
			return;
		}
		damage += amount;
	}

	/**
	 * 打撃を受ける
	 * 
	 * @param amount ダメージ量
	 */
	public final void strike(int amount) {
		if (isDead()) {
			return;
		}
		damage += amount;
		addStress(amount >> 2);
		setStaying(false);
		setStrike(true);
		stay();
		setDamageState(getDamageState());
		wakeup();
		// 背面固定でかつ針が刺さっていない場合
		if (isFixBack() && !isbNeedled()) {
			setFurifuri(true);
		}
	}

	/**
	 * お仕置き
	 */
	public void strikeByPunish() {
		if (isDead()) {
			return;
		}
		if (isIdiot()) {
			strike(Const.NEEDLE);
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream), true);
			setAngry();
			return;
		}

		// なつき度設定
		addLovePlayer(-10);
		// しつけ効果
		teachManner(1);
		// disclipline(2);
		strike(Const.NEEDLE);
		if (getCurrentEvent() instanceof ProposeEvent) {
			setForceFace(ImageCode.CRYING.ordinal());
			if (isDamaged())
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream), true);
			else
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Surprise), true);
		} else
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream), true);
		clearActions();
		setAngry();

		// 持ち物を全部落とす
		dropAllTakeoutItem();
		// dropOkazari();
		// 実ゆの場合、親が反応する
		checkReactionStalkMother(UnbirthBabyState.ATTAKED);

	}

	/**
	 * ハンマー
	 */
	public void strikeByHammer() {
		if (isDead()) {
			return;
		}
		// なつき度設定
		addLovePlayer(-200);
		strike(Const.HAMMER);
		setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.Scream), true);
		setAngry();
		if (isDead()) {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying), true);
			stay();
			if (getBodyAgeState() != AgeState.ADULT) {
				setCrushed(true);
			}
		}

		begForLife();

		// 持ち物を全部落とす
		dropAllTakeoutItem();

		// 実ゆの場合、親が反応する
		checkReactionStalkMother(UnbirthBabyState.ATTAKED);
	}

	/**
	 * 押さえつけ
	 */
	public void strikeByPress() {
		if (!isDead()) {
			strike(Const.HAMMER * 10);
			setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.Scream), true);
			setAngry();
		}
		if (isDead()) {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying), 40, true, true);
			stay();
			setCrushed(true);
		}
	}

	/**
	 * パンチ
	 */
	public void strikeByPunch() {
		if (isDead()) {
			return;
		}

		// なつき度設定
		addLovePlayer(-500);
		strike(getDAMAGELIMITorg()[getBodyAgeState().ordinal()] / 5);
		setCalm();

		setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.Scream), true);
		setAngry();
		if (isDead()) {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying), true);
			stay();
		}

		begForLife();
		// 持ち物を全部落とす
		dropAllTakeoutItem();
		// 実ゆの場合、親が反応する
		checkReactionStalkMother(UnbirthBabyState.ATTAKED);
	}

	/**
	 * ゆっくりから攻撃を受けた時の処理
	 * 
	 * @param enemy      攻撃してきたゆっくり
	 * @param e          イベント
	 * @param bAllowance 手加減ありの場合
	 */
	public void strikeByYukkuri(Body enemy, EventPacket e, boolean bAllowance) {
		if (isDead()) {
			return;
		}
		// 相手のベース攻撃力計算
		int ap = enemy.getStrength();
		// 状態によるダメージ変化
		if (enemy.isDamaged()) {
			ap *= 0.75f;
		}
		if (isMelt()) {
			ap *= 2.5f;
		} else if (isWet()) {
			ap *= 1.5f;
		}
		if (isHasPants()) {
			ap *= 0.8f;
		}
		if (isExciting()) {
			ap *= 0.25f;
		}
		if (isPredatorType() && !enemy.isPredatorType()) {
			ap *= 0.25f;
		}
		if (!isPredatorType() && enemy.isPredatorType()) {
			ap *= 2f;
		}
		// 吹っ飛び設定
		// 体重差
		int kickX = (enemy.getWeight() - getWeight()) / 100;
		int kickY = (enemy.getWeight() - getWeight()) / 500;
		if (kickX < 0)
			kickX = 0;
		if (kickY < 0)
			kickY = 0;
		kickX += 3;
		if (enemy.getDirection() == Direction.LEFT) {
			kickX = -kickX;
		}
		if (enemy.getY() >= getY()) {
			kickY = -kickY;
		}
		if (SimYukkuri.UNYO) {
			// ダメージが高いので補正
			if (ap > 0)
				ap = (int) (ap * 0.25f);
		}
		// 手加減あり
		if (bAllowance) {
			int nDamage = damage + ap;
			// 次の一撃でダメージが75%を超える場合
			if (getDAMAGELIMITorg()[getBodyAgeState().ordinal()] * 3 / 4 < nDamage) {
				ap = getDAMAGELIMITorg()[getBodyAgeState().ordinal()] * 4 / 5 - damage;
				if (ap < 0) {
					ap = 0;
				}
			}
		}

		// 実行
		// 持ち物を落とす
		dropAllTakeoutItem();
		strike(ap);
		// ぴこぴこ破壊
		if (!isBraidType() && isHasBraid() && 0 < getnBreakBraidRand()
				&& SimYukkuri.RND.nextInt(getnBreakBraidRand()) == 0) {
			setHasBraid(false);
		}
		setHappiness(Happiness.SAD);
		// 土に埋まっていないなら吹っ飛ぶ
		if (getBaryState() == BaryInUGState.NONE) {
			kick(kickX, kickY, -4);
		}

		// リアクション
		if (isDead()) {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying), true);
			stay();
			setCrushed(true);
		} else {
			if (SimYukkuri.UNYO) {
				// 0.25 多すぎる 0.01 少なすぎる 0.06 少なすぎる
				changeUnyo((int) (ap * 0.11f), 0, 0);
				enemy.changeUnyo(SimYukkuri.RND.nextInt(3), 0, 0);
			}
			if (isNotNYD() && !isUnBirth()) {
				if (e instanceof HateNoOkazariEvent) {
					// お飾りの迫害
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream), true);
					if (getPublicRank() != PublicRank.UnunSlave
							&& (isRude() || (getAttitude() == Attitude.AVERAGE && SimYukkuri.RND.nextBoolean()))) {
						setAngry();
						EventLogic.addBodyEvent(this, new RevengeAttackEvent(this, enemy, null, 1), null, null);
					}
				} else if (e instanceof PredatorsGameEvent) {
					// 自分が捕食種のおもちゃにされたとき
					// 逃げる
					runAway(enemy.getX(), enemy.getY());
					setPikoMessage(MessagePool.getMessage(this, MessagePool.Action.DontPlayMe), true);
					// おもちゃにされたとき、母がいたら33%の確率で「捕食種はあっちいってね！」イベントが発生。
					Body m = YukkuriUtil.getBodyInstance(getMother());
					if (m != null) {
						if (SimYukkuri.RND.nextInt(3) == 0 && m != null && !m.isDead() && !m.isRemoved()) {
							m.clearEvent();
							m.setAngry();
							m.setPanic(false, null);
							m.setPeropero(false);
							EventLogic.addBodyEvent(m, new KillPredeatorEvent(m, enemy, null, 10),
									null, null);
						}
					}
					if (SimYukkuri.RND.nextInt(10) == 0) {
						bodyInjure();
					}
				} else if (e instanceof RaperReactionEvent) {
					// 自分がレイパーで攻撃されたとき
					// 相手をレイプ対象に
					int colX = BodyLogic.calcCollisionX(this, enemy);
					moveToSukkiri(enemy, enemy.getX() + colX, enemy.getY());
					if (SimYukkuri.RND.nextInt(200) == 0) {
						bodyInjure();
					}
				} else if (e instanceof AvoidMoldEvent) {
					// 自分がかびてる時
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream), true);
					if (!isBaby() && !isSmart() && getIntelligence() == Intelligence.FOOL) {
						setAngry();
						EventLogic.addBodyEvent(this, new RevengeAttackEvent(this, enemy, null, 1), null, null);
					}
				} else {
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream), true);
					if (getAttitude() != Attitude.VERY_NICE) {
						setAngry();
						EventLogic.addBodyEvent(this, new RevengeAttackEvent(this, enemy, null, 1), null, null);
					}
				}
			}
		}
	}

	/**
	 * 攻撃力の基準
	 * 
	 * @return 攻撃力
	 */
	@Transient
	public final int getStrength() {
		return getSTRENGTHorg()[getBodyAgeState().ordinal()];
	}

	/**
	 * 何かで衝撃を加えられたとき
	 * 
	 * @param ap         基本ダメージ量
	 * @param weight     体重
	 * @param bAllowance 手加減あり
	 * @param vecX       X方向のベクトル
	 * @param vecY       Y方向のベクトル
	 */
	public void strikeByObject(int ap, int weight, boolean bAllowance, int vecX, int vecY) {
		if (isDead()) {
			return;
		}
		// 状態によるダメージ変化
		if (isMelt()) {
			ap *= 2.5f;
		} else if (isWet()) {
			ap *= 1.5f;
		}
		if (isHasPants()) {
			ap *= 0.8;
		}
		// 吹っ飛び設定
		// 体重差
		int kick = (weight - getWeight()) / 100;
		if (kick < 1)
			kick = 1;
		vecX *= kick;
		vecY *= kick;
		// 手加減あり
		if (bAllowance) {
			int nDamage = damage + ap;
			// 次の一撃でダメージが85%を超える場合
			if (getDAMAGELIMITorg()[getBodyAgeState().ordinal()] * 85 / 100 < nDamage) {
				ap = getDAMAGELIMITorg()[getBodyAgeState().ordinal()] * 85 / 100 - damage;
				if (ap < 0) {
					ap = 0;
				}
			}
		}
		strike(ap);
		// 土に埋まっていないなら吹っ飛ぶ
		if (getBaryState() == BaryInUGState.NONE) {
			kick(vecX, vecY, -5);
		}
		if (isDead()) {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying), true);
			stay();
			setCrushed(true);
		} else {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream), true);
		}
		// 持ち物を全部落とす
		dropAllTakeoutItem();
		// 実ゆの場合、親が反応する
		checkReactionStalkMother(UnbirthBabyState.ATTAKED);
	}

	/**
	 * 体の爆発
	 */
	public void bodyBurst() {
		if (!isCrushed()) {
			strike(Const.HAMMER * 30);
			toDead();
		}
		if (isDead() && getBaryState() != BaryInUGState.ALL) {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Dying), true);
			stay();
			setCrushed(true);
			for (int i = 0; i < (SimYukkuri.RND.nextInt(5) + 5); i++) {
				SimYukkuri.mypane.getTerrarium().addCrushedVomit(getX() + 7 - SimYukkuri.RND.nextInt(14),
						getY() + 7 - SimYukkuri.RND.nextInt(14),
						0, this, getShitType());
			}
		}
	}

	/**
	 * 体の切断
	 */
	public void bodyCut() {
		clearActions();
		setCriticalDamege(CriticalDamegeType.CUT);
		if (getBaryState() == BaryInUGState.NONE) {
			for (int i = 0; i < 5; i++) {
				SimYukkuri.mypane.getTerrarium().addVomit(getX() + 7 - SimYukkuri.RND.nextInt(14),
						getY() + 7 - SimYukkuri.RND.nextInt(14), 0,
						this, getShitType());
			}
		}
		// 実ゆの場合、親が反応する
		checkReactionStalkMother(UnbirthBabyState.ATTAKED);
	}

	/**
	 * 体のケガ
	 */
	public void bodyInjure() {
		clearActions();
		if (getCriticalDamege() == CriticalDamegeType.CUT)
			return;
		if (getCriticalDamege() == CriticalDamegeType.INJURED && SimYukkuri.RND.nextInt(50) == 0) {
			bodyCut();
			return;
		}
		setCalm();
		setForceFace(ImageCode.PAIN.ordinal());
		setHappiness(Happiness.VERY_SAD);
		setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream), 40, true, true);
		setCriticalDamege(CriticalDamegeType.INJURED);
		if (getBaryState() == BaryInUGState.NONE) {
			SimYukkuri.mypane.getTerrarium().addVomit(getX() + 7 - SimYukkuri.RND.nextInt(14),
					getY() + 7 - SimYukkuri.RND.nextInt(14), 0, this,
					getShitType());
		}
		// 実ゆの場合、親が反応する
		checkReactionStalkMother(UnbirthBabyState.ATTAKED);
	}

	/**
	 * キックされた
	 */
	public final void kick() {
		// 土に埋まっていないなら吹っ飛ぶ
		if (getBaryState() == BaryInUGState.NONE) {
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
		if (isDead() || isRemoved() || isUnBirth()) {
			return;
		}

		if (getOkazari() != null || isbNoticeNoOkazari()) {
			return;
		}

		// 起きてる
		if (!isSleeping()) {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.NoticeNoAccessory), true);
			setHappiness(Happiness.VERY_SAD);
			addStress(1200);
			setbNoticeNoOkazari(true);
		}
	}

	/**
	 * お飾りを取られる
	 * 
	 * @param bByPlayer プレイヤーに取られたかどうか
	 */
	public void takeOkazari(boolean bByPlayer) {
		setOkazari(null);
		if (isIdiot())
			return;
		if (!isDead()) {
			if (!isSleeping()) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.NoAccessory), true);
				setHappiness(Happiness.VERY_SAD);
				addStress(1200);
				if (bByPlayer) {
					// なつき度設定
					addLovePlayer(-100);
				}
				setbNoticeNoOkazari(true);
			} else {
				setbNoticeNoOkazari(false);
			}
		}
		// 実ゆの場合、親が反応する
		checkReactionStalkMother(UnbirthBabyState.SAD);
	}

	/**
	 * お飾りを落とす(未使用)
	 */
	public void dropOkazari() {
		if (getOkazari() != null) {
			getOkazari().setCalcX(x);
			getOkazari().setCalcY(y);
			getOkazari().setCalcZ(z + 10);
			SimYukkuri.world.getCurrentMap().getOkazari().put(getOkazari().objId, getOkazari());
			setOkazari(null);
		}
	}

	/**
	 * お飾りをあげたときの反応を記述する.
	 * たりないゆは別なのでオーバーライドする.
	 * 
	 * @param type お飾りのタイプ
	 */
	public void giveOkazari(OkazariType type) {
		setOkazari(new Okazari(this, type));
		setbNoticeNoOkazari(false);
		if (!isDead() && !isIdiot()) {
			if (getOkazari().getOkazariType() == OkazariType.DEFAULT) {
				setHappiness(Happiness.VERY_HAPPY);
				addStress(-1250);
				// なつき度設定
				addLovePlayer(10);
				// 実ゆの場合、親が反応する
				checkReactionStalkMother(UnbirthBabyState.HAPPY);
			} else {
				setHappiness(Happiness.SAD);
				addStress(-100);
				// なつき度設定
				addLovePlayer(10);
			}
		}
	}

	/**
	 * おくるみをあげる
	 */
	public void givePants() {
		setHasPants(true);
		if (canAction()) {
			if (!isDirty() && hasOkazari()) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.RelaxOkurumi), 30);
				setHappiness(Happiness.HAPPY);
				addStress(-250);
				// なつき度設定
				addLovePlayer(100);
				// 実ゆの場合、親が反応する
				checkReactionStalkMother(UnbirthBabyState.HAPPY);
			}
		}
	}

	/**
	 * ジュース
	 */
	public void giveJuice() {
		if (isDead()) {
			return;
		}
		if (!isCantDie() /* && !isTalking() */) {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Healing), Const.HOLDMESSAGE, true, true);
			// stay();
		}
		if (getCriticalDamegeType() == CriticalDamegeType.INJURED) {
			setCriticalDamege(null);
		}
		bodyBakePeriod = 0;
		damage = 0;
		setDamageState(getDamageState());
		hungry = getHungryLimit();
		bodyBakePeriod = 0;
		setAngry(false);
		setScare(false);
		setCalm();

		if (getAttachmentSize(Fire.class) != 0) {
			removeAttachment(Fire.class);
		}
		setHappiness(Happiness.VERY_HAPPY);
		setStress(0);
		addMemories(20);
		setForcePanicClear();
		if (getCurrentEvent() instanceof BegForLifeEvent) {
			// 空処理
		} else {
			clearActions();
		}
		// なつき度設定
		addLovePlayer(200);
		// 実ゆの場合、親が反応する
		checkReactionStalkMother(UnbirthBabyState.HAPPY);
	}

	/**
	 * ジュース注入
	 */
	public void injectJuice() {
		if (isDead()) {
			return;
		}
		// 反応
		if (isSleeping()) {
			wakeup();
		}
		if (!(getCurrentEvent() instanceof BegForLifeEvent)) {
			clearActions();
		}
		setForceFace(ImageCode.PAIN.ordinal());
		setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream), Const.HOLDMESSAGE, true, true);
		setHappiness(Happiness.VERY_SAD);
		setCalm();
		addStress(50);
		addMemories(-10);
		// 回復
		if (getCriticalDamegeType() == CriticalDamegeType.INJURED) {
			setCriticalDamege(null);
		}
		bodyBakePeriod = 0;
		damage = 0;
		setDamageState(getDamageState());
		hungry = getHungryLimit();
		// なつき度設定
		addLovePlayer(-50);
		// 実ゆの場合、親が反応する
		checkReactionStalkMother(UnbirthBabyState.ATTAKED);
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
			unyoForceH = 0;
			unyoForceW = 0;
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
		if (isDead()) {
			return;
		}
		analClose = flag;
		// 寝ていたらリアクションなし
		if (!canAction()) {
			return;
		}

		if (isAnalClose()) {
			// 閉鎖
			setHappiness(Happiness.HAPPY);
			setMessage(MessagePool.getMessage(this, MessagePool.Action.AnalSealed));
		} else {
			// 開放
			setHappiness(Happiness.AVERAGE);
			setMessage(MessagePool.getMessage(this, MessagePool.Action.ToFreedom));
			stay();
		}
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
		if (isDead()) {
			return;
		}
		stalkCastration = flag;
		// 寝ていたらリアクションなし
		if (!canAction()) {
			return;
		}
		if (isNotNYD()) {
			if (isStalkCastration()) {
				// 閉鎖
				setHappiness(Happiness.VERY_SAD);
				addStress(1000);
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Alarm));
				// なつき度設定
				addLovePlayer(-400);
				if (SimYukkuri.RND.nextBoolean())
					doYunnyaa(true);
			} else {
				// 開放
				setHappiness(Happiness.AVERAGE);
				setMessage(MessagePool.getMessage(this, MessagePool.Action.ToFreedom));
				// なつき度設定
				addLovePlayer(400);
			}
		}
	}

	/**
	 * 胎生去勢を設定する.
	 */
	public void castrateBody(boolean flag) {
		if (isDead()) {
			return;
		}
		bodyCastration = flag;
		// 寝ていたらリアクションなし
		if (!canAction()) {
			return;
		}
		if (isNotNYD()) {
			if (isBodyCastration()) {
				// 閉鎖
				setHappiness(Happiness.VERY_SAD);
				addStress(1000);
				// なつき度設定
				addLovePlayer(-400);
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Alarm));
				if (SimYukkuri.RND.nextBoolean())
					doYunnyaa(true);
			} else {
				// 開放
				setHappiness(Happiness.AVERAGE);
				// なつき度設定
				addLovePlayer(400);
				setMessage(MessagePool.getMessage(this, MessagePool.Action.ToFreedom));
			}
		}
	}

	/**
	 * 火をつける
	 */
	public void giveFire() {
		if (isBurned() || getAttachmentSize(Fire.class) != 0 || isCrushed()) {
			return;
		}

		clearActions();
		if (!isDead()) {
			// 寝てたら起きる
			if (isSleeping())
				wakeup();
			setCalm();
			setStaycount(0);
			setStaying(false);
			setToFood(false);
			setToSukkiri(false);
			setToShit(false);
			setShitting(false);
			setBirth(false);
			setAngry(false);
			if (!isFixBack()) {
				setFurifuri(false);
			}
			setEating(false);
			setPeropero(false);
			setSukkiri(false);
			setScare(false);
			setEatingShit(false);
			setBeVain(false);
			setNobinobi(false);
			setYunnyaa(false);
			setInOutTakeoutItem(false);
			setHappiness(Happiness.VERY_SAD);
			// なつき度設定
			addLovePlayer(-500);
			// 実ゆの場合、親が反応する
			checkReactionStalkMother(UnbirthBabyState.ATTAKED);
		}

		if (isNotNYD() && !isUnBirth()) {
			setPanicType(PanicType.BURN);
		}
		setWet(false);
		wetPeriod = 0;
		getAttach().add(new Fire(this));
	}

	/**
	 * 針を刺す（トグル
	 */
	public final void invNeedle() {
		setNeedle(!isbNeedled());
	}

	/**
	 * 針に刺さっているかどうかを取得（Shiftキーでの針に対応）.
	 * 
	 * @return 針に刺さっているかどうか
	 */
	public final boolean getNeedle() {
		return isbNeedled();
	}

	/**
	 * 針刺しを設定する.
	 * 
	 * @param bOn 針刺し
	 */
	public void setNeedle(boolean bOn) {
		if (getAttachmentSize(Needle.class) != 0) {
			// 針が刺さっている場合は抜く
			if (!bOn) {
				// 生まれていなくてもしゃべれるようにする
				if (isUnBirth()) {
					setCanTalk(false);
				}

				setbNeedled(false);
				setMessage(MessagePool.getMessage(this, MessagePool.Action.NeedleRemove));
				removeAttachment(Needle.class);

				// 粘着板で固定されていないなら背面固定解除
				Map<Integer, StickyPlate> stickyPlates = SimYukkuri.world.getCurrentMap().getStickyPlate();
				boolean bReset = true;
				for (Map.Entry<Integer, StickyPlate> entry : stickyPlates.entrySet()) {
					StickyPlate s = entry.getValue();
					if (s.getBindBody() == this) {
						bReset = false;
						break;
					}
				}
				if (bReset) {
					setFixBack(false);
				}
			}
		} else {
			// 針が刺さっていない場合は刺す
			if (bOn) {
				getAttach().add(new Needle(this));

				// 針を刺す際に判定するので刺した後で初期化する
				if (!isDead()) {
					// 生まれていなくてもしゃべれるようにする
					if (isUnBirth()) {
						setCanTalk(true);
					}

					// 寝てたら起きる
					if (isSleeping())
						wakeup();
					setCalm();
					setStaycount(0);
					setStaying(false);
					setToFood(false);
					setToSukkiri(false);
					setToShit(false);
					setShitting(false);
					setBirth(false);
					setAngry(false);
					setFurifuri(false);
					setBeVain(false);
					setEating(false);
					setPeropero(false);
					setSukkiri(false);
					setScare(false);
					setEatingShit(false);
					setNobinobi(false);
					setYunnyaa(false);
					setInOutTakeoutItem(false);
					// 飛行種なら墜落させる
					if (canflyCheck()) {
						moveToZ(0);
					}
					clearActions();
					// なつき度設定
					addLovePlayer(-20);
					setHappiness(Happiness.VERY_SAD);
					setMessage(MessagePool.getMessage(this, MessagePool.Action.NeedleStick), true);
					// 実ゆの場合、親が反応する
					checkReactionStalkMother(UnbirthBabyState.ATTAKED);
				}
				setbNeedled(true);
			}
		}
	}

	/**
	 * ゆ虐神拳を進捗させる（お前はもう死んでいる→ゆべら！）
	 */
	public void plusGodHand() {
		if (getAbFlagGodHand()[0]) {
			if (getBurstState() != Burst.NEAR) {
				// 爆発直前まで膨らませる
				setGodHandHoldPoint(getGodHandHoldPoint() + 1);
			}
		}
		if (getAbFlagGodHand()[1]) {
			// 伸ばす
			if (getGodHandStretchPoint() < Const.EXT_FORCE_PULL_LIMIT[getBodyAgeState().ordinal()]) {
				setGodHandStretchPoint(getGodHandStretchPoint() + 1);
			}
			lockSetZ(getGodHandStretchPoint());
		} else if (getAbFlagGodHand()[2]) {
			// 縮める
			if (Const.EXT_FORCE_PUSH_LIMIT[getBodyAgeState().ordinal()] < getGodHandCompressPoint()) {
				setGodHandCompressPoint(getGodHandCompressPoint() - 1);
			}
			lockSetZ(getGodHandCompressPoint());
		}
	}

	/**
	 * 水をかける
	 */
	public void giveWater() {
		if (!isDead() && !isUnBirth()) {
			// 寝てたら起きる
			if (isSleeping())
				wakeup();
			setCalm();
			setStaycount(0);
			setStaying(false);
			setToFood(false);
			setToSukkiri(false);
			setToShit(false);
			setShitting(false);
			setBirth(false);
			setAngry(false);
			if (!isFixBack())
				setFurifuri(false);
			setEating(false);
			setPeropero(false);
			setSukkiri(false);
			setScare(false);
			setEatingShit(false);
			setBeVain(false);
			setNobinobi(false);
			setYunnyaa(false);
			setInOutTakeoutItem(false);
			// 水が平気なら幸福度アップ
			if (isLikeWater()) {
				if (getPanicType() != PanicType.BURN) {
					setHappiness(Happiness.HAPPY);
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Cleaned));
				} else {
					setHappiness(Happiness.VERY_SAD);
				}
			} else {
				setHappiness(Happiness.VERY_SAD);
				// なつき度設定
				addLovePlayer(-100);
				if (getPanicType() != PanicType.BURN) {
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Wet), true);
					// 実ゆの場合、親が反応する
					checkReactionStalkMother(UnbirthBabyState.SAD);
				}
			}
		}
		setWet(true);
		wetPeriod = 0;
		if (getAttachmentSize(Fire.class) != 0) {
			removeAttachment(Fire.class);
		}
		setForcePanicClear();
	}

	/**
	 * 水の中にいる
	 * 
	 * @param eDepth 深さ
	 */
	public void inWater(Pool.DEPTH eDepth) {
		if (!isDead() && !isUnBirth()) {
			// 寝てたら起きる
			if (isSleeping())
				wakeup();
			setCalm();
			setStaycount(0);
			setStaying(false);
			setToFood(false);
			setToSukkiri(false);
			setToShit(false);
			setShitting(false);
			setBirth(false);
			setAngry(false);
			if (!isFixBack())
				setFurifuri(false);
			setEating(false);
			setPeropero(false);
			setSukkiri(false);
			setScare(false);
			setEatingShit(false);
			setBeVain(false);
			setNobinobi(false);
			setYunnyaa(false);
			setInOutTakeoutItem(false);

			// 水が平気なら幸福度アップ
			if (isLikeWater()) {
				if (getPanicType() != PanicType.BURN) {
					setHappiness(Happiness.HAPPY);
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Cleaned));
				} else {
					setHappiness(Happiness.VERY_SAD);
				}
			} else {
				setHappiness(Happiness.VERY_SAD);
				if (getPanicType() != PanicType.BURN) {
					switch (eDepth) {
						case SHALLOW:
							setMessage(MessagePool.getMessage(this, MessagePool.Action.WetInShallowWater), true);
							break;
						case DEEP:
							setMessage(MessagePool.getMessage(this, MessagePool.Action.WetInDeepwWater), true);
							break;
						default:
							break;
					}
					// 実ゆの場合、親が反応する
					checkReactionStalkMother(UnbirthBabyState.SAD);
				}
			}
		}
		// if( wet )
		// {
		// melt = true;
		// }
		if (getAttachmentSize(Fire.class) != 0) {
			removeAttachment(Fire.class);
		}
		setWet(true);
		wetPeriod = 0;
	}

	/**
	 * 土に埋める
	 */
	public void baryInUnderGround() {
		// 接地してるか
		if (0 < z) {
			return;
		}

		// 畑にいるか
		int nX = getX();
		int nY = getY();
		if ((Translate.getCurrentFieldMapNum(nX, nY) & FieldShapeBase.FIELD_FARM) == 0) {
			return;
		}

		int nH = getCollisionY();
		setLockmove(true);

		// 現在の深さチェック
		switch (getBaryState()) {
			case NONE:
				setBaryState(BaryInUGState.HALF);
				setMostDepth(-nH / 16);
				setCalcZ(-nH / 16);
				break;
			case HALF:
				setBaryState(BaryInUGState.NEARLY_ALL);
				setMostDepth(-nH / 8);
				setCalcZ(-nH / 8);
				break;
			case NEARLY_ALL:
				setBaryState(BaryInUGState.ALL);
				setMostDepth(-nH / 3);
				setCalcZ(-nH / 3);
				break;
			case ALL:
				break;
			default:
				break;
		}
		begForLife();
	}

	/**
	 * 環境によるパニック状態の設定
	 * 
	 * @param flag  すでにパニック状態か
	 * @param pType パニックのタイプ
	 */
	public void setPanic(boolean flag, PanicType pType) {
		if (isDead() || isSleeping() || isUnBirth())
			return;
		// 足りないゆは不動
		if (isIdiot())
			return;
		// 発情レイパーにはパニック無効 燃えようがれみりゃがいようがれいぷっぷする
		if (isRaper() && isExciting()) {
			setForcePanicClear();
			return;
		}
		if (flag) {
			// 既にパニック状態の場合はカウンタのリセットのみ
			if (getPanicType() != null) {
				panicPeriod = 0;
				return;
			}
			setPanicType(pType);
			panicPeriod = 0;
			setStaycount(0);
			setCalm();
			setStaying(false);
			setToFood(false);
			setToSukkiri(false);
			setToShit(false);
			setShitting(false);
			setBirth(false);
			setAngry(false);
			if (!isFixBack()) {
				setFurifuri(false);
			} else {
				if (!isSleeping() && isbNeedled() && SimYukkuri.RND.nextInt(10) == 0) {
					setFurifuri(true);
				}
			}
			setEating(false);
			setPeropero(false);
			setSukkiri(false);
			setScare(false);
			setEatingShit(false);
			setBeVain(false);
			setNobinobi(false);
			setYunnyaa(false);
			setInOutTakeoutItem(false);
			setHappiness(Happiness.VERY_SAD);
		} else {
			setPanicType(null);
			panicPeriod = 0;
			setHappiness(Happiness.SAD);
		}
	}

	/**
	 * 声掛け
	 * 
	 * @param type 声掛けタイプ（0:ゆっくりしていってね 1:ゆっくりしないで死んでね 2:もるんもるんしてね）
	 */
	public void voiceReaction(int type) {
		if (getPanicType() != null || isDead()) {
			return;
		}
		if (!canAction()) {
			return;
		}
		switch (type) {
			case 0: {
				// ゆっくりしていってね
				clearActions();
				setScare(false);
				setAngry(false);
				setFurifuri(false);
				if (!isRaper())
					setExciting(false);
				setbForceExciting(false);
				setNobinobi(false);
				setYunnyaa(false);
				excitingPeriod = 0;
				setRelax(true);
				setMessage(MessagePool.getMessage(this, MessagePool.Action.TakeItEasy));
				addStress(-100);
				wakeup();
				// なつき度設定
				addLovePlayer(100);
				break;
			}
			case 1: {
				// ゆっくりしないでしんでね
				wakeup();
				clearActions();
				setAngry();
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Alarm));
				addStress(150);
				// なつき度設定
				addLovePlayer(-100);
				if (SimYukkuri.RND.nextBoolean())
					doYunnyaa(true);
				break;
			}
			case 2: {
				// もるんもるん
				wakeup();
				clearActions();
				if (isNeedled() || (isGotBurnedHeavily()) && getFootBakeLevel() != FootBake.CRITICAL) {
					if (isDamaged())
						setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream2), 30);
					else
						setMessage(MessagePool.getMessage(this, MessagePool.Action.Scream), 30);
					setHappiness(Happiness.VERY_SAD);
					setForceFace(ImageCode.PAIN.ordinal());
					setFurifuri(false);
					addStress(50);
				} else if (getFootBakeLevel() == FootBake.CRITICAL) {
					setMessage(MessagePool.getMessage(this, MessagePool.Action.CantMove), 30);
					setHappiness(Happiness.VERY_SAD);
					setFurifuri(false);
					addStress(50);
				} else {
					setMessage(MessagePool.getMessage(this, MessagePool.Action.FuriFuri), 30);
					setFurifuri(true);
					addStress(-50);
				}
				stay(30);
				break;
			}
			default:
				break;
		}
	}

	/**
	 * 持つ
	 */
	public void Hold() {
		if (isDead())
			return;
		if (isPullAndPush()) {
			setPullAndPush(false);
			setLockmove(false);
			return;
		}
		// なつき度設定
		if (getZ() > 0)
			setCalcZ(0);
		Terrarium.setAlarm();
		setPullAndPush(true);
		setLockmove(true);
		setHappiness(Happiness.SAD);
		setMessage(MessagePool.getMessage(this, MessagePool.Action.Press));
		// 実ゆの場合、親が反応する
		// checkReactionStalkMother(UnbirthBabyState.SAD);
	}

	/**
	 * 押さえつけ
	 * 
	 * @param force 強制かどうか
	 */
	public void lockSetZ(int force) {
		extForce = force;
		if (extForce == 0) {
			return;
		}
		if (isDead()) {
			return;
		}
		clearActions();
		setAngry();
		if (extForce < 0) {
			// つぶれ
			if (extForce < Const.EXT_FORCE_PUSH_LIMIT[getBodyAgeState().ordinal()]) {
				// 圧死
				setLockmove(false);
				extForce = 0;
				setCalcZ(0);
				bodyBurst();
			} else if (extForce < (Const.EXT_FORCE_PUSH_LIMIT[getBodyAgeState().ordinal()] >> 1)) {
				// 限界
				if (SimYukkuri.RND.nextInt(10) == 0) {
					setHappiness(Happiness.VERY_SAD);
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Press2), Const.HOLDMESSAGE, true, true);
					addStress(25);
				}
				if (SimYukkuri.RND.nextInt(80) == 0) {
					// あんこを吐き出す
					int ofsX = Translate.invertX(getCollisionX() >> 1, y);
					if (getDirection() == Direction.LEFT)
						ofsX = -ofsX;
					SimYukkuri.mypane.getTerrarium().addVomit(getX() + ofsX, getY() + 2, getZ(), this, getShitType());
					damage += Const.HAMMER / 2;
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Vomit), 30);
				}
			} else {
				if (SimYukkuri.RND.nextInt(10) == 0) {
					setHappiness(Happiness.AVERAGE);
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Press));
				}
			}
		} else if (extForce > 0) {
			// ひっぱり
			if (extForce > Const.EXT_FORCE_PULL_LIMIT[getBodyAgeState().ordinal()]) {
				// ちぎれ
				extForce = 0;
				setLockmove(false);
				bodyCut();
			} else if (extForce > Const.EXT_FORCE_PULL_LIMIT[getBodyAgeState().ordinal()] >> 1) {
				// 限界
				if (SimYukkuri.RND.nextInt(10) == 0) {
					setHappiness(Happiness.VERY_SAD);
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Pull2), Const.HOLDMESSAGE, true, true);
					addStress(20);
				}
			} else {
				if (SimYukkuri.RND.nextInt(10) == 0) {
					setHappiness(Happiness.AVERAGE);
					setMessage(MessagePool.getMessage(this, MessagePool.Action.Pull));
				}
			}
		}
	}

	/**
	 * 押さえつけを放す
	 */
	public void releaseLockNobinobi() {
		if (extForce == 0)
			return;
		if (extForce < 0) {
			extForce = 0;
		} else if (extForce * 2 / 3 < getSize()) {
			strike(extForce * 100 * 24 / getSize());
			extForce = 0;
		} else {
			strikeByHammer();
			makeDirty(true);
			extForce = 0;
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
		if (!canAction() || isExciting() || isAngry() || isUnBirth()) {
			return;
		}
		int toX, toY;
		if (x > fromX) {
			toX = Translate.getMapW();
		} else {
			toX = 0;
		}
		if (y > fromY) {
			toY = Translate.getMapH();
		} else {
			toY = 0;
		}
		moveTo(toX, toY);
		clearActions();
		setScare(true);
	}
	// ------------------------------------------

	// --------------------------------------------------
	@Override
	public void remove() {
		synchronized (SimYukkuri.lock) {
			setRemoved(true);
			int[] is = { -1, -1 };
			setParents(is);
			Body pa = YukkuriUtil.getBodyInstance(getPartner());
			if (pa != null)
				pa.setPartner(-1);
			setPartner(-1);
			removeAllStalks();
			setStalks(null);
			if (SimYukkuri.world.getCurrentMap().getBody().containsKey(this.getUniqueID())) {
				SimYukkuri.world.getCurrentMap().getBody().remove(this.getUniqueID());
			}
			getChildrenList().clear();
			getElderSisterList().clear();
			getSisterList().clear();
			List<Body> bodies = new LinkedList<Body>(SimYukkuri.world.getCurrentMap().getBody().values());
			for (Body b : bodies) {
				if (b.getChildrenList() != null) {
					YukkuriUtil.removeContent(b.getChildrenList(), getUniqueID());
				}
				if (b.getElderSisterList() != null) {
					YukkuriUtil.removeContent(b.getElderSisterList(), getUniqueID());
				}
				if (b.getSisterList() != null) {
					YukkuriUtil.removeContent(b.getSisterList(), getUniqueID());
				}
			}
			getAttach().clear();
			setOkazari(null);
			getBabyTypes().clear();
			getStalkBabyTypes().clear();
			getAncestorList().clear();
			setLinkParent(-1);
			setMoveTarget(-1);
			getEventList().clear();
			setCurrentEvent(null);
			getFavItem().clear();
			getTakeoutItem().clear();
		}
	}

	/**
	 * 親子関係をなくす
	 */
	public void clearRelation() {
		if (YukkuriUtil.getBodyInstance(getParents()[Parent.PAPA.ordinal()]) != null)
			if (YukkuriUtil.getBodyInstance(getParents()[Parent.PAPA.ordinal()]).isRemoved())
				getParents()[Parent.PAPA.ordinal()] = -1;
		if (YukkuriUtil.getBodyInstance(getParents()[Parent.MAMA.ordinal()]) != null)
			if (YukkuriUtil.getBodyInstance(getParents()[Parent.MAMA.ordinal()]).isRemoved())
				getParents()[Parent.MAMA.ordinal()] = -1;
		if (getPartner() != -1) {
			Body partnerCandidate = YukkuriUtil.getBodyInstance(getPartner());
			if (partnerCandidate == null || partnerCandidate.isRemoved())
				setPartner(-1);
		}
	}

	/**
	 * 行動・イベントの取り消し
	 */
	public void clearActions() {
		setToSukkiri(false);
		setToBed(false);
		setToFood(false);
		setToShit(false);
		setToBody(false);
		setToSteal(false);
		if (getCurrentEvent() != null) {
			getCurrentEvent().end(this);
		}
		setCurrentEvent(null);

		setMoveTarget(-1);
		setForceFace(-1);
		setDropShadow(true);
		setTargetPosOfsX(0);
		setTargetPosOfsY(0);
		setTargetBind(false);
		stopPlaying();
		setOfsXY(0, 0);

	}

	/**
	 * イベントをクリアする.
	 */
	public void clearEvent() {
		if (getCurrentEvent() != null) {
			getCurrentEvent().end(this);
		}
		setCurrentEvent(null);
		setForceFace(-1);
		setDropShadow(true);
		stopPlaying();
	}

	/**
	 * 妊娠期間を早める
	 */
	public void rapidPregnantPeriod() {
		if (hasBabyOrStalk()) {
			pregnantPeriodBoost += TICK;
		}
	}

	/**
	 * うんうんを素早く貯めさせる
	 */
	public void rapidShit() {
		shitBoost += TICK * 5;
	}

	/**
	 * （死んだときとかに）茎とゆっくりのバインドを解く.
	 * 茎をゲームから取り除くわけではなく、何らかの形で残したい場合に使用する.
	 * 茎を完全に取り除きたい場合はremoveAllStalks()を使用する.
	 */
	public void disPlantStalks() {
		if (getStalks() != null) {
			for (Stalk s : getStalks()) {
				if (s != null) {
					s.setPlantYukkuri(null);
				}
			}
			getStalks().clear();
		}
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
		int direction = this.getDirection().ordinal();
		int idx = 0;

		// 正面かそうでないか
		layer.getOption()[0] = 0;
		//
		layer.getOption()[1] = 0;
		layer.getOption()[2] = 0;

		if (isBurned() && isDead()) {
			// 焼死体
			idx += getImage(ImageCode.BURNED.ordinal(), Const.LEFT, layer, idx);
		} else if (isCrushed()) {
			// 潰れた死体
			if (isBurned()) {
				idx += getImage(ImageCode.BURNED2.ordinal(), Const.LEFT, layer, idx);
			} else {
				if (isPealed()) {
					idx += getImage(ImageCode.CRUSHED3.ordinal(), Const.LEFT, layer, idx);
				} else if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
					idx += getImage(ImageCode.CRUSHED.ordinal(), Const.LEFT, layer, idx);
				} else {
					idx += getImage(ImageCode.CRUSHED2.ordinal(), Const.LEFT, layer, idx);
				}
			}
		} else if (isPacked()) {
			if (isDead()) {
				idx += getImage(ImageCode.PACKED_DEAD.ordinal(), Const.LEFT, layer, idx);
			} else if (getAge() % 6 <= 2) {
				idx += getImage(ImageCode.PACKED1.ordinal(), Const.LEFT, layer, idx);
			} else {
				idx += getImage(ImageCode.PACKED2.ordinal(), Const.LEFT, layer, idx);
			}
		} else if (isShitting() || isBirth() && getBabyTypes().size() > 0 || (isFixBack() && !isFurifuri())) {
			// 排泄、出産時
			idx += getImage(ImageCode.FRONT_SHIT.ordinal(), Const.LEFT, layer, idx);
			if (geteHairState() == HairState.DEFAULT) {
				idx += getImage(ImageCode.FRONT_HAIR.ordinal(), Const.LEFT, layer, idx);
			} else if (geteHairState() == HairState.BRINDLED1 || geteHairState() == HairState.BRINDLED2) {
				idx += getImage(ImageCode.FRONT_HAIR2.ordinal(), Const.LEFT, layer, idx);
			}
			if (isAnalClose()) {
				idx += getImage(ImageCode.FRONT_SEALED.ordinal(), Const.LEFT, layer, idx);
			}
			if (getCriticalDamege() == CriticalDamegeType.INJURED) {
				idx += getImage(ImageCode.FRONT_INJURED.ordinal(), Const.LEFT, layer, idx);
			}
			if (isBlind()) {
				idx += getImage(ImageCode.FRONT_BLIND.ordinal(), Const.LEFT, layer, idx);
			}
			if (isHasPants()) {
				idx += getImage(ImageCode.FRONT_PANTS.ordinal(), Const.LEFT, layer, idx);
			}
			if (isHasBraid()) {
				idx += getImage(ImageCode.FRONT_BRAID.ordinal(), Const.LEFT, layer, idx);
			}
			if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
				idx += getImage(ImageCode.ROLL_ACCESSORY.ordinal(), Const.LEFT, layer, idx);
			}
		} else if (isFurifuri() && !isSleeping() && (!isLockmove() || isFixBack())) {
			// ふりふり
			if (getAge() % 8 <= 3) {
				idx += getImage(ImageCode.ROLL_LEFT_SHIT.ordinal(), Const.LEFT, layer, idx);
				if (geteHairState() == HairState.DEFAULT) {
					idx += getImage(ImageCode.ROLL_LEFT_HAIR.ordinal(), Const.LEFT, layer, idx);
				} else if (geteHairState() == HairState.BRINDLED1 || geteHairState() == HairState.BRINDLED2) {
					idx += getImage(ImageCode.FRONT_HAIR2.ordinal(), Const.LEFT, layer, idx);
				}
				if (isAnalClose()) {
					idx += getImage(ImageCode.ROLL_LEFT_SEALED.ordinal(), Const.LEFT, layer, idx);
				}
				if (getCriticalDamege() == CriticalDamegeType.INJURED) {
					idx += getImage(ImageCode.ROLL_LEFT_INJURED.ordinal(), Const.LEFT, layer, idx);
				}
				if (isBlind()) {
					idx += getImage(ImageCode.ROLL_LEFT_BLIND.ordinal(), Const.LEFT, layer, idx);
				}
				if (isHasPants()) {
					idx += getImage(ImageCode.ROLL_LEFT_PANTS.ordinal(), Const.LEFT, layer, idx);
				}
				if (isHasBraid()) {
					idx += getImage(ImageCode.ROLL_LEFT_BRAID.ordinal(), Const.LEFT, layer, idx);
				}
			} else if (getAge() % 8 <= 7) {
				idx += getImage(ImageCode.ROLL_RIGHT_SHIT.ordinal(), Const.LEFT, layer, idx);
				if (geteHairState() == HairState.DEFAULT) {
					idx += getImage(ImageCode.ROLL_RIGHT_HAIR.ordinal(), Const.LEFT, layer, idx);
				} else if (geteHairState() == HairState.BRINDLED1 || geteHairState() == HairState.BRINDLED2) {
					idx += getImage(ImageCode.FRONT_HAIR2.ordinal(), Const.LEFT, layer, idx);
				}
				if (isAnalClose()) {
					idx += getImage(ImageCode.ROLL_RIGHT_SEALED.ordinal(), Const.LEFT, layer, idx);
				}
				if (getCriticalDamege() == CriticalDamegeType.INJURED) {
					idx += getImage(ImageCode.ROLL_RIGHT_INJURED.ordinal(), Const.LEFT, layer, idx);
				}
				if (isBlind()) {
					idx += getImage(ImageCode.ROLL_RIGHT_BLIND.ordinal(), Const.LEFT, layer, idx);
				}
				if (isHasPants()) {
					idx += getImage(ImageCode.ROLL_RIGHT_PANTS.ordinal(), Const.LEFT, layer, idx);
				}
				if (isHasBraid()) {
					idx += getImage(ImageCode.ROLL_RIGHT_BRAID.ordinal(), Const.LEFT, layer, idx);
				}
			}
			if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
				idx += getImage(ImageCode.ROLL_ACCESSORY.ordinal(), Const.LEFT, layer, idx);
			}
		}

		else {
			// 皮むき時
			if (isPealed()) {
				idx += getImage(ImageCode.PEALED.ordinal(), direction, layer, idx);
			}
			// 通常時
			else {
				idx += getImage(ImageCode.BODY.ordinal(), direction, layer, idx);
			}
			layer.getOption()[0] = 1;
		}
		return idx;
	}

	/**
	 * 切断等の通常ではないボディイメージ
	 * 
	 * @param layer レイヤ
	 * @return index
	 */
	public int getAbnormalBodyImage(BodyLayer layer) {
		int direction = this.getDirection().ordinal();
		int idx = 0;
		// 切断
		if (getCriticalDamege() != null) {
			if (getCriticalDamege() == CriticalDamegeType.CUT) {
				idx += getImage(ImageCode.BODY_CUT.ordinal(), direction, layer, idx);
			} else
				idx += getImage(ImageCode.BODY_INJURED.ordinal(), direction, layer, idx);
		}
		// 溶解
		if (isMelt()) {

			if (isPealed()) {
				idx += getImage(ImageCode.MELT_PEALED.ordinal(), direction, layer, idx);
			} else {
				idx += getImage(ImageCode.MELT.ordinal(), direction, layer, idx);
			}
		}
		return idx;
	}

	/**
	 * おかざりグラフィックを返す。
	 * 
	 * @param layer レイヤ
	 * @param type  0だと前方、1だと後方の分を返す
	 * @return index
	 */
	public int getOlazariImage(BodyLayer layer, int type) {
		int direction = this.getDirection().ordinal();
		int idx = 0;
		if (getOkazari() == null) {
			layer.getImage()[idx] = null;
			idx++;
		} else {
			if (type == 0) {
				if (getOkazari().getOkazariType() == OkazariType.DEFAULT) {
					idx += getImage(ImageCode.ACCESSORY.ordinal(), direction, layer, idx);
				}
				// ゴミおかざり
				else {
					layer.getImage()[idx] = Okazari.getOkazariImage(getOkazari().getOkazariType(), direction);
					idx++;
				}
			} else {
				idx += getImage(ImageCode.ACCESSORY_BACK.ordinal(), direction, layer, idx);
			}
		}
		return idx;
	}

	/**
	 * 汚れなどの体表エフェクトグラフィックを返す
	 * 
	 * @param layer レイヤ
	 * @return index
	 */
	public int getEffectImage(BodyLayer layer) {
		int direction = this.getDirection().ordinal();
		int idx = 0;
		// layer.option[0] = 0;
		// 死亡
		if (isDead())
			idx += getImage(ImageCode.DEAD_BODY.ordinal(), direction, layer, idx);

		// 空腹
		if (isTooHungry()) {
			idx += getImage(ImageCode.HUNGRY2.ordinal(), direction, layer, idx);
		} else if (isVeryHungry()) {
			idx += getImage(ImageCode.HUNGRY1.ordinal(), direction, layer, idx);
		} else if (isSoHungry()) {
			idx += getImage(ImageCode.HUNGRY0.ordinal(), direction, layer, idx);
		}

		// 足焼き
		FootBake f = getFootBakeLevel();
		if (f == FootBake.MIDIUM) {
			idx += getImage(ImageCode.FOOT_BAKE0.ordinal(), direction, layer, idx);
		} else if (f == FootBake.CRITICAL) {
			idx += getImage(ImageCode.FOOT_BAKE1.ordinal(), direction, layer, idx);
		}
		// 体の焦げ
		BodyBake b = getBodyBakeLevel();
		if (b == BodyBake.MIDIUM) {
			idx += getImage(ImageCode.BODY_BAKE0.ordinal(), direction, layer, idx);
		} else if (b == BodyBake.CRITICAL) {
			idx += getImage(ImageCode.BODY_BAKE1.ordinal(), direction, layer, idx);
		}
		// ダメージ
		if (isPealed())
			;
		else if (isDamagedHeavily()) {
			idx += getImage(ImageCode.DAMAGED2.ordinal(), direction, layer, idx);
		} else if (isDamaged()) {
			idx += getImage(ImageCode.DAMAGED1.ordinal(), direction, layer, idx);
		} else if (isDamagedLightly()) {
			idx += getImage(ImageCode.DAMAGED0.ordinal(), direction, layer, idx);
		} else if (isOld()) {
			idx += getImage(ImageCode.DAMAGED1.ordinal(), direction, layer, idx);
		} else if (getBodyRank() == BodyRank.NORAYU || getBodyRank() == BodyRank.YASEIYU) {
			// 野良ゆ&野生ゆの場合ダメージ表示(暫定対応)
			idx += getImage(ImageCode.DAMAGED0.ordinal(), direction, layer, idx);
		}

		// おくるみ
		if (isHasPants()) {
			idx += getImage(ImageCode.PANTS.ordinal(), direction, layer, idx);
		}
		// 足汚れ
		if (isNormalDirty()) {
			idx += getImage(ImageCode.STAIN.ordinal(), direction, layer, idx);
		}
		if (isStubbornlyDirty()) {
			idx += getImage(ImageCode.STAIN2.ordinal(), direction, layer, idx);
		}
		// かび
		if (sickPeriod > (INCUBATIONPERIODorg << 5)) {
			idx += getImage(ImageCode.SICK3.ordinal(), direction, layer, idx);
		} else if (sickPeriod > (INCUBATIONPERIODorg << 3)) {
			idx += getImage(ImageCode.SICK2.ordinal(), direction, layer, idx);
		} else if (sickPeriod > INCUBATIONPERIODorg) {
			idx += getImage(ImageCode.SICK1.ordinal(), direction, layer, idx);
		} else if (isSick()) {
			idx += getImage(ImageCode.SICK0.ordinal(), direction, layer, idx);
		}
		// 濡れ
		if (isWet()) {
			idx += getImage(ImageCode.WET.ordinal(), direction, layer, idx);
		}
		return idx;
	}

	/**
	 * 顔グラフィックを返す
	 * 
	 * @param layer レイヤ
	 * @return index
	 */
	public int getFaceImage(BodyLayer layer) {
		int direction = this.getDirection().ordinal();
		int idx = 0;

		// 跳ねない
		layer.getOption()[0] = 0;
		// optionは移動関係の設定
		if (isFlyingType()) {
			if (!isGrabbed() && !isSleeping() && !isPurupuru()) {
				if (isExciting()) {
					layer.getOption()[0] = 1; // 大ジャンプ
				} else if (isSukkiri()) {
					layer.getOption()[0] = 2; // すっきり
				} else if (isNobinobi()) {
					layer.getOption()[0] = 4; // のびのび
				} else if (isYunnyaa() || isBeggingForLife()) {
					layer.getOption()[0] = 5; // ゆんやあ&命乞い
				} else if (!isLockmove() && canflyCheck() && !isDontJump()) {
					layer.getOption()[0] = 3; // 跳ねて移動
				}
			}
		} else {
			if (!isGrabbed() && getZ() == 0 && !isSleeping() && !isPurupuru()) {
				if (isExciting() && !isDontJump() && !isbNeedled()) {
					layer.getOption()[0] = 1; // 大ジャンプ
				} else if (isSukkiri()) {
					layer.getOption()[0] = 2; // すっきり
				} else if (isNobinobi()) {
					layer.getOption()[0] = 4; // のびのび
				} else if (isYunnyaa() || isBeggingForLife()) {
					layer.getOption()[0] = 5; // ゆんやあ&命乞い
				} else if (!isLockmove() && !isDontJump()
						&& takeMappedObj(getLinkParent()) == null && !isPeropero() && !(isEating() && !isPikopiko())) {
					layer.getOption()[0] = 3; // 跳ねて移動
				}
			}
		}

		// 非ゆっくり症の場合
		if (isNYD()) {
			// 跳ねない
			layer.getOption()[0] = 0;
		}

		// 表情固定
		if (getForceFace() != -1) {
			idx += getImage(getForceFace(), direction, layer, idx);
			// 口封じグラの追加
			if (isShutmouth()) {
				idx += getImage(ImageCode.SHUTMOUTH.ordinal(), direction, layer, idx);
			}
			// 盲目グラの追加
			if (isBlind()) {
				idx += getImage(ImageCode.BLIND.ordinal(), direction, layer, idx);
			}
			// 舌の追加
			if (isPeropero() || isInOutTakeoutItem()) {
				if (getMessageBuf() != null) {
					idx += getImage(ImageCode.LICK.ordinal(), direction, layer, idx);
				}
			} else if (isEating() || isEatingShit()) {
				if (getMessageBuf() != null) {
					idx += getImage(ImageCode.NOMNOM.ordinal(), direction, layer, idx);
				}
			}
			return idx;
		}

		// 死亡
		if (isDead()) {
			// 皮むき時
			if (isPealed()) {
				idx += getImage(ImageCode.PEALEDDEADFACE.ordinal(), direction, layer, idx);
			} else {
				idx += getImage(ImageCode.DEAD.ordinal(), direction, layer, idx);
			}
		}
		// 皮むき
		else if (isPealed()) {
			idx += getImage(ImageCode.PEALEDFACE.ordinal(), direction, layer, idx);
		}
		// 非ゆっくり症など
		else if (isNYD()) {
			// 死亡以外では表情を変えない
			if (isUnBirth()) {
				// 未ゆ
				idx += getImage(ImageCode.NYD_FRONT_CRY2.ordinal(), direction, layer, idx);
			} else {
				idx += getImage(ImageCode.NYD_FRONT_WIDE.ordinal(), direction, layer, idx);
			}
		}
		// 致命傷
		else if (getCriticalDamege() == CriticalDamegeType.CUT) {
			idx += getImage(ImageCode.PAIN.ordinal(), direction, layer, idx);
		}
		// 興奮
		else if (isExciting()) {
			if (isAliceRaper())
				idx += getImage(ImageCode.EXCITING_raper.ordinal(), direction, layer, idx);
			else
				idx += getImage(ImageCode.EXCITING.ordinal(), direction, layer, idx);
		}
		// 睡眠
		else if (isSleeping() && (!isUnBirth() || (damage <= 0)) && !isNeedled()) {
			if (SimYukkuri.UNYO) {
				// うにょ版まばたき機能
				if (getMabatakiType() != ImageCode.SLEEPING.ordinal()
						&& getMabatakiType() != ImageCode.NIGHTMARE.ordinal()) {
					setMabatakiCnt(0);
				}
				if (getMabatakiCnt() >= 0 && getMabatakiCnt() <= 2) {
					idx += getImage(ImageCode.NORMAL0.ordinal(), direction, layer, idx);
					idx += getImage(ImageCode.EYE2.ordinal(), direction, layer, idx);
				} else if (getMabatakiCnt() >= 3 && getMabatakiCnt() <= 5) {
					idx += getImage(ImageCode.NORMAL0.ordinal(), direction, layer, idx);
					idx += getImage(ImageCode.EYE3.ordinal(), direction, layer, idx);
				} else {
					if (isNightmare())
						idx += getImage(ImageCode.NIGHTMARE.ordinal(), direction, layer, idx);
					else
						idx += getImage(ImageCode.SLEEPING.ordinal(), direction, layer, idx);
				}
				setMabatakiType(ImageCode.SLEEPING.ordinal());
				if (MainCommandUI.getSelectedGameSpeed() != 0 /* && mabatakiType < 100 */) {
					setMabatakiCnt(getMabatakiCnt() + 1);
				}
			} else {
				if (isNightmare())
					idx += getImage(ImageCode.NIGHTMARE.ordinal(), direction, layer, idx);
				else
					idx += getImage(ImageCode.SLEEPING.ordinal(), direction, layer, idx);
			}
		}
		// ゆんやあ&命乞い
		else if (isTalking() && (isYunnyaa() || isBeggingForLife())) {
			idx += getImage(ImageCode.CRYING.ordinal(), direction, layer, idx);
		}
		// ぺろぺろまたは食事、口から物を出し入れするとき
		else if (isPeropero() || isEating() || isInOutTakeoutItem()) {
			if (isStrike() || isVerySad() || isFeelHardPain()) {
				idx += getImage(ImageCode.CRYING.ordinal(), direction, layer, idx);
			} else if (isSad() || isEatingShit() || isFeelPain()) {
				if (SimYukkuri.UNYO) {
					if (mabatakiNormalImageCheck()) {
						if (getMabatakiType() != ImageCode.TIRED.ordinal()) {
							setMabatakiCnt(0);
						}
						if (getMabatakiCnt() >= 95 && getMabatakiCnt() <= 96) {
							idx += getImage(ImageCode.TIRED0.ordinal(), direction, layer, idx);
							idx += getImage(ImageCode.EYE3.ordinal(), direction, layer, idx);
						} else {
							idx += getImage(ImageCode.TIRED.ordinal(), direction, layer, idx);
						}
						setMabatakiType(ImageCode.TIRED.ordinal());
						if (MainCommandUI.getSelectedGameSpeed() != 0) {
							setMabatakiCnt(getMabatakiCnt() + 1);
						}
						if (getMabatakiType() == ImageCode.TIRED.ordinal() && getMabatakiCnt() > 100) {
							if (SimYukkuri.RND.nextInt(30) != 0) {
								setMabatakiCnt(SimYukkuri.RND.nextInt(30));
							} else {
								setMabatakiCnt(85);
							}
						}
					} else {
						idx += getImage(ImageCode.TIRED.ordinal(), direction, layer, idx);
					}
				} else {
					idx += getImage(ImageCode.TIRED.ordinal(), direction, layer, idx);
				}
			} else {
				idx += getImage(ImageCode.SMILE.ordinal(), direction, layer, idx);
			}
		}
		// すっきり
		else if (isSukkiri()) {
			idx += getImage(ImageCode.REFRESHED.ordinal(), direction, layer, idx);
		}
		// ダメージ、痛み
		else if (isDamaged() || isSick() || isFeelPain()) {
			if (isFeelPain() && getAge() % 50 == 0 && SimYukkuri.RND.nextInt(50) == 0) {
				setForceFace(ImageCode.PAIN.ordinal());
			}
			if (isStrike() || isVerySad() || isFeelHardPain()) {
				idx += getImage(ImageCode.CRYING.ordinal(), direction, layer, idx);
			} else {
				if (SimYukkuri.UNYO) {
					// うにょ版まばたき機能
					if (mabatakiNormalImageCheck()) {
						if (getMabatakiType() != ImageCode.TIRED.ordinal()) {
							setMabatakiCnt(0);
						}
						if (getMabatakiCnt() >= 95 && getMabatakiCnt() <= 96) {
							idx += getImage(ImageCode.TIRED0.ordinal(), direction, layer, idx);
							idx += getImage(ImageCode.EYE3.ordinal(), direction, layer, idx);
						} else {
							idx += getImage(ImageCode.TIRED.ordinal(), direction, layer, idx);
						}
						setMabatakiType(ImageCode.TIRED.ordinal());
						if (MainCommandUI.getSelectedGameSpeed() != 0) {
							setMabatakiCnt(getMabatakiCnt() + 1);
						}
						if (getMabatakiType() == ImageCode.TIRED.ordinal() && getMabatakiCnt() > 100) {
							if (SimYukkuri.RND.nextInt(30) != 0) {
								setMabatakiCnt(SimYukkuri.RND.nextInt(30));
							} else {
								setMabatakiCnt(85);
							}
						}
					} else {
						idx += getImage(ImageCode.TIRED.ordinal(), direction, layer, idx);
					}
				} else {
					idx += getImage(ImageCode.TIRED.ordinal(), direction, layer, idx);
				}
			}
		} else {
			// パニック
			if (getPanicType() != null) {
				idx += getImage(ImageCode.CRYING.ordinal(), direction, layer, idx);
			} else if (isStrike() || isVerySad()) {
				idx += getImage(ImageCode.CRYING.ordinal(), direction, layer, idx);
			} else if (isAngry()) {
				idx += getImage(ImageCode.PUFF.ordinal(), direction, layer, idx);
			} else if (isSad() || isOld()) {
				if (SimYukkuri.UNYO) {
					// うにょ版まばたき機能
					if (mabatakiNormalImageCheck()) {
						if (getMabatakiType() != ImageCode.TIRED.ordinal()) {
							setMabatakiCnt(0);
						}
						if (getMabatakiCnt() >= 95 && getMabatakiCnt() <= 96) {
							idx += getImage(ImageCode.TIRED0.ordinal(), direction, layer, idx);
							idx += getImage(ImageCode.EYE3.ordinal(), direction, layer, idx);
						} else {
							idx += getImage(ImageCode.TIRED.ordinal(), direction, layer, idx);
						}
						setMabatakiType(ImageCode.TIRED.ordinal());
						if (MainCommandUI.getSelectedGameSpeed() != 0) {
							setMabatakiCnt(getMabatakiCnt() + 1);
						}
						if (getMabatakiType() == ImageCode.TIRED.ordinal() && getMabatakiCnt() > 100) {
							if (SimYukkuri.RND.nextInt(30) != 0) {
								setMabatakiCnt(SimYukkuri.RND.nextInt(30));
							} else {
								setMabatakiCnt(85);
							}
						}
					} else {
						idx += getImage(ImageCode.TIRED.ordinal(), direction, layer, idx);
					}
				} else {
					idx += getImage(ImageCode.TIRED.ordinal(), direction, layer, idx);
				}
			} else if (isVain()) {
				idx += getImage(ImageCode.VAIN.ordinal(), direction, layer, idx);
			} else if (isHappy() || isNobinobi()) {
				idx += getImage(ImageCode.SMILE.ordinal(), direction, layer, idx);
			} else if (isTalking() && isRude()) {
				if (SimYukkuri.UNYO) {
					// うにょ版まばたき機能
					if (mabatakiNormalImageCheck()) {
						if (getMabatakiType() != ImageCode.RUDE.ordinal()) {
							setMabatakiCnt(0);
						}
						if ((getMabatakiCnt() >= 91 && getMabatakiCnt() <= 94) ||
								(getMabatakiCnt() >= 97 && getMabatakiCnt() <= 100)) {
							idx += getImage(ImageCode.RUDE0.ordinal(), direction, layer, idx);
							idx += getImage(ImageCode.EYE2.ordinal(), direction, layer, idx);
						} else if (getMabatakiCnt() >= 95 && getMabatakiCnt() <= 96) {
							idx += getImage(ImageCode.RUDE0.ordinal(), direction, layer, idx);
							idx += getImage(ImageCode.EYE3.ordinal(), direction, layer, idx);
						} else {
							idx += getImage(ImageCode.RUDE.ordinal(), direction, layer, idx);
						}
						setMabatakiType(ImageCode.RUDE.ordinal());
						if (MainCommandUI.getSelectedGameSpeed() != 0) {
							setMabatakiCnt(getMabatakiCnt() + 1);
						}
						if (getMabatakiType() == ImageCode.RUDE.ordinal() && getMabatakiCnt() > 100) {
							if (SimYukkuri.RND.nextInt(30) != 0) {
								setMabatakiCnt(SimYukkuri.RND.nextInt(30));
							} else {
								setMabatakiCnt(85);
							}
						}
					} else {
						idx += getImage(ImageCode.RUDE.ordinal(), direction, layer, idx);
					}
				} else {
					idx += getImage(ImageCode.RUDE.ordinal(), direction, layer, idx);
				}
			} else if (isTalking() && !isRude()) {
				if (SimYukkuri.UNYO) {
					// うにょ版まばたき機能
					if (mabatakiNormalImageCheck()) {
						if (getMabatakiType() != ImageCode.CHEER.ordinal()) {
							setMabatakiCnt(0);
						}
						if ((getMabatakiCnt() >= 91 && getMabatakiCnt() <= 94) ||
								(getMabatakiCnt() >= 97 && getMabatakiCnt() <= 100)) {
							idx += getImage(ImageCode.CHEER0.ordinal(), direction, layer, idx);
							idx += getImage(ImageCode.EYE2.ordinal(), direction, layer, idx);
						} else if (getMabatakiCnt() >= 95 && getMabatakiCnt() <= 96) {
							idx += getImage(ImageCode.CHEER0.ordinal(), direction, layer, idx);
							idx += getImage(ImageCode.EYE3.ordinal(), direction, layer, idx);
						} else {
							idx += getImage(ImageCode.CHEER.ordinal(), direction, layer, idx);
						}
						setMabatakiType(ImageCode.CHEER.ordinal());
						if (MainCommandUI.getSelectedGameSpeed() != 0) {
							setMabatakiCnt(getMabatakiCnt() + 1);
						}
						if (getMabatakiType() == ImageCode.CHEER.ordinal() && getMabatakiCnt() > 100) {
							if (SimYukkuri.RND.nextInt(30) != 0) {
								setMabatakiCnt(SimYukkuri.RND.nextInt(30));
							} else {
								setMabatakiCnt(85);
							}
						}
					} else {
						idx += getImage(ImageCode.CHEER.ordinal(), direction, layer, idx);
					}
				} else {
					idx += getImage(ImageCode.CHEER.ordinal(), direction, layer, idx);
				}
			}
			// 空が飛べない、空中にいる、移動不可ではない、すぃーにのってない場合→茎にいる実ゆの判定のよう
			else if ((!canflyCheck() && getZ() != 0) && !isLockmove()
					&& !(takeMappedObj(getLinkParent()) instanceof Sui)) {
				if (SimYukkuri.UNYO) {
					// うにょ版まばたき機能
					if (mabatakiNormalImageCheck()) {
						if (getMabatakiType() != ImageCode.CHEER.ordinal()) {
							setMabatakiCnt(0);
						}
						if ((getMabatakiCnt() >= 91 && getMabatakiCnt() <= 94) ||
								(getMabatakiCnt() >= 97 && getMabatakiCnt() <= 100)) {
							idx += getImage(ImageCode.CHEER0.ordinal(), direction, layer, idx);
							idx += getImage(ImageCode.EYE2.ordinal(), direction, layer, idx);
						} else if (getMabatakiCnt() >= 95 && getMabatakiCnt() <= 96) {
							idx += getImage(ImageCode.CHEER0.ordinal(), direction, layer, idx);
							idx += getImage(ImageCode.EYE3.ordinal(), direction, layer, idx);
						} else {
							idx += getImage(ImageCode.CHEER.ordinal(), direction, layer, idx);
						}
						setMabatakiType(ImageCode.CHEER.ordinal());
						if (MainCommandUI.getSelectedGameSpeed() != 0) {
							setMabatakiCnt(getMabatakiCnt() + 1);
						}
						if (getMabatakiType() == ImageCode.CHEER.ordinal() && getMabatakiCnt() > 100) {
							if (SimYukkuri.RND.nextInt(30) != 0) {
								setMabatakiCnt(SimYukkuri.RND.nextInt(30));
							} else {
								setMabatakiCnt(85);
							}
						}
					} else {
						idx += getImage(ImageCode.CHEER.ordinal(), direction, layer, idx);
					}
				} else {
					// ここに入った時点で実ゆはダメージをくらっているので嫌な顔をする
					idx += getImage(ImageCode.TIRED.ordinal(), direction, layer, idx);
				}
			} else {
				if (SimYukkuri.UNYO) {
					// うにょ版まばたき機能
					if (mabatakiNormalImageCheck()) {
						if (getMabatakiType() != ImageCode.NORMAL.ordinal()) {
							setMabatakiCnt(0);
						}
						if ((getMabatakiCnt() >= 91 && getMabatakiCnt() <= 94) ||
								(getMabatakiCnt() >= 97 && getMabatakiCnt() <= 100)) {
							idx += getImage(ImageCode.NORMAL0.ordinal(), direction, layer, idx);
							idx += getImage(ImageCode.EYE2.ordinal(), direction, layer, idx);
						} else if (getMabatakiCnt() >= 95 && getMabatakiCnt() <= 96) {
							idx += getImage(ImageCode.NORMAL0.ordinal(), direction, layer, idx);
							idx += getImage(ImageCode.EYE3.ordinal(), direction, layer, idx);
						} else {
							idx += getImage(ImageCode.NORMAL.ordinal(), direction, layer, idx);
						}
						setMabatakiType(ImageCode.NORMAL.ordinal());
						if (MainCommandUI.getSelectedGameSpeed() != 0) {
							setMabatakiCnt(getMabatakiCnt() + 1);
						}
						if (getMabatakiType() == ImageCode.NORMAL.ordinal() && getMabatakiCnt() > 100) {
							if (SimYukkuri.RND.nextInt(30) != 0) {
								setMabatakiCnt(SimYukkuri.RND.nextInt(30));
							} else {
								setMabatakiCnt(85);
							}
						}
					} else {
						idx += getImage(ImageCode.NORMAL.ordinal(), direction, layer, idx);
					}
				} else {
					idx += getImage(ImageCode.NORMAL.ordinal(), direction, layer, idx);
				}
			}
		}

		// 口封じグラの追加
		if (isShutmouth()) {
			idx += getImage(ImageCode.SHUTMOUTH.ordinal(), direction, layer, idx);
		}
		// 盲目グラの追加
		if (isBlind()) {
			idx += getImage(ImageCode.BLIND.ordinal(), direction, layer, idx);
		}
		// 舌の追加
		if (isPeropero() || isInOutTakeoutItem()) {
			if (getMessageBuf() != null) {
				idx += getImage(ImageCode.LICK.ordinal(), direction, layer, idx);
			}
		} else if (isEating() || isEatingShit()) {
			if (getMessageBuf() != null) {
				idx += getImage(ImageCode.NOMNOM.ordinal(), direction, layer, idx);
			}
		}

		return idx;
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
		if (getType() == 20000) {
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
		int direction = this.getDirection().ordinal();
		int idx = 0;
		if (type == 0) {
			if (hasBraidCheck()) {
				// 通常
				if (canflyCheck()) {
					// 飛行状態
					idx += getImage((int) (ImageCode.BRAID_MV0.ordinal() + ((getAge() % 6) >> 1)), direction, layer,
							idx);
				} else {
					if (isPikopiko()) {
						idx += getImage((int) (ImageCode.BRAID_MV0.ordinal() + ((getAge() % 6) >> 1)), direction, layer,
								idx);
					} else {
						idx += getImage(ImageCode.BRAID.ordinal(), direction, layer, idx);
					}
				}
			} else {
				// 破壊状態
				idx += getImage(ImageCode.BRAID_CUT.ordinal(), direction, layer, idx);
			}
		} else {
			if (hasBraidCheck()) {
				// 通常
				if (canflyCheck()) {
					// 飛行状態
					idx += getImage((int) (ImageCode.BRAID_BACK_MV0.ordinal() + ((getAge() % 6) >> 1)), direction,
							layer,
							idx);
				} else {
					if (isPikopiko()) {
						idx += getImage((int) (ImageCode.BRAID_BACK_MV0.ordinal() + ((getAge() % 6) >> 1)), direction,
								layer,
								idx);
					} else {
						idx += getImage(ImageCode.BRAID_BACK.ordinal(), direction, layer, idx);
					}
				}
			}
		}
		return idx;
	}

	/**
	 * イベントに反応できる状態かチェックする
	 * イベントの重要度で寝ていても起きたりできるようにするため
	 * ここでは動いたら見た目におかしくなる状況のみチェック
	 * 
	 * @return
	 */
	public final boolean canEventResponse() {
		if (isDead() || getCriticalDamege() == CriticalDamegeType.CUT || isPealed() ||
				isPacked() || (isBlind() && !isCutPeni()) || isSleeping() || isShitting() || isBirth() || isSukkiri() ||
				isbNeedled() || getCurrentEvent() != null || isNYD() || isTaken()
				|| getBaryState() != BaryInUGState.NONE || isLockmove() || isStarving()) {
			return false;
		}
		// Rapers ignore events while exciting and continue their action.
		if (isRaper() && (isExciting() || isForceExciting())) {
			return false;
		}
		return true;
	}

	/**
	 * ぺにぺに切断のみ、盲目状態でも起きて良い
	 * 
	 * @return ぺにぺに切断イベントが溜まってるかどうか
	 */
	@Transient
	protected boolean isCutPeni() {
		if (getEventList() == null || getEventList().size() == 0) {
			return false;
		}
		if (getEventList().get(0) instanceof CutPenipeniEvent) {
			return true;
		}
		return false;
	}

	/**
	 * 行動できる状態かチェックする
	 * ここでは動いたら見た目におかしくなる状況のみチェック
	 * 
	 * @return
	 */
	public final boolean canAction() {
		if (isDead() || getCriticalDamege() == CriticalDamegeType.CUT || isPealed() ||
				isPacked() || isSleeping() || isShitting() || isBirth() || isSukkiri() || isbNeedled() ||
				getCurrentEvent() != null || isNYD() ||
				getBaryState() != BaryInUGState.NONE) {
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
		if (isDead() || getCriticalDamege() == CriticalDamegeType.CUT || isPealed() ||
				isPacked() || isSleeping() || isShitting() || isBirth() || isSukkiri() || isbNeedled() ||
				isNYD() || getBaryState() != BaryInUGState.NONE) {
			return false;
		}
		return true;
	}

	@Override
	public void grab() {
		grabbed = true;
		if (getBindStalk() != null) {
			checkReactionStalkMother(UnbirthBabyState.KILLED);
			if (getBindStalk().getBindBabies() != null
					&& getBindStalk().getBindBabies().indexOf(this.getUniqueID()) >= 0) {
				getBindStalk().getBindBabies().set(getBindStalk().getBindBabies().indexOf(this.getUniqueID()), null);
			}
			setBindStalk(null);
		}
	}

	/**
	 * Tick処理本体
	 */
	@Override
	public Event clockTick() {
		if (Terrarium.getOperationTime() % 100 == 0) {
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
			checkMessage();
			if (SimYukkuri.UNYO) {
				resetUnyo();
			}
			setSilent(true);
			setDeadPeriod(getDeadPeriod() + 1);
			// 死後3日
			if (getROTTINGTIMEorg() < getDeadPeriod()) {
				if (!isCrushed()) {
					// 初回は潰れる
					setCrushed(true);
					setDeadPeriod(0);
				} else {
					// うんうんと吐餡に変わって消える
					SimYukkuri.mypane.getTerrarium().addCrushedVomit(x, y, z, this, getShitType());
					SimYukkuri.mypane.getTerrarium().addCrushedShit(x, y, z, this, getShitType());
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
		boolean bStopAmple = false;
		if (getAttachmentSize(StopAmpoule.class) != 0) {
			bStopAmple = true;
		}

		boolean bAccelAmple = false;
		if (getAttachmentSize(AccelAmpoule.class) != 0) {
			bAccelAmple = true;
		}

		if (Terrarium.getInterval() == 0) {
			if (Terrarium.isAgeBoostSteam() && getBodyAgeState() != AgeState.ADULT)
				addAge(10000);
			if (Terrarium.isAgeStopSteam() && !bAccelAmple)
				addAge(-256);
		}

		if (getBaryState() == BaryInUGState.NONE || getBaryState() == BaryInUGState.HALF) {
			if (Terrarium.isRapidPregnantSteam())
				rapidPregnantPeriod();
		}

		// check age
		// ageが変化しないと状態が変化しないロジックになっているのでそっとしておく
		setAge(getAge() + TICK);

		if (getAge() > getLIFELIMITorg()) {
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
			if (((Terrarium.isAgeStopSteam()) || (bStopAmple)) && !bAccelAmple) {
				setAgeState(curAge);
				setAge(getAge() + TICK);
			} else {
				// 加齢
				initAmount(getBodyAgeState());
				resetAttachmentBoundary();
				// DamageLimitを流用してるパラメータは状態を維持するためここで再計算
				switch (foot) {
					case MIDIUM:
						footBakePeriod = (getDAMAGELIMITorg()[getBodyAgeState().ordinal()] >> 1) + 1;
						break;
					case CRITICAL:
						footBakePeriod = getDAMAGELIMITorg()[getBodyAgeState().ordinal()] + 1;
						break;
					default:
						break;
				}
			}
		}
		// ゆ虐神拳カウント
		plusGodHand();

		boolean dontMove = false;
		if (geteCoreAnkoState() == CoreAnkoState.NonYukkuriDisease ||
				isbOnDontMoveBeltconveyor() || isbSurisuriFromPlayer() || isPealed() || isPacked()) {
			dontMove = true;
		}

		// 無限もるんもるん
		if (Terrarium.isEndlessFurifuriSteam()) {
			clearActions();
			checkMessage();
			if (canFurifuri()) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.FuriFuri), 30);
				setFurifuri(true);
			} else if (isNotNYD()) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.CantMove), 30);
				setHappiness(Happiness.VERY_SAD);
			} else {
				setNYDMessage(MessagePool.getMessage(this, MessagePool.Action.NonYukkuriDisease), false);
			}
			shit = 0;
			hungry = getHungryLimit();
			if (damage > getDAMAGELIMITorg()[getBodyAgeState().ordinal()] * 80 / 100) {
				damage = getDAMAGELIMITorg()[getBodyAgeState().ordinal()] * 80 / 100;
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
		if (SimYukkuri.RND.nextInt(80) == 0) {
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
		if (getPanicType() != null && !isbNeedled() && isNotNYD()) {
			retval = checkFear();
			if (isLockmove())
				dontMove = true;
			if (isMelt())
				dontMove = true;
			if (getCriticalDamege() != null)
				dontMove = true;
			if (getFootBakeLevel() == FootBake.CRITICAL && !canflyCheck())
				dontMove = true;
			if (isbNeedled())
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
				isbNeedled() || getBaryState() != BaryInUGState.NONE || isUnBirth()) {
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
				if (!isAnalClose() && !(isFixBack() && isbNeedled())) {
					// 寝ているか粘着床についているか針が刺さっていたら体勢をかえられずに漏らす
					if ((isLockmove() && isFixBack()) || isSleeping() || isbNeedled() ||
							getBaryState() != BaryInUGState.NONE) {
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
					&& SimYukkuri.RND.nextInt(getBreakBodyByShitProb()) == 0) {
				bodyCut();
			}
		}

		// 以下3項目のまとめ
		if (checkSleep() || isLockmove() || isMelt() || isFurifuri() || isEating() || isUnBirth()) {
			dontMove = true;
		}

		if (isStaying()) {
			setStaycount(getStaycount() + TICK);
			if (getStaycount() > stayTime) {
				setStaycount(0);
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
		if (canEventResponse()) {
			// 自身に向けられたイベントのチェック
			setCurrentEvent(EventLogic.checkBodyEvent(this));
			if (getCurrentEvent() == null) {
				// ワールドイベントのチェック
				setCurrentEvent(EventLogic.checkWorldEvent(this));
			}
			// イベント開始
			if (getCurrentEvent() != null) {
				getCurrentEvent().start(this);
			}
		} else {
			// イベント応答できない場合でも例外でsimpleActionだけ呼ばれる
			// 自身に向けられたイベントのチェック
			EventLogic.checkSimpleBodyEvent(this);
			// ワールドイベントのチェック
			EventLogic.checkSimpleWorldEvent(this);
		}

		// move to destination
		// if there is no destination, walking randomly.
		if (geteCoreAnkoState() == CoreAnkoState.NonYukkuriDiseaseNear) {
			// 非ゆっくり症初期の場合はあまり動かない
			if (SimYukkuri.RND.nextInt(5) == 0) {
				moveBody(true);
			} else {
				moveBody(dontMove);
			}
		} else {
			moveBody(dontMove);
		}

		checkMessage();

		// イベントで処理が設定された場合に実行する
		if (getCurrentEvent() != null) {
			if (retval == Event.DONOTHING || getCurrentEvent().getPriority() != EventPacket.EventPriority.LOW) {
				retval = getEventResultAction();
				setEventResultAction(Event.DONOTHING);
			}
		}
		calcPos();
		calcMoveTarget();
		return retval;
	}

	/**
	 * moveTargetが範囲外のとき、範囲内に収める
	 */
	public void calcMoveTarget() {
		Obj o = takeMoveTarget();
		if (o == null) {
			return;
		}
		int mapX = Translate.getMapW();
		int mapY = Translate.getMapH();
		if (o.getX() < 0) {
			o.setCalcX(0);
		}
		if (o.getY() < 0) {
			o.setCalcY(0);
		}
		if (o.getX() > mapX) {
			o.setCalcX(mapX);
		}
		if (o.getX() > mapY) {
			o.setCalcX(mapY);
		}
	}

	/**
	 * Removeされたゆっくりが姉妹リスト、子リストにいたら削除する
	 */
	private void checkRemovedFamilyList() {
		Body[] sisters = getArrayOfBody(getSisterList());
		getSisterList().clear();
		Set<Integer> set = new TreeSet<>();
		for (Body sister : sisters) {
			if (sister == null) {
				continue;
			}
			if (!sister.isRemoved()) {
				set.add(sister.getUniqueID());
			}
		}
		setSisterList(new LinkedList<Integer>(set));
		Collections.sort(getSisterList());

		Body[] elderSisters = getArrayOfBody(getElderSisterList());
		getElderSisterList().clear();
		set.clear();
		for (Body elderSister : elderSisters) {
			if (elderSister == null) {
				continue;
			}
			if (!elderSister.isRemoved()) {
				set.add(elderSister.getUniqueID());
			}
		}
		setElderSisterList(new LinkedList<Integer>(set));
		Collections.sort(getElderSisterList());

		Body[] children = getArrayOfBody(getChildrenList());
		getChildrenList().clear();
		set.clear();
		for (Body child : children) {
			if (child == null) {
				continue;
			}
			if (!child.isRemoved()) {
				set.add(child.getUniqueID());
			}
		}
		setChildrenList(new LinkedList<Integer>(set));
		Collections.sort(getChildrenList());
	}

	/**
	 * ユニークIDのlistからゆっくりのインスタンスの配列を返却する.
	 * 
	 * @param list ユニークIDのlist
	 * @return ゆっくりの配列
	 */
	public Body[] getArrayOfBody(List<Integer> list) {
		List<Body> bodies = new LinkedList<Body>();
		for (int i : list) {
			bodies.add(YukkuriUtil.getBodyInstance(i));
		}
		return bodies.toArray(new Body[0]);
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
	public Body(int initX, int initY, int initZ, AgeState initAgeState, Body mama, Body papa) {
		objType = Type.YUKKURI;
		objId = Numbering.INSTANCE.numberingObjId();
		x = initX;
		y = initY;
		z = initZ;
		if (z == 0) {
			setBFirstGround(false);
		} else {
			setBFirstGround(true);
		}
		getParents()[Parent.PAPA.ordinal()] = papa == null ? -1 : papa.getUniqueID();
		getParents()[Parent.MAMA.ordinal()] = mama == null ? -1 : mama.getUniqueID();
		setRemoved(false);
		if (SimYukkuri.RND.nextBoolean()) {
			setAttitude((papa != null ? papa.getAttitude() : null));
		} else {
			setAttitude((mama != null ? mama.getAttitude() : null));
		}
		if (getAttitude() == null) {
			setAttitude(getRandomAttitude());
		}
		switch (SimYukkuri.RND.nextInt(6)) {
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
					&& SimYukkuri.RND.nextBoolean()) {
				setIntelligence(Intelligence.FOOL);
			} else if (papa.getIntelligence() == Intelligence.WISE && mama.getIntelligence() == Intelligence.WISE
					&& SimYukkuri.RND.nextInt(5) <= 1) {
				setIntelligence(Intelligence.FOOL);
			}
		}

		if (papa != null && papa.getFavItem(FavItemType.BED) != null) {
			setFavItem(FavItemType.BED, papa.getFavItem(FavItemType.BED));
		} else if (mama != null && mama.getFavItem(FavItemType.BED) != null) {
			setFavItem(FavItemType.BED, mama.getFavItem(FavItemType.BED));
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
				setAge(getBABYLIMITorg());
				break;
			case ADULT:
			default:
				setAge(getCHILDLIMITorg());
				break;
		}
		setAge(getAge() + SimYukkuri.RND.nextInt(100));
		getBodyAgeState();
		getMindAgeState();
		initAmount(initAgeState);
		wakeUpTime = getAge();
		shit = SimYukkuri.RND.nextInt(getSHITLIMITorg()[getBodyAgeState().ordinal()] / 2);
		if (getBodyAgeState() == AgeState.BABY) {
			if (mama != null) {
				if (mama.isDamaged()) {
					damage = SimYukkuri.RND.nextInt(mama.damage) * getDAMAGELIMITorg()[Const.BABY_INDEX]
							/ mama.getDamageLimit();
					getBodyAgeState();
					getDamageState();
				}
				if (mama.isSick()) {
					addSickPeriod(100);
				}
				if (mama.isDead()) {
					damage += getDAMAGELIMITorg()[Const.BABY_INDEX] / 4 * 3
							+ SimYukkuri.RND.nextInt(getDAMAGELIMITorg()[Const.BABY_INDEX]);
				}
				setForceBirthMessage(true);
			}
		}
		dirX = randomDirection(dirX);
		dirY = randomDirection(dirY);
		setMessageTextSize(12);
		setUniqueID(Numbering.INSTANCE.numberingYukkuriID());
		// 生い立ちの設定
		BodyRank eBodyRank = BodyRank.KAIYU;
		PublicRank ePublicRank = PublicRank.NONE;
		if (mama != null) {
			eBodyRank = mama.getBodyRank();
			/*
			 * // 141229時点で飼いゆと野良ゆしか機能していないので他の選択肢はコメントアウト
			 * switch(eMotherBodyRank){
			 * case KAIYU:// 飼いゆ
			 * eBodyRank = Body.BodyRank.KAIYU;
			 * break;
			 * case SUTEYU:// 捨てゆ
			 * eBodyRank = Body.BodyRank.NORAYU_CLEAN;
			 * break;
			 * case NORAYU_CLEAN:// きれいな野良ゆ
			 * eBodyRank = Body.BodyRank.NORAYU_CLEAN;
			 * break;
			 * case NORAYU:// 野良ゆ
			 * eBodyRank = Body.BodyRank.NORAYU_CLEAN;
			 * break;
			 * case YASEIYU:// 野生ゆ
			 * eBodyRank = Body.BodyRank.YASEIYU
			 * break;
			 * default:
			 * break;
			 * }
			 */
			// 階級の設定
			PublicRank eMotherPubRank = mama.getPublicRank();
			// 母親のランクに応じて変更
			switch (eMotherPubRank) {
				case NONE:
					ePublicRank = PublicRank.NONE;
					break;
				case UnunSlave:// うんうん奴隷
					ePublicRank = PublicRank.UnunSlave;
					break;
				default:
					break;
			}
		} else {
			if (SimYukkuri.world.getCurrentMap().getMapIndex() == 5
					|| SimYukkuri.world.getCurrentMap().getMapIndex() == 6)
				eBodyRank = BodyRank.YASEIYU;
		}
		// 生い立ちを設定
		setBodyRank(eBodyRank);
		setPublicRank(ePublicRank);

		// 先祖の情報を引き継ぐ
		if (mama != null) {
			List<Integer> anTempList = mama.getAncestorList();
			int nType = mama.getType();
			addAncestorList(anTempList);
			addAncestorList(nType);
		}
		if (papa != null) {
			List<Integer> anTempList = papa.getAncestorList();
			int nType = papa.getType();
			addAncestorList(anTempList);
			addAncestorList(nType);
		}

		hungry = getHUNGRYLIMITorg()[getBodyAgeState().ordinal()] + (100 * getBodyAgeState().ordinal());

	}

	public Body() {
		objType = Type.YUKKURI;
		if (z == 0) {
			setBFirstGround(false);
		} else {
			setBFirstGround(true);
		}
		setRemoved(false);
		if (getAttitude() == null) {
			setAttitude(getRandomAttitude());
		}
		switch (SimYukkuri.RND.nextInt(6)) {
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

		setAge(getAge() + SimYukkuri.RND.nextInt(100));
		getBodyAgeState();
		getMindAgeState();
		wakeUpTime = getAge();
		shit = SimYukkuri.RND.nextInt(getSHITLIMITorg()[getBodyAgeState().ordinal()] / 2);
		dirX = randomDirection(dirX);
		dirY = randomDirection(dirY);
		setMessageTextSize(12);
		setUniqueID(Numbering.INSTANCE.numberingYukkuriID());
		// 生い立ちの設定
		BodyRank eBodyRank = BodyRank.KAIYU;
		PublicRank ePublicRank = PublicRank.NONE;
		// 生い立ちを設定
		setBodyRank(eBodyRank);
		setPublicRank(ePublicRank);

		hungry = getHUNGRYLIMITorg()[getBodyAgeState().ordinal()] + (100 * getBodyAgeState().ordinal());
	}

	@Transient
	private Attitude getRandomAttitude() {
		switch (SimYukkuri.RND.nextInt(9)) {
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
	 * @param nWaitTime 待ち時間
	 * @return 待ち時間が過ぎたらtrue
	 */
	public final boolean checkWait(int nWaitTime) {
		long lnNowTime = System.currentTimeMillis();
		long lnLastActionTime = getInLastActionTime();
		int speed = MyPane.getGameSpeed()[MainCommandUI.getSelectedGameSpeed()];
		if (lnNowTime - lnLastActionTime < nWaitTime * speed / MyPane.NORMAL) {
			return false;
		}
		// setlnLastActionTime(lnNowTime);
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
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Moldy));
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

	/**
	 * イベントのためのアクションのみのクリア
	 */
	public void clearActionsForEvent() {
		setToSukkiri(false);
		setToBed(false);
		setToFood(false);
		setToShit(false);
		setToBody(false);
		setToSteal(false);
	}
}
