package src.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.entity.core.world.bodylinked.Stalk;

class YukkuriStalkDelegateTest {
	@Test
	void setUnBirthDisablesTalkAndEnablesBirthFlags() {
		StubBody body = new StubBody();

		new YukkuriStalkDelegate(body).setUnBirth(true);

		assertTrue(body.isUnBirth());
		assertFalse(body.isCanTalk());
	}

	@Test
	void setUnBirthForLoadKeepsBirthState() {
		StubBody body = new StubBody();

		new YukkuriStalkDelegate(body).setUnBirthForLoad(true);

		assertTrue(body.isUnBirth());
		assertFalse(body.isCanTalk());
	}

	@Test
	void detachFromStalkClearsBindingAndParentLink() {
		StubBody body = new StubBody();
		Stalk stalk = new Stalk();
		body.setBindStalk(stalk);
		body.setParentLinkId(42);
		stalk.getBindBabies().add(body.getUniqueID());

		new YukkuriStalkDelegate(body).detachFromStalk();

		assertNull(body.getBindStalk());
		assertFalse(stalk.getBindBabies().contains(body.getUniqueID()));
		assertEquals(-1, body.getParentLinkId());
	}

	@Test
	void getStalksDequeue_returnsFirstStalkAndRemovesIt() {
		StubBody body = new StubBody();
		Stalk first = new Stalk();
		Stalk second = new Stalk();
		body.getStalks().add(first);
		body.getStalks().add(second);

		assertEquals(first, new YukkuriStalkDelegate(body).getStalksDequeue());
		assertEquals(second, body.getStalks().get(0));
	}
}
