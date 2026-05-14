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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Okazari;
import org.simyukkuri.entity.core.living.yukkuri.StubBody;
import org.simyukkuri.draw.World;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.CriticalDamegeType;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.entity.core.world.bodylinked.Okazari.OkazariType;
import org.simyukkuri.system.YukkuriLayer;

class BodyRenderStateTest {

	private RenderingStubBody body;
	private YukkuriLayer layer;

	@BeforeEach
	void setUp() {
		SimYukkuri.world = new World();
		body = new RenderingStubBody();
		layer = new YukkuriLayer();
	}

	@Test
	void getFaceImageUsesPealedDeadFace() {
		body.setDead(true);
		body.setPealed(true);

		YukkuriRenderState.getFaceImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.PEALEDDEADFACE.ordinal()));
	}

	@Test
	void getFaceImageUsesDeadFaceEvenWhenUnBirth() {
		body.setDead(true);
		body.setUnBirth(true);

		YukkuriRenderState.getFaceImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.DEAD.ordinal()));
	}

	@Test
	void getFaceImageUsesNydFace() throws Exception {
		setField(body, "coreAnkoState", CoreAnkoState.NonYukkuriDisease);

		YukkuriRenderState.getFaceImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.NYD_FRONT_WIDE.ordinal()));
	}

	@Test
	void getFaceImageUsesBlinkOverlayWhileSleepingInUnyoMode() throws Exception {
		SimYukkuri.UNYO = true;
		try {
			body.setSleeping(true);
			setField(body, "blinkCount", 1);

			YukkuriRenderState.getFaceImage(body, layer);

			assertTrue(body.codes.contains(ImageCode.EYE2.ordinal()));
		} finally {
			SimYukkuri.UNYO = false;
		}
	}

	@Test
	void getBodyBaseImageUsesCrushedImageWhenCrushedWithoutAccessory() throws Exception {
		body.setCrushed(true);
		body.setOkazari(null);

		YukkuriRenderState.getImageIndex(body, layer);

		assertTrue(body.codes.contains(ImageCode.CRUSHED2.ordinal()));
	}

	@Test
	void getBodyBaseImageUsesFrontShitImageWhileShitting() {
		body.setShitting(true);

		YukkuriRenderState.getImageIndex(body, layer);

		assertTrue(body.codes.contains(ImageCode.FRONT_SHIT.ordinal()));
	}

	@Test
	void getBodyBaseImageUsesBodyImageForNormalState() {
		YukkuriRenderState.getImageIndex(body, layer);

		assertTrue(body.codes.contains(ImageCode.BODY.ordinal()));
		assertTrue(layer.getOption()[0] == 1);
	}

	@Test
	void getAbnormalBodyImageUsesCutOverlayWhenCriticalDamageIsCut() {
		body.setCriticalDamege(CriticalDamegeType.CUT);

		YukkuriRenderState.getDamageImageIndex(body, layer);

		assertTrue(body.codes.contains(ImageCode.BODY_CUT.ordinal()));
	}

	@Test
	void getAbnormalBodyImageUsesPealedMeltOverlay() {
		body.setMelt(true);
		body.setPealed(true);

		YukkuriRenderState.getDamageImageIndex(body, layer);

		assertTrue(body.codes.contains(ImageCode.MELT_PEALED.ordinal()));
	}

	@Test
	void getEffectImageUsesHungryAndWetOverlays() {
		body.setAge(100000);
		body.setHungry(0);
		body.setDamage(8400);
		body.setWet(true);

		YukkuriRenderState.getEffectImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.HUNGRY2.ordinal()));
		assertTrue(body.codes.contains(ImageCode.WET.ordinal()));
	}

	@Test
	void getEffectImageUsesDeadBodyOverlayForDeadUnBirth() {
		body.setDead(true);
		body.setUnBirth(true);

		YukkuriRenderState.getEffectImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.DEAD_BODY.ordinal()));
	}

	@Test
	void getEffectImageUsesHighestSickOverlay() throws Exception {
		setField(body, "sickPeriod", 100000);

		YukkuriRenderState.getEffectImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.SICK3.ordinal()));
	}

	@Test
	void getOlazariImageUsesAccessoryForDefaultOkazari() {
		body.setOkazari(new Okazari(body, OkazariType.DEFAULT));

		YukkuriRenderState.getOkazariImageIndex(body, layer, 0);

		assertTrue(body.codes.contains(ImageCode.ACCESSORY.ordinal()));
	}

	@Test
	void getBraidImageUsesCutImageWhenNoBraidExists() {
		body.setHasBraid(false);

		YukkuriRenderState.getBraidImage(body, layer, 0);

		assertTrue(body.codes.contains(ImageCode.BRAID_CUT.ordinal()));
	}

	@Test
	void getBraidImageUsesBackImageForTypeOne() {
		body.setHasBraid(true);

		YukkuriRenderState.getBraidImage(body, layer, 1);

		assertTrue(body.codes.contains(ImageCode.BRAID_BACK.ordinal()));
	}

	private static void setField(Object obj, String fieldName, Object value) throws Exception {
		Field field = null;
		Class<?> clazz = obj.getClass();
		while (clazz != null) {
			try {
				field = clazz.getDeclaredField(fieldName);
				break;
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}
		if (field == null) {
			throw new NoSuchFieldException(fieldName);
		}
		field.setAccessible(true);
		field.set(obj, value);
	}

	private static final class RenderingStubBody extends StubBody {
		private static final long serialVersionUID = 1L;

		private final List<Integer> codes = new ArrayList<Integer>();

		@Override
		public int getImage(int type, int direction, YukkuriLayer layer, int index) {
			codes.add(type);
			return 1;
		}
	}
}
