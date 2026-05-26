package org.simyukkuri.entity.core.living.yukkuri;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import org.simyukkuri.Const;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Dimension4y;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.logic.YukkuriRenderState;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.system.YukkuriLayer;
import org.simyukkuri.util.GameImages;
import org.simyukkuri.util.GameRandom;

/**
 * ゆっくりの画像系責務をまとめる委譲クラス。
 */
public final class YukkuriSpriteDelegate {
	private static BufferedImage[] shadowImages = new BufferedImage[3];
	private static int[] shadowImgW = new int[3];
	private static int[] shadowImgH = new int[3];
	private static int[] shadowPivX = new int[3];
	private static int[] shadowPivY = new int[3];

	private final Yukkuri body;

	/**
	 * 画像系責務の委譲を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriSpriteDelegate(Yukkuri body) {
		this.body = body;
	}

	/**
	 * うにょ系の画像更新が有効かどうかを返す.
	 *
	 * @return うにょ系画像更新の対象かどうか
	 */
	public boolean isUnyoActionAll() {
		return body.isShitting() || body.isBirth() || body.isFurifuri() || body.isEating() || body.isPeropero()
				|| body.isSukkiri() || body.isEatingShit() || body.isNobinobi() || body.isVain()
				|| body.isPikopiko() || body.isYunnyaa();
	}

	/**
	 * うにょ画像のずれ量を加算する.
	 *
	 * @param x X方向の加算量
	 * @param y Y方向の加算量
	 * @param z Z方向の加算量
	 */
	public void changeUnyo(int x, int y, int z) {
		if (!body.isDead() && !body.isCrushed()) {
			int offsetH = body.getUnyoOffsetH();
			int offsetW = body.getUnyoOffsetW();
			if (x != 0) {
				offsetH += x;
				offsetW -= x;
			}
			if (y != 0) {
				offsetH -= y;
				offsetW += y;
			}
			if (z != 0) {
				offsetH -= z;
				offsetW += z;
			}
			if (offsetH > Const.EXT_FORCE_PULL_LIMIT[body.getAgeState().ordinal()]) {
				offsetH = Const.EXT_FORCE_PULL_LIMIT[body.getAgeState().ordinal()];
			} else if (offsetH < Const.EXT_FORCE_PUSH_LIMIT[body.getAgeState().ordinal()]) {
				offsetH = Const.EXT_FORCE_PUSH_LIMIT[body.getAgeState().ordinal()];
			}
			if (offsetW < Const.EXT_FORCE_PUSH_LIMIT[body.getAgeState().ordinal()]) {
				offsetW = Const.EXT_FORCE_PUSH_LIMIT[body.getAgeState().ordinal()];
			} else if (offsetW > Const.EXT_FORCE_PULL_LIMIT[body.getAgeState().ordinal()]) {
				offsetW = Const.EXT_FORCE_PULL_LIMIT[body.getAgeState().ordinal()];
			}
			body.setUnyoOffsetH(offsetH);
			body.setUnyoOffsetW(offsetW);
		}
	}

	/**
	 * うにょ画像のずれ量を自然減衰させる.
	 */
	public void changeReUnyo() {
		int offsetH = body.getUnyoOffsetH();
		int offsetW = body.getUnyoOffsetW();
		if (offsetH == 0) {
		} else if (offsetH < Const.EXT_FORCE_PUSH_LIMIT[body.getAgeState().ordinal()] * 0.6) {
			offsetH += GameRandom.nextInt(3) + 5;
		} else if (offsetH < 0) {
			offsetH += GameRandom.nextInt(3) + 2;
		} else if (offsetH > Const.EXT_FORCE_PULL_LIMIT[body.getAgeState().ordinal()] * 0.6) {
			offsetH -= GameRandom.nextInt(3) + 5;
		} else if (offsetH > 0) {
			offsetH -= GameRandom.nextInt(3) + 2;
		}
		if (offsetW == 0) {
		} else if (offsetW < Const.EXT_FORCE_PUSH_LIMIT[body.getAgeState().ordinal()] * 0.6) {
			offsetW += GameRandom.nextInt(3) + 5;
		} else if (offsetW < 0) {
			offsetW += GameRandom.nextInt(3) + 2;
		} else if (offsetW > Const.EXT_FORCE_PULL_LIMIT[body.getAgeState().ordinal()] * 0.6) {
			offsetW -= GameRandom.nextInt(3) + 5;
		} else if (offsetW > 0) {
			offsetW -= GameRandom.nextInt(3) + 2;
		}
		body.setUnyoOffsetH(offsetH);
		body.setUnyoOffsetW(offsetW);
	}

