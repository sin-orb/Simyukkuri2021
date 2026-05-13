package src;



import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

import java.util.Random;

/**
 * テスト用Randomサブクラス。
 * nextInt()/nextBoolean()が確定値を返す。
 * SimYukkuri.RND に差し込んで使う。
 */
public class ConstState extends Random {
    private int fixedInt = 0;
    private boolean fixedBoolean = false;

    public ConstState() {}

    public ConstState(int fixedInt) {
        this.fixedInt = fixedInt;
    }

    public void setFixedInt(int v) { this.fixedInt = v; }
    public void setFixedBoolean(boolean v) { this.fixedBoolean = v; }

    @Override
    public int nextInt(int bound) {
        return Math.min(fixedInt, bound - 1);
    }

    @Override
    public boolean nextBoolean() {
        return fixedBoolean;
    }
}
