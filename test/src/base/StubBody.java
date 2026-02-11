
package src.base;

import src.system.BodyLayer;
import src.draw.Point4y;
import src.enums.AgeState;

public class StubBody extends Body {
    private static final long serialVersionUID = 1L;

    public StubBody() {
        super();
    }

    public StubBody(int initX, int initY, int initZ, AgeState initAgeState, Body mama, Body papa) {
        super(initX, initY, initZ, initAgeState, mama, papa);
    }

    @Override
    public int getType() {
        return 0;
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
