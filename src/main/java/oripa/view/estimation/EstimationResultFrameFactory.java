package oripa.view.estimation;

import javax.swing.JComponent;
import javax.swing.JFrame;

import oripa.domain.fold.FoldedModelInfo;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.util.gui.ChildFrameManager;

public class EstimationResultFrameFactory {

	private final ChildFrameManager childFrameManager;

	/**
	 * Constructor
	 */
	public EstimationResultFrameFactory(final ChildFrameManager childFrameManager) {
		this.childFrameManager = childFrameManager;
	}

	public JFrame createFrame(
			final JComponent parent,
			final OrigamiModel origamiModel,
			final FoldedModelInfo foldedModelInfo) {

		EstimationResultFrame frame = (EstimationResultFrame) childFrameManager.find(parent,
				EstimationResultFrame.class);
		if (frame == null) {
			frame = new EstimationResultFrame();
		}

		frame.setModel(origamiModel, foldedModelInfo);
		childFrameManager.putChild(parent, frame);

		return frame;
	}
}
