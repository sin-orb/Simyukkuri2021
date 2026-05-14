package org.simyukkuri.entity.core.living.yukkuri;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.enums.UnbirthBabyState;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.event.impl.AvoidMoldEvent;
import org.simyukkuri.event.impl.HateNoOkazariEvent;
import org.simyukkuri.event.impl.KillPredeatorEvent;
import org.simyukkuri.event.impl.PredatorsGameEvent;
import org.simyukkuri.event.impl.RaperReactionEvent;
import org.simyukkuri.event.impl.RevengeAttackEvent;
import org.simyukkuri.logic.YukkuriLogic;
import org.simyukkuri.logic.YukkuriRelations;
import org.simyukkuri.logic.EventLogic;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameView;

/**
 * 摂食・被食・打撃などのダメージ系振る舞いをまとめる委譲クラス。
 */
public final class YukkuriDamageDelegate {
	private final Yukkuri body;

	/**
	 * ダメージ系の委譲を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriDamageDelegate(Yukkuri body) {
		this.body = body;
	}

	/**
	 * 食べられる処理.
	 *
	 * @param amount 食われる量
	 */
	public void eatYukkuri(int amount) {
		body.setAnkoAmount(body.getAnkoAmount() - amount);
		if (body.isDead()) {
			if (body.getAnkoAmount() <= body.getDamageLimitBase()[body.getAgeState().ordinal()] / 2) {
				body.setCrushed(true);
				if (body.getAnkoAmount() <= 0) {
					body.remove();
					body.setAnkoAmount(0);
				}
			}
		} else {
			body.addHungry(-amount);
			if (body.getHungry() <= 0) {
				body.addDamage(amount);
			}
			body.wakeup();
			if (body.getAnkoAmount() <= body.getDamageLimitBase()[body.getAgeState().ordinal()] / 2) {
				body.bodyCut();
				if (body.getAnkoAmount() <= 0) {
					body.toDead();
					body.setCrushed(true);
					body.setAnkoAmount(1);
				}
			}
		}
		body.checkReactionStalkMother(UnbirthBabyState.ATTAKED);
	}

