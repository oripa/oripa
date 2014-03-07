package oripa.controller.paint.linetype;

import java.util.Collection;

import oripa.ORIPA;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingLine;
import oripa.domain.cptool.Painter;
import oripa.persistent.doc.Doc;
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
	protected void undoAction(PaintContextInterface context) {
		super.undoAction(context);
	}

	@Override
	protected void onResult(PaintContextInterface context) {
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
