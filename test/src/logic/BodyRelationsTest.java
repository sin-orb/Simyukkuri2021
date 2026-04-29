package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.util.WorldTestHelper;

class BodyRelationsTest {

	private Body parent;
	private Body child;
	private Body partner;
	private Body sibling;
	private Body unrelated;

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

	private Body createRegisteredBody() {
		Body body = WorldTestHelper.createBody();
		SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);
		return body;
	}
}
