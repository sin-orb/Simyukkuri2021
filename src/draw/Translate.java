package src.draw;

import java.awt.Polygon;
import java.awt.Rectangle;

import src.SimYukkuri;


/***************************************************
 座標変換クラス 

 */
public class Translate {

	// フィールド画像に占める床部分の比率 現在のback.jpgからおよその値を持ってきている
	private static final float PERS_X = 0.75f;
	private static final float ORTH_X = 1.00f;
//	private static final float wallY = 0.8f;
	private static final float wallY = 0.7f;
	
	/** 飛行種の最大高度、適当に画面外に出ない値で */
	public static final float flyLimit = 0.175f;

	/** スケール値、内部規定値*スケールがマップサイズになる */
	public static int mapScale;
	/** マップサイズ 内部計算で使用するオブジェクトの位置座標 */
	public static int mapW;
	public static int mapH;
	public static int mapZ;
	/** フィールドサイズ 実際に描画されるフィールドのピクセル値 */
	public static int fieldW;
	public static int fieldH;
	/** バックバッファサイズ */
	public static int bufferW;
	public static int bufferH;
	/** バックバッファ描画位置、サイズ */
	private static Rectangle4y displayArea = new Rectangle4y();
	private static float[] zoomTable;
	private static int zoomRate = 0;
	
	/** キャンバスサイズ 画面に描画されるウィンドウ枠の大きさ */
	public static int canvasW;
	public static int canvasH;

	// 四角のマップを台形に歪めて配置するため
	/** フィールド各Y座標でのXのスケールレートをテーブル化 */
	public static float[] rateX;
	public static int[] ofsX;
	/** Y座標は直線なので単純なテーブル引きで済む */
	public static int[] mapToFieldY;
	public static int fieldMinY;
	/** Z座標はテーブルを使わずレート計算 */
	public static float rateZ;
	
	/**
	 *  マウスクリックなどフィールドのクリック位置からマップ座標へ変換するテーブル
	 *  Xはマップ->フィールドから逆引きできるのでYのみ作成
	 */
	public static int[] fieldToMapY;
	
	/** オブジェクトの内包を簡易判定するためのシェイプ */
	public static Polygon fieldPoly;
	/**
	 * マップサイズを設定する.
	 * @param mW 幅
	 * @param mH 奥行き
	 * @param mZ 高さ
	 */
	public static final void setMapSize(int mW, int mH, int mZ) {
		mapW = mW + 1;
		mapH = mH + 1;
		mapZ = mZ + 1;
	}

	/**
	 *  dW, dH : 描画範囲のパネルサイズ
	 *  fieldSize, bufSizeを掛けるとマップ全体のサイズになる
	 * @param dW 描画範囲のパネルサイズの幅
	 * @param dH 描画範囲のパネルサイズの奥行き
	 * @param fieldSize フィールドサイズ
	 * @param bufSize バッファサイズ
	 * @param rate ズームレート
	 */
	public static final void setCanvasSize(int dW, int dH, int fieldSize, int bufSize, float[] rate) {
		canvasW = dW;
		canvasH = dH;
		
		fieldW = canvasW * fieldSize / 100;
		fieldH = canvasH * fieldSize / 100;
		
		bufferW = canvasW * bufSize / 100;
		bufferH = canvasH * bufSize / 100;
		
		zoomTable = rate;
		zoomRate = 0;
		
		setBufferZoom();
		displayArea.x = 0;
		displayArea.y = 0;
	}
	/**
	 * ズーム率を加える.
	 * @param val 加えるズーム率
	 * @return ズーム率が範囲内かどうか
	 */
	public static final boolean addZoomRate(int val) {
		boolean ret = true;

		zoomRate += val;
		if(zoomRate < 0) {
			zoomRate = 0;
			ret = false;
		} else if(zoomRate >= zoomTable.length) {
			zoomRate = zoomTable.length - 1;
			ret = false;
		}
		return ret;
	}
	/**
	 * ズーム率を設定する.
	 * @param val ズーム率
	 */
	public static final void setZoomRate(int val) {
		zoomRate = val;
		if(zoomRate < 0) zoomRate = 0;
		else if(zoomRate >= zoomTable.length) zoomRate = zoomTable.length - 1;
	}
	/**
	 * 現在のズーム率を取得する.
	 * @return 現ザーのズーム率
	 */
	public static final float getCurrentZoomRate() {
		return zoomTable[zoomRate];
	}
	/**
	 * バッファズームを設定する.
	 */
	public static final void setBufferZoom() {
		displayArea.width = (int)((float)fieldW * zoomTable[zoomRate]);
		displayArea.height = (int)((float)fieldH * zoomTable[zoomRate]);
	}
	/**
	 * バッファポジションを設定する.
	 * @param sx バックバッファ描画位置X座標
	 * @param sy バックバッファ描画位置Y座標
	 */
	public static final void setBufferPos(int sx, int sy) {
		displayArea.x = sx;
		displayArea.y = sy;
		checkDisplayLimit();
	}
	/**
	 * バッファ中央位置を設定する.
	 * @param sx バックバッファ描画位置X座標
	 * @param sy バックバッファ描画位置Y座標
	 */
	public static final void setBufferCenterPos(int sx, int sy) {
		displayArea.x = sx - (displayArea.width >> 1);
		displayArea.y = sy - (displayArea.height >> 1);
		checkDisplayLimit();
	}
	/**
	 * バッファ位置を加える.
	 * @param sx 加えるバックバッファ描画位置X座標
	 * @param sy 加えるバックバッファ描画位置Y座標
	 */
	public static final void addBufferPos(int sx, int sy) {
		displayArea.x += sx;
		displayArea.y += sy;
		checkDisplayLimit();
	}

