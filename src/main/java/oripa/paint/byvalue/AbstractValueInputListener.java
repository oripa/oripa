package oripa.paint.byvalue;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;


/**
 * A template for catching input from text input.
 * Use as: text.getDocument().addDocumentListener(new SubclassOfThis());
 * 
 * @author koji
 *
 */
public abstract class AbstractValueInputListener implements DocumentListener{


	@Override
	public void insertUpdate(DocumentEvent e) {
		setToDB(e);

	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		setToDB(e);

	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		setToDB(e);
	}
	
	private void setToDB(DocumentEvent e){
		Document document = e.getDocument();
		try{
			String text = document.getText(0, document.getLength());
			double length = java.lang.Double.valueOf(text);

			setValue(length);
		}
		catch (Exception ex) {
			// TODO: handle exception
		}
	}

	/**
	 * implementation of what to do when the text changed.
	 * @param value
	 */
	protected abstract void setValue(double value);
}