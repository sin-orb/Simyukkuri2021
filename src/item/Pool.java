package src.item;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.draw.ModLoader;
import src.draw.Point4y;
import src.draw.Translate;
import src.enums.AgeState;
import src.system.FieldShapeBase;
import src.system.ItemMenu.ShapeMenu;
import src.system.ItemMenu.ShapeMenuTarget;
import src.system.MapPlaceData;

/***************************************************
 * 池
 */
public class Pool extends FieldShapeBase implements Serializable {

	private static final long serialVersionUID = 745411694776554936L;
	/**池のふちどりの色*/
	public static final Color ROCK_COLOR = new Color(200, 140, 30);
	private static final int MIN_SIZE = 8;

	private static BufferedImage images;
	private static TexturePaint texture;
	private int[] anWaterPointX = new int[4];
	private int[] anWaterPointY = new int[4];

	/**池に捕まってるオブジェクトのリスト*/
	List<Obj> bindObjList = new LinkedList<Obj>();
	/**池の深さの列挙*/
	public enum DEPTH {
		NONE,	// エリア外
		EDGE,	// 角でまだ入ってない
		SHALLOW,// 浅い
		DEEP,	// 深い
		}
	/**画像ロード*/
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		images = ModLoader.loadItemImage(loader, "pool" + File.separator + "pool.png");
		texture = new TexturePaint(images, new Rectangle2D.Float(0, 0, images.getWidth(), images.getHeight()));
	}
	
	@Override
	public ShapeMenuTarget hasShapePopup() {
		return ShapeMenuTarget.POOL;
	}

	@Override
	public void executeShapePopup(ShapeMenu menu) {

		List<Pool> list = SimYukkuri.world.getCurrentMap().pool;
		int pos;
		
		switch(menu) {
			case SETUP:
				break;
			case TOP:
				list.remove(this);
				list.add(0, this);
				break;
			case UP:
				pos = list.indexOf(this);
				if(pos > 0) {
					list.remove(this);
					list.add(pos - 1, this);
				}
				break;
			case DOWN:
				pos = list.indexOf(this);
				if(pos < (list.size() - 1)) {
					list.remove(this);
					list.add(pos + 1, this);
				}
				break;
			case BOTTOM:
				list.remove(this);
				list.add(this);
				break;
			default:
				break;
		}
	}

	@Override
	@Transient
	public int getAttribute() {
		return FIELD_POOL;
	}

	@Override
	@Transient
	public int getMinimumSize() {
		return MIN_SIZE;
	}
	/**プレビューラインの描画*/
	public static void drawPreview(Graphics2D g2, int sx, int sy, int ex, int ey) {
		int[] anPointX = new int[4];
		int[] anPointY = new int[4];
		Translate.getPolygonPoint(sx, sy, ex, ey, anPointX, anPointY);

		g2.drawPolygon(anPointX, anPointY, 4 );
	}
	
	@Override
	public void drawShape(Graphics2D g2) {
		int[] anPointX = new int[4];
		int[] anPointY = new int[4];
		Translate.getPolygonPoint(fieldSX, fieldSY, fieldEX, fieldEY, anPointX, anPointY);

		g2.setPaint(ROCK_COLOR);
		g2.fillPolygon(anPointX, anPointY, 4 );

		Translate.getPolygonPoint(fieldSX+8, fieldSY+8, fieldEX-8, fieldEY-8, anWaterPointX, anWaterPointY);
		g2.setPaint(texture);
		g2.fillPolygon(anWaterPointX, anWaterPointY, 4 );
	}
	/**
	 * コンストラクタ
	 * @param fsx 設置起点のX座標
	 * @param fsy 設置起点のY座標
	 * @param fex 設置終点のX座標
	 * @param fey 設置終点のY座標
	 */
	public Pool(int fsx, int fsy, int fex, int fey) {
		Point4y pS = Translate.getFieldLimitForMap( fsx, fsy );
		Point4y pE = Translate.getFieldLimitForMap( fex, fey );
		fieldSX = pS.x;
		fieldSY = pS.y;
		fieldEX = pE.x;
		fieldEY = pE.y;
		
		int[] anPointBaseX = new int[2];
		int[] anPointBaseY = new int[2];
		Translate.getMovedPoint(fieldSX, fieldSY, fieldEX, fieldEY, 0, 0, 0, 0, anPointBaseX, anPointBaseY );
		
		// フィールド座標が渡ってくるのでマップ座標も計算しておく
		Point4y pos = Translate.invertLimit(anPointBaseX[0], anPointBaseY[0]);
		mapSX = Math.max(0, Math.min(pos.x, Translate.mapW));
		mapSY = Math.max(0, Math.min(pos.y, Translate.mapH));
		
		pos = Translate.invertLimit(anPointBaseX[1], anPointBaseY[1]);
		mapEX = Math.max(0, Math.min(pos.x, Translate.mapW));
		mapEY = Math.max(0, Math.min(pos.y, Translate.mapH));

		// 規定サイズと位置へ合わせる
		if((mapEX - mapSX) < MIN_SIZE) mapEX = mapSX + MIN_SIZE;
		if((mapEY - mapSY) < MIN_SIZE) mapEY = mapSY + MIN_SIZE;
		if(mapEX > Translate.mapW) {
			mapSX -= (mapEX - Translate.mapW);
			mapEX -= (mapEX - Translate.mapW);
		}
		if(mapEY > Translate.mapH) {
			mapSY -= (mapEY - Translate.mapH);
			mapEY -= (mapEY - Translate.mapH);
		}

		Point4y f = new Point4y();
		Translate.translate(mapSX, mapSY, f);
		fieldSX = f.x;
		fieldSY = f.y;
		Translate.translate(mapEX, mapEY, f);
		fieldEX = f.x;
		fieldEY = f.y;

		fieldW = fieldEX - fieldSX + 1;
		fieldH = fieldEY - fieldSY + 1;
		mapW = mapEX - mapSX + 1;
		mapH = mapEY - mapSY + 1;

		SimYukkuri.world.getCurrentMap().pool.add(this);
		MapPlaceData.setFiledFlag(SimYukkuri.world.getCurrentMap().fieldMap, mapSX, mapSY, mapW, mapH, true, FIELD_POOL);
	}
	public Pool() {
		
	}

	/** フィールド座標にあるシェイプ取得*/
	public static Pool getPool(int fx, int fy) {
		
		for(Pool bc :SimYukkuri.world.getCurrentMap().pool) {
			if(bc.fieldSX <= fx && fx <= bc.fieldEX
					&& bc.fieldSY <= fy && fy <= bc.fieldEY) {
				return bc;
			}
		}
		return null;
	}
	
	/** 削除*/
	public static void deletePool(Pool b) {
		MapPlaceData.setFiledFlag(SimYukkuri.world.getCurrentMap().fieldMap, b.mapSX, b.mapSY, b.mapW, b.mapH, false, FIELD_POOL);
		SimYukkuri.world.getCurrentMap().pool.remove(b);
		// 重なってた部分の復元
		for(Pool bc :SimYukkuri.world.getCurrentMap().pool) {
			MapPlaceData.setFiledFlag(SimYukkuri.world.getCurrentMap().fieldMap, bc.mapSX, bc.mapSY, bc.mapW, bc.mapH, true, FIELD_POOL);
		}
	}
	/**
	 * ある点が畑の範囲内かどうか
	 * @param inX ある点のX座標
	 * @param inY ある点Y座標
	 * @param bIsField 渡された座標がフィールド座標かどうか
	 */
    public boolean checkContain( int inX, int inY , boolean bIsField )
    {
    	int nX = inX;
    	int nY = inY;
		if(bIsField )
		{
			Point4y pos = Translate.invertLimit( inX, inY );	
			nX = pos.x;
			nY = pos.y;
		}
		
		Point4y posFirst = Translate.invertLimit( anWaterPointX[0], anWaterPointY[0] );
		Point4y posSecond = Translate.invertLimit( anWaterPointX[2], anWaterPointY[2] );
		if( posFirst != null && posSecond != null)
		{
			if( posFirst.x <= nX && nX <= posSecond.x && posFirst.y <= nY && nY <= posSecond.y )
			{
                return true;
			}
		}
    	return false;
    }
    
	/**
	 * 渡されたオブジェクトが畑の中にあるかを判定
	 * <br>動作はobjHitProcess( Obj o )で
	 */
	public boolean checkHitObj(Obj o ) {
		if( o == null )
		{
			return false;
		}

		if( !checkContain(o.getX(), o.getY(), false))
		{
			return false;
		}
		
		List<BeltconveyorObj>  beltList = new LinkedList<>(SimYukkuri.world.getCurrentMap().beltconveyorObj.values());
		if( beltList != null && beltList.size() != 0 )
		{
			for( BeltconveyorObj belt: beltList )
			{
				// ベルトコンベア上なら池にまだ入ってない
				if( belt.checkContain( o.getX(), o.getY(), false ) )
				{
					return false;
				}
			}
		}
		// エリア内
		return true;
	}
	/**当たり判定されたオブジェクトへの処理*/
	public int objHitProcess( Obj o ) {
		// 空中は無視
		int nZ = o.getZ();
		if( 0 < nZ )
		{
			return 0;
		}

		boolean bIsInWater = false;
		o.setInPool(true);
		DEPTH eDepth = checkArea(o.getX(), o.getY());
		switch(eDepth){
		case EDGE:
			o.setFallingUnderGround(false);
			o.setMostDepth(0);
			if( nZ < 0 )
			{
				o.setCalcZ(0);
			}
			break;
		case SHALLOW:
			bIsInWater = true;
			// すこし沈む
			if( !o.getFallingUnderGround() )
			{
				o.setMostDepth(-1);
			}

			if( nZ == 0 )
			{
				o.setCalcZ(-1);
			}
			break;
		case DEEP:
			bIsInWater = true;
			// もうすこし沈む
			if( !o.getFallingUnderGround())
			{
				o.setMostDepth(-2);
			}
			if( nZ == 0 || nZ == -1)
			{
				o.setCalcZ(-2);
			}
			break;
		default:
			break;
		}
		
		if( o instanceof Body )
		{
			Body bodyTarget = (Body) o;
			AgeState eAge = bodyTarget.getBodyAgeState();
			boolean bLikeWater = bodyTarget.isLikeWater();
			int nLimit = -2;
			
			switch(eAge){
				case BABY:
					nLimit = 1;
					break;
				case CHILD:
					nLimit = 2;
					break;
				case ADULT:
					nLimit = 3;
					break;
				default:
					break;
			}
			
			switch(eDepth){
				case SHALLOW:
					bIsInWater = true;
					if(SimYukkuri.RND.nextInt(70) == 0 || !bodyTarget.isWet())
					{
						bodyTarget.inWater(eDepth);
					}
					break;
				case DEEP:
					bIsInWater = true;
					if(SimYukkuri.RND.nextInt(40) == 0 || !bodyTarget.isWet())
					{
						bodyTarget.inWater(eDepth);
					}
					break;
				default:
					break;
			}

			if( bIsInWater )
			{	
				int tz = Translate.translateZ(nZ-1);
				int nH = o.getH();

				if( !bLikeWater)
				{
					// ある程度沈むと大ダメージ
					if( tz < -nH/3 && SimYukkuri.RND.nextInt(10 + nLimit*5) == 0)
					{
						bodyTarget.addDamage(bodyTarget.getDamageLimit()/4);
					}
					
					// 水深が深いと動けなくなる
					if( nZ < -nLimit )
					{
						bodyTarget.setLockmove(true);
					}
					
					int nRndDeepInWater = 50;
					// 溶けている場合、沈む確率UP
					if( bodyTarget.isMelt() )
					{
						nRndDeepInWater = nRndDeepInWater / 2;
					}
					
					// 死んでいる場合、沈む確率UP
					if( bodyTarget.isDead() )
					{
						nRndDeepInWater = nRndDeepInWater / 2;
					}
					
					if( SimYukkuri.RND.nextInt(nRndDeepInWater) == 0 )
					{
						bodyTarget.setFallingUnderGround(true);
						o.setMostDepth(nZ-1);
						o.setCalcZ(nZ-1);
					}
				}
				
				// 溶けて消える
				if( tz < -nH )
				{
					o.remove();
				}
			}
		}else{
			// 溶ける
			if( Translate.translateZ(nZ) < -10 )
			{
				o.remove();
			}
		}

		return 0;
	}
	/**
	 * ある点の池の深さを取得
	 * @param x ある点のX座標
	 * @param y ある点のY座標
	 * @return ある点の池の深さ
	 */
	public DEPTH checkArea(int x, int y)
	{
		DEPTH eDepthW = DEPTH.NONE;
		DEPTH eDepthH = DEPTH.NONE;
		DEPTH eDepthRet = DEPTH.NONE;
		int nEdgeWidth = 10;
		if( mapEX - mapSX < nEdgeWidth )
		{
			nEdgeWidth = 0;
		}
		
		int nEdgeHeight = 5;
		if( mapEY - mapSY < nEdgeHeight )
		{
			nEdgeHeight = 0;
		}

		//--------------------------------------
		// 左右判定
		if( x < mapSX || mapEX < x){
			eDepthW = DEPTH.NONE;
		}else if( (mapSX <= x && x < mapSX+nEdgeWidth) ||  (mapEX - nEdgeWidth < x && x <= mapEX)){
			eDepthW = DEPTH.EDGE;
		}else if( (mapSX + nEdgeWidth <= x && x < mapSX + nEdgeWidth*2) || (mapEX - nEdgeWidth*2 < x && x <= mapEX - nEdgeWidth) ){
			eDepthW = DEPTH.SHALLOW;
		}else if( mapSX + nEdgeWidth*2 <= x && x < mapEX - nEdgeWidth*2 ){
			eDepthW = DEPTH.DEEP;
		}
		//--------------------------------------
		// 上下判定
		if( y < mapSY || mapEY < y){
			eDepthH = DEPTH.NONE;
		}else if( (mapSY <= y && y < mapSY+nEdgeHeight) ||  (mapEY - nEdgeHeight < y && y <= mapEY)){
			eDepthH = DEPTH.EDGE;
		}else if( (mapSY + nEdgeHeight <= y && y < mapSY + nEdgeHeight*2) || (mapEY - nEdgeHeight*2 < y && y <= mapEY - nEdgeHeight) ){
			eDepthH = DEPTH.SHALLOW;
		}else if( mapSY + nEdgeHeight*2 <= y && y < mapEY - nEdgeHeight*2 ){
			eDepthH = DEPTH.DEEP;
		}
		
		// 小さい方(浅い方)にあわせる
		if( eDepthW == eDepthH || eDepthW.ordinal() < eDepthH.ordinal() )
		{
			eDepthRet = eDepthW;
		}else{
			eDepthRet = eDepthH;
		}
		
		return eDepthRet;
	}

	public int[] getAnWaterPointX() {
		return anWaterPointX;
	}

	public void setAnWaterPointX(int[] anWaterPointX) {
		this.anWaterPointX = anWaterPointX;
	}

	public int[] getAnWaterPointY() {
		return anWaterPointY;
	}

	public void setAnWaterPointY(int[] anWaterPointY) {
		this.anWaterPointY = anWaterPointY;
	}

	public List<Obj> getBindObjList() {
		return bindObjList;
	}

	public void setBindObjList(List<Obj> bindObjList) {
		this.bindObjList = bindObjList;
	}
	
}



