package src.system;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.BasicStroke;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Test;

class BasicStrokeEXTest {

    @Test
    void testConstructorsAndGetters() {
        BasicStrokeEX stroke = new BasicStrokeEX(2.0f);
        assertEquals(2.0f, stroke.getLineWidth());

        stroke = new BasicStrokeEX(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        assertEquals(3.0f, stroke.getLineWidth());
        assertEquals(BasicStroke.CAP_ROUND, stroke.getEndCap());
        assertEquals(BasicStroke.JOIN_BEVEL, stroke.getLineJoin());

        float[] dash = { 10.0f, 5.0f };
        stroke = new BasicStrokeEX(4.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        assertArrayEquals(dash, stroke.getDashArray());
    }

    @Test
    void testSettersDontThrow() {
        // Java 21ではfinalフィールドへのreflectionが制限されるが例外は投げない
        BasicStrokeEX stroke = new BasicStrokeEX(1.0f);
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> stroke.setLineWidth(5.0f));
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> stroke.setEndCap(BasicStroke.CAP_SQUARE));
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> stroke.setLineJoin(BasicStroke.JOIN_ROUND));
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> stroke.setMiterLimit(2.0f));
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> stroke.setDashArray(new float[]{5.0f, 5.0f}));
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> stroke.setDashPhase(1.0f));
    }

    @Test
    void testDefaultConstructorAndMiterLimitConstructor() {
        // デフォルトコンストラクタ
        BasicStrokeEX def = new BasicStrokeEX();
        assertNotNull(def);

        // (float, int, int, float) コンストラクタ
        BasicStrokeEX stroke = new BasicStrokeEX(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f);
        assertEquals(2.0f, stroke.getLineWidth());
        assertEquals(BasicStroke.CAP_BUTT, stroke.getEndCap());
        assertEquals(BasicStroke.JOIN_MITER, stroke.getLineJoin());
        assertEquals(10.0f, stroke.getMiterLimit());
    }

    @Test
    void testSerializableWithAlreadySerializable() {
        // すでにSerializableなBasicStrokeEXを渡すとそのまま返す
        BasicStrokeEX original = new BasicStrokeEX(1.5f);
        BasicStroke result = BasicStrokeEX.serializable(original);
        assertNotNull(result);
        // BasicStrokeEXはSerializableなので同じオブジェクトが返る
        assertTrue(result == original);
    }

    @Test
    void testSerialization() throws Exception {
        BasicStrokeEX original = new BasicStrokeEX(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        BasicStrokeEX deserialized = (BasicStrokeEX) ois.readObject();

        assertEquals(original.getLineWidth(), deserialized.getLineWidth());
        assertEquals(original.getEndCap(), deserialized.getEndCap());
        assertEquals(original.getLineJoin(), deserialized.getLineJoin());
    }

    @Test
    void testSerializableUtility() {
        BasicStroke normal = new BasicStroke(1.0f);
        BasicStroke serializable = BasicStrokeEX.serializable(normal);

        assertNotNull(serializable);
        assertTrue(serializable instanceof BasicStrokeEX);
        assertEquals(normal.getLineWidth(), serializable.getLineWidth());
    }
}
