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
import java.util.function.Consumer;

import javax.swing.JComponent;

public class PaintUtils {

	static final Color black = new Color(0x10, 0x10, 0x10);
	static final Color white = new Color(0xF8, 0xF8, 0xF8);
	static final Color ltgray = new Color(0xE0, 0xE0, 0xE0);

	static void inCtx(Graphics gg, Consumer<? super Graphics2D> c) {
		Graphics2D g = (Graphics2D) gg;

		AffineTransform gt = g.getTransform();
		RenderingHints grh = g.getRenderingHints();
		Stroke gs = g.getStroke();
		Color gc = g.getColor();
		Shape gclip = g.getClip();

		try {
			c.accept(g);
		} finally {
			g.setTransform(gt);
			g.setRenderingHints(grh);
			g.setStroke(gs);
			g.setColor(gc);
			g.setClip(gclip);
		}

	}

	static void back(final JComponent j, Graphics gg) {
		inCtx(gg, g -> {
			Dimension dd = j.getSize();
			Insets ins = j.getInsets();

			int pixx = dd.width - ins.left - ins.right;
			int pixy = dd.height - ins.top - ins.bottom;

			g.setClip(ins.left, ins.top, pixx, pixy);

			g.translate(ins.left, ins.top);

			g.setColor(white);
			g.fillRect(0, 0, pixx, pixy);
			g.setColor(ltgray);

			for (int x = 0; x < pixx; x += 16) {
				for (int y = 0; y < pixy; y += 16) {
					if (x % 32 != y % 32) {
						g.fillRect(x, y, 16, 16);
					}
				}
			}
		});
	}
}
