package oripa.swing.view.estimation;

import oripa.gui.view.FrameView;
import oripa.gui.view.estimation.EstimationResultFrameFactory;
import oripa.gui.view.estimation.EstimationResultFrameView;
import oripa.gui.view.util.ChildFrameManager;

public class EstimationResultSwingFrameFactory implements EstimationResultFrameFactory {

	private final ChildFrameManager childFrameManager;

	/**
	 * Constructor
	 */
	public EstimationResultSwingFrameFactory(final ChildFrameManager childFrameManager) {
		this.childFrameManager = childFrameManager;
	}

	@Override
	public EstimationResultFrameView createFrame(
			final FrameView parent) {

		EstimationResultFrame frame = childFrameManager.find(parent,
				EstimationResultFrame.class);

		if (frame != null) {
			removeFromChildFrameManager(frame);
			frame.dispose();
		}
		frame = new EstimationResultFrame();

		frame.setOnCloseListener(this::removeFromChildFrameManager);
		childFrameManager.putChild(parent, frame);

		return frame;
	}

	private void removeFromChildFrameManager(final FrameView frame) {
		childFrameManager.closeAll(frame);
		childFrameManager.removeFromChildren(frame);
	}

}
