package src.logic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import src.SimYukkuri;
import src.base.Body;
import src.base.Okazari;
import src.draw.Translate;
import src.item.Trash;
import src.util.WorldTestHelper;
import src.yukkuri.Marisa;

import java.lang.reflect.Field;

/**
 * Test class for TrashLogic.
 *
 * TrashLogic has two methods:
 *   checkTrashOkazari(Body b) - public
 *   searchTrashObj(Body b)    - private (tested indirectly via checkTrashOkazari)
 */
public class TrashLogicTest {

    private Body body;

    // WorldTestHelper が new World(0,0) を呼び出すため、
    // wallMap のサイズは DEFAULT_MAP_X[0]*fieldScaleData[0]/100 + 1 = 151x151 に固定される
    // （Translate.setMapSize を後で変えても wallMap はリサイズされない）
    // そのため body と Trash の座標はすべて 0〜149 の範囲内に収める
    private static final int MAP_LIMIT = 140;

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();

        body = WorldTestHelper.createBody();
        body.setX(50);
        body.setY(50);
        body.setZ(0);

        // Ensure no okazari
        body.setOkazari(null);

        SimYukkuri.world.getCurrentMap().getBody().put(body.getObjId(), body);
    }

    @AfterEach
    void tearDown() {
        WorldTestHelper.resetWorld();
    }

    // ---------------------------------------------------------------
    // hasOkazari() == true のとき即 false を返す
    // ---------------------------------------------------------------
    @Test
    void testCheckTrashOkazari_HasOkazari_ReturnsFalse() {
        // setOkazari で okazari を持たせる（hasOkazari() == true）
        Okazari okazari = new Okazari();
        body.setOkazari(okazari);
        assertTrue(body.hasOkazari(), "前提: okazari を持っていること");

        boolean result = TrashLogic.checkTrashOkazari(body);

        assertFalse(result, "hasOkazari() == true のとき false を返すべき");
    }

    // ---------------------------------------------------------------
    // hasOkazari() == false かつ Trash なし -> false を返す
    // ---------------------------------------------------------------
    @Test
    void testCheckTrashOkazari_NoOkazari_NoTrash_ReturnsFalse() {
        assertFalse(body.hasOkazari(), "前提: okazari を持っていないこと");
        assertTrue(SimYukkuri.world.getCurrentMap().getTrash().isEmpty(), "前提: Trash リストが空");

        boolean result = TrashLogic.checkTrashOkazari(body);

        assertFalse(result, "Trash がないとき false を返すべき");
    }

    // ---------------------------------------------------------------
    // hasOkazari() == false かつ Trash が視野内にある -> true を返す + イベント追加
    // ---------------------------------------------------------------
    @Test
    void testCheckTrashOkazari_NoOkazari_TrashNearby_ReturnsTrue() {
        assertFalse(body.hasOkazari());

        // body=(50,50) のすぐ近くに Trash を配置（wallMap 範囲内）
        Trash trash = new Trash(55, 55, 0);

        // 視野範囲は EYESIGHTorg = 4000*4000 (デフォルト) なので 5 ピクセル距離は十分近い
        assertTrue(SimYukkuri.world.getCurrentMap().getTrash().containsKey(trash.getObjId()));

        boolean result = TrashLogic.checkTrashOkazari(body);

        assertTrue(result, "視野内に Trash があるとき true を返すべき");
        // GetTrashOkazariEvent が addBodyEvent で body のイベントリストに追加される
        assertFalse(body.getEventList().isEmpty(), "イベントが body に追加されるべき");
    }

    // ---------------------------------------------------------------
    // Trash が視野外にある（EYESIGHTorg を 0 にする）-> false を返す
    // ---------------------------------------------------------------
    @Test
    void testCheckTrashOkazari_TrashOutOfSight_ReturnsFalse() {
        assertFalse(body.hasOkazari());

        // EYESIGHTorg を 0 に設定 → searchTrashObj で minDistance=0 なので見つからない
        setEYESIGHTorg(body, 0);

        Trash trash = new Trash(55, 55, 0);

        boolean result = TrashLogic.checkTrashOkazari(body);

        assertFalse(result, "視野外の Trash はスキップされ false を返すべき");
    }

    // ---------------------------------------------------------------
    // Trash が body と全く同じ位置（距離 0）-> 見つかる
    // ---------------------------------------------------------------
    @Test
    void testCheckTrashOkazari_TrashAtSamePosition_ReturnsTrue() {
        assertFalse(body.hasOkazari());

        // body と同じ座標に Trash を置く（距離 = 0）
        Trash trash = new Trash(50, 50, 0);

        boolean result = TrashLogic.checkTrashOkazari(body);

        // 距離 0 は minDistance より小さい場合のみ採用される
        // minDistance 初期値 = EYESIGHTorg = 4000*4000 >> 大きい -> 0 < minDistance なので採用される
        assertTrue(result, "同位置の Trash は視野内として true を返すべき");
    }

    // ---------------------------------------------------------------
    // Trash が複数あるとき、最も近いものが見つかる（result は true）
    // ---------------------------------------------------------------
    @Test
    void testCheckTrashOkazari_MultipleTrash_ReturnsTrue() {
        assertFalse(body.hasOkazari());

        // body=(50,50) 近辺に2つ置く（wallMap範囲内）
        Trash trash1 = new Trash(55, 55, 0);
        Trash trash2 = new Trash(60, 60, 0);

        boolean result = TrashLogic.checkTrashOkazari(body);

        assertTrue(result, "複数 Trash がある場合も true を返すべき");
    }

    // ---------------------------------------------------------------
    // Trash を削除してから呼ぶと false
    // ---------------------------------------------------------------
    @Test
    void testCheckTrashOkazari_TrashRemovedFromWorld_ReturnsFalse() {
        assertFalse(body.hasOkazari());
        // ワールドにTrashを入れず、直接登録もしない
        assertTrue(SimYukkuri.world.getCurrentMap().getTrash().isEmpty());

        boolean result = TrashLogic.checkTrashOkazari(body);

        assertFalse(result);
    }

    // ---------------------------------------------------------------
    // checkTrashOkazari の呼び出し後、hasOkazari は変わらない
    // （イベント登録は行われるが okazari の付与はイベント処理後のため）
    // ---------------------------------------------------------------
    @Test
    void testCheckTrashOkazari_HasOkazariUnchangedAfterCall() {
        assertFalse(body.hasOkazari());

        Trash trash = new Trash(55, 55, 0);
        TrashLogic.checkTrashOkazari(body);

        // checkTrashOkazari 自体は okazari を変更しない
        assertFalse(body.hasOkazari(), "checkTrashOkazari は okazari を直接変更しない");
    }

    // ---------------------------------------------------------------
    // メソッドシグネチャの確認
    // ---------------------------------------------------------------
    @Test
    void testCheckTrashOkazariMethodExists() {
        try {
            TrashLogic.class.getDeclaredMethod("checkTrashOkazari", Body.class);
            assertTrue(true, "checkTrashOkazari メソッドが存在する");
        } catch (NoSuchMethodException e) {
            fail("checkTrashOkazari メソッドが存在しない");
        }
    }

    @Test
    void testSearchTrashObjMethodExists() {
        try {
            java.lang.reflect.Method m = TrashLogic.class.getDeclaredMethod("searchTrashObj", Body.class);
            assertNotNull(m);
            // private メソッドであることを確認
            assertTrue(java.lang.reflect.Modifier.isPrivate(m.getModifiers()),
                    "searchTrashObj は private であるべき");
        } catch (NoSuchMethodException e) {
            fail("searchTrashObj メソッドが存在しない");
        }
    }

    // ---------------------------------------------------------------
    // 複数回呼び出しても安定して動作する
    // ---------------------------------------------------------------
    @Test
    void testCheckTrashOkazari_CalledTwice_NoTrash() {
        boolean r1 = TrashLogic.checkTrashOkazari(body);
        boolean r2 = TrashLogic.checkTrashOkazari(body);
        assertFalse(r1);
        assertFalse(r2);
    }

    @Test
    void testCheckTrashOkazari_CalledTwice_WithTrash() {
        Trash trash = new Trash(55, 55, 0);

        boolean r1 = TrashLogic.checkTrashOkazari(body);
        assertTrue(r1);

        // 2回目: body のイベントリストにすでにイベントが入っているが、
        // hasOkazari() は false のまま → また true が返るはず
        boolean r2 = TrashLogic.checkTrashOkazari(body);
        assertTrue(r2);
    }

    // ---------------------------------------------------------------
    // 別のゆっくりでも動作する
    // ---------------------------------------------------------------
    @Test
    void testCheckTrashOkazari_DifferentBody() {
        Body body2 = new Marisa();
        body2.setX(30);
        body2.setY(30);
        body2.setOkazari(null);
        SimYukkuri.world.getCurrentMap().getBody().put(body2.getObjId(), body2);

        // body2 近くに Trash を配置（wallMap 範囲内）
        Trash trash = new Trash(35, 35, 0);

        boolean result = TrashLogic.checkTrashOkazari(body2);
        assertTrue(result);
    }

    // ---------------------------------------------------------------
    // hasOkazari が true の Body に対して複数 Trash があっても false を返す
    // ---------------------------------------------------------------
    @Test
    void testCheckTrashOkazari_HasOkazari_MultipleTrash_ReturnsFalse() {
        Okazari okazari = new Okazari();
        body.setOkazari(okazari);

        new Trash(55, 55, 0);
        new Trash(60, 60, 0);

        boolean result = TrashLogic.checkTrashOkazari(body);
        assertFalse(result);
    }

    // ---------------------------------------------------------------
    // EYESIGHTorg が負の場合、minDistance < 1 の判定で即ブレーク -> false
    // ---------------------------------------------------------------
    @Test
    void testCheckTrashOkazari_NegativeEyesight_ReturnsFalse() {
        assertFalse(body.hasOkazari());
        setEYESIGHTorg(body, -1);

        Trash trash = new Trash(55, 55, 0);

        boolean result = TrashLogic.checkTrashOkazari(body);
        assertFalse(result, "minDistance < 1 の場合、即ブレークして false を返すべき");
    }

    // ---------------------------------------------------------------
    // Trash が遠くにあり、かつ EYESIGHTorg がちょうど境界値の場合
    // body=(50,50), trash=(60,50)
    // distance = 10*10 = 100
    // ---------------------------------------------------------------
    @Test
    void testCheckTrashOkazari_TrashExactlyAtEyesight() {
        assertFalse(body.hasOkazari());

        // body=(50,50), trash=(60,50) -> distance = 100
        Trash trash = new Trash(60, 50, 0);

        // EYESIGHTorg = 101 (distance=100 < 101 なので見つかる)
        setEYESIGHTorg(body, 101);

        boolean result = TrashLogic.checkTrashOkazari(body);
        assertTrue(result, "EYESIGHTorg > distance の場合、Trash を見つけて true を返すべき");
    }

    @Test
    void testCheckTrashOkazari_TrashJustBeyondEyesight() {
        assertFalse(body.hasOkazari());

        // body=(50,50), trash=(60,50) -> distance = 100
        Trash trash = new Trash(60, 50, 0);

        // EYESIGHTorg = 100 (distance == minDistance なので採用されない: minDistance > distance が必要)
        setEYESIGHTorg(body, 100);

        boolean result = TrashLogic.checkTrashOkazari(body);
        // minDistance > distance は false なので見つからない
        assertFalse(result, "EYESIGHTorg == distance のとき Trash は視野ぎりぎり外なので false");
    }

    // ---------------------------------------------------------------
    // Helper: EYESIGHTorg フィールドをリフレクションで設定
    // ---------------------------------------------------------------
    private void setEYESIGHTorg(Body body, int value) {
        try {
            Field field = findField(body.getClass(), "EYESIGHTorg");
            if (field != null) {
                field.setAccessible(true);
                field.setInt(body, value);
            }
        } catch (Exception e) {
            fail("EYESIGHTorg の設定に失敗: " + e.getMessage());
        }
    }

    private Field findField(Class<?> clazz, String name) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}
