package oripa.bind.binder;

import javax.swing.AbstractButton;

/**
 * This interface provides a general format of
 * linking some object to a GUI button with ID.
 * @author Koji
 *
 * @param <ToBeBound>
 */
public interface BinderInterface<ToBeBound> {

	/**
	 * 
	 * @param buttonClass Class object of a button to be created
	 * @param target An object to be bound to button.doClick()
	 * @param id {@code StringID} member for label
	 * @return
	 */
    AbstractButton createButton(Class<? extends AbstractButton> buttonClass, ToBeBound target, String id);
	
//	public void setOptionalActionListeners(ActionListener[] others);

}