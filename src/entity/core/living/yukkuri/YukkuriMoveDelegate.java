package src.entity.core.living.yukkuri;

import src.Const;
import src.SimYukkuri;
import src.entity.core.Entity;
import src.entity.core.world.bodylinked.Stalk;
import src.logic.BodyMovement;
import src.util.GameRandom;
import src.draw.Translate;
import src.enums.BurialState;

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
			int centerH = (body.getBodySpr()[body.getBodyAgeState().ordinal()].getImageH() + body.getExpandSizeW()
					+ body.getExternalForceW());
			if (SimYukkuri.UNYO) {
				centerH = (body.getBodySpr()[body.getBodyAgeState().ordinal()].getImageH() + body.getExpandSizeH()
						+ body.getExternalForceW() + body.getUnyoOffsetH());
			}
			int k = 0;
			for (Stalk stalk : body.getStalks()) {
				if (stalk != null) {
					int sX = stalk.getPivotX() + Const.STALK_OF_S_X[k]
							- (int) ((3 - body.getBodyAgeState().ordinal()) * 8.75f);
					if (direction == Const.RIGHT) {
						stalk.setDirection(0);
					} else {
						stalk.setDirection(1);
						sX = -sX;
					}
					int ofsX = Translate.invertX(sX, body.getY());
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
	public void moveBody(boolean dontMove) {
		if (body.isGrabbed() || body.takeMappedObj(body.getParentLinkId()) != null) {
			body.setFalldownDamage(0);
			body.setNoDamageNextFall(false);
			body.setMotionX(0);
			body.setMotionY(0);
			body.setMotionZ(0);
			return;
		}
		if (BodyMovement.applyExternalMotion(body)) {
			return;
		}

		body.setX(Math.max(0, Math.min(body.getX(), Translate.getMapW())));
		body.setY(Math.max(0, Math.min(body.getY(), Translate.getMapH())));
		body.setCalcZ(Math.min(body.getZ(), Translate.getMapZ()));

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

		int step = BodyMovement.calculateMovementStep(body);
		int freq = BodyMovement.calculateMovementFrequency(body, step);
		if (body.getAge() % freq != 0) {
			body.setMotionX(0);
			body.setMotionY(0);
			body.setMotionZ(0);
			return;
		}

		if (body.getDestX() >= 0) {
			BodyMovement.updateDestinationDirectionX(body);
		} else {
			BodyMovement.updateRandomDirectionX(body);
		}
		if (body.getDestY() >= 0) {
			BodyMovement.updateDestinationDirectionY(body);
		} else {
			BodyMovement.updateRandomDirectionY(body);
		}
		BodyMovement.updateFlightDestination(body);

		step = BodyMovement.calculateDirectionalStep(body);
		BodyMovement.MovementVector vector = BodyMovement.calculateMovementVector(body, step);
		BodyMovement.applyDirectedMovement(body, vector);
		BodyMovement.resolveDirectedMovement(body, vector);
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
		body.setPurposeOfMoving(flag ? src.enums.PurposeOfMoving.FOOD : body.getPurposeOfMoving() == src.enums.PurposeOfMoving.FOOD
				? src.enums.PurposeOfMoving.NONE : body.getPurposeOfMoving());
	}

	/**
	 * 移動目的がすっきりかどうかを設定する.
	 *
	 * @param flag 移動目的がすっきりかどうか
	 */
	public void setToSukkiri(boolean flag) {
		body.setPurposeOfMoving(flag ? src.enums.PurposeOfMoving.SUKKIRI : body.getPurposeOfMoving() == src.enums.PurposeOfMoving.SUKKIRI
				? src.enums.PurposeOfMoving.NONE : body.getPurposeOfMoving());
	}

	/**
	 * 移動目的がうんうんかどうかを設定する.
	 *
	 * @param flag 移動目的がうんうんかどうか
	 */
	public void setToShit(boolean flag) {
		body.setPurposeOfMoving(flag ? src.enums.PurposeOfMoving.SHIT : body.getPurposeOfMoving() == src.enums.PurposeOfMoving.SHIT
				? src.enums.PurposeOfMoving.NONE : body.getPurposeOfMoving());
	}

	/**
	 * 移動目的がベッドかどうかを設定する.
	 *
	 * @param flag 移動目的がベッドかどうか
	 */
	public void setToBed(boolean flag) {
		body.setPurposeOfMoving(flag ? src.enums.PurposeOfMoving.BED : body.getPurposeOfMoving() == src.enums.PurposeOfMoving.BED
				? src.enums.PurposeOfMoving.NONE : body.getPurposeOfMoving());
	}

	/**
	 * 移動目的が他のゆっくりかどうかを設定する.
	 *
	 * @param flag 移動目的が他のゆっくりかどうか
	 */
	public void setToBody(boolean flag) {
		body.setPurposeOfMoving(flag ? src.enums.PurposeOfMoving.YUKKURI : body.getPurposeOfMoving() == src.enums.PurposeOfMoving.YUKKURI
				? src.enums.PurposeOfMoving.NONE : body.getPurposeOfMoving());
	}

	/**
	 * 移動目的がおかざりを盗むためかどうかを設定する.
	 *
	 * @param flag 移動目的がおかざりを盗むためかどうか
	 */
	public void setToSteal(boolean flag) {
		body.setPurposeOfMoving(flag ? src.enums.PurposeOfMoving.STEAL : body.getPurposeOfMoving() == src.enums.PurposeOfMoving.STEAL
				? src.enums.PurposeOfMoving.NONE : body.getPurposeOfMoving());
	}

	/**
	 * 移動目的がアイテムを持つことかどうかを設定する.
	 *
	 * @param flag 移動目的がアイテムを持つことかどうか
	 */
	public void setToTakeout(boolean flag) {
		body.setPurposeOfMoving(flag ? src.enums.PurposeOfMoving.TAKEOUT : body.getPurposeOfMoving() == src.enums.PurposeOfMoving.TAKEOUT
				? src.enums.PurposeOfMoving.NONE : body.getPurposeOfMoving());
	}

	/**
	 * 移動目標のみをキャンセルする.
	 */
	public void clearTargets() {
		body.setPurposeOfMoving(src.enums.PurposeOfMoving.NONE);
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
		int mapX = Translate.getMapW();
		int mapY = Translate.getMapH();
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
		return body.getStepBase()[body.getBodyAgeState().ordinal()];
	}

	/**
	 * 現在年齢の移動速度の二乗値を返す.
	 *
	 * @return 移動速度の二乗値
	 */
	public int getStepDist() {
		int p = body.getStepBase()[body.getBodyAgeState().ordinal()];
		return p * p;
	}
}
