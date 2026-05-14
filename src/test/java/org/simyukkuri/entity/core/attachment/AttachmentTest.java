package org.simyukkuri.entity.core.attachment;

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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.draw.World;
import org.simyukkuri.entity.core.attachment.Attachment;
import org.simyukkuri.entity.core.living.yukkuri.StubBody;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.AttachProperty;
import org.simyukkuri.enums.Event;
import org.simyukkuri.enums.Type;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;

/**
 * Attachmentベースクラスのテスト.
 * abstractクラスなのでテスト用の具象スタブを使用する.
 */
public class AttachmentTest {

	/** テスト用の具象Attachmentスタブ */
	private static class StubAttachment extends Attachment {
		private static final long serialVersionUID = 1L;
		private Event updateResult = Event.DONOTHING;
		private int updateCallCount = 0;

		public StubAttachment(Yukkuri body) {
			super(body);
		}

		public StubAttachment() {
			super();
		}

		@Override
		protected Event update() {
			updateCallCount++;
			return updateResult;
		}

		@Override
		public void resetBoundary() {
		}

		@Override
		public BufferedImage getImage(Yukkuri b) {
			return null;
		}

		public void setUpdateResult(Event result) {
			this.updateResult = result;
		}

		public int getUpdateCallCount() {
			return updateCallCount;
		}

		/** setAttachPropertyをテストから呼べるよう公開 */
		public void callSetAttachProperty(int[] p, String ofsKey) {
			setAttachProperty(p, ofsKey);
		}
	}

	@BeforeEach
	public void setUp() {
		SimYukkuri.world = new World();
	}

	// --- コンストラクタのテスト ---

	@Test
	public void testConstructorWithBody() {
		Yukkuri parent = createParent(AgeState.ADULT);
		StubAttachment att = new StubAttachment(parent);

		assertEquals(Type.ATTACHMENT, att.getObjType());
		assertEquals(parent.getUniqueID(), att.getParent());
	}

	@Test
	public void testDefaultConstructor() {
		StubAttachment att = new StubAttachment();
		Point4y[] posOfs = att.getPosOfs();

		assertNotNull(posOfs);
		assertEquals(3, posOfs.length);
	}

	// --- getters/setters のテスト ---

	@Test
	public void testParentGetterSetter() {
		Yukkuri parent = createParent(AgeState.BABY);
		StubAttachment att = new StubAttachment(parent);

		assertEquals(parent.getUniqueID(), att.getParent());

		att.setParent(999);
		assertEquals(999, att.getParent());
	}

	@Test
	public void testAnimateGetterSetter() {
		StubAttachment att = new StubAttachment();
		att.setAnimate(true);
		assertTrue(att.isAnimate());

		att.setAnimate(false);
		assertFalse(att.isAnimate());
	}

	@Test
	public void testAnimeFrameGetterSetter() {
		StubAttachment att = new StubAttachment();
		att.setAnimeFrame(5);
		assertEquals(5, att.getAnimeFrame());
	}

	@Test
	public void testAnimeIntervalGetterSetter() {
		StubAttachment att = new StubAttachment();
		att.setAnimeInterval(42);
		assertEquals(42, att.getAnimeInterval());
	}

	@Test
	public void testAnimeLoopGetterSetter() {
		StubAttachment att = new StubAttachment();
		att.setAnimeLoop(3);
		assertEquals(3, att.getAnimeLoop());
	}

	@Test
	public void testAttachPropertyGetterSetter() {
		StubAttachment att = new StubAttachment();
		int[] prop = { 1, 2, 3, 4, 5, 6, 7 };
		att.setAttachProperty(prop);
		assertArrayEquals(prop, att.getAttachProperty());
	}

	@Test
	public void testProcessIntervalGetterSetter() {
		StubAttachment att = new StubAttachment();
		assertEquals(10, att.getProcessInterval()); // デフォルト値
		att.setProcessInterval(100);
		assertEquals(100, att.getProcessInterval());
	}

