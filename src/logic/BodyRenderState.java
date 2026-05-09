package src.logic;

import src.Const;
import src.SimYukkuri;
import src.base.Yukkuri;
import src.entity.world.bodylinked.Okazari;
import src.entity.world.bodylinked.Okazari.OkazariType;
import src.enums.BodyBake;
import src.enums.BodyRank;
import src.enums.CriticalDamegeType;
import src.enums.FootBake;
import src.enums.HairState;
import src.enums.ImageCode;
import src.item.Sui;
import src.system.BodyLayer;
import src.system.MainCommandUI;
import src.util.GameRandom;

/**
 * {@link Yukkuri} の描画状態計算を扱う補助クラス。
 * <p>
 * Phase 2 では、描画用レイヤ構築のうち表情選択を {@link Yukkuri} から切り出し、
 * public API は facade として残す。
 * </p>
 */
public final class BodyRenderState {
	private BodyRenderState() {
	}

	/**
	 * 顔グラフィックをレイヤへ積み上げる。
	 *
	 * @param body  更新対象のゆっくり
	 * @param layer 描画先レイヤ
	 * @return 追加した画像数
	 */
	public static int getFaceImage(Yukkuri body, BodyLayer layer) {
		int direction = body.getDirection().ordinal();
		int idx = 0;

		layer.getOption()[0] = 0;
		applyFaceJumpOption(body, layer);

		if (body.isNYD()) {
			layer.getOption()[0] = 0;
		}

		if (body.getForceFace() != -1) {
			idx += appendFace(body, body.getForceFace(), direction, layer, idx);
			return appendFaceOverlays(body, direction, layer, idx);
		}

		if (body.isDead()) {
			idx += appendFace(body, body.isPealed() ? ImageCode.PEALEDDEADFACE.ordinal() : ImageCode.DEAD.ordinal(),
					direction, layer, idx);
		} else if (body.isUnBirth()) {
			idx += appendFace(body, ImageCode.SLEEPING.ordinal(), direction, layer, idx);
		} else if (body.isPealed()) {
			idx += appendFace(body, ImageCode.PEALEDFACE.ordinal(), direction, layer, idx);
		} else if (body.isNYD()) {
			idx += appendFace(body,
					body.isUnBirth() ? ImageCode.NYD_FRONT_CRY2.ordinal() : ImageCode.NYD_FRONT_WIDE.ordinal(), direction,
					layer, idx);
		} else if (body.getCriticalDamege() == src.enums.CriticalDamegeType.CUT) {
			idx += appendFace(body, ImageCode.PAIN.ordinal(), direction, layer, idx);
		} else if (body.isExciting()) {
			idx += appendFace(body,
					body.isAliceRaperForRender() ? ImageCode.EXCITING_raper.ordinal() : ImageCode.EXCITING.ordinal(),
					direction, layer, idx);
		} else if (body.isSleeping() && (!body.isUnBirth() || (body.getDamage() <= 0)) && !body.isNeedled()) {
			idx += appendSleepingFace(body, direction, layer, idx);
		} else if (body.isTalking() && (body.isYunnyaa() || body.isBeggingForLife())) {
			idx += appendFace(body, ImageCode.CRYING.ordinal(), direction, layer, idx);
		} else if (body.isPeropero() || body.isEating() || body.isInOutTakeoutItem()) {
			idx += appendEatingFace(body, direction, layer, idx);
		} else if (body.isSukkiri()) {
			idx += appendFace(body, ImageCode.REFRESHED.ordinal(), direction, layer, idx);
		} else if (body.isDamaged() || body.isSick() || body.isFeelPain()) {
			idx += appendDamagedFace(body, direction, layer, idx);
		} else {
			idx += appendNormalStateFace(body, direction, layer, idx);
		}

		return appendFaceOverlays(body, direction, layer, idx);
	}

