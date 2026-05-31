package org.simyukkuri.ui;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.util.WorldTestHelper;

public class ShowStatusFrameTest {

    @AfterEach
    void tearDown() {
        MyPane.setSelectedYukkuri(null);
        WorldTestHelper.resetWorld();
    }

    @Test
    void testGiveBodyInfoUpdatesSelectBody() {
        WorldTestHelper.initializeMinimalWorld();
        Yukkuri b = WorldTestHelper.createBody();

        // ShowStatusFrame.giveYukkuriInfo のコアな副作用: MyPane.setSelectedYukkuri(body)
        MyPane.setSelectedYukkuri(b);
        assertSame(b, MyPane.getSelectedYukkuri(),
                "setSelectedYukkuri 後は getSelectedYukkuri が同一インスタンスを返すこと");

        // null に変更することで選択がクリアされること
        MyPane.setSelectedYukkuri(null);
        assertNull(MyPane.getSelectedYukkuri(),
                "null セットで選択ゆっくりがクリアされること");
    }
}
