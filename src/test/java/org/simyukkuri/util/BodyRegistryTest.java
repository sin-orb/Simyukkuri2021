package org.simyukkuri.util;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;

public class BodyRegistryTest {
	@AfterEach
	void tearDown() {
		WorldTestHelper.resetWorld();
	}

	@Test
	public void testGetBodyInstance() {
		WorldTestHelper.initializeMinimalWorld();
		Yukkuri body = new Reimu();
		SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

		assertNotNull(BodyRegistry.getBodyInstance(body.getUniqueID()));
		assertNull(BodyRegistry.getBodyInstance(-1));
	}

	@Test
	public void testGetBodyInstanceFromObjId() {
		WorldTestHelper.initializeMinimalWorld();
		Yukkuri body = new Reimu();
		SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

		assertNotNull(BodyRegistry.getBodyInstanceFromObjId(body.getObjId()));
		assertNull(BodyRegistry.getBodyInstanceFromObjId(-1));
	}

	@Test
	public void testGetBodyInstances() {
		WorldTestHelper.initializeMinimalWorld();
		Yukkuri body = new Reimu();
		SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

		assertArrayEquals(new Yukkuri[] { body }, BodyRegistry.getBodyInstances());
	}
}
