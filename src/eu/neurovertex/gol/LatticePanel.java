package eu.neurovertex.gol;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * @author Neurovertex
 *         Date: 18/10/2014, 22:41
 */
public class LatticePanel extends JPanel implements Observer {
	private static final int DEFAULT_SIDE = 15;

	private final MainWindow window;
	private volatile Image buffer = new BufferedImage(DEFAULT_SIDE, DEFAULT_SIDE, BufferedImage.TYPE_INT_ARGB);

	public LatticePanel(MainWindow window) {
		super();
		this.window = window;
		setPreferredSize(new Dimension(DEFAULT_SIDE * 100, DEFAULT_SIDE));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(buffer, 0, 0, null);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof StaticLattice)
			drawLattice((StaticLattice) arg);
	}

	void drawLattice(Lattice lattice) {
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		int side = Math.min(getWidth() / lattice.getWidth(), getHeight() / lattice.getHeight()),
				actualWidth = side * lattice.getWidth(), actualHeight = side * lattice.getHeight(),
				xoffset = (getWidth() - actualWidth) / 2, yoffset = (getHeight() - actualHeight) / 2;
		g.setClip(xoffset, yoffset, actualWidth - 1, actualHeight - 1);

		int w = lattice.getWidth(), h = lattice.getHeight();
		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++) {
				g.setColor(lattice.getColor(i, j));
				g.fillRect(i * side + xoffset, j * side + yoffset, side - 1, side - 1);
			}
		g.setColor(Color.DARK_GRAY);
		for (int i = 1; i < w; i++)
			g.drawLine(i * side - 1 + xoffset, yoffset, i * side - 1 + xoffset, actualHeight + yoffset);

		for (int j = 1; j < h; j++)
			g.drawLine(xoffset, j * side - 1 + yoffset, actualWidth + xoffset, j * side - 1 + yoffset);
		buffer = img;
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
		window.pack();
	}

}