	/**
	 * 胴体のベースグラフィックをレイヤへ積み上げる。
	 *
	 * @param body  更新対象のゆっくり
	 * @param layer 描画先レイヤ
	 * @return 追加した画像数
	 */
	public static int getBodyBaseImage(Yukkuri body, BodyLayer layer) {
		int direction = body.getDirection().ordinal();
		int idx = 0;

		layer.getOption()[0] = 0;
		layer.getOption()[1] = 0;
		layer.getOption()[2] = 0;

		if (body.isBurned() && body.isDead()) {
			idx += appendFace(body, ImageCode.BURNED.ordinal(), Const.LEFT, layer, idx);
		} else if (body.isCrushed()) {
			idx += appendCrushedBody(body, layer, idx);
		} else if (body.isPacked()) {
			idx += appendPackedBody(body, layer, idx);
		} else if (body.isShitting() || body.isBirth() && body.getBabyTypes().size() > 0
				|| (body.isFixBack() && !body.isFurifuri())) {
			idx += appendFrontBody(body, layer, idx);
		} else if (body.isFurifuri() && !body.isUnBirth() && !body.isSleeping() && (!body.isLockmove() || body.isFixBack())) {
			idx += appendRollingBody(body, layer, idx);
		} else {
			idx += appendNormalBody(body, direction, layer, idx);
			layer.getOption()[0] = 1;
		}
		return idx;
	}

	/**
	 * 切断や溶解など、通常時以外の胴体エフェクトをレイヤへ積み上げる。
	 *
	 * @param body  更新対象のゆっくり
	 * @param layer 描画先レイヤ
	 * @return 追加した画像数
	 */
	public static int getAbnormalBodyImage(Yukkuri body, BodyLayer layer) {
		int direction = body.getDirection().ordinal();
		int idx = 0;

		if (body.getCriticalDamege() != null) {
			if (body.getCriticalDamege() == CriticalDamegeType.CUT) {
				idx += appendFace(body, ImageCode.BODY_CUT.ordinal(), direction, layer, idx);
			} else {
				idx += appendFace(body, ImageCode.BODY_INJURED.ordinal(), direction, layer, idx);
			}
		}
		if (body.isMelt()) {
			idx += appendFace(body, body.isPealed() ? ImageCode.MELT_PEALED.ordinal() : ImageCode.MELT.ordinal(),
					direction, layer, idx);
		}
		return idx;
	}

	/**
	 * 汚れや空腹などの体表エフェクトをレイヤへ積み上げる。
	 *
	 * @param body  更新対象のゆっくり
	 * @param layer 描画先レイヤ
	 * @return 追加した画像数
	 */
	public static int getEffectImage(Yukkuri body, BodyLayer layer) {
		int direction = body.getDirection().ordinal();
		int idx = 0;

		if (body.isDead()) {
			idx += appendFace(body, ImageCode.DEAD_BODY.ordinal(), direction, layer, idx);
		}

		if (body.isTooHungry()) {
			idx += appendFace(body, ImageCode.HUNGRY2.ordinal(), direction, layer, idx);
		} else if (body.isVeryHungry()) {
			idx += appendFace(body, ImageCode.HUNGRY1.ordinal(), direction, layer, idx);
		} else if (body.isSoHungry()) {
			idx += appendFace(body, ImageCode.HUNGRY0.ordinal(), direction, layer, idx);
		}

		FootBake footBake = body.getFootBakeLevel();
		if (footBake == FootBake.MIDIUM) {
			idx += appendFace(body, ImageCode.FOOT_BAKE0.ordinal(), direction, layer, idx);
		} else if (footBake == FootBake.CRITICAL) {
			idx += appendFace(body, ImageCode.FOOT_BAKE1.ordinal(), direction, layer, idx);
		}

		BodyBake bodyBake = body.getBodyBakeLevel();
		if (bodyBake == BodyBake.MIDIUM) {
			idx += appendFace(body, ImageCode.BODY_BAKE0.ordinal(), direction, layer, idx);
		} else if (bodyBake == BodyBake.CRITICAL) {
			idx += appendFace(body, ImageCode.BODY_BAKE1.ordinal(), direction, layer, idx);
		}

		idx += appendDamageEffect(body, direction, layer, idx);

		if (body.isHasPants()) {
			idx += appendFace(body, ImageCode.PANTS.ordinal(), direction, layer, idx);
		}
		if (body.isNormalDirty()) {
			idx += appendFace(body, ImageCode.STAIN.ordinal(), direction, layer, idx);
		}
		if (body.isStubbornlyDirty()) {
			idx += appendFace(body, ImageCode.STAIN2.ordinal(), direction, layer, idx);
		}

		idx += appendSickEffect(body, direction, layer, idx);

		if (body.isWet()) {
			idx += appendFace(body, ImageCode.WET.ordinal(), direction, layer, idx);
		}
		return idx;
	}

