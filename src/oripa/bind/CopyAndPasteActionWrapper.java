package oripa.bind;

import java.awt.geom.AffineTransform;

import oripa.paint.PaintContext;
import oripa.paint.copypaste.CopyAndPasteAction;
import oripa.viewsetting.uipanel.UIPanelSettingDB;

public class CopyAndPasteActionWrapper extends CopyAndPasteAction {

	@Override
	public void onRightClick(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		UIPanelSettingDB panelSetting = UIPanelSettingDB.getInstance();
		
		panelSetting.forceSelectMode();
		panelSetting.notifyObservers();
	}

	
	
}
