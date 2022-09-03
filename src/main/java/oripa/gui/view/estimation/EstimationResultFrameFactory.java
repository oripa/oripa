package oripa.gui.view.estimation;

import java.util.List;

import oripa.domain.fold.FoldedModel;
import oripa.gui.view.FrameView;
import oripa.gui.view.util.ChildFrameManager;

public class EstimationResultFrameFactory {

	private final ChildFrameManager childFrameManager;

	/**
	 * Constructor
	 */
	public EstimationResultFrameFactory(final ChildFrameManager childFrameManager) {
		this.childFrameManager = childFrameManager;
	}

	public EstimationResultFrameView createFrame(
			final FrameView parent,
			final List<FoldedModel> foldedModels) {

		EstimationResultFrame frame = childFrameManager.find(parent,
				EstimationResultFrame.class);
		if (frame == null) {
			frame = new EstimationResultFrame();
		}

		frame.setModels(foldedModels);
		frame.setOnCloseListener(f -> childFrameManager.removeFromChildren(f));
		childFrameManager.putChild(parent, frame);

		return frame;
	}
}
