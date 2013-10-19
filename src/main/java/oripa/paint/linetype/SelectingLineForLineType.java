package oripa.paint.linetype;

import java.util.Collection;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.paint.core.PaintContext;
import oripa.paint.core.PickingLine;
import oripa.paint.creasepattern.Painter;
import oripa.value.OriLine;
import oripa.viewsetting.main.uipanel.UIPanelSettingDB;

public class SelectingLineForLineType extends PickingLine {

	
	
	public SelectingLineForLineType() {
		super();
	}

	@Override
	protected void initialize() {
	}
	
	
	@Override
	protected void undoAction(PaintContext context) {
		super.undoAction(context);
	}

	@Override
	protected void onResult(PaintContext context) {
		Doc document = ORIPA.doc;
		Collection<OriLine> creasePattern = document.getCreasePattern();

		document.pushUndoInfo();

    	UIPanelSettingDB setting = UIPanelSettingDB.getInstance();
    	Painter painter = new Painter();
    	painter.alterLineType(
    			context.peekLine(),  setting.getTypeFrom(), setting.getTypeTo(),
    			creasePattern);

        context.clear(false);
	}

}