	@Test
	public void testPosOfsGetterSetter() {
		StubAttachment att = new StubAttachment();
		Point4y[] newOfs = new Point4y[] {
				new Point4y(10, 20),
				new Point4y(30, 40),
				new Point4y(50, 60)
		};
		att.setPosOfs(newOfs);
		assertArrayEquals(newOfs, att.getPosOfs());
	}

	// --- setAttachProperty のテスト ---

	@Test
	public void testSetAttachPropertyWithNullParent() {
		StubAttachment att = new StubAttachment();
		att.setParent(-1); // nullになるID
		int[] prop = { 2, 2, 1, 0, 0, 0, 1 };

		// parentがnullのとき何もせず終了する（例外が出ない）
		att.callSetAttachProperty(prop, "test");
	}

	@Test
	public void testSetAttachPropertySetsAnimateTrue() {
		Yukkuri parent = createParentWithMountPoint(AgeState.CHILD);
		StubAttachment att = new StubAttachment(parent);

		// ANIME_INTERVAL != 0 → animate = true
		int[] prop = new int[AttachProperty.values().length];
		prop[AttachProperty.ANIME_INTERVAL.ordinal()] = 5;
		prop[AttachProperty.ANIME_LOOP.ordinal()] = 3;
		prop[AttachProperty.ANIME_FRAMES.ordinal()] = 4;

		att.callSetAttachProperty(prop, "test");

		assertTrue(att.isAnimate());
		assertEquals(0, att.getAnimeInterval());
		assertEquals(3, att.getAnimeLoop());
		assertArrayEquals(prop, att.getAttachProperty());
	}

	@Test
	public void testSetAttachPropertySetsAnimateFalse() {
		Yukkuri parent = createParentWithMountPoint(AgeState.ADULT);
		StubAttachment att = new StubAttachment(parent);

		// ANIME_INTERVAL == 0 → animate = false
		int[] prop = new int[AttachProperty.values().length];
		prop[AttachProperty.ANIME_INTERVAL.ordinal()] = 0;
		prop[AttachProperty.ANIME_LOOP.ordinal()] = 0;

		att.callSetAttachProperty(prop, "test");

		assertFalse(att.isAnimate());
	}

	// --- clockTick のテスト ---

	@Test
	public void testClockTickIncrementsAge() {
		Yukkuri parent = createParent(AgeState.BABY);
		StubAttachment att = new StubAttachment(parent);
		att.setProcessInterval(10);

		long ageBefore = att.getAge();
		att.clockTick();

		assertEquals(ageBefore + Entity.TICK, att.getAge());
	}

	@Test
	public void testClockTickCallsUpdateAtProcessInterval() {
		Yukkuri parent = createParent(AgeState.BABY);
		StubAttachment att = new StubAttachment(parent);
		att.setProcessInterval(10);
		att.setAnimate(false);

		// processInterval=10でTICK=1なので、10回目で呼ばれるはず
		for (int i = 0; i < 10; i++) {
			att.clockTick();
		}
		assertEquals(1, att.getUpdateCallCount());
	}

	@Test
	public void testClockTickDoesNotCallUpdateBetweenIntervals() {
		Yukkuri parent = createParent(AgeState.BABY);
		StubAttachment att = new StubAttachment(parent);
		att.setProcessInterval(10);
		att.setAnimate(false);

		// 9回ではまだ呼ばれない
		for (int i = 0; i < 9; i++) {
			att.clockTick();
		}
		assertEquals(0, att.getUpdateCallCount());
	}

	@Test
	public void testClockTickReturnsUpdateResult() {
		Yukkuri parent = createParent(AgeState.BABY);
		StubAttachment att = new StubAttachment(parent);
		att.setProcessInterval(1); // 毎ティック呼ばれる
		att.setAnimate(false);
		att.setUpdateResult(Event.REMOVED);

		Event result = att.clockTick();
		assertEquals(Event.REMOVED, result);
	}

	@Test
	public void testClockTickReturnsDoNothingWhenNotAtInterval() {
		Yukkuri parent = createParent(AgeState.BABY);
		StubAttachment att = new StubAttachment(parent);
		att.setProcessInterval(100);
		att.setAnimate(false);

		// 最初のtickでは processInterval に達しない → DONOTHING
		Event result = att.clockTick();
		assertEquals(Event.DONOTHING, result);
	}