	/**
	 * うにょ画像のずれ量を初期化する.
	 */
	public void resetUnyo() {
		body.setUnyoOffsetH(0);
		body.setUnyoOffsetW(0);
	}

	/**
	 * 胴体ベース画像の種別を返す.
	 *
	 * @param layer 描画レイヤ
	 * @return 胴体ベース画像のindex
	 */
	public int getImageIndex(YukkuriLayer layer) {
		return YukkuriRenderState.getImageIndex(body, layer);
	}

	/**
	 * 切断など異常状態の胴体画像を返す.
	 *
	 * @param layer 描画レイヤ
	 * @return 異常胴体画像のindex
	 */
	public int getDamageImageIndex(YukkuriLayer layer) {
		return YukkuriRenderState.getDamageImageIndex(body, layer);
	}

	/**
	 * おかざり画像を返す.
	 *
	 * @param layer 描画レイヤ
	 * @param type  前後どちらの画像かを示す種別
	 * @return おかざり画像のindex
	 */
	public int getOkazariImageIndex(YukkuriLayer layer, int type) {
		return YukkuriRenderState.getOkazariImageIndex(body, layer, type);
	}

	/**
	 * 体表エフェクト画像を返す.
	 *
	 * @param layer 描画レイヤ
	 * @return 体表エフェクト画像のindex
	 */
	public int getEffectImage(YukkuriLayer layer) {
		return YukkuriRenderState.getEffectImage(body, layer);
	}

	/**
	 * 顔画像を返す.
	 *
	 * @param layer 描画レイヤ
	 * @return 顔画像のindex
	 */
	public int getFaceImage(YukkuriLayer layer) {
		return YukkuriRenderState.getFaceImage(body, layer);
	}

	/**
	 * おさげ・羽・尻尾画像を返す.
	 *
	 * @param layer 描画レイヤ
	 * @param type  前後どちらの画像かを示す種別
	 * @return おさげ・羽・尻尾画像のindex
	 */
	public int getBraidImage(YukkuriLayer layer, int type) {
		return YukkuriRenderState.getBraidImage(body, layer, type);
	}

	/**
	 * 描画用のありすれいぱー判定を返す.
	 *
	 * @return ありすれいぱーかどうか
	 */
	public boolean isAliceRaperForRender() {
		return body.isAliceRaper();
	}

	/**
	 * 現在の画像サイズを更新する.
	 */
	public void updateSpriteSize() {
		int forceW = body.getExternalForceW();
		int forceH = body.getExternalForceH();
		if (SimYukkuri.UNYO) {
			forceW = body.getExternalForceW() + body.getUnyoOffsetW();
			forceH = body.getExternalForceH() + body.getUnyoOffsetH();
		}

		int expSizeW = body.getExpandSizeW();
		int expSizeH = body.getExpandSizeH();
		body.getExpandSpr()[body.getAgeState().ordinal()].addSpriteSize(forceW + expSizeW, forceH + expSizeH);
	}

