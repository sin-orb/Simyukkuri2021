package org.simyukkuri.logic;

import java.util.Map;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Trampoline;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Damage;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.event.impl.SuperEatingTimeEvent;
import org.simyukkuri.field.FieldShape;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;

/**
 * ゆっくり本体の移動状態を計算するロジック集約クラス。
 * <p>
 * Phase 2では、まず{@link Yukkuri#moveBody(boolean)}全体を移すのではなく、
 * 移動速度に関する判定だけをこのクラスへ委譲する。落下、衝突、目的地更新などの
 * 副作用はまだ{@link Yukkuri}側に残し、挙動変更の範囲を小さく保つ。
 * </p>
 */
public final class BodyMovement {
	private BodyMovement() {
	}

	/**
	 * 方向と速度から算出した、1tick分の移動ベクトル。
	 */
	public static final class MovementVector {
		private final int x;
		private final int y;
		private final int z;

		private MovementVector(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		/**
		 * テストや補助処理向けに移動ベクトルを生成する。
		 *
		 * @param x X方向の移動量
		 * @param y Y方向の移動量
		 * @param z Z方向の移動量
		 * @return 指定値の移動ベクトル
		 */
		public static MovementVector of(int x, int y, int z) {
			return new MovementVector(x, y, z);
		}

		/**
		 * X方向の移動量を取得する。
		 *
		 * @return X方向の移動量
		 */
		public int getX() {
			return x;
		}

		/**
		 * Y方向の移動量を取得する。
		 *
		 * @return Y方向の移動量
		 */
		public int getY() {
			return y;
		}

		/**
		 * Z方向の移動量を取得する。
		 *
		 * @return Z方向の移動量
		 */
		public int getZ() {
			return z;
		}
	}

	/**
	 * 現在の状態から、移動処理で使う基礎step値を計算する。
	 * <p>
	 * 妊娠/茎、空腹、ダメージ、病気、痛み、飛行不能、重度火傷、蟻、盲目、
	 * 家族イベント中の最低速度を既存の{@link Yukkuri#moveBody(boolean)}と同じ順序で反映する。
	 * </p>
	 *
	 * @param body 判定対象のゆっくり
	 * @return 1以上に補正された移動step
	 */
	public static int calculateMovementStep(Yukkuri body) {
		int step = body.getStepBase()[body.getBodyAgeState().ordinal()];
		if (body.hasBabyOrStalk() || (body.isSoHungry() && !body.isPredatorType())
				|| body.getDamageState() != Damage.NONE || body.isSick() || body.isFeelPain()
				|| (body.isFlyingType() && !body.canflyCheck())
				|| (body.isGotBurnedHeavily() && !body.canflyCheck())) {
			step /= 2;
		}
		if (body.getAttachmentSize(Ants.class) != 0) {
			step /= 2;
		}
		if (body.isBlind()) {
			step /= 2;
		}

		if (body.getCurrentEvent() instanceof SuperEatingTimeEvent) {
			step = ((SuperEatingTimeEvent) body.getCurrentEvent()).getMinimumStep();
		}

		if (step == 0) {
			step = 1;
		}
		return step;
	}

	/**
	 * 現在のstep値から、何tickに一度移動するかを計算する。
	 *
	 * @param body 判定対象のゆっくり
	 * @param step {@link #calculateMovementStep(Yukkuri)}で計算した移動step
	 * @return 移動判定に使う周期
	 */
	public static int calculateMovementFrequency(Yukkuri body, int step) {
		return body.getStepBase()[AgeState.ADULT.ordinal()] / step;
	}

	/**
	 * X座標の目的地に向けた移動方向を更新する。
	 * <p>
	 * 目的地へ到達済みの場合は、既存の{@link Yukkuri#moveBody(boolean)}と同じく
	 * X目的地を未設定値へ戻す。目的地が未設定の場合のランダム方向更新は、
	 * 呼び出し元の分岐に残す。
	 * </p>
	 *
	 * @param body 更新対象のゆっくり
	 */
	public static void updateDestinationDirectionX(Yukkuri body) {
		int direction = body.decideDirection(body.getX(), body.getDestX(), 0);
		body.setDirX(direction);
		if (direction == 0) {
			body.setDestX(-1);
		}
	}

