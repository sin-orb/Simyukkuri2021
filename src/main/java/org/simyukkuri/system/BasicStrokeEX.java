package org.simyukkuri.system;

import java.awt.BasicStroke;
import java.lang.reflect.Field;

/*****************************************************************************
 * 
 * シリアライズ対応のBasicStroke
 * 
 * Copyright 2006 Thomas Hawtin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
public class BasicStrokeEX extends BasicStroke implements java.io.Serializable {

	private static class Serial implements java.io.Serializable {
		static final long serialVersionUID = 5538700973722429161L + 1;
		private transient BasicStrokeEX replacement;

		Serial(BasicStrokeEX replacement) {
			this.replacement = replacement;
		}

		private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
			out.writeFloat(replacement.getLineWidth());
			out.writeInt(replacement.getEndCap());
			out.writeInt(replacement.getLineJoin());
			out.writeFloat(replacement.getMiterLimit());
			out.writeUnshared(replacement.getDashArray());
			out.writeFloat(replacement.getDashPhase());
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, java.lang.ClassNotFoundException {
			try {
				this.replacement = new BasicStrokeEX(in.readFloat(), // lineWidth
						in.readInt(), // endCap
						in.readInt(), // lineJoin
						in.readFloat(), // miterLimit
						(float[]) in.readUnshared(), // dashArray
						in.readFloat() // dashPhase
				);
			} catch (IllegalArgumentException exc) {
				java.io.InvalidObjectException wrapper = new java.io.InvalidObjectException(exc.getMessage());
				wrapper.initCause(exc);
				throw wrapper;
			}
		}

		private Object readResolve() throws java.io.ObjectStreamException {
			return replacement;
		}
	}

	public static java.awt.BasicStroke serializable(java.awt.BasicStroke target) {
		return (target instanceof java.io.Serializable) ? target
				: new BasicStrokeEX(
						target.getLineWidth(),
						target.getEndCap(),
						target.getLineJoin(),
						target.getMiterLimit(),
						target.getDashArray(),
						target.getDashPhase());
	}

	/**
	 * デフォルト設定でインスタンスを生成する。
	 */
	public BasicStrokeEX() {
		super();
	}

	/**
	 * 線幅のみを指定してインスタンスを生成する。
	 *
	 * @param lineWidth 線の太さ
	 */
	public BasicStrokeEX(float lineWidth) {
		super(lineWidth);
	}

	/**
	 * 線幅・端点スタイル・結合スタイルを指定してインスタンスを生成する。
	 *
	 * @param lineWidth 線の太さ
	 * @param endCap 端点のスタイル（CAP_BUTT, CAP_ROUND, CAP_SQUARE）
	 * @param lineJoin 結合部のスタイル（JOIN_BEVEL, JOIN_MITER, JOIN_ROUND）
	 */
	public BasicStrokeEX(float lineWidth, int endCap, int lineJoin) {
		super(lineWidth, endCap, lineJoin);
	}

	/**
	 * 線幅・端点・結合・マイター制限を指定してインスタンスを生成する。
	 *
	 * @param lineWidth 線の太さ
	 * @param endCap 端点のスタイル
	 * @param lineJoin 結合部のスタイル
	 * @param miterLimit マイター継ぎの最大長さ
	 */
	public BasicStrokeEX(float lineWidth, int endCap, int lineJoin, float miterLimit) {
		super(lineWidth, endCap, lineJoin, miterLimit);
	}

	/**
	 * すべてのパラメータを指定してインスタンスを生成する。
	 *
	 * @param lineWidth 線の太さ
	 * @param endCap 端点のスタイル
	 * @param lineJoin 結合部のスタイル
	 * @param miterLimit マイター継ぎの最大長さ
	 * @param dashArray 破線パターンの配列（null で実線）
	 * @param dashPhase 破線パターンの開始オフセット
	 */
	public BasicStrokeEX(float lineWidth, int endCap, int lineJoin, float miterLimit, float[] dashArray,
			float dashPhase) {
		super(lineWidth, endCap, lineJoin, miterLimit, dashArray, dashPhase);
	}

	private Object writeReplace() throws java.io.ObjectStreamException {
		return new Serial(this);
	}

	// BasicStroke のフィールドは final のため、新しい JDK ではリフレクションで変更が困難。
	// セキュリティマネージャ設定によって静かに失敗するか例外を投げる場合がある。
	/**
	 * リフレクションを用いて線幅を変更する。
	 * BasicStroke の final フィールドを強制書き換えするため、JDK バージョンによっては無効になる場合がある。
	 *
	 * @param width 変更後の線の太さ
	 */
	public void setLineWidth(float width) {
		Class<?> superClazz = this.getClass().getSuperclass();
		Field field = null;
		try {
			field = superClazz.getDeclaredField("width");
			field.setAccessible(true);
			field.set(this, width);
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	/**
	 * リフレクションを用いて端点スタイルを変更する。
	 *
	 * @param cap 変更後の端点スタイル（BasicStroke.CAP_* 定数）
	 */
	public void setEndCap(int cap) {
		Class<?> superClazz = this.getClass().getSuperclass();
		Field field = null;
		try {
			field = superClazz.getDeclaredField("cap");
			field.setAccessible(true);
			field.set(this, cap);
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	/**
	 * リフレクションを用いて線の結合スタイルを変更する。
	 *
	 * @param join 変更後の結合スタイル（BasicStroke.JOIN_* 定数）
	 */
	public void setLineJoin(int join) {
		Class<?> superClazz = this.getClass().getSuperclass();
		Field field = null;
		try {
			field = superClazz.getDeclaredField("join");
			field.setAccessible(true);
			field.set(this, join);
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	/**
	 * リフレクションを用いてマイター継ぎの最大長さを変更する。
	 *
	 * @param miterlimit 変更後のマイター制限値
	 */
	public void setMiterLimit(float miterlimit) {
		Class<?> superClazz = this.getClass().getSuperclass();
		Field field = null;
		try {
			field = superClazz.getDeclaredField("miterlimit");
			field.setAccessible(true);
			field.set(this, miterlimit);
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	/**
	 * リフレクションを用いて破線パターンの配列を変更する。
	 *
	 * @param dash 変更後の破線パターン配列
	 */
	public void setDashArray(float[] dash) {
		Class<?> superClazz = this.getClass().getSuperclass();
		Field field = null;
		try {
			field = superClazz.getDeclaredField("dash");
			field.setAccessible(true);
			field.set(this, dash);
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	/**
	 * リフレクションを用いて破線パターンの開始オフセットを変更する。
	 *
	 * @param dash_phase 変更後の破線フェーズ（オフセット値）
	 */
	public void setDashPhase(float dash_phase) {
		Class<?> superClazz = this.getClass().getSuperclass();
		Field field = null;
		try {
			field = superClazz.getDeclaredField("dash_phase");
			field.setAccessible(true);
			field.set(this, dash_phase);
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

}
