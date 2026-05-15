package org.simyukkuri.command;

import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.util.GameWorld;

/**
 * リフレクション経由でゆっくりにメソッドを実行するディスパッチャ
 */
public class YukkuriMethodDispatcher {

	private static List<Yukkuri> getYukkuriBodies() {
		return new LinkedList<Yukkuri>(GameWorld.get().getCurrentWorldState().getYukkuriRegistry().values());
	}

	private static Yukkuri asBody(Entity targetObject) {
		if (targetObject instanceof Yukkuri) {
			return (Yukkuri) targetObject;
		}
		return null;
	}

	private static void invokeNoArgMethod(Yukkuri body, String methodName)
			throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Method method = body.getClass().getMethod(methodName, (Class<?>[]) null);
		method.invoke(body, (Object[]) null);
	}

	private static void invokeIntMethod(Yukkuri body, String methodName, int value)
			throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Method method = body.getClass().getMethod(methodName, int.class);
		method.invoke(body, value);
	}

	private static void invokeBooleanMethod(Yukkuri body, String methodName, boolean value)
			throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Method method = body.getClass().getMethod(methodName, boolean.class);
		method.invoke(body, value);
	}

	/**
	 * ゆっくりにメソッド実行
	 * <br>
	 * パラメータなし、SHIFTで全体実行系のコマンド用
	 *
	 * @param e            入力されたマウスの動作
	 * @param targetObject 対象オブジェクト(主にゆっくり)
	 * @param method       実行したいメソッド名
	 */
	public static final void execute(MouseEvent e, Entity targetObject, String method) {
		try {
			Yukkuri targetBody = asBody(targetObject);
			List<Yukkuri> bodyList = getYukkuriBodies();
			if (e.isShiftDown()) {
				for (Yukkuri body : bodyList) {
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
	 * <br>
	 * パラメータあり、SHIFTで全体実行系のコマンド用
	 *
	 * @param e            入力されたマウスの動作
	 * @param targetObject 対象オブジェクト(主にゆっくり)
	 * @param method       実行したいメソッド名
	 * @param prm          指定パラメータ
	 */
	public static final void execute(MouseEvent e, Entity targetObject, String method, int prm) {
		try {
			Yukkuri targetBody = asBody(targetObject);
			List<Yukkuri> bodyList = getYukkuriBodies();
			if (e.isShiftDown()) {
				for (Yukkuri body : bodyList) {
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
	 * <br>
	 * パラメータboolean、SHIFTで全体、CTRLで反転実行系のコマンド用
	 *
	 * @param e            入力されたマウスの動作
	 * @param targetObject 対象オブジェクト(主にゆっくり)
	 * @param getMethod    取得メソッド名
	 * @param setMethod    設定メソッド名
	 * @param invMethod    反転実行メソッド名
	 */
	public static final void execute(MouseEvent e, Entity targetObject, String getMethod, String setMethod,
			String invMethod) {
		try {
			Yukkuri targetBody = asBody(targetObject);
			List<Yukkuri> bodyList = getYukkuriBodies();
			if (e.isShiftDown()) {
				boolean enabled = true;
				if (targetBody != null) {
					Method method = targetBody.getClass().getMethod(getMethod, (Class<?>[]) null);
					enabled = !((Boolean) method.invoke(targetBody, (Object[]) null)).booleanValue();
				}
				for (Yukkuri body : bodyList) {
					invokeBooleanMethod(body, setMethod, enabled);
				}
			} else if (e.isControlDown()) {
				for (Yukkuri body : bodyList) {
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
