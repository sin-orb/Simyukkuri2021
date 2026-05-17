package org.simyukkuri.system;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.simyukkuri.entity.core.world.item.AutoFeeder;
import org.simyukkuri.entity.core.world.item.BeltconveyorObj;
import org.simyukkuri.entity.core.world.item.Diffuser;
import org.simyukkuri.entity.core.world.item.FoodMaker;
import org.simyukkuri.entity.core.world.item.GarbageChute;
import org.simyukkuri.entity.core.world.item.GarbageStation;
import org.simyukkuri.entity.core.world.item.HotPlate;
import org.simyukkuri.entity.core.world.item.MachinePress;
import org.simyukkuri.entity.core.world.item.Mixer;
import org.simyukkuri.entity.core.world.item.OrangePool;
import org.simyukkuri.entity.core.world.item.ProcessorPlate;
import org.simyukkuri.entity.core.world.item.ProductChute;
import org.simyukkuri.entity.core.world.item.StickyPlate;
import org.simyukkuri.entity.core.world.item.Yunba;
import org.simyukkuri.field.impl.Beltconveyor;

/**
 * 自動エサ箱・食べ物製造機・ミキサー・ディフューザー・プレス機・ホットプレート・
 * ベルトコンベアなど、ゆっくり飼育に使う機械系アイテムをまとめて管理するリポジトリ。
 * {@link WorldState} の一部として保持され、Jackson でセーブ／ロードされる。
 */
public class MachineRepository implements Serializable {

    private static final long serialVersionUID = 2097345618734509217L;

    private Map<Integer, AutoFeeder> autofeeder = new HashMap<>();
    private Map<Integer, FoodMaker> foodmaker = new HashMap<>();
    private Map<Integer, Mixer> mixer = new HashMap<>();
    private Map<Integer, Diffuser> diffuser = new HashMap<>();
    private Map<Integer, MachinePress> machinePress = new HashMap<>();
    private Map<Integer, HotPlate> hotPlate = new HashMap<>();
    private Map<Integer, StickyPlate> stickyPlate = new HashMap<>();
    private Map<Integer, ProcessorPlate> processorPlate = new HashMap<>();
    private Map<Integer, GarbageChute> garbagechute = new HashMap<>();
    private Map<Integer, GarbageStation> garbageStation = new HashMap<>();
    private Map<Integer, ProductChute> productchute = new HashMap<>();
    private Map<Integer, OrangePool> orangePool = new HashMap<>();
    private Map<Integer, Yunba> yunba = new HashMap<>();
    private Map<Integer, BeltconveyorObj> beltconveyorObj = new HashMap<>();
    private List<Beltconveyor> beltconveyor = new LinkedList<>();

    /**
     * デフォルトコンストラクタ。Jackson デシリアライズ用。
     */
    public MachineRepository() {
    }

    /**
     * 自動エサ箱マップを返す。キーは objId。
     *
     * @return 自動エサ箱マップ
     */
    public Map<Integer, AutoFeeder> getAutoFeeders() {
        return autofeeder;
    }

    /**
     * 自動エサ箱マップをセットする（主にデシリアライズ時に使用）。
     *
     * @param autofeeder 新しい自動エサ箱マップ
     */
    public void setAutoFeeders(Map<Integer, AutoFeeder> autofeeder) {
        this.autofeeder = autofeeder;
    }

    /**
     * 食べ物製造機マップを返す。キーは objId。
     *
     * @return 食べ物製造機マップ
     */
    public Map<Integer, FoodMaker> getFoodMakers() {
        return foodmaker;
    }

    /**
     * 食べ物製造機マップをセットする（主にデシリアライズ時に使用）。
     *
     * @param foodmaker 新しい食べ物製造機マップ
     */
    public void setFoodMakers(Map<Integer, FoodMaker> foodmaker) {
        this.foodmaker = foodmaker;
    }

    /**
     * ミキサーマップを返す。キーは objId。
     *
     * @return ミキサーマップ
     */
    public Map<Integer, Mixer> getMixers() {
        return mixer;
    }

    /**
     * ミキサーマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param mixer 新しいミキサーマップ
     */
    public void setMixers(Map<Integer, Mixer> mixer) {
        this.mixer = mixer;
    }

    /**
     * ディフューザーマップを返す。ディフューザーはスチームで非ゆっくり症を治癒する機械。キーは objId。
     *
     * @return ディフューザーマップ
     */
    public Map<Integer, Diffuser> getDiffusers() {
        return diffuser;
    }

    /**
     * ディフューザーマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param diffuser 新しいディフューザーマップ
     */
    public void setDiffusers(Map<Integer, Diffuser> diffuser) {
        this.diffuser = diffuser;
    }

    /**
     * プレス機マップを返す。キーは objId。
     *
     * @return プレス機マップ
     */
    public Map<Integer, MachinePress> getMachinePresses() {
        return machinePress;
    }

    /**
     * プレス機マップをセットする（主にデシリアライズ時に使用）。
     *
     * @param machinePress 新しいプレス機マップ
     */
    public void setMachinePresses(Map<Integer, MachinePress> machinePress) {
        this.machinePress = machinePress;
    }

