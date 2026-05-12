package src.util;

import java.awt.Component;

import src.draw.MyPane;
import src.draw.Terrarium;
import src.entity.core.effect.Effect;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.world.mobile.Vomit;
import src.enums.AgeState;
import src.enums.EffectType;
import src.enums.YukkuriType;

public interface ViewSource {
	MyPane getPane();

	Component getDialogParent();

	Terrarium getTerrarium();

	void initBodies();

	void loadImage(boolean isBg, boolean isItem, boolean isEffect, boolean isBody, boolean isAttach, boolean isIni);

	void loadBodyImage(YukkuriType type);

	void loadTerrainFile();

	void createBackBuffer();

	Yukkuri addBody(int x, int y, int z, YukkuriType type, AgeState age, Yukkuri p1, Yukkuri p2);

	Yukkuri makeBody(int x, int y, int z, YukkuriType type, Dna dna, AgeState age, Yukkuri p1, Yukkuri p2,
			boolean adjust);

	Vomit addVomit(int x, int y, int z, Yukkuri body, YukkuriType type);

	void addCrushedVomit(int x, int y, int z, Yukkuri body, YukkuriType type);

	void addCrushedShit(int x, int y, int z, Yukkuri body, YukkuriType type);

	Effect addEffect(EffectType type, int x, int y, int z, int vx, int vy, int vz, boolean invert, int life,
			int loop, boolean end, boolean grav, boolean front);
}
