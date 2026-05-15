package org.simyukkuri.entity.core.living.yukkuri;

import java.util.List;

import org.simyukkuri.Const;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.impl.PoisonAmpoule;
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.FootBake;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.enums.WindowType;
import org.simyukkuri.event.impl.RaperWakeupEvent;
import org.simyukkuri.logic.YukkuriLogic;
import org.simyukkuri.logic.YukkuriRelations;
import org.simyukkuri.logic.EventLogic;
import org.simyukkuri.logic.FamilyActionLogic;
import org.simyukkuri.logic.ToyLogic;
import org.simyukkuri.logic.TrashLogic;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;

/**
 * ゆっくりの感情・気分・状態反応を切り出した委譲クラス.
 */
public final class YukkuriEmotionDelegate {
	private final Yukkuri body;

	/**
	 * 感情・気分・状態反応を扱う委譲を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriEmotionDelegate(Yukkuri body) {
		this.body = body;
	}

	/**
	 * 自身の状態に対する反応を記述する.
	 */
	public void checkEmotion() {
		// 怒り状態の経過
		if (body.isAngry()) {
			body.setAngryPeriod(body.getAngryPeriod() + Entity.TICK);
			if (body.getAngryPeriod() > body.getAngryPeriodBase()) {
				body.setAngryPeriod(0);
				body.setAngry(false);
			}
		}
		// 恐怖状態の経過
		if (body.isScare()) {
			body.setScarePeriod(body.getScarePeriod() + Entity.TICK);
			if (body.getScarePeriod() > body.getScarePeriodBase()) {
				body.setScarePeriod(0);
				body.setScare(false);
			}
		}
		// 落ち込み状態の経過
		if (body.getHappiness() == Happiness.VERY_SAD) {
			body.setSadPeriod(body.getSadPeriod() - 1);
			if (body.getSadPeriod() < 0) {
				body.setSadPeriod(0);
				body.setHappiness(Happiness.SAD);
			}
		}
		// お遊び状態の経過
		if (body.getPlaying() != null) {
			body.setPlayingLimit(body.getPlayingLimit() - 1);
			boolean playingOk = false;
			switch (body.getPlaying()) {
				case BALL:
					playingOk = ToyLogic.checkToy(body);
					break;
				case SUI:
					playingOk = ToyLogic.checkSui(body);
					break;
				case TRAMPOLINE:
					playingOk = ToyLogic.checkTrampoline(body);
					break;
				default:
					playingOk = false;
					break;
			}
			if (body.isSleeping() || body.getPlayingLimit() < 0 || !playingOk) {
				body.stopPlaying();
			}
		}

		// 非ゆっくり症チェック
		if (body.hasNonYukkuriDisease()) {
			return;
		}
		// イベント中
		else if (body.getCurrentEvent() != null) {
			return;
		}
		// プレイヤーにすりすりされている
		else if (body.doSurisuriByPlayer()) {
			return;
		}

		// ゆんやー
		if (body.isYunnyaa() && !body.isSleeping()) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Yunnyaa), 30, false, true);
			body.setYunnyaa(true);
			body.stay(40);
			body.setHappiness(Happiness.VERY_SAD);
			return;
		}
		// 加工中を想定した反応
		else if ((body.isDamaged() || body.hasDisorder()) && body.isOnNonMovingConveyor() && !body.hasBabyOrStalk()
				&& !body.isPealed()) {
			if (GameRandom.nextInt(80) == 0) {
				body.begForLife();
			} else if (GameRandom.nextInt(40) == 0) {
				body.doYunnyaa(true);
			} else if (GameRandom.nextInt(3) == 0) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.KilledInFactory), WindowType.NORMAL,
						Const.HOLDMESSAGE, true, GameRandom.nextBoolean(), false);
			}
		}
		// 状態異常時
		// 足切断
		else if (body.getCriticalDamege() == CriticalDamageType.CUT || body.isPealed() || body.isPacked()) {
			return;
		}
		// 盲目
		else if (body.checkEmotionBlind()) {
			return;
		}
		// しゃべれない
		else if (body.checkEmotionCantSpeak()) {
			return;
		}
		// 粘着系オブジェクトの貼り付け状態
		else if (body.checkEmotionLockmove()) {
			return;
		}
		// 足焼き済み
		else if (body.checkEmotionFootbake()) {
			return;
		}
		// 代替おかざりの捜索
		else if (TrashLogic.checkTrashOkazari(body)) {
			return;
		}
		// おかざり、ぴこぴこなし
		else if (body.checkEmotionNoOkazariPikopiko()) {
			return;
		}
		// 興奮時
		else if (body.isExciting()) {
			body.setRelax(false);
			return;
		}

		// 空腹時
		if (body.isHungry() && GameRandom.nextInt(50) == 0) {
			if (body.isSoHungry()) {
				body.setHappiness(Happiness.SAD);
			}
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Hungry), 30);
			body.stay();
		}

		// 通常時
		// うんうん奴隷の場合
		// 食事検索、トイレ検索時にもろもろのセリフを吐く
		if (body.getPublicRank() == PublicRank.UNUN_SLAVE || body.isMelt()) {
			body.setHappiness(Happiness.SAD);
			body.setExcitingPeriod(0);
			// 強制発情ではない場合
			if ((!body.isVeryRude() || body.getIntelligence() != Intelligence.FOOL) && body.isExciting()
					&& !body.isForceExciting()) {
				body.setCalm();
				body.setForceFace(ImageCode.TIRED.ordinal());
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.CantUsePenipeni));
			}
			body.setRelax(false);
			body.setAngry(false);
			body.setScare(false);
			return;
		}

		// 汚れ時の反応
		if (body.isNormalDirty() && !body.isSleeping()) {
			// 大人と、善良子ゆは勝手にきれいにする
			if (body.isAdult() || (body.isChild() && body.isSmart())) {
				if (GameRandom.nextInt(600) == 0) {
					body.cleaningItself();
				}
			} else {
				if (body.getDirtyScreamPeriod() == 0) {
					if (body.isRude()) {
						body.setDirtyScreamPeriod(10 + GameRandom.nextInt(15));
					} else {
						body.setDirtyScreamPeriod(5 + GameRandom.nextInt(10));
					}
				} else {
					body.callParent();
				}
			}
		} else {
			body.setCallingParents(false);
			body.setDirtyScreamPeriod(0);
		}

		// ゆっくりしてるとき
		if (body.getNoHungryPeriod() > body.getRelaxPeriodBase() && body.getNoDamagePeriod() > body.getRelaxPeriodBase()
				&& !body.isSleeping() && !body.isShitting() && !body.isEating()
				&& !body.isSad() && !body.isVerySad() && !body.isFeelPain()
				&& body.getAttachmentSize(PoisonAmpoule.class) == 0) {
			// && moveTargetId == null) {
			// すっきり発動条件
			if (!body.isExciting() && body.isNotNYD() && GameRandom.nextInt(body.getExciteProb()) == 0) {
				int r = 1;
				int adjust = body.getExcitingDiscipline() * (body.isRude() ? 1 : 2);
				if (body.isSuperRapist()) {
					r = GameRandom.nextInt(1 + adjust);
				} else if (body.isRapist() && body.isRude()) {
					r = GameRandom.nextInt(6 + adjust);
				} else if (body.isRapist() || body.isRude()) {
					r = GameRandom.nextInt(12 + adjust);
				} else if (!body.isSoHungry() && !body.wantToShit()) {
					r = GameRandom.nextInt(24 + adjust);
				}
				// すっきりーしにいく条件判定
				boolean shouldExcite = false;
				// ぺにぺにがないとダメ
				if (body.isPenipeniCutted()) {
					r = 1;
				}
				// 大人じゃないとやらない(ドゲスの子ゆ除く)
				if (!body.isAdult() && !(body.isChild() && body.isVeryRude())) {
					r = 1;
				}
				// 妊娠してるとしない
				if (body.hasBabyOrStalk() && !body.isRaper()) {
					r = 1;
				}
				// if (isRaper() && (isExciting() || isForceExciting())) {
				// setCurrentEvent(null);
				// }
				if (r == 0 && body.getCurrentEvent() == null) {
					List<Yukkuri> fianceList = YukkuriLogic.createActiveFiances(body,
							body.getAgeState().ordinal());
					if (fianceList == null || fianceList.size() < 1) {
						body.setHappiness(Happiness.SAD);
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.WantPartner));
					}
					// 他にゆっくりがいる
					else {
						// レイパー
						if (body.isRapist()) {
							if (body.isRapist() && FamilyActionLogic.isRapeTarget()) {
								shouldExcite = true;
							}
						} else {
							// 自分の通常の子ゆリスト作成
							List<Yukkuri> childrenList = YukkuriLogic.createActiveChildren(body, true);
							// パートナーがいる場合
							Yukkuri pa = YukkuriRelations.getPartnerYukkuri(body);
							if (pa != null) {
								if (body.isVeryRude()) {
									// ドゲスはすぐ興奮
									shouldExcite = true;
								} else if (!pa.hasBabyOrStalk()) {
									if (childrenList == null || childrenList.size() == 0) {
										shouldExcite = true;
									} else {
										switch (body.getIntelligence()) {
											case WISE:
												// 賢いのは3匹以下で子づくり
												if (childrenList.size() <= 3) {
													shouldExcite = true;
												}
												break;
											case AVERAGE:
												// 普通の知性は10匹以下で子づくり
												if (childrenList.size() <= 10) {
													shouldExcite = true;
												}
												break;
											case FOOL:
												// 餡子脳は子の数を気にしない
												shouldExcite = true;
												break;
										}
									}
								}
							} else {
								// 独身orバツイチは、相手を探すために興奮する
								shouldExcite = true;
							}
						}
					}
				}

				if (shouldExcite) {
					body.clearActionsForEvent();
					body.setExciting(true);
					body.setExcitingPeriod(0);
					if (body.isRaper()) {
						EventLogic.addWorldEvent(new RaperWakeupEvent(body, null, null, 1), body,
								GameMessages.getMessage(body, MessagePool.Action.ExciteForRaper));
					} else {
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Excite));
					}
				} else {
					body.setRelax(true);
					body.setExcitingPeriod(0);
					if (GameRandom.nextInt(75) == 0) {
						body.killTime();
					}
				}
				body.setAngry(false);
				body.setScare(false);
			} else {
				body.setExcitingPeriod(body.getExcitingPeriod() + Entity.TICK + body.getExcitementPeriodBoost());
				if (body.getExcitingPeriod() > body.getExcitePeriodBase()) {
					body.setExcitingPeriod(0);
					if (!body.isRaper()) {
						body.setCalm();
					}
					body.setRelax(false);
				}
				// 興奮している場合、たまにつぶやく
				if (body.isExciting()) {
					if (GameRandom.nextInt(30) == 0) {
						if (body.isRaper()) {
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ExciteForRaper));
						} else {
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Excite));
						}
					}
				}
			}
		}
	}

	/**
	 * 声掛け.
	 *
	 * @param type 声掛けタイプ（0:ゆっくりしていってね 1:ゆっくりしないで死んでね 2:もるんもるんしてね）
	 */
	public void voiceReaction(int type) {
		if (body.getPanicType() != null || body.isDead()) {
			return;
		}
		if (!body.canAction()) {
			return;
		}
		switch (type) {
			case 0: {
				body.clearActions();
				body.setScare(false);
				body.setAngry(false);
				body.setFurifuri(false);
				if (!body.isRaper()) {
					body.setExciting(false);
				}
				body.setForceExciting(false);
				body.setNobinobi(false);
				body.setYunnyaa(false);
				body.setExcitingPeriod(0);
				body.setRelax(true);
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TakeItEasy));
				body.addStress(-100);
				body.wakeup();
				body.addLovePlayer(100);
				break;
			}
			case 1: {
				body.wakeup();
				body.clearActions();
				body.setAngry();
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Alarm));
				body.addStress(150);
				body.addLovePlayer(-100);
				if (GameRandom.nextBoolean()) {
					body.doYunnyaa(true);
				}
				break;
			}
			case 2: {
				body.wakeup();
				body.clearActions();
				if (body.isNeedled() || (body.isGotBurnedHeavily()) && body.getFootBakeLevel() != FootBake.CRITICAL) {
					if (body.isDamaged()) {
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Scream2), 30);
					} else {
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Scream), 30);
					}
					body.setHappiness(Happiness.VERY_SAD);
					body.setForceFace(ImageCode.PAIN.ordinal());
					body.setFurifuri(false);
					body.addStress(50);
				} else if (body.getFootBakeLevel() == FootBake.CRITICAL) {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.CantMove), 30);
					body.setHappiness(Happiness.VERY_SAD);
					body.setFurifuri(false);
					body.addStress(50);
				} else {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.FuriFuri), 30);
					body.setFurifuri(true);
					body.addStress(-50);
				}
				body.stay(30);
				break;
			}
			default:
				break;
		}
	}

}
