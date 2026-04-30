package src.command;

import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import src.base.Body;
import src.base.Obj;
import src.util.GameWorld;

/**
 * リフレクション経由でゆっくりにメソッドを実行するディスパッチャ
 */
public class BodyMethodDispatcher {

	/**
	 * ゆっくりにメソッド実行
	 * <br>パラメータなし、SHIFTで全体実行系のコマンド用
	 *
	 * @param e      入力されたマウスの動作
	 * @param found  対象オブジェクト(主にゆっくり)
	 * @param method 実行したいメソッド名
	 */
	public static final void execute(MouseEvent e, Obj found, String method) {
		try {
			Method m;
			List<Body> bodyList = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
			if (e.isShiftDown()) {
				for (Body b : bodyList) {
					m = b.getClass().getMethod(method, (Class<?>[]) null);
					m.invoke(b, (Object[]) null);
				}
			} else {
				if (found instanceof Body) {
					m = ((Body) found).getClass().getMethod(method, (Class<?>[]) null);
					m.invoke(((Body) found), (Object[]) null);
				}
			}
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * ゆっくりにメソッド実行
	 * <br>パラメータあり、SHIFTで全体実行系のコマンド用
	 *
	 * @param e      入力されたマウスの動作
	 * @param found  対象オブジェクト(主にゆっくり)
	 * @param method 実行したいメソッド名
	 * @param prm    指定パラメータ
	 */
	public static final void execute(MouseEvent e, Obj found, String method, int prm) {
		try {
			Method m;
			List<Body> bodyList = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
			if (e.isShiftDown()) {
				for (Body b : bodyList) {
					m = b.getClass().getMethod(method, int.class);
					m.invoke(b, prm);
				}
			} else {
				if (found instanceof Body) {
					m = ((Body) found).getClass().getMethod(method, int.class);
					m.invoke(((Body) found), prm);
				}
			}
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * ゆっくりにメソッド実行
	 * <br>パラメータboolean、SHIFTで全体、CTRLで反転実行系のコマンド用
	 *
	 * @param e         入力されたマウスの動作
	 * @param found     対象オブジェクト(主にゆっくり)
	 * @param getMethod 取得メソッド名
	 * @param setMethod 設定メソッド名
	 * @param invMethod 反転実行メソッド名
	 */
	public static final void execute(MouseEvent e, Obj found, String getMethod, String setMethod, String invMethod) {
		try {
			Method m;
			List<Body> bodyList = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
			if (e.isShiftDown()) {
				boolean flag = true;
				if (found instanceof Body) {
					m = ((Body) found).getClass().getMethod(getMethod, (Class<?>[]) null);
					flag = !((Boolean) m.invoke(((Body) found), (Object[]) null)).booleanValue();
				}
				for (Body b : bodyList) {
					m = b.getClass().getMethod(setMethod, boolean.class);
					m.invoke(b, flag);
				}
			} else if (e.isControlDown()) {
				for (Body b : bodyList) {
					m = b.getClass().getMethod(invMethod, (Class<?>[]) null);
					m.invoke(b, (Object[]) null);
				}
			} else {
				if (found instanceof Body) {
					m = ((Body) found).getClass().getMethod(invMethod, (Class<?>[]) null);
					m.invoke(((Body) found), (Object[]) null);
				}
			}
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}
}