	/**
	 * Y座標の目的地に向けた移動方向を更新する。
	 * <p>
	 * 目的地へ到達済みの場合は、既存の{@link Yukkuri#moveBody(boolean)}と同じく
	 * Y目的地を未設定値へ戻す。目的地が未設定の場合のランダム方向更新は、
	 * 呼び出し元の分岐に残す。
	 * </p>
	 *
	 * @param body 更新対象のゆっくり
	 */
	public static void updateDestinationDirectionY(Yukkuri body) {
		int direction = body.decideDirection(body.getY(), body.getDestY(), 0);
		body.setDirY(direction);
		if (direction == 0) {
			body.setDestY(-1);
		}
	}

	/**
	 * X座標に目的地がない場合のランダム方向更新を行う。
	 * <p>
	 * 既存実装の後置インクリメントを保ち、閾値未満ならカウントだけ進める。
	 * 閾値に達した場合はカウントを0に戻してX方向を更新する。
	 * </p>
	 *
	 * @param body 更新対象のゆっくり
	 */
	public static void updateRandomDirectionX(Yukkuri body) {
		int directionCount = body.getCountX();
		body.setCountX(directionCount + 1);
		if (directionCount >= getRandomDirectionLimit(body)) {
			body.setCountX(0);
			body.setDirX(body.randomDirection(body.getDirX()));
			showNoAccessoryMessageIfNeeded(body);
		}
	}

	/**
	 * Y座標に目的地がない場合のランダム方向更新を行う。
	 * <p>
	 * 既存実装の後置インクリメントを保ち、閾値未満ならカウントだけ進める。
	 * 閾値に達した場合はカウントを0に戻してY方向を更新する。
	 * </p>
	 *
	 * @param body 更新対象のゆっくり
	 */
	public static void updateRandomDirectionY(Yukkuri body) {
		int directionCount = body.getCountY();
		body.setCountY(directionCount + 1);
		if (directionCount >= getRandomDirectionLimit(body)) {
			body.setCountY(0);
			body.setDirY(body.randomDirection(body.getDirY()));
			showNoAccessoryMessageIfNeeded(body);
		}
	}

	/**
	 * 実移動ベクトル計算に使う方向ステップを計算する。
	 * <p>
	 * 通常は1、興奮中のレイパーは既存処理と同じく2を返す。
	 * </p>
	 *
	 * @param body 判定対象のゆっくり
	 * @return 方向ごとの移動量に掛けるステップ
	 */
	public static int calculateDirectionalStep(Yukkuri body) {
		if (body.isRaper() && body.isExciting()) {
			return 2;
		}
		return 1;
	}

	/**
	 * 現在の方向、方向ステップ、速度から1tick分の移動ベクトルを計算する。
	 * <p>
	 * {@code speed % 100}がある場合は、既存処理と同じく確率で各方向へ1単位を
	 * 加算する。
	 * </p>
	 *
	 * @param body            判定対象のゆっくり
	 * @param directionalStep {@link #calculateDirectionalStep(Yukkuri)}で計算した方向ステップ
	 * @return X/Y/Z方向の移動ベクトル
	 */
	public static MovementVector calculateMovementVector(Yukkuri body, int directionalStep) {
		int vecX = body.getDirX() * directionalStep * body.getSpeed() / 100;
		int vecY = body.getDirY() * directionalStep * body.getSpeed() / 100;
		int vecZ = body.getDirZ() * directionalStep * body.getSpeed() / 100;

		if (body.getSpeed() % 100 > 0) {
			if (GameRandom.nextInt(100) < body.getSpeed() % 100) {
				vecX += body.getDirX();
				vecY += body.getDirY();
				vecZ += body.getDirZ();
			}
		}
		return new MovementVector(vecX, vecY, vecZ);
	}

	/**
	 * 現在の目的地と移動ベクトルから、1tick分の移動先を本体へ反映する。
	 * <p>
	 * 明確な目的地がある軸では行き過ぎを抑止し、目的地未設定の軸では
	 * 既存実装どおりベクトル分だけ加算する。
	 * </p>
	 *
	 * @param body   更新対象のゆっくり
	 * @param vector このtickの移動ベクトル
	 */
	public static void applyDirectedMovement(Yukkuri body, MovementVector vector) {
		body.setX(applyAxisMovement(body.getX(), body.getDestX(), body.getDirX(), vector.getX()));
		body.setY(applyAxisMovement(body.getY(), body.getDestY(), body.getDirY(), vector.getY()));
		if (body.canflyCheck()) {
			body.setZ(applyAxisMovement(body.getZ(), body.getDestZ(), body.getDirZ(), vector.getZ()));
		} else {
			body.setZ(body.getZ() + vector.getZ());
		}
	}

