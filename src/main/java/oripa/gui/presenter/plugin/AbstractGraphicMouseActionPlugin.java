package oripa.gui.presenter.plugin;

import oripa.gui.view.main.MainFrameSetting;
import oripa.gui.view.main.UIPanelSetting;
import oripa.gui.viewchange.ChangeViewSetting;

/**
 * A template for plug-in.
 *
 * @author OUCHI Koji
 *
 */
public abstract class AbstractGraphicMouseActionPlugin implements GraphicMouseActionPlugin {

    private MainFrameSetting frameSetting;

    private UIPanelSetting uiPanelSetting;

    /**
     *
     * @return the text shown at the bottom of window.
     */
    protected abstract String getHint();

    @Override
    public void setMainFrameSetting(final MainFrameSetting setting) {
        frameSetting = setting;
    }

    protected MainFrameSetting getMainFrameSetting() {
        return frameSetting;
    }

    @Override
    public void setUIPanelSetting(final UIPanelSetting setting) {
        uiPanelSetting = setting;
    }

    protected UIPanelSetting getUIPanelSetting() {
        return uiPanelSetting;
    }

    @Override
    public ChangeViewSetting getChangeOnSelected() {
        return () -> configureChangeOnSelected(uiPanelSetting);
    }

    /**
     * Configure the given setting object.
     *
     * @param setting
     */
    protected abstract void configureChangeOnSelected(UIPanelSetting setting);

    @Override
    public ChangeViewSetting getChangeHint() {
        return () -> frameSetting.setHint(getHint());
    }
}
