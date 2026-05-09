package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.base.Body;
import src.entity.world.bodylinked.Okazari;
import src.enums.PredatorType;
import src.util.WorldTestHelper;

/**
 * {@link FoodEligibility} の直接テスト.
 */
class FoodEligibilityTest {

	private Body eater;
	private Body prey;

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
		eater = WorldTestHelper.createBody();
		prey = WorldTestHelper.createBody();
	}

	@AfterEach
	void tearDown() {
		WorldTestHelper.resetWorld();
	}

	@Test
	void predatorBodiesCanAlwaysEatBody() {
		eater.setPredatorType(PredatorType.BITE);
		assertTrue(FoodEligibility.checkCanEatBody(eater, prey));
	}

	@Test
	void nonPredatorCannotEatLivingBody() {
		prey.setDead(false);
		assertFalse(FoodEligibility.checkCanEatBody(eater, prey));
	}

	@Test
	void nonRudeBodiesRejectOkazariBodies() {
		prey.setDead(true);
		prey.setOkazari(new Okazari());
		assertFalse(FoodEligibility.checkCanEatBody(eater, prey));
	}
}
