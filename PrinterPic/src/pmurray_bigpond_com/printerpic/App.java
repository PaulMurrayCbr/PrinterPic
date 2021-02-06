package pmurray_bigpond_com.printerpic;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
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
		getContentPane().add(controlPanel, BorderLayout.SOUTH);
		setupControlPanel(controlPanel);

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

	private void setupControlPanel(JPanel controlPanel) {
		controlPanel.setLayout(new FlowLayout());

		JPanel pp = new JPanel();
		pp.setBorder(new TitledBorder("Printer"));
		controlPanel.add(pp);
		
		pp.setLayout(new BoxLayout(pp, BoxLayout.Y_AXIS));
		JPanel ln = new JPanel();
		ln.setLayout(new BoxLayout(ln, BoxLayout.X_AXIS));
		ln.add(new JLabel("Bed"));
		{
			final JTextField f = new JTextField();
			f.setColumns(6);
			f.setText(Float.toString(gcodePAne.getWidthmm()));

			f.addActionListener(e -> {
				try {
					int mm = Integer.valueOf(f.getText());
					if (mm > 0) {
						gcodePAne.setWidthmm(mm);
					}
				} catch (Exception ex) {
				}
				f.setText(Float.toString(gcodePAne.getWidthmm()));
			});

			ln.add(f);
		}
		ln.add(new JLabel("\u00D7"));
		{
			final JTextField f = new JTextField();
			f.setColumns(6);
			f.setText(Float.toString(gcodePAne.getHeightmm()));
			f.addActionListener(e -> {
				try {
					int mm = Integer.valueOf(f.getText());
					if (mm > 0) {
						gcodePAne.setHeightmm(mm);
					}
				} catch (Exception ex) {
				}
				f.setText(Float.toString(gcodePAne.getHeightmm()));
			});
			ln.add(f);
		}
		ln.add(new JLabel("mm"));
		pp.add(ln);

		ln = new JPanel();
		ln.setLayout(new BoxLayout(ln, BoxLayout.X_AXIS));
		ln.add(new JLabel("Nozzle"));
		pp.add(ln);
		{
			final JTextField f = new JTextField();
			f.setColumns(5);
			f.setText(Float.toString(gcodePAne.getNozzleWidth()));
			f.addActionListener(e -> {
				try {
					float mm = Float.valueOf(f.getText());
					if (mm > 0) {
						gcodePAne.setNozzleWidth(mm);
					}
				} catch (Exception ex) {
				}
				f.setText(Float.toString(gcodePAne.getNozzleWidth()));
			});
			ln.add(f);
		}
		ln.add(new JLabel("mm"));
		
		 pp = new JPanel();
		pp.setBorder(new TitledBorder("Filament"));
		controlPanel.add(pp);
		
		pp.setLayout(new BoxLayout(pp, BoxLayout.Y_AXIS));
		
		ln = new JPanel();
		ln.setLayout(new BoxLayout(ln, BoxLayout.X_AXIS));
		ln.add(new JLabel("Width"));
		pp.add(ln);
		{
			final JTextField f = new JTextField("(tbd)");
			f.setColumns(5);
			ln.add(f);
			f.setEnabled(false);
		}
		ln.add(new JLabel("mm"));
		
		ln = new JPanel();
		ln.setLayout(new BoxLayout(ln, BoxLayout.X_AXIS));
		ln.add(new JLabel("Width"));
		pp.add(ln);
		{
			final JRadioButton d = new JRadioButton("Dark");
			ln.add(d);
			d.setSelected(gcodePAne.isBlackOnWhite());
			final JRadioButton l = new JRadioButton("Light");
			ln.add(l);
			l.setSelected(!gcodePAne.isBlackOnWhite());
			
			final ButtonGroup btns = new ButtonGroup();
			btns.add(d);
			btns.add(l);
			d.addActionListener(e -> gcodePAne.setBlackOnWhite(true));
			l.addActionListener(e-> gcodePAne.setBlackOnWhite(false));
		}
		ln.add(new JLabel("mm"));
		

	}

}