	/**
	 * おかざりグラフィックをレイヤへ積み上げる。
	 *
	 * @param body  更新対象のゆっくり
	 * @param layer 描画先レイヤ
	 * @param type  0 なら前方、1 なら後方
	 * @return 追加した画像数
	 */
	public static int getOlazariImage(Yukkuri body, BodyLayer layer, int type) {
		int direction = body.getDirection().ordinal();
		int idx = 0;

		if (body.getOkazari() == null) {
			layer.getImage()[idx] = null;
			return idx + 1;
		}
		if (type == 0) {
			if (body.getOkazari().getOkazariType() == OkazariType.DEFAULT) {
				return idx + appendFace(body, ImageCode.ACCESSORY.ordinal(), direction, layer, idx);
			}
			layer.getImage()[idx] = Okazari.getOkazariImage(body.getOkazari().getOkazariType(), direction);
			return idx + 1;
		}
		return idx + appendFace(body, ImageCode.ACCESSORY_BACK.ordinal(), direction, layer, idx);
	}

	/**
	 * おさげ、羽、尻尾のグラフィックをレイヤへ積み上げる。
	 *
	 * @param body  更新対象のゆっくり
	 * @param layer 描画先レイヤ
	 * @param type  0 なら前方、1 なら後方
	 * @return 追加した画像数
	 */
	public static int getBraidImage(Yukkuri body, BodyLayer layer, int type) {
		int direction = body.getDirection().ordinal();
		int idx = 0;

		if (type == 0) {
			if (body.hasBraidCheck()) {
				if (body.canflyCheck()) {
					idx += appendFace(body, (int) (ImageCode.BRAID_MV0.ordinal() + ((body.getAge() % 6) >> 1)),
							direction, layer, idx);
				} else if (body.isPikopiko()) {
					idx += appendFace(body, (int) (ImageCode.BRAID_MV0.ordinal() + ((body.getAge() % 6) >> 1)),
							direction, layer, idx);
				} else {
					idx += appendFace(body, ImageCode.BRAID.ordinal(), direction, layer, idx);
				}
			} else {
				idx += appendFace(body, ImageCode.BRAID_CUT.ordinal(), direction, layer, idx);
			}
		} else if (body.hasBraidCheck()) {
			if (body.canflyCheck()) {
				idx += appendFace(body, (int) (ImageCode.BRAID_BACK_MV0.ordinal() + ((body.getAge() % 6) >> 1)),
						direction, layer, idx);
			} else if (body.isPikopiko()) {
				idx += appendFace(body, (int) (ImageCode.BRAID_BACK_MV0.ordinal() + ((body.getAge() % 6) >> 1)),
						direction, layer, idx);
			} else {
				idx += appendFace(body, ImageCode.BRAID_BACK.ordinal(), direction, layer, idx);
			}
		}
		return idx;
	}

