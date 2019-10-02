package oripa.paint.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.paint.core.PaintConfig;

public class LineTypeSetter implements ActionListener {

	protected final int lineType;
	
	public LineTypeSetter(int type) {
		lineType = type;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		PaintConfig.inputLineType = lineType;

	}

}
