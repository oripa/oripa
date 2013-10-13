package oripa.paint;

import java.awt.event.KeyListener;

public interface ScreenUpdaterInterface {

	/**
	 * send a request to a screen.
	 */
	public abstract void updateScreen();

	/**
	 * a option to change behavior.
	 * @return
	 */
	public abstract KeyListener getKeyListener();

}