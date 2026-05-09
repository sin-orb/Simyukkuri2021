package src.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.base.Yukkuri;
import src.yukkuri.HybridYukkuri;
import src.yukkuri.Reimu;
import src.system.MessagePool;
import src.system.BodyLayer;
import src.enums.AgeState;

/**
 * 過去に報告された NullPointerException (NPE) の再発を防止するためのテスト
 */
public class StabilityNPETest {

    /**
     * ハイブリッドゆっくりの画像配列が不完全な場合でもクラッシュしないことを検証
     */
    @Test
    public void testHybridYukkuri_getImage_withNullElements_shouldNotThrow() {
        HybridYukkuri hybrid = new HybridYukkuri();
        BodyLayer layer = new BodyLayer();
        
        // 1. images 配列自体が null の場合
        hybrid.setImages(null);
        assertDoesNotThrow(() -> hybrid.getImage(0, 0, layer, 0));
        assertEquals(0, hybrid.getImage(0, 0, layer, 0));

        // 2. 配列はあるが要素が null の場合
        hybrid.setImages(new Yukkuri[100]);
        assertDoesNotThrow(() -> hybrid.getImage(0, 0, layer, 0));
        assertEquals(0, hybrid.getImage(0, 0, layer, 0));
    }

    /**
     * 名前の取得に失敗する場合（nullを返す場合）でもメッセージ取得が成功することを検証
     */
    @Test
    public void testMessagePool_getMessage_withNullName_shouldNotThrow() {
        // Yukkuri のサブクラスを作成し、意図的に null 名前を返させる
        Yukkuri glitchyBody = new Reimu() {
            @Override
            public String getMyName() { return null; }
            @Override
            public String getNameJ() { return null; }
            @Override
            public String getNameE() { return null; }
        };

        // getMessage 内で name.isEmpty() が呼ばれて NPE にならないことを検証
        assertDoesNotThrow(() -> {
            MessagePool.getMessage(glitchyBody, MessagePool.Action.Birth);
        });
    }

    /**
     * GameWorld が未初期化の状態でも Yukkuri の生成が成功することを検証
     */
    @Test
    public void testBody_Constructor_withoutGameWorld_shouldNotThrow() {
        // GameWorld.get() が null の状態を確実にする
        GameWorld.set(null);
        
        assertDoesNotThrow(() -> {
            new Reimu(0, 0, 0, AgeState.BABY, null, null);
        });
    }
}
