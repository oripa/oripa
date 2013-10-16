package oripa.bind.copypaste;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.JOptionPane;

import oripa.ORIPA;
import oripa.bind.state.ErrorListener;
import oripa.doc.Doc;
import oripa.value.OriLine;

public class CopyPasteErrorListener implements ErrorListener {

	@Override
	public boolean isError(ActionEvent e) {
		Doc document = ORIPA.doc;
		Collection<OriLine> creasePattern = document.getCreasePattern();
		return (document.countSelectedLineNum(creasePattern) == 0);
	}

	@Override
	public void onError(Component parent, ActionEvent e) {
		showErrorMessage(parent, e);
	}

	private void showErrorMessage(Component parent, ActionEvent e) {
		JOptionPane.showMessageDialog(parent, "Select target lines",
				"Copy and Paste", JOptionPane.WARNING_MESSAGE);
	}

}
