package oripa.bind.state;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.paint.EditMode;

public class LastInputCommandPopper implements ActionListener{
	public ApplicationState<EditMode> pop(){
		StateManager manager = StateManager.getInstance();
		return manager.popLastInputCommand();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ApplicationState<EditMode> current = pop();
		current.performActions(e);
	}
}
