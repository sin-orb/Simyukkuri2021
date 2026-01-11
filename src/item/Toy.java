package src.item;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;

import src.SimYukkuri;
import src.base.Body;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.enums.ObjEXType;
import src.enums.Type;

/***************************************************
 * おもちゃ
 */
public class Toy extends ObjEX implements java.io.Serializable {

	private static final long serialVersionUID = -5700583776006467893L;
	private static final int BALL = 0;
	private static final int BALL_NORA = 1;
	private static final int SHADOW = 2;
	private static final int NUM_OF_BALL_IMG = 3;
	private static BufferedImage[] images = new BufferedImage[NUM_OF_BALL_IMG];
	private static Rectangle4y boundary = new Rectangle4y();
	private Body owner = null;

	private ItemRank itemRank;
	/**画像ロード*/
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {

		images[BALL] = ModLoader.loadItemImage(loader, "toy" + File.separator + "ball.png");
		images[BALL_NORA] = ModLoader.loadItemImage(loader, "toy" + File.separator + "ball" + ModLoader.getYkWordNora() + ".png");
		images[SHADOW] = ModLoader.loadItemImage(loader, "toy" + File.separator + "shadow.png");
		
		boundary.setWidth(images[0].getWidth(io));
		boundary.setHeight(images[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() - 1);
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if(itemRank == ItemRank.HOUSE) {
			layer[0] = images[BALL];
		} else {
			layer[0] = images[BALL_NORA];
		}
		return 1;
	}
	/**境界線の取得*/
	public static Rectangle4y getBounding() {
		return boundary;
	}

	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return images[SHADOW];
	}
	
	@Override
	public void removeListData(){
		SimYukkuri.world.getCurrentMap().getToy().remove(objId);
	}

	@Override
	public void grab() {
		owner = null;
		grabbed = true;
	}
	
	@Override
	public void kick() {
		kick(0, -8, -4);
	}
	/**
	 * おもちゃの持ち主を設定する.
	 * @param b おもちゃの持ち主
	 */
	public void setOwner(Body b) {
		owner = b;
	}
	/**
	 * おもちゃの持ち主を取得する.
	 * @return おもちゃの持ち主
	 */
	public Body getOwner() {
		return owner;
	}
	/**
	 * そのゆっくりに所有されているかどうか
	 * @param b 判定したいゆっくり
	 * @return そのゆっくりに所有されているかどうか
	 */
	@Transient
	public boolean isOwned(Body b) {
		return (owner == b);
	}

	/**
	 * コンストラクタ
	 * @param initX x座標
	 * @param initY y座標
	 * @param initOption 0:飼い用、1;野良用
	 */
	public Toy(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().getToy().put(objId, this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.TOY;
		
		itemRank = ItemRank.values()[initOption];
		if(itemRank == ItemRank.HOUSE) {
			value = 500;
			cost = 0;
		} else {
			value = 0;
			cost = 0;
		}
		
	}
	public Toy() {
		
	}

	public ItemRank getItemRank() {
		return itemRank;
	}

	public void setItemRank(ItemRank itemRank) {
		this.itemRank = itemRank;
	}
	
}




