package pmurray_bigpond_com.printerpic;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		App app = new App();
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Toolkit t = Toolkit.getDefaultToolkit();
		app.setSize(new Dimension(//
				t.getScreenSize().width * 3 / 4, //
				t.getScreenSize().height * 3 / 4));
		app.validate();
		app.setLocation(new Point(//
				(t.getScreenSize().width - app.getWidth()) / 2, //
				(t.getScreenSize().height - app.getHeight()) / 2));
		app.setVisible(true);
	}
}