	// --- アニメーション処理のテスト ---

	@Test
	public void testClockTickAdvancesAnimationFrame() {
		Yukkuri parent = createParent(AgeState.BABY);
		StubAttachment att = new StubAttachment(parent);
		att.setProcessInterval(9999); // updateを避ける
		att.setAnimate(true);
		att.setAnimeFrame(0);

		int[] prop = new int[AttachProperty.values().length];
		prop[AttachProperty.ANIME_INTERVAL.ordinal()] = 0; // 条件は > なので0で1tick後に超える
		prop[AttachProperty.ANIME_FRAMES.ordinal()] = 4;
		att.setAttachProperty(prop);
		att.setAnimeLoop(0); // 無限ループ

		att.clockTick();

		// animeInterval(0) + TICK(1) > 0 → フレーム進む
		assertEquals(1, att.getAnimeFrame());
	}

	@Test
	public void testClockTickWrapsAnimationFrame() {
		Yukkuri parent = createParent(AgeState.BABY);
		StubAttachment att = new StubAttachment(parent);
		att.setProcessInterval(9999);
		att.setAnimate(true);
		att.setAnimeFrame(3); // 最後のフレーム（4枚中index 3）

		int[] prop = new int[AttachProperty.values().length];
		prop[AttachProperty.ANIME_INTERVAL.ordinal()] = 0;
		prop[AttachProperty.ANIME_FRAMES.ordinal()] = 4;
		att.setAttachProperty(prop);
		att.setAnimeLoop(0); // 無限ループ

		att.clockTick();

		// フレームが巻き戻る
		assertEquals(0, att.getAnimeFrame());
	}

	@Test
	public void testClockTickDecreasesAnimeLoop() {
		Yukkuri parent = createParent(AgeState.BABY);
		StubAttachment att = new StubAttachment(parent);
		att.setProcessInterval(9999);
		att.setAnimate(true);
		att.setAnimeFrame(3);

		int[] prop = new int[AttachProperty.values().length];
		prop[AttachProperty.ANIME_INTERVAL.ordinal()] = 0;
		prop[AttachProperty.ANIME_FRAMES.ordinal()] = 4;
		att.setAttachProperty(prop);
		att.setAnimeLoop(2); // 残り2ループ

		att.clockTick();

		// ループ回数が減る
		assertEquals(1, att.getAnimeLoop());
		assertTrue(att.isAnimate()); // まだアニメ中
	}

	@Test
	public void testClockTickStopsAnimationWhenLoopReachesZero() {
		Yukkuri parent = createParent(AgeState.BABY);
		StubAttachment att = new StubAttachment(parent);
		att.setProcessInterval(9999);
		att.setAnimate(true);
		att.setAnimeFrame(3);

		int[] prop = new int[AttachProperty.values().length];
		prop[AttachProperty.ANIME_INTERVAL.ordinal()] = 0;
		prop[AttachProperty.ANIME_FRAMES.ordinal()] = 4;
		att.setAttachProperty(prop);
		att.setAnimeLoop(1); // 残り1ループ

		att.clockTick();

		// ループ回数が0になってアニメ停止
		assertEquals(0, att.getAnimeLoop());
		assertFalse(att.isAnimate());
	}

	@Test
	public void testClockTickNoAnimationWhenDisabled() {
		Yukkuri parent = createParent(AgeState.BABY);
		StubAttachment att = new StubAttachment(parent);
		att.setProcessInterval(9999);
		att.setAnimate(false);
		att.setAnimeFrame(0);

		att.clockTick();

		// アニメが無効なのでフレームは変わらない
		assertEquals(0, att.getAnimeFrame());
	}

	// --- getOfsX / getOfsY のテスト ---

	@Test
	public void testGetOfsXReturnsNegativeOneWhenParentNull() {
		StubAttachment att = new StubAttachment();
		att.setParent(-1);

		assertEquals(-1, att.getOfsX());
	}

	@Test
	public void testGetOfsYReturnsNegativeOneWhenParentNull() {
		StubAttachment att = new StubAttachment();
		att.setParent(-1);

		assertEquals(-1, att.getOfsY());
	}

