package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.entity.core.living.yukkuri.Yukkuri;
import src.enums.EnumRelationMine;
import src.util.WorldTestHelper;

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

		assertTrue(BodyRelations.isParent(parent, child));
		assertTrue(BodyRelations.isFather(parent, child));
		assertFalse(BodyRelations.isMother(parent, child));
		assertTrue(BodyRelations.isChild(child, parent));
		assertTrue(BodyRelations.isFamily(parent, child));
		assertTrue(BodyRelations.isFamily(child, parent));
	}

	@Test
	void detectsPartnerRelations() {
		parent.setPartner(partner.getUniqueID());

		assertTrue(BodyRelations.isPartner(parent, partner));
		assertTrue(BodyRelations.isFamily(parent, partner));
		assertFalse(BodyRelations.isPartner(partner, parent));
	}

	@Test
	void detectsSiblingRelationsByKnownParent() {
		WorldTestHelper.setParents(child, -1, parent.getUniqueID());
		WorldTestHelper.setParents(sibling, -1, parent.getUniqueID());
		child.setAge(10);
		sibling.setAge(5);

		assertTrue(BodyRelations.isSister(child, sibling));
		assertTrue(BodyRelations.isElderSister(child, sibling));
		assertTrue(BodyRelations.isFamily(child, sibling));
	}

	@Test
	void resolvesFamilyMembersByIndex() {
		WorldTestHelper.setParents(child, -1, parent.getUniqueID());
		WorldTestHelper.setParents(sibling, -1, parent.getUniqueID());
		child.getChildrenList().add(unrelated.getUniqueID());
		child.getSisterList().add(sibling.getUniqueID());
		child.getElderSisterList().add(partner.getUniqueID());

		assertSame(sibling, BodyRelations.getSister(child, 0));
		assertSame(partner, BodyRelations.getElderSister(child, 0));
		assertSame(unrelated, BodyRelations.getChildren(child, 0));
	}

	@Test
	void removesFamilyMembersByIndexTarget() {
		WorldTestHelper.setParents(child, -1, parent.getUniqueID());
		child.getChildrenList().add(unrelated.getUniqueID());
		child.getElderSisterList().add(partner.getUniqueID());
		child.getSisterList().add(sibling.getUniqueID());

		BodyRelations.removeChildrenList(child, unrelated);
		BodyRelations.removeElderSisterList(child, partner);
		BodyRelations.removeSisterList(child, sibling);

		assertTrue(child.getChildrenList().isEmpty());
		assertTrue(child.getElderSisterList().isEmpty());
		assertTrue(child.getSisterList().isEmpty());
	}

	@Test
	void detectsRelationMineClassification() {
		WorldTestHelper.setParents(child, parent.getUniqueID(), -1);
		parent.setPartner(partner.getUniqueID());
		child.setAge(10);

		assertEquals(EnumRelationMine.FATHER, BodyRelations.checkMyRelation(parent, child));
		assertEquals(EnumRelationMine.CHILD_FATHER, BodyRelations.checkMyRelation(child, parent));
		assertEquals(EnumRelationMine.PARTNAR, BodyRelations.checkMyRelation(parent, partner));
		assertEquals(EnumRelationMine.OTHER, BodyRelations.checkMyRelation(sibling, child));
	}

	@Test
	void unrelatedBodiesAreNotFamily() {
		WorldTestHelper.setParents(child, -1, -1);
		WorldTestHelper.setParents(unrelated, -1, -1);
		child.setPartner(-1);
		unrelated.setPartner(-1);

		assertFalse(BodyRelations.isParent(child, unrelated));
		assertFalse(BodyRelations.isChild(child, unrelated));
		assertFalse(BodyRelations.isPartner(child, unrelated));
		assertFalse(BodyRelations.isSister(child, unrelated));
		assertFalse(BodyRelations.isFamily(child, unrelated));
	}

	@Test
	void nullHandlingMatchesExistingBodyRelationMethods() {
		assertFalse(BodyRelations.isParent(parent, null));
		assertFalse(BodyRelations.isFather(parent, null));
		assertFalse(BodyRelations.isMother(parent, null));
		assertFalse(BodyRelations.isChild(parent, null));
		assertFalse(BodyRelations.isPartner(parent, null));

		WorldTestHelper.setParents(parent, -1, child.getUniqueID());
		assertThrows(NullPointerException.class, () -> BodyRelations.isSister(parent, null));
		assertThrows(NullPointerException.class, () -> BodyRelations.isFamily(parent, null));
	}

	private Yukkuri createRegisteredBody() {
		Yukkuri body = WorldTestHelper.createBody();
		SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);
		return body;
	}
}
