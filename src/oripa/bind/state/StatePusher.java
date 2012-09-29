package oripa.bind.state;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.appstate.StateManager;

public class StatePusher implements ActionListener{
	private PaintBoundState state;
	
	public StatePusher(PaintBoundState s){
		state = s;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		StateManager manager = StateManager.getInstance();
		manager.push(state);
		
	}		
}