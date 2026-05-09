package src.engine.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.enums.AgeState;
import src.enums.YukkuriType;
import src.yukkuri.Marisa;
import src.yukkuri.Reimu;

public class TransformationPolicyTest {

	@Test
	public void testNeedsDosReservation() {
		assertTrue(TransformationPolicy.needsDosReservation(YukkuriType.DOSMARISA));
		assertFalse(TransformationPolicy.needsDosReservation(YukkuriType.REIMU));
	}

	@Test
	public void testResolveBaseBodyFileName() {
		assertEquals("marisa", TransformationPolicy.resolveBaseBodyFileName(YukkuriType.MARISA));
		assertEquals("deibu", TransformationPolicy.resolveBaseBodyFileName(YukkuriType.DEIBU));
	}

	@Test
	public void testNormalizeTransformedAge() {
		Marisa to = new Marisa();
		Reimu from = new Reimu();

		from.setAgeState(AgeState.BABY);
		TransformationPolicy.normalizeTransformedAge(to, from);
		assertEquals(0, to.getAge());

		from.setAgeState(AgeState.CHILD);
		TransformationPolicy.normalizeTransformedAge(to, from);
		assertEquals(to.getBabyLimitBase() + 1, to.getAge());

		from.setAgeState(AgeState.ADULT);
		TransformationPolicy.normalizeTransformedAge(to, from);
		assertEquals(to.getChildLimitBase() + 1, to.getAge());
	}
}
