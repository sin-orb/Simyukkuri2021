package src.base;

import java.awt.image.BufferedImage;
import java.beans.Transient;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import src.draw.Point4y;
import src.enums.AttachProperty;
import src.enums.Event;
import src.enums.Type;
import src.util.YukkuriUtil;

/****************************************
 * ゆっくりの体に付くアタッチメントのベースクラス
 */
@JsonTypeInfo(use = Id.CLASS)
public abstract class Attachment extends Obj {

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
	public abstract BufferedImage getImage(Body b);

	/** X方向の描画座標オフセット分 */
	@Transient
	public int getOfsX() {
		Body pa = YukkuriUtil.getBodyInstance(parent);
		if (pa == null)
			return -1;
		return posOfs[pa.getBodyAgeState().ordinal()].getX();
	}

	/** Y方向の描画座標オフセット分 */
	@Transient
	public int getOfsY() {
		Body pa = YukkuriUtil.getBodyInstance(parent);
		if (pa == null)
			return -1;
		return posOfs[pa.getBodyAgeState().ordinal()].getY();
	}

	/** 親オブジェクトの原点取得 */
	@Transient
	public int getParentOrigin() {
		return attachProperty[AttachProperty.OFS_ORIGIN.ordinal()];
	}

	/** アタッチメントの詳細設定 */
	protected void setAttachProperty(int[] p, String ofsKey) {
		Body pa = YukkuriUtil.getBodyInstance(parent);
		if (pa == null)
			return;
		posOfs = pa.getMountPoint(ofsKey);
		attachProperty = p;
		animeInterval = 0;
		animeLoop = attachProperty[AttachProperty.ANIME_LOOP.ordinal()];
		if (attachProperty[AttachProperty.ANIME_INTERVAL.ordinal()] == 0)
			animate = false;
		else
			animate = true;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param body つけられるゆっくり
	 */
	public Attachment(Body body) {
		objType = Type.ATTACHMENT;
		parent = body.getUniqueID();
	}

	public Attachment() {
		posOfs = new Point4y[3];
	}

	/** 毎ティックごとの処理 */
	abstract protected Event update();

	/** 描画用の境界線のリセット */
	abstract public void resetBoundary();

	@Override
	/**
	 * 毎ティックごとに呼び出される処理
	 * アニメ処理をする
	 */
	public Event clockTick() {
		Event ret = Event.DONOTHING;
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
						if (animeLoop == 0)
							animate = false;
					}
				}
			}
		}
		return ret;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public boolean isAnimate() {
		return animate;
	}

	public void setAnimate(boolean animate) {
		this.animate = animate;
	}

	public int getAnimeFrame() {
		return animeFrame;
	}

	public void setAnimeFrame(int animeFrame) {
		this.animeFrame = animeFrame;
	}

	public int getAnimeInterval() {
		return animeInterval;
	}

	public void setAnimeInterval(int animeInterval) {
		this.animeInterval = animeInterval;
	}

	public int getAnimeLoop() {
		return animeLoop;
	}

	public void setAnimeLoop(int animeLoop) {
		this.animeLoop = animeLoop;
	}

	public int[] getAttachProperty() {
		return attachProperty;
	}

	public void setAttachProperty(int[] attachProperty) {
		this.attachProperty = attachProperty;
	}

	public int getProcessInterval() {
		return processInterval;
	}

	public void setProcessInterval(int processInterval) {
		this.processInterval = processInterval;
	}

	public Point4y[] getPosOfs() {
		return posOfs;
	}

	public void setPosOfs(Point4y[] posOfs) {
		this.posOfs = posOfs;
	}
}