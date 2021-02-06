package pmurray_bigpond_com.printerpic;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class GCodePane extends JComponent {
	int widthmm = 100;

	public int getWidthmm() {
		return widthmm;
	}

	public void setWidthmm(int widthmm) {
		this.widthmm = widthmm;
		repaint();
	}

	public int getHeightmm() {
		return heightmm;
	}

	public void setHeightmm(int heightmm) {
		this.heightmm = heightmm;
		repaint();
	}

	public float getNozzleWidth() {
		return nozzleWidth;
	}

	public void setNozzleWidth(float nozzleWidth) {
		this.nozzleWidth = nozzleWidth;
		repaint();
	}

	int heightmm = 100;
	float nozzleWidth = .5f;

	boolean blackOnWhite = true;

	List<GCode> gcode = new ArrayList<>();
	volatile boolean gcodeUpdated = false;

	void clear() {
		synchronized (gcode) {
			gcode.clear();
			gcodeUpdated = true;
		}
	}

	void moveTo(double xmm, double ymm) {
		synchronized (gcode) {
			gcode.add(new MoveTo((float) xmm, (float) ymm));
			gcodeUpdated = true;
		}
	}

	void lineTo(double xmm, double ymm) {
		synchronized (gcode) {
			gcode.add(new LineTo((float) xmm, (float) ymm));
			gcodeUpdated = true;
		}
	}

	void moveTo(int xmm, int ymm) {
		synchronized (gcode) {
			gcode.add(new MoveTo((float) xmm, (float) ymm));
			gcodeUpdated = true;
		}
	}

	void lineTo(int xmm, int ymm) {
		synchronized (gcode) {
			gcode.add(new LineTo((float) xmm, (float) ymm));
			gcodeUpdated = true;
		}
	}

	public GCodePane() {
		setDoubleBuffered(true);
	}

	public boolean isBlackOnWhite() {
		return blackOnWhite;
	}

	public void setBlackOnWhite(boolean blackOnWhite) {
		this.blackOnWhite = blackOnWhite;
		repaint();
	}

	public void paintComponent(Graphics gg) {
		PaintUtils.back(this, gg);
		PaintUtils.inCtx(gg, g -> {
			Dimension dd = this.getSize();
			Insets ins = this.getInsets();

			int pixx = dd.width - ins.left - ins.right;
			int pixy = dd.height - ins.top - ins.bottom;

			g.translate(ins.left, ins.top);

			float scaleToFitX = (float) pixx / (float) widthmm;
			float scaleToFitY = (float) pixy / (float) heightmm;

			if (scaleToFitX < scaleToFitY) {
				g.scale(scaleToFitX, scaleToFitX);
			} else {
				g.scale(scaleToFitY, scaleToFitY);
			}

			g.setColor(blackOnWhite ? PaintUtils.white : PaintUtils.black);
			g.fillRect(0, 0, widthmm, heightmm);

			g.setColor(blackOnWhite ? PaintUtils.black : PaintUtils.white);
			g.setStroke(new BasicStroke(nozzleWidth));
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Path2D path = new Path2D.Double();
			path.moveTo(0, 0);

			Iterator<GCode> gci;
			synchronized (gcode) {
				gci = gcode.iterator();
				gcodeUpdated = false;
			}

			for (;;) {
				GCode gc;
				synchronized (gcode) {
					if (gcodeUpdated) {
						gcodeUpdated = false;
						break;
					}
					if (!gci.hasNext())
						break;
					gc = gci.next();
				}
				gc.draw(path);
			}
			g.draw(path);
		});

	}

}

abstract class GCode {
	abstract void draw(Path2D path);
}

abstract class XY extends GCode {
	float xmm;
	float ymm;

	public XY(float xmm, float ymm) {
		super();
		this.xmm = xmm;
		this.ymm = ymm;
	}

}

class MoveTo extends XY {
	public MoveTo(float xmm, float ymm) {
		super(xmm, ymm);
	}

	@Override
	void draw(Path2D path) {
		path.moveTo(xmm, ymm);
	}

}

class LineTo extends XY {
	public LineTo(float xmm, float ymm) {
		super(xmm, ymm);
	}

	@Override
	void draw(Path2D path) {
		path.lineTo(xmm, ymm);
	}

}
