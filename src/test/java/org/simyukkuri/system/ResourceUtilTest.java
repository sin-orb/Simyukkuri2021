package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.simyukkuri.util.GameLocale;

/**
 * Test class for ResourceUtil.
 */
public class ResourceUtilTest {

    @Test
    public void testGetInstance() {
        ResourceUtil instance1 = ResourceUtil.getInstance();
        ResourceUtil instance2 = ResourceUtil.getInstance();
        assertNotNull(instance1);
        assertSame(instance1, instance2, "シングルトンは常に同一インスタンスを返すこと");
        // シングルトンが使用可能な状態であること（コア属性が読める）
        assertNotNull(instance1.read("title"), "シングルトン取得後すぐに title を読み取れること");
    }

    @Test
    public void testReadProperty() {
        ResourceUtil ru = ResourceUtil.getInstance();

        // title と version はコアプロパティとして常に存在すること
        String title = ru.read("title");
        assertNotNull(title, "title プロパティは null でないこと");
        assertNotEquals("title", title, "title はキーそのものを返さないこと");
        assertTrue(title.length() > 0, "title は空でないこと");

        String version = ru.read("version");
        assertNotNull(version, "version プロパティは null でないこと");
        assertFalse(version.isEmpty(), "version は空でないこと");
        // version は数字とドットを含む形式であること
        assertTrue(version.matches(".*\\d.*"), "version は数字を含む形式であること");
    }

    @Test
    public void testReadNonExistentProperty() {
        ResourceUtil ru = ResourceUtil.getInstance();
        // 存在しないキーは null を返すこと
        String value = ru.read("non.existent.key.12345");
        assertNull(value, "存在しないキーは null を返すこと");
        // 存在するキーでは null でないことの対比確認
        assertNotNull(ru.read("title"), "存在するキー(title)は null でないこと");
    }

    @Test
    public void testGameLocaleIsJapanese() {
        assertNotNull(GameLocale.getLocale(), "GameLocale.getLocale() は null でないこと");
        boolean isJp = GameLocale.isJapanese();
        // isJapanese() は getLocale の言語が日本語かどうかと一致すること
        assertEquals(Locale.JAPANESE.getLanguage().equals(GameLocale.getLocale().getLanguage()), isJp,
                "isJapanese() は getLocale の言語と整合していること");
    }
}