	private static final void checkDisplayLimit() {
		if(displayArea.x < 0) displayArea.x = 0;
		else if((displayArea.x + displayArea.width) >= fieldW) displayArea.x = fieldW - displayArea.width;

		if(displayArea.y < 0) displayArea.y = 0;
		else if((displayArea.y + displayArea.height) >= fieldH) displayArea.y = fieldH - displayArea.height;
	}
	/**
	 * 描画エリアを取得する.
	 * @return 描画エリア
	 */
	public static final Rectangle4y getDisplayArea() {
		return displayArea;
	}

	/**
	 * キャンバス -> フィールド変換
	 * @param x X座標
	 * @param y Y座標
	 * @param out 変換後座標
	 */
	public static final void transCanvasToField(int x, int y, int[] out) {
		out[0] = displayArea.x + (int)(x * fieldW / canvasW * zoomTable[zoomRate]);
		out[1] = displayArea.y + (int)(y * fieldH / canvasH * zoomTable[zoomRate]);
	}

	/**
	 *  フィールド -> キャンバス変換
	 * @param x X座標
	 * @param y Y座標
	 * @param out 変換後座標
	 */
	public static final void transFieldToCanvas(int x, int y, int[] out) {
		out[0] = (int)((x - displayArea.x) * canvasW / fieldW / zoomTable[zoomRate]);
		out[1] = (int)((y - displayArea.y) * canvasH / fieldH / zoomTable[zoomRate]);
	}

	/** 
	 * セットされたマップとフィールドサイズから変換テーブルを作成
	 * @param isPers 遠近法の有無
	 */
	public static final void createTransTable(boolean isPers) {
		
		// マップの擬似パース変換チェック
		float persX = ORTH_X;
		if(isPers) {
			persX = PERS_X;
	}

		// マップ->フィールド変換テーブル
		// 壁の部分を引いたYライン数を計算
		float groundY = (float)fieldH * wallY;
		float ofsY = (float)fieldH - groundY;
		// マップの各Y値に対応するフィールドのYを計算
		float deltaY = groundY / (float)mapH; 
		mapToFieldY = new int[mapH];
		for(int y = 0; y < mapH; y++) {
			mapToFieldY[y] = (int)(ofsY + (y * deltaY));
		}
		
		// XはYが小さいほど幅が狭くなるので比率と壁のオフセットを計算
		rateX = new float[mapH];
		ofsX = new int[mapH];
		
		float deltaX = (1.0f - persX) / (float)mapH;
		for(int y = 0; y < mapH; y++) {
			rateX[y] = (float)fieldW * (persX + deltaX * (float)y);
			ofsX[y] = (int)(fieldW - rateX[y]) >> 1;
			rateX[y] /= mapW;
		}

		// フィールド->マップ変換テーブル
		fieldToMapY = new int[fieldH];
		// 壁部分は-1で埋める
		for(int y = 0; y < fieldH; y++) {
			fieldToMapY[y] = -1;
		}
		// 壁の計算
		fieldMinY = (int)((float)fieldH * (1.0f - wallY));
		// 床の計算
		groundY = (float)(fieldH - fieldMinY);
		deltaY = mapH / groundY;
		for(int y = 0; y < (fieldH - fieldMinY); y++) {
			fieldToMapY[y + fieldMinY] = (int)(y * deltaY);
		}
		
		// 高さ
		rateZ = (float)fieldH / (float)mapZ;
		
		// 内包判定用ポリゴン
		int[] polx = new int[4];
		int[] poly = new int[4];
		polx[0] = ofsX[0];				poly[0] = fieldMinY;
		polx[1] = fieldW - ofsX[0];	poly[1] = fieldMinY;
		polx[2] = fieldW - 1;			poly[2] = fieldH - 1;
		polx[3] = 0;					poly[3] = fieldH - 1;
		fieldPoly = new Polygon(polx, poly, 4);
	}