	private static void applyFaceJumpOption(Yukkuri body, BodyLayer layer) {
		if (body.isFlyingType()) {
			if (!body.isGrabbed() && !body.isSleeping() && !body.isPurupuru()) {
				if (body.isExciting()) {
					layer.getOption()[0] = 1;
				} else if (body.isSukkiri()) {
					layer.getOption()[0] = 2;
				} else if (body.isNobinobi()) {
					layer.getOption()[0] = 4;
				} else if (body.isYunnyaa() || body.isBeggingForLife()) {
					layer.getOption()[0] = 5;
				} else if (!body.isLockmove() && body.canflyCheck() && !body.isDontJump()) {
					layer.getOption()[0] = 3;
				}
			}
			return;
		}
		if (!body.isGrabbed() && body.getZ() == 0 && !body.isSleeping() && !body.isPurupuru()) {
			if (body.isExciting() && !body.isDontJump() && !body.isNeedled()) {
				layer.getOption()[0] = 1;
			} else if (body.isSukkiri()) {
				layer.getOption()[0] = 2;
			} else if (body.isNobinobi()) {
				layer.getOption()[0] = 4;
			} else if (body.isYunnyaa() || body.isBeggingForLife()) {
				layer.getOption()[0] = 5;
			} else if (!body.isLockmove() && !body.isDontJump() && body.takeMappedObj(body.getParentLinkId()) == null
					&& !body.isPeropero() && !(body.isEating() && !body.isPikopiko())) {
				layer.getOption()[0] = 3;
			}
		}
	}

	private static int appendSleepingFace(Yukkuri body, int direction, BodyLayer layer, int idx) {
		if (!SimYukkuri.UNYO) {
			return appendFace(body, body.isNightmare() ? ImageCode.NIGHTMARE.ordinal() : ImageCode.SLEEPING.ordinal(),
					direction, layer, idx);
		}
		if (body.getBlinkType() != ImageCode.SLEEPING.ordinal()
				&& body.getBlinkType() != ImageCode.NIGHTMARE.ordinal()) {
			body.setBlinkCount(0);
		}
		if (body.getBlinkCount() >= 0 && body.getBlinkCount() <= 2) {
			idx += appendFace(body, ImageCode.NORMAL0.ordinal(), direction, layer, idx);
			idx += appendFace(body, ImageCode.EYE2.ordinal(), direction, layer, idx);
		} else if (body.getBlinkCount() >= 3 && body.getBlinkCount() <= 5) {
			idx += appendFace(body, ImageCode.NORMAL0.ordinal(), direction, layer, idx);
			idx += appendFace(body, ImageCode.EYE3.ordinal(), direction, layer, idx);
		} else {
			idx += appendFace(body, body.isNightmare() ? ImageCode.NIGHTMARE.ordinal() : ImageCode.SLEEPING.ordinal(),
					direction, layer, idx);
		}
		body.setBlinkType(ImageCode.SLEEPING.ordinal());
		if (MainCommandUI.getSelectedGameSpeed() != 0) {
			body.setBlinkCount(body.getBlinkCount() + 1);
		}
		return idx;
	}

	private static int appendEatingFace(Yukkuri body, int direction, BodyLayer layer, int idx) {
		if (body.isStrike() || body.isVerySad() || body.isFeelHardPain()) {
			return appendFace(body, ImageCode.CRYING.ordinal(), direction, layer, idx);
		}
		if (body.isSad() || body.isEatingShit() || body.isFeelPain()) {
			return appendTiredFace(body, direction, layer, idx);
		}
		return appendFace(body, ImageCode.SMILE.ordinal(), direction, layer, idx);
	}

	private static int appendDamagedFace(Yukkuri body, int direction, BodyLayer layer, int idx) {
		if (body.isFeelPain() && body.getAge() % 50 == 0 && GameRandom.nextInt(50) == 0) {
			body.setForceFace(ImageCode.PAIN.ordinal());
		}
		if (body.isStrike() || body.isVerySad() || body.isFeelHardPain()) {
			return appendFace(body, ImageCode.CRYING.ordinal(), direction, layer, idx);
		}
		return appendTiredFace(body, direction, layer, idx);
	}

