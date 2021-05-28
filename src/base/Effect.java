package src.base;


import java.awt.image.BufferedImage;

import src.SimYukkuri;
import src.enums.Event;
import src.enums.Type;

/****************************************
 *  セーブデータに保存する必要の無い軽量エフェクト
 * 
 * 
 */
public abstract class Effect extends Obj {

	private static final long serialVersionUID = 1L;

	/**向き*/
	protected int direction;
	/**アニメーション間隔*/
	protected int interval;	
	/**アニメーション総フレーム数*/
	protected int frames;

	/**エフェクトの継続時間*/
	protected int lifeTime;
	/**アニメーションがあるかどうか*/
	protected boolean animate;
	/**アニメーションに使用する画像の管理番号*/
	protected int animeFrame;
	/**アニメーション画像の入れ替わる間隔*/
	protected int animeInterval;
	/**アニメのループする上限数
	 * <br>0でアニメなし*/
	protected int animeLoop;
	/**アニメーション終了と同時に消えるか否か*/
	protected boolean animeEnd;
	/**重力の影響を受けるかどうか*/
	protected boolean enableGravity;

	/**画像取得*/
	public abstract BufferedImage getImage();

	/**コンストラクタ
	 * 
	 * @param sX 初期値のX座標
	 * @param sY 初期値のY座標
	 * @param sZ 初期値のZ座標
	 * @param vX 初期の移動量ベクトルX成分
	 * @param vY 初期の移動量ベクトルY成分
	 * @param vZ 初期の移動量ベクトルZ成分
	 * @param invert 初期の向き(0で左、1で右)
	 * @param life 継続時間
	 * @param loop アニメのループの有無
	 * @param end ループが一周したら消えるか否か
	 * @param grav 重力の影響の有無
	 * @param front エフェクトが親オブジェクトの前後どっちか(trueが前)
	 */
	public Effect(int sX, int sY, int sZ, int vX, int vY, int vZ, boolean invert,
						int life, int loop, boolean end, boolean grav, boolean front) {

		if(front) {
			SimYukkuri.world.getCurrentMap().frontEffect.add(this);
		}
		else {
			SimYukkuri.world.getCurrentMap().sortEffect.add(this);
		}
		objType = Type.LIGHT_EFFECT;
		x = sX;
		y = sY;
		z = sZ;
		vx = vX;
		vy = vY;
		vz = vZ;
		if(invert) direction = 1;
		else direction = 0;
		lifeTime = life;
		animeInterval = 0;
		animeLoop = loop;
		if(loop == 0) animate = false;
		else animate = true;
		animeEnd = end;
		enableGravity = grav;
		calcPos();
	}
	/**アニメーションのどの画像を出すかのセッター*/
	public void setAnimeFrame(int f) {
		animeFrame = f;		
	}
	@Override
	public Event clockTick()
	{
		setAge(getAge() + TICK);
		if ( isRemoved() || (lifeTime != -1 && getAge() > lifeTime)) {
			return Event.REMOVED;
		}
		x += vx;
		y += vy;
		if ( enableGravity ) {
			vz += 1;
		}
		z -= vz;
		
		if(animate) {
			animeInterval += TICK;
			if(animeInterval > interval) {
				animeInterval -= interval;
				animeFrame++;
				if(animeFrame == frames) {
					if(animeEnd) return Event.REMOVED;
					animeFrame = 0;
					if(animeLoop != -1) {
						animeLoop--;
						if(animeLoop <= 0) animate = false;
					}
				}
			}
		}
		calcPos();
		return Event.DONOTHING;
	}
}