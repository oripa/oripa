package oripa.gui.viewsetting;

public interface ViewScreenUpdater {

	public static final String REDRAW_REQUESTED = "redraw requested";

	public void updateScreen();

	public void addListener(Runnable listener);
}