	/**
	 * 指定座標への移動先を設定する。
	 * <p>
	 * 既存の {@link Yukkuri#moveTo(int, int, int)} と同じく、死亡中または blocked 中なら
	 * 何もしない。設定時はマップ範囲へ clamp する。
	 * </p>
	 *
	 * @param body 更新対象のゆっくり
	 * @param toX  X座標
	 * @param toY  Y座標
	 * @param toZ  Z座標
	 */
	public static void moveTo(Yukkuri body, int toX, int toY, int toZ) {
		if (body.isDead()) {
			return;
		}
		if (body.getBlockedTicks() != 0) {
			return;
		}
		body.setDestX(Math.max(0, Math.min(toX, Translate.getMapW())));
		body.setDestY(Math.max(0, Math.min(toY, Translate.getMapH())));
		body.setDestZ(Math.max(0, Math.min(toZ, Translate.getMapZ())));
	}

	/**
	 * 他オブジェクト追従用の移動状態を設定する。
	 *
	 * @param body   更新対象のゆっくり
	 * @param target 追従対象
	 * @param toX    X座標
	 * @param toY    Y座標
	 * @param toZ    Z座標
	 */
	public static void moveToBody(Yukkuri body, Entity target, int toX, int toY, int toZ) {
		body.clearActions();
		body.setToBody(true);
		body.setMoveTargetId(target.objId);
		moveTo(body, toX, toY, toZ);
	}

	/**
	 * 指定座標から遠ざかるように移動先を設定する。
	 *
	 * @param body  更新対象のゆっくり
	 * @param fromX 脅威側のX座標
	 * @param fromY 脅威側のY座標
	 */
	public static void runAway(Yukkuri body, int fromX, int fromY) {
		if (!body.canAction() || body.isExciting() || body.isAngry() || body.isUnBirth()) {
			return;
		}
		int toX = body.getX() > fromX ? Translate.getMapW() : 0;
		int toY = body.getY() > fromY ? Translate.getMapH() : 0;
		moveTo(body, toX, toY, 0);
		body.clearActions();
		body.setScare(true);
	}

	/**
	 * 飛行可能なゆっくりのZ目的地方向と高度維持先を更新する。
	 * <p>
	 * 飛行不能な場合は何もしない。飛行可能で明確な移動対象とイベントがない場合は、
	 * 既存処理と同じく飛行高度上限をZ目的地に設定する。
	 * </p>
	 *
	 * @param body 更新対象のゆっくり
	 */
	public static void updateFlightDestination(Yukkuri body) {
		if (!body.canflyCheck()) {
			return;
		}
		if (body.getDestZ() >= 0) {
			int direction = body.decideDirection(body.getZ(), body.getDestZ(), 0);
			body.setDirZ(direction);
			if (direction == 0) {
				body.setDestZ(-1);
			}
		}
		if (body.takeMoveTarget() == null && body.getCurrentEvent() == null) {
			body.setDestZ(Translate.getFlyHeightLimit());
		}
	}

	/**
	 * 外力による移動、壁衝突、落下、着地ダメージ処理を行う。
	 * <p>
	 * 既存の{@link Yukkuri#moveBody(boolean)}先頭にあった物理更新をそのまま移したもの。
	 * 落下処理を行ったtickは、既存実装と同じくこの段階で移動処理を打ち切る。
	 * </p>
	 *
	 * @param body 更新対象のゆっくり
	 * @return このtickの移動処理をここで終了すべき場合は{@code true}
	 */
	public static boolean applyExternalMotion(Yukkuri body) {
		int mx = body.getVx() + body.getMotionX();
		int my = body.getVy() + body.getMotionY();
		int mz = body.getVz() + body.getMotionZ();

		if (mx != 0) {
			body.setX(body.getX() + mx);
			if (Barrier.onBarrier(body.getX(), body.getY(), body.getW() >> 2, body.getH() >> 3,
					Barrier.MAP_BODY[body.getBodyAgeState().ordinal()])) {
				body.setX(body.getX() - mx);
				body.setVx(0);
			} else if (body.getX() < 0) {
				body.setFalldownDamage(body.getFalldownDamage() + Math.abs(body.getVx()));
				body.setX(0);
				body.setVx(0);
			} else if (body.getX() > Translate.getMapW()) {
				body.setFalldownDamage(body.getFalldownDamage() + Math.abs(body.getVx()));
				body.setX(Translate.getMapW());
				body.setVx(0);
			}
		}

		if (my != 0) {
			body.setY(body.getY() + my);
			if (Barrier.onBarrier(body.getX(), body.getY(), body.getW() >> 2, body.getH() >> 3,
					Barrier.MAP_BODY[body.getBodyAgeState().ordinal()])) {
				body.setY(body.getY() - my);
				body.setVy(0);
			} else if (body.getY() < 0) {
				body.setFalldownDamage(body.getFalldownDamage() + Math.abs(body.getVy()));
				body.setY(0);
				body.setVy(0);
				body.setDirY(1);
			} else if (body.getY() > Translate.getMapH()) {
				body.setFalldownDamage(body.getFalldownDamage() + Math.abs(body.getVy()));
				body.setY(Translate.getMapH());
				body.setVy(0);
				body.setDirY(-1);
			}
		}

		if (body.getZ() > 0) {
			body.setFallingUnderGround(false);
		}

		if ((mz != 0 || (!body.canflyCheck() && body.getMostDepth() != body.getZ() && body.getBindStalk() == null))
				&& !body.isFallingUnderGround()) {
			body.setFalldownDamage(body.getVz() > 0 ? body.getFalldownDamage() : 0);
			mz += 1;
			body.setVz(body.getVz() + 1);
			body.setZ(body.getZ() - mz);
			body.setFalldownDamage(body.getFalldownDamage() + (body.getVz() > 0 ? body.getVz() : 0));
			if (body.getZ() <= body.getMostDepth()) {
				applyLanding(body);
			}
			body.setMotionX(0);
			body.setMotionY(0);
			body.setMotionZ(0);
			return true;
		}
		return false;
	}

