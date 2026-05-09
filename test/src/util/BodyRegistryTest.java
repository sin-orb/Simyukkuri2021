package src.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.yukkuri.Reimu;

public class BodyRegistryTest {
	@AfterEach
	void tearDown() {
		WorldTestHelper.resetWorld();
	}

	@Test
	public void testGetBodyInstance() {
		WorldTestHelper.initializeMinimalWorld();
		Body body = new Reimu();
		SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

		assertNotNull(BodyRegistry.getBodyInstance(body.getUniqueID()));
		assertNull(BodyRegistry.getBodyInstance(-1));
	}

	@Test
	public void testGetBodyInstanceFromObjId() {
		WorldTestHelper.initializeMinimalWorld();
		Body body = new Reimu();
		SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

		assertNotNull(BodyRegistry.getBodyInstanceFromObjId(body.getObjId()));
		assertNull(BodyRegistry.getBodyInstanceFromObjId(-1));
	}

	@Test
	public void testGetBodyInstances() {
		WorldTestHelper.initializeMinimalWorld();
		Body body = new Reimu();
		SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

		assertArrayEquals(new Body[] { body }, BodyRegistry.getBodyInstances());
	}
}
