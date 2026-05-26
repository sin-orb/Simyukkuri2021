package org.simyukkuri.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.DosMarisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.util.WorldTestHelper;

public class YukkuriFactoryTest {

	@Test
	public void testCreateReimuLoadsExpectedImageAndReturnsBody() {
		WorldTestHelper.resetStates();
		WorldTestHelper.initializeMinimalWorld();
		List<YukkuriType> loaded = new ArrayList<YukkuriType>();
		Yukkuri body = YukkuriFactory.create(10, 20, 0, Reimu.type, null, AgeState.BABY, null, null, true,
				loaded::add, () -> true);

		assertInstanceOf(Reimu.class, body);
		assertEquals(1, loaded.size());
		assertEquals(YukkuriType.REIMU, loaded.get(0));
	}

	@Test
	public void testCreateDosMarisaFallsBackWhenDosMakerReturnsFalse() {
		WorldTestHelper.resetStates();
		WorldTestHelper.initializeMinimalWorld();
		List<YukkuriType> loaded = new ArrayList<YukkuriType>();
		Yukkuri body = YukkuriFactory.create(10, 20, 0, DosMarisa.type, null, AgeState.BABY, null, null, true,
				loaded::add, () -> false);

		assertInstanceOf(Marisa.class, body);
		assertEquals(1, loaded.size());
		assertEquals(YukkuriType.MARISA, loaded.get(0));
	}
}
