package src.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class YukkuriShitDelegateTest {
	@Test
	void checkShitReturnsFalseForIdleBody() {
		StubBody body = new StubBody();
		body.setUnBirth(false);
		body.setShit(0);
		body.setSleeping(false);
		body.setEating(false);
		body.setPeropero(false);
		body.setSukkiri(false);
		body.setPacked(false);
		body.setExciting(false);
		body.setMelt(false);

		assertFalse(new YukkuriShitDelegate(body).checkShit());
	}
}