	private static int appendNormalStateFace(Yukkuri body, int direction, BodyLayer layer, int idx) {
		if (body.getPanicType() != null || body.isStrike() || body.isVerySad()) {
			return appendFace(body, ImageCode.CRYING.ordinal(), direction, layer, idx);
		}
		if (body.isAngry()) {
			return appendFace(body, ImageCode.PUFF.ordinal(), direction, layer, idx);
		}
		if (body.isSad() || body.isOld()) {
			return appendTiredFace(body, direction, layer, idx);
		}
		if (body.isVain()) {
			return appendFace(body, ImageCode.VAIN.ordinal(), direction, layer, idx);
		}
		if (body.isHappy() || body.isNobinobi()) {
			return appendFace(body, ImageCode.SMILE.ordinal(), direction, layer, idx);
		}
		if (body.isTalking() && body.isRude()) {
			return appendBlinkingFace(body, direction, layer, idx, ImageCode.RUDE.ordinal(), ImageCode.RUDE0.ordinal());
		}
		if (body.isTalking() && !body.isRude()) {
			return appendBlinkingFace(body, direction, layer, idx, ImageCode.CHEER.ordinal(), ImageCode.CHEER0.ordinal());
		}
		if ((!body.canflyCheck() && body.getZ() != 0) && !body.isLockmove()
				&& !(body.takeMappedObj(body.getParentLinkId()) instanceof Sui)) {
			if (SimYukkuri.UNYO) {
				return appendBlinkingFace(body, direction, layer, idx, ImageCode.CHEER.ordinal(),
						ImageCode.CHEER0.ordinal());
			}
			return appendFace(body, ImageCode.TIRED.ordinal(), direction, layer, idx);
		}
		return appendBlinkingFace(body, direction, layer, idx, ImageCode.NORMAL.ordinal(), ImageCode.NORMAL0.ordinal());
	}

	private static int appendTiredFace(Yukkuri body, int direction, BodyLayer layer, int idx) {
		return appendBlinkingFace(body, direction, layer, idx, ImageCode.TIRED.ordinal(), ImageCode.TIRED0.ordinal());
	}

	private static int appendBlinkingFace(Yukkuri body, int direction, BodyLayer layer, int idx, int baseFace,
			int blinkBaseFace) {
		if (!SimYukkuri.UNYO || !supportsNormalBlinkImages(body)) {
			return appendFace(body, baseFace, direction, layer, idx);
		}
		if (body.getBlinkType() != baseFace) {
			body.setBlinkCount(0);
		}
		if ((body.getBlinkCount() >= 91 && body.getBlinkCount() <= 94)
				|| (body.getBlinkCount() >= 97 && body.getBlinkCount() <= 100)) {
			idx += appendFace(body, blinkBaseFace, direction, layer, idx);
			idx += appendFace(body, ImageCode.EYE2.ordinal(), direction, layer, idx);
		} else if (body.getBlinkCount() >= 95 && body.getBlinkCount() <= 96) {
			idx += appendFace(body, blinkBaseFace, direction, layer, idx);
			idx += appendFace(body, ImageCode.EYE3.ordinal(), direction, layer, idx);
		} else {
			idx += appendFace(body, baseFace, direction, layer, idx);
		}
		body.setBlinkType(baseFace);
		if (MainCommandUI.getSelectedGameSpeed() != 0) {
			body.setBlinkCount(body.getBlinkCount() + 1);
		}
		if (body.getBlinkType() == baseFace && body.getBlinkCount() > 100) {
			if (GameRandom.nextInt(30) != 0) {
				body.setBlinkCount(GameRandom.nextInt(30));
			} else {
				body.setBlinkCount(85);
			}
		}
		return idx;
	}

	private static int appendFaceOverlays(Yukkuri body, int direction, BodyLayer layer, int idx) {
		if (body.isShutmouth()) {
			idx += appendFace(body, ImageCode.SHUTMOUTH.ordinal(), direction, layer, idx);
		}
		if (body.isBlind()) {
			idx += appendFace(body, ImageCode.BLIND.ordinal(), direction, layer, idx);
		}
		if (body.isPeropero() || body.isInOutTakeoutItem()) {
			if (body.getMessageBuffer() != null) {
				idx += appendFace(body, ImageCode.LICK.ordinal(), direction, layer, idx);
			}
		} else if ((body.isEating() || body.isEatingShit()) && body.getMessageBuffer() != null) {
			idx += appendFace(body, ImageCode.NOMNOM.ordinal(), direction, layer, idx);
		}
		return idx;
	}

	private static boolean supportsNormalBlinkImages(Yukkuri body) {
		return body.getType() != 20000;
	}

