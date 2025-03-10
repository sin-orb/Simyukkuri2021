package src.item;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.enums.CriticalDamegeType;
import src.enums.ObjEXType;
import src.enums.Type;

/***************************************************
 * 小石
 */
public class Stone extends ObjEX implements java.io.Serializable {
	private static final long serialVersionUID = 6460240997300861568L;
	/**処理対象(ゆっくり)*/
	public static final int hitCheckObjType = ObjEX.YUKKURI;
	private static final int NUM_OF_STONE_IMG = 3;
	private static BufferedImage[] images = new BufferedImage[NUM_OF_STONE_IMG];
	private static Rectangle4y boundary = new Rectangle4y();

	private ItemRank itemRank;
	/**画像ロード*/
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "stone" + File.separator + "pubble.png");
		images[1] = ModLoader.loadItemImage(loader, "stone" + File.separator + "pubble" + ModLoader.YK_WORD_NORA + ".png");
		images[2] = ModLoader.loadItemImage(loader, "stone" + File.separator + "shadow.png");
		
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height - 1;
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if(itemRank == ItemRank.HOUSE) {
			layer[0] = images[0];
		}
		else {
			layer[0] = images[1];
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
		return images[2];
	}
	@Override
	@Transient
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}	
	@Override
	public int objHitProcess( Obj o ) {
		if ( o instanceof Body ){
			Body b = (Body)o;
			if( b.getCriticalDamege()  == CriticalDamegeType.CUT){
				return 0 ;
			}
			if(b.isBaby()) b.bodyCut();
			else{
				b.bodyInjure();
				b.runAway(getX(),getY());
			}
		}
		return 0;
	}

	
	@Override
	public void removeListData(){
		SimYukkuri.world.getCurrentMap().stone.remove(objId);
	}

	@Override
	public void grab() {
		grabbed = true;
	}
	
	@Override
	public void kick() {
		kick(0, -8, -4);
	}

	/**
	 * コンストラクタ
	 * @param initX x座標
	 * @param initY y座標
	 * @param initOption 0:家の中、1家の外
	 */
	public Stone(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().stone.put(objId, this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.STONE;
		interval = 5;
		itemRank = ItemRank.values()[initOption];
		if( SimYukkuri.world.getCurrentMap().mapIndex == 2 || SimYukkuri.world.getCurrentMap().mapIndex == 3 || SimYukkuri.world.getCurrentMap().mapIndex == 4){
			itemRank = ItemRank.NORA;
		}
		if( SimYukkuri.world.getCurrentMap().mapIndex == 5 ||  SimYukkuri.world.getCurrentMap().mapIndex == 6 ){
			itemRank = ItemRank.YASEI;
		}
		if(itemRank == ItemRank.HOUSE) {
			value = 0;
			cost = 0;
		}
		else {
			value = 0;
			cost = 0;
		}
		
	}
	public Stone() {
		
	}

	public ItemRank getItemRank() {
		return itemRank;
	}

	public void setItemRank(ItemRank itemRank) {
		this.itemRank = itemRank;
	}
	
}

