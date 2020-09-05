package oripa.view.estimation;

import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JFrame;

import oripa.domain.fold.OrigamiModel;
import oripa.util.gui.ChildFrameManager;
import oripa.value.OriLine;

public class FoldabilityCheckFrameFactory {
	private final ChildFrameManager childFrameManager;

	/**
	 * Constructor
	 */
	public FoldabilityCheckFrameFactory(final ChildFrameManager childFrameManager) {
		this.childFrameManager = childFrameManager;
	}

	public JFrame createFrame(final JComponent parent, final OrigamiModel origamiModel,
			final Collection<OriLine> creasePattern) {

		FoldabilityCheckFrame frame = (FoldabilityCheckFrame) childFrameManager.find(parent,
				FoldabilityCheckFrame.class);
		if (frame == null) {
			frame = new FoldabilityCheckFrame();
		}

		frame.setModel(origamiModel, creasePattern);
		childFrameManager.putChild(parent, frame);

		return frame;
	}
}
