package oripa.paint.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.paint.ScreenUpdaterInterface;
import oripa.paint.core.PaintContext;
import oripa.paint.creasepattern.Painter;
import oripa.value.OriLine;
import oripa.viewsetting.main.ScreenUpdater;

public class DeleteSelectedLines implements ActionListener {

	final PaintContext context = PaintContext.getInstance();
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Doc document = ORIPA.doc;
		Collection<OriLine> creasePattern = document.getCreasePattern();

		document.pushUndoInfo();

		Painter painter = new Painter();
		painter.removeSelectedLines(creasePattern);

		if(!context.isPasting()){
			context.clear(false);
		}
		
		ScreenUpdaterInterface screenUpdater = ScreenUpdater.getInstance();
		
		screenUpdater.updateScreen();

	}

}
