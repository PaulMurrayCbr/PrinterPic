package pmurray_bigpond_com.printerpic;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.function.Consumer;

enum HDir {
	LL_LR, LL_UL, UL_UR, UL_LL, UR_UL, UR_LR, LR_UR, LR_LL
}

public class HilbertDrawer extends Drawer {
	boolean fillBlack;
	double gamma;

	public HilbertDrawer(BufferedImage img, GCodePane g, boolean fillBlack, double gamma) {
		super(img, g);
		this.fillBlack = fillBlack;
		this.gamma = gamma;
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
	 * @throws InterruptedException
	 * 
	 */
	void drawQuadrant(final int depth, final int qx, final int qy, HDir hdir) {
		if (stop)
			return;
		if (Thread.interrupted())
			return;

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
			if (first) {
				g.moveTo(xw * (qx + .5), yw * (qy + .5));
				first = false;
			} else {
				g.lineTo(xw * (qx + .5), yw * (qy + .5));

			}
		}
	}

	private boolean need_need_to_break(int depth, int qx, int qy) {
		double multiplier = 1 << depth;

		// If I were to break this quadrant, how densely would the square be covered in
		// filament?

		double quadWidth = g.getWidthmm() / multiplier;
		double quadHeight = g.getHeightmm() / multiplier;

		// I'll just pretend that the path of the filament isn't missing a side
		double filamentLength = quadWidth + quadHeight;

		double coverage = (filamentLength * g.getNozzleWidth()) / (quadWidth * quadHeight);

		// once we black out the flament, return. This is a boundary condition to
		// prevent infinite recursion
		// for pure black/pure white.
		if (coverage >= 2)
			return false;

		// ok. So if I were to split the quadrant into 4, I'd expect about that much
		// coverage.
		// so. How dark is this quadrant of the image?

		Rectangle sample = new Rectangle(//
				(int) (img.getWidth() / (double) multiplier * qx), //
				(int) (img.getHeight() / (double) multiplier * qy), //
				(int) (img.getWidth() / (double) multiplier), //
				(int) (img.getHeight() / (double) multiplier));

		// are we down to less than a pixel?
		// this can happen if we are drawing a fine curve over a coarse pixelated image

		if (sample.x < 0)
			sample.x = 0;
		if (sample.y < 0)
			sample.y = 0;
		if (sample.x + sample.width >= img.getWidth())
			sample.width = img.getWidth() - sample.x;
		if (sample.y + sample.height >= img.getHeight())
			sample.height = img.getHeight() - sample.y;
		if (sample.width < 1)
			sample.width = 1;
		if (sample.height < 1)
			sample.height = 1;

		// ok! get the average brightness
		Raster data = img.getData(sample);
		Raster alpha = img.getAlphaRaster();

		// I know my images are black and white 1 byte
		int[] d = new int[4];
		long totalRed = 0;
		long totalAlpha = 0;

		for (int y = 0; y < sample.height; y++) {
			for (int x = 0; x < sample.width; x++) {
				try {
					data.getPixel(sample.x + x, sample.y + y, d);
					totalRed += d[0];
				} catch (RuntimeException ex) {
					System.err.println(sample);
					System.err.println(data);
					System.err.println(x);
					System.err.println(y);

					throw ex;
				}

				if (alpha != null) {
					try {
						alpha.getPixel(sample.x + x, sample.y + y, d);
						totalAlpha += d[0];
					} catch (RuntimeException ex) {
						System.err.println(sample);
						System.err.println(data);
						System.err.println(x);
						System.err.println(y);

						throw ex;
					}
				}

			}
		}

		double targetCoverage = totalRed / 256.0 / (double) sample.width / (double) sample.height;

		if (fillBlack)
			targetCoverage = 1 - targetCoverage; // convert black to white

		if (alpha != null) {
			targetCoverage *= totalAlpha / 256.0 / (double) sample.width / (double) sample.height;
		}

		targetCoverage = Math.pow(targetCoverage, 1 / gamma);

		return coverage < targetCoverage;
	}

}
