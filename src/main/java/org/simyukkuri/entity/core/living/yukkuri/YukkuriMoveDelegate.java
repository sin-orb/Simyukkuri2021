package org.simyukkuri.entity.core.living.yukkuri;

import org.simyukkuri.Const;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.PurposeOfMoving;
import org.simyukkuri.logic.YukkuriMovement;
import org.simyukkuri.util.GameRandom;

/**
 * ゆっくりの移動・茎更新を切り出した委譲クラス.
 */
public final class YukkuriMoveDelegate {
	private final Yukkuri body;

	/**
	 * 移動・茎更新を扱う委譲を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriMoveDelegate(Yukkuri body) {
		this.body = body;
	}

	/**
	 * 現在方向からランダムに向きを振り直す.
	 *
	 * @param curDir 現在の向き
	 * @return 更新後の向き
	 */
	public int randomDirection(int curDir) {
		switch (curDir) {
			case 0:
				curDir = (GameRandom.nextBoolean() ? 1 : -1);
				break;
			case 1:
				curDir = (GameRandom.nextBoolean() ? 0 : curDir);
				break;
			case -1:
				curDir = (GameRandom.nextBoolean() ? 0 : curDir);
				break;
			default:
				break;
		}
		return curDir;
	}

	/**
	 * 現在座標と目的地座標から向きを決める.
	 *
	 * @param curPos 現在座標
	 * @param destPos 目的地座標
	 * @param range 許容範囲
	 * @return 向き
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
	 * 茎の位置と移動状態を更新する.
	 */
	public void upDate() {
		if (body.getStalks() != null && body.getStalks().size() > 0) {
			int direction = body.getDirection().ordinal();
			int centerH = (body.getSpriteSet()[body.getAgeState().ordinal()].getImageH() + body.getExpandSizeW()
					+ body.getExternalForceW());
			if (SimYukkuri.UNYO) {
				centerH = (body.getSpriteSet()[body.getAgeState().ordinal()].getImageH() + body.getExpandSizeH()
						+ body.getExternalForceW() + body.getUnyoOffsetH());
			}
			int k = 0;
			for (Stalk stalk : body.getStalks()) {
				if (stalk != null) {
					int sourceX = stalk.getPivotX() + Const.STALK_OF_S_X[k]
							- (int) ((3 - body.getAgeState().ordinal()) * 8.75f);
					if (direction == Const.RIGHT) {
						stalk.setDirection(0);
					} else {
						stalk.setDirection(1);
						sourceX = -sourceX;
					}
					int ofsX = Translate.invertX(sourceX, body.getY());
					ofsX = Translate.transSize(ofsX);
					stalk.setMostDepth(body.getMostDepth());
					stalk.setCalcX(body.getX() + ofsX);
					stalk.setCalcY(body.getY());
					if (body.getBurialState() == BurialState.ALL) {
						stalk.setCalcZ(0);
					} else {
						stalk.setCalcZ(body.getZ() + (int) (centerH * 0.09f) + Const.STALK_OF_S_Y[k]);
					}
					stalk.upDate();
				}
				k = (k + 1) & 7;
			}
		}
	}

	/**
	 * 身体を移動させる.
	 *
	 * @param dontMove 移動禁止かどうか
	 */
	public void moveYukkuri(boolean dontMove) {
		if (body.isGrabbed() || body.takeMappedObj(body.getParentLinkId()) != null) {
			body.setFalldownDamage(0);
			body.setNoDamageNextFall(false);
			body.setMotionX(0);
			body.setMotionY(0);
			body.setMotionZ(0);
			return;
		}
		if (YukkuriMovement.applyExternalMotion(body)) {
			return;
		}

		body.setX(Math.max(0, Math.min(body.getX(), Translate.getWorldWidth())));
		body.setY(Math.max(0, Math.min(body.getY(), Translate.getWorldHeight())));
		body.setCalcZ(Math.min(body.getZ(), Translate.getWorldDepth()));

		if (dontMove || body.isLockmove()) {
			body.setMotionX(0);
			body.setMotionY(0);
			body.setMotionZ(0);
			return;
		}
		if ((body.getMotionX() + body.getMotionY() + body.getMotionZ()) != 0) {
			body.setMotionX(0);
			body.setMotionY(0);
			body.setMotionZ(0);
			return;
		}

		int step = YukkuriMovement.calculateMovementStep(body);
		int freq = YukkuriMovement.calculateMovementFrequency(body, step);
		if (body.getAge() % freq != 0) {
			body.setMotionX(0);
			body.setMotionY(0);
			body.setMotionZ(0);
			return;
		}

		if (body.getDestX() >= 0) {
			YukkuriMovement.updateDestinationDirectionX(body);
		} else {
			YukkuriMovement.updateRandomDirectionX(body);
		}
		if (body.getDestY() >= 0) {
			YukkuriMovement.updateDestinationDirectionY(body);
		} else {
			YukkuriMovement.updateRandomDirectionY(body);
		}
		YukkuriMovement.updateFlightDestination(body);

		step = YukkuriMovement.calculateDirectionalStep(body);
		YukkuriMovement.MovementVector vector = YukkuriMovement.calculateMovementVector(body, step);
		YukkuriMovement.applyDirectedMovement(body, vector);
		YukkuriMovement.resolveDirectedMovement(body, vector);
		body.setMotionX(0);
		body.setMotionY(0);
		body.setMotionZ(0);
	}

