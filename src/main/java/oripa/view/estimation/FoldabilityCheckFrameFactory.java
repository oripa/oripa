package oripa.view.estimation;

import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JFrame;

import oripa.fold.OrigamiModel;
import oripa.util.gui.ChildFrameManager;
import oripa.value.OriLine;

public class FoldabilityCheckFrameFactory {
	private static FoldabilityCheckFrame frame = null;

	public JFrame createFrame(JComponent parent, OrigamiModel origamiModel,
			Collection<OriLine> creasePattern // , FoldedModelInfo
												// foldedModelInfo
	) {

		if (frame == null) {
			frame = new FoldabilityCheckFrame();
		}

		frame.setModel(origamiModel, creasePattern // , foldedModelInfo
		);
		frame.repaint();
		ChildFrameManager.getManager().putChild(parent, frame);

		return frame;
	}
}