	/**
	 * 境界情報を設定する.
	 *
	 * @param bodyBoundary  胴体境界
	 * @param braidBoundary おさげ境界
	 */
	public void setBoundary(Dimension4y[] bodyBoundary, Dimension4y[] braidBoundary) {
		int bodyLen = body.getSpriteSet().length;
		for (int i = 0; i < bodyLen; i++) {
			int w = 0;
			int h = 0;
			if (bodyBoundary != null && i < bodyBoundary.length && bodyBoundary[i] != null) {
				w = bodyBoundary[i].getWidth();
				h = bodyBoundary[i].getHeight();
			}
			body.getSpriteSet()[i] = new org.simyukkuri.system.Sprite(w, h, org.simyukkuri.system.Sprite.PIVOT_CENTER_BOTTOM);
			body.getExpandSpr()[i] = new org.simyukkuri.system.Sprite(w, h, org.simyukkuri.system.Sprite.PIVOT_CENTER_BOTTOM);
		}
		int braidLen = body.getBraidSpr().length;
		for (int i = 0; i < braidLen; i++) {
			int w = 0;
			int h = 0;
			if (braidBoundary != null && i < braidBoundary.length && braidBoundary[i] != null) {
				w = braidBoundary[i].getWidth();
				h = braidBoundary[i].getHeight();
			}
			body.getBraidSpr()[i] = new org.simyukkuri.system.Sprite(w, h, org.simyukkuri.system.Sprite.PIVOT_CENTER_BOTTOM);
		}
	}

	/**
	 * 胴体境界の矩形を返す.
	 *
	 * @param r 返却先の矩形
	 */
	public void getBoundaryShape(Rectangle r) {
		r.x = body.getSpriteSet()[body.getAgeState().ordinal()].getPivotX();
		r.y = body.getSpriteSet()[body.getAgeState().ordinal()].getPivotY();
		r.width = body.getSpriteSet()[body.getAgeState().ordinal()].getImageW();
		r.height = body.getSpriteSet()[body.getAgeState().ordinal()].getImageH();
	}

	/**
	 * 拡張境界の矩形を返す.
	 *
	 * @param r 返却先の矩形
	 */
	public void getExpandShape(Rectangle4y r) {
		r.setWidth(body.getExpandSpr()[body.getAgeState().ordinal()].getScreenRect()[0].getWidth());
		r.setHeight(body.getExpandSpr()[body.getAgeState().ordinal()].getScreenRect()[0].getHeight());
		if (org.simyukkuri.SimYukkuri.UNYO) {
			r.setWidth(body.getExpandSpr()[body.getAgeState().ordinal()].getScreenRect()[0].getWidth()
					+ body.getUnyoOffsetW());
			r.setHeight(body.getExpandSpr()[body.getAgeState().ordinal()].getScreenRect()[0].getHeight()
					+ body.getUnyoOffsetH());
		}
		r.setX(r.getWidth() >> 1);
		r.setY(r.getHeight() - 1);
	}

	/**
	 * 当たり判定の横幅を返す.
	 *
	 * @return 当たり判定の横幅
	 */
	public int getCollisionX() {
		return (body.getSpriteSet()[body.getAgeState().ordinal()].getImageW() + body.getExpandSizeW()) >> 1;
	}

	/**
	 * 当たり判定の縦幅を返す.
	 *
	 * @return 当たり判定の縦幅
	 */
	public int getCollisionY() {
		return (body.getSpriteSet()[body.getAgeState().ordinal()].getImageH() + body.getExpandSizeH()) >> 1;
	}

	/**
	 * 現在年齢の胴体スプライトを返す.
	 *
	 * @return 胴体スプライト
	 */
	public org.simyukkuri.system.Sprite getSpriteSetite() {
		return body.getSpriteSet()[body.getAgeState().ordinal()];
	}

	/**
	 * 現在年齢の拡張スプライトを返す.
	 *
	 * @return 拡張スプライト
	 */
	public org.simyukkuri.system.Sprite getExpandedSpriteSet() {
		return body.getExpandSpr()[body.getAgeState().ordinal()];
	}

