package src.draw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Point4y クラスのユニットテスト
 */
public class Point4yTest {

    @Test
    @DisplayName("デフォルトコンストラクタでx=0, y=0になる")
    public void testDefaultConstructor() {
        Point4y point = new Point4y();
        assertEquals(0, point.getX());
        assertEquals(0, point.getY());
    }

    @Test
    @DisplayName("パラメータ付きコンストラクタで指定値がセットされる")
    public void testParameterizedConstructor() {
        Point4y point = new Point4y(100, 200);
        assertEquals(100, point.getX());
        assertEquals(200, point.getY());
    }

    @Test
    @DisplayName("負の値もセットできる")
    public void testNegativeValues() {
        Point4y point = new Point4y(-50, -100);
        assertEquals(-50, point.getX());
        assertEquals(-100, point.getY());
    }

    @Test
    @DisplayName("setXでx座標を変更できる")
    public void testSetX() {
        Point4y point = new Point4y();
        point.setX(42);
        assertEquals(42, point.getX());
    }

    @Test
    @DisplayName("setYでy座標を変更できる")
    public void testSetY() {
        Point4y point = new Point4y();
        point.setY(84);
        assertEquals(84, point.getY());
    }

    @Test
    @DisplayName("publicフィールドに直接アクセスして値を変更できる")
    public void testDirectFieldAccess() {
        Point4y point = new Point4y();
        point.setX(123);
        point.setY(456);
        assertEquals(123, point.getX());
        assertEquals(456, point.getY());
    }

    @Test
    @DisplayName("toStringが正しいフォーマットを返す")
    public void testToString() {
        Point4y point = new Point4y(10, 20);
        assertEquals("x: 10, y: 20", point.toString());
    }

    @Test
    @DisplayName("toStringで負の値も正しく表示される")
    public void testToStringNegative() {
        Point4y point = new Point4y(-5, -10);
        assertEquals("x: -5, y: -10", point.toString());
    }

    @Test
    @DisplayName("シリアライズ・デシリアライズできる")
    public void testSerialization() throws Exception {
        Point4y original = new Point4y(777, 888);

        // シリアライズ
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();

        // デシリアライズ
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Point4y restored = (Point4y) ois.readObject();
        ois.close();

        // 値が復元されていることを確認
        assertEquals(original.getX(), restored.getX());
        assertEquals(original.getY(), restored.getY());
    }

    @Test
    @DisplayName("Integer.MAX_VALUEやMIN_VALUEも扱える")
    public void testExtremeValues() {
        Point4y point = new Point4y(Integer.MAX_VALUE, Integer.MIN_VALUE);
        assertEquals(Integer.MAX_VALUE, point.getX());
        assertEquals(Integer.MIN_VALUE, point.getY());
    }
}
