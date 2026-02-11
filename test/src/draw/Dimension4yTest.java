package src.draw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Dimension4y クラスのユニットテスト
 */
public class Dimension4yTest {

    @Test
    @DisplayName("デフォルトコンストラクタでwidth=0, height=0になる")
    public void testDefaultConstructor() {
        Dimension4y dim = new Dimension4y();
        assertEquals(0, dim.getWidth());
        assertEquals(0, dim.getHeight());
        assertEquals(0, dim.getWidth());
        assertEquals(0, dim.getHeight());
    }

    @Test
    @DisplayName("パラメータ付きコンストラクタで指定値がセットされる")
    public void testParameterizedConstructor() {
        Dimension4y dim = new Dimension4y(800, 600);
        assertEquals(800, dim.getWidth());
        assertEquals(600, dim.getHeight());
        assertEquals(800, dim.getWidth());
        assertEquals(600, dim.getHeight());
    }

    @Test
    @DisplayName("負の値もセットできる（通常は使わないが）")
    public void testNegativeValues() {
        Dimension4y dim = new Dimension4y(-100, -200);
        assertEquals(-100, dim.getWidth());
        assertEquals(-200, dim.getHeight());
    }

    @Test
    @DisplayName("setWidthでwidthを変更できる")
    public void testSetWidth() {
        Dimension4y dim = new Dimension4y();
        dim.setWidth(1920);
        assertEquals(1920, dim.getWidth());
        assertEquals(1920, dim.getWidth());
    }

    @Test
    @DisplayName("setHeightでheightを変更できる")
    public void testSetHeight() {
        Dimension4y dim = new Dimension4y();
        dim.setHeight(1080);
        assertEquals(1080, dim.getHeight());
        assertEquals(1080, dim.getHeight());
    }

    @Test
    @DisplayName("setterメソッドを使用して値を変更できる")
    public void testSetters() {
        Dimension4y dim = new Dimension4y();
        dim.setWidth(640);
        dim.setHeight(480);
        assertEquals(640, dim.getWidth());
        assertEquals(480, dim.getHeight());
    }

    @Test
    @DisplayName("toStringが正しいフォーマットを返す")
    public void testToString() {
        Dimension4y dim = new Dimension4y(100, 200);
        // 注意: "width :"とコロンの前にスペースがある
        assertEquals("width :100, height:200", dim.toString());
    }

    @Test
    @DisplayName("シリアライズ・デシリアライズできる")
    public void testSerialization() throws Exception {
        Dimension4y original = new Dimension4y(1024, 768);

        // シリアライズ
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();

        // デシリアライズ
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Dimension4y restored = (Dimension4y) ois.readObject();
        ois.close();

        // 値が復元されていることを確認
        assertEquals(original.getWidth(), restored.getWidth());
        assertEquals(original.getHeight(), restored.getHeight());
    }

    @Test
    @DisplayName("Integer.MAX_VALUEも扱える")
    public void testExtremeValues() {
        Dimension4y dim = new Dimension4y(Integer.MAX_VALUE, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, dim.getWidth());
        assertEquals(Integer.MAX_VALUE, dim.getHeight());
    }
}
