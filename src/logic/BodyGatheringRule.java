package src.logic;

import java.util.List;
import java.util.Map;

import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Point4y;
import src.draw.Translate;
import src.enums.Direction;
import src.enums.GatheringDirection;
import src.item.Barrier;
import src.item.Toilet;
import src.util.GameWorld;
import src.util.YukkuriUtil;

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
		Body[] bodyList = YukkuriUtil.getBodyInstances();
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
	public static boolean gatheringYukkuriFront(Body bTop, List<Body> TargetList) {
		return gatheringYukkuriSquare(bTop, TargetList.toArray(new Body[0]), GatheringDirection.DOWN, null);
	}

	/**
	 * ぜんゆん集合(四角形前面)
	 */
	public static boolean gatheringYukkuriFront(Body bTop, List<Body> TargetList, EventPacket e) {
		return gatheringYukkuriSquare(bTop, TargetList.toArray(new Body[0]), GatheringDirection.DOWN, e);
	}

	/**
	 * ぜんゆん集合.
	 */
	public static boolean gatheringYukkuriSquare(Obj oTop, Body[] TargetList, GatheringDirection eDir,
			EventPacket e) {
		int nMaxRowSize = 3;

		if (oTop == null || TargetList == null) {
			return false;
		}
		int nSize = TargetList.length;
		if (nSize == 0) {
			return false;
		}

		boolean bKi = true;
		if (nSize < nMaxRowSize) {
			nMaxRowSize = nSize;
		}
		if (nMaxRowSize % 2 == 0) {
			bKi = false;
		}

		int nCount = 0;
		int nColY = 0;
		int nCol = 0;
		int nRow = 1;
		int nDir = -1;
		int colX = 10;
		Obj objFrontCenter = oTop;
		Obj objNextFrontCenter = null;

		boolean bFlag = true;
		for (Body b : TargetList) {
			int nSpace = 10;
			if (b == null) {
				continue;
			}
			if (e != null && b.getCurrentEvent() != null && b.getCurrentEvent() != e) {
				continue;
			}

			nCount++;
			int mz = 0;
			if (b.canflyCheck()) {
				mz = oTop.getZ();
			}

			colX = Translate.invertX(b.getCollisionX(), objFrontCenter.getY());
			int x = 0;
			int y = 0;
			boolean bMoved = false;

			if ((nMaxRowSize == 1) || (nCount % nMaxRowSize == 1)) {
				nCol++;
				if (objNextFrontCenter != null) {
					objFrontCenter = objNextFrontCenter;
				}
				objNextFrontCenter = b;
				int nLastLineSize = nSize - nMaxRowSize * (nCol - 1);
				if ((nLastLineSize < nMaxRowSize) && (0 < nLastLineSize)) {
					bKi = true;
					if (nLastLineSize % 2 == 0) {
						bKi = false;
					}
				}

				nColY = colX + nSpace;
				switch (eDir) {
					case UP:
						x = objFrontCenter.getX();
						y = objFrontCenter.getY() - nColY;
						break;
					case DOWN:
						x = objFrontCenter.getX();
						y = objFrontCenter.getY() + nColY;
						break;
					case LEFT:
						x = objFrontCenter.getX() - nColY;
						y = objFrontCenter.getY();
						break;
					case RIGHT:
						x = objFrontCenter.getX() + nColY;
						y = objFrontCenter.getY();
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

				nRow = 1;
				if (bKi) {
					if (e == null) {
						b.moveToBody(objFrontCenter, x, y, mz);
					} else {
						b.moveToEvent(e, x, y, mz);
					}
					bMoved = true;
				}
			}

			if (!bMoved) {
				switch (eDir) {
					case UP:
						if (bKi) {
							x = objFrontCenter.getX() + (colX + nSpace) * nRow * nDir;
							y = objFrontCenter.getY() - nColY;
						} else {
							if (nRow == 1) {
								nSpace = nSpace / 2;
							}
							x = objFrontCenter.getX() + (colX + nSpace) * (2 * nRow - 1) * nDir;
							y = objFrontCenter.getY() - nColY;
						}
						break;
					case DOWN:
						if (bKi) {
							x = objFrontCenter.getX() + (colX + nSpace) * nRow * nDir;
							y = objFrontCenter.getY() + nColY;
						} else {
							if (nRow == 1) {
								nSpace = nSpace / 2;
							}
							x = objFrontCenter.getX() + (colX * nRow + nSpace * 3 / 2 * nRow - 1) * nDir;
							y = objFrontCenter.getY() + nColY;
						}
						break;
					case LEFT:
						if (bKi) {
							x = objFrontCenter.getX() - nColY;
							y = objFrontCenter.getY() + (colX + nSpace) * nRow * nDir;
						} else {
							if (nRow == 1) {
								nSpace = nSpace / 2;
							}
							x = objFrontCenter.getX() - nColY;
							y = objFrontCenter.getY() + (colX + nSpace) * (2 * nRow - 1) * nDir;
						}
						break;
					case RIGHT:
						if (bKi) {
							x = objFrontCenter.getX() + nColY;
							y = objFrontCenter.getY() + (colX + nSpace) * nRow * nDir;
						} else {
							if (nRow == 1) {
								nSpace = nSpace / 2;
							}
							x = objFrontCenter.getX() + nColY;
							y = objFrontCenter.getY() + (colX + nSpace) * (2 * nRow - 1) * nDir;
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

				if (e == null) {
					b.moveToBody(oTop, x, y, mz);
				} else {
					b.moveToEvent(e, x, y, mz);
				}

				if (nDir == -1) {
					nDir = 1;
				} else {
					nDir = -1;
					nRow++;
				}
			}

			if (Barrier.onBarrier(b.getX(), b.getY(), x, y,
					Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				continue;
			}

			if (1 < Translate.distance(b.getX(), b.getY(), x, y)) {
				bFlag = false;
			}
		}
		return bFlag;
	}

	/**
	 * ぜんゆん集合(先頭の後ろに一列)
	 */
	public static boolean gatheringYukkuriBackLine(Body bTop, List<Body> TargetList, EventPacket e) {
		if (TargetList == null) {
			return false;
		}

		Body bodyFound = bTop;
		if (bodyFound.getDirection() == Direction.RIGHT) {
			// no-op
		}
		boolean bResult = true;

		for (Body b : TargetList) {
			if (b == null) {
				continue;
			}
			if (b.isDead()) {
				continue;
			}
			if (e != null && b.getCurrentEvent() != null && b.getCurrentEvent() != e) {
				continue;
			}
			int colX = Math.abs(BodyLogic.calcCollisionX(b, bodyFound));
			int mz = 0;
			if (b.canflyCheck()) {
				mz = bodyFound.getZ();
			}
			int dist = Translate.getRealDistance(b.getX(), b.getY(), bodyFound.getX(), bodyFound.getY());
			int nToDist = dist - colX * 2;
			if (nToDist < 1) {
				continue;
			}
			double dRad = Translate.getRadian(b.getX(), b.getY(), bodyFound.getX(), bodyFound.getY());
			Point4y p2 = Translate.getPointByDistAndRad(b.getX(), b.getY(), nToDist, dRad);
			int x = p2.getX();
			int y = p2.getY();
			if (e == null) {
				b.moveToBody(bodyFound, x, y, mz);
			} else {
				b.moveToEvent(e, x, y, mz);
			}
			b.setTargetBind(false);
			bodyFound = b;
			if (Barrier.onBarrier(b.getX(), b.getY(), x, y,
					Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				continue;
			}
			if (1 < Translate.distance(b.getX(), b.getY(), x, y)) {
				bResult = false;
			} else {
				b.setDirection(bodyFound.getDirection());
			}
		}
		return bResult;
	}
}
