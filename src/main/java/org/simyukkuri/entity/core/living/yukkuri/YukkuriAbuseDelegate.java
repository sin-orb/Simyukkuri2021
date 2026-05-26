package org.simyukkuri.entity.core.living.yukkuri;

import java.util.Map;
import org.simyukkuri.Const;
import org.simyukkuri.entity.core.attachment.impl.Fire;
import org.simyukkuri.entity.core.attachment.impl.Needle;
import org.simyukkuri.entity.core.world.item.StickyPlate;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.HairState;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.PanicType;
import org.simyukkuri.enums.UnbirthBabyState;
import org.simyukkuri.event.impl.ProposeEvent;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;

/**
 * 外見破壊や軽度の虐待系の振る舞いを扱う delegate.
 */
public final class YukkuriAbuseDelegate {
	private final Yukkuri body;

	/**
	 * 虐待系を扱う delegate を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriAbuseDelegate(Yukkuri body) {
		this.body = body;
	}

	/**
	 * おさげを破壊する.
	 */
	public void takeBraid() {
		if (body.isDead() || !body.isBraidType()) {
			return;
		}
		body.wakeup();
		body.clearActions();
		if (body.isHasBraid()) {
			body.addLovePlayer(-300);
			body.addStress(1200);
			body.setHasBraid(false);
			body.setForceFace(ImageCode.CRYING.ordinal());
			body.setHappiness(Happiness.VERY_SAD);
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.BraidCut), true);
			body.checkReactionStalkMother(UnbirthBabyState.SAD);
		} else {
			body.addLovePlayer(200);
			body.addStress(-1000);
			body.setHasBraid(true);
			body.setForceFace(ImageCode.EMBARRASSED.ordinal());
			body.setHappiness(Happiness.HAPPY);
		}
	}

	/**
	 * 皮むきまたは治す（トグル）.
	 */
	public void peal() {
		if (body.isDead()) {
			return;
		}
		if (body.isPealed()) {
			body.setMelt(false);
			body.setCriticalDamege(null);
			body.setBakePeriod(0);
			body.setPealed(false);
			return;
		}
		if (body.isPacked()) {
			packInternal();
			return;
		}
		body.wakeup();
		body.clearActions();
		body.addLovePlayer(-300);
		body.setPealed(true);
		body.setShutmouth(false);
		body.setHairState(HairState.BALDHEAD);
		body.setHappiness(Happiness.VERY_SAD);
		body.setMessage(GameMessages.getMessage(body, MessagePool.Action.PEALING), true);
		body.checkReactionStalkMother(UnbirthBabyState.SAD);
	}

	/**
	 * 饅頭化または治す（トグル）.
	 */
	public void pack() {
		if (body.isDead()) {
			return;
		}
		if (body.isPacked()) {
			body.setCanTalk(true);
			body.closeAnal(false);
			body.castrateYukkuri(false);
			body.setPacked(false);
			peal();
			return;
		}
		body.wakeup();
		body.clearActions();
		body.addLovePlayer(-300);
		body.setPealed(false);
		body.setOkazaris(null);
		body.setHasBaby(false);
		body.closeAnal(true);
		body.castrateYukkuri(true);
		body.setCanTalk(false);
		body.setBlind(true);
		body.setHairState(HairState.BALDHEAD);
		if (body.isBraidType()) {
			body.setHasBraid(false);
		}
		body.setPacked(true);
		body.setHappiness(Happiness.VERY_SAD);
		body.checkReactionStalkMother(UnbirthBabyState.KILLED);
	}

	/**
	 * 目破壊または修復（トグル）.
	 */
	public void breakeyes() {
		if (body.isDead()) {
			return;
		}
		if (body.isBlind()) {
			body.setBlind(false);
			body.setEyesightBase(400 * 400);
			return;
		}
		if (body.isSleeping()) {
			body.wakeup();
		}
		body.clearActions();
		body.addLovePlayer(-200);
		body.setBlind(true);
		body.setHappiness(Happiness.VERY_SAD);
		body.setPikoMessage(GameMessages.getMessage(body, MessagePool.Action.BLINDING), true);
		body.checkReactionStalkMother(UnbirthBabyState.SAD);
	}

	/**
	 * 口ふさぎまたは修復（トグル）.
	 */
	public void shutMouth() {
		if (body.isDead()) {
			return;
		}
		if (body.isShutmouth()) {
			body.setShutmouth(false);
			return;
		}
		body.wakeup();
		body.clearActions();
		body.addLovePlayer(-200);
		body.setShutmouth(true);
		body.setPikoMessage(GameMessages.getMessage(body, MessagePool.Action.CantTalk), true);
		body.setHappiness(Happiness.VERY_SAD);
		body.checkReactionStalkMother(UnbirthBabyState.SAD);
	}

	/**
	 * 髪をむしる.
	 */
	public void pickHair() {
		if (body.isDead()) {
			return;
		}
		switch (body.getHairState()) {
			case BALDHEAD:
				body.setHairState(HairState.DEFAULT);
				body.setHappiness(Happiness.HAPPY);
				body.addLovePlayer(100);
				return;
			case DEFAULT:
				body.setHairState(HairState.BRINDLED1);
				break;
			case BRINDLED1:
				body.setHairState(HairState.BRINDLED2);
				break;
			case BRINDLED2:
				body.setHairState(HairState.BALDHEAD);
				break;
			default:
		}

		body.wakeup();
		body.clearActions();
		body.addLovePlayer(-200);
		body.addStress(500);
		if (GameRandom.nextInt(3) == 0) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Scream), true);
			body.setForceFace(ImageCode.PAIN.ordinal());
		} else {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.PLUNCKING), true);
			body.setForceFace(ImageCode.CRYING.ordinal());
		}

		body.stayPurupuru(30);
		body.setHappiness(Happiness.VERY_SAD);
		body.checkReactionStalkMother(UnbirthBabyState.ATTACKED);
	}

	/**
	 * あにゃる閉鎖する.
	 *
	 * @param flag あにゃる閉鎖するか否か
	 */
	public void closeAnal(boolean flag) {
		if (body.isDead()) {
			return;
		}
		body.setAnalClose(flag);
		if (!body.canAction()) {
			return;
		}

		if (body.isAnalClose()) {
			body.setHappiness(Happiness.HAPPY);
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.AnalSealed));
		} else {
			body.setHappiness(Happiness.AVERAGE);
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ToFreedom));
			body.stay();
		}
	}

	/**
	 * 茎去勢を設定する.
	 */
	public void castrateStalk(boolean flag) {
		if (body.isDead()) {
			return;
		}
		body.setStalkCastration(flag);
		if (!body.canAction()) {
			return;
		}
		if (body.isNotNyd()) {
			if (body.isStalkCastration()) {
				body.setHappiness(Happiness.VERY_SAD);
				body.addStress(1000);
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Alarm));
				body.addLovePlayer(-400);
				if (GameRandom.nextBoolean()) {
					body.doYunnyaa(true);
				}
			} else {
				body.setHappiness(Happiness.AVERAGE);
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ToFreedom));
				body.addLovePlayer(400);
			}
		}
	}

	/**
	 * 胎生去勢を設定する.
	 */
	public void castrateYukkuri(boolean flag) {
		if (body.isDead()) {
			return;
		}
		body.setCastrated(flag);
		if (!body.canAction()) {
			return;
		}
		if (body.isNotNyd()) {
			if (body.isCastrated()) {
				body.setHappiness(Happiness.VERY_SAD);
				body.addStress(1000);
				body.addLovePlayer(-400);
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Alarm));
				if (GameRandom.nextBoolean()) {
					body.doYunnyaa(true);
				}
			} else {
				body.setHappiness(Happiness.AVERAGE);
				body.addLovePlayer(400);
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ToFreedom));
			}
		}
	}

	/**
	 * 火をつける.
	 */
	public void giveFire() {
		if (body.isBurned() || body.getAttachmentSize(Fire.class) != 0 || body.isCrushed()) {
			return;
		}

		body.clearActions();
		if (!body.isDead()) {
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
			body.setHappiness(Happiness.VERY_SAD);
			body.addLovePlayer(-500);
			body.checkReactionStalkMother(UnbirthBabyState.ATTACKED);
		}

		if (body.isNotNyd() && !body.isUnBirth()) {
			body.setPanicType(PanicType.BURN);
		}
		body.setWet(false);
		body.setWetPeriod(0);
		body.getAttach().add(new Fire(body));
	}

	/**
	 * 針刺しを設定する.
	 *
	 * @param needleOn 針刺し
	 */
	public void setNeedle(boolean needleOn) {
		if (body.getAttachmentSize(Needle.class) != 0) {
			if (!needleOn) {
				if (body.isUnBirth()) {
					body.setCanTalk(false);
				}

				body.setNeedled(false);
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.NeedleRemove));
				body.removeAttachment(Needle.class);

				Map<Integer, StickyPlate> stickyPlates = GameWorld.get().getCurrentWorldState().getStickyPlates();
				boolean resetBackFix = true;
				for (Map.Entry<Integer, StickyPlate> entry : stickyPlates.entrySet()) {
					StickyPlate s = entry.getValue();
					if (s.getBoundYukkuri() == body) {
						resetBackFix = false;
						break;
					}
				}
				if (resetBackFix) {
					body.setFixBack(false);
				}
			}
		} else {
			if (needleOn) {
				body.getAttach().add(new Needle(body));

				if (!body.isDead()) {
					if (body.isUnBirth()) {
						body.setCanTalk(true);
					}

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
					body.setFurifuri(false);
					body.setBeVain(false);
					body.setEating(false);
					body.setPeropero(false);
					body.setSukkiri(false);
					body.setScare(false);
					body.setEatingShit(false);
					body.setNobinobi(false);
					body.setYunnyaa(false);
					body.setInOutTakeoutItem(false);
					if (body.canflyCheck()) {
						body.moveToZ(0);
					}
					body.clearActions();
					body.addLovePlayer(-20);
					body.setHappiness(Happiness.VERY_SAD);
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.NeedleStick), true);
					body.checkReactionStalkMother(UnbirthBabyState.ATTACKED);
				}
				body.setNeedled(true);
			}
		}
	}

	/**
	 * 持つ.
	 */
	public void hold() {
		if (body.isDead()) {
			return;
		}
		if (body.canPullOrPush()) {
			body.setCanPullOrPush(false);
			body.setLockmove(false);
			return;
		}
		// なつき度設定
		if (body.getZ() > 0) {
			body.setCalcZ(0);
		}
		GameEnvironment.setAlarm();
		body.setCanPullOrPush(true);
		body.setLockmove(true);
		body.setHappiness(Happiness.SAD);
		body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Press));
		// 実ゆの場合、親が反応する
		// body.checkReactionStalkMother(UnbirthBabyState.SAD);
	}

	/**
	 * 押さえつけ.
	 *
	 * @param force 強制かどうか
	 */
	public void lockSetZ(int force) {
		body.setExternalPressure(force);
		if (body.getExternalPressure() == 0) {
			return;
		}
		if (body.isDead()) {
			return;
		}
		body.clearActions();
		body.setAngry();
		if (body.getExternalPressure() < 0) {
			if (body.getExternalPressure() < Const.EXT_FORCE_PUSH_LIMIT[body.getAgeState().ordinal()]) {
				body.setLockmove(false);
				body.setExternalPressure(0);
				body.setCalcZ(0);
				body.bodyBurst();
			} else if (body
					.getExternalPressure() < (Const.EXT_FORCE_PUSH_LIMIT[body.getAgeState().ordinal()] >> 1)) {
				if (GameRandom.nextInt(10) == 0) {
					body.setHappiness(Happiness.VERY_SAD);
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Press2), Const.HOLDMESSAGE, true,
							true);
					body.addStress(25);
				}
				if (GameRandom.nextInt(80) == 0) {
					body.bodyBurst();
				}
			}
		} else if (body.getExternalPressure() > 0) {
			if (body.getExternalPressure() > Const.EXT_FORCE_PULL_LIMIT[body.getAgeState().ordinal()]) {
				body.setLockmove(false);
				body.setExternalPressure(0);
				body.bodyCut();
			} else if (body.getExternalPressure() > Const.EXT_FORCE_PULL_LIMIT[body.getAgeState().ordinal()] >> 1) {
				if (GameRandom.nextInt(10) == 0) {
					body.setHappiness(Happiness.VERY_SAD);
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Press2), Const.HOLDMESSAGE, true,
							true);
					body.addStress(25);
				}
				if (GameRandom.nextInt(80) == 0) {
					body.bodyBurst();
				}
			}
		}
	}

	/**
	 * お仕置き.
	 */
	public void strikeByPunish() {
		if (body.isDead()) {
			return;
		}
		if (body.isIdiot()) {
			body.strike(org.simyukkuri.Const.NEEDLE);
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Scream), true);
			body.setAngry();
			return;
		}

		body.addLovePlayer(-10);
		body.teachManner(1);
		body.strike(org.simyukkuri.Const.NEEDLE);
		if (body.getCurrentEvent() instanceof ProposeEvent) {
			body.setForceFace(ImageCode.CRYING.ordinal());
			if (body.isDamaged()) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Scream), true);
			} else {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Surprise), true);
			}
		} else {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Scream), true);
		}
		body.clearActions();
		body.setAngry();
		body.dropAllTakeoutItem();
		body.checkReactionStalkMother(UnbirthBabyState.ATTACKED);
	}

	/**
	 * ハンマー.
	 */
	public void strikeByHammer() {
		if (body.isDead()) {
			return;
		}
		body.addLovePlayer(-200);
		body.strike(org.simyukkuri.Const.HAMMER);
		body.setPikoMessage(GameMessages.getMessage(body, MessagePool.Action.Scream), true);
		body.setAngry();
		if (body.isDead()) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Dying), true);
			body.stay();
			if (body.getAgeState() != AgeState.ADULT) {
				body.setCrushed(true);
			}
		}

		body.begForLife();
		body.dropAllTakeoutItem();
		body.checkReactionStalkMother(UnbirthBabyState.ATTACKED);
	}

	/**
	 * 押さえつけ.
	 */
	public void strikeByPress() {
		if (!body.isDead()) {
			body.strike(org.simyukkuri.Const.HAMMER * 10);
			body.setPikoMessage(GameMessages.getMessage(body, MessagePool.Action.Scream), true);
			body.setAngry();
		}
		if (body.isDead()) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Dying), 40, true, true);
			body.stay();
			body.setCrushed(true);
		}
	}

	/**
	 * パンチ.
	 */
	public void strikeByPunch() {
		if (body.isDead()) {
			return;
		}

		body.addLovePlayer(-500);
		body.strike(body.getDamageLimitBase()[body.getAgeState().ordinal()] / 5);
		body.setCalm();

		body.setPikoMessage(GameMessages.getMessage(body, MessagePool.Action.Scream), true);
		body.setAngry();
		if (body.isDead()) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Dying), true);
			body.stay();
		}

		body.begForLife();
		body.dropAllTakeoutItem();
		body.checkReactionStalkMother(UnbirthBabyState.ATTACKED);
	}

	private void packInternal() {
		body.setCanTalk(true);
		body.closeAnal(false);
		body.castrateYukkuri(false);
		body.setPacked(false);
		peal();
	}
}
