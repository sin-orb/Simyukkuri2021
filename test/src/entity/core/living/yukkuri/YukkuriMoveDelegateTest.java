package src.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import src.enums.PurposeOfMoving;

class YukkuriMoveDelegateTest {
	@Test
	void setToFoodAndClearTargetsChangePurposeOfMoving() {
		StubBody body = new StubBody();

		YukkuriMoveDelegate delegate = new YukkuriMoveDelegate(body);
		delegate.setToFood(true);
		assertEquals(PurposeOfMoving.FOOD, body.getPurposeOfMoving());

		delegate.clearTargets();
		assertEquals(PurposeOfMoving.NONE, body.getPurposeOfMoving());
	}
}
