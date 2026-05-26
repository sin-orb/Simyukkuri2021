package org.simyukkuri.util;

import java.awt.Component;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.engine.Terrarium;
import org.simyukkuri.entity.core.effect.Effect;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.EffectType;
import org.simyukkuri.enums.YukkuriType;

/**
 * ViewSource interface.
 */
public interface ViewSource {
	/** 描画ペインを返す。 */
	MyPane getPane();

	/** ダイアログの親コンポーネントを返す。 */
	Component getDialogParent();

	/** テラリウムを返す。 */
	Terrarium getTerrarium();

	/** ボディを初期化する。 */
	void initBodies();

	/** 画像をロードする。 */
	void loadImage(boolean isBg, boolean isItem, boolean isEffect, boolean isBody, boolean isAttach, boolean isIni);

	/** 指定タイプのゆっくり画像をロードする。 */
	void loadYukkuriImage(YukkuriType type);

	/** 地形ファイルをロードする。 */
	void loadTerrainFile();

	/** バックバッファを生成する。 */
	void createBackBuffer();

	/** ゆっくりを追加する。 */
	Yukkuri addYukkuri(int x, int y, int z, YukkuriType type, AgeState age, Yukkuri p1, Yukkuri p2);

	/** ゆっくりを生成して返す。 */
	Yukkuri makeYukkuri(int x, int y, int z, YukkuriType type, Dna dna, AgeState age, Yukkuri p1, Yukkuri p2,
			boolean adjust);

	/** 吐瀉物を追加する。 */
	Vomit addVomit(int x, int y, int z, Yukkuri body, YukkuriType type);

	/** 爆発吐瀉物を追加する。 */
	void addCrushedVomit(int x, int y, int z, Yukkuri body, YukkuriType type);

	/** 爆発うんうんを追加する。 */
	void addCrushedShit(int x, int y, int z, Yukkuri body, YukkuriType type);

	/** エフェクトを追加する。 */
	Effect addEffect(EffectType type, int x, int y, int z, int vx, int vy, int vz, boolean invert, int life,
			int loop, boolean end, boolean grav, boolean front);
}
