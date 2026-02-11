package src.draw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Rectangle4y クラスのユニットテスト
 */
public class Rectangle4yTest {

    @Test
    @DisplayName("デフォルトコンストラクタで全フィールドが0になる")
    public void testDefaultConstructor() {
        Rectangle4y rect = new Rectangle4y();
        assertEquals(0, rect.getX());
        assertEquals(0, rect.getY());
        assertEquals(0, rect.getWidth());
        assertEquals(0, rect.getHeight());
        assertEquals(0, rect.getX());
        assertEquals(0, rect.getY());
        assertEquals(0, rect.getWidth());
        assertEquals(0, rect.getHeight());
    }

    @Test
    @DisplayName("パラメータ付きコンストラクタで指定値がセットされる")
    public void testParameterizedConstructor() {
        Rectangle4y rect = new Rectangle4y(10, 20, 100, 200);
        assertEquals(10, rect.getX());
        assertEquals(20, rect.getY());
        assertEquals(100, rect.getWidth());
        assertEquals(200, rect.getHeight());
    }

    @Test
    @DisplayName("負の座標値もセットできる")
    public void testNegativePosition() {
        Rectangle4y rect = new Rectangle4y(-50, -100, 200, 150);
        assertEquals(-50, rect.getX());
        assertEquals(-100, rect.getY());
        assertEquals(200, rect.getWidth());
        assertEquals(150, rect.getHeight());
    }

    @Test
    @DisplayName("setXでx座標を変更できる")
    public void testSetX() {
        Rectangle4y rect = new Rectangle4y();
        rect.setX(42);
        assertEquals(42, rect.getX());
        assertEquals(42, rect.getX());
    }

    @Test
    @DisplayName("setYでy座標を変更できる")
    public void testSetY() {
        Rectangle4y rect = new Rectangle4y();
        rect.setY(84);
        assertEquals(84, rect.getY());
        assertEquals(84, rect.getY());
    }

    @Test
    @DisplayName("setWidthでwidthを変更できる")
    public void testSetWidth() {
        Rectangle4y rect = new Rectangle4y();
        rect.setWidth(640);
        assertEquals(640, rect.getWidth());
        assertEquals(640, rect.getWidth());
    }

    @Test
    @DisplayName("setHeightでheightを変更できる")
    public void testSetHeight() {
        Rectangle4y rect = new Rectangle4y();
        rect.setHeight(480);
        assertEquals(480, rect.getHeight());
        assertEquals(480, rect.getHeight());
    }

    @Test
    @DisplayName("setterメソッドを使用して値を変更できる")
    public void testSetters() {
        Rectangle4y rect = new Rectangle4y();
        rect.setX(10);
        rect.setY(20);
        rect.setWidth(30);
        rect.setHeight(40);
        assertEquals(10, rect.getX());
        assertEquals(20, rect.getY());
        assertEquals(30, rect.getWidth());
        assertEquals(40, rect.getHeight());
    }

    @Test
    @DisplayName("toStringが正しいフォーマットを返す")
    public void testToString() {
        Rectangle4y rect = new Rectangle4y(5, 10, 100, 200);
        // 注意: フォーマットの微妙な違い(width前のスペース)
        assertEquals("x: 5, y: 10, width :100, height:200", rect.toString());
    }

    @Test
    @DisplayName("シリアライズ・デシリアライズできる")
    public void testSerialization() throws Exception {
        Rectangle4y original = new Rectangle4y(100, 200, 800, 600);

        // シリアライズ
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();

        // デシリアライズ
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Rectangle4y restored = (Rectangle4y) ois.readObject();
        ois.close();

        // 値が復元されていることを確認
        assertEquals(original.getX(), restored.getX());
        assertEquals(original.getY(), restored.getY());
        assertEquals(original.getWidth(), restored.getWidth());
        assertEquals(original.getHeight(), restored.getHeight());
    }

    @Test
    @DisplayName("典型的なゲーム画面サイズで使える")
    public void testTypicalGameDimensions() {
        // 画面全体
        Rectangle4y screen = new Rectangle4y(0, 0, 1920, 1080);
        assertEquals(0, screen.getX());
        assertEquals(0, screen.getY());
        assertEquals(1920, screen.getWidth());
        assertEquals(1080, screen.getHeight());

        // ゆっくりの当たり判定エリアとか
        Rectangle4y hitbox = new Rectangle4y(100, 150, 64, 64);
        assertEquals(100, hitbox.getX());
        assertEquals(150, hitbox.getY());
        assertEquals(64, hitbox.getWidth());
        assertEquals(64, hitbox.getHeight());
    }
}
