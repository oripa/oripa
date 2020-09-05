package oripa.view.estimation;

import javax.swing.JComponent;
import javax.swing.JFrame;

import oripa.domain.fold.FoldedModelInfo;
import oripa.domain.fold.OrigamiModel;
import oripa.util.gui.ChildFrameManager;

public class EstimationResultFrameFactory {
	private static EstimationResultFrame frame = null;

	public JFrame createFrame(
			final JComponent parent,
			final OrigamiModel origamiModel,
			final FoldedModelInfo foldedModelInfo) {

		if (frame == null) {
			frame = new EstimationResultFrame();
		}

		frame.setModel(origamiModel, foldedModelInfo);
		ChildFrameManager.getManager().putChild(parent, frame);

		return frame;
	}
}
