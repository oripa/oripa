package oripa.gui.viewsetting.main;

import java.util.ResourceBundle;

import oripa.gui.view.main.MainFrameSetting;
import oripa.gui.viewsetting.ChangeViewSetting;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;

public class ChangeHint implements ChangeViewSetting {

	private final MainFrameSetting frameSetting;

	private final String id;

	public ChangeHint(final MainFrameSetting mainFrameSetting, final String resourceID) {
		frameSetting = mainFrameSetting;
		this.id = resourceID;
	}

	@Override
	public void changeViewSetting() {
		ResourceHolder holder = ResourceHolder.getInstance();

		ResourceBundle resource = holder.getResource(ResourceKey.EXPLANATION);

		String hint = null;
		try {
			hint = resource.getString(id);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		frameSetting.setHint(hint);
	}

}
