package org.simyukkuri.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.HybridYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.system.YukkuriLayer;

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
        YukkuriLayer layer = new YukkuriLayer();

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
            public String getMyName() {
                return null;
            }

            @Override
            public String getNameJ() {
                return null;
            }

            @Override
            public String getNameE() {
                return null;
            }
        };

        // getMessage 内で name.isEmpty() が呼ばれて NPE にならないことを検証
        // テスト環境ではメッセージプールが未ロードのため null が返ることも許容される
        // ただし NPE を投げないこと（実際の回帰保証）
        String[] result = new String[1];
        assertDoesNotThrow(() -> {
            result[0] = MessagePool.getMessage(glitchyBody, MessagePool.Action.Birth);
        });
        // 戻り値は null か、非空文字列のいずれかであること（無効な文字列は返さないこと）
        assertTrue(result[0] == null || !result[0].isEmpty(),
                "getMessage の戻り値は null か空でない文字列であること");
    }

    /**
     * GameWorld が未初期化の状態でも Yukkuri の生成が成功することを検証
     */
    @Test
    public void testBody_Constructor_withoutGameWorld_shouldNotThrow() {
        // GameWorld.get() が null の状態を確実にする
        GameWorld.set(null);

        Reimu[] created = new Reimu[1];
        assertDoesNotThrow(() -> {
            created[0] = new Reimu(0, 0, 0, AgeState.BABY, null, null);
        });
        assertNotNull(created[0], "GameWorld=null でも Reimu インスタンスが生成されること");
        assertEquals(AgeState.BABY, created[0].getAgeState(),
                "生成された Reimu のAgeStateが BABY であること");
    }
}
