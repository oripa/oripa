package oripa.gui.view.foldability;

import java.util.Collection;

import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.gui.view.FrameView;
import oripa.gui.view.util.ChildFrameManager;
import oripa.value.OriLine;

public class FoldabilityCheckFrameFactory {
	private final ChildFrameManager childFrameManager;

	/**
	 * Constructor
	 */
	public FoldabilityCheckFrameFactory(final ChildFrameManager childFrameManager) {
		this.childFrameManager = childFrameManager;
	}

	public FoldabilityCheckFrameView createFrame(final FrameView parent, final OrigamiModel origamiModel,
			final Collection<OriLine> creasePattern, final boolean zeroLineWidth) {

		FoldabilityCheckFrame frame = childFrameManager.find(parent,
				FoldabilityCheckFrame.class);
		if (frame == null) {
			frame = new FoldabilityCheckFrame();
		}

		frame.setModel(origamiModel, creasePattern, zeroLineWidth);
		frame.setOnCloseListener(f -> childFrameManager.removeFromChildren(f));
		childFrameManager.putChild(parent, frame);

		return frame;
	}
}