	private static int appendFace(Yukkuri body, int imageCode, int direction, BodyLayer layer, int idx) {
		return body.getImage(imageCode, direction, layer, idx);
	}

	private static int appendCrushedBody(Yukkuri body, BodyLayer layer, int idx) {
		if (body.isBurned()) {
			return idx + appendFace(body, ImageCode.BURNED2.ordinal(), Const.LEFT, layer, idx);
		}
		if (body.isPealed()) {
			return idx + appendFace(body, ImageCode.CRUSHED3.ordinal(), Const.LEFT, layer, idx);
		}
		if (body.getOkazari() != null && body.getOkazari().getOkazariType() == OkazariType.DEFAULT) {
			return idx + appendFace(body, ImageCode.CRUSHED.ordinal(), Const.LEFT, layer, idx);
		}
		return idx + appendFace(body, ImageCode.CRUSHED2.ordinal(), Const.LEFT, layer, idx);
	}

	private static int appendPackedBody(Yukkuri body, BodyLayer layer, int idx) {
		if (body.isDead()) {
			return idx + appendFace(body, ImageCode.PACKED_DEAD.ordinal(), Const.LEFT, layer, idx);
		}
		if (body.getAge() % 6 <= 2) {
			return idx + appendFace(body, ImageCode.PACKED1.ordinal(), Const.LEFT, layer, idx);
		}
		return idx + appendFace(body, ImageCode.PACKED2.ordinal(), Const.LEFT, layer, idx);
	}

	private static int appendFrontBody(Yukkuri body, BodyLayer layer, int idx) {
		idx += appendFace(body, ImageCode.FRONT_SHIT.ordinal(), Const.LEFT, layer, idx);
		if (body.getHairState() == HairState.DEFAULT) {
			idx += appendFace(body, ImageCode.FRONT_HAIR.ordinal(), Const.LEFT, layer, idx);
		} else if (body.getHairState() == HairState.BRINDLED1 || body.getHairState() == HairState.BRINDLED2) {
			idx += appendFace(body, ImageCode.FRONT_HAIR2.ordinal(), Const.LEFT, layer, idx);
		}
		if (body.isAnalClose()) {
			idx += appendFace(body, ImageCode.FRONT_SEALED.ordinal(), Const.LEFT, layer, idx);
		}
		if (body.getCriticalDamege() == CriticalDamegeType.INJURED) {
			idx += appendFace(body, ImageCode.FRONT_INJURED.ordinal(), Const.LEFT, layer, idx);
		}
		if (body.isBlind()) {
			idx += appendFace(body, ImageCode.FRONT_BLIND.ordinal(), Const.LEFT, layer, idx);
		}
		if (body.isHasPants()) {
			idx += appendFace(body, ImageCode.FRONT_PANTS.ordinal(), Const.LEFT, layer, idx);
		}
		if (body.isHasBraid()) {
			idx += appendFace(body, ImageCode.FRONT_BRAID.ordinal(), Const.LEFT, layer, idx);
		}
		if (body.getOkazari() != null && body.getOkazari().getOkazariType() == OkazariType.DEFAULT) {
			idx += appendFace(body, ImageCode.ROLL_ACCESSORY.ordinal(), Const.LEFT, layer, idx);
		}
		return idx;
	}

	private static int appendRollingBody(Yukkuri body, BodyLayer layer, int idx) {
		if (body.getAge() % 8 <= 3) {
			idx += appendFace(body, ImageCode.ROLL_LEFT_SHIT.ordinal(), Const.LEFT, layer, idx);
			idx += appendRollingStateOverlays(body, layer, idx, true);
		} else {
			idx += appendFace(body, ImageCode.ROLL_RIGHT_SHIT.ordinal(), Const.LEFT, layer, idx);
			idx += appendRollingStateOverlays(body, layer, idx, false);
		}
		if (body.getOkazari() != null && body.getOkazari().getOkazariType() == OkazariType.DEFAULT) {
			idx += appendFace(body, ImageCode.ROLL_ACCESSORY.ordinal(), Const.LEFT, layer, idx);
		}
		return idx;
	}