	@Test
	public void testGetOfsXReturnsCorrectValue() {
		Yukkuri parent = createParent(AgeState.CHILD);
		StubAttachment att = new StubAttachment(parent);
		Point4y[] ofs = new Point4y[] {
				new Point4y(10, 20),
				new Point4y(30, 40),
				new Point4y(50, 60)
		};
		att.setPosOfs(ofs);

		assertEquals(30, att.getOfsX()); // CHILD.ordinal() == 1
	}

	@Test
	public void testGetOfsYReturnsCorrectValue() {
		Yukkuri parent = createParent(AgeState.CHILD);
		StubAttachment att = new StubAttachment(parent);
		Point4y[] ofs = new Point4y[] {
				new Point4y(10, 20),
				new Point4y(30, 40),
				new Point4y(50, 60)
		};
		att.setPosOfs(ofs);

		assertEquals(40, att.getOfsY()); // CHILD.ordinal() == 1
	}

	// --- getParentOrigin のテスト ---

	@Test
	public void testGetParentOrigin() {
		StubAttachment att = new StubAttachment();
		int[] prop = new int[AttachProperty.values().length];
		prop[AttachProperty.OFS_ORIGIN.ordinal()] = 1;
		att.setAttachProperty(prop);

		assertEquals(1, att.getParentOrigin());
	}

	// --- ヘルパーメソッド ---

	private Yukkuri createParent(AgeState ageState) {
		Yukkuri parent = new Reimu();
		parent.setAgeState(ageState);
		SimYukkuri.world.getCurrentMap().getYukkuriMap().put(parent.getUniqueID(), parent);
		return parent;
	}

	/**
	 * getMountPointがPoint4y配列を返すStubBodyを作成する.
	 */
	private Yukkuri createParentWithMountPoint(AgeState ageState) {
		Yukkuri parent = new StubBody() {
			private static final long serialVersionUID = 1L;

			@Override
			public Point4y[] getMountPoint(String key) {
				return new Point4y[] {
						new Point4y(1, 2),
						new Point4y(3, 4),
						new Point4y(5, 6)
				};
			}
		};
		parent.setAgeState(ageState);
		SimYukkuri.world.getCurrentMap().getYukkuriMap().put(parent.getUniqueID(), parent);
		return parent;
	}

	@Nested
	class RegressionScenarios {

		@Test
		public void testScenario_UpdateAndAnimationAdvanceOnSameTick() {
			Yukkuri parent = createParent(AgeState.BABY);
			StubAttachment att = new StubAttachment(parent);
			att.setProcessInterval(1);
			att.setAnimate(true);
			att.setAnimeFrame(0);
			int[] prop = new int[AttachProperty.values().length];
			prop[AttachProperty.ANIME_INTERVAL.ordinal()] = 0;
			prop[AttachProperty.ANIME_FRAMES.ordinal()] = 4;
			att.setAttachProperty(prop);
			att.setAnimeLoop(0);

			Event result = att.clockTick();

			assertEquals(Event.DONOTHING, result);
			assertEquals(1, att.getUpdateCallCount());
			assertEquals(1, att.getAnimeFrame());
		}

		@Test
		public void testScenario_FinalAnimationLoopStopsExactlyWhenFrameWraps() {
			Yukkuri parent = createParent(AgeState.BABY);
			StubAttachment att = new StubAttachment(parent);
			att.setProcessInterval(9999);
			att.setAnimate(true);
			att.setAnimeFrame(3);
			int[] prop = new int[AttachProperty.values().length];
			prop[AttachProperty.ANIME_INTERVAL.ordinal()] = 0;
			prop[AttachProperty.ANIME_FRAMES.ordinal()] = 4;
			att.setAttachProperty(prop);
			att.setAnimeLoop(1);

			Event result = att.clockTick();

			assertEquals(Event.DONOTHING, result);
			assertEquals(0, att.getAnimeFrame());
			assertEquals(0, att.getAnimeLoop());
			assertFalse(att.isAnimate());
		}
	}
}
