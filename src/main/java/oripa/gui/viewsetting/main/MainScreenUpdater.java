package oripa.gui.viewsetting.main;

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

	private class KeyProcessingImpl implements KeyProcessing {

		@Override
		public void controlKeyPressed() {
			changeActionIfCopyAndPaste.accept(true);
			updateScreen();
		}

		@Override
		public void escapeKeyPressed() {
			updateScreen();
		}

		@Override
		public void keyReleased() {
			changeActionIfCopyAndPaste.accept(false);
			updateScreen();
		}
	}

	@Override
	public KeyProcessing getKeyProcessing() {
		return new KeyProcessingImpl();
	}
}
