package src.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.ConstState;
import src.attachment.Ants;
import src.yukkuri.Reimu;
import src.util.WorldTestHelper;

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
		SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);
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
		body.getBabyTypes().add(new src.game.Dna());

		AntInfestationPolicy.judgeNewAnt(body);

		assertEquals(240000, rng.lastBound);
		assertEquals(0, body.getAttachmentSize(Ants.class));
	}
}
