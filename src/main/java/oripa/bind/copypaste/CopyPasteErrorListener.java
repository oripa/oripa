package oripa.bind.copypaste;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import oripa.bind.state.ErrorListener;
import oripa.controller.paint.PaintContextInterface;
import oripa.domain.cptool.Painter;

public class CopyPasteErrorListener implements ErrorListener {

	private final PaintContextInterface context;

	/**
	 * 
	 * Constructor.
	 * 
	 * @param aContext
	 *            a context
	 */
	public CopyPasteErrorListener(final PaintContextInterface aContext) {
		context = aContext;
	}

	@Override
	public boolean isError(final ActionEvent e) {
		Painter painter = context.getPainter();
		return (painter.countSelectedLines() == 0);
	}

	@Override
	public void onError(final Component parent, final ActionEvent e) {
		showErrorMessage(parent, e);
	}

	private void showErrorMessage(final Component parent, final ActionEvent e) {
		JOptionPane.showMessageDialog(parent, "Select target lines",
				"Copy and Paste", JOptionPane.WARNING_MESSAGE);
	}

}
