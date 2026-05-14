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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.EnumRelationMine;
import org.simyukkuri.util.WorldTestHelper;

class BodyRelationsTest {

	private Yukkuri parent;
	private Yukkuri child;
	private Yukkuri partner;
	private Yukkuri sibling;
	private Yukkuri unrelated;

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();

		parent = createRegisteredBody();
		child = createRegisteredBody();
		partner = createRegisteredBody();
		sibling = createRegisteredBody();
		unrelated = createRegisteredBody();
	}

	@AfterEach
	void tearDown() {
		WorldTestHelper.resetWorld();
	}

	@Test
	void detectsParentChildRelations() {
		WorldTestHelper.setParents(child, parent.getUniqueID(), -1);

		assertTrue(YukkuriRelations.isParent(parent, child));
		assertTrue(YukkuriRelations.isFather(parent, child));
		assertFalse(YukkuriRelations.isMother(parent, child));
		assertTrue(YukkuriRelations.isChild(child, parent));
		assertTrue(YukkuriRelations.isFamily(parent, child));
		assertTrue(YukkuriRelations.isFamily(child, parent));
	}

	@Test
	void detectsPartnerRelations() {
		parent.setPartner(partner.getUniqueID());

		assertTrue(YukkuriRelations.isPartner(parent, partner));
		assertTrue(YukkuriRelations.isFamily(parent, partner));
		assertFalse(YukkuriRelations.isPartner(partner, parent));
	}

	@Test
	void detectsSiblingRelationsByKnownParent() {
		WorldTestHelper.setParents(child, -1, parent.getUniqueID());
		WorldTestHelper.setParents(sibling, -1, parent.getUniqueID());
		child.setAge(10);
		sibling.setAge(5);

		assertTrue(YukkuriRelations.isSister(child, sibling));
		assertTrue(YukkuriRelations.isElderSister(child, sibling));
		assertTrue(YukkuriRelations.isFamily(child, sibling));
	}

	@Test
	void resolvesFamilyMembersByIndex() {
		WorldTestHelper.setParents(child, -1, parent.getUniqueID());
		WorldTestHelper.setParents(sibling, -1, parent.getUniqueID());
		child.getChildrenList().add(unrelated.getUniqueID());
		child.getSisterList().add(sibling.getUniqueID());
		child.getElderSisterList().add(partner.getUniqueID());

		assertSame(sibling, YukkuriRelations.getSister(child, 0));
		assertSame(partner, YukkuriRelations.getElderSister(child, 0));
		assertSame(unrelated, YukkuriRelations.getChildren(child, 0));
	}

	@Test
	void removesFamilyMembersByIndexTarget() {
		WorldTestHelper.setParents(child, -1, parent.getUniqueID());
		child.getChildrenList().add(unrelated.getUniqueID());
		child.getElderSisterList().add(partner.getUniqueID());
		child.getSisterList().add(sibling.getUniqueID());

		YukkuriRelations.removeChildrenList(child, unrelated);
		YukkuriRelations.removeElderSisterList(child, partner);
		YukkuriRelations.removeSisterList(child, sibling);

		assertTrue(child.getChildrenList().isEmpty());
		assertTrue(child.getElderSisterList().isEmpty());
		assertTrue(child.getSisterList().isEmpty());
	}

	@Test
	void detectsRelationMineClassification() {
		WorldTestHelper.setParents(child, parent.getUniqueID(), -1);
		parent.setPartner(partner.getUniqueID());
		child.setAge(10);

		assertEquals(EnumRelationMine.FATHER, YukkuriRelations.checkMyRelation(parent, child));
		assertEquals(EnumRelationMine.CHILD_FATHER, YukkuriRelations.checkMyRelation(child, parent));
		assertEquals(EnumRelationMine.PARTNAR, YukkuriRelations.checkMyRelation(parent, partner));
		assertEquals(EnumRelationMine.OTHER, YukkuriRelations.checkMyRelation(sibling, child));
	}

	@Test
	void unrelatedBodiesAreNotFamily() {
		WorldTestHelper.setParents(child, -1, -1);
		WorldTestHelper.setParents(unrelated, -1, -1);
		child.setPartner(-1);
		unrelated.setPartner(-1);

		assertFalse(YukkuriRelations.isParent(child, unrelated));
		assertFalse(YukkuriRelations.isChild(child, unrelated));
		assertFalse(YukkuriRelations.isPartner(child, unrelated));
		assertFalse(YukkuriRelations.isSister(child, unrelated));
		assertFalse(YukkuriRelations.isFamily(child, unrelated));
	}

	@Test
	void nullHandlingMatchesExistingBodyRelationMethods() {
		assertFalse(YukkuriRelations.isParent(parent, null));
		assertFalse(YukkuriRelations.isFather(parent, null));
		assertFalse(YukkuriRelations.isMother(parent, null));
		assertFalse(YukkuriRelations.isChild(parent, null));
		assertFalse(YukkuriRelations.isPartner(parent, null));

		WorldTestHelper.setParents(parent, -1, child.getUniqueID());
		assertThrows(NullPointerException.class, () -> YukkuriRelations.isSister(parent, null));
		assertThrows(NullPointerException.class, () -> YukkuriRelations.isFamily(parent, null));
	}

	private Yukkuri createRegisteredBody() {
		Yukkuri body = WorldTestHelper.createBody();
		SimYukkuri.world.getCurrentMap().getYukkuriMap().put(body.getUniqueID(), body);
		return body;
	}
}
