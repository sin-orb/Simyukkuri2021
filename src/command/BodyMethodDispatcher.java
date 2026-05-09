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

	private static List<Body> getBodies() {
		return new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
	}

	private static Body asBody(Obj targetObject) {
		if (targetObject instanceof Body) {
			return (Body) targetObject;
		}
		return null;
	}

	private static void invokeNoArgMethod(Body body, String methodName)
			throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Method method = body.getClass().getMethod(methodName, (Class<?>[]) null);
		method.invoke(body, (Object[]) null);
	}

	private static void invokeIntMethod(Body body, String methodName, int value)
			throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Method method = body.getClass().getMethod(methodName, int.class);
		method.invoke(body, value);
	}

	private static void invokeBooleanMethod(Body body, String methodName, boolean value)
			throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Method method = body.getClass().getMethod(methodName, boolean.class);
		method.invoke(body, value);
	}

	/**
	 * ゆっくりにメソッド実行
	 * <br>パラメータなし、SHIFTで全体実行系のコマンド用
	 *
	 * @param e      入力されたマウスの動作
	 * @param targetObject  対象オブジェクト(主にゆっくり)
	 * @param method 実行したいメソッド名
	 */
	public static final void execute(MouseEvent e, Obj targetObject, String method) {
		try {
			Body targetBody = asBody(targetObject);
			List<Body> bodyList = getBodies();
			if (e.isShiftDown()) {
				for (Body body : bodyList) {
					invokeNoArgMethod(body, method);
				}
			} else if (targetBody != null) {
				invokeNoArgMethod(targetBody, method);
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
	 * @param targetObject  対象オブジェクト(主にゆっくり)
	 * @param method 実行したいメソッド名
	 * @param prm    指定パラメータ
	 */
	public static final void execute(MouseEvent e, Obj targetObject, String method, int prm) {
		try {
			Body targetBody = asBody(targetObject);
			List<Body> bodyList = getBodies();
			if (e.isShiftDown()) {
				for (Body body : bodyList) {
					invokeIntMethod(body, method, prm);
				}
			} else if (targetBody != null) {
				invokeIntMethod(targetBody, method, prm);
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
	 * @param targetObject     対象オブジェクト(主にゆっくり)
	 * @param getMethod 取得メソッド名
	 * @param setMethod 設定メソッド名
	 * @param invMethod 反転実行メソッド名
	 */
	public static final void execute(MouseEvent e, Obj targetObject, String getMethod, String setMethod, String invMethod) {
		try {
			Body targetBody = asBody(targetObject);
			List<Body> bodyList = getBodies();
			if (e.isShiftDown()) {
				boolean enabled = true;
				if (targetBody != null) {
					Method method = targetBody.getClass().getMethod(getMethod, (Class<?>[]) null);
					enabled = !((Boolean) method.invoke(targetBody, (Object[]) null)).booleanValue();
				}
				for (Body body : bodyList) {
					invokeBooleanMethod(body, setMethod, enabled);
				}
			} else if (e.isControlDown()) {
				for (Body body : bodyList) {
					invokeNoArgMethod(body, invMethod);
				}
			} else if (targetBody != null) {
				invokeNoArgMethod(targetBody, invMethod);
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
