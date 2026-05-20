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
		target.forceSetSick();

		assertTrue(YukkuriIllnessRule.findSick(self, target));
	}

	@Test
	void foolBodyRequiresSevereSickness() {
		self.setIntelligence(Intelligence.FOOL);
		target.forceSetSick();

		assertTrue(YukkuriIllnessRule.findSick(self, target));
	}

	@Test
	void healthyTargetIsIgnored() {
		self.setIntelligence(Intelligence.AVERAGE);

		assertFalse(YukkuriIllnessRule.findSick(self, target));
	}
}
