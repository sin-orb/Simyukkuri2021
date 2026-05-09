package src.engine.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.draw.MyPane;
import src.enums.AgeState;
import src.enums.YukkuriType;
import src.util.WorldTestHelper;
import src.yukkuri.Deibu;
import src.yukkuri.Reimu;

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
			SimYukkuri.world.getCurrentMap().getBody().put(reimu.getUniqueID(), reimu);

			int originalId = reimu.getUniqueID();

			TransformationService.transform(reimu, YukkuriType.DEIBU);

			Body transformed = SimYukkuri.world.getCurrentMap().getBody().get(originalId);
			assertNotNull(transformed);
			assertInstanceOf(Deibu.class, transformed);
			assertEquals(originalId, transformed.getUniqueID());
			assertTrue(reimu.isRemoved());
		} finally {
			WorldTestHelper.resetWorld();
		}
	}
}
