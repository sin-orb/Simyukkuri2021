package org.simyukkuri.entity.core.world.item;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;

import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.system.Cash;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;

/***************************************************
 * プレス機
 */
public class MachinePress extends WorldEntity {

	private static final long serialVersionUID = 5340736342470019134L;
	/** 処理対象(ゆっくり、うんうん、吐餡) */
	public static final int hitCheckObjType = WorldEntity.YUKKURI | WorldEntity.SHIT | WorldEntity.VOMIT;
	private static final int IMAGE_COUNT = 8; // このクラスの総使用画像数
	private static int[] animationFrameCounts = { 8 };// アニメごとに何枚使うか
	private static BufferedImage[] imageLayers = new BufferedImage[IMAGE_COUNT + 1];
	private static Rectangle4y boundary = new Rectangle4y();

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		for (int i = 0; i < IMAGE_COUNT; i++) {
			imageLayers[i] = ModLoader.loadItemImage(loader,
					"machinepress" + File.separator + "machinepress" + String.format("%03d", i + 1) + ".png");
		}
		imageLayers[IMAGE_COUNT] = ModLoader.loadItemImage(loader,
				"machinepress" + File.separator + "machinepress_off.png");
		boundary.setWidth(imageLayers[0].getWidth(io));
		boundary.setHeight(imageLayers[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() - 1);
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (enabled)
			layer[0] = imageLayers[(int) getAge() / 2 % animationFrameCounts[0]];
		else
			layer[0] = imageLayers[animationFrameCounts[0]];
		return 1;
	}

	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return null;
	}

	/** 境界線の取得 */
	public static Rectangle4y getBounding() {
		return boundary;
	}

	@Override
	@Transient
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	@Override
	public int objHitProcess(Entity o) {
		if (o.getObjType() == Type.YUKKURI) {
			Yukkuri p = (Yukkuri) o;
			if (!p.isDead() && p.isNotNYD() && GameRandom.nextInt(5) == 0) {
				p.setHappiness(Happiness.VERY_SAD);
				p.setForceFace(ImageCode.CRYING.ordinal());
				p.setMessage(GameMessages.getMessage(p, MessagePool.Action.KilledInFactory), 40, true, true);
			}
			if ((int) getAge() / 2 % animationFrameCounts[0] == 0) {
				p.setSilent(true);
				p.setShit(0, false);
				p.strikeByPress();
			}
		}
		return 0;
	}

	@Override
	public void upDate() {
		if (getAge() % 2400 == 0 && enabled) {
			Cash.addCash(-getCost());
		}
	}

	@Override
	public void removeFromWorld() {
		GameWorld.get().getCurrentWorldState().getMachinePresses().remove(objId);
	}

	/** コンストラクタ */
	public MachinePress(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), 8);
		GameWorld.get().getCurrentWorldState().getMachinePresses().put(objId, this);
		objType = Type.FIX_OBJECT;
		worldEntityType = WorldEntityKind.MACHINEPRESS;

		value = 500000;
		cost = 1500;
	}

	public MachinePress() {

	}

}
