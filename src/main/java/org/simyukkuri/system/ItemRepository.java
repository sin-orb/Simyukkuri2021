package org.simyukkuri.system;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.simyukkuri.entity.core.world.bodylinked.Okazari;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.item.Bed;
import org.simyukkuri.entity.core.world.item.BreedingPool;
import org.simyukkuri.entity.core.world.item.House;
import org.simyukkuri.entity.core.world.item.Stone;
import org.simyukkuri.entity.core.world.item.Sui;
import org.simyukkuri.entity.core.world.item.Toilet;
import org.simyukkuri.entity.core.world.item.Toy;
import org.simyukkuri.entity.core.world.item.Trampoline;
import org.simyukkuri.entity.core.world.item.Trash;

/**
 * トイレ・おもちゃ・小石・酢・ベッド・トランポリン・家・ゴミ箱・おかざり・交配池・茎など、
 * ゆっくりが直接関わるフィールドアイテムを管理するリポジトリ。
 * {@link WorldState} の一部として保持され、Jackson でセーブ／ロードされる。
 */
public class ItemRepository implements Serializable {

    private static final long serialVersionUID = -5634821094307128641L;

    private Map<Integer, Toilet> toilet = new HashMap<>();
    private Map<Integer, Toy> toy = new HashMap<>();
    private Map<Integer, Stone> stone = new HashMap<>();
    private Map<Integer, Sui> sui = new HashMap<>();
    private Map<Integer, Bed> bed = new HashMap<>();
    private Map<Integer, Trampoline> trampoline = new HashMap<>();
    private Map<Integer, House> house = new HashMap<>();
    private Map<Integer, Trash> trash = new HashMap<>();
    private Map<Integer, Okazari> okazari = new HashMap<>();
    private Map<Integer, BreedingPool> breedingPool = new HashMap<>();
    private Map<Integer, Stalk> stalk = new HashMap<>();

    /**
     * デフォルトコンストラクタ。Jackson デシリアライズ用。
     */
    public ItemRepository() {
    }

    /**
     * トイレマップを返す。キーは objId。
     *
     * @return トイレマップ
     */
    public Map<Integer, Toilet> getToilets() {
        return toilet;
    }

    /**
     * トイレマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param toilet 新しいトイレマップ
     */
    public void setToilets(Map<Integer, Toilet> toilet) {
        this.toilet = toilet;
    }

    /**
     * おもちゃマップを返す。キーは objId。
     *
     * @return おもちゃマップ
     */
    public Map<Integer, Toy> getToys() {
        return toy;
    }

    /**
     * おもちゃマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param toy 新しいおもちゃマップ
     */
    public void setToys(Map<Integer, Toy> toy) {
        this.toy = toy;
    }

    /**
     * 小石マップを返す。キーは objId。
     *
     * @return 小石マップ
     */
    public Map<Integer, Stone> getStones() {
        return stone;
    }

    /**
     * 小石マップをセットする（主にデシリアライズ時に使用）。
     *
     * @param stone 新しい小石マップ
     */
    public void setStones(Map<Integer, Stone> stone) {
        this.stone = stone;
    }

    /**
     * 酢（乗り物アイテム）マップを返す。キーは objId。
     *
     * @return 酢マップ
     */
    public Map<Integer, Sui> getSuis() {
        return sui;
    }

    /**
     * 酢マップをセットする（主にデシリアライズ時に使用）。
     *
     * @param sui 新しい酢マップ
     */
    public void setSuis(Map<Integer, Sui> sui) {
        this.sui = sui;
    }

    /**
     * ベッドマップを返す。キーは objId。
     *
     * @return ベッドマップ
     */
    public Map<Integer, Bed> getBeds() {
        return bed;
    }

    /**
     * ベッドマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param bed 新しいベッドマップ
     */
    public void setBeds(Map<Integer, Bed> bed) {
        this.bed = bed;
    }

    /**
     * トランポリンマップを返す。キーは objId。
     *
     * @return トランポリンマップ
     */
    public Map<Integer, Trampoline> getTrampolines() {
        return trampoline;
    }

    /**
     * トランポリンマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param trampoline 新しいトランポリンマップ
     */
    public void setTrampolines(Map<Integer, Trampoline> trampoline) {
        this.trampoline = trampoline;
    }

    /**
     * 家マップを返す。キーは objId。
     *
     * @return 家マップ
     */
    public Map<Integer, House> getHouses() {
        return house;
    }

    /**
     * 家マップをセットする（主にデシリアライズ時に使用）。
     *
     * @param house 新しい家マップ
     */
    public void setHouses(Map<Integer, House> house) {
        this.house = house;
    }

    /**
     * ゴミ箱マップを返す。キーは objId。
     *
     * @return ゴミ箱マップ
     */
    public Map<Integer, Trash> getTrashObjects() {
        return trash;
    }

    /**
     * ゴミ箱マップをセットする（主にデシリアライズ時に使用）。
     *
     * @param trash 新しいゴミ箱マップ
     */
    public void setTrashObjects(Map<Integer, Trash> trash) {
        this.trash = trash;
    }

    /**
     * おかざりマップを返す。キーは objId。
     *
     * @return おかざりマップ
     */
    public Map<Integer, Okazari> getOkazaris() {
        return okazari;
    }

    /**
     * おかざりマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param okazari 新しいおかざりマップ
     */
    public void setOkazaris(Map<Integer, Okazari> okazari) {
        this.okazari = okazari;
    }

    /**
     * 交配池マップを返す。キーは objId。
     *
     * @return 交配池マップ
     */
    public Map<Integer, BreedingPool> getBreedingPools() {
        return breedingPool;
    }

    /**
     * 交配池マップをセットする（主にデシリアライズ時に使用）。
     *
     * @param breedingPool 新しい交配池マップ
     */
    public void setBreedingPools(Map<Integer, BreedingPool> breedingPool) {
        this.breedingPool = breedingPool;
    }

    /**
     * 茎マップを返す。ゆっくりが出産するときに子を支える茎オブジェクトを管理する。キーは objId。
     *
     * @return 茎マップ
     */
    public Map<Integer, Stalk> getStalks() {
        return stalk;
    }

    /**
     * 茎マップをセットする（主にデシリアライズ時に使用）。
     *
     * @param stalk 新しい茎マップ
     */
    public void setStalks(Map<Integer, Stalk> stalk) {
        this.stalk = stalk;
    }
}
