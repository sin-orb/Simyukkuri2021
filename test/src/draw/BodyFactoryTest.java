package src.draw;

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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.DosMarisa;
import src.entity.core.living.yukkuri.impl.Marisa;
import src.entity.core.living.yukkuri.impl.Reimu;
import src.enums.AgeState;
import src.enums.YukkuriType;
import src.util.WorldTestHelper;

public class BodyFactoryTest {

	@Test
	public void testCreateReimuLoadsExpectedImageAndReturnsBody() {
		WorldTestHelper.resetStates();
		WorldTestHelper.initializeMinimalWorld();
		List<YukkuriType> loaded = new ArrayList<YukkuriType>();
		Yukkuri body = BodyFactory.create(10, 20, 0, Reimu.type, null, AgeState.BABY, null, null, true,
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
		Yukkuri body = BodyFactory.create(10, 20, 0, DosMarisa.type, null, AgeState.BABY, null, null, true,
				loaded::add, () -> false);

		assertInstanceOf(Marisa.class, body);
		assertEquals(1, loaded.size());
		assertEquals(YukkuriType.MARISA, loaded.get(0));
	}
}
