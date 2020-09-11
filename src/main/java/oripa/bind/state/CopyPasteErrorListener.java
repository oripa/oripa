package oripa.bind.state;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;

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
		JFrame frame = (parent instanceof JFrame) ? (JFrame) parent
				: (JFrame) SwingUtilities.getWindowAncestor(parent);

		JOptionPane.showMessageDialog(frame, "Select target lines",
				"Copy and Paste", JOptionPane.WARNING_MESSAGE);
	}

}
