package oripa.gui.view;

public interface ViewScreenUpdater {

    void updateScreen();

    void addListener(Runnable listener);
}