package src.item;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;

import src.base.ObjEX;
import src.draw.ModLoader;
import src.enums.ObjEXType;
import src.enums.Type;

/***************************************************
発電機(開発途上)
*/
public class Generator extends ObjEX implements java.io.Serializable {

	private static final long serialVersionUID = -4583197623536851356L;
//	static final long serialVersionUID = 1L;
//
//	public static final int hitCheckObjType = ObjEX.YUKKURI;
	private static final int images_num = 2; //このクラスの総使用画像数
	private static BufferedImage[] images = new BufferedImage[images_num];
//	private static Rectangle boundary = new Rectangle4y()();
//	protected Random rnd = new Random();
//	protected ArrayList<Body> fuelBodyList = new ArrayList<Body>();
//	protected ArrayList<Obj> bindObjList = new ArrayList<Obj>();
//
//	private Obj o = null;
//
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "garbagechute" + File.separator + "garbagechute.png");
		images[1] = ModLoader.loadItemImage(loader, "garbagechute" + File.separator + "garbagechute_off.png");
		boundary.setWidth(images[0].getWidth(io));
		boundary.setHeight(images[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() >> 1);
	}
//
	@Override
	public int getImageLayer(BufferedImage[] layer) {
			if(enabled) layer[0] = images[0];
			else layer[0] = images[1];
		return 1;
	}
//
	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return null;
	}
//
//	public static Rectangle getBounding() {
//		return boundary;
//	}
//
//	@Override
//	public int getHitCheckObjType() {
//		return hitCheckObjType;
//	}
//
//	@Override
//	public int objHitProcess( Obj o ) {
//		if( o == null || bindObjList.contains(o) ){
//			return 0;
//		}
//		if( o instanceof Body ){
//			Body fuel = (Body)o;
//			if(!fuel.isDead() && !fuel.isBurned() ){
//				if(fuel.isDamaged())fuel.addDamage(-TICK*100);
//				if(fuel.getAttachmentSize(Fire.class) == 0){
//					fuel.giveFire();
//					fuel.addFootBakePeriod(100*24*7*10);
//					if(fuel.isAdult()){
//						Cash.addCash(340);
//					}
//					else if(fuel.isChild()){
//						Cash.addCash(40);
//					}
//					else if(fuel.isBaby()){
//						Cash.addCash(5);
//					}
//				}
//			}
//		}
//		return 0;
//	}
//
//	@Override
//	public void upDate() {
//		if( fuelBodyList == null || fuelBodyList.size() == 0){
//			return;
//		}
//
//		for( int i=fuelBodyList.size()-1; 0<=i; i-- ){
//			Body bFuel = fuelBodyList.get(i);
//			if( bFuel == null || bFuel.isRemoved() )
//			{
//				continue;
//			}
//			bFuel.setDropShadow(false);
//			if(bFuel.isDead() && bFuel.isBurned()){
//				bFuel.setLockmove(false);
//				bFuel.setFallingUnderGround(true);
//				int nZ = bFuel.getZ();
//				int tz = Translate.translateZ(nZ-1);
//				int nColX = bFuel.getH();
//				if( tz < -nColX ){
//					fuelBodyList.remove(i);
//					bFuel.remove();
//				}
//			}
//		}
//	}
//
	@Override
	public void removeListData(){
		//SimYukkuri.world.currentMap.generator.remove(this);
	}
//
//	// initOption = 1 野良用
	public Generator(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		//SimYukkuri.world.currentMap.generator.add(this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.GENERATOR;
		interval = 4;
		value = 5000;
		cost = 10;
	}
}