	/**
	 * 移動目的がフードかどうかを設定する.
	 *
	 * @param flag 移動目的がフードかどうか
	 */
	public void setToFood(boolean flag) {
		body.setPurposeOfMoving(flag ? PurposeOfMoving.FOOD : body.getPurposeOfMoving() == PurposeOfMoving.FOOD
				? PurposeOfMoving.NONE : body.getPurposeOfMoving());
	}

	/**
	 * 移動目的がすっきりかどうかを設定する.
	 *
	 * @param flag 移動目的がすっきりかどうか
	 */
	public void setToSukkiri(boolean flag) {
		body.setPurposeOfMoving(flag ? PurposeOfMoving.SUKKIRI : body.getPurposeOfMoving() == PurposeOfMoving.SUKKIRI
				? PurposeOfMoving.NONE : body.getPurposeOfMoving());
	}

	/**
	 * 移動目的がうんうんかどうかを設定する.
	 *
	 * @param flag 移動目的がうんうんかどうか
	 */
	public void setToShit(boolean flag) {
		body.setPurposeOfMoving(flag ? PurposeOfMoving.SHIT : body.getPurposeOfMoving() == PurposeOfMoving.SHIT
				? PurposeOfMoving.NONE : body.getPurposeOfMoving());
	}

	/**
	 * 移動目的がベッドかどうかを設定する.
	 *
	 * @param flag 移動目的がベッドかどうか
	 */
	public void setToBed(boolean flag) {
		body.setPurposeOfMoving(flag ? PurposeOfMoving.BED : body.getPurposeOfMoving() == PurposeOfMoving.BED
				? PurposeOfMoving.NONE : body.getPurposeOfMoving());
	}

	/**
	 * 移動目的が他のゆっくりかどうかを設定する.
	 *
	 * @param flag 移動目的が他のゆっくりかどうか
	 */
	public void setToYukkuri(boolean flag) {
		body.setPurposeOfMoving(flag ? PurposeOfMoving.YUKKURI : body.getPurposeOfMoving() == PurposeOfMoving.YUKKURI
				? PurposeOfMoving.NONE : body.getPurposeOfMoving());
	}

	/**
	 * 移動目的がおかざりを盗むためかどうかを設定する.
	 *
	 * @param flag 移動目的がおかざりを盗むためかどうか
	 */
	public void setToSteal(boolean flag) {
		body.setPurposeOfMoving(flag ? PurposeOfMoving.STEAL : body.getPurposeOfMoving() == PurposeOfMoving.STEAL
				? PurposeOfMoving.NONE : body.getPurposeOfMoving());
	}

	/**
	 * 移動目的がアイテムを持つことかどうかを設定する.
	 *
	 * @param flag 移動目的がアイテムを持つことかどうか
	 */
	public void setToTakeout(boolean flag) {
		body.setPurposeOfMoving(flag ? PurposeOfMoving.TAKEOUT : body.getPurposeOfMoving() == PurposeOfMoving.TAKEOUT
				? PurposeOfMoving.NONE : body.getPurposeOfMoving());
	}

	/**
	 * 移動目標のみをキャンセルする.
	 */
	public void clearTargets() {
		body.setPurposeOfMoving(PurposeOfMoving.NONE);
		body.stopStaying();
	}

	/**
	 * 移動目標の相対オフセットを設定する.
	 *
	 * @param ox X方向オフセット
	 * @param oy Y方向オフセット
	 */
	public void setTargetMoveOffset(int ox, int oy) {
		body.setTargetOffsetX(ox);
		body.setTargetOffsetY(oy);
	}

	/**
	 * 移動目標をマップ範囲内に収める.
	 */
	public void calcMoveTarget() {
		Entity target = body.takeMoveTarget();
		if (target == null) {
			return;
		}
		int mapX = Translate.getWorldWidth();
		final int mapY = Translate.getWorldHeight();
		if (target.getX() < 0) {
			target.setCalcX(0);
		}
		if (target.getY() < 0) {
			target.setCalcY(0);
		}
		if (target.getX() > mapX) {
			target.setCalcX(mapX);
		}
		if (target.getX() > mapY) {
			target.setCalcX(mapY);
		}
	}

	/**
	 * 現在年齢の移動速度を返す.
	 *
	 * @return 移動速度
	 */
	public int getStep() {
		return body.getStepBase()[body.getAgeState().ordinal()];
	}

	/**
	 * 現在年齢の移動速度の二乗値を返す.
	 *
	 * @return 移動速度の二乗値
	 */
	public int getStepDist() {
		int p = body.getStepBase()[body.getAgeState().ordinal()];
		return p * p;
	}
}
