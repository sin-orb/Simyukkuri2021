package org.simyukkuri.util;

import java.awt.Component;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.draw.Terrarium;
import org.simyukkuri.entity.core.effect.Effect;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.EffectType;
import org.simyukkuri.enums.YukkuriType;

public final class GameView {
	private static final ViewSource DEFAULT = new ViewSource() {
		@Override
		public MyPane getPane() {
			return SimYukkuri.mypane;
		}

		@Override
		public Component getDialogParent() {
			return getPane();
		}

		@Override
		public Terrarium getTerrarium() {
			MyPane pane = getPane();
			return pane != null ? pane.getTerrarium() : null;
		}

		@Override
		public void initBodies() {
			getPane().initBodies();
		}

		@Override
		public void loadImage(boolean isBg, boolean isItem, boolean isEffect, boolean isBody, boolean isAttach,
				boolean isIni) {
			getPane().loadImage(isBg, isItem, isEffect, isBody, isAttach, isIni);
		}

		@Override
		public void loadBodyImage(YukkuriType type) {
			getPane().loadBodyImage(type);
		}

		@Override
		public void loadTerrainFile() {
			getPane().loadTerrainFile();
		}

		@Override
		public void createBackBuffer() {
			getPane().createBackBuffer();
		}

		@Override
		public Yukkuri addBody(int x, int y, int z, YukkuriType type, AgeState age, Yukkuri p1, Yukkuri p2) {
			return getPane().getTerrarium().addBody(x, y, z, type, age, p1, p2);
		}

		@Override
		public Yukkuri makeBody(int x, int y, int z, YukkuriType type, Dna dna, AgeState age, Yukkuri p1, Yukkuri p2,
				boolean adjust) {
			return getPane().getTerrarium().makeBody(x, y, z, type, dna, age, p1, p2, adjust);
		}

		@Override
		public Vomit addVomit(int x, int y, int z, Yukkuri body, YukkuriType type) {
			return getPane().getTerrarium().addVomit(x, y, z, body, type);
		}

		@Override
		public void addCrushedVomit(int x, int y, int z, Yukkuri body, YukkuriType type) {
			getPane().getTerrarium().addCrushedVomit(x, y, z, body, type);
		}

		@Override
		public void addCrushedShit(int x, int y, int z, Yukkuri body, YukkuriType type) {
			getPane().getTerrarium().addCrushedShit(x, y, z, body, type);
		}

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

	public static MyPane getPane() {
		return source().getPane();
	}

	public static Component getDialogParent() {
		return source().getDialogParent();
	}

	public static Terrarium getTerrarium() {
		return source().getTerrarium();
	}

	public static void initBodies() {
		source().initBodies();
	}

	public static void loadImage(boolean isBg, boolean isItem, boolean isEffect, boolean isBody, boolean isAttach,
			boolean isIni) {
		source().loadImage(isBg, isItem, isEffect, isBody, isAttach, isIni);
	}

	public static void loadBodyImage(YukkuriType type) {
		source().loadBodyImage(type);
	}

	public static void loadTerrainFile() {
		source().loadTerrainFile();
	}

	public static void createBackBuffer() {
		source().createBackBuffer();
	}

	public static Yukkuri addBody(int x, int y, int z, YukkuriType type, AgeState age, Yukkuri p1, Yukkuri p2) {
		return source().addBody(x, y, z, type, age, p1, p2);
	}

	public static Yukkuri makeBody(int x, int y, int z, YukkuriType type, Dna dna, AgeState age, Yukkuri p1, Yukkuri p2,
			boolean adjust) {
		return source().makeBody(x, y, z, type, dna, age, p1, p2, adjust);
	}

	public static Vomit addVomit(int x, int y, int z, Yukkuri body, YukkuriType type) {
		return source().addVomit(x, y, z, body, type);
	}

	public static void addCrushedVomit(int x, int y, int z, Yukkuri body, YukkuriType type) {
		source().addCrushedVomit(x, y, z, body, type);
	}

	public static void addCrushedShit(int x, int y, int z, Yukkuri body, YukkuriType type) {
		source().addCrushedShit(x, y, z, body, type);
	}

	public static Effect addEffect(EffectType type, int x, int y, int z, int vx, int vy, int vz, boolean invert,
			int life, int loop, boolean end, boolean grav, boolean front) {
		return source().addEffect(type, x, y, z, vx, vy, vz, invert, life, loop, end, grav, front);
	}

	public static void setOverride(ViewSource source) {
		override = source;
	}

	public static void clearOverride() {
		override = null;
	}

	private static ViewSource source() {
		return override != null ? override : DEFAULT;
	}
}
