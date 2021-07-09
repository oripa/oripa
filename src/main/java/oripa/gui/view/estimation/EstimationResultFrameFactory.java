package oripa.gui.view.estimation;

import javax.swing.JComponent;
import javax.swing.JFrame;

import oripa.domain.fold.FoldedModel;
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
			final FoldedModel foldedModel) {

		EstimationResultFrame frame = (EstimationResultFrame) childFrameManager.find(parent,
				EstimationResultFrame.class);
		if (frame == null) {
			frame = new EstimationResultFrame();
		}

		frame.setModel(foldedModel);
		childFrameManager.putChild(parent, frame);

		return frame;
	}
}