	/**
	 *  マップ->フィールド変換
	 * @param x X座標
	 * @param y Y座標
	 * @param pos Point
	 */
	public static final void translate(int x, int y, Point4y pos) {
		if(y < 0) y = 0;
		if(y >= mapH) y = mapH - 1;
		pos.x = ofsX[y] + (int)(rateX[y] * x);
		pos.y = mapToFieldY[y];
		return;
	}
	/**
	 * Z座標を変換する.
	 * @param z Z座標
	 * @return 変換後座標
	 */
	public static final int translateZ(int z) {
		return (int)((float)z * rateZ);
	}

	/**
	 *  フィールド->マップ変換 範囲外の座標はnullを返す
	 * @param x X座標
	 * @param y Y座標
	 * @return Point
	 */
	public static final Point4y invert(int x, int y) {
		if(y < 0) return null;
		if(y >= fieldH) return null;
		
		int py = fieldToMapY[y];
		if(py < 0) return null;
		int px = (int)((x - ofsX[py]) / rateX[py]);
		if(px < 0) return null;
		if(px >= mapW) return null;
		Point4y ret = new Point4y();
		ret.x = px;
		ret.y = py;
		return ret;
	}

	/**
	 *  フィールド->マップ変換 範囲外の座標は限界位置として扱う
	 * @param x X座標
	 * @param y Y座標
	 * @return Point
	 */
	public static final Point4y invertLimit(int x, int y) {
		if(y < 0) y = 0;
		if(y >= fieldH) y = fieldH - 1;
		
		int py = fieldToMapY[y];
		if(py < 0) py = 0;
		int px = (int)((x - ofsX[py]) / rateX[py]);
		if(px < 0) px = 0;
		if(px >= mapW) px = mapW - 1;
		Point4y ret = new Point4y();
		ret.x = px;
		ret.y = py;
		return ret;
	}
	/**
	 * フィールド内かどうか
	 * @param x X座標
	 * @param y Y座標
	 * @return フィールド内かどうか
	 */
	public static final boolean inInvertLimit(int x, int y){
		if(y < 0) y = 0;
		if(y >= fieldH) y = fieldH - 1;
		
		int py = fieldToMapY[y];
		if(py < 0)
		{
			return false;
		}
		
		int px = (int)((x - ofsX[py]) / rateX[py]);
		if(px < 0)
		{
			return false;
		}
		if(px >= mapW)
		{
			return false;
		}
		
		return true;
	}

	/**
	 *  オブジェクト用 フィールド->マップ変換 立っているとみなしてYは座標制限に猶予がある
	 * @param x X座標
	 * @param y Y座標
	 * @param pivX X座標MIN/MAX猶予
	 * @param margin Y座標猶予
	 * @return Point
	 */
	public static final Point4y invertObject(int x, int y, int pivX, int margin) {
		int minY = y - margin;
		int maxY = y + margin;
		if(minY < fieldMinY) {
			minY = fieldMinY;
			y = minY + margin;
		} else if(maxY >= fieldH) {
			maxY = fieldH - 1;
			y = maxY - margin;
		}
		int py = fieldToMapY[y];

		int minX = x - pivX;
		int maxX = x + pivX;
		if(minX < ofsX[py]) {
			minX = ofsX[py];
			x = minX + pivX;
		} else if(maxX >= (fieldW - ofsX[py])) {
			maxX = fieldW - ofsX[py] - 1;
			x = maxX - pivX;
		}
		int px = (int)((x - ofsX[py]) / rateX[py]);
		Point4y ret = new Point4y();
		ret.x = px;
		ret.y = py;
		return ret;
	}

