package pmurray_bigpond_com.printerpic;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class App extends JFrame {
	
	ImagePane imagePane = new ImagePane();
	GCodePane gcodePAne = new GCodePane();
	
	public App() {
		JMenuBar bar = new JMenuBar();
		JMenu menu;
		JMenuItem item;
		menu = new JMenu("USBTerm");
		item = new JMenuItem("Exit");
		menu.add(item);
		bar.add(menu);
		item.addActionListener(e -> {
			System.exit(0);
		});
		menu = new JMenu("File");
		item = new JMenuItem("Load");
		menu.add(item);
		bar.add(menu);
		item.addActionListener(e -> {
		});
		getContentPane().add(bar, BorderLayout.NORTH);
		
		JPanel controlPanel = new JPanel();
		controlPanel.setBorder(new TitledBorder("\u2646"));
		getContentPane().add(controlPanel, BorderLayout.SOUTH);
		
		JPanel main = new JPanel();
		main.setLayout(new GridLayout());
		getContentPane().add(main, BorderLayout.CENTER);
		
		main.setLayout(new GridLayout(1, 2));
		main.add(imagePane);
		main.add(gcodePAne);
		
		main.setBorder(new TitledBorder("main"));
		imagePane.setBorder(new TitledBorder("Source"));
		gcodePAne.setBorder(new TitledBorder("Print"));
		
	}
	
	
}

