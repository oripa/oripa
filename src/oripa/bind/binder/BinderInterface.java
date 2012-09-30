package oripa.bind.binder;

import java.awt.event.ActionListener;

import javax.swing.AbstractButton;

public interface BinderInterface<ToBeBound> {

	/**
	 * 
	 * @param buttonClass Class object of a button to be created
	 * @param target An object to be bound to button.doClick()
	 * @param textID {@code StringID} member for label
	 * @return
	 */
	public AbstractButton createButton(
			Class<? extends AbstractButton> buttonClass,
			ToBeBound target, String id);
	
//	public void setOptionalActionListeners(ActionListener[] others);

}