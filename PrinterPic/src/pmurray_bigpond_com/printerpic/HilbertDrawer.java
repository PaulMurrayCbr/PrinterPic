package pmurray_bigpond_com.printerpic;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

enum HDir {
	LL_LR, LL_UL, UL_UR, UL_LL, UR_UL, UR_LR, LR_UR, LR_LL
}

public class HilbertDrawer extends Drawer {

	public HilbertDrawer(BufferedImage img, GCodePane g) {
		super(img, g);
	}

	boolean first = true;
	
	@Override
	void go() {
		drawQuadrant(0, 0, 0, HDir.LL_LR);
	}

	/**
	 * 
	 * @param depth
	 *            - zero is the whole image, 1 is one of the quadrants of the image,
	 *            2 is 1 16th of the image, etc
	 * @param qx,
	 *            qy - the location of the quadrant, in terms of the depth. if depth
	 *            is 2, then the lower right quadrant will be (3,3)
	 * @param hdir
	 *            - if this quadrant needs to be broken into 4, this is how to draw
	 *            the smaller fractal
	 * 
	 */
	void drawQuadrant(final int depth, final int qx, final int qy, HDir hdir) {
		Consumer<HDir> UL = h -> drawQuadrant(depth + 1, qx * 2, qy * 2, h);
		Consumer<HDir> UR = h -> drawQuadrant(depth + 1, qx * 2 + 1, qy * 2, h);
		Consumer<HDir> LL = h -> drawQuadrant(depth + 1, qx * 2, qy * 2 + 1, h);
		Consumer<HDir> LR = h -> drawQuadrant(depth + 1, qx * 2 + 1, qy * 2 + 1, h);

		if (need_need_to_break(depth, qx, qy)) {
			// i'll do this the boring and long-winded way

			switch (hdir) {
			case LL_LR:
				LL.accept(HDir.LL_UL);
				UL.accept(HDir.LL_LR);
				UR.accept(HDir.LL_LR);
				LR.accept(HDir.UR_LR);
				break;
			case LL_UL:
				LL.accept(HDir.LL_LR);
				LR.accept(HDir.LL_UL);
				UR.accept(HDir.LL_UL);
				UL.accept(HDir.UR_UL);
				break;
			case LR_LL:
				LR.accept(HDir.LR_UR);
				UR.accept(HDir.LR_LL);
				UL.accept(HDir.LR_LL);
				LL.accept(HDir.UL_LL);
				break;
			case LR_UR:
				LR.accept(HDir.LR_LL);
				LL.accept(HDir.LR_UR);
				UL.accept(HDir.LR_UR);
				UR.accept(HDir.UL_UR);
				break;
			case UL_LL:
				UL.accept(HDir.UL_UR);
				UR.accept(HDir.UL_LL);
				LR.accept(HDir.UL_LL);
				LL.accept(HDir.LR_LL);
				break;
			case UL_UR:
				UL.accept(HDir.UL_LL);
				LL.accept(HDir.UL_UR);
				LR.accept(HDir.UL_UR);
				UR.accept(HDir.LR_UR);
				break;
			case UR_LR:
				UR.accept(HDir.UR_UL);
				UL.accept(HDir.UR_LR);
				LL.accept(HDir.UR_LR);
				LR.accept(HDir.LL_LR);
				break;
			case UR_UL:
				UR.accept(HDir.UR_LR);
				LR.accept(HDir.UR_UL);
				LL.accept(HDir.UR_UL);
				UL.accept(HDir.LL_UL);
				break;
			}

		} else {
			// draw to the center point of this quadrant
			int sz = 1 << depth;
			double xw = (double) g.getWidthmm() / (double) sz;
			double yw = (double) g.getHeightmm() / (double) sz;
			if(first) {
			g.moveTo(xw * (qx + .5), yw * (qy + .5));
			first = false;
			} else {
				g.lineTo(xw * (qx + .5), yw * (qy + .5));
				
			}
		}
	}

	private boolean need_need_to_break(int depth, int qx, int qy) {
		return depth <= 2;
	}

}
