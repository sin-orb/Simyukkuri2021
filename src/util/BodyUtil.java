package src.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import src.base.Attachment;
import src.base.Body;
import src.base.Obj;
import src.base.Okazari;
import src.base.Okazari.OkazariType;
import src.draw.Point4y;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.enums.Direction;
import src.enums.HairState;
import src.enums.ImageCode;
import src.system.BodyLayer;
import src.system.Sprite;

/*****************************************************
 * 
 * ゆっくりの画像管理
 * 画像パターンの定義など
 * 新しい画像は、"HybridYukkuri.java"での定義も忘れずに!
 */
public class BodyUtil {

	// ゆっくりの跳ね移動テーブル
	private static final int BODY_JUMP[] = { 0, 8, 12, 14, 15, 14, 12, 8, 0 };
	private static final int BODY_JUMP_LEVEL[] = { 2, 2, 1 };
	private static final int BODY_FLY[] = { 0, 5, 10, 14, 14, 14, 10, 5, 0 };

	// テンポラリ
	private static BodyLayer layer = new BodyLayer();
	private static BodyLayer layer2 = new BodyLayer();
	private static BodyLayer layer3 = new BodyLayer();
	private static Rectangle4y rectTmp = new Rectangle4y();

	/**
	 * ゆっくり一体の描画(通常種用)
	 * Body内のSprite座標変換は呼び出し元で行っておく
	 * 
	 * @param g2 Graphics2D
	 * @param io イメージオブザーバ
	 * @param b  ゆっくり
	 */
	public static void drawBody(Graphics2D g2, ImageObserver io, Body b) {

		int direction = b.getDirection().ordinal();
		int ageIndex = b.getBodyAgeState().ordinal();
		Sprite base = b.getBodyBaseSpr();
		Sprite expand = b.getBodyExpandSpr();
		Sprite braid = b.getBraidSprite();
		int[] jumpTable;
		if (b.isFlyingType()) {
			jumpTable = BODY_FLY;
		} else
			jumpTable = BODY_JUMP;
		int z = b.getZ();

		// 本体描画
		int numBB = b.getBodyBaseImage(layer);

		// 正面向き
		if (layer.getOption()[0] == 0) {
			// 全レイヤーを同一座標に描画
			for (int i = 0; i < numBB; i++) {
				drawBody(g2, z, 0, layer.getImage()[i], expand.getScreenRect()[layer.getDir()[i]].getX(), expand.getScreenRect()[layer.getDir()[i]].getY(),
						expand.getScreenRect()[layer.getDir()[i]].getWidth(), expand.getScreenRect()[layer.getDir()[i]].getHeight(),
						base.getScreenRect()[layer.getDir()[i]].getWidth(), base.getScreenRect()[layer.getDir()[i]].getHeight(), io);
			}
			// <-- TEST
		}

		// 通常(斜視)
		else if (layer.getOption()[0] == 1) {
			int faceOfsY = 0;
			int faceOfsH = 0;
			int okazariOfsY = 0;
			// 妊娠時の各種位置ののオフセット計算
			okazariOfsY -= (int) ((expand.getScreenRect()[direction].getHeight() - base.getScreenRect()[direction].getHeight()) * 0.9f);
			faceOfsY -= (int) ((expand.getScreenRect()[direction].getHeight() - base.getScreenRect()[direction].getHeight()) * 0.6f);

			// 表情取得＆位置計算
			int jy = 0;
			int jh = 0;
			int num2 = b.getFaceImage(layer2);
			switch (layer2.getOption()[0]) {
				case 0:
				default:
					break;
				case 1:
					// 発情 大ジャンプ
					jy = -Translate.transSize(jumpTable[(int) b.getAge() % 9] / BODY_JUMP_LEVEL[ageIndex]);
					break;
				case 2:
					// すっきり
					jh = Translate
							.transSize(jumpTable[(int) b.getAge() / 2 % 9] / 2 / BODY_JUMP_LEVEL[ageIndex] * 5 / 2);
					jy = -jh;
					break;
				case 3:
					// 通常ジャンプ
					jy = -Translate.transSize(jumpTable[(int) b.getAge() % 9] / 2 / BODY_JUMP_LEVEL[ageIndex]);
					break;
				case 4:
					// のびのび
					jh = Translate
							.transSize(jumpTable[(int) b.getAge() / 2 % 9] / 2 / BODY_JUMP_LEVEL[ageIndex] * 5 / 2);
					jy = -jh;
					break;
				case 5:
					// ゆんやあーー
					jh = Translate
							.transSize(jumpTable[(int) b.getAge() / 2 % 9] / 2 / BODY_JUMP_LEVEL[ageIndex] * (-5));
					jy = -jh;
					break;
			}
			base.getScreenRect()[0].setY(base.getScreenRect()[0].getY() + jy);
			base.getScreenRect()[1].setY(base.getScreenRect()[1].getY() + jy);
			expand.getScreenRect()[0].setY(expand.getScreenRect()[0].getY() + jy);
			expand.getScreenRect()[1].setY(expand.getScreenRect()[1].getY() + jy);
			braid.getScreenRect()[0].setY(braid.getScreenRect()[0].getY() + jy);
			braid.getScreenRect()[1].setY(braid.getScreenRect()[1].getY() + jy);
			base.getScreenRect()[0].setHeight(base.getScreenRect()[0].getHeight() + jh);
			base.getScreenRect()[1].setHeight(base.getScreenRect()[1].getHeight() + jh);
			expand.getScreenRect()[0].setHeight(expand.getScreenRect()[0].getHeight() + jh);
			expand.getScreenRect()[1].setHeight(expand.getScreenRect()[1].getHeight() + jh);

			// 何かとリンクしてる場合の全体の高度補正
			Obj oLinkParent = b.takeMappedObj(b.getLinkParent());
			if (oLinkParent != null && oLinkParent.getZ() < b.getZ() && oLinkParent instanceof Body) {
				Body bLink = (Body) oLinkParent;
				int jyLink = 0;
				int jhLink = 0;
				BodyLayer layer2Link = new BodyLayer();
				bLink.getFaceImage(layer2Link);
				switch (layer2Link.getOption()[0]) {
					case 0:
					default:
						break;
					case 1:
						// 発情 大ジャンプ
						jyLink = -Translate.transSize(jumpTable[(int) bLink.getAge() % 9]
								/ BODY_JUMP_LEVEL[bLink.getBodyAgeState().ordinal()]);
						break;
					case 2:
						// すっきり
						jhLink = Translate.transSize(jumpTable[(int) bLink.getAge() / 2 % 9] / 2
								/ BODY_JUMP_LEVEL[bLink.getBodyAgeState().ordinal()] * 5 / 2);
						jyLink = -jhLink;
						break;
					case 3:
						// 通常ジャンプ
						jyLink = -Translate.transSize(jumpTable[(int) bLink.getAge() % 9] / 2
								/ BODY_JUMP_LEVEL[bLink.getBodyAgeState().ordinal()]);
						break;
					case 4:
						// のびのび
						jhLink = Translate.transSize(jumpTable[(int) bLink.getAge() / 2 % 9] / 2
								/ BODY_JUMP_LEVEL[bLink.getBodyAgeState().ordinal()] * 5 / 2);
						jyLink = -jhLink;
						break;
					case 5:
						// ゆんやあーー
						jh = Translate
								.transSize(jumpTable[(int) b.getAge() / 2 % 9] / 2 / BODY_JUMP_LEVEL[ageIndex] * (-5));
						jy = -jh;
						break;
				}
				base.getScreenRect()[0].setY(base.getScreenRect()[0].getY() + jyLink);
				base.getScreenRect()[1].setY(base.getScreenRect()[1].getY() + jyLink);
				expand.getScreenRect()[0].setY(expand.getScreenRect()[0].getY() + jyLink);
				expand.getScreenRect()[1].setY(expand.getScreenRect()[1].getY() + jyLink);
				braid.getScreenRect()[0].setY(braid.getScreenRect()[0].getY() + jyLink);
				braid.getScreenRect()[1].setY(braid.getScreenRect()[1].getY() + jyLink);
			}

			// ひっぱり時の各種の位置補正計算
			// 倍率は見た目で適当に調整
			int force = b.getExtForce();
			if (force > 0) {
				faceOfsY += (int) ((float) force * 1.25f);
				okazariOfsY += (int) ((float) force * 0.9f);
			} else if (force < 0) {
				faceOfsY -= (int) ((float) force * 0.4f);
				faceOfsH += (int) ((float) force * 1.25f);
				okazariOfsY += (int) ((float) force * 0.5f);
			}

			// 後方のおかざり
			if (b.getOkazari() != null && b.getOkazariPosition() != 1) {
				Okazari okazari = b.getOkazari();
				OkazariType otype = okazari.getOkazariType();
				if (otype == OkazariType.DEFAULT) {
					b.getOlazariImage(layer3, 1);
					drawBody(g2, z, okazariOfsY, layer3.getImage()[0], base.getScreenRect()[layer3.getDir()[0]].getX(),
							base.getScreenRect()[layer3.getDir()[0]].getY() + okazariOfsY,
							base.getScreenRect()[layer3.getDir()[0]].getWidth(), base.getScreenRect()[layer3.getDir()[0]].getHeight(),
							base.getScreenRect()[layer3.getDir()[0]].getWidth(), base.getScreenRect()[layer3.getDir()[0]].getHeight(), io);
				}
			}
			// 奥側のおさげ
			if (b.isBraidType()) {
				int numBr = b.getBraidImage(layer3, 1);
				for (int i = 0; i < numBr; i++) {
					// g2.drawImage(layer.getImage()[i], braid.getScreenRect()[layer.getDir()[i]].x,
					// braid.getScreenRect()[layer.getDir()[i]].y + faceOfsY,
					// braid.getScreenRect()[layer.getDir()[i]].width, braid.getScreenRect()[layer.getDir()[i]].height,
					// io);
					drawBody(g2, z, faceOfsY, layer3.getImage()[i], braid.getScreenRect()[layer3.getDir()[i]].getX(),
							braid.getScreenRect()[layer3.getDir()[i]].getY() + faceOfsY,
							braid.getScreenRect()[layer3.getDir()[i]].getWidth(), braid.getScreenRect()[layer3.getDir()[i]].getHeight(),
							braid.getScreenRect()[layer3.getDir()[i]].getWidth(), braid.getScreenRect()[layer3.getDir()[i]].getHeight(), io);
				}
			}

			// 胴体描画
			drawBody(g2, z, 0, layer.getImage()[0], expand.getScreenRect()[layer.getDir()[0]].getX(), expand.getScreenRect()[layer.getDir()[0]].getY(),
					expand.getScreenRect()[layer.getDir()[0]].getWidth(), expand.getScreenRect()[layer.getDir()[0]].getHeight(),
					base.getScreenRect()[layer.getDir()[0]].getWidth(), base.getScreenRect()[layer.getDir()[0]].getHeight(), io);

			// 切断&溶解マスク
			int numAB = b.getAbnormalBodyImage(layer);
			if (numAB != 0) {
				for (int i = 0; i < numAB; i++) {
					drawBody(g2, z, (expand.getScreenRect()[layer.getDir()[i]].getHeight() / 4), layer.getImage()[i],
							expand.getScreenRect()[layer.getDir()[i]].getX(),
							expand.getScreenRect()[layer.getDir()[i]].getY() + (expand.getScreenRect()[layer.getDir()[i]].getHeight() / 4),
							expand.getScreenRect()[layer.getDir()[i]].getWidth(), expand.getScreenRect()[layer.getDir()[i]].getHeight(),
							base.getScreenRect()[layer.getDir()[i]].getWidth(), base.getScreenRect()[layer.getDir()[i]].getHeight(), io);
				}
			}

			for (int i = 0; i < num2; i++) {
				drawBody(g2, z, faceOfsY, layer2.getImage()[i], base.getScreenRect()[layer2.getDir()[i]].getX(),
						base.getScreenRect()[layer2.getDir()[i]].getY() + faceOfsY,
						base.getScreenRect()[layer2.getDir()[i]].getWidth(), base.getScreenRect()[layer2.getDir()[i]].getHeight() + faceOfsH,
						base.getScreenRect()[layer2.getDir()[i]].getWidth(), base.getScreenRect()[layer2.getDir()[i]].getHeight(), io);
			}

			// 口封じマスク描画は顔グラフィック表示のほうに含めたためオミット

			// 髪の毛マスク
			if (b.geteHairState() != HairState.BALDHEAD) {
				if (b.geteHairState() == HairState.DEFAULT) {
					b.getImage(ImageCode.HAIR0.ordinal(), direction, layer, 0);
				} else if (b.geteHairState() == HairState.BRINDLED1) {
					b.getImage(ImageCode.HAIR1.ordinal(), direction, layer, 0);
				} else if (b.geteHairState() == HairState.BRINDLED2) {
					b.getImage(ImageCode.HAIR2.ordinal(), direction, layer, 0);
				}

				drawBody(g2, z, 0, layer.getImage()[0], expand.getScreenRect()[layer.getDir()[0]].getX(), expand.getScreenRect()[layer.getDir()[0]].getY(),
						expand.getScreenRect()[layer.getDir()[0]].getWidth(), expand.getScreenRect()[layer.getDir()[0]].getHeight(),
						base.getScreenRect()[layer.getDir()[0]].getWidth(), base.getScreenRect()[layer.getDir()[0]].getHeight(), io);
			}
			// 体表エフェクト表示
			int numE = b.getEffectImage(layer);
			for (int i = 0; i < numE; i++) {
				drawBody(g2, z, 0, layer.getImage()[i], expand.getScreenRect()[layer.getDir()[i]].getX(), expand.getScreenRect()[layer.getDir()[i]].getY(),
						expand.getScreenRect()[layer.getDir()[i]].getWidth(), expand.getScreenRect()[layer.getDir()[i]].getHeight(),
						base.getScreenRect()[layer.getDir()[i]].getWidth(), base.getScreenRect()[layer.getDir()[i]].getHeight(), io);
			}
			/* 皮むき描画はベース描画に含めたため、オミット */
			/* 盲目マスク描画は、顔グラ表示のほうに入れたためオミット */

			// 前方おかざり
			if (b.getOkazari() != null) {
				b.getOlazariImage(layer3, 0);
				Okazari okazari = b.getOkazari();
				OkazariType otype = okazari.getOkazariType();
				if (otype == OkazariType.DEFAULT) {
					if (b.getOkazariPosition() != 2) {
						// --> TEST
						drawBody(g2, z, okazariOfsY, layer3.getImage()[0], base.getScreenRect()[layer3.getDir()[0]].getX(),
								base.getScreenRect()[layer3.getDir()[0]].getY() + okazariOfsY,
								base.getScreenRect()[layer3.getDir()[0]].getWidth(), base.getScreenRect()[layer3.getDir()[0]].getHeight(),
								base.getScreenRect()[layer3.getDir()[0]].getWidth(), base.getScreenRect()[layer3.getDir()[0]].getHeight(), io);
						// <-- TEST
					}
				} else {
					Point4y ofs = okazari.takeOkazariOfsPos();
					okazari.getBoundaryShape(rectTmp);
					int okX = Translate.transSize(rectTmp.getX());
					int okY = Translate.transSize(rectTmp.getY());
					int okW = Translate.transSize(rectTmp.getWidth());
					int okH = Translate.transSize(rectTmp.getHeight());
					int ofsX = Translate.transSize(ofs.getX());
					int ofsY = Translate.transSize(ofs.getY());
					int bx = base.getScreenRect()[direction].getX();
					int by = base.getScreenRect()[direction].getY() + base.getPivotY();
					if (b.getDirection() == Direction.RIGHT) {
						bx -= base.getPivotX();
						ofsX = -ofsX;
					} else {
						bx += base.getPivotX();
					}
					// --> TEST
					drawBody(g2, z, okazariOfsY, layer3.getImage()[0], bx - okX + ofsX, by - okY + ofsY + okazariOfsY, okW,
							okH, okW, okH, io);
					// <-- TEST
				}
			}

			// 手前側ののおさげ
			if (b.isBraidType()) {
				int numBr = b.getBraidImage(layer3, 0);
				for (int i = 0; i < numBr; i++) {
					// g2.drawImage(layer.getImage()[i], braid.getScreenRect()[layer.getDir()[i]].x,
					// braid.getScreenRect()[layer.getDir()[i]].y + faceOfsY,
					// braid.getScreenRect()[layer.getDir()[i]].width, braid.getScreenRect()[layer.getDir()[i]].height,
					// io);
					drawBody(g2, z, faceOfsY, layer3.getImage()[i], braid.getScreenRect()[layer3.getDir()[i]].getX(),
							braid.getScreenRect()[layer3.getDir()[i]].getY() + faceOfsY,
							braid.getScreenRect()[layer3.getDir()[i]].getWidth(), braid.getScreenRect()[layer3.getDir()[i]].getHeight(),
							braid.getScreenRect()[layer3.getDir()[i]].getWidth(), braid.getScreenRect()[layer3.getDir()[i]].getHeight(), io);
				}
			}

		}

		// アタッチメントの描画
		for (Attachment at : b.getAttach()) {
			at.getBoundaryShape(rectTmp);
			int atX = Translate.transSize(rectTmp.getX());
			int atY = Translate.transSize(rectTmp.getY());
			int atW = Translate.transSize(rectTmp.getWidth());
			int atH = Translate.transSize(rectTmp.getHeight());
			int ofsX = Translate.transSize(at.getOfsX());
			int ofsY = Translate.transSize(at.getOfsY());
			int parentOrigin = at.getParentOrigin();
			int bx, by;
			if (parentOrigin == 0) {
				bx = base.getScreenRect()[direction].getX();
				by = base.getScreenRect()[direction].getY() + base.getPivotY();
			} else {
				bx = expand.getScreenRect()[direction].getX();
				by = expand.getScreenRect()[direction].getY() + base.getPivotY();
			}
			if (b.getDirection() == Direction.RIGHT) {
				bx -= base.getPivotX();
				ofsX = -ofsX;
			} else {
				bx += base.getPivotX();
			}
			// g2.drawImage(at.getImage(b), bx - atX + ofsX, by - atY + ofsY, atW, atH, io);
			drawBody(g2, z, ofsY, at.getImage(b), bx - atX + ofsX, by - atY + ofsY, atW, atH, atW, atH, io);
		}
	}

