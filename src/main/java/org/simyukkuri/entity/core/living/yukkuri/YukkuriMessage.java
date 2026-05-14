package org.simyukkuri.entity.core.living.yukkuri;

import java.awt.Color;

import org.simyukkuri.draw.Color4y;
import org.simyukkuri.enums.WindowType;
import org.simyukkuri.logic.BodyEventState;

/**
 * ゆっくりのメッセージ系責務をまとめる委譲クラス。
 */
public final class YukkuriMessage {
	private final Yukkuri body;

	/**
	 * メッセージ委譲を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriMessage(Yukkuri body) {
		this.body = body;
	}

	/**
	 * 標準メッセージを出す.
	 *
	 * @param message メッセージ
	 */
	public void setMessage(String message) {
		BodyEventState.setMessage(body, message);
	}

	/**
	 * ピコピコ付きメッセージを出す.
	 *
	 * @param message メッセージ
	 * @param interrupt 割り込み可否
	 */
	public void setPikoMessage(String message, boolean interrupt) {
		BodyEventState.setPikoMessage(body, message, interrupt);
	}

	/**
	 * 時間指定のピコピコ付きメッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count メッセージ時間
	 * @param interrupt 割り込み可否
	 */
	public void setPikoMessage(String message, int count, boolean interrupt) {
		BodyEventState.setPikoMessage(body, message, count, interrupt);
	}

	/**
	 * 時間指定で標準メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count メッセージ時間
	 */
	public void setMessage(String message, int count) {
		BodyEventState.setMessage(body, message, count);
	}

	/**
	 * 割り込み可否付きで標準メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param interrupt 割り込み可否
	 */
	public void setMessage(String message, boolean interrupt) {
		BodyEventState.setMessage(body, message, interrupt);
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
		BodyEventState.setMessage(body, message, count, interrupt, piko);
	}

	/**
	 * ワールドイベント開始メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count メッセージ時間
	 */
	public void setWorldEventSendMessage(String message, int count) {
		BodyEventState.setWorldEventSendMessage(body, message, count);
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
		BodyEventState.setWorldEventResMessage(body, message, count, interrupt, piko);
	}

	/**
	 * 個体イベント開始メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count メッセージ時間
	 */
	public void setBodyEventSendMessage(String message, int count) {
		BodyEventState.setBodyEventSendMessage(body, message, count);
	}

	/**
	 * 個体イベント応答メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count メッセージ時間
	 * @param interrupt 割り込み可否
	 * @param piko ピコピコ可否
	 */
	public void setBodyEventResMessage(String message, int count, boolean interrupt, boolean piko) {
		BodyEventState.setBodyEventResMessage(body, message, count, interrupt, piko);
	}

	/**
	 * 非ゆっくり症用メッセージを出す.
	 *
	 * @param message メッセージ
	 * @param piko ピコピコ可否
	 */
	public void setNYDMessage(String message, boolean piko) {
		BodyEventState.setNYDMessage(body, message, piko);
	}

	/**
	 * メッセージ送出の共通実装を呼ぶ.
	 *
	 * @param message メッセージ
	 * @param type ウィンドウ種別
	 * @param count メッセージ時間
	 * @param interrupt 割り込み可否
	 * @param piko ピコピコ可否
	 * @param NYD 非ゆっくり症用かどうか
	 */
	public void setMessage(String message, WindowType type, int count, boolean interrupt, boolean piko, boolean NYD) {
		BodyEventState.setMessage(body, message, type, count, interrupt, piko, NYD);
	}

	/**
	 * ねぎぃメッセージを出す.
	 *
	 * @param message メッセージ
	 * @param piko ピコピコ可否
	 */
	public void setNegiMessage(String message, boolean piko) {
		BodyEventState.setNegiMessage(body, message, piko);
	}

	/**
	 * 時間指定でねぎぃメッセージを出す.
	 *
	 * @param message メッセージ
	 * @param count メッセージ時間
	 * @param piko ピコピコ可否
	 */
	public void setNegiMessage(String message, int count, boolean piko) {
		BodyEventState.setNegiMessage(body, message, count, piko);
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
