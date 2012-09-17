package oripa.bind;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractButton;

import oripa.paint.GraphicMouseAction;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.viewsetting.ViewSettingActionListener;
import oripa.viewsetting.main.ChangeHint;
import oripa.viewsetting.main.ScreenUpdater;

public class PaintActionBinder implements BinderInterface<GraphicMouseAction> {

	/* (non-Javadoc)
	 * @see oripa.command.BinderInterface#createButton(java.lang.Class, oripa.paint.GraphicMouseAction, java.lang.String)
	 */
	@Override
	public AbstractButton createButton(
			Class<? extends AbstractButton> buttonClass,
			GraphicMouseAction action, String id){

		return createMultiCommandButton(
				buttonClass, new GraphicMouseAction[]{action}, id);
	}	

	/* (non-Javadoc)
	 * @see oripa.command.BinderInterface#getBoundListeners(oripa.paint.GraphicMouseAction, java.lang.String)
	 */
	@Override
	public Collection<ActionListener> getBoundListeners(
			GraphicMouseAction action, String id){
		return getBoundListeners(new GraphicMouseAction[] {action}, id);
	}

	@Override
	public AbstractButton createMultiCommandButton(
			Class<? extends AbstractButton> buttonClass,
			GraphicMouseAction[] actions, String id) {
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

		for(ActionListener listener : getBoundListeners(actions, id)){
			button.addActionListener(listener);
		}

		try{
			button.setText(resourceHolder.getString(ResourceKey.LABEL, id));
		}
		catch (Exception e) {
		}
		
		// For catching key actions with immediately drawing
		button.addKeyListener(new ScreenUpdater());


		return button;
	}

	@Override
	public Collection<ActionListener> getBoundListeners(
			GraphicMouseAction[] actions, String id) {
		ArrayList<ActionListener> listeners = new ArrayList<>();

		if(id != null){
			// add view updater
			listeners.add(
					new ViewSettingActionListener(new ChangeHint(id))
					);
		}

		if(actions == null){
			return listeners;
		}

		for(GraphicMouseAction action : actions){
			if(action != null){
				listeners.add(new CommandSetter(action));
			}		
		}



		return listeners;
	}
}
