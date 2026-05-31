package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.util.WorldTestHelper;

/**
 * Tests for YukkuriUnunSlaveEmotionRule.
 */
public class BodyUnunSlaveEmotionRuleTest {

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void testCheckEmotionFromUnunSlave_returnsFalseForNull() {
		assertFalse(YukkuriUnunSlaveEmotionRule.checkEmotionFromUnunSlave(null, null));
	}

	@Test
	void testCheckEmotionFromUnunSlave_handlesUnunSlaveEnvyReaction() {
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		// isPartner() は yukkuriRegistry 経由で解決するため登録が必要
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueId(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(you.getUniqueId(), you);
		me.setPublicRank(PublicRank.UNUN_SLAVE);
		me.setIntelligence(Intelligence.AVERAGE);  // isIdiot()=false を保証
		me.setHappiness(Happiness.AVERAGE);
		you.setHappiness(Happiness.VERY_HAPPY);
		// PARTNER 関係を設定 → AVERAGE + VERY_HAPPY + PARTNER で emotionFlags[5]=true
		me.setPartner(you.getUniqueId());
		you.setPartner(me.getUniqueId());
		SimYukkuri.RND = new ConstState(0);  // nextInt(50)=0 で確率チェック通過

		int initialStress = me.getStress();
		boolean result = YukkuriUnunSlaveEmotionRule.checkEmotionFromUnunSlave(me, you);

		assertTrue(result, "UNUN_SLAVE が嫉妬感情を持つとき true を返すこと");
		assertEquals(Happiness.VERY_SAD, me.getHappiness(), "嫉妬反応で me が VERY_SAD になること");
		assertTrue(me.getStress() > initialStress, "嫉妬反応で me のストレスが増加すること");
	}
}
