package src.game;



import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.draw.Translate;
import src.enums.Direction;
import src.enums.Event;
import src.enums.ObjEXType;
import src.enums.Type;
import src.item.Barrier;


/**
 * 茎
 */
public class Stalk extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	private static final int images_num = 1; //このクラスの総使用画像数
	private static BufferedImage[] images = new BufferedImage[images_num*2+1];
	private static Rectangle boundary = new Rectangle();

	private Body plantYukkuri = null;		// この茎が生えてる親
	private ArrayList<Body> bindBaby = new ArrayList<Body>();	// この茎にぶら下がってる子
	
	public int amount = 0;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		final String path = "images/yukkuri/general/";
		for( int i = 0; images_num > i ; i++ ){
			images[i*2] = ImageIO.read(loader.getResourceAsStream(path+"stalk"+String.format("%03d",i+1)+".png"));
			images[i*2+1] = ModLoader.flipImage(images[i*2]);
		}
		images[images_num*2] = ImageIO.read(loader.getResourceAsStream(path + "stalk_shadow.png"));
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height - 1;
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if ( option == 0 ){
			layer[0] = images[1];
		}else{
			layer[0] = images[0];
		}
		return 1;
	}

	@Override
	public BufferedImage getShadowImage() {
		if(plantYukkuri == null) return images[2];
		return null;
	}

	public void setDirection( int dir ) {
		if ( dir == 0 ){
			option = 0;
		}else{
			option = 1;
		}
	}
	
	@Override
	public void upDate () {
		int i = 0;
		int babyX = 0;
		int babyZ = 0;
		for (Body b : getBindBaby()){
			if ( b == null ) {
				i++;
				continue;
			}
			if (option == 0) {
				babyX = (( i % 5 ) * -5 + 14);
				b.setDirection(Direction.RIGHT);
			}else{
				babyX = (( i % 5 ) * -5 + 14) * -1;
				b.setDirection(Direction.LEFT);
			}
			babyZ = (( i % 5 ) * -2 + 14);
			b.setX( getX() + babyX );
			b.setY( getY() + 1 );
			b.setZ( getZ() + babyZ );
			b.kick(0,0,0);
			i++;
		}
	}

	@Override
	public void removeListData(){
		SimYukkuri.world.currentMap.stalk.remove(this);
	}
	
	public void setPlantYukkuri( Body b ) {
		plantYukkuri = b;
	}
	
	public Body getPlantYukkuri() {
		return plantYukkuri;
	}

	@Override
	public Obj getBindObj() {
		return getPlantYukkuri();
	}

	public void setBindBaby( Body b ) {
		if ( bindBaby.size() < 5 ) {
			bindBaby.add(b);
		}
	}
	
	public ArrayList<Body> getBindBaby() {
		return bindBaby;
	}
	
	public void disBindBabys() {
		if ( plantYukkuri != null ){
			plantYukkuri.getStalks().set(plantYukkuri.getStalks().indexOf( this ), null );
		}
		for ( Body b : bindBaby ){
			if ( b != null ){
				b.setBindStalk(null) ;
			}
		}
	}

	public void setX (int X)
	{
		if (X < 0 && plantYukkuri == null) {
			x = 0;
		}
		else if (X > Translate.mapW && plantYukkuri == null) {
			x = Translate.mapW;
		}
		else {
			x = X;
		}
	}

	public void setY (int Y) {
		if (Y < 0 && plantYukkuri == null) {
			y = 0;
		}
		else if(Y > Translate.mapH && plantYukkuri == null) {
			y = Translate.mapH;
		}
		else {
			y = Y;
		}
	}

	public void setZ(int Z)
	{
		if (Z < nMostDepth && plantYukkuri == null) {
			if( bFallingUnderGround )
			{
				z = Z;
			}else{
				z = nMostDepth;				
			}
		}
		else if (Z > Translate.mapZ && plantYukkuri == null) {
			z = Translate.mapZ;
		}
		else {
			z = Z;
		}
	}

	public boolean isPlantYukkuri(){
		for ( Body b : bindBaby ){
			if ( b != null ){
				return true;
			}
		}
		return (plantYukkuri!=null);
	}

	public void eatStalk(int eatAmount)
	{
		amount -= eatAmount;
		if (amount <= 0) {
			amount = 0;
			for ( Body b : bindBaby ){
				if ( b != null ){
					b.setBindStalk(null) ;
				}
			}
			setRemoved(true);
		}
	}

	@Override
	public void grab() {
		grabbed = true;
		if ( getPlantYukkuri() != null ){
			getPlantYukkuri().removeStalk(this);
		}
		setPlantYukkuri(null);
	}
	
	public Event clockTick()
	{
		setAge(getAge() + TICK);
		if (isRemoved()) {
			removeListData();
			disBindBabys();
			return Event.REMOVED;
		}
		if (!grabbed && plantYukkuri == null) {
			if (vx != 0) {
				x += vx;
				if (x < 0) {
					x = 0;
					vx *= -1;
				}
				else if (x > Translate.mapW) {
					x = Translate.mapW;
					vx *= -1;
				}
				else if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.MAP_ITEM)) {
					x -= vx;
					vx = 0;
				}
			}
			if (vy != 0) {
				y += vy;
				if (y < 0) {
					y = 0;
					vy *= -1;
				}
				else if (y > Translate.mapH) {
					y = Translate.mapH;
					vy *= -1;
				}
				else if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.MAP_ITEM)) {
					y -= vy;
					vy = 0;
				}
			}
			if (z != 0 || vz != 0) {
				vz += 1;
				z -= vz;
				if( !bFallingUnderGround)
				{
					if (z <= nMostDepth) {
						z = nMostDepth;
						vx = 0;
						vy = 0;
						vz = 0;
					}
				}
			}
		}
		upDate();
		return Event.DONOTHING;
	}

	public Stalk(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		objType = Type.OBJECT;
		objEXType = ObjEXType.STALK;
		amount = 100*24*5;
		SimYukkuri.world.currentMap.stalk.add(this);
	}

	@Override
	public int getHitCheckObjType() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public int objHitProcess(Obj o) {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}
}

