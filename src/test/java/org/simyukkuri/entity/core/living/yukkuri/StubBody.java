package org.simyukkuri.entity.core.living.yukkuri;

import java.util.HashMap;
import java.util.Map;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.YukkuriLayer;

public class StubBody extends Yukkuri {
    private static final long serialVersionUID = 1L;
    private static final Map<String, Point4y[]> MOUNT_POINTS = createMountPoints();

    private static Map<String, Point4y[]> createMountPoints() {
        Map<String, Point4y[]> map = new HashMap<>();
        Point4y[] points = new Point4y[] {
                new Point4y(1, 4),
                new Point4y(2, 5),
                new Point4y(3, 6),
        };
        map.put("AccelAmpoule", points);
        map.put("AnydAmpoule", points);
        map.put("Ants", points);
        map.put("Badge", points);
        map.put("BreedingAmpoule", points);
        map.put("Fire", points);
        map.put("HungryAmpoule", points);
        map.put("Needle", points);
        map.put("Needle_In_Anal", points);
        map.put("OrangeAmpoule", points);
        map.put("PoisonAmpoule", points);
        map.put("StopAmpoule", points);
        map.put("VeryShitAmpoule", points);
        return map;
    }

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
    public void reinitializeBoundary() {}

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
        return MOUNT_POINTS.get(key);
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
    public int getNonYukkuriDiseaseTolerance() {
        return 0;
    }
}
