package org.simyukkuri.system;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.field.impl.Farm;
import org.simyukkuri.field.impl.Pool;

/**
 * フィールドの地形データ（壁マップ・地形マップ）と、バリア・水場・農場などの
 * フィールド上構造物を管理するリポジトリ。
 * {@link WorldState} の一部として保持される。
 */
public class FieldRepository implements Serializable {

    private static final long serialVersionUID = -1450923786541209348L;

    private int[][] wallMap;
    private int[][] fieldMap;
    private List<Barrier> barrier = new LinkedList<>();
    private List<Pool> pool = new LinkedList<>();
    private List<Farm> farm = new LinkedList<>();

    /**
     * デフォルトコンストラクタ。Jackson デシリアライズ用。
     */
    public FieldRepository() {
    }

    /**
     * 壁の配置を示す二次元グリッドを返す。{@code wallMap[x][y]} が非ゼロなら壁。
     *
     * @return 壁マップ（[x][y]）
     */
    public int[][] getWallGrid() {
        return wallMap;
    }

    /**
     * 壁マップをセットする。
     *
     * @param wallMap 新しい壁マップ
     */
    public void setWallGrid(int[][] wallMap) {
        this.wallMap = wallMap;
    }

    /**
     * 地形の種類を示す二次元グリッドを返す。{@code fieldMap[x][y]} の値で地形タイプを識別する。
     *
     * @return 地形マップ（[x][y]）
     */
    public int[][] getFieldGrid() {
        return fieldMap;
    }

    /**
     * 地形マップをセットする。
     *
     * @param fieldMap 新しい地形マップ
     */
    public void setFieldGrid(int[][] fieldMap) {
        this.fieldMap = fieldMap;
    }

    /**
     * ゆっくりの移動を遮るバリアのリストを返す。
     *
     * @return バリアリスト
     */
    public List<Barrier> getBarriers() {
        return barrier;
    }

    /**
     * バリアリストをセットする（主にデシリアライズ時に使用）。
     *
     * @param barrier 新しいバリアリスト
     */
    public void setBarriers(List<Barrier> barrier) {
        this.barrier = barrier;
    }

    /**
     * ゆっくりが溺れる水場のリストを返す。
     *
     * @return 水場リスト
     */
    public List<Pool> getPools() {
        return pool;
    }

    /**
     * 水場リストをセットする（主にデシリアライズ時に使用）。
     *
     * @param pool 新しい水場リスト
     */
    public void setPools(List<Pool> pool) {
        this.pool = pool;
    }

    /**
     * 農場のリストを返す。ゆっくりが食料を自給できる農場エリア。
     *
     * @return 農場リスト
     */
    public List<Farm> getFarms() {
        return farm;
    }

    /**
     * 農場リストをセットする（主にデシリアライズ時に使用）。
     *
     * @param farm 新しい農場リスト
     */
    public void setFarms(List<Farm> farm) {
        this.farm = farm;
    }
}
