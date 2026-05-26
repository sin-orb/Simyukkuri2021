package org.simyukkuri.entity.core.world.item;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.util.GameWorld;

/**
 * おもちゃ
 */
public class Toy extends WorldEntity {

	private static final long serialVersionUID = -5700583776006467893L;
	private static final int BALL = 0;
	private static final int BALL_NORA = 1;
	private static final int SHADOW = 2;
	private static final int NUM_OF_BALL_IMG = 3;
	private static BufferedImage[] images = new BufferedImage[NUM_OF_BALL_IMG];
	private static Rectangle4y boundary = new Rectangle4y();
	private Yukkuri owner = null;

	private ItemRank itemRank;

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		images[BALL] = ModLoader.loadItemImage(loader, "toy" + File.separator + "ball.png");
		images[BALL_NORA] = ModLoader.loadItemImage(loader,
				"toy" + File.separator + "ball" + ModLoader.getYkWordNora() + ".png");
		images[SHADOW] = ModLoader.loadItemImage(loader, "toy" + File.separator + "shadow.png");

		boundary.setWidth(images[0].getWidth(io));
		boundary.setHeight(images[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() - 1);
	}

	/** アイテム画像をレイヤー配列にセットし、使用レイヤー数を返す。 */
	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (itemRank == ItemRank.HOUSE) {
			layer[0] = images[BALL];
		} else {
			layer[0] = images[BALL_NORA];
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
		return images[SHADOW];
	}

	/** ワールドからこのアイテムを除去する。 */
	@Override
	public void removeFromWorld() {
		GameWorld.get().getCurrentWorldState().getToys().remove(objId);
	}

	/**
	 * Grab.
	 */
	@Override
	public void grab() {
		owner = null;
		grabbed = true;
	}

	/**
	 * Kick.
	 */
	@Override
	public void kick() {
		kick(0, -8, -4);
	}

	/**
	 * おもちゃの持ち主を設定する.
	 * 
	 * @param body おもちゃの持ち主
	 */
	public void setOwner(Yukkuri body) {
		owner = body;
	}

	/**
	 * おもちゃの持ち主を取得する.
	 * 
	 * @return おもちゃの持ち主
	 */
	public Yukkuri getOwner() {
		return owner;
	}

	/**
	 * そのゆっくりに所有されているかどうか
	 * 
	 * @param body 判定したいゆっくり
	 * @return そのゆっくりに所有されているかどうか
	 */
	@Transient
	public boolean isOwned(Yukkuri body) {
		return (owner == body);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param initX      x座標
	 * @param initY      y座標
	 * @param initOption 0:飼い用、1;野良用
	 */
	public Toy(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		GameWorld.get().getCurrentWorldState().getToys().put(objId, this);
		GameWorld.get().getCurrentWorldState().registerEntity(objId, this);
		objType = Type.OBJECT;
		worldEntityType = WorldEntityKind.TOY;

		itemRank = ItemRank.values()[initOption];
		if (itemRank == ItemRank.HOUSE) {
			value = 500;
			cost = 0;
		} else {
			value = 0;
			cost = 0;
		}

	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public Toy() {

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
