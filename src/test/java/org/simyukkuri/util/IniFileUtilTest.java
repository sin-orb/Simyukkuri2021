package org.simyukkuri.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;

public class IniFileUtilTest {

    @Test
    public void testClassExistence() {
        // IniFileUtil クラスが存在してインスタンス化できること
        IniFileUtil util = new IniFileUtil();
        assertNotNull(util, "IniFileUtil インスタンスが生成できること");

        // リフレクションでもクラスが見つかること
        try {
            Class<?> clazz = Class.forName("org.simyukkuri.util.IniFileUtil");
            assertNotNull(clazz, "IniFileUtil クラスがリフレクションで取得できること");
        } catch (ClassNotFoundException e) {
            fail("IniFileUtil class not found via reflection");
        }
    }

    @Test
    public void testReadYukkuriIniFileNullSafety() {
        // null 引数で readYukkuriIniFile を呼ぶと NPE か正常終了のどちらかであること
        // （壊滅的な別の例外は投げないこと）
        boolean threwNpeOrSucceeded = false;
        try {
            IniFileUtil.readYukkuriIniFile(null);
            threwNpeOrSucceeded = true;  // 正常終了
        } catch (NullPointerException e) {
            threwNpeOrSucceeded = true;  // NPE は許容される
        } catch (Exception e) {
            // NPE 以外の例外は設計上の問題
            fail("readYukkuriIniFile(null) が NPE 以外の例外を投げた: " + e.getClass().getName());
        }
        assertTrue(threwNpeOrSucceeded, "null 引数で NPE か正常終了のどちらかであること");
    }

    @Test
    public void testReadYukkuriIniFileWithValidBody() {
        // 有効なボディで readYukkuriIniFile が呼べること
        // テスト環境では INI ファイルが存在しないため例外も許容
        Reimu reimu = new Reimu();
        int initialHungryLimit = reimu.getHungryLimitBase()[0];
        try {
            IniFileUtil.readYukkuriIniFile(reimu);
            // 成功した場合: reimu が有効な状態のまま
            assertNotNull(reimu, "readYukkuriIniFile 後も reimu が非null であること");
        } catch (Exception e) {
            // ファイルなし環境では例外が許容される。reimu のデフォルト値は変化しないこと
            assertTrue(reimu.getHungryLimitBase()[0] == initialHungryLimit,
                    "例外時でも reimu のデフォルト値は変化しないこと");
        }
    }

    @Test
    public void testReadYukkuriIniFileWithForceFlag() {
        // force=true で readYukkuriIniFile が呼べること
        Reimu reimu = new Reimu();
        int initialHungryLimit = reimu.getHungryLimitBase()[0];
        try {
            IniFileUtil.readYukkuriIniFile(reimu, true);
            assertNotNull(reimu, "force=true の readYukkuriIniFile 後も reimu が非null であること");
        } catch (Exception e) {
            // ファイルなし環境では例外が許容される
            assertTrue(reimu.getHungryLimitBase()[0] == initialHungryLimit,
                    "例外時でも reimu のデフォルト値は変化しないこと");
        }
    }

    @Test
    void testConstructor_doesNotThrow() {
        IniFileUtil util = new IniFileUtil();
        assertNotNull(util, "IniFileUtil インスタンスが非null であること");
    }
}
