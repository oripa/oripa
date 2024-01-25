package oripa.swing.view.foldability;

import oripa.gui.view.FrameView;
import oripa.gui.view.foldability.FoldabilityCheckFrameFactory;
import oripa.gui.view.foldability.FoldabilityCheckFrameView;
import oripa.gui.view.util.ChildFrameManager;

public class FoldabilityCheckSwingFrameFactory implements FoldabilityCheckFrameFactory {
	private final ChildFrameManager childFrameManager;

	/**
	 * Constructor
	 */
	public FoldabilityCheckSwingFrameFactory(final ChildFrameManager childFrameManager) {
		this.childFrameManager = childFrameManager;
	}

	@Override
	public FoldabilityCheckFrameView createFrame(final FrameView parent) {

		var frameOpt = childFrameManager.find(parent,
				FoldabilityCheckFrame.class);

		frameOpt.ifPresent(frame -> {
			removeFromChildFrameManager(frame);
			frame.dispose();
		});

		var frame = new FoldabilityCheckFrame();

		frame.setOnCloseListener(this::removeFromChildFrameManager);
		childFrameManager.putChild(parent, frame);

		return frame;
	}

	private void removeFromChildFrameManager(final FrameView frame) {
		childFrameManager.closeAll(frame);
		childFrameManager.removeFromChildren(frame);
	}
}