	/**
	 * 現在年齢のおさげスプライトを返す.
	 *
	 * @return おさげスプライト
	 */
	public org.simyukkuri.system.Sprite getBraidSprite() {
		return body.getBraidSpr()[body.getAgeState().ordinal()];
	}

	/**
	 * 現在年齢の影画像を返す.
	 *
	 * @return 影画像
	 */
	public BufferedImage getShadowImage() {
		return shadowImages[body.getAgeState().ordinal()];
	}

	/**
	 * 現在年齢の影画像の高さを返す.
	 *
	 * @return 影画像の高さ
	 */
	public int getShadowH() {
		return shadowImgH[body.getAgeState().ordinal()];
	}

	/**
	 * 現在年齢の胴体幅を返す.
	 *
	 * @return 胴体幅
	 */
	public int getW() {
		return body.getSpriteSet()[body.getAgeState().ordinal()].getImageW();
	}

	/**
	 * 現在年齢の胴体高さを返す.
	 *
	 * @return 胴体高さ
	 */
	public int getH() {
		return body.getSpriteSet()[body.getAgeState().ordinal()].getImageH();
	}

	/**
	 * 現在年齢の胴体ピボットXを返す.
	 *
	 * @return 胴体ピボットX
	 */
	public int getPivotX() {
		return body.getSpriteSet()[body.getAgeState().ordinal()].getPivotX();
	}

	/**
	 * 現在年齢の胴体ピボットYを返す.
	 *
	 * @return 胴体ピボットY
	 */
	public int getPivotY() {
		return body.getSpriteSet()[body.getAgeState().ordinal()].getPivotY();
	}

	/**
	 * 現在年齢のおさげ幅を返す.
	 *
	 * @return おさげ幅
	 */
	public int getBraidW() {
		return body.getBraidSpr()[body.getAgeState().ordinal()].getImageW();
	}

	/**
	 * 現在年齢のおさげ高さを返す.
	 *
	 * @return おさげ高さ
	 */
	public int getBraidH() {
		return body.getBraidSpr()[body.getAgeState().ordinal()].getImageH();
	}

	/**
	 * 影画像の配列を返す.
	 *
	 * @return 影画像配列
	 */
	public static BufferedImage[] getShadowImages() {
		return shadowImages;
	}

	/**
	 * 影画像の配列を設定する.
	 *
	 * @param shadowImages 影画像配列
	 */
	public static void setShadowImages(BufferedImage[] shadowImages) {
		YukkuriSpriteDelegate.shadowImages = shadowImages;
	}

	/**
	 * 影画像の幅配列を返す.
	 *
	 * @return 幅配列
	 */
	public static int[] getShadowImgW() {
		return shadowImgW;
	}

	/**
	 * 影画像の幅配列を設定する.
	 *
	 * @param shadowImgW 幅配列
	 */
	public static void setShadowImgW(int[] shadowImgW) {
		YukkuriSpriteDelegate.shadowImgW = shadowImgW;
	}

	/**
	 * 影画像の高さ配列を返す.
	 *
	 * @return 高さ配列
	 */
	public static int[] getShadowImgH() {
		return shadowImgH;
	}

	/**
	 * 影画像の高さ配列を設定する.
	 *
	 * @param shadowImgH 高さ配列
	 */
	public static void setShadowImgH(int[] shadowImgH) {
		YukkuriSpriteDelegate.shadowImgH = shadowImgH;
	}

	/**
	 * 影画像の中心X配列を返す.
	 *
	 * @return 中心X配列
	 */
	public static int[] getShadowPivX() {
		return shadowPivX;
	}

	/**
	 * 影画像の中心X配列を設定する.
	 *
	 * @param shadowPivX 中心X配列
	 */
	public static void setShadowPivX(int[] shadowPivX) {
		YukkuriSpriteDelegate.shadowPivX = shadowPivX;
	}

