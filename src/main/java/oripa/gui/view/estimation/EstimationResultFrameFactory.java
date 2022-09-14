package oripa.gui.view.estimation;

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
