package oripa.paint.colorscheme;

import java.awt.*;

public interface ColorScheme {
	Color getBackgroundColor();
	Color getPaperColor();
	Color getColorForLine(int lineType);
	Stroke getStrokeForLine(int lineType, double scale);
	Color getVertexColor();
	Color getSelectionColor();
	Color getCandidateColor();
	Color getUiOverlayColor();
}
