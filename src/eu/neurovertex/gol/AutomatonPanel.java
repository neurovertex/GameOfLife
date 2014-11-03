package eu.neurovertex.gol;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * @author Neurovertex
 *         Date: 18/10/2014, 22:41
 */
public class AutomatonPanel extends JPanel implements Observer {
	private static final int DEFAULT_SIDE = 660;
	private static final Map<State, Color> colors = new HashMap<>();

	static {
		colors.put(null, Color.RED);
		colors.put(State.ZERO, Color.BLACK);
		colors.put(State.ONE, Color.GREEN);
	}

	private Optional<CellularAutomaton> automaton = Optional.empty();
	private final MainWindow window;

	public AutomatonPanel(MainWindow window) {
		super();
		this.window = window;
		setPreferredSize(new Dimension(DEFAULT_SIDE, DEFAULT_SIDE));
	}

	public AutomatonPanel(CellularAutomaton automaton, MainWindow window) {
		super();
		this.window = window;
		setAutomaton(automaton);
	}

	public void setAutomaton(CellularAutomaton auto) {
		if (this.automaton.isPresent())
			this.automaton.get().deleteObserver(this);
		this.automaton = Optional.of(auto);
		auto.addObserver(this);
		int side = DEFAULT_SIDE / Math.max(auto.getWidth(), auto.getHeight());
		setPreferredSize(new Dimension(side * auto.getWidth() - 1, side * auto.getHeight() - 1));
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!automaton.isPresent())
			return;
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		CellularAutomaton auto = automaton.get();
		int side = Math.min(getWidth() / auto.getWidth(), getHeight() / auto.getHeight()),
				actualWidth = side * auto.getWidth(), actualHeight = side * auto.getHeight(),
				xoffset = (getWidth() - actualWidth) / 2, yoffset = (getHeight() - actualHeight) / 2;
		g.setClip(xoffset, yoffset, actualWidth - 1, actualHeight - 1);

		int w = auto.getWidth(), h = auto.getHeight();
		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++) {
				g.setColor(colors.get(auto.get(i, j)));
				g.fillRect(i * side + xoffset, j * side + yoffset, side - 1, side - 1);
			}
		g.setColor(Color.DARK_GRAY);
		for (int i = 1; i < w; i++)
			g.drawLine(i * side - 1 + xoffset, yoffset, i * side - 1 + xoffset, actualHeight + yoffset);

		for (int j = 1; j < h; j++)
			g.drawLine(xoffset, j * side - 1 + yoffset, actualWidth + xoffset, j * side - 1 + yoffset);
	}

	@Override
	public void update(Observable o, Object arg) {
		repaint();
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
		window.pack();
	}

	public Point translateCoordinates(int mouseX, int mouseY) {
		if (!automaton.isPresent())
			return null;
		CellularAutomaton auto = automaton.get();
		int side = Math.min(getWidth() / auto.getWidth(), getHeight() / auto.getHeight()),
				xoffset = (getWidth() - side * auto.getWidth()) / 2, yoffset = (getHeight() - side * auto.getHeight()) / 2;
		return new Point((mouseX - xoffset)/side, (mouseY - yoffset)/side);
	}

	public Optional<CellularAutomaton> getAutomaton() {
		return automaton;
	}
}
