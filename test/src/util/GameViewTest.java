package src.util;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Panel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import src.base.Yukkuri;
import src.effect.Effect;
import src.draw.MyPane;
import src.draw.Terrarium;
import src.enums.AgeState;
import src.enums.EffectType;
import src.enums.YukkuriType;
import src.game.Dna;
import src.game.Vomit;

public class GameViewTest {

	@AfterEach
	public void tearDown() {
		GameView.clearOverride();
	}

	@Test
	public void testViewAccessUsesOverrideWhenSet() {
		RecordingViewSource source = new RecordingViewSource();
		GameView.setOverride(source);

		GameView.initBodies();
		GameView.loadImage(false, false, false, false, false, true);
		GameView.loadBodyImage(YukkuriType.REIMU);
		GameView.loadTerrainFile();
		GameView.createBackBuffer();

		assertSame(source.pane, GameView.getPane());
		assertSame(source.parent, GameView.getDialogParent());
		assertSame(source.terrarium, GameView.getTerrarium());
		assertTrue(source.initBodies);
		assertTrue(source.loadImage);
		assertTrue(source.loadBodyImage);
		assertTrue(source.loadTerrainFile);
		assertTrue(source.createBackBuffer);
	}

	@Test
	public void testTerrariumActionsUseOverrideWhenSet() {
		RecordingViewSource source = new RecordingViewSource();
		GameView.setOverride(source);

		GameView.addBody(1, 2, 3, 4, AgeState.BABY, null, null);
		GameView.makeBody(1, 2, 3, 4, null, AgeState.BABY, null, null, true);
		GameView.addVomit(1, 2, 3, null, YukkuriType.REIMU);
		GameView.addCrushedVomit(1, 2, 3, null, YukkuriType.REIMU);
		GameView.addCrushedShit(1, 2, 3, null, YukkuriType.REIMU);
		GameView.addEffect(EffectType.HIT, 1, 2, 3, 4, 5, 6, true, 7, 8, false, true, false);

		assertTrue(source.addBody);
		assertTrue(source.makeBody);
		assertTrue(source.addVomit);
		assertTrue(source.addCrushedVomit);
		assertTrue(source.addCrushedShit);
		assertTrue(source.addEffect);
	}

	private static class RecordingViewSource implements ViewSource {
		private final MyPane pane = new MyPane();
		private final Component parent = new Panel();
		private final Terrarium terrarium = new Terrarium();
		private boolean initBodies;
		private boolean loadImage;
		private boolean loadBodyImage;
		private boolean loadTerrainFile;
		private boolean createBackBuffer;
		private boolean addBody;
		private boolean makeBody;
		private boolean addVomit;
		private boolean addCrushedVomit;
		private boolean addCrushedShit;
		private boolean addEffect;

		@Override
		public MyPane getPane() {
			return pane;
		}

		@Override
		public Component getDialogParent() {
			return parent;
		}

		@Override
		public Terrarium getTerrarium() {
			return terrarium;
		}

		@Override
		public void initBodies() {
			initBodies = true;
		}

		@Override
		public void loadImage(boolean isBg, boolean isItem, boolean isEffect, boolean isBody, boolean isAttach,
				boolean isIni) {
			loadImage = isIni;
		}

		@Override
		public void loadBodyImage(YukkuriType type) {
			loadBodyImage = type == YukkuriType.REIMU;
		}

		@Override
		public void loadTerrainFile() {
			loadTerrainFile = true;
		}

		@Override
		public void createBackBuffer() {
			createBackBuffer = true;
		}

		@Override
		public Yukkuri addBody(int x, int y, int z, int type, AgeState age, Yukkuri p1, Yukkuri p2) {
			addBody = true;
			return null;
		}

		@Override
		public Yukkuri makeBody(int x, int y, int z, int type, Dna dna, AgeState age, Yukkuri p1, Yukkuri p2,
				boolean adjust) {
			makeBody = adjust;
			return null;
		}

		@Override
		public Vomit addVomit(int x, int y, int z, Yukkuri body, YukkuriType type) {
			addVomit = true;
			return null;
		}

		@Override
		public void addCrushedVomit(int x, int y, int z, Yukkuri body, YukkuriType type) {
			addCrushedVomit = true;
		}

		@Override
		public void addCrushedShit(int x, int y, int z, Yukkuri body, YukkuriType type) {
			addCrushedShit = true;
		}

		@Override
		public Effect addEffect(EffectType type, int x, int y, int z, int vx, int vy, int vz, boolean invert,
				int life, int loop, boolean end, boolean grav, boolean front) {
			addEffect = type == EffectType.HIT;
			return null;
		}
	}
}
