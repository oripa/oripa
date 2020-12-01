package oripa.viewsetting.main;

import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import oripa.domain.paint.GraphicMouseActionInterface;
import oripa.domain.paint.MouseActionHolder;
import oripa.domain.paint.copypaste.CopyAndPasteAction;
import oripa.viewsetting.ViewScreenUpdater;

public class ScreenUpdater implements
		ViewScreenUpdater {

	private MouseActionHolder actionHolder;

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public void setMouseActionHolder(final MouseActionHolder actionHolder) {
		this.actionHolder = actionHolder;
	}

	// -------------------------

	public void addPropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.viewsetting.main.ViewScreenUpdater#updateScreen()
	 */
	@Override
	public void updateScreen() {
		propertyChangeSupport.firePropertyChange(REDRAW_REQUESTED, null, null);

	}

	private class KeyListener implements java.awt.event.KeyListener {
		@Override
		public void keyTyped(final KeyEvent e) {
		}

		@Override
		public void keyPressed(final KeyEvent e) {
			if (e.isControlDown()) {
				updateIfCopyAndPaste(true);
			} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
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
	 * (non Javadoc)
	 *
	 * @see oripa.viewsetting.main.ViewScreenUpdater#getKeyListener()
	 */
	@Override
	public java.awt.event.KeyListener getKeyListener() {
		return new KeyListener();
	}

}
