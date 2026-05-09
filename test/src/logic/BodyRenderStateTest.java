package src.logic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Yukkuri;
import src.entity.world.bodylinked.Okazari;
import src.base.StubBody;
import src.draw.World;
import src.enums.CoreAnkoState;
import src.enums.CriticalDamegeType;
import src.enums.ImageCode;
import src.entity.world.bodylinked.Okazari.OkazariType;
import src.system.BodyLayer;

class BodyRenderStateTest {

	private RenderingStubBody body;
	private BodyLayer layer;

	@BeforeEach
	void setUp() {
		SimYukkuri.world = new World();
		body = new RenderingStubBody();
		layer = new BodyLayer();
	}

	@Test
	void getFaceImageUsesPealedDeadFace() {
		body.setDead(true);
		body.setPealed(true);

		BodyRenderState.getFaceImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.PEALEDDEADFACE.ordinal()));
	}

	@Test
	void getFaceImageUsesDeadFaceEvenWhenUnBirth() {
		body.setDead(true);
		body.setUnBirth(true);

		BodyRenderState.getFaceImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.DEAD.ordinal()));
	}

	@Test
	void getFaceImageUsesNydFace() throws Exception {
		setField(body, "coreAnkoState", CoreAnkoState.NonYukkuriDisease);

		BodyRenderState.getFaceImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.NYD_FRONT_WIDE.ordinal()));
	}

	@Test
	void getFaceImageUsesBlinkOverlayWhileSleepingInUnyoMode() throws Exception {
		SimYukkuri.UNYO = true;
		try {
			body.setSleeping(true);
			setField(body, "blinkCount", 1);

			BodyRenderState.getFaceImage(body, layer);

			assertTrue(body.codes.contains(ImageCode.EYE2.ordinal()));
		} finally {
			SimYukkuri.UNYO = false;
		}
	}

	@Test
	void getBodyBaseImageUsesCrushedImageWhenCrushedWithoutAccessory() throws Exception {
		body.setCrushed(true);
		body.setOkazari(null);

		BodyRenderState.getBodyBaseImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.CRUSHED2.ordinal()));
	}

	@Test
	void getBodyBaseImageUsesFrontShitImageWhileShitting() {
		body.setShitting(true);

		BodyRenderState.getBodyBaseImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.FRONT_SHIT.ordinal()));
	}

	@Test
	void getBodyBaseImageUsesBodyImageForNormalState() {
		BodyRenderState.getBodyBaseImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.BODY.ordinal()));
		assertTrue(layer.getOption()[0] == 1);
	}

	@Test
	void getAbnormalBodyImageUsesCutOverlayWhenCriticalDamageIsCut() {
		body.setCriticalDamege(CriticalDamegeType.CUT);

		BodyRenderState.getAbnormalBodyImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.BODY_CUT.ordinal()));
	}

	@Test
	void getAbnormalBodyImageUsesPealedMeltOverlay() {
		body.setMelt(true);
		body.setPealed(true);

		BodyRenderState.getAbnormalBodyImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.MELT_PEALED.ordinal()));
	}

	@Test
	void getEffectImageUsesHungryAndWetOverlays() {
		body.setAge(100000);
		body.setHungry(0);
		body.setDamage(8400);
		body.setWet(true);

		BodyRenderState.getEffectImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.HUNGRY2.ordinal()));
		assertTrue(body.codes.contains(ImageCode.WET.ordinal()));
	}

	@Test
	void getEffectImageUsesDeadBodyOverlayForDeadUnBirth() {
		body.setDead(true);
		body.setUnBirth(true);

		BodyRenderState.getEffectImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.DEAD_BODY.ordinal()));
	}

	@Test
	void getEffectImageUsesHighestSickOverlay() throws Exception {
		setField(body, "sickPeriod", 100000);

		BodyRenderState.getEffectImage(body, layer);

		assertTrue(body.codes.contains(ImageCode.SICK3.ordinal()));
	}

	@Test
	void getOlazariImageUsesAccessoryForDefaultOkazari() {
		body.setOkazari(new Okazari(body, OkazariType.DEFAULT));

		BodyRenderState.getOlazariImage(body, layer, 0);

		assertTrue(body.codes.contains(ImageCode.ACCESSORY.ordinal()));
	}

	@Test
	void getBraidImageUsesCutImageWhenNoBraidExists() {
		body.setHasBraid(false);

		BodyRenderState.getBraidImage(body, layer, 0);

		assertTrue(body.codes.contains(ImageCode.BRAID_CUT.ordinal()));
	}

	@Test
	void getBraidImageUsesBackImageForTypeOne() {
		body.setHasBraid(true);

		BodyRenderState.getBraidImage(body, layer, 1);

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
		public int getImage(int type, int direction, BodyLayer layer, int index) {
			codes.add(type);
			return 1;
		}
	}
}
