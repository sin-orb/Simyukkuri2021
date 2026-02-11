package src.base;

import src.system.BodyLayer;
import src.draw.Point4y;
import src.enums.Happiness;

public class PlainBodyAttributes extends BodyAttributes {
    private static final long serialVersionUID = 1L;

    @Override
    public int getType() {
        return 0;
    }

    public void forceSetHappiness(Happiness h) {
        try {
            java.lang.reflect.Field f = BodyAttributes.class.getDeclaredField("happiness");
            f.setAccessible(true);
            f.set(this, h);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
}
