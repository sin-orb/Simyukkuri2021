package src.item;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.io.Serializable;
import java.util.ArrayList;

import src.SimYukkuri;
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
壁
*/
public class Barrier extends FieldShapeBase implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final Stroke WALL_STROKE = new BasicStroke(3.0f);
	private static final int MIN_SIZE = 1;
	
	private Color color;
	private int attribute;

	public Color getColor() {
		return color;
	}

	@Override
	public int getAttribute() {
		return attribute;
	}

	@Override
	public int getMinimumSize() {
		return MIN_SIZE;
	}

	public Barrier(int fsx, int fsy, int fex, int fey, int type) {
		// フィールド座標が渡ってくるのでマップ座標も計算しておく
		fieldSX = fsx;
		fieldSY = fsy;
		fieldEX = fex;
		fieldEY = fey;

		Point pos;
		pos = Translate.invertLimit(fieldSX, fieldSY);
		mapSX = Math.max(0, Math.min(pos.x, Translate.mapW));
		mapSY = Math.max(0, Math.min(pos.y, Translate.mapH));
		
		pos = Translate.invertLimit(fieldEX, fieldEY);
		mapEX = Math.max(0, Math.min(pos.x, Translate.mapW));
		mapEY = Math.max(0, Math.min(pos.y, Translate.mapH));

		attribute = type;
		switch(type) {
			case BARRIER_GAP_MINI:
				color = Color.YELLOW;
				break;
			case BARRIER_GAP_BIG:
				color = Color.ORANGE;
				break;
			case BARRIER_NET_MINI:
				color = Color.PINK;
				break;
			case BARRIER_NET_BIG:
				color = Color.MAGENTA;
				break;
			case BARRIER_WALL:
				color = Color.GRAY;
				break;
			case BARRIER_ITEM:
				color = Color.GREEN;
				break;
			case BARRIER_NOUNUN:
				color = Color.RED;
				break;			
			case BARRIER_KEKKAI:
				color = Color.LIGHT_GRAY;
				break;	
		}

		MapPlaceData.setWallLine(SimYukkuri.world.currentMap.wallMap, mapSX, mapSY, mapEX, mapEY, true, attribute);
		SimYukkuri.world.currentMap.barrier.add(this);
	}

	public static void drawPreview(Graphics2D g2, int sx, int sy, int ex, int ey) {
		g2.drawLine(sx, sy, ex, ey);
	}

	@Override
	public void drawShape(Graphics2D g2) {
		g2.setColor(color);
		g2.drawLine(fieldSX, fieldSY, fieldEX, fieldEY);
	}

	public static void clearBarrier(Barrier b) {
		int x1 = b.getMapSX();
		int y1 = b.getMapSY();
		int x2 = b.getMapEX();
		int y2 = b.getMapEY();
		if(SimYukkuri.world.currentMap.barrier.remove(b)) {
			MapPlaceData.setWallLine(SimYukkuri.world.currentMap.wallMap, x1, y1, x2, y2, false, b.attribute);
		}
	}

	public static boolean onBarrier(int cx, int cy, int thx, int thy, int attr) {
		MapPlaceData tmp = SimYukkuri.world.currentMap;
		int sx = Math.max(0, cx - thx/2);
		int sy = Math.max(0, cy - thy/2);
		int ex = Math.min(cx + thx/2, Translate.mapW);
		int ey = Math.min(cy + thy/2, Translate.mapH);
		for (int x = sx; x < ex; x++) {
			for (int y = sy; y < ey; y++) {
				if ((tmp.wallMap[x][y] & attr) != 0) {
					return true;
				}
		}
	}
		return false;
	}

	public static Barrier getBarrier(int cx, int cy, int thickness) {
		ArrayList<Barrier> barrierList = SimYukkuri.world.currentMap.barrier;
		
		for (Barrier b: barrierList) {
			int x1 = b.getMapSX();
			int y1 = b.getMapSY();
			int x2 = b.getMapEX();
			int y2 = b.getMapEY();
			int distance = (int)Math.sqrt(Translate.distance(x1, y1, x2, y2));
			double deltaX = (double)(x2 - x1)/(double)distance;
			double deltaY = (double)(y2 - y1)/(double)distance;
			int sX = x1;
			int sY = y1;
			for (int t = 0; t <= distance; t++) {
				int x = sX + (int)(deltaX * t);
				int y = sY + (int)(deltaY * t);
				if ((Math.abs(x - cx) <= thickness) && (Math.abs(y - cy) <= thickness)) {
					return b;
				}
			}
		}
		return null;
	}

	public static boolean acrossBarrier(int x1, int y1, int x2, int y2, int attr) {
		MapPlaceData tmp = SimYukkuri.world.currentMap;
		
		x1 = Math.max(0, Math.min(x1, Translate.mapW));
		x2 = Math.max(0, Math.min(x2, Translate.mapW));
		y1 = Math.max(0, Math.min(y1, Translate.mapH));
		y2 = Math.max(0, Math.min(y2, Translate.mapH));
		
		int distance = (int)Math.sqrt(Translate.distance(x1, y1, x2, y2));
		double deltaX = (double)(x2 - x1)/(double)distance;
		double deltaY = (double)(y2 - y1)/(double)distance;
		int sX = x1;
		int sY = y1;
		for (int t = 0; t <= distance; t++) {
			int x = sX + (int)(deltaX * t);
			int y = sY + (int)(deltaY * t);
			if ((tmp.wallMap[x][y] & attr) != 0) {
				return true;
			}
		}
		return false;
	}
}


