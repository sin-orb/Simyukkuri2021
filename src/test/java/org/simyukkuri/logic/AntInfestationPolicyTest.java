package org.simyukkuri.logic;

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

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.ConstState;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.util.WorldTestHelper;

public class AntInfestationPolicyTest {
	@AfterEach
	void tearDown() {
		WorldTestHelper.resetWorld();
	}

	@Test
	public void testJudgeNewAnt_hitAddsAttachment() {
		WorldTestHelper.initializeMinimalWorld();
		Ants.setImages(new BufferedImage[3][3]);
		Ants.setImgW(new int[] { 10, 20, 30 });
		Ants.setImgH(new int[] { 11, 21, 31 });
		Ants.setPivX(new int[] { 1, 2, 3 });
		Ants.setPivY(new int[] { 4, 5, 6 });

		Reimu body = new Reimu();
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueID(), body);
		SimYukkuri.RND = new ConstState(1);

		AntInfestationPolicy.judgeNewAnt(body);

		assertEquals(1, body.getAttachmentSize(Ants.class));
		assertEquals(50, body.getAntCount());
	}

	@Test
	public void testJudgeNewAnt_dirtyAndDontJumpHalveProbabilityTwice() {
		class BoundRecordingRandom extends java.util.Random {
			int lastBound;

			@Override
			public int nextInt(int bound) {
				lastBound = bound;
				return 0;
			}
		}

		BoundRecordingRandom rng = new BoundRecordingRandom();
		SimYukkuri.RND = rng;

		Reimu body = new Reimu();
		body.setAge(100000);
		body.setDirty(true);
		body.setHasBaby(true);
		body.getBabyTypes().add(new org.simyukkuri.entity.core.living.yukkuri.Dna());

		AntInfestationPolicy.judgeNewAnt(body);

		assertEquals(240000, rng.lastBound);
		assertEquals(0, body.getAttachmentSize(Ants.class));
	}
}
