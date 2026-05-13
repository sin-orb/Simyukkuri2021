package src.entity.core.living.yukkuri;

import src.Const;
import src.entity.core.Entity;
import src.entity.core.attachment.impl.VeryShitAmpoule;
import src.entity.core.world.item.Toilet;
import src.enums.AgeState;
import src.enums.Attitude;
import src.enums.BurialState;
import src.enums.Burst;
import src.enums.CriticalDamegeType;
import src.enums.FootBake;
import src.enums.Happiness;
import src.enums.Intelligence;
import src.enums.PublicRank;
import src.enums.PurposeOfMoving;
import src.enums.UnbirthBabyState;
import src.event.EventPacket;
import src.system.MessagePool;
import src.util.GameMessages;
import src.util.GameRandom;
import src.util.GameView;

/**
 * うんうん関連処理をまとめる委譲クラス。
 */
public final class YukkuriShitDelegate {
	private final Yukkuri body;

	/**
	 * うんうん関連を扱う委譲を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriShitDelegate(Yukkuri body) {
		this.body = body;
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
		int shit = body.getShit();

		// 実ゆっくりの場合
		if (body.isUnBirth()) {
			// うんうんアンプルが刺さっている
			if (body.getAttachmentSize(VeryShitAmpoule.class) != 0) {
				// 限界を超えた場合のチェック
				if (shit > body.getShitLimitBase()[body.getBodyAgeState().ordinal()]) {
					int currentDamagePercent = 100 * body.getDamage() / body.getDamageLimit();
					// 現在のダメージがダメージ限界の1/10以下ならダメージを与える
					if (currentDamagePercent < 10) {
						body.addDamage(Const.NEEDLE * 5);
					}
					// あなる閉鎖時
					if (body.isAnalClose() || (body.isFixBack() && body.isNeedled())) {
						body.setHappiness(Happiness.VERY_SAD);
						// 破裂寸前までうんうんをためる
						if (body.getBurstState() != Burst.NEAR) {
							shit += Entity.TICK * 2 + (body.getExcretionBoost() * 20);
						}
					} else {
						// あなる未閉鎖
						body.makeDirty(true);
						// おくるみあり
						if (body.isHasPants()) {
							body.setHappiness(Happiness.VERY_SAD);
							shit = 1;
							body.clearActions();
						} else {
							body.setHappiness(Happiness.SAD);
							shit = 0;
							body.clearActions();
						}
					}
					body.setShitting(false);
					body.addStress(100);
					// 実ゆの場合、親が反応する
					if (GameRandom.nextInt(20) == 0) {
						body.checkReactionStalkMother(UnbirthBabyState.SAD);
					}
				} else {
					shit += Entity.TICK * 2 + (body.getExcretionBoost() * 20);
				}
			}
			body.setShit(shit);
			return true;
		}

		// うんうん無効判定
		// 溶けている場合,完全足焼きした場合,食事中、ぺろぺろ中、すっきり中はうんうんしない
		if ((body.getFootBakeLevel() == FootBake.CRITICAL && !body.isPealed()) ||
				body.isMelt() || body.isEating() || body.isPeropero() || body.isSukkiri() || body.isPacked()) {
			body.setShit(shit);
			return false;
		}
		// レイパー発情中はうんうん無効
		if (body.isRaper() && body.isExciting()) {
			body.setShitting(false);
			shit--;
			if (body.getPurposeOfMoving() == PurposeOfMoving.SHIT) {
				body.setPurposeOfMoving(null);
			}
			body.setStaying(false);
			body.setShit(shit);
			return false;
		}
		// 実験 イベント中は空腹、睡眠、便意が増えないように
		if (body.getCurrentEvent() != null && body.getCurrentEvent().getPriority() != EventPacket.EventPriority.LOW) {
			body.setShit(shit);
			return false;
		}

		// うんうん蓄積処理
		// うんうんの蓄積の減少度判定
		int dropChanceDivisor = 1;
		// うんうん奴隷
		if (body.getPublicRank() == PublicRank.UnunSlave) {
			if (!body.isShitting()) {
				dropChanceDivisor = 5;
			}
		}
		// 地中
		if (body.getBurialState() != BurialState.NONE) {
			dropChanceDivisor = 10;
		}

		boolean cantMove = false;
		// 蓄積実行
		if (GameRandom.nextInt(dropChanceDivisor) == 0) {
			if (body.isFull()) {
				shit += Entity.TICK * 2 + (body.getExcretionBoost() * 20);
			} else {
				shit += Entity.TICK + (body.getExcretionBoost() * 20);
			}
		}

		// ちぎれ状態の場合は餡子を漏らす
		if ((body.getCriticalDamege() == CriticalDamegeType.CUT || body.isPealed()) &&
				body.getBurialState() == BurialState.NONE) {
			if (shit > body.getShitLimitBase()[body.getBodyAgeState().ordinal()] - Entity.TICK * Const.SHITSTAY * 2) {
				GameView.addCrushedVomit(body.getX() + 3 - GameRandom.nextInt(6), body.getY() - 2, 0,
						body,
						body.getShitType());
				body.addDamage(Const.NEEDLE * 2);
				shit = 1;
				if (body.getExcretionBoost() > 0) {
					body.setExcretionBoost(body.getExcretionBoost() - 1);
					body.strike(Const.NEEDLE * 2);
				}
				body.setShit(shit);
				return true;
			}
		}

		// 寝ている場合はうんうん限界の1.5倍までは我慢できる
		if (body.isSleeping()) {
			if (shit < (body.getShitLimitBase()[body.getBodyAgeState().ordinal()] * 1.5f)) {
				body.setShitting(false);
				body.setShit(shit);
				return false;
			}
		}

		// 非ゆっくり症ではない場合
		if (body.isNotNYD() && body.getBurialState() == BurialState.NONE) {
			// うんうん奴隷ではない場合
			if (body.getPublicRank() != PublicRank.UnunSlave) {
				Entity oTarget = body.takeMoveTarget();
				// もしトイレに到着していたら即排泄へ
				if (body.isToShit() && oTarget instanceof Toilet) {
					if (((Toilet) oTarget).checkHitObj(body)) {
						if (shit < body.getShitLimitBase()[body.getBodyAgeState().ordinal()]
								- Entity.TICK * Const.SHITSTAY + 1) {
							shit = body.getShitLimitBase()[body.getBodyAgeState().ordinal()]
									- Entity.TICK * Const.SHITSTAY + 1;
						}
					} else if (body.checkOnBed()) {// トイレがある場合
						// 大人で寝てたなら起きる
						if (body.getBodyAgeState() == AgeState.ADULT && body.isSleeping()) {
							body.wakeup();
						}
						// トイレに到着していないかつベッドの上では我慢する
						if (shit < (body.getShitLimitBase()[body.getBodyAgeState().ordinal()] * 1.5f)) {
							body.setShitting(false);
							body.setShit(shit);
							return false;
						}
					} else if ((body.getAttitude() == Attitude.NICE || body.getAttitude() == Attitude.VERY_NICE)
							|| (body.getAttitude() == Attitude.AVERAGE
									&& body.getIntelligence() == Intelligence.WISE)) {
						// 性格が善良か普通でも知能が高ければトイレに着くまで150%まで我慢できる
						if (shit < (body.getShitLimitBase()[body.getBodyAgeState().ordinal()] * 1.5f)) {
							body.setShitting(false);
							body.setShit(shit);
							return false;
						}
					}
				}
				// トイレがない場合
				else if (body.checkOnBed()) {
					// ベッドの上では我慢する
					if (shit < (body.getShitLimitBase()[body.getBodyAgeState().ordinal()] * 1.5f)) {
						body.setShitting(false);
						body.setShit(shit);
						return false;
					}
				}
			}

			// 限界が近づいたら排泄チェック
			if (shit > body.getShitLimitBase()[body.getBodyAgeState().ordinal()] - Entity.TICK * Const.SHITSTAY) {
				// あなるがふさがれていない
				if (!body.isAnalClose() && !(body.isFixBack() && body.isNeedled())) {
					// 寝ているか埋まっているか粘着床(あんよ固定)についているか針が刺さっていたら体勢をかえられずに漏らす
					if ((body.isLockmove() && !body.isFixBack()) || body.isSleeping() || body.isNeedled()
							|| body.getBurialState() != BurialState.NONE) {
						body.makeDirty(true);
						body.setHappiness(Happiness.VERY_SAD);
						body.addStress(150);
						shit = 0;
						body.clearActions();
						if (body.getExcretionBoost() > 0) {
							body.setExcretionBoost(body.getExcretionBoost() - 1);
							body.addDamage(Const.NEEDLE * 2);
							body.addStress(400);
						}
						body.setShit(shit);
						return true;
					}
				}

				// 排泄準備
				if (body.isHasPants()) {
					body.setHappiness(Happiness.SAD);
				}

				// あなるがふさがれていない
				if (!body.isAnalClose() && !(body.isFixBack() && body.isNeedled())) {
					if (body.getAge() % 100 == 0) {
						if (!body.isShitting()) {
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Shit),
									Entity.TICK * Const.SHITSTAY);
							body.stay();
							body.wakeup();
							body.setShitting(true);
							cantMove = true;
							body.setShit(shit);
							return cantMove;
						}
					}
				}
				if (body.isShitting()) {
					cantMove = true;
				}
			} else {
				// While shitting is true, the yukkuri might grow up. So, these flags should be
				// clear.
				body.setShitting(false);
				cantMove = false;
			}
		}

		// 限界を超えた場合のチェック
		if (shit > body.getShitLimitBase()[body.getBodyAgeState().ordinal()]) {
			// 肛門が塞がれてなければ排泄
			if (!body.isAnalClose() && !(body.isFixBack() || body.isNeedled())
					&& body.getBurialState() == BurialState.NONE) {
				body.setShitting(false);
				body.clearActions();
				shit = 0;
				if (body.getBodyAgeState() == AgeState.BABY) {
					body.makeDirty(true);
					body.setHappiness(Happiness.SAD);
					body.addStress(200);
				}

				if (body.isHasPants() || body.isNYD()) {
					body.makeDirty(true);
					body.setHappiness(Happiness.VERY_SAD);
					body.addStress(400);
				}

				if (body.isNotNYD()) {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Shit2));
					body.stay();
					if (!body.isHasPants()) {
						if (body.willingFurifuri()) {
							body.setFurifuri(true);
							body.addStress(-200);
						}
						body.stay();
						body.addStress(-100);
					}
				}

				if (body.getExcretionBoost() > 0) {
					body.setExcretionBoost(body.getExcretionBoost() - 1);
					body.addDamage(Const.NEEDLE * 2);
					body.addStress(400);
				}
			} else {
				// 塞がってたら膨らんで破裂
				body.wakeup();
				if (body.isNotNYD()) {
					if (body.getBurstState() == Burst.NEAR) {
						if (GameRandom.nextInt(10) == 0) {
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Inflation));
							body.stay();
						}
					} else {
						if (GameRandom.nextInt(10) == 0) {
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.CantShit), true);
							body.stay();
						}
					}
				}

				body.setHappiness(Happiness.SAD);
				// if(GameRandom.nextInt(4) == 0){
				shit += Entity.TICK + (body.getExcretionBoost() * 10);
				// }

				if (!body.isAnalClose() || body.getAge() % 100 == 0) {
					body.setShitting(false);
				}
				body.addStress(1);
			}
		}

		body.setShit(shit);
		return cantMove;
	}
}