	/**
	 *  床配置物用 フィールド->マップ変換 設置物が完全にフィールド内に納まるように座標を制限する
	 * @param x X座標
	 * @param y Y座標
	 * @param pivX X座標猶予
	 * @param pivY Y座標猶予
	 * @param pos 位置
	 */
	public static final void invertGround(int x, int y, int pivX, int pivY, Point4y pos) {
		int minY = y - pivY;
		int maxY = y + pivY;
		if(minY < fieldMinY) {
			minY = fieldMinY;
			y = minY + pivY;
		} else if(maxY >= fieldH) {
			maxY = fieldH - 1;
			y = maxY - pivY;
		}
		int py = fieldToMapY[y];

		int minX = x - pivX;
		int maxX = x + pivX;
		if(minX < ofsX[py]) {
			minX = ofsX[py];
			x = minX + pivX;
		} else if(maxX >= (fieldW - ofsX[py])) {
			maxX = fieldW - ofsX[py] - 1;
			x = maxX - pivX;
		}
		int px = (int)((x - ofsX[py]) / rateX[py]);
		pos.x = px;
		pos.y = py;
	}

	/**
	 *  空中物用 フィールド->マップ変換 ここで返す値はx,z
	 * @param x X座標
	 * @param y Y座標
	 * @param z Z座標
	 * @param pivX X座標猶予
	 * @param pos 位置
	 */
	public static final void invertFlying(int x, int y, int z, int pivX, Point4y pos) {
		int py = fieldToMapY[y];
		if(py < 0) py = 0;

		int minX = x - pivX;
		int maxX = x + pivX;
		if(minX < ofsX[py]) {
			minX = ofsX[py];
			x = minX + pivX;
		} else if(maxX >= (fieldW - ofsX[py])) {
			maxX = fieldW - ofsX[py] - 1;
			x = maxX - pivX;
		}
		int px = (int)((x - ofsX[py]) / rateX[py]);
		if(z < 0) z = 0;
		pos.x = px;
		pos.y = z * mapZ / fieldH;
	}

	/**
	 *  移動量フィールド->マップ変換
	 * @param x X座標
	 * @param y Y座標
	 * @param pos 位置
	 */
	public static final void invertDelta(int x, int y, Point4y pos) {
		if(y < 0) y = 0;
		if(y >= fieldH) y = fieldH - 1;

		int py = fieldToMapY[y];
		if(py < 0) py = 0;
		int px = (int)(x / rateX[py]);
		if(px < 0) px = 0;
		if(px >= mapW) px = mapW - 1;
		pos.x = px;
		pos.y = py;
	}

	/**
	 *  マップY座標から画像サイズの距離を計算
	 * @param x X座標
	 * @param mapY マップY座標
	 * @return 距離
	 */
	public static final int invertX(int x, int mapY) {
		if(mapY < 0) mapY = 0;
		if(mapY >= mapH) mapY = mapH - 1;
		return (int)((float)x / rateX[mapY]);
	}

	/**
	 *  アイテム配置座標の計算
	 * @param mx X座標
	 * @param my Y座標
	 * @param rect 矩形
	 * @return 位置
	 */
	public static Point4y calcObjctPutPoint(int mx, int my, Rectangle4y rect) {
		Point4y ret = null;

		int colH = rect.height - rect.y;
		Rectangle r = new Rectangle(mx - rect.x, my - colH, rect.width, colH << 1);
		if(fieldPoly.contains(r)) {
			ret = invert(mx, my);
		}
		return ret;
	}
	/**
	 * Y座標変換
	 * @param y Y座標
	 * @return 変換後Y座標
	 */
	public static final int invertY(int y) {
		return (int)((float)y * (float)mapH / (float)fieldH);
	}
	/**
	 * 背景のY座標変換
	 * @param y Y座標
	 * @return 変換後Y座標
	 */
	public static final int invertBgY(int y) {
		return (int)((float)y * (float)mapH / (float)fieldH * wallY);
	}
	/**
	 * Z座標変換
	 * @param z Z座標
	 * @return 変換後Z座標
	 */
	public static final int invertZ(int z) {
		return (int)((float)z * (float)mapZ / (float)fieldH);
	}
	/**
	 * サイズを返却する.
	 * @param size サイズ
	 * @return サイズ
	 */
	public static final int transSize(int size) {
		return size;
	}

