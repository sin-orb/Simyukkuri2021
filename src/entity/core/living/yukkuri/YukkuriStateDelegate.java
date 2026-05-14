package src.entity.core.living.yukkuri;

import src.Const;
import src.draw.Translate;
import src.entity.core.Entity;
import src.enums.CoreAnkoState;
import src.enums.Damage;
import src.entity.core.attachment.impl.Fire;
import src.field.FieldShape;
import src.field.impl.Pool;
import src.enums.BurialState;
import src.enums.Burst;
import src.enums.CriticalDamegeType;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.PanicType;
import src.enums.UnbirthBabyState;
import src.event.EventPacket;
import src.event.impl.BegForLifeEvent;
import src.event.impl.BreedEvent;
import src.logic.BodyRelations;
import src.logic.EventLogic;
import src.system.MessagePool;
import src.util.GameEnvironment;
import src.util.GameMessages;
import src.util.GameRandom;
import src.util.GameView;
import src.util.GameWorld;

/**
 * ゆっくりの状態変化を切り出した委譲クラス.
 */
public final class YukkuriStateDelegate {
	private final Yukkuri body;

	/**
	 * 状態変化を扱う委譲を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriStateDelegate(Yukkuri body) {
		this.body = body;
	}

	/**
	 * ゆかびに感染している際の基本反応
	 */
	public void checkSick() {
		// （汚くてダメージを受けている、またはディフューザーで湿度が高まっていてダメージを受けている）、かつディフューザーでゆかび禁止になっていないとき
		if (((body.isDirty() && body.isDamaged()) || (GameEnvironment.isHumid() && body.getDamage() > 0))
				&& !GameEnvironment.isAntifungalSteam()) {
			body.advanceDirtyPeriod(GameEnvironment.isHumid(), body.isWet() || body.isMelt(), body.isStubbornlyDirty());
			body.promoteDirtyToSickIfNeeded();
		} else {
			body.setDirtyPeriod(0);
		}
		if (body.isSick()) {
			body.addSickPeriod(GameEnvironment.isHumid() ? 4 : 1);
			if (body.getSickPeriod() > body.getIncubationPeriodBase() * 32
					&& (body.getDamage() >= body.getDamageLimitBase()[body.getBodyAgeState().ordinal()] * 85 / 100)
					&& !body.isTalking()) {
				if (body.isSleeping()) {
					body.wakeup();
				}
				// 末期症状
				body.setPikoMessage(GameMessages.getMessage(body, MessagePool.Action.MoldySeriousry), 40, true);
				body.addStress(Entity.TICK * 100);
				body.addMemories(-5);
				if (GameRandom.nextInt(60) == 0) {
					body.setForceFace(ImageCode.PAIN.ordinal());
				}
				if (GameRandom.nextInt(10) == 0) {
					if (GameRandom.nextBoolean()) {
						body.doYunnyaa(false);
					} else {
						body.setNobinobi(true);
					}
				}
				body.setHappiness(Happiness.VERY_SAD);
				if (body.getCurrentEvent() != null
						&& body.getCurrentEvent().getPriority() != EventPacket.EventPriority.HIGH) {
					body.clearEvent();
				}
				return;
			}
			if (body.isSickHeavily() && GameRandom.nextInt(600) == 0) {
				if (body.isSickTooHeavily()) {
					body.setPikoMessage(GameMessages.getMessage(body, MessagePool.Action.Scream2), true);
				} else {
					body.setPikoMessage(GameMessages.getMessage(body, MessagePool.Action.Scream), true);
				}
				body.setForceFace(ImageCode.PAIN.ordinal());
			}
			if (body.isSick() && GameRandom.nextInt(50) == 0) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Moldy));
			}
			body.setHappiness(Happiness.SAD);
			body.addStress(Entity.TICK);
			if (body.isSickTooHeavily() && body.getCurrentEvent() != null
					&& body.getCurrentEvent().getPriority() != EventPacket.EventPriority.HIGH) {
				body.clearEvent();
			}
		}
	}

	/**
	 * メッセージを出すかどうか.
	 */
	public void checkMessage() {
		if (!body.updateMessageCommon()) {
			return;
		}
		if (body.isSleeping()) {
			if (GameRandom.nextInt(10) == 0) {
				if (body.isNightmare()) {
					body.setNYDMessage(GameMessages.getMessage(body, MessagePool.Action.Nightmare), false);
					body.addStress(20);
				} else {
					body.setNYDMessage(GameMessages.getMessage(body, MessagePool.Action.Sleep), false);
					body.addStress(-20);
				}
			}
		} else if (!body.isUnBirth() && body.isBirthMessageForced()) {
			body.setBirthMessageForced(false);
			body.setPikoMessage(GameMessages.getMessage(body, MessagePool.Action.Birth), true);
			body.addMemories(10);
		} else if (!body.isFlyingType() && body.getZ() > 15 && body.getPanicType() == null && !body.isLockmove()
				&& body.getCriticalDamege() != CriticalDamegeType.CUT && !body.isPealed() && !body.isBlind()) {
			// 持ち上げたとき
			// 妊娠限界を超えている場合
			if (body.isStressful() && body.isOverPregnantLimit() && GameRandom.nextBoolean()) {
				body.setPikoMessage(GameMessages.getMessage(body, MessagePool.Action.DontThrowMeAway), true);
				body.setForceFace(ImageCode.CRYING.ordinal());
				body.addStress(100);
				// なつき度設定
				body.addLovePlayer(-10);
			}
			// おそらとんでるみたい！
			else {
				body.setPikoMessage(GameMessages.getMessage(body, MessagePool.Action.Flying), true);
				body.addStress(-10);
				// なつき度設定
				body.addLovePlayer(10);
			}
		} else if (body.isStressful() && body.isOverPregnantLimit() && GameRandom.nextBoolean() && body.isGrabbed()) {
			body.setPikoMessage(GameMessages.getMessage(body, MessagePool.Action.DontThrowMeAway), true);
			body.setForceFace(ImageCode.CRYING.ordinal());
			body.addStress(100);
			// なつき度設定
			body.addLovePlayer(-10);
		} else if (body.getBurstState() == Burst.NEAR) {
			if (body.isSleeping()) {
				body.wakeup();
			}
			if (GameRandom.nextInt(8) == 0) {
				body.setPikoMessage(GameMessages.getMessage(body, MessagePool.Action.Inflation),
						GameRandom.nextBoolean());
			}
		} else if (body.nearToBirth() && !body.isBirth()) {
			if (!body.isTalking() && body.getBurialState() == BurialState.NONE && GameRandom.nextInt(8) == 0) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.NearToBirth));
				// BreedEventの重複作成を防ぐ（同じfromのイベントが既にあれば追加しない）
				boolean hasBreedEvent = false;
				for (src.event.EventPacket ep : GameWorld.get().getCurrentMap().getEvent()) {
					if (ep instanceof BreedEvent && BodyRelations.getBody(ep.getFrom()) == body) {
						hasBreedEvent = true;
						break;
					}
				}
				if (!hasBreedEvent) {
					EventLogic.addWorldEvent(new BreedEvent(body, null, null, 2), null, null);
				}
			}
		}
	}

	/**
	 * 体の爆発.
	 */
	public void bodyBurst() {
		if (!body.isCrushed()) {
			body.strike(Const.HAMMER * 30);
			body.toDead();
		}
		if (body.isDead() && body.getBurialState() != BurialState.ALL) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Dying), true);
			body.stay();
			body.setCrushed(true);
			if (GameView.getTerrarium() != null) {
				for (int i = 0; i < (GameRandom.nextInt(5) + 5); i++) {
					GameView.addCrushedVomit(body.getX() + 7 - GameRandom.nextInt(14),
							body.getY() + 7 - GameRandom.nextInt(14),
							0, body, body.getShitType());
				}
			}
		}
	}

	/**
	 * 体の切断.
	 */
	public void bodyCut() {
		body.clearActions();
		body.setCriticalDamege(CriticalDamegeType.CUT);
		if (body.getBurialState() == BurialState.NONE) {
			for (int i = 0; i < 5; i++) {
				GameView.addVomit(body.getX() + 7 - GameRandom.nextInt(14),
						body.getY() + 7 - GameRandom.nextInt(14), 0,
						body, body.getShitType());
			}
		}
		body.checkReactionStalkMother(UnbirthBabyState.ATTAKED);
	}

	/**
	 * 体のケガ.
	 */
	public void bodyInjure() {
		body.clearActions();
		if (body.getCriticalDamege() == CriticalDamegeType.CUT) {
			return;
		}
		if (body.getCriticalDamege() == CriticalDamegeType.INJURED && GameRandom.nextInt(50) == 0) {
			bodyCut();
			return;
		}
		body.setCalm();
		body.setForceFace(ImageCode.PAIN.ordinal());
		body.setHappiness(Happiness.VERY_SAD);
		body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Scream), 40, true, true);
		body.setCriticalDamege(CriticalDamegeType.INJURED);
		if (body.getBurialState() == BurialState.NONE) {
			GameView.addVomit(body.getX() + 7 - GameRandom.nextInt(14),
					body.getY() + 7 - GameRandom.nextInt(14), 0, body, body.getShitType());
		}
		body.checkReactionStalkMother(UnbirthBabyState.ATTAKED);
	}

	/**
	 * ジュース.
	 */
	public void giveJuice() {
		if (body.isDead()) {
			return;
		}
		if (!body.isCantDie() /* && !body.isTalking() */) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Healing), Const.HOLDMESSAGE, true, true);
			// body.stay();
		}
		if (body.getCriticalDamegeType() == CriticalDamegeType.INJURED) {
			body.setCriticalDamege(null);
		}
		body.setBodyBakePeriod(0);
		body.setDamage(0);
		body.setDamageState(body.getDamageState());
		body.setHungry(body.getHungryLimit());
		body.setBodyBakePeriod(0);
		body.setAngry(false);
		body.setScare(false);
		body.setCalm();

		if (body.getAttachmentSize(Fire.class) != 0) {
			body.removeAttachment(Fire.class);
		}
		body.setHappiness(Happiness.VERY_HAPPY);
		body.setStress(0);
		body.addMemories(20);
		body.setForcePanicClear();
		if (body.getCurrentEvent() instanceof BegForLifeEvent) {
			// 空処理
		} else {
			body.clearActions();
		}
		// なつき度設定
		body.addLovePlayer(200);
		// 実ゆの場合、親が反応する
		body.checkReactionStalkMother(UnbirthBabyState.HAPPY);
	}

	/**
	 * ジュース注入.
	 */
	public void injectJuice() {
		if (body.isDead()) {
			return;
		}
		// 反応
		if (body.isSleeping()) {
			body.wakeup();
		}
		if (!(body.getCurrentEvent() instanceof BegForLifeEvent)) {
			body.clearActions();
		}
		body.setForceFace(ImageCode.PAIN.ordinal());
		body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Scream), Const.HOLDMESSAGE, true, true);
		body.setHappiness(Happiness.VERY_SAD);
		body.setCalm();
		body.addStress(50);
		body.addMemories(-10);
		// 回復
		if (body.getCriticalDamegeType() == CriticalDamegeType.INJURED) {
			body.setCriticalDamege(null);
		}
		body.setBodyBakePeriod(0);
		body.setDamage(0);
		body.setDamageState(body.getDamageState());
		body.setHungry(body.getHungryLimit());
		// なつき度設定
		body.addLovePlayer(-50);
		// 実ゆの場合、親が反応する
		body.checkReactionStalkMother(UnbirthBabyState.ATTAKED);
	}

	/**
	 * 水をかける.
	 */
	public void giveWater() {
		if (!body.isDead() && !body.isUnBirth()) {
			// 寝てたら起きる
			if (body.isSleeping()) {
				body.wakeup();
			}
			body.setCalm();
			body.setStayTicks(0);
			body.setStaying(false);
			body.setToFood(false);
			body.setToSukkiri(false);
			body.setToShit(false);
			body.setShitting(false);
			body.setBirth(false);
			body.setAngry(false);
			if (!body.isFixBack()) {
				body.setFurifuri(false);
			}
			body.setEating(false);
			body.setPeropero(false);
			body.setSukkiri(false);
			body.setScare(false);
			body.setEatingShit(false);
			body.setBeVain(false);
			body.setNobinobi(false);
			body.setYunnyaa(false);
			body.setInOutTakeoutItem(false);
			// 水が平気なら幸福度アップ
			if (body.isLikeWater()) {
				if (body.getPanicType() != PanicType.BURN) {
					body.setHappiness(Happiness.HAPPY);
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Cleaned));
				} else {
					body.setHappiness(Happiness.VERY_SAD);
				}
			} else {
				body.setHappiness(Happiness.VERY_SAD);
				// なつき度設定
				body.addLovePlayer(-100);
				if (body.getPanicType() != PanicType.BURN) {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Wet), true);
					// 実ゆの場合、親が反応する
					body.checkReactionStalkMother(UnbirthBabyState.SAD);
				}
			}
		}
		body.setWet(true);
		body.setWetPeriod(0);
		if (body.getAttachmentSize(Fire.class) != 0) {
			body.removeAttachment(Fire.class);
		}
		body.setForcePanicClear();
	}

	/**
	 * 土に埋める.
	 */
	public void baryInUnderGround() {
		// 接地してるか
		if (0 < body.getZ()) {
			return;
		}

		// 畑にいるか
		int xCoord = body.getX();
		int yCoord = body.getY();
		if ((Translate.getCurrentFieldMapNum(xCoord, yCoord) & FieldShape.FIELD_FARM) == 0) {
			return;
		}

		int collisionHeight = body.getCollisionY();
		body.setLockmove(true);

		// 現在の深さチェック
		switch (body.getBurialState()) {
			case NONE:
				body.setBurialState(BurialState.HALF);
				body.setMostDepth(-collisionHeight / 16);
				body.setCalcZ(-collisionHeight / 16);
				break;
			case HALF:
				body.setBurialState(BurialState.NEARLY_ALL);
				body.setMostDepth(-collisionHeight / 8);
				body.setCalcZ(-collisionHeight / 8);
				break;
			case NEARLY_ALL:
				body.setBurialState(BurialState.ALL);
				body.setMostDepth(-collisionHeight / 3);
				body.setCalcZ(-collisionHeight / 3);
				break;
			case ALL:
				break;
			default:
				break;
		}
		body.begForLife();
	}

	/**
	 * 怒らせる.
	 */
	public void setAngry() {
		if (body.isDead() || body.isNYD() || body.isSleeping()) {
			return;
		}
		if (body.getDamageState() == src.enums.Damage.NONE && !body.isVerySad()) {
			body.setAngry(true);
			body.setScare(false);
		}
		if (body.isFixBack() && body.isFurifuri()) {
			body.setFurifuri(true);
		} else {
			body.setFurifuri(false);
		}
		if (!body.isRaper()) {
			body.setExciting(false);
		}
		body.setForceExciting(false);
		body.setRelax(false);
		body.setBeVain(false);
		body.setNobinobi(false);
		body.setYunnyaa(false);
		body.setExcitingPeriod(0);
		body.setNoDamagePeriod(0);
		body.setNoHungryPeriod(0);
	}

	/**
	 * 汚れさせる.
	 */
	public void makeDirty(boolean flag) {
		body.setDirty(flag);
		if (body.isDead()) {
			return;
		}
		if (body.isDirty()) {
			body.setHappiness(Happiness.SAD);
			body.addStress(50);
			body.checkReactionStalkMother(UnbirthBabyState.ATTAKED);
		} else {
			body.setStubbornlyDirty(false);
			body.setHappiness(Happiness.HAPPY);
			body.addStress(-50);
			if (!body.isSleeping()) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Cleaned), 60);
				body.stay(60);
			}
			body.checkReactionStalkMother(UnbirthBabyState.HAPPY);
		}
	}

	/**
	 * 水の中にいる.
	 *
	 * @param depth 深さ
	 */
	public void inWater(Pool.DEPTH depth) {
		if (!body.isDead() && !body.isUnBirth()) {
			if (body.isSleeping()) {
				body.wakeup();
			}
			body.setCalm();
			body.setStayTicks(0);
			body.setStaying(false);
			body.setToFood(false);
			body.setToSukkiri(false);
			body.setToShit(false);
			body.setShitting(false);
			body.setBirth(false);
			body.setAngry(false);
			if (!body.isFixBack()) {
				body.setFurifuri(false);
			}
			body.setEating(false);
			body.setPeropero(false);
			body.setSukkiri(false);
			body.setScare(false);
			body.setEatingShit(false);
			body.setBeVain(false);
			body.setNobinobi(false);
			body.setYunnyaa(false);
			body.setInOutTakeoutItem(false);
			if (body.isLikeWater()) {
				if (body.getPanicType() != PanicType.BURN) {
					body.setHappiness(Happiness.HAPPY);
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Cleaned));
				} else {
					body.setHappiness(Happiness.VERY_SAD);
				}
			} else {
				body.setHappiness(Happiness.VERY_SAD);
				if (body.getPanicType() != PanicType.BURN) {
					switch (depth) {
						case SHALLOW:
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.WetInShallowWater), true);
							break;
						case DEEP:
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.WetInDeepwWater), true);
							break;
						default:
							break;
					}
					body.checkReactionStalkMother(UnbirthBabyState.SAD);
				}
			}
		}
		if (body.getAttachmentSize(Fire.class) != 0) {
			body.removeAttachment(Fire.class);
		}
		body.setWet(true);
		body.setWetPeriod(0);
	}

	/**
	 * 環境によるパニック状態の設定.
	 *
	 * @param flag  すでにパニック状態か
	 * @param pType パニックのタイプ
	 */
	public void setPanic(boolean flag, PanicType pType) {
		if (body.isDead() || body.isSleeping() || body.isUnBirth()) {
			return;
		}
		if (body.isIdiot()) {
			return;
		}
		if (body.isRaper() && body.isExciting()) {
			body.setForcePanicClear();
			return;
		}
		if (flag) {
			if (body.getPanicType() != null) {
				body.setPanicPeriod(0);
				return;
			}
			body.setPanicType(pType);
			body.setPanicPeriod(0);
			body.setStayTicks(0);
			body.setCalm();
			body.setStaying(false);
			body.setToFood(false);
			body.setToSukkiri(false);
			body.setToShit(false);
			body.setShitting(false);
			body.setBirth(false);
			body.setAngry(false);
			if (!body.isFixBack()) {
				body.setFurifuri(false);
			} else if (!body.isSleeping() && body.isNeedled() && GameRandom.nextInt(10) == 0) {
				body.setFurifuri(true);
			}
			body.setEating(false);
			body.setPeropero(false);
			body.setSukkiri(false);
			body.setScare(false);
			body.setEatingShit(false);
			body.setBeVain(false);
			body.setNobinobi(false);
			body.setYunnyaa(false);
			body.setInOutTakeoutItem(false);
			body.setHappiness(Happiness.VERY_SAD);
		} else {
			body.setPanicType(null);
			body.setPanicPeriod(0);
			body.setHappiness(Happiness.SAD);
		}
	}

	/** 皮を剥がされているときのメモリ減少・メッセージ反応. */
	public void onPealed() {
		body.setPeropero(false);
		body.addMemories(-5);
		if (body.getCoreAnkoState() == CoreAnkoState.NonYukkuriDiseaseNear) {
			body.setNYDMessage(GameMessages.getMessage(body, MessagePool.Action.Dying2), false);
		}
		body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Dying2));
	}

	/** 詰め込まれているときのメモリ減少反応. */
	public void onPacked() {
		body.setPeropero(false);
		body.addMemories(-2);
	}

	/** 毒スチーム被曝時の行動停止・感情・メッセージ反応. */
	public void onPoisonSteam() {
		body.clearActions();
		if (body.isNotNYD()) {
			body.setHappiness(Happiness.VERY_SAD);
			if (body.getDamageState() != Damage.NONE) {
				body.setNegiMessage(GameMessages.getMessage(body, MessagePool.Action.PoisonDamage), true);
			} else {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.PoisonDamage), Const.HOLDMESSAGE, false, true);
			}
		}
	}

	/** CUT 致命傷時のメッセージ反応. */
	public void onCutDamageReaction() {
		if (body.getCoreAnkoState() != CoreAnkoState.NonYukkuriDiseaseNear) {
			body.setNYDMessage(GameMessages.getMessage(body, MessagePool.Action.Dying2), false);
		} else {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Dying2));
		}
	}

	/** INJURED 致命傷時の悲鳴・吐瀉物・汚れ反応. */
	public void onInjuredScream(int x, int y) {
		body.addCrushedVomit(x, y, 0);
		body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Scream));
		body.setForceFace(ImageCode.PAIN.ordinal());
		body.makeDirty(true);
	}

	/** 悪夢/通常睡眠に応じた顔変化. nightmare=true なら悪夢顔. */
	public void onNightmare(boolean nightmare) {
		body.setForceFace(nightmare ? ImageCode.NIGHTMARE.ordinal() : ImageCode.SLEEPING.ordinal());
	}

	/** 飢餓による強制起床時のメッセージ・感情反応. */
	public void onWakeByHunger() {
		body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Hungry));
		body.setHappiness(Happiness.SAD);
	}

	/** 自然起床時のメッセージ反応. */
	public void onWakeupNaturally() {
		body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Wakeup), true);
	}
}
