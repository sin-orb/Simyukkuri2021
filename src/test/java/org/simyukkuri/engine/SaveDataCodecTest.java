package org.simyukkuri.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SaveDataCodecTest {

	@Test
	@DisplayName("保存/復元で世界の主要フィールドが保持される")
	public void testSaveAndLoadRoundTripPreservesCoreWorldFields() throws Exception {
		World world = new World();
		world.setMaxUniqueId(123);
		world.setMaxObjId(456);
		world.setWindowType(2);
		world.setTerrariumSizeIndex(3);
		world.setCurrentWorldStateIndex(1);
		world.setNextWorldStateIndex(2);

		File tempFile = File.createTempFile("simyukkuri-save", ".dat");
		tempFile.deleteOnExit();

		SaveDataCodec.save(world, tempFile);
		World restored = SaveDataCodec.load(tempFile);

		assertEquals(123, restored.getMaxUniqueId());
		assertEquals(456, restored.getMaxObjId());
		assertEquals(2, restored.getWindowType());
		assertEquals(3, restored.getTerrariumSizeIndex());
		assertEquals(1, restored.getCurrentWorldStateIndex());
		assertEquals(2, restored.getNextWorldStateIndex());
		assertEquals(world.getWorldStates().size(), restored.getWorldStates().size());
		assertEquals(world.getPlayer().getClass(), restored.getPlayer().getClass());
	}
}