    /**
     * ホットプレートマップを返す。ゆっくりを焼く調理器具。キーは objId。
     *
     * @return ホットプレートマップ
     */
    public Map<Integer, HotPlate> getHotPlates() {
        return hotPlate;
    }

    /**
     * ホットプレートマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param hotPlate 新しいホットプレートマップ
     */
    public void setHotPlates(Map<Integer, HotPlate> hotPlate) {
        this.hotPlate = hotPlate;
    }

    /**
     * 粘着プレートマップを返す。ゆっくりを張り付かせるトラップ。キーは objId。
     *
     * @return 粘着プレートマップ
     */
    public Map<Integer, StickyPlate> getStickyPlates() {
        return stickyPlate;
    }

    /**
     * 粘着プレートマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param stickyPlate 新しい粘着プレートマップ
     */
    public void setStickyPlates(Map<Integer, StickyPlate> stickyPlate) {
        this.stickyPlate = stickyPlate;
    }

    /**
     * プロセッサープレートマップを返す。キーは objId。
     *
     * @return プロセッサープレートマップ
     */
    public Map<Integer, ProcessorPlate> getProcessorPlates() {
        return processorPlate;
    }

    /**
     * プロセッサープレートマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param processorPlate 新しいプロセッサープレートマップ
     */
    public void setProcessorPlates(Map<Integer, ProcessorPlate> processorPlate) {
        this.processorPlate = processorPlate;
    }

    /**
     * ゴミ投入口マップを返す。フンや食べ残しをゴミステーションに搬送する機器。キーは objId。
     *
     * @return ゴミ投入口マップ
     */
    public Map<Integer, GarbageChute> getGarbageChutes() {
        return garbagechute;
    }

    /**
     * ゴミ投入口マップをセットする（主にデシリアライズ時に使用）。
     *
     * @param garbagechute 新しいゴミ投入口マップ
     */
    public void setGarbageChutes(Map<Integer, GarbageChute> garbagechute) {
        this.garbagechute = garbagechute;
    }

    /**
     * ゴミステーションマップを返す。キーは objId。
     *
     * @return ゴミステーションマップ
     */
    public Map<Integer, GarbageStation> getGarbageStations() {
        return garbageStation;
    }

    /**
     * ゴミステーションマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param garbageStation 新しいゴミステーションマップ
     */
    public void setGarbageStations(Map<Integer, GarbageStation> garbageStation) {
        this.garbageStation = garbageStation;
    }

    /**
     * 製品搬出口マップを返す。キーは objId。
     *
     * @return 製品搬出口マップ
     */
    public Map<Integer, ProductChute> getProductChutes() {
        return productchute;
    }

    /**
     * 製品搬出口マップをセットする（主にデシリアライズ時に使用）。
     *
     * @param productchute 新しい製品搬出口マップ
     */
    public void setProductChutes(Map<Integer, ProductChute> productchute) {
        this.productchute = productchute;
    }

    /**
     * オレンジ汁池マップを返す。ゆっくりが溺れる液体プールの一種。キーは objId。
     *
     * @return オレンジ汁池マップ
     */
    public Map<Integer, OrangePool> getOrangePools() {
        return orangePool;
    }

    /**
     * オレンジ汁池マップをセットする（主にデシリアライズ時に使用）。
     *
     * @param orangePool 新しいオレンジ汁池マップ
     */
    public void setOrangePools(Map<Integer, OrangePool> orangePool) {
        this.orangePool = orangePool;
    }

    /**
     * ゆんばマップを返す。キーは objId。
     *
     * @return ゆんばマップ
     */
    public Map<Integer, Yunba> getYunbas() {
        return yunba;
    }

    /**
     * ゆんばマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param yunba 新しいゆんばマップ
     */
    public void setYunbas(Map<Integer, Yunba> yunba) {
        this.yunba = yunba;
    }

    /**
     * ベルトコンベア上のオブジェクトマップを返す。キーは objId。
     *
     * @return ベルトコンベアオブジェクトマップ
     */
    public Map<Integer, BeltconveyorObj> getBeltconveyorObjects() {
        return beltconveyorObj;
    }

    /**
     * ベルトコンベアオブジェクトマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param beltconveyorObj 新しいベルトコンベアオブジェクトマップ
     */
    public void setBeltconveyorObjects(Map<Integer, BeltconveyorObj> beltconveyorObj) {
        this.beltconveyorObj = beltconveyorObj;
    }

    /**
     * ベルトコンベアのレーンリストを返す。
     *
     * @return ベルトコンベアリスト
     */
    public List<Beltconveyor> getBeltconveyors() {
        return beltconveyor;
    }

    /**
     * ベルトコンベアリストをセットする（主にデシリアライズ時に使用）。
     *
     * @param beltconveyor 新しいベルトコンベアリスト
     */
    public void setBeltconveyors(List<Beltconveyor> beltconveyor) {
        this.beltconveyor = beltconveyor;
    }
}
