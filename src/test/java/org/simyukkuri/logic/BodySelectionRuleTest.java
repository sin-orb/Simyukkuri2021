package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.util.WorldTestHelper;

class BodySelectionRuleTest {

	private Yukkuri body;

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
		body = WorldTestHelper.createBody();
		body.setAgeState(AgeState.ADULT);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueId(), body);
	}

	@AfterEach
	void tearDown() {
		SimYukkuri.RND = new java.util.Random();
		WorldTestHelper.resetWorld();
	}

	private Yukkuri addCandidate(AgeState age) {
		Yukkuri c = WorldTestHelper.createBody();
		c.setAgeState(age);
		c.setDead(false);
		c.setPublicRank(PublicRank.NONE);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(c.getUniqueId(), c);
		return c;
	}

	// --- createActiveFiances ---

	@Nested
	class CreateActiveFiances {

		@Test
		void singleBodyWorldReturnsNull() {
			// registry.size() == 1 → null
			assertNull(YukkuriSelectionRule.createActiveFiances(body, 0),
					"自分だけの世界では候補なし");
		}

		@Test
		void existingPartnerReturnsOnlyPartner() {
			// 既婚の場合は相手のみ
			Yukkuri partner = addCandidate(AgeState.ADULT);
			body.setPartner(partner.getUniqueId());
			partner.setPartner(body.getUniqueId());

			List<Yukkuri> result = YukkuriSelectionRule.createActiveFiances(body, AgeState.ADULT.ordinal());

			assertNotNull(result);
			assertTrue(result.contains(partner), "既婚の場合は相手のみリストに入ること");
			assertTrue(result.size() == 1, "既婚リストはちょうど1件");
		}

		@Test
		void deadCandidateExcluded() {
			Yukkuri dead = addCandidate(AgeState.ADULT);
			dead.setDead(true);

			List<Yukkuri> result = YukkuriSelectionRule.createActiveFiances(body, AgeState.ADULT.ordinal());

			assertNotNull(result);
			assertFalse(result.contains(dead), "死亡ゆっくりは候補に入らないこと");
		}

		@Test
		void rankMismatchExcludesCandidate() {
			Yukkuri unun = addCandidate(AgeState.ADULT);
			unun.setPublicRank(PublicRank.UNUN_SLAVE);

			List<Yukkuri> result = YukkuriSelectionRule.createActiveFiances(body, AgeState.ADULT.ordinal());

			assertNotNull(result);
			assertFalse(result.contains(unun), "ランクが違う候補は除外されること");
		}

		@Test
		void disorderedCandidateExcluded() {
			Yukkuri c = addCandidate(AgeState.ADULT);
			c.setOkazaris(null); // hasOkazari=false → hasDisorder()=true

			List<Yukkuri> result = YukkuriSelectionRule.createActiveFiances(body, AgeState.ADULT.ordinal());

			assertNotNull(result);
			assertFalse(result.contains(c), "障害ゆっくりは候補に入らないこと");
		}

		@Test
		void babyCandiateExcludedFromAdultSearch() {
			// ロリコン防止: age > candidate.ageState.ordinal → skip
			Yukkuri baby = addCandidate(AgeState.BABY);
			// body is ADULT (ordinal=2), baby is BABY (ordinal=0)
			// age param = ADULT.ordinal() = 2 > 0 → skip

			List<Yukkuri> result = YukkuriSelectionRule.createActiveFiances(body, AgeState.ADULT.ordinal());

			assertNotNull(result);
			assertFalse(result.contains(baby), "幼い相手は候補に入らないこと（ロリコン防止）");
		}

		@Test
		void sickCandidateExcludedByAverageActor() {
			// AVERAGE intel でも barely sick 候補は findSick で検知され除外される
			Yukkuri sick = addCandidate(AgeState.ADULT);
			sick.setSickPeriod(sick.getIncubationPeriodBase() + 1); // barely sick

			List<Yukkuri> result = YukkuriSelectionRule.createActiveFiances(body, AgeState.ADULT.ordinal());

			assertNotNull(result);
			assertFalse(result.contains(sick), "barely sick 候補は AVERAGE actor に検知され除外されること");
		}

		@Test
		void candidateWithPartnerIncludedWhenNextBooleanFalse() {
			// 既婚候補: nextBoolean()=false なら候補に入る（50%）
			Yukkuri third = addCandidate(AgeState.ADULT);
			Yukkuri candidate = addCandidate(AgeState.ADULT);
			candidate.setPartner(third.getUniqueId());
			third.setPartner(candidate.getUniqueId());
			SimYukkuri.RND = new ConstState(0); // nextBoolean()=false → skip=false → 候補に入る

			List<Yukkuri> result = YukkuriSelectionRule.createActiveFiances(body, AgeState.ADULT.ordinal());

			assertNotNull(result);
			assertTrue(result.contains(candidate), "nextBoolean=false なら既婚候補も入ること");
		}
	}

	// --- createActiveChildren ---

	@Nested
	class CreateActiveChildren {

		private Yukkuri parent;

		@BeforeEach
		void setUpParent() {
			parent = WorldTestHelper.createBody();
			parent.setAgeState(AgeState.ADULT);
			SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(parent.getUniqueId(), parent);
		}

		private Yukkuri addChild(AgeState age) {
			Yukkuri child = WorldTestHelper.createBody();
			child.setAgeState(age);
			// getChildren(i) は YukkuriLookup.getYukkuriById を使うので registry 登録必須
			SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(child.getUniqueId(), child);
			WorldTestHelper.addChild(parent, child.getUniqueId());
			return child;
		}

		@Test
		void noChildrenReturnsNull() {
			assertNull(YukkuriSelectionRule.createActiveChildren(parent, true),
					"子なしの場合は null を返すこと");
		}

		@Test
		void takenChildExcluded() {
			Yukkuri child = addChild(AgeState.BABY);
			child.setTaken(true);

			List<Yukkuri> result = YukkuriSelectionRule.createActiveChildren(parent, true);

			assertTrue(result == null || !result.contains(child),
					"プレイヤーに持たれた子は候補に入らないこと");
		}

		@Test
		void ununSlaveChildExcluded() {
			Yukkuri child = addChild(AgeState.BABY);
			child.setPublicRank(PublicRank.UNUN_SLAVE);

			List<Yukkuri> result = YukkuriSelectionRule.createActiveChildren(parent, true);

			assertTrue(result == null || !result.contains(child),
					"UNUN_SLAVE の子は候補に入らないこと");
		}

		@Test
		void nydChildExcluded() {
			Yukkuri child = addChild(AgeState.BABY);
			child.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);

			List<Yukkuri> result = YukkuriSelectionRule.createActiveChildren(parent, true);

			assertTrue(result == null || !result.contains(child),
					"NYD の子は候補に入らないこと");
		}

		@Test
		void birthEventBlockedChildExcluded() {
			Yukkuri child = addChild(AgeState.BABY);
			child.setBirthEventBlockedTicks(1); // 産まれたてのイベントブロック中

			List<Yukkuri> result = YukkuriSelectionRule.createActiveChildren(parent, true);

			assertTrue(result == null || !result.contains(child),
					"birthEventBlockedTicks > 0 の子は候補に入らないこと");
		}

		@Test
		void firstGroundChildExcluded() {
			Yukkuri child = addChild(AgeState.BABY);
			child.setFirstGround(true); // 初接地（まだ落下中）

			List<Yukkuri> result = YukkuriSelectionRule.createActiveChildren(parent, true);

			assertTrue(result == null || !result.contains(child),
					"firstGround=true の赤ゆは候補に入らないこと");
		}

		@Test
		void includeChildrenFalseExcludesChildYukkuri() {
			// includeChildren=false → 赤ゆのみ、子ゆはスキップ
			Yukkuri child = addChild(AgeState.CHILD);

			List<Yukkuri> result = YukkuriSelectionRule.createActiveChildren(parent, false);

			assertTrue(result == null || !result.contains(child),
					"includeChildren=false のとき子ゆ(CHILD)は候補に入らないこと");
		}

		@Test
		void includeChildrenTrueExcludesAdult() {
			// includeChildren=true → 成ゆはスキップ
			Yukkuri adult = addChild(AgeState.ADULT);

			List<Yukkuri> result = YukkuriSelectionRule.createActiveChildren(parent, true);

			assertTrue(result == null || !result.contains(adult),
					"includeChildren=true のとき成ゆは候補に入らないこと");
		}
	}
}
