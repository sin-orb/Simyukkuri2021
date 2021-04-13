package src.draw;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import src.base.Obj;
import src.enums.Type;




/*************************************************

	背景の部品タイプイメージ


*/
public class TerrainBillboard extends Obj {

	private BufferedImage image;
	private AffineTransform xform;

	public TerrainBillboard(BufferedImage img) {
		objType = Type.BG_OBJECT;
		image = img;
		xform = new AffineTransform();
	}
	
	public BufferedImage getImage() {
		return image;
	}

	public void scale(double sx, double sy) {
		xform.scale(sx, sy);
	}

	public void trans(double tx, double ty) {
		xform.translate(tx, ty);
	}
	
	public void draw(Graphics2D g2, ImageObserver obs) {
		g2.drawImage(image, xform, obs);
	}
}


