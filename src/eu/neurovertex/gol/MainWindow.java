package eu.neurovertex.gol;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * Main Window of the GUI. Manages everything in the GUI except the display of the automaton.
 * @author Neurovertex
 *         Date: 18/10/2014, 22:40
 */
public class MainWindow extends JFrame implements ActionListener, Runnable, ChangeListener {
	private final AutomatonPanel panel;
	private boolean playing = false;
	private Optional<ActionEvent> command = Optional.empty();
	private int delay = 100;
	private final JSpinner ratio, period;
	private final JLabel output, status;
	private final JPopupMenu resizePopup;

	public MainWindow() {
		super("Cellular Automaton");

		JToolBar toolBar = new JToolBar(SwingConstants.HORIZONTAL);
		toolBar.setFloatable(false);
		JButton step = new JButton("Step"),
				play = new JButton("Play/Pause"),
				rand = new JButton("Randomize"),
				resize = new JButton("Resize ▼");
		step.setActionCommand("step");
		step.addActionListener(this);

		play.setActionCommand("play");
		play.addActionListener(this);

		rand.setActionCommand("rand");
		rand.addActionListener(this);

		resizePopup = new JPopupMenu("test");
		{
			JMenuItem item;
			for (int i = 1; i <= 10; i ++) {
				item = new JMenuItem(String.format("%dx%d", i*10, i*10));
				item.setActionCommand(String.format("resize_%d_%d", i * 10, i * 10));
				item.addActionListener(this);
				resizePopup.add(item);
			}
		}

		resize.setActionCommand("resize");
		resize.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				resizePopup.show(e.getComponent(), e.getX(), e.getY());
			}
		});

		ratio = new JSpinner(new SpinnerNumberModel(0.5, 0, 1.0, 0.1));
		ratio.setMaximumSize(new Dimension(40, 50));

		period = new JSpinner(new SpinnerNumberModel(100, 10, 1000, 10));
		period.setMaximumSize(new Dimension(45, 50));
		period.addChangeListener(this);

		toolBar.add(step);
		toolBar.add(play);
		toolBar.addSeparator();
		toolBar.add(rand);
		toolBar.add(new JLabel(" ratio:"));
		toolBar.add(ratio);
		toolBar.add(new JLabel(" time period:"));
		toolBar.add(period);
		toolBar.add(resize);

		add(toolBar, BorderLayout.NORTH);

		panel = new AutomatonPanel(this);
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				MainWindow.this.mouseClicked(e);
			}
		});
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

		Thread t = new Thread(this);
		t.setName("Update thread");
		t.setDaemon(true);
		t.start();
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

	public AutomatonPanel getAutomatonPanel() {
		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		command = Optional.of(e);
	}

	@SuppressWarnings("InfiniteLoopStatement")
	@Override
	public void run() {
		long next = System.currentTimeMillis() + delay;
		boolean step = false;
		try {
			while (true) {
				synchronized (this) {
					if (command.isPresent()) {
						switch (command.get().getActionCommand()) {
							case "step":
								playing = false;
								step = true;
								break;
							case "play":
								playing = !playing;
								setStatus((playing) ? String.format("Iterating at %.1ffps", 1000.0 / delay) : " ");
								break;
							case "rand":
								playing = false;
								setOutput(" ");
								panel.getAutomaton().ifPresent(a -> a.randomize(((SpinnerNumberModel) ratio.getModel()).getNumber().doubleValue()));
								break;
							default:
								if (command.get().getActionCommand().startsWith("resize_")) {
									String[] parts = command.get().getActionCommand().split("_");
									int w = Integer.parseInt(parts[1]), h = Integer.parseInt(parts[2]);
									panel.getAutomaton().ifPresent(a -> a.resize(w, h));
									setOutput(" ");
									playing = step = false;
								}
								break;
						}
						command = Optional.empty();
					}
					if ((playing || step) && panel.getAutomaton().isPresent()) {
						panel.getAutomaton().get().iterate();
						step = false;
					}
				}
				Thread.sleep(Math.max(next - System.currentTimeMillis(), 10));
				while ((next-System.currentTimeMillis()) < 10) // Sauter des frames en cas de ralentissement.
					next += delay;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		delay = Math.max(((SpinnerNumberModel) period.getModel()).getNumber().intValue(), 10); // Sanity check
		if (playing)
			setStatus(String.format("Iterating at %.1ffps", 1000.0 / delay));
	}

	public void mouseClicked(MouseEvent e) {
		Point pos = panel.translateCoordinates(e.getX(), e.getY());
		if (pos != null)
			panel.getAutomaton().get().set(pos.x, pos.y, panel.getAutomaton().get().get(pos.x, pos.y) == State.ZERO ? State.ONE : State.ZERO);
	}
}
