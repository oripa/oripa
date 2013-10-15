package oripa.view.estimation;

import java.util.Collection;

import javax.swing.JFrame;

import oripa.fold.FoldedModelInfo;
import oripa.fold.OrigamiModel;
import oripa.value.OriLine;

public class FoldabilityCheckFrameFactory {
	private static FoldabilityCheckFrame frame = null;

	public JFrame createFrame(
			OrigamiModel origamiModel, 
    		Collection<OriLine> creasePattern, FoldedModelInfo foldedModelInfo) {

		if (frame == null) {
			frame = new FoldabilityCheckFrame();
		}

		frame.showModel(origamiModel, creasePattern, foldedModelInfo);
		return frame;
	}
}