	/**
	 * 移動後の壁衝突、水場進入、マップ境界、向き更新を適用する。
	 *
	 * @param body   更新対象のゆっくり
	 * @param vector このtickの移動ベクトル
	 */
	public static void resolveDirectedMovement(Yukkuri body, MovementVector vector) {
		if (Barrier.onBarrier(body.getX(), body.getY(), body.getW() >> 2, body.getH() >> 3,
				Barrier.MAP_BODY[body.getBodyAgeState().ordinal()])) {
			revertDirectedMovement(body, vector);
			handleWallCollision(body);
		} else {
			body.setBlockedTicks(Math.max(0, body.getBlockedTicks() - 1));
			handlePoolEntry(body, vector);
		}

		clampPositionToMap(body);
		updateFacingDirection(body);
	}

	private static int getRandomDirectionLimit(Yukkuri body) {
		return body.getSameDirectionFactor() * body.getStepBase()[body.getBodyAgeState().ordinal()];
	}

	private static int applyAxisMovement(int current, int destination, int direction, int vector) {
		if (destination == -1) {
			return current + vector;
		}
		if (direction < 0) {
			return (current + vector) < destination ? destination : current + vector;
		}
		if (direction > 0) {
			return (current + vector) > destination ? destination : current + vector;
		}
		return current;
	}

	private static void revertDirectedMovement(Yukkuri body, MovementVector vector) {
		body.setX(body.getX() - vector.getX());
		body.setY(body.getY() - vector.getY());
		body.setZ(body.getZ() - vector.getZ());
	}

