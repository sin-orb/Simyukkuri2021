package org.simyukkuri.entity.core.attachment;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.awt.image.BufferedImage;
import java.beans.Transient;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AttachProperty;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.Type;

/**
 * ゆっくりの体に付くアタッチメントのベースクラス
 */
@JsonTypeInfo(use = Id.CLASS)
public abstract class Attachment extends Entity {

	private static final long serialVersionUID = 4324305548250241185L;
	/** アタッチメントのつけられている元のID */
	protected int parent;
	/** アニメーションするかどうか */
	protected boolean animate;
	/** アニメーション表示フレームの数 */
	protected int animeFrame;
	/** アニメーションの描画間隔 */
	protected int animeInterval;
	/** アニメループ回数。0でアニメなし */
	protected int animeLoop;
	/** どの種類の画像を使うか */
	protected int[] attachProperty;
	/**
	 * 毎時処理が必要ないもののための処理インターバル
	 * <br>
	 * 初期値は10=1s
	 */
	protected int processInterval = 10;

	/** 各年齢での描画オフセット */
	protected Point4y[] posOfs;

	/** 画像取得 */
	@Transient
	public abstract BufferedImage getImage(Yukkuri body);

	/** X方向の描画座標オフセット分 */
	@Transient
	public int getOfsX() {
		Yukkuri pa = org.simyukkuri.util.YukkuriLookup.getYukkuriById(parent);
		if (pa == null) {
			return -1;
		}
		return posOfs[pa.getAgeState().ordinal()].getX();
	}

	/** Y方向の描画座標オフセット分 */
	@Transient
	public int getOfsY() {
		Yukkuri pa = org.simyukkuri.util.YukkuriLookup.getYukkuriById(parent);
		if (pa == null) {
			return -1;
		}
		return posOfs[pa.getAgeState().ordinal()].getY();
	}

	/** 親オブジェクトの原点取得 */
	@Transient
	public int getParentOrigin() {
		return attachProperty[AttachProperty.OFS_ORIGIN.ordinal()];
	}

	/**
	 * アタッチメントの描画プロパティ配列を設定する。
	 *
	 * @param attachProperty {@link org.simyukkuri.enums.AttachProperty} に対応した設定値の配列
	 */
	public void setAttachProperty(int[] attachProperty) {
		this.attachProperty = attachProperty;
	}

	/** アタッチメントの詳細設定 */
	protected void setAttachProperty(int[] property, String ofsKey) {
		Yukkuri pa = org.simyukkuri.util.YukkuriLookup.getYukkuriById(parent);
		if (pa == null) {
			return;
		}
		posOfs = pa.getMountPoint(ofsKey);
		attachProperty = property;
		animeInterval = 0;
		animeLoop = attachProperty[AttachProperty.ANIME_LOOP.ordinal()];
		if (attachProperty[AttachProperty.ANIME_INTERVAL.ordinal()] == 0) {
			animate = false;
		} else {
			animate = true;
		}
	}

	/**
	 * コンストラクタ
	 *
	 * @param body つけられるゆっくり
	 */
	public Attachment(Yukkuri body) {
		objType = Type.ATTACHMENT;
		parent = body.getUniqueId();
	}

	/**
	 * デシリアライズ用のデフォルトコンストラクタ。
	 * 年齢ごとの描画オフセット配列を初期化する。
	 */
	public Attachment() {
		posOfs = new Point4y[3];
	}

	/** 毎ティックごとの処理 */
	protected abstract TickResult update();

	/** 描画用の境界線のリセット */
	public abstract void resetBoundary();

