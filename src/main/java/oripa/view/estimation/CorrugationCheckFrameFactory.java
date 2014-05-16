package oripa.view.estimation;

import java.util.Collection;
import oripa.corrugation.CorrugationChecker;

import javax.swing.JFrame;

import oripa.fold.OrigamiModel;
import oripa.value.OriLine;

public class CorrugationCheckFrameFactory {
	private static CorrugationCheckFrame frame = null;

	public JFrame createFrame(
			OrigamiModel origamiModel, 
    		Collection<OriLine> creasePattern,
    		CorrugationChecker corrugationChecker//, FoldedModelInfo foldedModelInfo
    		) {

		if (frame == null) {
			frame = new CorrugationCheckFrame();
		}

		frame.setModel(origamiModel, creasePattern, corrugationChecker //, foldedModelInfo
				);
		return frame;
	}
}
