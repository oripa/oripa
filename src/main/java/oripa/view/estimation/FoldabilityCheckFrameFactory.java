package oripa.view.estimation;

import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JFrame;

import oripa.domain.fold.OrigamiModel;
import oripa.util.gui.ChildFrameManager;
import oripa.value.OriLine;

public class FoldabilityCheckFrameFactory {
	private static FoldabilityCheckFrame frame = null;

	public JFrame createFrame(final JComponent parent, final OrigamiModel origamiModel,
			final Collection<OriLine> creasePattern) {

		if (frame == null) {
			frame = new FoldabilityCheckFrame();
		}

		frame.setModel(origamiModel, creasePattern);
		ChildFrameManager.getManager().putChild(parent, frame);

		return frame;
	}
}
