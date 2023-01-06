package oripa.gui.presenter.plugin;

import oripa.gui.view.main.MainFrameSetting;
import oripa.gui.view.main.UIPanelSetting;
import oripa.gui.viewchange.ChangeViewSetting;

public abstract class AbstractGraphicMouseActionPlugin implements GraphicMouseActionPlugin {

	private MainFrameSetting frameSetting;

	private UIPanelSetting uiPanelSetting;

	@Override
	public void setMainFrameSetting(final MainFrameSetting setting) {
		frameSetting = setting;
	}

	public MainFrameSetting getMainFrameSetting() {
		return frameSetting;
	}

	@Override
	public void setUIPanelSetting(final UIPanelSetting setting) {
		uiPanelSetting = setting;
	}

	public UIPanelSetting getUIPanelSetting() {
		return uiPanelSetting;
	}

	@Override
	public ChangeViewSetting getChangeHint() {
		return () -> {
			frameSetting.setHint(getHint());
		};
	}
}
