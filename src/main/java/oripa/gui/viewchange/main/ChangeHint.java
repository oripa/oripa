package oripa.gui.viewchange.main;

import java.util.ResourceBundle;

import oripa.gui.view.main.MainFrameSetting;
import oripa.gui.viewchange.ChangeViewSetting;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;

public class ChangeHint implements ChangeViewSetting {
    private final ResourceHolder resourceHolder;

    private final MainFrameSetting frameSetting;

    private final String id;

    public ChangeHint(final MainFrameSetting mainFrameSetting, final String resourceID,
            final ResourceHolder resourceHolder) {
        frameSetting = mainFrameSetting;
        this.id = resourceID;
        this.resourceHolder = resourceHolder;
    }

    @Override
    public void changeViewSetting() {

        ResourceBundle resource = resourceHolder.getResource(ResourceKey.EXPLANATION);

        String hint = null;
        try {
            hint = resource.getString(id);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        frameSetting.setHint(hint);
    }

}
