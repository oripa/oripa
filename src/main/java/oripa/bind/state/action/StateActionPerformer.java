package oripa.bind.state.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.appstate.ApplicationState;
import oripa.paint.EditMode;


public class StateActionPerformer implements ActionListener{
	private ApplicationState<EditMode> state;
	
	public StateActionPerformer(ApplicationState<EditMode> s) {
		state = s;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		state.performActions(e);
	}
}