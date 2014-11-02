package eu.neurovertex.gol;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Main Window of the GUI. Manages everything in the GUI except the display of the automaton.
 * @author Neurovertex
 *         Date: 18/10/2014, 22:40
 */
public class MainWindow extends JFrame {
	private final LatticePanel panel;
	private final JLabel output, status;

	public MainWindow() {
		super("Cellular Automaton");
		panel = new LatticePanel(this);
		add(panel, BorderLayout.CENTER);

		output = new JLabel(" ");
		status = new JLabel(" ");
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new GridLayout(1, 2));
		labelPanel.add(output);
		labelPanel.add(status);
		add(labelPanel, BorderLayout.SOUTH);

		setIconImage(generateIcon());
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();
		setMinimumSize(new Dimension(500, 500));
		setVisible(true);
	}

	public void setOutput(String text) {
		output.setText(text);
	}

	public void setStatus(String text) {
		status.setText(text);
	}

	private Image generateIcon() {
		BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.GREEN);
		g.fillRect(21, 0, 21, 21);
		g.fillRect(42, 21, 21, 21);
		g.fillRect(0, 42, 63, 21);
		return image;
	}

	void drawLattice(Lattice lattice) {
		panel.drawLattice(lattice);
		repaint();
	}
}
