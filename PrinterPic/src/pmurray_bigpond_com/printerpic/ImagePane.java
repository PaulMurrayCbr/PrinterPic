package pmurray_bigpond_com.printerpic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import javax.swing.JComponent;

public class ImagePane extends JComponent {
	BufferedImage img;

	public ImagePane() {
	}

	public void paintComponent(Graphics gg) {
		PaintUtils.back(this, gg);

		if (img == null || img.getHeight() == 0 || img.getWidth() == 0)
			return;

		PaintUtils.inCtx(gg, g -> {
			Dimension dd = this.getSize();
			Insets ins = this.getInsets();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int pixx = dd.width - ins.left - ins.right;
			int pixy = dd.height - ins.top - ins.bottom;

			float scaleToFitX = (float) pixx / (float) img.getWidth();
			float scaleToFitY = (float) pixy / (float) img.getHeight();

			if (scaleToFitX < scaleToFitY) {
				g.drawImage(img, ins.left, ins.top, //
						pixx, (int) (img.getHeight() * scaleToFitX), //
						null);
			} else {
				g.drawImage(img, ins.left, ins.top, //
						(int) (img.getWidth() * scaleToFitY), pixy, //
						null);
			}

		});

	}

	public void setImage(BufferedImage img) {
		this.img = img;
		repaint();
	}

}
