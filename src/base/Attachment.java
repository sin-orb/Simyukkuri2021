package src.base;

import java.awt.Point;
import java.awt.image.BufferedImage;

import src.enums.AttachProperty;
import src.enums.Event;
import src.enums.Type;

/****************************************
 *  ゆっくりの体に付くアタッチメントのベースクラス
 */
public abstract class Attachment extends Obj {

	private static final long serialVersionUID = 1L;

	/**アタッチメントのつけられている元*/
	protected Body parent;
	/**アニメーションするかどうか*/
	protected boolean animate;
	/**アニメーション表示フレームの数*/
	protected int animeFrame;
	/**アニメーションの描画間隔*/
	protected int animeInterval;
	/**アニメループ回数。0でアニメなし*/
	protected int animeLoop;
	/**どの種類の画像を使うか*/
	protected int[] attachProperty;
	/**毎時処理が必要ないもののための処理インターバル
	 * <br>初期値は10=1s*/
	protected int processInterval = 10;

	/**各年齢での描画オフセット*/
	protected Point[] posOfs;

	/**画像取得*/
	public abstract BufferedImage getImage(Body b);

	/**X方向の描画座標オフセット分*/
	public int getOfsX() {
		return posOfs[parent.getBodyAgeState().ordinal()].x;
	}

	/**Y方向の描画座標オフセット分*/
	public int getOfsY() {
		return posOfs[parent.getBodyAgeState().ordinal()].y;
	}

	/**親オブジェクトの原点取得*/
	public int getParentOrigin() {
		return attachProperty[AttachProperty.OFS_ORIGIN.ordinal()];
	}

	/**アタッチメントの詳細設定*/
	protected void setAttachProperty(int[] p, String ofsKey) {
		posOfs = parent.getMountPoint(ofsKey);
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
	 * @param body つけられるゆっくり
	 */
	public Attachment(Body body) {
		objType = Type.ATTACHMENT;
		parent = body;
	}

	/**毎ティックごとの処理*/
	abstract protected Event update();

	/**描画用の境界線のリセット*/
	abstract public void resetBoundary();

	@Override
	/**
	 * 毎ティックごとに呼び出される処理
	 * アニメ処理をする
	 */
	public Event clockTick() {
		Event ret = Event.DONOTHING;
		setAge(getAge() + TICK);
		//処理量軽減処置
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
}