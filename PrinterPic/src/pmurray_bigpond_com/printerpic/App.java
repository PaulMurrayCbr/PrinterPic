package pmurray_bigpond_com.printerpic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileSystemView;

public class App extends JFrame {

	ImagePane imagePane = new ImagePane();
	GCodePane gcodePAne = new GCodePane();

	BufferedImage img;

	boolean fillBlack = true;
	double gamma = .5;

	public App() {
		JMenuBar bar = new JMenuBar();
		JMenu menu;
		JMenuItem item;
		menu = new JMenu("Hilbert Image Thingy");
		item = new JMenuItem("Exit");
		menu.add(item);
		bar.add(menu);
		item.addActionListener(e -> {
			System.exit(0);
		});
		menu = new JMenu("File");
		item = new JMenuItem("Load");
		item.addActionListener((e) -> loadFile());
		menu.add(item);
		item = new JMenuItem("Test Pattern");
		item.addActionListener((e) -> loadTestPattern());
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

		loadTestPattern();
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
			f.setText(Integer.toString(gcodePAne.getWidthmm()));

			f.addActionListener(e -> {
				try {
					int mm = Integer.valueOf(f.getText());
					if (mm > 0) {
						gcodePAne.setWidthmm(mm);
						if (img != null)
							renderImage(img);
					}
				} catch (Exception ex) {
				}
				f.setText(Integer.toString(gcodePAne.getWidthmm()));
			});

			ln.add(f);
		}
		ln.add(new JLabel("\u00D7"));
		{
			final JTextField f = new JTextField();
			f.setColumns(6);
			f.setText(Integer.toString(gcodePAne.getHeightmm()));
			f.addActionListener(e -> {
				try {
					int mm = Integer.valueOf(f.getText());
					if (mm > 0) {
						gcodePAne.setHeightmm(mm);
						if (img != null)
							renderImage(img);
					}
				} catch (Exception ex) {
				}
				f.setText(Integer.toString(gcodePAne.getHeightmm()));
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
						if (img != null)
							renderImage(img);
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
			l.addActionListener(e -> gcodePAne.setBlackOnWhite(false));
		}
		ln.add(new JLabel("mm"));

		pp = new JPanel();
		pp.setBorder(new TitledBorder("Generation"));
		pp.setLayout(new BoxLayout(pp, BoxLayout.Y_AXIS));
		controlPanel.add(pp);

		ln = new JPanel();
		ln.setLayout(new BoxLayout(ln, BoxLayout.X_AXIS));
		pp.add(ln);
		{
			final JRadioButton d = new JRadioButton("Fill Black");
			ln.add(d);
			d.setSelected(fillBlack);
			final JRadioButton l = new JRadioButton("FillWhite");
			ln.add(l);
			l.setSelected(!fillBlack);

			final ButtonGroup btns = new ButtonGroup();
			btns.add(d);
			btns.add(l);
			d.addActionListener(e -> {
				fillBlack = true;
				renderImage(img);
			});
			l.addActionListener(e -> {
				fillBlack = false;
				renderImage(img);
			});
		}
		ln = new JPanel();
		ln.setLayout(new BoxLayout(ln, BoxLayout.X_AXIS));
		pp.add(ln);
		{
			ln.add(new JLabel("gamma"));
			JSlider sld = new JSlider(0, 1000, 500);
			ln.add(sld);

			sld.addChangeListener(e -> {
				gamma = Math.pow(20, sld.getValue() / 500.0 - 1);
				renderImage(img);
			});
		}

	}

	private void loadFile() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		int returnValue = jfc.showOpenDialog(null);
		// int returnValue = jfc.showSaveDialog(null);

		if (returnValue != JFileChooser.APPROVE_OPTION)
			return;

		File selectedFile = jfc.getSelectedFile();

		BufferedImage image;
		try {
			image = ImageIO.read(selectedFile);
		} catch (IOException e) {
			JOptionPane.showInputDialog(this, e, "Can't load", JOptionPane.ERROR_MESSAGE);
			return;
		}

		ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		colorConvert.filter(image, image);

		this.img = image;
		renderImage(img);
		imagePane.setImage(img);

	}

