package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.attachment.impl.Ants;
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
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueId(), body);
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
		body.setAge(100000);  // age=100000 >= childLimit=50400 → ADULT, base=960000
		body.setDirty(true);
		body.setHasBaby(true);
		body.getBabyTypes().add(new org.simyukkuri.entity.core.living.yukkuri.Dna());

		// 前提条件の確認: ADULTかつdirty・dontJump両方が有効なこと
		assertEquals(org.simyukkuri.enums.AgeState.ADULT, body.getAgeState(),
				"age=100000 は ADULT");
		assertTrue(body.isDirty(), "dirty フラグが true");
		assertTrue(body.isDontJump(), "hasBaby=true により isDontJump()=true");

		AntInfestationPolicy.judgeNewAnt(body);

		// ADULT(960000) /2(dirty) /2(dontJump) = 240000
		assertEquals(240000, rng.lastBound,
				"ADULT 基準値 960000 を dirty と dontJump で 2 回半減した値");
		assertEquals(0, body.getAttachmentSize(Ants.class),
				"rng.nextInt が 0 を返すため hit せずアリは付かない");
	}
}
