package src.logic;

import java.util.List;
import java.util.Map;

import src.base.Yukkuri;
import src.event.EventPacket;
import src.base.Entity;
import src.draw.Point4y;
import src.draw.Translate;
import src.enums.Direction;
import src.enums.GatheringDirection;
import src.field.impl.Barrier;
import src.item.Toilet;
import src.util.BodyRegistry;
import src.util.GameWorld;

/**
 * ゆっくりの集合移動ロジック.
 */
public final class BodyGatheringRule {

	private BodyGatheringRule() {
	}

	/**
	 * ぜんゆん集合.
	 */
	public static void gatheringYukkuri() {
		Yukkuri[] bodyList = BodyRegistry.getBodyInstances();
		if (bodyList.length != 0) {
			Toilet t = null;
			for (Map.Entry<Integer, Toilet> entry : GameWorld.get().getCurrentMap().getToilet().entrySet()) {
				t = entry.getValue();
				break;
			}
			if (t != null) {
				gatheringYukkuriSquare(t, bodyList, GatheringDirection.UP, null);
			}
		}
	}

	/**
	 * ぜんゆん集合(四角形前面)
	 */
	public static boolean gatheringYukkuriFront(Yukkuri topBody, List<Yukkuri> targetList) {
		return gatheringYukkuriSquare(topBody, targetList.toArray(new Yukkuri[0]), GatheringDirection.DOWN, null);
	}

	/**
	 * ぜんゆん集合(四角形前面)
	 */
	public static boolean gatheringYukkuriFront(Yukkuri topBody, List<Yukkuri> targetList, EventPacket event) {
		return gatheringYukkuriSquare(topBody, targetList.toArray(new Yukkuri[0]), GatheringDirection.DOWN, event);
	}

