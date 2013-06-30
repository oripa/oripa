package oripa.bind.state;

import java.awt.Component;
import java.awt.event.ActionEvent;

/**
 * An interface for handling error in ActionListener.actionPerformed().
 * @author koji
 *
 */
public interface ErrorListener {
	/**
	 * Define a condition of error by this method.
	 * @param e
	 * @return {@code true} if an error occurs. otherwise false. 
	 * 
	 */
	public boolean isError(ActionEvent e);
	
	
	/**
	 * Define what to do when an error has happened.
	 * @param parent 	an parent component. Use this for showing message, e.g..
	 * @param e			
	 */
	public void onError(Component parent, ActionEvent e);
}