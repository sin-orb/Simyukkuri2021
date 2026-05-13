package src.engine.transform;

import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.entity.core.living.yukkuri.impl.Marisa;
import src.entity.core.living.yukkuri.impl.Reimu;
import src.enums.AgeState;
import src.enums.YukkuriType;

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
