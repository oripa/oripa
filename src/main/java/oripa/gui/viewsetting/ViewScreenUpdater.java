package oripa.gui.viewsetting;

import oripa.gui.presenter.creasepattern.ScreenUpdater;

public interface ViewScreenUpdater extends ScreenUpdater {

	public static final String REDRAW_REQUESTED = "redraw requested";

	public interface KeyProcessing {
		public void controlKeyPressed();

		public void escapeKeyPressed();

		public void keyReleased();
	}

	public KeyProcessing getKeyProcessing();
}