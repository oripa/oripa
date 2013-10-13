package oripa.viewsetting;

import oripa.paint.ScreenUpdaterInterface;

public interface ViewScreenUpdater extends ScreenUpdaterInterface {

	public static final String REDRAW_REQUESTED = "redraw requested";


	/**
	 * a option for View classes to change behavior.
	 * @return
	 */
	public abstract java.awt.event.KeyListener getKeyListener();

}