package src.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;
import src.enums.Attitude;
import src.enums.Damage;
import src.enums.HairState;
import src.enums.Intelligence;
import src.enums.LovePlayer;
import src.enums.Trauma;

class BodyCoreStateRuleTest {
	@Test
	void delegatesCoreState() {
		StubBodyAttributes body = new StubBodyAttributes();

		BodyCoreStateRule.setDamage(body, 123);
		BodyCoreStateRule.setStress(body, 456);
		BodyCoreStateRule.setTang(body, 789);
		BodyCoreStateRule.setDamageState(body, Damage.VERY);
		BodyCoreStateRule.setAttitude(body, Attitude.SHITHEAD);
		BodyCoreStateRule.setIntelligence(body, Intelligence.FOOL);
		BodyCoreStateRule.setShit(body, 321);
		BodyCoreStateRule.setMemories(body, 654);
		BodyCoreStateRule.setTrauma(body, Trauma.Ubuse);
		BodyCoreStateRule.setLovePlayer(body, 987);
		BodyCoreStateRule.setLovePlayerState(body, LovePlayer.GOOD);
		BodyCoreStateRule.setHairState(body, HairState.BRINDLED1);

		assertEquals(123, BodyCoreStateRule.getDamage(body));
		assertEquals(456, BodyCoreStateRule.getStress(body));
		assertEquals(789, BodyCoreStateRule.getTang(body));
		assertEquals(Damage.VERY, body.getDamageStateRaw());
		assertEquals(Attitude.SHITHEAD, BodyCoreStateRule.getAttitude(body));
		assertEquals(Intelligence.FOOL, BodyCoreStateRule.getIntelligence(body));
		assertEquals(321, BodyCoreStateRule.getShit(body));
		assertEquals(654, BodyCoreStateRule.getMemories(body));
		assertEquals(Trauma.Ubuse, BodyCoreStateRule.getTrauma(body));
		assertEquals(987, BodyCoreStateRule.getLovePlayer(body));
		assertEquals(LovePlayer.GOOD, BodyCoreStateRule.getLovePlayerState(body));
		assertEquals(HairState.BRINDLED1, BodyCoreStateRule.getHairState(body));
	}
}
