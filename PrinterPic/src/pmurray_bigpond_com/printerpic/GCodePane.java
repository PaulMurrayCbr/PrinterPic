package pmurray_bigpond_com.printerpic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

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

	public GCodePane() {
		gcode.add(new MoveTo(10, 10));
		gcode.add(new LineTo(10, 90));
		gcode.add(new LineTo(90, 90));
		gcode.add(new LineTo(90, 10));
		gcode.add(new LineTo(10, 10));
	}

	public boolean isBlackOnWhite() {
		return blackOnWhite;
	}

	public void setBlackOnWhite(boolean blackOnWhite) {
		this.blackOnWhite = blackOnWhite;
		repaint();
	}

	public void paintComponent(Graphics gg) {
		Graphics2D g = (Graphics2D) gg;

		AffineTransform gt = g.getTransform();
		RenderingHints grh = g.getRenderingHints();
		Stroke gs = g.getStroke();
		Color gc = g.getColor();

		try {
			Color black = new Color(0x10, 0x10, 0x10);
			Color white = new Color(0xF8, 0xF8, 0xF8);

			Dimension dd = this.getSize();
			Insets ins = this.getInsets();

			int pixx = dd.width - ins.left - ins.right;
			int pixy = dd.height - ins.top - ins.bottom;

			g.translate(ins.left, ins.top);

			float scaleToFitX = (float)pixx / (float)widthmm;
			float scaleToFitY = (float)pixy / (float)heightmm;

			if (scaleToFitX < scaleToFitY) {
				g.scale(scaleToFitX, scaleToFitX);
			} else {
				g.scale(scaleToFitY, scaleToFitY);
			}
			
			g.setColor(blackOnWhite ? white : black);
			g.fillRect(0, 0, widthmm, heightmm);

			g.setColor(blackOnWhite ? black : white);
			g.setStroke(new BasicStroke(nozzleWidth));
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			g.drawOval(0, 0, 100, 100);

			
			 Path2D path = new Path2D.Double();
			 path.moveTo(0, 0);
			
			 for(GCode gco: gcode) {
			 gco.draw(path);
			 }
			 g.draw(path);
			 

		} finally {
			g.setTransform(gt);
			g.setRenderingHints(grh);
			g.setStroke(gs);
			g.setColor(gc);
		}
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
