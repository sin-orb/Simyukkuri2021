package org.simyukkuri.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;

public class YukkuriLookupTest {
	@AfterEach
	void tearDown() {
		WorldTestHelper.resetWorld();
	}

	@Test
	public void testGetBodyInstance() {
		WorldTestHelper.initializeMinimalWorld();
		Yukkuri body = new Reimu();
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueID(), body);

		assertNotNull(YukkuriLookup.getYukkuriById(body.getUniqueID()));
		assertNull(YukkuriLookup.getYukkuriById(-1));
	}

	@Test
	public void testGetBodyInstanceFromObjId() {
		WorldTestHelper.initializeMinimalWorld();
		Yukkuri body = new Reimu();
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueID(), body);

		assertNotNull(YukkuriLookup.findYukkuriByObjId(body.getObjId()));
		assertNull(YukkuriLookup.findYukkuriByObjId(-1));
	}

	@Test
	public void testGetBodyInstances() {
		WorldTestHelper.initializeMinimalWorld();
		Yukkuri body = new Reimu();
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueID(), body);

		assertArrayEquals(new Yukkuri[] { body }, YukkuriLookup.getYukkuriBodies());
	}
}
