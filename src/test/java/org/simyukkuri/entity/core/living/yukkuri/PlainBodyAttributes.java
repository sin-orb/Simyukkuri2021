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
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.YukkuriType;

public class PlainBodyAttributes extends Yukkuri {
    private static final long serialVersionUID = 1L;

    @Override
    public YukkuriType getType() {
        return YukkuriType.TARINAI;
    }

    public void forceSetHappiness(Happiness h) {
        happiness = h;
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
    public int checkNonYukkuriDiseaseTolerance() {
        return 0;
    }

    @Override
    public int getExpandSizeW() {
        return 0;
    }

    @Override
    public int getExpandSizeH() {
        return 0;
    }

    private String baseBodyFileName;
    private String[] babyNames;
    private String[] childNames;
    private String[] adultNames;
    private String[] myNames = new String[3];
    private String[] babyNamesDamaged;
    private String[] childNamesDamaged;
    private String[] adultNamesDamaged;
    private String[] myNamesDamaged = new String[3];

    @Override public String getBaseBodyFileName() { return baseBodyFileName; }
    @Override public void setBaseBodyFileName(String v) { baseBodyFileName = v; }
    @Override public String[] getBabyNames() { return babyNames; }
    @Override public void setBabyNames(String[] v) { babyNames = v; }
    @Override public String[] getChildNames() { return childNames; }
    @Override public void setChildNames(String[] v) { childNames = v; }
    @Override public String[] getAdultNames() { return adultNames; }
    @Override public void setAdultNames(String[] v) { adultNames = v; }
    @Override public String[] getMyNames() { return myNames; }
    @Override public void setMyNames(String[] v) { myNames = v; }
    @Override public String[] getBabyNamesDamaged() { return babyNamesDamaged; }
    @Override public void setBabyNamesDamaged(String[] v) { babyNamesDamaged = v; }
    @Override public String[] getChildNamesDamaged() { return childNamesDamaged; }
    @Override public void setChildNamesDamaged(String[] v) { childNamesDamaged = v; }
    @Override public String[] getAdultNamesDamaged() { return adultNamesDamaged; }
    @Override public void setAdultNamesDamaged(String[] v) { adultNamesDamaged = v; }
    @Override public String[] getMyNamesDamaged() { return myNamesDamaged; }
    @Override public void setMyNamesDamaged(String[] v) { myNamesDamaged = v; }
}
