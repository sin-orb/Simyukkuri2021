package org.simyukkuri.engine.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Deibu;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.util.WorldTestHelper;

public class TransformationServiceTest {

	@Test
	public void testTransformReplacesBodyAtSameUniqueId() {
		WorldTestHelper.resetWorld();
		try {
			WorldTestHelper.initializeMinimalWorld();
			SimYukkuri.mypane = new MyPane();

			Reimu reimu = new Reimu();
			reimu.setAge(100000);
			WorldTestHelper.makeTransformationReady(reimu);
			SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(reimu.getUniqueID(), reimu);

			int originalId = reimu.getUniqueID();

			TransformationService.transform(reimu, YukkuriType.DEIBU);

			Yukkuri transformed = SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().get(originalId);
			assertNotNull(transformed);
			assertInstanceOf(Deibu.class, transformed);
			assertEquals(originalId, transformed.getUniqueID());
			assertTrue(reimu.isRemoved());
		} finally {
			WorldTestHelper.resetWorld();
		}
	}
}
