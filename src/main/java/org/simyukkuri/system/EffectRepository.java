package org.simyukkuri.system;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.simyukkuri.entity.core.effect.Effect;
import org.simyukkuri.event.EventPacket;

/**
 * 描画エフェクト（煙・炎・スチームなど）とアクティブなイベントパケットを管理するリポジトリ。
 * sortEffect は z ソートされる通常エフェクト、frontEffect は常に最前面に描画されるエフェクト。
 * {@link WorldState} の一部として保持される。
 */
public class EffectRepository implements Serializable {

    private static final long serialVersionUID = 6723018459302874651L;

    private Map<Integer, Effect> sortEffect = new HashMap<>();
    private Map<Integer, Effect> frontEffect = new HashMap<>();
    private List<EventPacket> event = new LinkedList<>();

    /**
     * デフォルトコンストラクタ。Jackson デシリアライズ用。
     */
    public EffectRepository() {
    }

    /**
     * z ソートして描画される通常エフェクトマップを返す。キーは objId。
     *
     * @return ソート対象エフェクトマップ
     */
    public Map<Integer, Effect> getSortedEffects() {
        return sortEffect;
    }

    /**
     * ソート対象エフェクトマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param sortEffect 新しいエフェクトマップ
     */
    public void setSortedEffects(Map<Integer, Effect> sortEffect) {
        this.sortEffect = sortEffect;
    }

    /**
     * 常に最前面に描画されるエフェクトマップを返す。キーは objId。
     *
     * @return 前面エフェクトマップ
     */
    public Map<Integer, Effect> getFrontEffects() {
        return frontEffect;
    }

    /**
     * 前面エフェクトマップをセットする（主にデシリアライズ時に使用）。
     *
     * @param frontEffect 新しい前面エフェクトマップ
     */
    public void setFrontEffects(Map<Integer, Effect> frontEffect) {
        this.frontEffect = frontEffect;
    }

    /**
     * 現在実行中のイベントパケットリストを返す。毎ティックで update が呼ばれる。
     *
     * @return アクティブイベントリスト
     */
    public List<EventPacket> getEvents() {
        return event;
    }

    /**
     * アクティブイベントリストをセットする（主にデシリアライズ時に使用）。
     *
     * @param event 新しいイベントリスト
     */
    public void setEvents(List<EventPacket> event) {
        this.event = event;
    }
}
