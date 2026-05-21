package org.simyukkuri.system;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;

/**
 * 摂食・排泄に関わるゲーム内オブジェクト（食べ物・フン・嘔吐物）を管理するリポジトリ。
 * {@link WorldState} の一部として保持され、Jackson でセーブ／ロードされる。
 */
public class FoodRepository implements Serializable {

    private static final long serialVersionUID = 3812745690234571823L;

    private Map<Integer, Food> food = new HashMap<>();
    private Map<Integer, Food> takenOutFood = new HashMap<>();
    private Map<Integer, Shit> shit = new HashMap<>();
    private Map<Integer, Shit> takenOutShit = new HashMap<>();
    private Map<Integer, Vomit> vomit = new HashMap<>();

    /**
     * デフォルトコンストラクタ。Jackson デシリアライズ用。
     */
    public FoodRepository() {
    }

    /**
     * フィールド上に置かれた食べ物マップを返す。キーは objId。
     *
     * @return 食べ物マップ
     */
    public Map<Integer, Food> getFoods() {
        return food;
    }

    /**
     * 食べ物マップをセットする（主にデシリアライズ時に使用）。
     *
     * @param food 新しい食べ物マップ
     */
    public void setFoods(Map<Integer, Food> food) {
        this.food = food;
    }

    /**
     * プレイヤーが「取り出した」状態の食べ物マップを返す。キーは objId。
     *
     * @return 取り出し中の食べ物マップ
     */
    public Map<Integer, Food> getTakenOutFoods() {
        return takenOutFood;
    }

    /**
     * 取り出し中の食べ物マップをセットする（主にデシリアライズ時に使用）。
     *
     * @param takenOutFood 新しい取り出し食べ物マップ
     */
    public void setTakenOutFoods(Map<Integer, Food> takenOutFood) {
        this.takenOutFood = takenOutFood;
    }

    /**
     * フィールド上のフンマップを返す。キーは objId。
     *
     * @return フンマップ
     */
    public Map<Integer, Shit> getShit() {
        return shit;
    }

    /**
     * フンマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param shit 新しいフンマップ
     */
    public void setShit(Map<Integer, Shit> shit) {
        this.shit = shit;
    }

    /**
     * プレイヤーが「取り出した」状態のフンマップを返す。キーは objId。
     *
     * @return 取り出し中のフンマップ
     */
    public Map<Integer, Shit> getTakenOutShits() {
        return takenOutShit;
    }

    /**
     * 取り出し中のフンマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param takenOutShit 新しい取り出しフンマップ
     */
    public void setTakenOutShits(Map<Integer, Shit> takenOutShit) {
        this.takenOutShit = takenOutShit;
    }

    /**
     * フィールド上の嘔吐物マップを返す。キーは objId。
     *
     * @return 嘔吐物マップ
     */
    public Map<Integer, Vomit> getVomit() {
        return vomit;
    }

    /**
     * 嘔吐物マップをセットする（主にデシリアライズ時に使用）。
     *
     * @param vomit 新しい嘔吐物マップ
     */
    public void setVomit(Map<Integer, Vomit> vomit) {
        this.vomit = vomit;
    }
}
