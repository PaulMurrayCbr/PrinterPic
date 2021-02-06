package pmurray_bigpond_com.printerpic;

import java.awt.image.BufferedImage;

public class SimpleDrawer extends Drawer {
	
	public SimpleDrawer(BufferedImage img, GCodePane g) {
		super(img, g);
	}
	
	void go() {
		System.out.println("Ok! Time to draw! " + serial);
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			System.out.println("Interrupted! " + serial);
			return;
		}
		System.out.println("Done drawing! " + serial);
	}
	
}
