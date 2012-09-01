package oripa.viewsetting.command;

import javax.swing.AbstractButton;

import oripa.paint.CommandSetter;
import oripa.paint.GraphicMouseAction;
import oripa.viewsetting.ViewSettingActionListener;
import oripa.viewsetting.main.ChangeHint;

public class CommandBinder {

	public AbstractButton createButton(
			Class<? extends AbstractButton> buttonClass,
			GraphicMouseAction action, String id){
		
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
		this.bind(button, action, id);
		
		return button;
	}
	
	public void bind(AbstractButton button, 
			GraphicMouseAction action, String id){
		if(action != null){
			button.addActionListener(
					new CommandSetter(action));
		}		
		if(id != null){
			button.addActionListener(
					new ViewSettingActionListener(new ChangeHint(id))
			);
		}
	}
	
	
	
}
