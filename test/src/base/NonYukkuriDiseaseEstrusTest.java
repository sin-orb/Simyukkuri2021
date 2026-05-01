package src.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.SimYukkuri;
import src.draw.World;
import src.enums.AgeState;
import src.enums.CoreAnkoState;
import src.enums.Happiness;
import src.enums.YukkuriType;
import src.util.WorldTestHelper;
import src.system.Sprite;
import java.util.Random;

/**
 * 非ゆっくり症の個体が発情しないことを検証するテスト.
 */
public class NonYukkuriDiseaseEstrusTest {

    private StubBody body;

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetStates();
        WorldTestHelper.initializeLoadedMessagePool(NonYukkuriDiseaseEstrusTest.class.getClassLoader());
        SimYukkuri.RND = new Random();
        SimYukkuri.world = new World();
        body = createBody(AgeState.ADULT);
    }

    private StubBody createBody(AgeState age) {
        StubBody b = new StubBody();
        for (int i = 0; i < 3; i++) {
            b.bodySpr[i] = new Sprite();
            b.bodySpr[i].setImageW(100);
            b.bodySpr[i].setImageH(100);
            b.expandSpr[i] = new Sprite();
            b.braidSpr[i] = new Sprite();
        }
        b.setAgeState(age);
        b.setMsgType(YukkuriType.REIMU);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        return b;
    }

    private void prepareRelaxBase(StubBody target) {
        target.setAgeState(AgeState.ADULT);
        target.setNoHungryPeriod(target.getRELAXPERIODorg() + 1);
        target.setNoDamagePeriod(target.getRELAXPERIODorg() + 1);
        target.setSleeping(false);
        target.setShitting(false);
        target.setEating(false);
        target.setHappiness(Happiness.AVERAGE);
        target.setCriticalDamege(null);
        target.setExciting(false);
        target.setExciteProb(1);
        target.setHungry(target.getHUNGRYLIMITorg()[target.getBodyAgeState().ordinal()]);
        target.setShit(0);
        target.setHasBraid(true);
        target.setDirty(false);
    }

    @Test
    public void testNYDDoesNotEnterEstrus() {
        prepareRelaxBase(body);
        // 通常であれば発情する条件を整える
        body.setPartner(-1); // 独身
        
        // 非ゆっくり症を設定
        body.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        assertTrue(body.isNYD());
        
        // 感情チェック実行
        body.checkEmotion();
        
        // 発情していないことを確認
        assertFalse(body.isExciting(), "非ゆっくり症の個体は発情してはいけません");
    }

    @Test
    public void testEstrusClearedWhenNYDDevelops() {
        // 最初は発情している
        body.setExciting(true);
        assertTrue(body.isExciting());
        
        // ストレスを上げて非ゆっくり症を発症させる
        body.setStress(10000); 
        
        // 非ゆっくり症の判定処理を実行
        invokeCheckNonYukkuriDisease(body);
        
        // 非ゆっくり症になり、かつ発情が解除されていることを確認
        assertTrue(body.isNYD());
        assertFalse(body.isExciting(), "非ゆっくり症を発症した際に発情状態が解除されるべきです");
    }

    private void invokeCheckNonYukkuriDisease(Body b) {
        try {
            java.lang.reflect.Method m = Body.class.getDeclaredMethod("checkNonYukkuriDisease");
            m.setAccessible(true);
            m.invoke(b);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
