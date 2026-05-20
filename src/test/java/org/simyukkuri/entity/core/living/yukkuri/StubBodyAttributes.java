package org.simyukkuri.entity.core.living.yukkuri;

import org.simyukkuri.draw.Point4y;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.YukkuriLayer;

public class StubBodyAttributes extends Yukkuri {
    private static final long serialVersionUID = 1L;

    private int expandSizeW = 0;
    private boolean noticeNoOkazariField = false;

    @Override
    public boolean isNoticeNoOkazari() {
        return noticeNoOkazariField;
    }

    public void setNoticeNoOkazari(boolean v) {
        this.noticeNoOkazariField = v;
    }

    @Override
    public YukkuriType getType() {
        return YukkuriType.TARINAI;
    }

    public void setExpandSizeW(int val) {
        this.expandSizeW = val;
    }

    @Override
    public int getExpandSizeW() {
        return expandSizeW;
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
    public int getImage(int type, int direction, YukkuriLayer layer, int index) {
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
    public int getNonYukkuriDiseaseTolerance() {
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

    @Override
    public String getBaseYukkuriFileName() {
        return baseBodyFileName;
    }

    @Override
    public void setBaseYukkuriFileName(String v) {
        baseBodyFileName = v;
    }

    @Override
    public String[] getBabyNames() {
        return babyNames;
    }

    @Override
    public void setBabyNames(String[] v) {
        babyNames = v;
    }

    @Override
    public String[] getChildNames() {
        return childNames;
    }

    @Override
    public void setChildNames(String[] v) {
        childNames = v;
    }

    @Override
    public String[] getAdultNames() {
        return adultNames;
    }

    @Override
    public void setAdultNames(String[] v) {
        adultNames = v;
    }

    @Override
    public String[] getMyNames() {
        return myNames;
    }

    @Override
    public void setMyNames(String[] v) {
        myNames = v;
    }

    @Override
    public String[] getBabyNamesDamaged() {
        return babyNamesDamaged;
    }

    @Override
    public void setBabyNamesDamaged(String[] v) {
        babyNamesDamaged = v;
    }

    @Override
    public String[] getChildNamesDamaged() {
        return childNamesDamaged;
    }

    @Override
    public void setChildNamesDamaged(String[] v) {
        childNamesDamaged = v;
    }

    @Override
    public String[] getAdultNamesDamaged() {
        return adultNamesDamaged;
    }

    @Override
    public void setAdultNamesDamaged(String[] v) {
        adultNamesDamaged = v;
    }

    @Override
    public String[] getMyNamesDamaged() {
        return myNamesDamaged;
    }

    @Override
    public void setMyNamesDamaged(String[] v) {
        myNamesDamaged = v;
    }
}
