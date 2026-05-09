package src.draw;

import java.util.Comparator;

import src.base.Yukkuri;
import src.base.Entity;

/**
 * 描画用ソートクラス
 */
public final class ObjDrawComp implements Comparator<Entity> {
	private static final ObjDrawComp INSTANCE = new ObjDrawComp();

	public static ObjDrawComp getInstance() {
		return INSTANCE;
	}

	@Override 
	public final int compare(Entity o1, Entity o2) {
		int c = o1.getY() - o2.getY();
		if(c == 0) {
			//Improve visibility: at the same y-coordinate, draw small
			//objects after large ones.
			c = (o2 instanceof Yukkuri ? ((Yukkuri)o2).getBodyAgeState().ordinal() : 1) -
					(o1 instanceof Yukkuri ? ((Yukkuri)o1).getBodyAgeState().ordinal() : 1);
		}
		return c;
	}
}