	/**
	 * 他のゆっくりから食べられる.
	 *
	 * @param amount 食べられる量
	 * @param eater  食べてくるゆっくり
	 */
	public void eatYukkuri(int amount, Yukkuri eater) {
		eatYukkuri(amount);
		if (body.isDead()) {
			return;
		}
		if (body.isUnBirth()) {
			return;
		}
		Vomit vomit = GameView.addVomit(body.getX(), body.getY(), body.getZ(), body, body.getShitType());
		vomit.crushVomit();
		if (body.isNotNYD()) {
			if (body.isSmart() || body.getAgeState().ordinal() < eater.getAgeState().ordinal()
					|| body.isLockmove() || body.isGotBurnedHeavily()) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.EatenByBody));
				body.setHappiness(Happiness.VERY_SAD);
				body.runAway(body.getX(), body.getY());
			} else {
				body.setAngry();
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.EatenByBody));
				EventLogic.addYukkuriEvent(body, new RevengeAttackEvent(body, eater, null, 1), null, null);
			}
		}
	}

	/**
	 * ゆっくり以外から食べられる（現在はアリのみ）.
	 *
	 * @param amount 食われる量
	 * @param p      アリなら0
	 * @param av     食べられた際に吐くかどうか
	 */
	public void beEaten(int amount, int p, boolean av) {
		eatYukkuri(amount);
		body.makeDirty(true);
		if (body.isDead()) {
			return;
		}
		if (body.isUnBirth()) {
			return;
		}
		if (av) {
			Vomit vomit = GameView.addVomit(body.getX(), body.getY(), body.getZ(), body, body.getShitType());
			vomit.crushVomit();
		}
		if (body.isNotNYD()) {
			if (p == 0) {
				body.setHappiness(Happiness.VERY_SAD);
				if (GameRandom.nextInt(4) == 0) {
					if (!body.isAdult() && GameRandom.nextInt(4) == 0) {
						body.callParent();
					}
					if (GameRandom.nextInt(3) == 0) {
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Scream));
						body.setForceFace(ImageCode.PAIN.ordinal());
					} else {
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.EatenByAnts));
					}
					if (body.isDamaged() || body.isLockmove() || body.isGotBurnedHeavily()) {
						body.stayPurupuru(10);
					} else {
						if (GameRandom.nextInt(3) == 0) {
							switch (GameRandom.nextInt(3)) {
								case 0:
									if (!body.isShutmouth()) {
										body.setPeropero(true);
										body.substractNumOfAnts(10);
									}
									break;
								case 1:
									body.setNobinobi(true);
									body.substractNumOfAnts(5);
									break;
								case 2:
									if (body.canFurifuri()) {
										body.setFurifuri(true);
										body.substractNumOfAnts(35);
									}
								default:
									// NOP.
							}
							body.stay();
							body.setPikoMessage(GameMessages.getMessage(body, MessagePool.Action.RevengeAnts), true);
						}
					}
					if (body.isDamaged() || body.isLockmove() || body.isGotBurnedHeavily()) {
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Dying2));
					}
				}
			}
		}
	}

	/**
	 * 打撃を受ける.
	 *
	 * @param amount ダメージ量
	 */
	public void strike(int amount) {
		if (body.isDead()) {
			return;
		}
		body.setDamage(body.getDamage() + amount);
		body.addStress(amount >> 2);
		body.setStaying(false);
		body.setStrike(true);
		body.stay();
		body.setDamageState(body.getDamageState());
		body.wakeup();
		if (body.isFixBack() && !body.isNeedled()) {
			body.setFurifuri(true);
		}
	}

	/**
	 * ゆっくりから攻撃を受けた時の処理.
	 *
	 * @param enemy 攻撃してきたゆっくり
	 * @param event イベント
	 * @param allowDamageCap 手加減ありの場合
	 */
	public void strikeByYukkuri(Yukkuri enemy, EventPacket event, boolean allowDamageCap) {
		if (body.isDead()) {
			return;
		}
		int ap = enemy.getStrength();
		if (enemy.isDamaged()) {
			ap *= 0.75f;
		}
		if (body.isMelt()) {
			ap *= 2.5f;
		} else if (body.isWet()) {
			ap *= 1.5f;
		}
		if (body.isHasPants()) {
			ap *= 0.8f;
		}
		if (body.isExciting()) {
			ap *= 0.25f;
		}
		if (body.isPredatorType() && !enemy.isPredatorType()) {
			ap *= 0.25f;
		}
		if (!body.isPredatorType() && enemy.isPredatorType()) {
			ap *= 2f;
		}
		int kickX = (enemy.getWeight() - body.getWeight()) / 100;
		int kickY = (enemy.getWeight() - body.getWeight()) / 500;
		if (kickX < 0) {
			kickX = 0;
		}
		if (kickY < 0) {
			kickY = 0;
		}
		kickX += 3;
		if (enemy.getDirection() == Direction.LEFT) {
			kickX = -kickX;
		}
		if (enemy.getY() >= body.getY()) {
			kickY = -kickY;
		}
		if (SimYukkuri.UNYO && ap > 0) {
			ap = (int) (ap * 0.25f);
		}
		if (allowDamageCap) {
			int damageAfterHit = body.getDamage() + ap;
			if (body.getDamageLimitBase()[body.getAgeState().ordinal()] * 3 / 4 < damageAfterHit) {
				ap = body.getDamageLimitBase()[body.getAgeState().ordinal()] * 4 / 5 - body.getDamage();
				if (ap < 0) {
					ap = 0;
				}
			}
		}
		body.dropAllTakeoutItem();
		strike(ap);
		if (!body.isBraidType() && body.isHasBraid() && 0 < body.getBraidBreakChance()
				&& GameRandom.nextInt(body.getBraidBreakChance()) == 0) {
			body.setHasBraid(false);
		}
		body.setHappiness(Happiness.SAD);
		if (body.getBurialState() == BurialState.NONE) {
			body.kick(kickX, kickY, -4);
		}
		if (body.isDead()) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Dying), true);
			body.stay();
			body.setCrushed(true);
		} else {
			if (SimYukkuri.UNYO) {
				body.changeUnyo((int) (ap * 0.11f), 0, 0);
				enemy.changeUnyo(GameRandom.nextInt(3), 0, 0);
			}
			if (body.isNotNYD() && !body.isUnBirth()) {
				if (event instanceof HateNoOkazariEvent) {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Scream), true);
					if (body.getPublicRank() != PublicRank.UnunSlave
							&& (body.isRude() || (body.getAttitude() == Attitude.AVERAGE && GameRandom.nextBoolean()))) {
						body.setAngry();
						EventLogic.addYukkuriEvent(body, new RevengeAttackEvent(body, enemy, null, 1), null, null);
					}
				} else if (event instanceof PredatorsGameEvent) {
					body.runAway(enemy.getX(), enemy.getY());
					body.setPikoMessage(GameMessages.getMessage(body, MessagePool.Action.DontPlayMe), true);
					Yukkuri m = YukkuriRelations.getMotherYukkuri(body);
					if (m != null) {
						if (GameRandom.nextInt(3) == 0 && !m.isDead() && !m.isRemoved()) {
							m.clearEvent();
							m.setAngry();
							m.setPanic(false, null);
							m.setPeropero(false);
							EventLogic.addYukkuriEvent(m, new KillPredeatorEvent(m, enemy, null, 10), null, null);
						}
					}
					if (GameRandom.nextInt(10) == 0) {
						body.bodyInjure();
					}
				} else if (event instanceof RaperReactionEvent) {
					int colX = YukkuriLogic.calcCollisionX(body, enemy);
					body.moveToSukkiri(enemy, enemy.getX() + colX, enemy.getY());
					if (GameRandom.nextInt(200) == 0) {
						body.bodyInjure();
					}
				} else if (event instanceof AvoidMoldEvent) {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Scream), true);
					if (!body.isBaby() && !body.isSmart() && body.getIntelligence() == Intelligence.FOOL) {
						body.setAngry();
						EventLogic.addYukkuriEvent(body, new RevengeAttackEvent(body, enemy, null, 1), null, null);
					}
				} else {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Scream), true);
					if (body.getAttitude() != Attitude.VERY_NICE) {
						body.setAngry();
						EventLogic.addYukkuriEvent(body, new RevengeAttackEvent(body, enemy, null, 1), null, null);
					}
				}
			}
		}
	}

	/**
	 * 何かで衝撃を加えられたとき.
	 *
	 * @param ap             基本ダメージ量
	 * @param weight         体重
	 * @param allowDamageCap 手加減あり
	 * @param vecX           X方向のベクトル
	 * @param vecY           Y方向のベクトル
	 */
	public void strikeByObject(int ap, int weight, boolean allowDamageCap, int vecX, int vecY) {
		if (body.isDead()) {
			return;
		}
		// 状態によるダメージ変化
		if (body.isMelt()) {
			ap *= 2.5f;
		} else if (body.isWet()) {
			ap *= 1.5f;
		}
		if (body.isHasPants()) {
			ap *= 0.8;
		}
		// 吹っ飛び設定
		// 体重差
		int kick = (weight - body.getWeight()) / 100;
		if (kick < 1) {
			kick = 1;
		}
		vecX *= kick;
		vecY *= kick;
		// 手加減あり
		if (allowDamageCap) {
			int damageAfterHit = body.getDamage() + ap;
			if (body.getDamageLimitBase()[body.getAgeState().ordinal()] * 85 / 100 < damageAfterHit) {
				ap = body.getDamageLimitBase()[body.getAgeState().ordinal()] * 85 / 100 - body.getDamage();
				if (ap < 0) {
					ap = 0;
				}
			}
		}
		strike(ap);
		// 土に埋まっていないなら吹っ飛ぶ
		if (body.getBurialState() == BurialState.NONE) {
			body.kick(vecX, vecY, -5);
		}
		if (body.isDead()) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Dying), true);
			body.stay();
			body.setCrushed(true);
		} else {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Scream), true);
		}
		// 持ち物を全部落とす
		body.dropAllTakeoutItem();
		// 実ゆの場合、親が反応する
		body.checkReactionStalkMother(UnbirthBabyState.ATTAKED);
	}
}
