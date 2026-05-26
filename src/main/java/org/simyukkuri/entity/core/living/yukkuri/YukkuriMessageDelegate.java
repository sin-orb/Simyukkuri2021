package org.simyukkuri.entity.core.living.yukkuri;

import java.awt.Color;
import org.simyukkuri.draw.Color4y;
import org.simyukkuri.enums.WindowType;
import org.simyukkuri.logic.YukkuriEventState;

/**
 * ゆっくりのメッセージ系責務をまとめる委譲クラス。
 */
public final class YukkuriMessageDelegate {
	private final Yukkuri body;

	/**
	 * メッセージ委譲を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriMessageDelegate(Yukkuri body) {
		this.body = body;
	}

	/**
	 * 標準メッセージを出す.
	 *
	 * @param message メッセージ
	 */
	public void setMessage(String message) {
		YukkuriEventState.setMessage(body, message);
	}

	/**
	 * 時間指定で標準メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count メッセージ時間
	 */
	public void setMessage(String message, int count) {
		YukkuriEventState.setMessage(body, message, count);
	}

	/**
	 * 割り込み可否付きで標準メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param interrupt 割り込み可否
	 */
	public void setMessage(String message, boolean interrupt) {
		YukkuriEventState.setMessage(body, message, interrupt);
	}

	/**
	 * 時間指定・割り込み指定・ピコピコ指定付きで標準メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count メッセージ時間
	 * @param interrupt 割り込み可否
	 * @param piko ピコピコ可否
	 */
	public void setMessage(String message, int count, boolean interrupt, boolean piko) {
		YukkuriEventState.setMessage(body, message, count, interrupt, piko);
	}

	/**
	 * メッセージ送出の共通実装を呼ぶ.
	 *
	 * @param message メッセージ
	 * @param type ウィンドウ種別
	 * @param count メッセージ時間
	 * @param interrupt 割り込み可否
	 * @param piko ピコピコ可否
	 * @param nyd 非ゆっくり症用かどうか
	 */
	public void setMessage(String message, WindowType type, int count, boolean interrupt, boolean piko, boolean nyd) {
		YukkuriEventState.setMessage(body, message, type, count, interrupt, piko, nyd);
	}

	/**
	 * ピコピコ付きメッセージを出す.
	 *
	 * @param message メッセージ
	 * @param interrupt 割り込み可否
	 */
	public void setPikoMessage(String message, boolean interrupt) {
		YukkuriEventState.setPikoMessage(body, message, interrupt);
	}

	/**
	 * 時間指定のピコピコ付きメッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count メッセージ時間
	 * @param interrupt 割り込み可否
	 */
	public void setPikoMessage(String message, int count, boolean interrupt) {
		YukkuriEventState.setPikoMessage(body, message, count, interrupt);
	}

	/**
	 * ワールドイベント開始メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count メッセージ時間
	 */
	public void setWorldEventSendMessage(String message, int count) {
		YukkuriEventState.setWorldEventSendMessage(body, message, count);
	}

	/**
	 * ワールドイベント応答メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count メッセージ時間
	 * @param interrupt 割り込み可否
	 * @param piko ピコピコ可否
	 */
	public void setWorldEventResMessage(String message, int count, boolean interrupt, boolean piko) {
		YukkuriEventState.setWorldEventResMessage(body, message, count, interrupt, piko);
	}

	/**
	 * 個体イベント開始メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count メッセージ時間
	 */
	public void setEventSendMessage(String message, int count) {
		YukkuriEventState.setEventSendMessage(body, message, count);
	}

	/**
	 * 個体イベント応答メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count メッセージ時間
	 * @param interrupt 割り込み可否
	 * @param piko ピコピコ可否
	 */
	public void setEventResMessage(String message, int count, boolean interrupt, boolean piko) {
		YukkuriEventState.setEventResMessage(body, message, count, interrupt, piko);
	}

	/**
	 * 非ゆっくり症用メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param piko ピコピコ可否
	 */
	public void setNydMessage(String message, boolean piko) {
		YukkuriEventState.setNydMessage(body, message, piko);
	}

	/**
	 * ねぎぃメッセージを出す.
	 *
	 * @param message メッセージ
	 * @param piko ピコピコ可否
	 */
	public void setNegiMessage(String message, boolean piko) {
		YukkuriEventState.setNegiMessage(body, message, piko);
	}

	/**
	 * 時間指定でねぎぃメッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count メッセージ時間
	 * @param piko ピコピコ可否
	 */
	public void setNegiMessage(String message, int count, boolean piko) {
		YukkuriEventState.setNegiMessage(body, message, count, piko);
	}

	/**
	 * メッセージラインの色を設定する.
	 *
	 * @param messageLineColor メッセージラインの色
	 */
	public void setOrigMessageLineColor(Color messageLineColor) {
		body.setMessageLineColor(new Color4y(messageLineColor.getRed(), messageLineColor.getGreen(),
				messageLineColor.getBlue(), messageLineColor.getAlpha()));
	}

	/**
	 * メッセージボックスの色を設定する.
	 *
	 * @param messageBoxColor メッセージボックスの色
	 */
	public void setOrigMessageBoxColor(Color messageBoxColor) {
		body.setMessageBoxColor(new Color4y(messageBoxColor.getRed(), messageBoxColor.getGreen(),
				messageBoxColor.getBlue(), messageBoxColor.getAlpha()));
	}

	/**
	 * メッセージテキストの色を設定する.
	 *
	 * @param messageTextColor メッセージテキストの色
	 */
	public void setOrigMessageTextColor(Color messageTextColor) {
		body.setMessageTextColor(new Color4y(messageTextColor.getRed(), messageTextColor.getGreen(),
				messageTextColor.getBlue(), messageTextColor.getAlpha()));
	}
}