	/**
	 * 毎ティックごとに呼び出される処理
	 * アニメ処理をする
	 */
	@Override
	public TickResult clockTick() {
		TickResult ret = TickResult.NONE;
		setAge(getAge() + TICK);
		// 処理量軽減処置
		if (getAge() % processInterval == 0) {
			ret = update();
		}

		if (animate) {
			animeInterval += TICK;
			if (animeInterval > attachProperty[AttachProperty.ANIME_INTERVAL.ordinal()]) {
				animeInterval -= attachProperty[AttachProperty.ANIME_INTERVAL.ordinal()];
				animeFrame++;
				if (animeFrame == attachProperty[AttachProperty.ANIME_FRAMES.ordinal()]) {
					animeFrame = 0;
					if (animeLoop > 0) {
						animeLoop--;
						if (animeLoop == 0) {
							animate = false;
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * アタッチメントが装着されているゆっくりのユニークIDを返す。
	 *
	 * @return 親ゆっくりのユニークID
	 */
	public int getParent() {
		return parent;
	}

	/**
	 * 親ゆっくりのユニークIDを設定する。
	 *
	 * @param parent 親ゆっくりのユニークID
	 */
	public void setParent(int parent) {
		this.parent = parent;
	}

	/**
	 * アニメーションが進行中かどうかを返す。
	 *
	 * @return アニメーション中なら true
	 */
	public boolean isAnimate() {
		return animate;
	}

	/**
	 * アニメーションの進行フラグを設定する。
	 *
	 * @param animate true でアニメーションを有効にする
	 */
	public void setAnimate(boolean animate) {
		this.animate = animate;
	}

	/**
	 * 現在表示中のアニメーションフレーム番号を返す。
	 *
	 * @return アニメーションフレーム番号
	 */
	public int getAnimeFrame() {
		return animeFrame;
	}

	/**
	 * アニメーションフレーム番号を設定する。
	 *
	 * @param animeFrame 表示するフレーム番号
	 */
	public void setAnimeFrame(int animeFrame) {
		this.animeFrame = animeFrame;
	}

	/**
	 * 次のフレームに切り替わるまでの経過時間カウンタを返す。
	 *
	 * @return アニメーション間隔カウンタ
	 */
	public int getAnimeInterval() {
		return animeInterval;
	}

	/**
	 * アニメーション間隔カウンタを設定する。
	 *
	 * @param animeInterval アニメーション間隔カウンタ
	 */
	public void setAnimeInterval(int animeInterval) {
		this.animeInterval = animeInterval;
	}

	/**
	 * アニメーションの残りループ回数を返す。0 でアニメーション停止。
	 *
	 * @return 残りループ回数
	 */
	public int getAnimeLoop() {
		return animeLoop;
	}

	/**
	 * アニメーションのループ回数を設定する。0 を指定するとアニメーション停止。
	 *
	 * @param animeLoop ループ回数
	 */
	public void setAnimeLoop(int animeLoop) {
		this.animeLoop = animeLoop;
	}

	/**
	 * アタッチメントの描画プロパティ配列（画像サイズ比率・アニメ設定等）を返す。
	 *
	 * @return {@link org.simyukkuri.enums.AttachProperty} に対応した設定値の配列
	 */
	public int[] getAttachProperty() {
		return attachProperty;
	}

	/**
	 * update() を呼び出す処理間隔（ティック数）を返す。
	 * 初期値は 10（約1秒）。
	 *
	 * @return update() の呼び出し間隔（ティック数）
	 */
	public int getProcessInterval() {
		return processInterval;
	}

	/**
	 * update() を呼び出す処理間隔（ティック数）を設定する。
	 *
	 * @param processInterval update() の呼び出し間隔（ティック数）
	 */
	public void setProcessInterval(int processInterval) {
		this.processInterval = processInterval;
	}

	/**
	 * 年齢ごとの描画オフセット（赤ゆ・子ゆ・成ゆ）を格納した配列を返す。
	 *
	 * @return 年齢別描画オフセット配列
	 */
	public Point4y[] getPosOfs() {
		return posOfs;
	}

	/**
	 * 年齢ごとの描画オフセット配列を設定する。
	 *
	 * @param posOfs 年齢別描画オフセット配列
	 */
	public void setPosOfs(Point4y[] posOfs) {
		this.posOfs = posOfs;
	}
}
