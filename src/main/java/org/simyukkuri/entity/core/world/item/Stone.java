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
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.util.GameWorld;

/***************************************************
 * 小石
 */
public class Stone extends WorldEntity {
	private static final long serialVersionUID = 6460240997300861568L;
	/** 処理対象(ゆっくり) */
	public static final int hitCheckObjType = WorldEntity.YUKKURI;
	private static final int NUM_OF_STONE_IMG = 3;
	private static BufferedImage[] images = new BufferedImage[NUM_OF_STONE_IMG];
	private static Rectangle4y boundary = new Rectangle4y();

	private ItemRank itemRank;

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "stone" + File.separator + "pubble.png");
		images[1] = ModLoader.loadItemImage(loader,
				"stone" + File.separator + "pubble" + ModLoader.getYkWordNora() + ".png");
		images[2] = ModLoader.loadItemImage(loader, "stone" + File.separator + "shadow.png");

		boundary.setWidth(images[0].getWidth(io));
		boundary.setHeight(images[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() - 1);
	}

	/** アイテム画像をレイヤー配列にセットし、使用レイヤー数を返す。 */
	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (itemRank == ItemRank.HOUSE) {
			layer[0] = images[0];
		} else {
			layer[0] = images[1];
		}
		return 1;
	}

	/** 境界線の取得 */
	public static Rectangle4y getBounding() {
		return boundary;
	}

	/** アイテムの影画像を返す。 */
	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return images[2];
	}

	/** 衝突判定対象タイプを返す。 */
	@Override
	@Transient
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	@Override
	/** 衝突処理を行い、結果コードを返す。 */
	public int objHitProcess(Entity targetObject) {
		if (targetObject instanceof Yukkuri) {
			Yukkuri body = (Yukkuri) targetObject;
			if (body.getCriticalDamege() == CriticalDamageType.CUT) {
				return 0;
			}
			if (body.isBaby())
				body.bodyCut();
			else {
				body.bodyInjure();
				body.runAway(getX(), getY());
			}
		}
		return 0;
	}

	/** ワールドからこのアイテムを除去する。 */
	@Override
	public void removeFromWorld() {
		GameWorld.get().getCurrentWorldState().getStones().remove(objId);
	}

	@Override
	/**
	 * Grab.
	 */
	public void grab() {
		grabbed = true;
	}

	@Override
	/**
	 * Kick.
	 */
	public void kick() {
		kick(0, -8, -4);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param initX      x座標
	 * @param initY      y座標
	 * @param initOption 0:家の中、1家の外
	 */
	public Stone(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		GameWorld.get().getCurrentWorldState().getStones().put(objId, this);
		GameWorld.get().getCurrentWorldState().registerEntity(objId, this);
		objType = Type.OBJECT;
		worldEntityType = WorldEntityKind.STONE;
		interval = 5;
		itemRank = ItemRank.values()[initOption];
		if (GameWorld.get().getCurrentWorldState().getWorldIndex() == 2 || GameWorld.get().getCurrentWorldState().getWorldIndex() == 3
				|| GameWorld.get().getCurrentWorldState().getWorldIndex() == 4) {
			itemRank = ItemRank.NORA;
		}
		if (GameWorld.get().getCurrentWorldState().getWorldIndex() == 5
				|| GameWorld.get().getCurrentWorldState().getWorldIndex() == 6) {
			itemRank = ItemRank.YASEI;
		}
		if (itemRank == ItemRank.HOUSE) {
			value = 0;
			cost = 0;
		} else {
			value = 0;
			cost = 0;
		}

	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public Stone() {

	}

	/** アイテムのランク（品質）を返す。 */
	public ItemRank getItemRank() {
		return itemRank;
	}

	/** アイテムのランク（品質）をセットする。 */
	public void setItemRank(ItemRank itemRank) {
		this.itemRank = itemRank;
	}

}
