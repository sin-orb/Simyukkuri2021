package src.util;

import java.awt.Component;

import src.base.Body;
import src.base.Effect;
import src.draw.MyPane;
import src.draw.Terrarium;
import src.enums.AgeState;
import src.enums.EffectType;
import src.enums.YukkuriType;
import src.game.Dna;
import src.game.Vomit;

public interface ViewSource {
	MyPane getPane();

	Component getDialogParent();

	Terrarium getTerrarium();

	void initBodies();

	void loadImage(boolean isBg, boolean isItem, boolean isEffect, boolean isBody, boolean isAttach, boolean isIni);

	void loadBodyImage(YukkuriType type);

	void loadTerrainFile();

	void createBackBuffer();

	Body addBody(int x, int y, int z, int type, AgeState age, Body p1, Body p2);

	Body makeBody(int x, int y, int z, int type, Dna dna, AgeState age, Body p1, Body p2, boolean adjust);

	Vomit addVomit(int x, int y, int z, Body body, YukkuriType type);

	void addCrushedVomit(int x, int y, int z, Body body, YukkuriType type);

	void addCrushedShit(int x, int y, int z, Body body, YukkuriType type);

	Effect addEffect(EffectType type, int x, int y, int z, int vx, int vy, int vz, boolean invert, int life,
			int loop, boolean end, boolean grav, boolean front);
}
