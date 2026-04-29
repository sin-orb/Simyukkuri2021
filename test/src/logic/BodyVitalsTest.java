package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.base.Body;
import src.enums.AgeState;
import src.util.WorldTestHelper;

class BodyVitalsTest {

	private Body body;

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
		body = WorldTestHelper.createBody();
		body.setAgeState(AgeState.ADULT);
		body.setDead(false);
	}

	@AfterEach
	void tearDown() {
		WorldTestHelper.resetWorld();
	}

	@Test
	void detectsDamageLevels() {
		body.setDamage(0);
		assertTrue(BodyVitals.isNoDamaged(body));
		assertFalse(BodyVitals.isDamaged(body));
		assertFalse(BodyVitals.isDamagedHeavily(body));

		body.setDamage(body.getDamageLimit() / 2);
		assertFalse(BodyVitals.isNoDamaged(body));
		assertTrue(BodyVitals.isDamaged(body));
		assertFalse(BodyVitals.isDamagedHeavily(body));

		body.setDamage(body.getDamageLimit() * 3 / 4);
		assertTrue(BodyVitals.isDamaged(body));
		assertTrue(BodyVitals.isDamagedHeavily(body));
	}

	@Test
	void detectsHungerLevelsForLivingBody() {
		body.setHungry(body.getHungryLimit());
		assertTrue(BodyVitals.isFull(body));
		assertFalse(BodyVitals.isHungry(body));
		assertFalse(BodyVitals.isSoHungry(body));
		assertFalse(BodyVitals.isVeryHungry(body));

		body.setHungry(body.getHungryLimit() / 2);
		assertTrue(BodyVitals.isHungry(body));
		assertFalse(BodyVitals.isSoHungry(body));

		body.setHungry(1);
		assertTrue(BodyVitals.isSoHungry(body));
		assertFalse(BodyVitals.isVeryHungry(body));

		body.setHungry(0);
		assertTrue(BodyVitals.isVeryHungry(body));
	}

	@Test
	void deadBodyIsNotHungryOrFull() {
		body.setDead(true);
		body.setHungry(0);

		assertFalse(BodyVitals.isFull(body));
		assertFalse(BodyVitals.isHungry(body));
		assertFalse(BodyVitals.isSoHungry(body));
		assertFalse(BodyVitals.isVeryHungry(body));
		assertFalse(BodyVitals.isTooHungry(body));
		assertFalse(BodyVitals.isStarving(body));
	}

	@Test
	void detectsTooHungryAndStarvingFromDamageState() {
		body.setHungry(0);
		body.setDamage(0);
		assertFalse(BodyVitals.isTooHungry(body));
		assertFalse(BodyVitals.isStarving(body));

		body.setDamage(body.getDamageLimit() / 2);
		assertTrue(BodyVitals.isTooHungry(body));
		assertFalse(BodyVitals.isStarving(body));

		body.setDamage(body.getDamageLimit() * 3 / 4);
		assertTrue(BodyVitals.isTooHungry(body));
		assertTrue(BodyVitals.isStarving(body));
	}

	@Test
	void detectsSicknessStages() {
		body.setSickPeriod(body.getINCUBATIONPERIODorg());
		assertFalse(BodyVitals.isSick(body));
		assertFalse(BodyVitals.isSickHeavily(body));

		body.setSickPeriod(body.getINCUBATIONPERIODorg() + 1);
		assertTrue(BodyVitals.isSick(body));
		assertFalse(BodyVitals.isSickHeavily(body));

		body.setSickPeriod(body.getINCUBATIONPERIODorg() * 8 + 1);
		assertTrue(BodyVitals.isSickHeavily(body));
	}

	@Test
	void sickTooHeavilyRequiresHeavyStageAndDamage() {
		body.setSickPeriod(body.getINCUBATIONPERIODorg() * 32 + 1);
		body.setDamage(0);
		assertFalse(BodyVitals.isSickTooHeavily(body));

		body.setDamage(body.getDamageLimit() / 2);
		assertTrue(BodyVitals.isSickTooHeavily(body));
	}
}