	/**
	 *  2点間の距離計算 ルートを省いているので実際の距離にはならないので注意。また、帰ってくる値も距離の２乗なので注意
	 * @param x1 X座標1
	 * @param y1 Y座標1
	 * @param x2 X座標2
	 * @param y2 Y座標2
	 * @return 距離の2乗
	 */
	public static final int distance(int x1, int y1, int x2, int y2) {
		return ((x2 - x1)*(x2 - x1)+(y2 - y1)*(y2 - y1));
	}
	
	/**
	 *  飛行種の最大高度マップZを返す
	 * @return 飛行種の最大高度マップZ
	 */
	public static final int getFlyHeightLimit() {
		return (int)(mapZ * Translate.flyLimit);
	}
	
	/**
	 *  2点間の距離（正式）
	 * @param x1 X座標1
	 * @param y1 Y座標1
	 * @param x2 X座標2
	 * @param y2 Y座標2
	 * @return 距離
	 */
	public static final int getRealDistance(int x1, int y1, int x2, int y2) {
		return (int)Math.sqrt(((x2 - x1)*(x2 - x1)+(y2 - y1)*(y2 - y1)));
	}
    
	/**
	 *  2点間の角度
	 * @param x X座標1
	 * @param y Y座標1
	 * @param x2 X座標2
	 * @param y2 Y座標2
	 * @return 2点間の角度
	 */
	public static final double getRadian(double x, double y, double x2, double y2) {
	    double radian = Math.atan2(y2 - y, x2 - x);
		return radian;
	}

