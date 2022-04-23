package oripa.gui.view.estimation;

import javax.swing.JComponent;

import oripa.domain.fold.FoldedModel;
import oripa.gui.view.util.ChildFrameManager;

public class EstimationResultFrameFactory {

	private final ChildFrameManager childFrameManager;

	/**
	 * Constructor
	 */
	public EstimationResultFrameFactory(final ChildFrameManager childFrameManager) {
		this.childFrameManager = childFrameManager;
	}

	public EstimationResultFrame createFrame(
			final JComponent parent,
			final FoldedModel foldedModel) {

		EstimationResultFrame frame = childFrameManager.find(parent,
				EstimationResultFrame.class);
		if (frame == null) {
			frame = new EstimationResultFrame();
		}

		frame.setModel(foldedModel);
		childFrameManager.putChild(parent, frame);

		return frame;
	}
}
