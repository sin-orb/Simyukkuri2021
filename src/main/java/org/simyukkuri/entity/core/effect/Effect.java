package org.simyukkuri.entity.core.effect;

import java.awt.image.BufferedImage;
import java.beans.Transient;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.Type;
import org.simyukkuri.util.GameWorld;

/****************************************
 * セーブデータに保存する必要の無い軽量エフェクト
 *
 *
 */
@JsonTypeInfo(use = Id.CLASS)
public abstract class Effect extends Entity {

	private static final long serialVersionUID = 8592462375468621258L;
	/** 向き */
	protected int direction;
	/** アニメーション間隔 */
	protected int interval;
	/** アニメーション総フレーム数 */
	protected int frames;

	/** エフェクトの継続時間 */
	protected int lifeTime;
	/** アニメーションがあるかどうか */
	protected boolean animate;
	/** アニメーションに使用する画像の管理番号 */
	protected int animeFrame;
	/** アニメーション画像の入れ替わる間隔 */
	protected int animeInterval;
	/**
	 * アニメのループする上限数
	 * <br>
	 * 0でアニメなし
	 */
	protected int animeLoop;
	/** アニメーション終了と同時に消えるか否か */
	protected boolean animeEnd;
	/** 重力の影響を受けるかどうか */
	protected boolean enableGravity;

	/** 画像取得 */
	@Transient
	public abstract BufferedImage getImage();

	/**
	 * コンストラクタ
	 *
	 * @param startX 初期値のX座標
	 * @param startY 初期値のY座標
	 * @param startZ 初期値のZ座標
	 * @param velocityX 初期の移動量ベクトルX成分
	 * @param velocityY 初期の移動量ベクトルY成分
	 * @param velocityZ 初期の移動量ベクトルZ成分
	 * @param invert 初期の向き(0で左、1で右)
	 * @param life   継続時間
	 * @param loop   アニメのループの有無
	 * @param end    ループが一周したら消えるか否か
	 * @param grav   重力の影響の有無
	 * @param front  エフェクトが親オブジェクトの前後どっちか(trueが前)
	 */
	public Effect(int startX, int startY, int startZ, int velocityX, int velocityY, int velocityZ, boolean invert,
			int life, int loop, boolean end, boolean grav, boolean front) {

		if (front) {
			GameWorld.get().getCurrentWorldState().getFrontEffects().put(objId, this);
			GameWorld.get().getCurrentWorldState().registerEntity(objId, this);
		} else {
			GameWorld.get().getCurrentWorldState().getSortedEffects().put(objId, this);
			GameWorld.get().getCurrentWorldState().registerEntity(objId, this);
		}
		objType = Type.LIGHT_EFFECT;
		x = startX;
		y = startY;
		z = startZ;
		vx = velocityX;
		vy = velocityY;
		vz = velocityZ;
		if (invert)
			direction = 1;
		else
			direction = 0;
		lifeTime = life;
		animeInterval = 0;
		animeLoop = loop;
		if (loop == 0)
			animate = false;
		else
			animate = true;
		animeEnd = end;
		enableGravity = grav;
		calcPos();
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public Effect() {

	}

	/** アニメーションのどの画像を出すかのセッター */
	public void setAnimeFrame(int f) {
		animeFrame = f;
	}

	/** エフェクトの毎ティック処理。座標更新・アニメ進行・寿命チェックを行う。 */
	@Override
	public TickResult clockTick() {
		setAge(getAge() + TICK);
		if (isRemoved() || (lifeTime != -1 && getAge() > lifeTime)) {
			return TickResult.REMOVED;
		}
		x += vx;
		y += vy;
		if (enableGravity) {
			vz += 1;
		}
		z -= vz;

		if (animate) {
			animeInterval += TICK;
			if (animeInterval > interval) {
				animeInterval -= interval;
				animeFrame++;
				if (animeFrame == frames) {
					if (animeEnd)
						return TickResult.REMOVED;
					animeFrame = 0;
					if (animeLoop != -1) {
						animeLoop--;
						if (animeLoop <= 0)
							animate = false;
					}
				}
			}
		}
		calcPos();
		return TickResult.NONE;
	}

	/** エフェクトの向きを返す（0=左、1=右）。 */
	public int getDirection() {
		return direction;
	}

	/** エフェクトの向きをセットする。 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/** アニメーション間隔を返す。 */
	public int getInterval() {
		return interval;
	}

	/** アニメーション間隔をセットする。 */
	public void setInterval(int interval) {
		this.interval = interval;
	}

	/** アニメーション総フレーム数を返す。 */
	public int getFrames() {
		return frames;
	}

	/** アニメーション総フレーム数をセットする。 */
	public void setFrames(int frames) {
		this.frames = frames;
	}

	/** エフェクトの継続時間（ティック）を返す。 */
	public int getLifeTime() {
		return lifeTime;
	}

	/** エフェクトの継続時間（ティック）をセットする。 */
	public void setLifeTime(int lifeTime) {
		this.lifeTime = lifeTime;
	}

	/** アニメーション再生中かどうかを返す。 */
	public boolean isAnimate() {
		return animate;
	}

	/** アニメーション再生フラグをセットする。 */
	public void setAnimate(boolean animate) {
		this.animate = animate;
	}

	/** アニメーション経過カウンタを返す。 */
	public int getAnimeInterval() {
		return animeInterval;
	}

	/** アニメーション経過カウンタをセットする。 */
	public void setAnimeInterval(int animeInterval) {
		this.animeInterval = animeInterval;
	}

	/** アニメループ残数を返す。-1 で無限ループ。 */
	public int getAnimeLoop() {
		return animeLoop;
	}

	/** アニメループ残数をセットする。 */
	public void setAnimeLoop(int animeLoop) {
		this.animeLoop = animeLoop;
	}

	/** アニメ終了時に消滅するかどうかを返す。 */
	public boolean isAnimeEnd() {
		return animeEnd;
	}

	/** アニメ終了時消滅フラグをセットする。 */
	public void setAnimeEnd(boolean animeEnd) {
		this.animeEnd = animeEnd;
	}

	/** 重力の影響を受けるかどうかを返す。 */
	public boolean isEnableGravity() {
		return enableGravity;
	}

	/** 重力フラグをセットする。 */
	public void setEnableGravity(boolean enableGravity) {
		this.enableGravity = enableGravity;
	}

	/** 現在のアニメーションフレーム番号を返す。 */
	public int getAnimeFrame() {
		return animeFrame;
	}

}
