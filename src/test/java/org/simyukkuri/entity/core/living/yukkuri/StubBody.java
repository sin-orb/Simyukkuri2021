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

import org.simyukkuri.system.BodyLayer;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.YukkuriType;

public class StubBody extends Yukkuri {
    private static final long serialVersionUID = 1L;

    public StubBody() {
        super();
    }

    public StubBody(int initX, int initY, int initZ, AgeState initAgeState, Yukkuri mama, Yukkuri papa) {
        super(initX, initY, initZ, initAgeState, mama, papa);
    }

    @Override
    public YukkuriType getType() {
        return YukkuriType.TARINAI;
    }

    @Override
    public String getNameJ() {
        return "TestJ";
    }

    @Override
    public String getNameE() {
        return "TestE";
    }

    @Override
    public String getNameJ2() {
        return "";
    }

    @Override
    public String getNameE2() {
        return "";
    }

    @Override
    public String getMyName() {
        return "MyTest";
    }

    @Override
    public String getMyNameD() {
        return "MyTestD";
    }

    @Override
    public int getImage(int type, int direction, BodyLayer layer, int index) {
        return 0;
    }

    @Override
    public void tuneParameters() {
    }

    @Override
    public boolean isImageLoaded() {
        return true;
    }

    @Override
    public Point4y[] getMountPoint(String key) {
        return null;
    }

    @Override
    public int getPivotX() {
        return 0;
    }

    @Override
    public int getPivotY() {
        return 0;
    }

    @Override
    public int checkNonYukkuriDiseaseTolerance() {
        return 0;
    }
}
