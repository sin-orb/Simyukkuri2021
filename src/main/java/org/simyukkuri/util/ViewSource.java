package org.simyukkuri.util;

import java.awt.Component;

import org.simyukkuri.draw.MyPane;
import org.simyukkuri.draw.Terrarium;
import org.simyukkuri.entity.core.effect.Effect;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.EffectType;
import org.simyukkuri.enums.YukkuriType;

public interface ViewSource {
	MyPane getPane();

	Component getDialogParent();

	Terrarium getTerrarium();

	void initBodies();

	void loadImage(boolean isBg, boolean isItem, boolean isEffect, boolean isBody, boolean isAttach, boolean isIni);

	void loadYukkuriImage(YukkuriType type);

	void loadTerrainFile();

	void createBackBuffer();

	Yukkuri addYukkuri(int x, int y, int z, YukkuriType type, AgeState age, Yukkuri p1, Yukkuri p2);

	Yukkuri makeYukkuri(int x, int y, int z, YukkuriType type, Dna dna, AgeState age, Yukkuri p1, Yukkuri p2,
			boolean adjust);

	Vomit addVomit(int x, int y, int z, Yukkuri body, YukkuriType type);

	void addCrushedVomit(int x, int y, int z, Yukkuri body, YukkuriType type);

	void addCrushedShit(int x, int y, int z, Yukkuri body, YukkuriType type);

	Effect addEffect(EffectType type, int x, int y, int z, int vx, int vy, int vz, boolean invert, int life,
			int loop, boolean end, boolean grav, boolean front);
}
