package oripa.swing.view.estimation;

import jakarta.inject.Inject;
import oripa.gui.view.FrameView;
import oripa.gui.view.estimation.EstimationResultFrameFactory;
import oripa.gui.view.estimation.EstimationResultFrameView;
import oripa.gui.view.util.ChildFrameManager;
import oripa.resource.ResourceHolder;

public class EstimationResultSwingFrameFactory implements EstimationResultFrameFactory {

	private final ChildFrameManager childFrameManager;

	private final ResourceHolder resourceHolder;

	@Inject
	public EstimationResultSwingFrameFactory(final ChildFrameManager childFrameManager,
			final ResourceHolder resourceHolder) {
		this.childFrameManager = childFrameManager;
		this.resourceHolder = resourceHolder;
	}

	@Override
	public EstimationResultFrameView createFrame(
			final FrameView parent) {

		var frameOpt = childFrameManager.find(parent,
				EstimationResultFrame.class);

		frameOpt.ifPresent(frame -> {
			removeFromChildFrameManager(frame);
			frame.dispose();
		});

		var frame = new EstimationResultFrame(resourceHolder);

		frame.setOnCloseListener(this::removeFromChildFrameManager);
		childFrameManager.putChild(parent, frame);

		return frame;
	}

	private void removeFromChildFrameManager(final FrameView frame) {
		childFrameManager.closeAll(frame);
		childFrameManager.removeFromChildren(frame);
	}

}
