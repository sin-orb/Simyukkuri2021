package org.simyukkuri.engine.transform;

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
			SimYukkuri.world.getCurrentMap().getYukkuriMap().put(reimu.getUniqueID(), reimu);

			int originalId = reimu.getUniqueID();

			TransformationService.transform(reimu, YukkuriType.DEIBU);

			Yukkuri transformed = SimYukkuri.world.getCurrentMap().getYukkuriMap().get(originalId);
			assertNotNull(transformed);
			assertInstanceOf(Deibu.class, transformed);
			assertEquals(originalId, transformed.getUniqueID());
			assertTrue(reimu.isRemoved());
		} finally {
			WorldTestHelper.resetWorld();
		}
	}
}
