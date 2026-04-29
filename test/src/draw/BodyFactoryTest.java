package src.draw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import src.base.Body;
import src.enums.AgeState;
import src.enums.YukkuriType;
import src.yukkuri.DosMarisa;
import src.yukkuri.Marisa;
import src.yukkuri.Reimu;
import src.util.WorldTestHelper;

public class BodyFactoryTest {

	@Test
	public void testCreateReimuLoadsExpectedImageAndReturnsBody() {
		WorldTestHelper.resetStates();
		WorldTestHelper.initializeMinimalWorld();
		List<YukkuriType> loaded = new ArrayList<YukkuriType>();
		Body body = BodyFactory.create(10, 20, 0, Reimu.type, null, AgeState.BABY, null, null, true,
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
		Body body = BodyFactory.create(10, 20, 0, DosMarisa.type, null, AgeState.BABY, null, null, true,
				loaded::add, () -> false);

		assertInstanceOf(Marisa.class, body);
		assertEquals(1, loaded.size());
		assertEquals(YukkuriType.MARISA, loaded.get(0));
	}
}
