package org.simyukkuri.engine.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.YukkuriType;

/**
 * Transformation policy のテスト.
 */
public class TransformationPolicyTest {

	@BeforeEach
	public void setUp() {
		MyPane.setSelectedYukkuri(null);
	}

	@AfterEach
	public void tearDown() {
		MyPane.setSelectedYukkuri(null);
	}

	@Test
	public void testNeedsDosReservation() {
		assertTrue(TransformationPolicy.needsDosReservation(YukkuriType.DOSMARISA));
		assertFalse(TransformationPolicy.needsDosReservation(YukkuriType.REIMU));
	}

	@Test
	public void testResolveBaseBodyFileName() {
		assertEquals("marisa", TransformationPolicy.resolveBaseYukkuriFileName(YukkuriType.MARISA));
		assertEquals("deibu", TransformationPolicy.resolveBaseYukkuriFileName(YukkuriType.DEIBU));
	}

	@Test
	public void testIsSelectedYukkuri() {
		Reimu selected = new Reimu();
		Marisa other = new Marisa();

		MyPane.setSelectedYukkuri(selected);

		assertTrue(TransformationPolicy.isSelectedYukkuri(selected));
		assertFalse(TransformationPolicy.isSelectedYukkuri(other));
		assertFalse(TransformationPolicy.isSelectedYukkuri(null));
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
