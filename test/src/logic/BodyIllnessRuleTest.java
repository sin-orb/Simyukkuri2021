package src.logic;

import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.entity.core.living.yukkuri.Yukkuri;
import src.enums.Intelligence;
import src.util.WorldTestHelper;

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

		assertTrue(BodyIllnessRule.findSick(self, target));
	}

	@Test
	void foolBodyRequiresSevereSickness() {
		self.setIntelligence(Intelligence.FOOL);
		target.forceSetSick();

		assertTrue(BodyIllnessRule.findSick(self, target));
	}

	@Test
	void healthyTargetIsIgnored() {
		self.setIntelligence(Intelligence.AVERAGE);

		assertFalse(BodyIllnessRule.findSick(self, target));
	}
}
