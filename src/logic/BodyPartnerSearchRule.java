package src.logic;

import java.util.Map;

import src.attachment.Ants;
import src.attachment.Fire;
import src.base.Body;
import src.base.Okazari.OkazariType;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.BaryInUGState;
import src.enums.PanicType;
import src.enums.PublicRank;
import src.event.KillPredeatorEvent;
import src.item.Barrier;
import src.util.GameEnvironment;
import src.util.GameRandom;
import src.util.GameWorld;

/**
 * Candidate search for {@link BodyLogic#checkPartner(Body)}.
 */
public final class BodyPartnerSearchRule {

	/**
	 * Search result for partner selection.
	 */
	public static final class SearchResult {
		private final Body found;
		private final Body bodyHasOkazari;

		SearchResult(Body found, Body bodyHasOkazari) {
			this.found = found;
			this.bodyHasOkazari = bodyHasOkazari;
		}

		/**
		 * @return selected target body
		 */
		public Body getFound() {
			return found;
		}

		/**
		 * @return okazari steal target
		 */
		public Body getBodyHasOkazari() {
			return bodyHasOkazari;
		}
	}

	private BodyPartnerSearchRule() {
	}

	/**
	 * Select the best search targets for the current body.
	 *
	 * @param b                 actor body
	 * @param found             current preferred body
	 * @param minDistance       current minimum distance
	 * @param secondMinDistance current second minimum distance
	 * @return search result
	 */
	public static SearchResult selectTargets(Body b, Body found, int minDistance, int secondMinDistance) {
		Body bodyHasOkazari = null;
		Body bodyHasOkazariAndPherommone = null;
		Body bodyHasPheromone = null;

		for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			Body p = entry.getValue();
			if (p == b) {
				continue;
			}
			if (minDistance < 1 && !p.isbPheromone()) {
				continue;
			}
			if (!b.canflyCheck() && p.getZ() != 0) {
				continue;
			}

			if (p.isPacked()) {
			} else if (b.isServant() && p.isPredator()) {
			} else if (p.isFamily(b)) {
			} else if (GameEnvironment.isPredatorSteam()) {
			} else {
				if (b.getCurrentEvent() != null && b.getCurrentEvent().getClass().equals(KillPredeatorEvent.class)
						&& b.isAdult() && b.isNotNYD() && !b.isPacked() && !b.isBurned()
						&& !b.isHasBaby() && !b.isHasStalk()) {
					b.setPanic(false, null);
					b.setAngry();
				} else {
					int dist = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());
					if (p.isPredatorType() && dist <= b.getEYESIGHTorg() && b.getPanicType() == null) {
						if (b.canAction() && !b.isPredatorType() && !p.isFamily(b) && !b.isSleeping()) {
							if (p.getZ() < Translate.getFlyHeightLimit() || b.canflyCheck()) {
								if (!Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(),
										Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
									if (b.isNotNYD() && !b.isNeedled() && !b.isRaper()) {
										b.setPanic(true, PanicType.REMIRYA);
									}
								}
							}
						}
					}
				}
			}

			if (b.isExciting()) {
				if (p.getBaryState() != BaryInUGState.NONE) {
					continue;
				}
				if (p.getAttachmentSize(Fire.class) != 0) {
					continue;
				}
				if (p.isPacked()) {
					continue;
				}
				if (b.isRaper()) {
					if ((p.isDead() && p.isCrushed()) || p.isUnBirth() || p.isRaper()) {
						continue;
					}
				} else {
					if (p.isDead()) {
						continue;
					}
					if (!b.isForceExciting()) {
						if (b.getPublicRank() != p.getPublicRank()) {
							continue;
						}
						if (b.getBodyAgeState().ordinal() > p.getBodyAgeState().ordinal() || p.isChild(b)
								|| p.isParent(b)) {
							continue;
						}
					}
				}
			} else if (p.isDead() && !p.hasOkazari() && b.isIdiot()) {
				continue;
			}
			if (p.isRaper() && p.isExciting()) {
				continue;
			}
			if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(),
					Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				continue;
			}
			if (p.getBaryState() == BaryInUGState.ALL) {
				continue;
			}
			if (p.getBaryState() == BaryInUGState.NEARLY_ALL && !p.hasOkazari()) {
				continue;
			}
			if (p.isbPheromone()) {
				bodyHasPheromone = p;
			}
			int dist = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());
			if (minDistance > dist) {
				minDistance = dist;
				found = p;
			} else if (minDistance <= dist && dist < secondMinDistance) {
				secondMinDistance = dist;
				if (GameRandom.nextBoolean()) {
					found = p;
				}
			}
			if (!b.hasOkazari() && p.hasOkazari() && b.getBodyAgeState() == p.getBodyAgeState()
					&& b.getType() == p.getType() && !b.isHybrid()
					&& p.getOkazari().getOkazariType() == OkazariType.DEFAULT
					&& (p.getPublicRank() == PublicRank.NONE || b.getPublicRank() == PublicRank.UnunSlave)
					&& !b.isLockmove()) {
				if (b.isRude()) {
					bodyHasOkazari = p;
					if (p.isbPheromone()) {
						bodyHasOkazariAndPherommone = p;
					}
				}
			}
		}

		if (bodyHasPheromone != null) {
			found = bodyHasPheromone;
		}
		if (bodyHasOkazariAndPherommone != null) {
			bodyHasOkazari = bodyHasOkazariAndPherommone;
		}

		return new SearchResult(found, bodyHasOkazari);
	}
}
