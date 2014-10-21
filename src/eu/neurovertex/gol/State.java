package eu.neurovertex.gol;

/**
 * @author Neurovertex
 *         Date: 18/10/2014, 13:57
 */
public enum State {
	ZERO, ONE;

	public static State getDefault() {
		return values()[0];
	}
}
