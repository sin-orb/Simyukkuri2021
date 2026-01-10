package src.item;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.beans.Transient;
import java.io.Serializable;
import java.util.List;

import src.SimYukkuri;
import src.draw.Color4y;
import src.draw.Point4y;
import src.draw.Translate;
import src.system.FieldShapeBase;
import src.system.MapPlaceData;

/* 
 *    Copyright 2013 Mimisuke
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/***************************************************
 * 壁
 * <br>
 * これはほかのアイテムと違い、ObjEXを継承していないので注意。
 */
public class Barrier extends FieldShapeBase implements Serializable {
	private static final long serialVersionUID = -1750205300136035405L;
	/** 壁の線のデザイン */
	public static final Stroke WALL_STROKE = new BasicStroke(3.0f);
	/** 最小サイズ */
	private static final int MIN_SIZE = 1;
	/** 壁の色 */
	private Color4y color;
	/** 壁の属性 */
	private int attribute;

	/** 色の取得 */
	public Color4y getColor() {
		return color;
	}

	@Override
	public int getAttribute() {
		return attribute;
	}

	@Override
	@Transient
	public int getMinimumSize() {
		return MIN_SIZE;
	}

	/**
	 * コンストラクタ
	 *
	 * @param fsx  設置起点のX座標
	 * @param fsy  設置起点のY座標
	 * @param fex  設置終点のX座標
	 * @param fey  設置終点のY座標
	 * @param type 属性(どんな壁か)
	 */
	public Barrier(int fsx, int fsy, int fex, int fey, int type) {
		// フィールド座標が渡ってくるのでマップ座標も計算しておく
		fieldSX = fsx;
		fieldSY = fsy;
		fieldEX = fex;
		fieldEY = fey;
		// フィールド座標が渡ってくるのでマップ座標も計算しておく
		Point4y pos;
		pos = Translate.invertLimit(fieldSX, fieldSY);
		mapSX = Math.max(0, Math.min(pos.getX(), Translate.mapW));
		mapSY = Math.max(0, Math.min(pos.getY(), Translate.mapH));

		pos = Translate.invertLimit(fieldEX, fieldEY);
		mapEX = Math.max(0, Math.min(pos.getX(), Translate.mapW));
		mapEY = Math.max(0, Math.min(pos.getY(), Translate.mapH));

		attribute = type;
		switch (type) {
			case BARRIER_GAP_MINI:
				color = new Color4y(255, 255, 0, 255);
				break;
			case BARRIER_GAP_BIG:
				color = new Color4y(255, 200, 0, 255);
				break;
			case BARRIER_NET_MINI:
				color = new Color4y(255, 175, 175, 255);
				break;
			case BARRIER_NET_BIG:
				color = new Color4y(255, 0, 255, 255);
				break;
			case BARRIER_WALL:
				color = new Color4y(128, 128, 128, 255);
				break;
			case BARRIER_ITEM:
				color = new Color4y(0, 255, 0, 255);
				break;
			case BARRIER_NOUNUN:
				color = new Color4y(255, 0, 0, 255);
				break;
			case BARRIER_KEKKAI:
				color = new Color4y(192, 192, 192, 255);
				break;
		}

		MapPlaceData.setWallLine(SimYukkuri.world.getCurrentMap().wallMap, mapSX, mapSY, mapEX, mapEY, true, attribute);
		SimYukkuri.world.getCurrentMap().barrier.add(this);
	}

	public Barrier() {

	}

	/** プレビューの線の描画 */
	public static void drawPreview(Graphics2D g2, int sx, int sy, int ex, int ey) {
		g2.drawLine(sx, sy, ex, ey);
	}

	@Override
	public void drawShape(Graphics2D g2) {
		g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
		g2.drawLine(fieldSX, fieldSY, fieldEX, fieldEY);
	}

	/** 除去 */
	public static void clearBarrier(Barrier b) {
		int x1 = b.getMapSX();
		int y1 = b.getMapSY();
		int x2 = b.getMapEX();
		int y2 = b.getMapEY();
		if (SimYukkuri.world.getCurrentMap().barrier.remove(b)) {
			MapPlaceData.setWallLine(SimYukkuri.world.getCurrentMap().wallMap, x1, y1, x2, y2, false, b.attribute);
		}
	}

	/** 壁に引っかかるかのチェック */
	public static boolean onBarrier(int cx, int cy, int thx, int thy, int attr) {
		MapPlaceData tmp = SimYukkuri.world.getCurrentMap();
		int sx = Math.max(0, cx - thx / 2);
		int sy = Math.max(0, cy - thy / 2);
		int ex = Math.min(cx + thx / 2, Translate.mapW);
		int ey = Math.min(cy + thy / 2, Translate.mapH);
		for (int x = sx; x < ex; x++) {
			for (int y = sy; y < ey; y++) {
				if ((tmp.wallMap[x][y] & attr) != 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * ある点が壁の上かの判定
	 *
	 * @param cx        ある点のX座標
	 * @param cy        ある点のY座標
	 * @param thickness 壁の厚さ
	 * @return ある点が壁の上か
	 */
	public static Barrier getBarrier(int cx, int cy, int thickness) {
		List<Barrier> barrierList = SimYukkuri.world.getCurrentMap().barrier;

		for (Barrier b : barrierList) {
			int x1 = b.getMapSX();
			int y1 = b.getMapSY();
			int x2 = b.getMapEX();
			int y2 = b.getMapEY();
			int distance = (int) Math.sqrt(Translate.distance(x1, y1, x2, y2));
			double deltaX = (double) (x2 - x1) / (double) distance;
			double deltaY = (double) (y2 - y1) / (double) distance;
			int sX = x1;
			int sY = y1;
			for (int t = 0; t <= distance; t++) {
				int x = sX + (int) (deltaX * t);
				int y = sY + (int) (deltaY * t);
				if ((Math.abs(x - cx) <= thickness) && (Math.abs(y - cy) <= thickness)) {
					return b;
				}
			}
		}
		return null;
	}

	/**
	 * 壁が動線(視線)上にあるかどうか
	 * 
	 * @param x1   起点のX座標
	 * @param y1   起点のY座標
	 * @param x2   終点のX座標
	 * @param y2   終点のY座標
	 * @param attr さえぎる壁の属性
	 * @return 壁が動線(視線)上にあるかどうか
	 */
	public static boolean acrossBarrier(int x1, int y1, int x2, int y2, int attr) {
		MapPlaceData tmp = SimYukkuri.world.getCurrentMap();

		x1 = Math.max(0, Math.min(x1, Translate.mapW));
		x2 = Math.max(0, Math.min(x2, Translate.mapW));
		y1 = Math.max(0, Math.min(y1, Translate.mapH));
		y2 = Math.max(0, Math.min(y2, Translate.mapH));

		int distance = (int) Math.sqrt(Translate.distance(x1, y1, x2, y2));
		double deltaX = (double) (x2 - x1) / (double) distance;
		double deltaY = (double) (y2 - y1) / (double) distance;
		int sX = x1;
		int sY = y1;
		for (int t = 0; t <= distance; t++) {
			int x = sX + (int) (deltaX * t);
			int y = sY + (int) (deltaY * t);
			if ((tmp.wallMap[x][y] & attr) != 0) {
				return true;
			}
		}
		return false;
	}
}
