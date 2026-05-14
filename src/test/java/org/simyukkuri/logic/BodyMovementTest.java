package org.simyukkuri.logic;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.CriticalDamegeType;
import org.simyukkuri.enums.PredatorType;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;
import org.simyukkuri.util.RandomSource;
import org.simyukkuri.util.WorldTestHelper;

class BodyMovementTest {

	private Yukkuri body;

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
		body = WorldTestHelper.createBody();
		body.setAgeState(AgeState.ADULT);
		body.setDead(false);
		body.setDamage(0);
		body.setHungry(body.getHungryLimit());
		body.setFlyingType(false);
		body.setHasBaby(false);
		body.setHasStalk(false);
		body.setBlind(false);
		body.setPredatorType(null);
	}

	@AfterEach
	void tearDown() {
		GameRandom.clearOverride();
		WorldTestHelper.resetWorld();
	}

	@Test
	void normalAdultUsesConfiguredStep() {
		assertEquals(body.getStepBase()[AgeState.ADULT.ordinal()], BodyMovement.calculateMovementStep(body));
	}

	@Test
	void hungryNonPredatorHalvesStep() {
		body.setHungry(0);
		body.setPredatorType(null);

		assertEquals(2, BodyMovement.calculateMovementStep(body));
	}

	@Test
	void hungryPredatorKeepsStep() {
		body.setHungry(0);
		body.setPredatorType(PredatorType.BITE);

		assertEquals(4, BodyMovement.calculateMovementStep(body));
	}

	@Test
	void damageSicknessPainAndCarryingHalveStep() {
		body.setDamage(body.getDamageLimit() / 2);
		assertEquals(2, BodyMovement.calculateMovementStep(body));

		body.setDamage(0);
		body.forceSetSick();
		assertEquals(2, BodyMovement.calculateMovementStep(body));

		body.setSickPeriod(0);
		body.setCriticalDamege(CriticalDamegeType.INJURED);
		assertEquals(2, BodyMovement.calculateMovementStep(body));

		body.setCriticalDamege(null);
		body.setHasBaby(true);
		assertEquals(2, BodyMovement.calculateMovementStep(body));
	}

	@Test
	void antsAndBlindCanReduceStepToMinimumOne() {
		body.addAttachment(new Ants());
		body.setBlind(true);

		assertEquals(1, BodyMovement.calculateMovementStep(body));
	}

	@Test
	void stepZeroIsCorrectedToOne() {
		body.setStepBase(new int[] { 0, 0, 0 });

		assertEquals(1, BodyMovement.calculateMovementStep(body));
	}

	@Test
	void movementFrequencyUsesAdultStepAsBase() {
		assertEquals(2, BodyMovement.calculateMovementFrequency(body, 2));
		assertEquals(4, BodyMovement.calculateMovementFrequency(body, 1));
	}

	@Test
	void destinationXAheadSetsPositiveDirection() {
		body.setX(10);
		body.setDestX(20);

		BodyMovement.updateDestinationDirectionX(body);

		assertEquals(1, body.getDirX());
		assertEquals(20, body.getDestX());
	}

	@Test
	void destinationXBehindSetsNegativeDirection() {
		body.setX(20);
		body.setDestX(10);

		BodyMovement.updateDestinationDirectionX(body);

		assertEquals(-1, body.getDirX());
		assertEquals(10, body.getDestX());
	}

	@Test
	void destinationXReachedClearsDestination() {
		body.setX(10);
		body.setDestX(10);

		BodyMovement.updateDestinationDirectionX(body);

		assertEquals(0, body.getDirX());
		assertEquals(-1, body.getDestX());
	}

	@Test
	void destinationYAheadSetsPositiveDirection() {
		body.setY(10);
		body.setDestY(20);

		BodyMovement.updateDestinationDirectionY(body);

		assertEquals(1, body.getDirY());
		assertEquals(20, body.getDestY());
	}

	@Test
	void destinationYBehindSetsNegativeDirection() {
		body.setY(20);
		body.setDestY(10);

		BodyMovement.updateDestinationDirectionY(body);

		assertEquals(-1, body.getDirY());
		assertEquals(10, body.getDestY());
	}

	@Test
	void destinationYReachedClearsDestination() {
		body.setY(10);
		body.setDestY(10);

		BodyMovement.updateDestinationDirectionY(body);

		assertEquals(0, body.getDirY());
		assertEquals(-1, body.getDestY());
	}

	@Test
	void randomDirectionXBeforeThresholdOnlyIncrementsCount() {
		body.setSameDirectionFactor(2);
		body.setCountX(body.getSameDirectionFactor() * body.getStepBase()[AgeState.ADULT.ordinal()] - 1);
		body.setDirX(1);

		BodyMovement.updateRandomDirectionX(body);

		assertEquals(body.getSameDirectionFactor() * body.getStepBase()[AgeState.ADULT.ordinal()], body.getCountX());
		assertEquals(1, body.getDirX());
	}

	@Test
	void randomDirectionXAtThresholdResetsCountAndUpdatesDirection() {
		body.setSameDirectionFactor(2);
		body.setCountX(body.getSameDirectionFactor() * body.getStepBase()[AgeState.ADULT.ordinal()]);
		body.setDirX(0);
		GameRandom.setOverride(fixedRandom(0, true));

		BodyMovement.updateRandomDirectionX(body);

		assertEquals(0, body.getCountX());
		assertEquals(1, body.getDirX());
	}

	@Test
	void randomDirectionYBeforeThresholdOnlyIncrementsCount() {
		body.setSameDirectionFactor(2);
		body.setCountY(body.getSameDirectionFactor() * body.getStepBase()[AgeState.ADULT.ordinal()] - 1);
		body.setDirY(-1);

		BodyMovement.updateRandomDirectionY(body);

		assertEquals(body.getSameDirectionFactor() * body.getStepBase()[AgeState.ADULT.ordinal()], body.getCountY());
		assertEquals(-1, body.getDirY());
	}

	@Test
	void randomDirectionYAtThresholdResetsCountAndUpdatesDirection() {
		body.setSameDirectionFactor(2);
		body.setCountY(body.getSameDirectionFactor() * body.getStepBase()[AgeState.ADULT.ordinal()]);
		body.setDirY(0);
		GameRandom.setOverride(fixedRandom(0, false));

		BodyMovement.updateRandomDirectionY(body);

		assertEquals(0, body.getCountY());
		assertEquals(-1, body.getDirY());
	}

	@Test
	void directionalStepDoublesForExcitingRaper() {
		assertEquals(1, BodyMovement.calculateDirectionalStep(body));

		body.setRaper(true);
		body.setExciting(true);

		assertEquals(2, BodyMovement.calculateDirectionalStep(body));
	}

	@Test
	void movementVectorUsesDirectionsStepAndSpeed() {
		body.setDirX(1);
		body.setDirY(-1);
		body.setDirZ(1);
		body.setSpeed(200);

		BodyMovement.MovementVector vector = BodyMovement.calculateMovementVector(body, 1);

		assertEquals(2, vector.getX());
		assertEquals(-2, vector.getY());
		assertEquals(2, vector.getZ());
	}

	@Test
	void movementVectorAddsRemainderStepWhenRandomHits() {
		body.setDirX(1);
		body.setDirY(-1);
		body.setDirZ(1);
		body.setSpeed(150);
		GameRandom.setOverride(fixedRandom(0, false));

		BodyMovement.MovementVector vector = BodyMovement.calculateMovementVector(body, 1);

		assertEquals(2, vector.getX());
		assertEquals(-2, vector.getY());
		assertEquals(2, vector.getZ());
	}

	@Test
	void movementVectorKeepsBaseVectorWhenRandomMissesRemainder() {
		body.setDirX(1);
		body.setDirY(-1);
		body.setDirZ(1);
		body.setSpeed(150);
		GameRandom.setOverride(fixedRandom(99, false));

		BodyMovement.MovementVector vector = BodyMovement.calculateMovementVector(body, 1);

		assertEquals(1, vector.getX());
		assertEquals(-1, vector.getY());
		assertEquals(1, vector.getZ());
	}

	@Test
	void flightDestinationAheadSetsPositiveZDirection() {
		Yukkuri target = createMappedMoveTarget();
		body.setMoveTargetId(target.getObjId());
		body.setFlyingType(true);
		body.setHasBraid(true);
		body.setZ(10);
		body.setDestZ(20);

		BodyMovement.updateFlightDestination(body);

		assertEquals(1, body.getDirZ());
		assertEquals(20, body.getDestZ());
	}

	@Test
	void flightDestinationReachedClearsDestinationWhenTargetExists() {
		Yukkuri target = createMappedMoveTarget();
		body.setMoveTargetId(target.getObjId());
		body.setFlyingType(true);
		body.setHasBraid(true);
		body.setZ(10);
		body.setDestZ(10);

		BodyMovement.updateFlightDestination(body);

		assertEquals(0, body.getDirZ());
		assertEquals(-1, body.getDestZ());
	}

	@Test
	void flightWithoutTargetKeepsHeightLimitAsDestination() {
		body.setMoveTargetId(-1);
		body.setFlyingType(true);
		body.setHasBraid(true);
		body.setDestZ(-1);

		BodyMovement.updateFlightDestination(body);

		assertEquals(Translate.getFlyHeightLimit(), body.getDestZ());
	}

	@Test
	void nonFlyingBodyDoesNotUpdateFlightDestination() {
		body.setFlyingType(false);
		body.setDirZ(7);
		body.setDestZ(20);

		BodyMovement.updateFlightDestination(body);

		assertEquals(7, body.getDirZ());
		assertEquals(20, body.getDestZ());
	}

	@Test
	void directedMovementClampsToDestinationWithoutOvershoot() {
		body.setX(10);
		body.setY(10);
		body.setZ(10);
		body.setDestX(11);
		body.setDestY(9);
		body.setDestZ(11);
		body.setDirX(1);
		body.setDirY(-1);
		body.setDirZ(1);
		body.setFlyingType(true);
		body.setHasBraid(true);

		BodyMovement.applyDirectedMovement(body, BodyMovement.MovementVector.of(2, -2, 2));

		assertEquals(11, body.getX());
		assertEquals(9, body.getY());
		assertEquals(11, body.getZ());
	}

	@Test
	void externalMotionClampsXUnderflowAndAddsFallDamage() {
		initializeSprites(body);
		body.setX(0);
		body.setY(10);
		body.setVx(-5);
		body.setVy(0);
		body.setVz(0);
		body.setMotionX(0);
		body.setMotionY(0);
		body.setMotionZ(0);

		boolean handled = BodyMovement.applyExternalMotion(body);

		assertEquals(0, body.getX());
		assertEquals(0, body.getVx());
		assertEquals(5, body.getFalldownDamage());
		assertFalse(handled);
	}

	@Test
	void externalMotionYOverflowSetsNegativeDirection() {
		initializeSprites(body);
		body.setX(10);
		body.setY(Translate.getMapH());
		body.setVx(0);
		body.setVy(5);
		body.setVz(0);
		body.setMotionX(0);
		body.setMotionY(0);
		body.setMotionZ(0);

		boolean handled = BodyMovement.applyExternalMotion(body);

		assertEquals(Translate.getMapH(), body.getY());
		assertEquals(0, body.getVy());
		assertEquals(-1, body.getDirY());
		assertFalse(handled);
	}

	@Test
	void externalMotionFallsWhenDepthDiffersWithoutFlight() {
		body.setHasBraid(false);
		body.setZ(1);
		body.setMostDepth(0);
		body.setVx(0);
		body.setVy(0);
		body.setVz(0);
		body.setMotionX(0);
		body.setMotionY(0);
		body.setMotionZ(0);

		boolean handled = BodyMovement.applyExternalMotion(body);

		assertTrue(handled);
		assertEquals(0, body.getZ());
	}

	@Test
	void externalMotionLandingClearsNoDamageNextFall() {
		body.setZ(1);
		body.setMostDepth(0);
		body.setVx(0);
		body.setVy(0);
		body.setVz(1);
		body.setMotionX(0);
		body.setMotionY(0);
		body.setMotionZ(0);
		body.setFalldownDamage(10);
		body.setNoDamageNextFall(true);

		boolean handled = BodyMovement.applyExternalMotion(body);

		assertTrue(handled);
		assertEquals(0, body.getZ());
		assertEquals(0, body.getFalldownDamage());
		assertEquals(false, body.isNoDamageNextFall());
	}

	@Test
	void directedMovementWallHitWithDestinationIncrementsBlockedCount() {
		initializeSprites(body);
		body.setX(100);
		body.setY(100);
		body.setZ(0);
		body.setDestX(101);
		body.setDestY(100);
		body.setDirX(1);
		body.setDirY(0);
		Translate.setCurrentWallMapNum(101, 100, org.simyukkuri.field.impl.Barrier.MAP_BODY[body.getBodyAgeState().ordinal()]);

		BodyMovement.applyDirectedMovement(body, BodyMovement.MovementVector.of(1, 0, 0));
		BodyMovement.resolveDirectedMovement(body, BodyMovement.MovementVector.of(1, 0, 0));

		assertEquals(100, body.getX());
		assertEquals(1, body.getBlockedTicks());
	}

	@Test
	void directedMovementAvoidsPoolWhenBodyDislikesWater() {
		initializeSprites(body);
		body.setX(140);
		body.setY(140);
		body.setZ(0);
		body.setDestX(141);
		body.setDestY(140);
		body.setDirX(1);
		body.setDirY(0);
		body.setLikeWater(false);
		body.setIntelligence(org.simyukkuri.enums.Intelligence.WISE);
		GameRandom.setOverride(fixedRandom(1, true));
		Translate.setCurrentFieldMapNum(141, 140, org.simyukkuri.field.FieldShape.FIELD_POOL);
		Translate.setCurrentFieldMapNum(140, 140, 0);

		BodyMovement.applyDirectedMovement(body, BodyMovement.MovementVector.of(1, 0, 0));
		BodyMovement.resolveDirectedMovement(body, BodyMovement.MovementVector.of(1, 0, 0));

		assertEquals(140, body.getX());
	}

	@Test
	void moveToClampsDestinationToMapRange() {
		BodyMovement.moveTo(body, -10, Translate.getMapH() + 10, Translate.getMapZ() + 20);

		assertEquals(0, body.getDestX());
		assertEquals(Translate.getMapH(), body.getDestY());
		assertEquals(Translate.getMapZ(), body.getDestZ());
	}

	@Test
	void moveToBodyClearsActionsAndSetsTargetFlag() {
		Entity target = createMappedMoveTarget();
		body.setToShit(true);
		body.setToSukkiri(true);

		BodyMovement.moveToBody(body, target, 100, 200, 0);

		assertTrue(body.isToBody());
		assertFalse(body.isToShit());
		assertFalse(body.isToSukkiri());
		assertEquals(target.getObjId(), body.getMoveTargetId());
		assertEquals(100, body.getDestX());
		assertEquals(Math.min(200, Translate.getMapH()), body.getDestY());
	}

	@Test
	void runAwayTargetsOppositeCornerAndSetsScare() {
		body.setX(200);
		body.setY(200);
		body.setScare(false);

		BodyMovement.runAway(body, 100, 100);

		assertEquals(Translate.getMapW(), body.getDestX());
		assertEquals(Translate.getMapH(), body.getDestY());
		assertTrue(body.isScare());
	}

	@Test
	void runAwayIgnoredWhenBodyCannotAct() {
		body.setDead(true);
		body.setDestX(-1);
		body.setDestY(-1);

		BodyMovement.runAway(body, 100, 100);

		assertEquals(-1, body.getDestX());
		assertEquals(-1, body.getDestY());
	}

	private RandomSource fixedRandom(final int fixedInt, final boolean fixedBoolean) {
		return new RandomSource() {
			@Override
			public int nextInt(int bound) {
				return Math.min(fixedInt, bound - 1);
			}

			@Override
			public boolean nextBoolean() {
				return fixedBoolean;
			}
		};
	}

	private Yukkuri createMappedMoveTarget() {
		Yukkuri target = WorldTestHelper.createBody();
		GameWorld.get().getCurrentMap().getBody().put(target.getUniqueID(), target);
		return target;
	}

	private void initializeSprites(Yukkuri target) {
		Sprite[] bodySprites = new Sprite[3];
		Sprite[] expandSprites = new Sprite[3];
		Sprite[] braidSprites = new Sprite[3];
		for (int i = 0; i < bodySprites.length; i++) {
			bodySprites[i] = new Sprite(100, 100, Sprite.PIVOT_CENTER_BOTTOM);
			expandSprites[i] = new Sprite(100, 100, Sprite.PIVOT_CENTER_BOTTOM);
			braidSprites[i] = new Sprite(100, 100, Sprite.PIVOT_CENTER_BOTTOM);
		}
		target.setBodySpr(bodySprites);
		target.setExpandSpr(expandSprites);
		target.setBraidSpr(braidSprites);
	}
}