	private static void handleWallCollision(Yukkuri body) {
		if (hasAnyDestination(body)) {
			body.setBlockedTicks(Math.min(body.getBlockedTicks() + 1, body.getBlockedLimitBase() * 2));
			if (body.getBlockedTicks() > body.getBlockedLimitBase()) {
				randomizeBlockedDirection(body);
				body.setDestX(-1);
				body.setDestY(-1);
				clearActionsForBlockedMovement(body);
				if (body.getIntelligence() == Intelligence.FOOL && body.getPanicType() != null) {
					body.setHappiness(Happiness.VERY_SAD);
				}
			} else if (body.getBlockedTicks() > body.getBlockedLimitBase() / 2
					&& body.getIntelligence() == Intelligence.FOOL && body.getPanicType() != null) {
				if (body.isRude()) {
					body.setAngry();
				} else {
					body.setCalm();
					body.setHappiness(Happiness.SAD);
				}
			}
			if (body.getIntelligence() == Intelligence.FOOL && body.getPanicType() != null) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.BlockedByWall));
			}
		} else {
			body.setDirX(body.randomDirection(body.getDirX()));
			body.setDirY(body.randomDirection(body.getDirY()));
		}
	}

	private static boolean hasAnyDestination(Yukkuri body) {
		return body.getDestX() >= 0 || body.getDestY() >= 0 || body.getDestZ() >= 0;
	}

	private static void randomizeBlockedDirection(Yukkuri body) {
		if (GameRandom.nextBoolean()) {
			body.setDirX(body.randomDirection(body.getDirX()));
			return;
		}
		body.setDirY(body.randomDirection(body.getDirY()));
	}

	private static void clearActionsForBlockedMovement(Yukkuri body) {
		if (body.getCurrentEvent() != null) {
			body.clearActionsForEvent();
			return;
		}
		body.clearActions();
	}

	private static void handlePoolEntry(Yukkuri body, MovementVector vector) {
		if ((Translate.getCurrentFieldMapNum(body.getX(), body.getY()) & FieldShape.FIELD_POOL) == 0) {
			return;
		}
		if ((Translate.getCurrentFieldMapNum(body.getX() - vector.getX(), body.getY() - vector.getY())
				& FieldShape.FIELD_POOL) != 0) {
			return;
		}
		if (body.isLikeWater()) {
			return;
		}

		int randomLimit = getPoolAvoidanceRandomLimit(body);
		if (GameRandom.nextInt(randomLimit) != 0) {
			revertDirectedMovement(body, vector);
			body.setDirX(body.randomDirection(body.getDirX()));
			body.setDirY(body.randomDirection(body.getDirY()));
		}
	}

	private static int getPoolAvoidanceRandomLimit(Yukkuri body) {
		switch (body.getIntelligence()) {
			case FOOL:
				return 10;
			case AVERAGE:
				return 30;
			case WISE:
				return 100;
			default:
				return 1;
		}
	}

	private static void clampPositionToMap(Yukkuri body) {
		if (body.getX() < 0) {
			body.setX(0);
			body.setDirX(1);
		} else if (body.getX() > Translate.getMapW()) {
			body.setX(Translate.getMapW());
			body.setDirX(-1);
		}
		if (body.getY() < 0) {
			body.setY(0);
			body.setDirY(1);
		} else if (body.getY() > Translate.getMapH()) {
			body.setY(Translate.getMapH());
			body.setDirY(-1);
		}
		if (body.getZ() > Translate.getMapZ()) {
			body.setZ(Translate.getMapZ());
		}
	}

	private static void updateFacingDirection(Yukkuri body) {
		if (body.getDirX() == -1) {
			body.setDirection(Direction.LEFT);
		} else if (body.getDirX() == 1) {
			body.setDirection(Direction.RIGHT);
		}
	}

	private static void showNoAccessoryMessageIfNeeded(Yukkuri body) {
		if (!body.hasOkazari() && (body.isSad() || body.isVerySad())) {
			if (GameRandom.nextInt(10) == 0) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.NoAccessory));
			}
		}
	}

	/**
	 * 着地時のダメージ計算と接地副作用を適用する。
	 *
	 * @param body 更新対象のゆっくり
	 */
	private static void applyLanding(Yukkuri body) {
		if (SimYukkuri.UNYO) {
			body.changeUnyo(0, 0, (int) (body.getFalldownDamage() * 0.4 + 1));
		}
		body.setFalldownDamage(body.getFalldownDamage() + Math.abs(body.getVy()));
		body.setZ(body.getMostDepth());
		body.setVz(0);
		body.setVy(0);
		body.setVx(0);
		int[] jumpLevel = { 2, 2, 1 };
		int damageCut = 1;
		if (body.getFalldownDamage() >= 8 / jumpLevel[body.getBodyAgeState().ordinal()]) {
			if (body.checkOnBed()) {
				damageCut = 4;
			} else if (body.isFirstGround()) {
				body.addMemories(-20);
			}

			if (damageCut != 4) {
				for (Map.Entry<Integer, Trampoline> entry : GameWorld.get().getCurrentMap().getTrampoline()
						.entrySet()) {
					Trampoline trampoline = entry.getValue();
					if (trampoline.checkHitObj(body)) {
						damageCut = 100;
						break;
					}
				}
			}

			if (body.isNoDamageNextFall() && body.getFalldownDamage() != 0) {
				body.setNoDamageNextFall(false);
				body.setFalldownDamage(0);
			}

			if (!body.checkOnBed() || !body.isBaby()) {
				body.strike(body.getFalldownDamage() * 100 * 24 * 3 / 100 / damageCut);
			}

			if (body.isFirstGround()) {
				if (!body.isNYD()) {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TakeItEasy));
					body.addStress(-400);
					body.addMemories(20);
				}
			}
			body.setFirstGround(false);

			if (body.isPealed()) {
				body.toDead();
			}
			if (body.isDead()) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Dying));
				body.stay();
				body.setCrushed(true);
			}
		}
	}
}