	/**
	 * ぜんゆん集合.
	 */
	public static boolean gatheringYukkuriSquare(Entity topObject, Yukkuri[] targetList, GatheringDirection direction,
			EventPacket event) {
		int maxRowSize = 3;

		if (topObject == null || targetList == null) {
			return false;
		}
		int targetCount = targetList.length;
		if (targetCount == 0) {
			return false;
		}

		boolean isOddLayout = true;
		if (targetCount < maxRowSize) {
			maxRowSize = targetCount;
		}
		if (maxRowSize % 2 == 0) {
			isOddLayout = false;
		}

		int processedCount = 0;
		int lineOffset = 0;
		int lineIndex = 0;
		int row = 1;
		int horizontalDirection = -1;
		int collisionOffset = 10;
		Entity frontCenter = topObject;
		Entity nextFrontCenter = null;

		boolean success = true;
		for (Yukkuri body : targetList) {
			int gap = 10;
			if (body == null) {
				continue;
			}
			if (event != null && body.getCurrentEvent() != null && body.getCurrentEvent() != event) {
				continue;
			}

			processedCount++;
			int zShift = 0;
			if (body.canflyCheck()) {
				zShift = topObject.getZ();
			}

			collisionOffset = Translate.invertX(body.getCollisionX(), frontCenter.getY());
			int x = 0;
			int y = 0;
			boolean moved = false;

			if ((maxRowSize == 1) || (processedCount % maxRowSize == 1)) {
				lineIndex++;
				if (nextFrontCenter != null) {
					frontCenter = nextFrontCenter;
				}
				nextFrontCenter = body;
				int lastLineSize = targetCount - maxRowSize * (lineIndex - 1);
				if ((lastLineSize < maxRowSize) && (0 < lastLineSize)) {
					isOddLayout = true;
					if (lastLineSize % 2 == 0) {
						isOddLayout = false;
					}
				}

				lineOffset = collisionOffset + gap;
				switch (direction) {
					case UP:
						x = frontCenter.getX();
						y = frontCenter.getY() - lineOffset;
						break;
					case DOWN:
						x = frontCenter.getX();
						y = frontCenter.getY() + lineOffset;
						break;
					case LEFT:
						x = frontCenter.getX() - lineOffset;
						y = frontCenter.getY();
						break;
					case RIGHT:
						x = frontCenter.getX() + lineOffset;
						y = frontCenter.getY();
						break;
				}

				if (x < 0) {
					x = 0;
				} else if (Translate.getMapW() < x) {
					x = Translate.getMapW();
				}

				if (y < 0) {
					y = 0;
				} else if (Translate.getMapH() < y) {
					y = Translate.getMapH();
				}

				row = 1;
				if (isOddLayout) {
					if (event == null) {
						body.moveToBody(frontCenter, x, y, zShift);
					} else {
						body.moveToEvent(event, x, y, zShift);
					}
					moved = true;
				}
			}

			if (!moved) {
				switch (direction) {
					case UP:
						if (isOddLayout) {
							x = frontCenter.getX() + (collisionOffset + gap) * row * horizontalDirection;
							y = frontCenter.getY() - lineOffset;
						} else {
							if (row == 1) {
								gap = gap / 2;
							}
							x = frontCenter.getX() + (collisionOffset + gap) * (2 * row - 1) * horizontalDirection;
							y = frontCenter.getY() - lineOffset;
						}
						break;
					case DOWN:
						if (isOddLayout) {
							x = frontCenter.getX() + (collisionOffset + gap) * row * horizontalDirection;
							y = frontCenter.getY() + lineOffset;
						} else {
							if (row == 1) {
								gap = gap / 2;
							}
							x = frontCenter.getX() + (collisionOffset * row + gap * 3 / 2 * row - 1) * horizontalDirection;
							y = frontCenter.getY() + lineOffset;
						}
						break;
					case LEFT:
						if (isOddLayout) {
							x = frontCenter.getX() - lineOffset;
							y = frontCenter.getY() + (collisionOffset + gap) * row * horizontalDirection;
						} else {
							if (row == 1) {
								gap = gap / 2;
							}
							x = frontCenter.getX() - lineOffset;
							y = frontCenter.getY() + (collisionOffset + gap) * (2 * row - 1) * horizontalDirection;
						}
						break;
					case RIGHT:
						if (isOddLayout) {
							x = frontCenter.getX() + lineOffset;
							y = frontCenter.getY() + (collisionOffset + gap) * row * horizontalDirection;
						} else {
							if (row == 1) {
								gap = gap / 2;
							}
							x = frontCenter.getX() + lineOffset;
							y = frontCenter.getY() + (collisionOffset + gap) * (2 * row - 1) * horizontalDirection;
						}
						break;
				}

				if (x < 0) {
					x = 0;
				} else if (Translate.getMapW() < x) {
					x = Translate.getMapW();
				}
				if (y < 0) {
					y = 0;
				} else if (Translate.getMapH() < y) {
					y = Translate.getMapH();
				}

				if (event == null) {
					body.moveToBody(topObject, x, y, zShift);
				} else {
					body.moveToEvent(event, x, y, zShift);
				}

				if (horizontalDirection == -1) {
					horizontalDirection = 1;
				} else {
					horizontalDirection = -1;
					row++;
				}
			}

			if (Barrier.onBarrier(body.getX(), body.getY(), x, y,
					Barrier.MAP_BODY[body.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				continue;
			}

			if (1 < Translate.distance(body.getX(), body.getY(), x, y)) {
				success = false;
			}
		}
		return success;
	}

	/**
	 * ぜんゆん集合(先頭の後ろに一列)
	 */
	public static boolean gatheringYukkuriBackLine(Yukkuri topBody, List<Yukkuri> targetList, EventPacket event) {
		if (targetList == null) {
			return false;
		}

		Yukkuri currentBody = topBody;
		if (currentBody.getDirection() == Direction.RIGHT) {
			// no-op
		}
		boolean success = true;

		for (Yukkuri body : targetList) {
			if (body == null) {
				continue;
			}
			if (body.isDead()) {
				continue;
			}
			if (event != null && body.getCurrentEvent() != null && body.getCurrentEvent() != event) {
				continue;
			}
			int collisionOffset = Math.abs(BodyLogic.calcCollisionX(body, currentBody));
			int zShift = 0;
			if (body.canflyCheck()) {
				zShift = currentBody.getZ();
			}
			int dist = Translate.getRealDistance(body.getX(), body.getY(), currentBody.getX(), currentBody.getY());
			int targetDistance = dist - collisionOffset * 2;
			if (targetDistance < 1) {
				continue;
			}
			double radian = Translate.getRadian(body.getX(), body.getY(), currentBody.getX(), currentBody.getY());
			Point4y p2 = Translate.getPointByDistAndRad(body.getX(), body.getY(), targetDistance, radian);
			int x = p2.getX();
			int y = p2.getY();
			if (event == null) {
				body.moveToBody(currentBody, x, y, zShift);
			} else {
				body.moveToEvent(event, x, y, zShift);
			}
			body.setTargetBind(false);
			currentBody = body;
			if (Barrier.onBarrier(body.getX(), body.getY(), x, y,
					Barrier.MAP_BODY[body.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				continue;
			}
			if (1 < Translate.distance(body.getX(), body.getY(), x, y)) {
				success = false;
			} else {
				body.setDirection(currentBody.getDirection());
			}
		}
		return success;
	}
}
