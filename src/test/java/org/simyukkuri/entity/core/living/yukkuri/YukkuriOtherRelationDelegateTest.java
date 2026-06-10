package org.simyukkuri.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.util.WorldTestHelper;

class YukkuriOtherRelationDelegateTest {

	private Yukkuri actor;
	private Yukkuri target;

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
		actor = WorldTestHelper.createBody();
		target = WorldTestHelper.createBody();
		actor.setHungry(actor.getHungryLimit()); // isVeryHungry=false
		target.setHungry(target.getHungryLimit());
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(actor.getUniqueId(), actor);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(target.getUniqueId(), target);
	}

	@AfterEach
	void tearDown() {
		SimYukkuri.RND = new java.util.Random();
		WorldTestHelper.resetWorld();
	}

	@Test
	void doSurisuriReturnsWithoutThrowingWhenActorIsDead() {
		StubBody body = new StubBody();
		StubBody tgt = new StubBody();
		body.setDead(true);

		assertDoesNotThrow(() -> new YukkuriOtherRelationDelegate(body).doSurisuri(tgt));
	}

	@Test
	void doPeroperoReturnsWithoutThrowingWhenActorIsDead() {
		StubBody body = new StubBody();
		StubBody tgt = new StubBody();
		body.setDead(true);

		assertDoesNotThrow(() -> new YukkuriOtherRelationDelegate(body).doPeropero(tgt));
	}

	// --- doSurisuri: アリ伝染テスト ---

	@Test
	void doSurisuriInfectsActorWithAntsWhenTargetHasAnts() {
		// target にアリあり + ConstState(0) で nextInt(200)==0 → actor にアリが付く
		target.addAttachment(new Ants(target));
		assertEquals(0, actor.getAttachmentSize(Ants.class));
		SimYukkuri.RND = new ConstState(0);

		new YukkuriOtherRelationDelegate(actor).doSurisuri(target);

		assertTrue(actor.getAttachmentSize(Ants.class) > 0,
				"アリのいるゆっくりにすりすりするとアリが伝染ること");
	}

	@Test
	void doSurisuriDoesNotInfectActorWhenTargetHasNoAnts() {
		// target にアリなし → 伝染しない
		assertEquals(0, target.getAttachmentSize(Ants.class));
		assertEquals(0, actor.getAttachmentSize(Ants.class));
		SimYukkuri.RND = new ConstState(0);

		new YukkuriOtherRelationDelegate(actor).doSurisuri(target);

		assertEquals(0, actor.getAttachmentSize(Ants.class),
				"アリのいないゆっくりにすりすりしても伝染らないこと");
	}

	// --- doPeropero: アリ撃退・伝染テスト ---

	@Test
	void doPeroperoReducesAntCountByForty() {
		// target.antCount=50 → doPeropero → antCount=10 (50-40=10)
		target.addAttachment(new Ants(target));
		target.setAntCount(50);
		SimYukkuri.RND = new ConstState(1); // nextInt(200)=1≠0 → 伝染なし

		new YukkuriOtherRelationDelegate(actor).doPeropero(target);

		assertEquals(10, target.getAntCount(), "ぺろぺろでアリが 40 減ること（50-40=10）");
	}

	@Test
	void doPeroperoRemovesAntsWhenCountDropsToZero() {
		// target.antCount=30 → doPeropero → 30-40≤0 → removeAnts
		target.addAttachment(new Ants(target));
		target.setAntCount(30);
		SimYukkuri.RND = new ConstState(1);

		new YukkuriOtherRelationDelegate(actor).doPeropero(target);

		assertEquals(0, target.getAttachmentSize(Ants.class),
				"アリが閾値以下まで減ると除去されること（30-40≤0）");
	}

	@Test
	void doPeroperoInfectsActorWhenAntCountRemainsAboveZero() {
		// target.antCount=50 → ant=10 > 0 かつ nextInt(200)==0 → actor にアリが伝染
		target.addAttachment(new Ants(target));
		target.setAntCount(50);
		SimYukkuri.RND = new ConstState(0); // nextInt(200)=0 → 伝染発生

		new YukkuriOtherRelationDelegate(actor).doPeropero(target);

		assertTrue(actor.getAttachmentSize(Ants.class) > 0,
				"ぺろぺろ後アリが残る場合 actor にアリが伝染すること");
	}
}
