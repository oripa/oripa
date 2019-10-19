package oripa.domain.paint.mirror;

import oripa.domain.paint.EditMode;
import oripa.domain.paint.selectline.SelectLineAction;

public class MirrorCopyAction extends SelectLineAction {

	public MirrorCopyAction() {

		setEditMode(EditMode.INPUT);
		setNeedSelect(true);

		setActionState(new SelectingLineForMirror());
	}
}
