package src.logic;

import src.base.Yukkuri;

import java.util.Map;

import src.attachment.Ants;
import src.attachment.Fire;
import src.base.Yukkuri;
import src.entity.world.bodylinked.Okazari.OkazariType;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.BurialState;
import src.enums.PanicType;
import src.enums.PublicRank;
import src.event.KillPredeatorEvent;
import src.field.impl.Barrier;
import src.util.GameEnvironment;
import src.util.GameRandom;
import src.util.GameWorld;

/**
 * Candidate search for {@link BodyLogic#checkPartner(Yukkuri)}.
 */
public final class BodyPartnerSearchRule {

	/**
	 * Search result for partner selection.
	 */
	public static final class SearchResult {
		private final Yukkuri targetBody;
		private final Yukkuri bodyHasOkazari;

		SearchResult(Yukkuri targetBody, Yukkuri bodyHasOkazari) {
			this.targetBody = targetBody;
			this.bodyHasOkazari = bodyHasOkazari;
		}

		/**
		 * @return selected target body
		 */
		public Yukkuri getFound() {
			return targetBody;
		}

		/**
		 * @return okazari steal target
		 */
		public Yukkuri getBodyHasOkazari() {
			return bodyHasOkazari;
		}
	}

	private BodyPartnerSearchRule() {
	}

	/**
	 * Select the best search targets for the current body.
	 *
	 * @param b                 actor body
	 * @param targetBody        current preferred body
	 * @param minDistance       current minimum distance
	 * @param secondMinDistance current second minimum distance
	 * @return search result
	 */
	public static SearchResult selectTargets(Yukkuri body, Yukkuri targetBody, int minDistance, int secondMinDistance) {
		Yukkuri bodyHasOkazari = null;
		Yukkuri bodyHasOkazariAndPherommone = null;
		Yukkuri bodyHasPheromone = null;

		for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			Yukkuri candidateBody = entry.getValue();
			if (candidateBody == body) {
				continue;
			}
			if (minDistance < 1 && !candidateBody.isPheromone()) {
				continue;
			}
			if (!body.canflyCheck() && candidateBody.getZ() != 0) {
				continue;
			}

			if (candidateBody.isPacked()) {
			} else if (body.isServantOf(candidateBody.getType()) && candidateBody.isPredator()) {
				continue;
			} else if (candidateBody.isFamily(body)) {
			} else if (GameEnvironment.isPredatorSteam()) {
			} else {
				if (body.getCurrentEvent() != null && body.getCurrentEvent().getClass().equals(KillPredeatorEvent.class)
						&& body.isAdult() && body.isNotNYD() && !body.isPacked() && !body.isBurned()
						&& !body.isHasBaby() && !body.isHasStalk()) {
					body.setPanic(false, null);
					body.setAngry();
				} else {
					int distance = Translate.distance(body.getX(), body.getY(), candidateBody.getX(), candidateBody.getY());
					if (candidateBody.isPredatorType() && distance <= body.getEyesightBase() && body.getPanicType() == null) {
						if (body.canAction() && !body.isPredatorType() && !candidateBody.isFamily(body) && !body.isSleeping()) {
							if (candidateBody.getZ() < Translate.getFlyHeightLimit() || body.canflyCheck()) {
								if (!Barrier.acrossBarrier(body.getX(), body.getY(), candidateBody.getX(), candidateBody.getY(),
										Barrier.MAP_BODY[body.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
									if (body.isNotNYD() && !body.isNeedled() && !body.isRaper()) {
										body.setPanic(true, PanicType.REMIRYA);
									}
								}
							}
						}
					}
				}
			}

			if (body.isExciting()) {
				if (candidateBody.getBurialState() != BurialState.NONE) {
					continue;
				}
				if (candidateBody.getAttachmentSize(Fire.class) != 0) {
					continue;
				}
				if (candidateBody.isPacked()) {
					continue;
				}
				if (body.isRaper()) {
					if ((candidateBody.isDead() && candidateBody.isCrushed()) || candidateBody.isUnBirth() || candidateBody.isRaper()) {
						continue;
					}
				} else {
					if (candidateBody.isDead()) {
						continue;
					}
					if (!body.isForceExciting()) {
						if (body.getPublicRank() != candidateBody.getPublicRank()) {
							continue;
						}
						if (body.getBodyAgeState().ordinal() > candidateBody.getBodyAgeState().ordinal() || candidateBody.isChild(body)
								|| candidateBody.isParent(body)) {
							continue;
						}
					}
				}
			} else if (candidateBody.isDead() && !candidateBody.hasOkazari() && body.isIdiot()) {
				continue;
			}
			if (candidateBody.isRaper() && candidateBody.isExciting()) {
				continue;
			}
			if (Barrier.acrossBarrier(body.getX(), body.getY(), candidateBody.getX(), candidateBody.getY(),
					Barrier.MAP_BODY[body.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				continue;
			}
			if (candidateBody.getBurialState() == BurialState.ALL) {
				continue;
			}
			if (candidateBody.getBurialState() == BurialState.NEARLY_ALL && !candidateBody.hasOkazari()) {
				continue;
			}
			if (candidateBody.isPheromone()) {
				bodyHasPheromone = candidateBody;
			}
			int distance = Translate.distance(body.getX(), body.getY(), candidateBody.getX(), candidateBody.getY());
			if (minDistance > distance) {
				minDistance = distance;
				targetBody = candidateBody;
			} else if (minDistance <= distance && distance < secondMinDistance) {
				secondMinDistance = distance;
				if (GameRandom.nextBoolean()) {
					targetBody = candidateBody;
				}
			}
			if (!body.hasOkazari() && candidateBody.hasOkazari() && body.getBodyAgeState() == candidateBody.getBodyAgeState()
					&& body.getType() == candidateBody.getType() && !body.isHybrid()
					&& candidateBody.getOkazari().getOkazariType() == OkazariType.DEFAULT
					&& (candidateBody.getPublicRank() == PublicRank.NONE || body.getPublicRank() == PublicRank.UnunSlave)
					&& !body.isLockmove()) {
				if (body.isRude()) {
					bodyHasOkazari = candidateBody;
					if (candidateBody.isPheromone()) {
						bodyHasOkazariAndPherommone = candidateBody;
					}
				}
			}
		}

		if (bodyHasPheromone != null) {
			targetBody = bodyHasPheromone;
		}
		if (bodyHasOkazariAndPherommone != null) {
			bodyHasOkazari = bodyHasOkazariAndPherommone;
		}

		return new SearchResult(targetBody, bodyHasOkazari);
	}
}
