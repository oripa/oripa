package oripa.gui.bind;

import java.awt.Component;
import java.awt.event.KeyListener;

import javax.swing.AbstractButton;

import oripa.resource.StringID;

/**
 * A factory which creates some clickable GUI object with a state to be after
 * the object is clicked.
 *
 * @author Koji
 *
 */
public interface ButtonFactory {

	/**
	 *
	 * @param parent
	 * @param buttonClass
	 * @param id
	 *            a member of {@link StringID} for the button's action and
	 *            label.
	 * @param keyListener
	 * @return an object of {@code buttonClass}
	 */
	public abstract <TButton extends AbstractButton> TButton create(Component parent,
			Class<TButton> buttonClass,
			String id,
			KeyListener keyListener) throws IllegalArgumentException;

}