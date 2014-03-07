package oripa.controller.paint.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import oripa.ORIPA;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.ScreenUpdaterInterface;
import oripa.controller.paint.core.PaintContext;
import oripa.domain.cptool.Painter;
import oripa.persistent.doc.Doc;
import oripa.value.OriLine;
import oripa.viewsetting.main.ScreenUpdater;

public class DeleteSelectedLines implements ActionListener {

	PaintContextInterface context = PaintContext.getInstance();
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Doc document = ORIPA.doc;
		Collection<OriLine> creasePattern = document.getCreasePattern();

		document.pushUndoInfo();

		Painter painter = new Painter();
		painter.removeSelectedLines(creasePattern);

		if(context.isPasting() == false){
			context.clear(false);
		}
		
		ScreenUpdaterInterface screenUpdater = ScreenUpdater.getInstance();
		
		screenUpdater.updateScreen();

	}

}
