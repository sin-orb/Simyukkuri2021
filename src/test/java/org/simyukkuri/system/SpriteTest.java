package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.draw.Rectangle4y;

/**
 * Test class for Sprite.
 * Sprite is pure geometry logic with no dependencies - highly testable.
 */
public class SpriteTest {

    @Test
    public void testConstructorCenterCenter() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);

        assertNotNull(sprite, "Sprite インスタンスが生成されること");
        assertEquals(100, sprite.getOriginalW(),           "originalW=100");
        assertEquals(50,  sprite.getOriginalH(),           "originalH=50");
        assertEquals(100, sprite.getImageW(),              "imageW=100（original と同じ）");
        assertEquals(50,  sprite.getImageH(),              "imageH=50（original と同じ）");
        assertEquals(Sprite.PIVOT_CENTER_CENTER, sprite.getPivotType(), "pivotType が CENTER_CENTER");
        // CENTER_CENTER: pivotX=w>>1=50, pivotY=h>>1=25
        assertEquals(50, sprite.getPivotX(), "pivotX=100>>1=50");
        assertEquals(25, sprite.getPivotY(), "pivotY=50>>1=25");
    }

    @Test
    public void testConstructorCenterBottom() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_BOTTOM);

        assertEquals(Sprite.PIVOT_CENTER_BOTTOM, sprite.getPivotType(), "pivotType が CENTER_BOTTOM");
        // CENTER_BOTTOM: pivotX=w>>1=50, pivotY=h-1=49
        assertEquals(50, sprite.getPivotX(), "pivotX=100>>1=50");
        assertEquals(49, sprite.getPivotY(), "pivotY=50-1=49（CENTERの25より下方）");
        // CENTER と BOTTOM で pivotY が異なること
        Sprite center = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);
        assertEquals(25, center.getPivotY(),  "CENTER pivotY=25");
        assertEquals(49, sprite.getPivotY(),  "BOTTOM pivotY=49 > CENTER の 25");
    }

    @Test
    public void testDefaultConstructor() {
        Sprite sprite = new Sprite();
        assertNotNull(sprite, "デフォルトコンストラクタで Sprite が生成されること");
        // デフォルト初期値: originalW/H=0, imageW/H=0
        assertEquals(0, sprite.getOriginalW(), "デフォルトの originalW は 0 であること");
        assertEquals(0, sprite.getOriginalH(), "デフォルトの originalH は 0 であること");
        assertEquals(0, sprite.getImageW(),    "デフォルトの imageW は 0 であること");
        assertEquals(0, sprite.getImageH(),    "デフォルトの imageH は 0 であること");
    }

    @Test
    public void testSetSpriteSize() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);

        sprite.setSpriteSize(200, 100);

        assertEquals(200, sprite.getImageW(),  "setSpriteSize後は imageW=200");
        assertEquals(100, sprite.getImageH(),  "setSpriteSize後は imageH=100");
        // ピボットが新しいサイズで再計算されること
        assertEquals(100, sprite.getPivotX(), "CENTER: 200>>1=100");
        assertEquals(50,  sprite.getPivotY(), "CENTER: 100>>1=50");
        // originalW/H は変化しないこと
        assertEquals(100, sprite.getOriginalW(), "setSpriteSize後も originalW は不変");
        assertEquals(50,  sprite.getOriginalH(), "setSpriteSize後も originalH は不変");
    }

    @Test
    public void testAddSpriteSize() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);

        sprite.addSpriteSize(20, 10);

        // original + delta でimage sizeが設定されること
        assertEquals(120, sprite.getImageW(), "addSpriteSize: 100+20=120");
        assertEquals(60,  sprite.getImageH(), "addSpriteSize: 50+10=60");
        // ピボットが新しいサイズで再計算されること
        assertEquals(60, sprite.getPivotX(), "CENTER: 120>>1=60");
        assertEquals(30, sprite.getPivotY(), "CENTER: 60>>1=30");
        // originalは変化しないこと
        assertEquals(100, sprite.getOriginalW(), "addSpriteSize後も originalW=100 のまま");
        assertEquals(50,  sprite.getOriginalH(), "addSpriteSize後も originalH=50 のまま");
    }

    @Test
    public void testCalcScreenRect() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);
        Point4y origin = new Point4y(500, 300);

        sprite.calcScreenRect(origin, 10, 5, 100, 50);

        Rectangle4y[] rects = sprite.getScreenRect();
        assertNotNull(rects);
        assertEquals(2, rects.length);

        // Left rect
        assertEquals(490, rects[0].getX()); // 500 - 10
        assertEquals(295, rects[0].getY()); // 300 - 5
        assertEquals(100, rects[0].getWidth());
        assertEquals(50, rects[0].getHeight());

        // Right rect
        assertEquals(510, rects[1].getX()); // 500 + 10
        assertEquals(295, rects[1].getY()); // 300 - 5
        assertEquals(-100, rects[1].getWidth()); // Negative for mirroring
        assertEquals(50, rects[1].getHeight());
    }

    @Test
    public void testSetPivotType() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);

        sprite.setPivotType(Sprite.PIVOT_CENTER_BOTTOM);
        assertEquals(Sprite.PIVOT_CENTER_BOTTOM, sprite.getPivotType(),
                "setPivotType 後は getPivotType() が新しい type を返すこと");

        // setPivotType 単独ではpivotは再計算されないが、size変更で新しいtypeが使われること
        sprite.setSpriteSize(80, 40);
        // PIVOT_CENTER_BOTTOM: pivotX=80>>1=40, pivotY=40-1=39
        assertEquals(40, sprite.getPivotX(), "BOTTOM type でサイズ変更後の pivotX が正しいこと");
        assertEquals(39, sprite.getPivotY(), "BOTTOM type でサイズ変更後の pivotY が正しいこと");
    }

    @Test
    public void testGettersSetters() {
        Sprite sprite = new Sprite();

        sprite.setOriginalW(150);
        assertEquals(150, sprite.getOriginalW(), "setOriginalW(150) → getOriginalW()=150");

        sprite.setOriginalH(75);
        assertEquals(75, sprite.getOriginalH(), "setOriginalH(75) → getOriginalH()=75");

        sprite.setImageW(200);
        assertEquals(200, sprite.getImageW(), "setImageW(200) → getImageW()=200");

        sprite.setImageH(100);
        assertEquals(100, sprite.getImageH(), "setImageH(100) → getImageH()=100");

        sprite.setPivotX(50);
        assertEquals(50, sprite.getPivotX(), "setPivotX(50) → getPivotX()=50");

        sprite.setPivotY(25);
        assertEquals(25, sprite.getPivotY(), "setPivotY(25) → getPivotY()=25");

        // originalW/H と imageW/H は独立しているため相互に影響しないこと
        assertEquals(150, sprite.getOriginalW(), "imageW を変更しても originalW が変化しないこと");
        assertEquals(75,  sprite.getOriginalH(), "imageH を変更しても originalH が変化しないこと");
    }

    @Test
    public void testPivotCalculationCenter() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);

        // CENTER: pivotX = w>>1, pivotY = h>>1
        sprite.setSpriteSize(200, 100);
        assertEquals(100, sprite.getPivotX(), "CENTER: 200>>1=100");
        assertEquals(50,  sprite.getPivotY(), "CENTER: 100>>1=50");

        sprite.setSpriteSize(50, 25);
        assertEquals(25, sprite.getPivotX(), "CENTER: 50>>1=25");
        assertEquals(12, sprite.getPivotY(), "CENTER: 25>>1=12");

        // 初期サイズ (100,50) に戻った場合の確認
        sprite.setSpriteSize(100, 50);
        assertEquals(50, sprite.getPivotX(), "CENTER: 100>>1=50");
        assertEquals(25, sprite.getPivotY(), "CENTER: 50>>1=25");
    }

    @Test
    public void testPivotCalculationBottom() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_BOTTOM);

        // BOTTOM: pivotX = w>>1, pivotY = h-1
        sprite.setSpriteSize(200, 100);
        assertEquals(100, sprite.getPivotX(), "BOTTOM: 200>>1=100");
        assertEquals(99,  sprite.getPivotY(), "BOTTOM: 100-1=99");

        sprite.setSpriteSize(50, 25);
        assertEquals(25, sprite.getPivotX(), "BOTTOM: 50>>1=25");
        assertEquals(24, sprite.getPivotY(), "BOTTOM: 25-1=24");

        // BOTTOM は CENTER より pivotY が大きい（下方）: (100,50) のとき BOTTOM=49 > CENTER=25
        sprite.setSpriteSize(100, 50);
        assertEquals(49, sprite.getPivotY(), "BOTTOM: pivotY=h-1=49 であること");
    }

    @Test
    public void testScreenRectLeftRight() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);
        Point4y origin = new Point4y(0, 0);

        sprite.calcScreenRect(origin, 0, 0, 50, 25);

        Rectangle4y[] rects = sprite.getScreenRect();

        // 左rect（正向き）と右rect（ミラー）の確認
        assertEquals(0,   rects[0].getX(),     "左rect の X が 0 であること");
        assertEquals(0,   rects[1].getX(),     "右rect の X が 0 であること（同じ起点）");
        assertEquals(50,  rects[0].getWidth(), "左rect の width が 50 であること");
        assertEquals(-50, rects[1].getWidth(), "右rect の width が -50（ミラー）であること");
        // 高さは同じであること
        assertEquals(rects[0].getHeight(), rects[1].getHeight(), "左右rect の height が同じであること");
    }

    @Test
    public void testOriginalVsImageSize() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);

        // setSpriteSize 前の original は変わらないこと
        assertEquals(100, sprite.getOriginalW(), "初期 originalW=100");
        assertEquals(50,  sprite.getOriginalH(), "初期 originalH=50");

        // imageサイズを変更
        sprite.setSpriteSize(200, 100);

        // originalは変化しないこと
        assertEquals(100, sprite.getOriginalW(), "setSpriteSize後も originalW=100 のままであること");
        assertEquals(50,  sprite.getOriginalH(), "setSpriteSize後も originalH=50 のままであること");
        // imageはサイズ変更されること
        assertEquals(200, sprite.getImageW(), "setSpriteSize後は imageW=200 に変わること");
        assertEquals(100, sprite.getImageH(), "setSpriteSize後は imageH=100 に変わること");
    }

    @Nested
    class RegressionScenarios {
        @Test
        public void testScenario_SetPivotTypeAloneDoesNotRecalculatePivotUntilSizeChanges() {
            Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);

            // setPivotType 単独ではpivot値は変化しないこと（CENTER_CENTERのまま）
            sprite.setPivotType(Sprite.PIVOT_CENTER_BOTTOM);
            assertEquals(Sprite.PIVOT_CENTER_BOTTOM, sprite.getPivotType(),
                    "setPivotType後はtypeが変わること");
            assertEquals(50, sprite.getPivotX(), "type変更直後はpivotXが変化しないこと");
            assertEquals(25, sprite.getPivotY(), "type変更直後はpivotYが変化しないこと（まだCENTERの値）");

            // setSpriteSize後はBOTTOMとして再計算されること
            sprite.setSpriteSize(120, 60);
            assertEquals(60, sprite.getPivotX(), "BOTTOM: 120>>1=60");
            assertEquals(59, sprite.getPivotY(), "BOTTOM: 60-1=59（CENTERなら30のはず）");
        }

        @Test
        public void testScenario_AddSpriteSizeAlwaysUsesOriginalSizeRatherThanAccumulating() {
            Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);

            sprite.addSpriteSize(20, 10);
            assertEquals(120, sprite.getImageW());
            assertEquals(60, sprite.getImageH());
            assertEquals(60, sprite.getPivotX());
            assertEquals(30, sprite.getPivotY());

            sprite.addSpriteSize(5, 5);

            assertEquals(105, sprite.getImageW());
            assertEquals(55, sprite.getImageH());
            assertEquals(52, sprite.getPivotX());
            assertEquals(27, sprite.getPivotY());
        }
    }
}
