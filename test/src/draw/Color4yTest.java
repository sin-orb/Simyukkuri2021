package src.draw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Color4y クラスのユニットテスト
 */
public class Color4yTest {

    @Test
    @DisplayName("デフォルトコンストラクタで全成分が0になる")
    public void testDefaultConstructor() {
        Color4y color = new Color4y();
        assertEquals(0, color.getRed());
        assertEquals(0, color.getGreen());
        assertEquals(0, color.getBlue());
        assertEquals(0, color.getAlpha());
    }

    @Test
    @DisplayName("パラメータ付きコンストラクタで指定値がセットされる")
    public void testParameterizedConstructor() {
        Color4y color = new Color4y(255, 128, 64, 200);
        assertEquals(255, color.getRed());
        assertEquals(128, color.getGreen());
        assertEquals(64, color.getBlue());
        assertEquals(200, color.getAlpha());
    }

    @Test
    @DisplayName("setRedでred成分を変更できる")
    public void testSetRed() {
        Color4y color = new Color4y();
        color.setRed(100);
        assertEquals(100, color.getRed());
    }

    @Test
    @DisplayName("setGreenでgreen成分を変更できる")
    public void testSetGreen() {
        Color4y color = new Color4y();
        color.setGreen(150);
        assertEquals(150, color.getGreen());
    }

    @Test
    @DisplayName("setBlueでblue成分を変更できる")
    public void testSetBlue() {
        Color4y color = new Color4y();
        color.setBlue(200);
        assertEquals(200, color.getBlue());
    }

    @Test
    @DisplayName("setAlphaでalpha成分を変更できる")
    public void testSetAlpha() {
        Color4y color = new Color4y();
        color.setAlpha(128);
        assertEquals(128, color.getAlpha());
    }

    @Test
    @DisplayName("toStringが正しいフォーマットを返す")
    public void testToString() {
        Color4y color = new Color4y(255, 128, 64, 255);
        assertEquals("red: 255, green: 128, blue: 64, alpha: 255", color.toString());
    }

    @Test
    @DisplayName("シリアライズ・デシリアライズできる")
    public void testSerialization() throws Exception {
        Color4y original = new Color4y(100, 150, 200, 250);

        // シリアライズ
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();

        // デシリアライズ
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Color4y restored = (Color4y) ois.readObject();
        ois.close();

        // 値が復元されていることを確認
        assertEquals(original.getRed(), restored.getRed());
        assertEquals(original.getGreen(), restored.getGreen());
        assertEquals(original.getBlue(), restored.getBlue());
        assertEquals(original.getAlpha(), restored.getAlpha());
    }

    @Test
    @DisplayName("典型的な色を表現できる")
    public void testTypicalColors() {
        // 完全な赤
        Color4y red = new Color4y(255, 0, 0, 255);
        assertEquals(255, red.getRed());
        assertEquals(0, red.getGreen());
        assertEquals(0, red.getBlue());

        // 完全な緑
        Color4y green = new Color4y(0, 255, 0, 255);
        assertEquals(0, green.getRed());
        assertEquals(255, green.getGreen());
        assertEquals(0, green.getBlue());

        // 完全な青
        Color4y blue = new Color4y(0, 0, 255, 255);
        assertEquals(0, blue.getRed());
        assertEquals(0, blue.getGreen());
        assertEquals(255, blue.getBlue());

        // 白
        Color4y white = new Color4y(255, 255, 255, 255);
        assertEquals(255, white.getRed());
        assertEquals(255, white.getGreen());
        assertEquals(255, white.getBlue());

        // 黒
        Color4y black = new Color4y(0, 0, 0, 255);
        assertEquals(0, black.getRed());
        assertEquals(0, black.getGreen());
        assertEquals(0, black.getBlue());
    }

    @Test
    @DisplayName("透明度の範囲テスト")
    public void testAlphaRange() {
        // 完全透明
        Color4y transparent = new Color4y(255, 255, 255, 0);
        assertEquals(0, transparent.getAlpha());

        // 半透明
        Color4y semiTransparent = new Color4y(255, 255, 255, 128);
        assertEquals(128, semiTransparent.getAlpha());

        // 完全不透明
        Color4y opaque = new Color4y(255, 255, 255, 255);
        assertEquals(255, opaque.getAlpha());
    }

    @Test
    @DisplayName("255を超える値はコンストラクタで255にクランプされる")
    public void testValuesOver255Clamped() {
        Color4y color = new Color4y(1000, 2000, 3000, 4000);
        assertEquals(255, color.getRed());
        assertEquals(255, color.getGreen());
        assertEquals(255, color.getBlue());
        assertEquals(255, color.getAlpha());
    }

    @Test
    @DisplayName("負の値はコンストラクタで0にクランプされる")
    public void testNegativeValuesClamped() {
        Color4y color = new Color4y(-1, -50, -100, -255);
        assertEquals(0, color.getRed());
        assertEquals(0, color.getGreen());
        assertEquals(0, color.getBlue());
        assertEquals(0, color.getAlpha());
    }

    @Test
    @DisplayName("setterでも255を超える値は255にクランプされる")
    public void testSetterClampsOver255() {
        Color4y color = new Color4y();
        color.setRed(300);
        color.setGreen(500);
        color.setBlue(1000);
        color.setAlpha(999);
        assertEquals(255, color.getRed());
        assertEquals(255, color.getGreen());
        assertEquals(255, color.getBlue());
        assertEquals(255, color.getAlpha());
    }

    @Test
    @DisplayName("setterでも負の値は0にクランプされる")
    public void testSetterClampsNegative() {
        Color4y color = new Color4y();
        color.setRed(-10);
        color.setGreen(-100);
        color.setBlue(-1);
        color.setAlpha(-255);
        assertEquals(0, color.getRed());
        assertEquals(0, color.getGreen());
        assertEquals(0, color.getBlue());
        assertEquals(0, color.getAlpha());
    }

    @Test
    @DisplayName("境界値: 0と255は変更されずにセットされる")
    public void testBoundaryValues() {
        Color4y color = new Color4y(0, 255, 0, 255);
        assertEquals(0, color.getRed());
        assertEquals(255, color.getGreen());
        assertEquals(0, color.getBlue());
        assertEquals(255, color.getAlpha());
    }
}