	/**
	 *  角度と距離から点２を計算
	 * @param x X座標
	 * @param y Y座標
	 * @param radius 半径
	 * @param radian 角度
	 * @return 位置
	 */
	public static final Point4y getPointByDistAndRad( int x, int y, int radius, double radian)
	{
		Point4y p1 = new Point4y();
		p1.x = x +(int)(Math.cos(radian) * radius);
		p1.y = y + (int)(Math.sin(radian) * radius);
		return p1;
	}
	/**
	 * ポリゴン位置を取得する.
	 * @param sx フィールドX座標
	 * @param sy フィールドY座標
	 * @param ex マップX座標
	 * @param ey マップY座標
	 * @param anPointX ポリゴン位置X座標
	 * @param anPointY ポリゴン位置Y座標
	 */
	public static final void getPolygonPoint( int sx, int sy, int ex, int ey, int[] anPointX, int[] anPointY )
	{
		// フィールド座標が渡ってくるのでマップ座標も計算しておく
		Point4y pos;
		pos = Translate.invertLimit(sx, sy);
		int mSX = Math.max(0, Math.min(pos.x, Translate.mapW));
		int mSY = Math.max(0, Math.min(pos.y, Translate.mapH));
		
		pos = Translate.invertLimit(ex, ey);
		int mEX = Math.max(0, Math.min(pos.x, Translate.mapW));
		int mEY = Math.max(0, Math.min(pos.y, Translate.mapH));

		// EX,EYのマップ座標xからSYにおけるフィールド座標xを取得する
		Point4y posInv = new Point4y();
		Translate.translate(mEX, mSY, posInv);
		int fieldSX2 = posInv.x;
		Translate.translate(mSX, mEY, posInv);
		int fieldSX3 = posInv.x;
		// マップ座標で調整されたフィールド座標を再取得する
		Translate.translate(mSX, mSY, posInv);
		int nsx = posInv.x;
		int nsy = posInv.y;
		Translate.translate(mEX, mEY, posInv);
		int nex = posInv.x;
		int ney = posInv.y;
		
		anPointX[0] = nsx;
		anPointY[0] = nsy;
		anPointX[1] = fieldSX2;
		anPointY[1] = nsy;
		anPointX[3] = fieldSX3;
		anPointY[3] = ney;
		anPointX[2] = nex;
		anPointY[2] = ney;
	}
	/**
	 * 移動後座標計算
	 * @param sx フィールドX座標
	 * @param sy フィールドY座標
	 * @param ex マップX座標
	 * @param ey マップY座標
	 * @param firstx 初期X座標
	 * @param firsty  初期Y座標
	 * @param nextx 移動後X座標
	 * @param nexty 移動後Y座標
	 * @param anPointX 計算後X座標
	 * @param anPointY 計算後Y座標
	 */
	public static final void getMovedPoint( int sx, int sy, int ex, int ey, int firstx, int firsty, int nextx, int nexty, int[] anPointX, int[] anPointY )
	{
		int nDecX = nextx - firstx;
		int nDecY = nexty - firsty;
	
		// フィールド座標が渡ってくるのでマップ座標も計算しておく
		Point4y pos;
		pos = Translate.invertLimit(sx, sy);
		int mSX = Math.max(0, Math.min(pos.x, Translate.mapW));
		int mSY = Math.max(0, Math.min(pos.y, Translate.mapH));
		
		pos = Translate.invertLimit(ex, ey);
		int mEX = Math.max(0, Math.min(pos.x, Translate.mapW));
		int mEY = Math.max(0, Math.min(pos.y, Translate.mapH));

		mSX += nDecX;
		mEX += nDecX;
		mSY += nDecY;
		mEY += nDecY;
		
		int nTemp = 0;
		if( mEX < mSX)
		{
			nTemp = mSX;
			mSX = mEX;
			mEX = nTemp;
		}
		if( mEY < mSY)
		{
			nTemp = mSY;
			mSY = mEY;
			mEY = nTemp;
		}		
		// マップ座標で調整されたフィールド座標を再取得する
		Point4y posInv = new Point4y();
		Translate.translate(mSX, mSY, posInv);
		anPointX[0] = posInv.x;
		anPointY[0] = posInv.y;
		Translate.translate(mEX, mEY, posInv);
		anPointX[1] = posInv.x;
		anPointY[1] = posInv.y;
	}
	/**
	 * フィールドの限界位置をマップ用に取得する.
	 * @param x X座標
	 * @param y Y座標
	 * @return 位置
	 */
	public static Point4y getFieldLimitForMap(int x, int y )
	{
		// フィールド座標が渡ってくるのでマップ座標も計算しておく
		Point4y pos = Translate.invertLimit(x, y);
		int mSX = Math.max(0, Math.min(pos.x, Translate.mapW));
		int mSY = Math.max(0, Math.min(pos.y, Translate.mapH));
		
		// マップ座標で調整されたフィールド座標を再取得する
		Point4y retPos = new Point4y();
		Translate.translate(mSX, mSY, retPos);
		return retPos;
	}
	
	/**
	 *  壁の数取得
	 * @param x X座標
	 * @param y Y座標
	 * @return 壁の数
	 */
	public static int getCurrentWallMapNum( int x, int y )
	{
		int mSX = Math.max(0, Math.min(x, Translate.mapW));
		int mSY = Math.max(0, Math.min(y, Translate.mapH));
					
		return SimYukkuri.world.getCurrentMap().wallMap[mSX][mSY];
	}
	/**
	 * 壁の数設定
	 * @param x X座標
	 * @param y Y座標
	 * @param num 数
	 */
	public static void setCurrentWallMapNum( int x, int y , int num)
	{
		int mSX = Math.max(0, Math.min(x, Translate.mapW));
		int mSY = Math.max(0, Math.min(y, Translate.mapH));
					
		SimYukkuri.world.getCurrentMap().wallMap[mSX][mSY] = num;
	}
	
	/**
	 * フィールドの数取得
	 * @param x X座標
	 * @param y Y座標
	 * @return 数
	 */
	public static int getCurrentFieldMapNum( int x, int y )
	{
		int mSX = Math.max(0, Math.min(x, Translate.mapW));
		int mSY = Math.max(0, Math.min(y, Translate.mapH));
					
		return SimYukkuri.world.getCurrentMap().fieldMap[mSX][mSY];
	}
	
	/**
	 * フィールドの数設定
	 * @param x X座標
	 * @param y Y座標
	 * @param num 数
	 */
	public static void setCurrentFieldMapNum( int x, int y , int num)
	{
		int mSX = Math.max(0, Math.min(x, Translate.mapW));
		int mSY = Math.max(0, Math.min(y, Translate.mapH));
					
		SimYukkuri.world.getCurrentMap().fieldMap[mSX][mSY] = num;
	}
}
