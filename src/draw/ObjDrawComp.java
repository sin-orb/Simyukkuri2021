package src.draw;

import java.util.Comparator;

import src.base.Body;
import src.base.Obj;

/**
 * 描画用ソートクラス
 */
public final class ObjDrawComp implements Comparator<Obj> {
	private static final ObjDrawComp INSTANCE = new ObjDrawComp();

	public static ObjDrawComp getInstance() {
		return INSTANCE;
	}

	@Override 
	public final int compare(Obj o1, Obj o2) {
		int c = o1.getY() - o2.getY();
		if(c == 0) {
			//Improve visibility: at the same y-coordinate, draw small
			//objects after large ones.
			c = (o2 instanceof Body ? ((Body)o2).getBodyAgeState().ordinal() : 1) -
					(o1 instanceof Body ? ((Body)o1).getBodyAgeState().ordinal() : 1);
		}
		return c;
	}
}
