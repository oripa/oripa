package oripa.viewsetting;

import oripa.paint.ScreenUpdaterInterface;

public interface ViewScreenUpdater extends ScreenUpdaterInterface {

	String REDRAW_REQUESTED = "redraw requested";


	/**
	 * a option for View classes to change behavior.
	 * @return
	 */
    java.awt.event.KeyListener getKeyListener();

}