	/**
	 * ゆっくりの描画
	 * 
	 * @param g2           Graphics2D
	 * @param z            高さ
	 * @param ofsY         オフセットY座標
	 * @param img          バッファイメージ
	 * @param x            X座標
	 * @param y            Y座標
	 * @param expandWidth  広がった幅
	 * @param expandHeight 広がった奥行き
	 * @param baseWidth    基本幅
	 * @param basedHeight  基本奥行き
	 * @param io           イメージオブザーバ
	 */
	public static void drawBody(Graphics2D g2, int z, int ofsY, BufferedImage img, int x, int y, int expandWidth,
			int expandHeight, int baseWidth, int basedHeight, ImageObserver io) {
		if (img == null) {
			return;
		}
		if (0 <= z) {
			z = 0;
		}
		int tz = 0;
		if (z < 0) {
			tz = Translate.translateZ(z);
			tz = tz - ofsY;
			if (0 <= tz) {
				tz = 0;
			}
		}
		// 領域外は描写しない
		if (Translate.getFieldH() < y) {
			return;
		}

		int dx1 = x;
		int dy1 = y;
		int dx2 = dx1 + expandWidth;
		int dy2 = dy1 + expandHeight + tz;
		int sx1 = 0;
		int sy1 = 0;
		int nWidth = img.getWidth();
		int nHeight = img.getHeight();
		int sx2 = nWidth;
		int sy2 = nHeight + tz * nHeight / expandHeight;
		g2.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, io);
	}
}

