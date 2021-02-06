package pmurray_bigpond_com.printerpic;

import java.awt.image.BufferedImage;

public class SimpleDrawer extends Drawer {
	
	public SimpleDrawer(BufferedImage img, GCodePane g) {
		super(img, g);
	}
	
	void go() {
		System.out.println("Ok! Time to draw " + this + "!");
		try {
			g.clear();
			for(int i = 0; i < 100; i+= 10) {
				g.lineTo(75,  i);
				g.lineTo(25, i+5);
				Thread.sleep(1000L);
			}
		} catch (InterruptedException e) {
			System.out.println(this + " Interrupted!");
			return;
		}
		System.out.println(this + " done drawing.");
	}
	
}