	private void loadTestPattern() {
		img = new BufferedImage(640, 480, BufferedImage.TYPE_BYTE_GRAY);
		Graphics gg = img.getGraphics();
		PaintUtils.inCtx(gg, g -> {
			g.setColor(Color.white);
			g.fillRect(0, 0, 640, 480);

			double inc = .01;
			for (double th = 0; th < 2 * Math.PI; th += inc) {
				int c = (int) (th / 2 / Math.PI * 3 * 256) % 256;
				g.setColor(new Color(c, c, c));
				Path2D p = new Path2D.Double();

				p.moveTo(320, 240);
				p.lineTo(320 + Math.cos(th) * (320 - 17), 240 + Math.sin(th) * (240 - 17));
				p.lineTo(320 + Math.cos(th + inc) * (320 - 17), 240 + Math.sin(th + inc) * (240 - 17));
				p.closePath();
				g.fill(p);
			}
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			for (double th = 0; th < 2 * Math.PI; th += inc) {
				int c = (int) (th / 2 / Math.PI * 3 * 256) % 256;
				g.setColor(new Color(c, c, c));
				Path2D p = new Path2D.Double();

				p.moveTo(320, 240);
				p.lineTo(320 + Math.cos(th) * (320 - 16), 240 + Math.sin(th) * (240 - 16));
				p.lineTo(320 + Math.cos(th + inc) * (320 - 16), 240 + Math.sin(th + inc) * (240 - 16));
				p.closePath();
				g.fill(p);
			}
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

			g.setColor(Color.black);
			g.fillRect(0, 0, 32, 32);
			g.fillRect(640 - 32, 480 - 32, 32, 32);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(Color.black);
			g.fillOval(0, 480 - 32, 32, 32);
			g.fillOval(640 - 32, 0, 32, 32);
			g.setColor(Color.white);
			g.fillOval(0, 0, 32, 32);
			g.fillOval(640 - 32, 480 - 32, 32, 32);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

			for (int x = 0; x < 640 - 64; x++) {
				int c = x % 256;
				g.setColor(new Color(c, c, c));
				g.fillRect(x + 32, 0, 1, 32);
				g.setColor(new Color(c, c, c));
				g.fillRect(x + 32, 480 - 32, 1, 32);
			}
			for (int y = 0; y < 480 - 64; y++) {
				int c = y % 256;
				g.setColor(new Color(c, c, c));
				g.fillRect(0, y + 32, 32, 1);
				g.setColor(new Color(c, c, c));
				g.fillRect(640 - 32, y + 32, 32, 1);
			}

		});
		gg.dispose();
		imagePane.setImage(img);
		renderImage(img);
	}

	final Object cgodePaneOwnerMutex = new Object();
	volatile DrawerThread gcodePaneOwner = null;
	volatile DrawerThread nextGcodePaneOwner = null;

	class DrawerThread extends Thread {
		final Drawer d;

		public String toString() {
			return d + "-thread";
		}

		DrawerThread(Drawer d) {
			this.d = d;
			this.setDaemon(true);
		}

		public void run() {
			synchronized (cgodePaneOwnerMutex) {
				nextGcodePaneOwner = this; // I am the newest and latest owner of the mutex.
				if (gcodePaneOwner != null && gcodePaneOwner.isAlive()) {
					gcodePaneOwner.d.stopDrawing();
					gcodePaneOwner.interrupt();

					while (gcodePaneOwner != null && gcodePaneOwner.isAlive()) {
						try {
							cgodePaneOwnerMutex.wait();
						} catch (InterruptedException e) {
						}
					}
				}

				if (nextGcodePaneOwner == this) {
					gcodePaneOwner = this;
				} else {
					return;
				}

			}
			try {
				d.go();
			} finally {
				synchronized (cgodePaneOwnerMutex) {
					SwingUtilities.invokeLater(() -> gcodePAne.repaint());
					if (gcodePaneOwner == this) {
						gcodePaneOwner = null;
						cgodePaneOwnerMutex.notifyAll();
					}
				}
			}
		}
	}

	void renderImage(BufferedImage img) {
		if (img == null)
			return;
		gcodePAne.clear();
		// I know it's rude to spawn threads rather than spawing drawers ... but meh.
		new DrawerThread(new HilbertDrawer(img, gcodePAne, fillBlack, gamma)).start();

	}

}
