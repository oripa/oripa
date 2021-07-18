package oripa.gui.view.foldability;

import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JFrame;

import oripa.domain.fold.halfedge.OrigamiModel;
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

	public JFrame createFrame(final JComponent parent, final OrigamiModel origamiModel,
			final Collection<OriLine> creasePattern, final boolean zeroLineWidth) {

		FoldabilityCheckFrame frame = (FoldabilityCheckFrame) childFrameManager.find(parent,
				FoldabilityCheckFrame.class);
		if (frame == null) {
			frame = new FoldabilityCheckFrame();
		}

		frame.setModel(origamiModel, creasePattern, zeroLineWidth);
		childFrameManager.putChild(parent, frame);

		return frame;
	}
}
