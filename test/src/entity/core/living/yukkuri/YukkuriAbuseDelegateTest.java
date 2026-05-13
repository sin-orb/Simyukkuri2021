package src.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.enums.HairState;
import src.draw.World;

class YukkuriAbuseDelegateTest {

	@BeforeEach
	void setUp() {
		SimYukkuri.world = new World();
	}

	@Test
	void takeBraid_turnsOnBraidWhenBraidType() {
		StubBody body = new StubBody();
		body.setBraidType(true);
		body.setHasBraid(false);
		new YukkuriAbuseDelegate(body).takeBraid();
		assertTrue(body.isHasBraid());
	}

	@Test
	void peal_turnsOnPealedAndBaldHead() {
		StubBody body = new StubBody();
		body.setPealed(false);
		body.setPacked(false);
		body.setHairState(HairState.DEFAULT);
		new YukkuriAbuseDelegate(body).peal();
		assertTrue(body.isPealed());
		assertTrue(body.getHairState() == HairState.BALDHEAD);
	}

	@Test
	void pack_turnsOnPackedAndDisablesTalk() {
		StubBody body = new StubBody();
		body.setPacked(false);
		body.setPealed(true);
		body.setCanTalk(true);
		new YukkuriAbuseDelegate(body).pack();
		assertTrue(body.isPacked());
		assertFalse(body.isCanTalk());
	}

	@Test
	void breakeyes_turnsOnBlind() {
		StubBody body = new StubBody();
		body.setBlind(false);
		new YukkuriAbuseDelegate(body).breakeyes();
		assertTrue(body.isBlind());
	}

	@Test
	void shutMouth_turnsOnShutmouth() {
		StubBody body = new StubBody();
		body.setShutmouth(false);
		new YukkuriAbuseDelegate(body).ShutMouth();
		assertTrue(body.isShutmouth());
	}

	@Test
	void pickHair_fromBaldHeadRestoresDefault() {
		StubBody body = new StubBody();
		body.setHairState(HairState.BALDHEAD);
		new YukkuriAbuseDelegate(body).pickHair();
		assertTrue(body.getHairState() == HairState.DEFAULT);
	}
}