	private static int appendRollingStateOverlays(Yukkuri body, BodyLayer layer, int idx, boolean left) {
		if (body.getHairState() == HairState.DEFAULT) {
			idx += appendFace(body, left ? ImageCode.ROLL_LEFT_HAIR.ordinal() : ImageCode.ROLL_RIGHT_HAIR.ordinal(),
					Const.LEFT, layer, idx);
		} else if (body.getHairState() == HairState.BRINDLED1 || body.getHairState() == HairState.BRINDLED2) {
			idx += appendFace(body, ImageCode.FRONT_HAIR2.ordinal(), Const.LEFT, layer, idx);
		}
		if (body.isAnalClose()) {
			idx += appendFace(body, left ? ImageCode.ROLL_LEFT_SEALED.ordinal() : ImageCode.ROLL_RIGHT_SEALED.ordinal(),
					Const.LEFT, layer, idx);
		}
		if (body.getCriticalDamege() == CriticalDamegeType.INJURED) {
			idx += appendFace(body, left ? ImageCode.ROLL_LEFT_INJURED.ordinal() : ImageCode.ROLL_RIGHT_INJURED.ordinal(),
					Const.LEFT, layer, idx);
		}
		if (body.isBlind()) {
			idx += appendFace(body, left ? ImageCode.ROLL_LEFT_BLIND.ordinal() : ImageCode.ROLL_RIGHT_BLIND.ordinal(),
					Const.LEFT, layer, idx);
		}
		if (body.isHasPants()) {
			idx += appendFace(body, left ? ImageCode.ROLL_LEFT_PANTS.ordinal() : ImageCode.ROLL_RIGHT_PANTS.ordinal(),
					Const.LEFT, layer, idx);
		}
		if (body.isHasBraid()) {
			idx += appendFace(body, left ? ImageCode.ROLL_LEFT_BRAID.ordinal() : ImageCode.ROLL_RIGHT_BRAID.ordinal(),
					Const.LEFT, layer, idx);
		}
		return idx;
	}

	private static int appendNormalBody(Yukkuri body, int direction, BodyLayer layer, int idx) {
		if (body.isPealed()) {
			return idx + appendFace(body, ImageCode.PEALED.ordinal(), direction, layer, idx);
		}
		return idx + appendFace(body, ImageCode.BODY.ordinal(), direction, layer, idx);
	}

	private static int appendDamageEffect(Yukkuri body, int direction, BodyLayer layer, int idx) {
		if (body.isPealed()) {
			return 0;
		}
		if (body.isDamagedHeavily()) {
			return appendFace(body, ImageCode.DAMAGED2.ordinal(), direction, layer, idx);
		}
		if (body.isDamaged()) {
			return appendFace(body, ImageCode.DAMAGED1.ordinal(), direction, layer, idx);
		}
		if (body.isDamagedLightly()) {
			return appendFace(body, ImageCode.DAMAGED0.ordinal(), direction, layer, idx);
		}
		if (body.isOld()) {
			return appendFace(body, ImageCode.DAMAGED1.ordinal(), direction, layer, idx);
		}
		if (body.getBodyRank() == BodyRank.NORAYU || body.getBodyRank() == BodyRank.YASEIYU) {
			return appendFace(body, ImageCode.DAMAGED0.ordinal(), direction, layer, idx);
		}
		return 0;
	}

	private static int appendSickEffect(Yukkuri body, int direction, BodyLayer layer, int idx) {
		int sickPeriod = body.getSickPeriod();
		int incubation = body.getIncubationPeriodBase();
		if (sickPeriod > (incubation << 5)) {
			return appendFace(body, ImageCode.SICK3.ordinal(), direction, layer, idx);
		}
		if (sickPeriod > (incubation << 3)) {
			return appendFace(body, ImageCode.SICK2.ordinal(), direction, layer, idx);
		}
		if (sickPeriod > incubation) {
			return appendFace(body, ImageCode.SICK1.ordinal(), direction, layer, idx);
		}
		if (body.isSick()) {
			return appendFace(body, ImageCode.SICK0.ordinal(), direction, layer, idx);
		}
		return 0;
	}
}
