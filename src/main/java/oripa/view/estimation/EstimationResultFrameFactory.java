package oripa.view.estimation;

import javax.swing.JFrame;

import oripa.fold.FoldedModelInfo;
import oripa.fold.OrigamiModel;

public class EstimationResultFrameFactory {
	private static EstimationResultFrame frame = null;

	public JFrame createFrame(
			OrigamiModel origamiModel, 
    		FoldedModelInfo foldedModelInfo
    		) {

		if (frame == null) {
			frame = new EstimationResultFrame();
		}

		frame.setModel(origamiModel, foldedModelInfo
				);
		return frame;
	}
}
