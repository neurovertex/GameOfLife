package eu.neurovertex.gol;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jeremie
 * Date: 03/11/2014
 * Time: 22:15
 */
public interface Lattice {
	public boolean get(int i, int j);
	public Color getColor(int i, int j);
	public int getWidth();
	public int getHeight();
}
