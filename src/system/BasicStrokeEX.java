package src.system;

import java.awt.BasicStroke;
import java.lang.reflect.Field;

/*****************************************************************************

	シリアライズ対応のBasicStroke

Copyright 2006 Thomas Hawtin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/
public class BasicStrokeEX extends BasicStroke implements java.io.Serializable {

	private static class Serial implements java.io.Serializable {
		static final long serialVersionUID = 5538700973722429161L+1;
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

		private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
			try {
				this.replacement = new BasicStrokeEX(in.readFloat(), // lineWidth
													in.readInt(), // endCap
													in.readInt(), // lineJoin
													in.readFloat(), // miterLimit
													(float[])in.readUnshared(), // dashArray
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
		return (target instanceof java.io.Serializable) ?
				target :
					new BasicStrokeEX(
							target.getLineWidth(),
							target.getEndCap(),
							target.getLineJoin(),
							target.getMiterLimit(),
							target.getDashArray(),
							target.getDashPhase());
	}

	public BasicStrokeEX() {
		super();
	}

	public BasicStrokeEX(float lineWidth) {
		super(lineWidth);
	}

	public BasicStrokeEX(float lineWidth, int endCap, int lineJoin) {
		super(lineWidth, endCap, lineJoin);
	}

	public BasicStrokeEX(float lineWidth, int endCap, int lineJoin, float miterLimit) {
		super(lineWidth, endCap, lineJoin, miterLimit);
	}

	public BasicStrokeEX(float lineWidth, int endCap, int lineJoin, float miterLimit, float[] dashArray, float dashPhase) {
		super(lineWidth, endCap, lineJoin, miterLimit, dashArray, dashPhase);
	}

	private Object writeReplace() throws java.io.ObjectStreamException {
		return new Serial(this);
	}
	
	public void setLineWidth(float width) {
		Class<?> superClazz = this.getClass().getSuperclass();
		Field field = null;
		try {
			field = superClazz.getDeclaredField("width");
			field.setAccessible(true);
			field.set(this, width);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void setEndCap(int cap) {
    	Class<?> superClazz = this.getClass().getSuperclass();
		Field field = null;
		try {
			field = superClazz.getDeclaredField("cap");
			field.setAccessible(true);
			field.set(this, cap);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void setLineJoin(int join) {
    	Class<?> superClazz = this.getClass().getSuperclass();
		Field field = null;
		try {
			field = superClazz.getDeclaredField("join");
			field.setAccessible(true);
			field.set(this, join);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void setMiterLimit(float miterlimit) {
    	Class<?> superClazz = this.getClass().getSuperclass();
		Field field = null;
		try {
			field = superClazz.getDeclaredField("miterlimit");
			field.setAccessible(true);
			field.set(this, miterlimit);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void setDashArray(float[] dash) {
    	Class<?> superClazz = this.getClass().getSuperclass();
		Field field = null;
		try {
			field = superClazz.getDeclaredField("dash");
			field.setAccessible(true);
			field.set(this, dash);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void setDashPhase(float dash_phase) {
    	Class<?> superClazz = this.getClass().getSuperclass();
		Field field = null;
		try {
			field = superClazz.getDeclaredField("dash_phase");
			field.setAccessible(true);
			field.set(this, dash_phase);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}


