package oripa.bind;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractButton;

import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.viewsetting.ChangeViewSetting;
import oripa.viewsetting.ViewSettingActionListener;

public class ViewSettingBinder implements BinderInterface<ChangeViewSetting> {

	/* (non-Javadoc)
	 * @see oripa.command.BinderInterface#createButton(java.lang.Class, oripa.paint.Command, java.lang.String)
	 */
	@Override
	public AbstractButton createButton(
			Class<? extends AbstractButton> buttonClass,
			ChangeViewSetting command, String id){

		return createMultiCommandButton(
				buttonClass, new ChangeViewSetting[]{command}, id);
	}	

	/* (non-Javadoc)
	 * @see oripa.command.BinderInterface#getBoundListeners(oripa.paint.Command, java.lang.String)
	 */
	@Override
	public Collection<ActionListener> getBoundListeners(
			ChangeViewSetting command, String id){
		return getBoundListeners(new ChangeViewSetting[] {command}, id);
	}
	
	@Override
	public AbstractButton createMultiCommandButton(
			Class<? extends AbstractButton> buttonClass,
			ChangeViewSetting[] commands, String id) {
		ResourceHolder resourceHolder = ResourceHolder.getInstance();

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
		
		for(ActionListener listener : getBoundListeners(commands, id)){
			button.addActionListener(listener);
		}
		
		try{
			button.setText(resourceHolder.getString(ResourceKey.LABEL, id));
		}
		catch (Exception e) {
		}
		
		return button;
	}
	
	@Override
	public Collection<ActionListener> getBoundListeners(
			ChangeViewSetting[] commands, String id) {
		ArrayList<ActionListener> listeners = new ArrayList<>();
		
		if(commands == null){
			return listeners;
		}
		
		for(ChangeViewSetting command : commands){
			if(command != null){
				listeners.add( new ViewSettingActionListener(command));
			}		
		}
		
		return listeners;
	}
	

}
