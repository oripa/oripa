package oripa.domain.paint.mirror;

import oripa.domain.paint.EditMode;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.selectline.SelectLineAction;

public class MirrorCopyAction extends SelectLineAction {

	public MirrorCopyAction() {

		setEditMode(EditMode.INPUT);
		setNeedSelect(true);

		setActionState(new SelectingLineForMirror());
	}

	@Override
	public void destroy(final PaintContextInterface context) {
		context.clear(false);
	}
}
