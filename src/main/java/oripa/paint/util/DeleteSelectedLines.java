package oripa.paint.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.ORIPA;
import oripa.paint.ScreenUpdaterInterface;
import oripa.paint.core.PaintContext;
import oripa.viewsetting.main.ScreenUpdater;

public class DeleteSelectedLines implements ActionListener {

	PaintContext context = PaintContext.getInstance();
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ORIPA.doc.pushUndoInfo();
		ORIPA.doc.deleteSelectedLines();

		if(context.isPasting() == false){
			context.clear(false);
		}
		
		ScreenUpdaterInterface screenUpdater = ScreenUpdater.getInstance();
		
		screenUpdater.updateScreen();

	}

}
