package oripa.viewsetting.main;

import java.awt.event.KeyEvent;

import oripa.controller.paint.GraphicMouseActionInterface;
import oripa.controller.paint.MouseActionHolder;
import oripa.controller.paint.copypaste.CopyAndPasteAction;
import oripa.viewsetting.ViewScreenUpdater;
import oripa.viewsetting.ViewSettingDataBase;

public class ScreenUpdater extends ViewSettingDataBase implements
		ViewScreenUpdater {

	private final MouseActionHolder actionHolder = MouseActionHolder
			.getInstance();

	// -------------------------
	// singleton
	// -------------------------
	private static ScreenUpdater instance = null;

	private ScreenUpdater() {
	}

	public static ScreenUpdater getInstance() {
		if (instance == null) {
			instance = new ScreenUpdater();
		}

		return instance;
	}

	// -------------------------

	/*
	 * (非 Javadoc)
	 * 
	 * @see oripa.viewsetting.main.ViewScreenUpdater#updateScreen()
	 */
	@Override
	public void updateScreen() {
		setChanged();
		notifyObservers(REDRAW_REQUESTED);

	}

	public class KeyListener implements java.awt.event.KeyListener {
		@Override
		public void keyTyped(final KeyEvent e) {
		}

		@Override
		public void keyPressed(final KeyEvent e) {

			if (e.isControlDown()) {
				updateIfCopyAndPaste(true);
			}
			else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				updateScreen();

			}

		}

		@Override
		public void keyReleased(final KeyEvent e) {
			updateIfCopyAndPaste(false);
		}

	}

	private void updateIfCopyAndPaste(final boolean changeOrigin) {
		GraphicMouseActionInterface action = actionHolder.getMouseAction();

		if (action instanceof CopyAndPasteAction) {
			CopyAndPasteAction casted = (CopyAndPasteAction) action;
			casted.changeAction(changeOrigin);

			updateScreen();
		}

	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see oripa.viewsetting.main.ViewScreenUpdater#getKeyListener()
	 */
	@Override
	public java.awt.event.KeyListener getKeyListener() {
		return new KeyListener();
	}

}
