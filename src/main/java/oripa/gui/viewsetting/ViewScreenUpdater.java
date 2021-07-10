package oripa.gui.viewsetting;

import oripa.gui.presenter.creasepattern.ScreenUpdater;

public interface ViewScreenUpdater extends ScreenUpdater {

	public static final String REDRAW_REQUESTED = "redraw requested";


	/**
	 * a option for View classes to change behavior.
	 * @return
	 */
	public abstract java.awt.event.KeyListener getKeyListener();

}