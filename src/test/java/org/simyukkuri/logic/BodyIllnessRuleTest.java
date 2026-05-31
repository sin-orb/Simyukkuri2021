package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.util.WorldTestHelper;

class BodyIllnessRuleTest {

	private Yukkuri self;
	private Yukkuri target;

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
		self = WorldTestHelper.createBody();
		target = WorldTestHelper.createBody();
	}

	@AfterEach
	void tearDown() {
		WorldTestHelper.resetWorld();
	}

	@Test
	void wiseBodyRecognizesNormalSickness() {
		self.setIntelligence(Intelligence.WISE);
		// 軽症（isSick=true, isSickHeavily=false）でも WISE は見抜けること
		target.setSickPeriod(target.getIncubationPeriodBase() + 1);
		assertTrue(target.isSick(),       "軽症状態の確認");
		assertFalse(target.isSickHeavily(), "重症ではないことの確認");

		assertTrue(YukkuriIllnessRule.findSick(self, target),
				"WISE は軽症でも病気を見抜くこと");
	}

	@Test
	void foolBodyRequiresSevereSickness() {
		self.setIntelligence(Intelligence.FOOL);

		// 軽症では FOOL は見抜けない
		target.setSickPeriod(target.getIncubationPeriodBase() + 1);
		assertFalse(YukkuriIllnessRule.findSick(self, target),
				"FOOL は軽症（isSickHeavily=false）では病気を見抜けないこと");

		// 重症（forceSetSick）なら FOOL も見抜ける
		target.forceSetSick();
		assertTrue(target.isSickHeavily(), "forceSetSick で重症になることの確認");
		assertTrue(YukkuriIllnessRule.findSick(self, target),
				"FOOL も重症（isSickHeavily=true）なら病気を見抜くこと");
	}

	@Test
	void healthyTargetIsIgnored() {
		// 健康な target はいずれの Intelligence でも false
		self.setIntelligence(Intelligence.AVERAGE);
		assertFalse(YukkuriIllnessRule.findSick(self, target),
				"健康な target は AVERAGE に無視されること");

		self.setIntelligence(Intelligence.WISE);
		assertFalse(YukkuriIllnessRule.findSick(self, target),
				"健康な target は WISE にも無視されること");
	}
}
