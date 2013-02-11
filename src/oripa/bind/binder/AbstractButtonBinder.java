package oripa.bind.binder;

import javax.swing.AbstractButton;

import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;

public abstract class AbstractButtonBinder<ToBeBound> 
implements BinderInterface<ToBeBound>{

	
	/**
	 * 
	 * @param buttonClass
	 * @param textID StringID member for label
	 * @return
	 */
	protected AbstractButton createEmptyButton(
			Class<? extends AbstractButton> buttonClass, String textID) {

		ResourceHolder resourceHolder = ResourceHolder.getInstance();
	
		/*
		 * construct button
		 */
		AbstractButton button = null;
		try {
			button = buttonClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		/*
		 * set label
		 */
		try{
			button.setText(resourceHolder.getString(ResourceKey.LABEL, textID));
		}
		catch (Exception e) {
		}
	
		

		return button;
	}

}