	/**
	 * 影画像の中心Y配列を返す.
	 *
	 * @return 中心Y配列
	 */
	public static int[] getShadowPivY() {
		return shadowPivY;
	}

	/**
	 * 影画像の中心Y配列を設定する.
	 *
	 * @param shadowPivY 中心Y配列
	 */
	public static void setShadowPivY(int[] shadowPivY) {
		YukkuriSpriteDelegate.shadowPivY = shadowPivY;
	}

	/**
	 * 影画像をファイルから読み込む.
	 *
	 * @param loader クラスローダー
	 * @param io     画像オブザーバ
	 * @throws IOException 読み込み失敗時
	 */
	public static void loadShadowImages(ClassLoader loader, ImageObserver io) throws IOException {
		final String path = "images/";
		int sx;
		int sy;

		getShadowImages()[Const.ADULT_INDEX] = GameImages.read(loader.getResourceAsStream(path + "shadow.png"));

		sx = (int) ((float) getShadowImages()[Const.ADULT_INDEX].getWidth(io) * Const.BODY_SIZE[1]);
		sy = (int) ((float) getShadowImages()[Const.ADULT_INDEX].getHeight(io) * Const.BODY_SIZE[1]);
		getShadowImages()[Const.CHILD_INDEX] = ModLoader.scaleImage(getShadowImages()[Const.ADULT_INDEX], sx, sy);
		sx = (int) ((float) getShadowImages()[Const.ADULT_INDEX].getWidth(io) * Const.BODY_SIZE[0]);
		sy = (int) ((float) getShadowImages()[Const.ADULT_INDEX].getHeight(io) * Const.BODY_SIZE[0]);
		getShadowImages()[Const.BABY_INDEX] = ModLoader.scaleImage(getShadowImages()[Const.ADULT_INDEX], sx, sy);

		for (int i = 0; i < 3; i++) {
			getShadowImgW()[i] = getShadowImages()[i].getWidth(io);
			getShadowImgH()[i] = getShadowImages()[i].getHeight(io);
			getShadowPivX()[i] = getShadowImgW()[i] >> 1;
			getShadowPivY()[i] = getShadowImgH()[i] >> 1;
		}
	}

	/**
	 * スプライト関連データを他の Yukkuri から深く複製する.
	 *
	 * @param from 複製元
	 */
	public void copySpriteSetFrom(Yukkuri from) {
		if (from == null) {
			return;
		}
		body.setSpriteSet(copySprites(from.getSpriteSet()));
		body.setExpandSpr(copySprites(from.getExpandSpr()));
		body.setBraidSpr(copySprites(from.getBraidSpr()));
	}

	private static Sprite[] copySprites(Sprite[] src) {
		if (src == null) {
			return null;
		}
		Sprite[] ret = new Sprite[src.length];
		for (int i = 0; i < src.length; i++) {
			ret[i] = copySprite(src[i]);
		}
		return ret;
	}

	private static Sprite copySprite(Sprite src) {
		if (src == null) {
			return null;
		}
		Sprite ret = new Sprite();
		ret.setOriginalW(src.getOriginalW());
		ret.setOriginalH(src.getOriginalH());
		ret.setImageW(src.getImageW());
		ret.setImageH(src.getImageH());
		ret.setPivotX(src.getPivotX());
		ret.setPivotY(src.getPivotY());
		ret.setPivotType(src.getPivotType());
		Rectangle4y[] rect = src.getScreenRect();
		if (rect != null) {
			Rectangle4y[] rectCopy = new Rectangle4y[rect.length];
			for (int i = 0; i < rect.length; i++) {
				Rectangle4y r = rect[i];
				rectCopy[i] = (r == null) ? null : new Rectangle4y(r.getX(), r.getY(), r.getWidth(), r.getHeight());
			}
			ret.setScreenRect(rectCopy);
		}
		return ret;
	}
}
