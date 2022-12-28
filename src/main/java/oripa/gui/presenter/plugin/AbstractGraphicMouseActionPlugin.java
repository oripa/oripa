package oripa.gui.presenter.plugin;

import oripa.gui.view.main.MainFrameSetting;
import oripa.gui.view.main.UIPanelSetting;
import oripa.gui.viewchange.ChangeViewSetting;

public abstract class AbstractGraphicMouseActionPlugin implements GraphicMouseActionPlugin {

	protected MainFrameSetting frameSetting;

	protected UIPanelSetting uiPanelSetting;

	@Override
	public void setMainFrameSetting(final MainFrameSetting setting) {
		frameSetting = setting;
	}

	@Override
	public void setUIPanelSetting(final UIPanelSetting setting) {
		uiPanelSetting = setting;
	}

	@Override
	public ChangeViewSetting getChangeHint() {
		return () -> {
			frameSetting.setHint(getHint());
		};
	}
}
