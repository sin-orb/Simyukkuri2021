package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.BasicStroke;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.jupiter.api.Test;

/**
 * BasicStrokeEx のテスト.
 */
class BasicStrokeExTest {

	@Test
	void testConstructorsAndGetters() {
		BasicStrokeEx stroke = new BasicStrokeEx(2.0f);
		assertEquals(2.0f, stroke.getLineWidth());

		stroke = new BasicStrokeEx(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
		assertEquals(3.0f, stroke.getLineWidth());
		assertEquals(BasicStroke.CAP_ROUND, stroke.getEndCap());
		assertEquals(BasicStroke.JOIN_BEVEL, stroke.getLineJoin());

		float[] dash = { 10.0f, 5.0f };
		stroke = new BasicStrokeEx(4.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
		assertArrayEquals(dash, stroke.getDashArray());
	}

	@Test
	void testDefaultConstructorAndMiterLimitConstructor() {
		BasicStrokeEx def = new BasicStrokeEx();
		assertEquals(1.0f, def.getLineWidth());
		assertEquals(BasicStroke.CAP_SQUARE, def.getEndCap());
		assertEquals(BasicStroke.JOIN_MITER, def.getLineJoin());
		assertEquals(10.0f, def.getMiterLimit());
		assertEquals(0.0f, def.getDashPhase());
		assertNull(def.getDashArray());

		BasicStrokeEx stroke = new BasicStrokeEx(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f);
		assertEquals(2.0f, stroke.getLineWidth());
		assertEquals(BasicStroke.CAP_BUTT, stroke.getEndCap());
		assertEquals(BasicStroke.JOIN_MITER, stroke.getLineJoin());
		assertEquals(10.0f, stroke.getMiterLimit());
	}

	@Test
	void testSerializableWithAlreadySerializable() {
		// すでにSerializableなBasicStrokeExを渡すとそのまま返す
		BasicStrokeEx original = new BasicStrokeEx(1.5f);
		BasicStroke result = BasicStrokeEx.serializable(original);
		assertNotNull(result);
		// BasicStrokeExはSerializableなので同じオブジェクトが返る
		assertTrue(result == original);
	}

	@Test
	void testSerialization() throws Exception {
		BasicStrokeEx original = new BasicStrokeEx(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(original);
		oos.close();

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);
		BasicStrokeEx deserialized = (BasicStrokeEx) ois.readObject();

		assertEquals(original.getLineWidth(), deserialized.getLineWidth());
		assertEquals(original.getEndCap(), deserialized.getEndCap());
		assertEquals(original.getLineJoin(), deserialized.getLineJoin());
	}

	@Test
	void testSerializableUtility() {
		BasicStroke normal = new BasicStroke(1.0f);
		BasicStroke serializable = BasicStrokeEx.serializable(normal);

		assertNotNull(serializable);
		assertTrue(serializable instanceof BasicStrokeEx);
		assertEquals(normal.getLineWidth(), serializable.getLineWidth());
	}
}
