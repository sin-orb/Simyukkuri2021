package src.draw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;

import org.junit.jupiter.api.Test;

public class SaveDataCodecTest {

	@Test
	public void testSaveAndLoadRoundTripPreservesCoreWorldFields() throws Exception {
		World world = new World();
		world.setMaxUniqueId(123);
		world.setMaxObjId(456);
		world.setWindowType(2);
		world.setTerrariumSizeIndex(3);

		File tempFile = File.createTempFile("simyukkuri-save", ".dat");
		tempFile.deleteOnExit();

		SaveDataCodec.save(world, tempFile);
		World restored = SaveDataCodec.load(tempFile);

		assertNotNull(restored);
		assertEquals(123, restored.getMaxUniqueId());
		assertEquals(456, restored.getMaxObjId());
		assertEquals(2, restored.getWindowType());
		assertEquals(3, restored.getTerrariumSizeIndex());
		assertNotNull(restored.getPlayer());
		assertNotNull(restored.getMapList());
	}
}
