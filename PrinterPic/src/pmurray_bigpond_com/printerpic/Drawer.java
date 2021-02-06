package pmurray_bigpond_com.printerpic;

import java.awt.image.BufferedImage;

abstract class Drawer {
	BufferedImage img;
	GCodePane g;
	volatile boolean stop = false;

	static int serialct;
	final int serial;

	public Drawer(BufferedImage img, GCodePane g) {
		this.img = img;
		this.g = g;
		this.serial = ++serialct;
	}

	void stopDrawing() {
		stop = true;
	}

	abstract void go();

}
