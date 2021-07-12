package oripa.gui.viewsetting.main;

import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.function.Consumer;

import oripa.gui.viewsetting.ViewScreenUpdater;

public class MainScreenUpdater implements ViewScreenUpdater {

	private Consumer<Boolean> changeActionIfCopyAndPaste;

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public void setChangeActionIfCopyAndPaste(final Consumer<Boolean> changeAction) {
		this.changeActionIfCopyAndPaste = changeAction;
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
				changeActionIfCopyAndPaste.accept(true);
				updateScreen();
			} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				updateScreen();
			}

		}

		@Override
		public void keyReleased(final KeyEvent e) {
			changeActionIfCopyAndPaste.accept(false);
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
