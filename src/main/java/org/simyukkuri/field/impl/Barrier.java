package org.simyukkuri.field.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.beans.Transient;
import java.util.List;
import org.simyukkuri.draw.Color4y;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.field.FieldShape;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameWorld;

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

/**
 * 壁
 * <br>
 * これはほかのアイテムと違い、ObjEXを継承していないので注意。
 */
public class Barrier extends FieldShape {
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

	/** バリアの属性値（どんな壁か）を返す。 */
	@Override
	public int getAttribute() {
		return attribute;
	}

	/** バリアの最小サイズを返す。 */
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
		fieldSx = fsx;
		fieldSy = fsy;
		fieldEx = fex;
		fieldEy = fey;
		// フィールド座標が渡ってくるのでマップ座標も計算しておく
		Point4y pos;
		pos = Translate.invertLimit(fieldSx, fieldSy);
		mapSx = Math.max(0, Math.min(pos.getX(), Translate.getWorldWidth()));
		mapSy = Math.max(0, Math.min(pos.getY(), Translate.getWorldHeight()));

		pos = Translate.invertLimit(fieldEx, fieldEy);
		mapEx = Math.max(0, Math.min(pos.getX(), Translate.getWorldWidth()));
		mapEy = Math.max(0, Math.min(pos.getY(), Translate.getWorldHeight()));

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
			default:
				break;
		}

		WorldState.setWallLine(GameWorld.get().getCurrentWorldState().getWallGrid(), mapSx, mapSy, mapEx, mapEy, true,
				attribute);
		GameWorld.get().getCurrentWorldState().getBarriers().add(this);
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public Barrier() {

	}

	/** プレビューの線の描画 */
	public static void drawPreview(Graphics2D g2, int sx, int sy, int ex, int ey) {
		g2.drawLine(sx, sy, ex, ey);
	}

	/** シェイプの外形を描画する。 */
	@Override
	public void drawShape(Graphics2D g2) {
		g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
		g2.drawLine(fieldSx, fieldSy, fieldEx, fieldEy);
	}

	/** 除去 */
	public static void clearBarrier(Barrier barrier) {
		int x1 = barrier.getStartX();
		int y1 = barrier.getStartY();
		int x2 = barrier.getEndX();
		int y2 = barrier.getEndY();
		if (GameWorld.get().getCurrentWorldState().getBarriers().remove(barrier)) {
			WorldState.setWallLine(GameWorld.get().getCurrentWorldState().getWallGrid(), x1, y1, x2, y2, false, barrier.attribute);
		}
	}

	/** 壁に引っかかるかのチェック */
	public static boolean onBarrier(int cx, int cy, int thx, int thy, int attr) {
		WorldState tmp = GameWorld.get().getCurrentWorldState();
		int sx = Math.max(0, cx - thx / 2);
		int sy = Math.max(0, cy - thy / 2);
		int ex = Math.min(cx + thx / 2, Translate.getWorldWidth());
		int ey = Math.min(cy + thy / 2, Translate.getWorldHeight());
		for (int x = sx; x < ex; x++) {
			for (int y = sy; y < ey; y++) {
				if ((tmp.getWallGrid()[x][y] & attr) != 0) {
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
		List<Barrier> barrierList = GameWorld.get().getCurrentWorldState().getBarriers();

		for (Barrier targetBarrier : barrierList) {
			int x1 = targetBarrier.getStartX();
			int y1 = targetBarrier.getStartY();
			int x2 = targetBarrier.getEndX();
			int y2 = targetBarrier.getEndY();
			int distance = (int) Math.sqrt(Translate.distance(x1, y1, x2, y2));
			double deltaX = (double) (x2 - x1) / (double) distance;
			double deltaY = (double) (y2 - y1) / (double) distance;
			int startX = x1;
			int startY = y1;
			for (int t = 0; t <= distance; t++) {
				int x = startX + (int) (deltaX * t);
				int y = startY + (int) (deltaY * t);
				if ((Math.abs(x - cx) <= thickness) && (Math.abs(y - cy) <= thickness)) {
					return targetBarrier;
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
		final WorldState tmp = GameWorld.get().getCurrentWorldState();

		x1 = Math.max(0, Math.min(x1, Translate.getWorldWidth()));
		x2 = Math.max(0, Math.min(x2, Translate.getWorldWidth()));
		y1 = Math.max(0, Math.min(y1, Translate.getWorldHeight()));
		y2 = Math.max(0, Math.min(y2, Translate.getWorldHeight()));

		int distance = (int) Math.sqrt(Translate.distance(x1, y1, x2, y2));
		double deltaX = (double) (x2 - x1) / (double) distance;
		double deltaY = (double) (y2 - y1) / (double) distance;
		int startX = x1;
		int startY = y1;
		for (int t = 0; t <= distance; t++) {
			int x = startX + (int) (deltaX * t);
			int y = startY + (int) (deltaY * t);
			if ((tmp.getWallGrid()[x][y] & attr) != 0) {
				return true;
			}
		}
		return false;
	}
}
