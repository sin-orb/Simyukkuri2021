package org.simyukkuri.entity.core.living.yukkuri;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.World;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.util.WorldTestHelper;
import org.simyukkuri.system.Sprite;
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
            b.getSpriteSet()[i] = new Sprite();
            b.getSpriteSet()[i].setImageW(100);
            b.getSpriteSet()[i].setImageH(100);
            b.getExpandSpr()[i] = new Sprite();
            b.getBraidSpr()[i] = new Sprite();
        }
        b.setAgeState(age);
        b.setMsgType(YukkuriType.REIMU);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(b.getUniqueID(), b);
        return b;
    }

    private void prepareRelaxBase(StubBody target) {
        target.setAgeState(AgeState.ADULT);
        target.setNoHungryPeriod(target.getRelaxPeriodBase() + 1);
        target.setNoDamagePeriod(target.getRelaxPeriodBase() + 1);
        target.setSleeping(false);
        target.setShitting(false);
        target.setEating(false);
        target.setHappiness(Happiness.AVERAGE);
        target.setCriticalDamege(null);
        target.setExciting(false);
        target.setExciteProb(1);
        target.setHungry(target.getHungryLimitBase()[target.getAgeState().ordinal()]);
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
        body.setCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
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

    private void invokeCheckNonYukkuriDisease(Yukkuri b) {
        try {
            java.lang.reflect.Method m = Yukkuri.class.getDeclaredMethod("hasNonYukkuriDisease");
            m.setAccessible(true);
            m.invoke(b);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
