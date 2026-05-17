package org.simyukkuri.util;

import java.awt.Component;

import org.simyukkuri.SimYukkuri;
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
 * GameView.
 */
public final class GameView {
	private static final ViewSource DEFAULT = new ViewSource() {
		/** メインキャンバスペインを返す。 */
		@Override
		public MyPane getPane() {
			return SimYukkuri.mypane;
		}

		/** ダイアログ親コンポーネントを返す。 */
		@Override
		public Component getDialogParent() {
			return getPane();
		}

		/** テラリウムインスタンスを返す。 */
		@Override
		public Terrarium getTerrarium() {
			MyPane pane = getPane();
			return pane != null ? pane.getTerrarium() : null;
		}

		/** ゆっくりの表示状態を初期化する。 */
		@Override
		public void initBodies() {
			getPane().initBodies();
		}

		/** 各カテゴリの画像を読み込む。 */
		@Override
		public void loadImage(boolean isBg, boolean isItem, boolean isEffect, boolean isBody, boolean isAttach,
				boolean isIni) {
			getPane().loadImage(isBg, isItem, isEffect, isBody, isAttach, isIni);
		}

		/** 指定タイプのゆっくり画像を読み込む。 */
		@Override
		public void loadYukkuriImage(YukkuriType type) {
			getPane().loadYukkuriImage(type);
		}

		/** 地形ファイルを読み込む。 */
		@Override
		public void loadTerrainFile() {
			getPane().loadTerrainFile();
		}

		/** バックバッファを作成する。 */
		@Override
		public void createBackBuffer() {
			getPane().createBackBuffer();
		}

		/** ゆっくりをワールドに追加して返す。 */
		@Override
		public Yukkuri addYukkuri(int x, int y, int z, YukkuriType type, AgeState age, Yukkuri p1, Yukkuri p2) {
			return getPane().getTerrarium().addYukkuri(x, y, z, type, age, p1, p2);
		}

		/** DNA情報を指定してゆっくりを生成して返す。 */
		@Override
		public Yukkuri makeYukkuri(int x, int y, int z, YukkuriType type, Dna dna, AgeState age, Yukkuri p1, Yukkuri p2,
				boolean adjust) {
			return getPane().getTerrarium().makeYukkuri(x, y, z, type, dna, age, p1, p2, adjust);
		}

		/** 吐瀉物エンティティをワールドに追加して返す。 */
		@Override
		public Vomit addVomit(int x, int y, int z, Yukkuri body, YukkuriType type) {
			return getPane().getTerrarium().addVomit(x, y, z, body, type);
		}

		/** 踏み潰し吐瀉物エンティティをワールドに追加する。 */
		@Override
		public void addCrushedVomit(int x, int y, int z, Yukkuri body, YukkuriType type) {
			getPane().getTerrarium().addCrushedVomit(x, y, z, body, type);
		}

		/** 踏み潰し糞エンティティをワールドに追加する。 */
		@Override
		public void addCrushedShit(int x, int y, int z, Yukkuri body, YukkuriType type) {
			getPane().getTerrarium().addCrushedShit(x, y, z, body, type);
		}

		/** エフェクトをワールドに追加して返す。 */
		@Override
		public Effect addEffect(EffectType type, int x, int y, int z, int vx, int vy, int vz, boolean invert,
				int life, int loop, boolean end, boolean grav, boolean front) {
			return getPane().getTerrarium().addEffect(type, x, y, z, vx, vy, vz, invert, life, loop, end, grav,
					front);
		}
	};

	private static ViewSource override;

	private GameView() {
	}

	/** メインキャンバスペインを返す。 */
	public static MyPane getPane() {
		return source().getPane();
	}

	/** ダイアログ親コンポーネントを返す。 */
	public static Component getDialogParent() {
		return source().getDialogParent();
	}

	/** テラリウムインスタンスを返す。 */
	public static Terrarium getTerrarium() {
		return source().getTerrarium();
	}

	/** ゆっくりの表示状態を初期化する。 */
	public static void initBodies() {
		source().initBodies();
	}

	/** 各カテゴリの画像を読み込む。 */
	public static void loadImage(boolean isBg, boolean isItem, boolean isEffect, boolean isBody, boolean isAttach,
			boolean isIni) {
		source().loadImage(isBg, isItem, isEffect, isBody, isAttach, isIni);
	}

	/** 指定タイプのゆっくり画像を読み込む。 */
	public static void loadYukkuriImage(YukkuriType type) {
		source().loadYukkuriImage(type);
	}

	/** 地形ファイルを読み込む。 */
	public static void loadTerrainFile() {
		source().loadTerrainFile();
	}

	/** バックバッファを作成する。 */
	public static void createBackBuffer() {
		source().createBackBuffer();
	}

	/** ゆっくりをワールドに追加して返す。 */
	public static Yukkuri addYukkuri(int x, int y, int z, YukkuriType type, AgeState age, Yukkuri p1, Yukkuri p2) {
		return source().addYukkuri(x, y, z, type, age, p1, p2);
	}

	/** DNA情報を指定してゆっくりを生成して返す。 */
	public static Yukkuri makeYukkuri(int x, int y, int z, YukkuriType type, Dna dna, AgeState age, Yukkuri p1, Yukkuri p2,
			boolean adjust) {
		return source().makeYukkuri(x, y, z, type, dna, age, p1, p2, adjust);
	}

	/** 吐瀉物エンティティをワールドに追加して返す。 */
	public static Vomit addVomit(int x, int y, int z, Yukkuri body, YukkuriType type) {
		return source().addVomit(x, y, z, body, type);
	}

	/** 踏み潰し吐瀉物エンティティをワールドに追加する。 */
	public static void addCrushedVomit(int x, int y, int z, Yukkuri body, YukkuriType type) {
		source().addCrushedVomit(x, y, z, body, type);
	}

	/** 踏み潰し糞エンティティをワールドに追加する。 */
	public static void addCrushedShit(int x, int y, int z, Yukkuri body, YukkuriType type) {
		source().addCrushedShit(x, y, z, body, type);
	}

	/** エフェクトをワールドに追加して返す。 */
	public static Effect addEffect(EffectType type, int x, int y, int z, int vx, int vy, int vz, boolean invert,
			int life, int loop, boolean end, boolean grav, boolean front) {
		return source().addEffect(type, x, y, z, vx, vy, vz, invert, life, loop, end, grav, front);
	}

	/** テスト用の注入ソースをセットする。 */
	public static void setOverride(ViewSource source) {
		override = source;
	}

	/** テスト用注入ソースをクリアする。 */
	public static void clearOverride() {
		override = null;
	}

	private static ViewSource source() {
		return override != null ? override : DEFAULT;
	}
}
