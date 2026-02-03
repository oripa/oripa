package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.mirror.SelectingLineForMirror;

public class MirrorCopyAction extends SelectLineAction {

    public MirrorCopyAction() {

        setEditMode(EditMode.INPUT);
        setNeedSelect(true);

        setActionState(new SelectingLineForMirror());
    }
}
